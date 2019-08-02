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
package org.parabuild.ci.versioncontrol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.BuildScriptGenerator;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.Agent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by simeshev on May 3, 2006 at 3:54:01 PM
 */
public abstract class AbstractCommandBasedSourceControl extends AbstractSourceControl {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(FileSystemSourceControl.class); // NOPMD
  private static final String PARAMETER_PARABUILD_LABEL_TO_DELETE = "PARABUILD_LABEL_TO_DELETE";
  private static final String PARAMETER_PARABUILD_LABEL_TO_CREATE = "PARABUILD_LABEL_TO_CREATE";
  private static final String PARAMETER_PARABUILD_LABEL_TIMESTAMP_TO_CREATE = "PARABUILD_LABEL_TIMESTAMP";
  private static final String PARAMETER_PARABUILD_LABEL_DATETIME_TO_CREATE = "PARABUILD_LABEL_DATETIME";
  private static final String PARAMETER_PARABUILD_CHANGE_LIST_TIMESTAMP = "PARABUILD_CHANGE_LIST_TIMESTAMP";
  private static final String PARAMETER_PARABUILD_CHANGE_LIST_DATETIME = BuildScriptGenerator.VAR_PARABUILD_CHANGE_LIST_DATETIME;
  private Date lastSyncDate = null;


  public AbstractCommandBasedSourceControl(final BuildConfig buildConfig) {
    super(buildConfig);
  }


  /**
   * Returns ID of list of changes that were made to controlled
   * source line since the given change list ID
   * <p/>
   * In order to run successfuly this method needs an already
   * checked out local copy on the client.
   * <p/>
   * Handling zero ID change list. When this method is called
   * first time in build's life, the ID of the change list is
   * zero. It means that caller expects that there are no change
   * lists in the database. Version control should retrieve all
   * the past changes, and pick fixed number of the latest
   * changes. This number is identified by SourceControl.DEFAULT_FIRST_RUN_SIZE
   * constant.
   *
   * @param changeListID base change list ID
   * @return new change list ID if there were changes made, or
   *         the same base change list if there were changes
   * @throws BuildException
   */
  public abstract int getChangesSince(int changeListID) throws BuildException, AgentFailureException;


  /**
   * This method requests SourceControl to reload its
   * configuration from the database.
   * <p/>
   * If configuration has changed in such a way that requires
   * cleaning up source line, next operation involving
   * manipulation on source line file should should be performed
   * on a clean checkout directory.
   * <p/>
   * For instance, if source line path has changed, the content
   * of the old checkout directory should be cleaned up
   * (deleted).
   */
  public abstract void reloadConfiguration();


  /**
   * This GoF strategy method validates that build directory is
   * initialized according to build configuration. Implementing
   * classes may use this method to perform additional validation
   * of build directory.
   * <p/>
   * If this method returns false, initLocalCopyIfNecessary()
   * will call checkoutLatest() to populate build dir.
   *
   * @return build directory is initialized according to build
   *         configuration.
   * @see AbstractSourceControl#initLocalCopyIfNecessary()
   * @see SourceControl#checkoutLatest()
   */
  public final boolean isBuildDirInitialized() {
    return true; // checkout dir is always present
  }


  /**
   * Checks out latest state of the source line
   */
  public final void checkoutLatest() {
    // Do not need to check out latest
  }


  /**
   * Syncs to a given change list number
   */
  public final void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    // check if we have a command
    final String syncToChangeListCommand = getSettingValue(SourceControlSetting.COMMAND_VCS_SYNC_TO_CHANGE_LIST_COMMAND, null);
    if (StringUtils.isBlank(syncToChangeListCommand)) return;

