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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.error.ErrorManagerFactory;

/**
 * Tests BuildStatusServlet
 *
 * @see JavascriptBuildStatusServlet
 */
public final class SSTestJavascriptBuildStatusServlet extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestJavascriptBuildStatusServlet.class); // NOPMD


  public void test_failedBuild() throws Exception {
    TestHelper.assertPageSmokes("/parabuild/build/status/buildstatus.js?buildid=1", "bullet_ball_glass_red.gif");
    TestHelper.assertPageSmokes("/parabuild/build/status/buildstatus.js?buildid=1&showbuildname=true", "bullet_ball_glass_red.gif");
  }


  public void test_neverRunBuildBuild() throws Exception {
    TestHelper.assertPageSmokes("/parabuild/build/status/buildstatus.js?buildid=5", "bullet_ball_glass_blue.gif");
  }


  public void test_neverExistingBuild() throws Exception {
    TestHelper.assertPageSmokes("/parabuild/build/status/buildstatus.js?buildid=999", "bullet_ball_glass_yellow.gif");
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    ErrorManagerFactory.getErrorManager().clearAllActiveErrors();
  }


  protected void tearDown() throws Exception {
    assertEquals(0, ErrorManagerFactory.getErrorManager().errorCount());
    super.tearDown();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestJavascriptBuildStatusServlet.class);
  }


  public SSTestJavascriptBuildStatusServlet(final String s) {
    super(s);
  }
}
