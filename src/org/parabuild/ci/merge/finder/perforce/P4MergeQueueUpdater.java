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
package org.parabuild.ci.merge.finder.perforce;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.ThreadUtils;
import org.parabuild.ci.configuration.BuildRunParticipantVO;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.merge.MergeDAO;
import org.parabuild.ci.object.BranchBuildRunParticipant;
import org.parabuild.ci.object.BranchChangeList;
import org.parabuild.ci.object.BranchMergeConfiguration;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.MergeConfigurationAttribute;
import org.parabuild.ci.object.Merge;
import org.parabuild.ci.object.MergeSourceBuildRun;
import org.parabuild.ci.object.MergeChangeList;

/**
 * Responsible for updating merge queue.
 */
final class P4MergeQueueUpdater {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(P4MergeQueueUpdater.class);


  private static final String MARKER_MERGEALL = "mergeall";
  private static final int MAX_UNMERGED_CHANGE_LIST_SWEEP_BLOCK_SIZE = 100;

  private final BranchMergeConfiguration mergeConfiguration;


  P4MergeQueueUpdater(final BranchMergeConfiguration mergeConfiguration) {

    this.mergeConfiguration = mergeConfiguration;
  }


  /**
   * Updates merge queue with new change lists, if any.
   */
  public void updateMergeQueue() throws CommandStoppedException {
    if (log.isDebugEnabled()) log.debug("=========== begin updateMergeQueue ===========");
    final int activeMergeID = mergeConfiguration.getActiveMergeID();
    final MergeDAO mergeDAO = MergeDAO.getInstance();

    // Go through the list of unmerged change lists and add
    // those that match the automerge criteria to the merge
    // queue. Change lists that are alreat in the merge
    // queue are note added.
    Collection unmergedChangeLists = mergeDAO.getUnmergedChangeLists(activeMergeID, -1, MAX_UNMERGED_CHANGE_LIST_SWEEP_BLOCK_SIZE);
    if (log.isDebugEnabled()) log.debug("unmergedChangeLists.size(): " + unmergedChangeLists.size());
    while (!unmergedChangeLists.isEmpty()) {
      ThreadUtils.checkIfInterrupted();

      int startNextBlockMergeID = -1;
      for (final Iterator i = unmergedChangeLists.iterator(); i.hasNext();) {

        // get objects
        final Object[] unmerged = (Object[])i.next();
        final BranchChangeList branchChangeList = (BranchChangeList)unmerged[0];
        final ChangeList changeList = (ChangeList)unmerged[1];
        startNextBlockMergeID = branchChangeList.getID();

        // find out if this change list should be merged
        if (log.isDebugEnabled()) log.debug("find out if this change list should be merged");
        final String lowerCaseDescription = changeList.getDescription().toLowerCase();
        final String lowerCaseMarker = mergeConfiguration.getMarker().trim().toLowerCase();
        if (log.isDebugEnabled()) log.debug("analizing unmerged branchChangeList: " + branchChangeList);
        if (lowerCaseDescription.contains(lowerCaseMarker) || lowerCaseMarker.equals(MARKER_MERGEALL)) {
          if (log.isDebugEnabled()) log.debug("marker found");
          // find if it is already in the merge queue
          if (log.isDebugEnabled()) log.debug("find if it is already in the merge queue");
          if (!mergeDAO.branchChangeListIsInQueue(branchChangeList.getID())) {
            // merge
            if (log.isDebugEnabled()) log.debug("adding branchChangeList: " + branchChangeList);
            mergeDAO.addToUnvalidatedMergeQueue(branchChangeList);
          }
        }
      }
      unmergedChangeLists = mergeDAO.getUnmergedChangeLists(activeMergeID, startNextBlockMergeID, MAX_UNMERGED_CHANGE_LIST_SWEEP_BLOCK_SIZE);
    }

    // connect unvalidated MergeQueues with build runs
    if (log.isDebugEnabled()) log.debug("connect unvalidated MergeQueues with build runs");

    // find what build configuration and run was checked last time
    if (log.isDebugEnabled()) log.debug("find what build configuration and run was checked last time");
    final Integer lastCheckedBuildConfigID = mergeDAO.getMergeConfigurationAttributeValue(mergeConfiguration.getActiveMergeID(), MergeConfigurationAttribute.SOURCE_BUILD_CONFIGURATION_ID_CHECKED_LAST_TIME, null);
    final Integer lastCheckedBuildRunID = mergeDAO.getMergeConfigurationAttributeValue(mergeConfiguration.getActiveMergeID(), MergeConfigurationAttribute.SOURCE_BUILD_RUN_ID_CHECKED_LAST_TIME, null);
    if (log.isDebugEnabled()) log.debug("lastCheckedBuildConfigID: " + lastCheckedBuildConfigID);
    if (log.isDebugEnabled()) log.debug("lastCheckedBuildRunID: " + lastCheckedBuildRunID);
    final int checkBuildConfigID;
    final int checkBuildRunID;
    if (lastCheckedBuildConfigID == null) {
      // never checked
      checkBuildConfigID = mergeConfiguration.getSourceBuildID();
      checkBuildRunID = BuildRun.UNSAVED_ID;
      if (log.isDebugEnabled()) log.debug("never checked, set checkBuildRunID: " + checkBuildRunID + ", checkBuildRunID: " + checkBuildRunID);
    } else {
      // checked before
      if (lastCheckedBuildConfigID == mergeConfiguration.getSourceBuildID()) {
        // last checked build config ID was the same
        checkBuildConfigID = mergeConfiguration.getSourceBuildID();
        if (lastCheckedBuildRunID == null) {
          checkBuildRunID = BuildRun.UNSAVED_ID;
        } else {
          checkBuildRunID = lastCheckedBuildRunID;
        }
      } else {
        // last checked build config is different
        checkBuildConfigID = mergeConfiguration.getSourceBuildID();
        checkBuildRunID = BuildRun.UNSAVED_ID;
        if (log.isDebugEnabled()) log.debug("checked before, set checkBuildRunID: " + checkBuildRunID + ", checkBuildRunID: " + checkBuildRunID);
      }
    }

    // go through the successful build runs
    if (log.isDebugEnabled()) log.debug("go through the successful build runs, checkBuildRunID: " + checkBuildRunID + ", checkBuildConfigID: " + checkBuildConfigID);

    // REVIEWME: simeshev@parabuilci.org -> consider iterator or max/start row results to
    // conserve memory
    final List completedSuccessfulBuildRunIDs = ConfigurationManager.getInstance().getCompletedSuccessfulBuildRunIDs(checkBuildConfigID, checkBuildRunID);
    if (log.isDebugEnabled()) log.debug("completedSuccessfulBuildRunIDs.size(): " + completedSuccessfulBuildRunIDs.size());
    for (final Iterator i = completedSuccessfulBuildRunIDs.iterator(); i.hasNext();) {
      final int buildRunID = (Integer) i.next();
      // get change lists in this


      // REVIEWME: simeshev@parabuilci.org -> consider iterator or max/start row results
      // to conserve memory
      final List buildRunParticipants = ConfigurationManager.getInstance().getBuildRunParticipantsOrderedByDate(buildRunID);
      if (log.isDebugEnabled()) log.debug("buildRunParticipants.size(): " + buildRunParticipants.size());
      int validatedQueueID = Merge.UNSAVED_ID;
      for (int j = 0; j < buildRunParticipants.size(); j++) {
        final BuildRunParticipantVO buildRunParticipantVO = (BuildRunParticipantVO)buildRunParticipants.get(j);
        if (log.isDebugEnabled()) log.debug("processing buildRunParticipantVO: " + buildRunParticipantVO);
        final ChangeList buildRunChangeList = buildRunParticipantVO.getChangeList();

        // connect build run participant to a branch change list
        if (log.isDebugEnabled()) log.debug("connect build run participant to a branch change list");

        // REVIEWME: searching by number etc could be
        // repalced with searching by ID if change lists
        // were the same (searching at change list
        // creation).
        final BranchChangeList branchChangeList = mergeDAO.findBranchChangeList(mergeConfiguration.getActiveMergeID(), buildRunChangeList.getNumber(), buildRunChangeList.getCreatedAt(), buildRunChangeList.getUser());
        if (log.isDebugEnabled()) log.debug("branchChangeList: " + branchChangeList);
        if (branchChangeList != null) {
          // branch change list exists

          // find if connection betwen the build run participant and the branch change list already exists
          if (log.isDebugEnabled()) log.debug("find if connection betwen the build run participant and the branch change list already exists");
          BranchBuildRunParticipant branchBuildRunParticipant = mergeDAO.getBranchBuildRunChangeList(branchChangeList.getID(), buildRunParticipantVO.getParticipantID());
          if (log.isDebugEnabled()) log.debug("found branchBuildRunParticipant: " + branchBuildRunParticipant);
          if (branchBuildRunParticipant == null) {
            // create BranchBuildRunParticipant
            if (log.isDebugEnabled()) log.debug("will create BranchBuildRunParticipant");
            branchBuildRunParticipant = new BranchBuildRunParticipant();
            branchBuildRunParticipant.setBranchChangeListID(branchChangeList.getID());
            branchBuildRunParticipant.setBuildRunParticipantID(buildRunParticipantVO.getParticipantID());
            mergeDAO.save(branchBuildRunParticipant);
            if (log.isDebugEnabled()) log.debug("create branchBuildRunParticipant: " + branchBuildRunParticipant);
          }

          // connect build run and merge queue
          if (log.isDebugEnabled()) log.debug("connect build run and merge queue");

          // find unvalidated branch change list in a queue
          final MergeChangeList unvalidatedMergeChangeList = mergeDAO.findUnvalidatedMergeQueueMember(branchChangeList.getID());
          if (log.isDebugEnabled()) log.debug("found unvalidatedMergeChangeList: " + unvalidatedMergeChangeList);

          // add to validated queue
          if (log.isDebugEnabled()) log.debug("add to validated queue");
          if (unvalidatedMergeChangeList != null) {

            // create the valiated queue
            if (validatedQueueID == Merge.UNSAVED_ID) {
              if (log.isDebugEnabled()) log.debug("create the valiated queue");
              final Merge validatedQueue = new Merge();
              validatedQueue.setMergeConfigurationID(branchChangeList.getMergeConfigurationID());
              validatedQueue.setValidated(true);
              validatedQueue.setCreated(new Date());
              mergeDAO.save(validatedQueue);
              if (log.isDebugEnabled()) log.debug("validatedQueue: " + validatedQueue);

              // assign so that other elements of the cycle
              // can use it to add branch change lists
              validatedQueueID = validatedQueue.getID();

              // add reference to build run
              if (log.isDebugEnabled()) log.debug("add reference to build run");
              final MergeSourceBuildRun validatedSourceBuildRun = new MergeSourceBuildRun();
              validatedSourceBuildRun.setMergeID(validatedQueue.getID());
              validatedSourceBuildRun.setBuildRunID(buildRunID);
              mergeDAO.save(validatedSourceBuildRun);
              if (log.isDebugEnabled()) log.debug("validatedSourceBuildRun: " + validatedSourceBuildRun);
            }

            // delete unvalidated branch change list
            if (log.isDebugEnabled()) log.debug("delete unvalidated branch change list");
            mergeDAO.deleteMergeQueueMember(unvalidatedMergeChangeList.getID());

            // add the branch change lists to the validated queue
            if (log.isDebugEnabled()) log.debug("add the branch change lists to the validated queue");
            final MergeChangeList validatedChangeList = new MergeChangeList();
            validatedChangeList.setBranchChangeListID(branchChangeList.getID());
            validatedChangeList.setMergeID(validatedQueueID);
            mergeDAO.save(validatedChangeList);
            if (log.isDebugEnabled()) log.debug("validatedMergeQueueMember: " + validatedChangeList);
          }
        }
      }
    }


    if (log.isDebugEnabled()) log.debug("=========== end updateMergeQueue ===========");
  }


  public String toString() {
    return "P4MergeQueueUpdater{" +
      "mergeConfiguration=" + mergeConfiguration +
      '}';
  }
}
