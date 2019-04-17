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

public class Project implements Serializable {

  private static final long serialVersionUID = -5729825197486189788L;
  private int ID;
  private String description;
  private String key;
  private String name;
  private long timeStamp;
  private byte type;


  public Project() {
  }


  public Project(
          final int ID,
          final boolean deleted,
          final String description,
          final String key,
          final String name,
          final long timeStamp,
          final byte type) {
    this.ID = ID;
    this.description = description;
    this.key = key;
    this.name = name;
    this.timeStamp = timeStamp;
    this.type = type;
  }


  /**
   * Gets the ID value for this Project.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this Project.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the description value for this Project.
   *
   * @return description
   */
  public String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this Project.
   *
   * @param description
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Gets the key value for this Project.
   *
   * @return key
   */
  public String getKey() {
    return key;
  }


  /**
   * Sets the key value for this Project.
   *
   * @param key
   */
  public void setKey(final String key) {
    this.key = key;
  }


  /**
   * Gets the name value for this Project.
   *
   * @return name
   */
  public String getName() {
    return name;
  }


  /**
   * Sets the name value for this Project.
   *
   * @param name
   */
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Gets the timeStamp value for this Project.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this Project.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the type value for this Project.
   *
   * @return type
   */
  public byte getType() {
    return type;
  }


  /**
   * Sets the type value for this Project.
   *
   * @param type
   */
  public void setType(final byte type) {
    this.type = type;
  }


  private Object __equalsCalc = null;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof Project)) return false;
    final Project other = (Project) obj;
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
            ((this.key == null && other.key == null) ||
                    (this.key != null &&
                            this.key.equals(other.key))) &&
            ((this.name == null && other.name == null) ||
                    (this.name != null &&
                            this.name.equals(other.name))) &&
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
    if (description != null) {
      _hashCode += description.hashCode();
    }
    if (key != null) {
      _hashCode += key.hashCode();
    }
    if (name != null) {
      _hashCode += name.hashCode();
    }
    _hashCode += new Long(timeStamp).hashCode();
    _hashCode += type;
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(Project.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "Project"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("deleted");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "deleted"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("description");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "description"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("key");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "key"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
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
