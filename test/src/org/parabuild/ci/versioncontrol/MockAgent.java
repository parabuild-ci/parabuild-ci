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

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.process.TailBufferSize;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.services.ModifiedFileList;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;
import org.parabuild.ci.services.TailUpdate;

/**
 * This class is a proxy to Agent that overwrites a check out
 * dir name.
 *
 * @see SSTestSVNCheckoutCommand
 */
class MockAgent implements Agent {

  private final Agent agent;
  private final String checkoutDirName;


  /**
   * Proxy constrcutor. Forces checjout dir name be a given
   * name.
   *
   * @param agent
   * @param checkouDirName
   */
  public MockAgent(final Agent agent, final String checkouDirName) {
    this.agent = agent;
    this.checkoutDirName = checkouDirName;
  }


  public int getActiveBuildID() {
    return agent.getActiveBuildID();
  }


  public String cygwinWindowsPathToUnix(final String path) throws IOException, AgentFailureException {
    return agent.cygwinWindowsPathToUnix(path);
  }


  public boolean emptyCheckoutDir() throws IOException, AgentFailureException {
    return agent.emptyCheckoutDir();
  }


  /**
   * Deletes checkout dir.
   *
   * @return true if there are no files left after cleanup
   */
  public boolean deleteCheckoutDir() throws IOException {
    return IoUtils.deleteFileHard(new File(checkoutDirName));
  }


  public String getCheckoutDirHome() throws IOException, AgentFailureException {
    return agent.getCheckoutDirHome();
  }


  public boolean checkoutDirIsEmpty() throws IOException, AgentFailureException {
    return agent.checkoutDirIsEmpty();
  }


  public boolean emptyLogDir() throws IOException, AgentFailureException {
    return agent.emptyLogDir();
  }


  public boolean logDirIsEmpty() throws IOException, AgentFailureException {
    return agent.logDirIsEmpty();
  }


  public boolean emptyPasswordDir() throws IOException, AgentFailureException {
    return agent.emptyPasswordDir();
  }


  public boolean passwordDirIsEmpty() throws IOException, AgentFailureException {
    return agent.passwordDirIsEmpty();
  }


  public boolean checkoutDirExists() throws IOException, AgentFailureException {
    return agent.checkoutDirExists();
  }


  public int execute(final int executorHandle, final String directoryToExecuteIn, final String command, final Map environment, final TailBufferSize tailBufferSize, final File stdoutFile, final File stderrFile, final File mergedFile) throws IOException, CommandStoppedException, AgentFailureException {
    return agent.execute(executorHandle, directoryToExecuteIn, command, environment, tailBufferSize, stdoutFile, stderrFile, mergedFile);
  }


  public boolean commandIsAvailable(final String command) throws IOException, AgentFailureException {
    return agent.commandIsAvailable(command);
  }


  public int systemType() throws IOException, AgentFailureException {
    return agent.systemType();
  }


  public String getCheckoutDirName() throws IOException, AgentFailureException {
    return checkoutDirName;
  }


  public String getTempDirName() throws IOException, AgentFailureException {
    return agent.getTempDirName();
  }


  public boolean fileRelativeToCheckoutDirExists(final String relativePath) throws IOException, AgentFailureException {
    return agent.fileRelativeToCheckoutDirExists(relativePath);
  }


  public boolean dirRelativeToCheckoutDirIsEmpty(final String relativePath) throws IOException, AgentFailureException {
    return agent.dirRelativeToCheckoutDirIsEmpty(relativePath);
  }


  public boolean absolutePathExists(final String absolutePath) throws IOException, AgentFailureException {
    return agent.absolutePathExists(absolutePath);
  }


  public boolean deleteFileUnderCheckoutDir(final String relativePath) throws IOException, AgentFailureException {
    return agent.deleteFileUnderCheckoutDir(relativePath);
  }


  public void createBuildDirs() throws IOException, AgentFailureException {
    agent.createBuildDirs();
  }


  public boolean relativeTempPathExists(final String relativePathInTempDirectory) throws IOException, AgentFailureException {
    return agent.relativeTempPathExists(relativePathInTempDirectory);
  }


  public boolean isWindows() throws IOException, AgentFailureException {
    return agent.isWindows();
  }


  public boolean isUnix() throws IOException, AgentFailureException {
    return agent.isUnix();
  }


  public String createTempFile(final String prefix, final String suffix, final String content) throws IOException, AgentFailureException {
    return agent.createTempFile(prefix, suffix, content);
  }


