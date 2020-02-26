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
package org.parabuild.ci.object;

import org.parabuild.ci.util.StringUtils;

import java.io.Serializable;

/**
 * Version control system user to e-mail mapping
 *
 * @hibernate.class table="BUILD_SEQUENCE" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 * @noinspection StaticInheritance
 */
public final class BuildSequence implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -8936184017787430177L; // NOPMD

  private boolean continueOnFailure = false;
  private boolean disabled = false;
  private boolean finalizer = false;
  private boolean initializer = false;
  private boolean respectErrorCode = false;
  private byte type = BuildStepType.BUILD.byteValue();
  private int buildID = BuildConfig.UNSAVED_ID;
  private int lineNumber = 0;
  private int sequenceID = UNSAVED_ID;
  private int timeoutMins = 5;
  private long timeStamp = 1L;
  private String failurePatterns = "";
  private String scriptText = null;
  private String stepName = null;
  private String successPatterns = "";


  /**
   * Default constructor
   */
  public BuildSequence() {
  }


  /**
   * Copy constructor.
   */
  public BuildSequence(final BuildSequence source) {
    this.buildID = source.buildID;
    this.lineNumber = source.lineNumber;
    this.respectErrorCode = source.respectErrorCode;
    this.scriptText = source.scriptText;
    this.sequenceID = source.sequenceID;
    this.stepName = source.stepName;
    this.successPatterns = source.successPatterns;
    this.timeoutMins = source.timeoutMins;
    this.timeStamp = source.timeStamp;
    this.type = source.type;
  }


  /**
   * Returns build ID
   *
   * @return String
   * @hibernate.property column="BUILD_ID" unique="false" null="false"
   */
  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Sequence ID
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID" unsaved-value="-1"
   */
  public int getSequenceID() {
    return sequenceID;
  }


  public void setSequenceID(final int sequenceID) {
    this.sequenceID = sequenceID;
  }


  /**
   * Returns sequence name
   *
   * @return String
   * @hibernate.property column="NAME" unique="true" null="false"
   */
  public String getStepName() {
    return stepName;
  }


  public void setStepName(final String stepName) {
    this.stepName = stepName;
  }


  /**
   * Returns text of sequence script
   *
   * @return String
   * @hibernate.property column="SCRIPT" unique="false" null="false"
   */
  public String getScriptText() {
    return scriptText;
  }


  public void setScriptText(final String scriptText) {
    this.scriptText = scriptText;
  }


  /**
   * Returns sequence's success pattern
   *
   * @return String
   * @hibernate.property column="SUCCESS_PATTERNS" unique="false" null="false"
   */
  public String getSuccessPatterns() {
    return successPatterns;
  }


  public void setSuccessPatterns(final String successPatterns) {
    this.successPatterns = successPatterns;
  }


  /**
   * Returns sequence's failure pattern
   *
   * @return String
   * @hibernate.property column="FAILURE_PATTERNS" unique="false" null="false"
   */
  public String getFailurePatterns() {
    return failurePatterns;
  }


  public void setFailurePatterns(final String failurePatterns) {
    this.failurePatterns = failurePatterns;
  }


  /**
   * @return true if failure pattern(s) is empty
   */
  public boolean failurePatternIsEmpty() {
    return StringUtils.patternIsEmpty(failurePatterns);
  }


  /**
   * @return true if success pattern(s) is empty
   */
  public boolean successPatternIsEmpty() {
    return StringUtils.patternIsEmpty(successPatterns);
  }


  /**
   * Returns true if error code should be respected.
   *
   * @hibernate.property column="RESPECT_ERROR_CODE"
   * type="yes_no" unique="false" null="false"
   */
  public boolean getRespectErrorCode() {
    return respectErrorCode;
  }


  public void setRespectErrorCode(final boolean respectErrorCode) {
    this.respectErrorCode = respectErrorCode;
  }


  /**
   * Returns this sequence timeout
   *
   * @return int
   * @hibernate.property column="TIMEOUT"  null="false"
   */
  public int getTimeoutMins() {
    return timeoutMins;
  }


  public void setTimeoutMins(final int timeoutMins) {
    this.timeoutMins = timeoutMins;
  }


  /**
   * Returns true the given build step is disabled
   *
   * @return true the given build step is disabled
   * @hibernate.property column="DISABLED"  type="yes_no" unique="false" null="false"
   */
  public boolean isDisabled() {
    return disabled;
  }


  public void setDisabled(final boolean disabled) {
    this.disabled = disabled;
  }


  public boolean isEnabled() {
    return !disabled;
  }


  /**
   * Returns true the given build step is disabled
   *
   * @return true the given build step is disabled
   * @hibernate.property column="CONTINUE"  type="yes_no" unique="false" null="false"
   */
  public boolean isContinueOnFailure() {
    return continueOnFailure;
  }


  public void setContinueOnFailure(final boolean continueOnFailure) {
    this.continueOnFailure = continueOnFailure;
  }


  /**
   * Returns true the given build step is a build finalizer (step that runs even
   * if some ot all previous steps failed.
   *
   * @return true the given build step is disabled
   * @hibernate.property column="FINALIZER"  type="yes_no" unique="false" null="false"
   */
  public boolean isFinalizer() {
    return finalizer;
  }


  public void setFinalizer(final boolean finalizer) {
    this.finalizer = finalizer;
  }


  /**
   * Returns true the given build step is a build initializer
   * step that runs before starting dependent builds.
   *
   * @return true the given build step is disabled
   * @hibernate.property column="INITIALIZER"  type="yes_no" unique="false" null="false"
   */
  public boolean isInitializer() {
    return initializer;
  }


  public void setInitializer(final boolean initializer) {
    this.initializer = initializer;
  }


  /**
   * Returns line number. Build steps are sorted by line number.
   *
   * @hibernate.property column="LINE_NUMBER" unique="false" null="false"
   */
  public int getLineNumber() {
    return lineNumber;
  }


  public void setLineNumber(final int lineNumber) {
    this.lineNumber = lineNumber;
  }


  /**
   * Returns timestamp
   *
   * @return long
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Returns type for this build step
   *
   * @hibernate.property column="TYPE" unique="false" null="false"
   * @see BuildStepType#BUILD
   * @see BuildStepType#PUBLISH
   */
  public byte getType() {
    return type;
  }


  public void setType(final byte type) {
    this.type = type;
  }


  public String toString() {
    return "BuildSequence{" +
            "buildID=" + buildID +
            ", sequenceID=" + sequenceID +
            ", lineNumber=" + lineNumber +
            ", timeoutMins=" + timeoutMins +
            ", timeStamp=" + timeStamp +
            ", failurePatterns='" + failurePatterns + '\'' +
            ", scriptText='" + scriptText + '\'' +
            ", stepName='" + stepName + '\'' +
            ", successPatterns='" + successPatterns + '\'' +
            ", respectErrorCode=" + respectErrorCode +
            ", disabled=" + disabled +
            ", continueOnFailure=" + continueOnFailure +
            ", finalizer=" + finalizer +
            ", initializer=" + initializer +
            ", type=" + type +
            '}';
  }
}
