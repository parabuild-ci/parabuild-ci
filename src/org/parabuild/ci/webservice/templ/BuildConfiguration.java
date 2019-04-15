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

public class BuildConfiguration implements java.io.Serializable {

  private byte access;
  private int activeBuildID;
  private int buildID;
  private java.lang.String buildName;
  private int farmID;
  private java.lang.String emailDomain;
  private byte scheduleType;
  private byte sourceControl;
  private boolean sourceControlEmail;
  private boolean subordinate;


  public BuildConfiguration() {
  }


  public BuildConfiguration(
          final byte access,
          final int activeBuildID,
          final int buildID,
          final java.lang.String buildName,
          final int farmID,
          final java.lang.String emailDomain,
          final byte scheduleType,
          final byte sourceControl,
          final boolean sourceControlEmail,
          final boolean subordinate) {
    this.access = access;
    this.activeBuildID = activeBuildID;
    this.buildID = buildID;
    this.buildName = buildName;
    this.farmID = farmID;
    this.emailDomain = emailDomain;
    this.scheduleType = scheduleType;
    this.sourceControl = sourceControl;
    this.sourceControlEmail = sourceControlEmail;
    this.subordinate = subordinate;
  }


  /**
   * Gets the access value for this BuildConfiguration.
   *
   * @return access
   */
  public byte getAccess() {
    return access;
  }


  /**
   * Sets the access value for this BuildConfiguration.
   *
   * @param access
   */
  public void setAccess(final byte access) {
    this.access = access;
  }


  /**
   * Gets the activeBuildID value for this BuildConfiguration.
   *
   * @return activeBuildID
   */
  public int getActiveBuildID() {
    return activeBuildID;
  }


  /**
   * Sets the activeBuildID value for this BuildConfiguration.
   *
   * @param activeBuildID
   */
  public void setActiveBuildID(final int activeBuildID) {
    this.activeBuildID = activeBuildID;
  }


  /**
   * Gets the buildID value for this BuildConfiguration.
   *
   * @return buildID
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Sets the buildID value for this BuildConfiguration.
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Gets the buildName value for this BuildConfiguration.
   *
   * @return buildName
   */
  public java.lang.String getBuildName() {
    return buildName;
  }


  /**
   * Sets the buildName value for this BuildConfiguration.
   *
   * @param buildName
   */
  public void setBuildName(final java.lang.String buildName) {
    this.buildName = buildName;
  }


  /**
   * Gets the builderID value for this BuildConfiguration.
   *
   * @return builderID
   */
  public int getFarmID() {
    return farmID;
  }


  /**
   * Sets the builderID value for this BuildConfiguration.
   *
   * @param farmID
   */
  public void setFarmID(final int farmID) {
    this.farmID = farmID;
  }


  /**
   * Gets the emailDomain value for this BuildConfiguration.
   *
   * @return emailDomain
   */
  public java.lang.String getEmailDomain() {
    return emailDomain;
  }


  /**
   * Sets the emailDomain value for this BuildConfiguration.
   *
   * @param emailDomain
   */
  public void setEmailDomain(final java.lang.String emailDomain) {
    this.emailDomain = emailDomain;
  }


  /**
   * Gets the scheduleType value for this BuildConfiguration.
   *
   * @return scheduleType
   */
  public byte getScheduleType() {
    return scheduleType;
  }


  /**
   * Sets the scheduleType value for this BuildConfiguration.
   *
   * @param scheduleType
   */
  public void setScheduleType(final byte scheduleType) {
    this.scheduleType = scheduleType;
  }


  /**
   * Gets the sourceControl value for this BuildConfiguration.
   *
   * @return sourceControl
   */
  public byte getSourceControl() {
    return sourceControl;
  }


  /**
   * Sets the sourceControl value for this BuildConfiguration.
   *
   * @param sourceControl
   */
  public void setSourceControl(final byte sourceControl) {
    this.sourceControl = sourceControl;
  }


  /**
   * Gets the sourceControlEmail value for this BuildConfiguration.
   *
   * @return sourceControlEmail
   */
  public boolean isSourceControlEmail() {
    return sourceControlEmail;
  }


  /**
   * Sets the sourceControlEmail value for this BuildConfiguration.
   *
   * @param sourceControlEmail
   */
  public void setSourceControlEmail(final boolean sourceControlEmail) {
    this.sourceControlEmail = sourceControlEmail;
  }


  /**
   * Gets the subordinate value for this BuildConfiguration.
   *
   * @return subordinate
   */
  public boolean isSubordinate() {
    return subordinate;
  }


  /**
   * Sets the subordinate value for this BuildConfiguration.
   *
   * @param subordinate
   */
  public void setSubordinate(final boolean subordinate) {
    this.subordinate = subordinate;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof BuildConfiguration)) return false;
    final BuildConfiguration other = (BuildConfiguration) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.access == other.access &&
            this.activeBuildID == other.activeBuildID &&
            this.buildID == other.buildID &&
            ((this.buildName == null && other.buildName == null) ||
                    (this.buildName != null &&
                            this.buildName.equals(other.buildName))) &&
            this.farmID == other.farmID &&
            ((this.emailDomain == null && other.emailDomain == null) ||
                    (this.emailDomain != null &&
                            this.emailDomain.equals(other.emailDomain))) &&
            this.scheduleType == other.scheduleType &&
            this.sourceControl == other.sourceControl &&
            this.sourceControlEmail == other.sourceControlEmail &&
            this.subordinate == other.subordinate;
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
    _hashCode += access;
    _hashCode += activeBuildID;
    _hashCode += buildID;
    if (buildName != null) {
      _hashCode += buildName.hashCode();
    }
    _hashCode += farmID;
    if (emailDomain != null) {
      _hashCode += emailDomain.hashCode();
    }
    _hashCode += scheduleType;
    _hashCode += sourceControl;
    _hashCode += (sourceControlEmail ? Boolean.TRUE : Boolean.FALSE).hashCode();
    _hashCode += (subordinate ? Boolean.TRUE : Boolean.FALSE).hashCode();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(BuildConfiguration.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "BuildConfiguration"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("access");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "access"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("activeBuildID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "activeBuildID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "buildID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildName");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "buildName"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("builderID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "builderID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("emailDomain");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "emailDomain"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("scheduleType");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "scheduleType"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("sourceControl");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "sourceControl"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("sourceControlEmail");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "sourceControlEmail"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("subordinate");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "subordinate"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
