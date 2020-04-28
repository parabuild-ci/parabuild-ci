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

public class Change implements Serializable {

  private static final long serialVersionUID = 8880093469929434161L;
  private int changeID;
  private int changeListID;
  private byte changeType;
  private String filePath;
  private String revision;


  public Change() {
  }


  public Change(
          final int changeID,
          final int changeListID,
          final byte changeType,
          final String filePath,
          final String revision) {
    this.changeID = changeID;
    this.changeListID = changeListID;
    this.changeType = changeType;
    this.filePath = filePath;
    this.revision = revision;
  }


  /**
   * Gets the changeID value for this Change.
   *
   * @return changeID
   */
  public int getChangeID() {
    return changeID;
  }


  /**
   * Sets the changeID value for this Change.
   *
   * @param changeID
   */
  public void setChangeID(final int changeID) {
    this.changeID = changeID;
  }


  /**
   * Gets the changeListID value for this Change.
   *
   * @return changeListID
   */
  public int getChangeListID() {
    return changeListID;
  }


  /**
   * Sets the changeListID value for this Change.
   *
   * @param changeListID
   */
  public void setChangeListID(final int changeListID) {
    this.changeListID = changeListID;
  }


  /**
   * Gets the changeType value for this Change.
   *
   * @return changeType
   */
  public byte getChangeType() {
    return changeType;
  }


  /**
   * Sets the changeType value for this Change.
   *
   * @param changeType
   */
  public void setChangeType(final byte changeType) {
    this.changeType = changeType;
  }


  /**
   * Gets the filePath value for this Change.
   *
   * @return filePath
   */
  public String getFilePath() {
    return filePath;
  }


  /**
   * Sets the filePath value for this Change.
   *
   * @param filePath
   */
  public void setFilePath(final String filePath) {
    this.filePath = filePath;
  }


  /**
   * Gets the revision value for this Change.
   *
   * @return revision
   */
  public String getRevision() {
    return revision;
  }


  /**
   * Sets the revision value for this Change.
   *
   * @param revision
   */
  public void setRevision(final String revision) {
    this.revision = revision;
  }


  private Object __equalsCalc;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof Change)) return false;
    final Change other = (Change) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.changeID == other.changeID &&
            this.changeListID == other.changeListID &&
            this.changeType == other.changeType &&
            ((this.filePath == null && other.filePath == null) ||
                    (this.filePath != null &&
                            this.filePath.equals(other.filePath))) &&
            ((this.revision == null && other.revision == null) ||
                    (this.revision != null &&
                            this.revision.equals(other.revision)));
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
    _hashCode += changeID;
    _hashCode += changeListID;
    _hashCode += changeType;
    if (filePath != null) {
      _hashCode += filePath.hashCode();
    }
    if (revision != null) {
      _hashCode += revision.hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(Change.class, true);


  static {
    typeDesc.setXmlType(new QName("http://webservice.ci.parabuild.org", "Change"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("changeID");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "changeID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("changeListID");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "changeListID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("changeType");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "changeType"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("filePath");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "filePath"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("revision");
    elemField.setXmlName(new QName("http://webservice.ci.parabuild.org", "revision"));
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
