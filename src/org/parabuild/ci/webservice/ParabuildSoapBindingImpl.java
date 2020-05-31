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
package org.parabuild.ci.webservice;

import org.parabuild.ci.Version;
import org.parabuild.ci.archive.ArchiveEntry;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.DisplayGroupManager;
import org.parabuild.ci.configuration.GlobalVCSUserMapManager;
import org.parabuild.ci.configuration.ResultGroupManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.object.LogConfigProperty;
import org.parabuild.ci.object.PersistentBuildStats;
import org.parabuild.ci.object.PersistentDistribution;
import org.parabuild.ci.object.PersistentTestStats;
import org.parabuild.ci.object.ResultConfigProperty;
import org.parabuild.ci.object.SourceControlSettingVO;
import org.parabuild.ci.object.User;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.BuildListService;
import org.parabuild.ci.services.BuildService;
import org.parabuild.ci.services.BuildStartRequest;
import org.parabuild.ci.services.BuildStartRequestParameter;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.statistics.DailyPersistentBuildStatsRetriever;
import org.parabuild.ci.statistics.DailyPersistentTestStatsRetriever;
import org.parabuild.ci.statistics.HourlyBuildDistributionRetriever;
import org.parabuild.ci.statistics.HourlyPersistentBuildStatsRetriever;
import org.parabuild.ci.statistics.HourlyPersistentTestStatsRetriever;
import org.parabuild.ci.statistics.MonthlyPersistentBuildStatsRetriever;
import org.parabuild.ci.statistics.MonthlyPersistentTestStatsRetriever;
import org.parabuild.ci.statistics.WeekDayBuildDistributionRetriever;
import org.parabuild.ci.statistics.YearlyPersistentBuildStatsRetriever;
import org.parabuild.ci.webui.common.WebuiUtils;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Implements Parabuild service
 *
 * @noinspection ParameterNameDiffersFromOverriddenParameter,UnnecessaryFullyQualifiedName,UnusedDeclaration
 */
public final class ParabuildSoapBindingImpl implements Parabuild {

  public void startBuild(final int buildID) throws RemoteException {
    // NOTE: vimeshev - currently we allow only admin to strat
    // the build, so we use admin user. In future we may have to
    // figure out how to pass a user name to the SOAP binding.
    final User adminUser = SecurityManager.getInstance().getUserByName(User.DEFAULT_ADMIN_USER);
    getBuildService(buildID).startBuild(new BuildStartRequest(adminUser.getUserID()));
  }


  public void startBuild(final int buildID, final org.parabuild.ci.webservice.BuildStartRequest buildStartRequest) throws RemoteException {
    getBuildService(buildID).startBuild(toBuildStartRequest(buildStartRequest));
  }


  private static BuildStartRequest toBuildStartRequest(final org.parabuild.ci.webservice.BuildStartRequest buildStartRequest) {
    final AgentHost agentHost = buildStartRequest.getAgentHost();
    final int buildRunID = buildStartRequest.getBuildRunID();
    final int changeListID = buildStartRequest.getChangeListID();
    final String label = buildStartRequest.getLabel();
    final String note = buildStartRequest.getNote();
    final org.parabuild.ci.webservice.BuildStartRequestParameter[] parameterList = buildStartRequest.getParameterList();
    final int requestType = buildStartRequest.getRequestType();
    final SourceControlSettingOverride[] sourceControlSettingOverrides = buildStartRequest.getSourceControlSettingsOverrides();
    final int userID = buildStartRequest.getUserID() == -1 ? SecurityManager.getInstance().getUserByName(User.DEFAULT_ADMIN_USER).getUserID() : buildStartRequest.getUserID();
    final int versionCounter = buildStartRequest.getVersionCounter();
    final String versionTemplate = buildStartRequest.getVersionTemplate();
    final boolean pinResult = buildStartRequest.isPinResult();
    final boolean cleanCheckout = buildStartRequest.isCleanCheckout();
    final boolean ignoreSerialization = buildStartRequest.isIgnoreSerialization();

    // Convert to the parameter list
    final List paramList = new ArrayList(5);
    if (parameterList != null) {
      for (int i = 0; i < parameterList.length; i++) {
        final org.parabuild.ci.webservice.BuildStartRequestParameter parameter = parameterList[i];
        paramList.add(new BuildStartRequestParameter(parameter.getVariableName(), parameter.getDescription(), parameter.getVariableValues(), i));
      }
    }

    // Converts an array of SourceControlSettingOverride to a list of SourceControlSettingVO
    final ArrayList sourceControlSettingsOverrides = new ArrayList(3);
    if (sourceControlSettingOverrides != null) {
      for (int i = 0; i < sourceControlSettingOverrides.length; i++) {
        final SourceControlSettingOverride override = sourceControlSettingOverrides[i];
        sourceControlSettingsOverrides.add(new SourceControlSettingVO(override.getName(), override.getValue()));
      }
    }

    //noinspection NumericCastThatLosesPrecision
    final BuildStartRequest result = new BuildStartRequest((byte) requestType, userID, changeListID, buildRunID, paramList,
            label, note, pinResult, versionTemplate, versionCounter, sourceControlSettingsOverrides);
    result.setCleanCheckout(cleanCheckout);
    result.setIgnoreSerialization(ignoreSerialization);
    if (agentHost != null) {
      result.setAgentHost(new org.parabuild.ci.configuration.AgentHost(agentHost.getHost()));
    }
    return result;
  }


  public void stopBuild(final int buildID) throws RemoteException {
    getBuildService(buildID).stopBuild(-1);
  }


  public void resumeBuild(final int buildID) throws RemoteException {
    getBuildService(buildID).resumeBuild();
  }


  public void requestCleanCheckout(final int buildID) throws RemoteException {
    final BuildService buildService = getBuildService(buildID);
    buildService.requestCleanCheckout();
  }


  /**
   * Helper method to return build service.
   *
   * @param buildID
   * @return return build service.
   */
  private static BuildService getBuildService(final int buildID) throws ServerException {
    final BuildListService buildListService = ServiceManager.getInstance().getBuildListService();
    final BuildService build = buildListService.getBuild(buildID);
    if (build == null) {
      throw new ServerException("Requested build ID \"" + buildID + "\" not found.");
    }
    return build;
  }


  /**
   * @return
   */
  public BuildStatus[] getCurrentBuildStatuses() {
    final BuildListService listService = ServiceManager.getInstance().getBuildListService();
    final List list = listService.getCurrentBuildStatuses();
    final BuildStatus[] result = new BuildStatus[list.size()];
    for (int i = 0; i < list.size(); i++) {
      result[i] = toBuildStatus((BuildState) list.get(i));
    }
    return result;
  }


  /**
   * @return
   */
  public BuildStatus[] findCurrentBuildStatuses(final String regularExpression) {
    final Pattern pattern = Pattern.compile(regularExpression);
    final BuildListService listService = ServiceManager.getInstance().getBuildListService();
    final List statuses = listService.getCurrentBuildStatuses();
    final List list = new ArrayList(11);
    for (int i = 0; i < statuses.size(); i++) {
      final BuildState buildState = (BuildState) statuses.get(i);
      if (pattern.matcher(buildState.getBuildName()).matches()) {
        list.add(toBuildStatus(buildState));
      }
    }
    final BuildStatus[] result = new BuildStatus[list.size()];
    for (int i = 0; i < list.size(); i++) {
      result[i] = toBuildStatus((BuildState) list.get(i));
    }
    return result;
  }


