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

import org.apache.cactus.*;

import junit.framework.*;

import com.meterware.httpunit.WebResponse;
import org.parabuild.ci.TestHelper;

/**
 * Tests about page
 */
public class SSTestBuildRunChangesPage extends ServletTestCase {

  public SSTestBuildRunChangesPage(final String s) {
    super(s);
  }


  /**
   * Makes sure that home page responds
   */
  public void test_BuildChangesPage() throws Exception {
    // has expected substrings
    TestHelper.assertPageSmokes("/parabuild/build/changes.htm?buildrunid=1", "Summary");
    final WebResponse webResponse = TestHelper.assertPageSmokes("/parabuild/build/changes.htm?buildrunid=1", "Changes");

    // response has "show files" cookie
    final String newCookieValue = webResponse.getNewCookieValue(BuildRunChangesPage.COOKIE_SHOW_FILES);
    assertNotNull(newCookieValue);
    assertTrue(newCookieValue.equals(Boolean.TRUE.toString()) || newCookieValue.equals(Boolean.FALSE.toString()));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBuildRunChangesPage.class);
  }
}
