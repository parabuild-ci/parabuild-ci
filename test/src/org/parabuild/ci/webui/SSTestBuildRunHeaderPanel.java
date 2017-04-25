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
package org.parabuild.ci.webui;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 * Tests home page
 */
public class SSTestBuildRunHeaderPanel extends ServersideTestCase {

  private BuildRun buildRun;
  private BuildRunHeaderPanel buildRunHeaderPanel;


  public SSTestBuildRunHeaderPanel(final String s) {
    super(s);
  }


  /**
   */
  public void test_setBuildRun() throws Exception {
    buildRunHeaderPanel.showBuildRun(buildRun);
    assertNotNull(buildRunHeaderPanel);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBuildRunHeaderPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    buildRun = ConfigurationManager.getInstance().getBuildRun(1);
    buildRunHeaderPanel = new BuildRunHeaderPanel();
  }
}
