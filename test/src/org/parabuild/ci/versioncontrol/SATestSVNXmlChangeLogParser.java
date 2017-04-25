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
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;


/**
 * Tests SVNChangeLogParser
 */
public class SATestSVNXmlChangeLogParser extends TestCase {

  private static final Log log = LogFactory.getLog(SATestSVNXmlChangeLogParser.class);

  private SVNXmlChangeLogParser changeLogParser = null;
  private static final int TEST_MAX_CHANGE_LIST_SIZE = 5;


  public void test_parseChangeLog() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      // parse
      changeLogInputStream = makeTestChangeLogInputStream();
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      if (log.isDebugEnabled()) log.debug("result.size() = " + result.size());
      assertTrue(!result.isEmpty());
      assertEquals(510, result.size());

    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseChangeLogBug1381() throws Exception {
    doTestBug1381("/trunk/floggy-persistence-framework-impl/");
    doTestBug1381("/trunk/floggy-persistence-framework-impl");
  }


  private void doTestBug1381(final String parent) throws IOException {
    InputStream changeLogInputStream = null;
    try {
      // parse
      changeLogParser.ignoreSubSubdirectory(parent);
      changeLogInputStream = makeTestChangeLogInputStream();
      final List result = changeLogParser.parseChangeLog(changeLogInputStream);
      if (log.isDebugEnabled()) log.debug("result.size() = " + result.size());
      assertTrue(!result.isEmpty());
      assertEquals(509, result.size());
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseChangeLogUnlimitedChangeListSize() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      // parse
      changeLogInputStream = makeTestChangeLogInputStream();
      final SVNXmlChangeLogParser unlimitedChangeListSizeParser = new SVNXmlChangeLogParser(Integer.MAX_VALUE);
      final List result = unlimitedChangeListSizeParser.parseChangeLog(changeLogInputStream);
      VersionControlTestUtil.assertChangeListExistsAndValid(result, "38", "thiagorossato", SVNXmlChangeLogParser.createSvnChangeLogDateFormatter(), "2007-04-28T19:36:26.000542Z", 4);
      VersionControlTestUtil.assertChangeListExistsAndValid(result, "5", "thiagolm", SVNXmlChangeLogParser.createSvnChangeLogDateFormatter(), "2007-01-04T02:21:22.864138", 1);
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseNoEntriesChangeLog() throws Exception {
    final List result = changeLogParser.parseChangeLog(TestHelper.getTestFile("test_svn_no_entries_change_log.xml"));
    assertEquals(0, result.size());
  }


  /**
   * Calling for empty file should throw an exception.
   */
  public void test_parseEmptyChangeLog() throws Exception {
    try {
      changeLogParser.parseChangeLog(TestHelper.getTestFile("test_svn_empty_change_log.txt"));
      TestHelper.failNoExceptionThrown();
    } catch (IOException e) {
    }
  }


  /**
   * Calling for empty file should throw an exception.
   */
  public void test_bug1567() throws Exception {
    final List list = changeLogParser.parseChangeLog(TestHelper.getTestFile("svn-xml-log-PARABUILD-1567.txt"));
    for (int i = 0; i < list.size(); i++) {
      ChangeList o = (ChangeList) list.get(i);
      System.out.println("o = " + o);
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


  private FileInputStream makeTestChangeLogInputStream() throws FileNotFoundException {
    return new FileInputStream(new File(TestHelper.getTestDataDir(), "test_svn_xml_change_log.xml"));
  }


  public SATestSVNXmlChangeLogParser(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    changeLogParser = new SVNXmlChangeLogParser(TEST_MAX_CHANGE_LIST_SIZE);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestSVNXmlChangeLogParser.class, new String[]{
            "test_parseChangeLog",
            "test_parseChangeLogBug1381"
    });
  }
}
