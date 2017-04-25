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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.DirectoryTraverserCallback;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.RuntimeUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.process.OsCommand;
import org.parabuild.ci.process.TailBufferSize;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.services.ModifiedFileList;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;
import org.parabuild.ci.services.TailUpdate;

/**
 * @noinspection ProhibitedExceptionThrown
 */
public final class LocalAgent implements Agent {

  private static final Log log = LogFactory.getLog(LocalAgent.class);

  private int activeBuildID = BuildConfig.UNSAVED_ID;
  private final LocalBuilderFiles bf;
  private LocalAgentEnvironment localBuilderEnvironment = null;


  /**
   * Constructor
   *
   * @param checkoutDir - custom checkout dir. Can be
   *                    empty or null. If empty or null a default location for
   *                    a checout dir will be used.
   */
  public LocalAgent(final int activeBuildID, final String checkoutDir) throws IOException {
    this.activeBuildID = activeBuildID;
    this.bf = new LocalBuilderFiles(activeBuildID, checkoutDir);
    this.localBuilderEnvironment = new LocalAgentEnvironment();
  }


  /**
   * @return this Agent's buildID
   */
  public int getActiveBuildID() {
    return activeBuildID;
  }


  public String cygwinWindowsPathToUnix(final String path) throws IOException {
    return localBuilderEnvironment.cygwinWindowsPathToUnix(path);
  }


  /**
   * Removes all files from checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  public boolean emptyCheckoutDir() throws IOException {
    // get dir
    final File checkoutDir = bf.getCheckoutDir(true);

    // empty soft
    IoUtils.emptyDir(checkoutDir);

    // clean up hard if necessary
    if (!checkoutDirIsEmpty() && checkoutDir.exists()) {
      // delete hard
      RuntimeUtils.deleteHard(checkoutDir);
      // re-create
      bf.createBuildDirs();
    }
    return checkoutDirIsEmpty();
  }


  /**
   * Deletes a file or a directory. All exceptions
   * are ignored.
   *
   * @param path
   * @return true if a file was deleted or false if it was not
   */
  public boolean deleteFileHard(final String path) {
    if (StringUtils.isBlank(path)) {
      return true;
    }
    final File file = new File(path);
    return IoUtils.deleteFileHard(file) || RuntimeUtils.deleteHard(file);
  }


  public boolean deleteCheckoutDir() throws IOException {
    // first try to delte using java means.
    final File checkoutDir = bf.getCheckoutDir(false);
    final boolean deleteResult = IoUtils.deleteFileHard(checkoutDir);
    if (log.isDebugEnabled()) {
      log.debug("delete results for: " + checkoutDir + " is " + deleteResult);
    }

    // if it didn't work try forced using runtime
    if (checkoutDir.exists()) {
      RuntimeUtils.deleteHard(checkoutDir);
    }

    // return result
    return !checkoutDir.exists();
  }


  /**
   * Returns checkout directory home. All project files are
   * checked out under this directory.
   */
  public String getCheckoutDirHome() throws IOException {
    return bf.getCheckoutHomeDir().getCanonicalPath();
  }


  /**
   * Returns true if checkout dir is empty.
   */
  public boolean checkoutDirIsEmpty() throws IOException {
    //noinspection UnnecessaryLocalVariable
    final boolean result = bf.getCheckoutDir(true).listFiles().length == 0;
    //if (log.isDebugEnabled()) log.debug("Checking checkout dir: " + bf.getCheckoutDir() + ", result: " + result);
    return result;
  }


  /**
   * Removes all files from log dir.
   *
   * @return true if there are no files left after cleanup
   */
  public boolean emptyLogDir() {
    IoUtils.emptyDir(bf.getBuildLogDir());
    return logDirIsEmpty();
  }


  /**
   * Returns true if log dir is empty.
   */
  public boolean logDirIsEmpty() {
    return bf.getBuildLogDir().listFiles().length == 0;
  }


  /**
   * Removes all files from password dir.
   *
   * @return true if there are no files left after cleanup
   */
  public boolean emptyPasswordDir() {
    IoUtils.emptyDir(bf.getMainBuildPasswordDir());
    return passwordDirIsEmpty();
  }


