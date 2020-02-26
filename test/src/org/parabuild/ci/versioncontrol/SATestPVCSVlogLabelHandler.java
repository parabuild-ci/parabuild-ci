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

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.object.Change;


/**
 * Tests PVCSVlogLabelHandler
 */
public class SATestPVCSVlogLabelHandler extends TestCase {

  private static final Log log = LogFactory.getLog(SATestPVCSVlogLabelHandler.class);
  private static final String STRING_SOURCE_LINE_ONE = "test_project/sourceline/alwaysvalid";
  private static final PVCSDateFormat PVCS_DATE_FORMAT = new PVCSDateFormat(Locale.US);
  private static final int TEST_FILE_BLOCK_SIZE = 2;

  private MocLabelCreator mocLabelCreator;
  private PVCSVlogLabelHandler handler;


  public void test_handle() throws ParseException, IOException, CommandStoppedException, AgentFailureException {

    // test_project/sourceline/alwaysvalid/test1.txt
    // #1.2
    handler.handle(PVCS_DATE_FORMAT.parseOutput("Jan 30 2006 23:20:00"), // after label date
            new StringBuffer(), "test_owner", "test_branch",
            "test_project/sourceline/alwaysvalid/test1.txt", "1.2",
            Change.TYPE_CHECKIN);
    // #1.1
    handler.handle(PVCS_DATE_FORMAT.parseOutput("Jan 30 2006 23:18:03"), // a bit before (1 second)
            new StringBuffer(), "test_owner", "test_branch",
            "test_project/sourceline/alwaysvalid/test1.txt", "1.1",
            Change.TYPE_CHECKIN);
    // #1.0
    handler.handle(PVCS_DATE_FORMAT.parseOutput("Dec 20 2005 10:05:44"), // before label date
            new StringBuffer(), "test_owner", "test_branch",
            "test_project/sourceline/alwaysvalid/test1.txt", "1.0",
            Change.TYPE_ADDED);

    // test_project/sourceline/alwaysvalid/test2.txt
    // #1.2
    handler.handle(PVCS_DATE_FORMAT.parseOutput("Jan 30 2006 23:20:00"), // after label date
            new StringBuffer(), "test_owner", "test_branch",
            "test_project/sourceline/alwaysvalid/test2.txt", "1.2",
            Change.TYPE_CHECKIN);
    // #1.1
    handler.handle(PVCS_DATE_FORMAT.parseOutput("Jan 30 2006 23:18:04"), // exactly on
            new StringBuffer(), "test_owner", "test_branch",
            "test_project/sourceline/alwaysvalid/test2.txt", "1.1",
            Change.TYPE_CHECKIN);
    // #1.0
    handler.handle(PVCS_DATE_FORMAT.parseOutput("Dec 20 2005 10:05:44"), // before label date
            new StringBuffer(), "test_owner", "test_branch",
            "test_project/sourceline/alwaysvalid/test2.txt", "1.0",
            Change.TYPE_ADDED);

    // test_project/sourceline/alwaysvalid/test3.txt - all after
    // #1.2
    handler.handle(PVCS_DATE_FORMAT.parseOutput("Dec 20 2007 11:05:44"), //
            new StringBuffer(), "test_owner", "test_branch",
            "test_project/sourceline/alwaysvalid/test3.txt", "1.2",
            Change.TYPE_CHECKIN);
    // #1.1
    handler.handle(PVCS_DATE_FORMAT.parseOutput("Dec 20 2007 10:05:44"), //
            new StringBuffer(), "test_owner", "test_branch",
            "test_project/sourceline/alwaysvalid/test3.txt", "1.1",
            Change.TYPE_CHECKIN);
    // #1.0
    handler.handle(PVCS_DATE_FORMAT.parseOutput("Dec 20 2007 19:05:44"), //
            new StringBuffer(), "test_owner", "test_branch",
            "test_project/sourceline/alwaysvalid/test3.txt", "1.0",
            Change.TYPE_ADDED);


    // test_project/sourceline/alwaysvalid/test4.txt
    // #1.2
    handler.handle(PVCS_DATE_FORMAT.parseOutput("Jan 30 2006 23:20:00"), // after label date
            new StringBuffer(), "test_owner", "test_branch",
            "test_project/sourceline/alwaysvalid/test4.txt", "1.2",
            Change.TYPE_CHECKIN);
    // #1.1
    handler.handle(PVCS_DATE_FORMAT.parseOutput("Jan 30 2006 23:18:04"), // exactly on
            new StringBuffer(), "test_owner", "test_branch",
            "test_project/sourceline/alwaysvalid/test4.txt", "1.1",
            Change.TYPE_CHECKIN);
    // #1.0
    handler.handle(PVCS_DATE_FORMAT.parseOutput("Dec 20 2005 10:05:44"), // before label date
            new StringBuffer(), "test_owner", "test_branch",
            "test_project/sourceline/alwaysvalid/test4.txt", "1.0",
            Change.TYPE_ADDED);


    // finish and assert
    handler.afterHandle();

    // assert
    assertEquals(2, mocLabelCreator.getLabelCalledCounter());
    assertEquals(3, mocLabelCreator.getRevisionsSubmittedCounter());

    final List revisions = mocLabelCreator.getRevisions();
    for (int i = 0; i < revisions.size(); i++) {
      final PVCSRevision pvcsRevision = (PVCSRevision) revisions.get(i);
      assertNotNull("revision", pvcsRevision.getRevision());
      assertNotNull("file path", pvcsRevision.getFilePath());
    }
  }


  protected void setUp() throws Exception {
    this.mocLabelCreator = new MocLabelCreator();
    final PVCSVlogCommandParameters testParameters = new PVCSVlogCommandParameters();
    testParameters.setEndDate(PVCS_DATE_FORMAT.parseOutput("Jan 30 2006 23:18:04"));
    this.handler = new PVCSVlogLabelHandler(testParameters, mocLabelCreator, TEST_FILE_BLOCK_SIZE);
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestPVCSVlogLabelHandler.class, new String[]{
    });
  }


  public SATestPVCSVlogLabelHandler(final String s) {
    super(s);
  }


  private static final class MocLabelCreator implements PVCSLabelCreator {

    private int labelCalledCounter = 0;
    private int revisionsSubmittedCounter = 0;
    private final List revisions = new ArrayList(5);


    /**
     * Labels given list of {@link PVCSRevision} objects
     *
     * @param revisionBlock list of {@link PVCSRevision}
     *                      objects to label
     */
    public void label(final List revisionBlock) {
      if (log.isDebugEnabled()) log.debug("revisionBlock: " + revisionBlock);
      labelCalledCounter++;
      revisionsSubmittedCounter += revisionBlock.size();
      revisions.addAll(revisionBlock);
    }


    public int getLabelCalledCounter() {
      return labelCalledCounter;
    }


    public int getRevisionsSubmittedCounter() {
      return revisionsSubmittedCounter;
    }


    public List getRevisions() {
      return revisions;
    }
  }
}
