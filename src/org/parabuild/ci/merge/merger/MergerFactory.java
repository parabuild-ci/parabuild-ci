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

import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.merge.merger.build.perforce.P4Merger;
import org.parabuild.ci.merge.merger.nag.NaggingMerger;
import org.parabuild.ci.notification.NotificationManagerFactory;
import org.parabuild.ci.object.MergeConfiguration;

/**
 * Produces merger suitable for this configuration.
 */
final class MergerFactory {

  public static Merger makeMerger(final int activeMergeID) {
    final MergeConfiguration mergeConfiguration = MergeManager.getInstance().getActiveMergeConfiguration(activeMergeID);
    if (mergeConfiguration.getMergeMode() == MergeConfiguration.MERGE_MODE_NAG) {
      return new NaggingMerger(activeMergeID);
    } else {
      return new P4Merger(activeMergeID, NotificationManagerFactory.makeNotificationManager());
    }
  }
}
