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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.configuration.LDAPReferralCodeToValueConverter;
import org.parabuild.ci.configuration.LDAPVersionCodeToValueConverter;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.Group;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.security.JNDIAuthenticator;
import org.parabuild.ci.security.JNDIUserLookupStringGenerator;
import org.parabuild.ci.webui.admin.AbstractSystemConfigPanel;
import org.parabuild.ci.webui.admin.GroupDropDown;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CodeNameDropDown;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.EmailField;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.Pages;
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
import viewtier.ui.RadioButton;
import viewtier.ui.Tierlet;

import java.util.ArrayList;
import java.util.List;

/**
 * LDAP config panel
 */
final class LDAPConfigPanel extends AbstractSystemConfigPanel {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(LDAPConfigPanel.class); // NOPMD
  private static final long serialVersionUID = -4368893153919253517L; // NOPMD

  private static final String CAPTION_ADD_FIRST_TIME_USER_TO_GROUP = "Add first-time users to group: ";
  private static final String CAPTION_BASE_ELEMENT_FOR_USER_SEARCHES = "Base element for user searches: ";
  private static final String CAPTION_CONNECTION_SECURITY_LEVEL = "Connection security level: ";
  private static final String CAPTION_CONNECTION_PASSWORD = "Connection credentials: ";
  private static final String CAPTION_CONNECTION_URL = "Connection URL";
  private static final String CAPTION_CONNECTION_USER_NAME = "Connection principal: ";
  private static final String CAPTION_CREDENTIALS_DIGEST = "Credentials digest algorithm: ";
  private static final String CAPTION_ENABLE_LDAP_AUTHENTICATION = "Enable LDAP authentication: ";
  private static final String CAPTION_LDAP_CONFIGURATION = "LDAP Configuration";
  private static final String CAPTION_SEARCH_ENTIRE_SUBTREE = "Search entire subtree: ";
  private static final String CAPTION_USER_DISTINGUISHED_NAME_TEMPLATE = "User distinguished name template: ";
  private static final String CAPTION_USER_PASSWORD_ATTRIBUTE_NAME = "User password attribute name: ";
  private static final String CAPTION_USER_SEARCH_TEMPLATE = "User search template: ";
  private static final String CAPTION_USE = " use ";
  private static final String CAPTION_USER_E_MAIL_ATTRIBUTE_NAME = "User e-mail attribute name:";
  private static final String CAPTION_LDAP_VERSION = "LDAP version:";
  private static final String CAPTION_PROCESSING_REFERRALS = "Processing referrals:";
  private static final String CAPTION_USE_LDAP_TO_LOOK_UP_VCS_USER_E_MAIL = "Use LDAP to look up VCS user e-mail: ";

  private final CheckBox flEnableLDAPAuthentication = new CheckBox(); // NOPMD SingularField
  private final CheckBox flSearchEntireSubtree = new CheckBox(); // NOPMD SingularField
  private final CheckBox flUseCredentialDigest = new CheckBox(); // NOPMD SingularField
  private final CodeNameDropDown flConnectionSecurityLevel = new ConnectionSecurityLevelDropDown(); // NOPMD SingularField
  private final CodeNameDropDown flCredentialDigest = new CredentialDigestDropDown(); // NOPMD SingularField
  private final CodeNameDropDown flAddToGroup = new GroupDropDown(); // NOPMD SingularField
  private final Field flConnectionURL = new CommonField(200, 80); // NOPMD SingularField
  private final Field flConnectionUserName = new CommonField(200, 60); // NOPMD SingularField
  private final Field flUserBase = new CommonField(200, 80); // NOPMD SingularField
  private final Field flUserDistinguishedNameTemplate = new CommonField(200, 80); // NOPMD SingularField
  private final Field flUserPasswordAttributeName = new CommonField(200, 60); // NOPMD SingularField
  private final Field flUserSearchTemplate = new CommonField(200, 80); // NOPMD SingularField
  private final EncryptingPassword flConnectionPassword = new EncryptingPassword(30, 30, "ldap_connection_password"); // NOPMD SingularField
  private final RadioButton flLookupUsingDN = new RadioButton("lookup_mode"); // NOPMD SingularField
  private final RadioButton flLookupUsingSearch = new RadioButton("lookup_mode"); // NOPMD SingularField
  private final Field flUserEmailAttributeName = new EmailField(); // NOPMD SingularField
  private final LDAPVersionDropDown flLDAPVersion = new LDAPVersionDropDown(); // NOPMD SingularField
  private final LDAPReferralDropDown flLDAPReferral = new LDAPReferralDropDown(); // NOPMD SingularField

