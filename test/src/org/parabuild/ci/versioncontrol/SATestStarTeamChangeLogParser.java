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

import java.io.*;
import java.util.*;
import org.apache.commons.logging.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.*;
import org.parabuild.ci.object.*;

/**
 * Tests StarTeamChangeLogParser
 */
public final class SATestStarTeamChangeLogParser extends TestCase {

  private static final Log log = LogFactory.getLog(SATestStarTeamChangeLogParser.class);
  private static final String TEST_STARTEAM_HIST = "test_starteam_hist.txt";
  private static final String TEST_WORKING_DIR = "D:\\projectory\\test_starteam";
  private static final String TEST_STARTEAM_HIST_NO_ENTRIES_TXT = "test_starteam_hist_no_entries.txt";

  private StarTeamChangeLogParser parser = null;
  private static final String SOURCELINE_ALWAYSVALID_SRC_README_TXT = "\\sourceline\\alwaysvalid\\src\\readme.txt";
  private static final String SECOND_SOURCELINE_SRC_README_TXT = "\\second_sourceline\\src\\readme.txt";
  private static final String TEST_STARTEAM_HIST_EMPTY_FOLDER_TXT = "test_starteam_hist_empty_folder.txt";
  private static final int TEST_MAX_CHANGE_LIST_SIZE = 2;


  public void test_changeLogDateFormatter() throws Exception {
    final StarTeamDateFormat format = new StarTeamDateFormat(Locale.US);
    final Date result = format.parseOutput("3/6/06 1:06:59 AM PST");
    final Calendar testCalendar = Calendar.getInstance();
    testCalendar.clear();
    testCalendar.set(Calendar.YEAR, 2006);
    testCalendar.set(Calendar.MONTH, 2);
    testCalendar.set(Calendar.DATE, 6);
    testCalendar.set(Calendar.HOUR_OF_DAY, 1);
    testCalendar.set(Calendar.MINUTE, 6);
    testCalendar.set(Calendar.SECOND, 59);
//    if (log.isDebugEnabled()) log.debug("time: " + testCalendar.getTime());
//    if (log.isDebugEnabled()) log.debug("date = " + result);
    assertEquals(testCalendar.getTime(), result);
  }


  public void test_parseChangeLog() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      boolean sourceLineAlwaysvalidSrcReadmeTxtFound = false;
      boolean secondSourceLineSrcReadmeTxtFound = false;
      // parse
      changeLogInputStream = makeTestChangeLogInputStream();
      final List result = parser.parseChangeLog(changeLogInputStream);
      if (log.isDebugEnabled()) log.debug("result.size() = " + result.size());
      assertEquals(4, result.size());
      for (final Iterator i = result.iterator(); i.hasNext();) {
        final ChangeList changeList = (ChangeList)i.next();
        //if (log.isDebugEnabled()) log.debug("==========: ");
        //if (log.isDebugEnabled()) log.debug("changeList: " + changeList);
        //if (log.isDebugEnabled()) log.debug("==========: ");
        for (final Iterator j = changeList.getChanges().iterator(); j.hasNext();) {
          final Change change = (Change)j.next();
          final String filePath = change.getFilePath();
          if (log.isDebugEnabled()) log.debug("filePath: " + filePath);
          assertTrue(!filePath.startsWith(TEST_WORKING_DIR));
          if (filePath.equals(SOURCELINE_ALWAYSVALID_SRC_README_TXT)) {
            sourceLineAlwaysvalidSrcReadmeTxtFound = true;
          }
          if (filePath.equals(SECOND_SOURCELINE_SRC_README_TXT)) {
            secondSourceLineSrcReadmeTxtFound = true;
          }
        }
      }
      assertTrue("sourceLineAlwaysvalidSrcReadmeTxtFound", sourceLineAlwaysvalidSrcReadmeTxtFound);
      assertTrue("secondSourceLineSrcReadmeTxtFound", secondSourceLineSrcReadmeTxtFound);

      // validate change lists are present
      //VersionControlTestUtil.assertChangeListExistsAndValid(result, null, "Administrator", "8/12/2005 4:35 PM", 1);
      //VersionControlTestUtil.assertChangeListExistsAndValid(result, null, "Administrator", "7/26/2005 4:11 PM", 1);
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseChangeLogCutsAtRightDate() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      final StarTeamChangeLogParser cutDateChangeLogParser = new StarTeamChangeLogParser(Locale.US, TEST_WORKING_DIR, Integer.MAX_VALUE, TestHelper.makeDate(2006, 2, 6, 0, 51, 24), TEST_MAX_CHANGE_LIST_SIZE);
      changeLogInputStream = makeTestChangeLogInputStream();
      assertEquals(2, cutDateChangeLogParser.parseChangeLog(changeLogInputStream).size());
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseNoEntriesChangeLog() throws Exception {
    final List result = parser.parseChangeLog(TestHelper.getTestFile(TEST_STARTEAM_HIST_NO_ENTRIES_TXT));
    assertEquals(0, result.size());
  }


  public void test_parseEmptyFolderChangeLog() throws Exception {
    final List result = parser.parseChangeLog(TestHelper.getTestFile(TEST_STARTEAM_HIST_EMPTY_FOLDER_TXT));
    assertEquals(0, result.size());
  }


  public void test_parseLogTakesMaxChangeListSizeInAccount() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      // parse
      changeLogInputStream = makeTestChangeLogInputStream();
      final List result = parser.parseChangeLog(changeLogInputStream);
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


  private InputStream makeTestChangeLogInputStream() throws FileNotFoundException {
    return new FileInputStream(new File(TestHelper.getTestDataDir(), TEST_STARTEAM_HIST));
  }


  public SATestStarTeamChangeLogParser(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    parser = new StarTeamChangeLogParser(Locale.US, TEST_WORKING_DIR, Integer.MAX_VALUE, TestHelper.makeDate(2000, 2, 6, 1, 6, 59), TEST_MAX_CHANGE_LIST_SIZE);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestStarTeamChangeLogParser.class, new String[]{
      "test_changeLogDateFormatter",
      "test_parseChangeLog",
      "test_parseNoEntriesChangeLog"
    });
  }
}
