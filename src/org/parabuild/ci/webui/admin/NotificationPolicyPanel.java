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
package org.parabuild.ci.webui.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.MailUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.CheckBox;

import java.util.List;

final class NotificationPolicyPanel extends MessagePanel implements Validatable, Saveable, Loadable {

  private static final long serialVersionUID = -1003109916576534321L; // NOPMD
  private static final Log log = LogFactory.getLog(NotificationPolicyPanel.class);

  private static final String TEXT_USE_EMAIL = "Use version control e-mails:";
  private static final String TEXT_SEND_FAILURE_ONCE = "Send failures only once:";
  private static final String TEXT_EMAIL_DOMAIN = "Default E-mail domain:";
  private static final String TEXT_SUBJECT_PREFIX = "E-mail subject prefix:";
  private static final String TEXT_SEND_FAILURES_ONLY = "Send failures only:";
  private static final String TEXT_SEND_FILES = "Send file details:";
  private static final String TEXT_SEND_LAST_STEP_ONLY = "Send result for last step only:";
  private static final String TEXT_SEND_START_NOTICE_FOR_FIRST_STEP = "Send start notice for first step only:";
  private static final String TEXT_NOTIFY_WATCHERS_ONLY = "Notify watchers only: ";

  private final CommonFieldLabel lbUseVCSEmails = new CommonFieldLabel(TEXT_USE_EMAIL);

  private final CheckBox flUseVCSEmails = new CheckBox(); // NOPMD
  private final CheckBox flFailuresOnlyOnce = new CheckBox(); // NOPMD
  private final CheckBox flSendFailuresOnly = new CheckBox(); // NOPMD
  private final CheckBox flSendFileNames = new CheckBox(); // NOPMD
  private final CheckBox flSendStartNotice = new CheckBox(); // NOPMD
  private final CheckBox flSendLastBuildOnly = new CheckBox(); // NOPMD
  private final CheckBox flSendStartNoticeFirstStep = new CheckBox(); // NOPMD
  private final CheckBox flNotifyWatchersOnly = new CheckBox(); // NOPMD
  private final CheckBox flNotifyBuildAdmin = new CheckBox(); // NOPMD
  private final CommonField flEmailDomain = new CommonField(30, 30); // NOPMD
  private final CommonField flSubjectPrefix = new CommonField(30, 25); // NOPMD

  private final PropertyToInputMap inputMap = new PropertyToInputMap(false, new BuildAttributeHandler()); // strict map
  private int buildID = BuildConfig.UNSAVED_ID;
  private static final String CAPTION_SEND_START_NOTICE = "Send start notice:";
  private static final String CAPTION_NOTIFY_BUILD_ADMINISTRATOR = "Notify build administrator: ";


