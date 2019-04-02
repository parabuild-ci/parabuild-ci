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
package org.parabuild.ci.webui.agent.status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.webui.common.Pages;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * AgentStatusImageServlet
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 24, 2009 7:34:08 PM
 */
public final class AgentStatusChartServlet extends HttpServlet {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(AgentStatusChartServlet.class); // NOPMD
  private static final long serialVersionUID = -7431763551265797664L;


  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    final String stringAgentID = req.getParameter(Pages.PARAM_AGENT_ID);
//    if (LOG.isDebugEnabled()) LOG.debug("stringAgentID: " + stringAgentID);

    // Validate ID
    if (!StringUtils.isValidInteger(stringAgentID)) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // Get status
    final AgentsStatusMonitor statusMonitor = ServiceManager.getInstance().getAgentStatusMonitor();
    final AgentStatus status = statusMonitor.getStatus(Integer.parseInt(stringAgentID));
//    if (LOG.isDebugEnabled()) LOG.debug("status: " + status);
    if (status == null) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    final ImmutableImage immutableImage = status.getChart();

    // Set response content type
    resp.setContentType("image/png");
    resp.setContentLength(immutableImage.getImageLegth());


    // Write image
    OutputStream out = null;
    try {
      out = new BufferedOutputStream(resp.getOutputStream());
      immutableImage.write(out);
      out.flush();
//      if (LOG.isDebugEnabled()) LOG.debug("Finished writing image");
    } finally {
      IoUtils.closeHard(out);
    }
  }
}
