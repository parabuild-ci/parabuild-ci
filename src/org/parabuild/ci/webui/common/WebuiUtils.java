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
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.MailUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.DisplayGroupManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.object.User;
import org.parabuild.ci.object.UserProperty;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.ChangeURLFactory;
import org.parabuild.ci.webui.FisheyeChangeURLFactory;
import org.parabuild.ci.webui.GithubChangeURLFactory;
import org.parabuild.ci.webui.P4WebChangeURLFactory;
import org.parabuild.ci.webui.ViewCVSChangeURLFactory;
import org.parabuild.ci.webui.ViewSVNChangeURLFactory;
import org.parabuild.ci.webui.WebSVNURLFactory;
import viewtier.ui.AbstractInput;
import viewtier.ui.CheckBox;
import viewtier.ui.Color;
import viewtier.ui.Component;
import viewtier.ui.Field;
import viewtier.ui.Flow;
import viewtier.ui.Image;
import viewtier.ui.Label;
import viewtier.ui.Parameters;
import viewtier.ui.Password;
import viewtier.ui.Tierlet;
import viewtier.ui.TierletContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class
 */
public final class WebuiUtils {

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(WebuiUtils.class); // NOPMD
  private static final String RESULT_URL_PREFIX = "/parabuild/build/result/";
  private static final int DEFAULT_THROBBER_SIZE = 16;


  private WebuiUtils() {
  }


  /**
   * This factory method produces blank horisonal panel.
   *
   * @return main content Panel
   */
  public static Component makeHorizontalDivider(final int height) {
    final Component divider = new Label();
    divider.setHeight(height);
    return divider;
  }


  public static Component makePanelDivider() {
    return makeHorizontalDivider(Pages.PANEL_DIVIDER);
  }


  public static boolean validateColumnNotBlank(final List errors, final int rowIndex, final String name, final String value) {
    if (StringUtils.isBlank(value)) {
      errors.add("Column \"" + name + "\" at row number " + (rowIndex + 1) + " is blank. This column can not be blank.");
      return false;
    } else {
      return true;
    }
  }


  public static void validateColumnValidEmail(final List errors, final int rowIndex, final String name, final String value) {
    if (!MailUtils.isValidEmail(value)) {
      errors.add("Column \"" + name + "\" at row number " + (rowIndex + 1) + " is not a valid e-mail.");
    }
  }


  public static void validatePatternNotBlank(final List errors, final int rowIndex, final String name, final String value) {
    if (StringUtils.patternIsEmpty(value)) {
      errors.add("Column \"" + name + "\" at row number " + (rowIndex + 1) + " is blank. This column can not be blank.");
    }
  }


  public static void validateColumnNotBlank(final List errors, final int rowIndex, final String name, final Field field) {
    validateColumnNotBlank(errors, rowIndex, name, field.getValue());
  }


  public static boolean validateColumnPositiveInteger(final List errors, final int rowIndex, final String name, final Field field) {
    final boolean result;
    if (!StringUtils.isValidInteger(field.getValue()) || Integer.parseInt(field.getValue()) <= 0) {
      errors.add("Column \"" + name + "\" at row number " + (rowIndex + 1) + " should be a positive integer.");
      result = false;
    } else {
      result = true;
    }
    return result;
  }


  public static void validateFieldNotBlank(final List errors, final String fieldName, final String fieldValue) {
    if (StringUtils.isBlank(fieldValue)) {
      errors.add("Field \"" + fieldName + "\" can not be blank.");
    }
  }


  public static void validateFieldIsRGBColor(final List errors, final String fieldName, final Field field) {
    final Pattern pattern = Pattern.compile("[0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]");
    final Matcher matcher = pattern.matcher(field.getValue());
    if (!matcher.matches()) {
      errors.add("Field \"" + fieldName + "\" should contain a valid RGB value. Example: FF0000");
    }
  }


  public static void validateFieldValidNonNegativeInteger(final List errors, final String fieldName, final Field field) {
    final String value = field.getValue();
    if (!StringUtils.isValidInteger(value) || Integer.parseInt(field.getValue()) < 0) {
      errors.add("Field \"" + fieldName + "\" should be a valid non-negative integer.");
    }
  }


  public static void validateFieldValidPositiveInteger(final List errors, final String fieldName, final Field field) {
    final String value = field.getValue();
    if (!StringUtils.isValidInteger(value) || Integer.parseInt(field.getValue()) <= 0) {
      errors.add("Field \"" + fieldName + "\" should be a valid positive integer.");
    }
  }


  /**
   * Validates that a given field contains valid e-mail.
   *
   * @param errors    will add error msg to this list if not valid.
   * @param fieldName field caption.
   * @param field     to validate
   */
  public static void validateFieldValidEmail(final List errors, final String fieldName, final Field field) {
    if (!MailUtils.isValidEmail(field.getValue())) {
      errors.add("Field \"" + fieldName + "\" is not a valid e-mail address.");
    }
  }