  /**
   * Returns true if logd dir is empty.
   */
  public boolean passwordDirIsEmpty() {
    return bf.getMainBuildPasswordDir().listFiles().length == 0;
  }


  /**
   * Returns true if checkout dir exists
   */
  public boolean checkoutDirExists() throws IOException {
    return bf.getCheckoutDir(false).exists();
  }


  /**
   * Executes command in Agent box.
   *
   * @param executorHandle
   * @param relativeDirectoryToExecuteIn directory absolute or relative
   *                                     to checkout path
   * @param command                      to execute
   * @param environment
   * @param tailBufferSize
   * @param stdoutFile                   name of a caller's local file stdout will
   *                                     we delivered to.
   * @param stderrFile                   name of a caller's local file stderr will
   *                                     we delivered to.
   * @param mergedFile                   name of a caller's local file merged
   *                                     stdout and stderr will we delivered to.
   * @return command error code
   * @throws IOException
   */
  public int execute(final int executorHandle, final String relativeDirectoryToExecuteIn, final String command, final Map environment, final TailBufferSize tailBufferSize, final File stdoutFile, final File stderrFile, final File mergedFile) throws IOException, CommandStoppedException {
    String directoryName = null;
    if (relativeDirectoryToExecuteIn != null) {
      if (new File(relativeDirectoryToExecuteIn).isAbsolute()) {
        directoryName = new File(relativeDirectoryToExecuteIn).getCanonicalPath();
      } else {
        directoryName = new File(bf.getCheckoutDir(true), relativeDirectoryToExecuteIn).getCanonicalPath();
      }
    } else { // NOPMD
      // NOTE: vimeshev - 2006-02-10 - we disable this to minimize
      // the amount of environment information passed around in attempt
      // to fix a native memory leak under Linux.

      //dir = new File(bf.getCheckoutDir(true), "");
    } // NOPMD

    final OsCommand osCommand = new OsCommand(executorHandle, directoryName,
            command, environment, tailBufferSize, stdoutFile, stderrFile, mergedFile);
    return osCommand.execute();
  }


  /**
   * Returns true if command is available.
   *
   * @param command to check if available
   */
  public boolean commandIsAvailable(final String command) {
    return localBuilderEnvironment.commandIsAvailable(command);
  }


  /**
   * Returns agent's system type.
   *
   * @return agent's system type.
   * @see AgentEnvironment#SYSTEM_TYPE_CYGWIN
   * @see AgentEnvironment#SYSTEM_TYPE_SUNOS
   * @see AgentEnvironment#SYSTEM_TYPE_WIN95
   * @see AgentEnvironment#SYSTEM_TYPE_WINNT
   */
  public int systemType() {
    return localBuilderEnvironment.systemType();
  }


  /**
   * Returns name of remote checkout directory.
   *
   * @return name of remote checkout directory
   * @throws IOException
   */
  public String getCheckoutDirName() throws IOException {
    return bf.getCheckoutDir(true).getCanonicalPath();
  }


  /**
   * Returns name of remote checkout directory.
   *
   * @return name of remote checkout directory
   * @throws IOException
   */
  public String getTempDirName() throws IOException {
    return bf.getTempDirectory().getCanonicalPath();
  }


  /**
   * Returns name of home directory for build files.
   *
   * @return Returns name of home directory for build files.
   * @throws IOException
   */
  public String getSystemWorkingDirName() throws IOException {
    return bf.getSystemWorkingDir().getCanonicalPath();
  }


  /**
   * @return file separator
   */
  public String separator() {
    return localBuilderEnvironment.separator();
  }


  /**
   * Returns true if file relative to checkout dir exists.
   */
  public boolean fileRelativeToCheckoutDirExists(final String relativePath) throws IOException {
    return new File(bf.getCheckoutDir(false), relativePath).exists();
  }


