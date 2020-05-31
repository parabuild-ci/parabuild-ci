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

import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

import javax.xml.namespace.QName;
import java.io.Serializable;

public class ScheduleItem implements Serializable {

  private static final long serialVersionUID = 5609252511898990737L;
  private int buildID;
  private boolean cleanCheckout;
  private String dayOfMonth;
  private String dayOfWeek;
  private String hour;
  private boolean runIfNoChanges;
  private int scheduleItemID;
  private long timeStamp;


  public ScheduleItem() {
  }


  public ScheduleItem(
          final int buildID,
          final boolean cleanCheckout,
          final String dayOfMonth,
          final String dayOfWeek,
          final String hour,
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
  public String getDayOfMonth() {
    return dayOfMonth;
  }


  /**
   * Sets the dayOfMonth value for this ScheduleItem.
   *
   * @param dayOfMonth
   */
  public void setDayOfMonth(final String dayOfMonth) {
    this.dayOfMonth = dayOfMonth;
  }


  /**
   * Gets the dayOfWeek value for this ScheduleItem.
   *
   * @return dayOfWeek
   */
  public String getDayOfWeek() {
    return dayOfWeek;
  }


  /**
   * Sets the dayOfWeek value for this ScheduleItem.
   *
   * @param dayOfWeek
   */
  public void setDayOfWeek(final String dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }


  /**
   * Gets the hour value for this ScheduleItem.
   *
   * @return hour
   */
  public String getHour() {
    return hour;
  }


  /**
   * Sets the hour value for this ScheduleItem.
   *
   * @param hour
   */
  public void setHour(final String hour) {
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


  private Object __equalsCalc;


  public synchronized boolean equals(final Object obj) {
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


  private boolean __hashCodeCalc;


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
    _hashCode += Long.valueOf(timeStamp).hashCode();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(ScheduleItem.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "ScheduleItem"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "buildID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("cleanCheckout");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "cleanCheckout"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("dayOfMonth");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "dayOfMonth"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("dayOfWeek");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "dayOfWeek"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("hour");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "hour"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("runIfNoChanges");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "runIfNoChanges"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("scheduleItemID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "scheduleItemID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("timeStamp");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "timeStamp"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
  }


  /**
   * Return type metadata object
   */
  public static TypeDesc getTypeDesc() {
    return typeDesc;
  }


  /**
   * Get Custom Serializer
   */
  public static Serializer getSerializer(
          final String mechType,
          final Class _javaType,
          final QName _xmlType) {
    return
            new BeanSerializer(
                    _javaType, _xmlType, typeDesc);
  }


  /**
   * Get Custom Deserializer
   */
  public static Deserializer getDeserializer(
          final String mechType,
          final Class _javaType,
          final QName _xmlType) {
    return
            new BeanDeserializer(
                    _javaType, _xmlType, typeDesc);
  }

}
