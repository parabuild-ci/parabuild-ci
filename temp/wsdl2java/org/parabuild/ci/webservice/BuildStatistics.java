/**
 * BuildStatistics.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class BuildStatistics  implements java.io.Serializable {
    private int ID;
    private int activeBuildID;
    private int changeListCount;
    private int failedBuildCount;
    private int failedBuildPercent;
    private int issueCount;
    private java.util.Calendar sampleTime;
    private int successfulBuildCount;
    private int successfulBuildPercent;
    private int totalBuildCount;

    public BuildStatistics() {
    }

    public BuildStatistics(
           int ID,
           int activeBuildID,
           int changeListCount,
           int failedBuildCount,
           int failedBuildPercent,
           int issueCount,
           java.util.Calendar sampleTime,
           int successfulBuildCount,
           int successfulBuildPercent,
           int totalBuildCount) {
           this.ID = ID;
           this.activeBuildID = activeBuildID;
           this.changeListCount = changeListCount;
           this.failedBuildCount = failedBuildCount;
           this.failedBuildPercent = failedBuildPercent;
           this.issueCount = issueCount;
           this.sampleTime = sampleTime;
           this.successfulBuildCount = successfulBuildCount;
           this.successfulBuildPercent = successfulBuildPercent;
           this.totalBuildCount = totalBuildCount;
    }


    /**
     * Gets the ID value for this BuildStatistics.
     * 
     * @return ID
     */
    public int getID() {
        return ID;
    }


    /**
     * Sets the ID value for this BuildStatistics.
     * 
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }


    /**
     * Gets the activeBuildID value for this BuildStatistics.
     * 
     * @return activeBuildID
     */
    public int getActiveBuildID() {
        return activeBuildID;
    }


    /**
     * Sets the activeBuildID value for this BuildStatistics.
     * 
     * @param activeBuildID
     */
    public void setActiveBuildID(int activeBuildID) {
        this.activeBuildID = activeBuildID;
    }


    /**
     * Gets the changeListCount value for this BuildStatistics.
     * 
     * @return changeListCount
     */
    public int getChangeListCount() {
        return changeListCount;
    }


    /**
     * Sets the changeListCount value for this BuildStatistics.
     * 
     * @param changeListCount
     */
    public void setChangeListCount(int changeListCount) {
        this.changeListCount = changeListCount;
    }


    /**
     * Gets the failedBuildCount value for this BuildStatistics.
     * 
     * @return failedBuildCount
     */
    public int getFailedBuildCount() {
        return failedBuildCount;
    }


    /**
     * Sets the failedBuildCount value for this BuildStatistics.
     * 
     * @param failedBuildCount
     */
    public void setFailedBuildCount(int failedBuildCount) {
        this.failedBuildCount = failedBuildCount;
    }


    /**
     * Gets the failedBuildPercent value for this BuildStatistics.
     * 
     * @return failedBuildPercent
     */
    public int getFailedBuildPercent() {
        return failedBuildPercent;
    }


    /**
     * Sets the failedBuildPercent value for this BuildStatistics.
     * 
     * @param failedBuildPercent
     */
    public void setFailedBuildPercent(int failedBuildPercent) {
        this.failedBuildPercent = failedBuildPercent;
    }


    /**
     * Gets the issueCount value for this BuildStatistics.
     * 
     * @return issueCount
     */
    public int getIssueCount() {
        return issueCount;
    }


    /**
     * Sets the issueCount value for this BuildStatistics.
     * 
     * @param issueCount
     */
    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }


    /**
     * Gets the sampleTime value for this BuildStatistics.
     * 
     * @return sampleTime
     */
    public java.util.Calendar getSampleTime() {
        return sampleTime;
    }


    /**
     * Sets the sampleTime value for this BuildStatistics.
     * 
     * @param sampleTime
     */
    public void setSampleTime(java.util.Calendar sampleTime) {
        this.sampleTime = sampleTime;
    }


    /**
     * Gets the successfulBuildCount value for this BuildStatistics.
     * 
     * @return successfulBuildCount
     */
    public int getSuccessfulBuildCount() {
        return successfulBuildCount;
    }


    /**
     * Sets the successfulBuildCount value for this BuildStatistics.
     * 
     * @param successfulBuildCount
     */
    public void setSuccessfulBuildCount(int successfulBuildCount) {
        this.successfulBuildCount = successfulBuildCount;
    }


    /**
     * Gets the successfulBuildPercent value for this BuildStatistics.
     * 
     * @return successfulBuildPercent
     */
    public int getSuccessfulBuildPercent() {
        return successfulBuildPercent;
    }


    /**
     * Sets the successfulBuildPercent value for this BuildStatistics.
     * 
     * @param successfulBuildPercent
     */
    public void setSuccessfulBuildPercent(int successfulBuildPercent) {
        this.successfulBuildPercent = successfulBuildPercent;
    }


    /**
     * Gets the totalBuildCount value for this BuildStatistics.
     * 
     * @return totalBuildCount
     */
    public int getTotalBuildCount() {
        return totalBuildCount;
    }


    /**
     * Sets the totalBuildCount value for this BuildStatistics.
     * 
     * @param totalBuildCount
     */
    public void setTotalBuildCount(int totalBuildCount) {
        this.totalBuildCount = totalBuildCount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BuildStatistics)) return false;
        BuildStatistics other = (BuildStatistics) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.ID == other.getID() &&
            this.activeBuildID == other.getActiveBuildID() &&
            this.changeListCount == other.getChangeListCount() &&
            this.failedBuildCount == other.getFailedBuildCount() &&
            this.failedBuildPercent == other.getFailedBuildPercent() &&
            this.issueCount == other.getIssueCount() &&
            ((this.sampleTime==null && other.getSampleTime()==null) || 
             (this.sampleTime!=null &&
              this.sampleTime.equals(other.getSampleTime()))) &&
            this.successfulBuildCount == other.getSuccessfulBuildCount() &&
            this.successfulBuildPercent == other.getSuccessfulBuildPercent() &&
            this.totalBuildCount == other.getTotalBuildCount();
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
        _hashCode += getActiveBuildID();
        _hashCode += getChangeListCount();
        _hashCode += getFailedBuildCount();
        _hashCode += getFailedBuildPercent();
        _hashCode += getIssueCount();
        if (getSampleTime() != null) {
            _hashCode += getSampleTime().hashCode();
        }
        _hashCode += getSuccessfulBuildCount();
        _hashCode += getSuccessfulBuildPercent();
        _hashCode += getTotalBuildCount();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BuildStatistics.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildStatistics"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activeBuildID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "activeBuildID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changeListCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "changeListCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("failedBuildCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "failedBuildCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("failedBuildPercent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "failedBuildPercent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("issueCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "issueCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sampleTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sampleTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("successfulBuildCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "successfulBuildCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("successfulBuildPercent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "successfulBuildPercent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalBuildCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totalBuildCount"));
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
