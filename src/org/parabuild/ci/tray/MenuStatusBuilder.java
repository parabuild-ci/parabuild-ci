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
package org.parabuild.ci.tray;

/**
 * Makes MenuStatus from buildStatus
 */
final class MenuStatusBuilder {

  private static final String IMAGE_FAILED = TrayImageResourceCollection.IMAGE_FAILED;
  private static final String IMAGE_INACTIVE = TrayImageResourceCollection.IMAGE_INACTIVE;
  private static final String IMAGE_NOT_RUN_YET = TrayImageResourceCollection.IMAGE_NOT_RUN_YET;
  private static final String IMAGE_RUNNING_FAILED = TrayImageResourceCollection.IMAGE_RUNNING_FAILED;
  private static final String IMAGE_RUNNING_NOT_RUN_YET = TrayImageResourceCollection.IMAGE_RUNNING_NOT_RUN_YET;
  private static final String IMAGE_RUNNING_WAS_SUCCESSFUL = TrayImageResourceCollection.IMAGE_RUNNING_WAS_SUCCESSFUL;
  private static final String IMAGE_SUCCESSFUL = TrayImageResourceCollection.IMAGE_SUCCESSFUL;


  public MenuStatus makeMenuStatus(final BuildStatus buildStatus) {

    // prepare remoteStatus components
    final String lastBuildRunNumberString = Integer.toString(buildStatus.getLastBuildRunNumber());
    final String buildName = buildStatus.getBuildName();
    final boolean lastBuildRunIsSuccessful = buildStatus.getLastBuildRunResultID() == 1;
    final boolean isRunning = buildStatus.getCurrentlyRunnigBuildRunID() != -1;
    final boolean active = !buildStatus.isInactive();

    // compose image and caption
    String imageName = null;
    String caption = null;
    if (buildStatus.getLastCompleteBuildRunID() == -1) {
      caption = buildName;
      imageName = imageNameSwitch(isRunning, IMAGE_RUNNING_NOT_RUN_YET, active ? IMAGE_NOT_RUN_YET : IMAGE_INACTIVE);
    } else if (lastBuildRunIsSuccessful) {
      caption = buildName + '#' + lastBuildRunNumberString;
      imageName = imageNameSwitch(isRunning, IMAGE_RUNNING_WAS_SUCCESSFUL, active ? IMAGE_SUCCESSFUL : IMAGE_INACTIVE);
    } else {
      caption = buildName + '#' + lastBuildRunNumberString;
      imageName = imageNameSwitch(isRunning, IMAGE_RUNNING_FAILED, active ? IMAGE_FAILED : IMAGE_INACTIVE);
    }

    return new MenuStatus(imageName, caption);
  }


  /**
   * Helper method to switch image names based on the
   * selector.
   *
   * @param selector
   * @param imageNameTrue returned if selector is true
   * @param imageNameFalse returned if selector is false
   */
  private static String imageNameSwitch(final boolean selector, final String imageNameTrue, final String imageNameFalse) {
    if (selector) {
      return imageNameTrue;
    } else {
      return imageNameFalse;
    }
  }
}
