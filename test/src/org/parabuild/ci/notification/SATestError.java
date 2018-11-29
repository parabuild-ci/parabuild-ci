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
import java.util.*;

import junit.framework.*;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.*;
import org.parabuild.ci.error.Error;

/**
 * Tests error
 */
public class SATestError extends TestCase {

  final File TEST_ERROR_FILE = new File(TestHelper.getTestTempDir(), SATestError.class.getName() + System.currentTimeMillis());

  private org.parabuild.ci.error.Error error = null;
  public static final String TEST_BUILD_NAME = "test_build_name";


  public SATestError(final String s) {
    super(s);
  }


  /**
   * Tests that loading empty error files doesn' t cause
   * appearance of null in the details and description.
   */
  public void test_loadZeroLengthFile() throws IOException {
    TEST_ERROR_FILE.createNewFile(); // create zero length file
    error.load(TEST_ERROR_FILE);     // load
    assertNotNull(error.getTime());
    assertNotNull(error.getDescription());
    assertNotNull(error.getDetails());
    assertNotNull(error.getPossibleCause());
    assertEquals(error.getDescription().indexOf("null"), -1);
    assertEquals(error.getDetails().indexOf("null"), -1);
    assertTrue(error.getPossibleCause().length() > 1);
    assertTrue(!StringUtils.isBlank(error.getProductVersion()));
  }


  /**
   * Tests that test file w/o data does not blow up.
   */
  public void test_bug586() throws IOException {
    error.load(TestHelper.getTestFile("20050324160620513.error"));
    assertNotNull(error.getTime());
  }


  /**
   * Tests that a error is created and stored with non-blank
   * product version.
   */
  public void test_getProductVersion() throws IOException {

    // check created with non-blank version
    assertTrue(!StringUtils.isBlank(error.getProductVersion()));

    // store
    OutputStream os = null;
    try {
      TEST_ERROR_FILE.createNewFile(); // create empty temp file
      os = new FileOutputStream(TEST_ERROR_FILE);
      final Properties content = error.getContent();
      content.store(os, "");
    } finally {
      IoUtils.closeHard(os);
    }

    // load
    error.load(TEST_ERROR_FILE);
    assertTrue(!StringUtils.isBlank(error.getProductVersion()));
  }


  public void test_getSetTime() {
    final Date time = error.getTime();
    assertNotNull(time);

    final Date newTime = new Date();
    error.setTime(newTime);
    assertEquals(newTime.toString(), error.getTime().toString());
  }


  public void test_setBuildName() {
    error.setBuildName(TEST_BUILD_NAME);
    assertEquals(TEST_BUILD_NAME, error.getBuildName());
  }


  public void test_ErrorWarning() {
    final org.parabuild.ci.error.Error objError = Error.newWarning(org.parabuild.ci.error.Error.ERROR_SUBSYSTEM_LOGGING);
    assertEquals(objError.getErrorLevelAsString(), "WARNING");
    assertEquals(objError.getSubsystemName(), "Logging");
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();
    error = new org.parabuild.ci.error.Error();
    IoUtils.deleteFileHard(TEST_ERROR_FILE);
  }


  public static TestSuite suite() {
    return new TestSuite(SATestError.class);
  }
}
