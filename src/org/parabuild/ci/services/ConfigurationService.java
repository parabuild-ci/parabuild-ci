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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.BuildConfigCloner;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.PersistanceConstants;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.configuration.SystemConstants;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.*;
import org.parabuild.ci.statistics.StatisticsManager;
import org.parabuild.ci.statistics.StatisticsManagerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @noinspection StaticFieldReferencedViaSubclass
 */
public final class ConfigurationService implements Service {

  private static final Log log = LogFactory.getLog(ConfigurationService.class);
  private SessionFactory sessionFactory = null;


  private byte status = SERVICE_STATUS_NOT_STARTED;


  /**
   * Returns serivce status
   *
   * @return service status
   */
  public byte getServiceStatus() {
    return status;
  }


  public void shutdownService() {
    try {
      shutdownQuarts();
    } catch (Exception e) {
      System.err.println("Error while shutting down configuration service: " + StringUtils.toString(e)); // NOPMD
    }
  }


  private void shutdownQuarts() throws SchedulerException {
    final SchedulerFactory factory = new StdSchedulerFactory();
    final Scheduler scheduler = factory.getScheduler();
    scheduler.shutdown();
  }


  public ServiceName serviceName() {
    return ServiceName.CONFIGURATION_SERVICE;
  }


  public void startupService() {
    try {
      initConfigManager();
      initQuartz();
      status = SERVICE_STATUS_STARTED;
      runPostStartUpActions();
    } catch (Exception e) {
      log.error("Error while starting configuration service", e);
    }
  }


  /**
   * Starts up Quartz scheduler.
   */
  private void initQuartz() throws SchedulerException {
    final SchedulerFactory factory = new StdSchedulerFactory();
    final Scheduler scheduler = factory.getScheduler();
    scheduler.start();
  }


  /**
   * Inits configuration manager
   */
  private void initConfigManager() throws HibernateException, IOException {

    // get props made from config/hibernate.properties
    final Properties props = new Properties();
    props.load(IoUtils.stringToInputStream(IoUtils.getResourceAsString("hibernate.properties")));

    // init hibernate
    final Configuration cfg = new Configuration();
    cfg.setProperties(props);
    cfg.setProperty("hibernate.connection.url", "jdbc:hsqldb:" + ConfigurationConstants.DATABASE_HOME.getAbsolutePath() + ";ifexists=true");
    cfg.setProperty("hibernate.connection.password", PersistanceConstants.DATABASE_PASSWORD);
    cfg.setProperty("hibernate.connection.username", PersistanceConstants.DATABASE_USER_NAME);
    cfg.addClass(BuildConfig.class)
            .addClass(User.class)
            .addClass(SystemProperty.class)
            .addClass(VCSUserToEmailMap.class)
            .addClass(ScheduleProperty.class)
            .addClass(LabelProperty.class)
            .addClass(SourceControlSetting.class)
            .addClass(BuildRun.class)
            .addClass(StepRun.class)
            .addClass(StepLog.class)
            .addClass(Change.class)
            .addClass(ChangeList.class)
            .addClass(BuildRunParticipant.class)
            .addClass(ScheduleItem.class)
            .addClass(BuildConfigAttribute.class)
            .addClass(BuildWatcher.class)
            .addClass(BuildChangeList.class)
            .addClass(LogConfig.class)
            .addClass(LogConfigProperty.class)
            .addClass(BuildRunAttribute.class)
            .addClass(IssueTracker.class)
            .addClass(IssueTrackerProperty.class)
            .addClass(Issue.class)
            .addClass(IssueAttribute.class)
            .addClass(ReleaseNote.class)
            .addClass(PendingIssue.class)
            .addClass(IssueChangeList.class)
            .addClass(StepRunAttribute.class)
            .addClass(Group.class)
            .addClass(UserGroup.class)
            .addClass(GroupBuildAccess.class)
            .addClass(ResultConfig.class)
            .addClass(ResultConfigProperty.class)
            .addClass(StepResult.class)
            .addClass(UserProperty.class)
            .addClass(ActiveBuild.class)
            .addClass(ActiveBuildAttribute.class)
            .addClass(HourlyStats.class)
            .addClass(DailyStats.class)
            .addClass(YearlyStats.class)
            .addClass(MonthlyStats.class)
            .addClass(HourlyDistribution.class)
            .addClass(WeekDayDistribution.class)
            .addClass(StartParameter.class)
            .addClass(DisplayGroup.class)
            .addClass(DisplayGroupBuild.class)
            .addClass(BuilderConfiguration.class)
            .addClass(AgentConfig.class)
            .addClass(BuilderAgent.class)
            .addClass(HourlyTestStats.class)
            .addClass(DailyTestStats.class)
            .addClass(MonthlyTestStats.class)
            .addClass(YearlyTestStats.class)
            .addClass(ResultGroup.class)
            .addClass(PublishedStepResult.class)
            .addClass(ResultGroupAccess.class)
            .addClass(BuildRunAction.class)
            .addClass(BuildRunDependence.class)
            .addClass(Project.class)
            .addClass(ProjectAttribute.class)
            .addClass(ProjectBuild.class)
            .addClass(ProjectResultGroup.class)
            .addClass(MergeServiceConfiguration.class)
            .addClass(MergeConfiguration.class)
            .addClass(MergeConfigurationAttribute.class)
            .addClass(Merge.class)
            .addClass(MergeSourceBuildRun.class)
            .addClass(MergeTargetBuildRun.class)
            .addClass(MergeChangeList.class)
            .addClass(BranchChangeList.class)
            .addClass(BranchBuildRunParticipant.class)
            .addClass(PromotionPolicy.class)
            .addClass(PromotionPolicyStep.class)
            .addClass(PromotionStepDependency.class)
            .addClass(TestSuiteName.class)
            .addClass(TestCaseName.class)
            .addClass(BuildRunTest.class)
            .addClass(GlobalVCSUserMap.class)
            .addClass(BuildChangeListAttribute.class)
            .addClass(BuildSequence.class);
    if (log.isDebugEnabled()) {
      log.debug("creating session factory");
    }
    sessionFactory = cfg.buildSessionFactory();
  }


