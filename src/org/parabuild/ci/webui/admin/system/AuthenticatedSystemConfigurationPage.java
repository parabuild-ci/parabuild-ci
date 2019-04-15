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

import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;

/**
 * Base page for system configuration pages.
 */
public abstract class AuthenticatedSystemConfigurationPage extends BasePage {

  private static final long serialVersionUID = 4375245896177329143L;


  protected AuthenticatedSystemConfigurationPage(final int flags) {
    super(flags);
    super.markTopMenuItemSelected(MENU_SELECTION_ADMINISTRATION);
  }


  protected AuthenticatedSystemConfigurationPage() {
  }


  /**
   * Lifecycle callback
   */
  public final Result executePage(final Parameters parameters) {

    // added this hack to handle cases when everything locks up after
    // setting up wrong encoding page.
    if (parameters.isParameterPresent("reset_code_page")) {
      final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
      final SystemProperty systemProperty = systemCM.getSystemProperty(SystemProperty.OUTPUT_ENCODING);
      systemProperty.setPropertyValue("");
      systemCM.saveSystemProperty(systemProperty);
      return Result.Done(Pages.ADMIN_SYSTEM_CONFIG_LINKS);
    }

    // Authenticate
    if (!isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.ADMIN_SYSTEM_CONFIG_LINKS, parameters);
    }

    // Authorise
    if (!isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    super.baseContentPanel().clearMessage();

    return executeAuthenticatedPage(parameters);
  }


  protected abstract Result executeAuthenticatedPage(Parameters params);
}