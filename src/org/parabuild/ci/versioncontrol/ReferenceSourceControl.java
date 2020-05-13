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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.BuildScriptGenerator;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.internal.LocalBuilderFiles;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;

import java.io.IOException;
import java.util.Map;

/**
 * ReferenceSourceControl serves reference builds.
 * <p/>
 * Reference version control implements delegate pattern. It
 * makes decision to wich version control it delegates call bases
 * on build reference ID obtained from build configuration.
 */
final class ReferenceSourceControl implements SourceControl {

  private final SourceControl delegate;
  private final ConfigurationManager cm;
  private int buildID = BuildConfig.UNSAVED_ID;


  ReferenceSourceControl(final BuildConfig buildConfig, final SourceControl delegate) {
    this.cm = ConfigurationManager.getInstance();
    this.delegate = delegate;
    this.buildID = buildConfig.getBuildID();
  }


  /**
   * Returns ID of list of changes that were made to controlled
   * source line since the given change list ID.
   * <p/>
   * ReferenceVersion control uses referred build change lists
   * stored in the database instead of accessing version control
   * system.
   */
  public int getChangesSince(final int startChangeListID) {
    // here we get a list of our referred build change list after
    // this change list to the last clean referred build

    // REVIEWME: consider doing it at one shot
    final SourceControlSetting setting = cm.getSourceControlSetting(buildID, VersionControlSystem.REFERENCE_BUILD_ID);
    final int referredActiveBuildID = cm.getActiveIDFromBuildID(setting.getPropertyValueAsInt());
    final int activeBuildID = cm.getActiveIDFromBuildID(buildID);
    return cm.copyChangeListsToBuild(referredActiveBuildID, activeBuildID, startChangeListID);
  }


  /**
   * Returns relative project path
   */
  public String getRelativeBuildDir() throws BuildException, AgentFailureException {
    return delegate.getRelativeBuildDir();
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
  public Map getUsersMap() throws CommandStoppedException, AgentFailureException {
    return delegate.getUsersMap();
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
  public void label(final String label) throws BuildException, CommandStoppedException, AgentFailureException {
    delegate.label(label);
  }


  /**
   * Removes label with a given name.
   *
   * @param labels to remove.
   */
  public int removeLabels(final String[] labels) throws BuildException, CommandStoppedException, AgentFailureException {
    return delegate.removeLabels(labels);
  }


  /**
   * Syncs to a given change list number
   */
  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    delegate.syncToChangeList(changeListID);
  }


  /**
   * Checks out latest state of the source line.
   */
  public void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    delegate.checkoutLatest();
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
  public void reloadConfiguration() {
    delegate.reloadConfiguration();
  }


  /**
   * Returns text description of a command to be used by a
   * customer to sync to a given changelist.
   *
   * @param changeListID
   */
  public String getSyncCommandNote(final int changeListID) throws AgentFailureException {
    return delegate.getSyncCommandNote(changeListID);
  }


  /**
   * Returns build ID associated with this SourceControl
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * @return Map with a shell variable name as a key and variable
   *         value as value. The shell variables will be made
   *         avaiable to the build commands.
   *         <p/>
   *         If not variables available, returns an empty map.
   * @see BuildScriptGenerator#addVariables(Map)
   */
  public Map getShellVariables() throws IOException, AgentFailureException {
    return delegate.getShellVariables();
  }


  /**
   * Cleans up local copy. Classes implementing SourceControl may
   * overwrite this method to deliver specific cleanup logic.
   * <p/>
   * At this moment the local copy is fixed for every build and
   * is defined by {@link LocalBuilderFiles#getCheckoutDir(boolean)}
   *
   * @throws IOException
   * @see LocalBuilderFiles#getCheckoutDir(boolean)
   * @see Agent#emptyCheckoutDir()
   */
  public boolean cleanupLocalCopy() throws IOException, AgentFailureException {
    return delegate.cleanupLocalCopy();
  }


  /**
   * Requests source control system to find a native change
   * list number. The found change list number is stored in
   * the list of pending change lists in the database.
   *
   * @param nativeChangeListNumber String native change list
   *                               number. Other version control systems may store
   *                               information other then change lists.
   * @return new changelist ID
   */
  public int getNativeChangeList(final String nativeChangeListNumber) throws IOException, CommandStoppedException, BuildException, AgentFailureException {
    return delegate.getNativeChangeList(nativeChangeListNumber);
  }


  public void setAgentHost(final AgentHost agentHost) {
    delegate.setAgentHost(agentHost);
  }


  public AgentHost getAgentHost() {
    return delegate.getAgentHost();
  }


  /**
   * @return Map with a reference variable name as a key and variable
   *         value as value. The reference variables will be made
   *         available to the user interface.
   *         <p/>
   *         This is a default implementation that returns an
   *         empty map.
   * @see BuildScriptGenerator#addVariables(Map)
   */
  public Map getBuildRunAttributes() throws IOException, AgentFailureException {
    return delegate.getBuildRunAttributes();
  }


  public String toString() {
    return "ReferenceSourceControl{" +
            "delegate=" + delegate +
            ", cm=" + cm +
            ", buildID=" + buildID +
            '}';
  }
}