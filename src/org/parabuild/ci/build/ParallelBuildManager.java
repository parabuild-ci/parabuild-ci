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

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.services.*;

/**
 * Manages starting and stopping of parallel builds
 */
abstract class ParallelBuildManager {

  /**
   * Factory method. Creates an implementation of the
   * ParallelBuildManager specific to a given build run.
   *
   * @param buildRun
   *
   * @return an implementation of the ParallelBuildManager
   *  specific to a given build run.
   */
  public static ParallelBuildManager newInstance(final BuildRun buildRun) {
    final ActiveBuildConfig activeBuildConfig = ConfigurationManager.getInstance().getActiveBuildConfig(buildRun.getActiveBuildID());
    final byte scheduleType = activeBuildConfig.getScheduleType();
    if (scheduleType == ActiveBuildConfig.SCHEDULE_TYPE_AUTOMATIC
      || scheduleType == ActiveBuildConfig.SCHEDULE_TYPE_MANUAL
      || scheduleType == ActiveBuildConfig.SCHEDULE_TYPE_RECURRENT) {
      return new ParallelBuildManagerImpl(buildRun);
    } else {
      return new DummyParallelBuildManager();
    }
  }


  /**
   * Requests dependent (lead) parallel builds to start.
   * Only builds called that are active at the moment when
   * this method is called.
   *
   * @param leadingStartRequest a request that was used to
   * @param changeListID
   */
  public abstract int startDependentBuilds(BuildStartRequest leadingStartRequest, final int changeListID);


  /**
   * Waits for dependent builds to stop. If this method has
   * already been called it has no effect and exits immediately.
   */
  public abstract void waitForDependentBuildsToStop() throws InterruptedException;


  public abstract int startedCount();


  /**
   * Does nothing. This class is used by the factory method
   * when a build run for that constrcution of the parallel
   * build manger is requested is not a leading build run.
   */
  private static class DummyParallelBuildManager extends ParallelBuildManager {

    public int startDependentBuilds(final BuildStartRequest leadingStartRequest, final int changeListID) {
      // do nothing
      return 0;
    }


    public void waitForDependentBuildsToStop() {
      // do nothing
    }


    public int startedCount() {
      // do nothing
      return 0;
    }
  }
}
