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
package org.parabuild.ci.tray;

import java.util.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.services.*;

/**
 * Tests proxy to build status service
 */
public class SSTestMenuStatusBuilder extends ServersideTestCase {


  public static final String TEMP_FILE_PREFIX = "parabuild";
  private BuildStatusService webService = null;
  private MenuStatusBuilder menuStatusBuilder = null;


  /**
   * makeMenuStatus
   */
  public void test_makeMenuStatus() throws Exception {
    // NOTE: vimeshev - 2006-12-23 - the idea is that at
    // least one build in default build status list is
    // inactive and never run before.
    boolean inactiveImageFound = false;
    for (final Iterator i = webService.getBuildStatusList().iterator(); i.hasNext();) {
      final BuildStatus buildStatus = (BuildStatus)i.next();
      final MenuStatus menuStatus = menuStatusBuilder.makeMenuStatus(buildStatus);
      if (menuStatus.getImage().equals(TrayImageResourceCollection.IMAGE_INACTIVE)) {
        inactiveImageFound = true;
        break;
      }
    }
    assertTrue("At least one menu status wiht inactive image should be generated", inactiveImageFound);
  }


  protected void setUp() throws Exception {
    super.setUp();
    this.webService = new BuildStatusServiceLocator("localhost:" + ServiceManager.getInstance().getListenPort(), null, null).getWebService();
    this.menuStatusBuilder = new MenuStatusBuilder();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestMenuStatusBuilder.class, new String[]{
    });
  }


  public SSTestMenuStatusBuilder(final String s) {
    super(s);
  }
}
