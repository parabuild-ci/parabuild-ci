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

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.merge.finder.perforce.P4MergeQueueCreator;

/**
 */
public class SSTestMergeQueueUpdaterFactory extends ServersideTestCase {

  private static final int ACTIVE_MERGE_ID = 0;


  public void test_getMergeQueueUpdater() {
    final MergeQueueCreator mergeQueueUpdater = MergeQueueUpdaterFactory.getMergeQueueUpdater(ACTIVE_MERGE_ID);
    assertNotNull(mergeQueueUpdater);
    assertTrue(mergeQueueUpdater instanceof P4MergeQueueCreator);
  }


  public SSTestMergeQueueUpdaterFactory(final String s) {
    super(s);
  }
}
