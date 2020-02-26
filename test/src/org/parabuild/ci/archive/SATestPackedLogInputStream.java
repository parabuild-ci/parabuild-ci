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
package org.parabuild.ci.archive;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.archive.internal.PackedLogInputStream;
import org.parabuild.ci.util.IoUtils;

import java.io.File;
import java.io.IOException;

/**
 * Tests PackedLogInputStream
 */
public class SATestPackedLogInputStream extends TestCase {

  private static final Log log = LogFactory.getLog(SATestPackedLogInputStream.class);
  public static final File TEST_DIR_TOZIP = new File(TestHelper.getTestDataDir(), "html_log");
  public static final File TEST_ZIPPED_FILE = new File(TestHelper.getTestTempDir(), SATestPackedLogInputStream.class.getName() + ".zip");
  public static final String TEST_ENTRY_NAME = "com/meterware/servletunit/InvocationContext.html";


  public SATestPackedLogInputStream(final String s) {
    super(s);
  }


  public void test_createFailsOnNullZipFile() {
    try {
      new PackedLogInputStream(null, TEST_ENTRY_NAME);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
    }
  }


  public void test_createFailsOnNonExistingZipFile() {
    try {
      new PackedLogInputStream(new File(Long.toString(System.currentTimeMillis()) + ".zip"), TEST_ENTRY_NAME);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
    }
  }


  public void test_createFailsOnFileWithNonZipException() {
    try {
      new PackedLogInputStream(new File(Long.toString(System.currentTimeMillis()) + ".zip"), TEST_ENTRY_NAME);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
    }
  }


  public void test_createFailsOnBlankEntryName() throws IOException {
    createZippedFileFromDir();
    try {
      new PackedLogInputStream(TEST_ZIPPED_FILE, "");
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
    }
  }


  public void test_read() throws IOException {
    // preExecute - zip dir
    createZippedFileFromDir();

    // create steam
    final PackedLogInputStream inputStream = new PackedLogInputStream(TEST_ZIPPED_FILE, TEST_ENTRY_NAME);

    // read
    final String originalItem = IoUtils.fileToString(new File(TEST_DIR_TOZIP, TEST_ENTRY_NAME));
    final String packedItem = IoUtils.inputStreamToString(inputStream);

    // compare
    assertEquals(originalItem, packedItem);
  }


  private void createZippedFileFromDir() throws IOException {
    IoUtils.zipDir(TEST_DIR_TOZIP, TEST_ZIPPED_FILE);
    TestHelper.assertExists(TEST_ZIPPED_FILE);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestPackedLogInputStream.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    assertTrue(IoUtils.deleteFileHard(TEST_ZIPPED_FILE));
  }
}
