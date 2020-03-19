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

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.parabuild.ci.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.EncryptingPassword;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tests home page
 */
public class SATestPropertyToInputMap extends TestCase {

  static final String TEST_PROPERTY_NAME1 = "test.property.name.one";
  static final String TEST_PROPERTY_NAME2 = "test.property.name.two";
  static final String TEST_PROPERTY_VALUE1 = "test.property.value.one";
  static final String TEST_PROPERTY_VALUE2 = "test.property.value.two";
  final CommonField FIELD1 = new CommonField(100, 100);
  final CommonField FIELD2 = new CommonField(100, 100);

  PropertyToInputMap.PropertyHandler<MockProperty> propertyHandler = null;
  PropertyToInputMap propertyToInputMap = null;


  public SATestPropertyToInputMap() {
  }


  /**
   * Makes sure that home page responds
   */
  public void test_getUpdatedProperties() throws Exception {

    // set up mapping
    propertyToInputMap.bindPropertyNameToInput(TEST_PROPERTY_NAME1, FIELD1);
    propertyToInputMap.bindPropertyNameToInput(TEST_PROPERTY_NAME2, FIELD2);

    // set up prop list consisted of one property
    final MockProperty propertyOne = new MockProperty();
    propertyOne.setPropertyName(TEST_PROPERTY_NAME1);
    propertyOne.setPropertyValue(TEST_PROPERTY_VALUE1);
    final List properties = new ArrayList(1);
    properties.add(propertyOne);

    // set prop list to the mapper
    propertyToInputMap.setProperties(properties);

    // check if the field is gou updated as a result of setting ptoperty list
    assertEquals(TEST_PROPERTY_VALUE1, FIELD1.getValue());

    // set value of the second field
    FIELD2.setValue(TEST_PROPERTY_VALUE2);

    // get updated props
    final List updatedProperties = propertyToInputMap.getUpdatedProperties();

    // check if size is updated
    assertEquals(2, updatedProperties.size());

    // check if property made to the list
    boolean found = false;
    for (Iterator updatedIter = updatedProperties.iterator(); updatedIter.hasNext();) {
      final MockProperty property = (MockProperty) updatedIter.next();
      if (property.getPropertyName().equals(TEST_PROPERTY_NAME2)) {
        found = true;
        assertEquals(TEST_PROPERTY_VALUE2, property.getPropertyValue());
      }
    }
    assertEquals(found, true);
  }


  public void test_handlesEncrypingPasswordField() {

    // create field
    final EncryptingPassword ep = new EncryptingPassword(20, 20, "test_encrypted_password");

    // set up mapping
    propertyToInputMap.bindPropertyNameToInput(TEST_PROPERTY_NAME1, ep);

    // check if the field is set to a descripted value
    final String original = "test_password";
    final String encrypted = "973908CD78928E660B047F5DE5130BE0";
    final MockProperty propertyOne = new MockProperty();
    propertyOne.setPropertyName(TEST_PROPERTY_NAME1);
    propertyOne.setPropertyValue(encrypted);
    final List properties = new ArrayList(5);
    properties.add(propertyOne);
    propertyToInputMap.setProperties(properties);
    assertEquals(original, ep.getValue());

    // check that property calue gets encrypted
    final String newOriginal = "password";
    final String newEncrypted = "FED13585645B0EE34267AAAED4697000";
    ep.setValue(newOriginal);
    final List updatedProperties = propertyToInputMap.getUpdatedProperties();
    final MockProperty updatedProperty = (MockProperty) updatedProperties.get(0);
    assertEquals(newEncrypted, updatedProperty.getPropertyValue());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestPropertyToInputMap.class);
  }


  protected void setUp() throws Exception {
    super.setUp();

    // init property handler
    propertyHandler = new MockPropertyHandler();
    // init map
    propertyToInputMap = new PropertyToInputMap(propertyHandler);
  }
}

class MockPropertyHandler implements PropertyToInputMap.PropertyHandler<MockProperty> {

  private static final long serialVersionUID = 6769137886617012782L;


  public MockProperty makeProperty(final String propertyName) {
    final MockProperty property = new MockProperty();
    property.setPropertyName(propertyName);
    return property;
  }


  public void setPropertyValue(final MockProperty property, final String propertyValue) {
    property.setPropertyValue(propertyValue);
  }


  public String getPropertyValue(final MockProperty property) {
    return property.getPropertyValue();
  }


  public String getPropertyName(final MockProperty property) {
    return property.getPropertyName();
  }
}