    // execute
    FileSystemSourceControl.CommandBasedSourceControlCommand command = null;
    try {
      final ChangeList changeList = configManager.getChangeList(changeListID);
      final Date changeListDate = changeList.getCreatedAt();
      final Agent agent = getCheckoutDirectoryAwareAgent();
      command = new CommandBasedSourceControlCommand(agent);
      command.setCommand(syncToChangeListCommand);
      command.addEnvironment(PARAMETER_PARABUILD_CHANGE_LIST_TIMESTAMP, Long.toString(changeListDate.getTime()));
      command.addEnvironment(PARAMETER_PARABUILD_CHANGE_LIST_DATETIME, new SimpleDateFormat("yyyyMMddHHmmss").format(changeListDate));
      command.addEnvironment(getCommonEnvironment());
      command.execute();
      this.lastSyncDate = (Date) changeListDate.clone();
    } catch (final IOException e) {
      throw processException(e);
    } finally {
      cleanup(command);
    }
  }


  /**
   * Returns relative project path
   */
  public final String getRelativeBuildDir() {
    return ""; // checkout dir
  }


  /**
   * Labels the last synced checkout directory with the given
   * label.
   * <p/>
   * Must throw a BuildException if there was no last sync made
   * or if checkout directory is empty.
   *
   * @param label
   */
  public final void label(final String label) throws BuildException, CommandStoppedException, AgentFailureException {
    // check if we have a command
    final String labelCommand = getSettingValue(SourceControlSetting.COMMAND_VCS_LABEL_COMMAND, null);
    if (StringUtils.isBlank(labelCommand)) return;

    // execute
    FileSystemSourceControl.CommandBasedSourceControlCommand command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      command = new CommandBasedSourceControlCommand(agent);
      command.setCommand(labelCommand);
      command.addEnvironment(PARAMETER_PARABUILD_LABEL_TO_CREATE, label);
      command.addEnvironment(PARAMETER_PARABUILD_LABEL_TIMESTAMP_TO_CREATE, Long.toString(lastSyncDate.getTime()));
      command.addEnvironment(PARAMETER_PARABUILD_LABEL_DATETIME_TO_CREATE, new SimpleDateFormat("yyyyMMddHHss").format(lastSyncDate));
      command.addEnvironment(getCommonEnvironment());
      command.execute();
    } catch (final IOException e) {
      throw processException(e);
    } finally {
      cleanup(command);
    }
  }


  /**
   * @return variables to be added to all commands executed
   *         by this FileSystemSourceControl.
   */
  final Map getCommonEnvironment() {
    final Map result = new HashMap(11);
    final ActiveBuildConfig activeBuildConfig = configManager.getActiveBuildConfig(activeBuildID);
    result.put(BuildScriptGenerator.VAR_PARABUILD_CONFIGURATION_ID, Integer.toString(activeBuildID));
    result.put(BuildScriptGenerator.VAR_PARABUILD_BUILD_NAME, activeBuildConfig.getBuildName());
    return result;
  }


  /**
   * Labels are note supported.
   *
   * @param labels to remove.
   * @return int number of removed labels
   */
  public final int removeLabels(final String[] labels) throws BuildException, CommandStoppedException, AgentFailureException {
    try {
      // check if we have a command
      final String removeLabelCommand = getSettingValue(SourceControlSetting.COMMAND_VCS_REMOVE_LABEL_COMMAND, null);
      if (StringUtils.isBlank(removeLabelCommand)) return 0;

      // execute
      final Agent agent = getCheckoutDirectoryAwareAgent();
      int deleteCount = 0;
      for (int i = 0; i < labels.length; i++) {
        final String label = labels[i];
        final CommandBasedSourceControlCommand command = new CommandBasedSourceControlCommand(agent);
        command.setCommand(removeLabelCommand);
        command.addEnvironment(PARAMETER_PARABUILD_LABEL_TO_DELETE, label);
        try {
          command.execute();
          deleteCount++;
        } catch (final IOException e) {
          throw processException(e);
        } finally {
          cleanup(command);
        }
      }
      return deleteCount;
    } catch (final IOException e) {
      throw processException(e);
    }
  }


  /**
   * Returns a map containing version control user names as keys
   * and e-mails as values. This method doesn't throw exceptions
   * as it's failure is not critical but it reports errors by
   * calling to ErrorManager.
   *
   * @see ErrorManagerFactory
   * @see ErrorManager
   */
  public final Map getUsersMap() {
    return Collections.emptyMap();
  }


  /**
   * @return Map with a shell variable name as a key and variable
   *         value as value. The shell variables will be made
   *         available to the build commands.
   *         <p/>
   *         If not variables available, returns an empty map.
   * @see BuildScriptGenerator#addVariables(Map)
   */
  public final Map getShellVariables() {
    return Collections.emptyMap();
  }


  protected BuildException processException(final Exception e) {
    return new BuildException("Error while accessing file system-based VCS: " + StringUtils.toString(e), e, getAgentHost());
  }


  static final class CommandBasedSourceControlCommand extends VersionControlRemoteCommand {

    /**
     * Creates CommandBasedSourceControlCommand that uses system-wide
     * timeout for version control commands
     *
     * @param agent
     */
    protected CommandBasedSourceControlCommand(final Agent agent) throws AgentFailureException {
      super(agent, true);
      setRespectErrorCode(true);
    }
  }
}
