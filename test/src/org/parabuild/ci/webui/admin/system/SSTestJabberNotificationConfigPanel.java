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
import org.parabuild.ci.configuration.ConfigurationManager;

/**
 * Tests JabberNotificationConfigPanel
 */
public class SSTestJabberNotificationConfigPanel extends ServletTestCase {

  private JabberNotificationConfigPanel testPanel = null;
  private ConfigurationManager cm = null;


  public SSTestJabberNotificationConfigPanel(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_create() throws Exception {
    // does nothing, set up creates the instance
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestJabberNotificationConfigPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    this.cm = ConfigurationManager.getInstance();
    this.testPanel = new JabberNotificationConfigPanel();
  }
}
