/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parabuild.ci.merge.merger.build.perforce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.BuildRunner;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.merge.MergeClientNameGenerator;
import org.parabuild.ci.merge.MergeDAO;
import org.parabuild.ci.merge.merger.MergedChangeListDescriptionGenerator;
import org.parabuild.ci.merge.merger.Merger;
import org.parabuild.ci.notification.DummyNotificationManager;
import org.parabuild.ci.notification.NotificationManager;
import org.parabuild.ci.object.BranchMergeConfiguration;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.Merge;
import org.parabuild.ci.object.MergeChangeList;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.object.MergeTargetBuildRun;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.services.BuildStartRequest;
import org.parabuild.ci.versioncontrol.perforce.Integration;
import org.parabuild.ci.versioncontrol.perforce.P4DummyResolveDriver;
import org.parabuild.ci.versioncontrol.perforce.P4DummyResolveParser;
import org.parabuild.ci.versioncontrol.perforce.P4IntegrateParserDriver;
import org.parabuild.ci.versioncontrol.perforce.P4ResolveDriver;
import org.parabuild.ci.versioncontrol.perforce.P4ResolveParserImpl;
import org.parabuild.ci.versioncontrol.perforce.P4SourceControl;
import org.parabuild.ci.versioncontrol.perforce.Resolve;
import org.parabuild.ci.versioncontrol.perforce.ResolveMode;

/**
 * Validating merger is a merger that uses a build to
 * validate results of the merge.
 */
public final class P4Merger implements Merger {

  private static final Log log = LogFactory.getLog(P4Merger.class);

  private static final String UNEXPECTED_MERGE_RESULT = "Unexpected merge result: ";
  private static final String MERGE_WAS_SUCCESSFUL = "Merge was successful";

  private static final int MAX_CONFLICTS = 10;

  private final int activeMergeConfigurationID;
  private final NotificationManager notificationManager;


  /**
   * Constrcutor.
   *
   * @param activeMergeConfigurationID
   * @param notificationManager
   */
  public P4Merger(final int activeMergeConfigurationID, final NotificationManager notificationManager) {

    this.activeMergeConfigurationID = activeMergeConfigurationID;
    this.notificationManager = notificationManager;
  }


  public void merge() throws CommandStoppedException {
    if (log.isDebugEnabled()) {
      log.debug("begin merge");
    }

    // get list of validated merge queues orderred by build run ID.

    // REVIEWME: simeshev@parabuilci.org - 2007-08-02 - this
    // assumes that the earlier build runs contain earlier
    // change lists. This may not always be a correct
    // assumption.
    final List mergeIDs = MergeDAO.getInstance().getValidatedMergeIDs(activeMergeConfigurationID);
    if (log.isDebugEnabled()) {
      log.debug("validatedMergeQueueIDs: " + mergeIDs);
    }
    for (final Iterator i = mergeIDs.iterator(); i.hasNext();) {
      try {
        mergeQueue(((Number) i.next()).intValue());
      } catch (final CommandStoppedException e) {
        throw e;
      } catch (final Exception e) {
        reportErrorWhileProcessingMergeQueue(e);
      }
    }
  }


