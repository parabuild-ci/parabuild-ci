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
import org.parabuild.ci.common.FatalConfigurationException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.object.User;
import org.parabuild.ci.object.UserProperty;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ReturnTierletAttribuite;
import org.parabuild.ci.webui.common.WebUIConstants;
import viewtier.ui.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Random;

/**
 * Login page.
 */
public final class LoginPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = 5063413612846324313L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(LoginPage.class); // NOPMD

  private final LoginPanel loginPanel = new LoginPanel();


  /**
   * Reqired default constructor
   */
  public LoginPage() {
    super.markTopMenuItemSelected(MENU_SELECTION_LOGINLOGOUT);
    setTitle(makeTitle("Login page"));
    setFocusOnFirstInput(true);
    final Panel cp = baseContentPanel().getUserPanel();

    // left side
    final Layout layout = new Layout(0, 0, 1, 1);
    final Label lbLeftSide = new Label();
    lbLeftSide.setWidth(250);
    cp.add(lbLeftSide, layout);

    // panel
    layout.positionX++;
    cp.add(loginPanel, layout);

    // right side
    layout.positionX++;
    final Label lbRightSide = new Label();
    lbRightSide.setWidth(250);
    cp.add(lbRightSide, layout);
  }


  /**
   * Tierlet lifecycle method
   *
   * @param parameters
   */
  public Tierlet.Result executePage(final Parameters parameters) {
    loginPanel.clearMessage();

    // first call ?
    if (this.isNew()) {
      // first try to handle case if login paramters are provoded
      if (parameters.isParameterPresent(LoginPanel.FIELD_NAME_LOGIN_NAME)
              && parameters.isParameterPresent(LoginPanel.FIELD_NAME_LOGIN_PASSWORD)) {
        return doLogin(parameters.getParameterValue(LoginPanel.FIELD_NAME_LOGIN_NAME),
                parameters.getParameterValue(LoginPanel.FIELD_NAME_LOGIN_PASSWORD));
      } else {
        loadDefaults();
        return ConversationalTierlet.Result.Continue(); // let user enter login information
      }
    }

    // Check if user is already logged in
    final User user = SecurityManager.getInstance().getUserFromRequest(super.getTierletContext().getHttpServletRequest());
    if (user != null) return makeResultForLoggedInUser(user);

    // Check if user is valid
    return doLogin(loginPanel.getName(), loginPanel.getPassword());
  }


  /**
   * Tries to logs in the user with given user name and password.
   *
   * @param name     user name
   * @param password user password
   * @return result - either a forward to build list page or
   *         error message and login page again.
   */
  private Result doLogin(final String name, final String password) {

    try {
      final User user = SecurityManager.getInstance().login(name, password);
      if (user != null) {
        // save the name for the last logged user
        storeUserInSession(user);
        setLastLoggedNameCookie(user.getName());

        // "rememeber me" if asked
        if (loginPanel.isRememberMeSelected()) {
          setRememberMeCookieAndStoreDigest(user.getUserID());
        } else {
          removeRememberMeCookieAndDigest();
        }

        // has return value stored?
        final HttpSession session = this.getTierletContext().getHttpServletRequest().getSession();
        final ReturnTierletAttribuite returnAttribuite = (ReturnTierletAttribuite) session.getAttribute(Pages.ATTRIBUTE_RETURN_TIERLET);
        if (returnAttribuite != null) {
          // there is return path
          session.removeAttribute(Pages.ATTRIBUTE_RETURN_TIERLET);
          return Result.Done(returnAttribuite.getTierlet(), returnAttribuite.getParameters());
        } else {
          // return home
          return makeResultForLoggedInUser(user);
        }
      } else {
        loginPanel.showErrorMessage("Error logging in - invalid name or password. Enter correct name and password and press \"Login\" button.");
        loginPanel.setPassword("");
        return Result.Continue();
      }
    } catch (final FatalConfigurationException e) {
      loginPanel.showErrorMessage("Critical error while logging in: " + StringUtils.toString(e));
      loginPanel.setPassword("");
      return Result.Done();
    }
  }


  private void setRememberMeCookieAndStoreDigest(final int userID) {
    try {
      // create digest
      final StringBuilder stringToDigest = new StringBuilder(100);
      final Random random = new Random(System.currentTimeMillis());
      stringToDigest.append(random.nextInt());
      stringToDigest.append(random.nextInt());
      stringToDigest.append(random.nextInt());
      final String digest = StringUtils.digest(stringToDigest.toString());

      // save cookie
      final Cookie cookie = new Cookie(WebUIConstants.COOKIE_REMEMBER_ME, digest);
      cookie.setPath("/");
      getTierletContext().addCookie(cookie, WebUIConstants.TWO_WEEKS_IN_SECONDS);

      // save gigest
      UserProperty userProperty = SecurityManager.getInstance().getUserProperty(userID, UserProperty.REMEMBER_ME);
      if (userProperty == null) {
        userProperty = new UserProperty(userID, UserProperty.REMEMBER_ME, digest);
      } else {
        userProperty.setValue(digest);
      }
      SecurityManager.getInstance().saveUserProperty(userProperty);

    } catch (final Exception e) {
      // NOTE: vimeshev - 2006-05-07 - catching all for
      // so that we don't affect the log in proceess.
      log.warn("Error while saving \"rememeber me\" token", e);
    }
  }


  /**
   * @param user
   */
  private static Result makeResultForLoggedInUser(final User user) {
    final String defauildDisplayGroup = SecurityManager.getInstance().getUserPropertyValue(user.getUserID(), UserProperty.DEFAULT_DISPLAY_GROUP);
    final Parameters parameters = new Parameters();
    parameters.addParameter(Pages.PARAM_DISPLAY_GROUP_ID, StringUtils.isValidInteger(defauildDisplayGroup) ? Integer.parseInt(defauildDisplayGroup) : DisplayGroup.DISPLAY_GROUP_ID_ALL);
    if (user.isAdmin()) {
      return Result.Done(Pages.ADMIN_BUILDS, parameters);
    } else {
      return Result.Done(Pages.PUBLIC_BUILDS, parameters);
    }
  }


  /**
   * Loads last logged name into the "Name" field of the login
   * panel.
   */
  private void loadDefaults() {
//    if (log.isDebugEnabled()) log.debug("Loading defaults");

    // try to load last logged name
    final HttpServletRequest httpRequest = this.getTierletContext().getHttpServletRequest();
    final Cookie[] cookies = httpRequest.getCookies();
//    if (log.isDebugEnabled()) log.debug("cookies.size(): " + cookies.length);
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        final Cookie c = cookies[i];
//        if (log.isDebugEnabled()) log.debug("theCookie.getName(): " + c.getName());
        if (c.getName().equals(WebUIConstants.COOKIE_LAST_LOGGED_NAME)) {
          if (c.getValue() != null) loginPanel.setName(c.getValue().trim());
          break;
        }
      }
    }

    // check if there is only default admin user w/default password
    if (SecurityManager.getInstance().onlyDefaultAdminExists()) {
      loginPanel.setName(User.DEFAULT_ADMIN_USER);
      loginPanel.setPassword(User.DEFAULT_ADMIN_PASSW);
    }

    if (StringUtils.isBlank(loginPanel.getName())) loginPanel.setName(User.DEFAULT_ADMIN_USER);
  }


  /**
   * Saves last logged name
   */
  private void setLastLoggedNameCookie(final String name) {
    if (StringUtils.isBlank(name)) return;
    final Cookie cookie = new Cookie(WebUIConstants.COOKIE_LAST_LOGGED_NAME, name);
    cookie.setPath("/");
    getTierletContext().addCookie(cookie, WebUIConstants.YEAR_IN_SECONDS);
  }
}
