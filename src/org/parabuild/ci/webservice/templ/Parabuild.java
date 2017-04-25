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

import java.io.IOException;
import java.util.Calendar;

/**
 * Parabuild web service template. This template is used to generate WSDL.
 *
 * @noinspection InterfaceNeverImplemented
 */
public interface Parabuild {


  /**
   * Starts the build.
   *
   * @param activeBuildID ID of the build to start.
   */
  void startBuild(int activeBuildID) throws IOException;


  /**
   * Starts the build.
   *
   * @param activeBuildID     ID of the build to start.
   * @param buildStartRequest an object containing a detailed build start request.
   */
  void startBuild(int activeBuildID, BuildStartRequest buildStartRequest) throws IOException;


  /**
   * Stops the build.
   *
   * @param activeBuildID ID of the build to stop.
   */
  void stopBuild(int activeBuildID);


  /**
   * Resumes the build.
   *
   * @param activeBuildID ID of the build to resume.
   */
  void resumeBuild(int activeBuildID);


  /**
   * Requests clean checkout. Parabuild will erase build's work space before running next build.
   *
   * @param activeBuildID ID of the build for that to perfrom a clean checkout.
   */
  void requestCleanCheckout(int activeBuildID);


  /**
   * Returns a list of current build statuses.
   *
   * @return a list of current build statuses.
   */
  BuildStatus[] getCurrentBuildStatuses();


  /**
   * Returns a current status for a build with the given name.
   *
   * @param buildName case-sensitive build
   * @return a current status for a build with the given name or null if there is no such build
   */
  BuildStatus getCurrentBuildStatus(final String buildName);


  /**
   * Returns a current status for a build with the given ID.
   *
   * @param activeBuildID build ID
   * @return a current status for a build with the given ID or null if there is no such build.
   */
  BuildStatus getCurrentBuildStatus(final int activeBuildID);


  /**
   * Returns a list of current statuses for builds whose names match the give regex.
   *
   * @param regularExpression regular expression to match build names
   * @return a list of current statuses for builds whose names match the give regex
   */
  BuildStatus[] findCurrentBuildStatuses(final String regularExpression);


  /**
   * Returns server version.
   *
   * @return a string containing server version.
   */
  String serverVersion();


  /**
   * Returns a list of global system properties.
   *
   * @return a list of global system properties.
   */
  SystemProperty[] getSystemProperties();


  /**
   * Returns a global version control user to email map.
   *
   * @return a global version control user to email map.
   */
  GlobalVCSUserMap[] getGlobalVcsUserMap();


  /**
   * Returns a list of projects.
   *
   * @return a list of projects.
   */
  Project[] getProjects();

  /**
   * Returns a list of project attributes.
   *
   * @param projectID project ID
   * @return a list of project attributes.
   */
  ProjectAttribute[] getProjectAttributes(int projectID);

  /**
   * Returns a list of build configurations belonging to a project.
   *
   * @param projectID project ID
   * @return a list of build configurations belonging to a project.
   */
  ProjectBuild[] getProjectBuilds(int projectID);

  /**
   * Returns a list of display groups.
   *
   * @return a list of display groups.
   */
  DisplayGroup[] getDisplayGroups();

  /**
   * Returns a list of build configurations are that part of a display group.
   *
   * @param displayGroupID display group ID
   * @return a list of build configurations are that part of a display group.
   */
  DisplayGroupBuild[] getDisplayGroupBuilds(int displayGroupID);

  /**
   * Retuns a list of build farm configurations.
   *
   * @return a list of build farm configurations.
   */
  BuildFarmConfiguration[] getBuildFarmConfigurations();

  /**
   * Returns a list of attributes of a build farm.
   *
   * @param buildFarmID build farm ID
   * @return a list of attributes of a build farm.
   */
  BuildFarmConfigurationAttribute[] getBuildFarmConfigurationAttributes(int buildFarmID);

  /**
   * Returns a list of agents belonging to a build farm.
   *
   * @param buildFarmID build farm ID
   * @return a list of build agents belonging to a build farm.
   */
  BuildFarmAgent[] getBuildFarmAgents(int buildFarmID);

  /**
   * Returns a list agent configurations.
   *
   * @return a list agent configurations.
   */
  AgentConfiguration[] getAgentConfigurations();

  /**
   * Returns a list of agent statuses.
   *
   * The agent status code is contained in the field status. 1 means disabled. 2 means idle.
   * 3 means busy. 4 means offline. 5 means there is a version mismatch.
   *
   * @return a list of agent statuses.
   */
  AgentStatus[] getAgentStatuses();

  /**
   * Returns agent configuration.
   *
   * @param agentID agent configuration ID
   * @return Returns agent configuration..
   * @see #getBuildFarmAgents(int)
   */
  AgentConfiguration getAgentConfiguration(int agentID);

  /**
   * Returns a list of active build confiurations.
   *
   * @return a list of active build confiurations.
   */
  BuildConfiguration[] getActiveBuildConfigurations();