  /**
   * Runs actions that might need to run at startup.
   */
  private void runPostStartUpActions() {
    if (Boolean.valueOf(System.getProperty(SystemConstants.SYSTEM_PROPERTY_POPULATE_BUILD_RUN_CONFIGS, "false")).booleanValue()) {
      // have to create missing build run configs.
      final List buildRunList = (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          final Collection result = new ArrayList(7777);
          // get all build runs that are tied to active builds
          // (i.e. don't have copy versions of build configs.
          // that is, we can use build run's config id being
          // cerain it is an active build config.

          // first process automatic
          final Query qNonRef = session.createQuery(
                  " select br from BuildRun br, BuildConfig bc, ActiveBuild ab " +
                          "   where br.buildID = bc.buildID " +
                          "     and bc.buildID = ab.ID" +
                          "     and bc.sourceControl != ?");
          qNonRef.setInteger(0, BuildConfig.SCM_REFERENCE);
          result.addAll(qNonRef.list());

          // than process sched/ref
          final Query qRef = session.createQuery(
                  " select br from BuildRun br, BuildConfig bc, ActiveBuild ab " +
                          "   where br.buildID = bc.buildID " +
                          "     and bc.buildID = ab.ID" +
                          "     and bc.sourceControl = ?");
          qRef.setInteger(0, BuildConfig.SCM_REFERENCE);
          result.addAll(qRef.list());

          // traverse result
          return result;
        }
      });

