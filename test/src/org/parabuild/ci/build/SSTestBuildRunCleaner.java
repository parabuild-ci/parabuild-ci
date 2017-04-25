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
package org.parabuild.ci.build;

import org.apache.commons.logging.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.*;


/**
 * Tests BuildRunCleaner
 */
public class SSTestBuildRunCleaner extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestBuildRunCleaner.class);

  public static final int TEST_BUILD_RUN = 1;

  private ConfigurationManager configManager = null;
  private BuildRunCleaner buildRunCleaner = null;


  public SSTestBuildRunCleaner(final String s) {
    super(s);
  }


  public void test_cleanUp() {
    buildRunCleaner.cleanUp();
    assertEquals(0, configManager.getStepRuns(TEST_BUILD_RUN).size());
    assertEquals(0, configManager.getBuildRunResults(TEST_BUILD_RUN).size());
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    this.configManager = ConfigurationManager.getInstance();
    this.buildRunCleaner = new BuildRunCleaner(TEST_BUILD_RUN);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestBuildRunCleaner.class, new String[]{
    });
  }
}
