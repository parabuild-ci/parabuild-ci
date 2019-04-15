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

public class BuildSequence implements java.io.Serializable {

  private static final long serialVersionUID = 3406308589212150164L;
  private int buildID;
  private boolean continueOnFailure;
  private boolean disabled;
  private java.lang.String failurePatterns;
  private boolean finalizer;
  private boolean initializer;
  private int lineNumber;
  private boolean respectErrorCode;
  private java.lang.String scriptText;
  private int sequenceID;
  private java.lang.String stepName;
  private java.lang.String successPatterns;
  private long timeStamp;
  private int timeoutMins;
  private byte type;


  public BuildSequence() {
  }


  public BuildSequence(
          final int buildID,
          final boolean continueOnFailure,
          final boolean disabled,
          final java.lang.String failurePatterns,
          final boolean finalizer,
          final boolean initializer,
          final int lineNumber,
          final boolean respectErrorCode,
          final java.lang.String scriptText,
          final int sequenceID,
          final java.lang.String stepName,
          final java.lang.String successPatterns,
          final long timeStamp,
          final int timeoutMins,
          final byte type) {
    this.buildID = buildID;
    this.continueOnFailure = continueOnFailure;
    this.disabled = disabled;
    this.failurePatterns = failurePatterns;
    this.finalizer = finalizer;
    this.initializer = initializer;
    this.lineNumber = lineNumber;
    this.respectErrorCode = respectErrorCode;
    this.scriptText = scriptText;
    this.sequenceID = sequenceID;
    this.stepName = stepName;
    this.successPatterns = successPatterns;
    this.timeStamp = timeStamp;
    this.timeoutMins = timeoutMins;
    this.type = type;
  }


  /**
   * Gets the buildID value for this BuildSequence.
   *
   * @return buildID
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Sets the buildID value for this BuildSequence.
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Gets the continueOnFailure value for this BuildSequence.
   *
   * @return continueOnFailure
   */
  public boolean isContinueOnFailure() {
    return continueOnFailure;
  }


  /**
   * Sets the continueOnFailure value for this BuildSequence.
   *
   * @param continueOnFailure
   */
  public void setContinueOnFailure(final boolean continueOnFailure) {
    this.continueOnFailure = continueOnFailure;
  }


  /**
   * Gets the disabled value for this BuildSequence.
   *
   * @return disabled
   */
  public boolean isDisabled() {
    return disabled;
  }


  /**
   * Sets the disabled value for this BuildSequence.
   *
   * @param disabled
   */
  public void setDisabled(final boolean disabled) {
    this.disabled = disabled;
  }


  /**
   * Gets the failurePatterns value for this BuildSequence.
   *
   * @return failurePatterns
   */
  public java.lang.String getFailurePatterns() {
    return failurePatterns;
  }


  /**
   * Sets the failurePatterns value for this BuildSequence.
   *
   * @param failurePatterns
   */
  public void setFailurePatterns(final java.lang.String failurePatterns) {
    this.failurePatterns = failurePatterns;
  }


  /**
   * Gets the finalizer value for this BuildSequence.
   *
   * @return finalizer
   */
  public boolean isFinalizer() {
    return finalizer;
  }


  /**
   * Sets the finalizer value for this BuildSequence.
   *
   * @param finalizer
   */
  public void setFinalizer(final boolean finalizer) {
    this.finalizer = finalizer;
  }


  /**
   * Gets the initializer value for this BuildSequence.
   *
   * @return initializer
   */
  public boolean isInitializer() {
    return initializer;
  }


  /**
   * Sets the initializer value for this BuildSequence.
   *
   * @param initializer
   */
  public void setInitializer(final boolean initializer) {
    this.initializer = initializer;
  }


  /**
   * Gets the lineNumber value for this BuildSequence.
   *
   * @return lineNumber
   */
  public int getLineNumber() {
    return lineNumber;
  }


  /**
   * Sets the lineNumber value for this BuildSequence.
   *
   * @param lineNumber
   */
  public void setLineNumber(final int lineNumber) {
    this.lineNumber = lineNumber;
  }


  /**
   * Gets the respectErrorCode value for this BuildSequence.
   *
   * @return respectErrorCode
   */
  public boolean isRespectErrorCode() {
    return respectErrorCode;
  }


  /**
   * Sets the respectErrorCode value for this BuildSequence.
   *
   * @param respectErrorCode
   */
  public void setRespectErrorCode(final boolean respectErrorCode) {
    this.respectErrorCode = respectErrorCode;
  }


  /**
   * Gets the scriptText value for this BuildSequence.
   *
   * @return scriptText
   */
  public java.lang.String getScriptText() {
    return scriptText;
  }


