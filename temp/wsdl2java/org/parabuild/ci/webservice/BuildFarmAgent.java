/**
 * BuildFarmAgent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class BuildFarmAgent  implements java.io.Serializable {
    private int ID;
    private int agentID;
    private long timeStamp;
    private int farmID;

    public BuildFarmAgent() {
    }

    public BuildFarmAgent(
           int ID,
           int agentID,
           long timeStamp,
           int farmID) {
           this.ID = ID;
           this.agentID = agentID;
           this.timeStamp = timeStamp;
           this.farmID = farmID;
    }


    /**
     * Gets the ID value for this BuildFarmAgent.
     * 
     * @return ID
     */
    public int getID() {
        return ID;
    }


    /**
     * Sets the ID value for this BuildFarmAgent.
     * 
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }


    /**
     * Gets the agentID value for this BuildFarmAgent.
     * 
     * @return agentID
     */
    public int getAgentID() {
        return agentID;
    }


    /**
     * Sets the agentID value for this BuildFarmAgent.
     * 
     * @param agentID
     */
    public void setAgentID(int agentID) {
        this.agentID = agentID;
    }


    /**
     * Gets the timeStamp value for this BuildFarmAgent.
     * 
     * @return timeStamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }


    /**
     * Sets the timeStamp value for this BuildFarmAgent.
     * 
     * @param timeStamp
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }


    /**
     * Gets the farmID value for this BuildFarmAgent.
     * 
     * @return farmID
     */
    public int getFarmID() {
        return farmID;
    }


    /**
     * Sets the farmID value for this BuildFarmAgent.
     * 
     * @param farmID
     */
    public void setFarmID(int farmID) {
        this.farmID = farmID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BuildFarmAgent)) return false;
        BuildFarmAgent other = (BuildFarmAgent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.ID == other.getID() &&
            this.agentID == other.getAgentID() &&
            this.timeStamp == other.getTimeStamp() &&
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
        _hashCode += getID();
        _hashCode += getAgentID();
        _hashCode += new Long(getTimeStamp()).hashCode();
        _hashCode += getFarmID();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BuildFarmAgent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildFarmAgent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agentID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agentID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeStamp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timeStamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
