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

public class GlobalVCSUserMap implements Serializable {

  private static final long serialVersionUID = -2771061619820069326L;
  private int ID;
  private String description;
  private String email;
  private String vcsUserName;


  public GlobalVCSUserMap() {
  }


  public GlobalVCSUserMap(
          final int ID,
          final String description,
          final String email,
          final String vcsUserName) {
    this.ID = ID;
    this.description = description;
    this.email = email;
    this.vcsUserName = vcsUserName;
  }


  /**
   * Gets the ID value for this GlobalVCSUserMap.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this GlobalVCSUserMap.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the description value for this GlobalVCSUserMap.
   *
   * @return description
   */
  public String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this GlobalVCSUserMap.
   *
   * @param description
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Gets the email value for this GlobalVCSUserMap.
   *
   * @return email
   */
  public String getEmail() {
    return email;
  }


  /**
   * Sets the email value for this GlobalVCSUserMap.
   *
   * @param email
   */
  public void setEmail(final String email) {
    this.email = email;
  }


  /**
   * Gets the vcsUserName value for this GlobalVCSUserMap.
   *
   * @return vcsUserName
   */
  public String getVcsUserName() {
    return vcsUserName;
  }


  /**
   * Sets the vcsUserName value for this GlobalVCSUserMap.
   *
   * @param vcsUserName
   */
  public void setVcsUserName(final String vcsUserName) {
    this.vcsUserName = vcsUserName;
  }


  private Object __equalsCalc = null;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof GlobalVCSUserMap)) return false;
    final GlobalVCSUserMap other = (GlobalVCSUserMap) obj;
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
            ((this.email == null && other.email == null) ||
                    (this.email != null &&
                            this.email.equals(other.email))) &&
            ((this.vcsUserName == null && other.vcsUserName == null) ||
                    (this.vcsUserName != null &&
                            this.vcsUserName.equals(other.vcsUserName)));
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
    if (email != null) {
      _hashCode += email.hashCode();
    }
    if (vcsUserName != null) {
      _hashCode += vcsUserName.hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(GlobalVCSUserMap.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "GlobalVCSUserMap"));
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
    elemField.setFieldName("email");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "email"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("vcsUserName");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "vcsUserName"));
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
