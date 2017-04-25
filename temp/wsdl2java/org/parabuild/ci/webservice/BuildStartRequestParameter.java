/**
 * BuildStartRequestParameter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class BuildStartRequestParameter  implements java.io.Serializable {
    private java.lang.String description;
    private java.lang.String variableName;
    private java.lang.String[] variableValues;

    public BuildStartRequestParameter() {
    }

    public BuildStartRequestParameter(
           java.lang.String description,
           java.lang.String variableName,
           java.lang.String[] variableValues) {
           this.description = description;
           this.variableName = variableName;
           this.variableValues = variableValues;
    }


    /**
     * Gets the description value for this BuildStartRequestParameter.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this BuildStartRequestParameter.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the variableName value for this BuildStartRequestParameter.
     * 
     * @return variableName
     */
    public java.lang.String getVariableName() {
        return variableName;
    }


    /**
     * Sets the variableName value for this BuildStartRequestParameter.
     * 
     * @param variableName
     */
    public void setVariableName(java.lang.String variableName) {
        this.variableName = variableName;
    }


    /**
     * Gets the variableValues value for this BuildStartRequestParameter.
     * 
     * @return variableValues
     */
    public java.lang.String[] getVariableValues() {
        return variableValues;
    }


    /**
     * Sets the variableValues value for this BuildStartRequestParameter.
     * 
     * @param variableValues
     */
    public void setVariableValues(java.lang.String[] variableValues) {
        this.variableValues = variableValues;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BuildStartRequestParameter)) return false;
        BuildStartRequestParameter other = (BuildStartRequestParameter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.variableName==null && other.getVariableName()==null) || 
             (this.variableName!=null &&
              this.variableName.equals(other.getVariableName()))) &&
            ((this.variableValues==null && other.getVariableValues()==null) || 
             (this.variableValues!=null &&
              java.util.Arrays.equals(this.variableValues, other.getVariableValues())));
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
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getVariableName() != null) {
            _hashCode += getVariableName().hashCode();
        }
        if (getVariableValues() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVariableValues());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVariableValues(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BuildStartRequestParameter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStartRequestParameter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("variableName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "variableName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("variableValues");
        elemField.setXmlName(new javax.xml.namespace.QName("", "variableValues"));
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