  /**
   * @return
   */
  public BuildStatus getCurrentBuildStatus(final String buildName) {
    final BuildListService listService = ServiceManager.getInstance().getBuildListService();
    final List list = listService.getCurrentBuildStatuses();
    for (int i = 0; i < list.size(); i++) {
      final BuildState o = (BuildState) list.get(i);
      if (o.getBuildName().equals(buildName)) {
        return toBuildStatus(o);
      }
    }
    return null;
  }


  /**
   * @return
   */
  public BuildStatus getCurrentBuildStatus(final int activeBuildID) {
    final BuildListService listService = ServiceManager.getInstance().getBuildListService();
    final BuildService service = listService.getBuild(activeBuildID);
    final BuildState state = service.getBuildState();
    return toBuildStatus(state);
  }


  public String serverVersion() {
    return Version.versionToString(true);
  }


  /**
   * Returns system variables.
   *
   * @param type
   * @param ownerID
   * @return
   * @throws RemoteException
   * @noinspection UnnecessaryFullyQualifiedName
   */
  public StartParameter[] getVariables(final int type, final int ownerID) throws RemoteException {
    final List startParameters = ConfigurationManager.getInstance().getStartParameters(
            org.parabuild.ci.object.StartParameterType.byteToType((byte) type), ownerID);
    final StartParameter[] resultArray = new StartParameter[startParameters.size()];
    for (int i = 0; i < startParameters.size(); i++) {
      final org.parabuild.ci.object.StartParameter p = (org.parabuild.ci.object.StartParameter) startParameters.get(i);
      final StartParameter result = new StartParameter();
      result.setBuildID(p.getBuildID());
      result.setDescription(p.getDescription());
      result.setEnabled(p.isEnabled());
      result.setFirstValue(p.getFirstValue());
      result.setID(p.getID());
      result.setModifiable(p.isModifiable());
      result.setName(p.getName());
      result.setPresentation(p.getPresentation());
      result.setRequired(p.isRequired());
      result.setRuntimeValue(p.getRuntimeValue());
      result.setTimeStamp(p.getTimeStamp());
      result.setValue(p.getValue());
      resultArray[i] = result;
    }
    return resultArray;
  }


  public SystemProperty[] getSystemProperties() throws RemoteException {

    // Get list
    final List systemProperties = SystemConfigurationManagerFactory.getManager().getSystemProperties();

    // Create result
    final SystemProperty[] resultArray = new SystemProperty[systemProperties.size()];
    for (int i = 0; i < systemProperties.size(); i++) {
      final org.parabuild.ci.object.SystemProperty o = (org.parabuild.ci.object.SystemProperty) systemProperties.get(i);
      final SystemProperty property = new SystemProperty();
      property.setPropertyID(o.getPropertyID());
      property.setPropertyName(o.getPropertyName());
      property.setPropertyTimeStamp(o.getPropertyTimeStamp());
      property.setPropertyValue(o.getPropertyValue());
      resultArray[i] = property;
    }
    return resultArray;
  }


  public Project[] getProjects() throws RemoteException {
    final List projects = ProjectManager.getInstance().getProjects();
    final Project[] resultArray = new Project[projects.size()];
    for (int i = 0; i < projects.size(); i++) {
      final org.parabuild.ci.object.Project o = (org.parabuild.ci.object.Project) projects.get(i);
      final Project project = new Project();
      project.setDescription(o.getDescription());
      project.setID(o.getID());
      project.setKey(o.getKey());
      project.setName(o.getName());
      project.setTimeStamp(o.getTimeStamp());
      project.setType(o.getType());
      resultArray[i] = project;
    }
    return resultArray;
  }


  public GlobalVCSUserMap[] getGlobalVcsUserMap() throws RemoteException {
    final List list = GlobalVCSUserMapManager.getInstance().getAllMappings();
    final GlobalVCSUserMap[] resultArray = new GlobalVCSUserMap[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.GlobalVCSUserMap o = (org.parabuild.ci.object.GlobalVCSUserMap) list.get(i);
      final GlobalVCSUserMap userMap = new GlobalVCSUserMap();
      userMap.setDescription(o.getDescription());
      userMap.setEmail(o.getEmail());
      userMap.setID(o.getID());
      userMap.setVcsUserName(o.getVcsUserName());
      resultArray[i] = userMap;
    }
    return resultArray;
  }


  /**
   * @noinspection ZeroLengthArrayAllocation
   */
  public ProjectAttribute[] getProjectAttributes(final int projectID) throws RemoteException {
    return new ProjectAttribute[0];
  }


  public ProjectBuild[] getProjectBuilds(final int projectID) throws RemoteException {
    final List builds = ProjectManager.getInstance().getProjectBuilds(projectID);
    final ProjectBuild[] resultArray = new ProjectBuild[builds.size()];
    for (int i = 0; i < builds.size(); i++) {
      final org.parabuild.ci.object.ProjectBuild o = (org.parabuild.ci.object.ProjectBuild) builds.get(i);
      final ProjectBuild projectBuild = new ProjectBuild(o.getID(), o.getActiveBuildID(), o.getProjectID());
      resultArray[i] = projectBuild;
    }
    return resultArray;
  }


  public DisplayGroup[] getDisplayGroups() throws RemoteException {
    final List list = DisplayGroupManager.getInstance().getAllDisplayGroups();
    final DisplayGroup[] resultArray = new DisplayGroup[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.DisplayGroup o = (org.parabuild.ci.object.DisplayGroup) list.get(i);
      final DisplayGroup displayGroup = new DisplayGroup(o.getID(), o.getDescription(), o.isEnabled(), o.getName(), o.getTimeStamp());
      resultArray[i] = displayGroup;
    }
    return resultArray;
  }


  public DisplayGroupBuild[] getDisplayGroupBuilds(final int displayGroupID) throws RemoteException {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final List groupBuilds = DisplayGroupManager.getInstance().getDisplayGroupBuilds(displayGroupID);
    final DisplayGroupBuild[] result = new DisplayGroupBuild[groupBuilds.size()];
    for (int i = 0; i < groupBuilds.size(); i++) {
      final org.parabuild.ci.object.DisplayGroupBuild o = (org.parabuild.ci.object.DisplayGroupBuild) groupBuilds.get(i);
      // Filter out deleted builds
      if (cm.getExistingBuildConfig(o.getBuildID()) == null) {
        continue;
      }
      result[i] = new DisplayGroupBuild(o.getID(), o.getBuildID(), o.getDisplayGroupID(), o.getTimeStamp());
    }
    return result;
  }


  public BuildFarmConfiguration[] getBuildFarmConfigurations() throws RemoteException {
    final List builders = BuilderConfigurationManager.getInstance().getBuilders();
    final BuildFarmConfiguration[] result = new BuildFarmConfiguration[builders.size()];
    for (int i = 0; i < builders.size(); i++) {
      final org.parabuild.ci.object.BuilderConfiguration o = (org.parabuild.ci.object.BuilderConfiguration) builders.get(i);
      final BuildFarmConfiguration configuration = new BuildFarmConfiguration();
      configuration.setDescription(o.getDescription());
      configuration.setEnabled(o.isEnabled());
      configuration.setID(o.getID());
      configuration.setName(o.getName());
      configuration.setTimeStamp(o.getTimeStamp());
      result[i] = configuration;
    }
    return result;
  }


  /**
   * @noinspection ZeroLengthArrayAllocation
   */
  public BuildFarmConfigurationAttribute[] getBuildFarmConfigurationAttributes(final int in0) throws RemoteException {
    return new BuildFarmConfigurationAttribute[0];
  }


