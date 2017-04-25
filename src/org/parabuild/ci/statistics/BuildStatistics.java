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

import java.io.*;

import org.parabuild.ci.object.*;


/**
 * This class holds build up-to-date stats
 */
public final class BuildStatistics implements Serializable {

  private static final long serialVersionUID = -5345515381643142925L; // NOPMD

  private int changeLists = 0;
  private int issues = 0;
  private int failedBuilds = 0;
  private int failedBuildsPercent = 0;
  private int successfulBuilds = 0;
  private int successfulBuildsPercent = 0;
  private int totalBuilds = 0;
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
   *
   * @param successfulBuilds
   * @param brokenBuilds
   * @param checkins
   */
  public BuildStatistics(final Integer successfulBuilds, final Integer brokenBuilds, final Integer checkins, final Integer issues) {
    this(successfulBuilds.intValue(), brokenBuilds.intValue(), checkins.intValue(), issues.intValue());
  }


  /**
   * Creates zero statistics object.
   */
  public BuildStatistics() {
  }


  public BuildStatistics(final int successfulBuilds, final int failedBuilds, final int checkins, final int isses) {
    this.failedBuilds = failedBuilds;
    this.changeLists = checkins;
    this.successfulBuilds = successfulBuilds;
    this.issues = isses;
  }


  public BuildStatistics(final int successfulBuilds, final int failedBuilds, final int checkins, final int isses, final int failedPercent, final int successfulBuildPercent, final int totalBuilds) {
    this.failedBuilds = failedBuilds;
    this.changeLists = checkins;
    this.successfulBuilds = successfulBuilds;
    this.issues = isses;
    this.failedBuildsPercent = failedPercent;
    this.totalBuilds = totalBuilds;
    this.successfulBuildsPercent = successfulBuildPercent;
    this.isDirty = false;
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
        if (totalBuilds == 0) {
          successfulBuildsPercent = 0;
        } else {
          successfulBuildsPercent = (successfulBuilds * 100) / totalBuilds;
        }
        this.failedBuildsPercent = 100 - successfulBuildsPercent; // to eliminate rounding errors so we have 100% summa
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


  /**
   * Factory method.
   *
   * @return
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
}
