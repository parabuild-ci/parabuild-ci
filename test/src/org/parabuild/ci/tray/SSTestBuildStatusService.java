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
import org.apache.commons.logging.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.services.*;

/**
 * Tests proxy to build status service
 */
public class SSTestBuildStatusService extends ServersideTestCase {


  private static final Log log = LogFactory.getLog(SSTestBuildStatusService.class);
  public static final String TEMP_FILE_PREFIX = "parabuild";
  private BuildStatusService webService;


  /**
   * getBuildStatusList
   */
  public void test_getBuildStatusList() throws Exception {
    final List buildStatusList = webService.getBuildStatusList();
    assertNotNull(buildStatusList);
    assertTrue(!buildStatusList.isEmpty());
  }


  protected void setUp() throws Exception {
    super.setUp();
    this.webService = new BuildStatusServiceLocator("localhost:" + ServiceManager.getInstance().getListenPort(), null, null).getWebService();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestBuildStatusService.class, new String[]{
    });
  }


  public SSTestBuildStatusService(final String s) {
    super(s);
  }
}
