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

import java.io.*;

/**
 * NotificationManagerFactory is a factory class that delivers
 * isolation of NotificationManager clients from implementation
 * details of the NotificationManager and the way how it's
 * created.
 */
public final class NotificationManagerFactory implements Serializable {

  private static final long serialVersionUID = 7143257488995569178L; // NOPMD
  private static final NotificationManager DUMMY_NOTIFICATION_MANAGER = new DummyNotificationManager();


  /**
   * Factory constuctor.
   */
  private NotificationManagerFactory() {
  }


  /**
   * Returns implementation of NotificationManager.
   *
   * @return object implementing NotificationManager.
   */
  public static NotificationManager makeNotificationManager() {
    final CompositeNotificationManager cnm = new CompositeNotificationManager();
    cnm.add(new EmailNotificationManager());
    cnm.add(new JabberNotificationManager());
    return cnm;
  }


  /**
   * @return NotificationManager that does not send any
   *         notifications.
   */
  public static NotificationManager makeDummyNotificationManager() {
    // return stateless dummy notification manager.
    return DUMMY_NOTIFICATION_MANAGER;
  }
}