      final BuildConfigCloner cloner = new BuildConfigCloner();
      for (final Iterator i = buildRunList.iterator(); i.hasNext();) {
        final BuildRun buildRun = (BuildRun) i.next();
        // have to create missing build run configs.
        ConfigurationManager.runInHibernate(new TransactionCallback() {
          public Object runInTransaction() throws Exception {
            if (log.isDebugEnabled()) {
              log.debug("Will create config for " + buildRun);
            }
            // create build run config copy
            final BuildConfig newBuildRunConfig = cloner.createBuildRunConfig(buildRun.getBuildID(), "null");
            // set new ID
            buildRun.setBuildID(newBuildRunConfig.getBuildID());
            // save
            session.saveOrUpdate(buildRun);
            // flush - size of the session object can be pretty build.
            session.flush();
            if (log.isDebugEnabled()) {
              log.debug("Created config " + buildRun.getBuildRunID());
            }
            return null;
          }
        });
      }
    }

    // init statistics if requested by updater
    if (Boolean.valueOf(System.getProperty(SystemConstants.SYSTEM_PROPERTY_INIT_STATISTICS, "false")).booleanValue()) {
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      for (final Iterator i = cm.getExistingBuildConfigs().iterator(); i.hasNext();) {
        final StatisticsManager statisticsManager = StatisticsManagerFactory
                .getStatisticsManager(((BuildConfig) i.next()).getActiveBuildID());
        statisticsManager.initStatistics();
      }
    }

    // enable advanced settings if missed. this is done for customers that
    // already used Parabuild when advanced seeting appeared - they should
    // have it enabled so that they don't face change of UI behaviour.
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    if (Boolean.valueOf(System.getProperty(SystemConstants.SYSTEM_PROPERTY_INIT_ADVANCED_SETTINGS, "false")).booleanValue()) {
      scm.createSystemPropertyIfDoesNotExist(SystemProperty.ENABLE_ADVANCED_BUILD_SETTING, SystemProperty.OPTION_CHECKED);
    }

    // Init retry settings
    if (Boolean.valueOf(System.getProperty(SystemConstants.SYSTEM_PROPERTY_INIT_RETRY_SETTINGS, "false")).booleanValue()) {
      scm.createSystemPropertyIfDoesNotExist(SystemProperty.RETRY_VCS_COMMAND_INTERVAL, SystemProperty.DEFAULT_RETRY_VCS_COMMAND_INTERVAL);
      scm.createSystemPropertyIfDoesNotExist(SystemProperty.RETRY_VCS_COMMAND_TIMES, SystemProperty.DEFAULT_RETRY_VCS_COMMAND_TIMES);
      scm.createSystemPropertyIfDoesNotExist(SystemProperty.RETRY_VCS_COMMAND_PATTERNS, SystemProperty.DEFAULT_RETRY_VCS_COMMAND_PATTERNS);
    }


    //
    // Set default values if not set
    //
    scm.createSystemPropertyIfDoesNotExist(SystemProperty.ROUND_ROBIN_LOAD_BALANCING, SystemProperty.OPTION_CHECKED);
    scm.createSystemPropertyIfDoesNotExist(SystemProperty.DEFAULT_BUILD_NAME_VALIDATION, SystemProperty.RADIO_SELECTED);
    scm.createSystemPropertyIfDoesNotExist(SystemProperty.DEFAULT_VARIABLE_NAME_VALIDATION, SystemProperty.RADIO_SELECTED);
    scm.createSystemPropertyIfDoesNotExist(SystemProperty.CUSTOM_BUILD_NAME_VALIDATION, SystemProperty.RADIO_UNSELECTED);
    scm.createSystemPropertyIfDoesNotExist(SystemProperty.CUSTOM_VARIABLE_NAME_VALIDATION, SystemProperty.RADIO_UNSELECTED);
    scm.createSystemPropertyIfDoesNotExist(SystemProperty.USE_XML_LOG_FORMAT_FOR_SUBVERSION, SystemProperty.OPTION_CHECKED);
    scm.createSystemPropertyIfDoesNotExist(SystemProperty.NOTIFY_USERS_WITH_EDIT_RIGHTS_ABOUT_SYSTEM_ERRORS, SystemProperty.OPTION_CHECKED);
    scm.createSystemPropertyIfDoesNotExist(SystemProperty.RESPECT_INTERMEDIATE_STEP_FAILURE, SystemProperty.OPTION_CHECKED);
    scm.createSystemPropertyIfDoesNotExist(SystemProperty.MAX_PARALLEL_UPGRADES, "2");
  }


  public SessionFactory getSessionFactory() {
    validateIsUp();
    return sessionFactory;
  }


  private void validateIsUp() {
    if (status != SERVICE_STATUS_STARTED) {
      throw new IllegalStateException("Service " + serviceName() + " has not started yet");
    }
  }


  public String toString() {
    return "ConfigurationService{" +
            "status=" + status +
            '}';
  }
}
