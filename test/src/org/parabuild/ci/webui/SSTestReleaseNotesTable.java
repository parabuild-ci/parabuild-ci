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

/**
 * Tests home page
 */
public class SSTestReleaseNotesTable extends ServersideTestCase {

  private ReleaseNotesTable releaseNotesTable = null;
  private ConfigurationManager cm = null;


  public SSTestReleaseNotesTable(final String s) {
    super(s);
  }


  public void test_setBuildRunID() {
    // NOTE: vimeshev - 05/15/2004 - this test relays on the fact that
    // there are TWO issues in release notes table in dataset.xml. It
    // otherwise breaks.
    releaseNotesTable.populateFromBuildRun(1);
    assertEquals(2, releaseNotesTable.getRowCount());

    // test run with change lists attached to issues
    releaseNotesTable.populateFromBuildRun(6);
    assertEquals(1, releaseNotesTable.getRowCount());
  }


  protected void setUp() throws Exception {
    super.setUp();
    releaseNotesTable = new ReleaseNotesTable();
    cm = ConfigurationManager.getInstance();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestReleaseNotesTable.class);
  }
}
