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

import org.apache.cactus.*;

import junit.framework.*;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.object.StartParameterType;

/**
 * Tests EditManualRunParametersPanel
 */
public final class SSTestEditManualStartParametersPanel extends ServletTestCase {

  /** @noinspection UNUSED_SYMBOL,FieldCanBeLocal*/
  private ManualStartParametersPanel testPanel = null;


  public SSTestEditManualStartParametersPanel(final String s) {
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
    return new TestSuite(SSTestEditManualStartParametersPanel.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    testPanel = new ManualStartParametersPanel(TestHelper.TEST_CVS_EMPTY_BUILD_ID, false, StartParameterType.BUILD, WebUIConstants.MODE_EDIT);
  }
}
