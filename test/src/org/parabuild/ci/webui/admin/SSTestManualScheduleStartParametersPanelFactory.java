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
package org.parabuild.ci.webui.admin;

import org.apache.cactus.*;

import junit.framework.*;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.object.*;

/**
 * Tests ManualScheduleStartParametersPanelFactory
 */
public class SSTestManualScheduleStartParametersPanelFactory extends ServletTestCase {

  public SSTestManualScheduleStartParametersPanelFactory(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_makeCVSPanel() throws Exception {
    TestHelper.changeScheduleType(TestHelper.TEST_CVS_VALID_BUILD_ID, ActiveBuildConfig.SCHEDULE_TYPE_MANUAL);
    final ManualScheduleStartParametersPanel manualScheduleStartParametersPanel = ManualScheduleStartParametersPanelFactory.makePanel(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertTrue("Type should be CVSManualScheduleStartParametersPanel but it was " + manualScheduleStartParametersPanel.getClass().getName(), manualScheduleStartParametersPanel instanceof CVSManualScheduleStartParametersPanel);
  }


  /**
   *
   */
  public void test_makeSVNPanel() throws Exception {
    TestHelper.changeScheduleType(TestHelper.TEST_SVN_VALID_BUILD_ID, ActiveBuildConfig.SCHEDULE_TYPE_MANUAL);
    assertTrue(ManualScheduleStartParametersPanelFactory.makePanel(TestHelper.TEST_SVN_VALID_BUILD_ID)
      instanceof SVNManualScheduleStartParametersPanel);
  }


  /**
   *
   */
  public void test_makeOtherPanel() throws Exception {
    assertTrue(ManualScheduleStartParametersPanelFactory.makePanel(TestHelper.TEST_PVCS_VALID_BUILD_ID)
      instanceof EmptyManualScheduleStartParametersPanel);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestManualScheduleStartParametersPanelFactory.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
