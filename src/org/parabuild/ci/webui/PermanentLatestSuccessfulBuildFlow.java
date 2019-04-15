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

import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;

/**
 * Flow to show a last clean build link with a caption and a
 * green square.
 */
public final class PermanentLatestSuccessfulBuildFlow extends Flow {

  private static final long serialVersionUID = 2722782667287585604L;


  /**
   * Constructor.
   *
   * @param activeBuildID
   */
  public PermanentLatestSuccessfulBuildFlow(final int activeBuildID) {
    add(WebuiUtils.makeGreenBulletSquareImage16x16());
    add(new CommonLabel(" Permalink: "));
    add(new PermanentLatestSuccessfulBuildLink(activeBuildID));
  }
}
