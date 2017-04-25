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
package org.parabuild.ci.feed;

import org.apache.commons.logging.*;

import junit.framework.*;

import com.meterware.httpunit.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.error.*;

/**
 * Tests BuildStatusServlet
 *
 * @see org.parabuild.ci.webui.JavascriptBuildStatusServlet
 */
public final class SSTestStatusFeedServlet extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestStatusFeedServlet.class); // NOPMD


  public void test_getAllBuildsFeed() throws Exception {
    final WebResponse webResponse = TestHelper.assertPageSmokes("/parabuild/build/status/feed.xml", "");
    // TODO: parsing and testing feed
  }


  public void test_getBuildFeed() throws Exception {
    final WebResponse webResponse = TestHelper.assertPageSmokes("/parabuild/build/status/feed.xml?buildid=1", "");
    // TODO: parsing and testing feed
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
    return new TestSuite(SSTestStatusFeedServlet.class);
  }


  public SSTestStatusFeedServlet(final String s) {
    super(s);
  }
}
