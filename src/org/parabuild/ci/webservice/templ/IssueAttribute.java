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

package org.parabuild.ci.webservice.templ;

import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

import javax.xml.namespace.QName;
import java.io.Serializable;

public class IssueAttribute implements Serializable {

  private static final long serialVersionUID = -7117524437953047435L;
  private int ID;
  private int issueID;
  private String name;
  private int propertyValue;
  private int propertyValueAsInteger;
  private String value;


  public IssueAttribute() {
  }


  public IssueAttribute(
          final int ID,
          final int issueID,
          final String name,
          final int propertyValue,
          final int propertyValueAsInteger,
          final String value) {
    this.ID = ID;
    this.issueID = issueID;
    this.name = name;
    this.propertyValue = propertyValue;
    this.propertyValueAsInteger = propertyValueAsInteger;
    this.value = value;
  }


  /**
   * Gets the ID value for this IssueAttribute.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this IssueAttribute.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the issueID value for this IssueAttribute.
   *
   * @return issueID
   */
  public int getIssueID() {
    return issueID;
  }


  /**
   * Sets the issueID value for this IssueAttribute.
   *
   * @param issueID
   */
  public void setIssueID(final int issueID) {
    this.issueID = issueID;
  }


  /**
   * Gets the name value for this IssueAttribute.
   *
   * @return name
   */
  public String getName() {
    return name;
  }


  /**
   * Sets the name value for this IssueAttribute.
   *
   * @param name
   */
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Gets the propertyValue value for this IssueAttribute.
   *
   * @return propertyValue
   */
  public int getPropertyValue() {
    return propertyValue;
  }


  /**
   * Sets the propertyValue value for this IssueAttribute.
   *
   * @param propertyValue
   */
  public void setPropertyValue(final int propertyValue) {
    this.propertyValue = propertyValue;
  }


  /**
   * Gets the propertyValueAsInteger value for this IssueAttribute.
   *
   * @return propertyValueAsInteger
   */
  public int getPropertyValueAsInteger() {
    return propertyValueAsInteger;
  }


  /**
   * Sets the propertyValueAsInteger value for this IssueAttribute.
   *
   * @param propertyValueAsInteger
   */
  public void setPropertyValueAsInteger(final int propertyValueAsInteger) {
    this.propertyValueAsInteger = propertyValueAsInteger;
  }


  /**
   * Gets the value value for this IssueAttribute.
   *
   * @return value
   */
  public String getValue() {
    return value;
  }


  /**
   * Sets the value value for this IssueAttribute.
   *
   * @param value
   */
  public void setValue(final String value) {
    this.value = value;
  }


  private Object __equalsCalc;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof IssueAttribute)) return false;
    final IssueAttribute other = (IssueAttribute) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.ID &&
            this.issueID == other.issueID &&
            ((this.name == null && other.name == null) ||
                    (this.name != null &&
                            this.name.equals(other.name))) &&
            this.propertyValue == other.propertyValue &&
            this.propertyValueAsInteger == other.propertyValueAsInteger &&
            ((this.value == null && other.value == null) ||
                    (this.value != null &&
                            this.value.equals(other.value)));
    __equalsCalc = null;
    return _equals;
  }


  private boolean __hashCodeCalc;


  public synchronized int hashCode() {
    if (__hashCodeCalc) {
      return 0;
    }
    __hashCodeCalc = true;
    int _hashCode = 1;
    _hashCode += ID;
    _hashCode += issueID;
    if (name != null) {
      _hashCode += name.hashCode();
    }
    _hashCode += propertyValue;
    _hashCode += propertyValueAsInteger;
    if (value != null) {
      _hashCode += value.hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(IssueAttribute.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "IssueAttribute"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("issueID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "issueID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("name");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "name"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("propertyValue");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "propertyValue"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("propertyValueAsInteger");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "propertyValueAsInteger"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("value");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "value"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
  }


  /**
   * Return type metadata object
   */
  public static TypeDesc getTypeDesc() {
    return typeDesc;
  }


  /**
   * Get Custom Serializer
   */
  public static Serializer getSerializer(
          final String mechType,
          final Class _javaType,
          final QName _xmlType) {
    return
            new BeanSerializer(
                    _javaType, _xmlType, typeDesc);
  }


  /**
   * Get Custom Deserializer
   */
  public static Deserializer getDeserializer(
          final String mechType,
          final Class _javaType,
          final QName _xmlType) {
    return
            new BeanDeserializer(
                    _javaType, _xmlType, typeDesc);
  }

}
