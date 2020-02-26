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
package org.parabuild.ci.webui.admin.usermanagement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.User;
import org.parabuild.ci.object.UserProperty;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.DisplayGroupDropDown;
import org.parabuild.ci.webui.admin.InstantMessagingTypeDropdown;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CodeNameDropDown;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonPasswordField;
import org.parabuild.ci.webui.common.EmailField;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.LoginNameField;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.CheckBox;
import viewtier.ui.Color;
import viewtier.ui.Field;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Password;
import viewtier.ui.Tierlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @noinspection FieldCanBeLocal, UnsecureRandomNumberGeneration
 */
public final class UserPanel extends MessagePanel implements Validatable, Saveable {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  private static final Log LOG = LogFactory.getLog(UserPanel.class); // NOPMD

  public static final byte MODE_EDIT_USER = 0;
  public static final byte MODE_EDIT_PREFERENCES = 1;

  private static final String CAPTION_ACCESSIBILITY = "Accessibility:";
  private static final String CAPTION_ADMIN_USER = "Admin user: ";
  private static final String CAPTION_APPEARANCE = "Appearance: ";
  private static final String CAPTION_AUTHENTICATE_USING_LDAP = "Authenticate using LDAP: ";
  private static final String CAPTION_DASHBOARD_ROW_SIZE = "Dashboard row size: ";
  private static final String CAPTION_DEFAULT_DISPLAY_GROUP = "Default display group:";
  private static final String CAPTION_DISABLE_E_MAIL = "Disable all e-mail: ";
  private static final String CAPTION_EMAIL = "E-mail:";
  private static final String CAPTION_FAILED_BUILD_COLOR = "Failed build color, RGB: ";
  private static final String CAPTION_FULL_NAME = "Full name:";
  private static final String CAPTION_IM_ADDRESS = "Instant messaging address:";
  private static final String CAPTION_IM_NOTIFY_ABOUT = "Notify about:";
  private static final String CAPTION_IM_SEND_FAILURES = "Failed builds";
  private static final String CAPTION_IM_SEND_SUCCESSES = "Successful builds";
  private static final String CAPTION_IM_SEND_SYSTEM_ERRORS = "System errors";
  private static final String CAPTION_IM_TYPE = "Instant messaging:";
  private static final String CAPTION_LOG_TAIL_BUFFER_SIZE = "Log tail window size: ";
  private static final String CAPTION_LOGIN_NAME = "Login name:";
  private static final String CAPTION_MAX_BUILDS_ON_RECENT_HISTORY_VIEW = "Max builds on recent history view";
  private static final String CAPTION_PASSWORD = "Login password:";
  private static final String CAPTION_REFRESH_RATE = "Build status refresh rate:";
  private static final String CAPTION_RETYPE_PASSWORD = "Retype password:";
  private static final String CAPTION_SECONDS = " seconds";
  private static final String CAPTION_SHOW_INACTIVE_BUILDS = "Show inactive builds with all: ";
  private static final String CAPTION_SUCCESSFUL_BUILD_COLOR = "Successful build color, RGB: ";
  private static final String CAPTION_TEST = "Test";
  private static final String CAPTION_USER_GROUPS = "User belongs to group(s):";

  private static final String FNAME_CONFIRMPASSWD = "cpasswd";
  private static final String FNAME_EMAIL = "email";
  private static final String FNAME_FULLNAME = "fullname";
  private static final String FNAME_SUCCESSFUL_BUILD_COLOR = "greencolor";
  private static final String FNAME_IM_ADDRESS = "imaddress";
  private static final String FNAME_IM_TYPE = "imtype";
  private static final String FNAME_LOGINNAME = "loginname";
  private static final String FNAME_PASSWD = "passwd";
  private static final String FNAME_RED_COLOR = "redcolor";

