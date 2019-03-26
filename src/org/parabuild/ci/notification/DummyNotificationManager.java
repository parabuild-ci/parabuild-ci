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
package org.parabuild.ci.notification;

import java.util.*;

import org.parabuild.ci.object.*;

/**
 * This notification manager is a stateless, dummy notification
 * manager does not send any notifications and serves as a
 * disabled notification manager.
 *
 * @see NotificationManagerFactory#makeDummyNotificationManager()
 */
public final class DummyNotificationManager implements NotificationManager {

  /**
   * Does nothing.
   */
  public void notifyBuildStepStarted(final BuildRun buildRun, final BuildSequence buildSequence) {
    // do nothing
  }


  /**
   * Does nothing.
   */
  public void notifyBuildStepFinished(final StepRun stepRun) {
    // do nothing
  }


  /**
   * Does nothing.
   */
  public void setVCSUserMap(final Map sourceControlUsersMap) {
    // do nothing
  }


  /**
   * Does nothing.
   */
  public void notifyBuildStepHung(final BuildRun buildRun, final BuildSequence buildSequence) {
    // do nothing
  }


  /**
   * Does nothing.
   */
  public void notifyBuildAdministrator(final org.parabuild.ci.error.Error error) {
    // do nothing
  }


  /**
   * Does nothing.
   */
  public void sendUserPassword(final String userName, final String newPassword) {
    // do nothing
  }


  /**
   * Does nothing.
   */
  public void notifyBuildStepFailed(final BuildRun buildRun, final Exception e) {
    // do nothing
  }


  public void sendTestEmailMessage(final List properties) {
    // do nothing
  }


  public void notifyChangeListsWaitingForMerge(final int activeMergeID) {
    // do nothing
  }


  public void enableNotification(final boolean enable) {
    // do nothing
  }


  public void notifyMergeFailedBecauseOfConflicts(final BranchMergeConfiguration mergeConfiguration, final ChangeList changeList, final List conflicts) {
    // do nothing
  }
}
