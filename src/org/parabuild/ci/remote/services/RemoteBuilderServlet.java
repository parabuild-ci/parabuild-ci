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

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.server.HessianServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationFile;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.process.TailBufferSize;
import org.parabuild.ci.realm.RealmConstants;
import org.parabuild.ci.remote.internal.LocalAgent;
import org.parabuild.ci.remote.internal.LocalAgentEnvironment;
import org.parabuild.ci.remote.internal.LocaleData;
import org.parabuild.ci.remote.internal.Tail;
import org.parabuild.ci.services.TailUpdate;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.DirectoryTraverserCallback;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.ServletUtils;
import org.parabuild.ci.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Agent's Hessian RMI front
 */
public final class RemoteBuilderServlet extends HessianServlet implements RemoteBuilderWebService {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(RemoteBuilderServlet.class); //NOPMD
  private static final long serialVersionUID = -1426274901331328124L; // NOPMD

  private static final String LOOPBACK_ADDRESS = "127.0.0.1";
  private static final String BUILD_MANAGER_ADDRESS = ConfigurationFile.getInstance().getBuildManagerAddress();
  private static final boolean NONLOCAL_ADDRESS_ENABLED = ConfigurationFile.getInstance().isNonLocalAddressEnabled();


  /**
   * Execute a request.  The path-info of the request selects the
   * bean. Once the bean's selected, it will be applied.
   */
  public void service(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
    final HttpServletRequest httpServletRequest = (HttpServletRequest) request;

    // first, validate security
    final SecurityValidationResult securityValidationResult = validateRequestSecurity(httpServletRequest);
    if (!securityValidationResult.isSuccessful()) {
      final HttpServletResponse httpServletResponce = (HttpServletResponse) response;
      httpServletResponce.setStatus(HttpServletResponse.SC_FORBIDDEN);
      PrintWriter writer = null;
      try {
        writer = httpServletResponce.getWriter();
        writer.write(securityValidationResult.getMessage());
        writer.flush();
      } finally {
        IoUtils.closeHard(writer);
        IoUtils.closeHard(httpServletRequest.getInputStream());
      }
      return;
    }

    // check if the runner started under root
    if (ConfigurationManager.BLOCK_ROOT_USER || ConfigurationManager.BLOCK_ADMIN_USER) {
      throw new IOException("Remote agent was started under root user. This is not allowed. Please restart it under non-root user.");
    }

    // process
    final String isCustomProtocol = httpServletRequest.getHeader(WebServiceConstants.REQUEST_HEADER_CUSTOM_PROTOCOL);
    if (isCustomProtocol != null && isCustomProtocol.equals(Boolean.toString(true))) {
      serviceCustomCall(request, response);
    } else {
      super.service(request, response);
    }
  }


  /**
   * Validates that a reuests is a valid and secure reqest.
   * Checks if user is in role and if caller IP address is the
   * one this agent is configured for.
   *
   * @return true if request is valid.
   */
  private static SecurityValidationResult validateRequestSecurity(final HttpServletRequest httpServletRequest) {

    // validate IP

    // get configured build manager address
    String assignedBuildManagerAddress = BUILD_MANAGER_ADDRESS;
    if (StringUtils.isBlank(assignedBuildManagerAddress)) {
      assignedBuildManagerAddress = LOOPBACK_ADDRESS;
    }

    // prevalidate
    final String remoteAddr = httpServletRequest.getRemoteAddr();
    if (!NONLOCAL_ADDRESS_ENABLED) {
      if (!ServletUtils.requestIsLocal(remoteAddr)) return SecurityValidationResult.FAILURE_NONLOCAL_MANAGER;
    }

    // validate address
    if (!remoteAddr.equals(assignedBuildManagerAddress)) {
      // REVIEWME: if remote address doesn't match configured build
      // manager address, try to find if we are called from the same
      // host we are running.
      // REVIEWME: multihome machine handling
      return SecurityValidationResult.FAILURE_NON_REGISTERED_MANAGER;
    }

    // validate user
    final String remoteUser = httpServletRequest.getRemoteUser();
    if (StringUtils.isBlank(remoteUser)) return SecurityValidationResult.FAILURE_USER_NOT_SET;
    if (!httpServletRequest.isUserInRole(RealmConstants.PARABUILD_MANAGER_ROLE))
      return SecurityValidationResult.FAILURE_USER_NOT_AUTHORISED;
    return SecurityValidationResult.SUCCESS;
  }


