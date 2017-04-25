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
 * Changes ownership of the certains after-install directories.
 */
public final class MacOsXDirectoryOwnerChanger extends AbstractUnixDirectoryOwnerChanger {

  public static final String PATH_TO_CHOWN = "/usr/sbin/chown";


  /**
   * @return path to chown command
   */
  protected String pathToChown() {
    return PATH_TO_CHOWN;
  }
}