  /**
   * Returns true if dir relative to checkout dir exists.
   */
  public boolean dirRelativeToCheckoutDirIsEmpty(final String relativePath) throws IOException {
    final String[] names = new File(bf.getCheckoutDir(false), relativePath).list();
    return names != null && names.length == 0;
  }


  /**
   * Returns true if given path, either file or dir, exists.
   */
  public boolean absolutePathExists(final String absolutePath) {
    return new File(absolutePath).exists();
  }


  public boolean deleteFileUnderCheckoutDir(final String path) throws IOException {
    final File file = new File(path);
    if (file.isAbsolute() && IoUtils.isFileUnder(file, bf.getCheckoutDir(false))) {
      final boolean result = IoUtils.deleteFileHard(file);
      if (log.isDebugEnabled()) {
        log.debug("file: " + file + ", result: " + result);
      }
      return result;
    } else {
      final boolean result = IoUtils.deleteFileHard(new File(bf.getCheckoutDir(false), path));
      if (log.isDebugEnabled()) {
        log.debug("file: " + new File(bf.getCheckoutDir(false), path) + ", result: " + result);
      }
      return result;
    }
  }


  /**
   * Explicitely requests build host to create needed build
   * directories.
   */
  public void createBuildDirs() throws IOException {
    bf.createBuildDirs();
  }


  /**
   * Returns true if temp dir exists
   *
   * @param relativePathInTempDirectory
   */
  public boolean relativeTempPathExists(final String relativePathInTempDirectory) {
    return new File(bf.getTempDirectory(), relativePathInTempDirectory).exists();
  }


  /**
   * Return true if agent host system is Windows
   */
  public boolean isWindows() {
    return localBuilderEnvironment.isWindows();
  }


  /**
   * Return true if agent host system is Unix
   */
  public boolean isUnix() {
    return localBuilderEnvironment.isUnix();
  }


  /**
   * Creates a temp file and writes given string to it.
   */
  public String createTempFile(final String prefix, final String suffix, final String content) throws IOException {
    BufferedWriter bw = null;
    try {
      final File file = IoUtils.createTempFile(prefix, suffix, bf.getTempDirectory());
      //if (log.isDebugEnabled()) log.debug("temp file: " + file);
      bw = new BufferedWriter(new FileWriter(file));
      bw.write(content);
      //if (log.isDebugEnabled()) log.debug("returning: " + file.getCanonicalPath());
      return file.getCanonicalPath();
    } finally {
      IoUtils.closeHard(bw);
    }
  }


  public boolean deleteTempFile(final String name) throws IOException {
    if (StringUtils.isBlank(name)) {
      return true;
    }
    final File file = new File(name);
    if (!fileIsUnderTempDir(file)) {
      throw new IOException("Illegal temp file path \"" + file.getCanonicalPath() + '\"');
    }
    return IoUtils.deleteFileHard(file);
  }


  public boolean deleteTempFileHard(final String tempFileName) {
    try {
      return deleteTempFile(tempFileName);
    } catch (Exception e) {
      final ErrorManager em = ErrorManagerFactory.getErrorManager();
      final org.parabuild.ci.error.Error error = new org.parabuild.ci.error.Error("Could not delete temporary file");
      error.setDescription("Error while deleting temporary \"" + tempFileName + "\": " + StringUtils.toString(e));
      error.setErrorLevel(org.parabuild.ci.error.Error.ERROR_LEVEL_WARNING);
      error.setHostName(AgentConfig.BUILD_MANAGER);
      error.setDetails(e);
      em.reportSystemError(error);
      return false;
    }
  }


  /**
   * Create a sequence script file by writing scriptContent to
   * the newly created file.
   *
   * @param sequenceID    to create a script for.
   * @param scriptContent content of the shell script.
   * @return absolute path to created file
   * @throws IOException
   */
  public String createStepScript(final int sequenceID, final String scriptContent) throws IOException {
    FileWriter fw = null;
    try {
      final File script = new File(makeStepScriptPath(sequenceID));
      if (script.exists()) {
        script.delete();
      }
      fw = new FileWriter(script);
      fw.write(scriptContent);
      return script.getAbsolutePath();
    } finally {
      IoUtils.closeHard(fw);
    }
  }