  private static void serviceCustomCall(final ServletRequest request, final ServletResponse response) throws IOException {
    // manual handling
    ServletInputStream inputStream = null;
    ServletOutputStream outputStream = null;
    InputStream fileInputStream = null;
    HessianOutput ho = null;
    try {
      inputStream = request.getInputStream();
      outputStream = response.getOutputStream();
      final HessianInput hin = new HessianInput(inputStream);
      hin.startReply();
      // check method
      final String method = hin.readMethod();
      if (method.equals(WebServiceConstants.METHOD_GET_FILE)) {
        // get file name and IS
        final String fileName = hin.readString();
        ho = new HessianOutput(outputStream);

        // transfer
        ho.startReply();
        final File fileToRead = new File(fileName);
        if (!fileToRead.exists()) throw new FileNotFoundException("File not found: \"" + fileName + '\"');
        fileInputStream = new BufferedInputStream(new FileInputStream(fileToRead), 4098);
        // write file to hessian
        ho.writeObject(fileInputStream);
        ho.completeReply();
      } else {
        throw new IllegalArgumentException("Unknown method: " + method);
      }
      hin.completeReply();
    } catch (final Throwable throwable) {  // NOPMD - because startReply, thanks to caucho, declares Throwable
      if (ho != null) {
        ho.writeFault("", StringUtils.toString(throwable), throwable);
      }
    } finally {
      IoUtils.closeHard(inputStream);
      IoUtils.closeHard(outputStream);
      IoUtils.closeHard(fileInputStream);
    }
  }


  /**
   * @return agent version hash code
   */
  public int builderVersionHashCode() {
    // System.out.println("DEBUG: builderVersionHashCode ");
    return getLocalBuilderEnvironmnent().builderVersionHashCode();
  }


  /**
   * @return agent version hash code
   */
  public String builderVersionAsString() {
    // System.out.println("DEBUG: builderVersionAsString ");
    return getLocalBuilderEnvironmnent().builderVersionAsString();
  }


  /**
   * Return value of system environment variable.
   */
  public String getEnvVariable(final String variableName) {
    // System.out.println("DEBUG: getEnvVariable ");
    return getLocalBuilderEnvironmnent().getEnvVariable(variableName);
  }


  /**
   * @return Map containing shell environment variables.
   */
  public Map getEnv() {
    // System.out.println("DEBUG: getStartupEnv ");
    return getLocalBuilderEnvironmnent().getEnv();
  }


  /**
   * Returns system type
   */
  public byte systemType() {
    // System.out.println("DEBUG: systemType ");
    return getLocalBuilderEnvironmnent().systemType();
  }


  /**
   * Returns true if given executable is available.
   *
   * @param command
   * @return true if given executable is available.
   */
  public boolean commandIsAvailable(final String command) {
    // System.out.println("DEBUG: commandIsAvailable ");
    final LocalAgentEnvironment localBuilderEnvironment = getLocalBuilderEnvironmnent();
    return localBuilderEnvironment.commandIsAvailable(command);
  }


  /**
   * Retuns true if agent runs under Windows
   */
  public boolean isWindows() {
    // System.out.println("DEBUG: isWindows ");
    return getLocalBuilderEnvironmnent().isWindows();
  }


  /**
   * Retuns true if agent runs under Unix
   */
  public boolean isUnix() {
    // System.out.println("DEBUG: isUnix ");
    return getLocalBuilderEnvironmnent().isUnix();
  }


  /**
   * Converts Windows path to Cygwin path. This method mas be
   * executed in Cygwin environment.
   */
  public String cygwinWindowsPathToUnix(final String absolutePath) throws IOException {
    // System.out.println("DEBUG: cygwinWindowsPathToUnix ");
    return getLocalBuilderEnvironmnent().cygwinWindowsPathToUnix(absolutePath);
  }


