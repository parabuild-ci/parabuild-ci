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
package org.parabuild.ci.tray;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import org.apache.commons.logging.*;

import com.caucho.hessian.server.*;
import org.parabuild.ci.build.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.SecurityManager;
/**
 *
 */
public final class BuildStatusServlet extends HessianServlet implements BuildStatusService {

  private static final long serialVersionUID = -194369566029085673L; // NOPMD

    /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(BuildStatusServlet.class); //NOPMD


  public void service(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
    super.service(servletRequest, servletResponse);
  }


  /**
   * @return List of {@link BuildStatus} objects.
   */
  public List getBuildStatusList() {
    final List result = new ArrayList(11);
    final int userID = -1; // fix me
    // get user build statuses
    final SecurityManager securityManager = SecurityManager.getInstance();
    final List userBuildStatuses = securityManager.getFeedBuildStatuses(userID);
    for (final Iterator i = userBuildStatuses.iterator(); i.hasNext();) {
      final BuildState state = (BuildState)i.next();
      final BuildStatus status = new BuildStatus();
      status.setBuildName(state.getBuildName());
      status.setActiveBuildID(state.getActiveBuildID());
      status.setBuildStatusID(state.getStatus().byteValue());
      status.setCurrentlyRunnigBuildRunID(state.getCurrentlyRunningBuildRunID());
      status.setCurrentlyRunningBuildConfigID(state.getCurrentlyRunningBuildConfigID());
      status.setCurrentlyRunningStepID(state.getCurrentlyRunnigSequenceID());
      status.setCurrentlyRunningStepName(state.getCurrentlyRunningStepName());
      status.setLastBuildRunID(state.getLastBuildRunID());
      final BuildRun lastCompleteBuildRun = state.getLastCompleteBuildRun();
      if (lastCompleteBuildRun != null) {
        status.setLastBuildRunNumber(lastCompleteBuildRun.getBuildRunNumber());
        status.setLastBuildRunResultID(lastCompleteBuildRun.getResultID());
        status.setLastCompleteBuildRunID(lastCompleteBuildRun.getBuildRunID());
        // ???
        // status.setLastCompleteBuildRun();
      }
      final BuildRun lastCleanBuildRun = state.getLastCleanBuildRun();
      if (lastCleanBuildRun != null) {
        status.setLastCleanBuildRunID(lastCleanBuildRun.getBuildRunID());
      }
      status.setSchedule((byte)state.getSchedule());
      status.setSourceControl((byte)state.getSourceControl());
      result.add(status);
    }
    return result;
  }
}
