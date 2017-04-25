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

import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.webui.common.PageSectionLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Border;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;

/**
 * This clas is a composite of BuildRunSummaryPanel,
 * SequenceRunSummaryPanel and a hosrisontal bar that holds build
 * result flwRunResultsLinks, and a name to show (like "Build
 * log", "Changes", etc).
 * <p/>
 * It should not have labels.
 */
public final class BuildRunHeaderPanel extends Panel {

  private static final long serialVersionUID = 624348935284219005L; // NOPMD

  private final BuildRunSummaryPanel pnlBuildRunSummary = new BuildRunSummaryPanel(); // NOPMD
  private final BuildRunResultsLinksFlow flwRunResultsLinks = new BuildRunResultsLinksFlow(); // NOPMD
  private final Label lbDescription = new PageSectionLabel(); // NOPMD
  private final StepRunSummaryTable pnlStepRunSummary = new StepRunSummaryTable(); // NOPMD
  private final Panel pnlRunAndStepsSummaryHolder = new Panel(); // NOPMD


  public BuildRunHeaderPanel() {

    // align summary panel
    pnlBuildRunSummary.setAlignY(Layout.BOTTOM);
    pnlRunAndStepsSummaryHolder.add(pnlBuildRunSummary, new Layout(0, 0, 1, 1));

    // create separator between buil run summary and sequence run summary
    final Label lbSep = new Label(" ");
    lbSep.setWidth(10);
    pnlRunAndStepsSummaryHolder.add(lbSep, new Layout(1, 0, 1, 1));

    final Layout layout = new Layout(0, 0, 3, 1);

    // create sequence run summary panel
    pnlStepRunSummary.setAlignY(Layout.BOTTOM);
    pnlStepRunSummary.setAlignX(Layout.LEFT);
    pnlStepRunSummary.setHeaderBackground(Pages.TABLE_COLOR_BORDER);
    pnlRunAndStepsSummaryHolder.add(pnlStepRunSummary, new Layout(2, 0, 1, 1));
    add(pnlRunAndStepsSummaryHolder, layout);

    layout.positionX = 0;
    layout.positionY++;
    layout.spanX = 3;
    add(WebuiUtils.makePanelDivider(), layout);

    // add build history links
    flwRunResultsLinks.setAlignX(Layout.LEFT);
    flwRunResultsLinks.setWidth("100%");
    flwRunResultsLinks.setHeight(30);
    flwRunResultsLinks.setBorder(Border.ALL, 1, Pages.COLOR_PANEL_BORDER);
    flwRunResultsLinks.setBackground(Pages.COLOR_LIGHT_LIGHT_YELLOW);

    final Panel pnlResultLinks = new Panel();
    pnlResultLinks.setWidth("100%");
    pnlResultLinks.add(flwRunResultsLinks, new Layout(0, 0, 1, 1));
    layout.positionX = 0;
    layout.positionY++;
    layout.spanX = 3;
    add(pnlResultLinks, layout);

    // add horisonatal divider
    layout.positionX = 0;
    layout.positionY++;
    layout.spanX = 3;
    add(WebuiUtils.makePanelDivider(), layout);

    // in-page header
    layout.positionX = 0;
    layout.positionY++;
    layout.spanX = 3;
    add(lbDescription, layout);

    // add divider
    layout.positionX = 0;
    layout.positionY++;
    layout.spanX = 3;
    add(WebuiUtils.makePanelDivider(), layout);
  }


  /**
   * Sets description dispayed in a bar under BuildRunSummaryPanel
   * & StepRunSummaryTable. This bar serves as a header for the
   * details diplayed.
   *
   * @param descr String to dispalay
   */
  public void setDescription(final String descr) {
    lbDescription.setText(descr);
  }


  /**
   * Sets build run for which to show the header
   */
  public void showBuildRun(final BuildRun buildRun) {
    pnlBuildRunSummary.setBuildRun(buildRun);
    pnlStepRunSummary.setBuildRun(buildRun);
    flwRunResultsLinks.setBuildRun(buildRun, true, true, true, true, true);
    flwRunResultsLinks.setBoldLinkFont();
  }


  public void setBuildRunLinkFactory(final BuildRunURLFactory buildRunURLFactory) {
    this.pnlBuildRunSummary.setBuilRunLinkFactory(buildRunURLFactory);
  }
}
