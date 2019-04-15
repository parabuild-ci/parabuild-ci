/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parabuild.ci.webservice.templ;

public class ScheduleItem implements java.io.Serializable {

  private static final long serialVersionUID = 5609252511898990737L;
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
          final int buildID,
          final boolean cleanCheckout,
          final java.lang.String dayOfMonth,
          final java.lang.String dayOfWeek,
          final java.lang.String hour,
          final boolean runIfNoChanges,
          final int scheduleItemID,
          final long timeStamp) {
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
  public void setBuildID(final int buildID) {
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
  public void setCleanCheckout(final boolean cleanCheckout) {
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
  public void setDayOfMonth(final java.lang.String dayOfMonth) {
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
  public void setDayOfWeek(final java.lang.String dayOfWeek) {
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
  public void setHour(final java.lang.String hour) {
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
  public void setRunIfNoChanges(final boolean runIfNoChanges) {
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
  public void setScheduleItemID(final int scheduleItemID) {
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
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof ScheduleItem)) return false;
    final ScheduleItem other = (ScheduleItem) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.buildID == other.buildID &&
            this.cleanCheckout == other.cleanCheckout &&
            ((this.dayOfMonth == null && other.dayOfMonth == null) ||
                    (this.dayOfMonth != null &&
                            this.dayOfMonth.equals(other.dayOfMonth))) &&
            ((this.dayOfWeek == null && other.dayOfWeek == null) ||
                    (this.dayOfWeek != null &&
                            this.dayOfWeek.equals(other.dayOfWeek))) &&
            ((this.hour == null && other.hour == null) ||
                    (this.hour != null &&
                            this.hour.equals(other.hour))) &&
            this.runIfNoChanges == other.runIfNoChanges &&
            this.scheduleItemID == other.scheduleItemID &&
            this.timeStamp == other.timeStamp;
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
    _hashCode += buildID;
    _hashCode += (cleanCheckout ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (dayOfMonth != null) {
      _hashCode += dayOfMonth.hashCode();
    }
    if (dayOfWeek != null) {
      _hashCode += dayOfWeek.hashCode();
    }
    if (hour != null) {
      _hashCode += hour.hashCode();
    }
    _hashCode += (runIfNoChanges ? Boolean.TRUE : Boolean.FALSE).hashCode();
    _hashCode += scheduleItemID;
    _hashCode += new Long(timeStamp).hashCode();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(ScheduleItem.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "ScheduleItem"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("cleanCheckout");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "cleanCheckout"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("dayOfMonth");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "dayOfMonth"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("dayOfWeek");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "dayOfWeek"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("hour");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "hour"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("runIfNoChanges");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "runIfNoChanges"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("scheduleItemID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "scheduleItemID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("timeStamp");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "timeStamp"));
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
          final java.lang.String mechType,
          final java.lang.Class _javaType,
          final javax.xml.namespace.QName _xmlType) {
    return
            new org.apache.axis.encoding.ser.BeanSerializer(
                    _javaType, _xmlType, typeDesc);
  }


  /**
   * Get Custom Deserializer
   */
  public static org.apache.axis.encoding.Deserializer getDeserializer(
          final java.lang.String mechType,
          final java.lang.Class _javaType,
          final javax.xml.namespace.QName _xmlType) {
    return
            new org.apache.axis.encoding.ser.BeanDeserializer(
                    _javaType, _xmlType, typeDesc);
  }

}
