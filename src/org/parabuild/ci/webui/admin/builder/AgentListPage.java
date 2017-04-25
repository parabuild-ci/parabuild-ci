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
package org.parabuild.ci.webui.admin.builder;

import org.parabuild.ci.webui.CommonCommandLinkWithImage;
import org.parabuild.ci.webui.admin.system.NavigatableSystemConfigurationPage;
import org.parabuild.ci.webui.common.BreakLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * Page with a list of agents
 */
public final class AgentListPage extends NavigatableSystemConfigurationPage implements StatelessTierlet {

  private static final long serialVersionUID = -2472052514871569348L; // NOPMD
  private static final String CAPTION_ADD_NEW_AGENT = "Add Agent";
  private static final String AGENT_CONFIGURATIONS = "Agent Configurations";


  public AgentListPage() {
    setTitle(makeTitle(AGENT_CONFIGURATIONS));
  }


  protected Result executeSystemConfigurationPage(final Parameters params) {
    final GridIterator gi = new GridIterator(getRightPanel(), 1);

    final MessagePanel pnlHeader = new MessagePanel(AGENT_CONFIGURATIONS);
    pnlHeader.setWidth("100%");
    pnlHeader.getUserPanel().add(new BreakLabel());
    pnlHeader.getUserPanel().add(new CommonLabel("Agent is a machine that executes builds under control of Parabuild. An agent machine runs Parabuild that is installed in the remote agent mode. To run builds, an agent must be attached to one ore more build farms."));
    pnlHeader.getUserPanel().add(new BreakLabel());

    // Add agent list table
    gi.add(pnlHeader);
    gi.add(WebuiUtils.makePanelDivider());
    gi.add(new AgentsTable(super.isValidAdminUser()));

    // Add new agent link - bottom
    if (isValidAdminUser()) {
      gi.add(WebuiUtils.makeHorizontalDivider(5));
      gi.add(makeNewBuilderLink());
    }
    return Result.Done();
  }


  private CommonCommandLinkWithImage makeNewBuilderLink() {
    final CommonCommandLinkWithImage lnkAddNewBuilder = new CommonCommandLinkWithImage(CAPTION_ADD_NEW_AGENT, Pages.PAGE_EDIT_AGENT);
    lnkAddNewBuilder.setAlignX(Layout.LEFT);
    lnkAddNewBuilder.setAlignY(Layout.TOP);
    return lnkAddNewBuilder;
  }
}
