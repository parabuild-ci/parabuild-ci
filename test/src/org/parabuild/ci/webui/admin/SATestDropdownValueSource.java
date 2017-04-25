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

import junit.framework.TestCase;
import junit.framework.TestSuite;

import viewtier.ui.DropDown;

/**
 *
 */
public final class SATestDropdownValueSource extends TestCase {

  private VariableValueHolderFlow.DropdownValueSource valueSource = null;
  private static final String TEST_VALUE1 = " test_value1";
  private static final String TEST_VALUE2 = " test_value2";


  public void test_returnsValueIfChecked() {
    final DropDown dd = new DropDown();
    dd.addItem(SATestDropdownValueSource.TEST_VALUE1);
    dd.addItem(SATestDropdownValueSource.TEST_VALUE2);
    valueSource.setDropDown(dd);
    assertEquals(1, valueSource.getValues().size());
    assertEquals(TEST_VALUE1, valueSource.getValues().get(0));
  }


  public void test_applyFirstValueAsDefault() {
    final DropDown dd = new DropDown();
    dd.addItem(TEST_VALUE2);
    dd.addItem(TEST_VALUE1);
    valueSource.setDropDown(dd);
    valueSource.applyFirstValueAsDefault(true);
    assertEquals(1, valueSource.getValues().size());
    assertEquals(TEST_VALUE2, valueSource.getValues().get(0));
  }


  protected void setUp() throws Exception {
    super.setUp();
    valueSource = new VariableValueHolderFlow.DropdownValueSource();
  }


  /**
   * Required by JUnit
   */
  public SATestDropdownValueSource(final String s) {
    super(s);
  }


  public static TestSuite suite() {
    return new TestSuite(SATestDropdownValueSource.class);
  }
}
