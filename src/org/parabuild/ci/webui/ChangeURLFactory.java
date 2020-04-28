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
package org.parabuild.ci.webui;

import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SimpleChange;

/**
 * ChangeURLFactory defines an interaface that is capable of
 * URL-zing Change objects - That is it, capable of producing
 * clickable URLs out of file name names and revisions.
 *
 * @see ChangeURL
 */
public interface ChangeURLFactory {

  /**
   * Produces {@link ChangeURL} from the given change.
   *
   * @param change
   *
   * @return created {@link ChangeURL} object.
   */
  ChangeURL makeChangeFileURL(SimpleChange change);


  /**
   * Produces {@link ChangeURL} from the given change.
   *
   * @param change
   *
   * @return created {@link ChangeURL} object.
   */
  ChangeURL makeChangeRevisionURL(SimpleChange change);


  /**
   * Produces {@link ChangeURL} from the given change.
   *
   * @param changeList
   *
   * @return created {@link ChangeURL} object or null if URL
   *  creation for change lists is not supported.
   */
  ChangeURL makeChangeListNumberURL(ChangeList changeList);
}
