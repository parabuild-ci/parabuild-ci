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

import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.admin.BuildSequenceTable;
import org.parabuild.ci.webui.admin.ManualStartParametersPanel;
import org.parabuild.ci.webui.admin.NotificationSettingsPanel;
import org.parabuild.ci.webui.admin.SourceControlPanel;
import org.parabuild.ci.webui.admin.SourceControlPanelFactory;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;

/**
 * This class shows a page with build run's configuration.
 */
public final class BuildRunConfigReportPage extends AbstractBuildRunResultPage implements ConversationalTierlet {

  private static final long serialVersionUID = 2670365403954030583L; // NOPMD

  private static final String TITLE_BUILD_RESULT = "Build Run Configuration Report";
  private static final String DESCRIPTION = "Build Run Configuration";
  private static final String CAPTION_MISCELLANEOUS = "Miscellaneous";
  private static final String CAPTION_CHECKOUT_DIRECTORY = "Checkout directory: ";
  private static final String CAPTION_CONFIGURATION_REPORT_FOR = "Configuration Report For: ";


  /**
   * Constructor.
   */
  public BuildRunConfigReportPage() {
    setTitle(makeTitle(TITLE_BUILD_RESULT));
  }


  protected String description(final Parameters params) {
    return DESCRIPTION;
  }


  /**
   * Provides main handing. This is called after a common build
   * result panel was created and added to the layout.
   *
   * @param params that #executePage method was called with.
   *@param buildRun BuildRun to process.
   */
  protected Result executeBuildRunResultPage(final Parameters params, final BuildRun buildRun) {
    final Panel pnlContent = baseContentPanel().getUserPanel();
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildRunConfig buildRunConfig = cm.getBuildRunConfig(buildRun);

    // Show version control config
    final SourceControlPanel pnlVersionControl = SourceControlPanelFactory.getPanel(buildRunConfig);
    pnlVersionControl.setWidth(Pages.PAGE_WIDTH);
    pnlVersionControl.load(buildRunConfig);
    pnlVersionControl.setMode(WebUIConstants.MODE_VIEW);
    pnlContent.add(pnlVersionControl);
    pnlContent.add(WebuiUtils.makePanelDivider());

    // Show sequence
    final BuildSequenceTable tblSequence = new BuildSequenceTable(BuildStepType.BUILD, WebUIConstants.MODE_VIEW);
    tblSequence.load(buildRunConfig);
    pnlContent.add(tblSequence);
    pnlContent.add(WebuiUtils.makePanelDivider());

    // Show Notification
    final NotificationSettingsPanel pnlNotification = new NotificationSettingsPanel(WebUIConstants.MODE_VIEW);
    pnlContent.add(pnlNotification);
    pnlNotification.load(buildRunConfig);
    pnlContent.add(WebuiUtils.makePanelDivider());



    // Show parameters
    final BuildConfig buildConfig = cm.getActiveBuildConfig(buildRun.getActiveBuildID());
    final StartParameterType parameterType = buildRun.getType() == BuildRun.TYPE_PUBLISHING_RUN ? StartParameterType.PUBLISH : StartParameterType.BUILD;
    final ManualStartParametersPanel pnlStartParameters = new ManualStartParametersPanel(buildRunConfig.getBuildID(), buildConfig.isScheduled(), parameterType, WebUIConstants.MODE_VIEW);
    pnlContent.add(pnlStartParameters);
    pnlContent.add(WebuiUtils.makePanelDivider());

    // Show miscellaneous
    final MessagePanel pnlMiscellaneousParams = new MessagePanel(CAPTION_MISCELLANEOUS);
    final String checkoutDirectory = cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.CHECKOUT_DIRECTORY);
    if (!StringUtils.isBlank(checkoutDirectory)) {
      final Panel userPanel = pnlMiscellaneousParams.getUserPanel();
      final GridIterator gi = new GridIterator(userPanel, 2);
      gi.addPair(new CommonFieldLabel(CAPTION_CHECKOUT_DIRECTORY), new CommonLabel(checkoutDirectory));
      pnlContent.add(userPanel);
    }

    return makeDoneResult(buildRun);
  }


  /**
   * Creates a page title. Implementing classes should provide
   * page titles that correspond the given page result.
   *
   * @param buildRun to create a title for.
   *
   * @return created page title.
   */
  protected String buildRunResultPageTitle(final BuildRun buildRun) {
    return CAPTION_CONFIGURATION_REPORT_FOR + buildRun.getBuildName() + " # " + buildRun.getBuildRunNumber();
  }


  /**
   * @return Flow that constant Prev/Next nav links that will be
   *  inserted into the right side header divider.
   *
   * @param buildRun the thapge was executed with.
   * @param parameters that the page was executed with.
   *
   */
  protected Flow makePreviousNextNavigationLinks(final BuildRun buildRun, final Parameters parameters) {
    final StandardPreviousNextLinksMaker linksMaker = new StandardPreviousNextLinksMaker(Pages.BUILD_COFNIG_REPORT);
    return linksMaker.makeLinks(parameters, buildRun, getTierletContext());
  }


  /**
   * @return a page-specific build run link factory.
   *
   * @see BuildRunURLFactory
   */
  protected BuildRunURLFactory makeBuildRunURLFactory() {
    return new StandardBuildRunURLFactory(Pages.BUILD_COFNIG_REPORT);
  }
}
