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
 * ResultConfig.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice.templ;

public class ResultConfiguration implements java.io.Serializable {

  private int ID;
  private java.lang.Integer autopublishGroupID;
  private int buildID;
  private java.lang.String description;
  private boolean failIfNotFound;
  private boolean ignoreTimestamp;
  private java.lang.String path;
  private java.lang.String shellVariable;
  private long timeStamp;
  private byte type;


  public ResultConfiguration() {
  }


  public ResultConfiguration(
          final int ID,
          final java.lang.Integer autopublishGroupID,
          final int buildID,
          final java.lang.String description,
          final boolean failIfNotFound,
          final boolean ignoreTimestamp,
          final java.lang.String path,
          final java.lang.String shellVariable,
          final long timeStamp,
          final byte type) {
    this.ID = ID;
    this.autopublishGroupID = autopublishGroupID;
    this.buildID = buildID;
    this.description = description;
    this.failIfNotFound = failIfNotFound;
    this.ignoreTimestamp = ignoreTimestamp;
    this.path = path;
    this.shellVariable = shellVariable;
    this.timeStamp = timeStamp;
    this.type = type;
  }


  /**
   * Gets the ID value for this ResultConfig.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this ResultConfig.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the autopublishGroupID value for this ResultConfig.
   *
   * @return autopublishGroupID
   */
  public java.lang.Integer getAutopublishGroupID() {
    return autopublishGroupID;
  }


  /**
   * Sets the autopublishGroupID value for this ResultConfig.
   *
   * @param autopublishGroupID
   */
  public void setAutopublishGroupID(final java.lang.Integer autopublishGroupID) {
    this.autopublishGroupID = autopublishGroupID;
  }


  /**
   * Gets the buildID value for this ResultConfig.
   *
   * @return buildID
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Sets the buildID value for this ResultConfig.
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Gets the description value for this ResultConfig.
   *
   * @return description
   */
  public java.lang.String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this ResultConfig.
   *
   * @param description
   */
  public void setDescription(final java.lang.String description) {
    this.description = description;
  }


  /**
   * Gets the failIfNotFound value for this ResultConfig.
   *
   * @return failIfNotFound
   */
  public boolean isFailIfNotFound() {
    return failIfNotFound;
  }


  /**
   * Sets the failIfNotFound value for this ResultConfig.
   *
   * @param failIfNotFound
   */
  public void setFailIfNotFound(final boolean failIfNotFound) {
    this.failIfNotFound = failIfNotFound;
  }


  /**
   * Gets the ignoreTimestamp value for this ResultConfig.
   *
   * @return ignoreTimestamp
   */
  public boolean isIgnoreTimestamp() {
    return ignoreTimestamp;
  }


  /**
   * Sets the ignoreTimestamp value for this ResultConfig.
   *
   * @param ignoreTimestamp
   */
  public void setIgnoreTimestamp(final boolean ignoreTimestamp) {
    this.ignoreTimestamp = ignoreTimestamp;
  }


  /**
   * Gets the path value for this ResultConfig.
   *
   * @return path
   */
  public java.lang.String getPath() {
    return path;
  }


  /**
   * Sets the path value for this ResultConfig.
   *
   * @param path
   */
  public void setPath(final java.lang.String path) {
    this.path = path;
  }


  /**
   * Gets the shellVariable value for this ResultConfig.
   *
   * @return shellVariable
   */
  public java.lang.String getShellVariable() {
    return shellVariable;
  }


  /**
   * Sets the shellVariable value for this ResultConfig.
   *
   * @param shellVariable
   */
  public void setShellVariable(final java.lang.String shellVariable) {
    this.shellVariable = shellVariable;
  }


  /**
   * Gets the timeStamp value for this ResultConfig.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this ResultConfig.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the type value for this ResultConfig.
   *
   * @return type
   */
  public byte getType() {
    return type;
  }


  /**
   * Sets the type value for this ResultConfig.
   *
   * @param type
   */
  public void setType(final byte type) {
    this.type = type;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof ResultConfiguration)) return false;
    final ResultConfiguration other = (ResultConfiguration) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.getID() &&
            ((this.autopublishGroupID == null && other.getAutopublishGroupID() == null) ||
                    (this.autopublishGroupID != null &&
                            this.autopublishGroupID.equals(other.getAutopublishGroupID()))) &&
            this.buildID == other.getBuildID() &&
            ((this.description == null && other.getDescription() == null) ||
                    (this.description != null &&
                            this.description.equals(other.getDescription()))) &&
            this.failIfNotFound == other.isFailIfNotFound() &&
            this.ignoreTimestamp == other.isIgnoreTimestamp() &&
            ((this.path == null && other.getPath() == null) ||
                    (this.path != null &&
                            this.path.equals(other.getPath()))) &&
            ((this.shellVariable == null && other.getShellVariable() == null) ||
                    (this.shellVariable != null &&
                            this.shellVariable.equals(other.getShellVariable()))) &&
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
    if (getAutopublishGroupID() != null) {
      _hashCode += getAutopublishGroupID().hashCode();
    }
    _hashCode += getBuildID();
    if (getDescription() != null) {
      _hashCode += getDescription().hashCode();
    }
    _hashCode += (isFailIfNotFound() ? Boolean.TRUE : Boolean.FALSE).hashCode();
    _hashCode += (isIgnoreTimestamp() ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (getPath() != null) {
      _hashCode += getPath().hashCode();
    }
    if (getShellVariable() != null) {
      _hashCode += getShellVariable().hashCode();
    }
    _hashCode += new Long(getTimeStamp()).hashCode();
    _hashCode += getType();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(ResultConfiguration.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "ResultConfig"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("autopublishGroupID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "autopublishGroupID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(true);
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
    elemField.setFieldName("failIfNotFound");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "failIfNotFound"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("ignoreTimestamp");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "ignoreTimestamp"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("path");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "path"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("shellVariable");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "shellVariable"));
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
