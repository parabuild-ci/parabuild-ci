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
package org.parabuild.ci.merge.finder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.merge.MergeStatus;

/**
 * Implments runable to be executed periodically by ChangeListFinderDaemon.
 */
public final class MergeQueueUpdateRunnerImpl implements MergeQueueUpdateRunner {

  private static final Log log = LogFactory.getLog(MergeQueueUpdateRunnerImpl.class);

  private final int activeMergeID;
  private MergeStatus status;


  public MergeQueueUpdateRunnerImpl(final int activeMergeID) {
    this.activeMergeID = activeMergeID;
    this.status = MergeStatus.IDLE;
  }


  /**
   * Executed change list finder.
   */
  public void run() {
    try {
      status = MergeStatus.GETTING_CHANGES;
      final MergeQueueCreator mergeQueueCreator = MergeQueueUpdaterFactory.getMergeQueueUpdater(activeMergeID);
      mergeQueueCreator.updateQueue();
    } catch (final CommandStoppedException e) {
      if (log.isDebugEnabled()) log.debug("====================");
      if (log.isDebugEnabled()) log.debug("merge runner stopped");
      if (log.isDebugEnabled()) log.debug("====================");
      Thread.currentThread().interrupt();
    } catch (final Exception e) {
      reportError(e, Error.ERROR_LEVEL_ERROR);
    } catch (final java.lang.Error e) {
      reportError(e, Error.ERROR_LEVEL_FATAL);
      throw e;
    } finally {
      status = MergeStatus.IDLE;
    }
  }


  /**
   * @return status of this merge queue updater.
   *
   */
  public MergeStatus getStatus() {
    return status;
  }


  /**
   * Reports error.
   */
  private void reportError(final Throwable e, final byte errorLevelError) {
    final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
    final Error error = new Error();
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_MERGE);
    error.setMergeID(activeMergeID);
    error.setDescription("Error while updating merge queue: " + StringUtils.toString(e));
    error.setDetails(e);
    error.setErrorLevel(errorLevelError);
    error.setSendEmail(true);
    errorManager.reportSystemError(error);
  }


  public String toString() {
    return "MergeQueueUpdateRunnerImpl{" +
      "activeMergeID=" + activeMergeID +
      ", status=" + status +
      '}';
  }
}
