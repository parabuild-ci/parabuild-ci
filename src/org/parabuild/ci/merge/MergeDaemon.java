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
package org.parabuild.ci.merge;

import EDU.oswego.cs.dl.util.concurrent.ClockDaemon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.merge.finder.MergeQueueUpdateRunner;
import org.parabuild.ci.merge.finder.MergeQueueUpdateRunnerImpl;
import org.parabuild.ci.merge.merger.MergeRunner;
import org.parabuild.ci.merge.merger.MergeRunnerImpl;
import org.parabuild.ci.merge.util.DaemonThreadFactory;
import org.parabuild.ci.object.ActiveMergeConfiguration;
import org.parabuild.ci.object.MergeServiceConfiguration;

/**
 * Value object to hold the Merge's queue producer daemon,
 * runner daemon and runner.
 */
public final class MergeDaemon {

  private static final Log log = LogFactory.getLog(MergeDaemon.class);

  private static final long POLL_INTERVAL_MS = 60L * 1000L;

  private final ClockDaemon queueDaemon = new ClockDaemon();
  private final ClockDaemon mergeDaemon = new ClockDaemon();
  private MergeQueueUpdateRunner queueRunner = null;
  private MergeRunner mergeRunner = null;
  private final int activeMergeConfigurationID;


  /**
   * Creates MergeDaemon
   * @param mergeConfiguration
   */
  public MergeDaemon(final ActiveMergeConfiguration mergeConfiguration) {
    activeMergeConfigurationID = mergeConfiguration.getID();
    queueDaemon.setThreadFactory(new DaemonThreadFactory("ChangeListFinder: " + mergeConfiguration.getName()));
    mergeDaemon.setThreadFactory(new DaemonThreadFactory("Merger: " + mergeConfiguration.getName()));
    if (MergeDAO.getInstance().getMergeServiceConfiguration(activeMergeConfigurationID).getStartupMode() == MergeServiceConfiguration.STARTUP_MODE_ACTIVE) {
      schedule();
    }
  }


  private void schedule() {
    queueRunner = new MergeQueueUpdateRunnerImpl(activeMergeConfigurationID);
    mergeRunner = new MergeRunnerImpl(activeMergeConfigurationID);
    queueDaemon.executePeriodically(POLL_INTERVAL_MS, queueRunner, true);
    mergeDaemon.executePeriodically(POLL_INTERVAL_MS, mergeRunner, true);
  }


  /**
   * Stops this merge daemon.
   *
   * This method stops change list finder and merger.
   */
  public void stop() {
    // REVIEWME: simeshev@parabuilci.org -> this way stop doesn't work.

    // REVIEWME: does it really permanently move the thread to "stopped" state?
    interruptThread(queueDaemon.getThread());
    interruptThread(mergeDaemon.getThread());
  }


  public static void interruptThread(final Thread thread) {
    if (thread != null) {
      thread.interrupt();
    }
  }


  /**
   * Resumes previously stopped this merge daemon.
   *
   * This method restarts change list finder and merger.
   */
  public void start() {
    schedule();
    this.queueDaemon.restart();
    this.mergeDaemon.restart();
  }


  public int getActiveMergeConfigurationID() {
    return activeMergeConfigurationID;
  }


  /**
   * Returns daemon status.
   *
   * Status is combined from statuses of the merge runner an queue updater.
   *
   * @return daemon status.
   */
  public MergeStatus getStatus() {

    // check daemon thread status
    final Thread th1 = queueDaemon.getThread();
    if (log.isDebugEnabled()) log.debug("th1: " + th1);
    if (th1 == null) return MergeStatus.PAUSED;
    if (th1.isInterrupted()) return MergeStatus.PAUSED;
    final Thread th2 = mergeDaemon.getThread();
    if (log.isDebugEnabled()) log.debug("th2: " + th2);
    if (th2 == null) return MergeStatus.PAUSED;
    if (th2.isInterrupted()) return MergeStatus.PAUSED;

    // check ruunner status
    final MergeStatus mergeRunnerStatus = mergeRunner.getStatus();
    if (!mergeRunnerStatus.equals(MergeStatus.IDLE)) return mergeRunnerStatus;
    return queueRunner.getStatus();
  }


  /** @noinspection ObjectToString*/
  public String toString() {
    return "MergeDaemon{" +
      "queueDaemon=" + queueDaemon +
      ", queueRunner=" + queueRunner +
      ", mergeDaemon=" + mergeDaemon +
      ", mergeRunner=" + mergeRunner +
      ", activeMergeConfigurationID=" + activeMergeConfigurationID +
      '}';
  }
}
