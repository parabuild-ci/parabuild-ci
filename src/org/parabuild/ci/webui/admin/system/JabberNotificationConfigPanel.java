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
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.webui.admin.AbstractSystemConfigPanel;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.CheckBox;
import viewtier.ui.Panel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * E-mail config panel
 */
public final class JabberNotificationConfigPanel extends AbstractSystemConfigPanel {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(JabberNotificationConfigPanel.class); // NOPMD
  private static final long serialVersionUID = -4368893153919253517L; // NOPMD

  public static final String NAME_DISABLED = "Disabled: ";
  public static final String NAME_SEND_NO_PRESENCE = "Send if no presence: ";
  public static final String NAME_LOGIN_NAME = "Jabber login name: ";
  public static final String NAME_PASSWORD = "Jabber password: ";
  public static final String NAME_SERVER_ADDRESS = "Jabber server address: ";
  public static final String NAME_SERVER_PORT = "Jabber server port: ";

  private final CheckBox cbDisabled = new CheckBox();
  private final CommonField flLoginName = new CommonField("jabber_login_name", 60, 60);
  private final CommonField flServerAddress = new CommonField("jabber_sever_address", 65, 65);
  private final CommonField flServerPort = new CommonField("jabber_sever_port", 4, 4);
  private final EncryptingPassword flLoginPassword = new EncryptingPassword(30, 20, "jabber_login_password");

  private final CheckBox cbSendIfNoPresense = new CheckBox();
  private final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();


  /**
   * Constructor
   */
  public JabberNotificationConfigPanel() {
    super.setTitle("Jabber Configuration");
    // get MessagePanel's user content panel
    final Panel content = super.getUserPanel();

    // create grid, add components
    final GridIterator gi = new GridIterator(content, 2);
    gi.addPair(new CommonFieldLabel(NAME_SERVER_ADDRESS), flServerAddress);
    gi.addPair(new CommonFieldLabel(NAME_SERVER_PORT), flServerPort);
    gi.addPair(new CommonFieldLabel(NAME_LOGIN_NAME), flLoginName);
    gi.addPair(new CommonFieldLabel(NAME_PASSWORD), flLoginPassword);
    gi.addPair(new CommonFieldLabel(NAME_SEND_NO_PRESENCE), cbSendIfNoPresense);
    gi.addPair(new CommonFieldLabel(NAME_DISABLED), cbDisabled);

    // init property to input map
    inputMap.bindPropertyNameToInput(SystemProperty.JABBER_SERVER_NAME, flServerAddress);
    inputMap.bindPropertyNameToInput(SystemProperty.JABBER_SERVER_PORT, flServerPort);
    inputMap.bindPropertyNameToInput(SystemProperty.JABBER_LOGIN_NAME, flLoginName);
    inputMap.bindPropertyNameToInput(SystemProperty.JABBER_LOGIN_PASSWORD, flLoginPassword);
    inputMap.bindPropertyNameToInput(SystemProperty.JABBER_SEND_NO_PRESENCE, cbSendIfNoPresense);
    inputMap.bindPropertyNameToInput(SystemProperty.JABBER_DISABLED, cbDisabled);

    // set default values
    flServerPort.setValue(Integer.toString(ConfigurationConstants.DEFAULT_JABBER_PORT));
  }


  /**
   * When called, the panel should switch to the
   * corresponding mode.
   *
   * @param viewMode to set.
   */
  public void setMode(final byte viewMode) {
    final boolean editable = viewMode == WebUIConstants.MODE_EDIT;
    cbDisabled.setEditable(editable);
    cbSendIfNoPresense.setEditable(editable);
    flLoginName.setEditable(editable);
    flLoginPassword.setEditable(editable);
    flServerAddress.setEditable(editable);
    flServerPort.setEditable(editable);
  }


  /**
   * Validates reuired inputs. If there are errors, shows
   * errors.
   *
   * @return true if valid
   */
  public boolean validate() {
    clearMessage();
    final List errors = new ArrayList(5);

    // validate fields are valid
    if (cbDisabled.isChecked()) {
      WebuiUtils.validateFieldNotBlank(errors, NAME_SERVER_ADDRESS, flServerAddress);
      WebuiUtils.validateFieldNotBlank(errors, NAME_LOGIN_NAME, flLoginName);
      WebuiUtils.validateFieldNotBlank(errors, NAME_PASSWORD, flLoginPassword);
      if (!WebuiUtils.isBlank(flServerPort)) {
        WebuiUtils.validateFieldValidPositiveInteger(errors, NAME_SERVER_PORT, flServerPort);
      }
    }

    // validate jabber login name
    //if (errors.size() == 0) {
    //  WebuiUtils.validateFieldValidEmail(errors, NAME_LOGIN_NAME, flServerAddress);
    //}

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
    final List systemProperties = systemCM.getSystemProperties();
    inputMap.setProperties(systemProperties);

    // REVIEWME: set default to work around the fact that check
    // boxes are reset. It sucks and we have to find why check
    // box values set in the consrtuctor get reset. I.e. this
    // if we call code in the constructor:
    //    cbSendIfNoPresense.setChecked(true);
    // , in this load method  cbSendIfNoPresense is again not
    // checked.
    boolean sendNoPresenseFound = false;
    for (final Iterator i = systemProperties.iterator(); i.hasNext();) {
      final SystemProperty property = (SystemProperty)i.next();
      if (property.getPropertyName().equals(SystemProperty.JABBER_SEND_NO_PRESENCE)) {
        sendNoPresenseFound = true;
        break;
      }
    }
    if (!sendNoPresenseFound) {
      cbSendIfNoPresense.setChecked(true);
    }
  }


  /**
   * Saves modified data.
   *
   * @return
   */
  public boolean save() {
    systemCM.saveSystemProperties(inputMap.getUpdatedProperties());
    return true;
  }


  public String toString() {
    return "JabberNotificationConfigPanel{" +
      "cbDisabled=" + cbDisabled +
      ", flLoginName=" + flLoginName +
      ", flServerAddress=" + flServerAddress +
      ", flServerPort=" + flServerPort +
      ", flLoginPassword=" + flLoginPassword +
      ", cbSendIfNoPresense=" + cbSendIfNoPresense +
      ", systemCM=" + systemCM +
      '}';
  }
}
