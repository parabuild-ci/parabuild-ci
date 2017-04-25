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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.services.BuildFinishedEvent;

/**
 * Tests TimeToFixMonitor
 */
public class SSTestTimeToFixMonitor extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestTimeToFixMonitor.class);

  private TimeToFixMonitor timeToFixMonitor = null;


  public void test_buildFinished() {

    // just check nothing bad happens
    timeToFixMonitor.buildFinished(new BuildFinishedEvent() {
      public int getBuildRunID() {
        return 3;
      }


      public int getBuildTimeSeconds() {
        return 10;
      }


      public byte getBuildResultCode() {
        return BuildRun.BUILD_RESULT_BROKEN;
      }


      public Date getBuildFinishedAt() {
        return new Date();
      }
    });

  }


  public SSTestTimeToFixMonitor(final String s) {
    super(s);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestTimeToFixMonitor.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    timeToFixMonitor = new TimeToFixMonitor(TestHelper.TEST_CVS_VALID_BUILD_ID);
  }
}
