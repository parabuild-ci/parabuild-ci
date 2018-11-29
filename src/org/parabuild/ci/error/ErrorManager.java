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
package org.parabuild.ci.error;

import org.parabuild.ci.notification.NotificationManager;

import java.io.Serializable;
import java.util.List;

/**
 * Error manager is the last resort to report errors that can not
 * be reported though the NotificationManager, or if the
 * NotificationManger fails itself.<br/>
 * <p/>
 * ErrorManger's methods should not throw any exceptions.<br/>
 * <p/>
 * Implementing class should not make assumptions about
 * environment or availability of BT services, like database or
 * notification.<br/>
 *
 * @see NotificationManager
 */
public interface ErrorManager extends Serializable {


  void reportSystemError(Error error);


  int errorCount();


  /**
   * Deletes all error files from new errors directory. This
   * method just delete error files instead of clearing error by
   * moving it to "cleared" directory.
   */
  void clearAllActiveErrors();


  /**
   * Enables/disables sending e-mail notification to a build
   * admin
   */
  void enableNotification(boolean enable);


  boolean isNotificationEnabled();


  /**
   * Clears given error from new errors directory by moving it to
   * "cleared" directory.
   *
   * @param errorID - error ID, i.e. file name w/o ".error"
   * extension.
   */
  void clearActiveError(String errorID);


  /**
   * Loads error. If error can not be found or there was an error
   * while loading, this method will return empty error.
   *
   * @param errorID error ID
   *
   * @return
   */
  Error loadActiveError(String errorID);


  /**
   * Returns list of IDs of active errors.
   *
   * @param maxErrors maximum number of errors to return.
   *
   * @return
   */
  List getActiveErrorIDs(int maxErrors);
}
