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
package org.parabuild.ci.versioncontrol.perforce;

import org.parabuild.ci.configuration.ChangeListsAndIssues;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.versioncontrol.ExclusionPathFinder;

/**
 * This change driver is called by getChanges. It saves
 * the changes if any and remembers change list number.
 */
final class P4SavingChangeListDriver implements P4ChangeListDriver {

  private final String exclusionPaths;
  private final int activeBuildID;
  private int resultChangeListID = ChangeList.UNSAVED_ID;


  /**
   * Constrcutor.
   *
   * @param activeBuildID
   * @param exclusionPaths
   */
  P4SavingChangeListDriver(final int activeBuildID, final String exclusionPaths) {
    this.exclusionPaths = exclusionPaths;
    this.activeBuildID = activeBuildID;
  }


  public void process(final ChangeListsAndIssues changeListsAndIssuesAccumulator) {

    // validate that change lists contain not only exclusions
    if (new ExclusionPathFinder().onlyExclusionPathsPresentInChangeLists(changeListsAndIssuesAccumulator.getChangeLists(), exclusionPaths)) {
      return;
    }
    resultChangeListID = ConfigurationManager.getInstance().saveChangeListsAndIssues(activeBuildID, changeListsAndIssuesAccumulator);
  }


  public int getResultChangeListID() {
    return resultChangeListID;
  }


  public String toString() {
    return "P4SavingChangeListDriver{" +
      "exclusionPaths='" + exclusionPaths + '\'' +
      ", activeBuildID=" + activeBuildID +
      ", resultChangeListID=" + resultChangeListID +
      '}';
  }
}

