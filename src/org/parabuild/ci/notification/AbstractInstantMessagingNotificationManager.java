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

import org.parabuild.ci.common.BuildStatusURLGenerator;
import org.parabuild.ci.common.CommonConstants;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.object.BranchMergeConfiguration;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.object.BuildWatcher;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.object.User;
import org.parabuild.ci.object.UserProperty;
import org.parabuild.ci.security.SecurityManager;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * AbstractInstantMessagingNotificationManager is an
 * implementation of NotificationManager to send messages to IM
 * addressee.
 * <p/>
 * AbstractInstantMessagingNotificationManager realises GoF
 * Strategy pattern. Concrete classes implementing access to
 * different IM servers should extend this class by implementing
 * strategy methods.
 * <p/>
 * It's guaranteed that send(...) methods receive IM addresses
 * known to be of type returned by serverType().
 *
 * @see #serverType() - returns server type
 * @see #send(Collection, StringBuffer) -
 *      sends message to a list of recepints.
 * @see #send(String, StringBuffer) - sends a
 *      message to a single recepint.
 * @see #isServerEnabled() - returns true if it's allowed to send
 *      messages.
 */
public abstract class AbstractInstantMessagingNotificationManager implements NotificationManager, CommonConstants {

  private final ConfigurationManager cm = ConfigurationManager.getInstance();


