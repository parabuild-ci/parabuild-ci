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

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.ThreadUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ChangeListsAndIssues;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.merge.MergeClientNameGenerator;
import org.parabuild.ci.merge.MergeDAO;
import org.parabuild.ci.object.BranchChangeList;
import org.parabuild.ci.object.BranchMergeConfiguration;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.versioncontrol.perforce.P4ChangeListDriver;
import org.parabuild.ci.versioncontrol.perforce.P4SourceControl;

/**
 * NewChangeListsFinder is responsible for finding all new
 * changes that occured in the "From".
 * <p/>
 * Some of them will later be found as integrated "To", some
 * of them are subject of automerge.
 */
final class P4NewChangeListsFinder {

  private static final Log log = LogFactory.getLog(P4NewChangeListsFinder.class);

  private static final int CHANGE_LIST_BLOCK_SIZE = 500;

  private final BranchMergeConfiguration mergeConfiguration;


  P4NewChangeListsFinder(final BranchMergeConfiguration mergeConfiguration) {
    this.mergeConfiguration = mergeConfiguration;
  }


  /**
   * Finds change lists starting since a give change list.
   *
   * @param startNumber
   * @throws ValidationException
   */
  public void findAndStoreNewChangesSince(final int startNumber) throws ValidationException, CommandStoppedException, BuildException, IOException, AgentFailureException {
    if (log.isDebugEnabled())
      log.debug("=========== begin finiding new change lists from startNumber: " + startNumber + " ===========");

    // Create client view for the source part of the branch view.
    final P4BranchViewToClientViewTransformer branchViewTransformer = new P4BranchViewToClientViewTransformer(mergeConfiguration.getBranchView(), mergeConfiguration.isReverseBranchView());
    final String clientView = branchViewTransformer.transformToSourceClientView();
    if (log.isDebugEnabled()) log.debug("clientView: " + clientView);

    // Make client name
    final MergeClientNameGenerator mergeClientNameGenerator = new MergeClientNameGenerator(mergeConfiguration.getActiveMergeID());
    final String clientName = mergeClientNameGenerator.generateSourceClientName();
    if (log.isDebugEnabled()) log.debug("clientName: " + clientName);

    // Create perforce source control
    final BuildConfig buildConfiguration = ConfigurationManager.getInstance().getBuildConfiguration(mergeConfiguration.getSourceBuildID());
    final AgentHost agentHost = AgentManager.getInstance().getNextLiveAgentHost(buildConfiguration.getActiveBuildID());
    final Agent agent = AgentManager.getInstance().createAgent(buildConfiguration.getActiveBuildID(), agentHost);
    final P4SourceControl perforce = new P4SourceControl(buildConfiguration);
    perforce.setAgentHost(agentHost);

    // The idea here is that we go over the sets of blocks
    // starting from the last change list and then go down
    // untils there are no any
    int endNumber = 9999999;
    int count = CHANGE_LIST_BLOCK_SIZE;
    while (count >= CHANGE_LIST_BLOCK_SIZE) {
      final MyP4ChangeListDriver changeDriver = new MyP4ChangeListDriver(mergeConfiguration.getID());
      perforce.getChanges(Integer.toString(startNumber), Integer.toString(endNumber), CHANGE_LIST_BLOCK_SIZE, clientName, agent.getTempDirName(), ".", clientView, false, changeDriver);
      ThreadUtils.checkIfInterrupted();
      endNumber = changeDriver.getMinNumber() - 1;
      count = changeDriver.getCount();
      if (log.isDebugEnabled()) log.debug("changeDriver: " + changeDriver);
    }
    if (log.isDebugEnabled()) log.debug("=========== end finiding new change lists ===========");
  }


  /**
   * A driver for new chages found.
   *
   * @see P4SourceControl
   */
  private static final class MyP4ChangeListDriver implements P4ChangeListDriver {

    private final int mergeConfigurationID;
    private int minNumber = 0;
    private int count = 0;


    MyP4ChangeListDriver(final int mergeConfigurationID) {
      this.mergeConfigurationID = mergeConfigurationID;
    }


    /**
     * Perforce's getChanges calls this method when the changes are collected.
     *
     * @param changeListsAndIssues {@link ChangeListsAndIssues}
     */
    public void process(final ChangeListsAndIssues changeListsAndIssues) {
      minNumber = 0;
      count = 0;
      final List changeLists = changeListsAndIssues.getChangeLists();
      if (log.isDebugEnabled()) log.debug("will process changeLists, size: " + changeLists.size());
      if (changeLists.isEmpty()) return;
      count = changeLists.size();
      minNumber = getNumberAsInteger((ChangeList) changeLists.get(0));
      final MergeDAO mergeDAO = MergeDAO.getInstance();
      for (int i = 0; i < count; i++) {
        final ChangeList changeList = (ChangeList) changeLists.get(i);
        //
        // save change list
        // REVIEWME: simeshev@parabuilci.org -> add check for the description
        final List foundChangelists = mergeDAO.findChangeList(changeList.getNumber(), changeList.getCreatedAt(), changeList.getUser());
        if (log.isDebugEnabled()) log.debug("foundChangelists size: " + foundChangelists.size());
        final int changeListID;
        if (foundChangelists.isEmpty()) {
          changeListID = ConfigurationManager.getInstance().saveChangeList(changeList);
        } else {
          changeListID = ((ChangeList) foundChangelists.get(0)).getChangeListID();
        }
        //
        // save branch change list
        // REVIEWME: simeshev@parabuilci.org -> add look up for existing branch change list
        BranchChangeList branchChangeList = mergeDAO.getBrachChangeList(mergeConfigurationID, changeListID);
        if (branchChangeList == null) {
          if (log.isDebugEnabled()) log.debug("will create branchChangeList");
          branchChangeList = new BranchChangeList();
          branchChangeList.setChangeListID(changeListID);
          branchChangeList.setMergeConfigurationID(mergeConfigurationID);
          branchChangeList.setMergeStatus(BranchChangeList.MERGE_STATUS_UNKNOWN);
          mergeDAO.save(branchChangeList);
          if (log.isDebugEnabled()) log.debug("created branchChangeList: " + branchChangeList);
        } else {
          if (log.isDebugEnabled()) log.debug("found branchChangeList: " + branchChangeList);
        }
        //
        // calculate min/max
        final int number = getNumberAsInteger(changeList);
        minNumber = Math.min(minNumber, number);
        if (log.isDebugEnabled()) log.debug("minNumber: " + minNumber);
      }
    }


    /**
     * @return number of found and saved change lists
     */
    public int getCount() {
      return count;
    }


    /**
     * Helper method.
     */
    private int getNumberAsInteger(final ChangeList first) {
      return Integer.parseInt(first.getNumber());
    }


    public String toString() {
      return "MyP4ChangeListDriver{" +
              "mergeConfigurationID=" + mergeConfigurationID +
              ", minNumber=" + minNumber +
              ", count=" + count +
              '}';
    }


    public int getMinNumber() {
      return minNumber;
    }
  }


  public String toString() {
    return "P4NewChangeListsFinder{" +
            "mergeConfiguration=" + mergeConfiguration +
            '}';
  }
}
