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

import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 * Tests ChangeListsPanel
 */
public class SSTestChangeListsPanel extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestChangeListsPanel.class); // NOPMD
  private ChangeListsPanel changesPanel = null;


  public SSTestChangeListsPanel(final String s) {
    super(s);
  }


  /**
   */
  public void test_bug269_setBuildRun() throws Exception {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildRun buildRun = cm.getBuildRun(1);
    assertTrue(changesPanel.showChangeLists(buildRun.getActiveBuildID(), cm.getChangeListsOrderedByDate(buildRun.getBuildRunID())) > 0);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestChangeListsPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    changesPanel = new ChangeListsPanel();
  }
}
