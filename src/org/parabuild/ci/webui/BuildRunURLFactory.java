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

import org.parabuild.ci.object.BuildRun;

/**
 * This interface is used by {@link BuildRunHeaderPanel} to
 * create page-specific links to parallel build runs when
 * needed. For instance, if a user browses a log page then
 * links should point to a log page (if present) in other
 * build.
 *
 *
 */
public interface BuildRunURLFactory {

  /**
   * Creates a page-specific link to a parallel build run when
   * needed. For instance, if a user browses a log page then
   * links should point to a log page (if present) in other
   * build.
   */
  LinkURL makeLinkURL(final BuildRun buildRun);
}
