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
import java.util.*;

import junit.framework.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.archive.internal.*;
import org.parabuild.ci.common.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.configuration.*;

/**
 * Tests ResultRetentionHandler
 */
public class SSTestResultRetentionHandler extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestResultRetentionHandler.class);
  private static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;

  private ResultRetentionHandler retentionHandler = null;
  private ArchiveManager archiveManager = null;

  /** @noinspection FieldCanBeLocal*/
  private File TEST_FILE_2 = null;
  /** @noinspection FieldCanBeLocal*/
  private File TEST_FILE_1 = null;


  public SSTestResultRetentionHandler(final String s) {
    super(s);
  }


  /**
   * Dataset for this build id is configured to keep results for 2
   * day, so after call to retentionHandler.deleteExpired()
   * there should be only one file left
   *
   * @throws java.io.IOException
   */
  public void test_handle() throws IOException {

    // call method
    retentionHandler.deleteExpired();

    // assert
    assertEquals(1, archiveManager.getResultDir().listFiles().length);
    assertTrue(archiveManager.getResultDir().listFiles()[0].lastModified() >= retentionHandler.getCutOffTimeMillis().longValue());
  }


  public void test_handleDoesNotDeletePinned() throws IOException {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    for (Iterator i = cm.getBuildRunResults(1).iterator(); i.hasNext();) {
      final StepResult stepResult = (StepResult)i.next();
      stepResult.setPinned(true);
      cm.saveObject(stepResult);
    }

    // call method
    retentionHandler.deleteExpired();

    // assert
    assertEquals(2, archiveManager.getResultDir().listFiles().length);
  }


  public void test_recalculateCurrentCutOffTime() throws Exception {
    // test - does not fail if no setting.
    final ArchiveManager noSettingAM = ArchiveManagerFactory.getArchiveManager(3); // no retention setting
    final ResultRetentionHandler noSettingLRH = new ResultRetentionHandler(3, noSettingAM.getResultDir(), noSettingAM.getBuildResultPrefix());
    noSettingLRH.setCurrentCutOffDaysFromConfiguration();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestResultRetentionHandler.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    archiveManager = ArchiveManagerFactory.getArchiveManager(TEST_BUILD_ID);
    retentionHandler = new ResultRetentionHandler(TEST_BUILD_ID, archiveManager.getResultDir(), archiveManager.getBuildResultPrefix());
    retentionHandler.setCurrentCutOffDaysFromConfiguration();
    IoUtils.emptyDir(archiveManager.getResultDir());
    assertEquals(0, archiveManager.getResultDir().listFiles().length);

    // create a file 3 days ago
    final StepResult stepResult2 = ConfigurationManager.getInstance().getStepResult(0);
    TEST_FILE_2 = archiveManager.fileNameToResultPath(stepResult2.getArchiveFileName());
    TEST_FILE_2.getParentFile().mkdirs();
    TEST_FILE_2.createNewFile();
    assertTrue(TEST_FILE_2.exists());

    // create a file 1 days ago
    final StepResult stepResult1 = ConfigurationManager.getInstance().getStepResult(1);
    TEST_FILE_1 = archiveManager.fileNameToResultPath(stepResult1.getArchiveFileName());
    TEST_FILE_1.mkdirs(); // directory type
    assertTrue(TEST_FILE_1.exists());

    // set dates

    final long time2 = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000);
    final long time1 = System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000);
    TEST_FILE_2.setLastModified(time2);
    TEST_FILE_1.setLastModified(time1);
    assertEquals(2, archiveManager.getResultDir().listFiles().length);

  }
}
