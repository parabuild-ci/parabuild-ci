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
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A surrogate source control that watches changes in file system.
 */
public final class GenericSourceControl extends AbstractCommandBasedSourceControl {


  private static final Log log = LogFactory.getLog(GenericSourceControl.class);

  private static final String PARAMETER_PARABUILD_COMMAND_SINCE_TIMESTAMP = "PARABUILD_SINCE_TIMESTAMP";
  private static final String PARAMETER_PARABUILD_COMMAND_SINCE_DATETIME = "PARABUILD_SINCE_DATETIME";


  public GenericSourceControl(final BuildConfig buildConfig) {
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
   * changes. This number is adentified by SourceControl.DEFAULT_FIRST_RUN_SIZE
   * constant.
   *
   * @param startChangeListID base change list ID
   * @return new change list ID if there were changes made, or
   *         the same base change list if there were changes
   * @throws BuildException
   */
  public int getChangesSince(final int startChangeListID) throws BuildException, AgentFailureException {
    if (log.isDebugEnabled()) log.debug("begin getChangesSince changeListID: " + startChangeListID);
    try {
      int rowLimit = Integer.MAX_VALUE;
      Date changeListDate = null;
      // check if it is first run (changeListID equals UNSAVED_ID)
      if (startChangeListID == ChangeList.UNSAVED_ID) {
        rowLimit = initialNumberOfChangeLists();
      } else {
        // get last build change date
        final ChangeList latest = configManager.getChangeList(startChangeListID);
        // where there changes?
        if (latest == null) return startChangeListID;
        rowLimit = maxNumberOfChangeLists();
        changeListDate = latest.getCreatedAt();
      }

      final long changeListTimestamp = changeListDate == null ? 0L : changeListDate.getTime();

      // check if we have a command
      final String getChangesCommand = getSettingValue(VersionControlSystem.GENERIC_VCS_GET_CHANGES_COMMAND);
      //if (log.isDebugEnabled()) log.debug("getChangesCommand = " + getChangesCommand);

      // execute
      CommandBasedSourceControlCommand command = null;
      List result = null;
      try {
        final Agent agent = getCheckoutDirectoryAwareAgent();
        command = new CommandBasedSourceControlCommand(agent);
        command.setCommand(getChangesCommand);
        command.addEnvironment(PARAMETER_PARABUILD_COMMAND_SINCE_TIMESTAMP, Long.toString(changeListTimestamp));
        command.addEnvironment(PARAMETER_PARABUILD_COMMAND_SINCE_DATETIME, new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(changeListTimestamp)));
        command.addEnvironment(getCommonEnvironment());
        command.execute();

        // parse output
        final TokenizingChangeLogParser parser = new TokenizingChangeLogParser(
                rowLimit,
                getSettingValue(VersionControlSystem.COMMAND_VCS_CHANGE_WINDOW, 60) * 1000L,
                "no_branch",
                getSettingValue(VersionControlSystem.COMMAND_VCS_COLUMN_DIVIDER),
                getSettingValue(VersionControlSystem.COMMAND_VCS_END_OF_RECORD),
                getSettingValue(VersionControlSystem.COMMAND_VCS_CHANGE_DATE_FORMAT, "yyyyMMdd.HHmmss"),
                new String[]{"checkin"},
                new String[]{"mkelem"},
                new String[]{"**null operation kind**"},
                new String[]{"add"},
                new String[]{"delete", "remove"},
                new String[]{"mkbranch", "rmbranch"},
                maxChangeListSize());
        //if (log.isDebugEnabled()) log.debug("command.getStdoutFile() = " + IoUtils.fileToString(command.getStdoutFile()));
        result = parser.parseChangeLog(command.getStdoutFile());
        if (log.isDebugEnabled()) log.debug("result: " + result);
      } catch (final IOException | CommandStoppedException e) {
        throw processException(e);
      } finally {
        cleanup(command);
      }

      if (result.isEmpty()) {
        return startChangeListID;
      }

      // validate that change lists contain not only exclusions
      if (new ExclusionPathFinder().onlyExclusionPathsPresentInChangeLists(result, getSettingValue(VersionControlSystem.VCS_EXCLUSION_PATHS))) {
        return startChangeListID;
      }

      // store changes
      if (log.isDebugEnabled()) log.debug("end getChangesSince");
      result.sort(ChangeList.REVERSE_CHANGE_DATE_COMPARATOR);
      return configManager.saveBuildChangeLists(activeBuildID, result);
    } catch (final RuntimeException e) {
      throw processException(e);
    }
  }


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
  public final void reloadConfiguration() {
    // Get resolved settings
    final Map newSettings = getResolvedSettings();

    // check if critical settings has changed
    final SourceControlSettingChangeDetector scd = new SourceControlSettingChangeDetector(currentSettings, newSettings);
    boolean hasToCleanUp = false;
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.GENERIC_VCS_GET_CHANGES_COMMAND);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
    if (hasToCleanUp) {
      setHasToCleanUp();
    }
    // update current settings map
    replaceCurrentSettings(newSettings);
  }
}
