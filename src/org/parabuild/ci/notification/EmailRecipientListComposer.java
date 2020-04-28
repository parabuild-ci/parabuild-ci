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

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.GlobalVCSUserMapManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.merge.MergeNag;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.BuildWatcher;
import org.parabuild.ci.object.GlobalVCSUserMap;
import org.parabuild.ci.object.Group;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.object.User;
import org.parabuild.ci.object.VCSUserToEmailMap;
import org.parabuild.ci.security.JNDIAuthenticator;
import org.parabuild.ci.security.JNDIUser;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.util.MailUtils;
import org.parabuild.ci.util.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Helper class to compose a list of e-mail recipients
 */
public final class EmailRecipientListComposer {

  private final JNDIAuthenticator jndiAuthenticator;
  private boolean notifyBuildAdmin = true;
  private Map versionControlMap;
  private final Map configuredMap = new HashMap(11);
  private String defaultDomain;
  private final List unmappedUsers = new ArrayList(1);
  private final List invalidEmails = new ArrayList(1);
  private final List toRecipients = new ArrayList(11);
  private final List bccRecipients = new ArrayList(11); // NOPMD
  private boolean caseSensitiveUserName = true;
  private boolean advancedStopNotification;


  public EmailRecipientListComposer() {
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    if (scm.isLDAPAuthenticationEnabled() && scm.isUseLDAPToLookUpVCSUserEmails()) {
      jndiAuthenticator = SecurityManager.getInstance().createJNDIAuthenticator();
    } else {
      jndiAuthenticator = null;
    }
  }


  /**
   * @param buildConfiguredMap Map of {@link VCSUserToEmailMap} objects.
   */
  public void addBuildConfiguredVCSUserToEmailMap(final Map buildConfiguredMap) {
    for (final Iterator iter = buildConfiguredMap.values().iterator(); iter.hasNext(); ) {
      final VCSUserToEmailMap vcsUserToEmailMap = (VCSUserToEmailMap) iter.next();
      final UserToEmailMap userToEmailMap = new UserToEmailMap(vcsUserToEmailMap.getUserName(),
              vcsUserToEmailMap.getUserEmail(), !vcsUserToEmailMap.getDisabled());
      configuredMap.put(vcsUserToEmailMap.getUserName().toLowerCase(), userToEmailMap);
    }
  }


  /**
   * @param globalMap List of {@link GlobalVCSUserMap} objects.
   */
  private void addGlobalVCSUserToEmailMap(final List globalMap) {
    for (final Iterator iter = globalMap.iterator(); iter.hasNext(); ) {
      final GlobalVCSUserMap globalUserToEmailMap = (GlobalVCSUserMap) iter.next();
      final UserToEmailMap userToEmailMap = new UserToEmailMap(globalUserToEmailMap.getVcsUserName(),
              globalUserToEmailMap.getEmail(), true);
      configuredMap.put(globalUserToEmailMap.getVcsUserName().toLowerCase(), userToEmailMap);
    }
  }


  public void setDefaultDomain(final String defaultDomain) {
    this.defaultDomain = defaultDomain;
  }


  /**
   * Sets case sensitivity of VCS user names. If false, VCS user names are converted
   * to lower case in the expectation of VCS user to email map be lower-case.
   * <p/>
   * Default is false.
   *
   * @param caseSensitiveUserName
   */
  public void setCaseSensitiveUserName(final boolean caseSensitiveUserName) {
    this.caseSensitiveUserName = caseSensitiveUserName;
  }


  /**
   * Sets the flag indicating that the stop notification should be sent only to build owners and to the user that
   * stopped the build.
   *
   * @param advancedStopNotification <code>true</code> if the stop notification should be sent only to build owners and
   *                                 to the user that stopped the build.
   */
  public void setAdvancedStopNotification(final boolean advancedStopNotification) {
    this.advancedStopNotification = advancedStopNotification;
  }


  public void setVersionControlMap(final Map versionControlMap) {
    this.versionControlMap = new HashMap(versionControlMap);
  }


  /**
   * Returns "To" list of InternetAddress
   */
  public List getToRecipients() {
    return Collections.unmodifiableList(toRecipients);
  }


  public List getUnmappedUsers() {
    return Collections.unmodifiableList(unmappedUsers);
  }


  public List getInvalidEmails() {
    return Collections.unmodifiableList(invalidEmails);
  }


