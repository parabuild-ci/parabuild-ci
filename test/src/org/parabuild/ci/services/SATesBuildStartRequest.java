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
package org.parabuild.ci.services;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests BuildStartRequest
 */
public final class SATesBuildStartRequest extends TestCase {

  private static final int TEST_USER_ID = 999;
  private static final String TEST_LABEL = "test_label";


  /**
   * Makes sure that it doesn't throw the exception
   */
  public void test_create() throws Exception {
    // parameter values
    final List testValueList = new ArrayList(3);
    testValueList.add("test_value");

    // paramters
    final List testParameterList = new ArrayList(3);
    testParameterList.add(new BuildStartRequestParameter("test_name", "test_description", testValueList, 0));

    // request
    final BuildStartRequest request = new BuildStartRequest(TEST_USER_ID, testParameterList, TEST_LABEL);
    assertEquals(1, request.parameterList().size());
    assertEquals(TEST_USER_ID, request.userID());
    assertEquals(TEST_LABEL, request.label());
    assertNull("Default version template should be blank", request.versionTemplate());
    assertEquals("Default version template should be equal -1", -1, request.versionCounter());
  }


  public SATesBuildStartRequest(final String s) {
    super(s);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATesBuildStartRequest.class);
  }
}
