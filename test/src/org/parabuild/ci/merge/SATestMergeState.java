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
package org.parabuild.ci.merge;

import junit.framework.TestCase;

/**
 */
public final class SATestMergeState extends TestCase {

  private MergeState mergeState = null;
  private static final String TEST_DESCRIPTION = "test description";
  private static final int TEST_ID = 999;
  private static final String TEST_MARKER = "test marker";
  private static final String TEST_NAME = "test name";


  public void test_setDescription() {
    mergeState.setDescription(TEST_DESCRIPTION);
    assertEquals(TEST_DESCRIPTION, mergeState.getDescription());
  }


  public void test_setId() {
    mergeState.setActiveMergeConfigurationID(TEST_ID);
    assertEquals(TEST_ID, mergeState.getActiveMergeConfigurationID());
  }


  public void test_setMarker() {
    mergeState.setMarker(TEST_MARKER);
    assertEquals(TEST_MARKER, mergeState.getMarker());
  }


  public void test_setName() {
    mergeState.setName(TEST_NAME);
    assertEquals(TEST_NAME, mergeState.getName());
  }


  public void test_setStatus() {
    mergeState.setStatus(MergeStatus.CHECKING_OUT);
    assertEquals(MergeStatus.CHECKING_OUT, mergeState.getStatus());
  }


  protected void setUp() throws Exception {
    super.setUp();
    mergeState = new MergeState();
  }


  public SATestMergeState(final String s) {
    super(s);
  }
}
