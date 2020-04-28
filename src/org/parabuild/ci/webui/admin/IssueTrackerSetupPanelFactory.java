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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.object.IssueTracker;

/**
 */
public final class IssueTrackerSetupPanelFactory {

  private IssueTrackerSetupPanelFactory() {
  }


  /**
   * Factory method to create a tracker setup punel that correponds
   * tracker type.
   */
  public static final AbstractIssueTrackerSetupPanel makeTrackerPanel(final int trackerType) {
    AbstractIssueTrackerSetupPanel result = null;
    switch (trackerType) {
      case IssueTracker.TYPE_JIRA_LISTENER:
        result = new JiraListenerSetupPanel();
        break;
      case IssueTracker.TYPE_BUGZILLA_DIRECT:
        result = new BugzillaDirectSetupPanel();
        break;
      case IssueTracker.TYPE_PERFORCE:
        result = new P4JobsSetupPanel();
        break;
      default:
        throw new IllegalArgumentException("Unknown issue tracker code: \"" + trackerType + '\"');
    }
    return result;
  }
}
