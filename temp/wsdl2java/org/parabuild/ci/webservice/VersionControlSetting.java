/**
 * VersionControlSetting.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class VersionControlSetting  implements java.io.Serializable {
    private int buildID;
    private int propertyID;
    private java.lang.String propertyName;
    private long propertyTimeStamp;
    private java.lang.String propertyValue;

    public VersionControlSetting() {
    }

    public VersionControlSetting(
           int buildID,
           int propertyID,
           java.lang.String propertyName,
           long propertyTimeStamp,
           java.lang.String propertyValue) {
           this.buildID = buildID;
           this.propertyID = propertyID;
           this.propertyName = propertyName;
           this.propertyTimeStamp = propertyTimeStamp;
           this.propertyValue = propertyValue;
    }


    /**
     * Gets the buildID value for this VersionControlSetting.
     * 
     * @return buildID
     */
    public int getBuildID() {
        return buildID;
    }


    /**
     * Sets the buildID value for this VersionControlSetting.
     * 
     * @param buildID
     */
    public void setBuildID(int buildID) {
        this.buildID = buildID;
    }


    /**
     * Gets the propertyID value for this VersionControlSetting.
     * 
     * @return propertyID
     */
    public int getPropertyID() {
        return propertyID;
    }


    /**
     * Sets the propertyID value for this VersionControlSetting.
     * 
     * @param propertyID
     */
    public void setPropertyID(int propertyID) {
        this.propertyID = propertyID;
    }


    /**
     * Gets the propertyName value for this VersionControlSetting.
     * 
     * @return propertyName
     */
    public java.lang.String getPropertyName() {
        return propertyName;
    }


    /**
     * Sets the propertyName value for this VersionControlSetting.
     * 
     * @param propertyName
     */
    public void setPropertyName(java.lang.String propertyName) {
        this.propertyName = propertyName;
    }


    /**
     * Gets the propertyTimeStamp value for this VersionControlSetting.
     * 
     * @return propertyTimeStamp
     */
    public long getPropertyTimeStamp() {
        return propertyTimeStamp;
    }


    /**
     * Sets the propertyTimeStamp value for this VersionControlSetting.
     * 
     * @param propertyTimeStamp
     */
    public void setPropertyTimeStamp(long propertyTimeStamp) {
        this.propertyTimeStamp = propertyTimeStamp;
    }


    /**
     * Gets the propertyValue value for this VersionControlSetting.
     * 
     * @return propertyValue
     */
    public java.lang.String getPropertyValue() {
        return propertyValue;
    }


    /**
     * Sets the propertyValue value for this VersionControlSetting.
     * 
     * @param propertyValue
     */
    public void setPropertyValue(java.lang.String propertyValue) {
        this.propertyValue = propertyValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VersionControlSetting)) return false;
        VersionControlSetting other = (VersionControlSetting) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.buildID == other.getBuildID() &&
            this.propertyID == other.getPropertyID() &&
            ((this.propertyName==null && other.getPropertyName()==null) || 
             (this.propertyName!=null &&
              this.propertyName.equals(other.getPropertyName()))) &&
            this.propertyTimeStamp == other.getPropertyTimeStamp() &&
            ((this.propertyValue==null && other.getPropertyValue()==null) || 
             (this.propertyValue!=null &&
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
        new org.apache.axis.description.TypeDesc(VersionControlSetting.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "VersionControlSetting"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buildID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buildID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "propertyID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "propertyName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyTimeStamp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "propertyTimeStamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "propertyValue"));
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
