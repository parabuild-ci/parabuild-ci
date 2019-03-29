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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.Version;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.object.User;
import org.parabuild.ci.object.UserProperty;
import org.parabuild.ci.security.BuildRights;
import org.parabuild.ci.security.MergeRights;
import org.parabuild.ci.security.SecurityManager;
import viewtier.ui.Border;
import viewtier.ui.Color;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.TierletContext;
import viewtier.ui.Window;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * This is a base class for all Viewtier-based UI windows
 * (pages).
 */
public abstract class BasePage extends Window {

  private static final long serialVersionUID = 6192631738027871105L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(BasePage.class); // NOPMD
  private static final String DEFAULT_HEADER_TEXT = Version.versionToString(false) + " build " + Version.releaseBuild();


  protected static final byte MENU_SELECTION_BUILDS = 1;
  protected static final byte MENU_SELECTION_AGENTS = 2;
  protected static final byte MENU_SELECTION_MERGES = 3;
  protected static final byte MENU_SELECTION_RESULTS = 4;
  protected static final byte MENU_SELECTION_ADMINISTRATION = 5;
  protected static final byte MENU_SELECTION_ERRORS = 6;
  static final byte MENU_SELECTION_PREFERENCES = 7;
  protected static final byte MENU_SELECTION_DOCUMENTATION = 8;
  public static final byte MENU_SELECTION_SEARCH = 9;
  protected static final byte MENU_SELECTION_LOGINLOGOUT = 10;


  /**
   * When the page created with this flag, an empty header
   * separator will be displayed after the top menue panel.
   *
   * @see #HEADER_DIVIDER_HEIGHT
   * @see PageHeaderPanel
   */
  public static final int FLAG_SHOW_HEADER_SEPARATOR = 1;

  /**
   * When the page created with this flag, a header separator
   * with quick search panel on the right side will be displayed
   * after the top menue panel.
   *
   * @see HeaderDividerPanel
   * @see PageHeaderPanel
   */
  public static final int FLAG_SHOW_QUICK_SEARCH = 2;

  /**
   * When the page created with this flag, a header separator is
   * not shown.
   */
  public static final int FLAG_NO_HEADER_SEPARATOR = 4;

  /**
   * When the page created with this flag, a header separator is
   * not shown.
   */
  private static final int FLAG_NO_HEADER_AND_FOOTER = 8;

  /**
   * When the page created with this flag, a page header label is visible.
   */
  public static final int FLAG_SHOW_PAGE_HEADER_LABEL = 16;

  /**
   * When the page created with this flag, a page content panel is created 100% width. Otherwise it is created centered with {@link Pages#PAGE_WIDTH}.
   */
  public static final int FLAG_FLOATING_WIDTH = 32;

  /**
   * Height of a divider between header and user content.
   */
  public static final int HEADER_DIVIDER_HEIGHT = 25;


  private final MessagePanel pnlBaseContent;
  private final PageHeaderPanel pnlHeader;

  /**
   * @see
   */
  private PageHeaderLabel lbHeaderLabel = null;

  /**
   * Quick search panel is set when the tierlet was created with
   * link SWITCH_SHOW_QUICK_SEARCH switch on.
   */
  private HeaderDividerPanel pnlHeaderDivider = null;

  private boolean loginUsingRememberedUser = true;


  protected BasePage() {
    this(FLAG_SHOW_HEADER_SEPARATOR);
  }


