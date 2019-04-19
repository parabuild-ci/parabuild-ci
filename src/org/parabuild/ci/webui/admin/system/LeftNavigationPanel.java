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
package org.parabuild.ci.webui.admin.system;

import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Border;
import viewtier.ui.Color;
import viewtier.ui.Font;
import viewtier.ui.Label;
import viewtier.ui.Panel;

import java.util.Properties;

/**
 * LeftNavigationPanel holds section headers and links to the particular system configuration pages.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Sep 27, 2008 3:00:48 PM
 */
final class LeftNavigationPanel extends Panel {

  // Captions

  private static final String CAPTION_INFRASTRUCTURE = "Infrastructure";
  private static final String CAPTION_STABILITY = "Stability Settings";
  private static final String CAPTION_BUILDERS = "Build Farms";
  private static final String CAPTION_DISPLAY_GROUPS = "Display Groups";
  private static final String CAPTION_EMAIL = "E-mail Settings";
  private static final String CAPTION_GENERAL_SECURITY_SETTINGS = "General Configuration";
  private static final String CAPTION_GROUPS = "Groups";
  private static final String CAPTION_JABBER = "Instant Messaging Settings";
  private static final String CAPTION_LDAP_ACCESS = "LDAP Configuration";
  private static final String CAPTION_NOTIFICATION = "Notification Settings";
  private static final String CAPTION_PROJECTS = "Projects";
  private static final String CAPTION_BUILDER_AGENTS = "Build Agents";
  private static final String CAPTION_SECURITY = "Security";
  private static final String CAPTION_SYSTEM = "General System Settings";
  private static final String CAPTION_GLOBAL_VARIABLES = "Global Variables";
  private static final String CAPTION_USER_INTERFACE = "User Interface Settings";
  private static final String CAPTION_USER_MANAGEMENT = "User Management";
  private static final String CAPTION_USERS = "Users";
  private static final String CAPTION_VCS_USER_TO_EMAIL_MAP = "VCS User to Email Map";

  // Colors

  private static final Color BORDER_COLOR = Color.LightGray;
  private static final Color SECTION_HEADER_BACKGROUND_COLOR = Color.LightGray;
  private static final Color SECTION_HEADER_FOREGROUND_COLOR = Color.Black;
  private static final Font SECTION_HEADER_FONT = Pages.FONT_COMMON_BOLD_LABEL;
  private static final long serialVersionUID = -782299345700648594L;


  LeftNavigationPanel() {
    setBorder(Border.ALL, 1, BORDER_COLOR);

    // Notification
    addSectionHeader(CAPTION_NOTIFICATION);
    addSection(CAPTION_EMAIL, Pages.ADMIN_EMAIL_CONFIGURATION);
    addSection(CAPTION_VCS_USER_TO_EMAIL_MAP, Pages.ADMIN_EMAIL_GLOBAL_VCS_USER_MAP);
    addSection(CAPTION_JABBER, Pages.ADMIN_INSTANT_MESSAGING_CONFIG);

    // General system settings
    addSectionHeader(CAPTION_SYSTEM);
    addSection(CAPTION_USER_INTERFACE, Pages.ADMIN_APPEARANCE_CONFIGURATION);
    addSection(CAPTION_STABILITY, Pages.ADMIN_STABILITY_CONFIGURATION);
    addSection(CAPTION_DISPLAY_GROUPS, Pages.ADMIN_DISPLAY_GROUPS);
    addSection(CAPTION_PROJECTS, Pages.PAGE_PROJECTS);
    addSection(CAPTION_GLOBAL_VARIABLES, Pages.PAGE_VARIABLE_LIST, createVariableListParameters());
//    addSection(CAPTION_PROMOTION_POLICIES, Pages.PAGE_PROMOTION_POLICY_LIST);

    // Build
    addSectionHeader(CAPTION_INFRASTRUCTURE);
    addSection(CAPTION_BUILDER_AGENTS, Pages.PAGE_AGENT_LIST);
    addSection(CAPTION_BUILDERS, Pages.ADMIN_BUILDERS);

    // User management
    addSectionHeader(CAPTION_USER_MANAGEMENT);
    addSection(CAPTION_USERS, Pages.ADMIN_USERS);
    addSection(CAPTION_GROUPS, Pages.ADMIN_GROUPS);

    // Security
    addSectionHeader(CAPTION_SECURITY);
    addSection(CAPTION_GENERAL_SECURITY_SETTINGS, Pages.ADMIN_SECURITY_CONFIGURATION);
    addSection(CAPTION_LDAP_ACCESS, Pages.ADMIN_LDAP_CONFIG);
  }


  /**
   * Adds section header.
   *
   * @param header to add
   */
  private void addSectionHeader(final String header) {
    final CommonLabel lbHeader = new CommonLabel(header);
    lbHeader.setBackground(SECTION_HEADER_BACKGROUND_COLOR);
    lbHeader.setForeground(SECTION_HEADER_FOREGROUND_COLOR);
    lbHeader.setFont(SECTION_HEADER_FONT);
    lbHeader.setHeight(25);
    add(lbHeader);
  }


  /**
   * Adds section.
   *
   * @param caption section caption.
   * @param pageURL section page URL
   */
  private void addSection(final String caption, final String pageURL) {
    final Properties parameters = SystemConfigurationPageParameter.createPreviewParameters();
    addSection(caption, pageURL, parameters);
  }


  private void addSection(final String caption, final String pageURL, final Properties parameters) {
    final CommonLink lnkSection = new CommonLink(caption, pageURL, parameters);
    final CommonFlow flwSection = new CommonFlow(WebuiUtils.makeBlueBulletSquareImage16x16(), new Label(), lnkSection);
    flwSection.setHeight(25);
    add(flwSection);
  }


  private static Properties createVariableListParameters() {
    final Properties properties = new Properties();
    properties.setProperty(Pages.PARAM_VARIABLE_TYPE, Integer.toString(StartParameter.TYPE_SYSTEM));
    properties.setProperty(Pages.PARAM_VARIABLE_OWNER, Integer.toString(-1));
    return properties;
  }
}
