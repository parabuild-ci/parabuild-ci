/**
 * BuildConfiguration.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class BuildConfiguration  implements java.io.Serializable {
    private byte access;
    private int activeBuildID;
    private int buildID;
    private java.lang.String buildName;
    private java.lang.String emailDomain;
    private byte scheduleType;
    private byte sourceControl;
    private boolean sourceControlEmail;
    private boolean subordinate;
    private int farmID;

    public BuildConfiguration() {
    }

    public BuildConfiguration(
           byte access,
           int activeBuildID,
           int buildID,
           java.lang.String buildName,
           java.lang.String emailDomain,
           byte scheduleType,
           byte sourceControl,
           boolean sourceControlEmail,
           boolean subordinate,
           int farmID) {
           this.access = access;
           this.activeBuildID = activeBuildID;
           this.buildID = buildID;
           this.buildName = buildName;
           this.emailDomain = emailDomain;
           this.scheduleType = scheduleType;
           this.sourceControl = sourceControl;
           this.sourceControlEmail = sourceControlEmail;
           this.subordinate = subordinate;
           this.farmID = farmID;
    }


    /**
     * Gets the access value for this BuildConfiguration.
     * 
     * @return access
     */
    public byte getAccess() {
        return access;
    }


    /**
     * Sets the access value for this BuildConfiguration.
     * 
     * @param access
     */
    public void setAccess(byte access) {
        this.access = access;
    }


    /**
     * Gets the activeBuildID value for this BuildConfiguration.
     * 
     * @return activeBuildID
     */
    public int getActiveBuildID() {
        return activeBuildID;
    }


    /**
     * Sets the activeBuildID value for this BuildConfiguration.
     * 
     * @param activeBuildID
     */
    public void setActiveBuildID(int activeBuildID) {
        this.activeBuildID = activeBuildID;
    }


    /**
     * Gets the buildID value for this BuildConfiguration.
     * 
     * @return buildID
     */
    public int getBuildID() {
        return buildID;
    }


    /**
     * Sets the buildID value for this BuildConfiguration.
     * 
     * @param buildID
     */
    public void setBuildID(int buildID) {
        this.buildID = buildID;
    }


    /**
     * Gets the buildName value for this BuildConfiguration.
     * 
     * @return buildName
     */
    public java.lang.String getBuildName() {
        return buildName;
    }


    /**
     * Sets the buildName value for this BuildConfiguration.
     * 
     * @param buildName
     */
    public void setBuildName(java.lang.String buildName) {
        this.buildName = buildName;
    }


    /**
     * Gets the emailDomain value for this BuildConfiguration.
     * 
     * @return emailDomain
     */
    public java.lang.String getEmailDomain() {
        return emailDomain;
    }


    /**
     * Sets the emailDomain value for this BuildConfiguration.
     * 
     * @param emailDomain
     */
    public void setEmailDomain(java.lang.String emailDomain) {
        this.emailDomain = emailDomain;
    }


    /**
     * Gets the scheduleType value for this BuildConfiguration.
     * 
     * @return scheduleType
     */
    public byte getScheduleType() {
        return scheduleType;
    }


    /**
     * Sets the scheduleType value for this BuildConfiguration.
     * 
     * @param scheduleType
     */
    public void setScheduleType(byte scheduleType) {
        this.scheduleType = scheduleType;
    }


    /**
     * Gets the sourceControl value for this BuildConfiguration.
     * 
     * @return sourceControl
     */
    public byte getSourceControl() {
        return sourceControl;
    }


    /**
     * Sets the sourceControl value for this BuildConfiguration.
     * 
     * @param sourceControl
     */
    public void setSourceControl(byte sourceControl) {
        this.sourceControl = sourceControl;
    }


    /**
     * Gets the sourceControlEmail value for this BuildConfiguration.
     * 
     * @return sourceControlEmail
     */
    public boolean isSourceControlEmail() {
        return sourceControlEmail;
    }


    /**
     * Sets the sourceControlEmail value for this BuildConfiguration.
     * 
     * @param sourceControlEmail
     */
    public void setSourceControlEmail(boolean sourceControlEmail) {
        this.sourceControlEmail = sourceControlEmail;
    }


    /**
     * Gets the subordinate value for this BuildConfiguration.
     * 
     * @return subordinate
     */
    public boolean isSubordinate() {
        return subordinate;
    }


    /**
     * Sets the subordinate value for this BuildConfiguration.
     * 
     * @param subordinate
     */
    public void setSubordinate(boolean subordinate) {
        this.subordinate = subordinate;
    }


    /**
     * Gets the farmID value for this BuildConfiguration.
     * 
     * @return farmID
     */
    public int getFarmID() {
        return farmID;
    }


    /**
     * Sets the farmID value for this BuildConfiguration.
     * 
     * @param farmID
     */
    public void setFarmID(int farmID) {
        this.farmID = farmID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BuildConfiguration)) return false;
        BuildConfiguration other = (BuildConfiguration) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.access == other.getAccess() &&
            this.activeBuildID == other.getActiveBuildID() &&
            this.buildID == other.getBuildID() &&
            ((this.buildName==null && other.getBuildName()==null) || 
             (this.buildName!=null &&
              this.buildName.equals(other.getBuildName()))) &&
            ((this.emailDomain==null && other.getEmailDomain()==null) || 
             (this.emailDomain!=null &&
              this.emailDomain.equals(other.getEmailDomain()))) &&
            this.scheduleType == other.getScheduleType() &&
            this.sourceControl == other.getSourceControl() &&
            this.sourceControlEmail == other.isSourceControlEmail() &&
            this.subordinate == other.isSubordinate() &&
            this.farmID == other.getFarmID();
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
        _hashCode += getAccess();
        _hashCode += getActiveBuildID();
        _hashCode += getBuildID();
        if (getBuildName() != null) {
            _hashCode += getBuildName().hashCode();
        }
        if (getEmailDomain() != null) {
            _hashCode += getEmailDomain().hashCode();
        }
        _hashCode += getScheduleType();
        _hashCode += getSourceControl();
        _hashCode += (isSourceControlEmail() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isSubordinate() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getFarmID();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BuildConfiguration.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildConfiguration"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("access");
        elemField.setXmlName(new javax.xml.namespace.QName("", "access"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activeBuildID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "activeBuildID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buildID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buildID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buildName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buildName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emailDomain");
        elemField.setXmlName(new javax.xml.namespace.QName("", "emailDomain"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scheduleType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "scheduleType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sourceControl");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sourceControl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sourceControlEmail");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sourceControlEmail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subordinate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subordinate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("farmID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "farmID"));
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
