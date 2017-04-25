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
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.StepResult;
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
 * concrete result handler implementations should extend this
 * abstract class.
 *
 * @noinspection ProtectedField,JUnitAbstractTestClassNamingConvention,InstanceVariableNamingConvention
 */
public abstract class AbstractResultHandlerTest extends ServersideTestCase {

  private static final Log LOG = LogFactory.getLog(AbstractResultHandlerTest.class); // NOPMD

  protected static final int TEST_STEP_RESULT_ID = 1;
  protected static final int TEST_BUILD_RUN_CONFIG_ID = 9;

  protected ArchiveManager archiveManager = null;
  protected BuildRunConfig buildRunConfig;
  protected Agent agent = null;
  protected ConfigurationManager cm = null;
  protected ErrorManager errorManager = null;
  protected ResultConfig resultConfig = null;
  protected SearchManager searchManager;
  protected SourceControl scm = null;
  protected String relativeBuildDir;
  protected String remoteBuildDirName;
  protected String remoteCheckoutDir;


  protected final int stepRunID() {
    return TEST_STEP_RESULT_ID;
  }


  /**
   * Call result handler to process results
   */
  protected abstract void processResults() throws IOException;


  /**
   * Should return ID of result config to be used to configure
   * result handler.
   */
  protected abstract int resultConfigID();


  /**
   * @return result type handler being tested
   */
  protected abstract byte resultTypeBeingTested();


  /**
   * Return a string to be found in search after calling
   * processResults.
   *
   * @return
   * @see AbstractResultHandlerTest - parent class that will call
   *      this method after calling processResults().
   * @see #processResults
   */
  protected abstract String stringToBeFoundBySearch() throws IOException, AgentFailureException;


  /**
   * Test
   */
  public final void testProcess() throws Exception {
    // init current number of result files
    final StepRun stepRun = cm.getStepRun(stepRunID());
    final List resultsBefore = cm.getAllStepResults(stepRun);
    final int resultCountBefore = resultsBefore.size();
    final int resultCountExistsBefore = getExistsCount(resultsBefore);

    // call implementor's process results method
    if (LOG.isDebugEnabled()) {
      LOG.debug("first run");
    }
    processResults();

    // get counters after processing
    final List resultsAfter = cm.getAllStepResults(stepRun);
    final int resultCountAfter = resultsAfter.size();
    final int resultCountExistsAfter = getExistsCount(resultsAfter);

    // check if results in the db
    if (LOG.isDebugEnabled()) {
      LOG.debug("resultCountBefore = " + resultCountBefore);
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("resultCountAfter = " + resultCountAfter);
    }

    assertTrue("Number of results after should be bigger then before", resultCountAfter > resultCountBefore);
    assertTrue("Number of results should increase", resultCountAfter - resultCountBefore >= 1);
    assertEquals("Number of results accessible from archive should be the same as in the database", resultCountExistsAfter - resultCountExistsBefore, resultCountAfter - resultCountBefore);

    // run second time to make sure same results are not picked twice.
    if (LOG.isDebugEnabled()) {
      LOG.debug("second run");
    }
    processResults();
    assertEquals("Number of results should not change after second run", resultCountAfter, cm.getAllStepResults(stepRun).size());
    assertEquals("Number of archived results should not change after second run", resultCountExistsAfter, getExistsCount(resultsAfter));

    // check if results got indexed/searchable where applicable
    if (!StringUtils.isBlank(stringToBeFoundBySearch())) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Check if can find in result: " + stringToBeFoundBySearch());
      }
      Hits results = searchManager.search(new SearchRequest(stringToBeFoundBySearch()));
      if (results.length() == 0) {
        // let indexer queue to process results
        Thread.sleep(500);
        // retry search
        results = searchManager.search(new SearchRequest(stringToBeFoundBySearch()));
      }
      assertTrue(results.length() > 0);
      // check if required fields are there
      for (int i = 0; i < results.length(); i++) {
        final Document result = results.doc(i);
        // header
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_BUILD_ID);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_BUILD_NAME);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_BUILD_RUN_NUMBER);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_BUILD_RUN_ID);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_BUILD_STARTED);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_DOCUMENT_TYPE);
        // result specific
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_RESULT_FILE_NAME);
        assertFieldPresent(result, LuceneDocumentFactory.FIELD_RESULT_STEP_RESULT_ID);
      }
    }
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
    this.archiveManager = ArchiveManagerFactory.getArchiveManager(buildRunConfig.getActiveBuildID());
    this.errorManager = ErrorManagerFactory.getErrorManager();
    this.errorManager.clearAllActiveErrors();
    this.searchManager = SearchManager.getInstance();
    this.resultConfig = (ResultConfig) cm.getObject(ResultConfig.class, resultConfigID());

    // empty directory
    this.agent.emptyCheckoutDir();
    this.scm.setAgentHost(agent.getHost());

    // validate set up
    assertNotNull("Result config for ID " + resultConfigID() + " not found", resultConfig);
    if (this.resultConfig.getType() != resultTypeBeingTested()) {
      throw new IllegalArgumentException("Wrong type: " + resultConfig.getType());
    }

    // checkout - to have CVS system files in place. otherwise it will
    // fool other thests thinking that there is a checked out dir
    // while it's not (no CVS files).
    this.scm.checkoutLatest();

    // create result file to simulate that file is there
    this.remoteCheckoutDir = agent.getCheckoutDirName();
    this.relativeBuildDir = scm.getRelativeBuildDir();
    this.remoteBuildDirName = remoteCheckoutDir + agent.getSystemProperty("file.separator") + relativeBuildDir;
  }


  private int getExistsCount(final List results) throws IOException {
    int result = 0;
    for (Iterator i = results.iterator(); i.hasNext();) {
      final StepResult stepResult = (StepResult) i.next();
      final File resultFile = archiveManager.fileNameToResultPath(stepResult.getArchiveFileName());
      if (resultFile.exists()) {
        result++;
      }
    }
    return result;
  }


  protected AbstractResultHandlerTest(final String s) {
    super(s);
  }


  public String toString() {
    return "AbstractResultHandlerTest{" +
            "agent=" + agent +
            ", archiveManager=" + archiveManager +
            ", buildRunConfig=" + buildRunConfig +
            ", cm=" + cm +
            ", errorManager=" + errorManager +
            ", relativeBuildDir='" + relativeBuildDir + '\'' +
            ", remoteBuildDirName='" + remoteBuildDirName + '\'' +
            ", remoteCheckoutDir='" + remoteCheckoutDir + '\'' +
            ", resultConfig=" + resultConfig +
            ", scm=" + scm +
            ", searchManager=" + searchManager +
            "} " + super.toString();
  }
}
