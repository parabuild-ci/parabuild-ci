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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.security.SecurityManager;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This servlet implements GoF Strategy pattern. It provides
 * access to content of the Parabuild archive, like logs or build
 * results. AbstractArchiveAccessServlet incoreporates common
 * functionality and security.
 * <p/>
 * Concrete implementations should implement method
 * getArchiveInputStream.
 *
 * @see #getArchiveInputStream
 */
public abstract class AbstractArchiveAccessServlet extends HttpServlet {

  private static final long serialVersionUID = 7097552762277808738L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration,UnusedDeclaration */
  private static final Log log = LogFactory.getLog(AbstractArchiveAccessServlet.class); // NOPMD


  /**
   * doGet method.
   */
  protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    //if (log.isDebugEnabled()) log.debug("request.getServletPath(): " + request.getServletPath());
    //if (log.isDebugEnabled()) log.debug("request.getRequestURI(): " + request.getRequestURI());
    //if (log.isDebugEnabled()) log.debug("request.getContextPath(): " + request.getContextPath());
    //if (log.isDebugEnabled()) log.debug("request.getPathInfo(): " + request.getPathInfo());
    //if (log.isDebugEnabled()) log.debug("request.getPathTranslated(): " + request.getPathTranslated());
    //if (log.isDebugEnabled()) log.debug("request.getQueryString(): " + request.getQueryString());

    /*

    Query:

      http://localhost:8080/parabuild/build/log/html/index.htm?param=my_param

    Produces:

      request.getServletPath() = /parabuild/build/log/html
      request.getRequestURI() = /parabuild/build/log/html/index.htm
      request.getContextPath() =
      request.getPathInfo() = /index.htm
      request.getPathTranslated() = null
      request.getQueryString() = param=my_param

     */

    final int userIDFromRequest = SecurityManager.getInstance().getUserIDFromRequest(request);


    InputStream is = null;
    try {

      // get request path
      final String pathInfo = request.getPathInfo();
      if (StringUtils.isBlank(pathInfo) || !(pathInfo.charAt(0) == '/')) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      // get is
      is = getArchiveInputStream(userIDFromRequest, pathInfo);
      if (is == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      // output file
      response.setHeader("Content-Type", getMimeType(pathInfo));
      response.setHeader("Cache-control", "max-age=" + Integer.toString(60 * 60 * 24 * 30 * 12));
      final ServletOutputStream outputStream = response.getOutputStream();
      IoUtils.copyInputToOuputStream(new BufferedInputStream(is), outputStream);
    } finally {
      IoUtils.closeHard(is);
    }
  }


  protected abstract InputStream getArchiveInputStream(final int userID, String pathInfo);


  /**
   * Returns mime type for the given path
   *
   * @param downloadFilePath
   *
   */
  private String getMimeType(final String downloadFilePath) {
    String mimeType = getServletContext().getMimeType(downloadFilePath);
    if (StringUtils.isBlank(mimeType)) {
      mimeType = "application/octet-stream";
    }
    return mimeType;
  }
}
