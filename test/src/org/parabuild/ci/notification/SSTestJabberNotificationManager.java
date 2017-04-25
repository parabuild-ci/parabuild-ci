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
package org.parabuild.ci.notification;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.error.*;

/**
 * Tests JabberNotificationManager
 */
public class SSTestJabberNotificationManager extends ServersideTestCase {

  private JabberNotificationManager notifucationManager = null;
  private ErrorManager errorManager;


  public SSTestJabberNotificationManager(final String s) {
    super(s);
  }


  /**
   * Make sure doesn't throw any exception
   */
  public void test_isJabberEnabled() throws Exception {
    assertTrue(!notifucationManager.isServerEnabled());
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();
    errorManager = ErrorManagerFactory.getErrorManager();
    notifucationManager = new JabberNotificationManager();
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestJabberNotificationManager.class);
  }
}
