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

import java.io.*;
import junit.framework.*;
import org.apache.commons.logging.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.search.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.TestIndexRequest;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.search.*;
import org.parabuild.ci.services.*;

/**
 */
public class SSTestSearchService extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestSearchService.class);

  private SearchService searchService = null;
  private SearchManager searchManager = null;
  private ConfigurationManager configManager;


  public SSTestSearchService(final String s) {
    super(s);
  }


  public void test_queueIndexRequest() throws InterruptedException {
    searchService.queueIndexRequest(new TestIndexRequest());
    Thread.sleep(1000); // wait for index request to get through the indexer queue.
  }


  public void test_search() throws IOException, ParseException {
    // test search service
    Hits hits = searchService.search("BUILD SUCCESSFUL", LuceneDocumentFactory.FIELD_CONTENT);
    assertNotNull(hits);
    assertTrue(hits.length() > 0);

    // now test search manager
    final SearchRequest searchRequest = new SearchRequest("BUILD SUCCESSFUL");
    hits = searchManager.search(searchRequest);
    assertTrue(hits.length() > 0);

    // any build
    searchRequest.addParameter(SearchRequestParameter.BUILD_ID, "-1");
    hits = searchManager.search(searchRequest);
    assertTrue(hits.length() > 0);

    // build in the index
    searchRequest.addParameter(SearchRequestParameter.BUILD_ID, "1");
    hits = searchManager.search(searchRequest);
    assertTrue(hits.length() > 0);

    // build not in the index
    searchRequest.addParameter(SearchRequestParameter.BUILD_ID, "999999999");
    hits = searchManager.search(searchRequest);
    assertTrue(hits.length() == 0);
  }


  public void test_index() {
    // doesn't blow up if there is no directory there.
    searchManager.index(configManager.getStepResult(3));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestSearchService.class);

  }


  protected void setUp() throws Exception {
    super.setUp();

    final ServiceManager serviceManager = ServiceManager.getInstance();
    assertNotNull(serviceManager);

    searchService = serviceManager.getSearchService();
    assertNotNull(searchService);
    assertEquals(Service.SERVICE_STATUS_STARTED, searchService.getServiceStatus());

    // check if dir not empty
    TestHelper.assertDirectoryExists(ConfigurationConstants.INDEX_HOME);
    TestHelper.assertDirIsNotEmpty(ConfigurationConstants.INDEX_HOME);

    searchManager = SearchManager.getInstance();
    configManager = ConfigurationManager.getInstance();
  }
}
