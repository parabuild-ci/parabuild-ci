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

import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.archive.internal.ArchiveManagerImpl;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Tests ArchiveManagerImpl
 */
public class SSTestArchiveManagerImpl extends ServersideTestCase {

  private static final Log LOG = LogFactory.getLog(SSTestArchiveManagerImpl.class);
  private static final int TEST_BUILD_ID = 1;
  private static final int TEST_STEP_RESULT_ID = 1;

  private BuildSequence sequence = null;
  private ArchiveManagerImpl archiveManager = null;
  private ConfigurationManager cm;


  public SSTestArchiveManagerImpl(final String s) {
    super(s);
  }


  public void test_create() throws IOException {
    final File buildLogDir = archiveManager.getBuildLogDir();
    TestHelper.assertDirectoryExists(buildLogDir);
  }


  public void test_makeNewStepLogFile() throws Exception {
    final String logName1 = archiveManager.makeNewStepLogFileName(sequence);
    final String logName2 = archiveManager.makeNewStepLogFileName(sequence);
    final File log1 = archiveManager.fileNameToLogPath(logName1);
    final File log2 = archiveManager.fileNameToLogPath(logName2);

    assertNotNull(log1);
    assertNotNull(log2);
    assertTrue(!log1.equals(log2));
  }


  public void test_deleteLog() throws IOException {

    // create test logs
    final List runLogList = cm.getAllStepLogs(cm.getStepRun(TEST_STEP_RESULT_ID).getID());
    final int logCount = runLogList.size();
    assertTrue(logCount > 0);
    for (int i = 0; i < logCount; i++) {
      final StepLog stepLog = (StepLog) runLogList.get(i);
      final File logFile = archiveManager.fileNameToLogPath(stepLog.getArchiveFileName());
      logFile.getParentFile().mkdirs();
      logFile.createNewFile();
    }

    // delete
    for (int i = 0; i < logCount; i++) {
      final StepLog stepLog = (StepLog) runLogList.get(i);
      archiveManager.deleteLog(stepLog);
    }

    // fail if anything left
    for (int i = 0; i < logCount; i++) {
      final StepLog stepLog = (StepLog) runLogList.get(i);
      final File logFile = archiveManager.fileNameToLogPath(stepLog.getArchiveFileName());
      assertTrue("Log file " + logFile + " for step log " + stepLog + " exists though it should not", !logFile.exists());
    }
  }


  public void test_deleteResult() throws IOException {

    // create test logs
    final List runResultList = cm.getAllStepResults(cm.getStepRun(TEST_STEP_RESULT_ID));
    final int resultCount = runResultList.size();
    assertTrue(resultCount > 0);
    for (int i = 0; i < resultCount; i++) {
      final StepResult stepResult = (StepResult) runResultList.get(i);
      final File resultFile = archiveManager.fileNameToResultPath(stepResult.getArchiveFileName());
      resultFile.getParentFile().mkdirs();
      resultFile.createNewFile();
    }

    // delete
    final List deletedResults = new ArrayList(runResultList.size());
    for (int i = 0; i < resultCount; i++) {
      final StepResult stepResult = (StepResult) runResultList.get(i);
      deletedResults.add(new Object[]{Integer.valueOf(stepResult.getID()), stepResult.getArchiveFileName()});
      archiveManager.deleteResult(stepResult);
    }

    // fail if anything left
    for (int i = 0; i < resultCount; i++) {
      final Object[] deleted = (Object[]) deletedResults.get(i);
      final File resultFile = archiveManager.fileNameToResultPath((String) deleted[1]);
      assertTrue("Result file " + resultFile + " for step result " + deleted[0] + " exists though it should not", !resultFile.exists());
      // assert not step result exists
      assertNull("Step result should not exist", cm.getStepResult(((Number) deleted[0]).intValue()));
    }
  }


