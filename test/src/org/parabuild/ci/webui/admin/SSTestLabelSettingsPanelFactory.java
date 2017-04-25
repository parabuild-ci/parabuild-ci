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

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 *
 */
public final class SSTestLabelSettingsPanelFactory extends ServersideTestCase {

  /**
   */
  public void test_getPanelEnablesLabelDeletingForP4() throws Exception {
    final LabelSettingsPanel panel = makePanel(TestHelper.TEST_P4_VALID_BUILD_ID);
    assertTrue("Leabel deleting", panel.isLabelDeletingEnabled());
  }


  public void test_getPanelDoesNotEnableLabelDeletingForCVS() throws Exception {
    final LabelSettingsPanel panel = makePanel(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertTrue("Leabel deleting", !panel.isLabelDeletingEnabled());
  }


  private LabelSettingsPanel makePanel(final int buildID) {
    final BuildConfig bc = ConfigurationManager.getInstance().getBuildConfiguration(buildID);
    return LabelSettingsPanelFactory.getPanel(bc);
  }


  /**
   * Required by JUnit
   */
  public SSTestLabelSettingsPanelFactory(final String s) {
    super(s);
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestLabelSettingsPanelFactory.class);
  }
}