  /**
   * Deletes file on a agent.
   *
   * @param fileName
   */
  public void deleteFile(final String fileName) throws IOException {
    // System.out.println("DEBUG: deleteFile ");
    if (!deleteFileHard(fileName)) {
      throw new IOException("Could not delete file \"" + fileName + '\"');
    }
  }


  /**
   * Deletes file on a agent hard.
   *
   * @param fileName
   */
  public boolean deleteFileHard(final String fileName) throws IOException {
    if (StringUtils.isBlank(fileName)) return true;
    // System.out.println("DEBUG: deleteFileHard ");
    // security check
    validatePathIsAllowed(fileName);
    return IoUtils.deleteFileHard(new File(fileName));
  }


  public String getSystemProperty(final String propertyName) {
    // System.out.println("DEBUG: getSystemProperty ");
    return System.getProperty(propertyName);
  }


  /**
   * Executes a command
   *
   * @param directoryToExecuteIn - a directory command should run
   *                             in
   * @param cmd                  - command to execute
   * @param environment          - environment variables to add to
   *                             execution environment
   * @param mergeStdoutAndStdErr - if true, will create a merged
   *                             output for stdout and stderr
   */
  public ExecuteResult execute(final int handle, final String directoryToExecuteIn,
                               final String cmd, final Map environment, final TailBufferSize tailBufferSize,
                               final boolean mergeStdoutAndStdErr) throws IOException, CommandStoppedException {

    File stdoutFile = null;
    File stderrFile = null;
    File mergedFile = null;
    try {

      // create stdout
      stdoutFile = IoUtils.createTempFile(".auto", ".sou");
      final String stdoutFileName = stdoutFile.getCanonicalPath();

      // create stderr
      stderrFile = IoUtils.createTempFile(".auto", ".soe");
      final String stderrFileName = stderrFile.getCanonicalPath();

      // create merged
      String mergedFileName = null;
      if (mergeStdoutAndStdErr) {
        mergedFile = IoUtils.createTempFile(".auto", ".mrg");
        mergedFileName = mergedFile.getCanonicalPath();
      }

      // execute
      final LocalAgentEnvironment localBuilderEnvironment = getLocalBuilderEnvironmnent();
      final int resultCode = localBuilderEnvironment.execute(handle, directoryToExecuteIn, cmd, environment, tailBufferSize, stdoutFile, stderrFile, mergedFile);

      // return result
      return new ExecuteResult(resultCode, stdoutFileName, stderrFileName, mergedFileName);
    } catch (final IOException e) {
      // cleanup if there was a error
      IoUtils.deleteFileHard(stdoutFile);
      IoUtils.deleteFileHard(stderrFile);
      IoUtils.deleteFileHard(mergedFile);
      throw e;
    }
  }


  /**
   * Returns true if given path, either file or dir, exists.
   */
  public boolean absolutePathExists(final int buildID, final String absolutePath) throws IOException {
    // System.out.println("DEBUG: absolutePathExists ");
    return getLocalBuilder(buildID, null).absolutePathExists(absolutePath);
  }


  /**
   * Returns true if checkout dir exists
   */
  public boolean checkoutDirExists(final int buildID, final String checkoutDir) throws IOException {
    // System.out.println("DEBUG: checkoutDirExists ");
    return getLocalBuilder(buildID, checkoutDir).checkoutDirExists();
  }


  /**
   * Returns true if checkout dir is empty.
   */
  public boolean checkoutDirIsEmpty(final int buildID, final String checkoutDir) throws IOException {
    // System.out.println("DEBUG: checkoutDirIsEmpty ");
    return getLocalBuilder(buildID, checkoutDir).checkoutDirIsEmpty();
  }


  /**
   * Explicitely requests build host to create needed build
   * directories.
   */
  public void createBuildDirs(final int buildID, final String checkoutDir) throws IOException {
    // System.out.println("DEBUG: createBuildDirs ");
    getLocalBuilder(buildID, checkoutDir).createBuildDirs();
  }


