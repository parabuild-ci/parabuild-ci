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
import java.util.Calendar;

public class BuildRun implements Serializable {

  private static final long serialVersionUID = 2287075166088276601L;
  private int activeBuildID;
  private int buildID;
  private String buildName;
  private int buildRunID;
  private int buildRunNumber;
  private String changeListNumber;
  private byte complete;
  private byte dependence;
  private Calendar finishedAt;
  private String label;
  private String labelNote;
  private byte labelStatus;
  private String lastStepRunName;
  private String manualLabel;
  private boolean physicalChangeListNumber;
  private boolean reRun;
  private String resultDescription;
  private byte resultID;
  private Calendar startedAt;
  private String syncNote;
  private long timeStamp;
  private byte type;


  public BuildRun() {
  }


  public BuildRun(
          final int activeBuildID,
          final int buildID,
          final String buildName,
          final String buildNameAndNumberAsString,
          final int buildRunID,
          final String buildRunIDAsString,
          final int buildRunNumber,
          final String buildRunNumberAsString,
          final String changeListNumber,
          final byte complete,
          final byte dependence,
          final Calendar finishedAt,
          final String label,
          final String labelNote,
          final byte labelStatus,
          final String lastStepRunName,
          final String manualLabel,
          final boolean physicalChangeListNumber,
          final boolean reRun,
          final String resultDescription,
          final byte resultID,
          final Calendar startedAt,
          final String syncNote,
          final long timeStamp,
          final byte type) {
    this.activeBuildID = activeBuildID;
    this.buildID = buildID;
    this.buildName = buildName;
    this.buildRunID = buildRunID;
    this.buildRunNumber = buildRunNumber;
    this.changeListNumber = changeListNumber;
    this.complete = complete;
    this.dependence = dependence;
    this.finishedAt = finishedAt;
    this.label = label;
    this.labelNote = labelNote;
    this.labelStatus = labelStatus;
    this.lastStepRunName = lastStepRunName;
    this.manualLabel = manualLabel;
    this.physicalChangeListNumber = physicalChangeListNumber;
    this.reRun = reRun;
    this.resultDescription = resultDescription;
    this.resultID = resultID;
    this.startedAt = startedAt;
    this.syncNote = syncNote;
    this.timeStamp = timeStamp;
    this.type = type;
  }


  /**
   * Gets the activeBuildID value for this BuildRun.
   *
   * @return activeBuildID
   */
  public int getActiveBuildID() {
    return activeBuildID;
  }


  /**
   * Sets the activeBuildID value for this BuildRun.
   *
   * @param activeBuildID
   */
  public void setActiveBuildID(final int activeBuildID) {
    this.activeBuildID = activeBuildID;
  }


  /**
   * Gets the buildID value for this BuildRun.
   *
   * @return buildID
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Sets the buildID value for this BuildRun.
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Gets the buildName value for this BuildRun.
   *
   * @return buildName
   */
  public String getBuildName() {
    return buildName;
  }


  /**
   * Sets the buildName value for this BuildRun.
   *
   * @param buildName
   */
  public void setBuildName(final String buildName) {
    this.buildName = buildName;
  }


  /**
   * Gets the buildRunID value for this BuildRun.
   *
   * @return buildRunID
   */
  public int getBuildRunID() {
    return buildRunID;
  }


  /**
   * Sets the buildRunID value for this BuildRun.
   *
   * @param buildRunID
   */
  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


  /**
   * Gets the buildRunNumber value for this BuildRun.
   *
   * @return buildRunNumber
   */
  public int getBuildRunNumber() {
    return buildRunNumber;
  }


  /**
   * Sets the buildRunNumber value for this BuildRun.
   *
   * @param buildRunNumber
   */
  public void setBuildRunNumber(final int buildRunNumber) {
    this.buildRunNumber = buildRunNumber;
  }


  /**
   * Gets the changeListNumber value for this BuildRun.
   *
   * @return changeListNumber
   */
  public String getChangeListNumber() {
    return changeListNumber;
  }


  /**
   * Sets the changeListNumber value for this BuildRun.
   *
   * @param changeListNumber
   */
  public void setChangeListNumber(final String changeListNumber) {
    this.changeListNumber = changeListNumber;
  }


  /**
   * Gets the complete value for this BuildRun.
   *
   * @return complete
   */
  public byte getComplete() {
    return complete;
  }


  /**
   * Sets the complete value for this BuildRun.
   *
   * @param complete
   */
  public void setComplete(final byte complete) {
    this.complete = complete;
  }


  /**
   * Gets the dependence value for this BuildRun.
   *
   * @return dependence
   */
  public byte getDependence() {
    return dependence;
  }


