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

import java.util.*;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.ActiveMergeConfiguration;
import org.parabuild.ci.object.MergeServiceConfiguration;
import com.gargoylesoftware.base.testing.OrderedTestSuite;

/**
 * Tests MergeManager
 */
public class SSTestMergeManager extends ServersideTestCase {

  private static final int TEST_PROJECT_ID = 1;


  private MergeManager mm;


  public void test_getMergeNamesByBuildID() {
    //noinspection UNUSED_SYMBOL
    final List mergeNamesByBuildID = mm.getMergeNamesByBuildID(TestHelper.TEST_P4_VALID_BUILD_ID);
  }


  public void test_getNagReport() {
    //noinspection UNUSED_SYMBOL
    final List nagReportList = mm.getNagReport(0);
  }


  public void test_saveMerge() {
    ActiveMergeConfiguration mergeConfiguration = new ActiveMergeConfiguration();
    mergeConfiguration.setDescription("test description");
    mergeConfiguration.setMarker("test marker");
    mergeConfiguration.setName("test name");
    mergeConfiguration.setSourceBuildID(TestHelper.TEST_P4_VALID_BUILD_ID);
    mergeConfiguration.setTargetBuildID(TestHelper.TEST_P4_VALID_BUILD_ID); // REVIEWME: simeshev@parabuilci.org -> should be different
    mm.save(mergeConfiguration);
    MergeServiceConfiguration mergeServiceConfiguration = new MergeServiceConfiguration();
    mergeServiceConfiguration.setDeleted(false);
    mergeServiceConfiguration.setProjectID(TEST_PROJECT_ID);
    mergeServiceConfiguration.setID(mergeConfiguration.getID());
    mm.save(mergeServiceConfiguration);
  }


  public void test_getMergeStatuses() {
    assertTrue(!mm.getMergeStatuses().isEmpty());
  }


  public void test_getMergeReport() {
    assertNotNull(mm.getMergeReport(0, 0, 1000));
  }


  public void test_getMergeReportCount() {
    mm.getMergeReportCount(0);
  }


  public SSTestMergeManager(final String s) {
    super(s);
  }


  public void test_getQueueReport() {
    assertNotNull(mm.getQueueReport(0, 0, 1000));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestMergeManager.class, new String[]{
      "test_getQueueReport",
      "test_getMergeStatuses",
    });
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    mm = MergeManager.getInstance();
    ErrorManagerFactory.getErrorManager().clearAllActiveErrors();
  }
}