  /**
   * Makes a script file name to be used to write a step script.
   *
   * @param sequenceID to create a script for.
   * @return absolute path to created file
   * @throws IOException
   */
  public String makeStepScriptPath(final int sequenceID) throws IOException {
    return bf.getStepScriptFile(sequenceID).getCanonicalPath();
  }


  /**
   * Fixes CRLF according to local system.
   *
   * @param stringToFix
   * @return String with fixed CRLF according to local system.
   */
  public String fixCRLF(final String stringToFix) {
    return StringUtils.fixCRLF(stringToFix);
  }


  /**
   * Deletes all build files.
   *
   * @return true if all directories were deleted.
   * @see LocalBuilderFiles#getBuildLogDir()
   * @see LocalBuilderFiles#getCheckoutHomeDir()
   * @see LocalBuilderFiles#getMainBuildPasswordDir()
   * @see LocalBuilderFiles#getStepsScriptsDirectory()
   * @see LocalBuilderFiles#getTempDirectory()
   */
  public boolean deleteBuildFiles() {
    boolean result = true;
    result &= deleteBuildDirHard(bf.getMainBuildPasswordDir());
    result &= deleteBuildDirHard(bf.getStepsScriptsDirectory());
    result &= deleteBuildDirHard(bf.getBuildLogDir());
    result &= deleteBuildDirHard(bf.getTempDirectory());
    result &= deleteBuildDirHard(bf.getCheckoutHomeDir());
    return result;
  }


  /**
   * Returns agent's host
   *
   * @return agent's host
   */
  public AgentHost getHost() {
    return new AgentHost(AgentConfig.BUILD_MANAGER, "");
  }


