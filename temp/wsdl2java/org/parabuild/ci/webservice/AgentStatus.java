/**
 * AgentStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class AgentStatus  implements java.io.Serializable {
    private int agentID;
    private java.lang.String hostName;
    private java.lang.String remoteVersion;
    private byte status;

    public AgentStatus() {
    }

    public AgentStatus(
           int agentID,
           java.lang.String hostName,
           java.lang.String remoteVersion,
           byte status) {
           this.agentID = agentID;
           this.hostName = hostName;
           this.remoteVersion = remoteVersion;
           this.status = status;
    }


    /**
     * Gets the agentID value for this AgentStatus.
     * 
     * @return agentID
     */
    public int getAgentID() {
        return agentID;
    }


    /**
     * Sets the agentID value for this AgentStatus.
     * 
     * @param agentID
     */
    public void setAgentID(int agentID) {
        this.agentID = agentID;
    }


    /**
     * Gets the hostName value for this AgentStatus.
     * 
     * @return hostName
     */
    public java.lang.String getHostName() {
        return hostName;
    }


    /**
     * Sets the hostName value for this AgentStatus.
     * 
     * @param hostName
     */
    public void setHostName(java.lang.String hostName) {
        this.hostName = hostName;
    }


    /**
     * Gets the remoteVersion value for this AgentStatus.
     * 
     * @return remoteVersion
     */
    public java.lang.String getRemoteVersion() {
        return remoteVersion;
    }


    /**
     * Sets the remoteVersion value for this AgentStatus.
     * 
     * @param remoteVersion
     */
    public void setRemoteVersion(java.lang.String remoteVersion) {
        this.remoteVersion = remoteVersion;
    }


    /**
     * Gets the status value for this AgentStatus.
     * 
     * @return status
     */
    public byte getStatus() {
        return status;
    }


    /**
     * Sets the status value for this AgentStatus.
     * 
     * @param status
     */
    public void setStatus(byte status) {
        this.status = status;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AgentStatus)) return false;
        AgentStatus other = (AgentStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.agentID == other.getAgentID() &&
            ((this.hostName==null && other.getHostName()==null) || 
             (this.hostName!=null &&
              this.hostName.equals(other.getHostName()))) &&
            ((this.remoteVersion==null && other.getRemoteVersion()==null) || 
             (this.remoteVersion!=null &&
              this.remoteVersion.equals(other.getRemoteVersion()))) &&
            this.status == other.getStatus();
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
        _hashCode += getAgentID();
        if (getHostName() != null) {
            _hashCode += getHostName().hashCode();
        }
        if (getRemoteVersion() != null) {
            _hashCode += getRemoteVersion().hashCode();
        }
        _hashCode += getStatus();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AgentStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "AgentStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agentID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agentID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "hostName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("remoteVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "remoteVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
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
