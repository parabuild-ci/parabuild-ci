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
package org.parabuild.ci.merge.merger.nag;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.merge.MergeDAO;
import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.merge.merger.Merger;
import org.parabuild.ci.notification.NotificationManager;
import org.parabuild.ci.notification.NotificationManagerFactory;
import org.parabuild.ci.object.ActiveMergeConfiguration;
import org.parabuild.ci.object.MergeConfigurationAttribute;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.versioncontrol.SourceControl;
import org.parabuild.ci.versioncontrol.VersionControlFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

/**
 * Nagging merge sends nags instead of doing a merge.
 */
public final class NaggingMerger implements Merger {

  private final int activeMergeID;


  public NaggingMerger(final int activeMergeID) {

    this.activeMergeID = activeMergeID;
  }


  public void merge() {

    // today
    final int todayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    // check if this is time to send a message
    MergeConfigurationAttribute mca = MergeDAO.getInstance().getMergeConfigurationAttribute(activeMergeID, MergeConfigurationAttribute.NAG_DAY_SENT_LAST_TIME);
    if (mca == null || mca.getIntValue() < todayDay) {
      // go through the merge queue and send messages
      sendNags();
      // update
      if (mca == null) {
        mca = new MergeConfigurationAttribute();
        mca.setMergeConfigurationID(activeMergeID);
        mca.setName(MergeConfigurationAttribute.NAG_DAY_SENT_LAST_TIME);
      }
      mca.setValue(todayDay);
      MergeDAO.getInstance().save(mca);
    }
  }


  /**
   * Sends integration nags to those in the merge queue.
   */
  private void sendNags() {
    final NotificationManager nm = NotificationManagerFactory.makeNotificationManager();
    final ActiveMergeConfiguration activeConfiguration = MergeManager.getInstance().getActiveMergeConfiguration(activeMergeID);
    final int targetBuildID = activeConfiguration.getTargetBuildID();
    try {
      final Map map = getUserMapFromVCS(targetBuildID);
      nm.setVCSUserMap(map);
    } catch (Exception e) {
      final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
      errorManager.reportSystemError(new org.parabuild.ci.error.Error("Error while getting user to e-mail mapping from version control system: " + StringUtils.toString(e), e));
    }
    nm.notifyChangeListsWaitingForMerge(activeMergeID);
  }


  private Map getUserMapFromVCS(final int targetBuildID) throws IOException, CommandStoppedException, AgentFailureException {
    final SourceControl targetSourceControl = VersionControlFactory.makeVersionControl(ConfigurationManager.getInstance().getBuildConfiguration(targetBuildID));
    // NOTE: simeshev@parabuilci.org - 2009-02-23 - it is OK to use next because we need only user map
    final AgentHost agentHost = AgentManager.getInstance().getNextLiveAgentHost(targetBuildID);
    targetSourceControl.setAgentHost(agentHost);
    return targetSourceControl.getUsersMap();
  }


  public String toString() {
    return "NaggingMerger{" +
            "activeMergeID=" + activeMergeID +
            '}';
  }
}