  /**
   * Constructor
   */
  protected BasePage(final int pageFlags) {
    // set content panel properties
    final Panel cp = getContentPanel();
    cp.setWidth("100%");
    cp.setFont(Pages.FONT_COMMON);
    cp.setAlignX(Layout.CENTER);
    final GridIterator giCp = new GridIterator(cp, 3);

    String headerText;
    try {
      final SystemConfigurationManager manager = SystemConfigurationManagerFactory.getManager();
      // set encoding
      final String encoding = manager.getSystemPropertyValue(SystemProperty.OUTPUT_ENCODING, null);
      if (!StringUtils.isBlank(encoding)) {
        setEncoding(encoding);
      }
      // get branding
      final String branding = manager.getSystemPropertyValue(SystemProperty.BRANDING, null);
      headerText = StringUtils.isBlank(branding) ? DEFAULT_HEADER_TEXT : branding;
    } catch (final Exception e) {
      // if any shit hapens, set to default
      headerText = DEFAULT_HEADER_TEXT;
    }


    pnlHeader = new PageHeaderPanel(headerText);

    if (!flagSet(pageFlags, FLAG_NO_HEADER_AND_FOOTER)) {
      // add header
      giCp.add(pnlHeader, 3);
    }

    // add top divider if needed
    if (flagSet(pageFlags, FLAG_SHOW_QUICK_SEARCH)
            || flagSet(pageFlags, FLAG_SHOW_HEADER_SEPARATOR)) {
      // create and add divider using page flags
      pnlHeaderDivider = new HeaderDividerPanel(pageFlags);
      pnlHeaderDivider.setHeight(HEADER_DIVIDER_HEIGHT);
      pnlHeaderDivider.setWidth("100%");
      giCp.add(pnlHeaderDivider, 3);
    }

    // add top divider if needed
    if (flagSet(pageFlags, FLAG_SHOW_PAGE_HEADER_LABEL)) {
      // create and add divider using page flags
      lbHeaderLabel = new PageHeaderLabel();
      lbHeaderLabel.setWidth("100%");
      giCp.add(lbHeaderLabel, 3);
      giCp.add(WebuiUtils.makeHorizontalDivider(5), 3);
    }

    // add a panel to hold implementor's content
    pnlBaseContent = new MessagePanel(false);
    pnlBaseContent.getUserPanel().setWidth("100%");
    if (flagSet(pageFlags, FLAG_FLOATING_WIDTH)) {
      pnlBaseContent.setWidth("100%");
      giCp.add(pnlBaseContent, 3);
    } else {
      pnlBaseContent.setWidth("40%");
//      pnlBaseContent.setWidth(Pages.PAGE_WIDTH);
//      pnlBaseContent.setAlignX(Layout.CENTER);
//      pnlBaseContent.getUserPanel().setAlignX(Layout.CENTER);
      giCp.add(makeFiller());
      giCp.add(pnlBaseContent);
      giCp.add(makeFiller());
    }

    if (!flagSet(pageFlags, FLAG_NO_HEADER_AND_FOOTER)) {
      // add footer
      giCp.add(WebuiUtils.makeHorizontalDivider(30), 3);
      final Component footer = makeFooter();
      footer.setWidth("100%");
      giCp.add(footer, 3);
    }
  }


  private static Panel makeFiller() {
    final Panel pnlLeftContentFiller = new Panel();
    pnlLeftContentFiller.setWidth("30%");
    return pnlLeftContentFiller;
  }


  public static boolean flagSet(final int flags, final int flag) {
    return (flags & flag) != 0;
  }


  /**
   * Overrides Viewtier Window's execute to provide common
   * services available for every page.
   *
   * @param parameters
   */
  public final Result execute(final Parameters parameters) {
    // wrap everything into TCB so that we can report errors.
    try {
      // check if we are not running under root.
      if (ConfigurationManager.BLOCK_ROOT_USER || ConfigurationManager.BLOCK_ADMIN_USER) {
        baseContentPanel().getUserPanel().clear();
        baseContentPanel().showErrorMessage("Build manager was started under root user. This is not allowed. Please restart it under non-root user.");
        return Result.Done();
      }
      final Result result = executePage(parameters);

      // ----------------------------------------------------------
      // TODO: replace with adequate handling - this is a pure hack.
      // try to decide if it was a missed quick search request
      if (pnlHeaderDivider != null && result.isDone()
              && !StringUtils.isBlank(pnlHeaderDivider.getQuery())
              && StringUtils.isBlank(result.getURL())) {
        final String query = pnlHeaderDivider.getQuery();
        return HeaderDividerPanel.makeSearchForwardResult(query);
      }
      // ----------------------------------------------------------

      return result;
    } catch (final RuntimeException e) {
      // report runitme exception
      final Error error = new Error("Unexpected user interface error");
      error.setDetails(e);
      error.setErrorLevel(Error.ERROR_LEVEL_FATAL);
      error.setSubsystemName(Error.ERROR_SUBSYSTEM_WEBUI);
      error.setSendEmail(false);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
      // re-throw
      throw e;
    }
  }


