/**
 * PublishedStepResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class PublishedStepResult  implements java.io.Serializable {
    private int ID;
    private int activeBuildID;
    private java.lang.String buildName;
    private java.util.Calendar buildRunDate;
    private int buildRunID;
    private int buildRunNumber;
    private java.lang.String description;
    private java.util.Calendar publishDate;
    private int publisherBuildRunID;
    private int resultGroupID;
    private int stepResultID;

    public PublishedStepResult() {
    }

    public PublishedStepResult(
           int ID,
           int activeBuildID,
           java.lang.String buildName,
           java.util.Calendar buildRunDate,
           int buildRunID,
           int buildRunNumber,
           java.lang.String description,
           java.util.Calendar publishDate,
           int publisherBuildRunID,
           int resultGroupID,
           int stepResultID) {
           this.ID = ID;
           this.activeBuildID = activeBuildID;
           this.buildName = buildName;
           this.buildRunDate = buildRunDate;
           this.buildRunID = buildRunID;
           this.buildRunNumber = buildRunNumber;
           this.description = description;
           this.publishDate = publishDate;
           this.publisherBuildRunID = publisherBuildRunID;
           this.resultGroupID = resultGroupID;
           this.stepResultID = stepResultID;
    }


    /**
     * Gets the ID value for this PublishedStepResult.
     * 
     * @return ID
     */
    public int getID() {
        return ID;
    }


    /**
     * Sets the ID value for this PublishedStepResult.
     * 
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }


    /**
     * Gets the activeBuildID value for this PublishedStepResult.
     * 
     * @return activeBuildID
     */
    public int getActiveBuildID() {
        return activeBuildID;
    }


    /**
     * Sets the activeBuildID value for this PublishedStepResult.
     * 
     * @param activeBuildID
     */
    public void setActiveBuildID(int activeBuildID) {
        this.activeBuildID = activeBuildID;
    }


    /**
     * Gets the buildName value for this PublishedStepResult.
     * 
     * @return buildName
     */
    public java.lang.String getBuildName() {
        return buildName;
    }


    /**
     * Sets the buildName value for this PublishedStepResult.
     * 
     * @param buildName
     */
    public void setBuildName(java.lang.String buildName) {
        this.buildName = buildName;
    }


    /**
     * Gets the buildRunDate value for this PublishedStepResult.
     * 
     * @return buildRunDate
     */
    public java.util.Calendar getBuildRunDate() {
        return buildRunDate;
    }


    /**
     * Sets the buildRunDate value for this PublishedStepResult.
     * 
     * @param buildRunDate
     */
    public void setBuildRunDate(java.util.Calendar buildRunDate) {
        this.buildRunDate = buildRunDate;
    }


    /**
     * Gets the buildRunID value for this PublishedStepResult.
     * 
     * @return buildRunID
     */
    public int getBuildRunID() {
        return buildRunID;
    }


    /**
     * Sets the buildRunID value for this PublishedStepResult.
     * 
     * @param buildRunID
     */
    public void setBuildRunID(int buildRunID) {
        this.buildRunID = buildRunID;
    }


    /**
     * Gets the buildRunNumber value for this PublishedStepResult.
     * 
     * @return buildRunNumber
     */
    public int getBuildRunNumber() {
        return buildRunNumber;
    }


    /**
     * Sets the buildRunNumber value for this PublishedStepResult.
     * 
     * @param buildRunNumber
     */
    public void setBuildRunNumber(int buildRunNumber) {
        this.buildRunNumber = buildRunNumber;
    }


    /**
     * Gets the description value for this PublishedStepResult.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this PublishedStepResult.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the publishDate value for this PublishedStepResult.
     * 
     * @return publishDate
     */
    public java.util.Calendar getPublishDate() {
        return publishDate;
    }


    /**
     * Sets the publishDate value for this PublishedStepResult.
     * 
     * @param publishDate
     */
    public void setPublishDate(java.util.Calendar publishDate) {
        this.publishDate = publishDate;
    }


    /**
     * Gets the publisherBuildRunID value for this PublishedStepResult.
     * 
     * @return publisherBuildRunID
     */
    public int getPublisherBuildRunID() {
        return publisherBuildRunID;
    }


    /**
     * Sets the publisherBuildRunID value for this PublishedStepResult.
     * 
     * @param publisherBuildRunID
     */
    public void setPublisherBuildRunID(int publisherBuildRunID) {
        this.publisherBuildRunID = publisherBuildRunID;
    }


    /**
     * Gets the resultGroupID value for this PublishedStepResult.
     * 
     * @return resultGroupID
     */
    public int getResultGroupID() {
        return resultGroupID;
    }


    /**
     * Sets the resultGroupID value for this PublishedStepResult.
     * 
     * @param resultGroupID
     */
    public void setResultGroupID(int resultGroupID) {
        this.resultGroupID = resultGroupID;
    }


    /**
     * Gets the stepResultID value for this PublishedStepResult.
     * 
     * @return stepResultID
     */
    public int getStepResultID() {
        return stepResultID;
    }


    /**
     * Sets the stepResultID value for this PublishedStepResult.
     * 
     * @param stepResultID
     */
    public void setStepResultID(int stepResultID) {
        this.stepResultID = stepResultID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PublishedStepResult)) return false;
        PublishedStepResult other = (PublishedStepResult) obj;
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
            ((this.buildName==null && other.getBuildName()==null) || 
             (this.buildName!=null &&
              this.buildName.equals(other.getBuildName()))) &&
            ((this.buildRunDate==null && other.getBuildRunDate()==null) || 
             (this.buildRunDate!=null &&
              this.buildRunDate.equals(other.getBuildRunDate()))) &&
            this.buildRunID == other.getBuildRunID() &&
            this.buildRunNumber == other.getBuildRunNumber() &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.publishDate==null && other.getPublishDate()==null) || 
             (this.publishDate!=null &&
              this.publishDate.equals(other.getPublishDate()))) &&
            this.publisherBuildRunID == other.getPublisherBuildRunID() &&
            this.resultGroupID == other.getResultGroupID() &&
            this.stepResultID == other.getStepResultID();
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
        if (getBuildName() != null) {
            _hashCode += getBuildName().hashCode();
        }
        if (getBuildRunDate() != null) {
            _hashCode += getBuildRunDate().hashCode();
        }
        _hashCode += getBuildRunID();
        _hashCode += getBuildRunNumber();
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getPublishDate() != null) {
            _hashCode += getPublishDate().hashCode();
        }
        _hashCode += getPublisherBuildRunID();
        _hashCode += getResultGroupID();
        _hashCode += getStepResultID();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PublishedStepResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "PublishedStepResult"));
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
        elemField.setFieldName("buildName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buildName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buildRunDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buildRunDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buildRunID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buildRunID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buildRunNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buildRunNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("publishDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "publishDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("publisherBuildRunID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "publisherBuildRunID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultGroupID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "resultGroupID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stepResultID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "stepResultID"));
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