  public void addSourceControlUser(final String user) throws AddressException {

    // map version control user to email
    String eMail = null;
    if (versionControlMap != null && !versionControlMap.isEmpty()) {
      eMail = (String) versionControlMap.get(caseSensitiveUserName ? user : user.toLowerCase());
    }


    // If is still unmapped, use configured map.
    if (StringUtils.isBlank(eMail)) {
      final UserToEmailMap userToEmailMap = (UserToEmailMap) configuredMap.get(user.trim().toLowerCase());
      if (userToEmailMap != null) {
        if (!userToEmailMap.isEnabled()) {
          return; // skip disabled
        }
        eMail = userToEmailMap.getEmail();
      }
    }

    // If still unmapped, use LDAP
    if (StringUtils.isBlank(eMail)) {
      if (jndiAuthenticator != null) {
        try {
          final JNDIUser jndiUser = jndiAuthenticator.getUser(user);
          if (jndiUser != null) {
            eMail = jndiUser.getEmail();
          }
        } catch (final Exception e) {
          reportErrorGettingUserEmailFromLDAP(user, e);
        }
      }
    }


    // Use the user name as e-mail if it is itself a valid e-mail. See
    // feature request PARABUILD-1449 for more information.
    if (StringUtils.isBlank(eMail)) {
      if (MailUtils.isValidEmail(user)) {
        eMail = user;
      }
    }

    // if still unmapped, compose email using default domain
    if (StringUtils.isBlank(eMail)) {
      if (StringUtils.isBlank(defaultDomain)) {
        // add to unmapped for later reporting
        unmappedUsers.add(user);
        return;
      } else {
        eMail = user + '@' + defaultDomain;
      }
    }

    // final cover-ass check
    if (!MailUtils.isValidEmail(eMail)) {
      invalidEmails.add(eMail);
      return;
    }

    // add to "To" list
    toRecipients.add(new InternetAddress(eMail));
  }


  /**
   * Adds list of build watchers to be placed in BCC list
   *
   * @param cutOffWatchLevel watchers are added only of their
   *                         watch level lesser than this cut off level.
   * @param watchers         List of {@link BuildWatcher} objects
   * @see BuildWatcher
   */
  public void addWatchers(final int cutOffWatchLevel, final List watchers) throws AddressException {
    // iterate watchers
    for (final Iterator i = watchers.iterator(); i.hasNext(); ) {
      final BuildWatcher watcher = (BuildWatcher) i.next();
      if (watcher.getDisabled()) {
        continue;
      }
      if (watcher.getLevel() > cutOffWatchLevel) {
        continue;
      }
      final String email = watcher.getEmail();
      if (MailUtils.isValidEmail(email)) {
        toRecipients.add(new InternetAddress(email));
      } else { // not a valid e-mail address
        // check if this is a name of a security group
        final SecurityManager sm = SecurityManager.getInstance();
        final Group groupByName = sm.getGroupByName(email);
//        if (LOG.isDebugEnabled()) LOG.debug("groupByName: " + groupByName);
        if (groupByName != null) {
          final List groupUsers = sm.getGroupUsers(groupByName.getID());
          for (final Iterator j = groupUsers.iterator(); j.hasNext(); ) {
            final User user = (User) j.next();
//            if (log.isDebugEnabled()) log.debug("user.getEmail(): " + user.getEmail());
            if (!StringUtils.isBlank(user.getEmail())) {
              toRecipients.add(new InternetAddress(user.getEmail()));
            }
          }
        } else {
          // not a group name
          addSourceControlUser(email);
        }
      }
    }
  }


