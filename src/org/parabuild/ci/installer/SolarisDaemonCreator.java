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

/**
 * Created by simeshev on Nov 2, 2004 at 4:33:42 PM
 */
public final class SolarisDaemonCreator extends AbstractUnixDaemonCreator {

  /**
   * Constructor.
   *
   * @param directoryOwnerChanger
   */
  public SolarisDaemonCreator(final DirectoryOwnerChanger directoryOwnerChanger) {
    super(directoryOwnerChanger);
  }


  /**
   * Returns os-spefic path to that daemon script will be
   * installed.
   *
   * @return s-spefic path to that daemon script will be
   *         installed
   */
  public String getDaemonScriptDestinationPath() {
    return "/etc/init.d";
  }


  /**
   * Returns path to run level dir where a link to a daemon
   * script will be created. Empty string if a level should not
   * be created.
   *
   * @param level
   * @return path to run level dir where a link to a daemon
   *         script will be created. Empty string if a level
   *         should not be created.
   */
  public String getRunLevelPath(final int level) {
    if (level == 2 || level == 3) {
      return "/etc/rc" + level + ".d";
    } else if (level == 4) {
      return ""; // no level 4
    } else if (level == 5) {
      return "/etc/rcS.d";
    } else {
      return ""; // no any other
    }
  }


  protected String getRelativePathToDaemonScript() {
    return "../init.d/parabuild";
  }
}