  private final Field flTestUser = new CommonField(200, 30); // NOPMD SingularField
  private final Field flTestPassword = new CommonField(200, 20); // NOPMD SingularField
  private final Button btnTest = new CommonButton(" Test "); // NOPMD SingularField
  private final Label lbTestResult = new BoldCommonLabel(); // NOPMD SingularField
  private final CheckBox cbUseLDAPToLookupVCSUserEmails = new CheckBox(); // NOPMD SingularField


  /**
   * Constructor
   */
  public LDAPConfigPanel() {
    super.setTitle(CAPTION_LDAP_CONFIGURATION);
    // get MessagePanel's user content panel
    final Panel content = super.getUserPanel();

    // create grid, add components
    final GridIterator gi = new GridIterator(content, 2);
    gi.addPair(new CommonFieldLabel(CAPTION_ENABLE_LDAP_AUTHENTICATION), flEnableLDAPAuthentication);
    gi.addPair(new CommonFieldLabel(CAPTION_CONNECTION_URL), new RequiredFieldMarker(flConnectionURL));
    gi.addPair(new CommonFieldLabel(CAPTION_CONNECTION_USER_NAME), flConnectionUserName);
    gi.addPair(new CommonFieldLabel(CAPTION_CONNECTION_PASSWORD), flConnectionPassword);
    gi.addPair(new CommonFieldLabel(CAPTION_CONNECTION_SECURITY_LEVEL), flConnectionSecurityLevel);
    gi.addPair(new CommonFieldLabel(CAPTION_LDAP_VERSION), flLDAPVersion);
    gi.addPair(new CommonFieldLabel(CAPTION_PROCESSING_REFERRALS), flLDAPReferral);
    gi.addBlankLine();
    gi.addPair(new CommonFieldLabel("User lookup:"), new Label());
    gi.addPair(new CommonFieldLabel(CAPTION_USER_DISTINGUISHED_NAME_TEMPLATE), new CommonFlow(flLookupUsingDN, flUserDistinguishedNameTemplate));
    gi.addPair(new CommonFieldLabel(CAPTION_USER_SEARCH_TEMPLATE), new CommonFlow(flLookupUsingSearch, flUserSearchTemplate));
    gi.addPair(new CommonFieldLabel(CAPTION_BASE_ELEMENT_FOR_USER_SEARCHES), flUserBase);
    gi.addPair(new CommonFieldLabel(CAPTION_SEARCH_ENTIRE_SUBTREE), flSearchEntireSubtree);
    gi.addBlankLine();
    gi.addPair(new CommonFieldLabel(CAPTION_USER_PASSWORD_ATTRIBUTE_NAME), flUserPasswordAttributeName);
    gi.addPair(new CommonFieldLabel(CAPTION_CREDENTIALS_DIGEST), new CommonFlow(flUseCredentialDigest, new CommonLabel(CAPTION_USE), flCredentialDigest));
    gi.addPair(new CommonFieldLabel(CAPTION_USER_E_MAIL_ATTRIBUTE_NAME), new RequiredFieldMarker(flUserEmailAttributeName));
    gi.addPair(new CommonFieldLabel(CAPTION_ADD_FIRST_TIME_USER_TO_GROUP), new RequiredFieldMarker(flAddToGroup));
    gi.addPair(new CommonFieldLabel(CAPTION_USE_LDAP_TO_LOOK_UP_VCS_USER_E_MAIL), cbUseLDAPToLookupVCSUserEmails);
    gi.addPair(new Label(""), new CommonFlow(new CommonLabel("Test user: "), flTestUser, new CommonLabel(" Password: "), flTestPassword));
    gi.addPair(new Label(""), new CommonFlow(btnTest, new Label(" "), lbTestResult));

    // init property to input map
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_ADD_FIRST_TIME_USER_TO_GROUP, flAddToGroup);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_AUTHENTICATION_ENABLED, flEnableLDAPAuthentication);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_BASE_ELEMENT_FOR_USER_SEARCHES, flUserBase);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_CONNECTION_PASSWORD, flConnectionPassword);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_CONNECTION_SECURITY_LEVEL, flConnectionSecurityLevel);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_CONNECTION_URL, flConnectionURL);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_CONNECTION_USER_NAME, flConnectionUserName);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_CREDENTIALS_DIGEST, flCredentialDigest);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_REFERRAL, flLDAPReferral);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_SEARCH_ENTIRE_SUBTREE, flSearchEntireSubtree);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_USE_CREDENTIALS_DIGEST, flCredentialDigest);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_USE_TO_LOOKUP_VCS_USER_EMAIL, cbUseLDAPToLookupVCSUserEmails);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_USER_DISTINGUISHED_NAME_TEMPLATE, flUserDistinguishedNameTemplate);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_USER_EMAIL_ATTRIBUTE_NAME, flUserEmailAttributeName);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_USER_LOOKUP_MODE_DN, flLookupUsingDN);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_USER_LOOKUP_MODE_SEARCH, flLookupUsingSearch);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_USER_PASSWORD_ATTRIBUTE_NAME, flUserPasswordAttributeName);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_USER_SEARCH_TEMPLATE, flUserSearchTemplate);
    inputMap.bindPropertyNameToInput(SystemProperty.LDAP_VERSION, flLDAPVersion);

    // set default values
    flConnectionURL.setValue("ldap://");
    lbTestResult.setVisible(false);
    flLookupUsingDN.setSelected(true);
    flLookupUsingSearch.setSelected(false);

    // set button handlers
    btnTest.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 3478042393305054632L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {

        // general validation
        if (!validate()) {
          return Tierlet.Result.Continue();
        }

        // validate test data
        if (WebuiUtils.isBlank(flTestPassword) || WebuiUtils.isBlank(flTestUser)) {
          showTestErrorMessage("Enter test user name and password");
          return Tierlet.Result.Continue();
        }

        final JNDIAuthenticator authenticator = new JNDIAuthenticator(getLookupModeFromInput(), true);
        authenticator.setConnectionCredentials(flConnectionPassword.getValue());
        authenticator.setConnectionSecurityLevel(flConnectionSecurityLevel.getValue());
        authenticator.setConnectionURL(flConnectionURL.getValue());
        authenticator.setConnectionPrincipal(flConnectionUserName.getValue());
        authenticator.setSearchEntireSubtree(flSearchEntireSubtree.isChecked());
        authenticator.setUserBase(flUserBase.getValue());
        authenticator.setUserPasswordAttributeName(flUserPasswordAttributeName.getValue());
        authenticator.setUserDistinguishedNameTemplate(flUserDistinguishedNameTemplate.getValue());
        authenticator.setUserSearchTemplate(flUserSearchTemplate.getValue());
        authenticator.setUserEmailAttributeName(flUserEmailAttributeName.getValue());
        authenticator.setDigestAlgorithm(flCredentialDigest.getItem(flCredentialDigest.getSelection()));
        authenticator.setReferrals(new LDAPReferralCodeToValueConverter().convert((byte)flLDAPReferral.getCode()));
        authenticator.setLDAPVersion(new LDAPVersionCodeToValueConverter().convert((byte)flLDAPVersion.getCode()));
        try {
          if (log.isDebugEnabled()) log.debug("Running test authentication");
          if (authenticator.authenticate(flTestUser.getValue(), flTestPassword.getValue()) != null) {
            showTestSuccessMessage("Success!");
          } else {
            showTestErrorMessage("User not found");
          }
        } catch (final Exception e) {
          showTestErrorMessage("Cannot authenticate. Error message: " + StringUtils.toString(e));
        }
        return Tierlet.Result.Continue();
      }


      private byte getLookupModeFromInput() {
        if (flLookupUsingDN.isSelected()) return ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE;
        if (flLookupUsingSearch.isSelected()) return ConfigurationConstants.LDAP_USER_LOOKUP_BY_SEARCH;
        return ConfigurationConstants.LDAP_USER_LOOKUP_BY_DN_TEMPLATE;
      }
    });
  }


  /**
   * When called, the panel should switch to the
   * corresponding mode.
   *
   * @param viewMode to set.
   */
  public void setMode(final byte viewMode) {
    final boolean editable = viewMode == WebUIConstants.MODE_EDIT;
    btnTest.setVisible(editable);
    cbUseLDAPToLookupVCSUserEmails.setEditable(editable);
    flAddToGroup.setEditable(editable);
    flConnectionPassword.setEditable(editable);
    flConnectionSecurityLevel.setEditable(editable);
    flConnectionURL.setEditable(editable);
    flConnectionUserName.setEditable(editable);
    flCredentialDigest.setEditable(editable);
    flEnableLDAPAuthentication.setEditable(editable);
    flLDAPReferral.setEditable(editable);
    flLDAPVersion.setEditable(editable);
    flLookupUsingDN.setEditable(editable);
    flLookupUsingSearch.setEditable(editable);
    flSearchEntireSubtree.setEditable(editable);
    flTestPassword.setEditable(editable);
    flTestUser.setEditable(editable);
    flUseCredentialDigest.setEditable(editable);
    flUserBase.setEditable(editable);
    flUserDistinguishedNameTemplate.setEditable(editable);
    flUserEmailAttributeName.setEditable(editable);
    flUserPasswordAttributeName.setEditable(editable);
    flUserSearchTemplate.setEditable(editable);
    lbTestResult.setVisible(editable);
  }


  private void showTestSuccessMessage(final String s) {
    lbTestResult.setVisible(true);
    lbTestResult.setText(s);
    lbTestResult.setForeground(Color.DarkGreen);
  }


  private void showTestErrorMessage(final String s) {
    lbTestResult.setText(s);
    lbTestResult.setForeground(Pages.COLOR_ERROR_FG);
    lbTestResult.setVisible(true);
  }


  /**
   * Validates reuired inputs. If there are errors, shows
   * errors.
   *
   * @return true if valid
   */
  public boolean validate() {
    // clear and hide test message first
    lbTestResult.setText("");
    lbTestResult.setVisible(false);

    clearMessage();
    final ArrayList errors = new ArrayList(1);

    // validate fields are valid
    if (flEnableLDAPAuthentication.isChecked()) {
      WebuiUtils.validateFieldNotBlank(errors, CAPTION_CONNECTION_URL, flConnectionURL);
    }

    if (flUseCredentialDigest.isChecked()) {
      if (flCredentialDigest.getCode() == ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_NOT_SELECTED) {
        errors.add("Select credentials digest");
      }
    }


    if (flLookupUsingDN.isSelected()) {
      if (WebuiUtils.validateFieldNotBlank(errors, CAPTION_USER_DISTINGUISHED_NAME_TEMPLATE, flUserDistinguishedNameTemplate)) {
        final JNDIUserLookupStringGenerator generator = new JNDIUserLookupStringGenerator();
        try {
          generator.validateTemplate(flUserDistinguishedNameTemplate.getValue());
        } catch (final ValidationException e) {
          errors.add(StringUtils.toString(e));
        }
      }
    } else if (flLookupUsingSearch.isSelected()) {
      if (WebuiUtils.validateFieldNotBlank(errors, CAPTION_USER_SEARCH_TEMPLATE, flUserSearchTemplate)
        && WebuiUtils.validateFieldNotBlank(errors, CAPTION_BASE_ELEMENT_FOR_USER_SEARCHES, flUserBase)) {
        final JNDIUserLookupStringGenerator generator = new JNDIUserLookupStringGenerator();
        try {
          generator.validateTemplate(flUserSearchTemplate.getValue());
        } catch (final ValidationException e) {
          errors.add(StringUtils.toString(e));
        }
      }
    } else {
      // none is selected
      errors.add("Either \"" + CAPTION_USER_DISTINGUISHED_NAME_TEMPLATE + "\" or \"" + CAPTION_USER_SEARCH_TEMPLATE + "\" should be selected.");
    }

    //
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_USER_E_MAIL_ATTRIBUTE_NAME, flUserEmailAttributeName);

    // group should be selected
    final int groupID = flAddToGroup.getCode();
    if (groupID == Group.UNSAVED_ID) {
      errors.add("Please select \"" + CAPTION_ADD_FIRST_TIME_USER_TO_GROUP + '\"');
    }

    // show error if there are any
    if (!errors.isEmpty()) {
      showErrorMessage(errors);
      return false;
    }
    return true;
  }


  /**
   * Loads Jabber part of system configuration.
   */
  public void load() {
    // load
    final List systemProperties = SystemConfigurationManagerFactory.getManager().getSystemProperties();
    inputMap.setProperties(systemProperties);
  }


  /**
   * Saves modified data.
   */
  public boolean save() {
    SystemConfigurationManagerFactory.getManager().saveSystemProperties(inputMap.getUpdatedProperties());
    return true;
  }
}
