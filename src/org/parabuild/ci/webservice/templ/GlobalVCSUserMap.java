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
 * GlobalVCSUserMap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice.templ;

public class GlobalVCSUserMap implements java.io.Serializable {

  private int ID;
  private java.lang.String description;
  private java.lang.String email;
  private java.lang.String vcsUserName;


  public GlobalVCSUserMap() {
  }


  public GlobalVCSUserMap(
          final int ID,
          final java.lang.String description,
          final java.lang.String email,
          final java.lang.String vcsUserName) {
    this.ID = ID;
    this.description = description;
    this.email = email;
    this.vcsUserName = vcsUserName;
  }


  /**
   * Gets the ID value for this GlobalVCSUserMap.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this GlobalVCSUserMap.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the description value for this GlobalVCSUserMap.
   *
   * @return description
   */
  public java.lang.String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this GlobalVCSUserMap.
   *
   * @param description
   */
  public void setDescription(final java.lang.String description) {
    this.description = description;
  }


  /**
   * Gets the email value for this GlobalVCSUserMap.
   *
   * @return email
   */
  public java.lang.String getEmail() {
    return email;
  }


  /**
   * Sets the email value for this GlobalVCSUserMap.
   *
   * @param email
   */
  public void setEmail(final java.lang.String email) {
    this.email = email;
  }


  /**
   * Gets the vcsUserName value for this GlobalVCSUserMap.
   *
   * @return vcsUserName
   */
  public java.lang.String getVcsUserName() {
    return vcsUserName;
  }


  /**
   * Sets the vcsUserName value for this GlobalVCSUserMap.
   *
   * @param vcsUserName
   */
  public void setVcsUserName(final java.lang.String vcsUserName) {
    this.vcsUserName = vcsUserName;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof GlobalVCSUserMap)) return false;
    final GlobalVCSUserMap other = (GlobalVCSUserMap) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.getID() &&
            ((this.description == null && other.getDescription() == null) ||
                    (this.description != null &&
                            this.description.equals(other.getDescription()))) &&
            ((this.email == null && other.getEmail() == null) ||
                    (this.email != null &&
                            this.email.equals(other.getEmail()))) &&
            ((this.vcsUserName == null && other.getVcsUserName() == null) ||
                    (this.vcsUserName != null &&
                            this.vcsUserName.equals(other.getVcsUserName())));
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
    if (getDescription() != null) {
      _hashCode += getDescription().hashCode();
    }
    if (getEmail() != null) {
      _hashCode += getEmail().hashCode();
    }
    if (getVcsUserName() != null) {
      _hashCode += getVcsUserName().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(GlobalVCSUserMap.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "GlobalVCSUserMap"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "ID"));
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
    elemField.setFieldName("email");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "email"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("vcsUserName");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "vcsUserName"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
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
