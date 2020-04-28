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

import java.io.IOException;

/**
 * UndeployCommand
 * <p/>
 *
 * @author Slava Imeshev
 * @since May 24, 2009 12:04:50 PM
 */
public final class UndeployCommand extends AgentCommand {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log LOG = LogFactory.getLog(UndeployCommand.class); // NOPMD

  /**
   * The context path of the web application we are managing.
   */
  private String path;


  public String getPath() {
    return this.path;
  }


  public void setPath(final String path) {
    this.path = path;
  }

  /**
   * Execute the requested operation.
   *
   * @throws IOException if an error occurs
   */
  public void execute() throws IOException {
    super.execute();
    if (path == null) {
      throw new IllegalArgumentException("Must specify 'path' attribute");
    }
    execute("/undeploy?path=" + this.path);
  }


  public String toString() {
    return "UndeployCommand{" +
            "path='" + path + '\'' +
            '}';
  }
}
