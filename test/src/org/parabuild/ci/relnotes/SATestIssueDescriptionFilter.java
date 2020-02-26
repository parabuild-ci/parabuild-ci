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
package org.parabuild.ci.relnotes;

import junit.framework.TestCase;
import junit.framework.TestSuite;


public class SATestIssueDescriptionFilter extends TestCase {

  private IssueDescriptionFilter descriptionFilter = null;
  private static final String TEST_S1 = "";
  private static final String TEST_S2 = null;
  private static final String TEST_S3 = "test description";
  private static final String TEST_S4 = "blah[RN]";
  private static final String EXPECTED_S4_AFTER_FILTERING = TEST_S4;


  public void test_emptyFilter() throws Exception {

    descriptionFilter = new IssueDescriptionFilter(null);

    assertEquals(TEST_S1, descriptionFilter.filter(TEST_S1));
    assertEquals(TEST_S2, descriptionFilter.filter(TEST_S2));
    assertEquals(TEST_S3, descriptionFilter.filter(TEST_S3));
    assertEquals(TEST_S4, descriptionFilter.filter(TEST_S4));
  }


  public void test_filteringFilter() throws Exception {

    descriptionFilter = new IssueDescriptionFilter("[RN]");

    assertNull(descriptionFilter.filter(TEST_S1));
    assertNull(descriptionFilter.filter(TEST_S2));
    assertNull(descriptionFilter.filter(TEST_S3));
    assertNotNull(descriptionFilter.filter(TEST_S4));
    assertEquals(EXPECTED_S4_AFTER_FILTERING, descriptionFilter.filter(TEST_S4));
  }


  public static TestSuite suite() {
    return new TestSuite(SATestIssueDescriptionFilter.class);
  }


  public SATestIssueDescriptionFilter(final String s) {
    super(s);
  }
}