  /**
   * Creates message panel without title.
   *
   * @param viewMode
   */
  NotificationPolicyPanel(final byte viewMode) {
    super(true);

    final GridIterator gi = new GridIterator(getUserPanel(), 4);
    gi.addPair(new CommonFieldLabel(TEXT_EMAIL_DOMAIN), flEmailDomain);
    gi.addPair(new CommonFieldLabel(TEXT_SEND_FAILURES_ONLY), flSendFailuresOnly);
    gi.addPair(new CommonFieldLabel(TEXT_SUBJECT_PREFIX), flSubjectPrefix);
    gi.addPair(new CommonFieldLabel(TEXT_SEND_FAILURE_ONCE), flFailuresOnlyOnce);
    gi.addPair(lbUseVCSEmails, flUseVCSEmails);
    gi.addPair(new CommonFieldLabel(CAPTION_SEND_START_NOTICE), flSendStartNotice);
    gi.addPair(new CommonFieldLabel(TEXT_SEND_FILES), flSendFileNames);
    gi.addPair(new CommonFieldLabel(TEXT_SEND_START_NOTICE_FOR_FIRST_STEP), flSendStartNoticeFirstStep);
    gi.addPair(new CommonFieldLabel(TEXT_NOTIFY_WATCHERS_ONLY), flNotifyWatchersOnly);
    gi.addPair(new CommonFieldLabel(TEXT_SEND_LAST_STEP_ONLY), flSendLastBuildOnly);
    gi.addPair(new CommonFieldLabel(CAPTION_NOTIFY_BUILD_ADMINISTRATOR), flNotifyBuildAdmin);

    // bind props
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.MESSAGE_PREFIX, flSubjectPrefix);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.NOTIFY_BUILD_ADMINISTRATOR, flNotifyBuildAdmin);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.NOTIFY_WATCHERS_ONLY, flNotifyWatchersOnly);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.SEND_FAILURE_ONCE, flFailuresOnlyOnce);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.SEND_FAILURES_ONLY, flSendFailuresOnly);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.SEND_FILE_DETAILS, flSendFileNames);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.SEND_ONLY_LAST_STEP_RESULT, flSendLastBuildOnly);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.SEND_START_NOTICE, flSendStartNotice);
    inputMap.bindPropertyNameToInput(BuildConfigAttribute.SEND_START_NOTICE_FOR_FIRST_STEP_ONLY, flSendStartNoticeFirstStep);

    // load e-mail domain
    flSendFailuresOnly.setChecked(true);
    flSendLastBuildOnly.setChecked(true);
    flSendStartNotice.setChecked(true);
    flSendStartNoticeFirstStep.setChecked(true);
    flNotifyBuildAdmin.setChecked(true);

    // Set editability
    final boolean editable = viewMode == WebUIConstants.MODE_EDIT;
    flEmailDomain.setEditable(editable);
    flSendFailuresOnly.setEditable(editable);
    flSubjectPrefix.setEditable(editable);
    flFailuresOnlyOnce.setEditable(editable);
    flUseVCSEmails.setEditable(editable);
    flSendStartNotice.setEditable(editable);
    flSendFileNames.setEditable(editable);
    flSendStartNoticeFirstStep.setEditable(editable);
    flNotifyWatchersOnly.setEditable(editable);
    flSendLastBuildOnly.setEditable(editable);
    flNotifyBuildAdmin.setEditable(editable);
  }


  /**
   * When called, component should save it's content. This method
   * should return <code>true</code> when content of a component
   * is saved successfully. If not, a component should dispaly a
   * error message in it's area and return <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {
    ArgumentValidator.validateBuildIDInitialized(buildID);

    // save header
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildConfig buildConfig = cm.getBuildConfiguration(buildID);
    buildConfig.setEmailDomain(flEmailDomain.getValue());
    buildConfig.setSourceControlEmail(flUseVCSEmails.isChecked());
    cm.save(buildConfig);

    // save header attributes
    final List attrs = inputMap.getUpdatedProperties();
    cm.saveBuildAttributes(buildID, attrs);
    return true;
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {

    // validate domain
    flEmailDomain.setValue(flEmailDomain.getValue().trim());
    boolean valid = true;
    if (!StringUtils.isBlank(flEmailDomain.getValue())) {
      if (!MailUtils.isValidEmailDomain(flEmailDomain.getValue())) {
        showErrorMessage('\"' + TEXT_EMAIL_DOMAIN + "\" is invalid.");
        valid = false;
      }
    }

    return valid;
  }


  public void load(final BuildConfig buildConfig) {
    // set fields
    buildID = buildConfig.getBuildID();
    flEmailDomain.setValue(buildConfig.getEmailDomain());
    flUseVCSEmails.setChecked(buildConfig.getSourceControlEmail());

    // should we display "Use VCS e-mails"?
    final byte effectiveVCSID = getEffectiveVCSID(buildConfig);
    if (effectiveVCSID != BuildConfig.SCM_PERFORCE
            && effectiveVCSID != BuildConfig.SCM_CVS) {
      // no, hide it.
      flUseVCSEmails.setVisible(false);
      lbUseVCSEmails.setVisible(false);
    }

    // load mapped build attributes
    inputMap.setProperties(ConfigurationManager.getInstance().getBuildAttributes(buildConfig.getBuildID()));
  }


  private static byte getEffectiveVCSID(final BuildConfig buildConfig) {
    if (buildConfig.getSourceControl() == BuildConfig.SCM_REFERENCE) {
      final BuildConfig effectiveBuildConfig = ConfigurationManager.getInstance().getEffectiveBuildConfig(buildConfig);
      // cover-ass validation
      if (effectiveBuildConfig == null) {
        log.warn("Effective build config was null, returning undefined source control");
        return BuildConfig.SCM_UNDEFINED;
      }
      return effectiveBuildConfig.getSourceControl();
    } else {
      return buildConfig.getSourceControl();
    }
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  public String toString() {
    return "NotificationPolicyPanel{" +
            "lbUseVCSEmails=" + lbUseVCSEmails +
            ", flUseVCSEmails=" + flUseVCSEmails +
            ", flFailuresOnlyOnce=" + flFailuresOnlyOnce +
            ", flSendFailuresOnly=" + flSendFailuresOnly +
            ", flSendFileNames=" + flSendFileNames +
            ", flSendStartNotice=" + flSendStartNotice +
            ", flSendLastBuildOnly=" + flSendLastBuildOnly +
            ", flSendStartNoticeFirstStep=" + flSendStartNoticeFirstStep +
            ", flNotifyWatchersOnly=" + flNotifyWatchersOnly +
            ", flNotifyBuildAdmin=" + flNotifyBuildAdmin +
            ", flEmailDomain=" + flEmailDomain +
            ", flSubjectPrefix=" + flSubjectPrefix +
            ", inputMap=" + inputMap +
            ", buildID=" + buildID +
            '}';
  }
}