  /**
   * Makes a new script file name to be used to write a step
   * script.
   *
   * @param sequenceID to create a script for.
   * @return absolute path
   * @throws IOException
   */
  public String makeStepScriptPath(final int activeBuildID, final int sequenceID) throws IOException {
    return getLocalBuilder(activeBuildID, null).makeStepScriptPath(sequenceID);
  }


  /**
   * Deletes file retaive to checkout dir
   */
  public boolean deleteRelativeFile(final int buildID, final String checkoutDir, final String relativePath) throws IOException {
    // System.out.println("DEBUG: deleteRelativeFile ");
    return getLocalBuilder(buildID, checkoutDir).deleteFileUnderCheckoutDir(relativePath);
  }


  /**
   * Removes all files from checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  public boolean emptyCheckoutDir(final int buildID, final String checkoutDir) throws IOException {
    // System.out.println("DEBUG: emptyCheckoutDir ");
    return getLocalBuilder(buildID, checkoutDir).emptyCheckoutDir();
  }


  /**
   * Removes all files from checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  public boolean emptyLogDir(final int buildID) throws IOException {
    // System.out.println("DEBUG: emptyLogDir ");
    return getLocalBuilder(buildID, null).emptyLogDir();
  }


  /**
   * Removes all files from password dir.
   *
   * @return true if there are no files left after cleanup
   */
  public boolean emptyPasswordDir(final int buildID) throws IOException {
    // System.out.println("DEBUG: emptyPasswordDir ");
    return getLocalBuilder(buildID, null).emptyPasswordDir();
  }


  /**
   * Returns checkout directory home. All project files are
   * checked out under this directory.
   */
  public String getCheckoutDirHome(final int buildID, final String checkoutDir) throws IOException {
    // System.out.println("DEBUG: getCheckoutDirHome ");
    return getLocalBuilder(buildID, checkoutDir).getCheckoutDirHome();
  }


  /**
   * Returns name of remote checkout directory.
   *
   * @return name of remote checkout directory
   */
  public String getCheckoutDirName(final int buildID, final String checkoutDir) throws IOException {
    return getLocalBuilder(buildID, checkoutDir).getCheckoutDirName();
  }


  /**
   * Returns name part of the file presented by absolute path
   */
  public String getFileName(final String path) {
    // System.out.println("DEBUG: getFileName ");
    return new File(path).getName();
  }


  /**
   * Creates a temp file and writes given string to it.
   */
  public String createTempFile(final int buildID, final String prefix, final String suffix, final String content) throws IOException {
    // System.out.println("DEBUG: createTempFile ");
    return getLocalBuilder(buildID, null).createTempFile(prefix, suffix, content);
  }


  /**
   * Deletes a temp file.
   */
  public boolean deleteTempFile(final int buildID, final String name) throws IOException {
    // System.out.println("DEBUG: deleteTempFile ");
    return getLocalBuilder(buildID, null).deleteTempFile(name);
  }


  public String pathSeparator() {
    // System.out.println("DEBUG: pathSeparator ");
    return File.pathSeparator;
  }


  /**
   * Returns agent's environment separator  (as in
   * File.separator).
   */
  public String separator() {
    // System.out.println("DEBUG: separator ");
    return File.separator;
  }


  /**
   * Creates a file with given content. File should reside under
   * build's checkout dir.
   *
   * @param absoluteFile
   * @param content
   */
  public void createFile(final int buildID, final String absoluteFile, final String content) throws IOException {
    // System.out.println("DEBUG: createFile ");
    getLocalBuilder(buildID, null).createFile(absoluteFile, content);
  }


  /**
   * Create directories for given path under build checkout dir.
   *
   * @param path
   */
  public boolean mkdirs(final int buildID, final String path) throws IOException {
    // System.out.println("DEBUG: mkdirs ");
    validatePathIsAllowed(path);
    return new File(path).mkdirs();
  }


  public RemoteFileDescriptor getFileDescriptor(final String absolutePath) throws IOException {
    return new RemoteFileDescriptor(new File(absolutePath).getCanonicalFile());
  }


  /**
   * @return default locale.
   */
  public LocaleData defaultLocaleData() {
    final Locale aDefault = Locale.getDefault();
    return new LocaleData(aDefault.getLanguage(), aDefault.getCountry(), aDefault.getVariant());
  }


