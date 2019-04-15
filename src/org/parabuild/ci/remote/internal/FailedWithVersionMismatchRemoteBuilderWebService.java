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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.process.TailBufferSize;
import org.parabuild.ci.remote.services.ExecuteResult;
import org.parabuild.ci.remote.services.ModifiedFileList;
import org.parabuild.ci.remote.services.RemoteBuilderWebService;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;
import org.parabuild.ci.services.TailUpdate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Web service that can only return version
 * <p/>
 *
 * @author Slava Imeshev
 * @since Jun 23, 2009 11:38:57 PM
 */
final class FailedWithVersionMismatchRemoteBuilderWebService implements RemoteBuilderWebService {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log LOG = LogFactory.getLog(FailedWithVersionMismatchRemoteBuilderWebService.class); // NOPMD
  private final String builderVersionAsString;
  private final String url;
  private final String managerVersion;


  public FailedWithVersionMismatchRemoteBuilderWebService(final String managerVersion, final String builderVersionAsString, final String url) {
    this.builderVersionAsString = builderVersionAsString;
    this.url = url;
    this.managerVersion = managerVersion;
  }


  public int builderVersionHashCode() throws IOException {
    throw createVersionMismatchException();
  }


  public String builderVersionAsString() {
    return builderVersionAsString;
  }


  public String getEnvVariable(final String variableName) throws IOException {
    throw createVersionMismatchException();
  }


  private IOException createVersionMismatchException() {
    return new IOException(createErrorMessage());
  }


  private String createErrorMessage() {
    return "Version of the remote agent \"" + builderVersionAsString + "\" at \"" + url + "\" does not match build manager version \"" + managerVersion + "\". The versions should be the same.";
  }


  private IllegalStateException createRuntimeVersionMismatchException() {
    return new IllegalStateException(createErrorMessage());
  }


  public Map getEnv() throws IOException {
    throw createVersionMismatchException();
  }


  public byte systemType() throws IOException {
    throw createVersionMismatchException();
  }


  public boolean commandIsAvailable(final String command) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean isWindows() throws IOException {
    throw createVersionMismatchException();
  }


  public boolean isUnix() throws IOException {
    throw createVersionMismatchException();
  }


  public String cygwinWindowsPathToUnix(final String absolutePath) throws IOException {
    throw createVersionMismatchException();
  }


  public ExecuteResult execute(final int handle, final String directoryToExecuteIn, final String cmd, final Map environment, final TailBufferSize tailBufferSize, final boolean mergeStdoutAndStdErr) throws IOException {
    throw createVersionMismatchException();
  }


  public void deleteFile(final String stdoutFileName) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean deleteFileHard(final String fileName) throws IOException {
    throw createVersionMismatchException();
  }


  public String getSystemProperty(final String propertyName) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean emptyCheckoutDir(final int buildID, final String checkoutDir) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean checkoutDirIsEmpty(final int buildID, final String checkoutDir) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean checkoutDirExists(final int buildID, final String checkoutDir) throws IOException {
    throw createVersionMismatchException();
  }


  public String getCheckoutDirName(final int buildID, final String checkoutDir) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean relativeFileExists(final int buildID, final String checkoutDir, final String relativePath) throws IOException {
    throw createVersionMismatchException();
  }


  public void createBuildDirs(final int buildID, final String checkoutDir) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean deleteRelativeFile(final int buildID, final String checkoutDir, final String relativePath) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean logDirIsEmpty(final int buildID) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean passwordDirIsEmpty(final int buildID) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean relativeDirIsEmpty(final int buildID, final String checkoutDir, final String relativePath) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean emptyLogDir(final int buildID) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean absolutePathExists(final int buildID, final String absolutePath) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean emptyPasswordDir(final int buildID) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean tempDirExists(final int buildID, final String relativePathInTempDirectory) throws IOException {
    throw createVersionMismatchException();
  }


  public String getBuildTempDirName(final int buildID) throws IOException {
    throw createVersionMismatchException();
  }


  public String makeStepScriptPath(final int activeBuildID, final int sequenceID) throws IOException {
    throw createVersionMismatchException();
  }


  public String getCheckoutDirHome(final int buildID, final String checkoutDir) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean pathIsFile(final int buildID, final String file) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean pathIsDirectory(final int buildID, final String path) throws IOException {
    throw createVersionMismatchException();
  }


  public String[] listFilesInDirectory(final int buildID, final String path, final String extension) throws IOException {
    throw createVersionMismatchException();
  }


  public List listFiles(final int buildID, final String remoteDir) throws IOException {
    throw createVersionMismatchException();
  }


  public String getFileName(final String path) throws IOException {
    throw createVersionMismatchException();
  }


  public String createTempFile(final int buildID, final String prefix, final String suffix, final String content) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean deleteTempFile(final int buildID, final String name) throws IOException {
    throw createVersionMismatchException();
  }


  public String pathSeparator() throws IOException {
    throw createVersionMismatchException();
  }


  public String separator() throws IOException {
    throw createVersionMismatchException();
  }


  public void createFile(final int buildID, final String absoluteFile, final String content) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean mkdirs(final int buildID, final String path) throws IOException {
    throw createVersionMismatchException();
  }


  public RemoteFileDescriptor getFileDescriptor(final String absolutePath) throws IOException {
    throw createVersionMismatchException();
  }


  public LocaleData defaultLocaleData() {
    throw createRuntimeVersionMismatchException();
  }


  public boolean deleteCheckoutDir(final int activeBuildID, final String checkoutDir) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean isAbsoluteFile(final String path) throws IOException {
    throw createVersionMismatchException();
  }


  public String fixCRLF(final String stringToFix) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean deleteBuildFiles(final int activeBuildID, final String checkoutDir) throws IOException {
    throw createVersionMismatchException();
  }


  public long currentTimeMillis() {
    throw createRuntimeVersionMismatchException();
  }


  public ModifiedFileList getModifiedFiles(final int activeBuildID, final String path, final long timeSinceMillis, final int maximumNumberOfFiles) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean emptyScriptDir(final int activeBuildID) throws IOException {
    throw createVersionMismatchException();
  }


  public boolean scriptDirIsEmpty(final int activeBuildID) throws IOException {
    throw createVersionMismatchException();
  }


  public String getHostName() {
    throw createRuntimeVersionMismatchException();
  }


  public boolean isProhibitedPath(final String path) throws IOException {
    throw createVersionMismatchException();
  }


  public int createExecutorHandle() throws IOException {
    throw createVersionMismatchException();
  }


  public TailUpdate getTailUpdate(final int commandHandle, final long sinceServerTimeMs) {
    throw createRuntimeVersionMismatchException();
  }


  public String getSystemWorkingDirName(final int activeBuildID) throws IOException {
    throw createVersionMismatchException();
  }


  public Map getSystemProperties() throws IOException {
    throw createVersionMismatchException();
  }


  public String toString() {
    return "FailedWithVersionMismatchRemoteBuilderWebService{" +
            "builderVersionAsString='" + builderVersionAsString + '\'' +
            ", url='" + url + '\'' +
            ", managerVersion='" + managerVersion + '\'' +
            '}';
  }
}
