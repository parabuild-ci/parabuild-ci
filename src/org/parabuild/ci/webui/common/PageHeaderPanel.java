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
package org.parabuild.ci.webui.common;

import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.User;
import org.parabuild.ci.security.SecurityManager;
import viewtier.ui.Border;
import viewtier.ui.Color;
import viewtier.ui.Flow;
import viewtier.ui.Font;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Link;
import viewtier.ui.Panel;

import javax.servlet.http.HttpSession;
import java.util.Properties;

/**
 * PageHeaderPanel panel represents a top-screen menu. Internally
 * it contains items both for public and administrative modes.
 * Which items are shown is defined by setMode method.
 * <p/>
 * Default mode is PageHeaderPanel.MODE_ANONYMOUS
 *
 * @noinspection FieldCanBeLocal
 * @see #setMenuMode
 */
public final class PageHeaderPanel extends Panel {

  private static final long serialVersionUID = -4898427751636080443L; // NOPMD

  private static final String CAPTION_ABOUT = "About";
  private static final String CAPTION_AGENTS = "AGENTS";
  private static final String CAPTION_ADMINISTRATION = "ADMINISTRATION";
  private static final String CAPTION_BUILDS = "BUILDS";
  private static final String CAPTION_DOCUMENTATION = "DOCUMENTATION";
  private static final String CAPTION_ERRORS = "ERRORS";
  private static final String CAPTION_LOGIN = "Login";
  private static final String CAPTION_MERGES = "MERGES";
  private static final String CAPTION_PREFERENCES = "PREFERENCES";
  private static final String CAPTION_PROJECTS = "PROJECTS";
  private static final String CAPTION_RESULTS = "RESULTS";
  private static final String CAPTION_SEARCH = "SEARCH";
  private static final String CAPTION_SUPPORT = "Support";
  private static final String CAPTION_TURN_REFRESH_OFF = "Turn refresh OFF";
  private static final String CAPTION_TURN_REFRESH_ON = "Turn refresh ON";

  public static final String SESSION_ATTRIBUTE_REFRESH_ENABLED = "refresh.enabled";

  public static final int MODE_PUBLIC = 1;
  public static final int MODE_ADMIN = 2;
  public static final int MODE_ANONYMOUS = 3;

  private final CommonHeaderLink lnkProjects = new CommonHeaderLink(CAPTION_PROJECTS, Pages.PAGE_PROJECTS); // NOPMD SingularField
  private final CommonHeaderLink lnkBuilds = new CommonHeaderLink(CAPTION_BUILDS, Pages.PUBLIC_BUILDS); // NOPMD SingularField
  private final CommonHeaderLink lnkAgents = new CommonHeaderLink(CAPTION_AGENTS, Pages.PAGE_AGENTS); // NOPMD SingularField
  private final CommonHeaderLink lnkResults = new CommonHeaderLink(CAPTION_RESULTS, Pages.RESULT_GROUPS); // NOPMD SingularField
  private final CommonHeaderLink lnkMerges = new CommonHeaderLink(CAPTION_MERGES, Pages.PAGE_MERGE_LIST); // NOPMD SingularField
  private final CommonHeaderLink lnkAdministration = new CommonHeaderLink(CAPTION_ADMINISTRATION, Pages.ADMIN_SYSTEM_CONFIG_LINKS); // NOPMD SingularField
  private final CommonHeaderLink lnkErrors = new CommonHeaderLink(CAPTION_ERRORS, Pages.ADMIN_ERROR_LIST); // NOPMD SingularField
  private final CommonHeaderLink lnkDocs = new CommonHeaderLink(CAPTION_DOCUMENTATION, Pages.ADMIN_DOCS); // NOPMD SingularField
  private final CommonHeaderLink lnkLoginLogout = new CommonHeaderLink(CAPTION_LOGIN, Pages.PUBLIC_LOGIN); // NOPMD SingularField
  private final CommonHeaderLink lnkPreferences = new CommonHeaderLink(CAPTION_PREFERENCES, Pages.PUBLIC_PREFERENCES); // NOPMD SingularField
  private final CommonHeaderSmallLink lnkRefreshOnOff = new CommonHeaderSmallLink(CAPTION_TURN_REFRESH_OFF, Pages.PUBLIC_BUILDS); // NOPMD SingularField
  private final CommonHeaderLink lnkSearch = new CommonHeaderLink(CAPTION_SEARCH, Pages.PUBLIC_SEARCH);

