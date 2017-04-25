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
package org.parabuild.ci.webui.admin.error;

import junit.framework.TestSuite;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;

/**
 * Tests home page
 */
public class SSTestAdminErrorsTable extends ServersideTestCase {

  private ErrorTable errorsTable = null;


  public SSTestAdminErrorsTable(final String s) {
    super(s);
  }


  /**
   */
  public void testMakeHeader() throws Exception {
    assertNotNull(errorsTable.makeHeader());
    assertEquals(errorsTable.columnCount(), errorsTable.makeHeader().length);
  }


  /**
   */
  public void testMakeRow() throws Exception {
    assertNotNull(errorsTable.makeRow(0));
    assertEquals(errorsTable.columnCount(), errorsTable.makeRow(0).length);
  }


  /**
   */
  public void testPopulate() throws Exception {
    errorsTable.populate();
  }


  /**
   * Tests that populate doesn't break with undefined description.
   */
  public void testPopulate_Bug261() throws Exception {
    final ErrorManager em = ErrorManagerFactory.getErrorManager();
    final org.parabuild.ci.error.Error error = new org.parabuild.ci.error.Error();
    error.setSendEmail(false);
    em.clearAllActiveErrors();
    em.reportSystemError(error);
    errorsTable.populate();
  }


  protected void setUp() throws Exception {
    super.setUp();
    errorsTable = new ErrorTable(false);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestAdminErrorsTable.class);
  }
}
