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
package org.parabuild.ci.configuration;

import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.object.User;

/**
 * Verbial build result.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Dec 1, 2008 8:22:04 PM
 */
public final class VerbialBuildResult {

  /**
   * Returns verbial build result.
   *
   * @param stepRun step run for that to return the result.
   * @return verbial build result.
   */
  public String getVerbialResultString(final StepRun stepRun) {
    if (ConfigurationManager.getInstance().stepFixedPreviousBreakage(stepRun)) {
      return "was FIXED";
    } else {
      return getVerbialResult(stepRun.getBuildRunID(), stepRun.getResultID());
    }
  }


  /**
   * Returns verbial build result.
   *
   * @param buildRun step run for that to return the result.
   * @return verbial build result.
   */
  public String getVerbialResultString(final BuildRun buildRun) {
    return getVerbialResult(buildRun.getBuildRunID(), buildRun.getResultID());
  }


  private static String getVerbialResult(final int buildRunID, final byte resultID) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    if (resultID == BuildRun.BUILD_RESULT_STOPPED) {
      final int userStoppedBuildID = cm.getBuildRunAttributeValue(buildRunID, BuildRunAttribute.STOPPED_BY_USER_ID, User.UNSAVED_ID);
      if (userStoppedBuildID == User.UNSAVED_ID) {
        return BuildRun.buildResultToVerbialString(resultID);
      } else {
        return "was STOPPED by " + org.parabuild.ci.security.SecurityManager.getInstance().getUserName(userStoppedBuildID, "administrator");
      }
    } else {
      return BuildRun.buildResultToVerbialString(resultID);
    }
  }
}
