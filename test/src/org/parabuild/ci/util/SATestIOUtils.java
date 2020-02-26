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
package org.parabuild.ci.util;

import java.io.*;
import java.util.zip.*;

import junit.framework.*;

import org.parabuild.ci.TestHelper;

/**
 * Tests home page
 */
public class SATestIOUtils extends TestCase {

  public static final File TEST_SOURCE_FILE = new File(TestHelper.getTestDataDir(), "test_ant_successful_build.log");
  public static final File TEST_DESTINATION_FILE = new File(TestHelper.getTestTempDir(), SATestIOUtils.class.getName() + System.currentTimeMillis());

  public static final File TEST_DIR_TO_ZIP = TestHelper.getTestFile("html_log");
  public static final File TEMP_ZIPPPED_FILE = new File(TestHelper.getTestTempDir(), SATestIOUtils.class.getName() + ".zip");


  public SATestIOUtils(final String s) {
    super(s);
  }


  public void test_copyFile() throws IOException {
    IoUtils.copyFile(TEST_SOURCE_FILE, TEST_DESTINATION_FILE);
    assertTrue(TEST_DESTINATION_FILE.exists());
    assertEquals(TEST_SOURCE_FILE.length(), TEST_DESTINATION_FILE.length());
    assertEquals(TEST_SOURCE_FILE.lastModified(), TEST_DESTINATION_FILE.lastModified());
  }


  public void test_breaksIfSourceIsDir() {
    try {
      IoUtils.copyFile(TestHelper.getTestDataDir(), TEST_DESTINATION_FILE);
      TestHelper.failNoExceptionThrown();
    } catch (IOException e) {
    }
  }


  public void test_breaksIfDestinationIsDir() {
    try {
      IoUtils.copyFile(TEST_SOURCE_FILE, TestHelper.getTestTempDir());
      TestHelper.failNoExceptionThrown();
    } catch (IOException e) {
    }
  }


  public void test_isFileUnder() throws IOException {
    assertTrue(IoUtils.isFileUnder(TEST_SOURCE_FILE.getCanonicalPath(), TestHelper.getTestDataDir().getCanonicalPath()));
    final File realtiveParentPath = new File(TestHelper.getTestDataDir(), "../data");
    assertTrue(IoUtils.isFileUnder(TEST_SOURCE_FILE.getCanonicalPath(), realtiveParentPath.getCanonicalPath()));
  }


  public void test_traverseDir() throws Exception {
    final int filesCount[] = new int[]{0};
    final int dirsCount[] = new int[]{0};
    final int processedCount = IoUtils.traverseDir(TestHelper.getTestDataDir(), new DirectoryTraverserCallback() {
      public boolean callback(final File file) {
        if (file.isFile()) filesCount[0]++;
        if (file.isDirectory()) dirsCount[0]++;
        return true;
      }
    });

    assertTrue(filesCount[0] > 0);
    assertTrue(dirsCount[0] > 0);
    assertEquals(processedCount, dirsCount[0] + filesCount[0]);
  }


  public void test_zipDirDoesNotLeaveFileOpen() throws Exception {
    IoUtils.deleteFileHard(TEMP_ZIPPPED_FILE);
    IoUtils.zipDir(TEST_DIR_TO_ZIP, TEMP_ZIPPPED_FILE);
    assertEquals(true, TEMP_ZIPPPED_FILE.exists());
    assertTrue(IoUtils.deleteFileHard(TEMP_ZIPPPED_FILE));
  }


  public void test_zipDir() throws Exception {
    ZipFile zipFile = null;
    try {
      // zipped file
      IoUtils.deleteFileHard(TEMP_ZIPPPED_FILE);

      // zip
      IoUtils.zipDir(TEST_DIR_TO_ZIP, TEMP_ZIPPPED_FILE);
      assertEquals(true, TEMP_ZIPPPED_FILE.exists());

      // check content - "root" file
      zipFile = new ZipFile(TEMP_ZIPPPED_FILE);
      String relativeFileName = "allclasses-frame.html";
      assertEntryExistsAndEqualsOriginal(zipFile, relativeFileName, TEST_DIR_TO_ZIP);

      // check content - "pathed" file
      relativeFileName = "com/meterware/httpunit/javascript/JavaScript.Control.html";
      assertEntryExistsAndEqualsOriginal(zipFile, relativeFileName, TEST_DIR_TO_ZIP);
      IoUtils.closeHard(zipFile);

      // check we can delete file
      assertTrue(IoUtils.deleteFileHard(TEMP_ZIPPPED_FILE));
    } finally {
      IoUtils.closeHard(zipFile);
    }
  }


  public void test_zipFile() throws Exception {
    ZipFile zipFile = null;
    try {
      // to zip
      final File fileToZip = TestHelper.getTestFile("test_ant_failed_build.log");
      assertEquals(true, fileToZip.exists());

      IoUtils.deleteFileHard(TEMP_ZIPPPED_FILE);

      // zip
      IoUtils.zipFile(fileToZip, TEMP_ZIPPPED_FILE);
      assertEquals(true, TEMP_ZIPPPED_FILE.exists());

      // check content
      zipFile = new ZipFile(TEMP_ZIPPPED_FILE);
      assertEntryExistsAndEqualsOriginal(zipFile, "test_ant_failed_build.log", TestHelper.getTestDataDir());
    } finally {
      IoUtils.closeHard(zipFile);
    }
  }


  public void test_isProhibitedPath() throws IOException {
    if (RuntimeUtils.isWindows()) {
      assertTrue(IoUtils.isProhibitedPath(new File("C:\\")));
      assertTrue(IoUtils.isProhibitedPath(new File("D:\\")));
      assertTrue(IoUtils.isProhibitedPath(new File("D:\\\\")));
    } else {
      assertTrue(IoUtils.isProhibitedPath(new File("/")));
    }
  }


  /**
   * Test helper.
   */
  private void assertEntryExistsAndEqualsOriginal(final ZipFile zipFile, final String relativeFileName, final File sourceDire) throws IOException {
    InputStream is = null;
    try {
      final ZipEntry ze = zipFile.getEntry(relativeFileName);
      assertNotNull(ze);
      is = zipFile.getInputStream(ze);
      assertEquals(IoUtils.fileToString(new File(sourceDire, relativeFileName)), IoUtils.inputStreamToString(is));
    } finally {
      IoUtils.closeHard(is);
    }
  }


  protected void setUp() throws Exception {
    super.setUp();
    cleanupDestinationFile();
    assertSourceFileExists();
    assertEquals(true, TEST_DIR_TO_ZIP.exists());
  }


  /**
   * Helper method
   */
  private void assertSourceFileExists() {
    assertTrue(TEST_SOURCE_FILE.exists());
  }


  /**
   * Helper method
   */
  private void cleanupDestinationFile() {
    if (TEST_DESTINATION_FILE.exists()) TEST_DESTINATION_FILE.delete();
    assertTrue(!TEST_DESTINATION_FILE.exists());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestIOUtils.class);
  }
}
