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
import java.util.Calendar;

public class StepRun implements Serializable {

  private static final long serialVersionUID = -96466360594291777L;
  private int ID;
  private int buildRunID;
  private boolean complete;
  private int duration;
  private Calendar finishedAt;
  private String name;
  private String resultDescription;
  private byte resultID;
  private Calendar startedAt;
  private boolean successful;
  private long timeStamp;


  public StepRun() {
  }


  public StepRun(
          final int ID,
          final int buildRunID,
          final boolean complete,
          final int duration,
          final Calendar finishedAt,
          final String name,
          final String resultDescription,
          final byte resultID,
          final Calendar startedAt,
          final boolean successful,
          final long timeStamp) {
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
  public void setID(final int ID) {
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
  public void setBuildRunID(final int buildRunID) {
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
  public void setComplete(final boolean complete) {
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
  public void setDuration(final int duration) {
    this.duration = duration;
  }


  /**
   * Gets the finishedAt value for this StepRun.
   *
   * @return finishedAt
   */
  public Calendar getFinishedAt() {
    return finishedAt;
  }


  /**
   * Sets the finishedAt value for this StepRun.
   *
   * @param finishedAt
   */
  public void setFinishedAt(final Calendar finishedAt) {
    this.finishedAt = finishedAt;
  }


  /**
   * Gets the name value for this StepRun.
   *
   * @return name
   */
  public String getName() {
    return name;
  }


  /**
   * Sets the name value for this StepRun.
   *
   * @param name
   */
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Gets the resultDescription value for this StepRun.
   *
   * @return resultDescription
   */
  public String getResultDescription() {
    return resultDescription;
  }


  /**
   * Sets the resultDescription value for this StepRun.
   *
   * @param resultDescription
   */
  public void setResultDescription(final String resultDescription) {
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
  public void setResultID(final byte resultID) {
    this.resultID = resultID;
  }


  /**
   * Gets the startedAt value for this StepRun.
   *
   * @return startedAt
   */
  public Calendar getStartedAt() {
    return startedAt;
  }


  /**
   * Sets the startedAt value for this StepRun.
   *
   * @param startedAt
   */
  public void setStartedAt(final Calendar startedAt) {
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
  public void setSuccessful(final boolean successful) {
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
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  private Object __equalsCalc = null;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof StepRun)) return false;
    final StepRun other = (StepRun) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.ID &&
            this.buildRunID == other.buildRunID &&
            this.complete == other.complete &&
            this.duration == other.duration &&
            ((this.finishedAt == null && other.finishedAt == null) ||
                    (this.finishedAt != null &&
                            this.finishedAt.equals(other.finishedAt))) &&
            ((this.name == null && other.name == null) ||
                    (this.name != null &&
                            this.name.equals(other.name))) &&
            ((this.resultDescription == null && other.resultDescription == null) ||
                    (this.resultDescription != null &&
                            this.resultDescription.equals(other.resultDescription))) &&
            this.resultID == other.resultID &&
            ((this.startedAt == null && other.startedAt == null) ||
                    (this.startedAt != null &&
                            this.startedAt.equals(other.startedAt))) &&
            this.successful == other.successful &&
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
    _hashCode += ID;
    _hashCode += buildRunID;
    _hashCode += (complete ? Boolean.TRUE : Boolean.FALSE).hashCode();
    _hashCode += duration;
    if (finishedAt != null) {
      _hashCode += finishedAt.hashCode();
    }
    if (name != null) {
      _hashCode += name.hashCode();
    }
    if (resultDescription != null) {
      _hashCode += resultDescription.hashCode();
    }
    _hashCode += resultID;
    if (startedAt != null) {
      _hashCode += startedAt.hashCode();
    }
    _hashCode += (successful ? Boolean.TRUE : Boolean.FALSE).hashCode();
    _hashCode += new Long(timeStamp).hashCode();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(StepRun.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "StepRun"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("buildRunID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "buildRunID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("complete");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "complete"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("duration");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "duration"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("finishedAt");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "finishedAt"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("name");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "name"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("resultDescription");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "resultDescription"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("resultID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "resultID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("startedAt");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "startedAt"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("successful");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "successful"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
