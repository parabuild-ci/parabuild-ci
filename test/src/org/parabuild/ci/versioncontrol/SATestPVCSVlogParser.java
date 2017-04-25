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


/**
 * Tests PVCSVlogParser
 */
public class SATestPVCSVlogParser extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestPVCSVlogParser.class);
  private static final String TEST_REPOSITORY = "D:\\mor2\\dev\\bt\\test\\data\\pvcs";
  private static final String STRING_SOURCE_LINE_ONE = "test_project/sourceline/alwaysvalid";

  private PVCSVlogParser parser = null;
  private MockVlogHandler mockVlogHandler;


  public void test_parseChangeLog() throws Exception {
    InputStream changeLogInputStream = null;
    try {
      // parse
      changeLogInputStream = new FileInputStream(new File(TestHelper.getTestDataDir(), "test_pvcs_vlog.txt"));
      parser.parseChangeLog(changeLogInputStream);
      assertEquals(5, mockVlogHandler.getHandleCallCounter());
    } finally {
      IoUtils.closeHard(changeLogInputStream);
    }
  }


  public void test_parseNoEntriesChangeLog() throws Exception {
    parser.parseChangeLog(TestHelper.getTestFile("test_pvcs_no_entries_vlog.txt"));
    assertEquals(0, mockVlogHandler.getHandleCallCounter());
  }


  protected void setUp() throws Exception {
    super.setUp();
    this.mockVlogHandler = new MockVlogHandler();
    this.parser = new PVCSVlogParser(Locale.US, TEST_REPOSITORY, STRING_SOURCE_LINE_ONE, "", mockVlogHandler);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestPVCSVlogParser.class, new String[]{
    });
  }


  public SATestPVCSVlogParser(final String s) {
    super(s);
  }


  private static final class MockVlogHandler implements PVCSVlogHandler {

    private int handleCallCounter = 0;


    /**
     * This method is called when a revsion is found in a
     * change log. It is guaranteed that it is called only
     * once for a single revesion.
     *
     * @param changeDate
     * @param revisionDescription
     * @param owner
     * @param branch
     * @param filePath
     * @param revision
     * @param changeType
     */
    public void handle(final Date changeDate, final StringBuffer revisionDescription, final String owner, final String branch, final String filePath, final String revision, final byte changeType) {
      handleCallCounter++;
    }


    public int getHandleCallCounter() {
      return handleCallCounter;
    }


    /**
     * This method is called fater the handle is called last
     * time.
     */
    public void afterHandle() {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    public void beforeHandle() {

    }
  }
}
