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
package org.parabuild.ci.webui.admin;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.WebUIConstants;

/**
 * Tests VCSUserToEmailTable
 */
public class SSTestVCSUserToEmailTable extends ServersideTestCase {

  public SSTestVCSUserToEmailTable(final String s) {
    super(s);
  }


  /**
   * Tests the table component constructor
   */
  public void testCreate() throws Exception {
    final VCSUserToEmailTable userToEmailTable = new VCSUserToEmailTable(WebUIConstants.MODE_EDIT);
    assertEquals(BuildConfig.UNSAVED_ID, userToEmailTable.getBuildID());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestVCSUserToEmailTable.class);
  }


  protected void setUp() throws Exception {
    // call ServerSideTest setup that initializes db data
    super.setUp();
  }
}
