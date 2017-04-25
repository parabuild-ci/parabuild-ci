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
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.admin.*;

/**
 * Tests home page
 */
public class SSTestAccessDropDown extends ServersideTestCase {

  private AccessDropDown accessDropDown = null;


  public SSTestAccessDropDown(final String s) {
    super(s);
  }


  /**
   */
  public void test_defaultSelection() throws Exception {
    assertEquals(BuildConfig.ACCESS_PRIVATE, accessDropDown.getAccessType());
  }


  /**
   */
  public void test_setAccessType() throws Exception {
    setAndAssertAccessTypeIsSet(BuildConfig.ACCESS_PRIVATE);
    setAndAssertAccessTypeIsSet(BuildConfig.ACCESS_PUBLIC);
  }


  private void setAndAssertAccessTypeIsSet(final byte access) {
    accessDropDown.setAccessType(access);
    assertEquals(access, accessDropDown.getAccessType());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestAccessDropDown.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    accessDropDown = new AccessDropDown();
  }
}
