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
 * Tests VSSChangeLogParser
 */
public class SATestVSSChangeLogParser extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestVSSChangeLogParser.class);

  private static final String TEST_BRANCH_NAME = "test_branch_name";
  private static final int TEST_MAX_CHANGE_LIST_SIZE = 2;

  private static final File TEST_VSS_HISTORY_TXT = new File(TestHelper.getTestDataDir(), "test_vss_history.txt");
  private static final File TEST_VSS_HISTORY_NO_LABEL_TXT = new File(TestHelper.getTestDataDir(), "test_vss_history_no_label.txt");

  private VSSChangeLogParser changeLogParser = null;


  public void test_parseChangeLog() throws Exception {
    final List result = changeLogParser.parseChangeLog(TEST_VSS_HISTORY_TXT);
    int nonBlankVersionCount = 0;
    assertTrue(!result.isEmpty());
    for (int i = 0; i < result.size(); i++) {
      final ChangeList changeList = (ChangeList)result.get(i);
      assertTrue(!changeList.getDescription().startsWith("Comment:"));
      for (Iterator iter = changeList.getChanges().iterator(); iter.hasNext();) {
        final Change change = (Change)iter.next();
        if (change.getRevision().length() > 0) {
          assertTrue(change.getRevision() + " is not a valid integer",
            StringUtils.isValidInteger(change.getRevision()));
          nonBlankVersionCount++;
        }
      }
    }
    assertEquals(440, result.size());
    assertTrue(nonBlankVersionCount > 0);
  }


  public void test_parseChangeLogNoLabels() throws Exception {
    final List result = changeLogParser.parseChangeLog(TEST_VSS_HISTORY_TXT);
    assertTrue(!result.isEmpty());
    assertEquals(440, result.size());
  }


  /**
   * Makes sure changes reported for main branch doesn't contain
   * branching-out activities.
   */
  public void test_parseChangeLogWithChangesInABranch() throws Exception {
    // REVIEWME: simeshev@parabuilci.org -> implement
  }


  public void test_setProjectBranch() throws Exception {
    changeLogParser.setProjectBranch(TEST_BRANCH_NAME);
    final List result = changeLogParser.parseChangeLog(TEST_VSS_HISTORY_TXT);
    assertTrue(!result.isEmpty());
    final ChangeList chl = (ChangeList)result.get(0);
    assertEquals(TEST_BRANCH_NAME, chl.getBranch());
  }


  public void test_setProjectPath() throws Exception {
    final String testProjectPath = "$/test";
    changeLogParser.setProjectPath(testProjectPath);
    final List result = changeLogParser.parseChangeLog(TEST_VSS_HISTORY_TXT);
    for (Iterator i = result.iterator(); i.hasNext();) {
      final ChangeList changeList = (ChangeList)i.next();
      for (Iterator j = changeList.getChanges().iterator(); j.hasNext();) {
        final Change change = (Change)j.next();
//        if (log.isDebugEnabled()) log.debug("change.getFilePath() = " + change.getFilePath());
        //assertTrue("File path should start with $/test, but it was " + change.getFilePath(),
        //  change.getFilePath().startsWith(testProjectPath));
      }
    }

    // REVIEWME: simeshev@parabuilci.org -> for DocsListModelTest.java path is $/test\models\DocsListModelTest.java ???
    // should be $/Docs/Docs3.01/java/src/com/qiva/docs/models/DocsListModelTest.java ???
  }


  public void test_parseLogTakesMaxChangeListSizeInAccount() throws Exception {
    // parse
    final List result = changeLogParser.parseChangeLog(TEST_VSS_HISTORY_TXT);
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
  }


  /**
   * null\null problem for file names.
   */
  public void test_null_null() {
    //[java] [HttpProcessor[8080][5]] 17:53:42,153 DEBUG: stVSSChangeLogParser( 73) - change.getFilePath() = $/test\1483280007\$images
    //[java] [HttpProcessor[8080][5]] 17:53:42,153 DEBUG: stVSSChangeLogParser( 73) - change.getFilePath() = $/test\images\cisco.gif
    //[java] [HttpProcessor[8080][5]] 17:53:42,153 DEBUG: stVSSChangeLogParser( 73) - change.getFilePath() = null\null
    //[java] [HttpProcessor[8080][5]] 17:53:42,169 DEBUG: stVSSChangeLogParser( 73) - change.getFilePath() = $/test\images\cisco.gif renamed to Logo.gif
    //[java] [HttpProcessor[8080][5]] 17:53:42,169 DEBUG: stVSSChangeLogParser( 73) - change.getFilePath() = $/test\1473126007\$images
    //[java] [HttpProcessor[8080][5]] 17:53:42,169 DEBUG: stVSSChangeLogParser( 73) - change.getFilePath() = $/test\5550000007\$images

    // REVIEWME: simeshev@parabuilci.org -> implement
  }


  public SATestVSSChangeLogParser(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    changeLogParser = new VSSChangeLogParser(Locale.US, Integer.MAX_VALUE, TEST_MAX_CHANGE_LIST_SIZE);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestVSSChangeLogParser.class, new String[]{
    });
  }
}
