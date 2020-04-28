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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuildAttribute;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.util.StringUtils;

/**
 *
 */
public final class NextBuildNumberResetter {

  private static final int DEFAULT_BUILD_NUMBER_GAP = 10;

  private int gap = 0;
  private int buildID = BuildConfig.UNSAVED_ID;
  private final ConfigurationManager cm = ConfigurationManager.getInstance();


  /**
   * Creates resetter with a given build number gap
   */
  public NextBuildNumberResetter(final int buildID, final int gap) {
    if (gap <= 1) throw new IllegalArgumentException("Build number gap should be greater than zero");
    this.gap = gap;
    this.buildID = buildID;
  }


  public NextBuildNumberResetter(final int buildID) {
    this(buildID, DEFAULT_BUILD_NUMBER_GAP); // default
  }


  public void validate(final String number) throws ValidationException {
    // pre-validate
    if (StringUtils.isBlank(number)) return;
    if (!StringUtils.isValidInteger(number)) {
      throw new ValidationException("Build number should be a valid integer");
    }

    if (buildID != BuildConfig.UNSAVED_ID) {
      // not a new build, do additional check
      final int intNumber = calculateNumberToStore(number);
      final int minNumber = calculateMinNumber();
      // validate if is a correct number
      if (intNumber < minNumber) {
        throw new ValidationException("Build number should be greater than " + minNumber);
      }
    }
  }


  /**
   * Validates that a build is ready for reset.
   */
  private void validateBuildIDReadyForReset() {
    if (buildID == BuildConfig.UNSAVED_ID) {
      throw new IllegalArgumentException("Build ID should be defined");
    }
  }


  /**
   * Resets build number to a given value. This should be a
   * validated number.
   */
  public void reset(final String number) throws ValidationException {
    if (StringUtils.isBlank(number)) return;
    validateBuildIDReadyForReset();
    validate(number);
    ActiveBuildAttribute toUpdate = currentNumber();
    if (toUpdate == null) {
      toUpdate = new ActiveBuildAttribute(buildID, ActiveBuildAttribute.BUILD_NUMBER_SEQUENCE, "0");
    }
    toUpdate.setPropertyValue(calculateNumberToStore(number));
    cm.saveObject(toUpdate);
  }


  /**
   * Returns currentBuildNumber
   *
   * @return BuildConfigAttribute corresponding current build
   *         number
   */
  public ActiveBuildAttribute currentNumber() {
    return cm.getActiveBuildAttribute(buildID, ActiveBuildAttribute.BUILD_NUMBER_SEQUENCE);
  }


  /**
   * Helper
   */
  private int calculateMinNumber() {
    final ActiveBuildAttribute currNumberAttr = currentNumber();
    final int curr = currNumberAttr == null ? 0 : currNumberAttr.getPropertyValueAsInteger();
    return curr + gap;
  }


  /**
   * Helper
   */
  private static int calculateNumberToStore(final String number) {
    return Integer.parseInt(number) - 1;
  }
}
