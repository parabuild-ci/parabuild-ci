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

import java.io.*;

/**
 * Stored merge configuration
 *
 * @hibernate.class table="MERGE_CONFIGURATION" dynamic-update="true"
 * polymorphism="explicit"
 * @hibernate.discriminator column="DISCRIMINATOR" type="string"
 * @hibernate.cache usage="read-write"
 */
public abstract class MergeConfiguration implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -4202050502366353403L;

  /**
   * This merge mode means that this merge will merge changes.
   */
  public static final byte MERGE_MODE_MERGE = 0;

  /**
   * This merge mode means that this merge will send a nag
   * to the owner of the merge instead of merging.
   */
  public static final byte MERGE_MODE_NAG = 1;

  /**
   * This conflict resolution mode means that in case of a
   * conflict Parabuild will send a nag.
   */
  public static final byte CONFLICT_RESOLUTION_MODE_NAG = 0;

  /**
   * This conflict resolution mode means that in case of a
   * conflict Parabuild will use the content of chages to be merged.
   */
  public static final byte CONFLICT_RESOLUTION_MODE_ACCEPT_YOURS = 1;

  /**
   * This conflict resolution mode means that in case of a
   * conflict Parabuild will use the content the merge target.
   */
  public static final byte CONFLICT_RESOLUTION_MODE_ACCEPT_THEIRS = 2;

  /**
   * Branch view is retrieved from a named branch spec.
   */
  public static final byte BRANCH_VIEW_SOURCE_BRANCH_NAME = 0;

  /**
   * Branch view is retrieved from a directly stored branch view.
   */
  public static final byte BRANCH_VIEW_SOURCE_DIRECT = 1;


  private boolean indirectMerge = true;
  private boolean preserveMarker = false;
  private boolean reverseBranchView = true;
  private byte branchViewSource = BRANCH_VIEW_SOURCE_BRANCH_NAME;
  private byte conflictResolutionMode = CONFLICT_RESOLUTION_MODE_NAG;
  private byte mergeMode = MERGE_MODE_MERGE;
  private int activeMergeID = UNSAVED_ID;
  private int ID = UNSAVED_ID;
  private int sourceBuildID = ActiveBuild.UNSAVED_ID;
  private int targetBuildID = ActiveBuild.UNSAVED_ID;
  private long timeStamp = 1;
  private String branchView = "";
  private String branchViewName = "";
  private String description = null;
  private String marker = null;
  private String name = null;


  /**
   * The getter method for this build ID
   *
   * @return int
   *
   * @hibernate.id generator-class="identity" column="ID"
   * unsaved-value="-1"
   */
  public int getID() {
    return ID;
  }


  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Returns originating build ID - that is, a build ID that was
   * used to cread this build config.
   * <p/>
   * Active builds have this value equal buildID.
   *
   * @return int
   *
   * @hibernate.property column="ACTIVE_MERGE_CONFIGURATION_ID" unique="false"
   * null="false"
   */
  public int getActiveMergeID() {
    return activeMergeID;
  }


  public void setActiveMergeID(final int activeMergeID) {
    this.activeMergeID = activeMergeID;
  }


  /**
   * @hibernate.property column="SOURCE_BUILD_ID" unique="false"
   *  null="false"
   */
  public int getSourceBuildID() {
    return sourceBuildID;
  }


  public void setSourceBuildID(final int sourceBuildID) {
    this.sourceBuildID = sourceBuildID;
  }


  /**
   * @hibernate.property column="TARGET_BUILD_ID" unique="false"
   *  null="false"
   */
  public int getTargetBuildID() {
    return targetBuildID;
  }


  public void setTargetBuildID(final int targetBuildID) {
    this.targetBuildID = targetBuildID;
  }


  /**
   * Returns build name
   *
   * @return String
   *
   * @hibernate.property column="NAME" unique="true"
   *  null="false"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @hibernate.property column="DESCRIPTION" unique="false"
   *  null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @hibernate.property column="MARKER" unique="false"
   *  null="false"
   */
  public String getMarker() {
    return marker;
  }


  public void setMarker(final String marker) {
    this.marker = marker;
  }


  /**
   * @hibernate.property column="MERGE_MODE" unique="false"
   *  null="false"
   */
  public byte getMergeMode() {
    return mergeMode;
  }


  public void setMergeMode(final byte mergeMode) {
    this.mergeMode = mergeMode;
  }


  /**
   * @hibernate.property column="CONFLICT_RESOLUTION_MODE"
   *  unique="false" null="false"
   */
  public byte getConflictResolutionMode() {
    return conflictResolutionMode;
  }


  public void setConflictResolutionMode(final byte conflictResolutionMode) {
    this.conflictResolutionMode = conflictResolutionMode;
  }


  /**
   * @return code that defines where merge finds branch vew.
   *
   * @see #BRANCH_VIEW_SOURCE_BRANCH_NAME
   * @see #BRANCH_VIEW_SOURCE_DIRECT
   *
   * @hibernate.property column="BRANCH_VIEW_SOURCE"
   *  unique="false" null="false"
   */
  public byte getBranchViewSource() {
    return branchViewSource;
  }


  public void setBranchViewSource(final byte branchViewSource) {
    this.branchViewSource = branchViewSource;
  }


  /**
   * @return names branch view name stored in VCS such as
   *  one defined by p4 branch.
   *
   * @hibernate.property column="BRANCH_VIEW_NAME"
   *  unique="false" null="false"
   */
  public String getBranchViewName() {
    return branchViewName;
  }


  public void setBranchViewName(final String branchViewName) {
    this.branchViewName = branchViewName;
  }


  /**
   * @return directly stored branch view.
   *
   * @hibernate.property column="BRANCH_VIEW"
   *  unique="false" null="false"
   */
  public String getBranchView() {
    return branchView;
  }


  public void setBranchView(final String branchView) {
    this.branchView = branchView;
  }


  /**
   * @hibernate.property column="REVERSE_BRANCH_VIEW" type="yes_no"
   * unique="false" null="false"
   */
  public boolean isReverseBranchView() {
    return reverseBranchView;
  }


  public void setReverseBranchView(final boolean reverseBranchView) {
    this.reverseBranchView = reverseBranchView;
  }


  /**
   * @hibernate.property column="INDIRECT_MERGE" type="yes_no"
   * unique="false" null="false"
   */
  public boolean isIndirectMerge() {
    return indirectMerge;
  }


  public void setIndirectMerge(final boolean indirectMerge) {
    this.indirectMerge = indirectMerge;
  }


  /**
   * @hibernate.property column="PRESERVE_MARKER" type="yes_no"
   * unique="false" null="false"
   */
  public boolean isPreserveMarker() {
    return preserveMarker;
  }


  public void setPreserveMarker(final boolean preserveMarker) {
    this.preserveMarker = preserveMarker;
  }


  /**
   * Returns timestamp
   *
   * @return long
   *
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  public String toString() {
    return "MergeConfiguration{" +
      "indirectMerge=" + indirectMerge +
      ", preserveMarker=" + preserveMarker +
      ", reverseBranchView=" + reverseBranchView +
      ", branchViewSource=" + branchViewSource +
      ", conflictResolutionMode=" + conflictResolutionMode +
      ", mergeMode=" + mergeMode +
      ", activeMergeID=" + activeMergeID +
      ", ID=" + ID +
      ", sourceBuildID=" + sourceBuildID +
      ", targetBuildID=" + targetBuildID +
      ", timeStamp=" + timeStamp +
      ", branchView='" + branchView + '\'' +
      ", branchViewName='" + branchViewName + '\'' +
      ", description='" + description + '\'' +
      ", marker='" + marker + '\'' +
      ", name='" + name + '\'' +
      '}';
  }
}
