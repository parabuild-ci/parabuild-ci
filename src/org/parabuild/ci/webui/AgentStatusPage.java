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

import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.ConfigurationFile;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import org.parabuild.ci.Version;
import viewtier.ui.Border;
import viewtier.ui.Color;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;
import viewtier.ui.Window;

/**
 * This special page shows remote agent status.
 * <p/>
 * It does not extend BasePage because it should not access any
 * services.
 */
public final class AgentStatusPage extends Window implements StatelessTierlet {

  private static final long serialVersionUID = 159938320034016386L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(AgentStatusPage.class); // NOPMD

  public static final String CAPTION_SERVICING_REQUESTS = "Servicing requests coming from the build manager at: ";
  public static final String CAPTION_REMOTE_BUILD_RUNNER = "Remote agent";
  private static final String CAPTION_REMOTE_BUILDER_VERSION = "Remote agent version: ";

  // NOTE: simeshev@parabuilci.org - 07/25/2004 - init once at class load,
  // it's not going to change throughout the life of the JVM
  private static final String BUILD_MANAGER_ADDRESS = ConfigurationFile.getInstance().getBuildManagerAddress();
  private static final boolean BUILDER_MODE = !StringUtils.isBlank(BUILD_MANAGER_ADDRESS);


  public Result execute(final Parameters params) {

    setTitle(Version.versionToString(true) + " >> Remote Agent >> Servicing Requests From " + BUILD_MANAGER_ADDRESS);
    if (BUILDER_MODE) {
      // REVIEWME: consider making this label a class, otherwise
      // it duplicates the same in PageHeaderPanel.
      final Label headerLabel = new BoldCommonLabel(CAPTION_REMOTE_BUILD_RUNNER);
      headerLabel.setFont(Pages.FONT_HEADER_LABEL);
      headerLabel.setHeight(Pages.HEADER_HEIGHT);
      headerLabel.setAlignY(Layout.CENTER);
      headerLabel.setBorder(Border.BOTTOM, 1, Pages.COLOR_HEADER_FOREGROUND);
      headerLabel.setBackground(Pages.COLOR_HEADER_BACKGROUND);
      headerLabel.setForeground(Pages.COLOR_HEADER_FOREGROUND);

      // specific to this page
      headerLabel.setWidth(Pages.PAGE_WIDTH);
      getContentPanel().add(headerLabel);

      // add labels to show attached IP
      if (ConfigurationManager.BLOCK_ADMIN_USER || ConfigurationManager.BLOCK_ROOT_USER) {
        final BoldCommonLabel lbError = new BoldCommonLabel("This remote agent was started under root user. This is not allowed. The agent was disabled. Please restart it under non-root user.");
        lbError.setForeground(Color.DarkRed);
        getContentPanel().add(WebuiUtils.makeHorizontalDivider(Pages.PANEL_DIVIDER));
        getContentPanel().add(lbError);
      } else {
        final CommonLabel lbAddress = new CommonLabel(CAPTION_SERVICING_REQUESTS);
        lbAddress.setFont(Pages.FONT_COMMON);
        getContentPanel().add(WebuiUtils.makeHorizontalDivider(Pages.PANEL_DIVIDER));
        getContentPanel().add(new Flow().add(lbAddress).add(new BoldCommonLabel(BUILD_MANAGER_ADDRESS)));
      }
      final CommonLabel lbVersion = new CommonLabel(CAPTION_REMOTE_BUILDER_VERSION + Version.versionToString(true));
      lbVersion.setFont(Pages.FONT_COMMON);
      getContentPanel().add(lbVersion);
      return Result.Done();
    } else {
      // we are running in a normal build manager mode, redirect to index page
      return Result.Done(Pages.PUBLIC_BUILDS);
    }
  }
}