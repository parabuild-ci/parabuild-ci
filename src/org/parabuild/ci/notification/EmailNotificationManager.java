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
import org.parabuild.ci.archive.ArchiveEntry;
import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.common.BuildStatusURLGenerator;
import org.parabuild.ci.common.CommonConstants;
import org.parabuild.ci.common.RuntimeUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.configuration.SystemConstants;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.merge.MergeNag;
import org.parabuild.ci.object.ActiveMergeConfiguration;
import org.parabuild.ci.object.BranchMergeConfiguration;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.object.BuildWatcher;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.Issue;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.object.User;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.WebuiUtils;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Notification manager is responsible for providing notification
 * services via e-mail.
 */
final class EmailNotificationManager implements NotificationManager, CommonConstants {

  private static final Log log = LogFactory.getLog(EmailNotificationManager.class);
  private static final String STR_DIVIDER = "------------------------------------------------------------";

  // various service managers used by this class
  private final ConfigurationManager cm = ConfigurationManager.getInstance();


  // fields
  private Map vcsUserMap = null;
  private boolean enabled = true;


  /**
   * If true, e-mail will be sent. If false, e-mails won'tbe
   * sent.
   */
  public void enableNotification(final boolean enabled) {
    this.enabled = enabled;
  }


  public void notifyMergeFailedBecauseOfConflicts(final BranchMergeConfiguration mergeConfiguration, final ChangeList changeList, final List conflicts) {
    // REVIEWME: simeshev@parabuilci.org -> implement
  }


  /**
   * Sends out notification that build sequence started. This
   * method should not throw exceptions as this is the last point
   * point where notification can be delivered. If implementing
   * method encounters exceptional conditions, it should use
   * ErrorManager to report errors.
   *
   * @param buildRun      to report
   * @param buildSequence to report
   * @see ErrorManager
   */
  public void notifyBuildStepStarted(final BuildRun buildRun, final BuildSequence buildSequence) {
    try {

      // Compose subject

      final StringBuffer messageSubject = new StringBuffer(200);
      messageSubject.append(makePrefix(buildRun.getBuildID()));
      messageSubject.append(new MessageSubjectGenerator().makeStartedSubject(buildRun, buildSequence));

      // compose body

      final StringBuffer messageBody = makeStartedBody(buildRun, buildSequence);

      // send message

      final EmailRecipientListComposer composer = new EmailRecipientListComposer();
      composer.setCaseSensitiveUserName(NotificationUtils.isCaseSensitiveUserName(buildRun.getBuildID()));
      final BuildStepType type = buildRun.getType() == BuildRun.TYPE_PUBLISHING_RUN ? BuildStepType.PUBLISH : BuildStepType.BUILD;
      final boolean notFirstStepStart = !cm.isFirstBuildSequence(buildRun.getBuildID(), type, buildSequence.getStepName());
      final int watchLevel = (int) BuildWatcher.LEVEL_SUCCESS;
      final EmailRecipients recipients = composer.makeRecipients(buildRun, vcsUserMap, true, notFirstStepStart, false, true, watchLevel);
      sendMessage(recipients, messageSubject, messageBody, SystemProperty.MESSAGE_PRIORITY_NORMAL);
    } catch (final Exception e) {
      NotificationUtils.reportErrorSendingStepStarted(buildRun, buildSequence, e);
    }
  }


