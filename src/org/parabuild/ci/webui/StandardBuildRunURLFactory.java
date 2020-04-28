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
import org.parabuild.ci.webui.common.Pages;

/**
 *
 */
public class StandardBuildRunURLFactory implements BuildRunURLFactory {

  private final String page;


  /**
   * Cosntructor.
   *
   * @param page
   */
  public StandardBuildRunURLFactory(final String page) {
    this.page = page;
  }


  /**
   * Creates a page-specific link to a parallel build run when
   * needed. For instance, if a user browses a log page then
   * links should point to a log page (if present) in other
   * build.
   */
  public LinkURL makeLinkURL(final BuildRun buildRun) {
    return new LinkURL(page, Pages.PARAM_BUILD_RUN_ID, buildRun.getBuildRunIDAsString());
  }
}
