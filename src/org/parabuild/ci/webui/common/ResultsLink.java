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
package org.parabuild.ci.webui.common;

import java.util.*;

/**
 * Reusable class to show a link to the page contaning a build
 * run result.
 */
public class ResultsLink extends CommonLink {

  private static final String CAPTION_RESULTS = "Results";


  /**
   * Creates a link with the given caption and build run ID
   * parameter.
   *
   * @param caption
   * @param buildRunID
   */
  public ResultsLink(final String caption, final int buildRunID) {
    super(caption, Pages.BUILD_RESULTS, Pages.PARAM_BUILD_RUN_ID, buildRunID);
  }


  /**
   * Creates a link with the given build run ID parameter and caption "Results"
   *
   * @param buildRunID
   */
  public ResultsLink(final int buildRunID) {
    this(CAPTION_RESULTS, buildRunID);
  }


  public ResultsLink() {
    super("", Pages.BUILD_RESULTS);
  }


  /**
   * Populates this link with the given build run ID and sets
   * link caption.
   *
   * @param caption
   * @param buildRunID
   */
  public void setBuildRun(final String caption, final int buildRunID) {
    final Properties props = new Properties();
    props.setProperty(Pages.PARAM_BUILD_RUN_ID, Integer.toString(buildRunID));
    setParameters(props);
  }
}