  private final Label lbBuildsDivider = new PageHeaderMenuDividerLabel();  // NOPMD SingularField
  private final Label lbAgentsDivider = new PageHeaderMenuDividerLabel();  // NOPMD SingularField
  private final Label lbResultsDivider = new PageHeaderMenuDividerLabel();  // NOPMD SingularField
  private final Label lbConfigDivider = new PageHeaderMenuDividerLabel(); // NOPMD SingularField
  private final Label lbRefreshOnOffDivider = new MenuDividerLabel(); // NOPMD SingularField
  private final Label lbErrorsDivider = new PageHeaderMenuDividerLabel(); // NOPMD SingularField
  private final Label lbDocsDivider = new PageHeaderMenuDividerLabel(); // NOPMD SingularField
  private final Label lbPreferencesDivider = new PageHeaderMenuDividerLabel(); // NOPMD SingularField
  private final Label lbSearchDivider = new PageHeaderMenuDividerLabel(); // NOPMD SingularField
  private final Label lbMergeDivider = new PageHeaderMenuDividerLabel(); // NOPMD SingularField

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private final Panel pnlMenuHolder = new Panel(); // NOPMD


  public PageHeaderPanel(final String headerText) {

    // Self

    setForeground(Pages.COLOR_HEADER_FOREGROUND);
    setBackground(Pages.COLOR_HEADER_BACKGROUND);
    setWidth("100%");

    final Layout layout = new Layout(0, 0, 1, 1);

    // Add common Parabuild header label

    final Label headerLabel = new BoldCommonLabel(headerText);
    headerLabel.setFont(Pages.FONT_HEADER_LABEL);
    headerLabel.setHeight(Pages.HEADER_HEIGHT);
    headerLabel.setAlignY(Layout.CENTER);
    headerLabel.setBorder(Border.BOTTOM, 1, Pages.COLOR_HEADER_FOREGROUND);
    add(headerLabel, layout);

    // Top right menu that holds "About", "Support" and "EAP" menu

    final Link lnkSupport = new CommonHeaderSmallLink(CAPTION_SUPPORT, Pages.PUBLIC_SUPPORT);
    final Link lnkAbout = new CommonHeaderSmallLink(CAPTION_ABOUT, Pages.ADMIN_ABOUT);
    final Flow flwTopRightMenu = new Flow();
    flwTopRightMenu.setAlignX(Layout.RIGHT);
    flwTopRightMenu.setAlignY(Layout.TOP);
    flwTopRightMenu.setBorder(Border.BOTTOM, 1, Pages.COLOR_HEADER_FOREGROUND);
    flwTopRightMenu.add(lnkRefreshOnOff).add(lbRefreshOnOffDivider).add(lnkSupport).add(new MenuDividerLabel()).add(lnkAbout);

    layout.positionX++;
    add(flwTopRightMenu, layout);

    // Menu

    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();

    final Flow menuFlow = new Flow();
    menuFlow.setAlignY(Layout.TOP);
    menuFlow.setHeight(15);
    menuFlow.setBackground(Pages.COLOR_HEADER_MENU_BACKGROUND);
    menuFlow.add(new Label(" "));
    if (SystemConfigurationManagerFactory.getManager().isProjectDisplayEnabled()) {
      menuFlow.add(lnkProjects);
      menuFlow.add(lbBuildsDivider);
    }
    menuFlow.add(lnkBuilds);
    if (scm.isShowingAgentsEnabled()) {
      menuFlow.add(lbAgentsDivider);
      menuFlow.add(lnkAgents);
    }
    if (scm.isShowingMergesEnabled()) {
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      // REVIEWME: vimeshev - 2007-06-02 - cm.findBuildConfigsByVCS can be slow.
      if (!cm.findBuildConfigsByVCS(VersionControlSystem.SCM_PERFORCE).isEmpty()) {
        menuFlow.add(lbMergeDivider);
        menuFlow.add(lnkMerges);
      }
    }
    menuFlow.add(lbResultsDivider).add(lnkResults);
    menuFlow.add(lbConfigDivider).add(lnkAdministration);
    menuFlow.add(lbErrorsDivider).add(lnkErrors);
    menuFlow.add(lbPreferencesDivider).add(lnkPreferences);
    menuFlow.add(lbDocsDivider).add(lnkDocs);
    menuFlow.add(lbSearchDivider).add(lnkSearch);

//    lnkProjects.setBorder(Border.RIGHT, 1, Color.White);
//    lnkBuilds.setBorder(Border.RIGHT, 1, Color.White);
//    lnkResults.setBorder(Border.RIGHT, 1, Color.White);
//    lnkConfig.setBorder(Border.RIGHT, 1, Color.White);
//    lnkErrors.setBorder(Border.RIGHT, 1, Color.White);
//    lnkPreferences.setBorder(Border.RIGHT, 1, Color.White);
//    lnkDocs.setBorder(Border.RIGHT, 1, Color.White);
//    lnkSearch.setBorder(Border.RIGHT, 1, Color.White);
//
//    pnlMenuHolder.setPadding(0);
//    pnlMenuHolder.setHeight(15);
//    pnlMenuHolder.setBackground(Pages.COLOR_HEADER_MENU_BACKGROUND);
//    final GridIterator gi = new GridIterator(pnlMenuHolder, 8);
//    gi.add(lnkProjects);
//    gi.add(lnkBuilds);
//    gi.add(lnkResults);
//    gi.add(lnkConfig);
//    gi.add(lnkErrors);
//    gi.add(lnkPreferences);
//    gi.add(lnkDocs);
//    gi.add(lnkSearch);


    layout.positionY++;
    layout.positionX = 0;
    layout.spanX = 1;
    add(menuFlow, layout);
//    add(pnlMenuHolder, layout);

    lnkLoginLogout.setAlignX(Layout.RIGHT);
    lnkLoginLogout.setBackground(Pages.COLOR_HEADER_MENU_BACKGROUND);
    layout.positionX++;
    add(lnkLoginLogout, layout);

    // set def mode
    setMenuMode(MODE_ANONYMOUS);

    // hide refresh by default
    lnkRefreshOnOff.setVisible(false);
    lbRefreshOnOffDivider.setVisible(false);
  }


