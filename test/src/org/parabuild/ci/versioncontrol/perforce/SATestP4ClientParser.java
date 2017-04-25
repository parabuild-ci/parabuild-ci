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
package org.parabuild.ci.versioncontrol.perforce;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.parabuild.ci.TestHelper;

import java.io.IOException;

/**
 * SATestP4ClientParser
 * <p/>
 *
 * @author Slava Imeshev
 * @since Nov 5, 2009 9:52:39 PM
 */
public final class SATestP4ClientParser extends TestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(SATestP4ClientParser.class); // NOPMD
  private P4ClientParser parser;


  public SATestP4ClientParser(final String s) {
    super(s);
  }


  public void testParse() throws IOException {
    final P4Client client = parser.parse(TestHelper.getTestFile("test_p4_client.txt"));
    assertEquals("//depot/... //vimeshev/...\n" +
            "//test/... //vimeshev/test_depot/...\n" +
            "-//depot/website/current/... //vimeshev/website/current/...\n" +
            "-//depot/dev/bt_20_rerunable_branch/... //vimeshev/dev/bt_20_rerunable_branch/...\n" +
            "-//depot/dev/perforce/... //vimeshev/dev/perforce/...", client.getViewLines());
  }


  public void setUp() {
    parser = new P4ClientParser();
  }
}
