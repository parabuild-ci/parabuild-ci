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
import org.parabuild.ci.archive.*;
import org.parabuild.ci.configuration.*;

/**
 * Tests StepResultPanel
 */
public final class SSTestStepResultPanel extends ServersideTestCase {

  private static final int TEST_ACTIVE_BUILD_ID = 1;
  private static final int TEST_STEP_RESULT_ID = 0;

  private ConfigurationManager cm;
  private StepResultPanel pnlStepResult;


  public SSTestStepResultPanel(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_setStepResult() throws Exception {
    // no exceptions thrown
    pnlStepResult.setStepResult(TEST_ACTIVE_BUILD_ID, ArchiveManagerFactory.getArchiveManager(TEST_ACTIVE_BUILD_ID), cm.getStepResult(TEST_STEP_RESULT_ID), true);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
    pnlStepResult = new StepResultPanel();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestStepResultPanel.class);
  }
}
