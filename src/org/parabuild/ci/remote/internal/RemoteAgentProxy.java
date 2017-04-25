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

import com.caucho.hessian.client.HessianRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.process.TailBufferSize;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.RemoteUtils;
import org.parabuild.ci.remote.services.ModifiedFileList;
import org.parabuild.ci.remote.services.RemoteBuilderWebService;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;
import org.parabuild.ci.services.TailUpdate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Remote builder proxy.
 */
public final class RemoteAgentProxy implements Agent {

  private static final Log LOG = LogFactory.getLog(RemoteAgentProxy.class); // NOPMD
  private final WebServiceLocator webServiceLocator;
  private final String checkoutDir;
  private final RemoteAgentEnvironmentProxy builderEnvironmentProxy;
  private final int activeBuildID;


  /**
   * Constructor. Creates RemoteAgentProxy using given web
   * service locator.
   */
  public RemoteAgentProxy(final int activeBuildID, final WebServiceLocator webServiceLocator,
                          final String checkoutDir) {
    this.activeBuildID = activeBuildID;
    this.webServiceLocator = webServiceLocator;
    this.checkoutDir = checkoutDir;
    this.builderEnvironmentProxy = new RemoteAgentEnvironmentProxy(webServiceLocator);
    // paranoid build id validation
    if (ConfigurationManager.validateActiveID) {
      ConfigurationManager.getInstance().validateIsActiveBuildID(activeBuildID);
    }
  }


  /**
   * Constructor. Creates RemoteAgentProxy using given build
   * host. WebServiceLocator will be created internally.
   *
   * @param activeBuildID active build ID
   * @param agentHost     agent host, not necessarily current
   *                      active build host.
   */
  public RemoteAgentProxy(final int activeBuildID, final AgentHost agentHost, final String checkoutDir) {
    this(activeBuildID, new WebServiceLocator(agentHost), checkoutDir);
  }


