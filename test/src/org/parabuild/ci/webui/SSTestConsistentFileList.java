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
package org.parabuild.ci.webui;

import java.io.*;
import java.util.*;
import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.archive.*;
import org.parabuild.ci.archive.internal.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 * Tests ConsistentFileList
 */
public class SSTestConsistentFileList extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestConsistentFileList.class);

  public static final String TEST_FILE_NAME = "in_dir_file_0.log";
  public static final int TEST_IN_DIR_LOG_COUNT = 10;

  private ConsistentFileList consistentFileList;
  protected ArchiveManager archiveManager;
  protected ConfigurationManager cm;
  protected StepLog stepLog;
  public static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;
  public static final String TEST_PREFIX = "in_dir_file_";


  public SSTestConsistentFileList(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_getFileID() throws Exception {
    final int fileID = consistentFileList.getFileNameID(TEST_FILE_NAME);
    assertTrue(fileID >= 0);
  }


  /**
   *
   */
  public void test_getFileByID() throws Exception {
    assertNull(consistentFileList.getFileNameByID(33333333)); // does not find what should not
    final int fileID = consistentFileList.getFileNameID(TEST_FILE_NAME); // get ID
    final String fileNameByID = consistentFileList.getFileNameByID(fileID);
    assertEquals(TEST_FILE_NAME, fileNameByID); // make sure id is consistent with file
  }


  /**
   *
   */
  public void test_getFiles() throws Exception {
    final String[] fileNames = consistentFileList.getFileNames();
    assertNotNull(fileNames);
    assertTrue(fileNames.length > 1);
    for (int i = 0; i < fileNames.length; i++) {
      final File file = new File(archiveManager.getArchivedLogHome(stepLog), fileNames[i]);
      assertTrue(!file.isDirectory());
      assertTrue(file.isFile());
    }
  }


  /**
   * Tests that file names are unique.
   */
  public void test_bug590() throws Exception {
    // we use set as a duplicates detector. if file names are unique,
    // sizes of the names array and the set should be the same.
    final Set set = new HashSet(11);
    final String[] fileNames = consistentFileList.getFileNames();
    for (int i = 0; i < fileNames.length; i++) {
      set.add(fileNames[i]);
    }
    assertEquals(fileNames.length, set.size());
  }


  public void test_listsPacked() throws IOException {
    final ArchiveCompressor packingHandler = new ArchiveCompressor(TEST_BUILD_ID, archiveManager.getBuildLogDir(), TEST_PREFIX);
    packingHandler.forceCutOffTimeMillis(System.currentTimeMillis());
    packingHandler.compressExpiredArhiveEntities();
    final ConsistentFileList packedArchiveLogList = new ConsistentFileList(archiveManager, stepLog);
    assertEquals(TEST_IN_DIR_LOG_COUNT, packedArchiveLogList.getFileNames().length);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
    archiveManager = ArchiveManagerFactory.getArchiveManager(TEST_BUILD_ID);
    stepLog = cm.getStepLog(7);

    // create files in the dir
    final File archivedLogHome = archiveManager.getArchivedLogHome(stepLog);
    for (int i = 0; i < TEST_IN_DIR_LOG_COUNT; i++) {
      final File testLogFileToCreate = new File(archivedLogHome, TEST_PREFIX + i + ".log");
      testLogFileToCreate.getParentFile().mkdirs();
      testLogFileToCreate.createNewFile();
    }

    consistentFileList = new ConsistentFileList(archiveManager, stepLog);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestConsistentFileList.class);
  }
}
