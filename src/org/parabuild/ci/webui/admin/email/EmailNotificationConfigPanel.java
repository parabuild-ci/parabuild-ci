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
package org.parabuild.ci.webui.admin.email;

import org.parabuild.ci.common.MailUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.notification.NotificationManager;
import org.parabuild.ci.notification.NotificationManagerFactory;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.object.User;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.admin.AbstractSystemConfigPanel;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.EmailField;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.CheckBox;
import viewtier.ui.Color;
import viewtier.ui.Field;
import viewtier.ui.Label;
import viewtier.ui.Panel;
import viewtier.ui.Tierlet;

import java.util.ArrayList;
import java.util.List;


/**
 * E-mail config panel
 */
public final class EmailNotificationConfigPanel extends AbstractSystemConfigPanel {

  private static final long serialVersionUID = -4368893153919253517L; // NOPMD

  private static final int TEMPLATE_DATA_LENGTH = 200;

  private static final String CAPTION_ADVANCED_STOP_NOTIFICATION = "Advanced stop notification: ";
  private static final String CAPTION_BUILD_ADMIN_EMAIL = "Build administrator e-mail: ";
  private static final String CAPTION_BUILD_ADMIN_NAME = "Build administrator name: ";
  private static final String CAPTION_CASE_SENSITIVE_USER_NAMES = "Case sensitive Perforce user names: ";
  private static final String CAPTION_EMAIL_DOMAIN = "Default e-mail domain: ";
  private static final String CAPTION_EMAIL_MESSAGE_PRIORITY = "Email message priority: ";
  private static final String CAPTION_FAILED_BUILDS = "Failed builds:";
  private static final String CAPTION_INCLUDE_RESULTS_IN_MESSAGES = "Include links to results into messages: ";
  private static final String CAPTION_MESSAGE_SUBJECT_LINE = "Message subject templates:";
  private static final String CAPTION_NOTIFY_USERS_WITH_EDIT_RIGHTS_ABOUT_SYSTEM_ERRORS = "Notify users with edit rights: ";
  private static final String CAPTION_SMTP_LOGIN_NAME = "SMTP login name: ";
  private static final String CAPTION_SMTP_PASSWORD = "SMTP server password: ";
  private static final String CAPTION_SMTP_SERVER = "SMTP server: ";
  private static final String CAPTION_SMTP_SERVER_PORT = "SMTP server port: ";
  private static final String CAPTION_SMTP_SERVER_REQUIRES_ENCRYPTED_CONNECTION = "SMTP server requires encrypted connection (SSL):";
  private static final String CAPTION_STEP_FINISHED_SUBJECT = "Build step finished: ";
  private static final String CAPTION_STEP_STARTED_SUBJECT = "Build step started: ";
  private static final String CAPTION_SYSTEM_ERRORS = "System errors:";
  private static final String CAPTION_SYSTEM_MESSAGES_PREFIX = "System messages prefix: ";
  private static final String CAPTION_USE_GIT_E_MAIL_AS_USER_NAME = "Use Git user's e-mail: ";

  private final Button btnSendTestMessage = new CommonButton("Send test message");  // NOPMD
  private final CheckBox fldAdvancedStopNotification = new CheckBox();  // NOPMD
  private final CheckBox fldCaseSensitiveVCSUserNames = new CheckBox();  // NOPMD
  private final CheckBox fldIncludeResultsInMessages = new CheckBox(); // NOPMD
  private final CheckBox fldNotifyBuildAdminsAboutSystemErrors = new CheckBox(); // NOPMD
  private final CheckBox fldServerRequiresEncryptedConnection = new CheckBox(); // NOPMD
  private final CheckBox fldUseGitEmailAsUserName  = new CheckBox();  // NOPMD
  private final CommonField buildAdminEmail = new EmailField("build_admin_email", 60);  // NOPMD
  private final CommonField buildAdminName = new CommonField("build_admin_name", 60, 60);  // NOPMD
  private final CommonField defaultEmailDomain = new CommonField("default_email_domain", 60, 60);  // NOPMD
  private final CommonField smtpServer = new CommonField("smtp_server", 60, 60);  // NOPMD
  private final CommonField smtpServerPort = new CommonField("smtp_server_port", 5, 5);  // NOPMD
  private final CommonField smtpServerUser = new CommonField("smtp_server_user", 30, 30);  // NOPMD
  private final EncryptingPassword smtpServerPassword = new EncryptingPassword(30, 30, "smtp_server_password");  // NOPMD
  private final Field fldStepFinishedTemplate = new CommonField(TEMPLATE_DATA_LENGTH, 80); // NOPMD
  private final Field fldStepStartedTemplate = new CommonField(TEMPLATE_DATA_LENGTH, 80); // NOPMD
  private final Field fldSystemMessagePrefix = new CommonField(30, 30); // NOPMD
  private final Label lbTestMessage = new CommonLabel();  // NOPMD
  private final MessagePriorityDropDown fldMessagePriorityFailedBuild = new MessagePriorityDropDown(); // NOPMD
  private final MessagePriorityDropDown fldMessagePrioritySystemError = new MessagePriorityDropDown(); // NOPMD


