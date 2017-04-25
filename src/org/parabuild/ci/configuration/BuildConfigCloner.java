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
package org.parabuild.ci.configuration;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.BuildStatus;
import org.parabuild.ci.build.SystemVariableConfigurationManager;
import org.parabuild.ci.common.SourceControlSettingResolver;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.ActiveBuild;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.object.BuildWatcher;
import org.parabuild.ci.object.DisplayGroupBuild;
import org.parabuild.ci.object.GroupBuildAccess;
import org.parabuild.ci.object.IssueTracker;
import org.parabuild.ci.object.IssueTrackerProperty;
import org.parabuild.ci.object.LabelProperty;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.LogConfigProperty;
import org.parabuild.ci.object.ProjectBuild;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.ResultConfigProperty;
import org.parabuild.ci.object.ScheduleItem;
import org.parabuild.ci.object.ScheduleProperty;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SourceControlSettingVO;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.object.VCSUserToEmailMap;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.services.BuildListService;
import org.parabuild.ci.services.ServiceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Creates a copy of a given build configuration.
 *
 * @noinspection ProhibitedExceptionDeclared
 */
public final class BuildConfigCloner {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(BuildConfigCloner.class); // NOPMD

  private static final boolean COPY_ACTIVE_BUILD = true;
  private static final boolean NO_COPY_ACTIVE_BUILD = false;

  /**
   * This map contains a look up table with property name as a
   * key and String as value. The values should replace
   * SourceControlSettings in the resulting copy.
   */
  private final Map sourceControlSettingsOverwriteMap = new HashMap(3);


  public BuildConfigCloner() {
  }


  public BuildConfigCloner(final List sourceControlSettingsOverwriteList) {

    // init overrides map
    for (int i = 0, n = sourceControlSettingsOverwriteList.size(); i < n; i++) {
      final SourceControlSettingVO vo = (SourceControlSettingVO) sourceControlSettingsOverwriteList.get(i);
      sourceControlSettingsOverwriteMap.put(vo.getName(), vo.getValue());
    }
  }


  /**
   * Creates a copy of a given build configuration and starts up
   * a build. Cloned build will be in BuildStatus.INACTIVE state
   * - a build administrator will have to activate build
   * manually.
   *
   * @param sourceActiveBuildID buidl ID to clone.
   * @throws IllegalArgumentException if build config with this
   *                                  is not found or if sourceConfigID is not an active build.
   */
  public BuildConfig createActiveBuildConfig(final int sourceActiveBuildID) {
    final BuildConfig result = cloneData(sourceActiveBuildID, BuildConfig.UNSAVED_ID, COPY_ACTIVE_BUILD, "null");
    startNewBuildService();
    return result;
  }


  /**
   * Creates a copy of a given build configuration to use
   * as a build run configuration.
   *
   * @param sourceBuildID buidl ID to clone.
   * @param agentHostName
   * @throws IllegalArgumentException if build config with this
   *                                  is not found or if sourceBuildID is not an active
   *                                  build.
   */
  public BuildConfig createBuildRunConfig(final int sourceBuildID, final String agentHostName) {
    return cloneData(sourceBuildID, BuildConfig.UNSAVED_ID, NO_COPY_ACTIVE_BUILD, agentHostName);
  }


  /**
   * Creates a copy of a given build configuration to use as
   * a build run configuration. Uses given buildRunID as a
   * source of additional information. This method is used
   * to create build configurations for dependent parallel
   * runs.
   *
   * @param agentHostName
   * @param sourceBuildID buidl ID to clone.
   * @throws IllegalArgumentException if build config with this
   *                                  is not found or if sourceBuildID is not an active
   *                                  build.
   */
  public BuildConfig createBuildRunConfig(final int sourceBuildID, final int leadingBuildID, final String agentHostName) {
    return cloneData(sourceBuildID, leadingBuildID, NO_COPY_ACTIVE_BUILD, agentHostName);
  }


  /**
   * Starts new build service.
   */
  private void startNewBuildService() {
    final BuildListService buildListService = ServiceManager.getInstance().getBuildListService();
    buildListService.notifyConfigurationsChanged();
  }


