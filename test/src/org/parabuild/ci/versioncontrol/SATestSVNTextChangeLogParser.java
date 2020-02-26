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
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Tests SVNChangeLogParser
 *
 * @noinspection ProhibitedExceptionDeclared
 */
public class SATestSVNTextChangeLogParser extends TestCase {

  private static final Log log = LogFactory.getLog(SATestSVNTextChangeLogParser.class);

  private SVNChangeLogParser changeLogParser = null;
  private static final int TEST_MAX_CHANGE_LIST_SIZE = 10;


  public void test_changeLogDateFormatter() throws Exception {
    final String testDate = "2005-03-03 12:43:32 -0800";
    final Date result = SVNTextChangeLogParser.getSvnChangeLogDateFormatter().parse(testDate);
    if (log.isDebugEnabled()) {
      log.debug("date = " + result);
    }
    final String target = "Thu Mar 03 12:43:32 PST 2005";
    assertEquals(target, result.toString());
    assertEquals(testDate, SVNTextChangeLogParser.getSvnChangeLogDateFormatter().format(result));
  }


  public void test_parseChangeLog() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      // parse
      changeLogInputStream = makeTestChangeLogInputStream();
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      if (log.isDebugEnabled()) {
        log.debug("result.size() = " + result.size());
      }
      assertTrue(!result.isEmpty());
      assertEquals(10359, result.size());

      // validate change lists are present
      VersionControlTestUtil.assertChangeListExistsAndValid(result, "13251", "sunny256", SVNTextChangeLogParser.getSvnChangeLogDateFormatter(), "2005-03-03 14:40:21 -0800 (Thu, 03 Mar 2005)", 1);
      VersionControlTestUtil.assertChangeListExistsAndValid(result, "13247", "sussman", SVNTextChangeLogParser.getSvnChangeLogDateFormatter(), "2005-03-03 12:43:32 -0800 (Thu, 03 Mar 2005)", 10);
      VersionControlTestUtil.assertChangeListExistsAndValid(result, "1", "svn", SVNTextChangeLogParser.getSvnChangeLogDateFormatter(), "2001-08-30 21:24:14 -0700 (Thu, 30 Aug 2001)", 10);
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseChangeLogBug1381() throws Exception {
    doTestBug1381("/trunk/contrib");
    doTestBug1381("/trunk/contrib/");
  }


  private void doTestBug1381(final String parent) throws IOException {
    InputStream changeLogInputStream = null;
    try {
      // parse
      changeLogParser.ignoreSubSubdirectory(parent);
      changeLogInputStream = makeTestChangeLogInputStream();
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      if (log.isDebugEnabled()) {
        log.debug("result.size() = " + result.size());
      }
      assertTrue(!result.isEmpty());
      assertEquals(10238, result.size());

      // validate change lists are present
      boolean found = false;
      for (int i = 0; i < result.size(); i++) {
        final Set changes = ((ChangeList) result.get(i)).getChanges();
        for (final Iterator iter = changes.iterator(); iter.hasNext();) {
          final Change change = (Change) iter.next();
          if ("/trunk/contrib/hook-scripts".equals(change.getFilePath())) {
            found = true;
            break;
          }
        }
      }
      assertTrue("A path that should have been present is missing", found);
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseChangeLogUnlimitedChangeListSize() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      // parse
      changeLogInputStream = makeTestChangeLogInputStream();
      final SVNChangeLogParser unlimitedChangeListSizeParser = new SVNTextChangeLogParser(Integer.MAX_VALUE);
      final List result = unlimitedChangeListSizeParser.parseChangeLog(changeLogInputStream);
      VersionControlTestUtil.assertChangeListExistsAndValid(result, "13247", "sussman", SVNTextChangeLogParser.getSvnChangeLogDateFormatter(), "2005-03-03 12:43:32 -0800 (Thu, 03 Mar 2005)", 12);
      VersionControlTestUtil.assertChangeListExistsAndValid(result, "1", "svn", SVNTextChangeLogParser.getSvnChangeLogDateFormatter(), "2001-08-30 21:24:14 -0700 (Thu, 30 Aug 2001)", 581);
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseNoEntriesChangeLog() throws Exception {
    final List result = changeLogParser.parseChangeLog(TestHelper.getTestFile("test_svn_no_entries_change_log.txt"));
    assertEquals(0, result.size());
  }


  /**
   * Calling for empty file should throw an exception.
   *
   * @throws Exception in an error occurs while running this test.
   */
  public void test_parseEmptyChangeLog() throws Exception {
    try {
      changeLogParser.parseChangeLog(TestHelper.getTestFile("test_svn_empty_change_log.txt"));
      TestHelper.failNoExceptionThrown();
    } catch (IOException e) {
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


  private static FileInputStream makeTestChangeLogInputStream() throws FileNotFoundException {
    return new FileInputStream(new File(TestHelper.getTestDataDir(), "test_svn_change_log.txt"));
  }


  public SATestSVNTextChangeLogParser(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    changeLogParser = new SVNTextChangeLogParser(TEST_MAX_CHANGE_LIST_SIZE);
  }


  /**
   * Required by JUnit
   *
   * @return this TestSuite.
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestSVNTextChangeLogParser.class, new String[]{
            "test_parseChangeLogBug1381"
    });
  }
}
