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

import java.io.*;
import junit.framework.*;
import org.apache.commons.logging.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.archive.internal.*;
import org.parabuild.ci.util.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 * Tests directory helper
 */
public class SSTestArchiveCompressor extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestArchiveCompressor.class);
  private static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;

  private ArchiveCompressor packingHandler = null;
  private ArchiveManager archiveManager = null;


  public SSTestArchiveCompressor(final String s) {
    super(s);
  }


  /**
   * Dataset for this build id is configured to keep logs for 2
   * day, so after call to packingHandler.packExpiredBuildLogs()
   * there should be only one file left
   *
   * @throws java.io.IOException
   */
  public void test_compressExpiredArhiveEntities() throws IOException {

    // create a file 3 days ago
    final BuildSequence sequence2 = new BuildSequence();
    sequence2.setBuildID(TEST_BUILD_ID);
    sequence2.setSequenceID(20000);
    final String name2 = archiveManager.makeNewStepLogFileName(sequence2);
    final File file2 = archiveManager.fileNameToLogPath(name2);
    file2.createNewFile();
    final long time2 = System.currentTimeMillis() - (3L * 24L * 60L * 60L * 1000L);
    file2.setLastModified(time2);

    // create a file 1 days ago
    final BuildSequence sequence1 = new BuildSequence();
    sequence1.setBuildID(TEST_BUILD_ID);
    sequence1.setSequenceID(10000);
    final String name1 = archiveManager.makeNewStepLogFileName(sequence1);
    final File file1 = archiveManager.fileNameToLogPath(name1);
    file1.createNewFile();
    final long time1 = System.currentTimeMillis() - (1L * 24L * 60L * 60L * 1000L);
    file1.setLastModified(time1);
    assertEquals(2, archiveManager.getBuildLogDir().listFiles().length);

    // call method
    // log.debug("time1 = " + time1);
    // log.debug("time2 = " + time2);
    packingHandler.compressExpiredArchiveEntities();

    // assert
    boolean foundCompressed = false;
    final File[] files = archiveManager.getBuildLogDir().listFiles();
    assertEquals(2, files.length);
    for (int i = 0; i < files.length; i++) {
      final File file = files[i];
      // don't check compressed
      if (file.getPath().endsWith(".zip")) {
        foundCompressed = true;
        continue;
      }
      // none of the files has mod date
      assertTrue(file.lastModified() > packingHandler.getCutOffTimeMillis());
    }

    // compressed appeared
    assertTrue(foundCompressed);
  }


  /**
   *
   */
  public void test_compressDoesNotLeaveNonpackedDirs() throws IOException {
    // create files in the dir
    final long lastModifiedTime = System.currentTimeMillis() - 10000L;
    final StepLog stepLog = ConfigurationManager.getInstance().getStepLog(7);
    final File archivedLogHome = archiveManager.getArchivedLogHome(stepLog);
    archivedLogHome.mkdirs();
    final int TEST_IN_DIR_LOG_COUNT = 10;
    for (int i = 0; i < TEST_IN_DIR_LOG_COUNT; i++) {
      final File archivedFileToCreate = new File(archivedLogHome, "in_dir_file_" + i + ".log");
      archivedFileToCreate.createNewFile();
      archivedFileToCreate.setLastModified(lastModifiedTime);
    }
    archivedLogHome.setLastModified(lastModifiedTime);

    // pack
    packingHandler.forceCutOffTimeMillis(System.currentTimeMillis());
    packingHandler.compressExpiredArchiveEntities();

    // validate no files other then archives left
    final File[] files = archiveManager.getBuildLogDir().listFiles();
    for (int i = 0; i < files.length; i++) {
      final File file = files[i];
      final String path = file.getPath();
      if (log.isDebugEnabled()) log.debug("file = " + file);
      assertEquals("Is a file: " + path, true, file.isFile());
      assertEquals("Ends with .zip: " + path, true, path.endsWith(".zip"));
    }
  }


  public void test_recalculateCurrentCutOffTime() throws Exception {
    // test - does not fail if no setting.
    final ArchiveManager noSettingAM = ArchiveManagerFactory.getArchiveManager(3); // no retention setting
    final ArchiveCompressor noSettingLRH = new ArchiveCompressor(3, noSettingAM.getBuildLogDir(), noSettingAM.getBuildLogPrefix());
    noSettingLRH.recalculateCurrentCutOffTime();
  }


  public void test_calculateCutOffTime() {
    //
    // |_________________|_______________|
    //               cutOffTime       currentTime
    //
    final long currentTime = System.currentTimeMillis();
    final long cutOffTime = ArchiveCompressor.calculateCutOffTime(10);
    final long minimalDiff = 10L * 24L * 60L * 60L * 1000L;
    if (log.isDebugEnabled()) log.debug("cutOffTime = " + cutOffTime);
    if (log.isDebugEnabled()) log.debug("currentTime = " + currentTime);
    if (log.isDebugEnabled()) log.debug("currentTime - cutOffTime = " + (currentTime - cutOffTime));
    if (log.isDebugEnabled()) log.debug("minimalDiff = " + minimalDiff);
    assertTrue(cutOffTime < currentTime);
    assertTrue((currentTime - cutOffTime) + 1 >= minimalDiff);
  }


  /**
   *
   */
  public void test_compressDoesNotPackUnexpiredDirs() throws IOException {

    // get step log
    final StepLog stepLog = ConfigurationManager.getInstance().getStepLog(7);
    assertEquals(StepLog.PATH_TYPE_TEXT_DIR, stepLog.getPathType()); // ensure is a text dir log.

    // create files in the dir
    final File archivedLogHome = archiveManager.getArchivedLogHome(stepLog);
    final int TEST_IN_DIR_LOG_COUNT = 10;
    for (int i = 0; i < TEST_IN_DIR_LOG_COUNT; i++) {
      final File archivedFileToCreate = new File(archivedLogHome, "in_dir_file_" + i + ".log");
      archivedFileToCreate.getParentFile().mkdirs();
      archivedFileToCreate.createNewFile();
    }

    // pack
    packingHandler.compressExpiredArchiveEntities();

    // validate no files other then archives left
    final File[] files = archiveManager.getBuildLogDir().listFiles();
    for (int i = 0; i < files.length; i++) {
      final File file = files[i];
      final String path = file.getPath();
      assertEquals("Is a dir: " + path, true, file.isDirectory());
      assertEquals("Does not ends with .zip: " + path, true, !path.endsWith(".zip"));
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestArchiveCompressor.class, new String[]{
      "test_compressDoesNotLeaveNonpackedDirs"
    });
  }


  protected void setUp() throws Exception {
    super.setUp();
    archiveManager = ArchiveManagerFactory.getArchiveManager(TEST_BUILD_ID);
    packingHandler = new ArchiveCompressor(TEST_BUILD_ID, archiveManager.getBuildLogDir(), archiveManager.getBuildLogPrefix());
    IoUtils.emptyDir(archiveManager.getBuildLogDir());
    TestHelper.assertDirIsEmpty(archiveManager.getBuildLogDir());
  }
}
