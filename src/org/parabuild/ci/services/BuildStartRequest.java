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
package org.parabuild.ci.services;

import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.SourceControlSettingVO;
import org.parabuild.ci.webui.result.BuildRunResultVO;
import viewtier.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Request to start a build.
 */
public final class BuildStartRequest {

  public static final byte REQUEST_NORMAL = 0;
  public static final byte REQUEST_RERUN = 1;
  public static final byte REQUEST_PARALLEL = 2;
  public static final byte REQUEST_VERIFICATION = 3;


  /**
   * Chained request is sent by an upstream build on a
   * successful build run completion.
   */
  public static final byte REQUEST_CHAINED = 4;

  private boolean cleanCheckout = false;
  private boolean uniqueAgentCheckout = false;
  private boolean pinResult = false;
  private byte requestType;
  private int buildRunID;
  private final int changeListID;
  private final int userID;
  private final int versionCounter;
  private final List parameterList = new ArrayList(11);
  private final String label;
  private final String note;
  private final String versionTemplate;

  /**
   * List of {@link SourceControlSettingVO} objects.
   */
  private final List sourceControlSettingsOverrides = new ArrayList(11);

  /**
   * List of results published before calling published commands.
   */
  private final List publishedResults = new ArrayList(5);

  private boolean publishingRun = false;

  /**
   * Agent host.
   */
  private AgentHost agentHost = null;

  /**
   * A flag indicating that the agent host required.
   *
   * @see #agentHost
   */
  private boolean agentHostRequired = false;

  private boolean ignoreSerialization = false;

  /**
   * A flag indicating that a next scheduled build should be skipped if this build start request proceeds.
   */
  private boolean skipNextScheduledBuild = false;


  /**
   * Creates a "faceless" build start request that doesn't have
   * information about user change list id and parameters.
   */
  public BuildStartRequest() {
    this(REQUEST_NORMAL, -1, -1, BuildRun.UNSAVED_ID, Collections.EMPTY_LIST, null, "", false, null, -1, Collections.EMPTY_LIST);
  }


  /**
   * Creates a copy of a given request and sets new change list
   * ID.
   *
   * @param original     the original build request.
   * @param changeListID a change list override.
   */
  public BuildStartRequest(final BuildStartRequest original, final int changeListID) {
    this(original.requestType, original.userID,
            changeListID,
            original.buildRunID,
            original.parameterList,
            original.label,
            original.note,
            original.pinResult,
            original.versionTemplate,
            original.versionCounter,
            original.sourceControlSettingsOverrides);
    this.publishingRun = original.publishingRun;
    this.cleanCheckout = original.cleanCheckout;
    this.agentHost = original.agentHost;
    this.agentHostRequired = original.agentHostRequired;
    this.skipNextScheduledBuild = original.skipNextScheduledBuild;
  }


  public BuildStartRequest(final int userID, final List parameterList, final String label) {
    this(REQUEST_NORMAL, userID, -1, BuildRun.UNSAVED_ID, parameterList, label, "", false, null, -1, Collections.EMPTY_LIST);
  }


  public BuildStartRequest(final int userID, final int changeListID) {
    this(REQUEST_NORMAL, userID, changeListID, BuildRun.UNSAVED_ID, Collections.EMPTY_LIST, null, "", false, null, -1, Collections.EMPTY_LIST);
  }


  /**
   * @param requestType                    defines is this a "normal" request,
   *                                       a re-run request or a parallel request.
   * @param userID                         a user ID or -1 if not known
   * @param changeListID                   a change list ID to start build against or -1 if latest or it buildRunID is set.
   * @param buildRunID                     a build run ID.
   * @param parameterList                  a List of {@link BuildStartRequestParameter}
   * @param label                          a label to assign if successful.
   * @param note                           a build note.
   * @param pinBuildResult                 true if the build result should be pinned.
   * @param versionTemplate                a version template.
   * @param versionCounter                 a version counter.
   * @param sourceControlSettingsOverrides a list of overridden source control settings.
   */
  public BuildStartRequest(final byte requestType, final int userID, final int changeListID,
                           final int buildRunID, final List parameterList, final String label,
                           final String note, final boolean pinBuildResult, final String versionTemplate,
                           final int versionCounter, final List sourceControlSettingsOverrides) {

    // validate
    if ((requestType == REQUEST_RERUN || requestType == REQUEST_PARALLEL) && buildRunID == -1) {
      throw new IllegalArgumentException("For request type \"" + requestType + "\" build run ID should be set");
    }

    // set fields
    this.requestType = requestType;
    this.userID = userID;
    this.changeListID = changeListID;
    this.parameterList.addAll(parameterList);
    this.label = label;
    this.buildRunID = buildRunID;
    this.note = note;
    this.pinResult = pinBuildResult;
    this.versionCounter = versionCounter;
    this.versionTemplate = versionTemplate;
    this.sourceControlSettingsOverrides.addAll(sourceControlSettingsOverrides);
  }


