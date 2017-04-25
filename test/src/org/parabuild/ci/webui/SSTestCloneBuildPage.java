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

import javax.servlet.http.*;
import junit.framework.*;
import org.apache.cactus.*;
import org.apache.commons.logging.*;

import com.meterware.httpunit.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.services.*;
import org.parabuild.ci.error.*;

/**
 * Tests about page
 */
public class SSTestCloneBuildPage extends ServletTestCase {

  private static final Log log = LogFactory.getLog(SSTestCloneBuildPage.class);

  private ErrorManager errorManager;
  private BuildListService buildListService;
  private ConfigurationManager cm;


  public SSTestCloneBuildPage(final String s) {
    super(s);
  }


  /**
   * Makes sure that home page responds
   */
  public void test_clonesTheBuild() throws Exception {
    // simulate login
    // NOTE: does not work
    //User admin = ConfigurationManager.getInstance().loginAdministrator("admin", "admin");
    //assertNotNull(admin);
    //session.setAttribute(BasePage.ATTRIBUTE_USER, admin);

    // get the page
    final String urlBase = "http://localhost:" + ServiceManager.getInstance().getListenPort();
    final String url = urlBase + "/parabuild/admin/build/clone.htm?buildid=1"; // build ID 1
    final WebConversation wc = new WebConversation();
    final com.meterware.httpunit.WebResponse resp = wc.getResponse(url);
    final String text = resp.getText();
    Assert.assertEquals(HttpServletResponse.SC_OK, resp.getResponseCode());
    // TODO: implement - currently it fails at the next line - user is not logged in.
    //       the solution could be to parse login screen and try to submit it.
    //assertTrue(text.indexOf(CloneBuildPage.CAPTION_ACTIVATE) >= 0);
    //
    //// get the link
    //WebLink linkWithName = resp.getLinkWithName(CloneBuildPage.CAPTION_ACTIVATE);
    //String[] parameterValues = linkWithName.getParameterValues(Pages.PARAM_BUILD_ID);
    //int newBuildID = Integer.parseInt(parameterValues[0]);
    //
    //// *mandatory* cleanup
    //buildService.removeBuild(newBuildID);
    //cm.delete(ConfigurationManager.getInstance().getBuildConfiguration(newBuildID));
  }


  protected void setUp() throws Exception {
    super.setUp();
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
    buildListService = ServiceManager.getInstance().getBuildListService();
    cm = ConfigurationManager.getInstance();
    System.setProperty("parabuild.print.stacktrace", "true");
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestCloneBuildPage.class);
  }
}
