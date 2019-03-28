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

/**
 * User to email map.
 *
 * @see EmailRecipientListComposer
 *
 * @since Dec 28, 2008 1:38:42 PM
 */
final class UserToEmailMap {

  private final String userName;
  private final String email;
  private final boolean enabled;


  /**
   * Constructor.
   *
   * @param userName user name.
   * @param email email.
   * @param enabled true if enabled, false if disabled.
   */
  UserToEmailMap(final String userName, final String email, final boolean enabled) {
    this.userName = userName;
    this.email = email;
    this.enabled = enabled;
  }


  /**
   * Returns email.
   *
   * @return email.
   */
  public String getEmail() {
    return email;
  }


  /**
   * Returns true if this user is enabled.
   *
   * @return true if this user is enabled.
   */
  public boolean isEnabled() {
    return enabled;
  }


  public String toString() {
    return "UserToEmailMap{" +
            "userName='" + userName + '\'' +
            ", email='" + email + '\'' +
            ", enabled=" + enabled +
            '}';
  }
}