  public boolean deleteTempFile(final String name) throws IOException, AgentFailureException {
    return agent.deleteTempFile(name);
  }


  public boolean deleteTempFileHard(final String tempFileName) {
    return agent.deleteTempFileHard(tempFileName);
  }


  /**
   * Makes a script file name to be used to write a step script.
   *
   * @param sequenceID to create a script for.
   * @return absolute path
   * @throws java.io.IOException
   */
  public String makeStepScriptPath(final int sequenceID) throws IOException, AgentFailureException {
    return agent.makeStepScriptPath(sequenceID);
  }


  public boolean pathIsFile(final String path) throws IOException, AgentFailureException {
    return agent.pathIsFile(path);
  }


  public boolean pathIsDirectory(final String path) throws IOException, AgentFailureException {
    return agent.pathIsDirectory(path);
  }


  public void readFile(final String path, final File readInTo) throws IOException, AgentFailureException {
    agent.readFile(path, readInTo);
  }


  public void getDirectory(final String path, final File readInTo) throws IOException, AgentFailureException {
    agent.getDirectory(path, readInTo);
  }


  public String getFileName(final String path) throws IOException, AgentFailureException {
    return agent.getFileName(path);
  }


  public String getSystemProperty(final String propertyName) throws IOException, AgentFailureException {
    return agent.getSystemProperty(propertyName);
  }


  public String getEnvVariable(final String varName) throws IOException, AgentFailureException {
    return agent.getEnvVariable(varName);
  }


  public String pathSeparator() throws IOException, AgentFailureException {
    return agent.pathSeparator();
  }


  public void createFile(final String absoluteFile, final String content) throws IOException, AgentFailureException {
    agent.createFile(absoluteFile, content);
  }


  public boolean mkdirs(final String absolutePath) throws IOException, AgentFailureException {
    return agent.mkdirs(absolutePath);
  }


  public Locale defaultLocale() throws IOException, AgentFailureException {
    return agent.defaultLocale();
  }


  public RemoteFileDescriptor getFileDescriptor(final String absolutePath) throws IOException, AgentFailureException {
    return agent.getFileDescriptor(absolutePath);
  }


  public String[] listFilesInDirectory(final String path, final String extensions) throws IOException, AgentFailureException {
    return agent.listFilesInDirectory(path, extensions);
  }


  /**
   * Fixes CRLF according to local system.
   *
   * @param stringToFix
   * @return String with fixed CRLF according to local system.
   */
  public String fixCRLF(final String stringToFix) throws IOException, AgentFailureException {
    return agent.fixCRLF(stringToFix);
  }


  public boolean deleteBuildFiles() throws IOException, AgentFailureException {
    return agent.deleteBuildFiles();
  }


  public AgentHost getHost() {
    return agent.getHost();
  }


  public long currentTimeMillis() throws IOException, AgentFailureException {
    return agent.currentTimeMillis();
  }


  public ModifiedFileList getModifiedFiles(final String path, final long timeSinceMillis, final int maximumNumberOfFiles) throws IOException, AgentFailureException {
    return null;
  }


  public boolean deleteFileHard(final String path) {
    return agent.deleteFileHard(path);
  }


  /**
   * Deletes all content from the script dir.
   */
  public boolean emptyScriptDir() throws IOException, AgentFailureException {
    return agent.emptyScriptDir();
  }


  /**
   * Returns true if script directory is empty
   */
  public boolean scriptDirIsEmpty() throws IOException, AgentFailureException {
    return agent.scriptDirIsEmpty();
  }


  /**
   * @return agent's host name
   */
  public String getLocalHostName() throws IOException, AgentFailureException {
    return agent.getLocalHostName();
  }


  public int createCommandHandle() throws IOException, AgentFailureException {
    return agent.createCommandHandle();
  }


  public TailUpdate getTailUpdate(final int commandHandle, final long sinceServerTimeMs) throws IOException, AgentFailureException {
    return agent.getTailUpdate(commandHandle, sinceServerTimeMs);
  }


  public String getSystemWorkingDirName() throws IOException, AgentFailureException {
    return agent.getSystemWorkingDirName();
  }


  public String separator() throws IOException, AgentFailureException {
    return agent.separator();
  }


  public String toString() {
    return "MockAgent{" +
            "agent=" + agent +
            ", checkoutDirName='" + checkoutDirName + '\'' +
            '}';
  }
}
