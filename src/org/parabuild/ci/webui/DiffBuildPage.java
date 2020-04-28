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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Parameters;

/**
 * This page is responsible for displaying a diff of changes between two builds.
 */
public final class DiffBuildPage extends BasePage implements ConversationalTierlet {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(DiffBuildPage.class); // NOPMD
  private static final long serialVersionUID = -194369566029085673L;  // NOPMD

  // content diff panel created here so that input values are picked up
  private final DiffPanel pnlDiff = new DiffPanel("Show changes between two builds", Pages.BUILD_DIFF_TWO, Pages.ADMIN_BUILD_COMMANDS_LIST, true, true);


  /**
   * Constructor
   */
  public DiffBuildPage() {
    super(FLAG_SHOW_QUICK_SEARCH | FLAG_SHOW_PAGE_HEADER_LABEL | FLAG_FLOATING_WIDTH);
    super.setTitle(makeTitle("Changes between two builds"));
    super.baseContentPanel().getUserPanel().add(pnlDiff);
    pnlDiff.setWidth("100%");
  }


  /**
   * Lifecycle callback
   */
  public Result executePage(final Parameters params) {

    // check if build exists, show error message if not
    final BuildConfig buildConfig = ParameterUtils.getActiveBuildConfigFromParameters(params);
    if (buildConfig == null) {
      return WebuiUtils.showBuildNotFound(this);
    }

    // authorise
    if (!super.getUserRights(buildConfig.getActiveBuildID()).isAllowedToViewBuild()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // set title
    super.setTitle(makeTitle("Changes between two builds for \"" + buildConfig.getBuildName() + '\"'));

    // get params
    final Integer startBuildNumber = ParameterUtils.getIntegerParameter(params, Pages.PARAM_BUILD_START_NUMBER, null);
    final Integer endBuildNumber = ParameterUtils.getIntegerParameter(params, Pages.PARAM_BUILD_END_NUMBER, null);

//    if (log.isDebugEnabled()) log.debug("startBuildNumber = " + startBuildNumber);
//    if (log.isDebugEnabled()) log.debug("endBuildNumber = " + endBuildNumber);

    // create query panel
    pnlDiff.setBuildStartNumber(startBuildNumber);
    pnlDiff.setBuildEndNumber(endBuildNumber);

    // run search if params are OK
    if (startBuildNumber != null && endBuildNumber != null
            && startBuildNumber > 0 && endBuildNumber > 0) {
      pnlDiff.display(startBuildNumber, endBuildNumber);
    }

    return Result.Done();
  }
}
