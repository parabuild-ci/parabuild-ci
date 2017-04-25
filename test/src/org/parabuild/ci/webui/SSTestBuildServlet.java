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

import junit.framework.*;
import org.apache.cactus.*;
import org.apache.commons.logging.*;

import com.meterware.httpunit.*;
import com.meterware.httpunit.WebResponse;
import org.parabuild.ci.TestHelper;

/**
 * Tests BuildServler
 */
public class SSTestBuildServlet extends ServletTestCase {

  private static final Log log = LogFactory.getLog(SSTestBuildServlet.class);


  public SSTestBuildServlet(final String s) {
    super(s);
  }


  /**
   * Makes sure that remote agent does redirect to agent
   * status and does show what it should.
   */
  public void test_Bug442() throws Exception {
    final WebConversation wc = new WebConversation();
    final WebResponse resp = wc.getResponse("http://" + TestHelper.remoteTestBuilderHostName() + "/parabuild/index.htm");
    // assert there is this labels there
    assertTrue(resp.getText().indexOf(AgentStatusPage.CAPTION_SERVICING_REQUESTS.trim()) >= 0);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBuildServlet.class);
  }
}
