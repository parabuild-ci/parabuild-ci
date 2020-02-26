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
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.services.ModifiedFileList;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A surrogate source control that watches changes in file system.
 */
public final class FileSystemSourceControl extends AbstractCommandBasedSourceControl {

  private static final String FILES_CHANGED_SINCE = "Files changed since ";

  private static final Log log = LogFactory.getLog(FileSystemSourceControl.class); // NOPMD


  public FileSystemSourceControl(final BuildConfig buildConfig) {
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

      final List result = new ArrayList(101);
      final List files = new ArrayList(101);
      final Agent agent = getCheckoutDirectoryAwareAgent();
      long maxTimeStamp = 0L;
      for (final Iterator i = getPaths().iterator(); i.hasNext();) {
        final String path = (String) i.next();
        if (log.isDebugEnabled()) log.debug("getting changes for: " + path);
        if (path.startsWith("http://")) {
          // process http
          final URL url = new URL(path);
          final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
          final long urlDate = urlConnection.getLastModified();
          urlConnection.disconnect();
          // validate date
          if (urlDate == 0L) {
            throw new BuildException("URL \"" + url.toString() + "\" does not support modification dates.", getAgentHost());
          }
          // add if qualify
          if (urlDate > changeListTimestamp) {
            files.add(path);
            maxTimeStamp = Math.max(maxTimeStamp, urlDate);
          }
        } else {
          // process normal file
          final ModifiedFileList modifiedFiles = agent.getModifiedFiles(path, changeListTimestamp, rowLimit);
//          if (log.isDebugEnabled()) log.debug("changeListTimestamp: " + changeListTimestamp);
//          if (log.isDebugEnabled()) log.debug("rowLimit: " + rowLimit);
//          if (log.isDebugEnabled()) log.debug("modifiedFiles: " + modifiedFiles);
          maxTimeStamp = modifiedFiles.getMaxTimeStamp();
          for (final Iterator j = modifiedFiles.getFiles().iterator(); j.hasNext();) {
            final RemoteFileDescriptor remoteFileDescriptor = (RemoteFileDescriptor) j.next();
            files.add(remoteFileDescriptor.getCanonicalPath());
            j.remove(); // free up memory ASAP
          }
        }
      }

      // create change list
      if (!files.isEmpty()) {
        final ChangeList changeList = new ChangeList();
        changeList.setCreatedAt(new Date(maxTimeStamp));
        changeList.setUser(getSettingValue(VersionControlSystem.FILESYSTEM_VCS_USER, "system"));
        changeList.setDescription(FILES_CHANGED_SINCE + SystemConfigurationManagerFactory.getManager().formatDateTime(new Date(changeListTimestamp)));
        final Set changes = new HashSet(files.size());
        for (int i = 0, n = files.size(); i < n; i++) {
          final String filePath = (String) files.get(i);
          changes.add(new Change(filePath, "", Change.TYPE_MODIFIED));
        }
        changeList.setChanges(changes);
        result.add(changeList);
      }

      // return if no changes
      if (result.isEmpty()) {
        return startChangeListID;
      }

      // validate that change lists contain not only exclusions
      if (new ExclusionPathFinder().onlyExclusionPathsPresentInChangeLists(result, getSettingValue(VersionControlSystem.VCS_EXCLUSION_PATHS))) {
        return startChangeListID;
      }

      // store changes
      if (log.isDebugEnabled()) log.debug("end getChangesSince");
      return configManager.saveBuildChangeLists(activeBuildID, result);
    } catch (final RuntimeException | IOException e) {
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
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.FILESYSTEM_VCS_PATH);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
    if (hasToCleanUp) {
      setHasToCleanUp();
    }
    // update current settings map
    replaceCurrentSettings(newSettings);
  }


  /**
   *
   */
  private List getPaths() {
    return StringUtils.multilineStringToList(getSettingValue(VersionControlSystem.FILESYSTEM_VCS_PATH));
  }
}
