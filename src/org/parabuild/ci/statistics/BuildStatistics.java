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
package org.parabuild.ci.statistics;

import org.parabuild.ci.object.PersistentBuildStats;

import java.io.Serializable;


/**
 * This class holds build up-to-date stats
 */
public final class BuildStatistics implements Serializable {

  private static final long serialVersionUID = -5345515381643142925L; // NOPMD

  private int changeLists;
  private int issues;
  private int failedBuilds;
  private int failedBuildsPercent;
  private int successfulBuilds;
  private int successfulBuildsPercent;
  private int totalBuilds;
  private boolean isDirty = true;


  /**
   * Copy constructor.
   */
  public BuildStatistics(final BuildStatistics source) {
    changeLists = source.changeLists;
    issues = source.issues;
    failedBuilds = source.failedBuilds;
    successfulBuilds = source.successfulBuilds;
    totalBuilds = source.totalBuilds;
    failedBuildsPercent = source.failedBuildsPercent;
    successfulBuildsPercent = source.successfulBuildsPercent;
    isDirty = source.isDirty;
  }


  /**
   * Constructor.
   */
  public BuildStatistics(final Integer successfulBuilds, final Integer brokenBuilds, final Integer checkins, final Integer issues) {
    this(successfulBuilds.intValue(), brokenBuilds.intValue(), checkins.intValue(), issues.intValue());
  }


  /**
   * Creates zero statistics object.
   */
  public BuildStatistics() {
  }


  public BuildStatistics(final int successfulBuilds, final int failedBuilds, final int checkins, final int issues) {
    this.failedBuilds = failedBuilds;
    this.changeLists = checkins;
    this.successfulBuilds = successfulBuilds;
    this.issues = issues;
  }


  private BuildStatistics(final int successfulBuilds, final int failedBuilds, final int checkins, final int issues, final int failedPercent, final int successfulBuildPercent, final int totalBuilds) {
    this.failedBuilds = failedBuilds;
    this.changeLists = checkins;
    this.successfulBuilds = successfulBuilds;
    this.issues = issues;
    this.failedBuildsPercent = failedPercent;
    this.totalBuilds = totalBuilds;
    this.successfulBuildsPercent = successfulBuildPercent;
    this.isDirty = false;
  }


  /**
   * Factory method.
   */
  public static BuildStatistics newInstance(final PersistentBuildStats pStats) {
    return new BuildStatistics(pStats.getSuccessfulBuildCount(),
            pStats.getFailedBuildCount(),
            pStats.getChangeListCount(),
            pStats.getIssueCount(),
            pStats.getFailedBuildPercent(),
            pStats.getSuccessfulBuildPercent(),
            pStats.getTotalBuildCount());
  }


  public int getFailedBuilds() {
    recalculateIfNecessary();
    return failedBuilds;
  }


  public int getFailedBuildsPercent() {
    recalculateIfNecessary();
    return failedBuildsPercent;
  }


  /**
   * @return number of "new" change lists in this build run.
   */
  public int getChangeLists() {
    recalculateIfNecessary();
    return changeLists;
  }


  public int getIssues() {
    recalculateIfNecessary();
    return issues;
  }


  public int getSuccessfulBuilds() {
    recalculateIfNecessary();
    return successfulBuilds;
  }


  public int getSuccessfulBuildsPercent() {
    recalculateIfNecessary();
    return successfulBuildsPercent;
  }


  public int getTotalBuilds() {
    recalculateIfNecessary();
    return totalBuilds;
  }


  public void addFailedBuilds(final int count) {
    failedBuilds += count;
    isDirty = true;
  }


  public void addSuccessfulBuilds(final int count) {
    successfulBuilds += count;
    isDirty = true;
  }


  public void addChangeLists(final int count) {
    changeLists += count;
    isDirty = true;
  }


  public void addIssues(final int count) {
    issues += count;
    isDirty = true;
  }


  private void recalculateIfNecessary() {
    if (isDirty) {
      this.totalBuilds = successfulBuilds + failedBuilds;
      if (totalBuilds > 0) {
        successfulBuildsPercent = (successfulBuilds * 100) / totalBuilds;
        failedBuildsPercent = 100 - successfulBuildsPercent; // to eliminate rounding errors so we have 100% sum
      } else {
        successfulBuildsPercent = 0;
        failedBuildsPercent = 0;
      }
      isDirty = false;
    }
  }


  public String toString() {
    return "BuildStatistics{" +
            "changeLists=" + changeLists +
            ", issues=" + issues +
            ", failedBuilds=" + failedBuilds +
            ", failedBuildsPercent=" + failedBuildsPercent +
            ", successfulBuilds=" + successfulBuilds +
            ", successfulBuildsPercent=" + successfulBuildsPercent +
            ", totalBuilds=" + totalBuilds +
            ", isDirty=" + isDirty +
            '}';
  }
}
