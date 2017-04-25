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
package org.parabuild.ci.remote.services;

import java.io.*;
import java.util.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.remote.internal.*;
import org.parabuild.ci.services.TailUpdate;
import org.parabuild.ci.process.TailBufferSize;

/**
 * Native environment of a agent box.
 */
public interface RemoteBuilderWebService extends Serializable {

  /**
   * @return agent version hash code
   */
  int builderVersionHashCode() throws IOException;


  /**
   * @return agent version hash code
   */
  String builderVersionAsString() throws IOException;


  /**
   * Return value of system environment variable.
   */
  String getEnvVariable(String variableName) throws IOException;


  /**
   * @return Map containing shell environment variables.
   */
  Map getEnv() throws IOException;


  /**
   * Returns system type
   */
  byte systemType() throws IOException;


  /**
   * Returns true if given executable is available.
   *
   * @param command
   * @return true if given executable is available.
   */
  boolean commandIsAvailable(String command) throws IOException;


  /**
   * Retuns true if agent runs under Windows
   */
  boolean isWindows() throws IOException;


  /**
   * Retuns true if agent runs under Unix
   */
  boolean isUnix() throws IOException;


  /**
   * Converts Windows path to Cygwin path. This method mas be
   * executed in Cygwin environment.
   */
  String cygwinWindowsPathToUnix(String absolutePath) throws IOException;


  /**
   * Executes a command
   *
   * @param handle
   * @param directoryToExecuteIn - a directory command should
   *                             un
   *                             in
   * @param cmd                  - command to execute
   * @param environment          - environment variables to add
   *                             to
   *                             execution environment
   * @param tailBufferSize
   * @param mergeStdoutAndStdErr - if true, will create a merged
   */
  ExecuteResult execute(final int handle, String directoryToExecuteIn, String cmd, Map environment, final TailBufferSize tailBufferSize, boolean mergeStdoutAndStdErr) throws IOException, CommandStoppedException;


  /**
   * Deletes file on a agent.
   *
   * @param stdoutFileName
   */
  void deleteFile(String stdoutFileName) throws IOException;


  /**
   * Deletes file on a agent hard.
   *
   * @param fileName
   */
  boolean deleteFileHard(String fileName) throws IOException;


  /**
   * Return agent Java system property.
   *
   * @param propertyName to return value for
   * @return agent Java system property.
   */
  String getSystemProperty(String propertyName) throws IOException;


  /**
   * Removes all files from checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  boolean emptyCheckoutDir(int buildID, final String checkoutDir) throws IOException;


  /**
   * Returns true if checkout dir is empty.
   */
  boolean checkoutDirIsEmpty(int buildID, final String checkoutDir) throws IOException;


  /**
   * Returns true if checkout dir exists
   */
  boolean checkoutDirExists(int buildID, final String checkoutDir) throws IOException;


  /**
   * Returns name of remote checkout directory.
   *
   * @return name of remote checkout directory
   */
  String getCheckoutDirName(int buildID, final String checkoutDir) throws IOException;


  /**
   * Returns true if file relative to checkout dir exists.
   */
  boolean relativeFileExists(int buildID, final String checkoutDir, String relativePath) throws IOException;


  /**
   * Explicitely requests build host to create needed build
   * directories.
   */
  void createBuildDirs(int buildID, final String checkoutDir) throws IOException;


  /**
   * Deletes file retaive to checkout dir
   */
  boolean deleteRelativeFile(int buildID, final String checkoutDir, String relativePath) throws IOException;


  /**
   * Returns true if log dir is empty.
   */
  boolean logDirIsEmpty(int buildID) throws IOException;


  /**
   * Returns true if logd dir is empty.
   */
  boolean passwordDirIsEmpty(int buildID) throws IOException;


  /**
   * Returns true if dir relative to checkout dir exists.
   */
  boolean relativeDirIsEmpty(int buildID, final String checkoutDir, String relativePath) throws IOException;


  /**
   * Removes all files from checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  boolean emptyLogDir(int buildID) throws IOException;


  /**
   * Returns true if given path, either file or dir, exists.
   */
  boolean absolutePathExists(int buildID, String absolutePath) throws IOException;


  /**
   * Removes all files from password dir.
   *
   * @return true if there are no files left after cleanup
   */
  boolean emptyPasswordDir(int buildID) throws IOException;


  /**
   * Returns true if temp dir exists
   *
   * @param relativePathInTempDirectory
   */
  boolean tempDirExists(int buildID, String relativePathInTempDirectory) throws IOException;


  String getBuildTempDirName(int buildID) throws IOException;