  /**
   * Creates a copy of a given build configuration.
   *
   * @param agentHostName
   * @param leadingBuildID if this parameter contains
   *                       non-negative value it means that we are creating a
   *                       copy for a dependent parallel build.
   * @throws IllegalArgumentException if build config with this
   *                                  is not found or if sourceBuildID is not an active build.
   */
  private BuildConfig cloneData(final int sourceBuildID, final int leadingBuildID, final boolean copyActiveBuild, final String agentHostName) {
    return (BuildConfig) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {

        //noinspection ControlFlowStatementWithoutBraces
        if (LOG.isDebugEnabled()) LOG.debug("agentHostName at clone: " + agentHostName); // NOPMD

        final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();

        // validate
        final ConfigurationManager cm = ConfigurationManager.getInstance();
        final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
        final ProjectManager pm = ProjectManager.getInstance();

        // get build config
        final BuildConfig sourceBuildConfig = cm.getBuildConfiguration(sourceBuildID);
        if (sourceBuildConfig == null) {
          throw new IllegalArgumentException("Build configuration cannot be found: " + sourceBuildID);
        }

        // Check if this is a copy for old build
        final boolean sourceIsBuildRun = sourceBuildConfig.getBuildID() != sourceBuildConfig.getActiveBuildID();

        // create name
        String name = sourceBuildConfig.getBuildName();
        if (name.length() > 10) {
          name = name.substring(0, 9);
        }

        // copy header
        final BuildConfig resultBuildConfig;
        final int resultBuildID;
        if (copyActiveBuild) {
          // create active build
          resultBuildConfig = new ActiveBuildConfig(sourceBuildConfig);
          resultBuildID = cm.save(resultBuildConfig);
          resultBuildConfig.setBuildName(name + resultBuildID);
          resultBuildConfig.setActiveBuildID(resultBuildID);
          cm.save(resultBuildConfig);

          // get active build
          final ActiveBuild activeBuild = (ActiveBuild) cm.getObject(ActiveBuild.class, sourceBuildID);
          if (activeBuild == null) {
            throw new IllegalArgumentException("Active build configuration cannot be found: " + sourceBuildID);
          }

          // active build
          session.evict(activeBuild);
          activeBuild.setID(resultBuildID);
          activeBuild.setStartupStatus(BuildStatus.PAUSED_VALUE);
          activeBuild.setSequenceNumber(scm.incrementBuildSequenceNumber());
          session.save(activeBuild); // as AB has a BUILD ID as PK, we should use explicit save(...)

          // security - build access
          final List groupBuildAccesList = org.parabuild.ci.security.SecurityManager.getInstance().getGroupBuildAccessList(sourceBuildID);
          for (final Iterator i = groupBuildAccesList.iterator(); i.hasNext();) {
            final GroupBuildAccess groupBuildAccess = (GroupBuildAccess) i.next();
            session.evict(groupBuildAccess);
            groupBuildAccess.setBuildID(resultBuildID);
            session.save(groupBuildAccess);
          }


          // display groups connections
          final List displayGroupBuildList = DisplayGroupManager.getInstance().getDisplayGroupBuildsByBuildID(sourceBuildID);
          for (int i = 0; i < displayGroupBuildList.size(); i++) {
            final DisplayGroupBuild displayGroupBuild = (DisplayGroupBuild) displayGroupBuildList.get(i);
            session.evict(displayGroupBuild);
            displayGroupBuild.setID(DisplayGroupBuild.UNSAVED_ID);
            displayGroupBuild.setBuildID(resultBuildID);
            session.save(displayGroupBuild);
          }

          // project-build connection, new build belong to the same build
          final ProjectBuild projectBuild = pm.getProjectBuild(sourceBuildID);
          session.evict(projectBuild);
          projectBuild.setActiveBuildID(resultBuildID);
          session.save(projectBuild);

        } else {
          // create build run config
          resultBuildConfig = new BuildRunConfig(sourceBuildConfig);
          resultBuildID = cm.save(resultBuildConfig);
        }

        //
        // build attrs
        //
        final List buildAttributes = cm.getBuildAttributes(sourceBuildID);
        for (final Iterator i = buildAttributes.iterator(); i.hasNext();) {
          final BuildConfigAttribute buildConfigAttribute = (BuildConfigAttribute) i.next();
          // process
          session.evict(buildConfigAttribute);
          if (buildConfigAttribute.getPropertyName().equals(BuildConfigAttribute.SOURCE_BUILD_CONFIG_ID)
                  || buildConfigAttribute.getPropertyName().equals(BuildConfigAttribute.LAST_SAVED_TAB)) {
            continue;
          }
          buildConfigAttribute.setPropertyID(BuildConfigAttribute.UNSAVED_ID);
          buildConfigAttribute.setBuildID(resultBuildID);
          session.save(buildConfigAttribute);
        }
        session.save(new BuildConfigAttribute(resultBuildID, BuildConfigAttribute.SOURCE_BUILD_CONFIG_ID, sourceBuildID)); // source ID

        //
        // build sequences
        //
        processBuildSequences(session, cm.getAllBuildSequences(sourceBuildID, BuildStepType.BUILD), resultBuildID);
        processBuildSequences(session, cm.getAllBuildSequences(sourceBuildID, BuildStepType.PUBLISH), resultBuildID);

        //
        // issue trackers
        //
        final List issueTrackers = cm.getIssueTrackers(sourceBuildID);
        for (final Iterator i = issueTrackers.iterator(); i.hasNext();) {
          final IssueTracker issueTracker = (IssueTracker) i.next();
          final int issueTrakerIDToClone = issueTracker.getID();
          session.evict(issueTracker);
          issueTracker.setID(IssueTracker.UNSAVED_ID);
          issueTracker.setBuildID(resultBuildID);
          session.save(issueTracker);
          final List issueTrackerProperties = cm.getIssueTrackerProperties(issueTrakerIDToClone);
          for (final Iterator j = issueTrackerProperties.iterator(); j.hasNext();) {
            final IssueTrackerProperty issueTrackerProperty = (IssueTrackerProperty) j.next();
            session.evict(issueTrackerProperty);
            issueTrackerProperty.setID(IssueTrackerProperty.UNSAVED_ID);
            issueTrackerProperty.setTrackerID(issueTracker.getID());
            session.save(issueTrackerProperty);
          }
        }

        // build labels
        final List labelSettings = cm.getLabelSettings(sourceBuildID);
        for (final Iterator i = labelSettings.iterator(); i.hasNext();) {
          final LabelProperty labelProperty = (LabelProperty) i.next();
          session.evict(labelProperty);
          labelProperty.setPropertyID(LabelProperty.UNSAVED_ID);
          labelProperty.setBuildID(resultBuildID);
          session.save(labelProperty);
        }

        // build logs
        final List logConfigs = cm.getLogConfigs(sourceBuildID);
        for (final Iterator i = logConfigs.iterator(); i.hasNext();) {
          final LogConfig logConfig = (LogConfig) i.next();
          final int logConfigIDToClone = logConfig.getID();
          session.evict(logConfig);
          logConfig.setID(LogConfig.UNSAVED_ID);
          logConfig.setBuildID(resultBuildID);
          session.save(logConfig);
          final List logConfigProperties = cm.getLogConfigProperties(logConfigIDToClone);
          for (final Iterator j = logConfigProperties.iterator(); j.hasNext();) {
            final LogConfigProperty logConfigProperty = (LogConfigProperty) j.next();
            session.evict(logConfigProperty);
            logConfigProperty.setID(LogConfigProperty.UNSAVED_ID);
            logConfigProperty.setLogConfigID(logConfig.getID());
            session.save(logConfigProperty);
          }
        }

        // user maps
        final List maps = cm.getVCSUserToEmailMaps(sourceBuildID);
        for (final Iterator i = maps.iterator(); i.hasNext();) {
          final VCSUserToEmailMap map = (VCSUserToEmailMap) i.next();
          session.evict(map);
          map.setMapID(VCSUserToEmailMap.UNSAVED_ID);
          map.setBuildID(resultBuildID);
          session.save(map);
        }

        // schedule items
        final List scheduleItems = cm.getScheduleItems(sourceBuildID);
        for (final Iterator i = scheduleItems.iterator(); i.hasNext();) {
          final ScheduleItem scheduleItem = (ScheduleItem) i.next();
          session.evict(scheduleItem);
          scheduleItem.setBuildID(resultBuildID);
          scheduleItem.setScheduleItemID(ScheduleItem.UNSAVED_ID);
          session.save(scheduleItem);
        }

        // schedule settings
        final List scheduleSettings = cm.getScheduleSettings(sourceBuildID);
        for (final Iterator i = scheduleSettings.iterator(); i.hasNext();) {
          final ScheduleProperty scheduleProperty = (ScheduleProperty) i.next();
          session.evict(scheduleProperty);
          scheduleProperty.setPropertyID(ScheduleProperty.UNSAVED_ID);
          scheduleProperty.setBuildID(resultBuildID);
          session.save(scheduleProperty);
        }

        //
        // version control settings
        //
        final SourceControlSettingResolver sourceControlSettingResolver = new SourceControlSettingResolver(
                sourceBuildConfig.getBuildName(), sourceBuildConfig.getActiveBuildID(), agentHostName);
        final Map overwrittenSourceControlSettings = new HashMap(3);
        final boolean isReferenceSourceControl = sourceBuildConfig.getSourceControl() == BuildConfig.SCM_REFERENCE;
        final Query sourceControlSettingsQuery = session.createQuery("from SourceControlSetting as vcs where vcs.buildID = ?");
        sourceControlSettingsQuery.setInteger(0, sourceBuildID);
        sourceControlSettingsQuery.setCacheable(true);
        SourceControlSetting storageLocationCode = null;
        SourceControlSetting storageLocation = null;
        for (final Iterator i = sourceControlSettingsQuery.list().iterator(); i.hasNext();) {
          final SourceControlSetting sourceControlSetting = (SourceControlSetting) i.next();
          session.evict(sourceControlSetting);

          // handle ClearCase storage to avoid duplication of VWS path created by cloning
          if (copyActiveBuild) {

            // for ClearCase storage location code is switched to "Auto"
            if (sourceControlSetting.getPropertyName().equals(SourceControlSetting.CLEARCASE_VIEW_STORAGE_LOCATION_CODE)) {
              storageLocationCode = sourceControlSetting;
              continue; // process out of the cycle
            } else if (sourceControlSetting.getPropertyName().equals(SourceControlSetting.CLEARCASE_VIEW_STORAGE_LOCATION)) {
              storageLocation = sourceControlSetting;
              continue; // process out of the cycle
            } else if (sourceControlSetting.getPropertyName().equals(SourceControlSetting.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE)) {
              // check if it is a template.
              //
              // REVIEWME: vimeshev - 2007-01-21 - we assume
              // that templates produce non-intersecting
              // paths. This maybe wrong.
              if (!StringUtils.isBlank(sourceControlSetting.getPropertyValue())) {
                // trick: template will produce a value different from self
                final String testValue;
                try {
                  testValue = sourceControlSettingResolver.resolve(sourceControlSetting.getPropertyValue().trim());
                } catch (ValidationException e) {
                  //
                  // Invalid template, report and skip
                  //
                  // NOTE: simeshev@parabuildci.org -> 2009-05-12 - This may fail because a template may use a
                  // per-agent parameter, and it is not set in the sourceControlSettingResolver.
                  final Error error = new Error("Ignored invalid custom checkout dir template", e);
                  error.setSendEmail(false);
                  error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
                  errorManager.reportSystemError(error);
                  continue;
                }
                if (sourceControlSetting.getPropertyValue().equals(testValue)) {
                  // same value, hence not a template, skip
                  continue;
                }
              }
            }
          } else {
            // Not copy an active build

            // advanced handling for reference builds.
            //if (log.isDebugEnabled()) log.debug("isReferenceSourceControl: " + isReferenceSourceControl);
            //if (log.isDebugEnabled()) log.debug("copyActiveBuild: " + copyActiveBuild);
            if (isReferenceSourceControl && sourceControlSetting.getPropertyName().equals(SourceControlSetting.REFERENCE_BUILD_ID)) {

              // scheduled and parallel builds handle reference IDs differently
              if (sourceBuildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_RECURRENT) {

                // If the source is a build run config, this means that we are creating
                // a copy for a re-run, so don't do anything in that situation.
                if (!sourceIsBuildRun) {
                  // NOTE: vimeshev - 04/12/2005 - we have to find latest clean run
                  // configuration and override it because we are creating a build
                  // run configuration for a build with reference source control.

                  // get last clean build run for a build backing the source build.
                  final BuildRun lastCleanBackingBuildRun = cm.getLastCleanBuildRun(sourceControlSetting.getPropertyValueAsInt());
                  if (lastCleanBackingBuildRun == null) {
                    throw new IllegalStateException("Attempted to create reference configuration without backing clean build run. Please contact technical support.");
                  }

                  // set the reference build config id to last clean build run config.
                  if (ConfigurationManager.validateActiveID) {
                    cm.validateIsBuildRunBuildID(lastCleanBackingBuildRun.getBuildID());
                  }
                  sourceControlSetting.setPropertyValue(lastCleanBackingBuildRun.getBuildID());
                }
              } else if (sourceBuildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
                // make sure unset leading build config ID doesn't slip further
                if (leadingBuildID == BuildConfig.UNSAVED_ID) {
                  throw new IllegalStateException("Leading build ID not set. Please contact technical support.");
                }
                // use leading run build config ID
                sourceControlSetting.setPropertyValue(leadingBuildID);
              }
            } else {

              // Resolve path to Subversion exe
              if (sourceControlSetting.getPropertyName().equals(SourceControlSetting.SVN_PATH_TO_EXE)) {
                sourceControlSetting.setPropertyValue(sourceControlSettingResolver.resolve(sourceControlSetting.getPropertyValue()));
              }
            }
          }
          saveNewSourceControlSetting(session, sourceControlSetting, resultBuildID, overwrittenSourceControlSettings);
        }

        // finish handling CC storage to avoid duplication of VWS path created by cloning
        if (copyActiveBuild) { // NOPMD
          if (storageLocationCode != null) {
            if (storageLocationCode.getPropertyValueAsInt() != SourceControlSetting.CLEARCASE_STORAGE_CODE_VWS) {
              if (storageLocationCode.getPropertyValueAsInt() == SourceControlSetting.CLEARCASE_STORAGE_CODE_STGLOC) {
                saveNewSourceControlSetting(session, storageLocationCode, resultBuildID, overwrittenSourceControlSettings);
                if (storageLocation != null) {
                  saveNewSourceControlSetting(session, storageLocation, resultBuildID, overwrittenSourceControlSettings);
                }
              }
            }
          }
        }

        // add overwrites that have not been saved if any
        for (final Iterator iterator = sourceControlSettingsOverwriteMap.entrySet().iterator(); iterator.hasNext();) {
          final Map.Entry entry = (Map.Entry) iterator.next();
          final String sourceControlSettingName = (String) entry.getKey();
          if (!overwrittenSourceControlSettings.containsKey(sourceControlSettingName)) {
            // has to add
            saveNewSourceControlSetting(session,
                    new SourceControlSetting(resultBuildID, sourceControlSettingName, (String) entry.getValue()),
                    resultBuildID, overwrittenSourceControlSettings);
          }
        }

        //
        // watchers
        //
        final List watchers = cm.getWatchers(sourceBuildID);
        for (final Iterator i = watchers.iterator(); i.hasNext();) {
          final BuildWatcher buildWatcher = (BuildWatcher) i.next();
          session.evict(buildWatcher);
          buildWatcher.setWatcherID(BuildWatcher.UNSAVED_ID);
          buildWatcher.setBuildID(resultBuildID);
          session.save(buildWatcher);
        }

        //
        // result configs
        //
        final List resultConfigs = cm.getResultConfigs(sourceBuildID);
        for (final Iterator i = resultConfigs.iterator(); i.hasNext();) {
          final ResultConfig resultConfig = (ResultConfig) i.next();
          final int resultConfigIDToClone = resultConfig.getID();
          session.evict(resultConfig);
          resultConfig.setID(ResultConfig.UNSAVED_ID);
          resultConfig.setBuildID(resultBuildID);
          session.save(resultConfig);
          final List resultConfigProperties = cm.getResultConfigProperties(resultConfigIDToClone);
          for (final Iterator j = resultConfigProperties.iterator(); j.hasNext();) {
            final ResultConfigProperty resultConfigProperty = (ResultConfigProperty) j.next();
            session.evict(resultConfigProperty);
            resultConfigProperty.setID(LogConfigProperty.UNSAVED_ID);
            resultConfigProperty.setResultConfigID(resultConfig.getID());
            session.save(resultConfigProperty);
          }
        }

        //
        // manual start parameters
        //
        // NOTE: vimeshev - 2006-12-28 - parallel dependent
        // builds use there leader's manual parameters
        // because themselves they never get started
        // manually but rather by a start request from the
        // leader.
        //
        // NOTE: vimeshev - 2008-01-18 -
        // cm.getStartParameters returns all
        // parameter, i.e. both of PUBLISH and BUILD types.
        final int manualStartParameterSourceBuildConfigID = leadingBuildID == BuildConfig.UNSAVED_ID ? sourceBuildID : leadingBuildID;
        final List buildStartParameters = cm.getStartParameters(StartParameterType.BUILD, manualStartParameterSourceBuildConfigID);
        final List publishStartParameter = cm.getStartParameters(StartParameterType.PUBLISH, manualStartParameterSourceBuildConfigID);
        final Collection manualStartParameters = new ArrayList(11);
        if (copyActiveBuild) {
          manualStartParameters.addAll(buildStartParameters);
          manualStartParameters.addAll(publishStartParameter);
        } else {
          // Load system, project, agent and build parameters
          final Map map = SystemVariableConfigurationManager.getInstance().getCommonVariableMap(manualStartParameterSourceBuildConfigID, agentHostName);
          for (int i = 0; i < buildStartParameters.size(); i++) {
            final StartParameter parameter = (StartParameter) buildStartParameters.get(i);
            map.put(parameter.getName(), parameter);
          }
          manualStartParameters.addAll(map.values());
        }
        // REVIEWME: simeshev@parabuilci.org - It looks like this may override parallel's agent host settings.
        for (final Iterator i = manualStartParameters.iterator(); i.hasNext();) {
          final StartParameter parameter = (StartParameter) i.next();
          session.evict(parameter);
          parameter.setID(StartParameter.UNSAVED_ID);
          parameter.setBuildID(resultBuildID);
          if (parameter.getType() == StartParameter.TYPE_SYSTEM
                  || parameter.getType() == StartParameter.TYPE_PROJECT
                  || parameter.getType() == StartParameter.TYPE_AGENT) {
            parameter.setType(StartParameter.TYPE_BUILD);
          }
          session.save(parameter);
        }
        return resultBuildConfig;
      }


      private void processBuildSequences(final Session session, final List buildSequences, final int resultBuildID) throws HibernateException {
        for (final Iterator i = buildSequences.iterator(); i.hasNext();) {
          final BuildSequence buildSequence = (BuildSequence) i.next();
          session.evict(buildSequence);
          buildSequence.setSequenceID(BuildSequence.UNSAVED_ID);
          buildSequence.setBuildID(resultBuildID);
          session.save(buildSequence);
        }
      }


      /**
       * Helper method to save a source control setting.
       *
       * @param session
       * @param setting
       * @param buildID
       * @param overwrittenSettings
       * @throws HibernateException
       */
      private void saveNewSourceControlSetting(final Session session,
                                               final SourceControlSetting setting, final int buildID,
                                               final Map overwrittenSettings) throws HibernateException {

        setting.setPropertyID(SourceControlSetting.UNSAVED_ID);
        setting.setBuildID(buildID);
        if (!sourceControlSettingsOverwriteMap.isEmpty()) {
          final String propertyName = setting.getPropertyName();
          final String overwrite = (String) sourceControlSettingsOverwriteMap.get(propertyName);
          if (overwrite != null) { // only comparing to null because value *can* be empty string
            setting.setPropertyValue(overwrite);
            if (overwrittenSettings != null) {
              overwrittenSettings.put(propertyName, Boolean.TRUE);
            }
          }
        }
        session.save(setting);
      }
    });
  }


  public String toString() {
    return "BuildConfigCloner{" +
            "sourceControlSettingsOverwriteMap=" + sourceControlSettingsOverwriteMap +
            '}';
  }
}