  // inputs
  private final CheckBox cbAuthenticateUsingLDAP = new CheckBox();  // NOPMD
  private final CheckBox cbDisableAllEmail = new CheckBox();  // NOPMD
  private final CheckBox cbIMSendFailures = new CheckBox();  // NOPMD
  private final CheckBox cbIMSendSuccesses = new CheckBox();  // NOPMD
  private final CheckBox cbIMSendSystemErrors = new CheckBox();  // NOPMD
  private final CheckBox cbAdmin = new CheckBox();  // NOPMD
  private final CheckBox cbShowInactiveBuilds = new CheckBox();  // NOPMD
  private final CodeNameDropDown flIMType = new InstantMessagingTypeDropdown(FNAME_IM_TYPE);  // NOPMD
  private final CodeNameDropDown flDefaultDisplayGroup = new DisplayGroupDropDown(CodeNameDropDown.ALLOW_NONEXISTING_CODES);
  private final Field flEmail = new EmailField(FNAME_EMAIL, 60);  // NOPMD
  private final Field flFullName = new CommonField(FNAME_FULLNAME, 60, 60);  // NOPMD
  private final Field flSuccessfulBuildColor = new CommonField(FNAME_SUCCESSFUL_BUILD_COLOR, 6, 8);  // NOPMD
  private final Field flIMAddress = new CommonField(FNAME_IM_ADDRESS, 60, 60);  // NOPMD
  private final Field flLoginName = new LoginNameField(FNAME_LOGINNAME, 30);  // NOPMD
  private final Field flFailedBuildColor = new CommonField(FNAME_RED_COLOR, 6, 8);  // NOPMD
  private final Field flRefreshRate = new CommonField(3, 3); // NOPMD
  private final Field flDashboardRowSize = new CommonField(2, 2);
  private final Field flMaxRecentBuilds = new CommonField(3, 3);
  private final Password flPassword = new CommonPasswordField(FNAME_PASSWD);  // NOPMD
  private final Password flRetypePassword = new CommonPasswordField(FNAME_CONFIRMPASSWD);  // NOPMD
  private final UserGroupsPanel pnlUserGroups = new UserGroupsPanel();  // NOPMD

  // hide-able components
  private final BoldCommonLabel lbUserGroups = new BoldCommonLabel(CAPTION_USER_GROUPS);
  private final CommonFlow flwSystemErrors = new CommonFlow(cbIMSendSystemErrors, new CommonLabel(CAPTION_IM_SEND_SYSTEM_ERRORS));

  // test successful build color
  private final CommonButton btnTestSuccessfulBuildColor = new CommonButton(CAPTION_TEST); // NOPMD SingularField
  private final CommonLabel lbValueTestSuccessfulBuildColor = new BoldCommonLabel(); // NOPMD SingularField

  // test Failed build  color
  private final CommonButton btnTestFailedBuildColor = new CommonButton(CAPTION_TEST); // NOPMD SingularField
  private final CommonLabel lbValueTestFailedBuildColor = new BoldCommonLabel(); // NOPMD SingularField

  private final PropertyToInputMap userPropsInputMap = new PropertyToInputMap(false, makePropertyHandler());

  private final CommonFieldLabel lbPassword = new CommonFieldLabel(CAPTION_PASSWORD);
  private final CommonFieldLabel lbRetypePassword = new CommonFieldLabel(CAPTION_RETYPE_PASSWORD);
  private final CommonField flTailWindowSize = new CommonField(3, 2);

  private int userID = User.UNSAVED_ID;
  private final byte editMode;