  /**
   * Deletes checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  public boolean deleteCheckoutDir(final int activeBuildID, final String checkoutDir) throws IOException {
    return getLocalBuilder(activeBuildID, checkoutDir).deleteCheckoutDir();
  }


  public boolean isAbsoluteFile(final String path) {
    return getLocalBuilderEnvironmnent().isAbsoluteFile(path);
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


  public boolean deleteBuildFiles(final int activeBuildID, final String checkoutDir) throws IOException {
    return getLocalBuilder(activeBuildID, checkoutDir).deleteBuildFiles();
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


  public ModifiedFileList getModifiedFiles(final int buildID, final String path, final long timeSinceMillis, final int maximumNumberOfFiles) throws IOException {
    // System.out.println("DEBUG: getBuildTempDirName ");
    final LocalAgent localBuilder = getLocalBuilder(buildID, null);
    return localBuilder.getModifiedFiles(path, timeSinceMillis, maximumNumberOfFiles);
  }


  public String getBuildTempDirName(final int buildID) throws IOException {
    // System.out.println("DEBUG: getBuildTempDirName ");
    return getLocalBuilder(buildID, null).getTempDirName();
  }


  /**
   * Lists files under given path with given extension
   */
  public String[] listFilesInDirectory(final int buildID, final String path, final String extension) throws IOException {
    // System.out.println("DEBUG: listFiles ");
    return getLocalBuilder(buildID, null).listFilesInDirectory(path, extension);
  }


  /**
   * Returns list of files in a remote dir for the given build.
   *
   * @param dir - absolute canonical path.
   * @return List of RemoteFileDescriptor objects.
   * @see RemoteFileDescriptor
   */
  public List listFiles(final int buildID, final String dir) throws IOException {
    // REVIEWME: simeshev@parabuilci.org -> dir should be under secured location
    final LinkedList result = new LinkedList();
    IoUtils.traverseDir(new File(dir), new DirectoryTraverserCallback() {
      public boolean callback(final File file) throws IOException {
        result.add(new RemoteFileDescriptor(file));
        return true;
      }
    });
    return result;
  }


  /**
   * Returns true if log dir is empty.
   */
  public boolean logDirIsEmpty(final int buildID) throws IOException {
    // System.out.println("DEBUG: logDirIsEmpty ");
    return getLocalBuilder(buildID, null).logDirIsEmpty();
  }


  /**
   * Returns true if logd dir is empty.
   */
  public boolean passwordDirIsEmpty(final int buildID) throws IOException {
    // System.out.println("DEBUG: passwordDirIsEmpty ");
    return getLocalBuilder(buildID, null).passwordDirIsEmpty();
  }


  /**
   * Returns true if the given absolute path is a directory
   */
  public boolean pathIsDirectory(final int buildID, final String path) throws IOException {
    // System.out.println("DEBUG: pathIsDirectory ");
    return getLocalBuilder(buildID, null).pathIsDirectory(path);
  }


  /**
   * Returns true if the given absolute path is a file
   */
  public boolean pathIsFile(final int buildID, final String file) throws IOException {
    // System.out.println("DEBUG: pathIsFile ");
    return getLocalBuilder(buildID, null).pathIsFile(file);
  }


  /**
   * Returns true if dir relative to checkout dir exists.
   */
  public boolean relativeDirIsEmpty(final int buildID, final String checkoutDir, final String relativePath) throws IOException {
    // System.out.println("DEBUG: relativeDirIsEmpty ");
    return getLocalBuilder(buildID, checkoutDir).dirRelativeToCheckoutDirIsEmpty(relativePath);
  }


  /**
   * Returns true if file relative to checkout dir exists.
   */
  public boolean relativeFileExists(final int buildID, final String checkoutDir, final String relativePath) throws IOException {
    // System.out.println("DEBUG: relativeFileExists ");
    return getLocalBuilder(buildID, checkoutDir).fileRelativeToCheckoutDirExists(relativePath);
  }


