/**
 * StepRun.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class StepRun  implements java.io.Serializable {
    private int ID;
    private int buildRunID;
    private boolean complete;
    private int duration;
    private java.util.Calendar finishedAt;
    private java.lang.String name;
    private java.lang.String resultDescription;
    private byte resultID;
    private java.util.Calendar startedAt;
    private boolean successful;
    private long timeStamp;

    public StepRun() {
    }

    public StepRun(
           int ID,
           int buildRunID,
           boolean complete,
           int duration,
           java.util.Calendar finishedAt,
           java.lang.String name,
           java.lang.String resultDescription,
           byte resultID,
           java.util.Calendar startedAt,
           boolean successful,
           long timeStamp) {
           this.ID = ID;
           this.buildRunID = buildRunID;
           this.complete = complete;
           this.duration = duration;
           this.finishedAt = finishedAt;
           this.name = name;
           this.resultDescription = resultDescription;
           this.resultID = resultID;
           this.startedAt = startedAt;
           this.successful = successful;
           this.timeStamp = timeStamp;
    }


    /**
     * Gets the ID value for this StepRun.
     * 
     * @return ID
     */
    public int getID() {
        return ID;
    }


    /**
     * Sets the ID value for this StepRun.
     * 
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }


    /**
     * Gets the buildRunID value for this StepRun.
     * 
     * @return buildRunID
     */
    public int getBuildRunID() {
        return buildRunID;
    }


    /**
     * Sets the buildRunID value for this StepRun.
     * 
     * @param buildRunID
     */
    public void setBuildRunID(int buildRunID) {
        this.buildRunID = buildRunID;
    }


    /**
     * Gets the complete value for this StepRun.
     * 
     * @return complete
     */
    public boolean isComplete() {
        return complete;
    }


    /**
     * Sets the complete value for this StepRun.
     * 
     * @param complete
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }


    /**
     * Gets the duration value for this StepRun.
     * 
     * @return duration
     */
    public int getDuration() {
        return duration;
    }


    /**
     * Sets the duration value for this StepRun.
     * 
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }


    /**
     * Gets the finishedAt value for this StepRun.
     * 
     * @return finishedAt
     */
    public java.util.Calendar getFinishedAt() {
        return finishedAt;
    }


    /**
     * Sets the finishedAt value for this StepRun.
     * 
     * @param finishedAt
     */
    public void setFinishedAt(java.util.Calendar finishedAt) {
        this.finishedAt = finishedAt;
    }


    /**
     * Gets the name value for this StepRun.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this StepRun.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the resultDescription value for this StepRun.
     * 
     * @return resultDescription
     */
    public java.lang.String getResultDescription() {
        return resultDescription;
    }


    /**
     * Sets the resultDescription value for this StepRun.
     * 
     * @param resultDescription
     */
    public void setResultDescription(java.lang.String resultDescription) {
        this.resultDescription = resultDescription;
    }


    /**
     * Gets the resultID value for this StepRun.
     * 
     * @return resultID
     */
    public byte getResultID() {
        return resultID;
    }


    /**
     * Sets the resultID value for this StepRun.
     * 
     * @param resultID
     */
    public void setResultID(byte resultID) {
        this.resultID = resultID;
    }


    /**
     * Gets the startedAt value for this StepRun.
     * 
     * @return startedAt
     */
    public java.util.Calendar getStartedAt() {
        return startedAt;
    }


    /**
     * Sets the startedAt value for this StepRun.
     * 
     * @param startedAt
     */
    public void setStartedAt(java.util.Calendar startedAt) {
        this.startedAt = startedAt;
    }


    /**
     * Gets the successful value for this StepRun.
     * 
     * @return successful
     */
    public boolean isSuccessful() {
        return successful;
    }


    /**
     * Sets the successful value for this StepRun.
     * 
     * @param successful
     */
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }


    /**
     * Gets the timeStamp value for this StepRun.
     * 
     * @return timeStamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }


    /**
     * Sets the timeStamp value for this StepRun.
     * 
     * @param timeStamp
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StepRun)) return false;
        StepRun other = (StepRun) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.ID == other.getID() &&
            this.buildRunID == other.getBuildRunID() &&
            this.complete == other.isComplete() &&
            this.duration == other.getDuration() &&
            ((this.finishedAt==null && other.getFinishedAt()==null) || 
             (this.finishedAt!=null &&
              this.finishedAt.equals(other.getFinishedAt()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.resultDescription==null && other.getResultDescription()==null) || 
             (this.resultDescription!=null &&
              this.resultDescription.equals(other.getResultDescription()))) &&
            this.resultID == other.getResultID() &&
            ((this.startedAt==null && other.getStartedAt()==null) || 
             (this.startedAt!=null &&
              this.startedAt.equals(other.getStartedAt()))) &&
            this.successful == other.isSuccessful() &&
            this.timeStamp == other.getTimeStamp();
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
        _hashCode += getBuildRunID();
        _hashCode += (isComplete() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getDuration();
        if (getFinishedAt() != null) {
            _hashCode += getFinishedAt().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getResultDescription() != null) {
            _hashCode += getResultDescription().hashCode();
        }
        _hashCode += getResultID();
        if (getStartedAt() != null) {
            _hashCode += getStartedAt().hashCode();
        }
        _hashCode += (isSuccessful() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += new Long(getTimeStamp()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StepRun.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "StepRun"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buildRunID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buildRunID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("complete");
        elemField.setXmlName(new javax.xml.namespace.QName("", "complete"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("duration");
        elemField.setXmlName(new javax.xml.namespace.QName("", "duration"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("finishedAt");
        elemField.setXmlName(new javax.xml.namespace.QName("", "finishedAt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("", "resultDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "resultID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startedAt");
        elemField.setXmlName(new javax.xml.namespace.QName("", "startedAt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("successful");
        elemField.setXmlName(new javax.xml.namespace.QName("", "successful"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeStamp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timeStamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
