/**
 * BuildSequence.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class BuildSequence  implements java.io.Serializable {
    private int buildID;
    private boolean continueOnFailure;
    private boolean disabled;
    private java.lang.String failurePatterns;
    private boolean finalizer;
    private boolean initializer;
    private int lineNumber;
    private boolean respectErrorCode;
    private java.lang.String scriptText;
    private int sequenceID;
    private java.lang.String stepName;
    private java.lang.String successPatterns;
    private long timeStamp;
    private int timeoutMins;
    private byte type;

    public BuildSequence() {
    }

    public BuildSequence(
           int buildID,
           boolean continueOnFailure,
           boolean disabled,
           java.lang.String failurePatterns,
           boolean finalizer,
           boolean initializer,
           int lineNumber,
           boolean respectErrorCode,
           java.lang.String scriptText,
           int sequenceID,
           java.lang.String stepName,
           java.lang.String successPatterns,
           long timeStamp,
           int timeoutMins,
           byte type) {
           this.buildID = buildID;
           this.continueOnFailure = continueOnFailure;
           this.disabled = disabled;
           this.failurePatterns = failurePatterns;
           this.finalizer = finalizer;
           this.initializer = initializer;
           this.lineNumber = lineNumber;
           this.respectErrorCode = respectErrorCode;
           this.scriptText = scriptText;
           this.sequenceID = sequenceID;
           this.stepName = stepName;
           this.successPatterns = successPatterns;
           this.timeStamp = timeStamp;
           this.timeoutMins = timeoutMins;
           this.type = type;
    }


    /**
     * Gets the buildID value for this BuildSequence.
     * 
     * @return buildID
     */
    public int getBuildID() {
        return buildID;
    }


    /**
     * Sets the buildID value for this BuildSequence.
     * 
     * @param buildID
     */
    public void setBuildID(int buildID) {
        this.buildID = buildID;
    }


    /**
     * Gets the continueOnFailure value for this BuildSequence.
     * 
     * @return continueOnFailure
     */
    public boolean isContinueOnFailure() {
        return continueOnFailure;
    }


    /**
     * Sets the continueOnFailure value for this BuildSequence.
     * 
     * @param continueOnFailure
     */
    public void setContinueOnFailure(boolean continueOnFailure) {
        this.continueOnFailure = continueOnFailure;
    }


    /**
     * Gets the disabled value for this BuildSequence.
     * 
     * @return disabled
     */
    public boolean isDisabled() {
        return disabled;
    }


    /**
     * Sets the disabled value for this BuildSequence.
     * 
     * @param disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }


    /**
     * Gets the failurePatterns value for this BuildSequence.
     * 
     * @return failurePatterns
     */
    public java.lang.String getFailurePatterns() {
        return failurePatterns;
    }


    /**
     * Sets the failurePatterns value for this BuildSequence.
     * 
     * @param failurePatterns
     */
    public void setFailurePatterns(java.lang.String failurePatterns) {
        this.failurePatterns = failurePatterns;
    }


    /**
     * Gets the finalizer value for this BuildSequence.
     * 
     * @return finalizer
     */
    public boolean isFinalizer() {
        return finalizer;
    }


    /**
     * Sets the finalizer value for this BuildSequence.
     * 
     * @param finalizer
     */
    public void setFinalizer(boolean finalizer) {
        this.finalizer = finalizer;
    }


    /**
     * Gets the initializer value for this BuildSequence.
     * 
     * @return initializer
     */
    public boolean isInitializer() {
        return initializer;
    }


    /**
     * Sets the initializer value for this BuildSequence.
     * 
     * @param initializer
     */
    public void setInitializer(boolean initializer) {
        this.initializer = initializer;
    }


    /**
     * Gets the lineNumber value for this BuildSequence.
     * 
     * @return lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }


    /**
     * Sets the lineNumber value for this BuildSequence.
     * 
     * @param lineNumber
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }


    /**
     * Gets the respectErrorCode value for this BuildSequence.
     * 
     * @return respectErrorCode
     */
    public boolean isRespectErrorCode() {
        return respectErrorCode;
    }


    /**
     * Sets the respectErrorCode value for this BuildSequence.
     * 
     * @param respectErrorCode
     */
    public void setRespectErrorCode(boolean respectErrorCode) {
        this.respectErrorCode = respectErrorCode;
    }


    /**
     * Gets the scriptText value for this BuildSequence.
     * 
     * @return scriptText
     */
    public java.lang.String getScriptText() {
        return scriptText;
    }


    /**
     * Sets the scriptText value for this BuildSequence.
     * 
     * @param scriptText
     */
    public void setScriptText(java.lang.String scriptText) {
        this.scriptText = scriptText;
    }


    /**
     * Gets the sequenceID value for this BuildSequence.
     * 
     * @return sequenceID
     */
    public int getSequenceID() {
        return sequenceID;
    }


    /**
     * Sets the sequenceID value for this BuildSequence.
     * 
     * @param sequenceID
     */
    public void setSequenceID(int sequenceID) {
        this.sequenceID = sequenceID;
    }


    /**
     * Gets the stepName value for this BuildSequence.
     * 
     * @return stepName
     */
    public java.lang.String getStepName() {
        return stepName;
    }


    /**
     * Sets the stepName value for this BuildSequence.
     * 
     * @param stepName
     */
    public void setStepName(java.lang.String stepName) {
        this.stepName = stepName;
    }


    /**
     * Gets the successPatterns value for this BuildSequence.
     * 
     * @return successPatterns
     */
    public java.lang.String getSuccessPatterns() {
        return successPatterns;
    }


    /**
     * Sets the successPatterns value for this BuildSequence.
     * 
     * @param successPatterns
     */
    public void setSuccessPatterns(java.lang.String successPatterns) {
        this.successPatterns = successPatterns;
    }


    /**
     * Gets the timeStamp value for this BuildSequence.
     * 
     * @return timeStamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }


    /**
     * Sets the timeStamp value for this BuildSequence.
     * 
     * @param timeStamp
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }


    /**
     * Gets the timeoutMins value for this BuildSequence.
     * 
     * @return timeoutMins
     */
    public int getTimeoutMins() {
        return timeoutMins;
    }


    /**
     * Sets the timeoutMins value for this BuildSequence.
     * 
     * @param timeoutMins
     */
    public void setTimeoutMins(int timeoutMins) {
        this.timeoutMins = timeoutMins;
    }


    /**
     * Gets the type value for this BuildSequence.
     * 
     * @return type
     */
    public byte getType() {
        return type;
    }


    /**
     * Sets the type value for this BuildSequence.
     * 
     * @param type
     */
    public void setType(byte type) {
        this.type = type;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BuildSequence)) return false;
        BuildSequence other = (BuildSequence) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.buildID == other.getBuildID() &&
            this.continueOnFailure == other.isContinueOnFailure() &&
            this.disabled == other.isDisabled() &&
            ((this.failurePatterns==null && other.getFailurePatterns()==null) || 
             (this.failurePatterns!=null &&
              this.failurePatterns.equals(other.getFailurePatterns()))) &&
            this.finalizer == other.isFinalizer() &&
            this.initializer == other.isInitializer() &&
            this.lineNumber == other.getLineNumber() &&
            this.respectErrorCode == other.isRespectErrorCode() &&
            ((this.scriptText==null && other.getScriptText()==null) || 
             (this.scriptText!=null &&
              this.scriptText.equals(other.getScriptText()))) &&
            this.sequenceID == other.getSequenceID() &&
            ((this.stepName==null && other.getStepName()==null) || 
             (this.stepName!=null &&
              this.stepName.equals(other.getStepName()))) &&
            ((this.successPatterns==null && other.getSuccessPatterns()==null) || 
             (this.successPatterns!=null &&
              this.successPatterns.equals(other.getSuccessPatterns()))) &&
            this.timeStamp == other.getTimeStamp() &&
            this.timeoutMins == other.getTimeoutMins() &&
            this.type == other.getType();
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
        _hashCode += (isContinueOnFailure() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isDisabled() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getFailurePatterns() != null) {
            _hashCode += getFailurePatterns().hashCode();
        }
        _hashCode += (isFinalizer() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isInitializer() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getLineNumber();
        _hashCode += (isRespectErrorCode() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getScriptText() != null) {
            _hashCode += getScriptText().hashCode();
        }
        _hashCode += getSequenceID();
        if (getStepName() != null) {
            _hashCode += getStepName().hashCode();
        }
        if (getSuccessPatterns() != null) {
            _hashCode += getSuccessPatterns().hashCode();
        }
        _hashCode += new Long(getTimeStamp()).hashCode();
        _hashCode += getTimeoutMins();
        _hashCode += getType();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BuildSequence.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildSequence"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buildID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buildID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("continueOnFailure");
        elemField.setXmlName(new javax.xml.namespace.QName("", "continueOnFailure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("disabled");
        elemField.setXmlName(new javax.xml.namespace.QName("", "disabled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("failurePatterns");
        elemField.setXmlName(new javax.xml.namespace.QName("", "failurePatterns"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("finalizer");
        elemField.setXmlName(new javax.xml.namespace.QName("", "finalizer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("initializer");
        elemField.setXmlName(new javax.xml.namespace.QName("", "initializer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lineNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lineNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("respectErrorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "respectErrorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scriptText");
        elemField.setXmlName(new javax.xml.namespace.QName("", "scriptText"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sequenceID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sequenceID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stepName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "stepName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("successPatterns");
        elemField.setXmlName(new javax.xml.namespace.QName("", "successPatterns"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeStamp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timeStamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeoutMins");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timeoutMins"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
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
