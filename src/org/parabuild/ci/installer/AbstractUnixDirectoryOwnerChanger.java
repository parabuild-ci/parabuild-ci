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
package org.parabuild.ci.installer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.NullOutputStream;
import org.parabuild.ci.common.RuntimeUtils;
import org.parabuild.ci.common.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA. User: vimeshev Date: Dec 17, 2005
 * Time: 1:19:14 AM To change this template use File | Settings |
 * File Templates.
 */
public abstract class AbstractUnixDirectoryOwnerChanger implements DirectoryOwnerChanger {

  private static final Log log = LogFactory.getLog(AbstractUnixDaemonCreator.class);
  /**
   * List of relative dir names.
   */
  private static final String[] AUTO_CHOWN_DIR_NAME_LIST = {
          "etc/data",
          "etc/index",
          "etc/msgs",
          "etc/temp",
          "etc/work",
          "etc/system",
          "logs",
          "etc/app/parabuild.war",
  };


  /**
   * Changes ownership of parabuild dirercories.
   */
  public final void changeOwner(final File installationDir, final String user) {
    try {

      // Go through the list of paths
      for (int i = 0, n = AUTO_CHOWN_DIR_NAME_LIST.length; i < n; i++) {
        changeOwner(user, IoUtils.getCanonicalPathHard(new File(installationDir, AUTO_CHOWN_DIR_NAME_LIST[i])));
      }
      
      // Optionally change the build dir ownership
      if ("yes".equalsIgnoreCase(System.getProperty("parabuild.chown.build.dirs", "yes"))) {
        changeOwner(user, IoUtils.getCanonicalPathHard(new File(installationDir, "etc/build")));
        changeOwner(user, IoUtils.getCanonicalPathHard(new File(installationDir, "etc/logs")));
      }
    } catch (final Exception e) {
      // TODO: log exceptions into install log.
      log.warn("Error while changing directory owner", e);
    }
  }


  private void changeOwner(final String user, final String canonDirPath) throws IOException, CommandStoppedException {
    final OutputStream stdout = new NullOutputStream();
    final OutputStream stderr = new NullOutputStream();
    if (StringUtils.isBlank(canonDirPath)) {
      return;
    }
    RuntimeUtils.execute(null, pathToChown() + " -f -R " + user + ' ' + canonDirPath, null, stdout, stderr);
  }


  /**
   * @return path to chown command
   */
  protected abstract String pathToChown();
}
