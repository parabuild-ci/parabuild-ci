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

public class ProjectAttribute implements java.io.Serializable {

  private int ID;
  private java.lang.String name;
  private int projectID;
  private long timestamp;
  private java.lang.String value;
  private int valueAsInteger;


  public ProjectAttribute() {
  }


  public ProjectAttribute(
          final int ID,
          final java.lang.String name,
          final int projectID,
          final long timestamp,
          final java.lang.String value,
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
  public java.lang.String getName() {
    return name;
  }


  /**
   * Sets the name value for this ProjectAttribute.
   *
   * @param name
   */
  public void setName(final java.lang.String name) {
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
  public java.lang.String getValue() {
    return value;
  }


  /**
   * Sets the value value for this ProjectAttribute.
   *
   * @param value
   */
  public void setValue(final java.lang.String value) {
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


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
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
            this.ID == other.getID() &&
            ((this.name == null && other.getName() == null) ||
                    (this.name != null &&
                            this.name.equals(other.getName()))) &&
            this.projectID == other.getProjectID() &&
            this.timestamp == other.getTimestamp() &&
            ((this.value == null && other.getValue() == null) ||
                    (this.value != null &&
                            this.value.equals(other.getValue()))) &&
            this.valueAsInteger == other.getValueAsInteger();
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
    _hashCode += getID();
    if (getName() != null) {
      _hashCode += getName().hashCode();
    }
    _hashCode += getProjectID();
    _hashCode += new Long(getTimestamp()).hashCode();
    if (getValue() != null) {
      _hashCode += getValue().hashCode();
    }
    _hashCode += getValueAsInteger();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(ProjectAttribute.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "ProjectAttribute"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("name");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "name"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("projectID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "projectID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("timestamp");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "timestamp"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("value");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "value"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("valueAsInteger");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "valueAsInteger"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
  }


  /**
   * Return type metadata object
   */
  public static org.apache.axis.description.TypeDesc getTypeDesc() {
    return typeDesc;
  }


  /**
   * Get Custom Serializer
   */
  public static org.apache.axis.encoding.Serializer getSerializer(
          final java.lang.String mechType,
          final java.lang.Class _javaType,
          final javax.xml.namespace.QName _xmlType) {
    return
            new org.apache.axis.encoding.ser.BeanSerializer(
                    _javaType, _xmlType, typeDesc);
  }


  /**
   * Get Custom Deserializer
   */
  public static org.apache.axis.encoding.Deserializer getDeserializer(
          final java.lang.String mechType,
          final java.lang.Class _javaType,
          final javax.xml.namespace.QName _xmlType) {
    return
            new org.apache.axis.encoding.ser.BeanDeserializer(
                    _javaType, _xmlType, typeDesc);
  }

}
