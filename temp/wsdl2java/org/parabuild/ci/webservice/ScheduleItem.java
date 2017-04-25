/**
 * ScheduleItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public class ScheduleItem  implements java.io.Serializable {
    private int buildID;
    private boolean cleanCheckout;
    private java.lang.String dayOfMonth;
    private java.lang.String dayOfWeek;
    private java.lang.String hour;
    private boolean runIfNoChanges;
    private int scheduleItemID;
    private long timeStamp;

    public ScheduleItem() {
    }

    public ScheduleItem(
           int buildID,
           boolean cleanCheckout,
           java.lang.String dayOfMonth,
           java.lang.String dayOfWeek,
           java.lang.String hour,
           boolean runIfNoChanges,
           int scheduleItemID,
           long timeStamp) {
           this.buildID = buildID;
           this.cleanCheckout = cleanCheckout;
           this.dayOfMonth = dayOfMonth;
           this.dayOfWeek = dayOfWeek;
           this.hour = hour;
           this.runIfNoChanges = runIfNoChanges;
           this.scheduleItemID = scheduleItemID;
           this.timeStamp = timeStamp;
    }


    /**
     * Gets the buildID value for this ScheduleItem.
     * 
     * @return buildID
     */
    public int getBuildID() {
        return buildID;
    }


    /**
     * Sets the buildID value for this ScheduleItem.
     * 
     * @param buildID
     */
    public void setBuildID(int buildID) {
        this.buildID = buildID;
    }


    /**
     * Gets the cleanCheckout value for this ScheduleItem.
     * 
     * @return cleanCheckout
     */
    public boolean isCleanCheckout() {
        return cleanCheckout;
    }


    /**
     * Sets the cleanCheckout value for this ScheduleItem.
     * 
     * @param cleanCheckout
     */
    public void setCleanCheckout(boolean cleanCheckout) {
        this.cleanCheckout = cleanCheckout;
    }


    /**
     * Gets the dayOfMonth value for this ScheduleItem.
     * 
     * @return dayOfMonth
     */
    public java.lang.String getDayOfMonth() {
        return dayOfMonth;
    }


    /**
     * Sets the dayOfMonth value for this ScheduleItem.
     * 
     * @param dayOfMonth
     */
    public void setDayOfMonth(java.lang.String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }


    /**
     * Gets the dayOfWeek value for this ScheduleItem.
     * 
     * @return dayOfWeek
     */
    public java.lang.String getDayOfWeek() {
        return dayOfWeek;
    }


    /**
     * Sets the dayOfWeek value for this ScheduleItem.
     * 
     * @param dayOfWeek
     */
    public void setDayOfWeek(java.lang.String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }


    /**
     * Gets the hour value for this ScheduleItem.
     * 
     * @return hour
     */
    public java.lang.String getHour() {
        return hour;
    }


    /**
     * Sets the hour value for this ScheduleItem.
     * 
     * @param hour
     */
    public void setHour(java.lang.String hour) {
        this.hour = hour;
    }


    /**
     * Gets the runIfNoChanges value for this ScheduleItem.
     * 
     * @return runIfNoChanges
     */
    public boolean isRunIfNoChanges() {
        return runIfNoChanges;
    }


    /**
     * Sets the runIfNoChanges value for this ScheduleItem.
     * 
     * @param runIfNoChanges
     */
    public void setRunIfNoChanges(boolean runIfNoChanges) {
        this.runIfNoChanges = runIfNoChanges;
    }


    /**
     * Gets the scheduleItemID value for this ScheduleItem.
     * 
     * @return scheduleItemID
     */
    public int getScheduleItemID() {
        return scheduleItemID;
    }


    /**
     * Sets the scheduleItemID value for this ScheduleItem.
     * 
     * @param scheduleItemID
     */
    public void setScheduleItemID(int scheduleItemID) {
        this.scheduleItemID = scheduleItemID;
    }


    /**
     * Gets the timeStamp value for this ScheduleItem.
     * 
     * @return timeStamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }


    /**
     * Sets the timeStamp value for this ScheduleItem.
     * 
     * @param timeStamp
     */
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ScheduleItem)) return false;
        ScheduleItem other = (ScheduleItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.buildID == other.getBuildID() &&
            this.cleanCheckout == other.isCleanCheckout() &&
            ((this.dayOfMonth==null && other.getDayOfMonth()==null) || 
             (this.dayOfMonth!=null &&
              this.dayOfMonth.equals(other.getDayOfMonth()))) &&
            ((this.dayOfWeek==null && other.getDayOfWeek()==null) || 
             (this.dayOfWeek!=null &&
              this.dayOfWeek.equals(other.getDayOfWeek()))) &&
            ((this.hour==null && other.getHour()==null) || 
             (this.hour!=null &&
              this.hour.equals(other.getHour()))) &&
            this.runIfNoChanges == other.isRunIfNoChanges() &&
            this.scheduleItemID == other.getScheduleItemID() &&
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
        _hashCode += getBuildID();
        _hashCode += (isCleanCheckout() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getDayOfMonth() != null) {
            _hashCode += getDayOfMonth().hashCode();
        }
        if (getDayOfWeek() != null) {
            _hashCode += getDayOfWeek().hashCode();
        }
        if (getHour() != null) {
            _hashCode += getHour().hashCode();
        }
        _hashCode += (isRunIfNoChanges() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getScheduleItemID();
        _hashCode += new Long(getTimeStamp()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ScheduleItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.parabuildci.org/products/parabuild/webservice/parabuild", "ScheduleItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("buildID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "buildID"));
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
        elemField.setFieldName("dayOfMonth");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dayOfMonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dayOfWeek");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dayOfWeek"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hour");
        elemField.setXmlName(new javax.xml.namespace.QName("", "hour"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("runIfNoChanges");
        elemField.setXmlName(new javax.xml.namespace.QName("", "runIfNoChanges"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scheduleItemID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "scheduleItemID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