  /**
   * Sets the dependence value for this BuildRun.
   *
   * @param dependence
   */
  public void setDependence(final byte dependence) {
    this.dependence = dependence;
  }


  /**
   * Gets the finishedAt value for this BuildRun.
   *
   * @return finishedAt
   */
  public Calendar getFinishedAt() {
    return finishedAt;
  }


  /**
   * Sets the finishedAt value for this BuildRun.
   *
   * @param finishedAt
   */
  public void setFinishedAt(final Calendar finishedAt) {
    this.finishedAt = finishedAt;
  }


  /**
   * Gets the label value for this BuildRun.
   *
   * @return label
   */
  public String getLabel() {
    return label;
  }


  /**
   * Sets the label value for this BuildRun.
   *
   * @param label
   */
  public void setLabel(final String label) {
    this.label = label;
  }


  /**
   * Gets the labelNote value for this BuildRun.
   *
   * @return labelNote
   */
  public String getLabelNote() {
    return labelNote;
  }


  /**
   * Sets the labelNote value for this BuildRun.
   *
   * @param labelNote
   */
  public void setLabelNote(final String labelNote) {
    this.labelNote = labelNote;
  }


  /**
   * Gets the labelStatus value for this BuildRun.
   *
   * @return labelStatus
   */
  public byte getLabelStatus() {
    return labelStatus;
  }


  /**
   * Sets the labelStatus value for this BuildRun.
   *
   * @param labelStatus
   */
  public void setLabelStatus(final byte labelStatus) {
    this.labelStatus = labelStatus;
  }


  /**
   * Gets the lastStepRunName value for this BuildRun.
   *
   * @return lastStepRunName
   */
  public String getLastStepRunName() {
    return lastStepRunName;
  }


  /**
   * Sets the lastStepRunName value for this BuildRun.
   *
   * @param lastStepRunName
   */
  public void setLastStepRunName(final String lastStepRunName) {
    this.lastStepRunName = lastStepRunName;
  }


  /**
   * Gets the manualLabel value for this BuildRun.
   *
   * @return manualLabel
   */
  public String getManualLabel() {
    return manualLabel;
  }


  /**
   * Sets the manualLabel value for this BuildRun.
   *
   * @param manualLabel
   */
  public void setManualLabel(final String manualLabel) {
    this.manualLabel = manualLabel;
  }


  /**
   * Gets the physicalChangeListNumber value for this BuildRun.
   *
   * @return physicalChangeListNumber
   */
  public boolean isPhysicalChangeListNumber() {
    return physicalChangeListNumber;
  }


  /**
   * Sets the physicalChangeListNumber value for this BuildRun.
   *
   * @param physicalChangeListNumber
   */
  public void setPhysicalChangeListNumber(final boolean physicalChangeListNumber) {
    this.physicalChangeListNumber = physicalChangeListNumber;
  }


  /**
   * Gets the reRun value for this BuildRun.
   *
   * @return reRun
   */
  public boolean isReRun() {
    return reRun;
  }


  /**
   * Sets the reRun value for this BuildRun.
   *
   * @param reRun
   */
  public void setReRun(final boolean reRun) {
    this.reRun = reRun;
  }


  /**
   * Gets the resultDescription value for this BuildRun.
   *
   * @return resultDescription
   */
  public String getResultDescription() {
    return resultDescription;
  }


  /**
   * Sets the resultDescription value for this BuildRun.
   *
   * @param resultDescription
   */
  public void setResultDescription(final String resultDescription) {
    this.resultDescription = resultDescription;
  }


  /**
   * Gets the resultID value for this BuildRun.
   *
   * @return resultID
   */
  public byte getResultID() {
    return resultID;
  }


  /**
   * Sets the resultID value for this BuildRun.
   *
   * @param resultID
   */
  public void setResultID(final byte resultID) {
    this.resultID = resultID;
  }


  /**
   * Gets the startedAt value for this BuildRun.
   *
   * @return startedAt
   */
  public Calendar getStartedAt() {
    return startedAt;
  }


  /**
   * Sets the startedAt value for this BuildRun.
   *
   * @param startedAt
   */
  public void setStartedAt(final Calendar startedAt) {
    this.startedAt = startedAt;
  }


  /**
   * Gets the syncNote value for this BuildRun.
   *
   * @return syncNote
   */
  public String getSyncNote() {
    return syncNote;
  }


  /**
   * Sets the syncNote value for this BuildRun.
   *
   * @param syncNote
   */
  public void setSyncNote(final String syncNote) {
    this.syncNote = syncNote;
  }


  /**
   * Gets the timeStamp value for this BuildRun.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this BuildRun.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the type value for this BuildRun.
   *
   * @return type
   */
  public byte getType() {
    return type;
  }


