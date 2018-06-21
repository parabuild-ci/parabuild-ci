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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.MailUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to return CVS user to e-mail mapping based on the content of the CVSROOT/users.
 */
final class CVSUsersParser {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(CVSUsersParser.class); // NOPMD

  private File usersFile = null;
  private String cvsRoot = null;
  private int buildID = BuildConfig.UNSAVED_ID;


  /**
   * Sets mandatory users file to parse
   *
   * @param usersFile
   */
  public void setUsersFile(final File usersFile) {
    this.usersFile = usersFile;
  }


  /**
   * Sets optional CVSROOT used to compose a error message to administrator.
   */
  public void setCVSRoot(final String cvsRoot) {
    this.cvsRoot = cvsRoot;
  }


  /**
   * Optinally sets build ID. This is used for error reporting purposes.
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Parses content of the CVSROOT/users file
   */
  public Map parse() {
    final Map result = new HashMap(23);
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(usersFile), 1024);
      String line = null;
      while ((line = br.readLine()) != null) {
        final int colonPosition = line.indexOf(':');
        if (colonPosition <= 0) continue;
        final String user = line.substring(0, colonPosition);
        final String email = line.substring(colonPosition + 1);
        if (StringUtils.isBlank(user)) continue;
        if (StringUtils.isBlank(email)) continue;
        if (!MailUtils.isValidEmail(email)) {
          reportWarning(appendInfoAboutCVSROOT("Invalid e-mail found in CVSROOT/users: " + email + '.'), null);
          continue;
        }
        result.put(user.trim(), email);
      }
    } catch (IOException e) {
      reportWarning(appendInfoAboutCVSROOT("Can not retrieve user e-mails from CVSROOT/users."), e);
      return result; // return empty result
    } finally {
      IoUtils.closeHard(br);
    }
    return result;
  }


  /**
   * Reports warning
   */
  private void reportWarning(final String msg, final Exception ex) {
    final Error err = new Error();
    err.setDescription(msg);
    err.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    err.setSubsystemName(Error.ERROR_SUSBSYSTEM_SCM);
    err.setBuildID(buildID);
    if (ex != null) {
      err.setDetails(ex);
      err.setPossibleCause("CVSROOT/users for this build may be not accessble or misformed. Make it accessible and make sure it conatains correct e-mail addresses or disable retrieving user's e-mails from CVSROOT/users.");
    }
    final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.reportSystemError(err);
  }


  /**
   * If set, appends information about CVSROOT to the given string.
   */
  private String appendInfoAboutCVSROOT(final String s) {
    if (!StringUtils.isBlank(cvsRoot)) return s + " CVSROOT is \"" + cvsRoot + "\".";
    return s;
  }
}
