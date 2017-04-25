/**
 * BuildStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class BuildStatus  implements java.io.Serializable {
    private byte access;
    private int activeBuildID;
    private java.lang.String buildName;
    private int currentlyRunnigBuildRunID;
    private int currentlyRunningBuildConfigID;
    private int currentlyRunningBuildNumber;
    private java.lang.String currentlyRunningChangeListNumber;
    private java.lang.String currentlyRunningOnBuildHost;
    private int currentlyRunningStepID;
    private int lastCleanBuildRunID;
    private int lastCompleteBuildRun;
    private java.util.Calendar nextBuildTime;
    private byte schedule;
    private byte sourceControl;
    private int status;

    public BuildStatus() {
    }

    public BuildStatus(
           byte access,
           int activeBuildID,
           java.lang.String buildName,
           int currentlyRunnigBuildRunID,
           int currentlyRunningBuildConfigID,
           int currentlyRunningBuildNumber,
           java.lang.String currentlyRunningChangeListNumber,
           java.lang.String currentlyRunningOnBuildHost,
           int currentlyRunningStepID,
           int lastCleanBuildRunID,
           int lastCompleteBuildRun,
           java.util.Calendar nextBuildTime,
           byte schedule,
           byte sourceControl,
           int status) {
           this.access = access;
           this.activeBuildID = activeBuildID;
           this.buildName = buildName;
           this.currentlyRunnigBuildRunID = currentlyRunnigBuildRunID;
           this.currentlyRunningBuildConfigID = currentlyRunningBuildConfigID;
           this.currentlyRunningBuildNumber = currentlyRunningBuildNumber;
           this.currentlyRunningChangeListNumber = currentlyRunningChangeListNumber;
           this.currentlyRunningOnBuildHost = currentlyRunningOnBuildHost;
           this.currentlyRunningStepID = currentlyRunningStepID;
           this.lastCleanBuildRunID = lastCleanBuildRunID;
           this.lastCompleteBuildRun = lastCompleteBuildRun;
           this.nextBuildTime = nextBuildTime;
           this.schedule = schedule;
           this.sourceControl = sourceControl;
           this.status = status;
    }


    /**
     * Gets the access value for this BuildStatus.
     * 
     * @return access
     */
    public byte getAccess() {
        return access;
    }


    /**
     * Sets the access value for this BuildStatus.
     * 
     * @param access
     */
    public void setAccess(byte access) {
        this.access = access;
    }


    /**
     * Gets the activeBuildID value for this BuildStatus.
     * 
     * @return activeBuildID
     */
    public int getActiveBuildID() {
        return activeBuildID;
    }


    /**
     * Sets the activeBuildID value for this BuildStatus.
     * 
     * @param activeBuildID
     */
    public void setActiveBuildID(int activeBuildID) {
        this.activeBuildID = activeBuildID;
    }


    /**
     * Gets the buildName value for this BuildStatus.
     * 
     * @return buildName
     */
    public java.lang.String getBuildName() {
        return buildName;
    }


    /**
     * Sets the buildName value for this BuildStatus.
     * 
     * @param buildName
     */
    public void setBuildName(java.lang.String buildName) {
        this.buildName = buildName;
    }


    /**
     * Gets the currentlyRunnigBuildRunID value for this BuildStatus.
     * 
     * @return currentlyRunnigBuildRunID
     */
    public int getCurrentlyRunnigBuildRunID() {
        return currentlyRunnigBuildRunID;
    }


    /**
     * Sets the currentlyRunnigBuildRunID value for this BuildStatus.
     * 
     * @param currentlyRunnigBuildRunID
     */
    public void setCurrentlyRunnigBuildRunID(int currentlyRunnigBuildRunID) {
        this.currentlyRunnigBuildRunID = currentlyRunnigBuildRunID;
    }


    /**
     * Gets the currentlyRunningBuildConfigID value for this BuildStatus.
     * 
     * @return currentlyRunningBuildConfigID
     */
    public int getCurrentlyRunningBuildConfigID() {
        return currentlyRunningBuildConfigID;
    }


    /**
     * Sets the currentlyRunningBuildConfigID value for this BuildStatus.
     * 
     * @param currentlyRunningBuildConfigID
     */
    public void setCurrentlyRunningBuildConfigID(int currentlyRunningBuildConfigID) {
        this.currentlyRunningBuildConfigID = currentlyRunningBuildConfigID;
    }


    /**
     * Gets the currentlyRunningBuildNumber value for this BuildStatus.
     * 
     * @return currentlyRunningBuildNumber
     */
    public int getCurrentlyRunningBuildNumber() {
        return currentlyRunningBuildNumber;
    }


    /**
     * Sets the currentlyRunningBuildNumber value for this BuildStatus.
     * 
     * @param currentlyRunningBuildNumber
     */
    public void setCurrentlyRunningBuildNumber(int currentlyRunningBuildNumber) {
        this.currentlyRunningBuildNumber = currentlyRunningBuildNumber;
    }


    /**
     * Gets the currentlyRunningChangeListNumber value for this BuildStatus.
     * 
     * @return currentlyRunningChangeListNumber
     */
    public java.lang.String getCurrentlyRunningChangeListNumber() {
        return currentlyRunningChangeListNumber;
    }


    /**
     * Sets the currentlyRunningChangeListNumber value for this BuildStatus.
     * 
     * @param currentlyRunningChangeListNumber
     */
    public void setCurrentlyRunningChangeListNumber(java.lang.String currentlyRunningChangeListNumber) {
        this.currentlyRunningChangeListNumber = currentlyRunningChangeListNumber;
    }


    /**
     * Gets the currentlyRunningOnBuildHost value for this BuildStatus.
     * 
     * @return currentlyRunningOnBuildHost
     */
    public java.lang.String getCurrentlyRunningOnBuildHost() {
        return currentlyRunningOnBuildHost;
    }


    /**
     * Sets the currentlyRunningOnBuildHost value for this BuildStatus.
     * 
     * @param currentlyRunningOnBuildHost
     */
    public void setCurrentlyRunningOnBuildHost(java.lang.String currentlyRunningOnBuildHost) {
        this.currentlyRunningOnBuildHost = currentlyRunningOnBuildHost;
    }


    /**
     * Gets the currentlyRunningStepID value for this BuildStatus.
     * 
     * @return currentlyRunningStepID
     */
    public int getCurrentlyRunningStepID() {
        return currentlyRunningStepID;
    }


    /**
     * Sets the currentlyRunningStepID value for this BuildStatus.
     * 
     * @param currentlyRunningStepID
     */
    public void setCurrentlyRunningStepID(int currentlyRunningStepID) {
        this.currentlyRunningStepID = currentlyRunningStepID;
    }


    /**
     * Gets the lastCleanBuildRunID value for this BuildStatus.
     * 
     * @return lastCleanBuildRunID
     */
    public int getLastCleanBuildRunID() {
        return lastCleanBuildRunID;
    }


    /**
     * Sets the lastCleanBuildRunID value for this BuildStatus.
     * 
     * @param lastCleanBuildRunID
     */
    public void setLastCleanBuildRunID(int lastCleanBuildRunID) {
        this.lastCleanBuildRunID = lastCleanBuildRunID;
    }


    /**
     * Gets the lastCompleteBuildRun value for this BuildStatus.
     * 
     * @return lastCompleteBuildRun
     */
    public int getLastCompleteBuildRun() {
        return lastCompleteBuildRun;
    }


    /**
     * Sets the lastCompleteBuildRun value for this BuildStatus.
     * 
     * @param lastCompleteBuildRun
     */
    public void setLastCompleteBuildRun(int lastCompleteBuildRun) {
        this.lastCompleteBuildRun = lastCompleteBuildRun;
    }


    /**
     * Gets the nextBuildTime value for this BuildStatus.
     * 
     * @return nextBuildTime
     */
    public java.util.Calendar getNextBuildTime() {
        return nextBuildTime;
    }


    /**
     * Sets the nextBuildTime value for this BuildStatus.
     * 
     * @param nextBuildTime
     */
    public void setNextBuildTime(java.util.Calendar nextBuildTime) {
        this.nextBuildTime = nextBuildTime;
    }


    /**
     * Gets the schedule value for this BuildStatus.
     * 
     * @return schedule
     */
    public byte getSchedule() {
        return schedule;
    }


    /**
     * Sets the schedule value for this BuildStatus.
     * 
     * @param schedule
     */
    public void setSchedule(byte schedule) {
        this.schedule = schedule;
    }


    /**
     * Gets the sourceControl value for this BuildStatus.
     * 
     * @return sourceControl
     */
    public byte getSourceControl() {
        return sourceControl;
    }


    /**
     * Sets the sourceControl value for this BuildStatus.
     * 
     * @param sourceControl
     */
    public void setSourceControl(byte sourceControl) {
        this.sourceControl = sourceControl;
    }


    /**
     * Gets the status value for this BuildStatus.
     * 
     * @return status
     */
    public int getStatus() {
        return status;
    }


    /**
     * Sets the status value for this BuildStatus.
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BuildStatus)) return false;
        BuildStatus other = (BuildStatus) obj;
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
            ((this.buildName==null && other.getBuildName()==null) || 
             (this.buildName!=null &&
              this.buildName.equals(other.getBuildName()))) &&
            this.currentlyRunnigBuildRunID == other.getCurrentlyRunnigBuildRunID() &&
            this.currentlyRunningBuildConfigID == other.getCurrentlyRunningBuildConfigID() &&
            this.currentlyRunningBuildNumber == other.getCurrentlyRunningBuildNumber() &&
            ((this.currentlyRunningChangeListNumber==null && other.getCurrentlyRunningChangeListNumber()==null) || 
             (this.currentlyRunningChangeListNumber!=null &&
              this.currentlyRunningChangeListNumber.equals(other.getCurrentlyRunningChangeListNumber()))) &&
            ((this.currentlyRunningOnBuildHost==null && other.getCurrentlyRunningOnBuildHost()==null) || 
             (this.currentlyRunningOnBuildHost!=null &&
              this.currentlyRunningOnBuildHost.equals(other.getCurrentlyRunningOnBuildHost()))) &&
            this.currentlyRunningStepID == other.getCurrentlyRunningStepID() &&
            this.lastCleanBuildRunID == other.getLastCleanBuildRunID() &&
            this.lastCompleteBuildRun == other.getLastCompleteBuildRun() &&
            ((this.nextBuildTime==null && other.getNextBuildTime()==null) || 
             (this.nextBuildTime!=null &&
              this.nextBuildTime.equals(other.getNextBuildTime()))) &&
            this.schedule == other.getSchedule() &&
            this.sourceControl == other.getSourceControl() &&
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
        _hashCode += getAccess();
        _hashCode += getActiveBuildID();
        if (getBuildName() != null) {
            _hashCode += getBuildName().hashCode();
        }
        _hashCode += getCurrentlyRunnigBuildRunID();
        _hashCode += getCurrentlyRunningBuildConfigID();
        _hashCode += getCurrentlyRunningBuildNumber();
        if (getCurrentlyRunningChangeListNumber() != null) {
            _hashCode += getCurrentlyRunningChangeListNumber().hashCode();
        }
        if (getCurrentlyRunningOnBuildHost() != null) {
            _hashCode += getCurrentlyRunningOnBuildHost().hashCode();
        }
        _hashCode += getCurrentlyRunningStepID();
        _hashCode += getLastCleanBuildRunID();
        _hashCode += getLastCompleteBuildRun();
        if (getNextBuildTime() != null) {
            _hashCode += getNextBuildTime().hashCode();
        }
        _hashCode += getSchedule();
        _hashCode += getSourceControl();
        _hashCode += getStatus();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BuildStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStatus"));
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
        elemField.setFieldName("buildName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buildName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentlyRunnigBuildRunID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentlyRunnigBuildRunID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentlyRunningBuildConfigID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentlyRunningBuildConfigID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentlyRunningBuildNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentlyRunningBuildNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentlyRunningChangeListNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentlyRunningChangeListNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentlyRunningOnBuildHost");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentlyRunningOnBuildHost"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentlyRunningStepID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentlyRunningStepID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastCleanBuildRunID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastCleanBuildRunID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastCompleteBuildRun");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastCompleteBuildRun"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nextBuildTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "nextBuildTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("schedule");
        elemField.setXmlName(new javax.xml.namespace.QName("", "schedule"));
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
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
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
