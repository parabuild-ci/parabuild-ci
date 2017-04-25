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

import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 *
 */
public final class ProcessListPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = -4758150699291034504L; // NOPMD
  // private static final Log log = LogFactory.getLog(ProcessListPage.class);


  private final ProcessListTable processListTable = new ProcessListTable(false);


  public ProcessListPage() {
    setTitle(makeTitle("Build manager host process list"));
    baseContentPanel().getUserPanel().add(processListTable);
  }


  public Result executePage(final Parameters params) {
    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.ADMIN_PROCESS_LIST, params);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // populate
    processListTable.populate();
    return Result.Done();
  }
}

