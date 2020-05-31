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

public class AgentConfiguration implements Serializable {

  private static final long serialVersionUID = 3909159474735955447L;
  private int ID;
  private String description;
  private boolean enabled;
  private String host;
  private boolean local;
  private long timeStamp;


  public AgentConfiguration() {
  }


  public AgentConfiguration(
          final int ID,
          final boolean deleted,
          final String description,
          final boolean enabled,
          final String host,
          final boolean local,
          final String password,
          final String portAsString,
          final long timeStamp) {
    this.ID = ID;
    this.description = description;
    this.enabled = enabled;
    this.host = host;
    this.local = local;
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the ID value for this AgentConfig.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this AgentConfig.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the description value for this AgentConfig.
   *
   * @return description
   */
  public String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this AgentConfig.
   *
   * @param description
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Gets the enabled value for this AgentConfig.
   *
   * @return enabled
   */
  public boolean isEnabled() {
    return enabled;
  }


  /**
   * Sets the enabled value for this AgentConfig.
   *
   * @param enabled
   */
  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }


  /**
   * Gets the host value for this AgentConfig.
   *
   * @return host
   */
  public String getHost() {
    return host;
  }


  /**
   * Sets the host value for this AgentConfig.
   *
   * @param host
   */
  public void setHost(final String host) {
    this.host = host;
  }


  /**
   * Gets the local value for this AgentConfig.
   *
   * @return local
   */
  public boolean isLocal() {
    return local;
  }


  /**
   * Sets the local value for this AgentConfig.
   *
   * @param local
   */
  public void setLocal(final boolean local) {
    this.local = local;
  }


  /**
   * Gets the timeStamp value for this AgentConfig.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this AgentConfig.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  private Object __equalsCalc;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof AgentConfiguration)) return false;
    final AgentConfiguration other = (AgentConfiguration) obj;
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
            ((this.host == null && other.host == null) ||
                    (this.host != null &&
                            this.host.equals(other.host))) &&
            this.local == other.local &&
            this.timeStamp == other.timeStamp;
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
    if (description != null) {
      _hashCode += description.hashCode();
    }
    _hashCode += (enabled ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (host != null) {
      _hashCode += host.hashCode();
    }
    _hashCode += (local ? Boolean.TRUE : Boolean.FALSE).hashCode();
    _hashCode += Long.valueOf(timeStamp).hashCode();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(AgentConfiguration.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "AgentConfig"));
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
    elemField.setFieldName("enabled");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "enabled"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("host");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "host"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("local");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "local"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("password");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "password"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("portAsString");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "portAsString"));
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
