/**
 * ProjectBuild.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class ProjectBuild  implements java.io.Serializable {
    private int ID;
    private int activeBuildID;
    private int projectID;

    public ProjectBuild() {
    }

    public ProjectBuild(
           int ID,
           int activeBuildID,
           int projectID) {
           this.ID = ID;
           this.activeBuildID = activeBuildID;
           this.projectID = projectID;
    }


    /**
     * Gets the ID value for this ProjectBuild.
     * 
     * @return ID
     */
    public int getID() {
        return ID;
    }


    /**
     * Sets the ID value for this ProjectBuild.
     * 
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }


    /**
     * Gets the activeBuildID value for this ProjectBuild.
     * 
     * @return activeBuildID
     */
    public int getActiveBuildID() {
        return activeBuildID;
    }


    /**
     * Sets the activeBuildID value for this ProjectBuild.
     * 
     * @param activeBuildID
     */
    public void setActiveBuildID(int activeBuildID) {
        this.activeBuildID = activeBuildID;
    }


    /**
     * Gets the projectID value for this ProjectBuild.
     * 
     * @return projectID
     */
    public int getProjectID() {
        return projectID;
    }


    /**
     * Sets the projectID value for this ProjectBuild.
     * 
     * @param projectID
     */
    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProjectBuild)) return false;
        ProjectBuild other = (ProjectBuild) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.ID == other.getID() &&
            this.activeBuildID == other.getActiveBuildID() &&
            this.projectID == other.getProjectID();
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
        _hashCode += getActiveBuildID();
        _hashCode += getProjectID();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProjectBuild.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ProjectBuild"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activeBuildID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "activeBuildID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("projectID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "projectID"));
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
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
