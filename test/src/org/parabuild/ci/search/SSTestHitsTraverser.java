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
package org.parabuild.ci.search;

import java.io.*;
import junit.framework.*;
import org.apache.commons.logging.*;
import org.apache.lucene.document.*;
import org.apache.lucene.queryParser.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestIndexRequest;
import org.parabuild.ci.services.*;

/**
 */
public class SSTestHitsTraverser extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestHitsTraverser.class);

  private SearchManager searchManager = null;
  private SearchHitsTraverser hitsTraverser = null;


  public SSTestHitsTraverser(final String s) {
    super(s);
  }


  public void test_traverse() throws InterruptedException, ParseException, IOException {
    final MockTraverserCallback callback = new MockTraverserCallback();
    hitsTraverser.traverse(searchManager.search(new SearchRequest("BUILD SUCCESSFUL")), callback);
    assertTrue("foundStepLog should have been called", callback.foundStepLogWasCalled());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestHitsTraverser.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    // put in index request
    final SearchService searchService = ServiceManager.getInstance().getSearchService();
    searchService.queueIndexRequest(new TestIndexRequest());

    // get search manager
    searchManager = SearchManager.getInstance();

    // create traverser
    hitsTraverser = new SearchHitsTraverser();
  }


  /**
   * Mock implementation of HitsTraverserCallback
   *
   * @see SSTestHitsTraverser#test_traverse
   * @see HitsTraverserCallback
   * @see SearchHitsTraverser
   */
  private static class MockTraverserCallback implements HitsTraverserCallback {

    private boolean foundStepLogWasCalled = false;


    /**
     * Callback method.
     *
     * @param document
     */
    public void foundStepLog(final Document document) {
      foundStepLogWasCalled = true;
    }


    public void foundChangeList(final Document document) {
    }


    public void foundResult(final Document document) {
    }


    public boolean foundStepLogWasCalled() {
      return foundStepLogWasCalled;
    }
  }
}
