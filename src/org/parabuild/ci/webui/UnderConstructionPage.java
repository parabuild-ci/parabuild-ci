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

import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * This class is a generic error page
 */
public final class UnderConstructionPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = -7595129326429164519L; // NOPMD


  public UnderConstructionPage() {
    setTitle(makeTitle("Requested functionality is not implemented yet"));
  }


  public Result executePage(final Parameters parameters) {
    baseContentPanel().getUserPanel().add(new BoldCommonLabel("This page is under construction"));
    return Result.Done();
  }
}
