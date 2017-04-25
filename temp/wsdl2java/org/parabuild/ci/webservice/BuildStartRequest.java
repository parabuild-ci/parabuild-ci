/**
 * BuildStartRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class BuildStartRequest  implements java.io.Serializable {
    private org.parabuild.ci.webservice.AgentHost agentHost;
    private int buildRunID;
    private int changeListID;
    private boolean cleanCheckout;
    private boolean ignoreSerialization;
    private java.lang.String label;
    private java.lang.String note;
    private org.parabuild.ci.webservice.BuildStartRequestParameter[] parameterList;
    private boolean pinResult;
    private int requestType;
    private org.parabuild.ci.webservice.SourceControlSettingOverride[] sourceControlSettingsOverrides;
    private int userID;
    private int versionCounter;
    private java.lang.String versionTemplate;

    public BuildStartRequest() {
    }

    public BuildStartRequest(
           org.parabuild.ci.webservice.AgentHost agentHost,
           int buildRunID,
           int changeListID,
           boolean cleanCheckout,
           boolean ignoreSerialization,
           java.lang.String label,
           java.lang.String note,
           org.parabuild.ci.webservice.BuildStartRequestParameter[] parameterList,
           boolean pinResult,
           int requestType,
           org.parabuild.ci.webservice.SourceControlSettingOverride[] sourceControlSettingsOverrides,
           int userID,
           int versionCounter,
           java.lang.String versionTemplate) {
           this.agentHost = agentHost;
           this.buildRunID = buildRunID;
           this.changeListID = changeListID;
           this.cleanCheckout = cleanCheckout;
           this.ignoreSerialization = ignoreSerialization;
           this.label = label;
           this.note = note;
           this.parameterList = parameterList;
           this.pinResult = pinResult;
           this.requestType = requestType;
           this.sourceControlSettingsOverrides = sourceControlSettingsOverrides;
           this.userID = userID;
           this.versionCounter = versionCounter;
           this.versionTemplate = versionTemplate;
    }


    /**
     * Gets the agentHost value for this BuildStartRequest.
     * 
     * @return agentHost
     */
    public org.parabuild.ci.webservice.AgentHost getAgentHost() {
        return agentHost;
    }


    /**
     * Sets the agentHost value for this BuildStartRequest.
     * 
     * @param agentHost
     */
    public void setAgentHost(org.parabuild.ci.webservice.AgentHost agentHost) {
        this.agentHost = agentHost;
    }


    /**
     * Gets the buildRunID value for this BuildStartRequest.
     * 
     * @return buildRunID
     */
    public int getBuildRunID() {
        return buildRunID;
    }


    /**
     * Sets the buildRunID value for this BuildStartRequest.
     * 
     * @param buildRunID
     */
    public void setBuildRunID(int buildRunID) {
        this.buildRunID = buildRunID;
    }


    /**
     * Gets the changeListID value for this BuildStartRequest.
     * 
     * @return changeListID
     */
    public int getChangeListID() {
        return changeListID;
    }


    /**
     * Sets the changeListID value for this BuildStartRequest.
     * 
     * @param changeListID
     */
    public void setChangeListID(int changeListID) {
        this.changeListID = changeListID;
    }


    /**
     * Gets the cleanCheckout value for this BuildStartRequest.
     * 
     * @return cleanCheckout
     */
    public boolean isCleanCheckout() {
        return cleanCheckout;
    }


    /**
     * Sets the cleanCheckout value for this BuildStartRequest.
     * 
     * @param cleanCheckout
     */
    public void setCleanCheckout(boolean cleanCheckout) {
        this.cleanCheckout = cleanCheckout;
    }


    /**
     * Gets the ignoreSerialization value for this BuildStartRequest.
     * 
     * @return ignoreSerialization
     */
    public boolean isIgnoreSerialization() {
        return ignoreSerialization;
    }


    /**
     * Sets the ignoreSerialization value for this BuildStartRequest.
     * 
     * @param ignoreSerialization
     */
    public void setIgnoreSerialization(boolean ignoreSerialization) {
        this.ignoreSerialization = ignoreSerialization;
    }


    /**
     * Gets the label value for this BuildStartRequest.
     * 
     * @return label
     */
    public java.lang.String getLabel() {
        return label;
    }


    /**
     * Sets the label value for this BuildStartRequest.
     * 
     * @param label
     */
    public void setLabel(java.lang.String label) {
        this.label = label;
    }


    /**
     * Gets the note value for this BuildStartRequest.
     * 
     * @return note
     */
    public java.lang.String getNote() {
        return note;
    }


    /**
     * Sets the note value for this BuildStartRequest.
     * 
     * @param note
     */
    public void setNote(java.lang.String note) {
        this.note = note;
    }


    /**
     * Gets the parameterList value for this BuildStartRequest.
     * 
     * @return parameterList
     */
    public org.parabuild.ci.webservice.BuildStartRequestParameter[] getParameterList() {
        return parameterList;
    }


    /**
     * Sets the parameterList value for this BuildStartRequest.
     * 
     * @param parameterList
     */
    public void setParameterList(org.parabuild.ci.webservice.BuildStartRequestParameter[] parameterList) {
        this.parameterList = parameterList;
    }


    /**
     * Gets the pinResult value for this BuildStartRequest.
     * 
     * @return pinResult
     */
    public boolean isPinResult() {
        return pinResult;
    }


    /**
     * Sets the pinResult value for this BuildStartRequest.
     * 
     * @param pinResult
     */
    public void setPinResult(boolean pinResult) {
        this.pinResult = pinResult;
    }


    /**
     * Gets the requestType value for this BuildStartRequest.
     * 
     * @return requestType
     */
    public int getRequestType() {
        return requestType;
    }


    /**
     * Sets the requestType value for this BuildStartRequest.
     * 
     * @param requestType
     */
    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }


    /**
     * Gets the sourceControlSettingsOverrides value for this BuildStartRequest.
     * 
     * @return sourceControlSettingsOverrides
     */
    public org.parabuild.ci.webservice.SourceControlSettingOverride[] getSourceControlSettingsOverrides() {
        return sourceControlSettingsOverrides;
    }


    /**
     * Sets the sourceControlSettingsOverrides value for this BuildStartRequest.
     * 
     * @param sourceControlSettingsOverrides
     */
    public void setSourceControlSettingsOverrides(org.parabuild.ci.webservice.SourceControlSettingOverride[] sourceControlSettingsOverrides) {
        this.sourceControlSettingsOverrides = sourceControlSettingsOverrides;
    }


    /**
     * Gets the userID value for this BuildStartRequest.
     * 
     * @return userID
     */
    public int getUserID() {
        return userID;
    }


    /**
     * Sets the userID value for this BuildStartRequest.
     * 
     * @param userID
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }


    /**
     * Gets the versionCounter value for this BuildStartRequest.
     * 
     * @return versionCounter
     */
    public int getVersionCounter() {
        return versionCounter;
    }


    /**
     * Sets the versionCounter value for this BuildStartRequest.
     * 
     * @param versionCounter
     */
    public void setVersionCounter(int versionCounter) {
        this.versionCounter = versionCounter;
    }


    /**
     * Gets the versionTemplate value for this BuildStartRequest.
     * 
     * @return versionTemplate
     */
    public java.lang.String getVersionTemplate() {
        return versionTemplate;
    }


    /**
     * Sets the versionTemplate value for this BuildStartRequest.
     * 
     * @param versionTemplate
     */
    public void setVersionTemplate(java.lang.String versionTemplate) {
        this.versionTemplate = versionTemplate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BuildStartRequest)) return false;
        BuildStartRequest other = (BuildStartRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.agentHost==null && other.getAgentHost()==null) || 
             (this.agentHost!=null &&
              this.agentHost.equals(other.getAgentHost()))) &&
            this.buildRunID == other.getBuildRunID() &&
            this.changeListID == other.getChangeListID() &&
            this.cleanCheckout == other.isCleanCheckout() &&
            this.ignoreSerialization == other.isIgnoreSerialization() &&
            ((this.label==null && other.getLabel()==null) || 
             (this.label!=null &&
              this.label.equals(other.getLabel()))) &&
            ((this.note==null && other.getNote()==null) || 
             (this.note!=null &&
              this.note.equals(other.getNote()))) &&
            ((this.parameterList==null && other.getParameterList()==null) || 
             (this.parameterList!=null &&
              java.util.Arrays.equals(this.parameterList, other.getParameterList()))) &&
            this.pinResult == other.isPinResult() &&
            this.requestType == other.getRequestType() &&
            ((this.sourceControlSettingsOverrides==null && other.getSourceControlSettingsOverrides()==null) || 
             (this.sourceControlSettingsOverrides!=null &&
              java.util.Arrays.equals(this.sourceControlSettingsOverrides, other.getSourceControlSettingsOverrides()))) &&
            this.userID == other.getUserID() &&
            this.versionCounter == other.getVersionCounter() &&
            ((this.versionTemplate==null && other.getVersionTemplate()==null) || 
             (this.versionTemplate!=null &&
              this.versionTemplate.equals(other.getVersionTemplate())));
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
        if (getAgentHost() != null) {
            _hashCode += getAgentHost().hashCode();
        }
        _hashCode += getBuildRunID();
        _hashCode += getChangeListID();
        _hashCode += (isCleanCheckout() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isIgnoreSerialization() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getLabel() != null) {
            _hashCode += getLabel().hashCode();
        }
        if (getNote() != null) {
            _hashCode += getNote().hashCode();
        }
        if (getParameterList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParameterList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParameterList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += (isPinResult() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getRequestType();
        if (getSourceControlSettingsOverrides() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSourceControlSettingsOverrides());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSourceControlSettingsOverrides(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getUserID();
        _hashCode += getVersionCounter();
        if (getVersionTemplate() != null) {
            _hashCode += getVersionTemplate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BuildStartRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStartRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agentHost");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agentHost"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "AgentHost"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buildRunID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buildRunID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changeListID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "changeListID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cleanCheckout");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cleanCheckout"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ignoreSerialization");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ignoreSerialization"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("label");
        elemField.setXmlName(new javax.xml.namespace.QName("", "label"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("note");
        elemField.setXmlName(new javax.xml.namespace.QName("", "note"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parameterList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "parameterList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStartRequestParameter"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pinResult");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pinResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "requestType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sourceControlSettingsOverrides");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sourceControlSettingsOverrides"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "SourceControlSettingOverride"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("versionCounter");
        elemField.setXmlName(new javax.xml.namespace.QName("", "versionCounter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("versionTemplate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "versionTemplate"));
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