  /**
   * Sends out notification that build sequence hung. This method
   * should not throw exceptions as this is the last point point
   * where notification can be delivered. If implementing method
   * encounters exceptional conditions, it should use
   * ErrorManager to report errors.
   *
   * @param buildRun to report
   * @param sequence to report
   * @see ErrorManager
   */
  public void notifyBuildStepHung(final BuildRun buildRun, final BuildSequence sequence) {
    try {

      // compose subject
      final BuildConfig buildConfig = cm.getBuildRunConfig(buildRun);
      final StringBuffer subject = new StringBuffer(100);
      subject.append(makePrefix(buildConfig.getBuildID()));
      subject.append("BUILD SYSTEM ERROR:").append(' ');
      subject.append("Build ").append(buildConfig.getBuildName())
              .append(" (#").append(buildRun.getBuildRunNumberAsString()).append(") has hung.");

      // compose body
      final StringBuffer body = new StringBuffer(400);
      body.append("BUILD SYSTEM ERROR").append(STR_CR);

      // Add message
      body.append(STR_DIVIDER).append(STR_CRCR);
      body.append("Build hung at step \"").append(sequence.getStepName()).append("\" after ").append(sequence.getTimeoutMins()).append(" minutes timeout.").append(STR_CR);
      body.append("Parabuild attempted and failed to stop the build.").append(STR_CR);
      body.append("System requires immediate attention of the build administrator.").append(STR_CR);
      body.append("Build won't start until the problem is fixed.").append(STR_CR);
      body.append(STR_CRCR);
      body.append("Possible cause:").append(STR_CRCR);
      body.append("  Build script spawns long-running process(es) that cannot be identified \n");
      body.append("  as belonging to the build sequence.");

      // Add build details
      body.append(STR_DIVIDER).append(STR_CRCR);
      body.append("Build Details").append(STR_CR);
      final String buildRunNumber = buildRun.getBuildRunNumber() >= 0 ? buildRun.getBuildRunNumberAsString() : "";
      final SimpleDateFormat formatter = createFormatter();
      final String startedAt = buildRun.getStartedAt() != null ? formatter.format(buildRun.getStartedAt()) : "";
      StringUtils.appendWithNewLineIfNotNull(body, "Build name:", buildRun.getBuildName());
      StringUtils.appendWithNewLineIfNotNull(body, "Build host:", cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.AGENT_HOST));
      StringUtils.appendWithNewLineIfNotNull(body, "Build number:", buildRunNumber);
      StringUtils.appendWithNewLineIfNotNull(body, "Started at:", startedAt);
      StringUtils.appendWithNewLineIfNotNull(body, "Change list:", buildRun.getChangeListNumber());
      StringUtils.appendWithNewLineIfNotNull(body, "Sync to this build:", buildRun.getSyncNote());

      // send message
      final EmailRecipientListComposer composer = new EmailRecipientListComposer();
      composer.setCaseSensitiveUserName(NotificationUtils.isCaseSensitiveUserName(buildRun.getBuildID()));
      final int curOffWatchLevel = (int) BuildWatcher.LEVEL_SYSTEM_ERROR;
      final EmailRecipients recipients = composer.makeRecipients(buildRun, vcsUserMap, false, false, false, true, curOffWatchLevel);
      final byte messagePriority = SystemConfigurationManagerFactory.getManager().getSystemPropertyValue(SystemProperty.MESSAGE_PRIORITY_FAILED_BUILD, SystemProperty.MESSAGE_PRIORITY_NORMAL);
      sendMessage(recipients, subject, body, messagePriority);
    } catch (final RuntimeException | MessagingException ex) {
      NotificationUtils.reportErrorSendingBuildFailure(buildRun, ex);
    }
  }


  private static SimpleDateFormat createFormatter() {
    return new SimpleDateFormat(SystemConfigurationManagerFactory.getManager().getDateTimeFormat(), Locale.US);
  }


  /**
   * Sends out notification about build sequence results. This
   * method should not throw exceptions as this is the last point
   * point where notification can be delivered. If implementing
   * method encounters exceptional conditions, it should use
   * ErrorManager to report errors.
   *
   * @param stepRun to report
   * @see ErrorManager
   */
  public void notifyBuildStepFinished(final StepRun stepRun) {

    BuildRun buildRun = null;
    try {
      
      final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();

      // Compose subject and body
      final int buildRunID = stepRun.getBuildRunID();
      buildRun = cm.getBuildRun(buildRunID);
      final StringBuffer messageSubject = new MessageSubjectGenerator().makeFinishedSubject(stepRun).insert(0, makePrefix(buildRun.getBuildID()));
      final StringBuffer messageBody = makeFinishedBody(stepRun);

      // Identify is this is a step that fixes previous
      // breakage.
      final boolean stepFixedPreviousBreakage = cm.stepFixedPreviousBreakage(stepRun);

      final boolean applySendFailuresOnlyFilter = !stepFixedPreviousBreakage;

      // Identify if this is the last build
      final BuildStepType type = buildRun.getType() == BuildRun.TYPE_PUBLISHING_RUN ? BuildStepType.PUBLISH : BuildStepType.BUILD;
      final boolean lastBuildStep = cm.isLastEnabledBuildSequence(buildRun.getBuildID(), type, stepRun.getName());


      final boolean applyLastStepResultFilter = !lastBuildStep && !stepFixedPreviousBreakage;

      // Send message
      final boolean caseSensitiveUserName = NotificationUtils.isCaseSensitiveUserName(buildRun.getBuildID());
      final boolean advancedStopNotification = scm.getSystemPropertyValue(SystemProperty.ADVANCED_STOP_NOTIFICATION, SystemProperty.OPTION_UNCHECKED).equalsIgnoreCase(SystemProperty.OPTION_CHECKED);

      final EmailRecipientListComposer composer = new EmailRecipientListComposer();
      composer.setCaseSensitiveUserName(caseSensitiveUserName);
      composer.setAdvancedStopNotification(advancedStopNotification);

      final int cutOffWatchLevel = NotificationUtils.stepRunResultToWatchLevel(stepRun);
      final EmailRecipients recipients = composer.makeRecipients(cm.getBuildRun(buildRunID), vcsUserMap, false, false, applyLastStepResultFilter, applySendFailuresOnlyFilter, cutOffWatchLevel);

      final byte failedBuildMessagePriority = scm.getSystemPropertyValue(SystemProperty.MESSAGE_PRIORITY_FAILED_BUILD, SystemProperty.MESSAGE_PRIORITY_NORMAL);
      final byte messagePriority = stepRun.isSuccessful() ? SystemProperty.MESSAGE_PRIORITY_NORMAL : failedBuildMessagePriority;

      sendMessage(recipients, messageSubject, messageBody, messagePriority);
    } catch (final Exception e) {
      NotificationUtils.reportErrorSendingStepFinished(buildRun, stepRun, e);
    }
  }


  /**
   * Sends notification about error to build administrator
   *
   * @see
   */
  public void notifyBuildAdministrator(final Error error) {

    try {
      // subject
      final StringBuffer subj = new StringBuffer(100);
      subj.append(makePrefix());
      subj.append("Parabuild alert:" + ' ');
      subj.append(error.getDescription());

      // body
      final StringBuffer body = new StringBuffer(200);
      body.append(STR_CR);
      body.append("Parabuild alert: ");
      body.append(STR_CR);
      body.append(STR_DIVIDER);
      body.append(STR_CRCR);
      body.append("This is to notify you that the build server encountered\nan error that requires attention of a build administrator.");
      body.append(STR_CRCR);
      body.append("Error details");
      body.append(STR_CR);
      body.append(STR_DIVIDER);
      body.append(STR_CRCR);
      StringUtils.appendWithNewLineIfNotNull(body, "Product:", error.getProductVersion());
      StringUtils.appendWithNewLineIfNotNull(body, "Description:", error.getDescription());
      StringUtils.appendWithNewLineIfNotNull(body, "Severity:", error.getErrorLevelAsString());
      StringUtils.appendWithNewLineIfNotNull(body, "Build name:", error.getBuildName());
      StringUtils.appendWithNewLineIfNotNull(body, "Build step name:", error.getStepName());
      StringUtils.appendWithNewLineIfNotNull(body, "Host name:", error.getHostName());
      StringUtils.appendWithNewLineIfNotNull(body, "Subsystem:", error.getSubsystemName());
      StringUtils.appendWithNewLineIfNotNull(body, "Details:", error.getDetails());
      StringUtils.appendWithNewLineIfNotNull(body, "Log line:", error.getLogLines());
      StringUtils.appendWithNewLineIfNotNull(body, "Possible cause:", error.getPossibleCause());
      StringUtils.appendWithNewLineIfNotNull(body, "Trace:", error.getStacktrace());

      // add administrator to recipients
      final List toRecipients = new ArrayList(1);
      toRecipients.addAll(NotificationUtils.getAdminAddresslList(true));

      // Create recipients
      final EmailRecipientListComposer composer = new EmailRecipientListComposer();
      final EmailRecipients recipients = composer.makeRecipients(error.getBuildID(), BuildWatcher.LEVEL_SYSTEM_ERROR);

      // Send
      final byte messagePriority = SystemConfigurationManagerFactory.getManager()
              .getSystemPropertyValue(SystemProperty.MESSAGE_PRIORITY_SYSTEM_ERROR,
                      SystemProperty.MESSAGE_PRIORITY_NORMAL);
      sendMessage(recipients, subj, body, messagePriority);
    } catch (final Exception e) {
      NotificationUtils.reportErrorSendingSystemAlert(e);
    }
  }


  /**
   * Sends user password in accordance with user email in the
   * DB.
   */
  public void sendUserPassword(final String userName, final String newPassword) {
    try {
      final User user = SecurityManager.getInstance().getUserByName(userName);
      if (user != null) {
        // compose subject
        final List to = new ArrayList(1);
        to.add(new InternetAddress(user.getEmail()));
        final StringBuffer subj = new StringBuffer(100);
        subj.append(makePrefix());
        subj.append("Your new password");
        // compose message
        final StringBuffer msg = new StringBuffer(200);
        msg.append("You or someone else has requested to reset your password").append(STR_CR);
        msg.append(STR_DIVIDER).append(STR_CRCR);
        msg.append("Your new password is: ").append(newPassword);
        msg.append("\n\n");
        msg.append("---------------");
        msg.append('\n');
        msg.append("Build Administrator");
        // send
        sendMessage(new EmailRecipients(to, Collections.EMPTY_LIST), subj, msg, SystemProperty.MESSAGE_PRIORITY_NORMAL);
      } else {
        log.warn("Could not send notification to user \"" + user + "\" - user does not exist");
      }
    } catch (final Exception e) {
      reportErrorSendingPassword(e);
    }
  }


  /**
   * Sends out notification that build run failed with system
   * error. This method should not throw exceptions as this is
   * the last point point where notification can be delivered. If
   * implementing method encounters exceptional conditions, it
   * should use ErrorManager to report errors.
   *
   * @param buildRun that has failed
   * @param e        Exception at failure
   * @see ErrorManager
   */
  public void notifyBuildStepFailed(final BuildRun buildRun, final Exception e) {
    try {

      // make subject
      final StringBuffer subj = new StringBuffer(100);
      subj.append(makePrefix(buildRun.getBuildID()));
      subj.append(buildRun.getBuildName()).append(" (#").append(buildRun.getBuildRunNumberAsString())
              .append(") FAILED due to a system error.");

      // make body
      final StringBuffer body = new StringBuffer(200);
      body.append(subj);
      body.append(STR_CRCR);
      body.append("Build administrator has been notified about the error.");
      body.append(STR_CRCR);

      // add build details for body
      body.append("Build Details");
      body.append(STR_CR);
      body.append(STR_DIVIDER);
      body.append(STR_CRCR);
      final String buildRunNumber = buildRun.getBuildRunNumber() >= 0 ? buildRun.getBuildRunNumberAsString() : "";
      final SimpleDateFormat formatter = createFormatter();
      final String startedAt = buildRun.getStartedAt() != null ? formatter.format(buildRun.getStartedAt()) : "";
      final String finishedAt = buildRun.getFinishedAt() != null ? formatter.format(buildRun.getFinishedAt()) : "";
      StringUtils.appendWithNewLineIfNotNull(body, "Build name:", buildRun.getBuildName());
      StringUtils.appendWithNewLineIfNotNull(body, "Build host:", cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.AGENT_HOST));
      StringUtils.appendWithNewLineIfNotNull(body, "Build number:", buildRunNumber);
      StringUtils.appendWithNewLineIfNotNull(body, "Started at:", startedAt);
      StringUtils.appendWithNewLineIfNotNull(body, "Finished at:", finishedAt);
      StringUtils.appendWithNewLineIfNotNull(body, "Change list:", buildRun.getChangeListNumber());
      StringUtils.appendWithNewLineIfNotNull(body, "Sync to this build:", buildRun.getSyncNote());

      // send message
      final EmailRecipientListComposer composer = new EmailRecipientListComposer();
      composer.setCaseSensitiveUserName(NotificationUtils.isCaseSensitiveUserName(buildRun.getBuildID()));
      final int cutOffWatchLevel = (int) BuildWatcher.LEVEL_BROKEN;
      final EmailRecipients recipients = composer.makeRecipients(buildRun, vcsUserMap, false, false, false, true, cutOffWatchLevel);
      final byte messagePriority = buildRun.getResultID() == BuildRun.BUILD_RESULT_SUCCESS ? SystemProperty.MESSAGE_PRIORITY_NORMAL : SystemConfigurationManagerFactory.getManager().getSystemPropertyValue(SystemProperty.MESSAGE_PRIORITY_FAILED_BUILD, SystemProperty.MESSAGE_PRIORITY_NORMAL);
      sendMessage(recipients, subj, body, messagePriority);
    } catch (final RuntimeException | MessagingException ex) {
      NotificationUtils.reportErrorSendingBuildFailure(buildRun, ex);
    }
  }


  /**
   * Sends synchronous test e-mail notification message
   *
   * @param properties - list of SystemProperty objects.
   * @see SystemProperty#BUILD_ADMIN_EMAIL
   * @see SystemProperty#BUILD_ADMIN_NAME
   * @see SystemProperty#SMTP_SERVER_NAME
   * @see SystemProperty#SMTP_SERVER_PASSWORD
   * @see SystemProperty#SMTP_SERVER_PORT
   * @see SystemProperty#SMTP_SERVER_USER
   */
  public void sendTestEmailMessage(final List properties) throws MessagingException {
    try {
      // get props
      final Properties emailPropertiesValues = systemPropertiesToValues(properties);
      // get admin e-mail
      final String email = emailPropertiesValues.getProperty(SystemProperty.BUILD_ADMIN_EMAIL, null);
      if (StringUtils.isBlank(email)) {
        throw new AddressException("Build administrator e-mail is not set");
      }
      // get admin name
      final String nameProp = emailPropertiesValues.getProperty(SystemProperty.BUILD_ADMIN_NAME, email);
      final InternetAddress adminAddress = new InternetAddress(email, nameProp);
      // compose recipients list
      final List to = new ArrayList(1);
      to.add(adminAddress);
      // send
      sendMessage(properties, adminAddress, new EmailRecipients(to, Collections.EMPTY_LIST), new StringBuffer(20).append("Test message from Parabuild server"), new StringBuffer(20).append("This is a test message from Parabuild server."), SystemProperty.MESSAGE_PRIORITY_NORMAL);
    } catch (final MessagingException e) {
      throw e;
    } catch (final Exception e) {
      throw new MessagingException(StringUtils.toString(e), e);
    }
  }


  /**
   * Sends a nag to those in the merge queue defined by activeMergeID
   *
   * @param activeMergeID an ID of merge configuration.
   */
  public void notifyChangeListsWaitingForMerge(final int activeMergeID) {
    try {
      // get change list users, numbers and descriptions ordered by user and number
      final SimpleDateFormat sdf = new SimpleDateFormat(SystemConfigurationManagerFactory.getManager().getDateTimeFormat());
      final ActiveMergeConfiguration activeMergeConfiguration = MergeManager.getInstance().getActiveMergeConfiguration(activeMergeID);

      // create subject
      final StringBuffer subject = new StringBuffer('[' + activeMergeConfiguration.getName() + ']' + "Reminder: Some changes have to be integrated");

      // create body
      final StringBuffer body = new StringBuffer(1000);
      body.append(STR_CR);
      body.append("The following changes have to be integrated").append(STR_CR);
      body.append("Branch specification name: ").append(activeMergeConfiguration.getBranchViewName()).append(STR_CR);
      body.append("      Reverse branch view: ").append(activeMergeConfiguration.isReverseBranchView()).append(STR_CR);
      body.append(STR_DIVIDER).append(STR_CR);
      final List nagReport = MergeManager.getInstance().getNagReport(activeMergeID);
      for (int i = 0; i < nagReport.size(); i++) {
        final MergeNag nag = (MergeNag) nagReport.get(i);
        body.append("User name: ").append(nag.getUserName()).append(STR_CR);
        final List pendingChangeLists = nag.getPendingChangeLists();
        body.append("  Change(s): ").append(changeListToString((ChangeList) pendingChangeLists.get(0), sdf)).append(STR_CR);
        for (int j = 1; j < pendingChangeLists.size(); j++) {
          body.append("           ").append(changeListToString((ChangeList) pendingChangeLists.get(j), sdf)).append(STR_CR);
        }
        body.append(STR_DIVIDER).append(STR_CR);
      }

      // make e-mail addresses
      final EmailRecipientListComposer composer = new EmailRecipientListComposer();
      composer.setCaseSensitiveUserName(NotificationUtils.isCaseSensitiveUserName(activeMergeConfiguration.getSourceBuildID()));
      composer.addBuildConfiguredVCSUserToEmailMap(cm.getVCSUserToEmailMap(activeMergeConfiguration.getTargetBuildID()));
      composer.setDefaultDomain(cm.getBuildConfiguration(activeMergeConfiguration.getTargetBuildID()).getEmailDomain());
      composer.setVersionControlMap(vcsUserMap);
      final List list = composer.makeRecipients(nagReport);

      sendMessage(new EmailRecipients(list, Collections.EMPTY_LIST), subject, body, (byte) 0);
    } catch (final Exception e) {
      NotificationUtils.reportErrorSendingMessage(e);
    }
  }


  private static String changeListToString(final ChangeList changeList, final SimpleDateFormat sdf) {
    return changeList.getNumber() + ' ' + sdf.format(changeList.getCreatedAt()) + ' ' + changeList.getDescription();
  }


  /**
   * Sets version control user e-mail map to be respected when
   * generating "To:" list. If a user defined in the source
   * control map, it will be used first.
   */
  public void setVCSUserMap(final Map vcsUserMap) {
    this.vcsUserMap = new HashMap(vcsUserMap);
  }


  /**
   * Creates body of e-mail message notifying about sequence run
   * results.
   *
   * @param stepRun
   * @return StringBuffer containing message
   */
  private StringBuffer makeFinishedBody(final StepRun stepRun) throws ValidationException {
    final StringBuffer body = new StringBuffer(1000);

    // get build run
    final BuildRun buildRun = cm.getBuildRun(stepRun.getBuildRunID());
    final int activeBuildID = buildRun.getActiveBuildID();

    // add header
    body.append(stepRun.getName()).append(" result").append(STR_CR);
    body.append(STR_DIVIDER);
    body.append(STR_CRCR);
    body.append(new MessageSubjectGenerator().makeFinishedSubject(stepRun));
    body.append(STR_CRCR);

    //
    // add build status link
    //
    body.append(makeBuildStatusURLText(buildRun));
    body.append(STR_CRCR);

    //
    // add log links
    //
    final List logs = cm.getAllStepLogs(stepRun.getID());
    body.append(stepRun.getName()).append(" logs").append(STR_CR);
    body.append(STR_DIVIDER).append(STR_CRCR);
    if (logs.isEmpty()) {
      body.append(" Logs are not available.").append(STR_CR);
    } else {
      for (final Iterator logIter = logs.iterator(); logIter.hasNext();) {
        final StepLog stepLog = (StepLog) logIter.next();
        final String logURL = cm.makeBuildLogURL(stepLog);
        final String logName = stepLog.getDescription();
        body.append(logName).append(':').append(STR_CR);
        body.append("  ").append(logURL).append(STR_CR);
      }
    }
    body.append(STR_CR);

    final ArchiveManager am = ArchiveManagerFactory.getArchiveManager(activeBuildID);

    //
    // add error quote log lines, if build failed
    //
    if (!stepRun.isSuccessful()) {
      final List errorLines = am.getLogWindowLines(stepRun);
      if (!errorLines.isEmpty()) {
        body.append(STR_CR);
        body.append("Log Lines Around Error").append(STR_CR);
        body.append(STR_DIVIDER).append(STR_CRCR);
        for (final Iterator i = errorLines.iterator(); i.hasNext();) {
          final String line = (String) i.next();
          if (StringUtils.isBlank(line)) {
            continue;
          }
          body.append("  ").append(line).append(STR_CR);
        }
      }
    }

    //
    // add change list descriptions
    //
    final String changesTitle = stepRun.getResultID() == BuildRun.BUILD_RESULT_SUCCESS || stepRun.getResultID() == BuildRun.BUILD_RESULT_SYSTEM_ERROR ? "Changes" : "Suspect Changes";
    body.append(STR_CR);
    body.append(changesTitle).append(STR_CR);
    body.append(STR_DIVIDER).append(STR_CRCR);
    body.append(makeChangeListText(stepRun.getBuildRunID()));

    //
    // add result links
    //
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    if (scm.isIncludeResultsIntoMessages()) {
      final List results = cm.getAllStepResults(stepRun);
      body.append(stepRun.getName()).append(" Results").append(STR_CR);
      body.append(STR_DIVIDER).append(STR_CRCR);
      if (results.isEmpty()) {
        body.append(" Results are not available.").append(STR_CR);
      } else {
        for (final Iterator i = results.iterator(); i.hasNext();) {
          final StepResult stepResult = (StepResult) i.next();
          if (stepResult.getPathType() == StepResult.PATH_TYPE_EXTERNAL_URL) {
            body.append("  ").append(stepResult.getPath()).append(STR_CR);
          } else {
            final String resultName = stepResult.getDescription();
            body.append(resultName).append(':').append(STR_CR);
            try {
              final List entries = am.getArchivedResultEntries(stepResult);
              for (final Iterator j = entries.iterator(); j.hasNext();) {
                final String entryName = ((ArchiveEntry) j.next()).getEntryName();
                final String resultURL = WebuiUtils.makeBuildResultURL(activeBuildID, stepResult.getID(), entryName);
                body.append("  ").append(resultURL).append(STR_CR);
              }
            } catch (final IOException e) {
              final Error error = new Error("Unexpected I/O error while reading archive: " + StringUtils.toString(e));
              error.setSendEmail(false);
              error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
              error.setSubsystemName(Error.ERROR_SUBSYSTEM_WEBUI);
              ErrorManagerFactory.getErrorManager().reportSystemError(error);
            }
          }
        }
      }
      body.append(STR_CR);
    }

    // add release notes
    body.append(makeReleaseNotesText(buildRun));

    // add timing
    body.append(STR_CR);
    body.append("Timings").append(STR_CR);
    body.append(STR_DIVIDER).append(STR_CRCR);
    body.append(makeStepRunTiming(stepRun)).append(STR_CR);
    // cycle through completed sequences
    final List list = cm.getPreviousStepRuns(stepRun);
    if (!list.isEmpty()) {
      body.append(STR_CR);
      body.append("Previous Step(s):").append(STR_CR);
      for (final Iterator i = list.iterator(); i.hasNext();) {
        final StepRun run = (StepRun) i.next();
        body.append("  ").append(makeStepRunTiming(run)).append(STR_CR);
      }
    }
    return body;
  }


  /**
   * Creates a StringBuffer with release notes for a given build.
   * <br/> Only release notes for BuildConfig.SCHEDULE_TYPE_RECURRENT
   * are accounted.
   *
   * @param buildRun for which release notes are created.
   * @return StringBuffer with release notes for a given build.
   */
  private StringBuffer makeReleaseNotesText(final BuildRun buildRun) {
    final StringBuffer result = new StringBuffer(500);
    final BuildConfig buildConfig = cm.getBuildRunConfig(buildRun);
    if (buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_RECURRENT) {
      final String buildName = buildConfig.getBuildName();
      final List fixedIssues = cm.getBuildRunIssues(buildRun);
      if (!fixedIssues.isEmpty()) {
        result.append(STR_CR);
        result.append("Release Notes for ").append(buildName).append(" Number ").append(buildRun.getBuildRunNumberAsString()).append(STR_CR);
        result.append(STR_DIVIDER).append(STR_CRCR);
        for (final Iterator i = fixedIssues.iterator(); i.hasNext();) {
          final Issue issue = (Issue) i.next();
          result.append(issue.getKey()).append(' ').append(issue.getDescription()).append(STR_CR);
        }
      }
    }
    return result;
  }


  /**
   * Makes line containing sequence run timing
   */
  private static StringBuffer makeStepRunTiming(final StepRun run) {
    final StringBuffer result = new StringBuffer(30);
    result.append(run.getName()).append(" took ");
    result.append(StringUtils.durationToString((long) run.getDuration(), true));
    return result;
  }


  private StringBuffer makeStartedBody(final BuildRun buildRun, final BuildSequence buildSequence) throws ValidationException {
    final StringBuffer result = new StringBuffer(1000);

    // repeat subject here
    result.append(STR_CR);
    result.append(new MessageSubjectGenerator().makeStartedSubject(buildRun, buildSequence)).append('.');
    result.append(STR_CRCR);

    // add build status link
    result.append(makeBuildStatusURLText(buildRun));
    result.append(STR_CRCR);

    // add change list description
    result.append("Changes To Build").append('\n');
    result.append(STR_DIVIDER).append("\n\n");
    result.append(makeChangeListText(buildRun.getBuildRunID()));

    return result;
  }


  /**
   * Creates a string buffer that contains build status link
   *
   * @param buildRun
   */
  private static StringBuffer makeBuildStatusURLText(final BuildRun buildRun) {
    final StringBuffer sb = new StringBuffer(100);
    final BuildStatusURLGenerator statusURLGenerator = new BuildStatusURLGenerator();
    sb.append("Build status:").append(STR_CR);
    sb.append("  ").append(statusURLGenerator.makeBuildStatusURL(buildRun));
    return sb;
  }


  /**
   * @return empty subject, maybe prefixed with system-wide
   *         notification prefix if defined.
   */
  private String makePrefix() {
    return cm.getNotificationPrefix(BuildConfig.UNSAVED_ID);
  }


  /**
   * @return empty subject, maybe prefixed with system-wide
   *         notification and build-specific prefixes if
   *         defined.
   */
  private StringBuffer makePrefix(final int buildID) {
    return new StringBuffer(100).append(cm.getNotificationPrefix(buildID));
  }


  /**
   * Adds text of change list description to the give
   * StringBuffer
   *
   * @param buildRunID
   */
  private static StringBuffer makeChangeListText(final int buildRunID) {
    final ChangeListDescriptionGenerator descriptionGenerator = ChangeListDescriptionFactory.
            getGenerator(ConfigurationManager.getInstance().getBuildRun(buildRunID));
    final StringBuffer changeListDescription = descriptionGenerator.generateChangeListDescription();
    if (changeListDescription.length() > 0) {
      final ChangeListURLGenerator changeListURLGenerator = new ChangeListURLGenerator();
      changeListDescription.append("Change(s) Details:").append(STR_CR);
      changeListDescription.append("  ").append(changeListURLGenerator.makeBuildRunChangesURL(buildRunID));
      changeListDescription.append(STR_CRCR);
    }
    return changeListDescription;
  }


  /**
   * Returns local host name, IP address or null string if there
   * was an exception.
   *
   * @return local host name, IP address or null string if there
   *         was an exception.
   */
  private String getEHLOhost() {
    try {
      // try network address
      final String buildManagerHost = SystemConfigurationManagerFactory.getManager().getBuildManagerHost();
      if (!StringUtils.isBlank(buildManagerHost)) {
        return buildManagerHost;
      }
      return getHOSTNAMEEnvVariable();
    } catch (final UnknownHostException e) {
      return getHOSTNAMEEnvVariable();
    }
  }


  /**
   * Helper method to fall back on when build manager host name
   * cannot be identified.
   *
   * @return value of "HOSTNAME" variable
   */
  private static String getHOSTNAMEEnvVariable() {
    final String hostNameEnvVariable = RuntimeUtils.isWindows() ? RuntimeUtils.getEnvVariable("COMPUTERNAME") : RuntimeUtils.getEnvVariable("HOSTNAME");
    if (!StringUtils.isBlank(hostNameEnvVariable)) {
      return hostNameEnvVariable;
    }
    return null; // nothing found
  }


  /**
   * Sends e-mail message.
   *
   * @param emailProperties
   * @param adminAddress
   * @param recipients
   * @param messageSubject
   * @param messageBody
   * @param messagePriority
   * @throws MessagingException
   */
  private void sendMessage(final List emailProperties, final InternetAddress adminAddress,
                           final EmailRecipients recipients, final StringBuffer messageSubject,
                           final StringBuffer messageBody, final byte messagePriority) throws MessagingException {

    // convert list of SystemProperty objects to a Properties
    final Properties emailPropertiesValues = systemPropertiesToValues(emailProperties);

    // get receive lists
    final List toRecipients = recipients.getToList();
    final List bbcRecipients = recipients.getBccList();
    if (toRecipients.isEmpty() && bbcRecipients.isEmpty()) {
      return; // nobody to send to
    }

    // get system SMTP configuration
    final boolean secureConnection = emailPropertiesValues.getProperty(SystemProperty.SMTP_SERVER_ENCRYPTED_CONNECTION, SystemProperty.OPTION_UNCHECKED).equals(SystemProperty.OPTION_CHECKED);
    final String server = emailPropertiesValues.getProperty(SystemProperty.SMTP_SERVER_NAME, "localhost");
    final String port = emailPropertiesValues.getProperty(SystemProperty.SMTP_SERVER_PORT, secureConnection ? ConfigurationConstants.DEFAULT_SMTPS_SERVER_PORT : ConfigurationConstants.DEFAULT_SMTP_SERVER_PORT);
    final String encryptedPassword = emailPropertiesValues.getProperty(SystemProperty.SMTP_SERVER_PASSWORD, null);
    final String password = encryptedPassword == null ? null : SecurityManager.decryptPassword(encryptedPassword);
    final String user = emailPropertiesValues.getProperty(SystemProperty.SMTP_SERVER_USER, null);

    // create session
    final String transportProtocol = secureConnection ? "smpts" : "smtp";
    final String mailSmtpHost = secureConnection ? "mail.smtps.host" : "mail.smtp.host";
    final String mailSmtpPort = secureConnection ? "mail.smtps.port" : "mail.smtp.port";
    final String mailSmtpSendPartial = secureConnection ? "mail.smtps.sendpartial" : "mail.smtp.sendpartial";
    final String mailSmtpAuth = secureConnection ? "mail.smtps.auth" : "mail.smtp.auth";
    final String mailSmtpLocalhost = secureConnection ? "mail.smtps.localhost" : "mail.smtp.localhost";
    final String mailSmtpQuitwait = secureConnection ? "mail.smtps.quitwait" : "mail.smtp.quitwait";

    final Properties smtpProps = new Properties();
    smtpProps.setProperty("mail.transport.protocol", transportProtocol);
    smtpProps.setProperty(mailSmtpQuitwait, "false");
    smtpProps.setProperty(mailSmtpHost, server);
    smtpProps.setProperty(mailSmtpPort, port);
    smtpProps.setProperty(mailSmtpSendPartial, "true");
    if (!StringUtils.isBlank(user) && !StringUtils.isBlank(password)) {
      smtpProps.setProperty(mailSmtpAuth, "true");
    }
    if (!StringUtils.isBlank(getEHLOhost())) {
      smtpProps.setProperty(mailSmtpLocalhost, getEHLOhost());
    }
    final Session session = Session.getInstance(smtpProps, null);
    // session.setDebug(true);

    // compose MimeMessage
    final MimeMessage message = new MimeMessage(session);
    message.setFrom(adminAddress);
    message.setReplyTo(new InternetAddress[]{adminAddress});
    message.addRecipients(Message.RecipientType.TO, (Address[]) toRecipients.toArray(new Address[0]));
    message.addRecipients(Message.RecipientType.BCC, (Address[]) bbcRecipients.toArray(new Address[0]));
    message.setSentDate(new Date(System.currentTimeMillis()));
    message.setSubject(messageSubject.toString());

    // set message text along with encoding
    final String encoding = SystemConfigurationManagerFactory.getManager().getSystemPropertyValue(SystemProperty.OUTPUT_ENCODING, null);
    if (StringUtils.isBlank(encoding)) {
      message.setText(messageBody.toString());
    } else {
      message.setText(messageBody.toString(), encoding);
    }

    // add priority headers if needed
    if (messagePriority == SystemProperty.MESSAGE_PRIORITY_HIGH) {
      message.addHeader("X-Priority", "1");
      message.addHeader("Priority", "Urgent");
      message.addHeader("Importance", "high");
    }

    // check if send enabled
    if (!enabled || "true".equals(System.getProperty(SystemConstants.SYSTEM_PROPERTY_EMAIL_DISABLED, "false"))) {
      return;
    }

    // send message
    if (!StringUtils.isBlank(user) && !StringUtils.isBlank(password)) {
      message.saveChanges();
      final String transportName = secureConnection ? "smtps" : "smtp";
      final Transport transport = session.getTransport(transportName);
      transport.connect(server, Integer.parseInt(port), user, password);
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();
    } else {
      Transport.send(message);
    }
  }


  private static Properties systemPropertiesToValues(final List emailProperties) {
    final Properties emailPropertiesValues = new Properties();
    for (final Iterator i = emailProperties.iterator(); i.hasNext();) {
      final SystemProperty sp = (SystemProperty) i.next();
      emailPropertiesValues.setProperty(sp.getPropertyName(), sp.getPropertyValue());
    }
    return emailPropertiesValues;
  }


  /**
   * Sends message to the list of recipients and BCC list
   */
  private void sendMessage(final EmailRecipients recipients, final StringBuffer messageSubject,
                           final StringBuffer messageBody, final byte priorityCode) throws MessagingException {
    // grab *all* system props
    final List systemProperties = SystemConfigurationManagerFactory.getManager().getSystemProperties();
    // sendMessage will figure out which ones to use
    final InternetAddress senderAddress = NotificationUtils.getSystemAdminAddress();
    sendMessage(systemProperties, senderAddress, recipients, messageSubject, messageBody, priorityCode);
  }


  private static void reportErrorSendingPassword(final Exception e) {
    final Error err = NotificationUtils.makeNotificationError();
    err.setDescription("Error while sending user password notification");
    err.setDetails(e);
    ErrorManagerFactory.getErrorManager().reportSystemError(err);
  }


  public String toString() {
    return "EmailNotificationManager{" +
            "cm=" + cm +
            ", vcsUserMap=" + vcsUserMap +
            ", enabled=" + enabled +
            '}';
  }
}
