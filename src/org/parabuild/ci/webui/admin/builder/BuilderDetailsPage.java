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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.object.BuilderConfiguration;
import org.parabuild.ci.webui.CommonCommandLinkWithImage;
import org.parabuild.ci.webui.admin.system.NavigatableSystemConfigurationPage;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This page is responsible for creating/editting cluster
 */
public final class BuilderDetailsPage extends NavigatableSystemConfigurationPage implements StatelessTierlet {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(BuilderDetailsPage.class); // NOPMD

  private static final String PAGE_TITLE_DEFAULT = "Build Farm Details";
  private static final String ERROR_BUILDER_NOT_FOUND = "Requested build farm not found";
  private static final String ADD_AGENT_TO_BUILDER = "Add Agent to Build Farm";

  private final EditBuilderPanel pnlBuilder = new EditBuilderPanel(WebUIConstants.MODE_VIEW); // NOPMD
  private final BuilderAgentsTable tblBuilderAgents = new BuilderAgentsTable();
  private final CommonCommandLinkWithImage lnkAddBuilderAgent = new CommonCommandLinkWithImage(ADD_AGENT_TO_BUILDER, Pages.PAGE_ADD_BUILDER_AGENT);


  /**
   * Constructor.
   */
  public BuilderDetailsPage() {
    setTitle(makeTitle(PAGE_TITLE_DEFAULT)); // default title
    final GridIterator gi = new GridIterator(getRightPanel(), 2);
    gi.add(pnlBuilder, 2);
    gi.add(WebuiUtils.makePanelDivider(), 2);
    gi.addPair(createFiller(), tblBuilderAgents);
    gi.add(WebuiUtils.makePanelDivider(), 2);
    gi.addPair(createFiller(), lnkAddBuilderAgent);

    // In the details page builder panel is spread wide
    pnlBuilder.setWidth("100%");
    tblBuilderAgents.setWidth("97%");
    tblBuilderAgents.setAlignX(Layout.RIGHT);
    lnkAddBuilderAgent.setWidth("97%");
  }


  protected Result executeSystemConfigurationPage(final Parameters params) {
    // layout
    if (params.isParameterPresent(Pages.PARAM_BUILDER_ID)) {
      // cluster ID is provided
      final BuilderConfiguration builderConfiguration = ParameterUtils.getBuilderFromParameters(params);
      if (builderConfiguration == null) {
        // show error and exit
        baseContentPanel().getUserPanel().clear();
        baseContentPanel().showErrorMessage(ERROR_BUILDER_NOT_FOUND);
      } else {
        // cluster found, load data
        final String title = "Edit Build Farm - " + builderConfiguration.getName();
        setTitle(makeTitle(title));
        pnlBuilder.setTitle(title);
        pnlBuilder.load(builderConfiguration);
        tblBuilderAgents.load(builderConfiguration);
        lnkAddBuilderAgent.setParameters(BuilderUtils.createBuilderParameters(builderConfiguration.getID()));
      }
    } else {
      // show error and exit
      baseContentPanel().getUserPanel().clear();
      baseContentPanel().showErrorMessage(ERROR_BUILDER_NOT_FOUND);
    }
    return Result.Done();
  }


  private static Label createFiller() {
    final Label resut = new Label();
    resut.setWidth("3%");
    return resut;
  }


  public String toString() {
    return "BuilderDetailsPage{" +
            "pnlBuilder=" + pnlBuilder +
            ", tblBuilderAgents=" + tblBuilderAgents +
            ", lnkAddBuilderAgent=" + lnkAddBuilderAgent +
            "} " + super.toString();
  }
}