  /**
   * Returns a list of attributes for a build configuration.
   *
   * @param buildID build ID.
   * @return a list of attributes for a build configuration.
   */
  BuildConfigurationAttribute[] getBuildConfigurationAttributes(int buildID);

  /**
   * Returns a list of version control settings.
   *
   * @param buildID build ID
   * @return a list of version control settings.
   */
  VersionControlSetting[] getVersionControlSettings(int buildID);

  /**
   * Updates or creates version control settings.
   *
   * @param versionControlSettings version control settings to update or create.
   */
  void updateVersionControlSettings(VersionControlSetting[] versionControlSettings);

  /**
   * Returns a list of schedule properties.
   *
   * @param buildID build ID
   * @return a list of schedule properties.
   */
  ScheduleProperty[] getScheduleProperties(int buildID);

  /**
   * Returns a list of label properties.
   *
   * @param buildID build ID
   * @return a list of label properties.
   */
  LabelProperty[] getLabelProperties(int buildID);

  /**
   * Returns a list of log configurations.
   *
   * @param buildID build ID
   * @return a list of log configurations.
   */
  LogConfiguration[] getLogConfigurations(int buildID);

  /**
   * Returns a list of log configiuration properties.
   *
   * @param logID log ID
   * @return a list of log configiuration properties.
   */
  LogConfigurationProperty[] getLogConfigurationProperties(int logID);

  /**
   * Returns a list of version control user to email maps.
   *
   * @param buildID build ID
   * @return a list of version control use to email maps.
   */
  VCSUserToEmailMap[] getVCSUserToEmailMap(int buildID);

  /**
   * Returns a list of build watchers.
   *
   * @param buildID a list of build watchers.
   * @return a list of build watchers.
   */
  BuildWatcher[] getBuildWatchers(int buildID);

  /**
   * Returns a list of build steps.
   *
   * @param buildID build ID
   * @return a list of build steps.
   */
  BuildSequence[] getBuildSequence(int buildID);

  /**
   * Returns a list of schedule items
   *
   * @param buildID build ID
   * @return Returns a list of schedule items
   */
  ScheduleItem[] getScheduleItem(int buildID);

  /**
   * Returns a list of issues trackers.
   *
   * @param buildID build ID
   * @return a list of issues trackers.
   */
  IssueTracker[] getIssueTracker(int buildID);

  /**
   * Returns a list of issue tracker properties.
   *
   * @param issueTrackerID issue tracker ID
   * @return a list of issue tracker properties.
   */
  IssueTrackerProperty[] getIssueTrackerProperties(int issueTrackerID);

  /**
   * Returns a list of start parameters.
   *
   * @param type    parameter type.
   * @param ownerID owner ID
   * @return a list of start parameters.
   */
  StartParameter[] getVariables(int type, int ownerID);


  //
  //
  //

  /**
   * Returns number of build runs for the given build condifuration.
   *
   * @param activeBuildID build ID
   * @return number of build runs for the given build condifuration.
   */
  int getBuildRunCount(int activeBuildID);

  /**
   * Returns build run for the given build run ID.
   *
   * @param buildRunID build run ID
   * @return build run.
   */
  BuildRun getBuildRun(int buildRunID);

  /**
   * Returns a list of completed build runs.
   *
   * @param activeBuildID build ID
   * @param firstResult   number from that to start listing build runs.
   * @param maxCount      number of build runs to retrieve.
   * @return a list of completed build runs.
   */
  BuildRun[] getCompletedBuildRuns(final int activeBuildID, final int firstResult, final int maxCount);

  /**
   * Returns the last successful build run.
   *
   * @param activeBuildID build ID
   * @return the last successful build run or null if there is no any.
   */
  BuildRun getLastSuccessfulBuildRun(int activeBuildID);

  /**
   * Returns an array of last successful BuildRuns that match given display group ID and change list number.
   *
   * @param displayGroupID   display group ID
   * @param changeListNumber change list number
   * @return array of last successful BuildRuns that matche given display group ID and change list number or an empty array if no matching builds exist.
   */
  BuildRun[] findlLastSuccessfulBuildRuns(final int displayGroupID, final int changeListNumber);

  /**
   * Returns a list of build run attributes.
   *
   * @param buildRunID build run ID
   * @return a list of buildRunAttributes
   */
  BuildRunAttribute[] getBuildRunAttributes(int buildRunID);

  /**
   * Returns change lists that participated in the given build run.
   *
   * @param buildRunID build run ID.
   * @return change lists that participated in the given build run.
   */
  ChangeList[] getBuildRunParticipants(int buildRunID);

  /**
   * Returns a list of changes in the given change list.
   *
   * @param changeListID change list ID
   * @return a list of changes in the given change list.
   * @see #getBuildRunParticipants(int)
   * @see #getChangeList(int)
   */
  Change[] getChanges(int changeListID);

