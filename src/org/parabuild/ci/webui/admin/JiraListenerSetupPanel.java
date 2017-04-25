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

import java.util.*;

import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This panel is used by build set up panel to set up release
 * notes obtained from Parabuild Jira listener.
 */
public final class JiraListenerSetupPanel extends AbstractIssueTrackerSetupPanel {

  private static final long serialVersionUID = 4519140817055844161L; // NOPMD

  private static final String CAPTION_PARABUILD_LISTENER_ON_JIRA_SIDE = "Parabuild listener on Jira side";
  private static final String STR_JIRA_PROJECT = "Jira project:";
  private static final String STR_JIRA_VERSIONS = "Version(s):";
// NOTE: vimeshev 05/21/2004 - commented out till it's clear what to do with Jira "fix in" versions
//  private static final String STR_JIRA_FIX_VERSION = "Jira fix versions:";
  private static final String STR_CONNECTION_TYPE = "Connection type:";

// NOTE: vimeshev 05/21/2004 - commented out till it's clear what to do with Jira "fix in" versions
//  private Label lbFixVersions = new CommonFieldLabel(STR_JIRA_FIX_VERSION);

  private final Field flProject = new CommonField(15, 15); // NOPMD
  private final Field flVersions = new CommonField(10, 10); // NOPMD
// NOTE: vimeshev 05/21/2004 - commented out till it's clear what to do with Jira "fix in" versions
//  private Field flFixVersions = new CommonField(10, 10);


  public JiraListenerSetupPanel() {
    super("  Jira  ");
    // layout
    gridIter.addPair(new CommonFieldLabel(STR_CONNECTION_TYPE), new BoldCommonLabel(CAPTION_PARABUILD_LISTENER_ON_JIRA_SIDE));
    gridIter.addPair(new CommonFieldLabel(STR_JIRA_PROJECT), new RequiredFieldMarker(flProject));
    gridIter.addPair(new CommonFieldLabel(STR_JIRA_VERSIONS), flVersions);
// NOTE: vimeshev 05/21/2004 - commented out till it's clear what to do with Jira "fix in" versions
//    gridIter.addPair(lbFixVersions, flFixVersions);

    // bind props to fields
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.JIRA_PROJECT, flProject);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.JIRA_VERSIONS, flVersions);
// NOTE: vimeshev 05/21/2004 - commented out till it's clear what to do with Jira "fix in" versions
//    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.JIRA_FIX_VERSIONS, flFixVersions);

    // appearance
    showHeaderDivider(true);
    setPadding(5);
  }


  /**
   * Validates this panel's input.
   *
   * @param errors List to add validation error messages if any.
   *
   * @see AbstractIssueTrackerSetupPanel#doValidate(List)
   */
  protected void doValidate(final List errors) {
    WebuiUtils.validateFieldNotBlank(errors, STR_JIRA_PROJECT, flProject);
  }
}
