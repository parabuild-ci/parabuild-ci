/**
 * BuildDistribution.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class BuildDistribution  implements java.io.Serializable {
    private int ID;
    private int activeBuildID;
    private int changeListCount;
    private int failedBuildCount;
    private int issueCount;
    private int successfulBuildCount;
    private int target;
    private int totalBuildCount;

    public BuildDistribution() {
    }

    public BuildDistribution(
           int ID,
           int activeBuildID,
           int changeListCount,
           int failedBuildCount,
           int issueCount,
           int successfulBuildCount,
           int target,
           int totalBuildCount) {
           this.ID = ID;
           this.activeBuildID = activeBuildID;
           this.changeListCount = changeListCount;
           this.failedBuildCount = failedBuildCount;
           this.issueCount = issueCount;
           this.successfulBuildCount = successfulBuildCount;
           this.target = target;
           this.totalBuildCount = totalBuildCount;
    }


    /**
     * Gets the ID value for this BuildDistribution.
     * 
     * @return ID
     */
    public int getID() {
        return ID;
    }


    /**
     * Sets the ID value for this BuildDistribution.
     * 
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }


    /**
     * Gets the activeBuildID value for this BuildDistribution.
     * 
     * @return activeBuildID
     */
    public int getActiveBuildID() {
        return activeBuildID;
    }


    /**
     * Sets the activeBuildID value for this BuildDistribution.
     * 
     * @param activeBuildID
     */
    public void setActiveBuildID(int activeBuildID) {
        this.activeBuildID = activeBuildID;
    }


    /**
     * Gets the changeListCount value for this BuildDistribution.
     * 
     * @return changeListCount
     */
    public int getChangeListCount() {
        return changeListCount;
    }


    /**
     * Sets the changeListCount value for this BuildDistribution.
     * 
     * @param changeListCount
     */
    public void setChangeListCount(int changeListCount) {
        this.changeListCount = changeListCount;
    }


    /**
     * Gets the failedBuildCount value for this BuildDistribution.
     * 
     * @return failedBuildCount
     */
    public int getFailedBuildCount() {
        return failedBuildCount;
    }


    /**
     * Sets the failedBuildCount value for this BuildDistribution.
     * 
     * @param failedBuildCount
     */
    public void setFailedBuildCount(int failedBuildCount) {
        this.failedBuildCount = failedBuildCount;
    }


    /**
     * Gets the issueCount value for this BuildDistribution.
     * 
     * @return issueCount
     */
    public int getIssueCount() {
        return issueCount;
    }


    /**
     * Sets the issueCount value for this BuildDistribution.
     * 
     * @param issueCount
     */
    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }


    /**
     * Gets the successfulBuildCount value for this BuildDistribution.
     * 
     * @return successfulBuildCount
     */
    public int getSuccessfulBuildCount() {
        return successfulBuildCount;
    }


    /**
     * Sets the successfulBuildCount value for this BuildDistribution.
     * 
     * @param successfulBuildCount
     */
    public void setSuccessfulBuildCount(int successfulBuildCount) {
        this.successfulBuildCount = successfulBuildCount;
    }


    /**
     * Gets the target value for this BuildDistribution.
     * 
     * @return target
     */
    public int getTarget() {
        return target;
    }


    /**
     * Sets the target value for this BuildDistribution.
     * 
     * @param target
     */
    public void setTarget(int target) {
        this.target = target;
    }


    /**
     * Gets the totalBuildCount value for this BuildDistribution.
     * 
     * @return totalBuildCount
     */
    public int getTotalBuildCount() {
        return totalBuildCount;
    }


    /**
     * Sets the totalBuildCount value for this BuildDistribution.
     * 
     * @param totalBuildCount
     */
    public void setTotalBuildCount(int totalBuildCount) {
        this.totalBuildCount = totalBuildCount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BuildDistribution)) return false;
        BuildDistribution other = (BuildDistribution) obj;
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
            this.issueCount == other.getIssueCount() &&
            this.successfulBuildCount == other.getSuccessfulBuildCount() &&
            this.target == other.getTarget() &&
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
        _hashCode += getIssueCount();
        _hashCode += getSuccessfulBuildCount();
        _hashCode += getTarget();
        _hashCode += getTotalBuildCount();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BuildDistribution.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "BuildDistribution"));
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
        elemField.setFieldName("issueCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "issueCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("successfulBuildCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "successfulBuildCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("target");
        elemField.setXmlName(new javax.xml.namespace.QName("", "target"));
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