  /**
   * Returns a list of build step runs.
   *
   * @param buildRunID build run ID
   * @return a list of step runs.
   */
  StepRun[] getStepRuns(int buildRunID);

  /**
   * Returns a list of build step run attributes.
   *
   * @param stepRunID step run ID
   * @return a list of step run attributes.
   * @see #getStepRuns(int)
   */
  StepRunAttribute[] getStepRunRunAttributes(int stepRunID);

  /**
   * Returns a list of build step logs.
   *
   * @param stepRunID step run ID
   * @return a list of build step logs.
   */
  StepLog[] getStepLogs(int stepRunID);

  /**
   * Returns a list of build step results.
   *
   * @param stepRunID step run ID
   * @return a list of build step results.
   */
  StepResult[] getStepResults(int stepRunID);

  /**
   * Returns a change list.
   *
   * @param changeListID change list ID
   * @return a change list.
   */
  ChangeList getChangeList(int changeListID);

  /**
   * Returns a list of release notes.
   *
   * @param buildRunID build run ID
   * @return a list of release notes.
   */
  ReleaseNote[] getReleaseNotes(int buildRunID);

  /**
   * Returns an issue.
   *
   * @param issueID an issue ID.
   * @return an issue.
   */
  Issue getIssue(int issueID);

  /**
   * Returns a list of issue attributes.
   *
   * @param issueID issue ID.
   * @return a list of issue attributes.
   */
  IssueAttribute[] getIssueAttributes(int issueID);

  /**
   * Returns a list of issue change lists.
   *
   * @param issueID issue ID.
   * @return a list of issue change lists.
   */
  IssueChangeList[] getIssueChangeLists(int issueID);


  /**
   * Returns hourly build statistics.
   *
   * @param activeBuildID active build ID
   * @param fromDate      from date
   * @param toDate        to date
   * @return hourly build statistics.
   */
  BuildStatistics[] getHourlyStatistics(int activeBuildID, Calendar fromDate, Calendar toDate);

  BuildStatistics[] getDailyStatistics(int activeBuildID, Calendar fromDate, Calendar toDate);

  BuildStatistics[] getMonthlyStatistics(int activeBuildID, Calendar fromDate, Calendar toDate);

  BuildStatistics[] getYearlyStatistics(int activeBuildID, Calendar fromDate, Calendar toDate);


  /**
   * Return hourly build ditribution.
   *
   * @param activeBuildID
   * @return hourly build ditribution.
   */
  BuildDistribution[] getHourlyBuildDistributions(int activeBuildID);

  BuildDistribution[] getWeekdayBuildDistributions(int activeBuildID);

  /**
   * Returns hourly test statistics.
   *
   * @param activeBuildID active build ID
   * @param fromDate      from date
   * @param toDate        to date
   * @param testToolCode  test tool code.
   * @return hourly test statistics.
   */
  TestStatistics[] getHourlyTestStatistics(int activeBuildID, final Calendar fromDate, final Calendar toDate, byte testToolCode);

  TestStatistics[] getMonthlyTestStatistics(int activeBuildID, final Calendar fromDate, final Calendar toDate, byte testToolCode);

  TestStatistics[] getDailyTestStatistics(int activeBuildID, final Calendar fromDate, final Calendar toDate, byte testToolCode);


  /**
   * Returns a list of result groups.
   *
   * @return list of result groups.
   */
  ResultGroup[] getResultGroups();

  /**
   * Returns a list of project result groups.
   *
   * @param projectID project ID
   * @return a list of project result groups.
   */
  ProjectResultGroup[] getProjectResultGroups(int projectID);

  /**
   * Returns a list of result configurations.
   *
   * @param buildID build ID
   * @return a list of result configurations.
   */
  ResultConfiguration[] getResultConfigurations(int buildID);

  /**
   * Returns a list of result configuration properties.
   *
   * @param resultConfigID result configuration ID
   * @return a list of result configuration properties.
   */
  ResultConfigurationProperty[] getResultConfigurationProperties(int resultConfigID);

  /**
   * Returns a list published build step results.
   *
   * @param resultGroupID result group ID
   * @return a list published build step results.
   */
  PublishedStepResult[] getPublishedStepResults(int resultGroupID);


  /**
   * Returns a list of build runs actions.
   *
   * @param buildRunID
   * @return a list of build runs actions.
   */
  BuildRunAction[] getBuildRunActions(int buildRunID);

  /**
   * Returns a list of test suite names.
   *
   * @return a list of test suite names.
   */
  TestSuiteName[] getTestSuiteNames();

  /**
   * Returns a list of test case names.
   *
   * @param testSuiteNameID test suite name ID
   * @return a list of test case names.
   */
  TestCaseName[] getTestCaseNames(int testSuiteNameID);

  /**
   * Returns a list of build run tests.
   *
   * @param buildRunID build run ID
   * @return a list of build run tests.
   */
  BuildRunTest[] getBuildRunTests(int buildRunID);
}
