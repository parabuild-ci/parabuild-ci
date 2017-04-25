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
package org.parabuild.ci.build;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.services.*;

/**
 * Tests ServiceManager page
 */
public class SSTestServiceManager extends ServersideTestCase {

  private ServiceManager serviceManager = null;


  public SSTestServiceManager(final String s) {
    super(s);
  }


  /**
   */
  public void testgetDatabaseService() throws Exception {
    assertNotNull(serviceManager.getDatabaseService());
  }


  /**
   */
  public void testgetNotificationService() throws Exception {
    assertNotNull(serviceManager.getNotificationService());
  }


  /**
   */
  public void testgetConfigurationService() throws Exception {
    assertNotNull(serviceManager.getConfigurationService());
  }


  /**
   */
  public void testgetBuildService() throws Exception {
    assertNotNull(serviceManager.getBuildListService());
  }


  protected void setUp() throws Exception {
    super.setUp();
    serviceManager = ServiceManager.getInstance();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestServiceManager.class);
  }
}