  /**
   * Validates that a given field contains valid string.
   *
   * @param errors    will add error msg to this list if not valid.
   * @param fieldName field caption.
   * @param field     to validate
   */
  public static void validateFieldStrict(final List errors, final String fieldName, final Field field) {
    if (!StringUtils.isValidStrictName(field.getValue())) {
      errors.add("Field \"" + fieldName + "\" can contain only alphanumeric characters, \"-\" and \"_\".");
    }
  }


  /**
   * Validates that a given field contains valid string.
   *
   * @param errors    will add error msg to this list if not valid.
   * @param fieldName field caption.
   * @param field     to validate
   */
  public static void validateColumnIsStrict(final List errors, final int index, final String fieldName, final Field field) {
    if (!StringUtils.isValidStrictName(field.getValue())) {
      errors.add("Column \"" + fieldName + "\" at row \"" + index + "\" can contain only alphanumeric characters, \"-\" and \"_\".");
    }
  }


  public static boolean validateFieldNotBlank(final List errors, final String fieldName, final AbstractInput field) {
    boolean valid = true;
    if (StringUtils.isBlank(field.getValue())) {
      errors.add("Field \"" + fieldName + "\" can not be blank.");
      valid = false;
    }
    return valid;
  }


  /**
   * Returns true if Field is blank
   *
   * @param field
   * @return true if Field is blank
   */
  public static boolean isBlank(final AbstractInput field) {
    return StringUtils.isBlank(field.getValue());
  }


  /**
   * Returns true if Password is blank
   *
   * @param field
   * @return true if Password is blank
   */
  public static boolean isBlank(final Password field) {
    return StringUtils.isBlank(field.getValue());
  }


  /**
   * Helper method to alter component width
   *
   * @param comp  to change width
   * @param width to set
   * @return the same component
   */
  public static Component alterWidth(final Component comp, final int width) {
    comp.setWidth(width);
    return comp;
  }


  /**
   * Adds "Click here to continue" flow component
   */
  public static Flow clickHereToContinue(final String tierletURL) {
    return clickHereToContinue(tierletURL, new Properties());
  }


  /**
   * Adds "Click here to continue" flow component
   */
  public static Flow clickHereToContinue(final String tierletURL, final Properties params) {
    return new Flow()
            .add(new CommonLabel(" Click "))
            .add(new CommonLink("here", tierletURL, params))
            .add(new CommonLabel(" to continue."));
  }


  /**
   * Adds "Click here to continue" flow component that adds a
   * build ID parameter to the tierlet URL.
   */
  public static Flow clickHereToContinue(final String tierletURL, final int buildID) {
    final Properties props = new Properties();
    props.setProperty(Pages.PARAM_BUILD_ID, Integer.toString(buildID));
    return new Flow()
            .add(new CommonLabel("Click "))
            .add(new CommonLink("here", tierletURL, props))
            .add(new CommonLabel(" to continue."));
  }


  public static Tierlet.Result storeReturnPathAndForward(final TierletContext tierletContext, final String forwardTo,
                                                         final String returnTo, final Parameters returnToParams) {

    return storeReturnPathAndForward(tierletContext, forwardTo, new Parameters(), returnTo, returnToParams);
  }


  public static Tierlet.Result storeReturnPathAndForward(final TierletContext tierletContext, final String forwardTo,
                                                         final Parameters forwardToParams, final String returnTo,
                                                         final Parameters returnToParams) {
    // place return reference to the session
    final HttpSession session = tierletContext.getHttpServletRequest().getSession();
    final ReturnTierletAttribuite returnAttribute = new ReturnTierletAttribuite(returnTo, returnToParams);
    session.setAttribute(Pages.ATTRIBUTE_RETURN_TIERLET, returnAttribute);
    // farward to system configuration
    return Tierlet.Result.Done(forwardTo, forwardToParams);
  }


  public static Color getBuildResultColor(final TierletContext tierletContext, final BuildRun buildRun) {
    final boolean buildRunNotNullAndComplete = isBuildRunNotNullAndComplete(buildRun);
    if (!buildRunNotNullAndComplete) {
      return Color.Black;
    }
    return getBuildResultColor(tierletContext, buildRunNotNullAndComplete, buildRun.getResultID());
  }


  public static Color getBuildResultColor(final TierletContext tierletContext, final StepRun stepRun) {
    return getBuildResultColor(tierletContext, stepRun.isComplete(), stepRun.getResultID());
  }