  public BuildStartRequest(final int userID) {
    this(REQUEST_NORMAL, userID, -1, BuildRun.UNSAVED_ID, Collections.EMPTY_LIST, null, "", false, null, -1, Collections.EMPTY_LIST);
  }


  /**
   * @return List of {@link BuildStartRequestParameter}
   */
  public List parameterList() {
    return parameterList;
  }


  /**
   * @param parameter a build start parameter to add.
   */
  public void addParameter(final BuildStartRequestParameter parameter) {
    parameterList.add(parameter);
  }


  public void addParameters(final List parameters) {

    if (parameters == null || parameters.isEmpty()) {

      return;
    }

    for (int i = 0; i < parameters.size(); i++) {

      addParameter((BuildStartRequestParameter) parameters.get(i));
    }
  }


  public int userID() {
    return userID;
  }


  public boolean isUserSet() {
    return userID != -1;
  }


  /**
   * @return Change list ID to  run build against or -1 if undefined.
   */
  public int changeListID() {
    return changeListID;
  }


  /**
   * @return label to apply after build run succeeds.
   */
  public String label() {
    return label;
  }


  public int getBuildRunID() {
    return buildRunID;
  }


  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


  public boolean isReRun() {
    return requestType == REQUEST_RERUN;
  }


  public boolean isVerificationRun() {
    return requestType == REQUEST_VERIFICATION;
  }


  public boolean isParallel() {
    return requestType == REQUEST_PARALLEL;
  }


  /**
   * Returns true if this request is a result of a chained call.
   *
   * @return true if this request is a result of a chained call.
   */
  public boolean isChained() {
    return requestType == REQUEST_CHAINED;
  }


  /**
   * @return note associated with this build.
   */
  public String getNote() {
    return note;
  }


  public boolean isNoteSet() {
    return !StringUtils.isBlank(note);
  }


  /**
   * @return true if pinning of build results was requested.
   */
  public boolean isPinResult() {
    return pinResult;
  }


  /**
   * @return version counter, -1 if was not set
   */
  public int versionCounter() {
    return versionCounter;
  }


  /**
   * @return version template. Empty or null string if was not set.
   */
  public String versionTemplate() {
    return versionTemplate;
  }


  /**
   * @return List of {@link SourceControlSettingVO}
   *         objects. Value in these objects should override build
   *         settings for the given build run.
   */
  public List sourceControlSettingsOverwriteList() {
    return sourceControlSettingsOverrides;
  }


  /**
   * @return true if this build start request has has native
   *         change list number that a build is requested to run
   *         against.
   */
  public boolean hasNativeChangeListNumber() {
    for (final Iterator i = sourceControlSettingsOverrides.iterator(); i.hasNext(); ) {
      final SourceControlSettingVO vo = (SourceControlSettingVO) i.next();
      if (vo.isNativeChangeListNumber() && !StringUtils.isBlank(vo.getValue())) {
        return true;
      }
    }
    return false;
  }


