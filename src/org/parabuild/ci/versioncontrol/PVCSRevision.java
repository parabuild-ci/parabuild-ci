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
package org.parabuild.ci.versioncontrol;

/**
 * Value object to hold PVCS revison to label.
 */
final class PVCSRevision {

  private final String filePath;
  private final String revision;


  /**
   * @param filePath
   * @param revision
   */
  public PVCSRevision(final String filePath, final String revision) {
    this.filePath = filePath;
    this.revision = revision;
  }


  /**
   * @return file path
   */
  public String getFilePath() {
    return filePath;
  }


  /**
   * @return revision
   */
  public String getRevision() {
    return revision;
  }


  public String toString() {
    return "PVCSRevision{" +
      "filePath='" + filePath + '\'' +
      ", revision='" + revision + '\'' +
      '}';
  }
}
