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
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.*;

/**
 * Tests MonthlyStatsUpdater
 */
public class SSTestMonthlyBuildStatsUpdater extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestMonthlyBuildStatsUpdater.class);

  private ConfigurationManager cm = null;
  private MonthlyBuildStatsUpdater statsUpdater = null;
  private ErrorManager errorManager;


  public SSTestMonthlyBuildStatsUpdater(final String s) {
    super(s);
  }


  public void test_truncateLevel() {
    assertEquals(Calendar.MONTH, statsUpdater.truncateLevel());
  }


  protected void tearDown() throws Exception {
    super.tearDown();
    assertEquals(0, errorManager.errorCount());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestMonthlyBuildStatsUpdater.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
    cm = ConfigurationManager.getInstance();
    statsUpdater = new MonthlyBuildStatsUpdater();
  }
}
