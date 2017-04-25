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
package org.parabuild.ci.webui.common;

import java.util.*;
import org.apache.cactus.*;

import junit.framework.*;

import org.parabuild.ci.object.*;
import viewtier.ui.*;

/**
 */
public final class SSTestParameterUtils extends ServletTestCase {

  private static final int TEST_RESULT_GROUP_ID = 0;
  private static final int TEST_BUILD_RUN_ID = 1;


  public void test_getResultGroupFromParameters() {
    final Parameters prms = new Parameters();
    prms.addParameter(Pages.PARAM_RESULT_GROUP_ID, TEST_RESULT_GROUP_ID);
    final ResultGroup resultGroupFromParameters = ParameterUtils.getResultGroupFromParameters(prms);
    assertNotNull(resultGroupFromParameters);
    assertEquals(TEST_RESULT_GROUP_ID, resultGroupFromParameters.getID());
  }


  public void test_getEditFromParametersWhenTrue() {
    final Parameters prms = new Parameters();
    prms.addParameter(Pages.PARAM_EDIT, "true");
    assertTrue(ParameterUtils.getEditFromParameters(prms));
  }


  public void test_getEditFromParametersWhenNotPresent() {
    final Parameters prms = new Parameters();
    assertTrue(!ParameterUtils.getEditFromParameters(prms));
  }


  public void test_makeBuildRunResultsParameters() {
    final Properties properties = ParameterUtils.makeBuildRunResultsParameters(TEST_BUILD_RUN_ID, Boolean.TRUE);
    assertEquals(Integer.toString(TEST_BUILD_RUN_ID), properties.getProperty(Pages.PARAM_BUILD_RUN_ID));
    assertEquals("true", properties.getProperty(Pages.PARAM_EDIT));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestParameterUtils.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public SSTestParameterUtils(final String s) {
    super(s);
  }
}
