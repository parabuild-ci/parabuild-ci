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
package org.parabuild.ci.webui.admin;

import org.apache.cactus.ServletTestCase;

import junit.framework.TestSuite;

import org.parabuild.ci.configuration.ConfigurationManager;

/**
 * Tests DependenceSettingsPanel
 */
public final class SSTestDependenceSettingsPanel extends ServletTestCase {

  /** @noinspection UNUSED_SYMBOL,FieldCanBeLocal*/
  private DependenceSettingsPanel testPanel = null;
  private static final int BUILD_ID = 1;


  public SSTestDependenceSettingsPanel(final String s) {
    super(s);
  }


  /**
   */
  public void test_create() throws Exception {
    // do nothing, create is called in setUp method.
  }


  public void test_setBuildID() throws Exception {
    testPanel.setBuildID(BUILD_ID);
  }


  public void test_load() throws Exception {
    testPanel.load(ConfigurationManager.getInstance().getActiveBuildConfig(BUILD_ID));
  }


  public void test_validate() throws Exception {
    testPanel.load(ConfigurationManager.getInstance().getActiveBuildConfig(BUILD_ID));
    assertTrue(testPanel.validate());
  }


  public void test_save() throws Exception {
    testPanel.load(ConfigurationManager.getInstance().getActiveBuildConfig(BUILD_ID));
    assertTrue(testPanel.save());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestDependenceSettingsPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    testPanel = new DependenceSettingsPanel();
  }
}
