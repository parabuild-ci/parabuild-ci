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

import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Parameters;

/**
 * This page is repsonsible for resuming a group of builds.
 */
public final class ResumeGroupPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = -4958811997344302265L; // NOPMD
  private static final String CAPTION_RESUMING_THE_BUILD = "Resuming a Group of Builds";


  /**
   * Constructor
   */
  public ResumeGroupPage() {
    super(FLAG_FLOATING_WIDTH | FLAG_SHOW_PAGE_HEADER_LABEL | FLAG_SHOW_HEADER_SEPARATOR);
    super.setPageHeaderAndTitle(CAPTION_RESUMING_THE_BUILD);
  }


  /**
   * Lifecycle callback
   */
  protected Result executePage(final Parameters parameters) {

    // Authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.ADMIN_RESUME_GROUP, parameters);
    }

    // Check if admin
    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    if (isNew()) {
      final ResumeGroupPanel resumeGroupPanel = new ResumeGroupPanel(parameters);
      baseContentPanel().getUserPanel().add(resumeGroupPanel);
    }
    return Result.Continue();
  }


  public String toString() {
    return "ResumeGroupPage{}";
  }
}