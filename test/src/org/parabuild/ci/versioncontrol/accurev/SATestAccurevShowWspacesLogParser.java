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
package org.parabuild.ci.versioncontrol.accurev;

import junit.framework.TestCase;
import org.parabuild.ci.TestHelper;

import java.util.List;
import java.net.MalformedURLException;

import org.dom4j.DocumentException;

/**
 * AccurevShowWspacesLogParser Tester.
 *
 * @author simeshev@cacheonix.com
 * @version 1.0
 * @since <pre>02/13/2009</pre>
 */
public final class SATestAccurevShowWspacesLogParser extends TestCase {

  private AccurevShowWspacesLogParser accurevShowWspacesLogParser = null;
  private static final String TEST_ACCUREV_SHOW_WSPACES_TXT = "test_accurev_show_wspaces.txt";


  public SATestAccurevShowWspacesLogParser(String s) {
    super(s);
  }


  public void testParse() throws MalformedURLException, DocumentException {
    final List list = accurevShowWspacesLogParser.parseLog(TestHelper.getTestFile(TEST_ACCUREV_SHOW_WSPACES_TXT));
    assertTrue(!list.isEmpty());
  }


  public void testToString() {
    assertNotNull(accurevShowWspacesLogParser.toString());
  }


  protected void setUp() throws Exception {
    super.setUp();
    accurevShowWspacesLogParser = new AccurevShowWspacesLogParser();
  }
}
