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
 * Tests PVCSChangeListParser
 */
public final class SATestPVCSChangeListParser extends TestCase {

  private static final Log log = LogFactory.getLog(SATestPVCSChangeListParser.class);

  private PVCSChangeListParser parser = null;
  private static final String TEST_REPOSITORY = "D:\\mor2\\dev\\bt\\test\\data\\pvcs";
  private static final String TEST_BRANCH_1 = "test_branhch_1";
  private static final String STRING_SOURCE_LINE_ONE = "test_project/sourceline/alwaysvalid";
  private static final int TEST_MAX_CHANGE_LIST_SIZE = 2;


  public void test_parseChangeLog() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      // parse
      changeLogInputStream = makeTestChangeLogInputStream();
      final List result = parser.parseChangeLog(changeLogInputStream);
      if (log.isDebugEnabled()) log.debug("result.size() = " + result.size());
      assertTrue(!result.isEmpty());
      assertEquals(3, result.size());
      for (int i = 0; i < result.size(); i++) {
        final ChangeList changeList = (ChangeList)result.get(i);
        assertTrue(!changeList.getDescription().startsWith("Description:"));
        assertTrue(!changeList.getDescription().startsWith("Initial revision."));
        for (Iterator iter = changeList.getChanges().iterator(); iter.hasNext();) {
          final Change change = (Change)iter.next();
          assertTrue(!change.getFilePath().startsWith(TEST_REPOSITORY));
          assertTrue(!change.getFilePath().startsWith("\\archive"));
          assertTrue(!(change.getFilePath().indexOf('\\') >= 0));
          if (change.getFilePath().equals("\\test_project\\test_cvs_change_log_with_outside_branch.txt")) {
            assertEquals(change.getChangeType(), Change.TYPE_ADDED);
          }
        }
        log.debug("changeList.getDescription(): " + changeList.getDescription());
        log.debug("changeList.getCreatedAt(): " + changeList.getCreatedAt());
        log.debug("changeList.getUser(): " + changeList.getUser());
      }
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  private InputStream makeTestChangeLogInputStream() throws FileNotFoundException {
    final InputStream changeLogInputStream;
    changeLogInputStream = new FileInputStream(new File(TestHelper.getTestDataDir(), "test_pvcs_vlog.txt"));
    return changeLogInputStream;
  }


  public void test_parseBranchChangeLog() throws Exception {
    final PVCSChangeListParser branchParser = new PVCSChangeListParser(Locale.US, TEST_REPOSITORY,
      STRING_SOURCE_LINE_ONE, Integer.MAX_VALUE, TEST_BRANCH_1, 10);
    InputStream changeLogInputStream = null;
    try {
      // parse
      changeLogInputStream = new FileInputStream(new File(TestHelper.getTestDataDir(), "test_pvcs_vlog_test_branch_1.txt"));
      final List result = branchParser.parseChangeLog(changeLogInputStream);
      if (log.isDebugEnabled()) log.debug("result.size() = " + result.size());
      assertEquals(1, result.size());
      for (int i = 0; i < result.size(); i++) {
        final ChangeList changeList = (ChangeList)result.get(i);
        assertTrue(!changeList.getDescription().startsWith("Description:"));
        assertTrue(!changeList.getDescription().startsWith("Initial revision."));
        assertEquals(TEST_BRANCH_1, changeList.getBranch());
        final Set changes = changeList.getChanges();
        assertEquals(2, changes.size());
        for (Iterator iter = changes.iterator(); iter.hasNext();) {
          final Change change = (Change)iter.next();
          assertTrue(!change.getFilePath().startsWith(TEST_REPOSITORY));
          assertTrue(!change.getFilePath().startsWith("\\archive"));
          assertTrue(change.getRevision().startsWith("1.2.1"));
        }
//        log.debug("changeList.getDescription(): " + changeList.getDescription());
//        log.debug("changeList.getCreatedAt(): " + changeList.getCreatedAt());
//        log.debug("changeList.getUser(): " + changeList.getUser());
      }
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseNoEntriesChangeLog() throws Exception {
    final List result = parser.parseChangeLog(TestHelper.getTestFile("test_pvcs_no_entries_vlog.txt"));
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


  public SATestPVCSChangeListParser(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    parser = new PVCSChangeListParser(Locale.US, TEST_REPOSITORY, STRING_SOURCE_LINE_ONE, Integer.MAX_VALUE, "", TEST_MAX_CHANGE_LIST_SIZE);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestPVCSChangeListParser.class, new String[]{
    });
  }
}
