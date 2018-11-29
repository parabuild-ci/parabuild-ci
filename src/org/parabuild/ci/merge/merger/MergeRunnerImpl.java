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
package org.parabuild.ci.merge.merger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.merge.MergeStatus;

/**
 * A runnable object that is responsible for running a
 * merge. It is controls threading-related issues.
 */
public final class MergeRunnerImpl implements MergeRunner {

  private static final Log log = LogFactory.getLog(MergeRunnerImpl.class);

  private final int activeMergeID;
  private MergeStatus status;


  public MergeRunnerImpl(final int mergeID) {
    this.activeMergeID = mergeID;
    this.status = MergeStatus.IDLE;
  }


  /**
   */
  public void run() {
    try {
      status = MergeStatus.IDLE;
      final Merger merger = MergerFactory.makeMerger(activeMergeID);
      status = MergeStatus.MERGING;
      merger.merge();
    } catch (final CommandStoppedException e) {
      if (log.isDebugEnabled()) log.debug("====================");
      if (log.isDebugEnabled()) log.debug("merge runner stopped");
      if (log.isDebugEnabled()) log.debug("====================");
      Thread.currentThread().interrupt();
    } catch (final Exception e) {
      ErrorManagerFactory.getErrorManager().reportSystemError(new Error("Tried to escape but was caught: " + StringUtils.toString(e), e));
    } catch (final java.lang.Error e) {
      reportError(e, Error.ERROR_LEVEL_FATAL);
      throw e;
    } finally {
      status = MergeStatus.IDLE;
    }
  }


  /**
   * @return merge status.
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
    error.setDescription("Error while processing merge queue: " + StringUtils.toString(e));
    error.setDetails(e);
    error.setErrorLevel(errorLevelError);
    error.setSendEmail(true);
    errorManager.reportSystemError(error);
  }


  public String toString() {
    return "MergeRunner{" +
      "activeMergeID=" + activeMergeID +
      ", status=" + status +
      '}';
  }
}
