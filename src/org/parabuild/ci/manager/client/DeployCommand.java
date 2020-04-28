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
package org.parabuild.ci.manager.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * DeployCommand
 * <p/>
 *
 * @author Slava Imeshev
 * @since May 24, 2009 12:07:47 PM
 */
public final class DeployCommand extends AgentCommand {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log LOG = LogFactory.getLog(DeployCommand.class); // NOPMD

  /**
   * The context path of the web application we are managing.
   */
  private String path;


  /**
   * URL of the web application archive (WAR) file to be deployed.
   */
  private String war;


  public String getPath() {
    return this.path;
  }


  public void setPath(final String path) {
    this.path = path;
  }


  public String getWar() {
    return this.war;
  }


  public void setWar(final String war) {
    this.war = war;
  }

  /**
   * Execute the requested operation.
   *
   * @throws IOException if an error occurs
   */
  public void execute() throws IOException {

    super.execute();
    if (path == null) {
      throw new IOException("Must specify 'path' attribute");
    }
    if (war == null) {
      throw new IOException("Must specify 'war' attribute");
    }
    final URL url = new URL(war);
    final URLConnection conn = url.openConnection();
    final int contentLength = conn.getContentLength();
    final BufferedInputStream stream = new BufferedInputStream(conn.getInputStream(), 1024);
    execute("/deploy?path=" + URLEncoder.encode(this.path, "UTF-8"), stream, "application/octet-stream", contentLength);
  }


  public String toString() {
    return "DeployCommand{" +
            "path='" + path + '\'' +
            ", war='" + war + '\'' +
            '}';
  }
}
