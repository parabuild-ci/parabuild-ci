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
package org.parabuild.ci.versioncontrol.git;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.object.ChangeList;

import java.io.File;
import java.util.List;

/**
 * SATestGitTextChangeLogParser
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 13, 2010 5:07:50 PM
 */
public final class SATestGitTextChangeLogParser extends TestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SATestGitTextChangeLogParser.class); // NOPMD

  private GitTextChangeLogParser parser;


  public SATestGitTextChangeLogParser(final String name) {
    super(name);
  }


  public void testParseChangeLog() throws Exception {
    final File testGitLog = TestHelper.getTestFile("test_git_log.txt");
    final List list = parser.parseChangeLog(testGitLog);

    // Basic asserts
    assertEquals(7, list.size());
    for (int i = 0; i < list.size(); i++) {
      final ChangeList changeList = (ChangeList) list.get(i);
      assertEquals("unknown", changeList.getUser());
      assertEquals("vimeshev@.(none)", changeList.getEmail());
    }

    // Check if hash is parsed OK
    final ChangeList changeList6 = (ChangeList) list.get(6);
    assertEquals("76606c2", changeList6.getNumber());

    // Check if message is parsed OK
    assertEquals("Added first files to git repository", changeList6.getDescription());
  }


  public void testParseChangeLogWithUseUserEmailAsUserName() throws Exception {

    final File testGitLog = TestHelper.getTestFile("test_git_log.txt");

    parser.setUseUserEmailAsUserName(true);
    final List list = parser.parseChangeLog(testGitLog);

    // Basic asserts
    assertEquals(7, list.size());
    for (int i = 0; i < list.size(); i++) {
      final ChangeList changeList = (ChangeList) list.get(i);
      assertEquals("vimeshev@.(none)", changeList.getUser());
      assertEquals("vimeshev@.(none)", changeList.getEmail());
    }

    // Check if hash is parsed OK
    final ChangeList changeList6 = (ChangeList) list.get(6);
    assertEquals("76606c2", changeList6.getNumber());

    // Check if message is parsed OK
    assertEquals("Added first files to git repository", changeList6.getDescription());
  }


  public void testToString() throws Exception {
    assertNotNull(parser.toString());
  }


  protected void setUp() throws Exception {
    super.setUp();
    parser = new GitTextChangeLogParser();
  }


  public String toString() {
    return "SATestGitTextChangeLogParser{" +
            "parser=" + parser +
            "} " + super.toString();
  }
}
