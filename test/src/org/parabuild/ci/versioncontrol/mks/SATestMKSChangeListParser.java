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
package org.parabuild.ci.versioncontrol.mks;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Tests MKSChangeListParser
 */
public final class SATestMKSChangeListParser extends TestCase {

  private static final Log log = LogFactory.getLog(SATestMKSChangeListParser.class);

  private MKSChangeListParser changeLogParser = null;
  private static final String TEST_REPOSITORY = "D:\\mor2\\dev\\bt\\test\\data\\MKS";
  private static final String TEST_BRANCH_1 = "test_branhch_1";
  private static final String STRING_SOURCE_LINE_ONE = "test_project/sourceline/alwaysvalid";
  private static final String TEST_MKS_RLOG_TXT = "test_mks_rlog.txt";
  private static final String TEST_MKS_RLOG_JCI_TXT = "test_mks_rlog_jci.txt";
  private static final Date EXPECTED_PM_DATE = TestHelper.makeDate(2006, 3, 29, 18, 29, 0);
  private static final int TEST_MAX_CHANGE_LIST_SIZE = 2;


  public void test_parseChangeLog() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      // parse
      final InputStream changeLogInputStream1;
      changeLogInputStream1 = new FileInputStream(new File(TestHelper.getTestDataDir(), TEST_MKS_RLOG_TXT));
      changeLogInputStream = changeLogInputStream1;
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      if (log.isDebugEnabled()) log.debug("result.size() = " + result.size());
      assertTrue(!result.isEmpty());
      assertEquals(3, result.size());
      boolean pmDateFound = false;
      for (int i = 0; i < result.size(); i++) {
        final ChangeList changeList = (ChangeList) result.get(i);
        assertTrue(changeList.getDescription().trim().length() > 0);
        assertTrue(!changeList.getDescription().startsWith("change package:"));
        assertTrue(!changeList.getDescription().startsWith("Member added to project"));
        assertTrue(!changeList.getDescription().startsWith("Initial revision"));
        if (changeList.getCreatedAt().equals(EXPECTED_PM_DATE)) pmDateFound = true;
        for (Iterator iter = changeList.getChanges().iterator(); iter.hasNext();) {
          final Change change = (Change) iter.next();
          assertTrue(!change.getFilePath().startsWith(TEST_REPOSITORY));
          assertTrue(!change.getFilePath().startsWith("======"));
          assertTrue(!(change.getFilePath().indexOf('\\') >= 0));
          if (change.getFilePath().equals("test_cvs_change_log_with_outside_branch.txt")) {
            assertEquals(change.getChangeType(), Change.TYPE_ADDED);
          }
        }
        log.debug("changeList.getDescription(): " + changeList.getDescription());
        log.debug("changeList.getCreatedAt(): " + changeList.getCreatedAt());
        log.debug("changeList.getUser(): " + changeList.getUser());
      }
      assertTrue("PM date shoulbe be found", pmDateFound);
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseJciChangeLog() throws Exception {
    InputStream is = null;
    try {
      final MKSChangeListParser clp = new MKSChangeListParser(TEST_REPOSITORY, STRING_SOURCE_LINE_ONE,
              Integer.MAX_VALUE, "", null, TEST_MAX_CHANGE_LIST_SIZE, "MMM dd, yyyy hh:mm:ss a");
      // parse
      is = new FileInputStream(new File(TestHelper.getTestDataDir(), TEST_MKS_RLOG_JCI_TXT));
      final List result = clp.parseChangeLog(is);
      assertTrue(!result.isEmpty());
      assertEquals(914, result.size());
    } finally {
      IoUtils.closeHard(is);
    }
  }

// REVIEWME: simeshev@parabuilci.org -> uncomment when test branch/data is ready
//  public void test_parseBranchChangeLog() throws Exception {
//    final MKSChangeListParser branchParser = new MKSChangeListParser(Locale.US, TEST_REPOSITORY,
//      STRING_SOURCE_LINE_ONE, Integer.MAX_VALUE, TEST_BRANCH_1);
//    InputStream changeLogInputStream = null;
//    try {
//      // parse
//      changeLogInputStream = new FileInputStream(new File(TestHelper.getTestDataDir(), "test_mks_rlog_test_branch_1.txt"));
//      final List result = branchParser.parseChangeLog(changeLogInputStream);
//      if (log.isDebugEnabled()) log.debug("result.size() = " + result.size());
//      assertEquals(1, result.size());
//      for (int i = 0; i < result.size(); i++) {
//        final ChangeList changeList = (ChangeList)result.get(i);
//        assertTrue(!changeList.getDescription().startsWith("Description:"));
//        assertTrue(!changeList.getDescription().startsWith("Initial revision."));
//        assertEquals(TEST_BRANCH_1, changeList.getBranch());
//        final Set changes = changeList.getChanges();
//        assertEquals(2, changes.size());
//        for (Iterator iter = changes.iterator(); iter.hasNext();) {
//          final Change change = (Change)iter.next();
//          assertTrue(!change.getFilePath().startsWith(TEST_REPOSITORY));
//          assertTrue(!change.getFilePath().startsWith("\\archive"));
//          assertTrue(change.getRevision().startsWith("1.2.1"));
//        }
//        log.debug("changeList.getDescription(): " + changeList.getDescription());
//        log.debug("changeList.getCreatedAt(): " + changeList.getCreatedAt());
//        log.debug("changeList.getUser(): " + changeList.getUser());
//      }
//    } finally {
//      IoUtils.closeHard(changeLogInputStream);
//    }
//  }


  public void test_parseChangeLogCutsAtRightDate() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      final MKSChangeListParser cutDateChangeLogParser = new MKSChangeListParser(
              TEST_REPOSITORY, STRING_SOURCE_LINE_ONE, Integer.MAX_VALUE, "",
              TestHelper.makeDate(2006, 3, 11, 1, 33, 0), 10, MKSDateFormat.DEFAULT_OUTPUT_FORMAT); // Apr 11 01:32:00 PDT 2006 + 1 minute
      final InputStream changeLogInputStream1;
      changeLogInputStream1 = new FileInputStream(new File(TestHelper.getTestDataDir(), TEST_MKS_RLOG_TXT));
      changeLogInputStream = changeLogInputStream1;
      assertEquals(2, cutDateChangeLogParser.parseChangeLog(changeLogInputStream).size());
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseNoEntriesChangeLog() throws Exception {
    final List result = changeLogParser.parseChangeLog(TestHelper.getTestFile("test_mks_rlog_no_entries.txt"));
    assertEquals(0, result.size());
  }


  public void test_parseLogTakesMaxChangeListSizeInAccount() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      // parse
      final InputStream changeLogInputStream1;
      changeLogInputStream1 = new FileInputStream(new File(TestHelper.getTestDataDir(), TEST_MKS_RLOG_TXT));
      changeLogInputStream = changeLogInputStream1;
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      boolean truncatedPresent = false;
      for (final Iterator iter = result.iterator(); iter.hasNext();) {
        final ChangeList changeList = (ChangeList) iter.next();
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


  public SATestMKSChangeListParser(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    changeLogParser = new MKSChangeListParser(TEST_REPOSITORY, STRING_SOURCE_LINE_ONE, Integer.MAX_VALUE, "", null, TEST_MAX_CHANGE_LIST_SIZE, MKSDateFormat.DEFAULT_OUTPUT_FORMAT);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestMKSChangeListParser.class, new String[]{
            "test_parseChangeLogCutsAtRightDate",
    });
  }
}
