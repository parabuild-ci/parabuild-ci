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

import junit.framework.*;
import org.apache.commons.logging.*;

/**
 * Tests Logging service
 */
public class SATestUpToDateStatistics extends TestCase {

  private static final Log log = LogFactory.getLog(SATestUpToDateStatistics.class);


  public SATestUpToDateStatistics(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_create() throws Exception {
    final int successful = 10;
    final int failed = 3;
    final int checkind = 10;
    final int issues = 10;
    final BuildStatistics utds = new BuildStatistics(successful, failed, checkind, issues);
    assertEquals(successful, utds.getSuccessfulBuilds());
    assertEquals(failed, utds.getFailedBuilds());
    assertEquals(failed + successful, utds.getTotalBuilds());
    assertEquals(100, utds.getSuccessfulBuildsPercent() + utds.getFailedBuildsPercent());
    assertEquals(76, utds.getSuccessfulBuildsPercent());
    assertEquals(24, utds.getFailedBuildsPercent());
  }


  /**
   *
   */
  public void test_doesNotFailOnZeroes() throws Exception {
    new BuildStatistics(0, 0, 0, 0);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestUpToDateStatistics.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
