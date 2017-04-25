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
 * Tests CVSChangeLogParser
 */
public class SATestCVSChangeLogParser extends TestCase {

  private static final Log log = LogFactory.getLog(SATestCVSChangeLogParser.class);

  private static final String TEST_BRANCH_NAME = "test_branch_name";

  private CVSChangeLogParser changeLogParser = null;
  private static final int TEST_MAX_CHANGE_LIST_SIZE = 10;


  public void test_parseChangeLog() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      changeLogInputStream = makeTestChangeLogInputStream();
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);

//      System.out.println("result start ================================================================================");
//      Collections.sort(result, ChangeList.CHANGE_DATE_COMPARATOR);
//      for (int i = 0; i < result.size(); i++) {
//        ChangeList changeList = (ChangeList)result.get(i);
//        System.out.println("DEBUG: changeList at: " + changeList.getCreatedAt() + ": " + changeList);
//      }
//      System.out.println("result end   ================================================================================");
      assertTrue(!result.isEmpty());
      assertEquals(59, result.size());
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  /**
   * Validates that can parsw new log format.
   */
  public void test_parseChangeLogBug634() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      changeLogInputStream = new FileInputStream(new File(TestHelper.getTestDataDir(), "test_cvs_change_log_gt_20030801_with_1_12_9_date_format.txt"));
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
//      Collections.sort(result, ChangeList.CHANGE_DATE_COMPARATOR);
//      for (int i = 0; i < result.size(); i++) {
//        ChangeList changeList = (ChangeList)result.get(i);
//        System.out.println("DEBUG: changeList at: " + changeList.getCreatedAt() + ": " + changeList);
//      }
      assertEquals(59, result.size());
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseChangeLogWithExcludes() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      changeLogInputStream = makeTestChangeLogInputStream();
      final Map excludes = new HashMap();
      excludes.put(new Integer("/opt/cvs/cvsroot/bt/src/com/viewtier/parabuild/build/Build.java,v".hashCode()), Boolean.TRUE);
      changeLogParser.setRCSNamesHashesToExclude(excludes);
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      assertTrue(!result.isEmpty());
      assertEquals(58, result.size());
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  /**
   * Makes sure changes reported for main branch doesn't contain
   * branching-out activities.
   */
  public void test_parseChangeLogWithChangesInABranch() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      changeLogInputStream = new FileInputStream(new File(TestHelper.getTestDataDir(), "test_cvs_change_log_with_outside_branch.txt"));
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      assertTrue(!result.isEmpty());
      assertEquals(11, result.size());
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  /**
   * Tests that we can find chnages produced by cvsnt 2.5.03 (see bug #809).
   */
  public void test_bug809ParseChangeLog() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      changeLogInputStream = new FileInputStream(new File(TestHelper.getTestDataDir(), "test_cvsnt_2503_log.txt"));
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      assertTrue(!result.isEmpty());
      assertEquals(7, result.size());
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  /**
   * Tests parsing IDV's change log.
   */
  public void test_parseIDVChangeLog() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      changeLogInputStream = new FileInputStream(new File(TestHelper.getTestDataDir(), "test_idv_cvs_log.txt"));
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      assertTrue(!result.isEmpty());
      assertEquals(4, result.size());
      if (log.isDebugEnabled()) log.debug("result: " + result);
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_setBranchName() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      changeLogParser.setBranchName(TEST_BRANCH_NAME);
      changeLogInputStream = makeTestChangeLogInputStream();
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      assertTrue(!result.isEmpty());
      final ChangeList chl = (ChangeList)result.get(0);
      assertEquals(TEST_BRANCH_NAME, chl.getBranch());
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_setRepositoryPath() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      changeLogInputStream = makeTestChangeLogInputStream();
      changeLogParser.setRepositoryPath("bt");
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      for (Iterator i = result.iterator(); i.hasNext();) {
        final ChangeList changeList = (ChangeList)i.next();
        for (Iterator j = changeList.getChanges().iterator(); j.hasNext();) {
          final Change change = (Change)j.next();
          assertTrue(!change.getFilePath().startsWith("bt/"));
        }
      }
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_canLimitSize() throws Exception {
    final int maxChangeLogs = 20;
    changeLogParser = new CVSChangeLogParser(maxChangeLogs, 10);
    InputStream changeLogInputStream = null;
    try {
      changeLogInputStream = makeTestChangeLogInputStream();
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      assertEquals(maxChangeLogs, result.size());
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
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
    return new FileInputStream(new File(TestHelper.getTestDataDir(), "test_cvs_change_log_gt_20030801.txt"));
  }


  public SATestCVSChangeLogParser(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    changeLogParser = new CVSChangeLogParser(Integer.MAX_VALUE, TEST_MAX_CHANGE_LIST_SIZE);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestCVSChangeLogParser.class, new String[]{
      "test_bug809ParseChangeLog",
      "test_parseChangeLogWithChangesInABranch"
    });
  }
}
