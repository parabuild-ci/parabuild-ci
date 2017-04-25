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
import org.parabuild.ci.security.*;

/**
 * Tests ResultsPanel
 */
public final class SSTestResultsPanel extends ServersideTestCase {

  private static final int TEST_BUILD_RUN_ID = 1;
  private ConfigurationManager cm;


  public SSTestResultsPanel(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_createReadOnly() throws Exception {
    // no exceptions thrown
    new ResultsPanel(cm.getBuildRun(TEST_BUILD_RUN_ID), false, BuildRights.ALL_RIGHTS);
  }


  /**
   *
   */
  public void test_createEditable() throws Exception {
    // no exceptions thrown
    new ResultsPanel(cm.getBuildRun(TEST_BUILD_RUN_ID), true, BuildRights.ALL_RIGHTS);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestResultsPanel.class);
  }
}
