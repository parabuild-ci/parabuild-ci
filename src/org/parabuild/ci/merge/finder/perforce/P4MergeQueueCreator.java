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
package org.parabuild.ci.merge.finder.perforce;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.merge.finder.MergeQueueCreator;
import org.parabuild.ci.object.ActiveMergeConfiguration;
import org.parabuild.ci.object.BranchMergeConfiguration;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;

import java.io.IOException;

/**
 * This clas is instantiated, requted to perfrom its job and
 * then discarded.
 */
public final class P4MergeQueueCreator implements MergeQueueCreator {

  private final int activeMergeID;


  public P4MergeQueueCreator(final int activeMergeID) {
    this.activeMergeID = activeMergeID;
  }


  /**
   * Finds new change lists to merge and places them into
   * the merge queue.
   */
  public void updateQueue() throws ValidationException, IOException, CommandStoppedException, BuildException, AgentFailureException {
    final MergeConfiguration activeConfiguration = MergeManager.getInstance().getMergeConfiguration(activeMergeID);

    final BranchMergeConfiguration mergeConfiguration = new P4MergeConfigurationPreparer().prepare((ActiveMergeConfiguration) activeConfiguration);

    // find from what change list number we should start looking for
    // changes.
    final P4StartingChangeListFinder startingChangeListFinder = new P4StartingChangeListFinder(mergeConfiguration);
    final int startNumber = startingChangeListFinder.findStartingChangeListNumber() + 1; // we increment is because it is likely branch change list and it is huge.

    // find what changed since the starting change list. new
    // changes are all considered to be subject to
    // integration.
    final P4NewChangeListsFinder newChangeListsFinder = new P4NewChangeListsFinder(mergeConfiguration);
    newChangeListsFinder.findAndStoreNewChangesSince(startNumber);

    // Update an integration queue. Integation queue is those
    // changes that should be integrated using provided
    // citeria. Currently it is a marker that is used to
    // identify what should be merged.
    final P4MergeQueueUpdater mergeQueueUpdater = new P4MergeQueueUpdater(mergeConfiguration);
    mergeQueueUpdater.updateMergeQueue();

    // find and upate already integrated. we need to do this
    // because changes could have been integrated manually.
    final P4IntegratedSynchronizer integratedSynchronizer = new P4IntegratedSynchronizer(mergeConfiguration);
    integratedSynchronizer.findAndUpdateAlreadyIntegrated();
  }


  public String toString() {
    return "P4MergeQueueCreator{" +
            "activeMergeID=" + activeMergeID +
            '}';
  }
}
