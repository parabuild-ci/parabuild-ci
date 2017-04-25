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
package org.parabuild.ci.common;

import javax.mail.internet.*;

/**
 * Holds mail-related utility methods.
 */
public final class MailUtils {

  /**
   * Utility class constructor.
   */
  private MailUtils() {
  }


  /**
   * Check if it's a valid e-mail
   *
   * @param eMail String to validate
   *
   * @return true if e-mail is valid
   */
  public static boolean isValidEmail(final String eMail) {
    try {
      final InternetAddress[] addresses = InternetAddress.parse(eMail);
      if (addresses == null || addresses.length == 0) return false;
      for (int i = 0; i < addresses.length; i++) {
        final InternetAddress address = addresses[i];
        address.validate();
      }
      return true;
    } catch (AddressException e) {
      return false;
    }
  }


  /**
   * Checks if supplied e-mail domain is valid
   */
  public static boolean isValidEmailDomain(final String domain) {
    return isValidEmail("test" + '@' + domain);
  }
}
