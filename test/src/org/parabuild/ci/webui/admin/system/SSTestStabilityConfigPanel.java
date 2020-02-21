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
package org.parabuild.ci.webui.admin.system;

import junit.framework.TestSuite;
import org.apache.cactus.ServletTestCase;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;

/**
 * Tests StabilityConfigPanel
 */
public class SSTestStabilityConfigPanel extends ServletTestCase {

  /** @noinspection UNUSED_SYMBOL,FieldCanBeLocal*/
  private StabilityConfigPanel stabilityConfigPanel = null;


  public SSTestStabilityConfigPanel(final String s) {
    super(s);
  }


  /**
   */
  public void test_validate() throws Exception {
    assertTrue(stabilityConfigPanel.validate());
  }


  /**
   */
  public void test_setSystemProperties() throws Exception {
    final SystemConfigurationManager manager = SystemConfigurationManagerFactory.getManager();
    stabilityConfigPanel.setSystemProperties(manager.getSystemProperties());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestStabilityConfigPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    stabilityConfigPanel = new StabilityConfigPanel();
  }
}
