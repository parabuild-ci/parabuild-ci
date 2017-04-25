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

import viewtier.ui.RadioButton;

/**
 *
 */
public final class SATestRadioListValueSource extends TestCase {

  private VariableValueHolderFlow.RadioListValueSource valueSource = null;
  private static final String TEST_VALUE1 = " test_value1";
  private static final String TEST_VALUE2 = " test_value2";


  public void test_returnsValueIfChecked() {
    valueSource.add(makeRadioButton(true), SATestRadioListValueSource.TEST_VALUE1);
    valueSource.add(makeRadioButton(false), SATestRadioListValueSource.TEST_VALUE2);
    assertEquals(1, valueSource.getValues().size());
    assertEquals(TEST_VALUE1.trim(), valueSource.getValues().get(0));
  }


  public void test_doesNotReturnsValueIfNoChecked() {
    valueSource.add(makeRadioButton(false), SATestRadioListValueSource.TEST_VALUE1);
    valueSource.add(makeRadioButton(false), SATestRadioListValueSource.TEST_VALUE2);
    assertEquals(0, valueSource.getValues().size());
  }


  public void test_applyFirstValueAsDefault() {
    valueSource.add(makeRadioButton(false), SATestRadioListValueSource.TEST_VALUE1);
    valueSource.add(makeRadioButton(false), SATestRadioListValueSource.TEST_VALUE2);
    valueSource.applyFirstValueAsDefault(false);
    assertEquals(0, valueSource.getValues().size());
    valueSource.applyFirstValueAsDefault(true);
    assertEquals(1, valueSource.getValues().size());
  }


  private RadioButton makeRadioButton(final boolean checked) {
    final RadioButton rb = new RadioButton();
    rb.setSelected(checked);
    return rb;
  }


  protected void setUp() throws Exception {
    super.setUp();
    valueSource = new VariableValueHolderFlow.RadioListValueSource();
  }


  /**
   * Required by JUnit
   */
  public SATestRadioListValueSource(final String s) {
    super(s);
  }


  public static TestSuite suite() {
    return new TestSuite(SATestRadioListValueSource.class);
  }
}
