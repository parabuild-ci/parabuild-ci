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
package org.parabuild.ci.merge.merger.nag;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.MergeConfigurationAttribute;
import org.parabuild.ci.merge.MergeDAO;

/**
 * Tests NaggingMerger
 *
 * @see
 *
 *
 */
public class SSTestNaggingMerger extends ServersideTestCase {


  public void test_merge() {
    assertNull(MergeDAO.getInstance().getMergeConfigurationAttribute(TestHelper.TEST_MERGE_ID, MergeConfigurationAttribute.NAG_DAY_SENT_LAST_TIME));
    final NaggingMerger merger = new NaggingMerger(TestHelper.TEST_MERGE_ID);
    merger.merge();
    assertNotNull(MergeDAO.getInstance().getMergeConfigurationAttribute(TestHelper.TEST_MERGE_ID, MergeConfigurationAttribute.NAG_DAY_SENT_LAST_TIME));
  }


  protected void setUp() throws Exception {
    super.setUp();
    ErrorManagerFactory.getErrorManager().clearAllActiveErrors();
  }


  protected void tearDown() throws Exception {
    super.tearDown();
    assertEquals(0, ErrorManagerFactory.getErrorManager().errorCount());
  }


  public SSTestNaggingMerger(final String s) {
    super(s);
  }
}
