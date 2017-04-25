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
/**
 * BuildRun.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice.templ;

public class BuildRun implements java.io.Serializable {

  private int activeBuildID;
  private int buildID;
  private java.lang.String buildName;
  private int buildRunID;
  private int buildRunNumber;
  private java.lang.String changeListNumber;
  private byte complete;
  private byte dependence;
  private java.util.Calendar finishedAt;
  private java.lang.String label;
  private java.lang.String labelNote;
  private byte labelStatus;
  private java.lang.String lastStepRunName;
  private java.lang.String manualLabel;
  private boolean physicalChangeListNumber;
  private boolean reRun;
  private java.lang.String resultDescription;
  private byte resultID;
  private java.util.Calendar startedAt;
  private java.lang.String syncNote;
  private long timeStamp;
  private byte type;


  public BuildRun() {
  }


  public BuildRun(
          final int activeBuildID,
          final int buildID,
          final java.lang.String buildName,
          final java.lang.String buildNameAndNumberAsString,
          final int buildRunID,
          final java.lang.String buildRunIDAsString,
          final int buildRunNumber,
          final java.lang.String buildRunNumberAsString,
          final java.lang.String changeListNumber,
          final byte complete,
          final byte dependence,
          final java.util.Calendar finishedAt,
          final java.lang.String label,
          final java.lang.String labelNote,
          final byte labelStatus,
          final java.lang.String lastStepRunName,
          final java.lang.String manualLabel,
          final boolean physicalChangeListNumber,
          final boolean reRun,
          final java.lang.String resultDescription,
          final byte resultID,
          final java.util.Calendar startedAt,
          final java.lang.String syncNote,
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
  public java.lang.String getBuildName() {
    return buildName;
  }


  /**
   * Sets the buildName value for this BuildRun.
   *
   * @param buildName
   */
  public void setBuildName(final java.lang.String buildName) {
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
  public java.lang.String getChangeListNumber() {
    return changeListNumber;
  }


  /**
   * Sets the changeListNumber value for this BuildRun.
   *
   * @param changeListNumber
   */
  public void setChangeListNumber(final java.lang.String changeListNumber) {
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
  public java.util.Calendar getFinishedAt() {
    return finishedAt;
  }


  /**
   * Sets the finishedAt value for this BuildRun.
   *
   * @param finishedAt
   */
  public void setFinishedAt(final java.util.Calendar finishedAt) {
    this.finishedAt = finishedAt;
  }


  /**
   * Gets the label value for this BuildRun.
   *
   * @return label
   */
  public java.lang.String getLabel() {
    return label;
  }


  /**
   * Sets the label value for this BuildRun.
   *
   * @param label
   */
  public void setLabel(final java.lang.String label) {
    this.label = label;
  }


  /**
   * Gets the labelNote value for this BuildRun.
   *
   * @return labelNote
   */
  public java.lang.String getLabelNote() {
    return labelNote;
  }


  /**
   * Sets the labelNote value for this BuildRun.
   *
   * @param labelNote
   */
  public void setLabelNote(final java.lang.String labelNote) {
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
  public java.lang.String getLastStepRunName() {
    return lastStepRunName;
  }


  /**
   * Sets the lastStepRunName value for this BuildRun.
   *
   * @param lastStepRunName
   */
  public void setLastStepRunName(final java.lang.String lastStepRunName) {
    this.lastStepRunName = lastStepRunName;
  }


  /**
   * Gets the manualLabel value for this BuildRun.
   *
   * @return manualLabel
   */
  public java.lang.String getManualLabel() {
    return manualLabel;
  }


  /**
   * Sets the manualLabel value for this BuildRun.
   *
   * @param manualLabel
   */
  public void setManualLabel(final java.lang.String manualLabel) {
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
  public java.lang.String getResultDescription() {
    return resultDescription;
  }


  /**
   * Sets the resultDescription value for this BuildRun.
   *
   * @param resultDescription
   */
  public void setResultDescription(final java.lang.String resultDescription) {
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
  public java.util.Calendar getStartedAt() {
    return startedAt;
  }


  /**
   * Sets the startedAt value for this BuildRun.
   *
   * @param startedAt
   */
  public void setStartedAt(final java.util.Calendar startedAt) {
    this.startedAt = startedAt;
  }


  /**
   * Gets the syncNote value for this BuildRun.
   *
   * @return syncNote
   */
  public java.lang.String getSyncNote() {
    return syncNote;
  }


  /**
   * Sets the syncNote value for this BuildRun.
   *
   * @param syncNote
   */
  public void setSyncNote(final java.lang.String syncNote) {
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


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
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
            this.activeBuildID == other.getActiveBuildID() &&
            this.buildID == other.getBuildID() &&
            ((this.buildName == null && other.getBuildName() == null) ||
                    (this.buildName != null &&
                            this.buildName.equals(other.getBuildName()))) &&
            this.buildRunID == other.getBuildRunID() &&
            this.buildRunNumber == other.getBuildRunNumber() &&
            ((this.changeListNumber == null && other.getChangeListNumber() == null) ||
                    (this.changeListNumber != null &&
                            this.changeListNumber.equals(other.getChangeListNumber()))) &&
            this.complete == other.getComplete() &&
            this.dependence == other.getDependence() &&
            ((this.finishedAt == null && other.getFinishedAt() == null) ||
                    (this.finishedAt != null &&
                            this.finishedAt.equals(other.getFinishedAt()))) &&
            ((this.label == null && other.getLabel() == null) ||
                    (this.label != null &&
                            this.label.equals(other.getLabel()))) &&
            ((this.labelNote == null && other.getLabelNote() == null) ||
                    (this.labelNote != null &&
                            this.labelNote.equals(other.getLabelNote()))) &&
            this.labelStatus == other.getLabelStatus() &&
            ((this.lastStepRunName == null && other.getLastStepRunName() == null) ||
                    (this.lastStepRunName != null &&
                            this.lastStepRunName.equals(other.getLastStepRunName()))) &&
            ((this.manualLabel == null && other.getManualLabel() == null) ||
                    (this.manualLabel != null &&
                            this.manualLabel.equals(other.getManualLabel()))) &&
            this.physicalChangeListNumber == other.isPhysicalChangeListNumber() &&
            this.reRun == other.isReRun() &&
            ((this.resultDescription == null && other.getResultDescription() == null) ||
                    (this.resultDescription != null &&
                            this.resultDescription.equals(other.getResultDescription()))) &&
            this.resultID == other.getResultID() &&
            ((this.startedAt == null && other.getStartedAt() == null) ||
                    (this.startedAt != null &&
                            this.startedAt.equals(other.getStartedAt()))) &&
            ((this.syncNote == null && other.getSyncNote() == null) ||
                    (this.syncNote != null &&
                            this.syncNote.equals(other.getSyncNote()))) &&
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
    _hashCode += getActiveBuildID();
    _hashCode += getBuildID();
    if (getBuildName() != null) {
      _hashCode += getBuildName().hashCode();
    }
    if (getChangeListNumber() != null) {
      _hashCode += getChangeListNumber().hashCode();
    }
    _hashCode += getComplete();
    _hashCode += getDependence();
    if (getFinishedAt() != null) {
      _hashCode += getFinishedAt().hashCode();
    }
    if (getLabel() != null) {
      _hashCode += getLabel().hashCode();
    }
    if (getLabelNote() != null) {
      _hashCode += getLabelNote().hashCode();
    }
    _hashCode += getLabelStatus();
    if (getLastStepRunName() != null) {
      _hashCode += getLastStepRunName().hashCode();
    }
    if (getManualLabel() != null) {
      _hashCode += getManualLabel().hashCode();
    }
    _hashCode += (isPhysicalChangeListNumber() ? Boolean.TRUE : Boolean.FALSE).hashCode();
    _hashCode += (isReRun() ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (getResultDescription() != null) {
      _hashCode += getResultDescription().hashCode();
    }
    _hashCode += getResultID();
    if (getStartedAt() != null) {
      _hashCode += getStartedAt().hashCode();
    }
    if (getSyncNote() != null) {
      _hashCode += getSyncNote().hashCode();
    }
    _hashCode += new Long(getTimeStamp()).hashCode();
    _hashCode += getType();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(BuildRun.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "BuildRun"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("activeBuildID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "activeBuildID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildName");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildName"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildNameAndNumberAsString");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildNameAndNumberAsString"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildRunID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildRunID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildRunIDAsString");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildRunIDAsString"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildRunNumber");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildRunNumber"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildRunNumberAsString");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildRunNumberAsString"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("changeListNumber");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "changeListNumber"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("complete");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "complete"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("dependence");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "dependence"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("finishedAt");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "finishedAt"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("label");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "label"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("labelNote");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "labelNote"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("labelStatus");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "labelStatus"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("lastStepRunName");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "lastStepRunName"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("manualLabel");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "manualLabel"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("physicalChangeListNumber");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "physicalChangeListNumber"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("reRun");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "reRun"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("resultDescription");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "resultDescription"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("resultID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "resultID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("startedAt");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "startedAt"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("syncNote");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "syncNote"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
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