  public BuildFarmAgent[] getBuildFarmAgents(final int builderID) throws RemoteException {
    final List agents = BuilderConfigurationManager.getInstance().getBuilderAgents(builderID);
    final BuildFarmAgent[] result = new BuildFarmAgent[agents.size()];
    for (int i = 0; i < agents.size(); i++) {
      final org.parabuild.ci.object.BuilderAgent o = (org.parabuild.ci.object.BuilderAgent) agents.get(i);
      final BuildFarmAgent agent = new BuildFarmAgent(o.getID(), o.getAgentID(), o.getTimeStamp(), o.getBuilderID());
      result[i] = agent;
    }
    return result;
  }


  public AgentConfiguration[] getAgentConfigurations() throws RemoteException {
    final List agents = BuilderConfigurationManager.getInstance().getAgentList();
    final AgentConfiguration[] result = new AgentConfiguration[agents.size()];
    for (int i = 0; i < agents.size(); i++) {
      final AgentConfig o = (AgentConfig) agents.get(i);
      final AgentConfiguration config = new AgentConfiguration();
      config.setDescription(o.getDescription());
      config.setEnabled(o.isEnabled());
      config.setHost(o.getHost());
      config.setID(o.getID());
      config.setLocal(o.isLocal());
      config.setTimeStamp(o.getTimeStamp());
      result[i] = config;
    }
    return result;
  }


  public AgentConfiguration getAgentConfiguration(final int in0) throws RemoteException {
    final AgentConfig o = BuilderConfigurationManager.getInstance().getAgentConfig(in0);
    final AgentConfiguration config = new AgentConfiguration();
    config.setDescription(o.getDescription());
    config.setEnabled(o.isEnabled());
    config.setHost(o.getHost());
    config.setID(o.getID());
    config.setLocal(o.isLocal());
    config.setTimeStamp(o.getTimeStamp());
    return config;
  }


  /**
   * Returns a list of agent statuses.
   */
  public AgentStatus[] getAgentStatuses() {
    final List statuses = ServiceManager.getInstance().getAgentStatusMonitor().getStatuses();
    final AgentStatus[] result = new AgentStatus[statuses.size()];
    for (int i = 0; i < statuses.size(); i++) {
      final org.parabuild.ci.webui.agent.status.AgentStatus o = (org.parabuild.ci.webui.agent.status.AgentStatus) statuses.get(i);
      final AgentStatus agentStatus = new AgentStatus();
      agentStatus.setAgentID(o.getAgentID());
      agentStatus.setHostName(o.getHostName());
      agentStatus.setRemoteVersion(o.getRemoteVersion());
      agentStatus.setStatus(o.getActivityType());
      result[i] = agentStatus;
    }
    return result;
  }


  public BuildConfiguration[] getActiveBuildConfigurations() throws RemoteException {
    final List configs = ConfigurationManager.getInstance().getExistingBuildConfigs();
    final BuildConfiguration[] result = new BuildConfiguration[configs.size()];
    for (int i = 0; i < configs.size(); i++) {
      final ActiveBuildConfig o = (ActiveBuildConfig) configs.get(i);
      final BuildConfiguration configuration = new BuildConfiguration();
      configuration.setAccess(o.getAccess());
      configuration.setActiveBuildID(o.getActiveBuildID());
      configuration.setFarmID(o.getBuilderID());
      configuration.setBuildID(o.getBuildID());
      configuration.setBuildName(o.getBuildName());
      configuration.setEmailDomain(o.getEmailDomain());
      configuration.setScheduleType(o.getScheduleType());
      configuration.setSourceControl(o.getSourceControl());
      configuration.setSourceControlEmail(o.getSourceControlEmail());
      configuration.setSubordinate(o.isSubordinate());
      result[i] = configuration;
    }
    return result;
  }


  public VersionControlSetting[] getVersionControlSettings(final int buildID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getSourceControlSettings(buildID);
    final VersionControlSetting[] result = new VersionControlSetting[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.SourceControlSetting o = (org.parabuild.ci.object.SourceControlSetting) list.get(i);
      final VersionControlSetting setting = new VersionControlSetting();
      setting.setBuildID(o.getBuildID());
      setting.setPropertyID(o.getPropertyID());
      setting.setPropertyTimeStamp(o.getPropertyTimeStamp());
      setting.setPropertyName(o.getPropertyName());
      setting.setPropertyValue(o.getPropertyValue());
      result[i] = setting;
    }
    return result;
  }


  public ScheduleProperty[] getScheduleProperties(final int buildID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getScheduleSettings(buildID);
    final ScheduleProperty[] result = new ScheduleProperty[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.ScheduleProperty o = (org.parabuild.ci.object.ScheduleProperty) list.get(i);
      final ScheduleProperty property = new ScheduleProperty();
      property.setBuildID(o.getBuildID());
      property.setPropertyID(o.getPropertyID());
      property.setPropertyName(o.getPropertyName());
      property.setPropertyTimeStamp(o.getPropertyTimeStamp());
      property.setPropertyValue(o.getPropertyValue());
      result[i] = property;
    }
    return result;
  }


  public BuildConfigurationAttribute[] getBuildConfigurationAttributes(final int buildID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getBuildAttributes(buildID);
    final BuildConfigurationAttribute[] result = new BuildConfigurationAttribute[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final BuildConfigAttribute o = (BuildConfigAttribute) list.get(i);
      final BuildConfigurationAttribute attribute = new BuildConfigurationAttribute();
      attribute.setBuildID(o.getBuildID());
      attribute.setPropertyID(o.getPropertyID());
      attribute.setPropertyName(o.getPropertyName());
      attribute.setPropertyTimeStamp(o.getPropertyTimeStamp());
      attribute.setPropertyValue(o.getPropertyValue());
      result[i] = attribute;
    }
    return result;
  }


  public LabelProperty[] getLabelProperties(final int buildID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getLabelSettings(buildID);
    final LabelProperty[] result = new LabelProperty[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.LabelProperty o = (org.parabuild.ci.object.LabelProperty) list.get(i);
      final LabelProperty property = new LabelProperty();
      property.setBuildID(o.getBuildID());
      property.setPropertyID(o.getPropertyID());
      property.setPropertyName(o.getPropertyName());
      property.setPropertyTimeStamp(o.getPropertyTimeStamp());
      property.setPropertyValue(o.getPropertyValue());
      result[i] = property;
    }
    return result;
  }


  /**
   * @noinspection UnnecessaryFullyQualifiedName
   */
  public LogConfiguration[] getLogConfigurations(final int buildID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getLogConfigs(buildID);
    final LogConfiguration[] result = new LogConfiguration[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.LogConfig o = (org.parabuild.ci.object.LogConfig) list.get(i);
      final LogConfiguration config = new LogConfiguration();
      config.setBuildID(o.getBuildID());
      config.setDescription(o.getDescription());
      config.setID(o.getID());
      config.setPath(o.getPath());
      config.setTimeStamp(o.getTimeStamp());
      config.setType(o.getType());
      result[i] = config;
    }
    return result;
  }


  public LogConfigurationProperty[] getLogConfigurationProperties(final int buildID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getLogConfigProperties(buildID);
    final LogConfigurationProperty[] result = new LogConfigurationProperty[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final LogConfigProperty o = (LogConfigProperty) list.get(i);
      final LogConfigurationProperty property = new LogConfigurationProperty();
      property.setLogConfigID(o.getLogConfigID());
      property.setID(o.getID());
      property.setName(o.getName());
      property.setTimeStamp(o.getTimeStamp());
      property.setValue(o.getValue());
      result[i] = property;
    }
    return result;
  }


