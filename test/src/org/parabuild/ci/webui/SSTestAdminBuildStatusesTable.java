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
import org.parabuild.ci.webui.admin.*;
import org.parabuild.ci.services.*;

/**
 * Tests home page
 */
public class SSTestAdminBuildStatusesTable extends ServersideTestCase {

  private AdminBuildStatusesTable statusesTable = null;


  public SSTestAdminBuildStatusesTable(final String s) {
    super(s);
  }


  /**
   * Makes sure that home page responds
   */
  public void testMakeHeader() throws Exception {
    assertNotNull(statusesTable.makeHeader());
    assertEquals(statusesTable.columnCount(), statusesTable.makeHeader().length);
  }


  /**
   * Makes sure that home page responds
   */
  public void testMakeRow() throws Exception {
    assertNotNull(statusesTable.makeRow(0));
    assertEquals(statusesTable.columnCount(), statusesTable.makeRow(0).length);
  }


  /**
   * Makes sure that home page responds
   */
  public void testPopulate() throws Exception {
    statusesTable.populate(BuildManager.getInstance().getCurrentBuildsStatuses());
  }


  protected void setUp() throws Exception {
    super.setUp();
    statusesTable = new AdminBuildStatusesTable(0);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestAdminBuildStatusesTable.class);
  }
}
