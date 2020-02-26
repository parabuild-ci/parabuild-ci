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
package org.parabuild.ci.build;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.search.LuceneDocumentFactory;
import org.parabuild.ci.search.SearchManager;
import org.parabuild.ci.search.SearchRequest;
import org.parabuild.ci.versioncontrol.SourceControl;
import org.parabuild.ci.versioncontrol.VersionControlFactory;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Implements Strategy GoF pattern - test cases wanted to test
 * concrete log implementations should extend this abstract
 * class.
 */
public abstract class AbstractCustomLogTest extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(AbstractCustomLogTest.class);

  protected static final int TEST_STEP_RUN_ID = 1;
  protected static final int TEST_BUILD_RUN_CONFIG_ID = 9; // CVS build run

  protected ArchiveManager archiveManager = null;
  protected BuildRunConfig buildRunConfig;
  protected Agent agent = null;
  protected ConfigurationManager cm = null;
  protected ErrorManager errorManager = null;
  protected LogConfig logConfig = null;
  protected SearchManager searchManager;
  protected SourceControl scm = null;
  protected String relativeBuildDir;
  protected String remoteBuildDirName;
  protected String remoteCheckoutDir;


  /**
   * Call log handler to process logs
   */
  protected abstract void processLogs();


  /**
   * Should return ID of log config to be used to configure log
   * handler.
   */
  protected abstract int logConfigID();


  /**
   * @return log type handler being tested
   */
  protected abstract byte logTypeBeingTested();


  /**
   * Return a string to be found in search after calling
   * processLogs.
   *
   * @return
   * @see org.parabuild.ci.build.AbstractCustomLogTest - parent class that
   *      will call this method after calling processLogs().
   * @see #processLogs
   */
  protected abstract String stringToBeFoundBySearch();


  /**
   * Test
   */
  public final void test_process() throws Exception {
    // init current number of log files
    final StepRun stepRun = cm.getStepRun(stepResultID());
    final List logsBefore = cm.getAllStepLogs(stepRun.getID());
    final int logCountBefore = logsBefore.size();
    final int logCountExistsBefore = getExistsCount(logsBefore);

    // call implementor's process logs method
    if (log.isDebugEnabled()) log.debug("first run");
    processLogs();

    // get counters after processing
    final List logsAfter = cm.getAllStepLogs(stepRun.getID());
    final int logCountAfter = logsAfter.size();
    final int logCountExistsAfter = getExistsCount(logsAfter);

    // check if logs in the db
    if (log.isDebugEnabled()) log.debug("logCountBefore = " + logCountBefore);
    if (log.isDebugEnabled()) log.debug("logCountAfter = " + logCountAfter);
    assertTrue("Number of logs after should be bigger then before", logCountAfter > logCountBefore);
    assertEquals(1, logCountAfter - logCountBefore);

    // check if logs are accesible from archive
    assertEquals(logCountExistsAfter - logCountExistsBefore, logCountAfter - logCountBefore);

    // run second time to make sure same results are not picked twice.
    if (log.isDebugEnabled()) log.debug("second run");
    processLogs();
    assertEquals("Number of logs should not change after second run", logCountAfter, cm.getAllStepLogs(stepRun.getID()).size());
    assertEquals("Number of archived logs should not change after second run", logCountExistsAfter, getExistsCount(cm.getAllStepLogs(stepRun.getID())));

    // check if logs got indexed/searchable where applicable
    if (!StringUtils.isBlank(stringToBeFoundBySearch())) {
      if (log.isDebugEnabled()) log.debug("Check if can find in log: " + stringToBeFoundBySearch());
      Hits results = searchManager.search(new SearchRequest(stringToBeFoundBySearch()));
      if (results.length() == 0) {
        // let indexer queue to process logs
        Thread.sleep(500);
        // retry search
        results = searchManager.search(new SearchRequest(stringToBeFoundBySearch()));
      }
      assertTrue(results.length() > 0);
      // check if required fields are there
      for (int i = 0; i < results.length(); i++) {
        final Document result = results.doc(i);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_BUILD_ID);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_BUILD_NAME);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_BUILD_RUN_NUMBER);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_BUILD_RUN_ID);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_BUILD_STARTED);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_DOCUMENT_TYPE);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_SEQUENCE_LOG_ID);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_SEQUENCE_LOG_DESCR);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_SEQUENCE_LOG_CONFIG_PATH);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_SEQUENCE_LOG_PATH_TYPE);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_SEQUENCE_LOG_TYPE);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_SEQUENCE_NAME);
      }
    }
  }


  protected final int stepResultID() {
    return TEST_STEP_RUN_ID;
  }


  protected void assertFieldPresent(final Document result, final String fieldName) {
    assertTrue(!StringUtils.isBlank(result.get(fieldName)));
  }


  protected void setUp() throws Exception {
    super.setUp();

    // show reported errors stack traces.
    System.setProperty("parabuild.print.stacktrace", "true");

    // init obejcts
    this.cm = ConfigurationManager.getInstance();
    this.buildRunConfig = cm.getBuildRunConfig(TEST_BUILD_RUN_CONFIG_ID);
    this.scm = VersionControlFactory.makeVersionControl(buildRunConfig);
    this.agent = AgentManager.getInstance().getNextLiveAgent(buildRunConfig.getActiveBuildID());
    this.scm.setAgentHost(agent.getHost());
    this.archiveManager = ArchiveManagerFactory.getArchiveManager(buildRunConfig.getActiveBuildID());
    this.errorManager = ErrorManagerFactory.getErrorManager();
    this.errorManager.clearAllActiveErrors();
    this.searchManager = SearchManager.getInstance();
    this.logConfig = cm.getLogConfig(logConfigID());

    // delete everything
    this.agent.emptyCheckoutDir();

    // validate set up
    assertNotNull("Log config for ID " + logConfigID() + " not found", logConfig);
    if (this.logConfig.getType() != logTypeBeingTested()) {
      throw new IllegalArgumentException("Wrong type: " + logConfig.getType());
    }

    // checkout - to have CVS system files in place. otherwise it will
    // fool other thests thinking that there is a checked out dir
    // while it's not (no CVS files).
    this.scm.checkoutLatest();


    // create log file to simulate that file is there
    this.remoteCheckoutDir = agent.getCheckoutDirName();
    this.relativeBuildDir = scm.getRelativeBuildDir();
    this.remoteBuildDirName = remoteCheckoutDir + agent.getSystemProperty("file.separator") + relativeBuildDir;
  }


  private int getExistsCount(final List logs) throws IOException {
    int result = 0;
    for (Iterator i = logs.iterator(); i.hasNext();) {
      final StepLog stepLog = (StepLog) i.next();
      final File logFile = archiveManager.fileNameToLogPath(stepLog.getArchiveFileName());
      if (logFile.exists()) result++;
    }
    return result;
  }


  public AbstractCustomLogTest(final String s) {
    super(s);
  }
}
