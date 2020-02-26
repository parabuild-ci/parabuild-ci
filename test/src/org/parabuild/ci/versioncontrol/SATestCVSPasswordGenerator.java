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

import java.io.*;

import junit.framework.*;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.util.*;


/**
 * Tests CVSSourceControl
 */
public class SATestCVSPasswordGenerator extends TestCase {

  private static final String TEST_CVS_ROOT = "test_cvs_root";
  private static final String TEST_PASS_FILE_NAME = TestHelper.getTestTempDir().getAbsolutePath() + File.separator + SATestCVSPasswordGenerator.class.getName() + ".cvspass";
  private static final String TEST_PASSWORD_1 = "test_cvs_password_1";
  private static final String TEST_RESULT_LINE_1 = "test_cvs_root A,dZ,8h<Z8:yZZ30 e84";

  private CVSPasswordGenerator passwordGenerator = null;


  public SATestCVSPasswordGenerator(final String s) {
    super(s);
  }


  public void test_generatePassword() throws Exception {
    deleteTestPasswFile();
    passwordGenerator.setPassword(TEST_PASSWORD_1);
    passwordGenerator.setCVSRoot(TEST_CVS_ROOT);
    final String result = passwordGenerator.generatePassword();

    assertTrue(result.length() > 0);
    checkCorrect(result, TEST_RESULT_LINE_1);

    // now file exists, write again
    passwordGenerator.generatePassword();
    checkCorrect(result, TEST_RESULT_LINE_1);
  }


  private void checkCorrect(final String password, final String testLine) throws IOException {
    final BufferedReader fr = new BufferedReader(new StringReader(password));
    final String passLine = fr.readLine();
    assertNotNull(passLine);
    assertEquals(testLine, passLine);
    IoUtils.closeHard(fr);
  }


  protected void setUp() throws Exception {
    super.setUp();

    passwordGenerator = new CVSPasswordGenerator();
  }


  private void deleteTestPasswFile() {
    final File passFile = new File(TEST_PASS_FILE_NAME);
    if (passFile.exists()) passFile.delete();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestCVSPasswordGenerator.class);
  }
}
