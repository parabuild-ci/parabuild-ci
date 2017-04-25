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
 * ProjectResultGroup.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice.templ;

public class ProjectResultGroup implements java.io.Serializable {

  private int ID;
  private int projectID;
  private int resultGroupID;


  public ProjectResultGroup() {
  }


  public ProjectResultGroup(
          final int ID,
          final int projectID,
          final int resultGroupID) {
    this.ID = ID;
    this.projectID = projectID;
    this.resultGroupID = resultGroupID;
  }


  /**
   * Gets the ID value for this ProjectResultGroup.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this ProjectResultGroup.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the projectID value for this ProjectResultGroup.
   *
   * @return projectID
   */
  public int getProjectID() {
    return projectID;
  }


  /**
   * Sets the projectID value for this ProjectResultGroup.
   *
   * @param projectID
   */
  public void setProjectID(final int projectID) {
    this.projectID = projectID;
  }


  /**
   * Gets the resultGroupID value for this ProjectResultGroup.
   *
   * @return resultGroupID
   */
  public int getResultGroupID() {
    return resultGroupID;
  }


  /**
   * Sets the resultGroupID value for this ProjectResultGroup.
   *
   * @param resultGroupID
   */
  public void setResultGroupID(final int resultGroupID) {
    this.resultGroupID = resultGroupID;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof ProjectResultGroup)) return false;
    final ProjectResultGroup other = (ProjectResultGroup) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.getID() &&
            this.projectID == other.getProjectID() &&
            this.resultGroupID == other.getResultGroupID();
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
    _hashCode += getProjectID();
    _hashCode += getResultGroupID();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(ProjectResultGroup.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "ProjectResultGroup"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("projectID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "projectID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("resultGroupID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "resultGroupID"));
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
