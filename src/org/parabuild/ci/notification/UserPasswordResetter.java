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

import java.security.*;
import java.util.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.SecurityManager;

/**
 * Resets user password and sends notification.
 */
public final class UserPasswordResetter {

  /**
   * Resets User's password.
   *
   * @param userName
   * @param email
   * @throws FatalConfigurationException if algorithm required by digester is not found.
   */
  public void resetPassword(final String userName, final String email) throws FatalConfigurationException {
    try {
      // store it in the database
      final SecurityManager sm = SecurityManager.getInstance();
      final User user = sm.getUserByNameAndEmail(userName, email);
      if (user == null) return; // don't do anything, we expect that reset is called with validated user name and e-mail.

      // generate a random password
      final Random random = new Random(System.currentTimeMillis());
      int next = random.nextInt();
      if (next < 0) next *= -1;
      final String newPassword = Integer.toString(next);

      // save password
      user.setPassword(StringUtils.digest(newPassword));
      sm.save(user);

      // send notification
      final NotificationManager nm = NotificationManagerFactory.makeNotificationManager();
      nm.sendUserPassword(userName, newPassword);
    } catch (NoSuchAlgorithmException e) {
      throw new FatalConfigurationException(e);
    }
  }
}
