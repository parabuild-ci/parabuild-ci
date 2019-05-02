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
package org.parabuild.ci.webui.admin.security;

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.object.User;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.admin.AbstractSystemConfigPanel;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.CheckBox;
import viewtier.ui.Label;
import viewtier.ui.Password;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


/**
 * System build panel
 */
public final class SecurityConfigPanel extends AbstractSystemConfigPanel {

  private static final long serialVersionUID = -6521251722629803915L; // NOPMD

  private static final String CAPTION_ANON_USERS = "Anonymous users:";
  private static final String CAPTION_ADMIN_PASSWD = "Admin password:";
  private static final String CAPTION_CONFIRM_ADMIN_PASSWD = "Confirm password:";
  private static final String CAPTION_ENABLE = "Enable:";
  private static final String CAPTION_HIDE_CHANGE_DESCRIPTIONS = "Hide change list descriptions:";
  private static final String CAPTION_SECURITY_SETTINGS = "Security Settings";
  private static final String CAPTION_ALLOW_ACCESS_TO_PROTECTED_FEEDS = "Allow access to protected feeds: ";
  private static final String CAPTION_HIDE_CHANGE_LIST_FILES = "Hide change list files:";

  private final CheckBox fldAnonUsersAllowed = new CheckBox();  // NOPMD
  private final CheckBox fldHideChangeDescriptions = new CheckBox();  // NOPMD
  private final CheckBox fldHideChangeFiles = new CheckBox();  // NOPMD
  private final CheckBox fldAllowAccessToProtectedFeeds = new CheckBox();  // NOPMD
  private final Password fldAdminPasswd = new Password(30, 30);  // NOPMD
  private final Password fldConfirmPasswd = new Password(30, 30);  // NOPMD

  private String adminEmail = null;


  public SecurityConfigPanel() {
    setTitle(CAPTION_SECURITY_SETTINGS);
    final GridIterator gi = new GridIterator(super.getUserPanel(), 2);
    gi.addPair(new CommonFieldLabel(CAPTION_ANON_USERS), new Label());
    gi.addPair(new CommonFieldLabel(CAPTION_ENABLE), fldAnonUsersAllowed);
    gi.addPair(new CommonFieldLabel(CAPTION_HIDE_CHANGE_DESCRIPTIONS), fldHideChangeDescriptions);
    gi.addPair(new CommonFieldLabel(CAPTION_HIDE_CHANGE_LIST_FILES), fldHideChangeFiles);
    gi.addPair(new CommonFieldLabel(CAPTION_ALLOW_ACCESS_TO_PROTECTED_FEEDS), fldAllowAccessToProtectedFeeds);
    gi.addBlankLine();
    gi.addPair(new CommonFieldLabel(CAPTION_ADMIN_PASSWD), new RequiredFieldMarker(fldAdminPasswd));
    gi.addPair(new CommonFieldLabel(CAPTION_CONFIRM_ADMIN_PASSWD), new RequiredFieldMarker(fldConfirmPasswd));

    // init property to input map
    inputMap.bindPropertyNameToInput(SystemProperty.ENABLE_ANONYMOUS_BUILDS, fldAnonUsersAllowed);
    inputMap.bindPropertyNameToInput(SystemProperty.HIDE_CHANGE_DESCRIPTIONS_FROM_ANONYMOUS, fldHideChangeDescriptions);
    inputMap.bindPropertyNameToInput(SystemProperty.ENABLE_ANONYMOUS_ACCESS_TO_PROTECTED_FEEDS, fldAllowAccessToProtectedFeeds);
    inputMap.bindPropertyNameToInput(SystemProperty.HIDE_CHANGE_FILES_FROM_ANONYMOUS, fldHideChangeFiles);
  }


  /**
   * Switches panel to a given view mode.
   *
   * @param viewMode to set.
   */
  public void setMode(final byte viewMode) {
    final boolean editable = viewMode == WebUIConstants.MODE_EDIT;
    fldAdminPasswd.setEditable(editable);
    fldAllowAccessToProtectedFeeds.setEditable(editable);
    fldAnonUsersAllowed.setEditable(editable);
    fldConfirmPasswd.setEditable(editable);
    fldHideChangeDescriptions.setEditable(editable);
    fldHideChangeFiles.setEditable(editable);
  }


  /**
   * Requests to load panel's data
   */
  public void load() {
    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
    setSystemProperties(systemCM.getSystemProperties());
    adminEmail = systemCM.getSystemPropertyValue(SystemProperty.BUILD_ADMIN_EMAIL, null);
  }


  /**
   * Validates reuired inputs. If there are errors, shows
   * errors.
   *
   * @return true if valid
   */
  public boolean validate() {
    // preExecute
    super.clearMessage();
    final List errors = new ArrayList(1);
    // validate
    WebuiUtils.validatePasswordFields(errors, CAPTION_ADMIN_PASSWD, fldAdminPasswd, CAPTION_CONFIRM_ADMIN_PASSWD, fldConfirmPasswd);
    // show errors if any
    if (errors.isEmpty()) {
      return true;
    }
    showErrorMessage(errors);
    return false;
  }


  /**
   * When called, component should save it's content. This method
   * should return <code>true</code> when content of a component
   * is saved successfully. If not, a component should display a
   * error message in it's area and return <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {

    // save props
    SystemConfigurationManagerFactory.getManager().saveSystemProperties(getSystemProperties());

    //
    // save admin user
    try {
      if (StringUtils.isBlank(adminEmail)) {
        throw new IllegalStateException("Can not save admin settings without admin e-mail set. Please contact customer support.");
      }
      if (!WebuiUtils.isBlank(fldAdminPasswd)) {
        // load admin user
        final SecurityManager sm = SecurityManager.getInstance();
        User adm = sm.getUserByName(User.DEFAULT_ADMIN_USER);
        if (adm == null) {
          adm = new User();
        }
        adm.setName(User.DEFAULT_ADMIN_USER);
        adm.setPassword(StringUtils.digest(fldAdminPasswd.getValue()));
        adm.setEmail(adminEmail.toLowerCase());
        adm.setAdmin(true);
        sm.save(adm);
      }

      // Update system property with admin e-mail
      final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
      final SystemProperty sp = scm.getSystemProperty(SystemProperty.BUILD_ADMIN_EMAIL);
      sp.setPropertyValue(adminEmail.toLowerCase());
      scm.saveSystemProperty(sp);

      return true;
    } catch (final NoSuchAlgorithmException e) {
      showErrorMessage("There was an error updating admin password: " + StringUtils.toString(e));
      return false;
    }
  }


  public String toString() {
    return "SecurityConfigPanel{" +
            "fldAnonUsersAllowed=" + fldAnonUsersAllowed +
            ", fldHideChangeDescriptions=" + fldHideChangeDescriptions +
            ", fldHideChangeFiles=" + fldHideChangeFiles +
            ", fldAllowAccessToProtectedFeeds=" + fldAllowAccessToProtectedFeeds +
            ", fldAdminPasswd=" + fldAdminPasswd +
            ", fldConfirmPasswd=" + fldConfirmPasswd +
            ", adminEmail='" + adminEmail + '\'' +
            '}';
  }
}