  private void mergeQueue(final int mergeID) throws IOException, CommandStoppedException, BuildException, ValidationException, AgentFailureException {
    if (log.isDebugEnabled()) {
      log.debug("======================= begin merge queue =========================");
    }
    if (log.isDebugEnabled()) {
      log.debug("merge queue mergeID: " + mergeID);
    }

    // get merge queue configuration

    final BranchMergeConfiguration mergeConfiguration = MergeDAO.getInstance().getMergeQueueConfiguration(mergeID);
    if (log.isDebugEnabled()) {
      log.debug("mergeConfiguration: " + mergeConfiguration);
    }

    // REVIEWME: simeshev@parabuilci.org -> for the given queue, find the validating build run.
    // We need to do this to find out if this was a build
    // fix.

    // for the given queue, find the validating build
    // configuration ID
    final int targetBuildConfigurationID = MergeDAO.getInstance().getTargetBuildConfigurationIDForMergeQueue(mergeID);
    if (log.isDebugEnabled()) {
      log.debug("targetBuildConfigurationID: " + targetBuildConfigurationID);
    }

    // get last clean build run
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildRun lastCleanBuildRun = cm.getLastCleanBuildRun(targetBuildConfigurationID);
    if (log.isDebugEnabled()) {
      log.debug("lastCleanBuildRun: " + lastCleanBuildRun);
    }

    // get change list to sync
    final ChangeList changeListToSync = cm.getBuildRunChangeListFromBuildRunParicipants(lastCleanBuildRun.getBuildRunID());
    if (log.isDebugEnabled()) {
      log.debug("buildRunChangeList: " + changeListToSync);
    }

    // create workspace directory name
    final AgentHost agentHost = AgentManager.getInstance().getNextLiveAgentHost(targetBuildConfigurationID);
    final Agent temporarelyAgent = AgentManager.getInstance().createAgent(targetBuildConfigurationID, agentHost);
    final String mergeClientHomeDirName = temporarelyAgent.getSystemWorkingDirName() + temporarelyAgent.separator() + "mr" + activeMergeConfigurationID;
    if (log.isDebugEnabled()) {
      log.debug("mergeClientHomeDirName: " + mergeClientHomeDirName);
    }

    // create agent
    final Agent agent = AgentManager.getInstance().getAgent(targetBuildConfigurationID, mergeClientHomeDirName);

    // create perforce VCS
    if (log.isDebugEnabled()) {
      log.debug("create perforce VCS");
    }


    final MergeClientNameGenerator clientNameGenerator = new MergeClientNameGenerator(activeMergeConfigurationID);
    final String clientName = clientNameGenerator.generateValidateClientName();
    final BuildRunConfig buildRunConfig = cm.getBuildRunConfig(lastCleanBuildRun.getBuildID());
    final P4SourceControl perforce = new P4SourceControl(buildRunConfig, mergeClientHomeDirName, new MergerClientNameGenerator(clientName));

    // sync to the last known clean build run
    if (log.isDebugEnabled()) {
      log.debug("sync to the last known clean build run");
    }

    syncToChangeListCleanly(perforce, clientName, agent, changeListToSync);

    // this is a counter that is used to determine we we
    // should try to run the merge. Non-zro means that there
    // are conflicts and we cannot get through all the
    // merges to validation.

    // this is a counter that shows us if we have anything
    // to merge

    // get merge queue
    if (log.isDebugEnabled()) {
      log.debug("get merge queue");
    }
    final List mergeQueueChangeLists = MergeDAO.getInstance().getAllMergeChangeLists(mergeID);

    // go over the list of validated change lists and do a
    // dry run, in a sense that we do not submit anything.
    if (log.isDebugEnabled()) {
      log.debug("mergeQueueChangeLists: " + mergeQueueChangeLists);
    }
    int successfulChangeLists = 0;
    int failedChangeLists = 0;
    for (final Iterator i = mergeQueueChangeLists.iterator(); i.hasNext();) {
      final Object[] o = (Object[]) i.next();
      final ChangeList changeList = (ChangeList) o[0];
      final MergeChangeList mergeChangeList = (MergeChangeList) o[1];
      if (log.isDebugEnabled()) {
        log.debug("mergeChangeList: " + mergeChangeList);
      }
      // run integrate
      final ChangeListMergeResult result = mergeChangeList(changeList, perforce, clientName, mergeConfiguration);
      if (log.isDebugEnabled()) {
        log.debug("change list merge result: " + result);
      }
      // handle result
      if (result.getCode() == MergeChangeList.RESULT_NOTHING_TO_MERGE) {
        // handle nothing to merge
        // REVIEWME: simeshev@parabuilci.org -> log as much information as possible
        setMergeChangeListResult(mergeChangeList, result.getCode(), result.getDescription());
      } else if (result.getCode() == MergeChangeList.RESULT_CONFLICTS) {
        // handle conflicts
        setMergeChangeListResult(mergeChangeList, result.getCode(), result.getDescription());
        notificationManager.notifyMergeFailedBecauseOfConflicts(mergeConfiguration, changeList, result.getConflicts());
        failedChangeLists++;
      } else if (result.getCode() == MergeChangeList.RESULT_SUCCESS) {
        successfulChangeLists++; // so that we know we have something to merge
      } else {
        ErrorManagerFactory.getErrorManager().reportSystemError(new Error(UNEXPECTED_MERGE_RESULT + result));
      }
    }


    if (failedChangeLists == 0) {
      if (successfulChangeLists > 0) {
        // we have change lists to work on
        final int buildRunID = runValidationBuild(targetBuildConfigurationID, lastCleanBuildRun, perforce, agent);
        if (buildRunID == BuildRun.UNSAVED_ID) {
          // validation didn't produce any results
          MergeDAO.getInstance().setMergeResult(mergeID, Merge.RESULT_CANNOT_VALIDATE);
        } else {
          // validation build produced some results

          // record the validating build run for future reference.
          MergeDAO.getInstance().save(new MergeTargetBuildRun(mergeID, buildRunID));

          // handle result
          final BuildRun validationBuildRun = ConfigurationManager.getInstance().getBuildRun(buildRunID);
          if (validationBuildRun.successful()) {
            // handle successful validation build run

            // NOTE: simeshev@parabuilci.org - 2007 - We are
            // here because the whole change list block was
            // confirmed as mergeable and the test build run
            // succeded. To complete the operation, we revert
            // the accumulated merges and merge and submit
            // merge results one by one.

            syncToChangeListCleanly(perforce, clientName, agent, changeListToSync);

            // get merge queue
            final List pendingChangeLists = MergeDAO.getInstance().getPendingMergeChangeLists(mergeID);

            // go over penging change lists and merge them one by one.
            for (final Iterator i = pendingChangeLists.iterator(); i.hasNext();) {
              final Object[] o = (Object[]) i.next();
              final ChangeList changeList = (ChangeList) o[0];
              final MergeChangeList mergeChangeList = (MergeChangeList) o[1];
              // run integrate
              final ChangeListMergeResult result = mergeChangeList(changeList, perforce, clientName, mergeConfiguration);
              // handle result
              if (result.getCode() == MergeChangeList.RESULT_NOTHING_TO_MERGE) {
                // this should not happen because dry run
                // should have already filtered the
                // "nothing-to-merges"
                reportUnexpectedNothingToMerge(changeList);
              } else if (result.getCode() == MergeChangeList.RESULT_CONFLICTS) {
                // this should not happen because dry run
                // should have already detected unmergeable
                // and we would not get here.
                reportUnexpectedConflict(changeList);
                // cannot continue
                break;
              } else if (result.getCode() == MergeChangeList.RESULT_SUCCESS) {
                // check in
                final String description = new MergedChangeListDescriptionGenerator().generateDescription(changeList.getDescription(), changeList.getNumber(), changeList.getUser(), mergeConfiguration.getMarker(), !mergeConfiguration.isPreserveMarker(), mergeConfiguration.getBranchViewName(), mergeConfiguration.isReverseBranchView());
                perforce.submit(clientName, description);
                setMergeChangeListResult(mergeChangeList, MergeChangeList.RESULT_SUCCESS, MERGE_WAS_SUCCESSFUL);
              } else {
                ErrorManagerFactory.getErrorManager().reportSystemError(new Error(UNEXPECTED_MERGE_RESULT + result));
              }
            }

            //
          } else {
            MergeDAO.getInstance().setMergeResult(mergeID, Merge.RESULT_VALIDATION_FAILED);
          }
        }
      }
    } else {
      // Here we mark merge as not-mergeable. It means that
      // the merge group cointains change lists that cannot
      // be merged. The details are in actual
      // MergeChangeLists. The emails should have been sent
      // already.
      MergeDAO.getInstance().setMergeResult(mergeID, Merge.RESULT_CONFLICTS);
    }
    if (log.isDebugEnabled()) {
      log.debug("======================= end merge queue =========================");
    }
  }


