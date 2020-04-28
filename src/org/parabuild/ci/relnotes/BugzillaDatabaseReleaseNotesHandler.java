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
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.IssueTracker;

import java.sql.SQLException;

/**
 *
 */
final class BugzillaDatabaseReleaseNotesHandler implements ReleaseNotesHandler {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(BugzillaDatabaseReleaseNotesHandler.class); // NOPMD
  private final DatabaseReleaseNotesHandler delegate;


  /**
   * Constructor
   */
  public BugzillaDatabaseReleaseNotesHandler(final IssueTracker tracker) throws SQLException {
    delegate = new DatabaseReleaseNotesHandler(new BugzillaDatabaseIssueRetriever(tracker));
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
    return delegate.process(buildRun);
  }


  public String toString() {
    return "BugzillaDatabaseReleaseNotesHandler{" +
      "delegate=" + delegate +
      '}';
  }
}
