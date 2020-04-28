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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.webui.BuildsStatusesPage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;

/**
 * Lists admin statuses
 */
public final class AdminStatusPage extends BuildsStatusesPage {

  private static final long serialVersionUID = -2472052514871569348L; // NOPMD


  public AdminStatusPage() {
    setTitle(makeTitle("Administrative build statuses"));
  }


  public Result executePage(final Parameters params) {
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(), Pages.PUBLIC_LOGIN, Pages.ADMIN_STATUS, params);
    }
    return super.executePage(params); // BuildsStatusesPage is admin aware.
  }
}