  /**
   * Does a clean sync to the change changelist.
   *
   * @param perforce
   * @param clientName
   * @param agent
   * @param changeListToSync
   * @throws IOException
   * @throws CommandStoppedException
   * @throws BuildException
   * @throws ValidationException
   */
  private void syncToChangeListCleanly(final P4SourceControl perforce, final String clientName, final Agent agent, final ChangeList changeListToSync) throws IOException, CommandStoppedException, BuildException, ValidationException, AgentFailureException {// revert, just to be on the safe side
    perforce.revert(clientName);
    agent.deleteCheckoutDir();
    perforce.syncToChangeList(changeListToSync.getChangeListID(), clientName);
  }


  private ChangeListMergeResult mergeChangeList(final ChangeList changeList, final P4SourceControl perforce,
                                                final String clientName, final BranchMergeConfiguration mergeConfiguration)
          throws IOException, CommandStoppedException, BuildException, ValidationException, AgentFailureException {

    final String changeListNumberToIntegrate = changeList.getNumber();
    final int[] integrationCount = new int[1];
    perforce.integrate(clientName, changeListNumberToIntegrate, mergeConfiguration.getBranchViewName(), mergeConfiguration.isReverseBranchView(), false, mergeConfiguration.isIndirectMerge(), new P4IntegrateParserDriver() {
      public void foundIntegration(final Integration integration) {
        integrationCount[0]++;
      }
    });

    // exit if integration count equal zero
    if (integrationCount[0] == 0) {
      return new ChangeListMergeResult(MergeChangeList.RESULT_NOTHING_TO_MERGE, "Nothing to merge");
    }

    // resolve
    final List conflicts = new ArrayList(MAX_CONFLICTS);
    final int[] conflictCount = new int[1];
    perforce.resolve(clientName, ResolveMode.AM, new P4ResolveParserImpl(), new P4ResolveDriver() {
      public void process(final Resolve resolve) {
        if (resolve.getConflicting() > 0) {
          conflictCount[0] += resolve.getConflicting();
          if (conflicts.size() < MAX_CONFLICTS) {
            conflicts.add(resolve);
          }
        }
      }
    });

    // handle resolve result
    if (conflictCount[0] != 0) {
      // if required, use automatic conflict resolution
      if (mergeConfiguration.getConflictResolutionMode() == MergeConfiguration.CONFLICT_RESOLUTION_MODE_ACCEPT_YOURS) {
        perforce.resolve(clientName, ResolveMode.AY, new P4DummyResolveParser(), new P4DummyResolveDriver());
      } else if (mergeConfiguration.getConflictResolutionMode() == MergeConfiguration.CONFLICT_RESOLUTION_MODE_ACCEPT_THEIRS) {
        perforce.resolve(clientName, ResolveMode.AT, new P4DummyResolveParser(), new P4DummyResolveDriver());
      } else {
        // handle failed resolve
        return new ChangeListMergeResult(MergeChangeList.RESULT_CONFLICTS, "Cannot merge because of conflicts. Number of conflicts: " + conflictCount[0], conflicts);
      }
    }
    return new ChangeListMergeResult(MergeChangeList.RESULT_SUCCESS, MERGE_WAS_SUCCESSFUL);
  }