  /**
   * @return Agent's current time in milliseconds, same as
   *         {@link System#currentTimeMillis()) but for the remote
   *         agent.
   * @see System#currentTimeMillis()
   */
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }


  /**
   * @param path                 to check for modified files. Can be a directory or a single file.
   * @param timeSinceMillis      cut off time in milliseconds.
   * @param maximumNumberOfFiles limits number of returned files.
   * @return List of {@link RemoteFileDescriptor object} for
   *         files that were modified after the time provided by
   *         timeSinceMillis.
   * @throws FileNotFoundException if path not found
   * @throws IOException
   */
  public ModifiedFileList getModifiedFiles(final String path, final long timeSinceMillis, final int maximumNumberOfFiles) throws IOException {
    final List descriptorList = new ArrayList(101);

    // check if file exists
    final File file = new File(path);
    if (!file.exists()) {
      throw new FileNotFoundException("File not found: \"" + path + '\"');
    }

    // process file
    final long[] maxTimeStamp = new long[1];
    IoUtils.traversePath(file, new DirectoryTraverserCallback() {
      public boolean callback(final File file) throws IOException {
//        if (log.isDebugEnabled()) log.debug("file: " + file);
        // REVIEWME: will traverse further even if
        // the counter limit is reached.
//        if (log.isDebugEnabled()) log.debug("file.lastModified(): " + file.lastModified());
        if (file.isFile() && file.lastModified() > timeSinceMillis) {
          descriptorList.add(new RemoteFileDescriptor(file));
          maxTimeStamp[0] = Math.max(maxTimeStamp[0], file.lastModified());
        }
        return descriptorList.size() < maximumNumberOfFiles;
      }
    });
    return new ModifiedFileList(maxTimeStamp[0], descriptorList);
  }


  private boolean deleteBuildDirHard(final File dir) {
    if (IoUtils.deleteFileHard(dir)) {
      return true;
    }
    if (RuntimeUtils.deleteHard(dir)) {
      return true;
    } else {
      log.error("Could not delete directory: " + dir.toString());
      return false;
    }
  }


  /**
   * Returns true if the given absolute path is a file
   */
  public boolean pathIsFile(final String file) {
    return new File(file).isFile();
  }


  /**
   * Returns true if the given absolute path is a directory
   */
  public boolean pathIsDirectory(final String path) {
    return new File(path).isDirectory();
  }


  /**
   * Gets a file or a directory presented by the absolute path
   * into the given local file.
   *
   * @param path
   * @param readInTo
   */
  public void readFile(final String path, final File readInTo) throws IOException {
    try {
      IoUtils.copyFile(new File(path), readInTo);
    } catch (IOException e) {
      // Delete destination file
      IoUtils.deleteFileHard(readInTo);
      // Re-throw the exception
      throw e;
    } catch (RuntimeException e) {
      // Delete destination file
      IoUtils.deleteFileHard(readInTo);
      // Re-throw the exception
      throw e;
    }
  }


  public void getDirectory(final String path, final File readInTo) throws IOException {
    final File source = new File(path);
    try {
      IoUtils.copyDirectory(source, readInTo);
    } catch (IOException e) {
      // Delete destination file
      IoUtils.deleteFileHard(readInTo);
      // Re-throw the exception
      throw e;
    } catch (RuntimeException e) {
      // Delete destination file
      IoUtils.deleteFileHard(readInTo);
      // Re-throw the exception
      throw e;
    }
  }


  /**
   * Returns name part of the file presented by absolute path
   */
  public String getFileName(final String absolutePath) {
    return new File(absolutePath).getName();
  }


  /**
   * Return agent system property.
   *
   * @param propertyName
   * @return agent system property.
   */
  public String getSystemProperty(final String propertyName) {
    return System.getProperty(propertyName);
  }


  /**
   * Return agent shell environment variable.
   *
   * @param varName to return value for
   * @return agent shell environment variable.
   */
  public String getEnvVariable(final String varName) {
    return localBuilderEnvironment.getEnvVariable(varName);
  }


  /**
   * Returns agent's path separator.
   */
  public String pathSeparator() {
    return localBuilderEnvironment.pathSeparator();
  }


  /**
   * Creates a file with given context. If parent directories
   * don't exist, they get created. File should reside under
   * build's checkout dir.
   *
   * @param absoluteFile
   * @param content
   */
  public void createFile(final String absoluteFile, final String content) throws IOException {
    // REVIEWME: simeshev@parabuilci.org -> security
    FileWriter fw = null;
    try {

      // REVIEWME: simeshev@parabuilci.org -> temporarely changed to creating dirs
      // automatically - change back and make sure callers
      // are calling mkdirs from Agent interface.

      // create dirs
      final File file = new File(absoluteFile);
      if (file.getParentFile() != null) {
        file.getParentFile().mkdirs();
      }
      // write file
      fw = new FileWriter(absoluteFile);
      fw.write(content);
    } finally {
      IoUtils.closeHard(fw);
    }
  }


  /**
   * Create directories for given path under build checkout dir.
   *
   * @param absolutePath
   */
  public boolean mkdirs(final String absolutePath) throws IOException {
    // REVIEWME: simeshev@parabuilci.org -> security
    final File file = new File(absolutePath);
    if (!file.isAbsolute()) {
      throw new IOException("File is not absolute: " + absolutePath);
    }
    return file.mkdirs();
  }


  /**
   * Returns agent's locale.
   */
  public Locale defaultLocale() {
    return Locale.getDefault();
  }


  public RemoteFileDescriptor getFileDescriptor(final String absolutePath) throws IOException {
    return new RemoteFileDescriptor(new File(absolutePath).getCanonicalFile());
  }


  /**
   * Lists files under given path
   *
   * @param path
   */
  public String[] listFilesInDirectory(final String path, final String extensions) throws IOException {
    // System.out.println("DEBUG: list files ");

    final Set extensionSet = new HashSet(5); // we use extensionSet to avoid extension dupes
    if (StringUtils.isBlank(extensions)) {
      extensionSet.add("");
    } else {
      // parse extension list
      for (final StringTokenizer st = new StringTokenizer(extensions, ",; ", false); st.hasMoreTokens();) {
        final String token = st.nextToken();
        // add dot if necessary, IoUtils.ExtensionFileNameFilter needs it
        final String extension = token.charAt(0) == '.' ? token : '.' + token;
        extensionSet.add(extension);
      }
    }
    // define result
    final List result = new ArrayList(11);

    // iterate extensions
    // System.out.println("DEBUG: path: " + path);
    for (final Iterator iterator = extensionSet.iterator(); iterator.hasNext();) {
      final String extension = (String) iterator.next();
      // System.out.println("DEBUG: extension: " + extension);
      // list files with this extenson and add to result
      final File[] files = new File(path).listFiles(new IoUtils.ExtensionFileFilter(extension));
      // System.out.println("DEBUG: files: " + files);
      if (files == null || files.length == 0) {
        continue;
      }
      for (int i = 0; i < files.length; i++) {
        result.add(files[i].getCanonicalPath());
      }
    }

    // return result
    return StringUtils.toStringArray(result);
  }

  /**
   * Returns a diff for a log tail for running build.
   *
   * @param lastTailEndLineNumber to start the diff from.
   *
   * For instance, if a requester has a tail that started a build
   * log line number Ns1 and the length of the tail was Nl1, and
   * the log moved to Ns2 with the same length, the diff
   * requesting lines starting Ns1+Nl1 will return the number of
   */
