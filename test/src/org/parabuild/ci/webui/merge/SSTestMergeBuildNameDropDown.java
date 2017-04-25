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
package org.parabuild.ci.webui.merge;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.error.ErrorManagerFactory;

/**
 * Tests MergeBuildNameDropdown
 */
public class SSTestMergeBuildNameDropDown extends ServersideTestCase {

  private MergeBuildNameDropdown dropdown;


  public void test_populate() {
    dropdown.populate(1);
    assertTrue(dropdown.getItemCount() >= 1);
  }


  public SSTestMergeBuildNameDropDown(final String s) {
    super(s);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestMergeBuildNameDropDown.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    dropdown = new MergeBuildNameDropdown();
    ErrorManagerFactory.getErrorManager().clearAllActiveErrors();
  }
}