  /**
   * Constructor
   */
  public EmailNotificationConfigPanel() {
    // unified padding
    smtpServerPassword.setPadding(4);


    setTitle("E-Mail Settings");
    // get MessagePanel's user content panel
    final Panel content = super.getUserPanel();
    // create grid, add components
    final GridIterator gridIterator = new GridIterator(content, 2);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_BUILD_ADMIN_NAME), new RequiredFieldMarker(buildAdminName));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_BUILD_ADMIN_EMAIL), new RequiredFieldMarker(buildAdminEmail));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_EMAIL_DOMAIN), defaultEmailDomain);
    gridIterator.addBlankLine();

    gridIterator.addPair(new CommonFieldLabel(CAPTION_SMTP_SERVER), new RequiredFieldMarker(smtpServer));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SMTP_SERVER_REQUIRES_ENCRYPTED_CONNECTION), fldServerRequiresEncryptedConnection);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SMTP_SERVER_PORT), new RequiredFieldMarker(smtpServerPort));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SMTP_LOGIN_NAME), smtpServerUser);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SMTP_PASSWORD), smtpServerPassword);
    gridIterator.addPair(new Label(), new CommonFlow(btnSendTestMessage, new Label(" "), lbTestMessage));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_USE_GIT_E_MAIL_AS_USER_NAME), fldUseGitEmailAsUserName);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_CASE_SENSITIVE_USER_NAMES), fldCaseSensitiveVCSUserNames);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_INCLUDE_RESULTS_IN_MESSAGES), fldIncludeResultsInMessages);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SYSTEM_MESSAGES_PREFIX), fldSystemMessagePrefix);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_MESSAGE_SUBJECT_LINE), new Label());
    gridIterator.addPair(new CommonFieldLabel(CAPTION_STEP_STARTED_SUBJECT), fldStepStartedTemplate);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_STEP_FINISHED_SUBJECT), fldStepFinishedTemplate);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_EMAIL_MESSAGE_PRIORITY), new Label());
    gridIterator.addPair(new CommonFieldLabel(CAPTION_FAILED_BUILDS), fldMessagePriorityFailedBuild);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SYSTEM_ERRORS), fldMessagePrioritySystemError);
    gridIterator.addBlankLine();
    gridIterator.addPair(new CommonFieldLabel(CAPTION_NOTIFY_USERS_WITH_EDIT_RIGHTS_ABOUT_SYSTEM_ERRORS), fldNotifyBuildAdminsAboutSystemErrors);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_ADVANCED_STOP_NOTIFICATION), fldAdvancedStopNotification);

    // init property to input map
    inputMap.bindPropertyNameToInput(SystemProperty.ADVANCED_STOP_NOTIFICATION, fldAdvancedStopNotification);
    inputMap.bindPropertyNameToInput(SystemProperty.BUILD_ADMIN_EMAIL, buildAdminEmail);
    inputMap.bindPropertyNameToInput(SystemProperty.BUILD_ADMIN_NAME, buildAdminName);
    inputMap.bindPropertyNameToInput(SystemProperty.CASE_SENSITIVE_VCS_USER_NAMES, fldCaseSensitiveVCSUserNames);
    inputMap.bindPropertyNameToInput(SystemProperty.DEFAULT_EMAIL_DOMAIN, defaultEmailDomain);
    inputMap.bindPropertyNameToInput(SystemProperty.INCLUDE_RESULTS_IN_MESSAGES, fldIncludeResultsInMessages);
    inputMap.bindPropertyNameToInput(SystemProperty.MESSAGE_PRIORITY_FAILED_BUILD, fldMessagePriorityFailedBuild);
    inputMap.bindPropertyNameToInput(SystemProperty.MESSAGE_PRIORITY_SYSTEM_ERROR, fldMessagePrioritySystemError);
    inputMap.bindPropertyNameToInput(SystemProperty.NOTIFICATION_PREFIX, fldSystemMessagePrefix);
    inputMap.bindPropertyNameToInput(SystemProperty.NOTIFY_USERS_WITH_EDIT_RIGHTS_ABOUT_SYSTEM_ERRORS, fldNotifyBuildAdminsAboutSystemErrors);
    inputMap.bindPropertyNameToInput(SystemProperty.SMTP_SERVER_ENCRYPTED_CONNECTION, fldServerRequiresEncryptedConnection);
    inputMap.bindPropertyNameToInput(SystemProperty.SMTP_SERVER_NAME, smtpServer);
    inputMap.bindPropertyNameToInput(SystemProperty.SMTP_SERVER_PASSWORD, smtpServerPassword);
    inputMap.bindPropertyNameToInput(SystemProperty.SMTP_SERVER_PORT, smtpServerPort);
    inputMap.bindPropertyNameToInput(SystemProperty.SMTP_SERVER_USER, smtpServerUser);
    inputMap.bindPropertyNameToInput(SystemProperty.STEP_FINISHED_SUBJECT, fldStepFinishedTemplate);
    inputMap.bindPropertyNameToInput(SystemProperty.STEP_STARTED_SUBJECT, fldStepStartedTemplate);
    inputMap.bindPropertyNameToInput(SystemProperty.USE_GIT_USER_E_MAIL, fldUseGitEmailAsUserName);

    // set default values
    buildAdminName.setValue("Build Administrator");
    smtpServerPort.setValue(ConfigurationConstants.DEFAULT_SMTP_SERVER_PORT);

    // set up test message button listener
    btnSendTestMessage.addListener(new ButtonPressedListener() {

      /** @noinspection OverlyBroadCatchBlock*/
      public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
        lbTestMessage.setText(""); // clear up
        if (validate()) {
          Exception errorReport = null;
          try {
            final NotificationManager notificationManager = NotificationManagerFactory.makeNotificationManager();
            notificationManager.sendTestEmailMessage(getSystemProperties());
          } catch (Exception e) {
            errorReport = e;
          }
          if (errorReport == null) {
            lbTestMessage.setText(" Test message has been sent successfully.");
            lbTestMessage.setForeground(Color.DarkGreen);
          } else {
            lbTestMessage.setText(" Couldn't send test message: " + StringUtils.toString(errorReport));
            lbTestMessage.setForeground(Color.DarkRed);
          }
        }
        return Tierlet.Result.Continue();
      }
    });

    // Set defaults
    fldCaseSensitiveVCSUserNames.setChecked(true);
    fldUseGitEmailAsUserName.setChecked(true);
  }


  /**
   * When called, the panel should switch to the
   * corresponding mode.
   *
   * @param modeView to set.
   */
  public void setMode(final byte modeView) {
    final boolean editable = modeView == WebUIConstants.MODE_EDIT;
    btnSendTestMessage.setEditable(editable);
    btnSendTestMessage.setVisible(editable);
    buildAdminEmail.setEditable(editable);
    buildAdminName.setEditable(editable);
    defaultEmailDomain.setEditable(editable);
    fldAdvancedStopNotification.setEditable(editable);
    fldCaseSensitiveVCSUserNames.setEditable(editable);
    fldIncludeResultsInMessages.setEditable(editable);
    fldMessagePriorityFailedBuild.setEditable(editable);
    fldMessagePrioritySystemError.setEditable(editable);
    fldNotifyBuildAdminsAboutSystemErrors.setEditable(editable);
    fldServerRequiresEncryptedConnection.setEditable(editable);
    fldStepFinishedTemplate.setEditable(editable);
    fldStepStartedTemplate.setEditable(editable);
    fldSystemMessagePrefix.setEditable(editable);
    lbTestMessage.setVisible(editable);
    smtpServer.setEditable(editable);
    smtpServerPassword.setEditable(editable);
    smtpServerPort.setEditable(editable);
    smtpServerUser.setEditable(editable);
    fldUseGitEmailAsUserName.setEditable(editable);
  }


  /**
   * Requests to load panel's data
   */
  public void load() {
    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
    setSystemProperties(systemCM.getSystemProperties());
  }


  public boolean save() {
    final boolean saved = super.save();
    if (saved) {
      // load admin user
      final SecurityManager sm = SecurityManager.getInstance();
      final User adm = sm.getUserByName(User.DEFAULT_ADMIN_USER);
      adm.setFullName(buildAdminName.getValue());
      adm.setEmail(buildAdminEmail.getValue());
      sm.save(adm);
    }
    return saved;
  }


  /**
   * Validates reuired inputs. If there are errors, shows
   * errors.
   *
   * @return true if valid
   */
  public boolean validate() {
    clearMessage();
    final List errors = new ArrayList(1);

    // validate fields are not blank
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_BUILD_ADMIN_NAME, buildAdminName);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_BUILD_ADMIN_EMAIL, buildAdminEmail);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_SMTP_SERVER, smtpServer);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_SMTP_SERVER_PORT, smtpServerPort);

    // validate admin email
    if (errors.isEmpty()) {
      WebuiUtils.validateFieldValidEmail(errors, CAPTION_BUILD_ADMIN_EMAIL, buildAdminEmail);
    }

    // validate admin email
    if (!StringUtils.isBlank(defaultEmailDomain.getValue())) {
      if (!MailUtils.isValidEmailDomain(defaultEmailDomain.getValue())) {
        errors.add("Default e-mail domain is invalid");
      }
    }

    // show error if there are any
    if (!errors.isEmpty()) {
      showErrorMessage(errors);
      return false;
    }
    return true;
  }


  public String toString() {
    final StringBuffer sb = new StringBuffer(300);
    sb.append("EmailNotificationConfigPanel");
    sb.append("{btnSendTestMessage=").append(btnSendTestMessage);
    sb.append(", buildAdminEmail=").append(buildAdminEmail);
    sb.append(", buildAdminName=").append(buildAdminName);
    sb.append(", defaultEmailDomain=").append(defaultEmailDomain);
    sb.append(", fldAdvancedStopNotification=").append(fldAdvancedStopNotification);
    sb.append(", fldCaseSensitiveVCSUserNames=").append(fldCaseSensitiveVCSUserNames);
    sb.append(", fldIncludeResultsInMessages=").append(fldIncludeResultsInMessages);
    sb.append(", fldMessagePriorityFailedBuild=").append(fldMessagePriorityFailedBuild);
    sb.append(", fldMessagePrioritySystemError=").append(fldMessagePrioritySystemError);
    sb.append(", fldNotifyBuildAdminsAboutSystemErrors=").append(fldNotifyBuildAdminsAboutSystemErrors);
    sb.append(", fldServerRequiresEncryptedConnection=").append(fldServerRequiresEncryptedConnection);
    sb.append(", fldStepFinishedTemplate=").append(fldStepFinishedTemplate);
    sb.append(", fldStepStartedTemplate=").append(fldStepStartedTemplate);
    sb.append(", fldSystemMessagePrefix=").append(fldSystemMessagePrefix);
    sb.append(", lbTestMessage=").append(lbTestMessage);
    sb.append(", smtpServer=").append(smtpServer);
    sb.append(", smtpServerPassword=").append(smtpServerPassword);
    sb.append(", smtpServerPort=").append(smtpServerPort);
    sb.append(", smtpServerUser=").append(smtpServerUser);
    sb.append('}');
    return sb.toString();
  }
}
