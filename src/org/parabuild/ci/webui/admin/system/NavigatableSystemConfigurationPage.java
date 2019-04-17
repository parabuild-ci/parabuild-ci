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
package org.parabuild.ci.webui.admin.system;

import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;

/**
 * Base page for system configuration pages.
 */
public abstract class NavigatableSystemConfigurationPage extends AuthenticatedSystemConfigurationPage {

  private static final long serialVersionUID = -3496974032912031015L;
  private final Panel pnlRight = new Panel();
  private final LeftNavigationPanel pnlLeftNavigation = new LeftNavigationPanel();


  protected NavigatableSystemConfigurationPage() {
    this(FLAG_FLOATING_WIDTH | FLAG_SHOW_HEADER_SEPARATOR);
  }

  protected NavigatableSystemConfigurationPage(final int flags) {
    super(flags);

    // Create and set up left nav panel.
    pnlLeftNavigation.setWidth(180);

    // Create right panel with controls
    pnlRight.setWidth("95%");

    // Layout
    final Layout layout = new Layout(0, 0, 1, 1);
    baseContentPanel().getUserPanel().add(pnlLeftNavigation, layout);

    layout.positionX++;
    final Label lbVerticalSeparator = new Label(" ");
    lbVerticalSeparator.setWidth(15);
    baseContentPanel().getUserPanel().add(lbVerticalSeparator, layout);

    layout.positionX++;
    baseContentPanel().getUserPanel().add(pnlRight, layout);
  }

  protected final Panel getRightPanel() {
    return pnlRight;
  }

  /**
   * Lifecycle callback
   */
  public final Result executeAuthenticatedPage(final Parameters parameters) {
    return executeSystemConfigurationPage(parameters);
  }

  protected abstract Result executeSystemConfigurationPage(Parameters params);


  public String toString() {
    return "NavigatableSystemConfigurationPage{" +
            "pnlRight=" + pnlRight +
            ", pnlLeftNavigation=" + pnlLeftNavigation +
            '}';
  }
}
