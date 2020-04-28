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

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.ReleaseNote;

import java.util.Iterator;
import java.util.List;

/**
 * Copies over release notes from a previous build
 * if the previous build was not successful.
 */
final class PreviousBuildRunReleaseNotesHandler implements ReleaseNotesHandler {

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
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        // check if prev BR failed
        final BuildRun prevBuildRun = cm.getPreviousBuildRun(buildRun);
        if (prevBuildRun != null && prevBuildRun.getResultID() != BuildRun.BUILD_RESULT_SUCCESS) {
          // copy prev BR RN to the cureent one
          final List list = cm.getBuildRunReleaseNotes(prevBuildRun.getBuildRunID());
          for (final Iterator i = list.iterator(); i.hasNext();) {
            final ReleaseNote releaseNote = (ReleaseNote)i.next();
            session.evict(releaseNote);
            releaseNote.setID(ReleaseNote.UNSAVED_ID);
            releaseNote.setBuildRunID(buildRun.getBuildRunID());
            cm.saveObject(releaseNote);
          }
        }
        return null;
      }
    });
    return 0;
  }
}