  public VCSUserToEmailMap[] getVCSUserToEmailMap(final int buildID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getVCSUserToEmailMaps(buildID);
    final VCSUserToEmailMap[] result = new VCSUserToEmailMap[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.VCSUserToEmailMap o = (org.parabuild.ci.object.VCSUserToEmailMap) list.get(i);
      final VCSUserToEmailMap map = new VCSUserToEmailMap();
      map.setBuildID(o.getBuildID());
      map.setDisabled(o.getDisabled());
      map.setInstantMessengerAddress(o.getInstantMessengerAddress());
      map.setInstantMessengerType(o.getInstantMessengerType());
      map.setMapID(o.getMapID());
      map.setTimeStamp(o.getTimeStamp());
      map.setUserEmail(o.getUserEmail());
      map.setUserName(o.getUserName());
      result[i] = map;
    }
    return result;
  }


  public BuildWatcher[] getBuildWatchers(final int buildID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getWatchers(buildID);
    final BuildWatcher[] result = new BuildWatcher[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.BuildWatcher o = (org.parabuild.ci.object.BuildWatcher) list.get(i);
      final BuildWatcher watcher = new BuildWatcher();
      watcher.setBuildID(o.getBuildID());
      watcher.setDisabled(o.getDisabled());
      watcher.setInstantMessengerAddress(o.getInstantMessengerAddress());
      watcher.setInstantMessengerType(o.getInstantMessengerType());
      watcher.setTimeStamp(o.getTimeStamp());
      watcher.setEmail(o.getEmail());
      watcher.setLevel(o.getLevel());
      watcher.setWatcherID(o.getWatcherID());
      result[i] = watcher;
    }
    return result;
  }


  public BuildSequence[] getBuildSequence(final int buildID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getAllBuildSequences(buildID, BuildStepType.BUILD);
    final BuildSequence[] result = new BuildSequence[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.BuildSequence o = (org.parabuild.ci.object.BuildSequence) list.get(i);
      final BuildSequence sequence = new BuildSequence();
      sequence.setBuildID(o.getBuildID());
      sequence.setContinueOnFailure(o.isContinueOnFailure());
      sequence.setDisabled(o.isDisabled());
      sequence.setFailurePatterns(o.getFailurePatterns());
      sequence.setFinalizer(o.isFinalizer());
      sequence.setInitializer(o.isInitializer());
      sequence.setLineNumber(o.getLineNumber());
      sequence.setRespectErrorCode(o.getRespectErrorCode());
      sequence.setScriptText(o.getScriptText());
      sequence.setSequenceID(o.getSequenceID());
      sequence.setStepName(o.getStepName());
      sequence.setSuccessPatterns(o.getSuccessPatterns());
      sequence.setTimeoutMins(o.getTimeoutMins());
      sequence.setTimeStamp(o.getTimeStamp());
      sequence.setType(o.getType());
      result[i] = sequence;
    }
    return result;
  }


  public ScheduleItem[] getScheduleItem(final int buildID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getScheduleItems(buildID);
    final ScheduleItem[] result = new ScheduleItem[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.ScheduleItem o = (org.parabuild.ci.object.ScheduleItem) list.get(i);
      final ScheduleItem item = new ScheduleItem();
      item.setBuildID(o.getBuildID());
      item.setCleanCheckout(o.isCleanCheckout());
      item.setDayOfMonth(o.getDayOfMonth());
      item.setDayOfWeek(o.getDayOfWeek());
      item.setHour(o.getHour());
      item.setRunIfNoChanges(o.isRunIfNoChanges());
      item.setScheduleItemID(o.getScheduleItemID());
      item.setTimeStamp(o.getTimeStamp());
      result[i] = item;
    }
    return result;
  }


  public IssueTracker[] getIssueTracker(final int buildID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getIssueTrackers(buildID);
    final IssueTracker[] result = new IssueTracker[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.IssueTracker o = (org.parabuild.ci.object.IssueTracker) list.get(i);
      final IssueTracker tracker = new IssueTracker();
      tracker.setBuildID(o.getBuildID());
      tracker.setID(o.getID());
      tracker.setTimeStamp(o.getTimeStamp());
      tracker.setType(o.getType());
      result[i] = tracker;
    }
    return result;
  }


  public IssueTrackerProperty[] getIssueTrackerProperties(final int buildID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getIssueTrackerProperties(buildID);
    final IssueTrackerProperty[] result = new IssueTrackerProperty[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.IssueTrackerProperty o = (org.parabuild.ci.object.IssueTrackerProperty) list.get(i);
      final IssueTrackerProperty property = new IssueTrackerProperty();
      property.setID(o.getID());
      property.setTrackerID(o.getTrackerID());
      property.setName(o.getName());
      property.setTimeStamp(o.getTimeStamp());
      property.setValue(o.getValue());
      result[i] = property;
    }
    return result;
  }


  public BuildRun getBuildRun(final int buildRunID) throws RemoteException {
    return toBuildRun(ConfigurationManager.getInstance().getBuildRun(buildRunID));
  }


  public int getBuildRunCount(final int activeBuildID) throws RemoteException {
    return ConfigurationManager.getInstance().getCompletedBuildRunsCount(activeBuildID);
  }


