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

import java.io.IOException;
import java.lang.reflect.Method;

import static org.parabuild.ci.util.IoUtils.createIOException;
import static org.parabuild.ci.util.RuntimeUtils.SYSTEM_TYPE_MACOSX;
import static org.parabuild.ci.util.RuntimeUtils.SYSTEM_TYPE_WIN95;
import static org.parabuild.ci.util.RuntimeUtils.SYSTEM_TYPE_WINNT;
import static org.parabuild.ci.util.RuntimeUtils.execute;
import static org.parabuild.ci.util.RuntimeUtils.systemType;

/**
 * This class is responcible for launching a default Web browser
 * in a platform-independent way.
 */
final class BrowserLauncher {

//  private static final String[] UNIX_BROWSERS = new String[]{"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};


  public void launchBrowser(final String url) throws IOException, InterruptedException {

    try {
      final int systemType = systemType();
      switch (systemType) {
        case SYSTEM_TYPE_WIN95:
        case SYSTEM_TYPE_WINNT:
          execute("rundll32 url.dll,FileProtocolHandler " + url);
          break;
        case SYSTEM_TYPE_MACOSX:
          final Class fileMgr = Class.forName("com.apple.eio.FileManager");
          final Method openURL = fileMgr.getDeclaredMethod("openURL", String.class);
          openURL.invoke(null, url);
          break;
        default: // *nix
          //String browser = null;
          //for (int count = 0; count < UNIX_BROWSERS.length && browser == null; count++) {
          //  if (RuntimeUtils.execute(null, "which " + UNIX_BROWSERS[count], null, ) {
          //    browser = UNIX_BROWSERS[count];
          //  }
          //}
          //if (browser == null) {
          //  throw new IOException("Could not find web browser");
          //} else {
          //  Runtime.getRuntime().exec(new String[]{browser, url});
          //}
          break;
      }
    } catch (final InterruptedException | IOException e) {
      throw e;
    } catch (final Exception e) {
      throw createIOException(e);
    }
  }
}
