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
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Writes CVS password to a file
 */
final class CVSPasswordGenerator {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(CVSPasswordGenerator.class); // NOPMD

  private String cvsRoot;
  private String cvsPassword;

  private final char[] shifts = { // NOPMD
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
    16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
    114, 120, 53, 79, 96, 109, 72, 108, 70, 64, 76, 67, 116, 74, 68, 87,
    111, 52, 75, 119, 49, 34, 82, 81, 95, 65, 112, 86, 118, 110, 122, 105,
    41, 57, 83, 43, 46, 102, 40, 89, 38, 103, 45, 50, 42, 123, 91, 35,
    125, 55, 54, 66, 124, 126, 59, 47, 92, 71, 115, 78, 88, 107, 106, 56,
    36, 121, 117, 104, 101, 100, 69, 73, 99, 63, 94, 93, 39, 37, 61, 48,
    58, 113, 32, 90, 44, 98, 60, 51, 33, 97, 62, 77, 84, 80, 85, 223,
    225, 216, 187, 166, 229, 189, 222, 188, 141, 249, 148, 200, 184, 136, 248, 190,
    199, 170, 181, 204, 138, 232, 218, 183, 255, 234, 220, 247, 213, 203, 226, 193,
    174, 172, 228, 252, 217, 201, 131, 230, 197, 211, 145, 238, 161, 179, 160, 212,
    207, 221, 254, 173, 202, 146, 224, 151, 140, 196, 205, 130, 135, 133, 143, 246,
    192, 159, 244, 239, 185, 168, 215, 144, 139, 165, 180, 157, 147, 186, 214, 176,
    227, 231, 219, 169, 175, 156, 206, 198, 129, 164, 150, 210, 154, 177, 134, 127,
    182, 128, 158, 208, 162, 132, 167, 209, 149, 241, 153, 251, 237, 236, 171, 195,
    243, 233, 253, 240, 194, 250, 191, 155, 142, 137, 245, 235, 163, 242, 178, 152
  };


  /**
   * Writes a single-line file containing CVS password
   */
  public final String generatePassword() {
    validate();
    PrintWriter pw = null;
    try {
      final String passwordLine = cvsRoot + " A" + mangle(cvsPassword);
      final StringWriter sw = new StringWriter(200);
      pw = new PrintWriter(sw);
      pw.println(passwordLine);
      pw.flush();
      return sw.toString();
    } finally {
      IoUtils.closeHard(pw);
    }
  }


  private final String mangle(final String password) {
    if (StringUtils.isBlank(password)) return "";
    final StringBuilder buf = new StringBuilder(100);
    for (int i = 0; i < password.length(); i++) {
      buf.append(shifts[password.charAt(i)]);
    }
    return buf.toString();
  }


  /**
   * The CVS repository to add an entry for.
   */
  public final void setCVSRoot(final String cvsRoot) {
    this.cvsRoot = cvsRoot;
  }


  /**
   * Password to be added to the password file.
   */
  public final void setPassword(final String password) {
    this.cvsPassword = password;
  }


  /**
   * Validates that all properties are valid
   */
  private void validate() {
    if (StringUtils.isBlank(cvsRoot)) throw new IllegalStateException("Error while logging in: CVS root is undefined or blank");
  }
}
