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
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.admin.*;

/**
 *
 */
public class SSTestLogConfigPanelFactory extends ServersideTestCase {

  /**
   */
  public void test_makesJUnitPanel() throws Exception {
    final AbstractLogConfigPanel panel = LogConfigPanelFactory.makeLogConfigPanel(LogConfig.LOG_TYPE_JNUIT_XML_DIR);
    assertTrue(panel instanceof JUnitLogConfigPanel);
  }


  /**
   */
  public void test_makesFileListLogConfigPanel() throws Exception {
    final AbstractLogConfigPanel panel = LogConfigPanelFactory.makeLogConfigPanel(LogConfig.LOG_TYPE_TEXT_DIR);
    assertTrue(panel instanceof TextDirLogConfigPanel);
  }


  /**
   */
  public void test_makesSingleFileLogConfigPanel() throws Exception {
    final AbstractLogConfigPanel panel = LogConfigPanelFactory.makeLogConfigPanel(LogConfig.LOG_TYPE_TEXT_FILE);
    assertTrue(panel instanceof TextFileLogConfigPanel);
  }


  /**
   */
  public void test_makesPMDLogConfigPanel() throws Exception {
    final AbstractLogConfigPanel panel = LogConfigPanelFactory.makeLogConfigPanel(LogConfig.LOG_TYPE_PMD_XML_FILE);
    assertTrue(panel instanceof PMDLogConfigPanel);
  }


  /**
   */
  public void test_makesCheckstyleLogConfigPanel() throws Exception {
    final AbstractLogConfigPanel panel = LogConfigPanelFactory.makeLogConfigPanel(LogConfig.LOG_TYPE_CHECKSTYLE_XML_FILE);
    assertTrue(panel instanceof CheckstyleLogConfigPanel);
  }


  /**
   */
  public void test_makesFindbugsLogConfigPanel() throws Exception {
    final AbstractLogConfigPanel panel = LogConfigPanelFactory.makeLogConfigPanel(LogConfig.LOG_TYPE_FINDBUGS_XML_FILE);
    assertTrue(panel instanceof FindbugsLogConfigPanel);
  }


  /**
   */
  public void test_makesHTMLFileLogConfigPanel() throws Exception {
    final AbstractLogConfigPanel panel = LogConfigPanelFactory.makeLogConfigPanel(LogConfig.LOG_TYPE_HTML_FILE);
    assertTrue(panel instanceof HTMLFileLogConfigPanel);
  }


  /**
   */
  public void test_failsOnUnknowType() throws Exception {
    try {
      LogConfigPanelFactory.makeLogConfigPanel(111);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
    }
  }


  /**
   * Required by JUnit
   */
  public SSTestLogConfigPanelFactory(final String s) {
    super(s);
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestLogConfigPanelFactory.class);
  }
}
