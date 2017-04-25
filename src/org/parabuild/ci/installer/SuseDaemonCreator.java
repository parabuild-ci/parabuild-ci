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
 * This class creates Linux daemon files.
 */
public final class SuseDaemonCreator extends AbstractUnixDaemonCreator {

  public SuseDaemonCreator(final DirectoryOwnerChanger directoryOwnerChanger) {
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
   * Returns a path to run level dir where a link to a daemon
   * script will be created.
   *
   * @param level the run level.
   * @return the path to run level dir where a link to a daemon
   *         script will be created.
   */
  public String getRunLevelPath(final int level) {
    return "/etc/init.d/rc" + level + ".d";
  }


  /**
   * A path used to create a symlink in runlevel dir.
   *
   * @return the path used to create a symlink in runlevel dir.
   * @see #getRunLevelPath(int)
   */
  protected String getRelativePathToDaemonScript() {
    return "../parabuild";
  }
}
