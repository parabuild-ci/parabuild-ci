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
import junit.framework.*;
import org.apache.cactus.*;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.util.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.versioncontrol.*;

/**
 * Tests ClearCaseSettingsPanel
 */
public class SSTestCCSettingsPanel extends ServletTestCase {

  private ClearCaseSettingsPanel clearCasePanel = null;


  public SSTestCCSettingsPanel(final String s) {
    super(s);
  }


  /**
   * Makes sure that it doesn't throw the exception
   */
  public void test_setSettings_Bug143() throws Exception {
    clearCasePanel.load(ConfigurationManager.getInstance().getActiveBuildConfig(TestHelper.TEST_CLEARCASE_VALID_BUILD_ID));
  }


  /**
   * Text mode returned as valid integer code.
   */
  public void test_setGetSettings_Bug777() throws Exception {
    // process current content of dataset.xml
    assertEquals(VCSAttribute.CLEARCASE_TEXT_MODE_NOT_SET, loadAndReturnCode());

    // alter to contan "left" text value
    alterTextMode(ClearCaseTextModeCodeTranslator.NAME_NOT_SET);
    assertEquals(VCSAttribute.CLEARCASE_TEXT_MODE_NOT_SET, loadAndReturnCode());

    // alter to contan "left" text value
    alterTextMode(ClearCaseTextModeCodeTranslator.NAME_MSDOS);
    assertEquals(VCSAttribute.CLEARCASE_TEXT_MODE_MSDOS, loadAndReturnCode());
  }


  /**
   */
  public void test_setGetSettingsHandlesTotallyInvalidValue_Bug777() throws Exception {
    // alter to contan non-existent text value
    alterTextMode("blah");
    assertEquals(VCSAttribute.CLEARCASE_TEXT_MODE_NOT_SET, loadAndReturnCode());
  }


  /**
   */
  public void test_setGetSettingsHandlesUnexistingValue_Bug777() throws Exception {
    // alter to contan non-existent number value
    alterTextMode("999999");
    assertEquals(VCSAttribute.CLEARCASE_TEXT_MODE_NOT_SET, loadAndReturnCode());
  }


  private void alterTextMode(final String value) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    SourceControlSetting sourceControlSetting = cm.getSourceControlSetting(TestHelper.TEST_CLEARCASE_VALID_BUILD_ID, VCSAttribute.CLEARCASE_TEXT_MODE);
    if (sourceControlSetting == null) {
      sourceControlSetting = new SourceControlSetting();
      sourceControlSetting.setBuildID(TestHelper.TEST_CLEARCASE_VALID_BUILD_ID);
      sourceControlSetting.setPropertyName(VCSAttribute.CLEARCASE_TEXT_MODE);
    }
    sourceControlSetting.setPropertyValue(value);
    cm.saveObject(sourceControlSetting);
  }


  private int loadAndReturnCode() {
    clearCasePanel.load(ConfigurationManager.getInstance().getActiveBuildConfig(TestHelper.TEST_CLEARCASE_VALID_BUILD_ID));
    final List updatedSettings = clearCasePanel.getUpdatedSettings();
    boolean found = false;
    int code = -1;
    for (int i = 0; i < updatedSettings.size(); i++) {
      final SourceControlSetting setting = (SourceControlSetting)updatedSettings.get(i);
      if (setting.getPropertyName().equals(VCSAttribute.CLEARCASE_TEXT_MODE)) {
        final String value = setting.getPropertyValue();
        assertTrue(StringUtils.isValidInteger(value));
        code = Integer.parseInt(value);
        found = true;
      }
    }
    assertTrue(found);
    return code;
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestCCSettingsPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    clearCasePanel = new ClearCaseSettingsPanel();
  }
}
