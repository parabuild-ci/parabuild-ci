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

public class VCSUserToEmailMap implements java.io.Serializable {

  private int buildID;
  private boolean disabled;
  private java.lang.String instantMessengerAddress;
  private byte instantMessengerType;
  private int mapID;
  private long timeStamp;
  private java.lang.String userEmail;
  private java.lang.String userName;


  public VCSUserToEmailMap() {
  }


  public VCSUserToEmailMap(
          final int buildID,
          final boolean disabled,
          final java.lang.String instantMessengerAddress,
          final byte instantMessengerType,
          final int mapID,
          final long timeStamp,
          final java.lang.String userEmail,
          final java.lang.String userName) {
    this.buildID = buildID;
    this.disabled = disabled;
    this.instantMessengerAddress = instantMessengerAddress;
    this.instantMessengerType = instantMessengerType;
    this.mapID = mapID;
    this.timeStamp = timeStamp;
    this.userEmail = userEmail;
    this.userName = userName;
  }


  /**
   * Gets the buildID value for this VCSUserToEmailMap.
   *
   * @return buildID
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Sets the buildID value for this VCSUserToEmailMap.
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Gets the disabled value for this VCSUserToEmailMap.
   *
   * @return disabled
   */
  public boolean isDisabled() {
    return disabled;
  }


  /**
   * Sets the disabled value for this VCSUserToEmailMap.
   *
   * @param disabled
   */
  public void setDisabled(final boolean disabled) {
    this.disabled = disabled;
  }


  /**
   * Gets the instantMessengerAddress value for this VCSUserToEmailMap.
   *
   * @return instantMessengerAddress
   */
  public java.lang.String getInstantMessengerAddress() {
    return instantMessengerAddress;
  }


  /**
   * Sets the instantMessengerAddress value for this VCSUserToEmailMap.
   *
   * @param instantMessengerAddress
   */
  public void setInstantMessengerAddress(final java.lang.String instantMessengerAddress) {
    this.instantMessengerAddress = instantMessengerAddress;
  }


  /**
   * Gets the instantMessengerType value for this VCSUserToEmailMap.
   *
   * @return instantMessengerType
   */
  public byte getInstantMessengerType() {
    return instantMessengerType;
  }


  /**
   * Sets the instantMessengerType value for this VCSUserToEmailMap.
   *
   * @param instantMessengerType
   */
  public void setInstantMessengerType(final byte instantMessengerType) {
    this.instantMessengerType = instantMessengerType;
  }


  /**
   * Gets the mapID value for this VCSUserToEmailMap.
   *
   * @return mapID
   */
  public int getMapID() {
    return mapID;
  }


  /**
   * Sets the mapID value for this VCSUserToEmailMap.
   *
   * @param mapID
   */
  public void setMapID(final int mapID) {
    this.mapID = mapID;
  }


  /**
   * Gets the timeStamp value for this VCSUserToEmailMap.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this VCSUserToEmailMap.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the userEmail value for this VCSUserToEmailMap.
   *
   * @return userEmail
   */
  public java.lang.String getUserEmail() {
    return userEmail;
  }


  /**
   * Sets the userEmail value for this VCSUserToEmailMap.
   *
   * @param userEmail
   */
  public void setUserEmail(final java.lang.String userEmail) {
    this.userEmail = userEmail;
  }


  /**
   * Gets the userName value for this VCSUserToEmailMap.
   *
   * @return userName
   */
  public java.lang.String getUserName() {
    return userName;
  }


  /**
   * Sets the userName value for this VCSUserToEmailMap.
   *
   * @param userName
   */
  public void setUserName(final java.lang.String userName) {
    this.userName = userName;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof VCSUserToEmailMap)) return false;
    final VCSUserToEmailMap other = (VCSUserToEmailMap) obj;
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
            ((this.instantMessengerAddress == null && other.getInstantMessengerAddress() == null) ||
                    (this.instantMessengerAddress != null &&
                            this.instantMessengerAddress.equals(other.getInstantMessengerAddress()))) &&
            this.instantMessengerType == other.getInstantMessengerType() &&
            this.mapID == other.getMapID() &&
            this.timeStamp == other.getTimeStamp() &&
            ((this.userEmail == null && other.getUserEmail() == null) ||
                    (this.userEmail != null &&
                            this.userEmail.equals(other.getUserEmail()))) &&
            ((this.userName == null && other.getUserName() == null) ||
                    (this.userName != null &&
                            this.userName.equals(other.getUserName())));
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
    if (getInstantMessengerAddress() != null) {
      _hashCode += getInstantMessengerAddress().hashCode();
    }
    _hashCode += getInstantMessengerType();
    _hashCode += getMapID();
    _hashCode += new Long(getTimeStamp()).hashCode();
    if (getUserEmail() != null) {
      _hashCode += getUserEmail().hashCode();
    }
    if (getUserName() != null) {
      _hashCode += getUserName().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(VCSUserToEmailMap.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "VCSUserToEmailMap"));
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
    elemField.setFieldName("mapID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "mapID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("timeStamp");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "timeStamp"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("userEmail");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "userEmail"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("userName");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "userName"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
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