  private static Color getBuildResultColor(final TierletContext tierletContext, final boolean complete, final byte resultCode) {
    // build run complete?
    if (!complete) {
      // no, return default color
      return Color.Black;
    }

    if (resultCode == BuildRun.BUILD_RESULT_SUCCESS) {
      return makeResultColor(tierletContext, Pages.COLOR_BUILD_SUCCESSFUL, UserProperty.SUCCESSFUL_BUILD_COLOR);
    } else {
      return makeResultColor(tierletContext, Pages.COLOR_BUILD_FAILED, UserProperty.FAILED_BUILD_COLOR);
    }
  }


  /**
   * Composes "ago" string in round brackets
   *
   * @param buildRun
   * @return empty StringBuffer is buildRun is null, it's not
   *         complete or the duration is negative.
   */
  public static StringBuffer makeAgoAsString(final BuildRun buildRun, final boolean wrap) {
    final StringBuffer result = new StringBuffer(30);
    if (buildRun == null || buildRun.getFinishedAt() == null) {
      return result;
    }
    final long lastBuildRunAgo = (new Date().getTime() - buildRun.getFinishedAt().getTime()) / 1000L;
    if (lastBuildRunAgo < 0L) {
      return result;
    }
    if (wrap) {
      result.append('(');
    }
    result.append(StringUtils.durationToString(lastBuildRunAgo, false));
    result.append(" ago");
    if (wrap) {
      result.append(')');
    }
    return result;
  }


  /**
   * Composes "ago" string in round brackets
   *
   * @param buildRun
   * @return empty StringBuffer is buildRun is null, it's not
   *         complete or the duration is negative.
   */
  public static StringBuffer makeAgoAsString(final BuildRun buildRun) {
    return makeAgoAsString(buildRun, true);
  }


  /**
   * Ensures that both password fields are the same.
   * <p/>
   * If not, adds a error to the list.
   */
  public static void validatePasswordFields(final List errors, final String caption, final Password fldAdminPasswd, final String retypeCaption, final Password fldConfirmPasswd) {
    if (!fldAdminPasswd.getValue().trim().equals(fldConfirmPasswd.getValue().trim())) {
      errors.add("Field \"" + caption + "\" and \"" + retypeCaption + "\" should be the same");
    }
  }


  /**
   * @return true if the given build run is not null and
   *         complete.
   */
  public static boolean isBuildRunNotNullAndComplete(final BuildRun buildRun) {
    return buildRun != null && buildRun.completed();
  }


  public static String makeStatisticsChartURL(final int buildID, final int statsCode) {
    return "/parabuild/build/statistics/image/?buildid=" + buildID + "&statscode=" + statsCode;
  }


  public static Tierlet.Result showNotAuthorized(final BasePage basePage) {
    basePage.baseContentPanel().getUserPanel().clear();
    basePage.baseContentPanel().getUserPanel().add(new Flow()
            .add(new BoldCommonLabel("You are not authorized to access this page "))
            .add(clickHereToContinue(Pages.PUBLIC_BUILDS)));
    return Tierlet.Result.Done();
  }


  public static Tierlet.Result sshowNotAuthorized(final BasePage basePage) {
    basePage.baseContentPanel().getUserPanel().clear();
    basePage.baseContentPanel().getUserPanel().add(new Flow()
            .add(new BoldCommonLabel("You are not authorized to access this page "))
            .add(clickHereToContinue(Pages.PUBLIC_BUILDS)));
    return Tierlet.Result.Done();
  }


  public static Tierlet.Result showNotSupported(final BasePage basePage) {
    basePage.baseContentPanel().getUserPanel().clear();
    basePage.baseContentPanel().getUserPanel().add(new Flow()
            .add(new BoldCommonLabel("This operation is not supported "))
            .add(clickHereToContinue(Pages.PUBLIC_BUILDS)));
    return Tierlet.Result.Done();
  }


  public static Tierlet.Result showNotFound(final BasePage basePage) {
    basePage.baseContentPanel().getUserPanel().clear();
    basePage.baseContentPanel().getUserPanel().add(new Flow()
            .add(new BoldCommonLabel("Requested page not found")));
    return Tierlet.Result.Done();
  }


  public static Tierlet.Result showBuildNotFound(final BasePage basePage) {
    basePage.baseContentPanel().getUserPanel().clear();
    basePage.baseContentPanel().showErrorMessage("Requested build can not be found.");
    basePage.baseContentPanel().getUserPanel().add(clickHereToContinue(Pages.ADMIN_BUILDS));
    return Tierlet.Result.Done();
  }


  public static String makeURLParameters(final Properties params) {
    final StringBuilder result = new StringBuilder(100);
    if (!params.isEmpty()) {
      boolean first = true;
      final Set set = params.entrySet();
      for (final Iterator i = set.iterator(); i.hasNext(); ) {
        final Map.Entry entry = (Map.Entry) i.next();
        if (first) {
          result.append('?');
          first = false;
        } else {
          result.append('&');
        }
        result.append((String) entry.getKey());
        result.append('=');
        result.append((String) entry.getValue());
      }
    }
    return result.toString();
  }


