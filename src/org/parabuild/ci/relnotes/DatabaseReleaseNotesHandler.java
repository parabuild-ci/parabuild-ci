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

import java.sql.Date;
import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;

/**
 * This release notes handler processes release notes stored in
 * external dabasess.
 * <p/>
 * DatabaseReleaseNotesHandler delivers  common processing for
 * all issues stored in the databases while delegating the
 * details of connecting to database and retrieving bugs to
 * instances of IssueRetrievers.
 *
 * @see DatabaseIssueRetriever
 */
final class DatabaseReleaseNotesHandler implements ReleaseNotesHandler {

  private static final Log log = LogFactory.getLog(DatabaseReleaseNotesHandler.class);
  private final DatabaseIssueRetriever issueRetriever;


  /**
   * Constructor
   */
  public DatabaseReleaseNotesHandler(final DatabaseIssueRetriever issueRetriever) {
    this.issueRetriever = issueRetriever;
  }


  /**
   * This method finds and attaches release notes to the list of
   * pending issues.
   *
   * @param buildRun to find and attach release notes to.
   *
   * @return number of issues accepted and added to the list of
   *         pending issues.
   */
  public int process(final BuildRun buildRun) {
    int addedIssuesCounter = 0;
    try {
      // preExecute dates
      final long currentBuildRunStartedAt = buildRun.getStartedAt().getTime();
      final int activeBuildID = buildRun.getActiveBuildID();
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final BuildRun lastBuildRun = cm.getLastCompleteBuildRun(activeBuildID);
      Date fromDate = null;
      if (lastBuildRun != null) {
        // use last build start date
        fromDate = new Date(lastBuildRun.getStartedAt().getTime());
      } else {
        // use current build start date minus 1 day
        fromDate = new Date(currentBuildRunStartedAt - StringUtils.daysToMillis(1));
      }
      final Date toDate = new Date(currentBuildRunStartedAt);
      if (log.isDebugEnabled()) log.debug("fromDate: " + fromDate);
      if (log.isDebugEnabled()) log.debug("toDate: " + toDate);

      // get issues
      final List issues = issueRetriever.retrieveBugs(fromDate, toDate);
      if (log.isDebugEnabled()) log.debug("issues.size(): " + issues.size());

      addedIssuesCounter = issues.size();
      cm.saveIssuesAndAddToPendingList(activeBuildID, issues);
    } catch (Exception e) {
      final Error error = new Error(buildRun.getActiveBuildID(), "", Error.ERROR_SUSBSYSTEM_INTEGRATION, e);
      error.setSendEmail(false);
      error.setDescription("Error while processing processing change list to issue links: " + StringUtils.toString(e));
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
    }
    return addedIssuesCounter;
  }


  public String toString() {
    return "DatabaseReleaseNotesHandler{" +
      "issueRetriever=" + issueRetriever +
      '}';
  }
}