  /**
   * Sets the scriptText value for this BuildSequence.
   *
   * @param scriptText
   */
  public void setScriptText(final java.lang.String scriptText) {
    this.scriptText = scriptText;
  }


  /**
   * Gets the sequenceID value for this BuildSequence.
   *
   * @return sequenceID
   */
  public int getSequenceID() {
    return sequenceID;
  }


  /**
   * Sets the sequenceID value for this BuildSequence.
   *
   * @param sequenceID
   */
  public void setSequenceID(final int sequenceID) {
    this.sequenceID = sequenceID;
  }


  /**
   * Gets the stepName value for this BuildSequence.
   *
   * @return stepName
   */
  public java.lang.String getStepName() {
    return stepName;
  }


  /**
   * Sets the stepName value for this BuildSequence.
   *
   * @param stepName
   */
  public void setStepName(final java.lang.String stepName) {
    this.stepName = stepName;
  }


  /**
   * Gets the successPatterns value for this BuildSequence.
   *
   * @return successPatterns
   */
  public java.lang.String getSuccessPatterns() {
    return successPatterns;
  }


  /**
   * Sets the successPatterns value for this BuildSequence.
   *
   * @param successPatterns
   */
  public void setSuccessPatterns(final java.lang.String successPatterns) {
    this.successPatterns = successPatterns;
  }


  /**
   * Gets the timeStamp value for this BuildSequence.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this BuildSequence.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the timeoutMins value for this BuildSequence.
   *
   * @return timeoutMins
   */
  public int getTimeoutMins() {
    return timeoutMins;
  }


  /**
   * Sets the timeoutMins value for this BuildSequence.
   *
   * @param timeoutMins
   */
  public void setTimeoutMins(final int timeoutMins) {
    this.timeoutMins = timeoutMins;
  }


  /**
   * Gets the type value for this BuildSequence.
   *
   * @return type
   */
  public byte getType() {
    return type;
  }


  /**
   * Sets the type value for this BuildSequence.
   *
   * @param type
   */
  public void setType(final byte type) {
    this.type = type;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof BuildSequence)) return false;
    final BuildSequence other = (BuildSequence) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.buildID == other.buildID &&
            this.continueOnFailure == other.continueOnFailure &&
            this.disabled == other.disabled &&
            ((this.failurePatterns == null && other.failurePatterns == null) ||
                    (this.failurePatterns != null &&
                            this.failurePatterns.equals(other.failurePatterns))) &&
            this.finalizer == other.finalizer &&
            this.initializer == other.initializer &&
            this.lineNumber == other.lineNumber &&
            this.respectErrorCode == other.respectErrorCode &&
            ((this.scriptText == null && other.scriptText == null) ||
                    (this.scriptText != null &&
                            this.scriptText.equals(other.scriptText))) &&
            this.sequenceID == other.sequenceID &&
            ((this.stepName == null && other.stepName == null) ||
                    (this.stepName != null &&
                            this.stepName.equals(other.stepName))) &&
            ((this.successPatterns == null && other.successPatterns == null) ||
                    (this.successPatterns != null &&
                            this.successPatterns.equals(other.successPatterns))) &&
            this.timeStamp == other.timeStamp &&
            this.timeoutMins == other.timeoutMins &&
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
    _hashCode += buildID;
    _hashCode += (continueOnFailure ? Boolean.TRUE : Boolean.FALSE).hashCode();
    _hashCode += (disabled ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (failurePatterns != null) {
      _hashCode += failurePatterns.hashCode();
    }
    _hashCode += (finalizer ? Boolean.TRUE : Boolean.FALSE).hashCode();
    _hashCode += (initializer ? Boolean.TRUE : Boolean.FALSE).hashCode();
    _hashCode += lineNumber;
    _hashCode += (respectErrorCode ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (scriptText != null) {
      _hashCode += scriptText.hashCode();
    }
    _hashCode += sequenceID;
    if (stepName != null) {
      _hashCode += stepName.hashCode();
    }
    if (successPatterns != null) {
      _hashCode += successPatterns.hashCode();
    }
    _hashCode += new Long(timeStamp).hashCode();
    _hashCode += timeoutMins;
    _hashCode += type;
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(BuildSequence.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "BuildSequence"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("continueOnFailure");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "continueOnFailure"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("disabled");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "disabled"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("failurePatterns");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "failurePatterns"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("finalizer");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "finalizer"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("initializer");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "initializer"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("lineNumber");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "lineNumber"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("respectErrorCode");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "respectErrorCode"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("scriptText");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "scriptText"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("sequenceID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "sequenceID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("stepName");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "stepName"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("successPatterns");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "successPatterns"));
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
    elemField.setFieldName("timeoutMins");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "timeoutMins"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
