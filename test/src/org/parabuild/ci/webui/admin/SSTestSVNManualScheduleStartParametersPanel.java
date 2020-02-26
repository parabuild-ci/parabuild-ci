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
 * Tests SVNManualScheduleStartParametersPanel page
 */
public class SSTestSVNManualScheduleStartParametersPanel extends ServletTestCase {

  private static final int TEST_BUILD_ID = TestHelper.TEST_SVN_VALID_BUILD_ID;
  private static final String TEST_DEPOT_PATH = "/some/test/depot/path";

  private SVNManualScheduleStartParametersPanel panel = null;


  public SSTestSVNManualScheduleStartParametersPanel(final String s) {
    super(s);
  }


  /**
   * Makes sure that it doesn't throw the exception
   */
  public void test_load() throws Exception {

    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final SourceControlSetting setting = cm.getSourceControlSetting(TEST_BUILD_ID, VCSAttribute.SVN_DEPOT_PATH);
    setting.setPropertyValue(TEST_DEPOT_PATH);
    cm.saveObject(setting);
    panel.load(TEST_BUILD_ID);

    boolean found = false;
    final List updatedSettings = panel.getUpdatedSettings();
    for (int i = 0; i < updatedSettings.size(); i++) {
      final SourceControlSettingVO vo = (SourceControlSettingVO)updatedSettings.get(i);
      if (vo.getName().equals(SourceControlSettingVO.SVN_DEPOT_PATH) && vo.getValue().equals(TEST_DEPOT_PATH)) {
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
    return new TestSuite(SSTestSVNManualScheduleStartParametersPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    TestHelper.changeScheduleType(TEST_BUILD_ID, ActiveBuildConfig.SCHEDULE_TYPE_MANUAL);
    panel = new SVNManualScheduleStartParametersPanel();
  }
}
