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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.process.TailBufferSize;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Native environment view of a agent box.
 */
public interface AgentEnvironment {


  byte SYSTEM_TYPE_WINNT = 0;
  byte SYSTEM_TYPE_WIN95 = 1;
  byte SYSTEM_TYPE_CYGWIN = 2;
  byte SYSTEM_TYPE_UNIX = 3;
  byte SYSTEM_TYPE_SUNOS = 4;
  byte SYSTEM_TYPE_MACOSX = 5;
  byte SYSTEM_TYPE_HPUX = 6;
  byte SYSTEM_TYPE_LINUX = 7;


  /**
   * @return agent version hash code
   */
  int builderVersionHashCode() throws IOException, AgentFailureException;


  /**
   * @return agent version hash code
   */
  String builderVersionAsString() throws IOException, AgentFailureException;


  /**
   * Return value of system environment variable.
   */
  String getEnvVariable(String variableName) throws IOException, AgentFailureException;


  /**
   * @return Map containing shell environment variables.
   */
  Map getEnv() throws IOException, AgentFailureException;


  /**
   * Returns system type
   */
  byte systemType() throws IOException, AgentFailureException;


  /**
   * Returns true if given executable is available.
   *
   * @param command
   * @return true if given executable is available.
   */
  boolean commandIsAvailable(String command) throws IOException, AgentFailureException;


  /**
   * Retuns true if agent runs under Windows
   */
  boolean isWindows() throws IOException, AgentFailureException;


  /**
   * Returns true if agent runs under Unix
   */
  boolean isUnix() throws IOException, AgentFailureException;


  /**
   * Converts Windows path to Cygwin path. This method mas be executed
   * in Cygwin environment.
   */
  String cygwinWindowsPathToUnix(String absolutePath) throws IOException, AgentFailureException;


  /**
   * Executes command. If InputStream is not null; will
   * first write to process OutputStream.
   */
  void execute(String directoryToExecuteIn, String cmd, Map environment, OutputStream stdout, OutputStream stderr) throws IOException, CommandStoppedException, AgentFailureException;


  int execute(final int handle, String directoryToExecuteIn, String cmd, Map environment, final TailBufferSize tailBufferSize, File stdoutFile, File stderrFile, File mergedFile) throws IOException, CommandStoppedException, AgentFailureException;


  /**
   * Return agent Java system property.
   *
   * @param propertyName to return value for
   * @return agent Java system property.
   */
  String getSystemProperty(String propertyName) throws IOException, AgentFailureException;


  /**
   * Returns agent's environment path separator (as in File.pathSeparator).
   */
  String pathSeparator() throws IOException, AgentFailureException;


  /**
   * Returns agent's environment separator  (as in File.separator).
   */
  String separator() throws IOException, AgentFailureException;


  /**
   * Returns true if the given path is absolute. The path
   * does not have to exist.
   *
   * @param path to check
   * @return true if the given path is absolute.
   */
  boolean isAbsoluteFile(String path) throws IOException, AgentFailureException;


  /**
   * @return true if the given path is prohibited for deletion.
   */
  boolean isProhibitedPath(String path) throws IOException, AgentFailureException;


  /**
   * @return a host and port that was used to create this
   *         agent environment.
   */
  String getHost();


  /**
   * @return a new executor handle that can be used to
   *         access a remote command's environment.
   */
  int createExecutorHandle() throws IOException;

  /**
   * Returns JVM Properties.
   *
   * @return JVM Properties.
   */
  Map getSystemProperties() throws IOException;
}
