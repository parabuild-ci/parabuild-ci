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
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.internal.LocalBuilderFiles;

import java.io.IOException;
import java.util.Map;

/**
 * The SourceControl interface defines a common interface to a
 * cource control system.
 */
public interface SourceControl {

  /**
   * Checks out latest state of the source line
   */
  void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException;


  /**
   * Syncs to a given change list number
   */
  void syncToChangeList(int changeListID) throws BuildException, CommandStoppedException, AgentFailureException;


  /**
   * Returns relative project path
   */
  String getRelativeBuildDir() throws BuildException, AgentFailureException;


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
  int getChangesSince(int startChangeListID) throws BuildException, CommandStoppedException, AgentFailureException;


  /**
   * Labels the last synced checkout directory with the given
   * label.
   * <p/>
   * Must throw a BuildException if there was no last sync made
   * or if checkout directory is empty.
   *
   * @param label
   */
  void label(String label) throws BuildException, CommandStoppedException, AgentFailureException;


  /**
   * Removes label with a given name.
   *
   * @param labels to remove.
   * @return int number of removed labels
   */
  int removeLabels(String[] labels) throws BuildException, CommandStoppedException, AgentFailureException;


  /**
   * Returns a map containing version control user names as keys
   * and e-mails as values. This method doesn't throw exceptions
   * as it's failure is not critical but it reports errors by
   * calling to ErrorManager.
   *
   * @see ErrorManagerFactory
   * @see ErrorManager
   */
  Map getUsersMap() throws CommandStoppedException, AgentFailureException;


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
  void reloadConfiguration();


  /**
   * Returns text description of a command to be used by a
   * customer to sync to a given changelist.
   *
   * @param changeListID
   */
  String getSyncCommandNote(int changeListID) throws AgentFailureException;


  /**
   * Returns build ID associated with this SourceControl
   */
  int getBuildID();


  /**
   * @return Map with a shell variable name as a key and variable
   *         value as value. The shell variables will be made
   *         avaiable to the build commands.
   *         <p/>
   *         If not variables available, returns an empty map.
   * @see BuildScriptGenerator#addVariables(Map)
   */
  Map getShellVariables() throws IOException, AgentFailureException;


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
  boolean cleanupLocalCopy() throws IOException, AgentFailureException;


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
  int getNativeChangeList(final String nativeChangeListNumber) throws IOException, CommandStoppedException, BuildException, AgentFailureException;

  /**
   * Sets agent host this source control should operate on.
   * <p/>
   * This method should be called first before any other method is called.
   *
   * @param agentHost this source control should operate on.
   */
  void setAgentHost(AgentHost agentHost);

  /**
   * Returns agent host or null if not set.
   *
   * @return agent host or null if not set.
   */
  AgentHost getAgentHost();

  /**
   * @return Map with a reference variable name as a key and variable
   *         value as value. The reference variables will be made
   *         available to the user interface.
   *         <p/>
   *         This is a default implementation that returns an
   *         empty map.
   * @see BuildScriptGenerator#addVariables(Map)
   */
  Map getBuildRunAttributes() throws IOException, AgentFailureException;
}
