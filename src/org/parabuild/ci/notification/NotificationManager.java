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

import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.object.BranchMergeConfiguration;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.versioncontrol.perforce.Resolve;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;

/**
 * Norification manager is responsible for providing notification
 * services, including e-mail and IM
 */
public interface NotificationManager {

  /**
   * Sends out notification that build sequence started. This
   * method should not throw exceptions as this is the last point
   * point where notification can be delivered. If implementing
   * method encounters exceptional conditions, it should use
   * ErrorManager to report errors.
   *
   * @param buildRun      to report
   * @param buildSequence to report
   * @see ErrorManager
   */
  void notifyBuildStepStarted(BuildRun buildRun, BuildSequence buildSequence);


  /**
   * Sends out notification about build sequence results. This
   * method should not throw exceptions as this is the last point
   * point where notification can be delivered. If implementing
   * method encounters exceptional conditions, it should use
   * ErrorManager to report errors.
   *
   * @param stepRun to report
   * @see ErrorManager
   */
  void notifyBuildStepFinished(StepRun stepRun);


  /**
   * Sets version control user e-mail map to be respected when
   * generating "To:" list. If a user defined in the source
   * control map, it will be used first.
   */
  void setVCSUserMap(Map sourceControlUsersMap);


  /**
   * Sends out notification that build sequence hung. This method
   * should not throw exceptions as this is the last point point
   * where notification can be delivered. If implementing method
   * encounters exceptional conditions, it should use
   * ErrorManager to report errors.
   *
   * @param buildRun      to report
   * @param buildSequence to report
   * @see ErrorManager
   */
  void notifyBuildStepHung(BuildRun buildRun, BuildSequence buildSequence);


  /**
   * Sends notification about error to build administrator
   *
   * @see
   */
  void notifyBuildAdministrator(Error error);


  /**
   * Sends user password in accordance with user email in the
   * DB.
   */
  void sendUserPassword(String userName, String newPassword);


  /**
   * Sends out notification that build run failed with system
   * error. This method should not throw exceptions as this is
   * the last point point where notification can be delivered. If
   * implementing method encounters exceptional conditions, it
   * should use ErrorManager to report errors.
   *
   * @param buildRun that has failed
   * @param e        Exception at failure
   * @see ErrorManager
   */
  void notifyBuildStepFailed(BuildRun buildRun, Exception e);


  /**
   * Sends synchronous test e-mail notification message
   *
   * @param properties - list of SystemProperty objects.
   * @see SystemProperty#BUILD_ADMIN_EMAIL
   * @see SystemProperty#BUILD_ADMIN_NAME
   * @see SystemProperty#SMTP_SERVER_NAME
   * @see SystemProperty#SMTP_SERVER_PASSWORD
   * @see SystemProperty#SMTP_SERVER_PORT
   * @see SystemProperty#SMTP_SERVER_USER
   */
  void sendTestEmailMessage(List properties) throws MessagingException;


  /**
   * Sends a nag to those in the merge queue defined by activeMergeID
   *
   * @param activeMergeID an ID of merge configuration.
   */
  void notifyChangeListsWaitingForMerge(final int activeMergeID);


  /**
   * Enables notification.
   *
   * @param enable If true messages are sent. If false
   *               messages are not sent. Default is true.
   */
  void enableNotification(final boolean enable);


  /**
   * Sends a notification about merge conflict.
   *
   * @param mergeConfiguration
   * @param changeList
   * @param conflicts          List of {@link Resolve} objects.
   */
  void notifyMergeFailedBecauseOfConflicts(final BranchMergeConfiguration mergeConfiguration, final ChangeList changeList, final List conflicts);
}
