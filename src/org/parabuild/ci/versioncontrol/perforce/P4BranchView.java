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

/**
 * A Perforce Branch Specification
 */
public interface P4BranchView {

  /**
   * @return The branch name
   */
  String branch();


  /**
   * @return The date this specification was last modified.
   */
  String update();


  /**
   * @return The date of the last 'integrate' using this branch.
   */
  String access();


  /**
   * @return The user who created this branch.
   */
  String owner();


  /**
   * @return A short description of the branch (optional).
   */
  String description();


  /**
   * @return Branch update options: [un]locked [in]direct.
   */
  String options();


  /**
   * @return Lines to map source depot files to target depot files.
   */
  String view();
}
