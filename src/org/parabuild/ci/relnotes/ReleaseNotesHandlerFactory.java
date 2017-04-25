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

import java.sql.*;
import java.util.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;

/**
 * ReleaseNotesHandlerFactory is used by BuildRunner to create
 * ReleaseNotesHandler responsible for getting release notes for
 * a given build run.
 * <p/>
 * Creates an instance of ReleaseNotesHandler reflecting build
 * congifiguration in part of release notes/issue tracking.
 *
 * @see BuildRun
 * @see ReleaseNotesHandler
 */
public final class ReleaseNotesHandlerFactory {

  /**
   * Private and the only constructor - ReleaseNotesHandlerFactory
   * shoul no be instantiated.
   */
  private ReleaseNotesHandlerFactory() {
  }


  /**
   * Creates an instance of ReleaseNotesHandler reflecting build
   * congifiguration in part of release notes/issue tracking.
   *
   * @see IssueTracker
   * @see IssueTrackerProperty
   * @see ReleaseNotesHandler
   * @see PendingReleaseNotesHandler
   * @see ChangeListToIssueLinker
   */
  public static ReleaseNotesHandler getHandler(final int currentRunConfigID) {
    final CompositeReleaseNotesHandler compositeHandler = new CompositeReleaseNotesHandler();
    final List linkingPatterns = new ArrayList(11);
    // add mandatory handler for prev builds.
    compositeHandler.add(new PreviousBuildRunReleaseNotesHandler());
    // add configured handlers - iterate list
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final List trackers = cm.getIssueTrackers(currentRunConfigID);
    for (final Iterator i = trackers.iterator(); i.hasNext();) {
      final IssueTracker tracker = (IssueTracker)i.next();
      final String linkingPattern = cm.getIssueTrackerPropertyValue(tracker.getID(), IssueTrackerProperty.ISSUE_LINK_PATTERN, null);
      if (linkingPattern != null) linkingPatterns.add(linkingPattern);
      switch (tracker.getType()) {
        case IssueTracker.TYPE_BUGZILLA_DIRECT:
          // add bugzilla RNH
          try {
            compositeHandler.add(new BugzillaDatabaseReleaseNotesHandler(tracker));
          } catch (SQLException e) {
            final Error error = new Error();
            error.setDescription("Error while adding release notes handler: " + StringUtils.toString(e));
            error.setBuildID(currentRunConfigID);
            error.setDetails(e);
            error.setErrorLevel(Error.ERROR_LEVEL_ERROR);
            ErrorManagerFactory.getErrorManager().reportSystemError(error);
          }
          break;
        case IssueTracker.TYPE_FOGBUGZ:
          break;
        case IssueTracker.TYPE_JIRA_LISTENER:
          // Jira is handled by PendingReleaseNotesHandler that is added later
          break;
        case IssueTracker.TYPE_PERFORCE:
          // PP4 jobs are fetched as a part of getChangesSince and
          // attached to changelists, so we use
          // ChangeListReleaseNotesHandler to put such jobs/issues to
          // the list of PendingIssues.
          compositeHandler.add(new ChangeListReleaseNotesHandler(tracker));
          break;
        default:
          break;
      }
    }
    // add mandatory handler for pending builds.
    compositeHandler.add(new PendingReleaseNotesHandler());
    // add mandatory linker
    // NOTE: the liker is faceless, i.e. all issues from possible
    // multiplel issue trackers will be matched againstr all patterns.
    // this potentially may create "fantom" links, i.e. links form issue
    // trackers that does not actually contain patterns. from the other
    // side, when a change list is submitted with an issue id in description
    // there is no way to know, which cofngured source of issues the issue
    // belongs too.
    compositeHandler.add(new ChangeListToIssueLinker(linkingPatterns));
    return compositeHandler;
  }
}
