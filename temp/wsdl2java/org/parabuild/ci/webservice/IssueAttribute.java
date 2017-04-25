/**
 * IssueAttribute.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class IssueAttribute  implements java.io.Serializable {
    private int ID;
    private int issueID;
    private java.lang.String name;
    private int propertyValue;
    private int propertyValueAsInteger;
    private java.lang.String value;

    public IssueAttribute() {
    }

    public IssueAttribute(
           int ID,
           int issueID,
           java.lang.String name,
           int propertyValue,
           int propertyValueAsInteger,
           java.lang.String value) {
           this.ID = ID;
           this.issueID = issueID;
           this.name = name;
           this.propertyValue = propertyValue;
           this.propertyValueAsInteger = propertyValueAsInteger;
           this.value = value;
    }


    /**
     * Gets the ID value for this IssueAttribute.
     * 
     * @return ID
     */
    public int getID() {
        return ID;
    }


    /**
     * Sets the ID value for this IssueAttribute.
     * 
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }


    /**
     * Gets the issueID value for this IssueAttribute.
     * 
     * @return issueID
     */
    public int getIssueID() {
        return issueID;
    }


    /**
     * Sets the issueID value for this IssueAttribute.
     * 
     * @param issueID
     */
    public void setIssueID(int issueID) {
        this.issueID = issueID;
    }


    /**
     * Gets the name value for this IssueAttribute.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this IssueAttribute.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the propertyValue value for this IssueAttribute.
     * 
     * @return propertyValue
     */
    public int getPropertyValue() {
        return propertyValue;
    }


    /**
     * Sets the propertyValue value for this IssueAttribute.
     * 
     * @param propertyValue
     */
    public void setPropertyValue(int propertyValue) {
        this.propertyValue = propertyValue;
    }


    /**
     * Gets the propertyValueAsInteger value for this IssueAttribute.
     * 
     * @return propertyValueAsInteger
     */
    public int getPropertyValueAsInteger() {
        return propertyValueAsInteger;
    }


    /**
     * Sets the propertyValueAsInteger value for this IssueAttribute.
     * 
     * @param propertyValueAsInteger
     */
    public void setPropertyValueAsInteger(int propertyValueAsInteger) {
        this.propertyValueAsInteger = propertyValueAsInteger;
    }


    /**
     * Gets the value value for this IssueAttribute.
     * 
     * @return value
     */
    public java.lang.String getValue() {
        return value;
    }


    /**
     * Sets the value value for this IssueAttribute.
     * 
     * @param value
     */
    public void setValue(java.lang.String value) {
        this.value = value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof IssueAttribute)) return false;
        IssueAttribute other = (IssueAttribute) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.ID == other.getID() &&
            this.issueID == other.getIssueID() &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            this.propertyValue == other.getPropertyValue() &&
            this.propertyValueAsInteger == other.getPropertyValueAsInteger() &&
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              this.value.equals(other.getValue())));
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
        _hashCode += getIssueID();
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        _hashCode += getPropertyValue();
        _hashCode += getPropertyValueAsInteger();
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(IssueAttribute.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "IssueAttribute"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("issueID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "issueID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "propertyValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyValueAsInteger");
        elemField.setXmlName(new javax.xml.namespace.QName("", "propertyValueAsInteger"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "value"));
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
