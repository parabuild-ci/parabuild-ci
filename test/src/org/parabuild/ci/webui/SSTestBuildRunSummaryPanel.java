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

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.*;

/**
 * Tests BuildRunSummaryPanel
 */
public final class SSTestBuildRunSummaryPanel extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL,FieldCanBeLocal*/
  private BuildRunSummaryPanel panel;
  private static final int TEST_BUILD_RUN_ID = 1;


  public SSTestBuildRunSummaryPanel(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_setBuildRun() throws Exception {
    // no exceptions should be thrown
    panel.setBuildRun(ConfigurationManager.getInstance().getBuildRun(TEST_BUILD_RUN_ID));
  }


  protected void setUp() throws Exception {
    super.setUp();
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    panel = new BuildRunSummaryPanel();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBuildRunSummaryPanel.class);
  }
}
