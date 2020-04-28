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

import java.util.List;

/**
 * This panel is used by build set up panel to set up release
 * notes obtained from Parabuild Jira listener.
 */
public final class P4JobsSetupPanel extends AbstractIssueTrackerSetupPanel {

  private static final long serialVersionUID = 4519140817055844161L; // NOPMD


  public P4JobsSetupPanel() {
    super("  Perforce  ", false, false);
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
    // do nothing - this panel doesn't carry its
    // own fields - everything is valdated
  }
}
