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
package org.parabuild.ci.versioncontrol.perforce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parsed P4 client view.
 */
public final class P4ClientView {

  private final List clientViewLineSet;
  private final String relativeBuildDir;


  /**
   * Constructor.
   *
   * @param relativeBuildDir
   */
  public P4ClientView(final String relativeBuildDir, final List clientViewLines) {
    this.relativeBuildDir = relativeBuildDir;
    this.clientViewLineSet = new ArrayList(clientViewLines);
  }


  /**
   * @return client view lines
   */
  public List getClientViewLines() {
    return Collections.unmodifiableList(this.clientViewLineSet);
  }


  /**
   * @return relative client path
   */
  public String getRelativeBuildDir() {
    return this.relativeBuildDir;
  }


  public int lineSetSize() {
    return this.clientViewLineSet.size();
  }


  public String toString() {
    return "P4ClientView{" +
      "clientViewLineSet=" + clientViewLineSet +
      ", relativeBuildDir='" + relativeBuildDir + '\'' +
      '}';
  }
}
