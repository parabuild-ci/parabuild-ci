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
package org.parabuild.ci.util;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;

/**
 * Tests NamedPropertyStringGenerator
 */
public final class SATestNamedPropetyStringGenerator extends TestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SATestNamedPropetyStringGenerator.class); // NOPMD
  private static final String TEST_PROPERTY_1 = "test.property.1";
  private static final String TEST_PROPERTY_2 = "test.property.2";
  private static final String TEST_TEMPLATE = "test_template_${test.property.1}_${test.property.2}";


  public SATestNamedPropetyStringGenerator(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_isTemplateStatic() throws Exception {
    final NamedPropertyStringGenerator generator = new NamedPropertyStringGenerator(new NamedProperty[]{
            new NamedProperty(TEST_PROPERTY_1, false, true, true),
            new NamedProperty(TEST_PROPERTY_2, false, true, false),},
            TEST_TEMPLATE, true); // strict output
    assertTrue(!generator.isTemplateStatic());
  }


  /**
   *
   */
  public void test_generate() throws Exception {
    final NamedPropertyStringGenerator generator = new NamedPropertyStringGenerator(new NamedProperty[]{
            new NamedProperty(TEST_PROPERTY_1, false, true, false),
            new NamedProperty(TEST_PROPERTY_2, false, true, false),},
            TEST_TEMPLATE, false);
    assertTrue(generator.isTemplateStatic());
    assertTrue(validateTemplate(generator));
    assertTrue(!generator.isStrictResultRequired());

    // try to run w/o properties
    try {
      generator.generate();
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      assertEquals("Property ${test.property.1} has not been set", StringUtils.toString(e));
    }

    // now set parameters
    generator.setPropertyValue(TEST_PROPERTY_1, "test_value_1");
    generator.setPropertyValue(TEST_PROPERTY_2, "test_value_2");
    assertEquals("test_template_test_value_1_test_value_2", generator.generate());
  }


  /**
   *
   */
  public void test_isTemplateValid() throws Exception {
    final NamedPropertyStringGenerator generator = new NamedPropertyStringGenerator(new NamedProperty[]{
            new NamedProperty(TEST_PROPERTY_1, false, true, false),
            new NamedProperty(TEST_PROPERTY_2, false, true, false),},
            "#$_test_template", true); // strict output
    assertTrue(generator.isTemplateStatic());
    assertTrue(!validateTemplate(generator));

    // set props and make sure it breaks
    generator.setPropertyValue(TEST_PROPERTY_1, "test_value_1");
    generator.setPropertyValue(TEST_PROPERTY_2, "test_value_2");
    try {
      generator.generate();
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      assertEquals("Result \"#\" created from template \"#$_test_template\" is not a valid strict string.", StringUtils.toString(e));
    }
  }


  /**
   *
   */
  public void test_invalidatesMissingMandatoryProperties() throws Exception {
    final NamedPropertyStringGenerator generator = new NamedPropertyStringGenerator(new NamedProperty[]{
            new NamedProperty(TEST_PROPERTY_1, false, true, false),
            new NamedProperty(TEST_PROPERTY_2, true, true, false),}, // this property is manadatory
            TEST_TEMPLATE, true); // strict output

    // set only optional
    generator.setPropertyValue(TEST_PROPERTY_1, "test_value_1");
    try {
      generator.generate();
      // should break on no mandatory
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      assertEquals("Mandatory property \"test.property.2\" is not set.", StringUtils.toString(e));
    }
  }


  private static boolean validateTemplate(final NamedPropertyStringGenerator generator) {
    try {
      generator.validate();
    } catch (ValidationException e) {
      return false;
    }
    return true;
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestNamedPropetyStringGenerator.class);
  }
}
