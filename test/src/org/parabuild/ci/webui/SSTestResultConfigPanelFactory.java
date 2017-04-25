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
public class SSTestResultConfigPanelFactory extends ServersideTestCase {

  /**
   */
  public void test_makesDirResultConfigPanel() throws Exception {
    final AbstractResultConfigPanel panel = ResultConfigPanelFactory.makeResultConfigPanel(ResultConfig.RESULT_TYPE_DIR);
    assertTrue(panel instanceof DirResultConfigPanel);
  }


  /**
   */
  public void test_makeSingleFileResultConfigPanel() throws Exception {
    final AbstractResultConfigPanel panel = ResultConfigPanelFactory.makeResultConfigPanel(ResultConfig.RESULT_TYPE_FILE_LIST);
    assertTrue(panel instanceof FileListResultConfigPanel);
  }


  /**
   */
  public void test_makeURLResultConfigPanel() throws Exception {
    final AbstractResultConfigPanel panel = ResultConfigPanelFactory.makeResultConfigPanel(ResultConfig.RESULT_TYPE_URL);
    assertTrue(panel instanceof URLResultConfigPanel);
  }


  /**
   */
  public void test_failsOnUnknowType() throws Exception {
    try {
      ResultConfigPanelFactory.makeResultConfigPanel(111);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
    }
  }


  /**
   * Required by JUnit
   */
  public SSTestResultConfigPanelFactory(final String s) {
    super(s);
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestResultConfigPanelFactory.class);
  }
}
