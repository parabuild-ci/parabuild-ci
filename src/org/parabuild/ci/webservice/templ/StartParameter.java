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

public class StartParameter implements Serializable {

  private static final long serialVersionUID = -5206409257451301526L;
  private int ID;
  private int buildID;
  private String description;
  private boolean enabled;
  private String firstValue;
  private boolean modifiable;
  private String name;
  private byte presentation;
  private boolean required;
  private String runtimeValue;
  private long timeStamp;
  private byte type;
  private String value;


  public StartParameter() {
  }


  public StartParameter(
          final int ID,
          final int buildID,
          final String description,
          final boolean enabled,
          final String firstValue,
          final boolean modifiable,
          final String name,
          final byte presentation,
          final boolean required,
          final String runtimeValue,
          final long timeStamp,
          final byte type,
          final String value) {
    this.ID = ID;
    this.buildID = buildID;
    this.description = description;
    this.enabled = enabled;
    this.firstValue = firstValue;
    this.modifiable = modifiable;
    this.name = name;
    this.presentation = presentation;
    this.required = required;
    this.runtimeValue = runtimeValue;
    this.timeStamp = timeStamp;
    this.type = type;
    this.value = value;
  }


  /**
   * Gets the ID value for this StartParameter.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this StartParameter.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the buildID value for this StartParameter.
   *
   * @return buildID
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Sets the buildID value for this StartParameter.
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Gets the description value for this StartParameter.
   *
   * @return description
   */
  public String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this StartParameter.
   *
   * @param description
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Gets the enabled value for this StartParameter.
   *
   * @return enabled
   */
  public boolean isEnabled() {
    return enabled;
  }


  /**
   * Sets the enabled value for this StartParameter.
   *
   * @param enabled
   */
  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }


  /**
   * Gets the firstValue value for this StartParameter.
   *
   * @return firstValue
   */
  public String getFirstValue() {
    return firstValue;
  }


  /**
   * Sets the firstValue value for this StartParameter.
   *
   * @param firstValue
   */
  public void setFirstValue(final String firstValue) {
    this.firstValue = firstValue;
  }


  /**
   * Gets the modifiable value for this StartParameter.
   *
   * @return modifiable
   */
  public boolean isModifiable() {
    return modifiable;
  }


  /**
   * Sets the modifiable value for this StartParameter.
   *
   * @param modifiable
   */
  public void setModifiable(final boolean modifiable) {
    this.modifiable = modifiable;
  }


  /**
   * Gets the name value for this StartParameter.
   *
   * @return name
   */
  public String getName() {
    return name;
  }


  /**
   * Sets the name value for this StartParameter.
   *
   * @param name
   */
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Gets the presentation value for this StartParameter.
   *
   * @return presentation
   */
  public byte getPresentation() {
    return presentation;
  }


  /**
   * Sets the presentation value for this StartParameter.
   *
   * @param presentation
   */
  public void setPresentation(final byte presentation) {
    this.presentation = presentation;
  }


  /**
   * Gets the required value for this StartParameter.
   *
   * @return required
   */
  public boolean isRequired() {
    return required;
  }


  /**
   * Sets the required value for this StartParameter.
   *
   * @param required
   */
  public void setRequired(final boolean required) {
    this.required = required;
  }


  /**
   * Gets the runtimeValue value for this StartParameter.
   *
   * @return runtimeValue
   */
  public String getRuntimeValue() {
    return runtimeValue;
  }


  /**
   * Sets the runtimeValue value for this StartParameter.
   *
   * @param runtimeValue
   */
  public void setRuntimeValue(final String runtimeValue) {
    this.runtimeValue = runtimeValue;
  }


  /**
   * Gets the timeStamp value for this StartParameter.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this StartParameter.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the type value for this StartParameter.
   *
   * @return type
   */
  public byte getType() {
    return type;
  }


  /**
   * Sets the type value for this StartParameter.
   *
   * @param type
   */
  public void setType(final byte type) {
    this.type = type;
  }


  /**
   * Gets the value value for this StartParameter.
   *
   * @return value
   */
  public String getValue() {
    return value;
  }


  /**
   * Sets the value value for this StartParameter.
   *
   * @param value
   */
  public void setValue(final String value) {
    this.value = value;
  }


  private Object __equalsCalc;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof StartParameter)) return false;
    final StartParameter other = (StartParameter) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.ID &&
            this.buildID == other.buildID &&
            ((this.description == null && other.description == null) ||
                    (this.description != null &&
                            this.description.equals(other.description))) &&
            this.enabled == other.enabled &&
            ((this.firstValue == null && other.firstValue == null) ||
                    (this.firstValue != null &&
                            this.firstValue.equals(other.firstValue))) &&
            this.modifiable == other.modifiable &&
            ((this.name == null && other.name == null) ||
                    (this.name != null &&
                            this.name.equals(other.name))) &&
            this.presentation == other.presentation &&
            this.required == other.required &&
            ((this.runtimeValue == null && other.runtimeValue == null) ||
                    (this.runtimeValue != null &&
                            this.runtimeValue.equals(other.runtimeValue))) &&
            this.timeStamp == other.timeStamp &&
            this.type == other.type &&
            ((this.value == null && other.value == null) ||
                    (this.value != null &&
                            this.value.equals(other.value)));
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
    _hashCode += ID;
    _hashCode += buildID;
    if (description != null) {
      _hashCode += description.hashCode();
    }
    _hashCode += (enabled ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (firstValue != null) {
      _hashCode += firstValue.hashCode();
    }
    _hashCode += (modifiable ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (name != null) {
      _hashCode += name.hashCode();
    }
    _hashCode += presentation;
    _hashCode += (required ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (runtimeValue != null) {
      _hashCode += runtimeValue.hashCode();
    }
    _hashCode += Long.valueOf(timeStamp).hashCode();
    _hashCode += type;
    if (value != null) {
      _hashCode += value.hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(StartParameter.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "StartParameter"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
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
    elemField.setFieldName("enabled");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "enabled"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("firstValue");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "firstValue"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("modifiable");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "modifiable"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("name");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "name"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("presentation");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "presentation"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("required");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "required"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("runtimeValue");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "runtimeValue"));
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
    elemField = new ElementDesc();
    elemField.setFieldName("value");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "value"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
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