  /**
   * Creates message panel without title.
   *
   * @param editMode a edit mode.
   */
  public UserPanel(final byte editMode) {
    super(true); // don't show content border
    this.editMode = editMode;
    final Panel cp = getUserPanel();
    cp.setWidth(Pages.PAGE_WIDTH);
    final GridIterator gi = new GridIterator(cp, 2);
    gi.addPair(new CommonFieldLabel(CAPTION_LOGIN_NAME), new RequiredFieldMarker(flLoginName));
    gi.addPair(new CommonFieldLabel(CAPTION_FULL_NAME), new RequiredFieldMarker(flFullName));
    gi.addPair(new CommonFieldLabel(CAPTION_EMAIL), new RequiredFieldMarker(flEmail));
    gi.addPair(new CommonFieldLabel(CAPTION_ADMIN_USER), cbAdmin);
    gi.addPair(new CommonFieldLabel(CAPTION_AUTHENTICATE_USING_LDAP), cbAuthenticateUsingLDAP);
    gi.addPair(lbPassword, flPassword);
    gi.addPair(lbRetypePassword, flRetypePassword);
    gi.addBlankLine();
    gi.addPair(new CommonFieldLabel(CAPTION_DISABLE_E_MAIL), cbDisableAllEmail);
    gi.addBlankLine();
    gi.addPair(new CommonFieldLabel(CAPTION_IM_TYPE), flIMType);
    gi.addPair(new CommonFieldLabel(CAPTION_IM_ADDRESS), flIMAddress);
    gi.addPair(new CommonFieldLabel(CAPTION_IM_NOTIFY_ABOUT), new CommonFlow(cbIMSendSuccesses, new CommonLabel(CAPTION_IM_SEND_SUCCESSES)));
    gi.addPair(new Label(), new CommonFlow(cbIMSendFailures, new CommonLabel(CAPTION_IM_SEND_FAILURES)));
    gi.addPair(new Label(), flwSystemErrors);
    gi.addBlankLine();
    gi.addPair(new CommonFieldLabel(CAPTION_ACCESSIBILITY), new Label());
    gi.addPair(new CommonFieldLabel(CAPTION_REFRESH_RATE), new CommonFlow(new RequiredFieldMarker(flRefreshRate), new CommonLabel(CAPTION_SECONDS)));
    gi.addPair(new CommonFieldLabel(CAPTION_SUCCESSFUL_BUILD_COLOR), new CommonFlow(flSuccessfulBuildColor, new Label(" "), btnTestSuccessfulBuildColor, new Label(" "), lbValueTestSuccessfulBuildColor));
    gi.addPair(new CommonFieldLabel(CAPTION_FAILED_BUILD_COLOR), new CommonFlow(flFailedBuildColor, new Label(" "), btnTestFailedBuildColor, new Label(" "), lbValueTestFailedBuildColor));
    gi.addBlankLine();
    gi.addPair(new CommonFieldLabel(CAPTION_DASHBOARD_ROW_SIZE), new CommonFlow(new RequiredFieldMarker(flDashboardRowSize), new CommonLabel(" builds")));
    gi.addPair(new CommonFieldLabel(CAPTION_MAX_BUILDS_ON_RECENT_HISTORY_VIEW), new CommonFlow(new RequiredFieldMarker(flMaxRecentBuilds), new CommonLabel(" builds")));
    gi.addPair(new CommonFieldLabel(CAPTION_LOG_TAIL_BUFFER_SIZE), new CommonFlow(new RequiredFieldMarker(flTailWindowSize), new CommonLabel(" lines")));
    gi.addBlankLine();
    gi.addPair(new CommonFieldLabel(CAPTION_APPEARANCE), new Label());
    gi.addPair(new CommonFieldLabel(CAPTION_DEFAULT_DISPLAY_GROUP), flDefaultDisplayGroup);
    gi.addPair(new CommonFieldLabel(CAPTION_SHOW_INACTIVE_BUILDS), cbShowInactiveBuilds);
    gi.addBlankLine();
    gi.addPair(lbUserGroups, pnlUserGroups);

    // bindings for user attributes
    userPropsInputMap.bindPropertyNameToInput(UserProperty.BUILD_STATUS_REFRESH_RATE, flRefreshRate);
    userPropsInputMap.bindPropertyNameToInput(UserProperty.DASHBOARD_ROW_SIZE, flDashboardRowSize);
    userPropsInputMap.bindPropertyNameToInput(UserProperty.DEFAULT_DISPLAY_GROUP, flDefaultDisplayGroup);
    userPropsInputMap.bindPropertyNameToInput(UserProperty.FAILED_BUILD_COLOR, flFailedBuildColor);
    userPropsInputMap.bindPropertyNameToInput(UserProperty.IM_SEND_FAILURES, cbIMSendFailures);
    userPropsInputMap.bindPropertyNameToInput(UserProperty.IM_SEND_SUCCESSES, cbIMSendSuccesses);
    userPropsInputMap.bindPropertyNameToInput(UserProperty.IM_SEND_SYSTEM_ERRORS, cbIMSendSystemErrors);
    userPropsInputMap.bindPropertyNameToInput(UserProperty.MAX_RECENT_BUILDS, flMaxRecentBuilds);
    userPropsInputMap.bindPropertyNameToInput(UserProperty.SHOW_INACTIVE_BUILDS, cbShowInactiveBuilds);
    userPropsInputMap.bindPropertyNameToInput(UserProperty.SUCCESSFUL_BUILD_COLOR, flSuccessfulBuildColor);
    userPropsInputMap.bindPropertyNameToInput(UserProperty.TAIL_WINDOW_SIZE, flTailWindowSize);

    // align
    lbUserGroups.setAlignY(Layout.TOP);
    lbUserGroups.setAlignX(Layout.RIGHT);

    if (editMode != MODE_EDIT_USER) {
      if (editMode == MODE_EDIT_PREFERENCES) {
        flEmail.setEditable(false);
        flFullName.setEditable(false);
        flLoginName.setEditable(false);
        cbAuthenticateUsingLDAP.setEditable(false);
        lbUserGroups.setVisible(false);
        pnlUserGroups.setVisible(false);
        cbAdmin.setEditable(false);
      } else {
        throw new IllegalArgumentException("Unknown user edit mode: \"" + editMode + '\"');
      }
    }

    // defaults
    flRefreshRate.setValue(String.valueOf(SystemConfigurationManagerFactory.getManager().getBuildStatusRefreshSecs()));
    flSuccessfulBuildColor.setValue(Pages.COLOR_BUILD_SUCCESSFUL.toHexString());
    flFailedBuildColor.setValue(Pages.COLOR_BUILD_FAILED.toHexString());
    flwSystemErrors.setVisible(false);
    flDashboardRowSize.setValue(Integer.toString(ConfigurationConstants.DEFAULT_DASHBOARD_ROW_SIZE));
    flTailWindowSize.setValue(Integer.toString(ConfigurationConstants.DEFAULT_TAIL_WINDOW_SIZE));
    flMaxRecentBuilds.setValue(Integer.toString(ConfigurationConstants.DEFAULT_MAX_RECENT_BUILD));
    cbShowInactiveBuilds.setChecked(true);

    // set handlers for buttons testing colors
    btnTestSuccessfulBuildColor.addListener(new TestColorButtonPressedListener(this.flSuccessfulBuildColor, lbValueTestSuccessfulBuildColor, Pages.COLOR_BUILD_SUCCESSFUL));
    btnTestFailedBuildColor.addListener(new TestColorButtonPressedListener(this.flFailedBuildColor, lbValueTestFailedBuildColor, Pages.COLOR_BUILD_FAILED));
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    // general validation
    final List errors = new ArrayList(11);
    flEmail.setValue(flEmail.getValue().toLowerCase());
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_EMAIL, flEmail);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_FULL_NAME, flFullName);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_LOGIN_NAME, flLoginName);
    WebuiUtils.validatePasswordFields(errors, CAPTION_PASSWORD, flPassword, CAPTION_RETYPE_PASSWORD, flRetypePassword);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_DASHBOARD_ROW_SIZE, flDashboardRowSize);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_LOG_TAIL_BUFFER_SIZE, flTailWindowSize);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_MAX_BUILDS_ON_RECENT_HISTORY_VIEW, flMaxRecentBuilds);

    // validate e-mail is correct
    if (!WebuiUtils.isBlank(flEmail)) {
      WebuiUtils.validateFieldValidEmail(errors, CAPTION_EMAIL, flEmail);
    }

    // validate colors
    if (!WebuiUtils.isBlank(flSuccessfulBuildColor)) {
      WebuiUtils.validateFieldIsRGBColor(errors, CAPTION_SUCCESSFUL_BUILD_COLOR, flSuccessfulBuildColor);
    }
    if (!WebuiUtils.isBlank(flFailedBuildColor)) {
      WebuiUtils.validateFieldIsRGBColor(errors, CAPTION_FAILED_BUILD_COLOR, flFailedBuildColor);
    }

    // IM entry validation
    if (flIMType.getCode() != User.IM_TYPE_NONE && WebuiUtils.isBlank(flIMAddress)) {
      errors.add('\"' + CAPTION_IM_ADDRESS + "\" cannot be blank for instant messaging type \"" + flIMType.getValue() + "\". To disable instant messaging select \"" + InstantMessagingTypeDropdown.NAME_NONE + '\"');
    }

    // check if new user with this login name already exists
    if (errors.isEmpty()) {
      if (userID == User.UNSAVED_ID && SecurityManager.getInstance().getUserByName(flLoginName.getValue()) != null) {
        errors.add("User with login name \"" + flLoginName.getValue() + "\" already exists.");
      }
    }

    // OK ?
    if (errors.isEmpty()) {
      return true;
    }

    // validation failed, show errors
    super.showErrorMessage(errors);
    return false; // return
  }


  /**
   * Loads given user.
   *
   * @param user a user top load
   */
  public void load(final User user) {
    userID = user.getUserID();
    flEmail.setValue((user.getEmail() == null) ? "" : user.getEmail().toLowerCase());
    flFullName.setValue(user.getFullName());
    flLoginName.setValue(user.getName());
    flIMType.setCode(user.getImType());
    flIMAddress.setValue(user.getImAddress());
    cbAuthenticateUsingLDAP.setChecked(user.isAuthenticateUsingLDAP());
    cbAdmin.setChecked(user.isAdmin());
    cbDisableAllEmail.setChecked(user.isDisableAllEmail());

    // adjust editability of certain fields
    if (user.getName().equals(User.DEFAULT_ADMIN_USER)) {
      cbAdmin.setEditable(false);
      cbAuthenticateUsingLDAP.setChecked(false);
      cbAuthenticateUsingLDAP.setEditable(false);
      flEmail.setEditable(true);
      flFullName.setEditable(true);
      flLoginName.setEditable(false);
      flwSystemErrors.setVisible(true);
      lbUserGroups.setVisible(false);
      pnlUserGroups.setVisible(false);
    }

    // hide password if this is LDAP authenticating user
    if (editMode == MODE_EDIT_PREFERENCES) {
      if (cbAuthenticateUsingLDAP.isChecked()) {
        lbPassword.setVisible(false);
        lbRetypePassword.setVisible(false);
        flPassword.setVisible(false);
        flRetypePassword.setVisible(false);
      }
    }

    // always mark admin check box as not editable if the user is the same
    final User userFromContext = SecurityManager.getInstance().getUserFromContext(getTierletContext());
    if (userFromContext != null && userFromContext.getName().equals(user.getName())) {
      cbAdmin.setEditable(false);
    }

    // load user props
    userPropsInputMap.setProperties(SecurityManager.getInstance().getUserProperties(userID));

    // load user's groups
    pnlUserGroups.load(userID);
  }


  /**
   * Saves user data.
   *
   * @return if saved successfuly.
   * @noinspection NumericCastThatLosesPrecision
   */
  public boolean save() {
    try {
      // validate
      if (!validate()) {
        return false;
      }

      // get user object
      if (LOG.isDebugEnabled()) {
        LOG.debug("userID: " + userID);
      }
      final User user;
      if (userID == User.UNSAVED_ID) {
        // create user object and set random password for new user.
        user = new User();
        user.setPassword(StringUtils.digest(Integer.toString(new Random(System.currentTimeMillis()).nextInt())));
      } else {
        user = SecurityManager.getInstance().getUser(userID);
      }

      // cover-ass check - if the user is there
      if (user == null) {
        showErrorMessage("User being edited not found. Please cancel editing and try again.");
        return false;
      }

      // set user data
      user.setEmail(flEmail.getValue().trim().toLowerCase().toLowerCase());
      user.setFullName(flFullName.getValue().trim());
      user.setName(flLoginName.getValue().toLowerCase());
      user.setAuthenticateUsingLDAP(cbAuthenticateUsingLDAP.isChecked());
      user.setAdmin(cbAdmin.isChecked());
      user.setImType((byte) flIMType.getCode());
      user.setImAddress(flIMAddress.getValue());
      user.setDisableAllEmail(cbDisableAllEmail.isChecked());
      if (LOG.isDebugEnabled()) {
        LOG.debug("user.getImType(): " + user.getImType());
      }
      if (!WebuiUtils.isBlank(flPassword)) {
        user.setPassword(StringUtils.digest(flPassword.getValue()));
      }

      // save user object
      final SecurityManager sm = SecurityManager.getInstance();
      sm.save(user);

      // save user props
      sm.saveUserProperties(user.getUserID(), userPropsInputMap.getUpdatedProperties());

      // save user groups
      pnlUserGroups.setUserID(user.getUserID());

      // return
      return pnlUserGroups.save();
    } catch (final Exception e) {
      // show error
      final String description = "Error while saving user information: " + StringUtils.toString(e);
      showErrorMessage(description + ". Please cancel editing and try again.");
      // record error
      final Error error = new Error(description);
      error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
      error.setSendEmail(false);
      error.setDetails(e);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
      return false;
    }
  }


  /**
   * @return loaded user ID
   */
  public int getUserID() {
    return userID;
  }


  /**
   * Factory method to create UserProperty handler to be used by propertyToInputMap
   *
   * @return implementation of PropertyToInputMap.PropertyHandler
   * @see PropertyToInputMap.PropertyHandler
   */
  private static PropertyToInputMap.PropertyHandler makePropertyHandler() {
    return new PropertyToInputMap.PropertyHandler() {
      private static final long serialVersionUID = 3464763434111433003L;


      public Object makeProperty(final String propertyName) {
        final UserProperty prop = new UserProperty();
        prop.setName(propertyName);
        return prop;
      }


      public void setPropertyValue(final Object property, final String propertyValue) {
        ((UserProperty) property).setValue(propertyValue);
      }


      public String getPropertyValue(final Object property) {
        return ((UserProperty) property).getValue();
      }


      public String getPropertyName(final Object property) {
        return ((UserProperty) property).getName();
      }
    };
  }


  /**
   * This button listener is used to set up a listener for
   * a button that tests color.
   */
  private static class TestColorButtonPressedListener implements ButtonPressedListener {

    private final Field fieldWithHexadecimalColor;
    private final CommonLabel labelWithTestColor;
    private final Color defaultColor;
    private static final long serialVersionUID = 0L;


    TestColorButtonPressedListener(final Field fieldWithHexadecimalColor,
                                   final CommonLabel labelWithTestColor, final Color defaultColor) {

      this.fieldWithHexadecimalColor = fieldWithHexadecimalColor;
      this.labelWithTestColor = labelWithTestColor;
      this.defaultColor = defaultColor;
    }


    public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
      Color color;
      String text;
      if (WebuiUtils.isBlank(fieldWithHexadecimalColor)) {
        color = defaultColor;
        text = "Default color";
      } else {
        try {
          color = new Color(Integer.parseInt(fieldWithHexadecimalColor.getValue(), 16));
          text = "0123456789";
        } catch (final NumberFormatException e) {
          color = Color.Black;
          text = "Invalid color";
        }
      }
      labelWithTestColor.setText(text);
      labelWithTestColor.setForeground(color);
      return Tierlet.Result.Continue();
    }


    public String toString() {
      return "TestColorButtonPressedListener{" +
              "fieldWithHexadecimalColor=" + fieldWithHexadecimalColor +
              ", labelWithTestColor=" + labelWithTestColor +
              ", defaultColor=" + defaultColor +
              '}';
    }
  }


  public String toString() {
    return "UserPanel{" +
            "cbAuthenticateUsingLDAP=" + cbAuthenticateUsingLDAP +
            ", cbIMSendFailures=" + cbIMSendFailures +
            ", cbIMSendSuccesses=" + cbIMSendSuccesses +
            ", cbIMSendSystemErrors=" + cbIMSendSystemErrors +
            ", flIMType=" + flIMType +
            ", flDefaultDisplayGroup=" + flDefaultDisplayGroup +
            ", flEmail=" + flEmail +
            ", flFullName=" + flFullName +
            ", flSuccessfulBuildColor=" + flSuccessfulBuildColor +
            ", flIMAddress=" + flIMAddress +
            ", flLoginName=" + flLoginName +
            ", flFailedBuildColor=" + flFailedBuildColor +
            ", flRefreshRate=" + flRefreshRate +
            ", flDashboardRowSize=" + flDashboardRowSize +
            ", flPassword=" + flPassword +
            ", flRetypePassword=" + flRetypePassword +
            ", pnlUserGroups=" + pnlUserGroups +
            ", lbUserGroups=" + lbUserGroups +
            ", flwSystemErrors=" + flwSystemErrors +
            ", btnTestSuccessfulBuildColor=" + btnTestSuccessfulBuildColor +
            ", lbValueTestSuccessfulBuildColor=" + lbValueTestSuccessfulBuildColor +
            ", btnTestFailedBuildColor=" + btnTestFailedBuildColor +
            ", lbValueTestFailedBuildColor=" + lbValueTestFailedBuildColor +
            ", userPropsInputMap=" + userPropsInputMap +
            ", lbPassword=" + lbPassword +
            ", lbRetypePassword=" + lbRetypePassword +
            ", flTailWindowSize=" + flTailWindowSize +
            ", userID=" + userID +
            ", editMode=" + editMode +
            '}';
  }
}
