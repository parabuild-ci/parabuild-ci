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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.process.RemoteCommand;
import org.parabuild.ci.process.TailBufferSizeImpl;
import org.parabuild.ci.process.TimeoutCallback;
import org.parabuild.ci.remote.Agent;

/**
 * Abstract build runner implements Stategy pattern.
 * <p/>
 * Concerete classes should implement makeCommand method.
 */
public abstract class AbstractBuildScriptRunner implements BuildScriptRunner {

  private boolean timedOut = false;
  private File mergedFile = null;
  private final List timeoutMatches = new ArrayList(5);
  private int timeoutSecs = 60 * 60;
  private List eventListenerList = new ArrayList(3);
  private TimeoutCallback timeoutCallback = null;
  protected Agent agent = null;


  protected AbstractBuildScriptRunner(final Agent agent) {
    this.agent = agent;
  }


  /**
   * Executes build script
   *
   * @param scriptFileName to execute
   */
  public final int executeBuildScript(final String scriptFileName) throws BuildException, CommandStoppedException, AgentFailureException {
    // validate
    if (timeoutMatches.isEmpty()) throw new IllegalStateException("Timeout matches are not set");
    if (timeoutCallback == null) throw new IllegalStateException("Timeout callback is not set");
    // execute
    RemoteCommand command = null;
    try {
      final String commandString = makeCommand(scriptFileName);
      // NOTE: we do not add PARABUILD_TIMESTAMP here so that we inherit
      // execution environemnt instead of overwriting it. 
      command = new RemoteCommand(agent, false);
      command.setCommand(commandString);
      command.setMergedFile(mergedFile);
      command.setTimeoutSecs(timeoutSecs);
      command.setTimeoutCallback(timeoutCallback);
      command.addSignatures(timeoutMatches);
      command.setMergedOutput(true);
      command.setHandle(agent.createCommandHandle());
      command.setLogTailSize(new TailBufferSizeImpl(ConfigurationConstants.TAIL_BUFFER_SIZE, 200));
      notifyScriptStarted(command.getHandle());
      return command.execute();
    } catch (final IOException e) {
      throw new BuildException("Error while building: " + StringUtils.toString(e), e, command);
    } finally {
      if (command != null) {
        notifyScriptFinished(command.getHandle());
        command.cleanup();
      }
    }
  }


  /**
   * Adds timeout matches
   *
   * @param list of string to match when looking for timed out
   *             processes
   */
  public final void addTimeoutMatches(final Collection list) {
    this.timeoutMatches.addAll(list);
  }


  /**
   * Adds single timeout matche
   */
  public final void addTimeoutMatch(final String match) {
    this.timeoutMatches.add(match);
  }


  /**
   * Adds listeners {@link BuildScriptEventSubscriber} for script event.
   */
  public void addScriptEventListeners(final List buildScriptEventSubscribers) {
    eventListenerList.addAll(buildScriptEventSubscribers);
  }


  /**
   * Sets script time out
   *
   * @param timeoutSecs
   */
  public final void setTimeoutSecs(final int timeoutSecs) {
    this.timeoutSecs = timeoutSecs;
  }


  /**
   * Sets timedOutFlag
   */
  public final void markTimedOut() {
    timedOut = true;
  }


  /**
   * @return true if build timed out
   */
  public final boolean isTimedOut() {
    return timedOut;
  }


  /**
   * Sets mandatory timeout handler
   *
   * @param timeoutCallback
   */
  public final void setTimeoutCallback(final TimeoutCallback timeoutCallback) {
    this.timeoutCallback = timeoutCallback;
  }


  /**
   * Sets merged output file
   */
  public final void setMergedFile(final File merged) {
    this.mergedFile = merged;
  }


  /**
   * Returns String command to execute
   *
   * @param scriptFileName
   */
  protected abstract String makeCommand(String scriptFileName) throws IOException, AgentFailureException;


  /**
   * Helper method.
   *
   * @param handle
   */
  private void notifyScriptStarted(final int handle) {
    ArgumentValidator.validateArgumentNotZero(handle, "command handle");
    for (int i = 0; i < eventListenerList.size(); i++) {
      ((BuildScriptEventSubscriber) eventListenerList.get(i)).scriptStarted(new BuildScriptStartedEventImpl(handle));
    }
  }


  /**
   * Helper method.
   *
   * @param handle
   */
  private void notifyScriptFinished(final int handle) {
    ArgumentValidator.validateArgumentNotZero(handle, "command handle");
    for (int i = 0; i < eventListenerList.size(); i++) {
      ((BuildScriptEventSubscriber) eventListenerList.get(i)).scriptFinished(new BuildScriptFinishedEventImpl(handle));
    }
  }


  public String toString() {
    return "AbstractBuildScriptRunner{" +
            "mergedFile=" + mergedFile +
            ", timeoutMatches=" + timeoutMatches +
            ", timeoutCallback=" + timeoutCallback +
            ", timedOut=" + timedOut +
            ", timeoutSecs=" + timeoutSecs +
            ", agent=" + agent +
            ", eventListenerList=" + eventListenerList +
            '}';
  }
}
