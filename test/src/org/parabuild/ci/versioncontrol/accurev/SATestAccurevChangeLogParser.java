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

import java.io.File;
import java.util.List;

/**
 * AccurevChangeLogParser Tester.
 *
 * @author simeshev@cacheonix.com
 * @version 1.0
 * @since <pre>02/13/2009</pre>
 */
public final class SATestAccurevChangeLogParser extends TestCase {

  private static final String TEST_ACCUREV_HIST_TXT = "test_accurev_hist.txt";
  private static final File TEST_FILE = TestHelper.getTestFile(TEST_ACCUREV_HIST_TXT);
  private AccurevChangeLogParser accurevChangeLogParser = null;


  public SATestAccurevChangeLogParser(String s) {
    super(s);
  }


  public void testSetMaxChangeLists() throws Exception {
    //TODO: Test goes here...
  }


  public void testParseChangeLog() throws Exception {
    final List list = accurevChangeLogParser.parseChangeLog(TEST_FILE);
    assertEquals(12, list.size());
  }


  public void testToString() {
    assertNotNull(accurevChangeLogParser.toString());
  }


  protected void setUp() throws Exception {
    super.setUp();
    accurevChangeLogParser = new AccurevChangeLogParser(9999);
    accurevChangeLogParser.setMaxChangeLists(999);
  }
}
