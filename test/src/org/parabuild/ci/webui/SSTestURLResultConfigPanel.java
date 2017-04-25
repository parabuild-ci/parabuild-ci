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
import org.parabuild.ci.webui.admin.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 *
 */
public class SSTestURLResultConfigPanel extends ServersideTestCase {

  private DirResultConfigPanel pnlResultConfig = null;
  private ConfigurationManager cm;


  /**
   */
  public void test_setBuildID() throws Exception {
    pnlResultConfig.setBuildID(TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertEquals(pnlResultConfig.getBuildID(), TestHelper.TEST_CVS_VALID_BUILD_ID);
  }


  /**
   */
  public void test_load() throws Exception {
    final ResultConfig resultConfig = load();
    assertEquals(resultConfig.getBuildID(), pnlResultConfig.getBuildID());
    assertEquals(resultConfig.getID(), pnlResultConfig.getResultConfigID());
  }


  /**
   */
  public void test_validate() throws Exception {
    load();
    assertTrue(pnlResultConfig.validate());
  }


  /**
   */
  public void test_save() throws Exception {
    load();
    assertTrue(pnlResultConfig.validate());
    assertTrue(pnlResultConfig.save());
  }


  private ResultConfig load() {
    final ResultConfig resultConfig = createAndSaveResultConfig();
    pnlResultConfig.load(resultConfig);
    return resultConfig;
  }


  /**
   * Helper method.
   *
   */
  private ResultConfig createAndSaveResultConfig() {
    final ResultConfig resultConfig = new ResultConfig();
    resultConfig.setBuildID(TestHelper.TEST_CVS_VALID_BUILD_ID);
    resultConfig.setDescription("Description");
    resultConfig.setPath("http://www.yahoo.com");
    resultConfig.setType(ResultConfig.RESULT_TYPE_URL);
    cm.saveObject(resultConfig);
    return resultConfig;
  }


  /**
   * Required by JUnit
   */
  public SSTestURLResultConfigPanel(final String s) {
    super(s);
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestURLResultConfigPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
    pnlResultConfig = new DirResultConfigPanel();
  }
}
