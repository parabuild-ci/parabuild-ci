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
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;

/**
 * Tests SystemConfigPanel
 */
public class SSTestSystemConfigPanel extends ServletTestCase {

  private EmailNotificationConfigPanel systemConfigPanel = null;


  public SSTestSystemConfigPanel(final String s) {
    super(s);
  }


  /**
   * Makes sure that it doesn't throw the exception
   */
  public void test_setSystemProperties() throws Exception {
    systemConfigPanel.setSystemProperties(SystemConfigurationManagerFactory.getManager().getSystemProperties());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestSystemConfigPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    systemConfigPanel = new EmailNotificationConfigPanel();
  }
}