  public static void validateFieldStrict(final List errors, final String fieldName, final EncryptingPassword field) {
    if (!StringUtils.isValidStrictName(field.getValue())) {
      errors.add("Field \"" + fieldName + "\" can contain only alphanumeric characters, \"-\" and \"_\".");
    }
  }


  /**
   * Returns true if files in a changeslist should be shown based
   * on passed parameter. If the parameter is not present,
   * attempts to get this information from a cookie.
   *
   * @param paramName
   * @param cookieName
   * @param httpServletRequest
   * @return true if files should be displayed
   */
  public static boolean getShowFilesFromParamOrCookie(final String paramName, final String cookieName, final HttpServletRequest httpServletRequest) {

    // try to get from parameter
    final String parameter = httpServletRequest.getParameter(paramName);
    if (parameter != null) {
      return Boolean.valueOf(parameter);
    }

    // get from cookie
    final Cookie[] cookies = httpServletRequest.getCookies();
    if (cookies == null) {
      return false;
    }

    boolean result = false;
    for (int i = 0; i < cookies.length; i++) {
      final Cookie cookie = cookies[i];
      if (!StringUtils.isBlank(cookie.getName())
              && cookie.getName().equals(cookieName)
              && !StringUtils.isBlank(cookie.getValue())) {
        result = Boolean.valueOf(cookie.getValue());
        break;
      }
    }
    return result;
  }


  /**
   * Returns change view mode.
   */
  public static String getViewChangesModeFromParamOrCookie(final HttpServletRequest httpServletRequest) {

    final String defaultValue = Pages.PARAM_VIEW_CHANGES_MODE_VALUE_BY_CHANGE;
    final String cookieName = Pages.COOKIE_SHOW_CHANGES_MODE;
    final String paramName = Pages.PARAM_VIEW_CHANGES_MODE;

    // try to get from parameter
    final String parameter = httpServletRequest.getParameter(paramName);
    if (!StringUtils.isBlank(parameter)) {

      return parameter;
    }

    // get from cookie
    final Cookie[] cookies = httpServletRequest.getCookies();
    if (cookies == null) {

      return defaultValue;
    }

    for (int i = 0; i < cookies.length; i++) {

      final Cookie cookie = cookies[i];
      if (!StringUtils.isBlank(cookie.getName())
              && cookie.getName().equals(cookieName)
              && !StringUtils.isBlank(cookie.getValue())) {

        return cookie.getValue();
      }
    }

    return defaultValue;
  }


  /**
   * Helper method to convert Viewtier UI Parameters to Properties
   *
   * @param parameters
   * @return {@link Properties}
   */
  public static Properties parametersToProperties(final Parameters parameters) {
    final Properties properties = new Properties();
    for (final Iterator i = parameters.getParametersMap().entrySet().iterator(); i.hasNext(); ) {
      final Map.Entry entry = (Map.Entry) i.next();
      final String[] values = (String[]) entry.getValue();
      properties.setProperty((String) entry.getKey(), values.length > 0 ? values[0] : "");
    }
    return properties;
  }


  public static Color makeResultColor(final TierletContext tierletContext, final Color defaultColor, final String userColorPreferencePropertyName) {
    // get context
    if (tierletContext == null) {
      return defaultColor;
    }

    // get user
    final SecurityManager instance = SecurityManager.getInstance();
    final User userFromRequest = instance.getUserFromRequest(tierletContext.getHttpServletRequest());
    if (userFromRequest == null) {
      return defaultColor;
    }

    // get user preference
    final String colorPreference = instance.getUserPropertyValue(userFromRequest.getUserID(), userColorPreferencePropertyName);
    if (StringUtils.isBlank(colorPreference)) {
      return defaultColor;
    }

    // make color
    return new Color(Integer.parseInt(colorPreference, DEFAULT_THROBBER_SIZE));
  }


  /**
   * Helper method to compose a build name based on actual build
   * name and access field by adding (p) to the build name.
   *
   * @param state
   * @return a build name based on actual build name and access
   *         field.
   */
  public static String getBuildName(final BuildState state) {
    if (state.getAccess() == BuildConfig.ACCESS_PRIVATE) {
      return state.getBuildName() + " (p)";
    } else {
      return state.getBuildName();
    }
  }


  public static String makeResultURLPathInfo(final int buildID, final int stepResultID, final String entryName) {
    return RESULT_URL_PREFIX + Integer.toString(buildID)
            + '/' + stepResultID + '/' + URLEncoder.encode(entryName);
  }


