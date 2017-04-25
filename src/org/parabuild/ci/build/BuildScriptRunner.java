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
import java.util.Collection;
import java.util.List;

import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.process.TimeoutCallback;

/**
 * Runs build script
 */
public interface BuildScriptRunner {

  /**
   * Executes build script
   */
  int executeBuildScript(String scriptFileName) throws BuildException, CommandStoppedException, AgentFailureException;


  /**
   * Sets merged output file
   */
  void setMergedFile(File merged);


  /**
   * Sets mandatory timeout handler
   *
   * @param timeoutCallback
   */
  void setTimeoutCallback(TimeoutCallback timeoutCallback);


  /**
   * Sets timedOutFlag
   */
  void markTimedOut();


  /**
   * @return true if build timed out
   */
  boolean isTimedOut();


  /**
   * Adds timeout matches
   *
   * @param list of string to match when looking for timed out processes
   */
  void addTimeoutMatches(Collection list);


  /**
   * Adds single timeout matche
   */
  void addTimeoutMatch(String match);


  /**
   * Sets script time out
   *
   * @param timeoutSecs
   */
  void setTimeoutSecs(int timeoutSecs);


  /**
   * Adds listeners {@link BuildScriptEventSubscriber} for script event.
   */
  void addScriptEventListeners(final List buildScriptEventSubscribers);
}
