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
package org.parabuild.ci.build;

import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.process.TailBufferSize;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.services.ModifiedFileList;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;
import org.parabuild.ci.services.TailUpdate;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * FailedAgent
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 23, 2009 6:04:41 PM
 */
final class FailedAgent implements Agent {

  private final IOException exception;


  public FailedAgent(final IOException e) {
    exception = e;
  }


  public boolean emptyCheckoutDir() throws IOException {
    throw exception;
  }


  public boolean deleteCheckoutDir() throws IOException {
    throw exception;
  }


  public boolean checkoutDirIsEmpty() throws IOException {
    throw exception;
  }


  public boolean checkoutDirExists() throws IOException {
    throw exception;
  }


  public boolean commandIsAvailable(final String command) throws IOException {
    throw exception;
  }


  public String getCheckoutDirName() throws IOException {
    throw exception;
  }


  public boolean fileRelativeToCheckoutDirExists(final String relativePath) throws IOException {
    throw exception;
  }


  public void createBuildDirs() throws IOException {
    throw exception;
  }


  public boolean deleteFileUnderCheckoutDir(final String path) throws IOException {
    throw exception;
  }


  public boolean logDirIsEmpty() throws IOException {
    throw exception;

  }


  public boolean passwordDirIsEmpty() throws IOException {
    throw exception;

  }


  public boolean emptyLogDir() throws IOException {
    throw exception;

  }


  public boolean dirRelativeToCheckoutDirIsEmpty(final String relativePath) throws IOException {
    throw exception;

  }


  public boolean absolutePathExists(final String absolutePath) throws IOException {
    throw exception;

  }


  public boolean emptyPasswordDir() throws IOException {
    throw exception;

  }


  public boolean relativeTempPathExists(final String relativePathInTempDirectory) throws IOException {
    throw exception;

  }


  public boolean isWindows() throws IOException {
    throw exception;

  }


  public boolean isUnix() throws IOException {
    throw exception;

  }


  public int systemType() throws IOException {
    throw exception;
  }


  public int execute(final int executorHandle, final String relativeDirectoryToExecuteIn, final String command, final Map environment, final TailBufferSize tailBufferSize, final File stdoutFile, final File stderrFile, final File mergedFile) throws IOException {
    throw exception;
  }


  public String createTempFile(final String prefix, final String suffix, final String content) throws IOException {
    throw exception;
  }


  public boolean deleteTempFile(final String name) throws IOException {
    throw exception;

  }


  public boolean deleteTempFileHard(final String tempFileName) {
    throw new IllegalStateException(StringUtils.toString(exception), exception);
  }


  public String getTempDirName() throws IOException {
    throw exception;
  }


  public int getActiveBuildID() {
    throw new IllegalStateException(StringUtils.toString(exception), exception);
  }


  public String cygwinWindowsPathToUnix(final String path) throws IOException {
    throw exception;
  }


  public String getCheckoutDirHome() throws IOException {
    throw exception;
  }


  public boolean pathIsFile(final String file) throws IOException {
    throw exception;
  }


  public boolean pathIsDirectory(final String path) throws IOException {
    throw exception;

  }


  public void readFile(final String path, final File readInTo) throws IOException {
    throw exception;
  }


  public void getDirectory(final String path, final File readInTo) throws IOException {
    throw exception;
  }


  public String[] listFilesInDirectory(final String path, final String extension) throws IOException {
    throw exception;
  }


  public String getFileName(final String absolutePath) throws IOException {
    throw exception;
  }


  public String getSystemProperty(final String propertyName) throws IOException {
    throw exception;
  }


  public String getEnvVariable(final String varName) throws IOException {
    throw exception;
  }


  public String pathSeparator() throws IOException {
    throw exception;
  }


  public void createFile(final String absoluteFile, final String content) throws IOException {
    throw exception;
  }


  public boolean mkdirs(final String absolutePath) throws IOException {
    throw exception;

  }


  public Locale defaultLocale() throws IOException {
    throw exception;
  }


  public RemoteFileDescriptor getFileDescriptor(final String absolutePath) throws IOException {
    throw exception;
  }


  public String makeStepScriptPath(final int sequenceID) throws IOException {
    throw exception;
  }


  public String fixCRLF(final String stringToFix) throws IOException {
    throw exception;
  }


  public boolean deleteBuildFiles() throws IOException {
    throw exception;

  }


  public AgentHost getHost() {
    throw new IllegalStateException(StringUtils.toString(exception), exception);
  }


  public long currentTimeMillis() throws IOException {
    throw exception;
  }


  public ModifiedFileList getModifiedFiles(final String path, final long timeSinceMillis, final int maximumNumberOfFiles) throws IOException {
    throw exception;
  }


  public boolean deleteFileHard(final String path) {
    throw new IllegalStateException(StringUtils.toString(exception), exception);
  }


  public boolean emptyScriptDir() throws IOException {
    throw exception;
  }


  public boolean scriptDirIsEmpty() throws IOException {
    throw exception;
  }


  public String getLocalHostName() throws IOException {
    throw exception;
  }


  public int createCommandHandle() throws IOException {
    throw exception;
  }


  public TailUpdate getTailUpdate(final int commandHandle, final long sinceServerTimeMs) throws IOException {
    throw exception;
  }


  public String getSystemWorkingDirName() throws IOException {
    throw exception;
  }


  public String separator() throws IOException {
    throw exception;
  }
}