  private int runValidationBuild(final int targetBuildConfigurationID, final BuildRun lastCleanBuildRun, final P4SourceControl perforce, final Agent agent) {
    final BuildRunner buildRunner = new BuildRunner(targetBuildConfigurationID, new DummyNotificationManager(), new MergerBuildRunnerVersionControlFactory(perforce), new MergerAgentFactory(agent));
    final BuildStartRequest buildStartRequest = new BuildStartRequest();
    buildStartRequest.setCleanCheckout(false);
    buildStartRequest.setRequestType(BuildStartRequest.REQUEST_VERIFICATION);
    buildStartRequest.setBuildRunID(lastCleanBuildRun.getBuildRunID());
    return buildRunner.runBuild(buildStartRequest);
  }


  private void setMergeChangeListResult(final MergeChangeList mergeChangeList, final byte resultCode, final String description) {
    mergeChangeList.setResultCode(resultCode);
    mergeChangeList.setMergeResultDescription(description);
    MergeDAO.getInstance().save(mergeChangeList);
  }


  /**
   * Helper method.
   */
  private static void reportUnexpectedConflict(final ChangeList changeList) {
    ErrorManagerFactory.getErrorManager().reportSystemError(new Error("Integrate for a change list unexpectedly reported a conflict: " + changeList));
  }


  /**
   * Helper method.
   */
  private static void reportUnexpectedNothingToMerge(final ChangeList changeList) {
    ErrorManagerFactory.getErrorManager().reportSystemError(new Error("Integrate for a change list unexpectedly reported nothing to merge: " + changeList));
  }


  /**
   * Helper method.
   */
  private void reportErrorWhileProcessingMergeQueue(final Exception e) {
    final Error error = new Error("Error while processing merge queue element: " + StringUtils.toString(e), e);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_MERGE);
    error.setSendEmail(true);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  public String toString() {
    return "P4Merger{" +
            "activeMergeConfigurationID=" + activeMergeConfigurationID +
            ", notificationManager=" + notificationManager +
            '}';
  }
}