  /**
   * @param buildRun
   * @param vcsMap
   * @param applyStartFilter
   * @param applyFirstStepStartFilter   this parameter should be set to true if
   *                                    composer should not compose receiver list if build's notification setting
   *                                    {@link BuildConfigAttribute#SEND_START_NOTICE_FOR_FIRST_STEP_ONLY}
   *                                    is checked. If this parameter set to false, composer proceeds ignoring
   *                                    the setting value.
   * @param applyStartFilter            if true Parabuild will consider
   *                                    {@link BuildConfigAttribute#SEND_START_NOTICE filter}.
   *                                    If set to false this filter is not applied. This
   *                                    parameter should be set to true if this is a start
   *                                    message.
   * @param applyLastStepResultFilter
   * @param applySendFailuresOnlyFilter
   * @param watchLevel
   * @return
   * @throws AddressException
   */
  public EmailRecipients makeRecipients(final BuildRun buildRun, final Map vcsMap,
                                        final boolean applyStartFilter, final boolean applyFirstStepStartFilter,
                                        final boolean applyLastStepResultFilter, final boolean applySendFailuresOnlyFilter,
                                        final int watchLevel) throws AddressException {


    final List resultTo = new ArrayList(23);
    final List resultBcc = new ArrayList(23);
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildRunConfig buildRunConfig = cm.getBuildRunConfig(buildRun);
    final int buildRunID = buildRun.getBuildRunID();

    notifyBuildAdmin = cm.getBuildAttributeValue(buildRun.getBuildID(), BuildConfigAttribute.NOTIFY_BUILD_ADMINISTRATOR, BuildConfigAttribute.OPTION_CHECKED).equals(BuildConfigAttribute.OPTION_CHECKED);
    final boolean sendFailuresOnly = cm.getBuildAttributeValue(buildRun.getBuildID(), BuildConfigAttribute.SEND_FAILURES_ONLY, BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED);
    final boolean sendFailureOnce = cm.getBuildAttributeValue(buildRun.getBuildID(), BuildConfigAttribute.SEND_FAILURE_ONCE, BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED);
    final boolean sendOnlyLastStep = cm.getBuildAttributeValue(buildRun.getBuildID(), BuildConfigAttribute.SEND_ONLY_LAST_STEP_RESULT, BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED);
    final boolean sendStartNotice = cm.getBuildAttributeValue(buildRun.getBuildID(), BuildConfigAttribute.SEND_START_NOTICE, BuildConfigAttribute.OPTION_CHECKED).equals(BuildConfigAttribute.OPTION_CHECKED);
    final boolean sendStartForFirstStepOnly = cm.getBuildAttributeValue(buildRun.getBuildID(), BuildConfigAttribute.SEND_START_NOTICE_FOR_FIRST_STEP_ONLY, BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED);
    final boolean notifyWatchersOnly = cm.getBuildAttributeValue(buildRun.getBuildID(), BuildConfigAttribute.NOTIFY_WATCHERS_ONLY, BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED);

    if (buildRun.successful() && applyLastStepResultFilter && sendOnlyLastStep) { // not last but should send lat only
      return new EmailRecipients(resultTo, resultBcc);
    }

    if (applyStartFilter && !sendStartNotice) {
      return new EmailRecipients(resultTo, resultBcc);
    }

    if (applyFirstStepStartFilter && sendStartForFirstStepOnly) {
      return new EmailRecipients(resultTo, resultBcc);
    }

    // Add global map
    addGlobalVCSUserToEmailMap(GlobalVCSUserMapManager.getInstance().getAllMappings());

    // Overwrite the global map with build-specific settings.
    addBuildConfiguredVCSUserToEmailMap(cm.getVCSUserToEmailMap(buildRun.getBuildID()));

    versionControlMap = new HashMap(vcsMap);

    // Try to init default domain
    defaultDomain = buildRunConfig.getEmailDomain();
    if (StringUtils.isBlank(defaultDomain)) {
      final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
      defaultDomain = scm.getSystemPropertyValue(SystemProperty.DEFAULT_EMAIL_DOMAIN, "");
    }


    // Check if should notify only admin and the user that started the build
    if (buildRun.isStopped() && advancedStopNotification) {

      // Add administrator to recipients
      addAdminAddress(resultTo);

      // Add user that stopped the build to recipients
      addUser(cm.getBuildRunAttributeValue(buildRunID, BuildRunAttribute.STOPPED_BY_USER_ID, (Integer) null), resultTo);

      // Done
      return new EmailRecipients(resultTo, resultBcc);
    }

    // Calculate newSubmitters only flag
    boolean newSubmittersOnly = false;
    if (sendFailureOnce && !buildRun.successful()) {
      final BuildRun prevBuildRun = cm.getPreviousBuildRun(buildRun);
      newSubmittersOnly = prevBuildRun != null && !prevBuildRun.successful()
              && prevBuildRun.getResultDescription().equals(buildRun.getResultDescription());
    }

    // add watchers
    if (!newSubmittersOnly && buildRunConfig.getAccess() == BuildConfig.ACCESS_PUBLIC) {
      addWatchers(watchLevel, cm.getWatchers(buildRun.getBuildID()));
    }

    // add participants
    if (levelAccepted(watchLevel, applySendFailuresOnlyFilter && sendFailuresOnly)) {

      // add administrator to recipients
      addAdminAddress(resultTo);

      // add build starter if any
      addUser(cm.getBuildRunAttributeValue(buildRunID, BuildRunAttribute.ATTR_STARTED_USER_ID, (Integer) null), toRecipients);

      // add participants
      if (!notifyWatchersOnly && buildRunConfig.getAccess() == BuildConfig.ACCESS_PUBLIC) {
        // get string VCS participant names
        final List participantNames = cm.getBuildParticipantsNames(buildRunID);
        // if needed, filter out only new ones
        if (newSubmittersOnly) {
          final BuildRun prevRun = cm.getPreviousBuildRun(cm.getBuildRun(buildRunID));
          if (prevRun != null) {
            final List prevParticipantsNames = cm.getBuildParticipantsNames(prevRun.getBuildRunID());
            // dumb traversal of two lists to remove prev names from current
            for (final Iterator prevIter = prevParticipantsNames.iterator(); prevIter.hasNext(); ) {
              final String prevName = (String) prevIter.next();
              for (final Iterator currIter = participantNames.iterator(); currIter.hasNext(); ) {
                final String currName = (String) currIter.next();
                if (currName.equals(prevName)) {
                  currIter.remove();
                }
              }
            }
          }
        }

        // add version control users
        for (final Iterator i = participantNames.iterator(); i.hasNext(); ) {
          addSourceControlUser((String) i.next());
        }
      }
    }

    // report problems if any
    reportUnmappedUsers(buildRunConfig, unmappedUsers);
    reportInvalidEmails(buildRunConfig, invalidEmails);

    // add to already prepared list
    resultTo.addAll(toRecipients);
    resultBcc.addAll(bccRecipients);

    // cleanup both lists so that there are no duplicates
    final EmailDuplicatesRemover duplicatesRemover = new EmailDuplicatesRemover();
    duplicatesRemover.removeDuplicates(resultTo);
    duplicatesRemover.removeDuplicates(resultBcc);

    // Remove disabled
    final DisabledEmailRemover disabledEmailRemover = new DisabledEmailRemover();
    disabledEmailRemover.removeDisabled(resultTo);
    disabledEmailRemover.removeDisabled(resultBcc);
    // if (LOG.isDebugEnabled()) LOG.debug("to recipients: " + resultTo);
    // if (LOG.isDebugEnabled()) LOG.debug("bcc recipients: " + resultBcc);

    return new EmailRecipients(resultTo, resultBcc);
  }


