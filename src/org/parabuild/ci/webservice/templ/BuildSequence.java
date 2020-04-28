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

public class BuildSequence implements Serializable {

  private static final long serialVersionUID = 3406308589212150164L;
  private int buildID;
  private boolean continueOnFailure;
  private boolean disabled;
  private String failurePatterns;
  private boolean finalizer;
  private boolean initializer;
  private int lineNumber;
  private boolean respectErrorCode;
  private String scriptText;
  private int sequenceID;
  private String stepName;
  private String successPatterns;
  private long timeStamp;
  private int timeoutMins;
  private byte type;


  public BuildSequence() {
  }


  public BuildSequence(
          final int buildID,
          final boolean continueOnFailure,
          final boolean disabled,
          final String failurePatterns,
          final boolean finalizer,
          final boolean initializer,
          final int lineNumber,
          final boolean respectErrorCode,
          final String scriptText,
          final int sequenceID,
          final String stepName,
          final String successPatterns,
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
  public String getFailurePatterns() {
    return failurePatterns;
  }


  /**
   * Sets the failurePatterns value for this BuildSequence.
   *
   * @param failurePatterns
   */
  public void setFailurePatterns(final String failurePatterns) {
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
  public String getScriptText() {
    return scriptText;
  }


  /**
   * Sets the scriptText value for this BuildSequence.
   *
   * @param scriptText
   */
  public void setScriptText(final String scriptText) {
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
  public String getStepName() {
    return stepName;
  }


  /**
   * Sets the stepName value for this BuildSequence.
   *
   * @param stepName
   */
  public void setStepName(final String stepName) {
    this.stepName = stepName;
  }


  /**
   * Gets the successPatterns value for this BuildSequence.
   *
   * @return successPatterns
   */
  public String getSuccessPatterns() {
    return successPatterns;
  }


  /**
   * Sets the successPatterns value for this BuildSequence.
   *
   * @param successPatterns
   */
  public void setSuccessPatterns(final String successPatterns) {
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


  private Object __equalsCalc;


  public synchronized boolean equals(final Object obj) {
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


  private boolean __hashCodeCalc;


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
  private static final TypeDesc typeDesc =
          new TypeDesc(BuildSequence.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "BuildSequence"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("buildID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "buildID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("continueOnFailure");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "continueOnFailure"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("disabled");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "disabled"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("failurePatterns");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "failurePatterns"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("finalizer");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "finalizer"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("initializer");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "initializer"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("lineNumber");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "lineNumber"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("respectErrorCode");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "respectErrorCode"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("scriptText");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "scriptText"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("sequenceID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "sequenceID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("stepName");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "stepName"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("successPatterns");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "successPatterns"));
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
    elemField.setFieldName("timeoutMins");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "timeoutMins"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
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
