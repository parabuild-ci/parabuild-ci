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

import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This class is a admin support resources.
 */
public final class SupportLinksPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = 644441224120456213L; // NOPMD

  private static final String VIEWTIER_SUPPORT_HOME_URL = "http://www.parabuildci.org/support.htm";
  private static final String VIEWTIER_SUPPORT_HOME_CAPTION = "Parabuild CI technical support home";

  /**
   * Constructor.
   */
  public SupportLinksPage() {
    setTitle(makeTitle("Support Links"));
    final CommonLabel lbDocsHeader = new BoldCommonLabel("Support resources:");
    final OrderedList lstDocs = new OrderedList();

    final CommonLink lnkForumComment = new CommonLink(TechnicalSupportLink.SUPPORT_FORUM_CAPTION, TechnicalSupportLink.SUPPORT_FORUM_URL);
    final CommonLabel lbForumComment = new CommonLabel(" - Post your bug reports, suggestions and questions here.");
    lnkForumComment.setTarget("_blank");
    lstDocs.add(new CommonFlow(lnkForumComment, lbForumComment));

    final CommonLink lnkSupport = new CommonLink(VIEWTIER_SUPPORT_HOME_CAPTION, VIEWTIER_SUPPORT_HOME_URL);
    final CommonLabel lbSupportComment = new CommonLabel(" - general information on support options.");
    lnkForumComment.setTarget("_blank");
    lstDocs.add(new CommonFlow(lnkSupport, lbSupportComment));

    baseContentPanel().getUserPanel().add(lbDocsHeader);
    baseContentPanel().getUserPanel().add(lstDocs);
  }


  public Result executePage(final Parameters params) {
    return Result.Done();
  }
}