//  public BuildLogTail getBuildLogTailDiff(final int lastTailEndLineNumber) {
//    final BuildLogTail currentTail = BuildLogTailManager.getInstance().getLogTail(activeBuildID);
//    final int currentTailBeginLineNumber = currentTail.getTailBegintLineNumber();
//    final int currentTailEndLineNumber = currentTail.getTailEndLineNumber();
//    // check if there was movement
//    if (lastTailEndLineNumber == currentTailEndLineNumber) {
//      // no changes, return zero diff
//      return new BuildLogTail(lastTailEndLineNumber);
//    } else if (lastTailEndLineNumber  > currentTailBeginLineNumber) {
//      // the top of the tail has moved but it hasn't crossed the bottom of the last requested.
//      final List lines = currentTail.getLines();
//      for (int i = lastTailEndLineNumber; i < currentTailEndLineNumber; i++) {
//
//      }
//    }
//  }


  /**
   * Helper method. Returns true if a file is under a system or a
   * build's temp dir.
   */
  private boolean fileIsUnderTempDir(final File file) throws IOException {
    final File buildTempDir = bf.getTempDirectory();
    final boolean underBuildTemDir = IoUtils.isFileUnder(file, buildTempDir);
    final String systemTempDirName = System.getProperty("java.io.tmpdir");
    final File systemTempDir = new File(systemTempDirName);
    final boolean underSystemTempDir = IoUtils.isFileUnder(file, systemTempDir);
    return underBuildTemDir || underSystemTempDir;
  }


  /**
   * Deletes all content from the script dir.
   */
  public boolean emptyScriptDir() {
    IoUtils.emptyDir(bf.getStepsScriptsDirectory());
    return scriptDirIsEmpty();
  }


  /**
   * Returns true if script directory is empty
   */
  public boolean scriptDirIsEmpty() {
    return bf.getStepsScriptsDirectory().listFiles().length == 0;
  }


  public String getLocalHostName() {
    return IoUtils.getLocalHostNameHard();
  }


  /**
   * @return a new executor handle that can be used to
   *         access a remote command's environement.
   */
  public int createCommandHandle() {
    return localBuilderEnvironment.createExecutorHandle();
  }


  public TailUpdate getTailUpdate(final int commandHandle, final long sinceServerTimeMs) {
    if (log.isDebugEnabled()) {
      log.debug("commandHandle: " + commandHandle);
    }
    final TailUpdate tailUpdate = new Tail(commandHandle).getTailUpdate(sinceServerTimeMs);
    if (log.isDebugEnabled()) {
      log.debug("tailUpdate: " + tailUpdate);
    }
    return tailUpdate;
  }


  public String toString() {
    return "LocalAgent{" +
            "activeBuildID=" + activeBuildID +
            ", bf=" + bf +
            ", localBuilderEnvironment=" + localBuilderEnvironment +
            '}';
  }
}
