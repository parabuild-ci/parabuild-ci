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
package org.parabuild.ci.webui;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.services.BuildManager;

import java.util.List;

/**
 * Tests DetailedBuildStatusesPanel
 *
 * @see DetailedBuildStatusesPanel
 */
public class SSTestDetailedBuildStatusesPanel extends ServersideTestCase {

  private static final TailWindowActivator MOCK_TAIL_WINDOW_ACTIVATOR = new TailWindowActivator() {

    /**
     * Activates Javascript polling.
     *
     * @param activeBuildID
     * @param lastTimeStamp
     */
    public void activate(int activeBuildID, int tailWindowSize, final long lastTimeStamp) {
    }
  };


  /**
   * @see DetailedBuildStatusesPanel
   */
  public void test_create() {
    // check nothing happens with any parameter.
    DetailedBuildStatusesPanel buildStatusesPanel = null;
    int displayGroupID = -1;
    buildStatusesPanel = new DetailedBuildStatusesPanel(-1, displayGroupID, true, getCurrentBuildsStatuses(), MOCK_TAIL_WINDOW_ACTIVATOR);
    buildStatusesPanel = new DetailedBuildStatusesPanel(0, displayGroupID, false, getCurrentBuildsStatuses(), MOCK_TAIL_WINDOW_ACTIVATOR);
    buildStatusesPanel = new DetailedBuildStatusesPanel(1, displayGroupID, true, getCurrentBuildsStatuses(), MOCK_TAIL_WINDOW_ACTIVATOR);
    buildStatusesPanel = new DetailedBuildStatusesPanel(Integer.MAX_VALUE, displayGroupID, false, getCurrentBuildsStatuses(), MOCK_TAIL_WINDOW_ACTIVATOR);
  }


  private List getCurrentBuildsStatuses() {
    return BuildManager.getInstance().getCurrentBuildsStatuses();
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public SSTestDetailedBuildStatusesPanel(final String s) {
    super(s);
  }
}
