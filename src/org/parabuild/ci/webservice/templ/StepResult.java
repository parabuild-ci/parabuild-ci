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

public class StepResult implements Serializable {

  private static final long serialVersionUID = 1175122949823303332L;
  private int ID;
  private String archiveFileName;
  private String description;
  private boolean found;
  private String path;
  private byte pathType;
  private boolean pinned;
  private int stepRunID;
  private String[] urls;


  public StepResult() {
  }


  public StepResult(
          final int ID,
          final String archiveFileName,
          final String description,
          final boolean found,
          final String path,
          final byte pathType,
          final boolean pinned,
          final int stepRunID) {
    this.ID = ID;
    this.archiveFileName = archiveFileName;
    this.description = description;
    this.found = found;
    this.path = path;
    this.pathType = pathType;
    this.pinned = pinned;
    this.stepRunID = stepRunID;
  }


  /**
   * Gets the ID value for this StepResult.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this StepResult.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the archiveFileName value for this StepResult.
   *
   * @return archiveFileName
   */
  public String getArchiveFileName() {
    return archiveFileName;
  }


  /**
   * Sets the archiveFileName value for this StepResult.
   *
   * @param archiveFileName
   */
  public void setArchiveFileName(final String archiveFileName) {
    this.archiveFileName = archiveFileName;
  }


  /**
   * Gets the description value for this StepResult.
   *
   * @return description
   */
  public String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this StepResult.
   *
   * @param description
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Gets the found value for this StepResult.
   *
   * @return found
   */
  public boolean isFound() {
    return found;
  }


  /**
   * Sets the found value for this StepResult.
   *
   * @param found
   */
  public void setFound(final boolean found) {
    this.found = found;
  }


  /**
   * Gets the path value for this StepResult.
   *
   * @return path
   */
  public String getPath() {
    return path;
  }


  /**
   * Sets the path value for this StepResult.
   *
   * @param path
   */
  public void setPath(final String path) {
    this.path = path;
  }


  /**
   * Gets the pathType value for this StepResult.
   *
   * @return pathType
   */
  public byte getPathType() {
    return pathType;
  }


  /**
   * Sets the pathType value for this StepResult.
   *
   * @param pathType
   */
  public void setPathType(final byte pathType) {
    this.pathType = pathType;
  }


  /**
   * Gets the pinned value for this StepResult.
   *
   * @return pinned
   */
  public boolean isPinned() {
    return pinned;
  }


  /**
   * Sets the pinned value for this StepResult.
   *
   * @param pinned
   */
  public void setPinned(final boolean pinned) {
    this.pinned = pinned;
  }


  /**
   * Gets the stepRunID value for this StepResult.
   *
   * @return stepRunID
   */
  public int getStepRunID() {
    return stepRunID;
  }


  /**
   * Sets the stepRunID value for this StepResult.
   *
   * @param stepRunID
   */
  public void setStepRunID(final int stepRunID) {
    this.stepRunID = stepRunID;
  }


  public String[] getUrls() {
    return urls;
  }


  public void setUrls(final String[] urls) {
    this.urls = urls;
  }


  private Object __equalsCalc;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof StepResult)) return false;
    final StepResult other = (StepResult) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.ID &&
            ((this.archiveFileName == null && other.archiveFileName == null) ||
                    (this.archiveFileName != null &&
                            this.archiveFileName.equals(other.archiveFileName))) &&
            ((this.description == null && other.description == null) ||
                    (this.description != null &&
                            this.description.equals(other.description))) &&
            this.found == other.found &&
            ((this.path == null && other.path == null) ||
                    (this.path != null &&
                            this.path.equals(other.path))) &&
            this.pathType == other.pathType &&
            this.pinned == other.pinned &&
            this.stepRunID == other.stepRunID;
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
    if (archiveFileName != null) {
      _hashCode += archiveFileName.hashCode();
    }
    if (description != null) {
      _hashCode += description.hashCode();
    }
    _hashCode += (found ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (path != null) {
      _hashCode += path.hashCode();
    }
    _hashCode += pathType;
    _hashCode += (pinned ? Boolean.TRUE : Boolean.FALSE).hashCode();
    _hashCode += stepRunID;
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(StepResult.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "StepResult"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("IDAsString");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "IDAsString"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("archiveFileName");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "archiveFileName"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("description");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "description"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("found");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "found"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("path");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "path"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("pathType");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "pathType"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("pinned");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "pinned"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("stepRunID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "stepRunID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
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
