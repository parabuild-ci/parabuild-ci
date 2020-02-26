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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.Issue;
import org.parabuild.ci.object.IssueTracker;
import org.parabuild.ci.object.IssueTrackerProperty;
import org.parabuild.ci.object.PendingIssue;
import org.parabuild.ci.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This implmenetation of ReleaseNotesHandler handles issues
 * attached to change lists. It should be called before the
 * PendingReleaseNotesHandler.
 *
 * @see ReleaseNotesHandler
 * @see ReleaseNotesHandlerFactory
 * @see PendingReleaseNotesHandler
 * @see Issue
 * @see PendingIssue
 * @see BuildRun
 */
final class ChangeListReleaseNotesHandler implements ReleaseNotesHandler {

  private static final Log log = LogFactory.getLog(ChangeListReleaseNotesHandler.class);

  private IssueTracker tracker = null;


  /**
   * Constrctor
   */
  public ChangeListReleaseNotesHandler(final IssueTracker tracker) {
    this.tracker = tracker;
  }


  /**
   * This method finds and attaches release notes to the given
   * build run pending issues release
   *
   * @param buildRun to find and attach release notes to.
   */
  public int process(final BuildRun buildRun) {
    int addedPendingIssues = 0;
    try {
      if (log.isDebugEnabled()) log.debug("======= ChangeListReleaseNotesHandler =============");
      if (log.isDebugEnabled()) log.debug("buildRun.getBuildRunID(): " + buildRun.getBuildRunID());
      // REVIEWME: we may put it into run in hebernate wrapper.
      final ConfigurationManager cm = ConfigurationManager.getInstance();

      // put issues attached to this build run change lists to pending list
      final List buildRunChangeLists = cm.getBuildRunParticipants(buildRun);
      if (log.isDebugEnabled()) log.debug("buildRunChangeLists.size(): " + buildRunChangeLists.size());
      final Map map = cm.getIssueTrackerPropertiesAsMap(tracker.getID());
      final IssueTrackerProperty patternProperty = (IssueTrackerProperty)map.get(IssueTrackerProperty.ISSUE_FILTER);
      final String pattern = patternProperty != null ? patternProperty.getValue() : null;
      final IssueDescriptionFilter descriptionFilter = new IssueDescriptionFilter(pattern);

      // iterate change lists
      for (final Iterator changeListIter = buildRunChangeLists.iterator(); changeListIter.hasNext();) {
        final ChangeList changeList = (ChangeList)changeListIter.next();
        if (log.isDebugEnabled()) log.debug("changeList.getChangeListID(): " + changeList.getChangeListID());
        final List changeListIssues = cm.getChangeListIssues(changeList.getChangeListID());
        if (log.isDebugEnabled()) log.debug("changeListIssues.size(): " + changeListIssues.size());

        // iterate issues
        for (final Iterator issueIter = changeListIssues.iterator(); issueIter.hasNext();) {
          final Issue issue = (Issue)issueIter.next();

          // filter
          if (descriptionFilter.filter(issue.getDescription()) != null) {
            cm.savePendingIssue(new PendingIssue(buildRun.getActiveBuildID(), issue.getID()));
            addedPendingIssues++;
          }
        }
      }
    } catch (final Exception e) {
      final Error error = Error.newWarning(Error.ERROR_SUBSYSTEM_INTEGRATION);
      error.setDescription("Error while processing change list issues: " + StringUtils.toString(e));
      error.setSendEmail(false);
      error.setBuildName(buildRun.getBuildName());
      error.setDetails(e);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
    }
    return addedPendingIssues;
  }


  public String toString() {
    return "ChangeListReleaseNotesHandler{" +
      "tracker=" + tracker +
      '}';
  }
}