  public BuildRun[] getCompletedBuildRuns(final int activeBuildID, final int firstResult, final int maxCount) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getCompletedBuildRuns(activeBuildID, firstResult, maxCount);
    final BuildRun[] result = new BuildRun[list.size()];
    for (int i = 0; i < list.size(); i++) {
      result[i] = toBuildRun((org.parabuild.ci.object.BuildRun) list.get(i));
    }
    return result;
  }


  public BuildRun getLastSuccessfulBuildRun(final int activeBuildID) throws RemoteException {
    return toBuildRun(ConfigurationManager.getInstance().getLastCleanBuildRun(activeBuildID));
  }


  /**
   * Returns an array of last successful BuildRuns that match given display group ID and change list number.
   *
   * @param displayGroupID   display group ID
   * @param changeListNumber change list number
   * @return array of last successful BuildRuns that matche given display group ID and change list number or an empty array if no matching builds exist.
   * @throws RemoteException
   */
  public BuildRun[] findlLastSuccessfulBuildRuns(final int displayGroupID, final int changeListNumber) throws RemoteException {
    final List list = ConfigurationManager.getInstance().findLastCleanBuildRuns(displayGroupID, changeListNumber);
    final BuildRun[] result = new BuildRun[list.size()];
    for (int i = 0; i < list.size(); i++) {
      result[i] = toBuildRun((org.parabuild.ci.object.BuildRun) list.get(i));
    }
    return result;
  }


  public org.parabuild.ci.webservice.BuildRunAttribute[] getBuildRunAttributes(final int buildRunID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getBuildRunAttributes(buildRunID);
    final org.parabuild.ci.webservice.BuildRunAttribute[] result = new org.parabuild.ci.webservice.BuildRunAttribute[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.BuildRunAttribute o = (org.parabuild.ci.object.BuildRunAttribute) list.get(i);
      final org.parabuild.ci.webservice.BuildRunAttribute bra = new org.parabuild.ci.webservice.BuildRunAttribute();
      bra.setBuildRunID(o.getBuildRunID());
      bra.setID(o.getID());
      bra.setName(o.getName());
      bra.setTimeStamp(o.getTimeStamp());
      bra.setValue(o.getValue());
      result[i] = bra;
    }
    return result;
  }


  public ChangeList[] getBuildRunParticipants(final int buildRunID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getBuildRunParticipants(buildRunID);
    final ChangeList[] result = new ChangeList[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.ChangeList o = (org.parabuild.ci.object.ChangeList) list.get(i);
      final ChangeList changeList = toChangeList(o);
      result[i] = changeList;
    }
    return result;
  }


  public Change[] getChanges(final int changeListID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getChanges(changeListID);
    final Change[] result = new Change[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.Change o = (org.parabuild.ci.object.Change) list.get(i);
      final Change change = new Change();
      change.setChangeID(o.getChangeID());
      change.setChangeListID(o.getChangeListID());
      change.setChangeType(o.getChangeType());
      change.setFilePath(o.getFilePath());
      change.setRevision(o.getRevision());
      result[i] = change;
    }
    return result;
  }


  public StepRun[] getStepRuns(final int buildRunID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getStepRuns(buildRunID);
    final StepRun[] result = new StepRun[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.StepRun o = (org.parabuild.ci.object.StepRun) list.get(i);
      final StepRun stepRun = new StepRun();
      stepRun.setBuildRunID(o.getBuildRunID());
      stepRun.setComplete(o.isComplete());
      stepRun.setDuration(o.getDuration());
      stepRun.setFinishedAt(dateToCalendar(o.getFinishedAt()));
      stepRun.setID(o.getID());
      stepRun.setName(o.getName());
      stepRun.setResultDescription(o.getResultDescription());
      stepRun.setResultID(o.getResultID());
      stepRun.setStartedAt(dateToCalendar(o.getStartedAt()));
      stepRun.setSuccessful(o.isSuccessful());
      stepRun.setTimeStamp(o.getTimeStamp());
      result[i] = stepRun;
    }
    return result;
  }


  public StepRunAttribute[] getStepRunRunAttributes(final int stepRunID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getStepRunAttributes(stepRunID);
    final StepRunAttribute[] result = new StepRunAttribute[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.StepRunAttribute o = (org.parabuild.ci.object.StepRunAttribute) list.get(i);
      final StepRunAttribute attribute = new StepRunAttribute();
      attribute.setID(o.getID());
      attribute.setStepRunID(o.getStepRunID());
      attribute.setName(o.getName());
      attribute.setTimeStamp(o.getTimeStamp());
      attribute.setValue(o.getValue());
      result[i] = attribute;
    }
    return result;
  }


  public StepLog[] getStepLogs(final int stepRunID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getAllStepLogs(stepRunID);
    final StepLog[] result = new StepLog[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.StepLog o = (org.parabuild.ci.object.StepLog) list.get(i);
      final StepLog stepLog = new StepLog();
      stepLog.setArchiveFileName(o.getArchiveFileName());
      stepLog.setDescription(o.getDescription());
      stepLog.setFound(o.getFound());
      stepLog.setID(o.getID());
      stepLog.setPath(o.getPath());
      stepLog.setPathType(o.getPathType());
      stepLog.setStepRunID(o.getStepRunID());
      stepLog.setTimeStamp(o.getTimeStamp());
      stepLog.setType(o.getType());
      result[i] = stepLog;
    }
    return result;
  }


  public StepResult[] getStepResults(final int stepRunID) throws RemoteException {
    try {
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final org.parabuild.ci.object.StepRun stepRun = cm.getStepRun(stepRunID);
      final int buildRunID = stepRun.getBuildRunID();
      final org.parabuild.ci.object.BuildRun buildRun = cm.getBuildRun(buildRunID);
      final int activeBuildID = buildRun.getActiveBuildID();
      final List list = cm.getAllStepResults(stepRunID);
      final StepResult[] result = new StepResult[list.size()];
      for (int i = 0; i < list.size(); i++) {
        final org.parabuild.ci.object.StepResult o = (org.parabuild.ci.object.StepResult) list.get(i);
        final StepResult stepResult = new StepResult();
        stepResult.setArchiveFileName(o.getArchiveFileName());
        stepResult.setDescription(o.getDescription());
        stepResult.setID(o.getID());
        stepResult.setPath(o.getPath());
        stepResult.setPathType(o.getPathType());
        stepResult.setStepRunID(o.getStepRunID());
        stepResult.setFound(o.isFound());
        stepResult.setPinned(o.isPinned());
        // Get result urls
        final List urlList = new ArrayList(11);
        final byte pathType = stepResult.getPathType();
        if (pathType == org.parabuild.ci.object.StepResult.PATH_TYPE_DIR
                || pathType == org.parabuild.ci.object.StepResult.PATH_TYPE_SINGLE_FILE) {
          final List entries = ArchiveManagerFactory.getArchiveManager(activeBuildID).getArchivedResultEntries(o);
          for (final Iterator j = entries.iterator(); j.hasNext();) {
            final ArchiveEntry archiveEntry = (ArchiveEntry) j.next();
            urlList.add(WebuiUtils.makeBuildResultURL(activeBuildID, stepResult.getID(), archiveEntry.getEntryName()));
          }
        } else if (pathType == org.parabuild.ci.object.StepResult.PATH_TYPE_EXTERNAL_URL) {
          urlList.add(stepResult.getPath());
        }
        stepResult.setUrls((String[]) urlList.toArray(new String[0]));

        result[i] = stepResult;
      }
      return result;
    } catch (final IOException e) {
      throw new RemoteException(e.toString(), e);
    }
  }


  public ChangeList getChangeList(final int changeListID) throws RemoteException {
    return toChangeList(ConfigurationManager.getInstance().getChangeList(changeListID));
  }


  public Issue getIssue(final int issueID) throws RemoteException {
    final org.parabuild.ci.object.Issue o = ConfigurationManager.getInstance().getIssue(issueID);
    if (o == null) {
      return null;
    }
    final Issue issue = new Issue();
    issue.setClosed(dateToCalendar(o.getClosed()));
    issue.setClosedBy(o.getClosedBy());
    issue.setDescription(o.getDescription());
    issue.setID(o.getID());
    issue.setKey(o.getKey());
    issue.setPriority(o.getPriority());
    issue.setProduct(o.getProduct());
    issue.setProject(o.getProject());
    issue.setReceived(dateToCalendar(o.getReceived()));
    issue.setStatus(o.getStatus());
    issue.setTrackerType(o.getTrackerType());
    issue.setUrl(o.getUrl());
    issue.setVersion(o.getVersion());
    return issue;
  }


  /**
   * @noinspection ZeroLengthArrayAllocation
   */
  public IssueAttribute[] getIssueAttributes(final int issueID) throws RemoteException {
    return new IssueAttribute[0];
  }


  public IssueChangeList[] getIssueChangeLists(final int issueID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getIssueChangeLists(issueID);
    final IssueChangeList[] result = new IssueChangeList[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.IssueChangeList o = (org.parabuild.ci.object.IssueChangeList) list.get(i);
      final IssueChangeList issueChangeList = new IssueChangeList();
      issueChangeList.setChangeListID(o.getChangeListID());
      issueChangeList.setID(o.getID());
      issueChangeList.setIssueID(o.getIssueID());
      result[i] = issueChangeList;
    }
    return result;
  }


  public ReleaseNote[] getReleaseNotes(final int buildRunID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getBuildRunReleaseNotes(buildRunID);
    final ReleaseNote[] result = new ReleaseNote[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.ReleaseNote o = (org.parabuild.ci.object.ReleaseNote) list.get(i);
      final ReleaseNote releaseNote = new ReleaseNote();
      releaseNote.setBuildRunID(o.getBuildRunID());
      releaseNote.setID(o.getID());
      releaseNote.setIssueID(o.getIssueID());
      result[i] = releaseNote;
    }
    return result;
  }


  public ResultGroup[] getResultGroups() throws RemoteException {
    final List list = ResultGroupManager.getInstance().getResultGroups();
    final ResultGroup[] result = new ResultGroup[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.ResultGroup o = (org.parabuild.ci.object.ResultGroup) list.get(i);
      final ResultGroup resultGroup = new ResultGroup();
      resultGroup.setDescription(o.getDescription());
      resultGroup.setEnabled(o.isEnabled());
      resultGroup.setID(o.getID());
      resultGroup.setLastPublished(dateToCalendar(o.getLastPublished()));
      resultGroup.setName(o.getName());
      resultGroup.setTimeStamp(o.getTimeStamp());
      result[i] = resultGroup;
    }
    return result;
  }


  public ProjectResultGroup[] getProjectResultGroups(final int projectID) throws RemoteException {
    final List list = ProjectManager.getInstance().getProjectResultGroups(projectID);
    final ProjectResultGroup[] result = new ProjectResultGroup[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.ProjectResultGroup o = (org.parabuild.ci.object.ProjectResultGroup) list.get(i);
      final ProjectResultGroup resultGroup = new ProjectResultGroup();
      resultGroup.setID(o.getID());
      resultGroup.setProjectID(o.getProjectID());
      resultGroup.setResultGroupID(o.getResultGroupID());
      result[i] = resultGroup;
    }
    return result;
  }


  /**
   * @noinspection UnnecessaryFullyQualifiedName
   */
  public ResultConfiguration[] getResultConfigurations(final int buildID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getResultConfigs(buildID);
    final ResultConfiguration[] result = new ResultConfiguration[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.ResultConfig o = (org.parabuild.ci.object.ResultConfig) list.get(i);
      final ResultConfiguration config = new ResultConfiguration();
      config.setBuildID(o.getBuildID());
      config.setDescription(o.getDescription());
      config.setFailIfNotFound(o.isFailIfNotFound());
      config.setID(o.getID());
      config.setIgnoreTimestamp(o.isIgnoreTimestamp());
      config.setPath(o.getPath());
      config.setShellVariable(o.getShellVariable());
      config.setTimeStamp(o.getTimeStamp());
      config.setType(o.getType());
      result[i] = config;
    }
    return result;
  }


  public ResultConfigurationProperty[] getResultConfigurationProperties(final int resultConfigID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getResultConfigProperties(resultConfigID);
    final ResultConfigurationProperty[] result = new ResultConfigurationProperty[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final ResultConfigProperty o = (ResultConfigProperty) list.get(i);
      final ResultConfigurationProperty configProperty = new ResultConfigurationProperty();
      configProperty.setID(o.getID());
      configProperty.setName(o.getName());
      configProperty.setResultConfigID(o.getResultConfigID());
      configProperty.setTimeStamp(o.getTimeStamp());
      configProperty.setValue(o.getValue());
      result[i] = configProperty;
    }
    return result;
  }


  public PublishedStepResult[] getPublishedStepResults(final int buildRunID) throws RemoteException {
    final List list = ResultGroupManager.getInstance().getPublishedStepResults(buildRunID);
    final PublishedStepResult[] result = new PublishedStepResult[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.PublishedStepResult o = (org.parabuild.ci.object.PublishedStepResult) list.get(i);
      final PublishedStepResult publishedStepResult = new PublishedStepResult();
      publishedStepResult.setActiveBuildID(o.getActiveBuildID());
      publishedStepResult.setBuildName(o.getBuildName());
      publishedStepResult.setBuildRunDate(dateToCalendar(o.getBuildRunDate()));
      publishedStepResult.setBuildRunID(o.getBuildRunID());
      publishedStepResult.setBuildRunNumber(o.getBuildRunNumber());
      publishedStepResult.setDescription(o.getDescription());
      publishedStepResult.setID(o.getID());
      publishedStepResult.setPublishDate(dateToCalendar(o.getPublishDate()));
      publishedStepResult.setPublisherBuildRunID(o.getPublisherBuildRunID());
      publishedStepResult.setResultGroupID(o.getResultGroupID());
      publishedStepResult.setStepResultID(o.getStepResultID());
      result[i] = publishedStepResult;
    }
    return result;
  }


  public BuildRunAction[] getBuildRunActions(final int buildRunID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getBuildRunActions(buildRunID);
    final BuildRunAction[] result = new BuildRunAction[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.BuildRunAction o = (org.parabuild.ci.object.BuildRunAction) list.get(i);
      final BuildRunAction action = new BuildRunAction();
      action.setAction(o.getAction());
      action.setBuildRunID(o.getBuildRunID());
      action.setCode(o.getCode());
      action.setDate(dateToCalendar(o.getDate()));
      action.setDescription(o.getDescription());
      action.setID(o.getID());
      action.setUserID(o.getUserID());
      result[i] = action;
    }
    return result;
  }


  public TestSuiteName[] getTestSuiteNames() throws RemoteException {
    final List list = ConfigurationManager.getInstance().getTestSuiteNames();
    final TestSuiteName[] result = new TestSuiteName[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.TestSuiteName o = (org.parabuild.ci.object.TestSuiteName) list.get(i);
      final TestSuiteName suiteName = new TestSuiteName();
      suiteName.setID(o.getID());
      suiteName.setName(o.getName());
      result[i] = suiteName;
    }
    return result;
  }


  public TestCaseName[] getTestCaseNames(final int testSuiteNameID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getTestCaseNames(testSuiteNameID);
    final TestCaseName[] result = new TestCaseName[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.TestCaseName o = (org.parabuild.ci.object.TestCaseName) list.get(i);
      final TestCaseName testCaseName = new TestCaseName();
      testCaseName.setID(o.getID());
      testCaseName.setName(o.getName());
      testCaseName.setTestSuiteNameID(o.getTestSuiteNameID());
      result[i] = testCaseName;
    }
    return result;
  }


  public BuildRunTest[] getBuildRunTests(final int buildRunID) throws RemoteException {
    final List list = ConfigurationManager.getInstance().getBuildRunTests(buildRunID);
    final BuildRunTest[] result = new BuildRunTest[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final org.parabuild.ci.object.BuildRunTest o = (org.parabuild.ci.object.BuildRunTest) list.get(i);
      final BuildRunTest runTest = new BuildRunTest();
      runTest.setBroken(o.isBroken());
      runTest.setBrokenBuildRunCount(o.getBrokenBuildRunCount());
      runTest.setBrokenSinceBuildRunID(o.getBrokenSinceBuildRunID());
      runTest.setBuildRunID(o.getBuildRunID());
      runTest.setDurationMillis(o.getDurationMillis());
      runTest.setFix(o.isFix());
      runTest.setID(o.getID());
      runTest.setMessage(o.getMessage());
      runTest.setNewFailure(o.isNewFailure());
      runTest.setNewTest(o.isNewTest());
      runTest.setResultCode(o.getResultCode());
      runTest.setTestCaseNameID(o.getTestCaseNameID());
      result[i] = runTest;
    }
    return result;
  }


  public BuildStatistics[] getHourlyStatistics(final int activeBuildID, final Calendar fromDate, final Calendar toDate) throws RemoteException {
    final HourlyPersistentBuildStatsRetriever retriever = new HourlyPersistentBuildStatsRetriever(activeBuildID);
    final List list = retriever.getStatistics(fromDate.getTime(), toDate.getTime());
    final BuildStatistics[] result = new BuildStatistics[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final PersistentBuildStats o = (PersistentBuildStats) list.get(i);
      result[i] = toBuildStatistics(o);
    }
    return result;
  }


  public BuildStatistics[] getDailyStatistics(final int activeBuildID, final Calendar fromDate, final Calendar toDate) throws RemoteException {
    final DailyPersistentBuildStatsRetriever retriever = new DailyPersistentBuildStatsRetriever(activeBuildID);
    final List list = retriever.getStatistics(fromDate.getTime(), toDate.getTime());
    final BuildStatistics[] result = new BuildStatistics[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final PersistentBuildStats o = (PersistentBuildStats) list.get(i);
      result[i] = toBuildStatistics(o);
    }
    return result;
  }


  public BuildStatistics[] getMonthlyStatistics(final int activeBuildID, final Calendar fromDate, final Calendar toDate) throws RemoteException {
    final MonthlyPersistentBuildStatsRetriever retriever = new MonthlyPersistentBuildStatsRetriever(activeBuildID);
    final List list = retriever.getStatistics(fromDate.getTime(), toDate.getTime());
    final BuildStatistics[] result = new BuildStatistics[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final PersistentBuildStats o = (PersistentBuildStats) list.get(i);
      result[i] = toBuildStatistics(o);
    }
    return result;
  }


  public BuildStatistics[] getYearlyStatistics(final int activeBuildID, final Calendar fromDate, final Calendar toDate) throws RemoteException {
    final YearlyPersistentBuildStatsRetriever retriever = new YearlyPersistentBuildStatsRetriever(activeBuildID);
    final List list = retriever.getStatistics(fromDate.getTime(), toDate.getTime());
    final BuildStatistics[] result = new BuildStatistics[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final PersistentBuildStats o = (PersistentBuildStats) list.get(i);
      result[i] = toBuildStatistics(o);
    }
    return result;
  }


  public TestStatistics[] getHourlyTestStatistics(final int activeBuildID, final Calendar fromDate, final Calendar toDate, final byte testToolCode) throws RemoteException {
    final HourlyPersistentTestStatsRetriever retriever = new HourlyPersistentTestStatsRetriever(activeBuildID, testToolCode);
    final List list = retriever.getStatistics(fromDate.getTime(), toDate.getTime());
    final TestStatistics[] result = new TestStatistics[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final PersistentTestStats o = (PersistentTestStats) list.get(i);
      result[i] = toTestStatistics(o);
    }
    return result;
  }


  public TestStatistics[] getDailyTestStatistics(final int activeBuildID, final Calendar fromDate, final Calendar toDate, final byte testToolCode) throws RemoteException {
    final DailyPersistentTestStatsRetriever retriever = new DailyPersistentTestStatsRetriever(activeBuildID, testToolCode);
    final List list = retriever.getStatistics(fromDate.getTime(), toDate.getTime());
    final TestStatistics[] result = new TestStatistics[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final PersistentTestStats o = (PersistentTestStats) list.get(i);
      result[i] = toTestStatistics(o);
    }
    return result;
  }


  public TestStatistics[] getMonthlyTestStatistics(final int activeBuildID, final Calendar fromDate, final Calendar toDate, final byte testToolCode) throws RemoteException {
    final MonthlyPersistentTestStatsRetriever retriever = new MonthlyPersistentTestStatsRetriever(activeBuildID, testToolCode);
    final List list = retriever.getStatistics(fromDate.getTime(), toDate.getTime());
    final TestStatistics[] result = new TestStatistics[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final PersistentTestStats o = (PersistentTestStats) list.get(i);
      result[i] = toTestStatistics(o);
    }
    return result;
  }


  public BuildDistribution[] getHourlyBuildDistributions(final int activeBuildID) throws RemoteException {
    final HourlyBuildDistributionRetriever retriever = new HourlyBuildDistributionRetriever(activeBuildID);
    final List list = retriever.getDistribution();
    final BuildDistribution[] result = new BuildDistribution[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final PersistentDistribution o = (PersistentDistribution) list.get(i);
      result[i] = toDistribution(o);
    }
    return result;
  }


  public BuildDistribution[] getWeekdayBuildDistributions(final int activeBuildID) throws RemoteException {
    final WeekDayBuildDistributionRetriever retriever = new WeekDayBuildDistributionRetriever(activeBuildID);
    final List list = retriever.getDistribution();
    final BuildDistribution[] result = new BuildDistribution[list.size()];
    for (int i = 0; i < list.size(); i++) {
      final PersistentDistribution o = (PersistentDistribution) list.get(i);
      result[i] = toDistribution(o);
    }
    return result;
  }


  /**
   * Updates or creates version control settings.
   *
   * @param versionControlSettings version control settings to update or create.
   */
  public void updateVersionControlSettings(final VersionControlSetting[] versionControlSettings) {

    // Check parameter
    if (versionControlSettings == null) {
      return;
    }

    // Update settings
    final Set buildIDsToUpdate = new HashSet(11);
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    for (int i = 0; i < versionControlSettings.length; i++) {
      final VersionControlSetting versionControlSetting = versionControlSettings[i];
      final org.parabuild.ci.object.SourceControlSetting sourceControlSetting;
      if (versionControlSetting.getPropertyID() == -1) {
        sourceControlSetting = new org.parabuild.ci.object.SourceControlSetting();
      } else {
        sourceControlSetting = cm.getSourceControlSetting(versionControlSetting.getBuildID(), versionControlSetting.getPropertyName());
        if (sourceControlSetting == null) {
          throw new IllegalArgumentException("Version control setting not found: " + versionControlSetting.getPropertyID());
        }
      }
      sourceControlSetting.setBuildID(versionControlSetting.getBuildID());
      sourceControlSetting.setPropertyID(versionControlSetting.getPropertyID());
      sourceControlSetting.setPropertyName(versionControlSetting.getPropertyName());
      sourceControlSetting.setPropertyTimeStamp(versionControlSetting.getPropertyTimeStamp());
      sourceControlSetting.setPropertyValue(versionControlSetting.getPropertyValue());
      cm.saveObject(sourceControlSetting);
      buildIDsToUpdate.add(Integer.valueOf(versionControlSetting.getBuildID()));
    }

    // Notify about changes.
    notifyConfigurationChanged(buildIDsToUpdate);
  }


  /**
   * Updates or creates build sequence.
   *
   * @param buildSequences build sequence to update or create.
   */
  public void updateBuildSequences(final BuildSequence[] buildSequences) {

    // Check parameter
    if (buildSequences == null) {
      return;
    }

    // Update settings
    final Set buildIDsToUpdate = new HashSet(11);
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    for (int i = 0; i < buildSequences.length; i++) {
      final BuildSequence buildSequence = buildSequences[i];
      final org.parabuild.ci.object.BuildSequence o;
      if (buildSequence.getSequenceID() == -1) {
        o = new org.parabuild.ci.object.BuildSequence();
      } else {
        o = (org.parabuild.ci.object.BuildSequence) cm.getObject(org.parabuild.ci.object.BuildSequence.class, buildSequence.getSequenceID());
        if (o == null) {
          throw new IllegalArgumentException("Build sequence not found: " + buildSequence.getSequenceID());
        }
      }
      o.setBuildID(buildSequence.getBuildID());
      o.setContinueOnFailure(buildSequence.isContinueOnFailure());
      o.setDisabled(buildSequence.isDisabled());
      o.setFailurePatterns(buildSequence.getFailurePatterns());
      o.setFinalizer(buildSequence.isFinalizer());
      o.setInitializer(buildSequence.isInitializer());
      o.setLineNumber(buildSequence.getLineNumber());
      o.setRespectErrorCode(buildSequence.isRespectErrorCode());
      o.setScriptText(buildSequence.getScriptText());
      o.setSequenceID(buildSequence.getSequenceID());
      o.setStepName(buildSequence.getStepName());
      o.setSuccessPatterns(buildSequence.getSuccessPatterns());
      o.setTimeoutMins(buildSequence.getTimeoutMins());
      o.setTimeStamp(buildSequence.getTimeoutMins());
      o.setType(buildSequence.getType());
      cm.saveObject(o);
      buildIDsToUpdate.add(Integer.valueOf(buildSequence.getBuildID()));
    }

    // Notify about changes.
    notifyConfigurationChanged(buildIDsToUpdate);
  }


  /**
   * Updates or creates schedule properties.
   *
   * @param scheduleProperties schedule properties to update or create.
   */
  public void updateScheduleProperties(final ScheduleProperty[] scheduleProperties) {

    // Check parameter
    if (scheduleProperties == null) {
      return;
    }

    // Update settings
    final Set buildIDsToUpdate = new HashSet(11);
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    for (int i = 0; i < scheduleProperties.length; i++) {
      final ScheduleProperty scheduleProperty = scheduleProperties[i];
      final org.parabuild.ci.object.ScheduleProperty o;
      if (scheduleProperty.getPropertyID() == -1) {
        o = new org.parabuild.ci.object.ScheduleProperty();
      } else {
        o = cm.getScheduleSetting(scheduleProperty.getBuildID(), scheduleProperty.getPropertyName());
        if (o == null) {
          throw new IllegalArgumentException("Schedule property not found: " + scheduleProperty.getPropertyID());
        }
      }
      o.setBuildID(scheduleProperty.getBuildID());
      o.setPropertyID(scheduleProperty.getPropertyID());
      o.setPropertyName(scheduleProperty.getPropertyName());
      o.setPropertyTimeStamp(scheduleProperty.getPropertyTimeStamp());
      o.setPropertyValue(scheduleProperty.getPropertyValue());
      cm.saveObject(o);
      buildIDsToUpdate.add(Integer.valueOf(scheduleProperty.getBuildID()));
    }

    // Notify about changes.
    notifyConfigurationChanged(buildIDsToUpdate);
  }


  private static void notifyConfigurationChanged(final Set buildIDsToUpdate) {
    for (final Iterator iterator = buildIDsToUpdate.iterator(); iterator.hasNext();) {
      final Integer buildID = (Integer) iterator.next();
      ServiceManager.getInstance().getBuildListService().getBuild(buildID).notifyConfigurationChanged();
    }
  }


  private static BuildDistribution toDistribution(final PersistentDistribution o) {
    final BuildDistribution distribution = new BuildDistribution();
    distribution.setActiveBuildID(o.getActiveBuildID());
    distribution.setID(o.getID());
    distribution.setChangeListCount(o.getChangeListCount());
    distribution.setFailedBuildCount(o.getFailedBuildCount());
    distribution.setIssueCount(o.getIssueCount());
    distribution.setID(o.getID());
    distribution.setSuccessfulBuildCount(o.getSuccessfulBuildCount());
    distribution.setTarget(o.getTarget());
    distribution.setTotalBuildCount(o.getTotalBuildCount());
    return distribution;
  }


  private TestStatistics toTestStatistics(final PersistentTestStats o) {
    final TestStatistics statistics = new TestStatistics();
    statistics.setActiveBuildID(o.getActiveBuildID());
    statistics.setBuildCount(o.getBuildCount());
    statistics.setErrorTestCount(o.getErrorTestCount());
    statistics.setErrorTestPercent(o.getErrorTestPercent());
    statistics.setFailedTestCount(o.getFailedTestCount());
    statistics.setFailedTestPercent(o.getFailedTestPercent());
    statistics.setSampleTime(dateToCalendar(o.getSampleTime()));
    statistics.setSuccessfulTestCount(o.getSuccessfulTestCount());
    statistics.setSuccessfulTestPercent(o.getSuccessfulTestPercent());
    statistics.setTestCode(o.getTestCode());
    statistics.setTotalTestCount(o.getTotalTestCount());
    return statistics;
  }


  private BuildStatistics toBuildStatistics(final PersistentBuildStats o) {
    final BuildStatistics statistics = new BuildStatistics();
    statistics.setActiveBuildID(o.getActiveBuildID());
    statistics.setChangeListCount(o.getChangeListCount());
    statistics.setFailedBuildCount(o.getFailedBuildCount());
    statistics.setFailedBuildPercent(o.getFailedBuildPercent());
    statistics.setID(o.getID());
    statistics.setIssueCount(o.getIssueCount());
    statistics.setSampleTime(dateToCalendar(o.getSampleTime()));
    statistics.setSuccessfulBuildCount(o.getSuccessfulBuildCount());
    statistics.setSuccessfulBuildPercent(o.getSuccessfulBuildPercent());
    statistics.setTotalBuildCount(o.getTotalBuildCount());
    return statistics;
  }


  private BuildRun toBuildRun(final org.parabuild.ci.object.BuildRun o) {
    if (o == null) {
      return null;
    }
    final BuildRun buildRun = new BuildRun();
    buildRun.setActiveBuildID(o.getActiveBuildID());
    buildRun.setBuildID(o.getBuildID());
    buildRun.setBuildName(o.getBuildName());
    buildRun.setBuildRunID(o.getBuildRunID());
    buildRun.setBuildRunNumber(o.getBuildRunNumber());
    buildRun.setChangeListNumber(o.getChangeListNumber());
    buildRun.setComplete(o.getComplete());
    buildRun.setDependence(o.getDependence());
    buildRun.setFinishedAt(dateToCalendar(o.getFinishedAt()));
    buildRun.setLabel(o.getLabel());
    buildRun.setLabelNote(o.getLabelNote());
    buildRun.setLabelStatus(o.getLabelStatus());
    buildRun.setLastStepRunName(o.getLastStepRunName());
    buildRun.setManualLabel(o.getManualLabel());
    buildRun.setPhysicalChangeListNumber(o.isPhysicalChangeListNumber());
    buildRun.setReRun(o.isReRun());
    buildRun.setResultDescription(o.getResultDescription());
    buildRun.setResultID(o.getResultID());
    buildRun.setStartedAt(dateToCalendar(o.getStartedAt()));
    buildRun.setSyncNote(o.getSyncNote());
    buildRun.setTimeStamp(o.getTimeStamp());
    buildRun.setType(o.getType());
    return buildRun;
  }


  private ChangeList toChangeList(final org.parabuild.ci.object.ChangeList o) {
    if (o == null) {
      return null;
    }
    final ChangeList changeList = new ChangeList();
    changeList.setBranch(o.getBranch());
    changeList.setChangeListID(o.getChangeListID());
    changeList.setClient(o.getClient());
    changeList.setCreatedAt(dateToCalendar(o.getCreatedAt()));
    changeList.setDescription(o.getDescription());
    changeList.setEmail(o.getEmail());
    changeList.setNumber(o.getNumber());
    changeList.setOriginalSize(o.getOriginalSize());
    changeList.setUser(o.getUser());
    return changeList;
  }


  private static Calendar dateToCalendar(final Date date) {
    if (date == null) {
      return null;
    }
    final Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c;
  }


  private BuildStatus toBuildStatus(final BuildState o) {
    if (o == null) {
      return null;
    }
    final BuildStatus status = new BuildStatus();
    status.setAccess(o.getAccess());
    status.setActiveBuildID(o.getActiveBuildID());
    status.setBuildName(o.getBuildName());
    status.setCurrentlyRunnigBuildRunID(o.getCurrentlyRunningBuildRunID());
    status.setCurrentlyRunningBuildConfigID(o.getCurrentlyRunningBuildConfigID());
    status.setCurrentlyRunningBuildNumber(o.getCurrentlyRunningBuildNumber());
    status.setCurrentlyRunningChangeListNumber(o.getCurrentlyRunningChangeListNumber());
    status.setCurrentlyRunningOnBuildHost(o.getCurrentlyRunningOnBuildHost());
    status.setCurrentlyRunningStepID(o.getCurrentlyRunningStepID());
    status.setLastCleanBuildRunID(o.getLastCleanBuildRunID());
    status.setLastCompleteBuildRun(o.getLastCompleteBuildRunID());
    status.setNextBuildTime(dateToCalendar(o.getNextBuildTime()));
    status.setSchedule((byte) o.getSchedule());
    status.setSourceControl((byte) o.getSourceControl());
    status.setStatus(o.getStatus().byteValue());
    return status;
  }
}
