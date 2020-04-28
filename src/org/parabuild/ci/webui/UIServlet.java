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

import org.parabuild.ci.configuration.ConfigurationManager;
import viewtier.dispatch.DispatcherServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class UIServlet extends DispatcherServlet {

  private static final long serialVersionUID = 6247201370637762943L; // NOPMD

  private static final String BUILDER_STATUS_PAGE = "/parabuild/builderstatus.htm";


  /**
   * This methed does preliminary processing to forward UI
   * requests to a agent status page if we are running in the
   * remote agent mode.
   */
  public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    if (ConfigurationManager.isBuilderMode() &&
      !request.getRequestURL().toString().toLowerCase().endsWith(BUILDER_STATUS_PAGE)) {
      response.sendRedirect(BUILDER_STATUS_PAGE);
    } else {
      super.service(request, response); // normal UI handling
    }
  }
}
