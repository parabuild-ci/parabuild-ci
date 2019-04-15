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

public class ProjectAttribute implements Serializable {

  private static final long serialVersionUID = 7291714883008140790L;
  private int ID;
  private String name;
  private int projectID;
  private long timestamp;
  private String value;
  private int valueAsInteger;


  public ProjectAttribute() {
  }


  public ProjectAttribute(
          final int ID,
          final String name,
          final int projectID,
          final long timestamp,
          final String value,
          final int valueAsInteger) {
    this.ID = ID;
    this.name = name;
    this.projectID = projectID;
    this.timestamp = timestamp;
    this.value = value;
    this.valueAsInteger = valueAsInteger;
  }


  /**
   * Gets the ID value for this ProjectAttribute.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this ProjectAttribute.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the name value for this ProjectAttribute.
   *
   * @return name
   */
  public String getName() {
    return name;
  }


  /**
   * Sets the name value for this ProjectAttribute.
   *
   * @param name
   */
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Gets the projectID value for this ProjectAttribute.
   *
   * @return projectID
   */
  public int getProjectID() {
    return projectID;
  }


  /**
   * Sets the projectID value for this ProjectAttribute.
   *
   * @param projectID
   */
  public void setProjectID(final int projectID) {
    this.projectID = projectID;
  }


  /**
   * Gets the timestamp value for this ProjectAttribute.
   *
   * @return timestamp
   */
  public long getTimestamp() {
    return timestamp;
  }


  /**
   * Sets the timestamp value for this ProjectAttribute.
   *
   * @param timestamp
   */
  public void setTimestamp(final long timestamp) {
    this.timestamp = timestamp;
  }


  /**
   * Gets the value value for this ProjectAttribute.
   *
   * @return value
   */
  public String getValue() {
    return value;
  }


  /**
   * Sets the value value for this ProjectAttribute.
   *
   * @param value
   */
  public void setValue(final String value) {
    this.value = value;
  }


  /**
   * Gets the valueAsInteger value for this ProjectAttribute.
   *
   * @return valueAsInteger
   */
  public int getValueAsInteger() {
    return valueAsInteger;
  }


  /**
   * Sets the valueAsInteger value for this ProjectAttribute.
   *
   * @param valueAsInteger
   */
  public void setValueAsInteger(final int valueAsInteger) {
    this.valueAsInteger = valueAsInteger;
  }


  private Object __equalsCalc = null;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof ProjectAttribute)) return false;
    final ProjectAttribute other = (ProjectAttribute) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.ID &&
            ((this.name == null && other.name == null) ||
                    (this.name != null &&
                            this.name.equals(other.name))) &&
            this.projectID == other.projectID &&
            this.timestamp == other.timestamp &&
            ((this.value == null && other.value == null) ||
                    (this.value != null &&
                            this.value.equals(other.value))) &&
            this.valueAsInteger == other.valueAsInteger;
    __equalsCalc = null;
    return _equals;
  }


  private boolean __hashCodeCalc = false;


  public synchronized int hashCode() {
    if (__hashCodeCalc) {
      return 0;
    }
    __hashCodeCalc = true;
    int _hashCode = 1;
    _hashCode += ID;
    if (name != null) {
      _hashCode += name.hashCode();
    }
    _hashCode += projectID;
    _hashCode += new Long(timestamp).hashCode();
    if (value != null) {
      _hashCode += value.hashCode();
    }
    _hashCode += valueAsInteger;
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(ProjectAttribute.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "ProjectAttribute"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "ID"));
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
    elemField.setFieldName("projectID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "projectID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("timestamp");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "timestamp"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("value");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "value"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("valueAsInteger");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "valueAsInteger"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
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
