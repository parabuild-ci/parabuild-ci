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

public class BuildWatcher implements Serializable {

  private static final long serialVersionUID = -3159543946243929534L;
  private int buildID;
  private boolean disabled;
  private String email;
  private String instantMessengerAddress;
  private byte instantMessengerType;
  private byte level;
  private long timeStamp;
  private int watcherID;


  public BuildWatcher() {
  }


  public BuildWatcher(
          final int buildID,
          final boolean disabled,
          final String email,
          final String instantMessengerAddress,
          final byte instantMessengerType,
          final byte level,
          final long timeStamp,
          final int watcherID) {
    this.buildID = buildID;
    this.disabled = disabled;
    this.email = email;
    this.instantMessengerAddress = instantMessengerAddress;
    this.instantMessengerType = instantMessengerType;
    this.level = level;
    this.timeStamp = timeStamp;
    this.watcherID = watcherID;
  }


  /**
   * Gets the buildID value for this BuildWatcher.
   *
   * @return buildID
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Sets the buildID value for this BuildWatcher.
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Gets the disabled value for this BuildWatcher.
   *
   * @return disabled
   */
  public boolean isDisabled() {
    return disabled;
  }


  /**
   * Sets the disabled value for this BuildWatcher.
   *
   * @param disabled
   */
  public void setDisabled(final boolean disabled) {
    this.disabled = disabled;
  }


  /**
   * Gets the email value for this BuildWatcher.
   *
   * @return email
   */
  public String getEmail() {
    return email;
  }


  /**
   * Sets the email value for this BuildWatcher.
   *
   * @param email
   */
  public void setEmail(final String email) {
    this.email = email;
  }


  /**
   * Gets the instantMessengerAddress value for this BuildWatcher.
   *
   * @return instantMessengerAddress
   */
  public String getInstantMessengerAddress() {
    return instantMessengerAddress;
  }


  /**
   * Sets the instantMessengerAddress value for this BuildWatcher.
   *
   * @param instantMessengerAddress
   */
  public void setInstantMessengerAddress(final String instantMessengerAddress) {
    this.instantMessengerAddress = instantMessengerAddress;
  }


  /**
   * Gets the instantMessengerType value for this BuildWatcher.
   *
   * @return instantMessengerType
   */
  public byte getInstantMessengerType() {
    return instantMessengerType;
  }


  /**
   * Sets the instantMessengerType value for this BuildWatcher.
   *
   * @param instantMessengerType
   */
  public void setInstantMessengerType(final byte instantMessengerType) {
    this.instantMessengerType = instantMessengerType;
  }


  /**
   * Gets the level value for this BuildWatcher.
   *
   * @return level
   */
  public byte getLevel() {
    return level;
  }


  /**
   * Sets the level value for this BuildWatcher.
   *
   * @param level
   */
  public void setLevel(final byte level) {
    this.level = level;
  }


  /**
   * Gets the timeStamp value for this BuildWatcher.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this BuildWatcher.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the watcherID value for this BuildWatcher.
   *
   * @return watcherID
   */
  public int getWatcherID() {
    return watcherID;
  }


  /**
   * Sets the watcherID value for this BuildWatcher.
   *
   * @param watcherID
   */
  public void setWatcherID(final int watcherID) {
    this.watcherID = watcherID;
  }


  private Object __equalsCalc;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof BuildWatcher)) return false;
    final BuildWatcher other = (BuildWatcher) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.buildID == other.buildID &&
            this.disabled == other.disabled &&
            ((this.email == null && other.email == null) ||
                    (this.email != null &&
                            this.email.equals(other.email))) &&
            ((this.instantMessengerAddress == null && other.instantMessengerAddress == null) ||
                    (this.instantMessengerAddress != null &&
                            this.instantMessengerAddress.equals(other.instantMessengerAddress))) &&
            this.instantMessengerType == other.instantMessengerType &&
            this.level == other.level &&
            this.timeStamp == other.timeStamp &&
            this.watcherID == other.watcherID;
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
    _hashCode += (disabled ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (email != null) {
      _hashCode += email.hashCode();
    }
    if (instantMessengerAddress != null) {
      _hashCode += instantMessengerAddress.hashCode();
    }
    _hashCode += instantMessengerType;
    _hashCode += level;
    _hashCode += new Long(timeStamp).hashCode();
    _hashCode += watcherID;
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(BuildWatcher.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "BuildWatcher"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "buildID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("disabled");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "disabled"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("email");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "email"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("instantMessengerAddress");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "instantMessengerAddress"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("instantMessengerType");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "instantMessengerType"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("level");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "level"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("timeStamp");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "timeStamp"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("watcherID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "watcherID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
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
