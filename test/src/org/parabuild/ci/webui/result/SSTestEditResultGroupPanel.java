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
package org.parabuild.ci.webui.result;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.*;

/**
 */
public final class SSTestEditResultGroupPanel extends ServersideTestCase {

  private static final int TEST_RESULT_GROUP_ID = 0;

  private ResultGroupPanel panel = null;


  public void test_loadValidateSave() {
    panel.load(ResultGroupManager.getInstance().getResultGroup(TEST_RESULT_GROUP_ID));
    assertTrue(panel.validate());
    assertTrue(panel.save());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestEditResultGroupPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    panel = new ResultGroupPanel();
  }


  public SSTestEditResultGroupPanel(final String s) {
    super(s);
  }
}
