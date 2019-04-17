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

public class DisplayGroup implements Serializable {

  private static final long serialVersionUID = -4808631421741797373L;
  private int ID;
  private String description;
  private boolean enabled;
  private String name;
  private long timeStamp;


  public DisplayGroup() {
  }


  public DisplayGroup(
          final int ID,
          final String description,
          final boolean enabled,
          final String name,
          final long timeStamp) {
    this.ID = ID;
    this.description = description;
    this.enabled = enabled;
    this.name = name;
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the ID value for this DisplayGroup.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this DisplayGroup.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the description value for this DisplayGroup.
   *
   * @return description
   */
  public String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this DisplayGroup.
   *
   * @param description
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Gets the enabled value for this DisplayGroup.
   *
   * @return enabled
   */
  public boolean isEnabled() {
    return enabled;
  }


  /**
   * Sets the enabled value for this DisplayGroup.
   *
   * @param enabled
   */
  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }


  /**
   * Gets the name value for this DisplayGroup.
   *
   * @return name
   */
  public String getName() {
    return name;
  }


  /**
   * Sets the name value for this DisplayGroup.
   *
   * @param name
   */
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Gets the timeStamp value for this DisplayGroup.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this DisplayGroup.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  private Object __equalsCalc = null;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof DisplayGroup)) return false;
    final DisplayGroup other = (DisplayGroup) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.ID &&
            ((this.description == null && other.description == null) ||
                    (this.description != null &&
                            this.description.equals(other.description))) &&
            this.enabled == other.enabled &&
            ((this.name == null && other.name == null) ||
                    (this.name != null &&
                            this.name.equals(other.name))) &&
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
    if (description != null) {
      _hashCode += description.hashCode();
    }
    _hashCode += (enabled ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (name != null) {
      _hashCode += name.hashCode();
    }
    _hashCode += new Long(timeStamp).hashCode();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(DisplayGroup.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "DisplayGroup"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "ID"));
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
    elemField.setFieldName("name");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "name"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
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