  /**
   * Strategy method to be implemented by classes inheriting
   * BasePage.
   *
   * @param parameters
   * @return result of page execution
   */
  protected abstract Result executePage(Parameters parameters);


  /**
   * Main panel is a panel where all the content live except
   * header and footer.
   */
  public final MessagePanel baseContentPanel() {
    return pnlBaseContent;
  }


  /**
   * Makes branded title by adding a brand prefix to the title
   *
   * @param title
   */
  protected static String makeTitle(final String title) {
    return Version.productName() + " >> " + title;
  }


  /**
   * Sets top menu display mode
   *
   * @param mode
   * @see PageHeaderPanel#MODE_ADMIN
   * @see PageHeaderPanel#MODE_PUBLIC
   * @see PageHeaderPanel#MODE_ANONYMOUS
   */
  private void setTopMenuMode(final int mode, final User user) {
    pnlHeader.setMenuMode(mode, user);
  }


  /**
   * Sets top menu display mode
   *
   * @param mode
   * @see PageHeaderPanel#MODE_ADMIN
   * @see PageHeaderPanel#MODE_PUBLIC
   * @see PageHeaderPanel#MODE_ANONYMOUS
   */
  protected final void setTopMenuMode(final int mode) {
    pnlHeader.setMenuMode(mode);
  }


  /**
   * This factory method composes common header
   *
   * @return header Component
   */
  private static Component makeFooter() {

    // create footer panel
    final Panel footerPanel = new Panel();

    // create menu
    final Flow menuFlow = new Flow();
    menuFlow.setBorder(Border.TOP, 1, Pages.COLOR_PANEL_BORDER);
    menuFlow.setWidth("100%");
    menuFlow.setAlignY(Layout.BOTTOM);
    menuFlow.setAlignX(Layout.LEFT);

    // Add common menu
    menuFlow.add(new FooterLink("Builds", Pages.PUBLIC_BUILDS));
    menuFlow.add(new MenuDividerLabel());
    menuFlow.add(new FooterLink("Results", Pages.RESULT_GROUPS));
    menuFlow.add(new MenuDividerLabel());
    menuFlow.add(new FooterLink("Search", Pages.PUBLIC_SEARCH));
    menuFlow.add(new MenuDividerLabel());
    menuFlow.add(new FooterLink("Support", Pages.PUBLIC_SUPPORT));
    menuFlow.add(new MenuDividerLabel());
    menuFlow.add(new FooterLink("About", Pages.ADMIN_ABOUT));

    footerPanel.add(menuFlow);

    return footerPanel;
  }


  /**
   * Returns true if user logged in and is an administrator
   *
   * @return boolean true if user is a logged in administrator
   */
  protected final boolean isValidAdminUser() {
    final User user = getUser();
    return user != null && user.isAdmin();
  }


  /**
   * Returns true if user logged in and is an administrator
   *
   * @return boolean true if user is a logged in administrator
   */
  protected final boolean isValidUser() {
    return getUser() != null;
  }


  /**
   * @return User logged in or null if there is no one.
   * @see User
   */
  public final User getUser() {
    final HttpServletRequest req = this.getTierletContext().getHttpServletRequest();
    return SecurityManager.getInstance().getUserFromRequest(req);
  }


