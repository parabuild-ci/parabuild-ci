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

import java.io.Serializable;
import java.util.Arrays;

/**
 * Request to start a build.
 *
 * @noinspection UnusedDeclaration,AssignmentToCollectionOrArrayFieldFromParameter,ReturnOfCollectionOrArrayField
 */
public final class BuildStartRequest implements Serializable {

  private static final long serialVersionUID = 0L;

  public static final byte REQUEST_NORMAL = 0;
  public static final byte REQUEST_RERUN = 1;
  public static final byte REQUEST_PARALLEL = 2;
  public static final byte REQUEST_VERIFICATION = 3;


  /**
   * Chained request is sent by an upstream build on a
   * successful build run competition.
   */
  public static final byte REQUEST_CHAINED = 4;

  private BuildStartRequestParameter[] parameterList = null;
  private boolean cleanCheckout = false;
  private boolean pinResult = false;
  private int requestType;
  private int buildRunID;
  private int changeListID;
  private int userID;
  private int versionCounter;
  private String label = null;
  private String note = null;
  private final String versionTemplate = null;

  /**
   * List of {@link SourceControlSettingOverride} objects.
   */
  private SourceControlSettingOverride[] sourceControlSettingsOverrides = null;

  /**
   * Agent host.
   */
  private AgentHost agentHost = null;

  /**
   * Ignore serialization.
   */
  private boolean ignoreSerialization = false;


  /**
   * @return List of {@link org.parabuild.ci.services.BuildStartRequestParameter}
   */
  public BuildStartRequestParameter[] getParameterList() {
    return parameterList;
  }


  public void setParameterList(final BuildStartRequestParameter[] parameterList) {
    this.parameterList = parameterList;
  }


  public int getUserID() {
    return userID;
  }


  public void setUserID(final int userID) {
    this.userID = userID;
  }


  /**
   * @return Change list ID to  run build against or -1 if undefined.
   */
  public int getChangeListID() {
    return changeListID;
  }


  public void setChangeListID(final int changeListID) {
    this.changeListID = changeListID;
  }


  /**
   * @return label to apply after build run succeeds.
   */
  public String getLabel() {
    return label;
  }


  public void setLabel(final String label) {
    this.label = label;
  }


  public int getBuildRunID() {
    return buildRunID;
  }


  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


  /**
   * @return note associated with this build.
   */
  public String getNote() {
    return note;
  }


  public void setNote(final String note) {
    this.note = note;
  }


  /**
   * @return true if pinning of build results was requested.
   */
  public boolean isPinResult() {
    return pinResult;
  }


  public void setPinResult(final boolean pinResult) {
    this.pinResult = pinResult;
  }


  /**
   * @return version counter, -1 if was not set
   */
  public int getVersionCounter() {
    return versionCounter;
  }


  public void setVersionCounter(final int versionCounter) {
    this.versionCounter = versionCounter;
  }


  /**
   * @return version template. Empty or null string if was not set.
   */
  public String getVersionTemplate() {
    return versionTemplate;
  }


  /**
   * @return List of {@link SourceControlSettingOverride}
   *         objects. Value in these objects should override build
   *         settings for the given build run.
   */
  public SourceControlSettingOverride[] getSourceControlSettingsOverrides() {
    return sourceControlSettingsOverrides;
  }


  public void setSourceControlSettingsOverrides(final SourceControlSettingOverride[] sourceControlSettingsOverrides) {
    this.sourceControlSettingsOverrides = sourceControlSettingsOverrides;
  }


  public boolean isCleanCheckout() {
    return cleanCheckout;
  }


  public void setCleanCheckout(final boolean cleanCheckout) {
    this.cleanCheckout = cleanCheckout;
  }


  /**
   * Sets request type.
   *
   * @param requestType
   */
  public void setRequestType(final int requestType) {
    this.requestType = requestType;
  }


  public int getRequestType() {
    return requestType;
  }


  /**
   * Returns agent host for this request.
   *
   * @return agent host for this request.
   */
  public AgentHost getAgentHost() {
    return agentHost;
  }


  /**
   * Returns agent host for this request.
   *
   * @param agentHost agent host for this request.
   */
  public void setAgentHost(final AgentHost agentHost) {
    this.agentHost = agentHost;
  }


  public void setIgnoreSerialization(final boolean ignoreSerialization) {
    this.ignoreSerialization = ignoreSerialization;
  }


  public boolean isIgnoreSerialization() {
    return ignoreSerialization;
  }


  public String toString() {
    return "BuildStartRequest{" +
            "agentHost=" + agentHost +
            ", buildRunID=" + buildRunID +
            ", changeListID=" + changeListID +
            ", cleanCheckout=" + cleanCheckout +
            ", ignoreSerialization=" + ignoreSerialization +
            ", label='" + label + '\'' +
            ", note='" + note + '\'' +
            ", parameterList=" + (parameterList == null ? null : Arrays.asList(parameterList)) +
            ", pinResult=" + pinResult +
            ", requestType=" + requestType +
            ", sourceControlSettingsOverrides=" + (sourceControlSettingsOverrides == null ? null : Arrays.asList(sourceControlSettingsOverrides)) +
            ", userID=" + userID +
            ", versionCounter=" + versionCounter +
            ", versionTemplate='" + versionTemplate + '\'' +
            '}';
  }
}