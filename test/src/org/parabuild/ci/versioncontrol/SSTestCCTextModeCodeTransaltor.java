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

import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;

/**
 *
 */
public class SSTestCCTextModeCodeTransaltor extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestCCTextModeCodeTransaltor.class);
  private Agent agent;
  private ClearCaseTextModeCodeTranslator tr;


  public SSTestCCTextModeCodeTransaltor(final String s) {
    super(s);
  }


  public void test_translate() throws Exception {
    assertEquals("", tr.translateTextModeCode(VersionControlSystem.CLEARCASE_TEXT_MODE_NOT_SET));
    assertEquals("msdos", tr.translateTextModeCode(VersionControlSystem.CLEARCASE_TEXT_MODE_MSDOS));
    assertEquals("unix", tr.translateTextModeCode(VersionControlSystem.CLEARCASE_TEXT_MODE_UNIX));
    assertEquals("transparent", tr.translateTextModeCode(VersionControlSystem.CLEARCASE_TEXT_MODE_TRANSPARENT));
    assertEquals("strip_cr", tr.translateTextModeCode(VersionControlSystem.CLEARCASE_TEXT_MODE_STRIP_CR));
    assertEquals("insert_cr", tr.translateTextModeCode(VersionControlSystem.CLEARCASE_TEXT_MODE_INSERT_CR));
  }


  public void test_translateWindows() throws Exception {
    if (agent.isWindows()) {
      assertEquals("msdos", tr.translateTextModeCode(VersionControlSystem.CLEARCASE_TEXT_MODE_AUTO));
    }
  }


  public void test_translateUnix() throws Exception {
    if (!agent.isWindows()) {
      assertEquals("unix", tr.translateTextModeCode(VersionControlSystem.CLEARCASE_TEXT_MODE_AUTO));
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestCCTextModeCodeTransaltor.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    agent = AgentManager.getInstance().getNextLiveAgent(TestHelper.TEST_CLEARCASE_VALID_BUILD_ID);
    tr = new ClearCaseTextModeCodeTranslator(agent);
  }
}
