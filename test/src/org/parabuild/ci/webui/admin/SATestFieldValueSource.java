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

import viewtier.ui.*;

/**
 *
 */
public final class SATestFieldValueSource extends TestCase {

  private VariableValueHolderFlow.FieldValueSource valueSource = null;
  private static final String TEST_VALUE1 = " test_value1";


  public void test_returnsValueIfFieldNotEmpty() {
    final Field field = new Field(20, 20);
    field.setValue(TEST_VALUE1);
    valueSource.setField(field);
    assertEquals(1, valueSource.getValues().size());
    assertEquals(TEST_VALUE1.trim(), valueSource.getValues().get(0));
  }


  public void test_doesNotReturnsValueIfNoChecked() {
    final Field field = new Field(20, 20);
    field.setValue("");
    valueSource.setField(field);
    assertEquals(0, valueSource.getValues().size());
  }


  protected void setUp() throws Exception {
    super.setUp();
    valueSource = new VariableValueHolderFlow.FieldValueSource();
  }


  /**
   * Required by JUnit
   */
  public SATestFieldValueSource(final String s) {
    super(s);
  }


  public static TestSuite suite() {
    return new TestSuite(SATestFieldValueSource.class);
  }
}
