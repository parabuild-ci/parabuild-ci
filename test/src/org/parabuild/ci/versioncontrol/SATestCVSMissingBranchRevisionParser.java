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

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.util.*;


/**
 * Tests CVSMissingBranchRevisionParser
 */
public class SATestCVSMissingBranchRevisionParser extends TestCase {

  private CVSMissingBranchRevisionParser changeLogParser = null;


  /**
   * Makes sure changes reported for main branch doesn't contain
   * branching-out activities.
   */
  public void test_parseChangeLogWithChangesInABranch() throws Exception {
    InputStream is = null;
    try {
      is = new FileInputStream(TestHelper.getTestFile("test_cvs_change_log_warnings_no_revision.txt"));
      final Map result = changeLogParser.parse(is);
      assertTrue(!result.isEmpty());
      assertEquals(2, result.size());
      assertNotNull(result.get(new Integer("/opt/cvs/cvsroot/test/sourceline/alwaysvalid/src/Attic/readme.txt,v".hashCode())));
    } finally {
      IoUtils.closeHard(is);
    }
  }


  public SATestCVSMissingBranchRevisionParser(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    changeLogParser = new CVSMissingBranchRevisionParser("test_branch");
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestCVSMissingBranchRevisionParser.class, new String[]{
    });
  }
}
