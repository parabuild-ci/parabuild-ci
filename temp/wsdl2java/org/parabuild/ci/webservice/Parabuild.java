/**
 * Parabuild.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public interface Parabuild extends java.rmi.Remote {
    public org.parabuild.ci.webservice.StartParameter[] getVariables(int in0, int in1) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.SystemProperty[] getSystemProperties() throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.Project[] getProjects() throws java.rmi.RemoteException;
    public void startBuild(int in0) throws java.rmi.RemoteException;
    public void startBuild(int in0, org.parabuild.ci.webservice.BuildStartRequest in1) throws java.rmi.RemoteException;
    public void stopBuild(int in0) throws java.rmi.RemoteException;
    public void resumeBuild(int in0) throws java.rmi.RemoteException;
    public void requestCleanCheckout(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildStatus[] getCurrentBuildStatuses() throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildStatus getCurrentBuildStatus(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildStatus getCurrentBuildStatus(java.lang.String in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildStatus[] findCurrentBuildStatuses(java.lang.String in0) throws java.rmi.RemoteException;
    public java.lang.String serverVersion() throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.GlobalVCSUserMap[] getGlobalVcsUserMap() throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.ProjectAttribute[] getProjectAttributes(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.ProjectBuild[] getProjectBuilds(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.DisplayGroup[] getDisplayGroups() throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.DisplayGroupBuild[] getDisplayGroupBuilds(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildFarmConfiguration[] getBuildFarmConfigurations() throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildFarmConfigurationAttribute[] getBuildFarmConfigurationAttributes(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildFarmAgent[] getBuildFarmAgents(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.AgentConfiguration[] getAgentConfigurations() throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.AgentStatus[] getAgentStatuses() throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.AgentConfiguration getAgentConfiguration(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildConfiguration[] getActiveBuildConfigurations() throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildConfigurationAttribute[] getBuildConfigurationAttributes(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.VersionControlSetting[] getVersionControlSettings(int in0) throws java.rmi.RemoteException;
    public void updateVersionControlSettings(org.parabuild.ci.webservice.VersionControlSetting[] in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.ScheduleProperty[] getScheduleProperties(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.LabelProperty[] getLabelProperties(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.LogConfiguration[] getLogConfigurations(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.LogConfigurationProperty[] getLogConfigurationProperties(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.VCSUserToEmailMap[] getVCSUserToEmailMap(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildWatcher[] getBuildWatchers(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildSequence[] getBuildSequence(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.ScheduleItem[] getScheduleItem(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.IssueTracker[] getIssueTracker(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.IssueTrackerProperty[] getIssueTrackerProperties(int in0) throws java.rmi.RemoteException;
    public int getBuildRunCount(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildRun getBuildRun(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildRun[] getCompletedBuildRuns(int in0, int in1, int in2) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildRun getLastSuccessfulBuildRun(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildRun[] findlLastSuccessfulBuildRuns(int in0, int in1) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildRunAttribute[] getBuildRunAttributes(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.ChangeList[] getBuildRunParticipants(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.Change[] getChanges(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.StepRun[] getStepRuns(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.StepRunAttribute[] getStepRunRunAttributes(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.StepLog[] getStepLogs(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.StepResult[] getStepResults(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.ChangeList getChangeList(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.ReleaseNote[] getReleaseNotes(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.Issue getIssue(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.IssueAttribute[] getIssueAttributes(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.IssueChangeList[] getIssueChangeLists(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildStatistics[] getHourlyStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildStatistics[] getDailyStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildStatistics[] getMonthlyStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildStatistics[] getYearlyStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildDistribution[] getHourlyBuildDistributions(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildDistribution[] getWeekdayBuildDistributions(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.TestStatistics[] getHourlyTestStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2, byte in3) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.TestStatistics[] getMonthlyTestStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2, byte in3) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.TestStatistics[] getDailyTestStatistics(int in0, java.util.Calendar in1, java.util.Calendar in2, byte in3) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.ResultGroup[] getResultGroups() throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.ProjectResultGroup[] getProjectResultGroups(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.ResultConfiguration[] getResultConfigurations(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.ResultConfigurationProperty[] getResultConfigurationProperties(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.PublishedStepResult[] getPublishedStepResults(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildRunAction[] getBuildRunActions(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.TestSuiteName[] getTestSuiteNames() throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.TestCaseName[] getTestCaseNames(int in0) throws java.rmi.RemoteException;
    public org.parabuild.ci.webservice.BuildRunTest[] getBuildRunTests(int in0) throws java.rmi.RemoteException;
}
