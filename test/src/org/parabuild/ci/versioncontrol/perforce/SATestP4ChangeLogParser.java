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
package org.parabuild.ci.versioncontrol.perforce;

import java.io.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.ChangeListIssueBinding;
import org.parabuild.ci.configuration.ChangeListsAndIssues;
import org.parabuild.ci.configuration.ChangeListsAndIssuesImpl;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.Issue;


/**
 * Tests Perforce change log parser
 */
public class SATestP4ChangeLogParser extends TestCase {

  private static final File FILE_TEST_P4_CHANGES_TXT = new File(TestHelper.getTestDataDir(), "test_p4_changes.txt");
  private static final File FILE_TEST_P4_CHANGES_LARGE_TXT = new File(TestHelper.getTestDataDir(), "test_p4_changes_large.txt");

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestP4ChangeLogParser.class);
  private P4ChangeLogParser changeLogParser = null;
  private static final int TEST_MAX_CHANGE_LIST_SIZE = 100;


  public SATestP4ChangeLogParser(final String s) {
    super(s);
  }


  public void test_parseChangesLog() throws Exception {
    final Collection changeNumbers = changeLogParser.parseChangesLog(new FileInputStream(FILE_TEST_P4_CHANGES_TXT));
    assertNotNull(changeNumbers);
    assertTrue(!changeNumbers.isEmpty());
    final List list = (List)changeNumbers.iterator().next();
    assertEquals(237, list.size());
  }


  public void test_parseChangesHandlesChunkLimit() throws Exception {
    final Collection changeNumbers = changeLogParser.parseChangesLog(new FileInputStream(FILE_TEST_P4_CHANGES_LARGE_TXT), 1);
    assertNotNull(changeNumbers);
    assertEquals(1, changeNumbers.size());

    final List list = (List)changeNumbers.iterator().next();
    assertEquals(15, list.size());
    assertEquals("16", list.get(0));
    assertEquals("1", list.get(list.size() - 1));
  }


  public void test_parseDescribeLog() throws Exception {
    final ChangeListsAndIssues accumulator = new ChangeListsAndIssuesImpl();
    changeLogParser.parseDescribeLog(accumulator, TestHelper.getTestFileAsInputStream("test_p4_describe.txt"));
    assertEquals(0, accumulator.getChangeListIssueBindings().size());
    final List changeLists = accumulator.getChangeLists();
    assertNotNull(changeLists);
    assertTrue(!changeLists.isEmpty());
    assertEquals(237, changeLists.size());
    for (Iterator iter = changeLists.iterator(); iter.hasNext();) {
      final ChangeList changeList = (ChangeList)iter.next();
      assertNotNull(changeList.getCreatedAt());
      assertNotNull(changeList.getDescription());
      assertNotNull(changeList.getClient());
      assertNotNull(changeList.getUser());
      assertNotNull(changeList.getCreatedAt());
      assertNotNull(changeList.getNumber());
      assertTrue(Integer.parseInt(changeList.getNumber()) > 0);
      assertTrue("Size of changes should GT 0", !changeList.getChanges().isEmpty());
      // make sure job description didn' get through
      assertTrue(changeList.getDescription().indexOf("Jobs fixed ...") == -1);
      for (Iterator changeIter = changeList.getChanges().iterator(); changeIter.hasNext();) {
        final Change change = (Change)changeIter.next();
        final String filePath = change.getFilePath();
        assertNotNull(filePath);
        assertNotNull(change.getRevision());
        assertTrue(change.getChangeType() != Change.TYPE_UNKNOWN);
        assertEquals(-1, filePath.indexOf('#'));
      }
    }
  }


  public void test_parseLogTakesMaxChangeListSizeInAccount() throws Exception {
    changeLogParser.setMaxChangeListSize(TEST_MAX_CHANGE_LIST_SIZE);
    boolean truncatedPresent = false;
    final ChangeListsAndIssues accumulator = new ChangeListsAndIssuesImpl();
    changeLogParser.parseDescribeLog(accumulator, TestHelper.getTestFileAsInputStream("test_p4_describe.txt"));
    final List changeLists = accumulator.getChangeLists();
    for (Iterator iter = changeLists.iterator(); iter.hasNext();) {
      final ChangeList changeList = (ChangeList)iter.next();
      assertTrue(changeList.getChanges().size() <= TEST_MAX_CHANGE_LIST_SIZE);
      if (changeList.getChanges().size() < changeList.getOriginalSize()) {
        assertTrue(changeList.isTruncated());
        truncatedPresent = true;
      }
    }
    assertTrue("Truncated change lists should be present", truncatedPresent);
  }


  public void test_parseDescribeLogWithJobs() throws Exception {
    changeLogParser.enableJobCollection(true);
    final ChangeListsAndIssues accumulator = new ChangeListsAndIssuesImpl();
    changeLogParser.parseDescribeLog(accumulator, TestHelper.getTestFileAsInputStream("test_p4_describe_with_jobs.txt"));
    final List changeLists = accumulator.getChangeLists();
    assertNotNull(changeLists);
    assertTrue(!changeLists.isEmpty());
    assertEquals(8, changeLists.size());
    for (Iterator iter = changeLists.iterator(); iter.hasNext();) {
      final ChangeList changeList = (ChangeList)iter.next();
      assertNotNull(changeList.getCreatedAt());
      assertNotNull(changeList.getDescription());
      assertNotNull(changeList.getClient());
      assertNotNull(changeList.getUser());
      assertNotNull(changeList.getCreatedAt());
      assertNotNull(changeList.getNumber());
      assertTrue(Integer.parseInt(changeList.getNumber()) > 0);
      assertTrue("Size of changes should be GT 0", !changeList.getChanges().isEmpty());
      for (Iterator changeIter = changeList.getChanges().iterator(); changeIter.hasNext();) {
        final Change change = (Change)changeIter.next();
        assertNotNull(change.getFilePath());
        assertNotNull(change.getRevision());
        assertTrue(change.getChangeType() != Change.TYPE_UNKNOWN);
      }
    }

    // all jobs are there?
    final List bindings = accumulator.getChangeListIssueBindings();
    assertEquals(4, bindings.size());

    // no dupes in jobs ?
    final Map nameToIssueMap = new HashMap(11);
    for (Iterator iter = bindings.iterator(); iter.hasNext();) {
      final ChangeListIssueBinding binding = (ChangeListIssueBinding)iter.next();
      final Issue issue = binding.getIssue();
      final String name = issue.getKey();
      final Issue found = (Issue)nameToIssueMap.get(name);
      if (found == null) {
        nameToIssueMap.put(name, issue);
      } else {
        // yes, we do use direct object comparison
        assertTrue(issue == found);
      }
    }

  }


  public void test_parseDescribeLogWithJobsDisabled() throws Exception {
    changeLogParser.enableJobCollection(false);
    final ChangeListsAndIssues accumulator = new ChangeListsAndIssuesImpl();
    changeLogParser.parseDescribeLog(accumulator, TestHelper.getTestFileAsInputStream("test_p4_describe_with_jobs.txt"));
    final List changeLists = accumulator.getChangeLists();
    assertNotNull(changeLists);
    assertTrue(!changeLists.isEmpty());
    assertEquals(8, changeLists.size());

    final List bindings = accumulator.getChangeListIssueBindings();
    assertEquals(0, bindings.size());
  }


  protected void setUp() throws Exception {
    super.setUp();
    changeLogParser = new P4ChangeLogParser();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestP4ChangeLogParser.class,
      new String[]{
        "test_parseDescribeLogWithJobs",
      });
  }
}