  /**
   * Adds a user email address (InternetAddress) to the list
   * @param userID
   * @param list
   * @throws AddressException
   */
  private static void addUser(final Integer userID, final List list) throws AddressException {

    if (userID == null) {
      return;
    }

    final User user = SecurityManager.getInstance().getUser(userID);
    if (user == null) {
      return;
    }

    final String startedEmail = user.getEmail();
    if (StringUtils.isBlank(startedEmail)) {
      return;
    }

    list.add(new InternetAddress(startedEmail));
  }


  private static boolean levelAccepted(final int watchLevel, final boolean sendFailuresOnly) {
    return watchLevel != BuildWatcher.LEVEL_SUCCESS || !sendFailuresOnly;
  }


  /**
   * Makes list of recipients.
   *
   * @param nagReport
   */
  public List makeRecipients(final List nagReport) throws AddressException {
    addAdminAddress(toRecipients);
    for (int i = 0; i < nagReport.size(); i++) {
      addSourceControlUser(((MergeNag) nagReport.get(i)).getUserName());
    }

    final EmailDuplicatesRemover duplicatesRemover = new EmailDuplicatesRemover();
    duplicatesRemover.removeDuplicates(toRecipients);

    final DisabledEmailRemover disabledEmailRemover = new DisabledEmailRemover();
    disabledEmailRemover.removeDisabled(toRecipients);

    return Collections.unmodifiableList(toRecipients);
  }


