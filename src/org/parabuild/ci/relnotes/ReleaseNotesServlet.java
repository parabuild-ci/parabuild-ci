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
package org.parabuild.ci.relnotes;

import org.parabuild.ci.configuration.ConfigurationManager;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This sevlet works as an integration front for incoming release
 * notes notifications. Such noticications come in form of POST
 * requests conforming our "push" protocol.
 */
public final class ReleaseNotesServlet extends HttpServlet {

  private static final long serialVersionUID = 6247201370637762943L; // NOPMD


  /**
   * We accept only Post requests.
   */
  protected void doPost(final HttpServletRequest request, final HttpServletResponse response) {

    // additional check - don't server release notes requests if in the agent mode.
    if (ConfigurationManager.isBuilderMode()) return;

  }
}
