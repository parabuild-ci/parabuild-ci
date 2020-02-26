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
package org.parabuild.ci.build;

import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.util.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 * This functor is responsible for validating that there are no
 * duplicates in would-be generated build versions.
 */
public final class BuildVersionDuplicateValidator {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(BuildVersionDuplicateValidator.class); // NOPMD


  /**
   *
   * @param activeBuildID
   * @param versionTemplateToTest
   * @param buildName
   * @param versionCounterToTest
   * @throws ValidationException if a template is not valid or if a duplicate was found
   */
  public void validate(final int activeBuildID, final String versionTemplateToTest,
    final String buildName, final int versionCounterToTest
  ) throws ValidationException {
    validate(activeBuildID, versionTemplateToTest, buildName, versionCounterToTest, -1);
  }


  /**
   * Validates that a version does not exist.
   *
   * @param activeBuildID
   * @param versionTemplateToTest
   * @param buildName
   * @param versionCounterToTest
   * @param ignoreBuildRunID
   */
  public void validate(final int activeBuildID, final String versionTemplateToTest, final String buildName, final int versionCounterToTest, final int ignoreBuildRunID) throws ValidationException {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final int buildNumberToTest = cm.getNewBuildNumber(activeBuildID, false);
    validate(activeBuildID, versionTemplateToTest, buildName, buildNumberToTest, versionCounterToTest, ignoreBuildRunID);
  }


  /**
   * Validates that a version does not exist.
   *
   * @param activeBuildID
   * @param versionTemplateToTest
   * @param buildName
   * @param versionCounterToTest
   * @param ignoreBuildRunID
   */
  public void validate(final int activeBuildID, final String versionTemplateToTest, final String buildName, final int buildNumberToTest, final int versionCounterToTest, final int ignoreBuildRunID) throws ValidationException {

    if (versionCounterToTest < -1) throw new IllegalArgumentException("Version counter cannot be lesser than -1");

    // do duplicate validation only if version template is not blank
    if (StringUtils.isBlank(versionTemplateToTest)) return;

    // try to get a new would-be counter
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    int wouldBeNewVersionCounter = versionCounterToTest;
    if (wouldBeNewVersionCounter == -1) {
      // if auto mode generate would-be version counter
      if (cm.getBuildAttributeValue(activeBuildID, BuildConfigAttribute.VERSION_COUNTER_INCREMENT_MODE, new Integer(BuildConfigAttribute.VERSION_COUNTER_INCREMENT_MODE_MANUAL)).byteValue() == BuildConfigAttribute.VERSION_COUNTER_INCREMENT_MODE_AUTOMATIC)
      {
        wouldBeNewVersionCounter = cm.getNewVersionCounter(activeBuildID, false);
      }
    }

    // run validation if counter is ready
    if (wouldBeNewVersionCounter != -1) {

      // create version using current parameters
      final BuildVersionGenerator buildVersionGenerator = new BuildVersionGenerator();
      final String wouldBeVersion = buildVersionGenerator.makeBuildVersion(versionTemplateToTest, buildName, buildNumberToTest, wouldBeNewVersionCounter).toString();
      final List foundAttributes = cm.findBuildRunAttributes(activeBuildID, BuildRunAttribute.VERSION, wouldBeVersion);

      // check if anything found
      if (foundAttributes.isEmpty()) return;

      // check if this is ignoreable build run
      if (foundAttributes.size() == 1) {
        final BuildRunAttribute attr = (BuildRunAttribute)foundAttributes.get(0);
        if (attr.getBuildRunID() == ignoreBuildRunID) return;
      }

      // validation failed
      throw new ValidationException("Version \"" + wouldBeVersion + "\" already exists for this build configuration.");
    }
  }
}
