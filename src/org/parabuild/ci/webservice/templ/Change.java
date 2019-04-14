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

public class Change implements java.io.Serializable {

  private int changeID;
  private int changeListID;
  private byte changeType;
  private java.lang.String filePath;
  private java.lang.String revision;


  public Change() {
  }


  public Change(
          final int changeID,
          final int changeListID,
          final byte changeType,
          final java.lang.String filePath,
          final java.lang.String revision) {
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
  public java.lang.String getFilePath() {
    return filePath;
  }


  /**
   * Sets the filePath value for this Change.
   *
   * @param filePath
   */
  public void setFilePath(final java.lang.String filePath) {
    this.filePath = filePath;
  }


  /**
   * Gets the revision value for this Change.
   *
   * @return revision
   */
  public java.lang.String getRevision() {
    return revision;
  }


  /**
   * Sets the revision value for this Change.
   *
   * @param revision
   */
  public void setRevision(final java.lang.String revision) {
    this.revision = revision;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
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
            this.changeID == other.getChangeID() &&
            this.changeListID == other.getChangeListID() &&
            this.changeType == other.getChangeType() &&
            ((this.filePath == null && other.getFilePath() == null) ||
                    (this.filePath != null &&
                            this.filePath.equals(other.getFilePath()))) &&
            ((this.revision == null && other.getRevision() == null) ||
                    (this.revision != null &&
                            this.revision.equals(other.getRevision())));
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
    _hashCode += getChangeID();
    _hashCode += getChangeListID();
    _hashCode += getChangeType();
    if (getFilePath() != null) {
      _hashCode += getFilePath().hashCode();
    }
    if (getRevision() != null) {
      _hashCode += getRevision().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(Change.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "Change"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("changeID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "changeID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("changeListID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "changeListID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("changeType");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "changeType"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("filePath");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "filePath"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("revision");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "revision"));
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
