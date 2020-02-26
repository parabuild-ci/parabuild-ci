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
package org.parabuild.ci.versioncontrol;

import org.parabuild.ci.util.*;
import org.parabuild.ci.object.*;

/**
 * This clas is repsonsible for creating a command line argument
 * for {@link ClearCaseMkviewCommand}.
 */
final class ClearCaseStorageLocationArgumentFactory {

  private final boolean isWindows;
  private final int activeBuildID;


  /**
   * Constructor
   */
  public ClearCaseStorageLocationArgumentFactory(final int activeBuildID, final boolean isWindows) {
    this.isWindows = isWindows;
    this.activeBuildID = activeBuildID;
  }


  public final String makeStorageLocationArgument(final byte storageLocationMode, final String storageLocation) throws BuildException {
    switch (storageLocationMode) {
      case SourceControlSetting.CLEARCASE_STORAGE_CODE_STGLOC:
        if (StringUtils.isBlank(storageLocation)) return ""; // let ClearCase decide
        return " -stgloc " + normalizeStorageLocation(storageLocation);
        //break;
      case SourceControlSetting.CLEARCASE_STORAGE_CODE_VWS:
        if (StringUtils.isBlank(storageLocation)) return ""; // nothing, same as -stgloc auto
        return " -vws " + normalizeStorageLocation(storageLocation);
        //break;
      default:
        return ""; // let ClearCase decide
    }
  }


  /**
   * Processes optional template and puts result into quoutes if Windows.
   */
  private String normalizeStorageLocation(final String storageLocation) throws BuildException {
    final String processedStorageLocation = new ClearCaseStorageNameGenerator(activeBuildID, storageLocation).generate();
    return isWindows ? StringUtils.putIntoDoubleQuotes(processedStorageLocation) : processedStorageLocation;
  }
}
