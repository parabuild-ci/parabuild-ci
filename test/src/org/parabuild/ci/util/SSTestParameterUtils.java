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
package org.parabuild.ci.util;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * Tests home page
 */
public class SSTestParameterUtils extends ServersideTestCase {

  public SSTestParameterUtils(final String s) {
    super(s);
  }


  /**
   */
  public void testgetBuildIDFromParameters() throws Exception {
    final Parameters parameters = new Parameters();
    parameters.addParameter(Pages.PARAM_BUILD_ID, "2");
    assertEquals(ParameterUtils.getActiveBuildIDFromParameters(parameters).intValue(), 2);
  }


  /**
   */
  public void testCanGetBuildConfigFromParameters() throws Exception {
    final Parameters parameters = new Parameters();
    parameters.addParameter(Pages.PARAM_BUILD_ID, "1");

    // test can get existing build config
    final BuildConfig buildConfiguration = ParameterUtils.getActiveBuildConfigFromParameters(parameters);
    assertNotNull(buildConfiguration);
    assertEquals(buildConfiguration.getBuildID(), 1);
  }


  /**
   */
  public void testCanNotGetBuildConfigFromParameters() throws Exception {
    final Parameters parameters = new Parameters();
    parameters.addParameter(Pages.PARAM_BUILD_ID, "55555555");

    // test can not get non existing build config
    final BuildConfig buildConfiguration = ParameterUtils.getActiveBuildConfigFromParameters(parameters);
    assertNull(buildConfiguration);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestParameterUtils.class);
  }
}
