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
 * Tests home page
 */
public class SSTestLogConfigsTable extends ServersideTestCase {

  private LogConfigsTable logConfigsTable = null;
  private ConfigurationManager cm = null;
  private BuildConfig buildConfig;


  public SSTestLogConfigsTable(final String s) {
    super(s);
  }


  public void test_load() {
    logConfigsTable.load(buildConfig);
    assertEquals("Number of configured logs", cm.getLogConfigs(buildConfig.getBuildID()).size(), logConfigsTable.getRowCount());
  }


  public void test_validate() {
    // empty table validates
    assertTrue(logConfigsTable.validate());
    // filled table validates
    logConfigsTable.load(buildConfig);
    assertTrue(logConfigsTable.validate());
  }


  public void test_save() {
    logConfigsTable.load(buildConfig);
    assertTrue(logConfigsTable.save());
  }


  protected void setUp() throws Exception {
    super.setUp();
    logConfigsTable = new LogConfigsTable();
    cm = ConfigurationManager.getInstance();
    buildConfig = cm.getBuildConfiguration(TestHelper.TEST_CVS_VALID_BUILD_ID);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestLogConfigsTable.class);
  }
}
