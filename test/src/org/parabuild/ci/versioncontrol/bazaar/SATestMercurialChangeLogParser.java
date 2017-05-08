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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.versioncontrol.mercurial.MercurialChangeLogParser;

import java.io.File;
import java.util.List;

/**
 * Tester for MercurialChangeLogParser
 * <p/>
 *
 * @author Slava Imeshev
 */
public final class SATestMercurialChangeLogParser extends TestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SATestMercurialChangeLogParser.class); // NOPMD

  private MercurialChangeLogParser parser;


  public SATestMercurialChangeLogParser(final String name) {
    super(name);
  }


  public void testParseChangeLog() throws Exception {

    final File testBazaarLog = TestHelper.getTestFile("test-mercurial-log.txt");
    final List list = parser.parseChangeLog(testBazaarLog);

    // Basic asserts
    assertEquals(3, list.size());
    for (int i = 0; i < list.size(); i++) {
      final ChangeList changeList = (ChangeList) list.get(i);
      final String user = changeList.getUser();
      final String email = changeList.getEmail();
      assertTrue("User should be test_user or root but it was \"" + user + '\"', "test@parabuildci.org".equals(user) || "root@baybridge.cacheonix.com".equals(user));
      assertTrue("test@parabuildci.org".equals(email) || "root@baybridge.cacheonix.com".equals(email));
    }

    // Check if hash is parsed OK
    assertEquals("0", ((ChangeList) list.get(2)).getNumber());
    assertEquals("2", ((ChangeList) list.get(0)).getNumber());

    // Check if message is parsed OK
    assertEquals("Populated test repository. Hash: 398519a32d6a.", ((ChangeList) list.get(2)).getDescription());
  }


  public void testToString() throws Exception {
    assertNotNull(parser.toString());
  }


  protected void setUp() throws Exception {
    super.setUp();
    parser = new MercurialChangeLogParser();
  }


  public String toString() {
    return "SATestMercurialChangeLogParser{" +
            "parser=" + parser +
            "} " + super.toString();
  }
}