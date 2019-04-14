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

public class StartParameter implements java.io.Serializable {

  private int ID;
  private int buildID;
  private java.lang.String description;
  private boolean enabled;
  private java.lang.String firstValue;
  private boolean modifiable;
  private java.lang.String name;
  private byte presentation;
  private boolean required;
  private java.lang.String runtimeValue;
  private long timeStamp;
  private byte type;
  private java.lang.String value;


  public StartParameter() {
  }


  public StartParameter(
          final int ID,
          final int buildID,
          final java.lang.String description,
          final boolean enabled,
          final java.lang.String firstValue,
          final boolean modifiable,
          final java.lang.String name,
          final byte presentation,
          final boolean required,
          final java.lang.String runtimeValue,
          final long timeStamp,
          final byte type,
          final java.lang.String value) {
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
  public java.lang.String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this StartParameter.
   *
   * @param description
   */
  public void setDescription(final java.lang.String description) {
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
  public java.lang.String getFirstValue() {
    return firstValue;
  }


  /**
   * Sets the firstValue value for this StartParameter.
   *
   * @param firstValue
   */
  public void setFirstValue(final java.lang.String firstValue) {
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
  public java.lang.String getName() {
    return name;
  }


  /**
   * Sets the name value for this StartParameter.
   *
   * @param name
   */
  public void setName(final java.lang.String name) {
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
  public java.lang.String getRuntimeValue() {
    return runtimeValue;
  }


  /**
   * Sets the runtimeValue value for this StartParameter.
   *
   * @param runtimeValue
   */
  public void setRuntimeValue(final java.lang.String runtimeValue) {
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
  public java.lang.String getValue() {
    return value;
  }


  /**
   * Sets the value value for this StartParameter.
   *
   * @param value
   */
  public void setValue(final java.lang.String value) {
    this.value = value;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
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
            this.ID == other.getID() &&
            this.buildID == other.getBuildID() &&
            ((this.description == null && other.getDescription() == null) ||
                    (this.description != null &&
                            this.description.equals(other.getDescription()))) &&
            this.enabled == other.isEnabled() &&
            ((this.firstValue == null && other.getFirstValue() == null) ||
                    (this.firstValue != null &&
                            this.firstValue.equals(other.getFirstValue()))) &&
            this.modifiable == other.isModifiable() &&
            ((this.name == null && other.getName() == null) ||
                    (this.name != null &&
                            this.name.equals(other.getName()))) &&
            this.presentation == other.getPresentation() &&
            this.required == other.isRequired() &&
            ((this.runtimeValue == null && other.getRuntimeValue() == null) ||
                    (this.runtimeValue != null &&
                            this.runtimeValue.equals(other.getRuntimeValue()))) &&
            this.timeStamp == other.getTimeStamp() &&
            this.type == other.getType() &&
            ((this.value == null && other.getValue() == null) ||
                    (this.value != null &&
                            this.value.equals(other.getValue())));
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
    _hashCode += getID();
    _hashCode += getBuildID();
    if (getDescription() != null) {
      _hashCode += getDescription().hashCode();
    }
    _hashCode += (isEnabled() ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (getFirstValue() != null) {
      _hashCode += getFirstValue().hashCode();
    }
    _hashCode += (isModifiable() ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (getName() != null) {
      _hashCode += getName().hashCode();
    }
    _hashCode += getPresentation();
    _hashCode += (isRequired() ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (getRuntimeValue() != null) {
      _hashCode += getRuntimeValue().hashCode();
    }
    _hashCode += new Long(getTimeStamp()).hashCode();
    _hashCode += getType();
    if (getValue() != null) {
      _hashCode += getValue().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(StartParameter.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "StartParameter"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("description");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "description"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("enabled");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "enabled"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("firstValue");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "firstValue"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("modifiable");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "modifiable"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("name");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "name"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("presentation");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "presentation"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("required");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "required"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("runtimeValue");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "runtimeValue"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("timeStamp");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "timeStamp"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("type");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "type"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("value");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "value"));
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
