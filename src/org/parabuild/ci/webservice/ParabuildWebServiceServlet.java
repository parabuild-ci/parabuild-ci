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
package org.parabuild.ci.webservice;

import org.apache.axis.transport.http.AxisServlet;
import org.parabuild.ci.object.User;
import org.parabuild.ci.security.SecurityManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 */
public final class ParabuildWebServiceServlet extends AxisServlet {

  private static final long serialVersionUID = -3100366203394036650L;


  public void doGet(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
    final SecurityManager sm = SecurityManager.getInstance();
    final Principal userPrincipal = httpServletRequest.getUserPrincipal();
    final String name = userPrincipal.getName();
    final User userByName = sm.getUserByName(name);
    if (!userByName.isAdmin()) httpServletResponse.sendError(401);
    super.doGet(httpServletRequest, httpServletResponse);
  }


  public void doPost(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
    final SecurityManager instance = SecurityManager.getInstance();
    final Principal userPrincipal = httpServletRequest.getUserPrincipal();
    final String name = userPrincipal.getName();
    final User userByName = instance.getUserByName(name);
//    final RightSet userRights = instance.getUserRights();
    if (!userByName.isAdmin()) httpServletResponse.sendError(401);
    super.doPost(httpServletRequest, httpServletResponse);
  }
}
