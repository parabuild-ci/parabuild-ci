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

import junit.framework.TestSuite;
import org.apache.cactus.ServletTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.common.WebUIConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests EditManualStartParametersTable
 */
public final class SSTestEditManualStartParametersTable extends ServletTestCase {

  /**
   * @noinspection UNUSED_SYMBOL,FieldCanBeLocal
   */
  private EditManualStartParametersTable testTable = null;


  public SSTestEditManualStartParametersTable(final String s) {
    super(s);
  }


  /**
   */
  public void test_create() throws Exception {
    // do nothing, create is called in setUp method.
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestEditManualStartParametersTable.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    final List paramList = new ArrayList(11);
    paramList.add(new StartParameter(TestHelper.TEST_CVS_VALID_BUILD_ID, StartParameter.TYPE_BUILD, StartParameter.PRESENTATION_RADIO_LIST, "TEST_PARAM", "true,false", "Test descr", 0, false));
    testTable = new EditManualStartParametersTable(paramList, true, WebUIConstants.MODE_VIEW);
  }
}