  /**
   * @param activeBuildID
   * @return RightSet for the given buildID
   */
  public final BuildRights getUserRigths(final int activeBuildID) {
    final SecurityManager sm = SecurityManager.getInstance();
    return sm.getUserBuildRights(getUser(), activeBuildID);
  }


  /**
   * @return ID of a user logged in or User.UNSAVED_ID if there
   *         is no one.
   * @see User#UNSAVED_ID
   */
  protected final int getUserID() {
    final User user = getUser();
    return user == null ? User.UNSAVED_ID : user.getUserID();
  }


  protected final void logoutUser() {
    final HttpSession httpSession = this.getTierletContext().getHttpServletRequest().getSession();
    final User user = (User) httpSession.getAttribute(SecurityManager.SESSION_ATTRIBUTE_USER);
    if (user != null) {
      httpSession.removeAttribute(SecurityManager.SESSION_ATTRIBUTE_USER);
    }
    removeRememberMeCookieAndDigest();
  }


  /**
   * Viewtier callback
   */
  public final void create() {
    if (getUser() == null && loginUsingRememberedUser) {
      tryToLoginFromRememberedUser();
    }
    if (isValidAdminUser()) {
      setTopMenuMode(PageHeaderPanel.MODE_ADMIN, getUser());
    } else if (isValidUser()) {
      setTopMenuMode(PageHeaderPanel.MODE_PUBLIC, getUser());
    } else {
      setTopMenuMode(PageHeaderPanel.MODE_ANONYMOUS, null);
    }
  }


  protected final Result showPageErrorAndExit(final String error) {
    baseContentPanel().getUserPanel().clear();
    baseContentPanel().showErrorMessage(error);
    return Result.Done();
  }


  /**
   * If set to true (default) the base page will try to log in
   * after getting creating using information stored in the
   * {@link WebUIConstants#COOKIE_REMEMBER_ME}.
   *
   * @param loginUsingRememberedUser
   */
  public final void setLoginUsingRememberedUser(final boolean loginUsingRememberedUser) {
    this.loginUsingRememberedUser = loginUsingRememberedUser;
  }


  private void tryToLoginFromRememberedUser() {// try to log in from a remembered user
    final Cookie[] cookies = getTierletContext().getHttpServletRequest().getCookies();
    if (cookies == null) {
      return;
    }
    for (int i = 0; i < cookies.length; i++) {
      final Cookie cookie = cookies[i];
      if (StringUtils.isBlank(cookie.getName())) {
        continue;
      }
      if (cookie.getName().equals(WebUIConstants.COOKIE_REMEMBER_ME)) {
        // get user
        final String digest = cookie.getValue();
        if (StringUtils.isBlank(digest)) {
          break;
        }
        final List propList = SecurityManager.getInstance().findUserPropertiesByValue(UserProperty.REMEMBER_ME, digest);
        if (propList.isEmpty()) {
          break;
        }
        final UserProperty property = (UserProperty) propList.get(0);
        final User user = SecurityManager.getInstance().getUser(property.getUserID());
        // store used in the session
        storeUserInSession(user);
        break; // logged im from the digest stored in the cookie
      }
    }
  }


  protected final void storeUserInSession(final User user) {
    this.getTierletContext().getHttpServletRequest()
            .getSession().setAttribute(SecurityManager.SESSION_ATTRIBUTE_USER, user);
  }


  protected final void setNavigationLinks(final Flow links) {
    pnlHeaderDivider.setNavigationLinks(links);
  }


  /**
   * Removes "Rememer me" cookiet
   */
  protected final void removeRememberMeCookieAndDigest() {
    final Cookie cookie = new Cookie(WebUIConstants.COOKIE_REMEMBER_ME, "");
    cookie.setPath("/");
    cookie.setMaxAge(0);
    getTierletContext().addCookie(cookie);
  }


