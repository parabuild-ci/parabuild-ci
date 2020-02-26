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
package org.parabuild.ci.manager.server;

import org.apache.catalina.manager.ManagerServlet;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.configuration.ConfigurationFile;
import org.parabuild.ci.configuration.SystemConstants;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AgentUpgradeServlet
 * <p/>
 *
 * @author Slava Imeshev
 * @since May 22, 2009 9:51:04 PM
 */
public final class AgentManagerServlet extends ManagerServlet {

  private static final long serialVersionUID = -3923303883456454357L;

  private static final String BUILD_MANAGER_ADDRESS = ConfigurationFile.getInstance().getBuildManagerAddress();
  private static final boolean DISABLE_SOURCE_IP_ADDRESS_CHECK = Boolean.getBoolean(System.getProperty(SystemConstants.SYSTEM_PROPERTY_SOURCE_IP_ADDRESS_CHECK_DISABLED, "false"));


  /**
   * Will accept requests only from build manager.
   */
  public void service(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {

    // Prohibit non-remote manager address
    final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    final String remoteAddr = httpServletRequest.getRemoteAddr();
    if (!DISABLE_SOURCE_IP_ADDRESS_CHECK && !remoteAddr.equals(BUILD_MANAGER_ADDRESS)) {
      final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
      httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
      IoUtils.closeHard(request.getInputStream());
      IoUtils.closeHard(response.getOutputStream());
      return;
    }

    // Execute normally
    super.service(request, response);
  }
}
