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
/**
 * BuildConfigAttribute.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice.templ;

public class BuildConfigurationAttribute implements java.io.Serializable {

  private int buildID;
  private int propertyID;
  private java.lang.String propertyName;
  private long propertyTimeStamp;
  private java.lang.String propertyValue;


  public BuildConfigurationAttribute() {
  }


  public BuildConfigurationAttribute(
          final int buildID,
          final int propertyID,
          final java.lang.String propertyName,
          final long propertyTimeStamp,
          final java.lang.String propertyValue,
          final int propertyValueAsInteger) {
    this.buildID = buildID;
    this.propertyID = propertyID;
    this.propertyName = propertyName;
    this.propertyTimeStamp = propertyTimeStamp;
    this.propertyValue = propertyValue;
  }


  /**
   * Gets the buildID value for this BuildConfigAttribute.
   *
   * @return buildID
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Sets the buildID value for this BuildConfigAttribute.
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Gets the propertyID value for this BuildConfigAttribute.
   *
   * @return propertyID
   */
  public int getPropertyID() {
    return propertyID;
  }


  /**
   * Sets the propertyID value for this BuildConfigAttribute.
   *
   * @param propertyID
   */
  public void setPropertyID(final int propertyID) {
    this.propertyID = propertyID;
  }


  /**
   * Gets the propertyName value for this BuildConfigAttribute.
   *
   * @return propertyName
   */
  public java.lang.String getPropertyName() {
    return propertyName;
  }


  /**
   * Sets the propertyName value for this BuildConfigAttribute.
   *
   * @param propertyName
   */
  public void setPropertyName(final java.lang.String propertyName) {
    this.propertyName = propertyName;
  }


  /**
   * Gets the propertyTimeStamp value for this BuildConfigAttribute.
   *
   * @return propertyTimeStamp
   */
  public long getPropertyTimeStamp() {
    return propertyTimeStamp;
  }


  /**
   * Sets the propertyTimeStamp value for this BuildConfigAttribute.
   *
   * @param propertyTimeStamp
   */
  public void setPropertyTimeStamp(final long propertyTimeStamp) {
    this.propertyTimeStamp = propertyTimeStamp;
  }


  /**
   * Gets the propertyValue value for this BuildConfigAttribute.
   *
   * @return propertyValue
   */
  public java.lang.String getPropertyValue() {
    return propertyValue;
  }


  /**
   * Sets the propertyValue value for this BuildConfigAttribute.
   *
   * @param propertyValue
   */
  public void setPropertyValue(final java.lang.String propertyValue) {
    this.propertyValue = propertyValue;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof BuildConfigurationAttribute)) return false;
    final BuildConfigurationAttribute other = (BuildConfigurationAttribute) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.buildID == other.getBuildID() &&
            this.propertyID == other.getPropertyID() &&
            ((this.propertyName == null && other.getPropertyName() == null) ||
                    (this.propertyName != null &&
                            this.propertyName.equals(other.getPropertyName()))) &&
            this.propertyTimeStamp == other.getPropertyTimeStamp() &&
            ((this.propertyValue == null && other.getPropertyValue() == null) ||
                    (this.propertyValue != null &&
                            this.propertyValue.equals(other.getPropertyValue())));
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
    _hashCode += getBuildID();
    _hashCode += getPropertyID();
    if (getPropertyName() != null) {
      _hashCode += getPropertyName().hashCode();
    }
    _hashCode += new Long(getPropertyTimeStamp()).hashCode();
    if (getPropertyValue() != null) {
      _hashCode += getPropertyValue().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(BuildConfigurationAttribute.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "BuildConfigAttribute"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("propertyID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "propertyID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("propertyName");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "propertyName"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("propertyTimeStamp");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "propertyTimeStamp"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("propertyValue");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "propertyValue"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("propertyValueAsInteger");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "propertyValueAsInteger"));
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
