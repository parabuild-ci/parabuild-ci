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

import org.parabuild.ci.common.RuntimeUtils;

/**
 *
 */
public final class UnixDaemonCreatorFactory {

  /**
   * Factory constructor.
   */
  private UnixDaemonCreatorFactory() {
  }


  public static UnixDaemonCreator getCreator() {

    if (RuntimeUtils.isUnix()) {

      final int systemType = RuntimeUtils.systemType();
      if (systemType == RuntimeUtils.SYSTEM_TYPE_SUNOS) {

        return new SolarisDaemonCreator(new UnixDirectoryOwnerChanger());
      } else if (systemType == RuntimeUtils.SYSTEM_TYPE_DEBIAN) {

        return new SolarisDaemonCreator(new UnixDirectoryOwnerChanger());
      } else if (systemType == RuntimeUtils.SYSTEM_TYPE_LINUX) {

        final String machType = RuntimeUtils.getEnvVariable("MACHTYPE");
        if (machType != null && machType.toLowerCase().indexOf("suse") >= 0) {

          return new SuseDaemonCreator(new UnixDirectoryOwnerChanger());
        } else {

          return new LinuxDaemonCreator(new UnixDirectoryOwnerChanger());
        }
      } else if (systemType == RuntimeUtils.SYSTEM_TYPE_MACOSX) {

        return new MacOsXDaemonCreator(new MacOsXUserCreator(), new MacOsXDirectoryOwnerChanger());
      }
    }
    return new DummyDaemonCreator();
  }
}
