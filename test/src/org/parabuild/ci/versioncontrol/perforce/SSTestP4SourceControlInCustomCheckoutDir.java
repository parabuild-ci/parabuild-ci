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
package org.parabuild.ci.versioncontrol.perforce;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.VersionControlSystem;

/**
 * Tests P4SourceControl in a custom checkout dir.
 */
public final class SSTestP4SourceControlInCustomCheckoutDir extends SSTestP4SourceControl {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestP4SourceControlInCustomCheckoutDir.class);


  public SSTestP4SourceControlInCustomCheckoutDir(final String s) {
    super(s);
  }


  protected void afterSuperSetUp() {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE, TestHelper.getTestTempDir() + "/custom_" + TEST_BUILD_ID);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestP4SourceControlInCustomCheckoutDir.class, new String[]{
      "test_getChangesSinceDoesInludeFirstChangeListForFirstRun",
      "test_getChangesSincePicksUpIntegOnlyBranches",
      "test_bug721resyncsAfterCleanUpdates",
      "test_bug737syncToLatestWorksWithSpacedPath",
      "test_bug630syncToLatestReservedMSDOSPath",
      "test_bug630syncToLatestScrewedWindowsPath",
      "test_checkOutLatestCatchesConfigUpdates",
      "test_createClient",
      "test_syncToLatest",
      "test_createLabel"
    });
  }
}
