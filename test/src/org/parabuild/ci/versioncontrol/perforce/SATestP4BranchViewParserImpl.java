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

import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

import org.parabuild.ci.TestHelper;

/**
 */
public class SATestP4BranchViewParserImpl extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestP4BranchViewParserImpl.class);

  private static final File P4_BRANCH_OUTPUT_FILE = new File(TestHelper.getTestDataDir(), "test_p4_branch.txt");
  private static final File P4_BRANCH_MULTILINE_OUTPUT_FILE = new File(TestHelper.getTestDataDir(), "test_p4_branch_multiline.txt");

  private P4BranchViewParserImpl changeLogParser = null;


  public void test_pars() throws Exception {
    final P4BranchView view = changeLogParser.parse(P4_BRANCH_OUTPUT_FILE);
    if (log.isDebugEnabled()) log.debug("view: " + view);
    assertNotNull(view);
    assertEquals("bt31", view.branch());
    assertEquals("2007/07/05 23:19:10", view.update());
    assertEquals("2007/07/06 00:53:32", view.access());
    assertEquals("vimeshev", view.owner());
    assertEquals("Created by vimeshev.", view.description());
    assertEquals("locked direct", view.options());
    assertEquals("//depot/dev/bt/... //depot/dev/bt31/...", view.view());
  }


  public void test_parseMultiline() throws Exception {
    final P4BranchView view = changeLogParser.parse(P4_BRANCH_MULTILINE_OUTPUT_FILE);
    if (log.isDebugEnabled()) log.debug("view: " + view);
    assertNotNull(view);
    assertEquals("bt31", view.branch());
    assertEquals("2007/07/05 23:19:10", view.update());
    assertEquals("2007/07/06 00:53:32", view.access());
    assertEquals("vimeshev", view.owner());
    assertEquals("Created by vimeshev.", view.description());
    assertEquals("locked direct", view.options());
    assertEquals("//depot/dev/bt/... //depot/dev/bt31/...\n-//depot/dev/bt/blah/... //depot/dev/bt31/blah/...", view.view());
  }


  protected void setUp() throws Exception {
    super.setUp();
    changeLogParser = new P4BranchViewParserImpl();
  }


  public SATestP4BranchViewParserImpl(final String s) {
    super(s);
  }
}
