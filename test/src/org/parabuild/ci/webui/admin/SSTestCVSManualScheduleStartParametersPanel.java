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

import java.util.*;
import org.apache.cactus.*;

import junit.framework.*;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 * Tests CVSManualScheduleStartParametersPanel
 */
public class SSTestCVSManualScheduleStartParametersPanel extends ServletTestCase {

  private static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;
  private static final String TEST_BRANCH_NAME = "some_test_branch_name";

  private CVSManualScheduleStartParametersPanel panel = null;


  public SSTestCVSManualScheduleStartParametersPanel(final String s) {
    super(s);
  }


  /**
   * Makes sure that it doesn't throw the exception
   */
  public void test_load() throws Exception {

    ConfigurationManager.getInstance().saveObject(new SourceControlSetting(TEST_BUILD_ID, VCSAttribute.CVS_BRANCH_NAME, TEST_BRANCH_NAME));
    panel.load(TEST_BUILD_ID);

    boolean found = false;
    final List updatedSettings = panel.getUpdatedSettings();
    for (int i = 0; i < updatedSettings.size(); i++) {
      final SourceControlSettingVO vo = (SourceControlSettingVO)updatedSettings.get(i);
      if (vo.getName().equals(SourceControlSettingVO.CVS_BRANCH_NAME) && vo.getValue().equals(TEST_BRANCH_NAME)) {
        found = true;
        break;
      }
    }
    assertTrue("Setting should be found", found);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestCVSManualScheduleStartParametersPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    TestHelper.changeScheduleType(TEST_BUILD_ID, ActiveBuildConfig.SCHEDULE_TYPE_MANUAL);
    panel = new CVSManualScheduleStartParametersPanel();
  }
}