  /**
   * Makes a new script file name to be used to write a step
   * script.
   *
   * @param sequenceID to create a script for.
   * @return absolute path
   * @throws IOException
   */
  String makeStepScriptPath(int activeBuildID, int sequenceID) throws IOException;


  /**
   * Returns checkout directory home. All project files are
   * checked out under this directory.
   */
  String getCheckoutDirHome(int buildID, final String checkoutDir) throws IOException;


  /**
   * Returns true if the given absolute path is a file
   */
  boolean pathIsFile(int buildID, String file) throws IOException;


  /**
   * Returns true if the given absolute path is a directory
   */
  boolean pathIsDirectory(int buildID, String path) throws IOException;


  /**
   * Lists files under given path with given extension
   */
  String[] listFilesInDirectory(int buildID, String path, String extension) throws IOException;


  /**
   * Returns list of files in a remote dir for the given build.
   *
   * @param remoteDir - absolute canonical path.
   * @return List of RemoteFileDescriptor objects.
   * @see RemoteFileDescriptor
   */
  List listFiles(int buildID, String remoteDir) throws IOException;


  /**
   * Returns name part of the file presented by absolute path
   */
  String getFileName(String path) throws IOException;


  /**
   * Creates a temp file and writes given string to it.
   */
  String createTempFile(int buildID, String prefix, String suffix, String content) throws IOException;


  /**
   * Deletes a temp file.
   */
  boolean deleteTempFile(int buildID, String name) throws IOException;


  /**
   * Returns agent's environemnt path separator.
   */
  String pathSeparator() throws IOException;


  /**
   * Returns agent's environment separator  (as in
   * File.separator).
   */
  String separator() throws IOException;


  /**
   * Creates a file with given context. File should reside under
   * build's checkout dir.
   *
   * @param absoluteFile
   * @param content
   */
  void createFile(int buildID, String absoluteFile, String content) throws IOException;


  /**
   * Create directories for given path under build checkout dir.
   *
   * @param path
   */
  boolean mkdirs(int buildID, String path) throws IOException;


  /**
   * Returns remote file descriptor for a file described by the
   * given absolute path.
   */
  RemoteFileDescriptor getFileDescriptor(String absolutePath) throws IOException;


  /**
   * @return default locale.
   */
  LocaleData defaultLocaleData();


  /**
   * Deletes checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  boolean deleteCheckoutDir(int activeBuildID, final String checkoutDir) throws IOException;


  /**
   * Returns true if the given path is absolute.
   *
   * @param path to check
   * @return true if the given path is absolute.
   */
  boolean isAbsoluteFile(String path) throws IOException;


  /**
   * Fixes CRLF according to local system.
   *
   * @param stringToFix
   * @return String with fixed CRLF according to local system.
   */
  String fixCRLF(String stringToFix) throws IOException;


  /**
   * Deletes all build files.
   *
   * @return true if all directories were deleted.
   * @see LocalBuilderFiles
   */
  boolean deleteBuildFiles(final int activeBuildID, final String checkoutDir) throws IOException;


  /**
   * @return Agent's current time in milliseconds, same as
   *         {@link System#currentTimeMillis()) but for the remote
   *         agent.
   * @see System#currentTimeMillis()
   */
  long currentTimeMillis();


  /**
   * @param timeSinceMillis cut off time in milliseconds.
   * @return List of {@link RemoteFileDescriptor object} for
   *         files that were modified after the time provided by
   *         timeSinceMillis.
   */
  ModifiedFileList getModifiedFiles(final int activeBuildID, final String path, final long timeSinceMillis, final int maximumNumberOfFiles) throws IOException;


  /**
   * Deletes all content from the script dir.
   */
  boolean emptyScriptDir(final int activeBuildID) throws IOException;


  /**
   * Returns true if script directory is empty.
   */
  boolean scriptDirIsEmpty(final int activeBuildID) throws IOException;


  /**
   * @return host name
   */
  String getHostName();


  /**
   * @return true if the given path is prohibited for deletion.
   */
  boolean isProhibitedPath(final String path) throws IOException;


  /**
   * @return a new executor handle that can be used to
   *         access a remote command's environement.
   */
  int createExecutorHandle() throws IOException;


  /**
   * Requests log tail update for the given command handle.
   *
   * @param commandHandle
   * @param sinceServerTimeMs
   */
  TailUpdate getTailUpdate(final int commandHandle, final long sinceServerTimeMs);


  /**
   * Returns name of home directory for build files.
   *
   * @return Returns name of home directory for build files.
   */
  String getSystemWorkingDirName(final int activeBuildID) throws IOException;

  /**
   * Returns JVM Properties.
   *
   * @return JVM Properties.
   */
  Map getSystemProperties() throws IOException;
}
