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
import junit.framework.*;
import org.apache.commons.logging.*;
import org.apache.lucene.document.*;
import org.apache.lucene.queryParser.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.TestIndexRequest;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.search.*;
import org.parabuild.ci.services.*;
import org.parabuild.ci.webui.common.*;

/**
 */
public class SSTestWritingHitsTraverserCallback extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestWritingHitsTraverserCallback.class);
  public static final String TEST_FILE_NAME_IN_ARCHIVE_DIR = "test_log_file.log";

  private SearchHitsTraverser traverser = null;
  private SearchManager searchManager = null;
  private StringWriter callbackOut = null;
  private WritingHitsTraverserCallback callback = null;


  public SSTestWritingHitsTraverserCallback(final String s) {
    super(s);
  }


  public void test_traverse() throws InterruptedException, ParseException, IOException {
    // wait to let index request to get through
    boolean b1 = false;
    boolean b2 = false;
    boolean b3 = false;
    boolean everythingFound = false;
    // timed wait
    final long startedAt = System.currentTimeMillis();
    final long timeOut = startedAt + 10000;
    while (System.currentTimeMillis() < timeOut && !everythingFound) {
      traverser.traverse(searchManager.search(new SearchRequest("BUILD SUCCESSFUL")), callback);
      // check that everything we expected is there - it's just a smoke test
      final String callbackOutput = callbackOut.toString();
//      if (log.isDebugEnabled()) log.debug("callbackOutput: " + callbackOutput);
      b1 = callbackOutput.indexOf(TEST_FILE_NAME_IN_ARCHIVE_DIR) > 0;
      b2 = callbackOutput.indexOf(Pages.PARAM_LOG_ID) > 0;
      b3 = callbackOutput.indexOf(Pages.PARAM_FILE_NAME) > 0;
      everythingFound = b1 && b2 && b3;
      Thread.sleep(200);
    }
    if (log.isDebugEnabled()) log.debug("waited: " + (System.currentTimeMillis() - startedAt));
    assertTrue(b1);
    assertTrue(b2);
    assertTrue(b3);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestWritingHitsTraverserCallback.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    final SearchService searchService = ServiceManager.getInstance().getSearchService();
    searchService.queueIndexRequest(new CallbackTestIndexRequest());
    searchManager = SearchManager.getInstance();
    traverser = new SearchHitsTraverser();
    callbackOut = new StringWriter(50);
    callback = new WritingHitsTraverserCallback(new PrintWriter(callbackOut));
  }


  /**
   * This index request indexes a document that is a file in a
   * dir with text files.
   */
  private static final class CallbackTestIndexRequest implements SearchService.IndexRequest {

    private final Log log = LogFactory.getLog(TestIndexRequest.class);
    private final ConfigurationManager cm = ConfigurationManager.getInstance();


    public Document getDocumentToIndex() {
      Document document = null;
      try {
        if (log.isDebugEnabled()) log.debug("Prepare test document");

        // get params
        final BuildRun buildRun = cm.getBuildRun(1);
        final StepLog stepLog = cm.getStepLog(1);
        stepLog.setPathType(StepLog.PATH_TYPE_TEXT_DIR);
        final StepRun stepRun = cm.getStepRun(1);

        // request doc from factory
        document = LuceneDocumentFactory.makeDocument(buildRun, stepRun,
          stepLog, TestHelper.getTestFile("test_ant_successful_build.log"),
          TEST_FILE_NAME_IN_ARCHIVE_DIR);
      } catch (FileNotFoundException e) {
        throw new IllegalStateException(e.toString());
      }
      if (log.isDebugEnabled()) log.debug("document = " + document.toString());
      return document;
    }
  }
}
