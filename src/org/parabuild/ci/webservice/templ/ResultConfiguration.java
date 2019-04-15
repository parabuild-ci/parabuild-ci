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

public class ResultConfiguration implements Serializable {

  private static final long serialVersionUID = -6059802519072743272L;
  private int ID;
  private Integer autopublishGroupID;
  private int buildID;
  private String description;
  private boolean failIfNotFound;
  private boolean ignoreTimestamp;
  private String path;
  private String shellVariable;
  private long timeStamp;
  private byte type;


  public ResultConfiguration() {
  }


  public ResultConfiguration(
          final int ID,
          final Integer autopublishGroupID,
          final int buildID,
          final String description,
          final boolean failIfNotFound,
          final boolean ignoreTimestamp,
          final String path,
          final String shellVariable,
          final long timeStamp,
          final byte type) {
    this.ID = ID;
    this.autopublishGroupID = autopublishGroupID;
    this.buildID = buildID;
    this.description = description;
    this.failIfNotFound = failIfNotFound;
    this.ignoreTimestamp = ignoreTimestamp;
    this.path = path;
    this.shellVariable = shellVariable;
    this.timeStamp = timeStamp;
    this.type = type;
  }


  /**
   * Gets the ID value for this ResultConfig.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this ResultConfig.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the autopublishGroupID value for this ResultConfig.
   *
   * @return autopublishGroupID
   */
  public Integer getAutopublishGroupID() {
    return autopublishGroupID;
  }


  /**
   * Sets the autopublishGroupID value for this ResultConfig.
   *
   * @param autopublishGroupID
   */
  public void setAutopublishGroupID(final Integer autopublishGroupID) {
    this.autopublishGroupID = autopublishGroupID;
  }


  /**
   * Gets the buildID value for this ResultConfig.
   *
   * @return buildID
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Sets the buildID value for this ResultConfig.
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Gets the description value for this ResultConfig.
   *
   * @return description
   */
  public String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this ResultConfig.
   *
   * @param description
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Gets the failIfNotFound value for this ResultConfig.
   *
   * @return failIfNotFound
   */
  public boolean isFailIfNotFound() {
    return failIfNotFound;
  }


  /**
   * Sets the failIfNotFound value for this ResultConfig.
   *
   * @param failIfNotFound
   */
  public void setFailIfNotFound(final boolean failIfNotFound) {
    this.failIfNotFound = failIfNotFound;
  }


  /**
   * Gets the ignoreTimestamp value for this ResultConfig.
   *
   * @return ignoreTimestamp
   */
  public boolean isIgnoreTimestamp() {
    return ignoreTimestamp;
  }


  /**
   * Sets the ignoreTimestamp value for this ResultConfig.
   *
   * @param ignoreTimestamp
   */
  public void setIgnoreTimestamp(final boolean ignoreTimestamp) {
    this.ignoreTimestamp = ignoreTimestamp;
  }


  /**
   * Gets the path value for this ResultConfig.
   *
   * @return path
   */
  public String getPath() {
    return path;
  }


  /**
   * Sets the path value for this ResultConfig.
   *
   * @param path
   */
  public void setPath(final String path) {
    this.path = path;
  }


  /**
   * Gets the shellVariable value for this ResultConfig.
   *
   * @return shellVariable
   */
  public String getShellVariable() {
    return shellVariable;
  }


  /**
   * Sets the shellVariable value for this ResultConfig.
   *
   * @param shellVariable
   */
  public void setShellVariable(final String shellVariable) {
    this.shellVariable = shellVariable;
  }


  /**
   * Gets the timeStamp value for this ResultConfig.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this ResultConfig.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the type value for this ResultConfig.
   *
   * @return type
   */
  public byte getType() {
    return type;
  }


  /**
   * Sets the type value for this ResultConfig.
   *
   * @param type
   */
  public void setType(final byte type) {
    this.type = type;
  }


  private Object __equalsCalc = null;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof ResultConfiguration)) return false;
    final ResultConfiguration other = (ResultConfiguration) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.ID &&
            ((this.autopublishGroupID == null && other.autopublishGroupID == null) ||
                    (this.autopublishGroupID != null &&
                            this.autopublishGroupID.equals(other.autopublishGroupID))) &&
            this.buildID == other.buildID &&
            ((this.description == null && other.description == null) ||
                    (this.description != null &&
                            this.description.equals(other.description))) &&
            this.failIfNotFound == other.failIfNotFound &&
            this.ignoreTimestamp == other.ignoreTimestamp &&
            ((this.path == null && other.path == null) ||
                    (this.path != null &&
                            this.path.equals(other.path))) &&
            ((this.shellVariable == null && other.shellVariable == null) ||
                    (this.shellVariable != null &&
                            this.shellVariable.equals(other.shellVariable))) &&
            this.timeStamp == other.timeStamp &&
            this.type == other.type;
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
    if (autopublishGroupID != null) {
      _hashCode += autopublishGroupID.hashCode();
    }
    _hashCode += buildID;
    if (description != null) {
      _hashCode += description.hashCode();
    }
    _hashCode += (failIfNotFound ? Boolean.TRUE : Boolean.FALSE).hashCode();
    _hashCode += (ignoreTimestamp ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (path != null) {
      _hashCode += path.hashCode();
    }
    if (shellVariable != null) {
      _hashCode += shellVariable.hashCode();
    }
    _hashCode += new Long(timeStamp).hashCode();
    _hashCode += type;
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(ResultConfiguration.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "ResultConfig"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("autopublishGroupID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "autopublishGroupID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "buildID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("description");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "description"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("failIfNotFound");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "failIfNotFound"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("ignoreTimestamp");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "ignoreTimestamp"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("path");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "path"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("shellVariable");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "shellVariable"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("timeStamp");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "timeStamp"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("type");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "type"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
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
