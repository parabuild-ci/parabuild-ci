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

import org.parabuild.ci.util.BuildException;

import java.io.BufferedReader;
import java.io.IOException;


public final class P4ParserHelper {

  public static final String P4_ERROR = "error:";
  public static final String P4_EXIT_0 = "exit: 0";
  public static final String P4_EXIT_1 = "exit: 1";
  public static final String P4_INFO = "info:";
  public static final String P4_INFO_1 = "info1:";
  public static final String P4_TEXT = "text:";
  public static final String P4_TEXT_CHANGE = "text: Change";
  public static final String SPACED_P4_INFO = P4_INFO + ' ';
  public static final int SPACED_P4_INFO_LENGTH = SPACED_P4_INFO.length();


  /**
   * Validates that line being parsed does not containt P4 errors
   *
   * @param line to validate
   *
   * @throws BuildException
   */
  public static String validateLine(final String line) throws BuildException {
    if (line != null && !line.startsWith(P4_INFO)) {
      if (line.startsWith("exit: 1")) {
        throw new BuildException(makeUnexpectedErrorMessage(line));
      } else if (line.startsWith(P4_ERROR)) {
        if (line.startsWith("error: No file(s) to resolve.")) {
          // returned by p4 resolve if nothing open
          return null; // done
        } else if (line.startsWith("error: File(s) not opened on this client.")) {
          // returned by p4 opened if nothing open
          return null; // done
        } else {
          throw new BuildException(makeUnexpectedErrorMessage(line));
        }
      }
    }
    return line;
  }


  /**
   * Helper method to compose error message string
   *
   * @param error to process
   *
   * @return resulting message
   */
  public static String makeUnexpectedErrorMessage(final String error) {
    return "Error while getting P4 changes: " + error;
  }


  public static String readLineAndValidate(final BufferedReader br) throws IOException, BuildException {
    return validateLine(br.readLine());
  }
}
