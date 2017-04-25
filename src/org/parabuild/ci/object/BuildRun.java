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

import org.parabuild.ci.common.StringUtils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Build run result
 *
 * @hibernate.class table="BUILD_RUN" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class BuildRun implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -4591276259024162394L; // NOPMD

  public static final Comparator BUILD_NAME_IGNORE_CASE = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      return ((BuildRun) o1).getBuildName().compareToIgnoreCase(((BuildRun) o2).getBuildName());
    }
  };

  // NOTE: vimeshev - DON'T CHANGE VALUES
  public static final byte BUILD_RESULT_UNKNOWN = 0;
  public static final byte BUILD_RESULT_SUCCESS = 1;
  public static final byte BUILD_RESULT_BROKEN = 2;
  public static final byte BUILD_RESULT_SYSTEM_ERROR = 3;
  public static final byte BUILD_RESULT_TIMEOUT = 4;
  public static final byte BUILD_RESULT_STOPPED = 5;

  public static final byte RUN_INCOMPLETE = 0;
  public static final byte RUN_COMPLETE = 1;

  public static final byte LABEL_NOT_SET = 0;
  public static final byte LABEL_SET = 1;
  public static final byte LABEL_DELETED = 2;

  /**
   * Standalone dependence type means that a build run doesn't have any
   * dependancies.
   */
  public static final byte DEPENDENCE_STANDALONE = 0;

  /**
   * Leader dependence means that a build is a leader and there are builds that
   * run in parallel (subordinates).
   */
  public static final byte DEPENDENCE_LEADER = 1;

  /**
   * Leader dependence means that a build is a subordinate and there is builds that
   * leads while running with this build in parallel.
   */
  public static final byte DEPENDENCE_SUBORDINATE = 2;

  public static final byte TYPE_BUILD_RUN = 0;
  public static final byte TYPE_VERIFICATION_RUN = 1;
  public static final byte TYPE_PUBLISHING_RUN = 3;

  private int buildID = BuildConfig.UNSAVED_ID;
  private int buildRunID = UNSAVED_ID;

  private boolean physicalChangeListNumber = true;
  private boolean reRun = false;
  private byte complete = RUN_INCOMPLETE;
  private byte dependence = DEPENDENCE_STANDALONE;
  private byte labelStatus = LABEL_NOT_SET;
  private byte resultID = BUILD_RESULT_UNKNOWN;
  private byte type = TYPE_BUILD_RUN;
  private Date finishedAt = null;
  private Date startedAt = null;
  private int activeBuildID = BuildConfig.UNSAVED_ID;
  private int buildRunNumber = 0;
  private long timeStamp = 0;
  private String buildName = "";
  private String changeListNumber = null;
  private String label = null;
  private String lastStepRunName = null;
  private String manualLabel = null;
  private String resultDescription = "";
  private String syncNote = "No information provided";


  public BuildRun() {
  }


  /**
   * Copy constructor.
   *
   * @param source
   */
  public BuildRun(final BuildRun source) {
    this.buildID = source.buildID;
    this.activeBuildID = source.activeBuildID;
    this.buildRunID = source.buildRunID;
    this.buildRunNumber = source.buildRunNumber;
    this.resultID = source.resultID;
    this.complete = source.complete;
    this.startedAt = source.startedAt != null ? new Date(source.startedAt.getTime()) : (Date) null;
    this.finishedAt = source.finishedAt != null ? new Date(source.finishedAt.getTime()) : (Date) null;
    this.timeStamp = source.timeStamp;
    this.resultDescription = source.resultDescription;
    this.label = source.label;
    this.buildName = source.buildName;
    this.syncNote = source.syncNote;
    this.changeListNumber = source.changeListNumber;
    this.lastStepRunName = source.lastStepRunName;
  }


  /**
   * Returns build ID
   *
   * @return int
   * @hibernate.property column="BUILD_ID" unique="false"
   * null="false"
   */
  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Returns active build ID
   *
   * @return int
   * @hibernate.property column="ACTIVE_BUILD_ID" unique="false"
   * null="false"
   */
  public int getActiveBuildID() {
    return activeBuildID;
  }


  public void setActiveBuildID(final int activeBuildID) {
    this.activeBuildID = activeBuildID;
  }


  /**
   * Returns build run ID
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID"
   * unsaved-value="-1"
   */
  public int getBuildRunID() {
    return buildRunID;
  }


  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


  public String getBuildRunIDAsString() {
    return Integer.toString(buildRunID);
  }


  /**
   * Returns build run number
   *
   * @return int
   * @hibernate.property column="NUMBER" unique="false"
   * null="false"
   */
  public int getBuildRunNumber() {
    return buildRunNumber;
  }


  public void setBuildRunNumber(final int buildRunNumber) {
    this.buildRunNumber = buildRunNumber;
  }


  /**
   * Returns build run number as String.
   */
  public String getBuildRunNumberAsString() {
    return Integer.toString(buildRunNumber);
  }


  /**
   * Returns run result ID
   *
   * @return int
   * @hibernate.property column="RESULT" unique="false"
   * null="true"
   */
  public byte getResultID() {
    return resultID;
  }


  public void setResultID(final byte resultID) {
    this.resultID = resultID;
  }


  /**
   * Returns completion state
   *
   * @return int
   * @hibernate.property column="RESULT_DESCRIPTION"
   * unique="false" null="true"
   */
  public String getResultDescription() {
    return resultDescription;
  }


  public void setResultDescription(final String resultDescripting) {
    this.resultDescription = resultDescripting;
  }


  /**
   * Returns completion state
   *
   * @hibernate.property column="COMPLETE" unique="false"
   * null="false"
   */
  public byte getComplete() {
    return complete;
  }


  public void setComplete(final byte complete) {
    this.complete = complete;
  }


  /**
   * Returns finish date
   *
   * @return Date
   * @hibernate.property column="FINISHED_AT" unique="false"
   * null="true"
   */
  public Date getFinishedAt() {
    return finishedAt;
  }


  public void setFinishedAt(final Date finishedAt) {
    this.finishedAt = finishedAt;
  }


  /**
   * Returns start date
   *
   * @return Date
   * @hibernate.property column="STARTED_AT" unique="false"
   * null="true"
   */
  public Date getStartedAt() {
    return startedAt;
  }


  public void setStartedAt(final Date startedAt) {
    this.startedAt = startedAt;
  }


  /**
   * @hibernate.property column="BUILD_NAME" unique="false"
   * null="false"
   */
  public String getBuildName() {
    return buildName;
  }


  public void setBuildName(final String buildName) {
    this.buildName = buildName;
  }


  /**
   * @hibernate.property column="SYNC_NOTE" unique="false"
   * null="false"
   */
  public String getSyncNote() {
    return syncNote;
  }


  public void setSyncNote(final String syncNote) {
    this.syncNote = syncNote;
  }


  /**
   * Returns label assigned to the build run
   *
   * @return Date
   * @hibernate.property column="LABEL" unique="false"
   * null="true"
   */
  public String getLabel() {
    return label;
  }


  public void setLabel(final String label) {
    this.label = label;
  }


  /**
   * Returns change list number this build run built.
   *
   * @return String
   * @hibernate.property column="CHANGELIST_NUM" unique="false"
   * null="false"
   */
  public String getChangeListNumber() {
    return changeListNumber;
  }


  public void setChangeListNumber(final String changeListNumber) {
    this.changeListNumber = changeListNumber;
  }


  /**
   * Returns name of the last step run in this build run. It can
   * be used to show name of the broken step.
   *
   * @return String
   * @hibernate.property column="LAST_STEP_NAME" unique="false"
   * null="false"
   */
  public String getLastStepRunName() {
    return lastStepRunName;
  }


  public void setLastStepRunName(final String lastStepRunName) {
    this.lastStepRunName = lastStepRunName;
  }


  /**
   * Returns timestamp
   *
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Returns build/sequence result as string.
   *
   * @param resultID -
   */
  public static String buildResultToString(final int resultID) {
    switch (resultID) {
      case BUILD_RESULT_SUCCESS:
        return "Successful";
      case BUILD_RESULT_BROKEN:
        return "BROKEN";
      case BUILD_RESULT_TIMEOUT:
        return "TIMED OUT";
      case BUILD_RESULT_UNKNOWN:
        return "UNKNOWN";
      case BUILD_RESULT_STOPPED:
        return "STOPPED";
      case BUILD_RESULT_SYSTEM_ERROR:
        return "SYSTEM ERROR";
      default:
        return "UNKNOWN, code " + resultID;
    }
  }


  /**
   * Returns build/sequence result as string.
   *
   * @param resultID -
   */
  public static String buildResultToShellString(final int resultID) {
    switch (resultID) {
      case BUILD_RESULT_SUCCESS:
        return "SUCCESSFUL";
      case BUILD_RESULT_BROKEN:
        return "BROKEN";
      case BUILD_RESULT_TIMEOUT:
        return "TIMED_OUT";
      case BUILD_RESULT_UNKNOWN:
        return "UNKNOWN";
      case BUILD_RESULT_STOPPED:
        return "STOPPED";
      case BUILD_RESULT_SYSTEM_ERROR:
        return "SYSTEM_ERROR";
      default:
        return "UNKNOWN";
    }
  }


  /**
   * Returns build/sequence result as string.
   *
   * @param resultID -
   */
  public static String buildResultToVerbialString(final byte resultID) {
    switch (resultID) {
      case BUILD_RESULT_SUCCESS:
        return "was SUCCESSFUL";
      case BUILD_RESULT_BROKEN:
        return "was BROKEN";
      case BUILD_RESULT_TIMEOUT:
        return "TIMED OUT";
      case BUILD_RESULT_UNKNOWN:
        return "ended with UNKNOWN result";
      case BUILD_RESULT_STOPPED:
        return "was STOPPED by administrator";
      case BUILD_RESULT_SYSTEM_ERROR:
        return "FAILED due to system error";
      default:
        return "UNKNOWN, code " + resultID;
    }
  }


  /**
   * Returns build/sequence result as string.
   */
  public String buildResultToString() {
    return buildResultToString(resultID);
  }


  /**
   * @return true if this build run is complete (fully
   *         finished).
   */
  public boolean completed() {
    return complete == RUN_COMPLETE && finishedAt != null;
  }


  /**
   * @return true if this build run is successful.
   */
  public boolean successful() {
    return resultID == BUILD_RESULT_SUCCESS;
  }


  /**
   * Returns label status.
   *
   * @return int
   * @hibernate.property column="LABEL_STATUS" unique="false"
   * null="false"
   * @see #LABEL_NOT_SET
   * @see #LABEL_SET
   * @see #LABEL_DELETED
   */
  public byte getLabelStatus() {
    return labelStatus;
  }


  /**
   * Returns manual labels that may be applied when manual build run is requested.
   *
   * @return Date
   * @hibernate.property column="MANUAL_LABEL" unique="false"
   * null="true"
   */
  public String getManualLabel() {
    return manualLabel;
  }


  public void setManualLabel(final String manualLabel) {
    this.manualLabel = manualLabel;
  }


  /**
   * Returns true if this build was a re-run.
   *
   * @return true if this build was a re-run.
   * @hibernate.property column="RERUN"  type="yes_no"
   * unique="false" null="false"
   * @see BuildRunAttribute#ATTR_RE_RUN_BUILD_RUN_ID
   */
  public boolean isReRun() {
    return reRun;
  }


  public void setReRun(final boolean reRun) {
    this.reRun = reRun;
  }


  /**
   * Sets label status.
   *
   * @see #LABEL_NOT_SET
   * @see #LABEL_SET
   * @see #LABEL_DELETED
   */
  public void setLabelStatus(final byte labelStatus) {
    this.labelStatus = labelStatus;
  }


  /**
   * @return Human-readable label description, including
   *         sutuations when a build run was not labeled or label
   *         is deleted.
   */
  public String getLabelNote() {
    String result = null;
    switch (labelStatus) {
      case LABEL_NOT_SET:
        result = "";
        break;
      case LABEL_SET:
        result = label;
        break;
      case LABEL_DELETED:
        result = label + " (deleted)";
        break;
      default:
        if (StringUtils.isBlank(label)) {
          result = "";
        } else {
          result = label;
        }
        break;
    }
    return result;
  }


  public boolean isPhysicalChangeListNumber() {
    return physicalChangeListNumber;
  }


  public void setPhysicalChangeListNumber(final boolean physicalChangeListNumber) {
    this.physicalChangeListNumber = physicalChangeListNumber;
  }


  /**
   * Returns dependence type for this build run.
   *
   * @hibernate.property column="DEPENDENCE" unique="false"
   * null="false"
   */
  public byte getDependence() {
    return dependence;
  }


  public void setDependence(final byte dependence) {
    this.dependence = dependence;
  }


  /**
   * Returns type for this build run.
   *
   * @hibernate.property column="TYPE" unique="false"
   * null="false"
   */
  public byte getType() {
    return type;
  }


  public void setType(final byte type) {
    this.type = type;
  }


  /**
   * Sets build run result taking in account current state of the
   * object.
   * <p/>
   * If the result ID was set to anything by success, the attempt
   * to change it will be ignore and the result id and
   * discription will remain unchanged.
   *
   * @param resultID
   * @param resultDescription
   */
  public void setResult(final byte resultID, final String resultDescription) {
    setResult(true, resultID, resultDescription);
  }


  /**
   * Sets build run result taking in account current state of the
   * object.
   * <p/>
   * If the result ID was set to anything by success, the attempt
   * to change it will be ignore and the result id and
   * discription will remain unchanged.
   *
   * @param resultID
   * @param resultDescription
   */
  public void setResult(final boolean respectPreviousResult, final byte resultID, final String resultDescription) {

    // disallow if in non-settable state
    if (respectPreviousResult) {
      if (!(this.resultID == BUILD_RESULT_SUCCESS
              || this.resultID == BUILD_RESULT_UNKNOWN)) {
        return;
      }
    }

    // set values
    setResultID(resultID);
    setResultDescription(resultDescription);
  }


  /**
   * Creates a copy of the given build run.
   *
   * @param buildRun
   * @return new build run or null if the build to copy is null.
   */
  public static BuildRun copy(final BuildRun buildRun) {
    if (buildRun == null) return null;
    return new BuildRun(buildRun);
  }


  public boolean isSuccessful() {
    return resultID == BUILD_RESULT_SUCCESS;
  }


  public boolean isBroken() {
    return resultID == BUILD_RESULT_BROKEN;
  }


  /**
   * Returns <code>true</code> if the build run was stopped.
   *
   * @return <code>true</code> if the build run was stopped.
   */
  public boolean isStopped() {
    return resultID == BUILD_RESULT_STOPPED;
  }


  /**
   * @return build name and number as string.
   */
  public String getBuildNameAndNumberAsString() {
    return buildName + " # " + getBuildRunNumberAsString();
  }


  public String toString() {
    return "BuildRun{" +
            "buildID=" + buildID +
            ", buildRunID=" + buildRunID +
            ", physicalChangeListNumber=" + physicalChangeListNumber +
            ", reRun=" + reRun +
            ", complete=" + complete +
            ", dependence=" + dependence +
            ", labelStatus=" + labelStatus +
            ", resultID=" + resultID +
            ", type=" + type +
            ", finishedAt=" + finishedAt +
            ", startedAt=" + startedAt +
            ", activeBuildID=" + activeBuildID +
            ", buildRunNumber=" + buildRunNumber +
            ", timeStamp=" + timeStamp +
            ", buildName='" + buildName + '\'' +
            ", changeListNumber='" + changeListNumber + '\'' +
            ", label='" + label + '\'' +
            ", lastStepRunName='" + lastStepRunName + '\'' +
            ", manualLabel='" + manualLabel + '\'' +
            ", resultDescription='" + resultDescription + '\'' +
            ", syncNote='" + syncNote + '\'' +
            '}';
  }

}
