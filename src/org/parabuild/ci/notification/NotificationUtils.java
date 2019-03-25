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
package org.parabuild.ci.notification;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.BuildWatcher;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.object.User;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.security.SecurityManager;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Notification utilities.
 */
final class NotificationUtils {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(NotificationUtils.class); // NOPMD


  /**
   * Utility constructor.
   */
  private NotificationUtils() {
  }


  /**
   * Makes InternetAddress from admin system properties.
   *
   * @return InternetAddress build admin address
   */
  public static InternetAddress getSystemAdminAddress() throws AddressException {
    try {
      final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
      final String emailProp = systemCM.getSystemPropertyValue(SystemProperty.BUILD_ADMIN_EMAIL, null);
      if (StringUtils.isBlank(emailProp)) {
        throw new AddressException("Build administrator e-mail is not set");
      }
      final String nameProp = systemCM.getSystemPropertyValue(SystemProperty.BUILD_ADMIN_NAME, emailProp);
      return new InternetAddress(emailProp, nameProp);
    } catch (final UnsupportedEncodingException e) {
      final AddressException ae = new AddressException(StringUtils.toString(e));
      e.initCause(e);
      throw ae;
    }
  }


  /**
   * Helper method.
   */
  public static Error makeNotificationError() {
    return makeNotificationError(null);
  }


  public static void reportErrorSendingSystemAlert(final Exception e) {
    final Error err = makeNotificationError();
    err.setDescription("Error while sending system alert notification");
    err.setDetails(e);
    ErrorManagerFactory.getErrorManager().reportSystemError(err);
  }


  public static void reportErrorSendingMessage(final Exception e) {
    final Error err = makeNotificationError();
    err.setDescription("Error while sending an e-mail message:" + StringUtils.toString(e));
    err.setDetails(e);
    ErrorManagerFactory.getErrorManager().reportSystemError(err);
  }


  public static void reportErrorSendingBuildFailure(final BuildRun buildRun, final Exception ex) {
    final Error error = makeNotificationError(buildRun, ex);
    error.setDescription("Error while sending notification about build failure.");
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  /**
   * @param buildRun build run object. Can be null.
   * @param stepRun  step run object. Cannot be null.
   * @param e
   */
  public static void reportErrorSendingStepFinished(final BuildRun buildRun, final StepRun stepRun, final Exception e) {
    final Error error = makeNotificationError();
    error.setDescription("Error while sending build finished notification");
    error.setStepName(stepRun.getName());
    error.setDetails(e);
    error.setSendEmail(false);
    setBuildRun(error, buildRun);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  public static int stepRunResultToWatchLevel(final StepRun stepRun) {
    final boolean buildFix = ConfigurationManager.getInstance().stepFixedPreviousBreakage(stepRun);
    int watchLevel = BuildWatcher.LEVEL_BROKEN;
    if (stepRun.getResultID() == BuildRun.BUILD_RESULT_BROKEN || (stepRun.isSuccessful() && buildFix))
      watchLevel = BuildWatcher.LEVEL_BROKEN;
    if (stepRun.getResultID() == BuildRun.BUILD_RESULT_SUCCESS) watchLevel = BuildWatcher.LEVEL_SUCCESS;
    if (stepRun.getResultID() == BuildRun.BUILD_RESULT_SYSTEM_ERROR) watchLevel = BuildWatcher.LEVEL_SYSTEM_ERROR;
    if (stepRun.getResultID() == BuildRun.BUILD_RESULT_TIMEOUT) watchLevel = BuildWatcher.LEVEL_BROKEN;
    return watchLevel;
  }


  public static void reportErrorSendingStepHung(final BuildRun buildRun, final BuildSequence sequence, final Exception e) {
    final Error error = makeNotificationError(buildRun, e);
    error.setDescription("Error while sending system alert notification");
    error.setStepName(sequence.getStepName());
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  public static void reportErrorSendingStepStarted(final BuildRun buildRun, final BuildSequence buildSequence, final Exception e) {
    final Error error = makeNotificationError(buildRun, e);
    error.setDescription("Error while sending build start notification");
    error.setStepName(buildSequence.getStepName());
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  /**
   * Helper method.
   */
  public static Error makeNotificationWarning(final String buildName) {
    final Error error = new Error();
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_NOTIFICATION);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setBuildName(buildName);
    return error;
  }


  /**
   * Helper method.
   */
  private static Error makeNotificationError(final BuildRun buildRun, final Exception e) {
    final Error error = makeNotificationError(buildRun);
    error.setDetails(e);
    return error;
  }


  /**
   * Helper method. BuildRun can be null
   */
  private static Error makeNotificationError(final BuildRun buildRun) {
    final Error error = new Error();
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_NOTIFICATION);
    error.setErrorLevel(Error.ERROR_LEVEL_ERROR);
    error.setSendEmail(false);
    setBuildRun(error, buildRun);
    return error;
  }


  /**
   * Helper method.
   *
   * @param error
   * @param buildRun
   */
  private static void setBuildRun(final Error error, final BuildRun buildRun) {
    // check param
    if (buildRun == null) {
      return;
    }
    // set name if present and exit
    if (!StringUtils.isBlank(buildRun.getBuildName())) {
      error.setBuildName(buildRun.getBuildName());
      return;
    }
    // set build id if present and exit
    if (buildRun.getBuildID() != BuildConfig.UNSAVED_ID) {
      error.setBuildID(buildRun.getBuildID());
    }
  }

  /**
   * Returns a list of emails of admin users.
   *
   * @param addSystemAdminAddress tru if system admin should be added
   * @return list of emails of admin users.
   * @throws AddressException
   * @throws UnsupportedEncodingException
   */
  public static Collection getAdminAddresslList(final boolean addSystemAdminAddress) throws AddressException {
    try {
      // Add system admin email
      final List result = new ArrayList(11);
      if (addSystemAdminAddress) {
        result.add(getSystemAdminAddress());
      }
      // Add all enabled admin users emails
      final List adminUsers = SecurityManager.getInstance().getAdminUsers();
      for (int i = 0; i < adminUsers.size(); i++) {
        final User user = (User) adminUsers.get(i);
        result.add(new InternetAddress(user.getEmail(), user.getFullName()));
      }
      return result;
    } catch (final UnsupportedEncodingException e) {
      final AddressException ae = new AddressException(StringUtils.toString(e));
      ae.initCause(e);
      throw ae;
    }
  }


  public static boolean isCaseSensitiveUserName(final int buildID) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    final String value = cm.getSourceControlSettingValue(buildID, SourceControlSetting.P4_CASE_SENSITIVE_USER_NAMES,
            scm.isCaseSensitiveVCSNames() ? SourceControlSetting.OPTION_CHECKED : SourceControlSetting.OPTION_UNCHECKED);
    return value.equals(SourceControlSetting.OPTION_CHECKED);
  }
}
