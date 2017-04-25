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
package org.parabuild.ci.object;

import junit.framework.*;

/**
 *
 */
public class SATestGroup extends TestCase {

  private static final boolean TEST_ALLOWED_TO_CREATE_RESULT_GROUP = true;
  private static final boolean TEST_ALLOWED_TO_DELETE_RESULT_GROUP = true;
  private static final boolean TEST_ALLOWED_TO_DELETE_RESULTS = true;
  private static final boolean TEST_ALLOWED_TO_PUBLISH_RESULTS = true;
  private static final boolean TEST_ALLOWED_TO_UPDATE_RESULT_GROUP = true;
  private static final boolean TEST_ALLOWED_TO_VIEW_RESULT_GROUP = true;


  private Group group;


  public void test_setAllowedToCreateResultGroup() {
    group.setAllowedToCreateResultGroup(TEST_ALLOWED_TO_CREATE_RESULT_GROUP);
    assertEquals(TEST_ALLOWED_TO_CREATE_RESULT_GROUP, group.isAllowedToCreateResultGroup());
  }


  public void test_setAllowedToDeleteResultGroup() {
    group.setAllowedToDeleteResultGroup(TEST_ALLOWED_TO_DELETE_RESULT_GROUP);
    assertEquals(TEST_ALLOWED_TO_DELETE_RESULT_GROUP, group.isAllowedToDeleteResultGroup());
  }


  public void test_setAllowedToDeleteResults() {
    group.setAllowedToDeleteResults(TEST_ALLOWED_TO_DELETE_RESULTS);
    assertEquals(TEST_ALLOWED_TO_DELETE_RESULTS, group.isAllowedToDeleteResults());
  }


  public void test_setAllowedToPublishResults() {
    group.setAllowedToPublishResults(TEST_ALLOWED_TO_PUBLISH_RESULTS);
    assertEquals(TEST_ALLOWED_TO_PUBLISH_RESULTS, group.isAllowedToPublishResults());
  }


  public void test_setAllowedToUpdateResultGroup() {
    group.setAllowedToUpdateResultGroup(TEST_ALLOWED_TO_UPDATE_RESULT_GROUP);
    assertEquals(TEST_ALLOWED_TO_UPDATE_RESULT_GROUP, group.isAllowedToUpdateResultGroup());
  }


  public void test_setAllowedToViewResultGroup() {
    assertEquals(true, group.isAllowedToViewResultGroup()); // tests default value
    group.setAllowedToViewResultGroup(TEST_ALLOWED_TO_VIEW_RESULT_GROUP);
    assertEquals(TEST_ALLOWED_TO_VIEW_RESULT_GROUP, group.isAllowedToViewResultGroup());
  }


  protected void setUp() throws Exception {
    super.setUp();
    group = new Group();

  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestGroup.class);
  }


  public SATestGroup(final String s) {
    super(s);
  }
}
