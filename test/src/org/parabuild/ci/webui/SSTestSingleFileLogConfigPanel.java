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
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.admin.*;

/**
 * 
 */
public class SSTestSingleFileLogConfigPanel extends ServersideTestCase {

  private TextFileLogConfigPanel pnlLogConfig = null;


  /**
   */
  public void test_setBuildID() throws Exception {
    pnlLogConfig.setBuildID(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertEquals(pnlLogConfig.getBuildID(), TestHelper.TEST_CVS_VALID_BUILD_ID);
  }


  /**
   */
  public void test_load() throws Exception {
    final LogConfig logConfig = load();
    assertEquals(logConfig.getBuildID(), pnlLogConfig.getBuildID());
    assertEquals(logConfig.getID(), pnlLogConfig.getLogConfigID());
  }


  /**
   */
  public void test_validate() throws Exception {
    load();
    assertTrue(pnlLogConfig.validate());
  }


  /**
   */
  public void test_save() throws Exception {
    load();
    assertTrue(pnlLogConfig.validate());
    assertTrue(pnlLogConfig.save());
  }


  private LogConfig load() {
    final LogConfig logConfig = (LogConfig)ConfigurationManager.getInstance().getObject(LogConfig.class, 1);
    pnlLogConfig.load(logConfig);
    return logConfig;
  }


  /**
   * Required by JUnit
   */
  public SSTestSingleFileLogConfigPanel(final String s) {
    super(s);
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestSingleFileLogConfigPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    pnlLogConfig = new TextFileLogConfigPanel();
  }
}
