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
package org.parabuild.ci.versioncontrol;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.object.ChangeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * Tests ClearCaseChangeLogParser
 */
public final class SATestClearCaseChangeLogParser extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestClearCaseChangeLogParser.class);

  private ClearCaseChangeLogParser changeLogParser = null;

  private static final String TEST_BRANCH_NAME = "test_branch_name";
  private static final int TEST_MAX_CHANGE_LIST_SIZE = 10;


  public void test_changeLogDateFormatter() throws Exception {
    final String testDate = "20050906.210812";
    final SimpleDateFormat format = new SimpleDateFormat(ClearCaseChangeLogParser.DATE_PATTERN);
    final Date result = format.parse(testDate);
    assertEquals(testDate, format.format(result));
  }


  public void test_parseChangeLog() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      // parse
      changeLogInputStream = makeTestChangeLogInputStream();
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      assertEquals(10, result.size());

      // validate change lists are present
//  NOTE: simeshev@parabuildci.org ->
//      VersionControlTestUtil.assertChangeListExistsAndValid(result, null, "Administrator", "8/12/2005 4:35 PM", 1);
//      VersionControlTestUtil.assertChangeListExistsAndValid(result, null, "Administrator", "7/26/2005 4:11 PM", 1);
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  /**
   * Calling for empty file should throw an exception.
   */
  public void test_parseEmptyChangeLog() throws Exception {
// See #787 - it is possible that there are no changes in change lists
//    try {
//      parser.parseChangeLog(TestHelper.getTestFile("test_clearcase_lshistory_empty.txt"));
//      TestHelper.failNoExceptionThrown();
//    } catch (IOException e) {
//    }
  }

  public void test_parseLogTakesMaxChangeListSizeInAccount() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      // parse
      changeLogInputStream = makeTestChangeLogInputStream();
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      boolean truncatedPresent = false;
      for (final Iterator iter = result.iterator(); iter.hasNext();) {
        final ChangeList changeList = (ChangeList)iter.next();
        assertTrue(changeList.getChanges().size() <= TEST_MAX_CHANGE_LIST_SIZE);
        if (changeList.getChanges().size() < changeList.getOriginalSize()) {
          assertTrue(changeList.isTruncated());
          truncatedPresent = true;
        }
      }
      assertTrue("Truncated change lists should be present", truncatedPresent);
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }

  private FileInputStream makeTestChangeLogInputStream() throws FileNotFoundException {
    return new FileInputStream(new File(TestHelper.getTestDataDir(), "test_clearcase_lshistory.txt"));
  }


  public SATestClearCaseChangeLogParser(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    changeLogParser = new ClearCaseChangeLogParser(Integer.MAX_VALUE, TEST_BRANCH_NAME, TEST_MAX_CHANGE_LIST_SIZE);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestClearCaseChangeLogParser.class, new String[]{
    });
  }
}
