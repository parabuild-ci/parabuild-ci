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

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test
 */
public class SATestSearchRequest extends TestCase {

  private static final String TEST_SEARCH_QUERY = "test";
  public static final String TEST_BUILD_ID = "-1";
  private SearchRequest searchRequest;


  public SATestSearchRequest(final String s) {
    super(s);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestSearchRequest.class);
  }


  /**
   * Tests getSearchQuery
   */
  public void test_getSearchQuery() {
    assertEquals(TEST_SEARCH_QUERY, searchRequest.getSearchQuery());
  }


  public void test_getParameter() {
    searchRequest.addParameter(SearchRequestParameter.BUILD_ID, TEST_BUILD_ID);
    assertEquals(TEST_BUILD_ID, searchRequest.getParameter(SearchRequestParameter.BUILD_ID));
  }


  public void testToString() {
    assertNotNull(searchRequest.toString());
  }


  protected void setUp() throws Exception {
    searchRequest = new SearchRequest(TEST_SEARCH_QUERY);
  }
}
