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
package org.parabuild.ci.versioncontrol.perforce;

import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.MailUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Parses P4 change log generated as a result of
 * <p>p4 -s users</p> command.
 * <p/>
 * Format of the output is following:
 * <code>
 * info: system <system@parabuildci.org> (system) accessed 2003/06/16
 * info: vimeshev <simeshev@parabuilci.org> (Slava Imeshev) accessed 2003/11/23
 * exit: 0
 * </code>
 */
final class P4UsersParser {

  private File usersFile = null;
  private boolean caseSensitiveUserNames = true;


  /**
   * Sets file to parse
   */
  public void setUsersFile(final File usersFile) {
    this.usersFile = usersFile;
  }


  /**
   * Parses P4 change log generated as a result of
   * <p>p4 -s users</p> command.
   *
   * @return Map of users and emails
   */
  public Map parse() {
    final Map result = new HashMap(101);
    BufferedReader br = null;
    try {
      if (usersFile == null) {
        throw new IllegalStateException("P4 users file is undefined.");
      }
      br = new BufferedReader(new FileReader(usersFile), 1024);
      String line = null;
      while ((line = br.readLine()) != null) {
        // check for errors
        validateLine(line);
        // parse
        if (line.startsWith(P4ParserHelper.P4_INFO)) {
          final StringTokenizer st = new StringTokenizer(line);
          st.nextToken();// skip info token
          final String user = st.nextToken(); // user
          final String rawEmail = st.nextToken(); // p4 email
          final StringTokenizer stEmail = new StringTokenizer(rawEmail, "<>");
          final String email = stEmail.nextToken();
          if (StringUtils.isBlank(user)) {
            continue;
          }
          if (StringUtils.isBlank(email)) {
            continue;
          }
          if (!MailUtils.isValidEmail(email) && reportInvalidEmail(user, email)) {
            continue;
          }
          result.put(caseSensitiveUserNames ? user : user.toLowerCase(), email);
        } else if (line.startsWith(P4ParserHelper.P4_EXIT_0)) {
          break;
        }
      }
      // report if resulting user list was empty - unlikely situation.
      if (result.isEmpty()) {
        reportWarning("For unknown reason, P4 user list was empty. This may be a result of a previous infrastructure error.", null);
      }
    } catch (final Exception e) {
      reportWarning("Error while getting P4 users.", e);
    } finally {
      IoUtils.closeHard(br);
    }
    return result;
  }


  /**
   * Validates that line being parsed does not containt P4 errors
   */
  private void validateLine(final String line) throws BuildException {
    if (line.startsWith(P4ParserHelper.P4_EXIT_1) || line.startsWith(P4ParserHelper.P4_ERROR)) {
      throw new BuildException(makeUnexpectedErrorMessage(line));
    }
  }


  /**
   * Helper method to compose error message string
   *
   * @param error to process
   * @return resulting message
   */
  private static String makeUnexpectedErrorMessage(final String error) {
    return "Error while getting P4 users: " + error;
  }


  /**
   * Reports invalid e-mail to administrator
   *
   * @return true - always returns true
   */
  private static boolean reportInvalidEmail(final String user, final String email) {
    reportWarning("Invalid e-mail found in P4 users." + " User name: " + user + ", email: " + email, null);
    return true;
  }


  /**
   * Reports warning
   */
  public static void reportWarning(final String msg, final Exception ex) {
    final org.parabuild.ci.error.Error err = new org.parabuild.ci.error.Error();
    err.setDescription(msg);
    err.setErrorLevel(org.parabuild.ci.error.Error.ERROR_LEVEL_WARNING);
    if (ex != null) {
      err.setDetails(ex);
    }
    final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.reportSystemError(err);
  }


  /**
   * Sets is user names are case sensitive.
   *
   * @param caseSensitiveUserNames
   */
  public void setCaseSensitiveUserNames(final boolean caseSensitiveUserNames) {
    this.caseSensitiveUserNames = caseSensitiveUserNames;
  }


  public String toString() {
    return "P4UsersParser{" +
            "usersFile=" + usersFile +
            ", caseSensitiveUserNames=" + caseSensitiveUserNames +
            '}';
  }
}
