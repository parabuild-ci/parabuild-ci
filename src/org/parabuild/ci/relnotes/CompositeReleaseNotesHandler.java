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

import java.util.*;

import org.parabuild.ci.util.*;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.object.*;

/**
 * CompositeReleaseNotesHandler implements Composite pattern and
 * simply calls member ReleaseNotesHandlers.
 */
final class CompositeReleaseNotesHandler implements ReleaseNotesHandler {

  private final List handlers = new ArrayList(1);


  /**
   * Adds a ReleaseNotesHandler
   *
   * @param handler CompositeReleaseNotesHandler to add
   */
  public void add(final ReleaseNotesHandler handler) {
    handlers.add(handler);
  }


  /**
   * This method finds and attaches release notes to the given
   * build run.
   *
   * @param buildRun to find and attach release notes to.
   */
  public int process(final BuildRun buildRun) {
    int addedIssues = 0;
    for (int i = 0, n = handlers.size(); i < n; i++) {
      final ReleaseNotesHandler handler = (ReleaseNotesHandler)handlers.get(i);
      try {
        addedIssues += handler.process(buildRun);
      } catch (final Exception e) {
        final Error error = Error.newWarning(Error.ERROR_SUBSYSTEM_INTEGRATION);
        error.setBuildID(buildRun.getBuildID());
        error.setSendEmail(true);
        error.setDescription("Error while processing release notes: " + StringUtils.toString(e));
        error.setDetails(e);
      }
    }
    return addedIssues;
  }


  public String toString() {
    return "CompositeReleaseNotesHandler{" +
      "handlers=" + handlers +
      '}';
  }
}
