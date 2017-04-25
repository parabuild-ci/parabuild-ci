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
import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.archive.internal.*;
import org.parabuild.ci.common.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.configuration.*;

/**
 * Tests LogRetentionHandler
 */
public class SSTestLogRetentionHandler extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestLogRetentionHandler.class);
  private static final int TEST_BUILD_ID = TestHelper.TEST_P4_VALID_BUILD_ID;

  private LogRetentionHandler retentionHandler = null;
  private ArchiveManager archiveManager = null;


  public SSTestLogRetentionHandler(final String s) {
    super(s);
  }


  /**
   * Dataset for this build id is configured to keep logs for 2
   * day, so after call to retentionHandler.deleteExpiredBuildLogs()
   * there should be only one file left
   *
   * @throws IOException
   */
  public void test_handle() throws IOException {

    // create a file 3 days ago
    final StepLog stepLog2 = ConfigurationManager.getInstance().getStepLog(8);
    final File file2 = archiveManager.fileNameToLogPath(stepLog2.getArchiveFileName());
    file2.getParentFile().mkdirs();
    file2.createNewFile();
    assertTrue(file2.exists());

    // create a file 1 days ago
    final StepLog stepLog1 = ConfigurationManager.getInstance().getStepLog(9);
    final File file1 = archiveManager.fileNameToLogPath(stepLog1.getArchiveFileName());
    file1.getParentFile().mkdirs();
    file1.createNewFile();
    assertTrue(file1.exists());

    // set dates

    final long time2 = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000);
    final long time1 = System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000);
    file2.setLastModified(time2);
    file1.setLastModified(time1);
    assertEquals(2, archiveManager.getBuildLogDir().listFiles().length);

    // call method
    // log.debug("time1 = " + time1);
    // log.debug("time2 = " + time2);
    retentionHandler.deleteExpired();

    // assert
    assertEquals(1, archiveManager.getBuildLogDir().listFiles().length);
    assertTrue(archiveManager.getBuildLogDir().listFiles()[0].lastModified() >= retentionHandler.getCutOffTimeMillis().longValue());
  }


  public void test_recalculateCurrentCutOffTime() throws Exception {
    // test - does not fail if no setting.
    final ArchiveManager noSettingAM = ArchiveManagerFactory.getArchiveManager(3); // no retention setting
    final LogRetentionHandler noSettingLRH = new LogRetentionHandler(3, noSettingAM.getBuildLogDir(), noSettingAM.getBuildLogPrefix());
    noSettingLRH.setCurrentCutOffDaysFromConfiguration();
  }


  public void test_makeRecordedFileName() {
    // simple case
    final String testName1 = "test.log";
    assertEquals(testName1, LogRetentionHandler.makeRecordedFileName(testName1));

    // same file name but ending with ".zip"
    final String testName2 = testName1 + ".zip";
    assertEquals(testName1, LogRetentionHandler.makeRecordedFileName(testName2));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestLogRetentionHandler.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    archiveManager = ArchiveManagerFactory.getArchiveManager(TEST_BUILD_ID);
    retentionHandler = new LogRetentionHandler(TEST_BUILD_ID, archiveManager.getBuildLogDir(), archiveManager.getBuildLogPrefix());
    retentionHandler.setCurrentCutOffDaysFromConfiguration();
    IoUtils.emptyDir(archiveManager.getBuildLogDir());
    assertEquals(0, archiveManager.getBuildLogDir().listFiles().length);
  }
}