  public String getNativeChangeListNumber() {
    for (final Iterator i = sourceControlSettingsOverrides.iterator(); i.hasNext(); ) {
      final SourceControlSettingVO vo = (SourceControlSettingVO) i.next();
      if (vo.isNativeChangeListNumber()) {
        return vo.getValue();
      }
    }
    return null; // not found
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
   * @param requestType the request type to set.
   */
  public void setRequestType(final byte requestType) {
    this.requestType = requestType;
  }


  /**
   * @return true if this is a promotion run.
   */
  public boolean isPublishingRun() {
    return publishingRun;
  }


  /**
   * @param publishingRun true if this is a promotion run.
   */
  public void setPublishingRun(final boolean publishingRun) {
    this.publishingRun = publishingRun;
  }


  /**
   * Adds a list of {@link BuildRunResultVO}
   *
   * @param actuallyPublishedResults a list of published build results.
   */
  public void addPublishedResults(final List actuallyPublishedResults) {
    publishedResults.addAll(actuallyPublishedResults);
  }


  /**
   * @return a list of {@link BuildRunResultVO}
   */
  public List getPublishedResults() {
    return publishedResults;
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


  /**
   * Returns the flag indicating that the agent host required.
   *
   * @return flag indicating that the agent host required.
   */
  public boolean isAgentHostRequired() {
    return agentHostRequired;
  }


  /**
   * Sets the flag indicating that the agent host required.
   *
   * @param agentHostRequired the flag indicating that the agent host required.
   */
  public void setAgentHostRequired(final boolean agentHostRequired) {
    this.agentHostRequired = agentHostRequired;
  }


  public void setIgnoreSerialization(final boolean ignoreSerialization) {
    this.ignoreSerialization = ignoreSerialization;
  }


  public boolean isIgnoreSerialization() {
    return ignoreSerialization;
  }


  /**
   * Returns <code>true</code> if the build run should check out a
   * build agent for itself only. Returns <code>false</code> if the
   * agent can be shared.
   *
   * @return <code>true</code> if the build run should check out a
   *         build agent for itself only. Returns <code>false</code> if the
   *         agent can be shared.
   */
  public boolean isUniqueAgentCheckout() {
    return uniqueAgentCheckout;
  }


  /**
   * A flag that defines if the build should check out a build agent for itself.
   *
   * @param uniqueAgentCheckout <code>true</code> if the build run should check out a
   *                            build agent for itself only. <code>false</code> if the
   *                            agent can be shared.
   */
  public void setUniqueAgentCheckout(final boolean uniqueAgentCheckout) {
    this.uniqueAgentCheckout = uniqueAgentCheckout;
  }


  /**
   * Sets the flag indicating that a next scheduled build should be skipped if this build start request proceeds.
   *
   * @param skipNextScheduledBuild the flag indicating that a next scheduled build should be skipped if this build start request proceeds.
   */
  public void setSkipNextScheduledBuild(final boolean skipNextScheduledBuild) {

    this.skipNextScheduledBuild = skipNextScheduledBuild;
  }


  /**
   * Returns the flag indicating that a next scheduled build should be skipped if this build start request proceeds.
   *
   * @return the flag indicating that a next scheduled build should be skipped if this build start request proceeds.
   */
  public boolean isSkipNextScheduledBuild() {

    return skipNextScheduledBuild;
  }


  /**
   * Returns true if build runner should re-run change detection using
   * actual SCN overrides received in the build start request.
   *
   * @return true if build runner should re-run change detection using
   *         actual SCN overrides received in the build start request.
   */
  public boolean isChangeListRedetectionRequired() {
    for (int i = 0; i < sourceControlSettingsOverrides.size(); i++) {
      final String name = ((SourceControlSettingVO) sourceControlSettingsOverrides.get(i)).getName();
      if (SourceControlSettingVO.CVS_BRANCH_NAME.equals(name) ||
              SourceControlSettingVO.P4_DEPOT_PATH.equals(name) ||
              SourceControlSettingVO.SVN_DEPOT_PATH.equals(name) ||
              SourceControlSettingVO.BAZAAR_BRANCH_LOCATION.equals(name)) {
        return true;
      }
    }
    return false;  //To change body of created methods use File | Settings | File Templates.
  }


  public String toString() {
    return "BuildStartRequest{" +
            "agentHost=" + agentHost +
            ", cleanCheckout=" + cleanCheckout +
            ", pinResult=" + pinResult +
            ", requestType=" + requestType +
            ", buildRunID=" + buildRunID +
            ", changeListID=" + changeListID +
            ", userID=" + userID +
            ", versionCounter=" + versionCounter +
            ", parameterList=" + parameterList +
            ", label='" + label + '\'' +
            ", note='" + note + '\'' +
            ", versionTemplate='" + versionTemplate + '\'' +
            ", sourceControlSettingsOverrides=" + sourceControlSettingsOverrides +
            ", publishedResults=" + publishedResults +
            ", publishingRun=" + publishingRun +
            '}';
  }
}
