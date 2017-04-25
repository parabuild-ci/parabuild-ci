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

import java.util.*;
import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;

/**
 * Tests HourlyStatsUpdater
 */
public class SSTestHorlyBuildStatsUpdater extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestHorlyBuildStatsUpdater.class);

  private ConfigurationManager cm = null;
  private HourlyBuildStatsUpdater statsUpdater = null;
  private ErrorManager errorManager;


  public SSTestHorlyBuildStatsUpdater(final String s) {
    super(s);
  }


  public void test_truncateLevel() {
    assertEquals(Calendar.HOUR_OF_DAY, statsUpdater.truncateLevel());
  }


  public void test_updateStatistics() {
    final BuildRun lastCompleteBuildRun = cm.getLastCompleteBuildRun(TestHelper.TEST_CVS_VALID_BUILD_ID);
    lastCompleteBuildRun.setFinishedAt(new Date());
    statsUpdater.updateStatistics(lastCompleteBuildRun);
    statsUpdater.updateStatistics(lastCompleteBuildRun);
  }


  protected void tearDown() throws Exception {
    super.tearDown();
    assertEquals(0, errorManager.errorCount());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestHorlyBuildStatsUpdater.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
    cm = ConfigurationManager.getInstance();
    statsUpdater = new HourlyBuildStatsUpdater();
  }
}
