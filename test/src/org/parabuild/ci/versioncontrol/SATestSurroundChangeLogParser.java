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
import org.parabuild.ci.util.*;
import org.parabuild.ci.object.*;


/**
 * Tests SurroundChangeLogParser
 */
public final class SATestSurroundChangeLogParser extends TestCase {

  private static final Log log = LogFactory.getLog(SATestSurroundChangeLogParser.class);

  private static final String TEST_SURROUND_HR = "test_surround_hr.txt";
  private static final String TEST_SURROUND_HR_AU_DATES = "test_surround_hr_au_dates.txt";

  private SurroundChangeLogParser parser = null;
  private static final Locale LOCALE_AU = new Locale("En", "AU");
  private static final int TEST_MAX_CHANGE_LIST_SIZE = 2;


  public void test_changeLogDateFormatter() throws Exception {
    final SurroundDateFormat format = new SurroundDateFormat(Locale.US);

    final Date result = format.parse("7/26/2005", "2:46 PM");
    if (log.isDebugEnabled()) log.debug("date = " + result);
    final Calendar testCalendar = Calendar.getInstance();
    testCalendar.clear();
    testCalendar.set(Calendar.YEAR, 2005);
    testCalendar.set(Calendar.MONTH, 6);
    testCalendar.set(Calendar.DATE, 26);
    testCalendar.set(Calendar.HOUR_OF_DAY, 14);
    testCalendar.set(Calendar.MINUTE, 46);
    final Date time = testCalendar.getTime();
    assertEquals(time, result);
    if (log.isDebugEnabled()) log.debug("time: " + time);
  }


  public void test_changeLogDateFormatterAU() throws Exception {
    final SurroundDateFormat format = new SurroundDateFormat(LOCALE_AU);
    final Date result = format.parse("1/10/2005", "1:41 PM");
    if (log.isDebugEnabled()) log.debug("date = " + result);
    final Calendar testCalendar = Calendar.getInstance();
    testCalendar.clear();
    testCalendar.set(Calendar.YEAR, 2005);
    testCalendar.set(Calendar.MONTH, 9);
    testCalendar.set(Calendar.DATE, 1);
    testCalendar.set(Calendar.HOUR_OF_DAY, 13);
    testCalendar.set(Calendar.MINUTE, 41);
    final Date time = testCalendar.getTime();
    if (log.isDebugEnabled()) log.debug("time: " + time);
    assertEquals(time, result);
  }


  public void test_parseChangeLog() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      // parse
      changeLogInputStream = makeTestChangeLogInputStream();
      final List result = parser.parseChangeLog(changeLogInputStream);
      if (log.isDebugEnabled()) log.debug("result.size() = " + result.size());
//      assertEquals(11, result.size());
//      for (Iterator i = result.iterator(); i.hasNext();) {
//        ChangeList changeList = (ChangeList)i.next();
//        if (log.isDebugEnabled()) log.debug("==========: ");
//        if (log.isDebugEnabled()) log.debug("changeList: " + changeList);
//        if (log.isDebugEnabled()) log.debug("==========: ");
//      }
      // validate change lists are present
      //VersionControlTestUtil.assertChangeListExistsAndValid(result, null, "Administrator", "8/12/2005 4:35 PM", 1);
      //VersionControlTestUtil.assertChangeListExistsAndValid(result, null, "Administrator", "7/26/2005 4:11 PM", 1);
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_bug_733_parseChangeLogAUDateFormats() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      final SurroundChangeLogParser parserWithAUCountrty = new SurroundChangeLogParser(LOCALE_AU, Integer.MAX_VALUE, TEST_MAX_CHANGE_LIST_SIZE);
      // parse
      changeLogInputStream = new FileInputStream(new File(TestHelper.getTestDataDir(), TEST_SURROUND_HR_AU_DATES));
      final List result = parserWithAUCountrty.parseChangeLog(changeLogInputStream);
      assertEquals(2, result.size());
      assertEquals(((ChangeList)result.get(0)).getCreatedAt(), new SurroundDateFormat(LOCALE_AU).parse("31/08/2005", "8:54 PM"));
      for (Iterator i = result.iterator(); i.hasNext();) {
        final ChangeList changeList = (ChangeList)i.next();
        final Calendar createdAt = Calendar.getInstance();
        createdAt.setTime(changeList.getCreatedAt());
        assertTrue(createdAt.get(Calendar.YEAR) != 2007); // see #733 - cst. complained about wrong dates.
      }
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseNoEntriesChangeLog() throws Exception {
    final List result = parser.parseChangeLog(TestHelper.getTestFile("test_surround_hr_no_entries.txt"));
    assertEquals(0, result.size());
  }


  /**
   * Calling for empty file should throw an exception.
   */
  public void test_parseEmptyChangeLog() throws Exception {
    try {
      parser.parseChangeLog(TestHelper.getTestFile("test_svn_empty_change_log.txt"));
      TestHelper.failNoExceptionThrown();
    } catch (IOException e) {
    }
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
    return new FileInputStream(new File(TestHelper.getTestDataDir(), TEST_SURROUND_HR));
  }


  public SATestSurroundChangeLogParser(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    parser = new SurroundChangeLogParser(Locale.US, Integer.MAX_VALUE, TEST_MAX_CHANGE_LIST_SIZE);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestSurroundChangeLogParser.class, new String[]{
      "test_changeLogDateFormatter",
      "test_changeLogDateFormatterAU"
    });
  }
}
