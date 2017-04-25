/**
 * TestStatistics.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class TestStatistics  implements java.io.Serializable {
    private int activeBuildID;
    private int buildCount;
    private int errorTestCount;
    private int errorTestPercent;
    private int failedTestCount;
    private int failedTestPercent;
    private java.util.Calendar sampleTime;
    private int successfulTestCount;
    private int successfulTestPercent;
    private byte testCode;
    private int totalTestCount;

    public TestStatistics() {
    }

    public TestStatistics(
           int activeBuildID,
           int buildCount,
           int errorTestCount,
           int errorTestPercent,
           int failedTestCount,
           int failedTestPercent,
           java.util.Calendar sampleTime,
           int successfulTestCount,
           int successfulTestPercent,
           byte testCode,
           int totalTestCount) {
           this.activeBuildID = activeBuildID;
           this.buildCount = buildCount;
           this.errorTestCount = errorTestCount;
           this.errorTestPercent = errorTestPercent;
           this.failedTestCount = failedTestCount;
           this.failedTestPercent = failedTestPercent;
           this.sampleTime = sampleTime;
           this.successfulTestCount = successfulTestCount;
           this.successfulTestPercent = successfulTestPercent;
           this.testCode = testCode;
           this.totalTestCount = totalTestCount;
    }


    /**
     * Gets the activeBuildID value for this TestStatistics.
     * 
     * @return activeBuildID
     */
    public int getActiveBuildID() {
        return activeBuildID;
    }


    /**
     * Sets the activeBuildID value for this TestStatistics.
     * 
     * @param activeBuildID
     */
    public void setActiveBuildID(int activeBuildID) {
        this.activeBuildID = activeBuildID;
    }


    /**
     * Gets the buildCount value for this TestStatistics.
     * 
     * @return buildCount
     */
    public int getBuildCount() {
        return buildCount;
    }


    /**
     * Sets the buildCount value for this TestStatistics.
     * 
     * @param buildCount
     */
    public void setBuildCount(int buildCount) {
        this.buildCount = buildCount;
    }


    /**
     * Gets the errorTestCount value for this TestStatistics.
     * 
     * @return errorTestCount
     */
    public int getErrorTestCount() {
        return errorTestCount;
    }


    /**
     * Sets the errorTestCount value for this TestStatistics.
     * 
     * @param errorTestCount
     */
    public void setErrorTestCount(int errorTestCount) {
        this.errorTestCount = errorTestCount;
    }


    /**
     * Gets the errorTestPercent value for this TestStatistics.
     * 
     * @return errorTestPercent
     */
    public int getErrorTestPercent() {
        return errorTestPercent;
    }


    /**
     * Sets the errorTestPercent value for this TestStatistics.
     * 
     * @param errorTestPercent
     */
    public void setErrorTestPercent(int errorTestPercent) {
        this.errorTestPercent = errorTestPercent;
    }


    /**
     * Gets the failedTestCount value for this TestStatistics.
     * 
     * @return failedTestCount
     */
    public int getFailedTestCount() {
        return failedTestCount;
    }


    /**
     * Sets the failedTestCount value for this TestStatistics.
     * 
     * @param failedTestCount
     */
    public void setFailedTestCount(int failedTestCount) {
        this.failedTestCount = failedTestCount;
    }


    /**
     * Gets the failedTestPercent value for this TestStatistics.
     * 
     * @return failedTestPercent
     */
    public int getFailedTestPercent() {
        return failedTestPercent;
    }


    /**
     * Sets the failedTestPercent value for this TestStatistics.
     * 
     * @param failedTestPercent
     */
    public void setFailedTestPercent(int failedTestPercent) {
        this.failedTestPercent = failedTestPercent;
    }


    /**
     * Gets the sampleTime value for this TestStatistics.
     * 
     * @return sampleTime
     */
    public java.util.Calendar getSampleTime() {
        return sampleTime;
    }


    /**
     * Sets the sampleTime value for this TestStatistics.
     * 
     * @param sampleTime
     */
    public void setSampleTime(java.util.Calendar sampleTime) {
        this.sampleTime = sampleTime;
    }


    /**
     * Gets the successfulTestCount value for this TestStatistics.
     * 
     * @return successfulTestCount
     */
    public int getSuccessfulTestCount() {
        return successfulTestCount;
    }


    /**
     * Sets the successfulTestCount value for this TestStatistics.
     * 
     * @param successfulTestCount
     */
    public void setSuccessfulTestCount(int successfulTestCount) {
        this.successfulTestCount = successfulTestCount;
    }


    /**
     * Gets the successfulTestPercent value for this TestStatistics.
     * 
     * @return successfulTestPercent
     */
    public int getSuccessfulTestPercent() {
        return successfulTestPercent;
    }


    /**
     * Sets the successfulTestPercent value for this TestStatistics.
     * 
     * @param successfulTestPercent
     */
    public void setSuccessfulTestPercent(int successfulTestPercent) {
        this.successfulTestPercent = successfulTestPercent;
    }


    /**
     * Gets the testCode value for this TestStatistics.
     * 
     * @return testCode
     */
    public byte getTestCode() {
        return testCode;
    }


    /**
     * Sets the testCode value for this TestStatistics.
     * 
     * @param testCode
     */
    public void setTestCode(byte testCode) {
        this.testCode = testCode;
    }


    /**
     * Gets the totalTestCount value for this TestStatistics.
     * 
     * @return totalTestCount
     */
    public int getTotalTestCount() {
        return totalTestCount;
    }


    /**
     * Sets the totalTestCount value for this TestStatistics.
     * 
     * @param totalTestCount
     */
    public void setTotalTestCount(int totalTestCount) {
        this.totalTestCount = totalTestCount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TestStatistics)) return false;
        TestStatistics other = (TestStatistics) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.activeBuildID == other.getActiveBuildID() &&
            this.buildCount == other.getBuildCount() &&
            this.errorTestCount == other.getErrorTestCount() &&
            this.errorTestPercent == other.getErrorTestPercent() &&
            this.failedTestCount == other.getFailedTestCount() &&
            this.failedTestPercent == other.getFailedTestPercent() &&
            ((this.sampleTime==null && other.getSampleTime()==null) || 
             (this.sampleTime!=null &&
              this.sampleTime.equals(other.getSampleTime()))) &&
            this.successfulTestCount == other.getSuccessfulTestCount() &&
            this.successfulTestPercent == other.getSuccessfulTestPercent() &&
            this.testCode == other.getTestCode() &&
            this.totalTestCount == other.getTotalTestCount();
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
        _hashCode += getActiveBuildID();
        _hashCode += getBuildCount();
        _hashCode += getErrorTestCount();
        _hashCode += getErrorTestPercent();
        _hashCode += getFailedTestCount();
        _hashCode += getFailedTestPercent();
        if (getSampleTime() != null) {
            _hashCode += getSampleTime().hashCode();
        }
        _hashCode += getSuccessfulTestCount();
        _hashCode += getSuccessfulTestPercent();
        _hashCode += getTestCode();
        _hashCode += getTotalTestCount();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TestStatistics.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "TestStatistics"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activeBuildID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "activeBuildID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buildCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buildCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorTestCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "errorTestCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorTestPercent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "errorTestPercent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("failedTestCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "failedTestCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("failedTestPercent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "failedTestPercent"));
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
        elemField.setFieldName("successfulTestCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "successfulTestCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("successfulTestPercent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "successfulTestPercent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "testCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalTestCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "totalTestCount"));
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
