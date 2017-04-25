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
 * LogConfig.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice.templ;

public class LogConfiguration implements java.io.Serializable {

  private int ID;
  private int buildID;
  private java.lang.String description;
  private java.lang.String path;
  private long timeStamp;
  private byte type;


  public LogConfiguration() {
  }


  public LogConfiguration(
          final int ID,
          final int buildID,
          final java.lang.String description,
          final java.lang.String path,
          final long timeStamp,
          final byte type) {
    this.ID = ID;
    this.buildID = buildID;
    this.description = description;
    this.path = path;
    this.timeStamp = timeStamp;
    this.type = type;
  }


  /**
   * Gets the ID value for this LogConfig.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this LogConfig.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the buildID value for this LogConfig.
   *
   * @return buildID
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Sets the buildID value for this LogConfig.
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Gets the description value for this LogConfig.
   *
   * @return description
   */
  public java.lang.String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this LogConfig.
   *
   * @param description
   */
  public void setDescription(final java.lang.String description) {
    this.description = description;
  }


  /**
   * Gets the path value for this LogConfig.
   *
   * @return path
   */
  public java.lang.String getPath() {
    return path;
  }


  /**
   * Sets the path value for this LogConfig.
   *
   * @param path
   */
  public void setPath(final java.lang.String path) {
    this.path = path;
  }


  /**
   * Gets the timeStamp value for this LogConfig.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this LogConfig.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the type value for this LogConfig.
   *
   * @return type
   */
  public byte getType() {
    return type;
  }


  /**
   * Sets the type value for this LogConfig.
   *
   * @param type
   */
  public void setType(final byte type) {
    this.type = type;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof LogConfiguration)) return false;
    final LogConfiguration other = (LogConfiguration) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.getID() &&
            this.buildID == other.getBuildID() &&
            ((this.description == null && other.getDescription() == null) ||
                    (this.description != null &&
                            this.description.equals(other.getDescription()))) &&
            ((this.path == null && other.getPath() == null) ||
                    (this.path != null &&
                            this.path.equals(other.getPath()))) &&
            this.timeStamp == other.getTimeStamp() &&
            this.type == other.getType();
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
    _hashCode += getBuildID();
    if (getDescription() != null) {
      _hashCode += getDescription().hashCode();
    }
    if (getPath() != null) {
      _hashCode += getPath().hashCode();
    }
    _hashCode += new Long(getTimeStamp()).hashCode();
    _hashCode += getType();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(LogConfiguration.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "LogConfig"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("description");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "description"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("path");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "path"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("timeStamp");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "timeStamp"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("type");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "type"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
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