  /**
   * Removes all files from checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  public boolean emptyCheckoutDir() throws IOException, AgentFailureException {
    try {
      return getWebService().emptyCheckoutDir(activeBuildID, checkoutDir);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Deletes checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  public boolean deleteCheckoutDir() throws IOException, AgentFailureException {
    try {
      return getWebService().deleteCheckoutDir(activeBuildID, checkoutDir);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns true if checkout dir is empty.
   */
  public boolean checkoutDirIsEmpty() throws IOException, AgentFailureException {
    try {
      return getWebService().checkoutDirIsEmpty(activeBuildID, checkoutDir);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns true if checkout dir exists
   */
  public boolean checkoutDirExists() throws IOException, AgentFailureException {
    try {
      return getWebService().checkoutDirExists(activeBuildID, checkoutDir);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns true if command is available.
   *
   * @param command to check if available
   */
  public boolean commandIsAvailable(final String command) throws IOException, AgentFailureException {
    return builderEnvironmentProxy.commandIsAvailable(command);
  }


  /**
   * Returns name of remote checkout directory.
   *
   * @return name of remote checkout directory
   * @throws IOException
   */
  public String getCheckoutDirName() throws IOException, AgentFailureException {
    try {
      return getWebService().getCheckoutDirName(activeBuildID, checkoutDir);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns true if file relative to checkout dir exists.
   */
  public boolean fileRelativeToCheckoutDirExists(final String relativePath) throws IOException, AgentFailureException {
    try {
      return getWebService().relativeFileExists(activeBuildID, checkoutDir, relativePath);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Explicitly requests build host to create needed build
   * directories.
   */
  public void createBuildDirs() throws IOException, AgentFailureException {
    try {
      getWebService().createBuildDirs(activeBuildID, checkoutDir);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Deletes file relative to checkout dir
   */
  public boolean deleteFileUnderCheckoutDir(final String relativePath) throws IOException, AgentFailureException {
    try {
      return getWebService().deleteRelativeFile(activeBuildID, checkoutDir, relativePath);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns true if log dir is empty.
   */
  public boolean logDirIsEmpty() throws IOException, AgentFailureException {
    try {
      return getWebService().logDirIsEmpty(activeBuildID);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns true if password dir is empty.
   */
  public boolean passwordDirIsEmpty() throws IOException, AgentFailureException {
    try {
      return getWebService().passwordDirIsEmpty(activeBuildID);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Removes all files from checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  public boolean emptyLogDir() throws IOException, AgentFailureException {
    try {
      return getWebService().emptyLogDir(activeBuildID);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns true if dir relative to checkout dir exists.
   */
  public boolean dirRelativeToCheckoutDirIsEmpty(final String relativePath) throws IOException, AgentFailureException {
    try {
      return getWebService().relativeDirIsEmpty(activeBuildID, checkoutDir, relativePath);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns true if given path, either file or dir, exists.
   */
  public boolean absolutePathExists(final String absolutePath) throws IOException, AgentFailureException {
    try {
      return getWebService().absolutePathExists(activeBuildID, absolutePath);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Removes all files from password dir.
   *
   * @return true if there are no files left after cleanup
   */
  public boolean emptyPasswordDir() throws IOException, AgentFailureException {
    try {
      return getWebService().emptyPasswordDir(activeBuildID);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns true if temp dir exists
   *
   * @param relativePathInTempDirectory
   */
  public boolean relativeTempPathExists(final String relativePathInTempDirectory) throws IOException, AgentFailureException {
    try {
      return getWebService().tempDirExists(activeBuildID, relativePathInTempDirectory);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Return true if agent host system is Windows
   */
  public boolean isWindows() throws IOException, AgentFailureException {
    return this.builderEnvironmentProxy.isWindows();
  }


  /**
   * Return true if agent host system is Unix
   */
  public boolean isUnix() throws IOException, AgentFailureException {
    return this.builderEnvironmentProxy.isUnix();
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
  public int systemType() throws IOException, AgentFailureException {
    return this.builderEnvironmentProxy.systemType();
  }


  /**
   * Executes command in Agent box.
   *
   * @param handle
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
  public int execute(final int handle, final String relativeDirectoryToExecuteIn, final String command, final Map environment, final TailBufferSize tailBufferSize, final File stdoutFile, final File stderrFile, final File mergedFile) throws IOException, CommandStoppedException, AgentFailureException {
    // REVIEWME: simeshev@parabuilci.org -> relativeDirectoryToExecuteIn?
    return this.builderEnvironmentProxy.execute(handle, relativeDirectoryToExecuteIn, command, environment, tailBufferSize, stdoutFile, stderrFile, mergedFile);
  }


  /**
   * Creates a temp file and writes given string to it.
   */
  public String createTempFile(final String prefix, final String suffix, final String content) throws IOException, AgentFailureException {
    try {
      return getWebService().createTempFile(activeBuildID, prefix, suffix, content);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Deletes a temp file.
   */
  public boolean deleteTempFile(final String name) throws IOException, AgentFailureException {
    try {
      return getWebService().deleteTempFile(activeBuildID, name);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  public boolean deleteTempFileHard(final String tempFileName) {
    try {
      return getWebService().deleteFileHard(tempFileName);
    } catch (Exception e) {
      final ErrorManager em = ErrorManagerFactory.getErrorManager();
      final Error error = new Error("Could not delete temporary file");
      error.setDescription("Error while deleting temporary \"" + tempFileName + "\": " + StringUtils.toString(e));
      error.setHostName(webServiceLocator.agentHostName());
      error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
      error.setDetails(e);
      em.reportSystemError(error);
      final Throwable th = new Throwable("Stack trace at call");
      //noinspection ControlFlowStatementWithoutBraces
      if (LOG.isDebugEnabled()) LOG.debug(th);
      return false;
    }
  }


  /**
   * Returns name of remote checkout directory.
   *
   * @return name of remote checkout directory
   * @throws IOException
   */
  public String getTempDirName() throws IOException, AgentFailureException {
    try {
      return getWebService().getBuildTempDirName(activeBuildID);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * @return this Agent's buildID
   */
  public int getActiveBuildID() {
    return activeBuildID;
  }


  public String cygwinWindowsPathToUnix(final String path) throws IOException, AgentFailureException {
    return this.builderEnvironmentProxy.cygwinWindowsPathToUnix(path);
  }


  /**
   * Makes a script file name to be used to write a step
   * script.
   *
   * @param sequenceID to create a script for.
   * @return absolute path
   * @throws IOException
   */
  public String makeStepScriptPath(final int sequenceID) throws IOException, AgentFailureException {
    try {
      return getWebService().makeStepScriptPath(activeBuildID, sequenceID);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Fixes CRLF according to local system.
   *
   * @param stringToFix
   * @return String with fixed CRLF according to local system.
   */
  public String fixCRLF(final String stringToFix) throws IOException, AgentFailureException {
    try {
      return getWebService().fixCRLF(stringToFix);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  public boolean deleteBuildFiles() throws IOException, AgentFailureException {
    try {
      return getWebService().deleteBuildFiles(activeBuildID, checkoutDir);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns checkout directory home. All project files are
   * checked out under this directory.
   */
  public String getCheckoutDirHome() throws IOException, AgentFailureException {
    try {
      return getWebService().getCheckoutDirHome(activeBuildID, checkoutDir);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns true if the given absolute path is a file
   */
  public boolean pathIsFile(final String file) throws IOException, AgentFailureException {
    try {
      return getWebService().pathIsFile(activeBuildID, file);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns true if the given absolute path is a directory
   */
  public boolean pathIsDirectory(final String path) throws IOException, AgentFailureException {
    try {
      return getWebService().pathIsDirectory(activeBuildID, path);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Gets file or a directory presented by the absolute path
   * into the given local file.
   *
   * @param path
   * @param readInTo
   */
  public void readFile(final String path, final File readInTo) throws IOException, AgentFailureException {
    try {
      final RemoteFileGetter fileGetter = new RemoteFileGetter(webServiceLocator);
      fileGetter.copy(path, readInTo);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Gets a directory presented by the absolute path into the
   * given local directory.
   *
   * @param remoteDir - path on the agent
   * @param destDir   - local dir to read the information into.
   */
  public void getDirectory(final String remoteDir, final File destDir) throws IOException {
    //noinspection ControlFlowStatementWithoutBraces
    if (!destDir.isDirectory())
      throw new IOException("Can not copy directory \"" + remoteDir + "\" to file (" + destDir + ')');

    final RemoteBuilderWebService webService = getWebService();

    // validate source dir exists
    if (!webService.absolutePathExists(activeBuildID, remoteDir)) {
      throw new FileNotFoundException("Remote source directory \"" + remoteDir + '\"');
    }

    final int prefixLength = webService.getFileDescriptor(remoteDir).getCanonicalPath().length() + 1;
//    if (LOG.isDebugEnabled()) LOG.debug("remoteDir: " + remoteDir);
//    if (LOG.isDebugEnabled()) LOG.debug("canonicalPath: " + webService.getFileDescriptor(remoteDir).getCanonicalPath());

//    if (LOG.isDebugEnabled()) LOG.debug("prefixLength: " + prefixLength);

    // preExecute file getter
    final RemoteFileGetter fileGetter = new RemoteFileGetter(webServiceLocator);

    // traverse list of descriptors of remote files.
    final List remoteFileList = webService.listFiles(activeBuildID, remoteDir);
    final char remoteSeparator = webService.separator().charAt(0);
    final char localSeparator = File.separator.charAt(0);
    // REVIEWME: what about standard filter for dirs like "CVS" etc?
    for (final Iterator i = remoteFileList.iterator(); i.hasNext();) {
      final RemoteFileDescriptor remoteFileDescr = (RemoteFileDescriptor) i.next();
      final String remotePath = remoteFileDescr.getCanonicalPath();
//      if (LOG.isDebugEnabled()) LOG.debug("remotePath: " + remotePath);
      // get postfix path
      if (remotePath.length() <= prefixLength) {
        LOG.warn("Path is too short: " + remotePath);
        continue;
      }
      final String postfixPath = remotePath.substring(prefixLength).replace(remoteSeparator, localSeparator);
//      if (log.isDebugEnabled()) log.debug("postfixPath: " + postfixPath);
      // copy
      final File result = new File(destDir, postfixPath);
      if (remoteFileDescr.isDirectory()) {
        result.mkdirs();
      } else if (remoteFileDescr.isFile()) {
        fileGetter.copy(remotePath, result);
      }
    }
  }


  /**
   * Returns descriptor for a file denoted by this path.
   *
   * @param absolutePath
   * @return RemoteFileDescriptor
   * @throws IOException
   */
  public RemoteFileDescriptor getFileDescriptor(final String absolutePath) throws IOException {
    return getWebService().getFileDescriptor(absolutePath);
  }


  /**
   * Lists files under given path with given extension
   *
   * @param path
   */
  public String[] listFilesInDirectory(final String path, final String extension) throws IOException, AgentFailureException {
    try {
      return getWebService().listFilesInDirectory(activeBuildID, path, extension);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  private RemoteBuilderWebService getWebService() throws IOException {
    return webServiceLocator.getWebService();
  }


  /**
   * Returns name part of the file presented by absolute path
   */
  public String getFileName(final String path) throws IOException, AgentFailureException {
    try {
      return getWebService().getFileName(path);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Return agent Java system property.
   *
   * @param propertyName to return value for
   * @return agent Java system property.
   */
  public String getSystemProperty(final String propertyName) throws IOException, AgentFailureException {
    return this.builderEnvironmentProxy.getSystemProperty(propertyName);
  }


  /**
   * Return agent shell environment variable.
   *
   * @param varName to return value for
   * @return agent shell environment variable.
   */
  public String getEnvVariable(final String varName) throws IOException, AgentFailureException {
    return this.builderEnvironmentProxy.getEnvVariable(varName);
  }


  /**
   * Returns agent's path separator.
   */
  public String pathSeparator() throws IOException, AgentFailureException {
    return this.builderEnvironmentProxy.pathSeparator();
  }


  /**
   * Creates a file with given context. File should reside under
   * build's checkout dir.
   *
   * @param absoluteFile
   * @param content
   */
  public void createFile(final String absoluteFile, final String content) throws IOException, AgentFailureException {
    try {
      getWebService().createFile(activeBuildID, absoluteFile, content);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Create directories for given path under build checkout dir.
   *
   * @param absolutePath
   */
  public boolean mkdirs(final String absolutePath) throws IOException, AgentFailureException {
    try {
      return getWebService().mkdirs(activeBuildID, absolutePath);
    } catch (HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  public Locale defaultLocale() throws IOException, AgentFailureException {
    return this.builderEnvironmentProxy.defaultLocale();
  }


  /**
   * Returns agent's host
   *
   * @return agent's host
   */
  public AgentHost getHost() {
    return webServiceLocator.getAgentHost();
  }


  /**
   * @return Agent's current time in milliseconds, same as
   *         {@link System#currentTimeMillis()) but for the remote
   *         agent.
   * @see System#currentTimeMillis()
   */
  public long currentTimeMillis() throws IOException {
    return getWebService().currentTimeMillis();
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
    return getWebService().getModifiedFiles(activeBuildID, path, timeSinceMillis, maximumNumberOfFiles);
  }


  public boolean deleteFileHard(final String path) {
    try {
      return getWebService().deleteFileHard(path);
    } catch (Exception e) {
      final ErrorManager em = ErrorManagerFactory.getErrorManager();
      final Error error = new Error("Could not delete path " + path);
      error.setDescription("Error while deleting temporary \"" + path + "\": " + StringUtils.toString(e));
      error.setHostName(webServiceLocator.agentHostName());
      error.setDetails(e);
      error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
      em.reportSystemError(error);
      final Throwable th = new Throwable("Stack trace at call");
      //noinspection ControlFlowStatementWithoutBraces
      if (LOG.isDebugEnabled()) LOG.debug(th);
      return false;
    }
  }


  /**
   * Deletes all content from the script dir.
   */
  public boolean emptyScriptDir() throws IOException {
    return getWebService().emptyScriptDir(activeBuildID);
  }


  /**
   * Returns true if script directory is empty
   */
  public boolean scriptDirIsEmpty() throws IOException {
    return getWebService().scriptDirIsEmpty(activeBuildID);
  }


  public String getLocalHostName() throws IOException {
    return getWebService().getHostName();
  }


  public int createCommandHandle() throws IOException {
    return getWebService().createExecutorHandle();
  }


  /**
   * Requests log tail update for the given command handle.
   *
   * @param commandHandle
   * @param sinceServerTimeMs
   */
  public TailUpdate getTailUpdate(final int commandHandle, final long sinceServerTimeMs) throws IOException {
    return getWebService().getTailUpdate(commandHandle, sinceServerTimeMs);
  }


  /**
   * Returns name of home directory for build files.
   *
   * @return Returns name of home directory for build files.
   * @throws IOException
   */
  public String getSystemWorkingDirName() throws IOException {
    return getWebService().getSystemWorkingDirName(activeBuildID);
  }


  /**
   * @return file separator
   */
  public String separator() throws IOException {
    return getWebService().separator();
  }


  public String toString() {
    return "RemoteAgentProxy{" +
            "activeBuildID=" + activeBuildID +
            ", checkoutDir='" + checkoutDir + '\'' +
            ", builderEnvironmentProxy=" + builderEnvironmentProxy +
            ", webServiceLocator=" + webServiceLocator +
            '}';
  }
}
