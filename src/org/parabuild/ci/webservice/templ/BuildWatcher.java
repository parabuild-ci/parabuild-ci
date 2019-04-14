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

public class BuildWatcher implements java.io.Serializable {

  private int buildID;
  private boolean disabled;
  private java.lang.String email;
  private java.lang.String instantMessengerAddress;
  private byte instantMessengerType;
  private byte level;
  private long timeStamp;
  private int watcherID;


  public BuildWatcher() {
  }


  public BuildWatcher(
          final int buildID,
          final boolean disabled,
          final java.lang.String email,
          final java.lang.String instantMessengerAddress,
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
  public java.lang.String getEmail() {
    return email;
  }


  /**
   * Sets the email value for this BuildWatcher.
   *
   * @param email
   */
  public void setEmail(final java.lang.String email) {
    this.email = email;
  }


  /**
   * Gets the instantMessengerAddress value for this BuildWatcher.
   *
   * @return instantMessengerAddress
   */
  public java.lang.String getInstantMessengerAddress() {
    return instantMessengerAddress;
  }


  /**
   * Sets the instantMessengerAddress value for this BuildWatcher.
   *
   * @param instantMessengerAddress
   */
  public void setInstantMessengerAddress(final java.lang.String instantMessengerAddress) {
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


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
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
            this.buildID == other.getBuildID() &&
            this.disabled == other.isDisabled() &&
            ((this.email == null && other.getEmail() == null) ||
                    (this.email != null &&
                            this.email.equals(other.getEmail()))) &&
            ((this.instantMessengerAddress == null && other.getInstantMessengerAddress() == null) ||
                    (this.instantMessengerAddress != null &&
                            this.instantMessengerAddress.equals(other.getInstantMessengerAddress()))) &&
            this.instantMessengerType == other.getInstantMessengerType() &&
            this.level == other.getLevel() &&
            this.timeStamp == other.getTimeStamp() &&
            this.watcherID == other.getWatcherID();
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
    _hashCode += (isDisabled() ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (getEmail() != null) {
      _hashCode += getEmail().hashCode();
    }
    if (getInstantMessengerAddress() != null) {
      _hashCode += getInstantMessengerAddress().hashCode();
    }
    _hashCode += getInstantMessengerType();
    _hashCode += getLevel();
    _hashCode += new Long(getTimeStamp()).hashCode();
    _hashCode += getWatcherID();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(BuildWatcher.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "BuildWatcher"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("disabled");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "disabled"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("email");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "email"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("instantMessengerAddress");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "instantMessengerAddress"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("instantMessengerType");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "instantMessengerType"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("level");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "level"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("timeStamp");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "timeStamp"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("watcherID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "watcherID"));
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
