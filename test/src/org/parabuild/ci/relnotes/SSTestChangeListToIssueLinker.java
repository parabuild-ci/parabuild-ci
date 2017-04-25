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
package org.parabuild.ci.relnotes;

import java.util.*;
import junit.framework.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 *
 */
public class SSTestChangeListToIssueLinker extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestChangeListToIssueLinker.class);

  private ChangeListToIssueLinker linker = null;
  private ConfigurationManager cm = null;


  public SSTestChangeListToIssueLinker(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_process() throws Exception {
    //i nitial state OK?
    assertEquals(0, cm.getIssueChangeLists(1).size());
    assertEquals(0, cm.getIssueChangeLists(2).size());

    // process
    final BuildRun buildRun = cm.getBuildRun(8);
    linker.process(buildRun);
    final List issueChangeListsForID1 = cm.getIssueChangeLists(1);
    final List issueChangeListsForID2 = cm.getIssueChangeLists(2);

    // got linked?
    assertEquals(1, issueChangeListsForID1.size());
    assertEquals(1, issueChangeListsForID2.size());

    // right change lists got linked to right issues?
    assertEquals(5, ((ChangeList)issueChangeListsForID1.get(0)).getChangeListID());
    assertEquals(4, ((ChangeList)issueChangeListsForID2.get(0)).getChangeListID());

    // second run, nothing should change
    linker.process(buildRun);
    assertEquals(1, cm.getIssueChangeLists(1).size());
    assertEquals(1, cm.getIssueChangeLists(2).size());
    assertEquals(5, ((ChangeList)cm.getIssueChangeLists(1).get(0)).getChangeListID());
    assertEquals(4, ((ChangeList)cm.getIssueChangeLists(2).get(0)).getChangeListID());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestChangeListToIssueLinker.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    cm = ConfigurationManager.getInstance();
    final List list = new ArrayList(1);
    list.add("Fixed (TEST-[0-9]+)");
    list.add("Implemented (TEST-[0-9]+)");
    linker = new ChangeListToIssueLinker(list);
  }
}