  /**
   * Makes refresh switch visible of the "visible" paramter is set to true.
   *
   * @param visible
   */
  public final void setRefreshSwitchVisible(final boolean visible) {
    this.pnlHeader.setRefreshSwitchVisible(visible);
  }


  public final void setPageHeader(final String text) {
    if (lbHeaderLabel != null) {
      lbHeaderLabel.setText(text);
    }
  }


  public void setPageHeaderAndTitle(final String title) {
    setPageHeader(title);
    setTitle(title);
  }


  /**
   * Sets page header label's foreground color. If not, the
   * default color {@link
   * Pages#COLOR_PAGE_HEADER_FOREGROUND} is used.
   */
  public final void setPageHeaderForeground(final Color color) {
    if (lbHeaderLabel != null) {
      lbHeaderLabel.setForeground(color);
      lbHeaderLabel.setBorder(Border.BOTTOM, 1, color);
    }
  }


  protected final void refreshUserInSession(final boolean doUpdate) {
    if (doUpdate) {
      storeUserInSession(SecurityManager.getInstance().getUser(getUserID()));
    }
  }


  public MergeRights getMergeUserRigths(final int activeMergeID) {
    final SecurityManager sm = SecurityManager.getInstance();
    return sm.getUserMergeRights(getUser(), activeMergeID);
  }


  /**
   * Marks a particular top level menu selected.
   *
   * @param selection
   */
  protected final void markTopMenuItemSelected(final byte selection) {
    pnlHeader.markTopMenuItemSelected(selection);
  }


  protected final void setDisplayGroupID(final int displayGroupID) {
    pnlHeader.setDisplayGroupID(displayGroupID);
  }


  /**
   * Sets cookie to rememmer selection.
   *
   * @param name
   * @param value
   */
  protected final void setCookie(final String name, final String value) {
    boolean found = false;
    final TierletContext tierletContext = getTierletContext();
    final HttpServletRequest httpServletRequest = tierletContext.getHttpServletRequest();
    final Cookie[] cookies = httpServletRequest.getCookies();
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        final Cookie cookie = cookies[i];
        final String cookieName = cookie.getName();
        final String cookieValue = cookie.getValue();
        if (!StringUtils.isBlank(cookieName)
                && cookieName.equals(name)
                && !StringUtils.isBlank(cookieValue)
                && value.equals(cookieValue)) {
          found = true;
          break;
        }
      }
    }

    if (!found) {
      final Cookie cookie = new Cookie(name, value);
      cookie.setPath("/");
      cookie.setMaxAge(Integer.MAX_VALUE);
      getTierletContext().addCookie(cookie);
    }
  }


  /**
   * Returns status view selection, if any, based on
   * parameters, the session or the cookie.
   */
  protected static String getClientParameter(final Parameters params, final HttpServletRequest request,
                                             final String parameterName, final String sessionParameterName,
                                             final String cookieName) {
    String result = params.getParameterValue(parameterName);
    if (!StringUtils.isBlank(result)) {
      return result;
    }

    result = request.getParameter(parameterName);
    if (!StringUtils.isBlank(result)) {
      return result;
    }

    final HttpSession session = request.getSession();
    result = (String) session.getAttribute(sessionParameterName);
    if (!StringUtils.isBlank(result)) {
      return result;
    }

    final Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        final Cookie cookie = cookies[i];
        if (!StringUtils.isBlank(cookie.getName())
                && cookie.getName().equals(cookieName)
                && !StringUtils.isBlank(cookie.getValue())) {
          result = cookie.getValue();
          break;
        }
      }
    }
    return result;
  }


  public String toString() {
    return "BasePage{" +
            "pnlBaseContent=" + pnlBaseContent +
            ", pnlHeader=" + pnlHeader +
            ", lbHeaderLabel=" + lbHeaderLabel +
            ", pnlHeaderDivider=" + pnlHeaderDivider +
            ", loginUsingRememberedUser=" + loginUsingRememberedUser +
            "} " + super.toString();
  }
}
