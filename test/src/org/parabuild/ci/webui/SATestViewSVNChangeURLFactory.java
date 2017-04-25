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

import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.object.*;

public final class SATestViewSVNChangeURLFactory extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestViewSVNChangeURLFactory.class); // NOPMD

  private static final String TEST_URL = "http://svn.apache.org/viewvc";
  private static final String TEST_PATH = "/jakarta/poi/trunk/src/scratchpad/src/org/apache/poi/hwpf/model/FIBFieldHandler.java";
  private ViewSVNChangeURLFactory urlFactory;
  public static final String TEST_CHANGE_LIST_NUMBER = "480585";


  public void test_makeChangeListNumberURL() {
    final ChangeList changeList = new ChangeList();
    changeList.setNumber(TEST_CHANGE_LIST_NUMBER);
    final ChangeURL changeURL = urlFactory.makeChangeListNumberURL(changeList);
    assertEquals(changeURL.getURL(), "http://svn.apache.org/viewvc/?view=rev&revision=480585&diff_format=h");
    assertEquals(changeURL.getCaption(), TEST_CHANGE_LIST_NUMBER);
  }


  public void test_makemakeChangeFileURL() {
    final ChangeURL changeURL = urlFactory.makeChangeFileURL(makeChange(""));
    assertEquals(changeURL.getURL(), "http://svn.apache.org/viewvc/jakarta/poi/trunk/src/scratchpad/src/org/apache/poi/hwpf/model/FIBFieldHandler.java");
    assertEquals(changeURL.getCaption(), "/jakarta/poi/trunk/src/scratchpad/src/org/apache/poi/hwpf/model/FIBFieldHandler.java");
  }


  public void test_makeChangeRevisionURL() {
    // NOTE: vimeshev - 2006-12-01 - currently Parabuild does not store revision numbers for SVN
    final ChangeURL changeURL = urlFactory.makeChangeRevisionURL(makeChange(TEST_CHANGE_LIST_NUMBER));
    assertEquals(changeURL.getURL(), "http://svn.apache.org/viewvc/jakarta/poi/trunk/src/scratchpad/src/org/apache/poi/hwpf/model/FIBFieldHandler.java");
    assertEquals(changeURL.getCaption(), "");
  }


  /**
   * Factory method to create mock change.
   *
   * @param revision
   */
  private static SimpleChange makeChange(final String revision) {
    return new SimpleChange() {
      public String getRevision() {
        return revision;
      }


      public String getFilePath() {
        return TEST_PATH;
      }


      public byte getChangeType() {
        return 0;
      }


      public String getChangeTypeAsString() {
        return null;
      }
    };
  }


  protected void setUp() throws Exception {
    super.setUp();
    urlFactory = new ViewSVNChangeURLFactory(SATestViewSVNChangeURLFactory.TEST_URL);
  }


  public static TestSuite suite() {
    return new TestSuite(SATestViewSVNChangeURLFactory.class);
  }


  public SATestViewSVNChangeURLFactory(final String s) {
    super(s);
  }
}
