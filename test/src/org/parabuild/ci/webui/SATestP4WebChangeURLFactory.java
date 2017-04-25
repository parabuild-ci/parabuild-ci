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

import org.parabuild.ci.object.*;

public final class SATestP4WebChangeURLFactory extends TestCase {


  private static final String TEST_PATH = "//depot/test/path";
  private static final String HTTP_TEST_P4WEB = "http://test:99999";
  private static final String TEST_FILE_URL = "http://test:99999/@md=d&cd=//depot/test/path&ra=s&c=AQS@//depot/test/path?ac=22";
  private static final String TEST_REVISION_URL = "http://test:99999/@md=d&cd=//depot/test/path&cdf=//depot/test/path&ra=s&c=iyK@//depot/test/path?ac=19&rev1=1&rev2=2";


  private P4WebChangeURLFactory urlFactory;


  public void test_makeChangeFileURL() {
    assertEquals(TEST_PATH, urlFactory.makeChangeFileURL(makeChange("1")).getCaption());
    assertEquals(TEST_FILE_URL, urlFactory.makeChangeFileURL(makeChange("1")).getURL());
    assertEquals(TEST_PATH, urlFactory.makeChangeFileURL(makeChange("2")).getCaption());
    assertEquals(TEST_FILE_URL, urlFactory.makeChangeFileURL(makeChange("2")).getURL());
  }


  public void test_makeChangeRevisionURL() {
    assertEquals("1", urlFactory.makeChangeRevisionURL(makeChange("1")).getCaption());
    assertEquals(TEST_FILE_URL, urlFactory.makeChangeRevisionURL(makeChange("1")).getURL());

    assertEquals("2", urlFactory.makeChangeRevisionURL(makeChange("2")).getCaption());
    assertEquals(TEST_REVISION_URL, urlFactory.makeChangeRevisionURL(makeChange("2")).getURL());
  }


  public void test_makeChangeListNumberURL() {
    final String number = "888";
    final ChangeList changeList = new ChangeList();
    changeList.setNumber(number);
    final ChangeURL changeURL = urlFactory.makeChangeListNumberURL(changeList);
    assertEquals(number, changeURL.getCaption());
    assertEquals("http://test:99999/@md=d@/888?ac=10", changeURL.getURL());
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
    this.urlFactory = new P4WebChangeURLFactory(HTTP_TEST_P4WEB);
  }


  public static TestSuite suite() {
    return new TestSuite(SATestP4WebChangeURLFactory.class);
  }


  public SATestP4WebChangeURLFactory(final String s) {
    super(s);
  }
}
