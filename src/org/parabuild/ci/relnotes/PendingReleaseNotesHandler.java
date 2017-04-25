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
package org.parabuild.ci.relnotes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.Issue;
import org.parabuild.ci.object.PendingIssue;

/**
 * This implmenetation of ReleaseNotesHandler attaches Issues
 * panding for the given build id to a given build run. Pending
 * issues list gets cleared.
 *
 * @see ReleaseNotesHandler
 * @see ReleaseNotesHandlerFactory
 * @see Issue
 * @see PendingIssue
 * @see BuildRun
 */
final class PendingReleaseNotesHandler implements ReleaseNotesHandler {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(PendingReleaseNotesHandler.class); // NOPMD


  /**
   * This method finds and attaches release notes to the given
   * build run.
   *
   * @param buildRun to find and attach release notes to.
   * @return always returns zero as it moves issues <b>from</b>
   *         pending list, opposed to the description of
   *         ReleaseNotesHandler <code>process</code>.
   * @see ReleaseNotesHandler#process
   */
  public int process(final BuildRun buildRun) {
    try {
      return ConfigurationManager.getInstance().attachPendingIssuesToBuildRun(buildRun.getActiveBuildID(), buildRun.getBuildRunID()).intValue();
    } catch (Exception e) {
      final Error error = new Error(buildRun.getActiveBuildID(), "Error while processing pending release notes: " + StringUtils.toString(e), Error.ERROR_LEVEL_WARNING);
      error.setDetails(e);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
    }
    return 0;
  }
}
