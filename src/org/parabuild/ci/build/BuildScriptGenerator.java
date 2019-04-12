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
package org.parabuild.ci.build;

import org.parabuild.ci.object.BuildSequence;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * BuildScriptGenerator is responsible for generating build
 * scripts
 */
public interface BuildScriptGenerator {

  String VAR_PARABUILD_GIT_BRANCH = "PARABUILD_GIT_BRANCH";
  String VAR_PARABUILD_BUILD_DATE = "PARABUILD_BUILD_DATE";
  String VAR_PARABUILD_BUILD_DIR = "PARABUILD_BUILD_DIR";
  String VAR_PARABUILD_BUILD_NAME = "PARABUILD_BUILD_NAME";
  String VAR_PARABUILD_BUILD_NUMBER = "PARABUILD_BUILD_NUMBER";
  String VAR_PARABUILD_BUILD_RUN_ID = "PARABUILD_BUILD_RUN_ID";
  String VAR_PARABUILD_BUILD_ID = "PARABUILD_BUILD_ID";
  String VAR_PARABUILD_BUILD_STARTED_BY_USER = "PARABUILD_BUILD_STARTED_BY_USER";
  String VAR_PARABUILD_BUILD_TIMESTAMP = "PARABUILD_BUILD_TIMESTAMP";
  String VAR_PARABUILD_BUILD_TIMESTAMP_UTC = "PARABUILD_BUILD_TIMESTAMP_UTC";
  String VAR_PARABUILD_CLEAN_CHECKOUT = "PARABUILD_CLEAN_CHECKOUT";
  String VAR_PARABUILD_CHANGE_LIST_DATETIME = "PARABUILD_CHANGE_LIST_DATETIME";
  String VAR_PARABUILD_CHANGE_LIST_NUMBER = "PARABUILD_CHANGE_LIST_NUMBER";
  String VAR_PARABUILD_CHECKOUT_DIR = "PARABUILD_CHECKOUT_DIR";
  String VAR_PARABUILD_CONFIGURATION_ID = "PARABUILD_CONFIGURATION_ID";
  String VAR_PARABUILD_PROJECT_ID = "PARABUILD_PROJECT_ID";
  String VAR_PARABUILD_PROJECT_NAME = "PARABUILD_PROJECT_NAME";
  String VAR_PARABUILD_STEP_NAME = "PARABUILD_STEP_NAME";
  String VAR_PARABUILD_VERSION_COUNTER = "PARABUILD_VERSION_COUNTER";
  String VAR_PARABUILD_VERSION = "PARABUILD_VERSION";
  String VAR_PARABUILD_LEADING_BUILD_ID = "PARABUILD_LEADING_BUILD_ID";
  String VAR_PARABUILD_LEADING_BUILD_NAME = "PARABUILD_LEADING_BUILD_NAME";
  String VAR_PARABUILD_LEADING_BUILD_RUN_ID = "PARABUILD_LEADING_BUILD_RUN_ID";
  String VAR_PARABUILD_SEQUENCE_NUMBER = "PARABUILD_SEQUENCE_NUMBER";
  String VAR_PARABUILD_PREVIOUS_CHANGE_LIST_DATETIME = "PARABUILD_PREVIOUS_CHANGE_LIST_DATETIME";
  String VAR_PARABUILD_PREVIOUS_CHANGE_LIST_NUMBER = "PARABUILD_PREVIOUS_CHANGE_LIST_NUMBER";
  String VAR_PARABUILD_LAST_GOOD_BUILD_NUMBER = "PARABUILD_LAST_GOOD_BUILD_NUMBER";
  String VAR_PARABUILD_LAST_GOOD_BUILD_DATETIME = "PARABUILD_LAST_GOOD_BUILD_DATETIME";
  String VAR_PARABUILD_LAST_GOOD_CHANGE_LIST_DATETIME = "PARABUILD_LAST_GOOD_CHANGE_LIST_DATETIME";
  String VAR_PARABUILD_LAST_GOOD_CHANGE_LIST_NUMBER = "PARABUILD_LAST_GOOD_CHANGE_LIST_NUMBER";
  String VAR_PARABUILD_UPSTREAM_BUILD_RUN_ID = "PARABUILD_UPSTREAM_BUILD_RUN_ID";
  String VAR_PARABUILD_UPSTREAM_BUILD_ID = "PARABUILD_UPSTREAM_BUILD_ID";
  String VAR_PARABUILD_UPSTREAM_BUILD_NAME = "PARABUILD_UPSTREAM_BUILD_NAME";
  String VAR_PARABUILD_PREVIOUS_STEPS = "PARABUILD_PREVIOUS_STEPS";
  String BUILD_DATE_FORMAT = "yyyyMMdd";
  String BUILD_TIMESTAMP_FORMAT = "yyyyMMddHHmmss";


