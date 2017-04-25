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
package org.parabuild.ci.common;

import junit.framework.*;

/**
 * Tests NamedProperty
 */
public class SATestNamedProperty extends TestCase {

  private static final String TEST_NAME = "test.name";
  private static final String TEST_NAME_UPPERCASE = TEST_NAME.toUpperCase();


  public SATestNamedProperty(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_create() throws Exception {
    final NamedProperty namedProperty1 = new NamedProperty(TEST_NAME, true, true, true);
    assertEquals(TEST_NAME, namedProperty1.getPropertyName());
    assertTrue(namedProperty1.isDynamic());
    assertTrue(namedProperty1.isMandatory());
    assertTrue(namedProperty1.isStrictValue());

    // tests uppercase and false values
    final NamedProperty namedProperty2 = new NamedProperty(TEST_NAME_UPPERCASE, false, false, false);
    assertEquals(TEST_NAME, namedProperty2.getPropertyName());
    assertTrue(!namedProperty2.isDynamic());
    assertTrue(!namedProperty2.isMandatory());
    assertTrue(!namedProperty2.isStrictValue());
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestNamedProperty.class);
  }
}