  /**
   * Makes recipients for error notifications relates to a particular build or nor build at all. This method is used to report system errors.
   *
   * @param buildID    build ID or -1 if not defined.
   * @param watchLevel
   * @return recipients
   */
  public EmailRecipients makeRecipients(final int buildID, final byte watchLevel) throws AddressException {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    notifyBuildAdmin = cm.getBuildAttributeValue(buildID, BuildConfigAttribute.NOTIFY_BUILD_ADMINISTRATOR,
            BuildConfigAttribute.OPTION_CHECKED).equals(BuildConfigAttribute.OPTION_CHECKED);
    addAdminAddress(toRecipients);
    addWatchers(watchLevel, cm.getWatchers(buildID));

    // Add users that have edit rights for this build
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    final boolean notifyUsersWithEditRightsAboutSystemErrors = scm.isNotifyUsersWithEditRightsAboutSystemErrors();
    if (buildID != BuildConfig.UNSAVED_ID && notifyUsersWithEditRightsAboutSystemErrors) {
      final int activeBuildID = ConfigurationManager.getInstance().getActiveIDFromBuildID(buildID);
      final List users = SecurityManager.getInstance().findUsersWithEditRights(activeBuildID);
      for (int i = 0; i < users.size(); i++) {
        final String email = ((User) users.get(i)).getEmail();
        if (MailUtils.isValidEmail(email)) {
          toRecipients.add(new InternetAddress(email));
        }
      }
    }

    // Remove disabled
    final DisabledEmailRemover disabledEmailRemover = new DisabledEmailRemover();
    disabledEmailRemover.removeDisabled(toRecipients);

    // Return result
    return new EmailRecipients(toRecipients, Collections.emptyList());
  }


  /**
   * Helper method.
   */
  private void addAdminAddress(final List resultTo) throws AddressException {
    if (!notifyBuildAdmin) {
      return;
    }
    resultTo.addAll(NotificationUtils.getAdminAddressList(true));
  }


  /**
   * Reports invalid emails to administrative errors list
   */
  private static void reportInvalidEmails(final BuildConfig buildConfig, final List invalidEmails) {
    final int emailCount = invalidEmails.size();
    if (emailCount == 0) {
      return;
    }

    // preExecute details
    final StringBuffer sb = new StringBuffer(100);
    sb.append("Following invalid e-mail(s) were detected while composing a list of recipients: ");
    for (int i = 0; i < emailCount; i++) {
      sb.append((String) invalidEmails.get(i));
      sb.append(i == emailCount - 1 ? "." : ", "); // NOPMD
    }

    final Error error = NotificationUtils.makeNotificationWarning(buildConfig.getBuildName());
    error.setDescription("Invalid e-mail(s) detected while composing a list of recipients.");
    error.setPossibleCause("Invalid version control user to e-mail mapping(s).");
    error.setDetails(sb);
    error.setBuildID(buildConfig.getActiveBuildID());
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  /**
   * Reports unmapped version control users
   */
  private static void reportUnmappedUsers(final BuildConfig buildConfig, final List unmappedUsers) {
    final int userCount = unmappedUsers.size();
    if (userCount == 0) {
      return;
    }

    // preExecute details
    final StringBuffer details = new StringBuffer(100);
    details.append("Was not able to find or create e-mail(s) for the following version control users: ");
    for (int i = 0; i < userCount; i++) {
      details.append((String) unmappedUsers.get(i));
      details.append(i == userCount - 1 ? "." : ", "); // NOPMD
    }

    // report error
    final Error error = NotificationUtils.makeNotificationWarning(buildConfig.getBuildName());
    error.setDescription("E-mail(s) for version control users can not be found.");
    error.setDetails(details);
    error.setPossibleCause("Default e-mail domain for this build is not set.");
    error.setBuildID(buildConfig.getActiveBuildID());
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  /**
   * Reports exception while mapping user email from LDAP.
   *
   * @param user user
   * @param e    exception
   */
  private static void reportErrorGettingUserEmailFromLDAP(final String user, final Exception e) {
    final Error error = new Error("Could not map version control user " + user + " to e-mail using LDAP: " + StringUtils.toString(e));
    error.setDetails(e);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSendEmail(true);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  public String toString() {
    return "EmailRecipientListComposer{" +
            "notifyBuildAdmin=" + notifyBuildAdmin +
            ", versionControlMap=" + versionControlMap +
            ", configuredMap=" + configuredMap +
            ", defaultDomain='" + defaultDomain + '\'' +
            ", unmappedUsers=" + unmappedUsers +
            ", invalidEmails=" + invalidEmails +
            ", toRecipients=" + toRecipients +
            ", bccRecipients=" + bccRecipients +
            '}';
  }
}
