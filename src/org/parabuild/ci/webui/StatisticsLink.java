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

import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;

import java.util.Properties;

final class StatisticsLink extends CommonLink {

  public static final String PARAM_STAT_CODE = "sttscd";
  private static final long serialVersionUID = -7303561038581358644L;


  public StatisticsLink(final String caption) {
    super(caption, Pages.BUILD_STATISTICS);
  }


  public StatisticsLink(final String caption, final String selector, final int activeBuildID) {
    super(caption, Pages.BUILD_STATISTICS);
    setParameters(selector, activeBuildID);
  }


  public void setParameters(final String selector, final ActiveBuildConfig buildConfig) {
    setParameters(selector, buildConfig.getActiveBuildID());
  }


  public void setParameters(final String selector, final int activeBuildConfigID) {
    final Properties params = new Properties();
    params.setProperty(Pages.PARAM_BUILD_ID, Integer.toString(activeBuildConfigID));
    params.setProperty(PARAM_STAT_CODE, selector);
    super.setParameters(params);
  }
}