  /**
   * Sets menu mode with no user.
   */
  public final void setMenuMode(final int mode) {
    this.setMenuMode(mode, null);
  }


  /**
   * If true shows refresh switch.
   *
   * @param visible
   */
  public void setRefreshSwitchVisible(final boolean visible) {
    lnkRefreshOnOff.setVisible(visible);
    lbRefreshOnOffDivider.setVisible(visible);

    // detect caption status for the switch
    if (lnkRefreshOnOff.isVisible()) {
      final HttpSession session = getTierletContext().getHttpServletRequest().getSession();
      final Object refreshEnabled = session.getAttribute(SESSION_ATTRIBUTE_REFRESH_ENABLED);
      final Properties properties = new Properties();
      if (refreshEnabled != null && refreshEnabled.equals(Boolean.FALSE)) {
        properties.setProperty(Pages.PARAM_ENABLE_REFRESH, Boolean.TRUE.toString());
        lnkRefreshOnOff.setText(CAPTION_TURN_REFRESH_ON);
      } else {
        properties.setProperty(Pages.PARAM_ENABLE_REFRESH, Boolean.FALSE.toString());
        lnkRefreshOnOff.setText(CAPTION_TURN_REFRESH_OFF);
      }
      lnkRefreshOnOff.setParameters(properties);
    }
  }


  /**
   * Sets menu mode with given user.
   */
  public final void setMenuMode(final int mode, final User user) {
    switch (mode) {
      case MODE_PUBLIC:
        // validate user
        validateUserNotNull(user);
        // show/hide
        lnkBuilds.setUrl(Pages.PUBLIC_BUILDS);
        lbPreferencesDivider.setVisible(true);
        lnkPreferences.setVisible(true);
        lbConfigDivider.setVisible(false);
        lnkAdministration.setVisible(false);
        // Check if we should show Errors link
        final SecurityManager sm = SecurityManager.getInstance();
        if (sm.isAllowedToSeeErrors(user)) {
          lbErrorsDivider.setVisible(true);
          lnkErrors.setVisible(true);
        } else {
          lbErrorsDivider.setVisible(false);
          lnkErrors.setVisible(false);
        }
        lnkLoginLogout.setText(makeLogoutCaption(user));
        lnkLoginLogout.setUrl(Pages.PUBLIC_LOGOUT);
        break;
      case MODE_ADMIN:
        // validate user
        validateUserNotNull(user);
        // show/hide
        lnkBuilds.setUrl(Pages.ADMIN_BUILDS);
        lbPreferencesDivider.setVisible(true);
        lnkPreferences.setVisible(true);
        lbConfigDivider.setVisible(true);
        lnkAdministration.setVisible(true);
        lbErrorsDivider.setVisible(true);
        lnkErrors.setVisible(true);
        lnkLoginLogout.setText(makeLogoutCaption(user));
        lnkLoginLogout.setUrl(Pages.PUBLIC_LOGOUT);
        break;
      case MODE_ANONYMOUS:
        lnkBuilds.setUrl(Pages.PUBLIC_BUILDS);
        lbPreferencesDivider.setVisible(false);
        lnkPreferences.setVisible(false);
        lbConfigDivider.setVisible(false);
        lnkAdministration.setVisible(false);
        lbErrorsDivider.setVisible(false);
        lnkErrors.setVisible(false);
        lnkLoginLogout.setText(CAPTION_LOGIN);
        lnkLoginLogout.setUrl(Pages.PUBLIC_LOGIN);
        break;
      default:
        break;
    }
  }


