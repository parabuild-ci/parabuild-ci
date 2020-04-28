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
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.process.TailBufferSize;
import org.parabuild.ci.process.TailBufferSizeImpl;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.RemoteUtils;
import org.parabuild.ci.remote.services.ExecuteResult;
import org.parabuild.ci.remote.services.RemoteBuilderWebService;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public final class RemoteAgentEnvironmentProxy implements AgentEnvironment {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(RemoteAgentEnvironmentProxy.class); // NOPMD
  private WebServiceLocator webServiceLocator = null;


  /**
   * Constructor
   */
  public RemoteAgentEnvironmentProxy(final AgentHost agentHost) {
    this(new WebServiceLocator(agentHost));
  }


  /**
   * Constructor
   */
  public RemoteAgentEnvironmentProxy(final WebServiceLocator webServiceLocator) {
    this.webServiceLocator = webServiceLocator;
  }


  /**
   * @return agent version hash code
   */
  public int builderVersionHashCode() throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().builderVersionHashCode();
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * @return agent version hash code
   */
  public String builderVersionAsString() throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().builderVersionAsString();
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Return value of system environment variable.
   */
  public String getEnvVariable(final String variableName) throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().getEnvVariable(variableName);
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * @return Map containing shell environment variables.
   */
  public Map getEnv() throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().getEnv();
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns system type
   */
  public byte systemType() throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().systemType();
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns true if given executable is available.
   *
   * @param command
   * @return true if given executable is available.
   */
  public boolean commandIsAvailable(final String command) throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().commandIsAvailable(command);
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Retuns true if agent runs under Windows
   */
  public boolean isWindows() throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().isWindows();
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Retuns true if agent runs under Unix
   */
  public boolean isUnix() throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().isUnix();
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Converts Windows path to Cygwin path. This method mas be
   * executed in Cygwin environment.
   */
  public String cygwinWindowsPathToUnix(final String absolutePath) throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().cygwinWindowsPathToUnix(absolutePath);
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Executes command.
   */
  public void execute(final String directoryToExecuteIn, final String cmd, final Map environment,
                      final OutputStream stdout, final OutputStream stderr) throws IOException, CommandStoppedException, AgentFailureException {
    //
    // NOTE: simeshev@parabuilci.org - 07/18/2004 - Challenge here is
    // that we have to make more then one call to remote agent in
    // order to get all resuls of execution of the remote command,
    // stdout and stderr precisely. That means that remote agent
    // should keep command execution logs till it is asked to remove
    // them.
    //
    RemoteBuilderWebService remoteService = null;
    ExecuteResult result = null;
    try {
      // call remote agent
      remoteService = webServiceLocator.getWebService();
      result = remoteService.execute(0, directoryToExecuteIn, cmd, environment, new TailBufferSizeImpl(0, 0), false);

      // transfer result from remote
      final RemoteFileGetter remoteFileGetter = new RemoteFileGetter(webServiceLocator);
      remoteFileGetter.copy(result.getStdoutFileName(), stdout);
      remoteFileGetter.copy(result.getStderrFileName(), stderr);

    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    } finally {
      try {
        if (remoteService != null && result != null) {
          // cleanup remote
          remoteService.deleteFileHard(result.getStdoutFileName());
          remoteService.deleteFileHard(result.getStderrFileName());
        }
      } catch (final Exception e) {
        IoUtils.ignoreExpectedException(e);
      }
    }
  }


  public int execute(final int handle, final String directoryToExecuteIn, final String cmd, final Map environment, final TailBufferSize tailBufferSize, final File stdoutFile, final File stderrFile, final File mergedFile) throws IOException, CommandStoppedException, AgentFailureException {
    RemoteBuilderWebService remoteBuilderWebService = null;
    ExecuteResult result = null;
    try {
      // call remote agent
      remoteBuilderWebService = webServiceLocator.getWebService();
      result = remoteBuilderWebService.execute(handle, directoryToExecuteIn, cmd, environment, tailBufferSize, true);

      // transfer result from remote
      final RemoteFileGetter remoteFileGetter = new RemoteFileGetter(webServiceLocator);
      remoteFileGetter.copy(result.getStdoutFileName(), stdoutFile);
      remoteFileGetter.copy(result.getStderrFileName(), stderrFile);
      remoteFileGetter.copy(result.getMergedFileName(), mergedFile);
      return result.getResultCode();
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    } finally {
      try {
        if (remoteBuilderWebService != null && result != null) {
          // cleanup remote
          //if (log.isDebugEnabled()) log.debug("cleaning up stdout: " + result.getStdoutFileName());
          //if (log.isDebugEnabled()) log.debug("cleaning up stderr: " + result.getStderrFileName());
          //if (log.isDebugEnabled()) log.debug("cleaning up merged: " + result.getMergedFileName());
          remoteBuilderWebService.deleteFileHard(result.getStdoutFileName());
          remoteBuilderWebService.deleteFileHard(result.getStderrFileName());
          remoteBuilderWebService.deleteFileHard(result.getMergedFileName());
        }
      } catch (final Exception e) {
        IoUtils.ignoreExpectedException(e);
      }
    }
  }


  /**
   * Return agent Java system property.
   *
   * @param propertyName to return value for
   * @return agent Java system property.
   */
  public String getSystemProperty(final String propertyName) throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().getSystemProperty(propertyName);
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns agent's environemnt path separator.
   */
  public String pathSeparator() throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().pathSeparator();
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns agent's environment separator  (as in
   * File.separator).
   */
  public String separator() throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().separator();
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * Returns true if the given path is absolute.
   *
   * @param path to check
   * @return true if the given path is absolute.
   */
  public boolean isAbsoluteFile(final String path) throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().isAbsoluteFile(path);
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  public boolean isProhibitedPath(final String path) throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().isProhibitedPath(path);
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  /**
   * @return a host and port that was used to create this
   *         agent environment.
   */
  public String getHost() {
    return webServiceLocator.agentHostName();
  }


  /**
   * @return a new executor handle that can be used to
   *         access a remote command's environement.
   */
  public int createExecutorHandle() throws IOException {
    return webServiceLocator.getWebService().createExecutorHandle();
  }


  /**
   * Returns JVM Properties.
   *
   * @return JVM Properties.
   */
  public Map getSystemProperties() throws IOException {
    return webServiceLocator.getWebService().getSystemProperties();
  }


  /**
   * @return default locale
   * @throws MalformedURLException
   */
  public Locale defaultLocale() throws IOException, AgentFailureException {
    try {
      return webServiceLocator.getWebService().defaultLocaleData().getLocale();
    } catch (final HessianRuntimeException hre) {
      throw RemoteUtils.hessianRuntimeExeptionToIOException(hre, webServiceLocator.agentHostName());
    }
  }


  public String toString() {
    return "RemoteAgentEnvironmentProxy{" +
            "webServiceLocator=" + webServiceLocator +
            '}';
  }
}
