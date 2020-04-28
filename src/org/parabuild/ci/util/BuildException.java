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
package org.parabuild.ci.util;

import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.process.RemoteCommand;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentEnvironment;

import java.io.IOException;


/**
 * Build exception is used to report exceptional conditions
 * throughout the build process
 */
public final class BuildException extends Exception {

  private static final long serialVersionUID = -295003302496228790L; // NOPMD

  private String logContent = null;
  private boolean reported = false;
  private String hostName = null;


  /**
   * Constructs an <code>Exception</code> with the specified
   * detail message.
   *
   * @param s the detail message.
   */
  public BuildException(final String s) {
    super(s);
  }


  /**
   * Constructs a new exception with the specified detail message
   * and cause.  <p>Note that the detail message associated with
   * <code>cause</code> is <i>not</i> automatically incorporated
   * in this exception's detail message.
   *
   * @param message the detail message (which is saved for later
   *                retrieval by the {@link #getMessage()} method).
   * @param cause   the cause (which is saved for later retrieval
   *                by the {@link #getCause()} method).  (A <tt>null</tt> value
   *                is permitted, and indicates that the cause is nonexistent or
   *                unknown.)
   * @since 1.4
   */
  public BuildException(final String message, final Throwable cause) {
    super(message, cause);
  }


  public BuildException(final Throwable cause) {
    super(StringUtils.toString(cause), cause);
  }


  public BuildException(final String message, final Agent agent) {
    super(message);
    setHostNameFrom(agent);
  }


  /**
   * Constructor.
   *
   * @param message
   * @param cause
   * @param agent
   */
  public BuildException(final String message, final Exception cause, final Agent agent) {
    this(message, cause);
    setHostNameFrom(agent);
  }


  public BuildException(final String message, final Exception cause, final RemoteCommand command) {
    this(message, cause);
    setHostNameFrom(command);
  }


  public BuildException(final String message, final Exception cause, final AgentHost agentHost) {
    this(message, cause);
    setHostNameFrom(agentHost);
  }


  public BuildException(final String message, final AgentHost agentHost) {
    super(message);
    setHostNameFrom(agentHost);
  }


  public BuildException(final Exception cause, final AgentHost agentHost) {
    super(cause);
    setHostNameFrom(agentHost);
  }


  public BuildException(final String message, final IOException cause, final AgentEnvironment agentEnvironment) {
    super(message, cause);
    setHostNameFrom(agentEnvironment);
  }


  private void setHostNameFrom(final AgentEnvironment agentEnvironment) {
    if (agentEnvironment != null) {
      this.hostName = agentEnvironment.getHost();
    }
  }


  public BuildException(final Exception cause, final Agent agent) {
    super(cause);
    setHostNameFrom(agent);
  }


  public BuildException(final String message, final AgentEnvironment agentEnvironment) {
    super(message);
    setHostNameFrom(agentEnvironment);
  }


  /**
   * Sets oprional log content
   *
   * @param logContent
   */
  public void setLogContent(final String logContent) {
    this.logContent = logContent;
  }


  /**
   * Sets optional log content
   */
  public void setLogContent(final StringBuffer logContent) {
    this.logContent = logContent.toString();
  }


  /**
   * Returns true if log content is present
   */
  public boolean hasLogContent() {
    return !StringUtils.isBlank(logContent);
  }


  /**
   * Returns log content
   */
  public String getLogContent() {
    return logContent;
  }


  /**
   * Returns true if the exception was reported
   *
   * @return true if the exception was reported
   */
  public boolean isReported() {
    return reported;
  }


  /**
   * Returns false if the exception was reported
   */
  public void setReported(final boolean reported) {
    this.reported = reported;
  }


  /**
   * Returns host name or null if host name is not set.
   *
   * @return host name or null if host name is not set.
   */
  public String getHostName() {
    return hostName;
  }


  /**
   * Helper method.
   *
   * @param agent agent.
   */
  private void setHostNameFrom(final Agent agent) {
    if (agent != null) {
      setHostNameFrom(agent.getHost());
    }
  }


  /**
   * Helper method.
   *
   * @param command command
   */
  private void setHostNameFrom(final RemoteCommand command) {
    if (command != null) {
      setHostNameFrom(command.getAgentHost());
    }
  }


  private void setHostNameFrom(final AgentHost agentHost) {
    if (agentHost != null) {
      this.hostName = agentHost.getHost();
    }
  }


  public void setHostName(final String hostName) {
    this.hostName = hostName;
  }
}