  /**
   * Composes a URL to build result used to send build results
   * notifications.
   */
  public static String makeBuildResultURL(final int buildID, final int stepResultID, final String entryName) {
    final String hostName = SystemConfigurationManagerFactory.getManager().getBuildManagerProtocolHostAndPort();
    final String pathInfo = makeResultURLPathInfo(buildID, stepResultID, entryName);
    return hostName + pathInfo;
  }


  public static void printCookiesFromRequest(final String description, final HttpServletRequest request) {
    final Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return;
    }
    for (int i = 0; i < cookies.length; i++) {
      final Cookie cookie = cookies[i];
      if (log.isDebugEnabled()) {
        log.debug(description + ": cookie name: " + cookie.getName() + ", value: " + cookie.getValue());
      }
    }
  }


  /**
   * Helper method to wrap a call to agent env. If remote
   * agent is not available, it will ignore exceptions and
   * validate command even if it's not available.
   */
  public static void validateCommandExists(final AgentEnvironment agentEnv, final String commandPath, final List errors, final String message) {
    try {
      if (!agentEnv.commandIsAvailable(commandPath)) {
        errors.add(message);
      }
    } catch (final Exception e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public static void hideCaptionAndFieldIfBlank(final Label lbCaption, final AbstractInput flField) {
    if (isBlank(flField) || flField instanceof CheckBox && !((CheckBox) flField).isChecked()) {
      hideCaptionAndField(lbCaption, flField);
    }
  }


  /**
   * Hides a caption and a field.
   *
   * @param lbCaption Label to hide
   * @param flField   field to hide
   */
  public static void hideCaptionAndField(final Label lbCaption, final AbstractInput flField) {
    lbCaption.setVisible(false);
    flField.setVisible(false);
  }


  public static Image makeThrobber(final BuildState currentBuildState, final String buildNameAndStatusCaption) {
    return makeThrobber(currentBuildState, buildNameAndStatusCaption, DEFAULT_THROBBER_SIZE);
  }


  public static Image makeThrobber(final BuildState state, final String buildNameAndStatusCaption, final int activeThrobberSizePixels) {

    // Set paths to throbber images
    final String greenThrobberGif;
    final String redThrobberGif;
    final String blackThrobberGif;
    final int calculatedSizePixels;
    if (state.isBusy()) {
      // Building
      greenThrobberGif = WebUIConstants.IMAGE_GREEN_THROBBER_GIF;
      redThrobberGif = WebUIConstants.IMAGE_RED_THROBBER_GIF;
      blackThrobberGif = WebUIConstants.IMAGE_BLACK_THROBBER_GIF;
      calculatedSizePixels = activeThrobberSizePixels;
    } else {
      // Not building
      greenThrobberGif = WebUIConstants.IMAGE_3232_BULLET_BALL_GLASS_GREEN_GIF;
      redThrobberGif = WebUIConstants.IMAGE_3232_BULLET_BALL_GLASS_RED_GIF;
      blackThrobberGif = WebUIConstants.IMAGE_3232_BULLET_BALL_GLASS_BLUE_GIF;
      calculatedSizePixels = 22;
    }

    // Create image object
    final BuildRun lastCompleteBuildRun = state.getLastCompleteBuildRun();
    final Image throbber;
    if (isBuildRunNotNullAndComplete(lastCompleteBuildRun)) {
      if (lastCompleteBuildRun.successful()) {
        throbber = new Image(greenThrobberGif, buildNameAndStatusCaption, calculatedSizePixels, calculatedSizePixels);
      } else {
        throbber = new Image(redThrobberGif, buildNameAndStatusCaption, calculatedSizePixels, calculatedSizePixels);
      }
    } else {
      throbber = new Image(blackThrobberGif, buildNameAndStatusCaption, calculatedSizePixels, calculatedSizePixels);
    }
    return throbber;
  }


  public static void validateDropDownSelected(final List errors, final String caption, final CodeNameDropDown dropDown, final int notSelected) {
    if (dropDown.getItemCount() == 0) {
      errors.add('\"' + caption + "\" cannot be blank");
      return;
    }
    final int value = dropDown.getCode();
    if (value == notSelected) {
      errors.add("Please select \"" + caption + '\"');
    }
  }


  public static Tierlet.Result showMergeNotFound(final BasePage basePage) {
    basePage.baseContentPanel().getUserPanel().clear();
    basePage.baseContentPanel().showErrorMessage("Requested merge configuration can not be found.");
    basePage.baseContentPanel().getUserPanel().add(clickHereToContinue(Pages.ADMIN_BUILDS));
    return Tierlet.Result.Done();
  }


  public static Properties makeMergeIDParameters(final int mergeID) {
    final Properties props = new Properties();
    props.setProperty(Pages.PARAM_MERGE_ID, Integer.toString(mergeID));
    return props;
  }


  public static List groupParallelBuilds(final List statuses) {

    // Lasy set of integer leader build IDs
    Set leaderBuildIDs = null;

    // separate standalone and leader builds from parallel builds
    final List leaders = new ArrayList(statuses.size());
    final Map parallels = new HashMap(statuses.size());
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    for (int i = 0; i < statuses.size(); i++) {
      final BuildState buildState = (BuildState) statuses.get(i);
      if (buildState.isParallel()) {
        final Integer leaderBuildID = new Integer(cm.getSourceControlSettingValue(buildState.getActiveBuildID(), SourceControlSetting.REFERENCE_BUILD_ID, BuildConfig.UNSAVED_ID));
        // Lasy init
        if (leaderBuildIDs == null) {
          leaderBuildIDs = getLeaderBuildIDs(statuses);
        }
        // Check if leader is present in statuses
        if (leaderBuildIDs.contains(leaderBuildID)) {
          List dependents = (List) parallels.get(leaderBuildID);
          if (dependents == null) {
            dependents = new ArrayList(10);
            parallels.put(leaderBuildID, dependents);
          }
          dependents.add(buildState);
        } else {
          leaders.add(buildState);
        }
      } else {
        leaders.add(buildState);
      }
    }

    // compose result
    final boolean parallelsPresent = !parallels.isEmpty();
    final List result = new ArrayList(statuses.size());
    for (int i = 0, n = leaders.size(); i < n; i++) {
      final BuildState leaderState = (BuildState) leaders.get(i);
      result.add(leaderState);
      if (parallelsPresent) {
        final List dependents = (List) parallels.get(new Integer(leaderState.getActiveBuildID()));
        if (dependents != null) {
          result.addAll(dependents);
        }
      }
    }
    return result;
  }


  public static Set getLeaderBuildIDs(final List statuses) {
    final Set leaderBuildIDs = new HashSet(statuses.size());
    for (int i = 0; i < statuses.size(); i++) {
      final BuildState buildState = (BuildState) statuses.get(i);
      if (!buildState.isParallel()) {
        leaderBuildIDs.add(new Integer(buildState.getActiveBuildID()));
      }
    }
    return leaderBuildIDs;
  }


  public static Image makeBlueBulletSquareImage16x16() {
    return new Image("/parabuild/images/1616/bullet_square_blue.gif", "", 16, 16);
  }


  public static Image makeGreenBulletSquareImage16x16() {
    return new Image("/parabuild/images/1616/bullet_square_green.gif", "", 16, 16);
  }


  public static Image makeRedBulletTriangleRedUp16x16() {
    return new Image("/parabuild/images/1616/bullet_triangle_red_up.gif", "", 16, 16);
  }


  public static Image makeRedBulletTriangleGreenUp16x16() {
    return new Image("/parabuild/images/1616/bullet_triangle_green_up.gif", "", 16, 16);
  }


  public static String makeCurrentlyShowingLogTailAttribute(final int activeBuildID) {
    return "currently-showing-log-tail" + activeBuildID;
  }


  public static boolean currentlyShowingLogTail(final TierletContext tierletContext, final int activeBuildID) {// get current state
    if (activeBuildID == -1) {
      return false;
    }
    final HttpSession session = tierletContext.getHttpServletRequest().getSession();
    final Boolean currentlyShowing = (Boolean) session.getAttribute(makeCurrentlyShowingLogTailAttribute(activeBuildID));
    return currentlyShowing != null && currentlyShowing;
  }


  public static ChangeURLFactory makeChangeURLFactory(final int activeBuildID) {
    //
    // get effective build config to use to make chage url factory
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    BuildConfig effectiveBuildConfig = cm.getBuildConfiguration(activeBuildID);
    if (effectiveBuildConfig.getSourceControl() == BuildConfig.SCM_REFERENCE) {
      effectiveBuildConfig = cm.getEffectiveBuildConfig(effectiveBuildConfig);
    }
    final byte effectiveSourceControl = effectiveBuildConfig.getSourceControl();

    // make factory
    if (effectiveSourceControl == BuildConfig.SCM_CVS) {

      // get browser type
      final int browserType = getSourceBrowserType(cm, effectiveBuildConfig);

      // handle browser types
      if (browserType == SourceControlSetting.CODE_NOT_SELECTED) {

        return null;
      } else if (browserType == SourceControlSetting.CODE_VIEWVC) {

        final String viewcvsURL = cm.getSourceControlSettingValue(effectiveBuildConfig.getActiveBuildID(), SourceControlSetting.VIEWCVS_URL, null);
        if (StringUtils.isBlank(viewcvsURL)) {
          return null;
        }
        final String viewcvsRoot = cm.getSourceControlSettingValue(effectiveBuildConfig.getActiveBuildID(), SourceControlSetting.VIEWCVS_ROOT, "");
        return new ViewCVSChangeURLFactory(viewcvsURL, viewcvsRoot);
      } else if (browserType == SourceControlSetting.CODE_FISHEYE) {

        final String fishEyeURL = cm.getSourceControlSettingValue(effectiveBuildConfig.getActiveBuildID(), SourceControlSetting.FISHEYE_URL, null);
        if (StringUtils.isBlank(fishEyeURL)) {
          return null;
        }
        final String fishEyeRoot = cm.getSourceControlSettingValue(effectiveBuildConfig.getActiveBuildID(), SourceControlSetting.FISHEYE_ROOT, "");
        return new FisheyeChangeURLFactory(fishEyeURL, fishEyeRoot);
      } else {

        return null;
      }
    } else if (effectiveSourceControl == BuildConfig.SCM_SVN) {

      // get browser type
      final int browserType = getSourceBrowserType(cm, effectiveBuildConfig);
      // handle browser types
      if (browserType == SourceControlSetting.CODE_NOT_SELECTED) {

        return null;
      } else if (browserType == SourceControlSetting.CODE_VIEWVC) {

        final String viewcvsURL = cm.getSourceControlSettingValue(effectiveBuildConfig.getActiveBuildID(), SourceControlSetting.VIEWCVS_URL, null);
        if (StringUtils.isBlank(viewcvsURL)) {
          return null;
        }
        final String viewcvsRoot = cm.getSourceControlSettingValue(effectiveBuildConfig.getActiveBuildID(), SourceControlSetting.VIEWCVS_ROOT, "");
        return new ViewSVNChangeURLFactory(viewcvsURL, viewcvsRoot);
      } else if (browserType == SourceControlSetting.CODE_FISHEYE) {

        final String fishEyeURL = cm.getSourceControlSettingValue(effectiveBuildConfig.getActiveBuildID(), SourceControlSetting.FISHEYE_URL, null);
        if (StringUtils.isBlank(fishEyeURL)) {
          return null;
        }
        return new FisheyeChangeURLFactory(fishEyeURL);
      } else if (browserType == SourceControlSetting.CODE_WEB_SVN) {

        final String webSvnURL = cm.getSourceControlSettingValue(effectiveBuildConfig.getActiveBuildID(), SourceControlSetting.WEB_SVN_URL, null);
        if (StringUtils.isBlank(webSvnURL)) {
          return null;
        }
        final String webSvnRepname = cm.getSourceControlSettingValue(effectiveBuildConfig.getActiveBuildID(), SourceControlSetting.WEB_SVN_REPNAME, "");
        return new WebSVNURLFactory(webSvnURL, webSvnRepname);
      } else {

        return null;
      }
    } else if (effectiveSourceControl == BuildConfig.SCM_PERFORCE) {
      final SourceControlSetting p4webURL = cm.getSourceControlSetting(effectiveBuildConfig.getActiveBuildID(), SourceControlSetting.P4_P4WEB_URL);
      if (p4webURL == null || StringUtils.isBlank(p4webURL.getPropertyValue())) {
        return null;
      }
      return new P4WebChangeURLFactory(p4webURL.getPropertyValue());
    } else if (effectiveSourceControl == BuildConfig.SCM_GIT) {

      final String githubURL = cm.getSourceControlSettingValue(effectiveBuildConfig.getActiveBuildID(), SourceControlSetting.GITHUB_URL, null);
      if (StringUtils.isBlank(githubURL)) {
        return null;
      }
      return new GithubChangeURLFactory(githubURL);

    }
    return null;
  }


  /**
   * Helper method.
   */
  private static int getSourceBrowserType(final ConfigurationManager cm, final BuildConfig effectiveBuildConfig) {
    final int browserType;
    final SourceControlSetting browserTypeSetting = cm.getSourceControlSetting(effectiveBuildConfig.getActiveBuildID(), SourceControlSetting.REPOSITORY_BROWSER_TYPE);
    if (browserTypeSetting == null) {
      browserType = SourceControlSetting.CODE_VIEWVC; // this covers cases when
    } else {
      browserType = browserTypeSetting.getPropertyValueAsInt();
    }
    return browserType;
  }


  /**
   * Helper method to set value or to hide label an value
   * components if the provided value is blank.
   *
   * @param lbLabel
   * @param lbValue
   * @param value
   */
  public static void setValueOrHide(final Label lbLabel, final Label lbValue, final String value) {
    if (StringUtils.isBlank(value)) {
      lbValue.setVisible(false);
      lbLabel.setVisible(false);
    } else {
      lbLabel.setVisible(true);
      lbValue.setVisible(true);
      lbValue.setText(value);
    }
  }


  public static void setValueOrHideIfZero(final Label lbLabel, final Label lbValue, final Integer value) {
    setValueOrHide(lbLabel, lbValue, value == null || value == 0 ? (String) null : value.toString());
  }


  /**
   * @param displayGroupID
   * @return List of BuildState objects filtered accordingly to
   *         user rights.
   */
  public static List getUserBuildStatuses(final int displayGroupID, final int userID) {

    final SecurityManager sm = SecurityManager.getInstance();
    final boolean showInactiveWithAll = UserProperty.OPTION_CHECKED.equals(sm.getUserPropertyValue(userID, UserProperty.SHOW_INACTIVE_BUILDS));
    return getUserBuildStatuses(displayGroupID, userID, showInactiveWithAll);
  }


  /**
   * @param displayGroupID
   * @return List of BuildState objects filtered accordingly to
   *         user rights.
   */
  public static List getUserBuildStatuses(final int displayGroupID, final int userID, final boolean showInactiveWithAll) {

    final DisplayGroupManager dgm = DisplayGroupManager.getInstance();
    final SecurityManager sm = SecurityManager.getInstance();
    final List userBuildStatuses = sm.getUserBuildStatuses(userID);
    final List filteredStatuses = dgm.filterBuildStatuses(userBuildStatuses, displayGroupID, showInactiveWithAll);
    return groupParallelBuilds(filteredStatuses);
  }


  /**
   * Helper method to return refresh rate.
   */
  public static int getRefreshRate(final HttpSession session, final int userID) {
    // first check if refresh is enabled.
    if (Boolean.FALSE.equals(session.getAttribute(PageHeaderPanel.SESSION_ATTRIBUTE_REFRESH_ENABLED))) {
      return 0;
    }
    return SecurityManager.getInstance().getBuildStatusRefreshSecs(userID);
  }


  public static Tierlet.Result createBuildActionReturnResult(final TierletContext context, final Parameters params) {
    if (context == null) {
      return Tierlet.Result.Done(Pages.ADMIN_BUILDS); // just return
    }

    final HttpSession session = context.getHttpServletRequest().getSession();
    if (session == null) {
      return Tierlet.Result.Done(Pages.ADMIN_BUILDS); // just return
    }

    final ReturnPage returnPage = (ReturnPage) session.getAttribute(ReturnPage.PARABUILD_RETURN_PAGE);
    if (returnPage == null) {
      return Tierlet.Result.Done(Pages.ADMIN_BUILDS); // just return
    }
    session.setAttribute(ReturnPage.PARABUILD_RETURN_PAGE, null);

    // Copy page params
    final Parameters resultParams = new Parameters();
    for (final Iterator iter = returnPage.getParemeters().getParametersMap().entrySet().iterator(); iter.hasNext(); ) {
      final Map.Entry entry = (Map.Entry) iter.next();
      final String[] value = (String[]) entry.getValue();
      for (int i = 0; i < value.length; i++) {
        resultParams.addParameter((String) entry.getKey(), value[i]);
      }
    }

    // Copy method params
    for (final Iterator iter = params.getParametersMap().entrySet().iterator(); iter.hasNext(); ) {
      final Map.Entry entry = (Map.Entry) iter.next();
      final String[] value = (String[]) entry.getValue();
      for (int i = 0; i < value.length; i++) {
        resultParams.addParameter((String) entry.getKey(), value[i]);
      }
    }

    return Tierlet.Result.Done(returnPage.getPage(), resultParams);
  }


  public static Tierlet.Result createBuildActionReturnResult(final TierletContext tierletContext) {
    return createBuildActionReturnResult(tierletContext, new Parameters());
  }


  public static void validateVariableName(final List errors, final Field flName) {
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    if (scm.isCustomVariableNameValidation()) {
      final String regex = scm.getCustomVariableNameRegex();
      if (!Pattern.compile(regex).matcher(flName.getValue()).matches()) {
        errors.add("Variable name \"" + flName.getValue() + "\" does not match a custom regex defined in the system user interface properties: " + regex);
      }
    } else {
      // Default build name format validation
      if (!StringUtils.isValidStrictName(flName.getValue())) {
        errors.add("Variable name can contain only alphanumeric characters, \"-\" and \"_\".");
      }
    }
  }


  public static Properties parametersFromHttpServletRequest(final HttpServletRequest httpServletRequest) {

    final Properties result = new Properties();
    final Map parameterMap = httpServletRequest.getParameterMap();
    if (parameterMap == null) {
      return result;
    }
    for (final Iterator i = parameterMap.entrySet().iterator(); i.hasNext(); ) {
      final Map.Entry entry = (Map.Entry) i.next();
      final String paramName = (String) entry.getKey();
      if (paramName.startsWith("vti") || paramName.startsWith("cid")) {
        continue;
      }// skip internal viewtier parameters
      final String[] paramValues = (String[]) entry.getValue();
      if (paramValues == null || paramValues.length == 0) {
        continue;
      }
      result.setProperty(paramName, paramValues[0]);
    }
    return result;
  }
}
