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
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.webui.admin.*;

/**
 * Tests home page
 */
public class SSTestScheduleItemsTable extends ServersideTestCase {

  private ScheduleItemsTable tblItems = null;


  public SSTestScheduleItemsTable(final String s) {
    super(s);
  }


  /**
   * Makes sure that home page responds
   */
  public void test_setScheduleItems() throws Exception {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    assertEquals(0, tblItems.getRowCount());
    tblItems.setScheduleItems(cm.getScheduleItems(TestHelper.TEST_RECURRENT_BUILD_ID));
    assertEquals(2, tblItems.getRowCount());
  }


  protected void setUp() throws Exception {
    super.setUp();
    tblItems = new ScheduleItemsTable();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestScheduleItemsTable.class);
  }
}
