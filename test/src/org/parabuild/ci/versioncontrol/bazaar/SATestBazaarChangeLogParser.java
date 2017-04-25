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
package org.parabuild.ci.versioncontrol.bazaar;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.object.ChangeList;

import java.io.File;
import java.util.List;

/**
 * Tester for BazaarChangeLogParser
 * <p/>
 *
 * @author Slava Imeshev
 */
public final class SATestBazaarChangeLogParser extends TestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(SATestBazaarChangeLogParser.class); // NOPMD

  private BazaarChangeLogParser parser;


  public SATestBazaarChangeLogParser(final String name) {
    super(name);
  }


  public void testParseChangeLog() throws Exception {
    
    final File testBazaarLog = TestHelper.getTestFile("test_bzr_log.txt ");
    final List list = parser.parseChangeLog(testBazaarLog);

    // Basic asserts
    assertEquals(6, list.size());
    for (int i = 0; i < list.size(); i++) {
      final ChangeList changeList = (ChangeList) list.get(i);
      final String user = changeList.getUser();
      final String email = changeList.getEmail();
      assertTrue("User should be test_user or root but it was \"" + user + '\"', "test@parabuildci.org".equals(user) || "root@baybridge.cacheonix.com".equals(user));
      assertTrue("test@parabuildci.org".equals(email) || "root@baybridge.cacheonix.com".equals(email));
    }

    // Check if hash is parsed OK
    assertEquals("1", ((ChangeList) list.get(5)).getNumber());
    assertEquals("6", ((ChangeList) list.get(0)).getNumber());

    // Check if message is parsed OK
    assertEquals("Added a line", ((ChangeList) list.get(4)).getDescription());
  }


  public void testParseChangeLogBug1523() throws Exception {

    final File testBazaarLog = TestHelper.getTestFile("test_bazaar_bug_1523.txt");
    final List list = parser.parseChangeLog(testBazaarLog);

    // Basic asserts
    assertEquals(2, list.size());
  }


  public void testToString() throws Exception {
    assertNotNull(parser.toString());
  }


  protected void setUp() throws Exception {
    super.setUp();
    parser = new BazaarChangeLogParser();
  }


  public String toString() {
    return "SATestBazaarChangeLogParser{" +
            "parser=" + parser +
            "} " + super.toString();
  }
}