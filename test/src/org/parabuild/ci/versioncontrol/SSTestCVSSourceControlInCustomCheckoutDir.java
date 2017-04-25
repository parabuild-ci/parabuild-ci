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
package org.parabuild.ci.versioncontrol;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.object.SourceControlSetting;

/**
 * Tests CVSSourceControl in a custom checkout dir.
 */
public final class SSTestCVSSourceControlInCustomCheckoutDir extends SSTestCVSSourceControl {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SSTestCVSSourceControlInCustomCheckoutDir.class); // NOPMD


  public SSTestCVSSourceControlInCustomCheckoutDir(final String s) {
    super(s);
  }


  protected void afterSuperSetUp() {
    TestHelper.setSourceControlProperty(getTestBuildID(), SourceControlSetting.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE, getCheckoutDir());
  }


  private String getCheckoutDir() {
    return TestHelper.getTestTempDir() + "/custom_" + getTestBuildID();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestCVSSourceControlInCustomCheckoutDir.class,
            new String[]{
                    "test_syncToChangeList",
            });
  }
}
