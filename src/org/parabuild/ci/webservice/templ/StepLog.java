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

public class StepLog implements java.io.Serializable {

  private int ID;
  private java.lang.String archiveFileName;
  private java.lang.String description;
  private byte found;
  private java.lang.String path;
  private byte pathType;
  private int stepRunID;
  private long timeStamp;
  private byte type;


  public StepLog() {
  }


  public StepLog(
          final int ID,
          final java.lang.String IDAsString,
          final java.lang.String archiveFileName,
          final java.lang.String description,
          final byte found,
          final java.lang.String path,
          final byte pathType,
          final int stepRunID,
          final long timeStamp,
          final byte type) {
    this.ID = ID;
    this.archiveFileName = archiveFileName;
    this.description = description;
    this.found = found;
    this.path = path;
    this.pathType = pathType;
    this.stepRunID = stepRunID;
    this.timeStamp = timeStamp;
    this.type = type;
  }


  /**
   * Gets the ID value for this StepLog.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this StepLog.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the archiveFileName value for this StepLog.
   *
   * @return archiveFileName
   */
  public java.lang.String getArchiveFileName() {
    return archiveFileName;
  }


  /**
   * Sets the archiveFileName value for this StepLog.
   *
   * @param archiveFileName
   */
  public void setArchiveFileName(final java.lang.String archiveFileName) {
    this.archiveFileName = archiveFileName;
  }


  /**
   * Gets the description value for this StepLog.
   *
   * @return description
   */
  public java.lang.String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this StepLog.
   *
   * @param description
   */
  public void setDescription(final java.lang.String description) {
    this.description = description;
  }


  /**
   * Gets the found value for this StepLog.
   *
   * @return found
   */
  public byte getFound() {
    return found;
  }


  /**
   * Sets the found value for this StepLog.
   *
   * @param found
   */
  public void setFound(final byte found) {
    this.found = found;
  }


  /**
   * Gets the path value for this StepLog.
   *
   * @return path
   */
  public java.lang.String getPath() {
    return path;
  }


  /**
   * Sets the path value for this StepLog.
   *
   * @param path
   */
  public void setPath(final java.lang.String path) {
    this.path = path;
  }


  /**
   * Gets the pathType value for this StepLog.
   *
   * @return pathType
   */
  public byte getPathType() {
    return pathType;
  }


  /**
   * Sets the pathType value for this StepLog.
   *
   * @param pathType
   */
  public void setPathType(final byte pathType) {
    this.pathType = pathType;
  }


  /**
   * Gets the stepRunID value for this StepLog.
   *
   * @return stepRunID
   */
  public int getStepRunID() {
    return stepRunID;
  }


  /**
   * Sets the stepRunID value for this StepLog.
   *
   * @param stepRunID
   */
  public void setStepRunID(final int stepRunID) {
    this.stepRunID = stepRunID;
  }


  /**
   * Gets the timeStamp value for this StepLog.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this StepLog.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the type value for this StepLog.
   *
   * @return type
   */
  public byte getType() {
    return type;
  }


  /**
   * Sets the type value for this StepLog.
   *
   * @param type
   */
  public void setType(final byte type) {
    this.type = type;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof StepLog)) return false;
    final StepLog other = (StepLog) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.getID() &&
            ((this.archiveFileName == null && other.getArchiveFileName() == null) ||
                    (this.archiveFileName != null &&
                            this.archiveFileName.equals(other.getArchiveFileName()))) &&
            ((this.description == null && other.getDescription() == null) ||
                    (this.description != null &&
                            this.description.equals(other.getDescription()))) &&
            this.found == other.getFound() &&
            ((this.path == null && other.getPath() == null) ||
                    (this.path != null &&
                            this.path.equals(other.getPath()))) &&
            this.pathType == other.getPathType() &&
            this.stepRunID == other.getStepRunID() &&
            this.timeStamp == other.getTimeStamp() &&
            this.type == other.getType();
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
    if (getArchiveFileName() != null) {
      _hashCode += getArchiveFileName().hashCode();
    }
    if (getDescription() != null) {
      _hashCode += getDescription().hashCode();
    }
    _hashCode += getFound();
    if (getPath() != null) {
      _hashCode += getPath().hashCode();
    }
    _hashCode += getPathType();
    _hashCode += getStepRunID();
    _hashCode += new Long(getTimeStamp()).hashCode();
    _hashCode += getType();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(StepLog.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "StepLog"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("IDAsString");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "IDAsString"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("archiveFileName");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "archiveFileName"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("description");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "description"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("found");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "found"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("path");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "path"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("pathType");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "pathType"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
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
    elemField.setFieldName("type");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "type"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
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
