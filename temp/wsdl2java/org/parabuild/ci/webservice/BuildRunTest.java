/**
 * BuildRunTest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class BuildRunTest  implements java.io.Serializable {
    private int ID;
    private boolean broken;
    private int brokenBuildRunCount;
    private int brokenSinceBuildRunID;
    private int buildRunID;
    private long durationMillis;
    private boolean fix;
    private java.lang.String message;
    private boolean newFailure;
    private boolean newTest;
    private short resultCode;
    private int testCaseNameID;

    public BuildRunTest() {
    }

    public BuildRunTest(
           int ID,
           boolean broken,
           int brokenBuildRunCount,
           int brokenSinceBuildRunID,
           int buildRunID,
           long durationMillis,
           boolean fix,
           java.lang.String message,
           boolean newFailure,
           boolean newTest,
           short resultCode,
           int testCaseNameID) {
           this.ID = ID;
           this.broken = broken;
           this.brokenBuildRunCount = brokenBuildRunCount;
           this.brokenSinceBuildRunID = brokenSinceBuildRunID;
           this.buildRunID = buildRunID;
           this.durationMillis = durationMillis;
           this.fix = fix;
           this.message = message;
           this.newFailure = newFailure;
           this.newTest = newTest;
           this.resultCode = resultCode;
           this.testCaseNameID = testCaseNameID;
    }


    /**
     * Gets the ID value for this BuildRunTest.
     * 
     * @return ID
     */
    public int getID() {
        return ID;
    }


    /**
     * Sets the ID value for this BuildRunTest.
     * 
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }


    /**
     * Gets the broken value for this BuildRunTest.
     * 
     * @return broken
     */
    public boolean isBroken() {
        return broken;
    }


    /**
     * Sets the broken value for this BuildRunTest.
     * 
     * @param broken
     */
    public void setBroken(boolean broken) {
        this.broken = broken;
    }


    /**
     * Gets the brokenBuildRunCount value for this BuildRunTest.
     * 
     * @return brokenBuildRunCount
     */
    public int getBrokenBuildRunCount() {
        return brokenBuildRunCount;
    }


    /**
     * Sets the brokenBuildRunCount value for this BuildRunTest.
     * 
     * @param brokenBuildRunCount
     */
    public void setBrokenBuildRunCount(int brokenBuildRunCount) {
        this.brokenBuildRunCount = brokenBuildRunCount;
    }


    /**
     * Gets the brokenSinceBuildRunID value for this BuildRunTest.
     * 
     * @return brokenSinceBuildRunID
     */
    public int getBrokenSinceBuildRunID() {
        return brokenSinceBuildRunID;
    }


    /**
     * Sets the brokenSinceBuildRunID value for this BuildRunTest.
     * 
     * @param brokenSinceBuildRunID
     */
    public void setBrokenSinceBuildRunID(int brokenSinceBuildRunID) {
        this.brokenSinceBuildRunID = brokenSinceBuildRunID;
    }


    /**
     * Gets the buildRunID value for this BuildRunTest.
     * 
     * @return buildRunID
     */
    public int getBuildRunID() {
        return buildRunID;
    }


    /**
     * Sets the buildRunID value for this BuildRunTest.
     * 
     * @param buildRunID
     */
    public void setBuildRunID(int buildRunID) {
        this.buildRunID = buildRunID;
    }


    /**
     * Gets the durationMillis value for this BuildRunTest.
     * 
     * @return durationMillis
     */
    public long getDurationMillis() {
        return durationMillis;
    }


    /**
     * Sets the durationMillis value for this BuildRunTest.
     * 
     * @param durationMillis
     */
    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
    }


    /**
     * Gets the fix value for this BuildRunTest.
     * 
     * @return fix
     */
    public boolean isFix() {
        return fix;
    }


    /**
     * Sets the fix value for this BuildRunTest.
     * 
     * @param fix
     */
    public void setFix(boolean fix) {
        this.fix = fix;
    }


    /**
     * Gets the message value for this BuildRunTest.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this BuildRunTest.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }


    /**
     * Gets the newFailure value for this BuildRunTest.
     * 
     * @return newFailure
     */
    public boolean isNewFailure() {
        return newFailure;
    }


    /**
     * Sets the newFailure value for this BuildRunTest.
     * 
     * @param newFailure
     */
    public void setNewFailure(boolean newFailure) {
        this.newFailure = newFailure;
    }


    /**
     * Gets the newTest value for this BuildRunTest.
     * 
     * @return newTest
     */
    public boolean isNewTest() {
        return newTest;
    }


    /**
     * Sets the newTest value for this BuildRunTest.
     * 
     * @param newTest
     */
    public void setNewTest(boolean newTest) {
        this.newTest = newTest;
    }


    /**
     * Gets the resultCode value for this BuildRunTest.
     * 
     * @return resultCode
     */
    public short getResultCode() {
        return resultCode;
    }


    /**
     * Sets the resultCode value for this BuildRunTest.
     * 
     * @param resultCode
     */
    public void setResultCode(short resultCode) {
        this.resultCode = resultCode;
    }


    /**
     * Gets the testCaseNameID value for this BuildRunTest.
     * 
     * @return testCaseNameID
     */
    public int getTestCaseNameID() {
        return testCaseNameID;
    }


    /**
     * Sets the testCaseNameID value for this BuildRunTest.
     * 
     * @param testCaseNameID
     */
    public void setTestCaseNameID(int testCaseNameID) {
        this.testCaseNameID = testCaseNameID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BuildRunTest)) return false;
        BuildRunTest other = (BuildRunTest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.ID == other.getID() &&
            this.broken == other.isBroken() &&
            this.brokenBuildRunCount == other.getBrokenBuildRunCount() &&
            this.brokenSinceBuildRunID == other.getBrokenSinceBuildRunID() &&
            this.buildRunID == other.getBuildRunID() &&
            this.durationMillis == other.getDurationMillis() &&
            this.fix == other.isFix() &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            this.newFailure == other.isNewFailure() &&
            this.newTest == other.isNewTest() &&
            this.resultCode == other.getResultCode() &&
            this.testCaseNameID == other.getTestCaseNameID();
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
        _hashCode += (isBroken() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getBrokenBuildRunCount();
        _hashCode += getBrokenSinceBuildRunID();
        _hashCode += getBuildRunID();
        _hashCode += new Long(getDurationMillis()).hashCode();
        _hashCode += (isFix() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        _hashCode += (isNewFailure() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isNewTest() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getResultCode();
        _hashCode += getTestCaseNameID();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BuildRunTest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildRunTest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("broken");
        elemField.setXmlName(new javax.xml.namespace.QName("", "broken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("brokenBuildRunCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "brokenBuildRunCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("brokenSinceBuildRunID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "brokenSinceBuildRunID"));
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
        elemField.setFieldName("durationMillis");
        elemField.setXmlName(new javax.xml.namespace.QName("", "durationMillis"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fix");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fix"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newFailure");
        elemField.setXmlName(new javax.xml.namespace.QName("", "newFailure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newTest");
        elemField.setXmlName(new javax.xml.namespace.QName("", "newTest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "resultCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("testCaseNameID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "testCaseNameID"));
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
