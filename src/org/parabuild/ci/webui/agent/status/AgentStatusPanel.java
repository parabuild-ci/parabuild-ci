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
package org.parabuild.ci.webui.agent.status;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebUIConstants;
import viewtier.ui.Border;
import viewtier.ui.Color;
import viewtier.ui.Image;
import viewtier.ui.Panel;

/**
 * AgentStatusPanel
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 24, 2009 3:08:21 PM
 */
final class AgentStatusPanel extends Panel {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration,unused
   */
  private static final Log LOG = LogFactory.getLog(AgentStatusPanel.class); // NOPMD
  private static final long serialVersionUID = -3531504395853983857L;


  AgentStatusPanel(final AgentStatus status) {

//    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
//    final int imageHeightPixels = scm.getAgentStatusImageHeightPixels();
//    final int imageWidthPixels = scm.getAgentStatusImageWidthPixels();
    final ImmutableImage image = status.getChart();
    final int width = image.getWidth();
    final int height = image.getHeight();
    setWidth(width + 10);
    setPadding(5);
    setBorder(Border.ALL, 3, Color.White);

    //noinspection ThisEscapedInObjectConstruction
    final GridIterator gi = new GridIterator(this, 2);

    // Create imgThrobber
    final Image imgThrobber = createThrobber(status);
    imgThrobber.setBackground(Color.LightYellow);

    // Create host name label
    final CommonLabel lbHostName = new CommonLabel(status.getHostName());
    lbHostName.setBackground(Color.LightYellow);
    lbHostName.setForeground(Pages.COLOR_PAGE_SECTION_FOREGROUND);
    lbHostName.setFont(Pages.FONT_HEADER_LABEL);

    // Create status chart
    final Image statusChart = new Image("/parabuild/agent/status/chart?"
            + Pages.PARAM_AGENT_ID + '=' + status.getAgentID(),
            "Status for Build Agent " + status.getHostName(), width, height);

    // Panel settings
    this.setBackground(Pages.COLOR_PANEL_HEADER_BG);

    imgThrobber.setWidth(35);
    imgThrobber.setHeight(35);
    imgThrobber.setPadding(5);

    lbHostName.setHeight(35);
    lbHostName.setPadding(5);

    statusChart.setPadding(5);

    // layout
    gi.add(imgThrobber);
    gi.add(lbHostName);
    gi.add(statusChart, 2);
  }


  private Image createThrobber(final AgentStatus status) {
    final String throbberURL;
    final String throbberCaption;
    final byte activityType = status.getActivityType();
    switch (activityType) {
      case AgentStatus.ACTIVITY_BUSY:
        throbberURL = WebUIConstants.IMAGE_GREEN_THROBBER_GIF;
        throbberCaption = "Busy";
        break;
      case AgentStatus.ACTIVITY_DISABLED:
        throbberURL = WebUIConstants.IMAGE_3232_BULLET_BALL_GLASS_GRAY_GIF;
        throbberCaption = "Disabled";
        break;
      case AgentStatus.ACTIVITY_IDLE:
        throbberURL = WebUIConstants.IMAGE_3232_BULLET_BALL_GLASS_GREEN_GIF;
        throbberCaption = "Idle";
        break;
      case AgentStatus.ACTIVITY_OFFLINE:
        throbberURL = WebUIConstants.IMAGE_3232_BULLET_BALL_GLASS_RED_GIF;
        throbberCaption = "Offline";
        break;
      default:
        throbberURL = WebUIConstants.IMAGE_3232_BULLET_BALL_GLASS_BLUE_GIF;
        throbberCaption = "Unknown";
        break;
    }
    return new Image(throbberURL, throbberCaption, 16, 16);
  }
}
