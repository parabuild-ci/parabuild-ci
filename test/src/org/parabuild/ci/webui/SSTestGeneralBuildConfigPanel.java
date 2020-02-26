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
package org.parabuild.ci.webui;

import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.webui.admin.GeneralBuildConfigPanel;

/**
 *
 */
public class SSTestGeneralBuildConfigPanel extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestGeneralBuildConfigPanel.class);
  private static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;
  private ConfigurationManager cm;


  public SSTestGeneralBuildConfigPanel(final String s) {
    super(s);
  }


  /**
   */
  public void test_bug435_DoesNotThrowAnExceptionIfBuilderHostIsNotPresent() throws Exception {
    final BuildConfig buildConfig = new BuildConfig();
    buildConfig.setBuildID(BuildConfig.UNSAVED_ID);
    buildConfig.setBuildName("test");
    buildConfig.setScheduleType(BuildConfig.SCHEDULE_TYPE_AUTOMATIC);
    buildConfig.setSourceControl(VCSAttribute.SCM_CVS);
    buildConfig.setBuilderID(TestHelper.FAILED_BUILDER_ID);
    final GeneralBuildConfigPanel generalBuildConfigPanel = new GeneralBuildConfigPanel(buildConfig);
  }


  /**
   */
  public void test_load() throws Exception {
    final BuildConfig buildConfig = cm.getBuildConfiguration(TEST_BUILD_ID);
    final GeneralBuildConfigPanel generalBuildConfigPanel = new GeneralBuildConfigPanel(buildConfig);
    generalBuildConfigPanel.load(buildConfig);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestGeneralBuildConfigPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
  }
}
