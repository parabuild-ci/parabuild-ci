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
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.Version;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.RuntimeUtils;
import org.parabuild.ci.process.OsCommand;
import org.parabuild.ci.process.TailBufferSize;
import org.parabuild.ci.remote.AgentEnvironment;

/**
 * This is class represents a local environment.
 */
public final class LocalAgentEnvironment implements AgentEnvironment {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(IoUtils.class); // NOPMD


  /**
   * @return agent version hash code
   */
  public int builderVersionHashCode() {
    return Version.versionToString(true).hashCode();
  }


  /**
   * @return agent version hash code
   */
  public String builderVersionAsString() {
    return Version.versionToString(true);
  }


  /**
   * Return value of system environment variable.
   */
  public String getEnvVariable(final String variableName) {
    return RuntimeUtils.getEnvVariable(variableName);
  }


  /**
   * @return Map containing shell environment variables.
   */
  public Map getEnv() {
    return RuntimeUtils.getStartupEnv();
  }


  /**
   * Returns system type
   */
  public byte systemType() {
    return RuntimeUtils.systemType();
  }


  /**
   * Returns true if given executable is available.
   *
   * @param command
   * @return true if given executable is available.
   */
  public boolean commandIsAvailable(final String command) {
    return RuntimeUtils.commandIsAvailable(command);
  }


  /**
   * Retuns true if agent runs under Windows
   */
  public boolean isWindows() {
    return RuntimeUtils.isWindows();
  }


  /**
   * Retuns true if agent runs under Unix
   */
  public boolean isUnix() {
    return RuntimeUtils.isUnix();
  }


  /**
   * Converts Windows path to Cygwin path. This method mas be
   * executed in Cygwin environment.
   */
  public String cygwinWindowsPathToUnix(final String absolutePath) throws IOException {
    return RuntimeUtils.cygwinWindowsPathToUnix(absolutePath);
  }


  /**
   * Executes command. If InputStream is not null; will first
   * write to process OutputStream.
   */
  public void execute(final String directoryToExecuteIn, final String cmd, final Map environment, final OutputStream stdout, final OutputStream stderr) throws IOException, CommandStoppedException {
    RuntimeUtils.execute(getCurrentDir(directoryToExecuteIn), cmd, environment, stdout, stderr);
  }


  public int execute(final int handle, final String directoryToExecuteIn, final String cmd, final Map environment, final TailBufferSize tailBufferSize, final File stdoutFile, final File stderrFile, final File mergedFile) throws IOException, CommandStoppedException {
    return new OsCommand(handle, directoryToExecuteIn, cmd, environment, tailBufferSize, stdoutFile, stderrFile, mergedFile).execute();
  }


  public String getSystemProperty(final String propertyName) {
    return System.getProperty(propertyName);
  }


  /**
   * Returns agent's environment path separator.
   */
  public String pathSeparator() {
    return File.pathSeparator;
  }


  /**
   * Returns agent's environment separator  (as in
   * File.separator).
   */
  public String separator() {
    return File.separator;
  }


  /**
   * Returns true if the given path is absolute.
   *
   * @param path to check
   * @return true if the given path is absolute.
   */
  public boolean isAbsoluteFile(final String path) {
    return new File(path).isAbsolute();
  }


  public boolean isProhibitedPath(final String path) throws IOException {
    return IoUtils.isProhibitedPath(new File(path));
  }


  /**
   * @return blank string because this is a local one.
   */
  public String getHost() {
    return "";
  }


  /**
   * @return a new executor handle that can be used to
   *         access a remote command's environement.
   */
  public synchronized int createExecutorHandle() {
    return OsCommand.createCommandHandle();
  }


  public Map getSystemProperties() {
    return System.getProperties();
  }


  /**
   * Helper method.
   */
  private static File getCurrentDir(final String fileName) {
    if (fileName == null) {
      return null;
    }
    return new File(fileName);
  }
}
