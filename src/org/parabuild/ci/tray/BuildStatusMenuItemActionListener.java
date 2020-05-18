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
package org.parabuild.ci.tray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.remote.RemoteUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

final class BuildStatusMenuItemActionListener implements ActionListener {

  private static final Log log = LogFactory.getLog(BuildStatusMenuItemActionListener.class);

  private final JMenuItem menuItem;
  private final int activeBuildID;
  private final String hostPort;


  public BuildStatusMenuItemActionListener(final JMenuItem menuItem, final String hostPort, final int activeBuildID) {
    this.menuItem = menuItem;
    this.activeBuildID = activeBuildID;
    this.hostPort = hostPort;
  }


  public void actionPerformed(final ActionEvent event) {
    final JMenuItem source = (JMenuItem)event.getSource();
    if (log.isDebugEnabled()) log.debug("source: " + source);
    if (log.isDebugEnabled()) log.debug("menuItem: " + menuItem);
    if (source == menuItem) { // NOPMD - same object refrence check
      // compose a URL
      final String url = "http://" + RemoteUtils.normalizeHostPort(hostPort) + "/parabuild/index.htm?detview=true&buildid=" + activeBuildID;
      // launch a browser
      try {
        if (log.isDebugEnabled()) log.debug("url: " + url);
        final BrowserLauncher browserLauncher = new BrowserLauncher();
        browserLauncher.launchBrowser(url);
      } catch (final InterruptedException ignored) {
      } catch (final Exception e) {
        log.warn("Error while launching a browser", e);
      }
    }
  }
}
