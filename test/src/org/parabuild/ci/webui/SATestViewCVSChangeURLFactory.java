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


public final class SATestViewCVSChangeURLFactory extends TestCase {


  private static final String EXPECTED_FILE_URL_WITH_REVISION = "http://test_url_string/test/path?root=test_root_string&view=log&rev=1.2";
  private static final String EXPECTED_FILE_URL_WITHOUT_REVISION = "http://test_url_string/test/path?root=test_root_string";

  private static final String TEST_URL = "http://test_url_string";
  private static final String TEST_ROOT = "test_root_string";
  private static final String TEST_PATH = "test/path";
  private static final String TEST_REVISION = "1.2";

  private static final SimpleChange TEST_CHANGE = makeChange(TEST_REVISION);

  private ViewCVSChangeURLFactory urlFactory;
  private ViewCVSChangeURLFactory urlFactoryWORevision;
  private static final String EXPECTED_REVISION_URL = "http://test_url_string/test/path?root=test_root_string&r2=1.2&r1=1.1";


  public void test_makeChangeFileURL() {
    final ChangeURL fileURL = urlFactory.makeChangeFileURL(TEST_CHANGE);
    assertEquals(TEST_PATH, fileURL.getCaption());
    assertEquals(EXPECTED_FILE_URL_WITH_REVISION, fileURL.getURL());
  }


  public void test_makeChangeFileURLWORevision() {
    final ChangeURL fileURL = urlFactoryWORevision.makeChangeFileURL(TEST_CHANGE);
    assertEquals(TEST_PATH, fileURL.getCaption());
    assertEquals(EXPECTED_FILE_URL_WITHOUT_REVISION, fileURL.getURL());
  }


  public void test_makeChangeRevisionURL() {
    final ChangeURL revisionURL = urlFactory.makeChangeRevisionURL(TEST_CHANGE);
    assertEquals(TEST_REVISION, revisionURL.getCaption());
    assertEquals(EXPECTED_REVISION_URL, revisionURL.getURL());
  }


  public void test_makeChangeRevisionURLFallsBackForEmptyRevision() {
    final ChangeURL revisionURL = urlFactory.makeChangeRevisionURL(makeChange(""));
    assertEquals("http://test_url_string/test/path?root=test_root_string&view=log", revisionURL.getURL());
  }


  public void test_makeChangeRevisionURLFallsBackForDotOnlyRevision() {
    final ChangeURL revisionURL = urlFactory.makeChangeRevisionURL(makeChange("."));
    assertEquals("http://test_url_string/test/path?root=test_root_string&view=log&rev=.", revisionURL.getURL());
  }


  public void test_makeChangeRevisionURLFallsBackForFirstRevision() {
    final ChangeURL revisionURL = urlFactory.makeChangeRevisionURL(makeChange("1.1"));
    assertEquals("http://test_url_string/test/path?root=test_root_string&view=log&rev=1.1", revisionURL.getURL());
  }


  public void test_makeChangeListNumberURL() {
    final String number = "888";
    final ChangeList changeList = new ChangeList();
    changeList.setNumber(number);
    assertNull(urlFactory.makeChangeListNumberURL(changeList));
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
    this.urlFactory = new ViewCVSChangeURLFactory(TEST_URL, TEST_ROOT, true);
    this.urlFactoryWORevision = new ViewCVSChangeURLFactory(TEST_URL, TEST_ROOT, false);
  }


  public static TestSuite suite() {
    return new TestSuite(SATestViewCVSChangeURLFactory.class);
  }


  public SATestViewCVSChangeURLFactory(final String s) {
    super(s);
  }
}
