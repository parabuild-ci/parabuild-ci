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
package org.parabuild.ci.remote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.process.TailBufferSize;
import org.parabuild.ci.remote.internal.LocalBuilderFiles;
import org.parabuild.ci.remote.services.ModifiedFileList;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;
import org.parabuild.ci.services.TailUpdate;

/**
 * Presents remote build host
 */
public interface Agent {

  /**
   * Removes all files from checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  boolean emptyCheckoutDir() throws IOException, AgentFailureException;


  /**
   * Deletes checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  boolean deleteCheckoutDir() throws IOException, AgentFailureException;


  /**
   * Returns true if checkout dir is empty.
   */
  boolean checkoutDirIsEmpty() throws IOException, AgentFailureException;


  /**
   * Returns true if checkout dir exists
   */
  boolean checkoutDirExists() throws IOException, AgentFailureException;


  /**
   * Returns true if command is available.
   *
   * @param command to check if available
   */
  boolean commandIsAvailable(String command) throws IOException, AgentFailureException;


  /**
   * Returns name of remote checkout directory.
   *
   * @return name of remote checkout directory
   * @throws IOException
   */
  String getCheckoutDirName() throws IOException, AgentFailureException;


  /**
   * Returns true if file relative to checkout dir exists.
   */
  boolean fileRelativeToCheckoutDirExists(String relativePath) throws IOException, AgentFailureException;


  /**
   * Explicitely requests build host to create needed build
   * directories.
   */
  void createBuildDirs() throws IOException, AgentFailureException;


  /**
   * Deletes file under checkout dir. Path can be relative or
   * absolute.
   */
  boolean deleteFileUnderCheckoutDir(String path) throws IOException, AgentFailureException;


  /**
   * Returns true if log dir is empty.
   */
  boolean logDirIsEmpty() throws IOException, AgentFailureException;


  /**
   * Returns true if logd dir is empty.
   */
  boolean passwordDirIsEmpty() throws IOException, AgentFailureException;


  /**
   * Removes all files from checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  boolean emptyLogDir() throws IOException, AgentFailureException;


  /**
   * Returns true if dir relative to checkout dir exists.
   */
  boolean dirRelativeToCheckoutDirIsEmpty(String relativePath) throws IOException, AgentFailureException;


  /**
   * Returns true if given path, either file or dir, exists.
   */
  boolean absolutePathExists(String absolutePath) throws IOException, AgentFailureException;


  /**
   * Removes all files from password dir.
   *
   * @return true if there are no files left after cleanup
   */
  boolean emptyPasswordDir() throws IOException, AgentFailureException;


  /**
   * Returns true if temp dir exists
   *
   * @param relativePathInTempDirectory
   */
  boolean relativeTempPathExists(String relativePathInTempDirectory) throws IOException, AgentFailureException;


  /**
   * Return true if agent host system is Windows
   */
  boolean isWindows() throws IOException, AgentFailureException;


  /**
   * Return true if agent host system is Unix
   */
  boolean isUnix() throws IOException, AgentFailureException;


  /**
   * Returns agent's system type.
   *
   * @return agent's system type.
   * @see AgentEnvironment#SYSTEM_TYPE_CYGWIN
   * @see AgentEnvironment#SYSTEM_TYPE_SUNOS
   * @see AgentEnvironment#SYSTEM_TYPE_WIN95
   * @see AgentEnvironment#SYSTEM_TYPE_WINNT
   */
  int systemType() throws IOException, AgentFailureException;


  /**
   * Executes command in Agent box.
   *
   * @param executorHandle
   * @param relativeDirectoryToExecuteIn directory relative to
   *                                     checkout path
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
  int execute(final int executorHandle, String relativeDirectoryToExecuteIn, String command, Map environment, final TailBufferSize tailBufferSize, File stdoutFile, File stderrFile, File mergedFile) throws IOException, CommandStoppedException, AgentFailureException;


  /**
   * Creates a temp file and writes given string to it.
   */
  String createTempFile(String prefix, String suffix, String content) throws IOException, AgentFailureException;


  boolean deleteTempFile(String name) throws IOException, AgentFailureException;


  /**
   * Deletes remote file.
   */
  boolean deleteTempFileHard(String tempFileName);


  /**
   * Returns name of remote checkout directory.
   *
   * @return name of remote checkout directory
   * @throws IOException
   */
  String getTempDirName() throws IOException, AgentFailureException;


  /**
   * @return this Agent's buildID
   */
  int getActiveBuildID();


  String cygwinWindowsPathToUnix(String path) throws IOException, AgentFailureException;


  /**
   * Returns checkout directory home. All project files are
   * checked out under this directory.
   */
  String getCheckoutDirHome() throws IOException, AgentFailureException;


