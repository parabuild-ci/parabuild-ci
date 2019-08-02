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
package org.parabuild.ci.installer;

import com.install4j.api.context.FileOptions;
import com.install4j.api.context.InstallerContext;
import com.install4j.api.context.OverwriteMode;
import com.install4j.api.context.UserCanceledException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.NullOutputStream;
import org.parabuild.ci.common.RuntimeUtils;
import org.parabuild.ci.common.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Realizes Template Method pattern.
 */
public abstract class AbstractUnixDaemonCreator implements UnixDaemonCreator {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(AbstractUnixDaemonCreator.class); // NOPMD


  private InstallerContext installerContext = null;
  private DirectoryOwnerChanger directoryOwnerChanger = null;


  protected AbstractUnixDaemonCreator(final DirectoryOwnerChanger directoryOwnerChanger) {
    this.directoryOwnerChanger = directoryOwnerChanger;
  }


  public final void createDaemon(final InstallerContext installerContext) throws IOException, UserCanceledException {
    this.installerContext = installerContext;
    createGroup();
    createUser();
    installDaemon();
    createRunlevelSymlinks();
    chownDirs();
  }


  /**
   * Changes ownership of install dirs.
   */
  private void chownDirs() {
    directoryOwnerChanger.changeOwner(installerContext.getInstallationDirectory(), InstallerConstants.PARABUILD_USER);
  }


  public final void createGroup() throws IOException {
    try {
      if (!isGroupExists()) {
        // add group
        final OutputStream stdout = new NullOutputStream();
        final OutputStream stderr = new NullOutputStream();
        RuntimeUtils.execute(null, groupAddCommand() + ' ' + InstallerConstants.PARABUILD_GROUP, null, stdout, stderr);
      }
    } catch (final CommandStoppedException e) {
      throw IoUtils.createIOException("Command was stopped", e);
    }
  }


  public final void createUser() throws IOException {
    try {
      if (!isUserExists()) {
        // add user
        final OutputStream stdout = new NullOutputStream();
        final OutputStream stderr = new NullOutputStream();
        RuntimeUtils.execute(null, userAddCommand() + " -p * -m -g " + InstallerConstants.PARABUILD_GROUP + ' ' + InstallerConstants.PARABUILD_USER, null, stdout, stderr);
      }
    } catch (final CommandStoppedException e) {
      throw IoUtils.createIOException("Command was stopped", e);
    }
  }


  /**
   * @return command to add user
   */
  private static String userAddCommand() {
    return "/usr/sbin/useradd";
  }


  /**
   * @return command to add group
   */
  private static String groupAddCommand() {
    return "/usr/sbin/groupadd";
  }


  /**
   * @return true if our user exists
   */
  public static boolean isUserExists() throws IOException {
    boolean userFound = false;
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(new File("/etc/passwd")));
      for (String line = br.readLine(); line != null;) {
        if (line.startsWith(InstallerConstants.PARABUILD_USER + ':')) {
          userFound = true;
          break;
        }
        line = br.readLine();
      }
    } finally {
      IoUtils.closeHard(br);
    }
    return userFound;
  }


  /**
   * @return true if our group exists
   */
  public static boolean isGroupExists() throws IOException {
    boolean groupFound = false;
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(new File("/etc/group")));
      for (String line = br.readLine(); line != null;) {
        if (line.startsWith(InstallerConstants.PARABUILD_GROUP + ':')) {
          groupFound = true;
          break;
        }
        line = br.readLine();
      }
    } finally {
      IoUtils.closeHard(br);
    }
    return groupFound;
  }


  /**
   *
   */
  public final void createRunlevelSymlinks() throws IOException {// activate daemon
    createRunLevelSymlink(2);
    createRunLevelSymlink(3);
    createRunLevelSymlink(4);
    createRunLevelSymlink(5);
  }


  public final void installDaemon() throws UserCanceledException {
    final File installationDaemonScript = new File(installerContext.getInstallationDirectory(), "bin/parabuild");
    final String destinationScriptPath = getDaemonScriptDestinationPath();
    final File destinationDaemonScript = new File(destinationScriptPath, "parabuild");
    final FileOptions fileOptions = new FileOptions("755", OverwriteMode.IF_NEWER, false);
//    System.out.println("DEBUG: destinationDaemonScript: " + destinationDaemonScript);
    installerContext.installFile(installationDaemonScript, destinationDaemonScript, fileOptions);
  }


  private void createRunLevelSymlink(final int level) throws IOException {
    try {
      final String runLevelPath = getRunLevelPath(level);
      if (!StringUtils.isBlank(runLevelPath)) {
        final File directoryToExecuteIn = new File(runLevelPath);
        if (directoryToExecuteIn.exists()) {
          final OutputStream stdout = new NullOutputStream();
          final OutputStream stderr = new NullOutputStream();
          RuntimeUtils.execute(directoryToExecuteIn, "ln -f -s " + getRelativePathToDaemonScript() + " S99parabuild", null, stdout, stderr);
          RuntimeUtils.execute(directoryToExecuteIn, "ln -f -s " + getRelativePathToDaemonScript() + " K10parabuild", null, stdout, stderr);
        }
      }
    } catch (final CommandStoppedException e) {
      throw IoUtils.createIOException("Command was stopped", e);
    }
  }


  /**
   * Returns os-specific path to that daemon script will be
   * installed.
   *
   * @return s-specific path to that daemon script will be
   *         installed
   */
  public abstract String getDaemonScriptDestinationPath();


  /**
   * Returns path to run level dir where a link to a daemon
   * script will be created.
   *
   * @param level
   * @return
   */
  public abstract String getRunLevelPath(int level);


  /**
   * A path used to create a symlink in runlevel dir.
   *
   * @return
   * @see #getRunLevelPath(int)
   */
  protected abstract String getRelativePathToDaemonScript();


  public final String toString() {
    return "AbstractUnixDaemonCreator{" +
            "installerContext=" + installerContext +
            ", directoryOwnerChanger=" + directoryOwnerChanger +
            '}';
  }
}
