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
package org.parabuild.ci.versioncontrol;

import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.MailUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses Surround log generated as a result of <p>sscm.exe
 * lsuser -e -yuser:password -zhost:4900</p> command.
 * <p/>
 * Format of the output is following: <code> User name:
 * Administrator Email type: Email address: User name: test_user
 * Email type:              Internet Email address:
 * imeshev@yahoo.com </code>
 */
final class SurroundUsersParser {

  private static final String TEXT_USER_NAME = "User name:";
  private static final String TEXT_EMAIL_ADDRESS = " Email address:";
  private static final int TEXT_USER_NAME_LENGTH = TEXT_USER_NAME.length();
  private static final int TEXT_EMAIL_ADDRESS_LENGTH = TEXT_EMAIL_ADDRESS.length();

  private final File usersFile;


  public SurroundUsersParser(final File usersFile) {
    this.usersFile = usersFile;
  }


  /**
   * Parses Surround users list.
   *
   * @return Map of users and emails
   */
  public Map parse() {
    final Map result = new HashMap(101);
    BufferedReader br = null;
    try {
      if (usersFile == null) throw new IllegalStateException("Surround file is undefined.");
      br = new BufferedReader(new FileReader(usersFile), 1024);
      String line = null;
      while ((line = br.readLine()) != null) {
        // parse
        if (line.startsWith(TEXT_USER_NAME)) {
          final String user = line.substring(TEXT_USER_NAME_LENGTH).trim();
          line = IoUtils.readToNotPast(br, TEXT_EMAIL_ADDRESS, null);
          if (StringUtils.isBlank(line)) break; // done
          final String email = line.substring(TEXT_EMAIL_ADDRESS_LENGTH).trim();
          if (StringUtils.isBlank(user)) continue;
          if (StringUtils.isBlank(email)) continue;
          if (!MailUtils.isValidEmail(email) && reportInvalidEmail(user, email)) continue;
          result.put(user, email);
        }
      }
      // report if resulting user list was empty - unlikely situation.
      if (result.isEmpty()) {
        reportWarning("For unknown reason, Surround user list was empty", null);
      }
    } catch (final Exception e) {
      reportWarning("Error while getting Surround users.", e);
    } finally {
      IoUtils.closeHard(br);
    }
    return result;
  }


  /**
   * Reports invalid e-mail to administrator
   *
   * @return true - always returns true
   */
  private boolean reportInvalidEmail(final String user, final String email) {
    reportWarning("Invalid e-mail found in Surround users." + " User name: " + user + ", email: " + email, null);
    return true;
  }


  /**
   * Reports warning
   */
  public void reportWarning(final String msg, final Exception ex) {
    final Error err = new Error();
    err.setDescription(msg);
    err.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    if (ex != null) err.setDetails(ex);
    ErrorManagerFactory.getErrorManager().reportSystemError(err);
  }
}
