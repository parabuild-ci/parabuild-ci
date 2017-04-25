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
package org.parabuild.ci.webui;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.admin.*;

/**
 *
 */
public class SSTestIssueTrackerSetupPanelFactory extends ServersideTestCase {

  /**
   */
  public void test_makeTrackerPanel() throws Exception {
    assertMakesExpectedClass(BugzillaDirectSetupPanel.class, IssueTracker.TYPE_BUGZILLA_DIRECT);
    assertMakesExpectedClass(JiraListenerSetupPanel.class, IssueTracker.TYPE_JIRA_LISTENER);
    assertMakesExpectedClass(P4JobsSetupPanel.class, IssueTracker.TYPE_PERFORCE);
  }


  /**
   */
  public void test_failsOnUnknowType() throws Exception {
    try {
      IssueTrackerSetupPanelFactory.makeTrackerPanel(111);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
    }
  }


  private void assertMakesExpectedClass(final Class aClass, final byte trackerType) {
    final AbstractIssueTrackerSetupPanel p = IssueTrackerSetupPanelFactory.makeTrackerPanel(trackerType);
    assertEquals(aClass, p.getClass());
  }


  /**
   * Required by JUnit
   */
  public SSTestIssueTrackerSetupPanelFactory(final String s) {
    super(s);
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestIssueTrackerSetupPanelFactory.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