  /**
   * Generates build sequence script file for further execution.
   *
   * @param sequence fo which a script will be created
   * @return String absolute path to created step script file.
   * @throws IOException
   */
  String generateScriptFile(BuildSequence sequence) throws IOException, AgentFailureException;


  /**
   * Sets build, or source line home directory. Build directory
   * is the directory where the build script is running from, and
   * from all uses-specified directories and files, like logs,
   * are counted from.
   */
  void setRelativeBuildDir(String buildDirName);


  /**
   * Sets mandatory build number value.
   * <p/>
   * Each build has a sequence number. Value set by this method
   * is used to add setting an environment variable
   * PARABUILD_BUILD_NUMBER in a build script.
   */
  void setBuildNumber(int number);


  /**
   * Sets mandatory changelist number the build is built to
   *
   * @param changeListNumber
   */
  void setChangeListNumber(String changeListNumber);


  /**
   * Sets required build name.
   */
  void setBuildName(String buildName);


  /**
   * Sets required step name.
   */
  void setStepName(String stepName);


  /**
   * Sets required build run ID.
   */
  void setBuildRunID(int buildRunID);


  /**
   * Adds shell variables to be present in the script.
   *
   * @param variables a Map with a shell variable name as a key
   *                  and variable value as value.
   */
  void addVariables(Map variables);


  /**
   * Sets required date and time the build started at.
   *
   * @param startedAt date and time the build started at.
   */
  void setBuildStartedAt(Date startedAt);


  /**
   * Sets the name of the user who started the build.
   *
   * @param userName name of the user who started the build.
   */
  void setBuildStartedByUser(String userName);


  void addVariable(final String name, final String value);


  /**
   * Change list date.
   *
   * @param changeListDate
   */
  void setChangeListDate(Date changeListDate);


  /**
   * Sets optional version if it was generated
   *
   * @param version
   */
  void setVersion(final String version);


  /**
   * Sets optional version counter if it was generated
   *
   * @param versionCounter
   */
  void setVersionCounter(final int versionCounter);


  /**
   * Signifies that a build performed for a clean workspace.
   */
  void setCleanCheckout(final boolean clean);


  /**
   * Sets leading build run ID if any
   *
   * @param leadingBuildRunID
   */
  void setLeadingBuildRunID(final int leadingBuildRunID);


  /**
   * Sets leading build name if any.
   *
   * @param leadingBuildName
   */
  void setLeadingBuildName(final String leadingBuildName);


  /**
   * Sets leading build ID if any.
   *
   * @param activeBuildID
   */
  void setLeadingBuildID(final int activeBuildID);


  /**
   * Sets sequence number that is unique per build
   * configuration.
   */
  void setSequenceNumber(final int sequenceNumber);


  /**
   * @param previousChangeListNumber optional previous change list number.
   */
  void setPreviousChangeListNumber(String previousChangeListNumber);


  /**
   * @param previousChangeListDate optional previous change list date.
   */
  void setPreviousChangeListDate(Date previousChangeListDate);

  /**
   * Sets last good build date if any.
   *
   * @param lastGoodBuildDate last good build date
   */
  void setLastGoodBuildDate(Date lastGoodBuildDate);

  /**
   * Sets last good change list date if any.
   *
   * @param lastGoodChangeListDate last good change list date
   */
  void setLastGoodChangeListDate(Date lastGoodChangeListDate);

  /**
   * Sets last good change list number, if any.
   *
   * @param lastGoodChangeListNumber last good change list number
   */
  void setLastGoodChangeListNumber(String lastGoodChangeListNumber);

  /**
   * Sets last good build number, if any.
   *
   * @param lastGoodBuildNumber last good build number.
   */
  void setLastGoodBuildNumber(int lastGoodBuildNumber);


  /**
   * Sets unique active build configuration ID.
   *
   * @param buildID unique active build configuration ID.
   */
  void setBuildID(int buildID);

  /**
   * Sets unique project ID.
   *
   * @param projectID unique project ID.
   */
  void setProjectID(int projectID);

  /**
   * Sets project name.
   *
   * @param projectName project name.
   */
  void setProjectName(String projectName);

  /**
   * Sets an ID of the build run that started the build.
   *
   * @param upstreamBuildRunID
   */
  void setUpstreamBuildRunID(int upstreamBuildRunID);

  /**
   * Sets a name of the build that started the build.
   *
   * @param upstreamBuildName
   */
  void setUpstreamBuildName(String upstreamBuildName);

  /**
   * Sets an ID of the build that started the build.
   *
   * @param upstreamBuildID
   */
  void setUpstreamBuildID(int upstreamBuildID);

  /**
   * Sets an optional branch name.
   *
   * @param branchName a branch name to set.
   */
  void setGitBranchName(String branchName);

  /**
   * Sets a list of previous step results.
   */
  void setPreviousStepRuns(List previousStepRuns);
}