  /**
   * Sets display group ID. So that
   *
   * @param displayGroupID
   */
  public final void setDisplayGroupID(final int displayGroupID) {
    lnkProjects.setDisplayGroupID(displayGroupID);
    lnkBuilds.setDisplayGroupID(displayGroupID);
    lnkAgents.setDisplayGroupID(displayGroupID);
    lnkResults.setDisplayGroupID(displayGroupID);
    lnkMerges.setDisplayGroupID(displayGroupID);
    lnkAdministration.setDisplayGroupID(displayGroupID);
    lnkErrors.setDisplayGroupID(displayGroupID);
    lnkDocs.setDisplayGroupID(displayGroupID);
    lnkLoginLogout.setDisplayGroupID(displayGroupID);
    lnkPreferences.setDisplayGroupID(displayGroupID);
    lnkSearch.setDisplayGroupID(displayGroupID);
  }


  /**
   * Helper method to validate if given user is not null for
   * modes requiring valid user.
   *
   * @param user
   */
  private static void validateUserNotNull(final User user) {
    if (user == null) throw new IllegalArgumentException("User can not be null");
  }


  /**
   * Creates logout link caption.
   */
  private static String makeLogoutCaption(final User user) {
    return "Logout " + (user == null ? "" : user.getName());
  }


  /**
   * Marks a top nav menu item selected.
   *
   * @param selection
   */
  public void markTopMenuItemSelected(final byte selection) {
    if (true) {
      return;
    }
    final Color selectionColor = Pages.COLOR_HEADER_BACKGROUND;
    switch (selection) {
      case BasePage.MENU_SELECTION_ADMINISTRATION:
        lnkAdministration.setBackground(selectionColor);
        break;
      case BasePage.MENU_SELECTION_AGENTS:
        lnkAgents.setBackground(selectionColor);
        break;
      case BasePage.MENU_SELECTION_BUILDS:
        lnkBuilds.setBackground(selectionColor);
        break;
      case BasePage.MENU_SELECTION_DOCUMENTATION:
        lnkDocs.setBackground(selectionColor);
        break;
      case BasePage.MENU_SELECTION_ERRORS:
        lnkErrors.setBackground(selectionColor);
        break;
      case BasePage.MENU_SELECTION_LOGINLOGOUT:
        lnkLoginLogout.setBackground(selectionColor);
        break;
      case BasePage.MENU_SELECTION_MERGES:
        lnkMerges.setBackground(selectionColor);
        break;
      case BasePage.MENU_SELECTION_PREFERENCES:
        lnkPreferences.setBackground(selectionColor);
        break;
      case BasePage.MENU_SELECTION_RESULTS:
        lnkResults.setBackground(selectionColor);
        break;
      case BasePage.MENU_SELECTION_SEARCH:
        lnkSearch.setBackground(selectionColor);
        break;
    }
  }


  private static final class PageHeaderMenuDividerLabel extends Label {

    private static final Font FONT_DIVIDER = new Font(Font.SansSerif, Font.Plain, 16);
    private static final long serialVersionUID = 5514441726876361065L;


    /**
     * Constructor. Creates menu divider label with hard divider.
     */
    public PageHeaderMenuDividerLabel() {
      super("&nbsp;&nbsp;&nbsp;&nbsp;");
      setFont(FONT_DIVIDER);
    }
  }
}