  /**
   * Sets the type value for this BuildRun.
   *
   * @param type
   */
  public void setType(final byte type) {
    this.type = type;
  }


  private Object __equalsCalc;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof BuildRun)) return false;
    final BuildRun other = (BuildRun) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.activeBuildID == other.activeBuildID &&
            this.buildID == other.buildID &&
            ((this.buildName == null && other.buildName == null) ||
                    (this.buildName != null &&
                            this.buildName.equals(other.buildName))) &&
            this.buildRunID == other.buildRunID &&
            this.buildRunNumber == other.buildRunNumber &&
            ((this.changeListNumber == null && other.changeListNumber == null) ||
                    (this.changeListNumber != null &&
                            this.changeListNumber.equals(other.changeListNumber))) &&
            this.complete == other.complete &&
            this.dependence == other.dependence &&
            ((this.finishedAt == null && other.finishedAt == null) ||
                    (this.finishedAt != null &&
                            this.finishedAt.equals(other.finishedAt))) &&
            ((this.label == null && other.label == null) ||
                    (this.label != null &&
                            this.label.equals(other.label))) &&
            ((this.labelNote == null && other.labelNote == null) ||
                    (this.labelNote != null &&
                            this.labelNote.equals(other.labelNote))) &&
            this.labelStatus == other.labelStatus &&
            ((this.lastStepRunName == null && other.lastStepRunName == null) ||
                    (this.lastStepRunName != null &&
                            this.lastStepRunName.equals(other.lastStepRunName))) &&
            ((this.manualLabel == null && other.manualLabel == null) ||
                    (this.manualLabel != null &&
                            this.manualLabel.equals(other.manualLabel))) &&
            this.physicalChangeListNumber == other.physicalChangeListNumber &&
            this.reRun == other.reRun &&
            ((this.resultDescription == null && other.resultDescription == null) ||
                    (this.resultDescription != null &&
                            this.resultDescription.equals(other.resultDescription))) &&
            this.resultID == other.resultID &&
            ((this.startedAt == null && other.startedAt == null) ||
                    (this.startedAt != null &&
                            this.startedAt.equals(other.startedAt))) &&
            ((this.syncNote == null && other.syncNote == null) ||
                    (this.syncNote != null &&
                            this.syncNote.equals(other.syncNote))) &&
            this.timeStamp == other.timeStamp &&
            this.type == other.type;
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
    _hashCode += activeBuildID;
    _hashCode += buildID;
    if (buildName != null) {
      _hashCode += buildName.hashCode();
    }
    if (changeListNumber != null) {
      _hashCode += changeListNumber.hashCode();
    }
    _hashCode += complete;
    _hashCode += dependence;
    if (finishedAt != null) {
      _hashCode += finishedAt.hashCode();
    }
    if (label != null) {
      _hashCode += label.hashCode();
    }
    if (labelNote != null) {
      _hashCode += labelNote.hashCode();
    }
    _hashCode += labelStatus;
    if (lastStepRunName != null) {
      _hashCode += lastStepRunName.hashCode();
    }
    if (manualLabel != null) {
      _hashCode += manualLabel.hashCode();
    }
    _hashCode += (physicalChangeListNumber ? Boolean.TRUE : Boolean.FALSE).hashCode();
    _hashCode += (reRun ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (resultDescription != null) {
      _hashCode += resultDescription.hashCode();
    }
    _hashCode += resultID;
    if (startedAt != null) {
      _hashCode += startedAt.hashCode();
    }
    if (syncNote != null) {
      _hashCode += syncNote.hashCode();
    }
    _hashCode += new Long(timeStamp).hashCode();
    _hashCode += type;
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(BuildRun.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "BuildRun"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("activeBuildID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "activeBuildID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "buildID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("buildName");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "buildName"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("buildNameAndNumberAsString");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "buildNameAndNumberAsString"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("buildRunID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "buildRunID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("buildRunIDAsString");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "buildRunIDAsString"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("buildRunNumber");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "buildRunNumber"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("buildRunNumberAsString");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "buildRunNumberAsString"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("changeListNumber");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "changeListNumber"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("complete");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "complete"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("dependence");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "dependence"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("finishedAt");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "finishedAt"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("label");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "label"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("labelNote");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "labelNote"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("labelStatus");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "labelStatus"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("lastStepRunName");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "lastStepRunName"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("manualLabel");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "manualLabel"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("physicalChangeListNumber");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "physicalChangeListNumber"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("reRun");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "reRun"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("resultDescription");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "resultDescription"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("resultID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "resultID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("startedAt");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "startedAt"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("syncNote");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "syncNote"));
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