  public void test_deleteExpiredBuildResultsDeletes() throws IOException {

    // alter build configuration
    //
    // enabled
    cm.saveObject(new BuildConfigAttribute(TEST_BUILD_ID, BuildConfigAttribute.ENABLE_AUTOMATIC_DELETING_OLD_BUILD_RESULTS, BuildConfigAttribute.OPTION_CHECKED));

    // 9 days
    final BuildConfigAttribute buildAttribute = cm.getBuildAttribute(TEST_BUILD_ID, BuildConfigAttribute.RESULT_RETENTION_DAYS);
    buildAttribute.setPropertyValue(9);
    cm.saveObject(buildAttribute);

    // 11 days in system minimum
    SystemConfigurationManagerFactory.getManager().saveSystemProperty(new SystemProperty(SystemProperty.MINIMUM_RESULTS_RETENTION, 11));

    // create test result 10 days old
    final long createdTimeStamp = System.currentTimeMillis() - StringUtils.daysToMillis(12);
    final List runResultList = createTestResultFiles(createdTimeStamp);

    // Overwrite finishedAt timestamp
    final BuildRun buildRun = cm.getBuildRun(TEST_BUILD_ID);
    assert buildRun != null;
    buildRun.setFinishedAt(new Date(createdTimeStamp));
    cm.saveObject(buildRun);

    // delete
    archiveManager.deleteExpiredBuildResults();

    // fail if anything left
    for (int i = 0, n = runResultList.size(); i < n; i++) {
      final StepResult stepResult = (StepResult) runResultList.get(i);
      final File resltFile = archiveManager.fileNameToResultPath(stepResult.getArchiveFileName());
      assertTrue("Result file " + resltFile + " for step result " + stepResult + " exists though it should not", !resltFile.exists());
    }
  }


  public void test_deleteExpiredBuildResultsDoesntDeleteWhatShouldnt() throws IOException {

    // alter build configuration
    //
    // enabled
    cm.saveObject(new BuildConfigAttribute(TEST_BUILD_ID, BuildConfigAttribute.ENABLE_AUTOMATIC_DELETING_OLD_BUILD_RESULTS, BuildConfigAttribute.OPTION_CHECKED));

    // 9 days
    final BuildConfigAttribute buildAttribute = cm.getBuildAttribute(TEST_BUILD_ID, BuildConfigAttribute.RESULT_RETENTION_DAYS);
    buildAttribute.setPropertyValue(9);
    cm.saveObject(buildAttribute);

    // 11 days in system minimum
    SystemConfigurationManagerFactory.getManager().saveSystemProperty(new SystemProperty(SystemProperty.MINIMUM_RESULTS_RETENTION, 11));

    // create test result 10 days old
    final long createdTimeStamp = System.currentTimeMillis() - StringUtils.daysToMillis(10);

    // Overwrite finishedAt timestamp
    final BuildRun buildRun = cm.getBuildRun(TEST_BUILD_ID);
    assert buildRun != null;
    buildRun.setFinishedAt(new Date(createdTimeStamp));
    cm.saveObject(buildRun);


    //noinspection ControlFlowStatementWithoutBraces
    if (LOG.isDebugEnabled()) LOG.debug("createdTimeStamp: " + createdTimeStamp); // NOPMD
    final List runResultList = createTestResultFiles(createdTimeStamp);

    // delete
    archiveManager.deleteExpiredBuildResults();

    // fail if anything left
    for (int i = 0, n = runResultList.size(); i < n; i++) {

      final StepResult stepResult = (StepResult) runResultList.get(i);
      final File resultFile = archiveManager.fileNameToResultPath(stepResult.getArchiveFileName());
      assertTrue("Result file " + resultFile + " for step result " + stepResult + " should exist but it does not", resultFile.exists());
    }
  }


  /**
   * Helper method to create test files.
   * @param createdTimeStamp to timestamp files for to a realistic updated date.
   */
  private List createTestResultFiles(final long createdTimeStamp) throws IOException {

    final List runResultList = cm.getAllStepResults(cm.getStepRun(TEST_STEP_RESULT_ID));
    final int resultCount = runResultList.size();
    assertTrue(resultCount > 0);
    for (int i = 0; i < resultCount; i++) {
      final StepResult stepResult = (StepResult) runResultList.get(i);
      final File resultFile = archiveManager.fileNameToResultPath(stepResult.getArchiveFileName());
      resultFile.getParentFile().mkdirs();
      resultFile.createNewFile();
      resultFile.setLastModified(createdTimeStamp);
    }
    return runResultList;
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestArchiveManagerImpl.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
    final BuildConfig buildConfiguration = cm.getBuildConfiguration(TEST_BUILD_ID);
    archiveManager = new ArchiveManagerImpl(TEST_BUILD_ID);
    final List sequences = cm.getAllBuildSequences(buildConfiguration.getBuildID(), BuildStepType.BUILD);
    assertTrue(!sequences.isEmpty());
    sequence = (BuildSequence) sequences.get(0);
  }
}