  private Map vcsUserMap = null;
  private boolean enabled = true;


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
  public final void notifyBuildStepStarted(final BuildRun buildRun, final BuildSequence buildSequence) {
    try {
      if (!isServerEnabled()) return;

      // make body
      final StringBuffer body = new StringBuffer(200);
      body.append(new MessageSubjectGenerator().makeStartedSubject(buildRun, buildSequence));
      body.append(STR_CR);

      // add build status link
      final BuildStatusURLGenerator statusURLGenerator = new BuildStatusURLGenerator();
      body.append("Build status:").append(STR_CR);
      body.append("  ").append(statusURLGenerator.makeBuildStatusURL(buildRun));
      body.append(STR_CR);

      // add change list description
      final ChangeListURLGenerator changeListURLGenerator = new ChangeListURLGenerator();
      body.append("Changes to build:").append(STR_CR);
      body.append("  ").append(changeListURLGenerator.makeBuildRunChangesURL(buildRun.getBuildRunID()));

      // send
      final String imMessageSelection = UserProperty.IM_SEND_SUCCESSES;
      final byte watchLevel = BuildWatcher.LEVEL_BROKEN;
      final BuildStepType type = buildRun.getType() == BuildRun.TYPE_PUBLISHING_RUN ? BuildStepType.PUBLISH : BuildStepType.BUILD;
      final boolean notFistStepStart = !cm.isFirstBuildSequence(buildRun.getBuildID(), type, buildSequence.getStepName());
      if (enabled)
        send(makeReceiverList(buildRun, serverType(), imMessageSelection, true, notFistStepStart, false, watchLevel), body);
    } catch (final Exception ex) {
      NotificationUtils.reportErrorSendingStepStarted(buildRun, buildSequence, ex);
    }
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
  public final void notifyBuildStepFinished(final StepRun stepRun) {
    BuildRun buildRun = null;
    try {
      if (!isServerEnabled()) return;

      // make body
      final StringBuffer body = new StringBuffer(200);
      body.append(new MessageSubjectGenerator().makeFinishedSubject(stepRun));
      body.append(STR_CR);

      // add log links
      final List logs = cm.getAllStepLogs(stepRun.getID());
      body.append(stepRun.getName()).append(" logs:").append(STR_CR);
      if (logs.isEmpty()) {
        body.append("Logs are not available.").append(STR_CR);
      } else {
        for (final Iterator logIter = logs.iterator(); logIter.hasNext();) {
          final StepLog stepLog = (StepLog) logIter.next();
          final String logURL = cm.makeBuildLogURL(stepLog);
          final String logName = stepLog.getDescription();
          body.append("  ").append(logName).append(": ").append(logURL).append(STR_CR);
        }
      }

      // add change list description
      body.append("Changes in this build:").append(STR_CR);
      final ChangeListURLGenerator changeListURLGenerator = new ChangeListURLGenerator();
      body.append("  ").append(changeListURLGenerator.makeBuildRunChangesURL(stepRun.getBuildRunID()));
      body.append(STR_CR);

      // add status if failed
      buildRun = cm.getBuildRun(stepRun.getBuildRunID());
      if (stepRun.getResultID() != BuildRun.BUILD_RESULT_SUCCESS) {
        // add build status link
        final BuildStatusURLGenerator statusURLGenerator = new BuildStatusURLGenerator();
        body.append("Build status:").append(STR_CR);
        body.append("  ").append(statusURLGenerator.makeBuildStatusURL(buildRun));
        body.append(STR_CR);
      }

      // send
      final String imMessageSelection = UserProperty.IM_SEND_FAILURES;
      final byte watchLevel = (byte) NotificationUtils.stepRunResultToWatchLevel(stepRun);
      final BuildStepType type = buildRun.getType() == BuildRun.TYPE_PUBLISHING_RUN ? BuildStepType.PUBLISH : BuildStepType.BUILD;
      final boolean notLastStepResult = !cm.isLastEnabledBuildSequence(buildRun.getBuildID(), type, stepRun.getName());
      if (enabled)
        send(makeReceiverList(buildRun, serverType(), imMessageSelection, false, false, notLastStepResult, watchLevel), body);
    } catch (final RuntimeException | AddressException | ValidationException e) {
      NotificationUtils.reportErrorSendingStepFinished(buildRun, stepRun, e);
    }
  }


  /**
   * Sets version control user e-mail map to be respected when
   * generating "To:" list. If a user defined in the source
   * control map, it will be used first.
   */
  public final void setVCSUserMap(final Map vcsUserMap) {
    this.vcsUserMap = vcsUserMap;
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
  public final void notifyBuildStepHung(final BuildRun buildRun, final BuildSequence sequence) {
    try {
      if (!isServerEnabled()) return;

      // compose subject
      final BuildConfig buildConfig = cm.getBuildRunConfig(buildRun);
      final StringBuffer body = new StringBuffer(500);
      body.append("BUILD SYSTEM ERROR:").append(' ');
      body.append("Build ").append(buildConfig.getBuildName()).append(" has hung.");
      body.append(STR_CR);

      // compose body
      body.append("BUILD SYSTEM ERROR").append(STR_CR);
      body.append("Details:").append(STR_CR);
      body.append("  Build hung at step \"").append(sequence.getStepName()).append("\" after ").append(sequence.getTimeoutMins()).append(" minutes timeout.").append(STR_CR);
      body.append("  Parabuild attempted and failed to stop the build.").append(STR_CR);
      body.append("  System requires immediate attention of the build administrator.").append(STR_CR);
      body.append("  Build won't start until the problem is fixed.").append(STR_CR);
      body.append("Possible cause:").append(STR_CR);
      body.append("  Build script spawns long-running process(es) that cannot be identified \n");
      body.append("  as belonging to the build sequence.");

      // send
      final String imMessageSelection = UserProperty.IM_SEND_SYSTEM_ERRORS;
      final byte watchLevel = BuildWatcher.LEVEL_SYSTEM_ERROR;
      if (enabled)
        send(makeReceiverList(buildRun, serverType(), imMessageSelection, false, false, false, watchLevel), body);
    } catch (final Exception ex) {
      NotificationUtils.reportErrorSendingStepHung(buildRun, sequence, ex);
    }
  }


  /**
   * Sends user password in accordance with user email in the
   * DB.
   */
  public final void sendUserPassword(final String userName, final String newPassword) {
    // NOTE: simeshev@parabuilci.org - we don't send passwords via IM,
    // so this method does nothing.
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
  public final void notifyBuildStepFailed(final BuildRun buildRun, final Exception e) {
    try {
      if (!isServerEnabled()) return;

      // make body
      final StringBuffer body = new StringBuffer(200);
      body.append(buildRun.getBuildName()).append(" FAILED due to a system error.");
      body.append(STR_CRCR);
      body.append("Build administrator has been notified about the error.");
      body.append(STR_CRCR);

      // add build details for body
      body.append("Build details:");
      body.append(STR_CRCR);
      final String buildRunNumber = buildRun.getBuildRunNumber() >= 0 ? buildRun.getBuildRunNumberAsString() : "";
      final SimpleDateFormat formatter = new SimpleDateFormat(SystemConfigurationManagerFactory.getManager().getDateTimeFormat(), Locale.US);
      final String startedAt = buildRun.getStartedAt() != null ? formatter.format(buildRun.getStartedAt()) : "";
      final String finishedAt = buildRun.getFinishedAt() != null ? formatter.format(buildRun.getFinishedAt()) : "";
      StringUtils.appendWithNewLineIfNotNull(body, "Build name:", buildRun.getBuildName());
      StringUtils.appendWithNewLineIfNotNull(body, "Build number:", buildRunNumber);
      StringUtils.appendWithNewLineIfNotNull(body, "Started at:", startedAt);
      StringUtils.appendWithNewLineIfNotNull(body, "Finished at:", finishedAt);
      StringUtils.appendWithNewLineIfNotNull(body, "Change list:", buildRun.getChangeListNumber());
      StringUtils.appendWithNewLineIfNotNull(body, "Sync to this build:", buildRun.getSyncNote());

      final String imMessageSelection = UserProperty.IM_SEND_FAILURES;
      final byte watchLevel = BuildWatcher.LEVEL_BROKEN;
      if (enabled)
        send(makeReceiverList(buildRun, serverType(), imMessageSelection, false, false, false, watchLevel), body);
    } catch (final Exception ex) {
      NotificationUtils.reportErrorSendingBuildFailure(buildRun, ex);
    }
  }


  /**
   * Sends synchronous test e-mail notification message. IM
   * manager does nothing.
   */
  public final void sendTestEmailMessage(final List properties) {
    // do nothing
  }


  public void notifyChangeListsWaitingForMerge(final int activeMergeID) {
    // do nothing
  }


  public void enableNotification(final boolean enabled) {
    this.enabled = enabled;
  }


  public void notifyMergeFailedBecauseOfConflicts(final BranchMergeConfiguration mergeConfiguration, final ChangeList changeList, final List conflicts) {
    // REVIEWME: simeshev@parabuilci.org -> implement
  }


  /**
   * Sends notification about system error to build
   * administrator
   *
   * @see
   */
  public final void notifyBuildAdministrator(final Error error) {
    try {
      if (!isServerEnabled()) return;
      // send to admin
      final User adminUser = SecurityManager.getInstance().getUserByName(User.DEFAULT_ADMIN_USER);
      if (adminUser != null && adminUser.getImType() == serverType()
              && !StringUtils.isBlank(adminUser.getImAddress())) {

        // body
        final StringBuffer body = new StringBuffer(200);
        body.append(STR_CR);
        body.append("Parabuild alert: ");
        body.append(STR_CRCR);
        body.append("Build server encountered\nan error that requires immediate attention.");
        body.append(STR_CRCR);
        body.append("Error details:");
        body.append(STR_CRCR);
        StringUtils.appendWithNewLineIfNotNull(body, "  Product:", error.getProductVersion());
        StringUtils.appendWithNewLineIfNotNull(body, "  Description:", error.getDescription());
        StringUtils.appendWithNewLineIfNotNull(body, "  Severity:", error.getErrorLevelAsString());
        StringUtils.appendWithNewLineIfNotNull(body, "  Build name:", error.getBuildName());
        StringUtils.appendWithNewLineIfNotNull(body, "  Build step name:", error.getStepName());
        StringUtils.appendWithNewLineIfNotNull(body, "  Host name:", error.getHostName());
        StringUtils.appendWithNewLineIfNotNull(body, "  Subsystem:", error.getSubsystemName());
        StringUtils.appendWithNewLineIfNotNull(body, "  Details:", error.getDetails());
        StringUtils.appendWithNewLineIfNotNull(body, "  Log line:", error.getLogLines());
        StringUtils.appendWithNewLineIfNotNull(body, "  Possible cause:", error.getPossibleCause());
//      StringUtils.appendWithNewLineIfNotNull(body, "  Trace:", error.getStacktrace());
        // send
        if (enabled) send(adminUser.getImAddress(), body);
      }
    } catch (final Exception e) {
      NotificationUtils.reportErrorSendingSystemAlert(e);
    }
  }


  /**
   * @return server type this manager serves.
   * @see User#IM_TYPE_NONE - server is not set
   * @see User#IM_TYPE_JABBER - Jabber XMPP server.
   */
  protected abstract int serverType();


  /**
   * Sends an IM message to a collection of IM addresses.
   *
   * @param body
   */
  protected abstract void send(Collection addresses, StringBuffer body);


  /**
   * Sends a message to a single user.
   *
   * @param address
   * @param body
   */
  protected abstract void send(String address, StringBuffer body);


  /**
   * @return true if server of the serverType() is configured
   *         and enabled.
   */
  public abstract boolean isServerEnabled();


  /**
   * Returns String list of addresses.
   */
  private Collection makeReceiverList(final BuildRun buildRun, final int imType,
                                      final String imMessageSelection, final boolean applyStartFilter, final boolean applyFirstStepStartFilter,
                                      final boolean applyLastStepResultFilter, final byte watchLevel) throws AddressException {

    // get users having imType as IM
    final Map imUsersMap = cm.getIMUsersEmailMap(imType, imMessageSelection);

    // Createc composer
    final EmailRecipientListComposer composer = new EmailRecipientListComposer();
    composer.setCaseSensitiveUserName(NotificationUtils.isCaseSensitiveUserName(buildRun.getBuildID()));

    // Get recipients
    final EmailRecipients recipients = composer.makeRecipients(buildRun, vcsUserMap, applyStartFilter,
            applyFirstStepStartFilter, applyLastStepResultFilter, true, watchLevel);

    // filter users identified by this list of e-mail recipients to a list of IM receivers.
    final Map result = new HashMap(23);
    for (final Iterator i = recipients.getAllAddresses().iterator(); i.hasNext();) {
      final String imAddress = (String) imUsersMap.get(((InternetAddress) i.next()).getAddress().toLowerCase());
      if (imAddress != null) {
        result.put(imAddress, Boolean.TRUE);
      }
    }
    return new ArrayList(result.keySet());
  }


  public String toString() {
    return "AbstractInstantMessagingNotificationManager{" +
            "cm=" + cm +
            ", vcsUserMap=" + vcsUserMap +
            ", enabled=" + enabled +
            '}';
  }
}
