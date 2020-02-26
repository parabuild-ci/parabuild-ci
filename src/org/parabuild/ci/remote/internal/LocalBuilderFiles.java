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
package org.parabuild.ci.remote.internal;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.RuntimeUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;

/**
 */
public final class LocalBuilderFiles implements Serializable {

  private static final long serialVersionUID = -7569529818440910730L; // NOPMD

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(LocalBuilderFiles.class); // NOPMD

  private int buildID = BuildConfig.UNSAVED_ID;
  private final String customCheckoutDir;
  private File buildLogDir = null;
  private File checkoutDir = null;
  private File checkoutHomeDir = null;
  private File mainBuildPasswordDir = null;
  private File stepsScriptsDir = null;
  private File systemWorkingDir = null;
  private File tempDirectory = null;

  private static int logFileSourceControlSource = 0;


  public LocalBuilderFiles(final int buildID, final String customCheckoutDir) throws IOException {
    this.buildID = buildID;
    this.customCheckoutDir = customCheckoutDir;
    createBuildDirs();
  }


  /**
   * Creates all directories used by the build.
   */
  public void createBuildDirs() throws IOException {

    // create checkout directory
    final String systemWorkDirName = ConfigurationManager.getSystemWorkDirectoryName();

    // create build scripts directory
    systemWorkingDir = new File(systemWorkDirName);
    IoUtils.createDirs(systemWorkingDir);

    if (StringUtils.isBlank(customCheckoutDir)) {
      final String checkoutHomeDirName = systemWorkDirName + File.separator + 'b' + buildID + "co";
      checkoutHomeDir = new File(checkoutHomeDirName);
      checkoutDir = new File(checkoutHomeDirName + File.separator + 'a' + File.separator + 'u' + File.separator + 't' + File.separator + 'o' + File.separator);
      IoUtils.createDirs(checkoutDir.getParentFile()); // checkout createt only up to the last dir.
    } else {
      checkoutDir = new File(customCheckoutDir);
      checkoutHomeDir = new File(customCheckoutDir);
      IoUtils.createDirs(checkoutDir);
    }

    // create build scripts directory
    stepsScriptsDir = new File(systemWorkDirName + File.separator + 'b' + buildID + "sc");
    IoUtils.createDirs(stepsScriptsDir);

    // create main password directory
    mainBuildPasswordDir = new File(systemWorkDirName + File.separator + 'b' + buildID + "pw");
    IoUtils.createDirs(mainBuildPasswordDir);

    // create main temp directory
    tempDirectory = new File(systemWorkDirName + File.separator + 'b' + buildID + "tm");
    IoUtils.createDirs(tempDirectory);

    // create build logs dir
    buildLogDir = new File(archiveHome(), 'b' + Integer.toString(buildID));
    IoUtils.createDirs(buildLogDir);
  }


  /**
   * Returns a temporary directory dedicated to this build
   */
  public File getTempDirectory() {
    return tempDirectory;
  }


  /**
   * @return the home of the system work dir.
   */
  public File getSystemWorkingDir() {
    return systemWorkingDir;
  }


  /**
   * @return File directory used by a build to checkout a source
   *         line
   */
  public File getCheckoutDir(final boolean create) throws IOException {
    if (create) {
      IoUtils.createDirs(checkoutDir);
    } else {
      IoUtils.createDirs(checkoutDir.getParentFile()); // only parent, See #740
    }
    return checkoutDir;
  }


  public File getMainBuildPasswordDir() {
    return mainBuildPasswordDir;
  }


  /**
   * This is a directory under which long checkout dir is
   * created. Long checkout dir is used for actual checkout.
   *
   * @see #getCheckoutDir
   */
  public File getCheckoutHomeDir() {
    return checkoutHomeDir;
  }


  /**
   * @return File directory used by a build for storing internal
   *         build wrappers scripts. The warapper scripts perform
   *         serivce actions, change to the source line dir and
   *         call configured build sequence scripts.
   */
  public File getStepsScriptsDirectory() {
    return stepsScriptsDir;
  }


  /**
   * @return file to store sequence script file
   */
  public File getStepScriptFile(final int sequenceID) {
    return new File(stepsScriptsDir,
            "s" + sequenceID + makeStepScriptExtension());
  }


  /**
   * Creates new version control log file. It's guaranteed the
   * the returned file will have a distinct name.
   */
  public File makeNewSourceControlLogFile() {
    synchronized (LocalBuilderFiles.class) {
      logFileSourceControlSource++;
    }
    return new File(buildLogDir,
            getSourceControlLogPrefix()
                    + '_' + System.currentTimeMillis() + '_' + logFileSourceControlSource + ".log");
  }


  /**
   * @return Version control log prefix
   */
  public String getSourceControlLogPrefix() {
    return "scm_" + buildID;
  }


  /**
   * Creates new version control log file. It's guaranteed the
   * the returned file will have a distinct name.
   */
  public File makeNewSourceControlPasswordFile() throws IOException {
    return IoUtils.createTempFile("pwd", ".cvspass", mainBuildPasswordDir);
  }


  /**
   * Helper method. Makes sequence script extension string like
   * ".bat" or ".sh"
   *
   * @return String with script exension
   */
  private static String makeStepScriptExtension() {
    String extension = null;
    if (RuntimeUtils.isUnix()) {
      extension = ".sh";
    } else if (RuntimeUtils.isWindows()) {
      extension = ".bat";
    } else {
      throw new IllegalStateException("Uknown Operating System");
    }
    return extension;
  }


  /**
   * @return File main build log directory. The main log
   *         directory is a directory where build runner wrtites
   *         the buin build log to.
   */
  public File getBuildLogDir() {
    return buildLogDir;
  }


  /**
   * Returns buildID the files are set for
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Return build log home directory. Each build must have his
   * own subdir in this dir.
   * <p/>
   * If directory does not exist, creates it.
   */
  public static File archiveHome() {
    final File archiveHome = new File(ConfigurationConstants.CATALINA_BASE + ConfigurationConstants.FS + "logs");
    if (!archiveHome.exists()) archiveHome.mkdirs();
    return archiveHome;
  }


  public String toString() {
    return "LocalBuilderFiles{" +
            "buildID=" + buildID +
            ", customCheckoutDir='" + customCheckoutDir + '\'' +
            ", buildLogDir=" + buildLogDir +
            ", checkoutDir=" + checkoutDir +
            ", checkoutHomeDir=" + checkoutHomeDir +
            ", mainBuildPasswordDir=" + mainBuildPasswordDir +
            ", stepsScriptsDir=" + stepsScriptsDir +
            ", systemWorkingDir=" + systemWorkingDir +
            ", tempDirectory=" + tempDirectory +
            '}';
  }
}
