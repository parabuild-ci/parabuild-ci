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

import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * Undocumented commands
 */
public class UndocumentedCommandsPage extends BasePage implements StatelessTierlet {


  private static final long serialVersionUID = 4542828122356933097L;

  private final AnnotatedCommandLink lnkResetMerges = new AnnotatedCommandLink("Reset ALL Merges", Pages.PAGE_MERGE_RESET_ALL, "Resets data for all paused merges", true);
  private final AnnotatedCommandLink lnkCleanupInactiveWorkspaces = new AnnotatedCommandLink("Cleanup ALL Inactive Workspaces", Pages.PAGE_CLEANUP_ALL_INACTIVE_WORKSPACES, "Cleans up working build directories for all inactive builds", true);
  private static final String CAPTION_UNDOCUMENTED_COMMANDS = "Undocumented Commands";


  public UndocumentedCommandsPage() {
    super(FLAG_FLOATING_WIDTH | FLAG_SHOW_PAGE_HEADER_LABEL | FLAG_SHOW_HEADER_SEPARATOR);
    setPageHeader(CAPTION_UNDOCUMENTED_COMMANDS);
    setTitle(CAPTION_UNDOCUMENTED_COMMANDS);
    baseContentPanel().setWidth("100%");
    baseContentPanel().getUserPanel().add(lnkCleanupInactiveWorkspaces);
    baseContentPanel().getUserPanel().add(lnkResetMerges);
  }


  /**
   * Strategy method to be implemented by classes inheriting
   * BasePage.
   *
   * @param parameters
   *
   * @return result of page execution
   */
  protected Result executePage(final Parameters parameters) {
    if (!isValidAdminUser()) return WebuiUtils.showNotAuthorized(this);
    return Result.Done();
  }
}
