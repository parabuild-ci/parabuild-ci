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

import java.util.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.admin.*;

/**
 * Tests home page
 */
public class SSTestJiraListenerSetupPanel extends ServersideTestCase {

  private JiraListenerSetupPanel jiraListenerSetupPanel = null;
  private ConfigurationManager cm = null;


  public SSTestJiraListenerSetupPanel(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_emtyDosntValidate() throws Exception {
    assertTrue(!jiraListenerSetupPanel.validate());
  }


  public void test_load() {
    final List trackers = cm.getIssueTrackers(TestHelper.TEST_CVS_VALID_BUILD_ID);
    for (Iterator i = trackers.iterator(); i.hasNext();) {
      final IssueTracker tracker = (IssueTracker)i.next();
      if (tracker.getType() == IssueTracker.TYPE_JIRA_LISTENER) {
        jiraListenerSetupPanel.load(tracker);
        return;
      }
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestJiraListenerSetupPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
    jiraListenerSetupPanel = new JiraListenerSetupPanel();
  }
}
