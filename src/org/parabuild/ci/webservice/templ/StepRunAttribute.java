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

public class StepRunAttribute implements java.io.Serializable {

  private static final long serialVersionUID = -809293694454601562L;
  private int ID;
  private java.lang.String name;
  private int stepRunID;
  private long timeStamp;
  private java.lang.String value;


  public StepRunAttribute() {
  }


  public StepRunAttribute(
          final int ID,
          final java.lang.String name,
          final int stepRunID,
          final long timeStamp,
          final java.lang.String value,
          final int valueAsInt,
          final long valueAsLong) {
    this.ID = ID;
    this.name = name;
    this.stepRunID = stepRunID;
    this.timeStamp = timeStamp;
    this.value = value;
  }


  /**
   * Gets the ID value for this StepRunAttribute.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this StepRunAttribute.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the name value for this StepRunAttribute.
   *
   * @return name
   */
  public java.lang.String getName() {
    return name;
  }


  /**
   * Sets the name value for this StepRunAttribute.
   *
   * @param name
   */
  public void setName(final java.lang.String name) {
    this.name = name;
  }


  /**
   * Gets the stepRunID value for this StepRunAttribute.
   *
   * @return stepRunID
   */
  public int getStepRunID() {
    return stepRunID;
  }


  /**
   * Sets the stepRunID value for this StepRunAttribute.
   *
   * @param stepRunID
   */
  public void setStepRunID(final int stepRunID) {
    this.stepRunID = stepRunID;
  }


  /**
   * Gets the timeStamp value for this StepRunAttribute.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this StepRunAttribute.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the value value for this StepRunAttribute.
   *
   * @return value
   */
  public java.lang.String getValue() {
    return value;
  }


  /**
   * Sets the value value for this StepRunAttribute.
   *
   * @param value
   */
  public void setValue(final java.lang.String value) {
    this.value = value;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof StepRunAttribute)) return false;
    final StepRunAttribute other = (StepRunAttribute) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.ID &&
            ((this.name == null && other.name == null) ||
                    (this.name != null &&
                            this.name.equals(other.name))) &&
            this.stepRunID == other.stepRunID &&
            this.timeStamp == other.timeStamp &&
            ((this.value == null && other.value == null) ||
                    (this.value != null &&
                            this.value.equals(other.value)));
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
    if (name != null) {
      _hashCode += name.hashCode();
    }
    _hashCode += stepRunID;
    _hashCode += new Long(timeStamp).hashCode();
    if (value != null) {
      _hashCode += value.hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(StepRunAttribute.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "StepRunAttribute"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("name");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "name"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("stepRunID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "stepRunID"));
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
    elemField.setFieldName("value");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "value"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("valueAsInt");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "valueAsInt"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("valueAsLong");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "valueAsLong"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