  /**
   * Returns true if the given absolute path is a file
   */
  boolean pathIsFile(String file) throws IOException, AgentFailureException;


  /**
   * Returns true if the given absolute path is a directory
   */
  boolean pathIsDirectory(String path) throws IOException, AgentFailureException;


  /**
   * Get a file presented by the absolute path into the given
   * local file.
   *
   * @param path     - path on the agent
   * @param readInTo - local file to read the information into.
   */
  void readFile(String path, File readInTo) throws IOException, AgentFailureException;


  /**
   * Gets a directory presented by the absolute path into the
   * given local directory.
   *
   * @param path     - path on the agent
   * @param readInTo - local file to read the information into.
   */
  void getDirectory(String path, File readInTo) throws IOException, AgentFailureException;


  /**
   * Lists files under given path with given extension. Returns
   * an array of canonical path.
   *
   * @param path
   */
  String[] listFilesInDirectory(String path, String extension) throws IOException, AgentFailureException;


  /**
   * Returns name part of the file presented by absolute path
   */
  String getFileName(String absolutePath) throws IOException, AgentFailureException;


  /**
   * Return agent Java system property.
   *
   * @param propertyName to return value for
   * @return agent Java system property.
   */
  String getSystemProperty(String propertyName) throws IOException, AgentFailureException;


  /**
   * Return agent shell environment variable.
   *
   * @param varName to return value for
   * @return agent shell environment variable.
   */
  String getEnvVariable(String varName) throws IOException, AgentFailureException;


  /**
   * Returns agent's path separator.
   */
  String pathSeparator() throws IOException, AgentFailureException;


  /**
   * Creates a file with given context. File should reside under
   * build's checkout dir or agent/tomcat temp dir.
   *
   * @param absoluteFile ubsolute file path
   * @param content
   */
  void createFile(String absoluteFile, String content) throws IOException, AgentFailureException;


  /**
   * Create directories for given path under build checkout dir.
   *
   * @param absolutePath
   */
  boolean mkdirs(String absolutePath) throws IOException, AgentFailureException;


  /**
   * Returns agent's locale.
   */
  Locale defaultLocale() throws IOException, AgentFailureException;


  /**
   * Returns descriptor for a file denoted by this path.
   *
   * @param absolutePath
   * @return RemoteFileDescriptor
   * @throws IOException
   */
  RemoteFileDescriptor getFileDescriptor(String absolutePath) throws IOException, AgentFailureException;


  /**
   * Makes a script file name to be used to write a step script.
   *
   * @param sequenceID to create a script for.
   * @return absolute path
   * @throws IOException
   */
  String makeStepScriptPath(int sequenceID) throws IOException, AgentFailureException;


  /**
   * Fixes CRLF according to local system.
   *
   * @param stringToFix
   * @return String with fixed CRLF according to local system.
   */
  String fixCRLF(String stringToFix) throws IOException, AgentFailureException;


  /**
   * Deletes all build files.
   *
   * @see LocalBuilderFiles
   */
  boolean deleteBuildFiles() throws IOException, AgentFailureException;


  /**
   * Returns agent's host
   *
   * @return agent's host
   */
  AgentHost getHost();


  /**
   * @return Agent's current time in milliseconds, same as
   *         {@link System#currentTimeMillis()) but for the remote
   *         agent.
   * @see System#currentTimeMillis()
   */
  long currentTimeMillis() throws IOException, AgentFailureException;


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
  ModifiedFileList getModifiedFiles(final String path, final long timeSinceMillis, final int maximumNumberOfFiles) throws IOException, AgentFailureException;


  /**
   * Deletes a file or a directory. All exceptions
   * are ignored.
   *
   * @param path to delete.
   * @return true if a path was successfuly deleted.
   */
  boolean deleteFileHard(String path);


  /**
   * Deletes all content from the script dir.
   */
  boolean emptyScriptDir() throws IOException, AgentFailureException;


  /**
   * Returns true if script directory is empty
   */
  boolean scriptDirIsEmpty() throws IOException, AgentFailureException;


  /**
   * @return this agent host name
   */
  String getLocalHostName() throws IOException, AgentFailureException;


  /**
   * @return a new executor handle that can be used to
   *         access a remote command's environement.
   */
  int createCommandHandle() throws IOException, AgentFailureException;


  /**
   * Requests log tail update for the given command handle.
   *
   * @param commandHandle
   * @param sinceServerTimeMs
   */
  TailUpdate getTailUpdate(final int commandHandle, final long sinceServerTimeMs) throws IOException, AgentFailureException;


  /**
   * Returns name of home directory for build files.
   *
   * @return Returns name of home directory for build files.
   * @throws IOException
   */
  String getSystemWorkingDirName() throws IOException, AgentFailureException;


  /**
   * @return file separator
   */
  String separator() throws IOException, AgentFailureException;
}
