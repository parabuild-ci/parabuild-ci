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

public class BuildConfiguration implements Serializable {

  private static final long serialVersionUID = -8504746824236764604L;
  private byte access;
  private int activeBuildID;
  private int buildID;
  private String buildName;
  private int farmID;
  private String emailDomain;
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
          final String buildName,
          final int farmID,
          final String emailDomain,
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
  public String getBuildName() {
    return buildName;
  }


  /**
   * Sets the buildName value for this BuildConfiguration.
   *
   * @param buildName
   */
  public void setBuildName(final String buildName) {
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
  public String getEmailDomain() {
    return emailDomain;
  }


  /**
   * Sets the emailDomain value for this BuildConfiguration.
   *
   * @param emailDomain
   */
  public void setEmailDomain(final String emailDomain) {
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


  private Object __equalsCalc;


  public synchronized boolean equals(final Object obj) {
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


  private boolean __hashCodeCalc;


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
  private static final TypeDesc typeDesc =
          new TypeDesc(BuildConfiguration.class, true);


  static {
    typeDesc.setXmlType(new QName("http://webservice.ci.parabuild.org", "BuildConfiguration"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("access");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "access"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("activeBuildID");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "activeBuildID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "buildID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("buildName");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "buildName"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("builderID");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "builderID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("emailDomain");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "emailDomain"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("scheduleType");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "scheduleType"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("sourceControl");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "sourceControl"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("sourceControlEmail");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "sourceControlEmail"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("subordinate");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "subordinate"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