  /**
   * Returns true if temp dir exists
   *
   * @param relativePathInTempDirectory
   */
  public boolean tempDirExists(final int buildID, final String relativePathInTempDirectory) throws IOException {
    // System.out.println("DEBUG: tempDirExists ");
    return getLocalBuilder(buildID, null).relativeTempPathExists(relativePathInTempDirectory);
  }


  /**
   * Deletes all content from the script dir.
   */
  public boolean emptyScriptDir(final int activeBuildID) throws IOException {
    return getLocalBuilder(activeBuildID, null).emptyScriptDir();
  }


  /**
   * Returns true if script directory is empty.
   */
  public boolean scriptDirIsEmpty(final int activeBuildID) throws IOException {
    return getLocalBuilder(activeBuildID, null).scriptDirIsEmpty();
  }


  public String getHostName() {
    return IoUtils.getLocalHostNameHard();
  }


  /**
   * @return true if the given path is prohibited for deletion.
   */
  public boolean isProhibitedPath(final String path) throws IOException {
    return IoUtils.isProhibitedPath(new File(path));
  }


  /**
   * @return a new executor handle that can be used to
   *         access a remote command's environement.
   */
  public int createExecutorHandle() {
    return getLocalBuilderEnvironmnent().createExecutorHandle();
  }


  public TailUpdate getTailUpdate(final int commandHandle, final long sinceServerTimeMs) {
    return new Tail(commandHandle).getTailUpdate(sinceServerTimeMs);
  }


  public String getSystemWorkingDirName(final int activeBuildID) throws IOException {
    return getLocalBuilder(activeBuildID, null).getSystemWorkingDirName();
  }


  public Map getSystemProperties() {
    return getLocalBuilderEnvironmnent().getSystemProperties();
  }


  /**
   * Helper method
   */
  private static LocalAgent getLocalBuilder(final int buildID, final String checkoutDir) throws IOException {
    return new LocalAgent(buildID, checkoutDir);
  }


  private static LocalAgentEnvironment getLocalBuilderEnvironmnent() {
    return new LocalAgentEnvironment();
  }


  /**
   * Validates that the given path is under builds home dir or
   * under temp dir.
   */
  private static void validatePathIsAllowed(final String fileName) throws IOException {
    final File file = new File(fileName);
    final String filePath = file.getCanonicalPath();
    final String systemPath = new File(ConfigurationManager.getSystemWorkDirectoryName()).getCanonicalPath();
    String tempDirName = System.getProperty("java.io.tmpdir");
    if (StringUtils.isBlank(tempDirName)) tempDirName = systemPath;
    final String tempPath = new File(tempDirName).getCanonicalPath();
    if (!file.isAbsolute() || !(filePath.startsWith(systemPath) || filePath.startsWith(tempPath))) {
      throw new IOException("Deleting file \"" + fileName + "\" is prohibited.");
    }
  }


  /**
   * An enumeration to support validation for security of a
   * request. Constants are used to avoid memory thrashing
   * because requests are coming in rapid succession.
   */
  private static final class SecurityValidationResult {

    private static final boolean VALIDATION_SUCCESSFUL = true;
    private static final boolean VALIDATION_FAILED = false;

    public static final SecurityValidationResult FAILURE_NON_REGISTERED_MANAGER = new SecurityValidationResult(VALIDATION_FAILED, "Access from a non-registered build manager is forbidded");
    public static final SecurityValidationResult FAILURE_NONLOCAL_MANAGER = new SecurityValidationResult(VALIDATION_FAILED, "Access from a non-local build manager is forbidded");
    public static final SecurityValidationResult FAILURE_USER_NOT_AUTHORISED = new SecurityValidationResult(VALIDATION_FAILED, "User is not authorised");
    public static final SecurityValidationResult FAILURE_USER_NOT_SET = new SecurityValidationResult(VALIDATION_FAILED, "User is not set");
    public static final SecurityValidationResult SUCCESS = new SecurityValidationResult(VALIDATION_SUCCESSFUL, "Security validation successful");

    private final boolean successful;
    private final String message;


    private SecurityValidationResult(final boolean successful, final String message) {
      this.successful = successful;
      this.message = message;
    }


    public boolean isSuccessful() {
      return successful;
    }


    public String getMessage() {
      return message;
    }
  }
}
