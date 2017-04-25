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
package org.parabuild.ci.remote;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.SourceControlSettingResolver;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ThreadUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.BuilderAgentVO;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.manager.client.DeployCommand;
import org.parabuild.ci.manager.client.UndeployCommand;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuilderConfiguration;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.realm.RealmConstants;
import org.parabuild.ci.remote.internal.LocalAgent;
import org.parabuild.ci.remote.internal.LocalAgentEnvironment;
import org.parabuild.ci.remote.internal.RemoteAgentEnvironmentProxy;
import org.parabuild.ci.remote.internal.RemoteAgentProxy;
import org.parabuild.ci.remote.internal.WebServiceLocator;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public final class AgentManager {

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration, UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(AgentManager.class); //NOPMD

  /**
   * Singleton instance.
   */
  private static final AgentManager INSTANCE = new AgentManager();

  /**
   * Agent build version parser regex.
   */
  static final String AGENT_VERSION_PARSER_REGEX = "([0-9]+)\\.[0-9]+\\.[0-9]+.* build ([0-9]+)";

  /**
   * Map that keeps track of last selected agent host for a builder.
   */
  private final Map builderIDToLastReturnedAgent = new HashMap(11);

  private final PooledExecutor agentUpgradeExecutor;

  private final Set agentsScheduledForUpgrade = new HashSet(11);

  private final Map agentUseMap = new HashMap(11);


  /**
   * Singleton constructor.
   */
  private AgentManager() {

    // Create upgrade executor queue
    final int poolSize = SystemConfigurationManagerFactory.getManager().getMaxParallelSystemUpgrades();
    agentUpgradeExecutor = new PooledExecutor(new LinkedQueue(), poolSize);
    agentUpgradeExecutor.setThreadFactory(new ThreadFactory() {

      public Thread newThread(final Runnable command) {

        return ThreadUtils.makeDaemonThread(command, "AgentUpgrader");
      }
    });

    agentUpgradeExecutor.setKeepAliveTime(-1L); // live forever
  }


  /**
   * Returns singleton instance.
   *
   * @return singleton instance.
   */
  public static AgentManager getInstance() {
    return INSTANCE;
  }


  /**
   * Rotates an agent.
   *
   * @param activeBuildID      active build ID.
   * @param freeAgentHosts     if not empty, the method will try to pick an agent that is not busy
   * @param preferredAgentHost a preferred agent host.
   * @return an agent host.
   * @throws IOException if I/O exception occurred.
   */
  public AgentHost getNextLiveAgentHost(final int activeBuildID, final List freeAgentHosts,
                                        final AgentHost preferredAgentHost) throws IOException {

    // Get live agent liveHosts
    final BuilderConfigurationManager bcm = BuilderConfigurationManager.getInstance();
    final ConfigurationManager cm = ConfigurationManager.getInstance();

    final BuildConfig buildConfig = cm.getBuildConfiguration(activeBuildID);
    final BuilderConfiguration builderConfig = bcm.getBuilder(buildConfig.getBuilderID());
    final List liveHosts = freeAgentHosts.isEmpty() ? getLiveAgentHosts(buildConfig.getBuilderID(), false) : freeAgentHosts;
    final int liveHostsSize = liveHosts.size();
    if (liveHosts.isEmpty()) {
      throw new IOException("Build farm " + builderConfig.getName() + " for build " + buildConfig.getBuildName() + " does not have active agents");
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Hosts.size(): " + liveHostsSize);
    }

    // Satisfy the agent host preference
    if (preferredAgentHost != null) {

      for (int i = 0; i < liveHostsSize; i++) {

        final AgentHost agentHost = (AgentHost) liveHosts.get(i);
        if (agentHost.equals(preferredAgentHost)) {

          return agentHost;
        }
      }
    }


    // Get next
    synchronized (this) { // To protect builderIDToLastReturnedAgent

      // Prepare key
      final Integer builderIDObject = new Integer(buildConfig.getBuilderID());

      // Try to find next if enabled
      AgentHost result = null;
      if (SystemConfigurationManagerFactory.getManager().isRoundRobinLoadBalancing()) {

        final AgentHost lastSelectedAgentHost = (AgentHost) builderIDToLastReturnedAgent.get(builderIDObject);
        if (lastSelectedAgentHost != null) {

          for (int i = 0; i < liveHostsSize; i++) {

            final AgentHost liveHost = (AgentHost) liveHosts.get(i);
            if (liveHost.getHost().equalsIgnoreCase(lastSelectedAgentHost.getHost())) {

              // This is the previously selected, select next.
              if (i == liveHostsSize - 1) {

                // End of the list, get first
                result = (AgentHost) liveHosts.get(0);
                break;
              } else {

                // Get next
                result = (AgentHost) liveHosts.get(i + 1);
                break;
              }
            }
          }
        }
      }

      if (result == null) {

        //noinspection UnusedAssignment
        int randomIndex = 0;
        try {

          final SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
          randomIndex = random.nextInt() % liveHostsSize;
        } catch (NoSuchAlgorithmException e) {

          final Random random = new Random(System.currentTimeMillis());
          randomIndex = random.nextInt() % liveHostsSize;
        }
        result = (AgentHost) liveHosts.get(randomIndex > 0 ? randomIndex : -randomIndex);
      }

      if (LOG.isDebugEnabled()) {

        LOG.debug("Selected next agent " + result.getHost() + " for build " + buildConfig.getBuildName());
      }
      builderIDToLastReturnedAgent.put(builderIDObject, result);

      // Return
      return result;
    }
  }


  /**
   * Rotates an agent.
   *
   * @param activeBuildID
   * @return
   */
  public AgentHost getNextLiveAgentHost(final int activeBuildID) throws IOException {

    return getNextLiveAgentHost(activeBuildID, Collections.EMPTY_LIST, null);
  }


  /**
   * Gets next live agent host.
   *
   * @param activeBuildID       an active build's ID.
   * @param agentHost           a desired agent host, may be null. If null, the checkout algorithm follows the normal
   *                            load balancing algorithm.
   * @param uniqueAgentCheckout if the checkout should be performed only if the agent has not been checked out
   *                            (checkout counter is null).
   * @param agentHostRequired
   * @return an agent host or null if no live agents available or if unique agent checkout was requested and there is
   *         no free agent.
   * @throws IOException if there are no live agents.
   */
  public AgentHost checkoutAgentHost(final int activeBuildID, final AgentHost agentHost,
                                     final boolean uniqueAgentCheckout, final boolean agentHostRequired) throws IOException {

    //
    // Get live agent hosts
    //
    final BuilderConfigurationManager bcm = BuilderConfigurationManager.getInstance();
    final ConfigurationManager cm = ConfigurationManager.getInstance();

    final BuildConfig buildConfig = cm.getBuildConfiguration(activeBuildID);
    final BuilderConfiguration builderConfig = bcm.getBuilder(buildConfig.getBuilderID());
    final List liveHosts = getLiveAgentHosts(buildConfig.getBuilderID(), false);
    if (liveHosts.isEmpty()) {

      throw new IOException("Build farm " + builderConfig.getName() + " for build " + buildConfig.getBuildName() + " does not have active agents");
    }

    synchronized (agentUseMap) {

      //
      // Register new agents
      //
      for (final Iterator iterator = liveHosts.iterator(); iterator.hasNext(); ) {

        final AgentHost liveAgentHost = (AgentHost) iterator.next();
        if (!agentUseMap.containsKey(liveAgentHost)) {

          agentUseMap.put(liveAgentHost, new AgentUse(liveAgentHost));
        }
      }

      //
      // Filter live agent use
      //
      final List liveAgentUse = new ArrayList(liveHosts.size());
      for (final Iterator iterator = liveHosts.iterator(); iterator.hasNext(); ) {

        final AgentHost liveAgentHost = (AgentHost) iterator.next();
        final AgentUse agentUse = (AgentUse) agentUseMap.get(liveAgentHost);
        liveAgentUse.add(agentUse);
      }


      //
      // Update capacity
      //
      for (int i = 0; i < liveAgentUse.size(); i++) {

        final AgentUse agentUse = (AgentUse) liveAgentUse.get(i);
        final String host = agentUse.getAgentHost().getHost();
        final AgentConfig agentConfig = BuilderConfigurationManager.getInstance().findAgentByHost(host);
        agentUse.setCapacity(agentConfig == null ? 1 : agentConfig.getCapacity());
        agentUse.setMaxConcurrentBuilds(agentConfig.getMaxConcurrentBuilds());
      }


      //
      // Sort live agent use
      //
      Collections.sort(liveAgentUse, AgentUse.REVERSE_ORDER_USE_COMPARATOR);

      //
      // Pick first (least loaded) agent
      //
      if (uniqueAgentCheckout) {

        return checkoutUnique(agentHost, liveAgentUse, agentHostRequired);
      } else {

        // Non-unique agent checkout
        return checkoutNonUnique(agentHost, liveAgentUse, agentHostRequired);
      }
    }
  }


  private AgentHost checkoutNonUnique(final AgentHost agentHost, final List liveAgentUse,
                                      final boolean agentHostRequired) {

    final BuilderConfigurationManager bcm = BuilderConfigurationManager.getInstance();

    AgentUse found = null;

    if (agentHost != null) {

      // Find desired
      for (int i = 0; i < liveAgentUse.size(); i++) {

        final AgentUse agentUse = (AgentUse) liveAgentUse.get(i);
        if (agentUse.getAgentHost().equals(agentHost)) {

          // Check if at max concurrent builds
          if (agentUse.isUnlimitedConcurrentBuilds() || agentUse.getCheckoutCounter() < agentUse.getMaxConcurrentBuilds()) {

            found = agentUse;
            break;
          }
        }
      }
    }


    // Clear desired found if it is loaded and it should be serialized
    if (found != null) {

      final AgentConfig agentConfig = bcm.findAgentByHost(found.getAgentHost().getHost());
      if (found.getCheckoutCounter() > 0 && agentConfig.isSerialize()) {

        found = null;
      }
    }

    // Found desired ?
    if (found == null) {

      if (agentHost != null && agentHostRequired) {

        // Don't attempt to find other than required
        return null;
      }

      for (int i = 0; i < liveAgentUse.size(); i++) {

        final AgentUse agentUse = (AgentUse) liveAgentUse.get(i);
        final AgentConfig agentConfig = bcm.findAgentByHost(agentUse.getAgentHost().getHost());
        if (agentUse.getCheckoutCounter() == 0 || !agentConfig.isSerialize()) {

          // Check if at max concurrent builds
          if (agentUse.isUnlimitedConcurrentBuilds() || agentUse.getCheckoutCounter() < agentUse.getMaxConcurrentBuilds()) {

            found = agentUse;
            break;
          }
        }
      }
    }

    if (found == null) {

      return null;
    } else {

      // Increment use
      found.incrementCheckoutCounter();

      // Return result
      return found.getAgentHost();
    }
  }


  private AgentHost checkoutUnique(final AgentHost agentHost, final List liveAgentUse,
                                   final boolean agentHostRequired) {

    // Find desired
    AgentUse found = null;

    if (agentHost != null) {

      for (int i = 0; i < liveAgentUse.size(); i++) {
        final AgentUse agentUse = (AgentUse) liveAgentUse.get(i);

        if (agentUse.getAgentHost().equals(agentHost)) {

          found = agentUse;
          break;
        }
      }

      // Check if desired is busy
      if (found != null && found.getCheckoutCounter() > 0) {

        // Cannot use desired because of the unique load requirement
        found = null;
      }
    }


    // Found desired ?
    if (found == null) {

      if (agentHost != null && agentHostRequired) {

        // Don't attempt to find other than required
        return null;
      }

      // Find not loaded
      for (int i = 0; i < liveAgentUse.size(); i++) {

        final AgentUse agentUse = (AgentUse) liveAgentUse.get(i);
        if (agentUse.getCheckoutCounter() == 0) {

          found = agentUse;
          break;
        }
      }
    }

    if (found == null) {

      return null;
    } else {

      // Increment use
      found.incrementCheckoutCounter();

      // Return result
      return found.getAgentHost();
    }
  }


  /**
   * Puts an agent host to the pool of free agents.
   *
   * @param agentHost the agent host to put to the pool of free agents.
   */
  public void checkinAgentHost(final AgentHost agentHost) {

    synchronized (agentUseMap) {

      final AgentUse agentUse = (AgentUse) agentUseMap.get(agentHost);
      agentUse.decrementCheckoutCounter();
    }
  }


  /**
   * Returns a agent for the given build configuration.
   * <p/>
   * Created agent uses custom checkout directory.
   *
   * @param buildConfigID       the build configuration ID.
   * @param checkoutDirTemplate custom checkout directory. If
   *                            blank or null a default checkout directory is used.
   * @throws IllegalStateException if checkoutDirTemplate is invalid.
   */
  public Agent getAgent(final int buildConfigID, final String checkoutDirTemplate) throws IOException {

    // Get build config
    final BuildConfig buildConfig = ConfigurationManager.getInstance().getBuildConfiguration(buildConfigID);

    // Get agent config
    final AgentConfig agentConfig = BuilderConfigurationManager.getInstance().getFirstAgentConfig(buildConfig.getBuilderID());
    if (agentConfig == null) {
      throw new IOException("Build farm for build " + buildConfig.getBuildName() + " does not have active agents");
    }


    // Create agent 
    if (agentConfig.isLocal()) {

      final String localHostName = IoUtils.getLocalHostNameHard();

      // Create custom dir path
      final String customCheckoutDirPath = createCheckoutDirPath(checkoutDirTemplate, buildConfigID, buildConfig.getBuildName(), localHostName);

      // Create agent
      return new LocalAgent(buildConfig.getActiveBuildID(), customCheckoutDirPath);
    } else {

      final AgentHost agentHost = new AgentHost(agentConfig.getHost(), agentConfig.getPassword());

      final WebServiceLocator webServiceLocator = new WebServiceLocator(agentHost);

      // Create custom dir path
      final String customCheckoutDirPath = createCheckoutDirPath(checkoutDirTemplate, buildConfigID, buildConfig.getBuildName(), webServiceLocator.getWebService().getHostName());

      return new RemoteAgentProxy(buildConfig.getActiveBuildID(), webServiceLocator, customCheckoutDirPath);
    }
  }


  /**
   * Returns a agent for the given build configuration.
   * <p/>
   * A checkout is used either one defined in the build's
   * configuration or a default system directory if not set.
   *
   * @param buildConfigID
   * @throws IllegalStateException if a checkout dir
   *                               template defined for the given build configuration is
   *                               invalid.
   */
  public Agent getNextLiveAgent(final int buildConfigID) throws IOException {

    final AgentHost agentHost = getNextLiveAgentHost(buildConfigID);
    return createAgent(buildConfigID, agentHost);
  }


  /**
   * Creates agent.
   *
   * @param buildConfigID
   * @param checkoutDirTemplate
   * @param buildName
   * @param agentHost
   * @param activeBuildID
   * @return
   * @throws IOException
   * @see #getNextLiveAgentHost(int)
   */
  public Agent createAgent(final int buildConfigID, final String checkoutDirTemplate, final String buildName,
                           final AgentHost agentHost, final int activeBuildID) throws IOException {

    // Create agent
    if (agentHost.isLocal()) {

      final String localHostName = IoUtils.getLocalHostNameHard();

      // Create custom dir path
      final String customCheckoutDirPath = createCheckoutDirPath(checkoutDirTemplate, buildConfigID, buildName, localHostName);

      // Create agent
      return new LocalAgent(activeBuildID, customCheckoutDirPath);
    } else {

      final WebServiceLocator webServiceLocator = new WebServiceLocator(agentHost);

      // Create custom dir path
      final String customCheckoutDirPath = createCheckoutDirPath(checkoutDirTemplate, buildConfigID, buildName, webServiceLocator.getWebService().getHostName());

      return new RemoteAgentProxy(activeBuildID, webServiceLocator, customCheckoutDirPath);
    }
  }


  /**
   * Returns a agent for the given build configuration.
   * <p/>
   * A checkout is used either one defined in the build's
   * configuration or a default system directory if not set.
   *
   * @param buildConfigID a build configuration ID.
   * @param agentHost     an agent host.
   * @return the agent.
   * @throws IllegalStateException if a checkout dir
   *                               template defined for the given build configuration is
   *                               invalid.
   * @throws IOException           if an I/O error occured.
   */
  public Agent createAgent(final int buildConfigID, final AgentHost agentHost) throws IOException {

    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final String checkoutDirTemplate = cm.getSourceControlSettingValue(buildConfigID, SourceControlSetting.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE, null);
    return createAgent(buildConfigID, checkoutDirTemplate, cm.getBuildConfiguration(buildConfigID).getBuildName(), agentHost, buildConfigID);
  }


  /**
   * Return agent host environment for the given host.
   *
   * @param host to return build environment for.
   * @return agent host environment.
   */
  public AgentEnvironment getAgentEnvironment(final AgentHost host) {

    if (host.isLocal()) {

      return new LocalAgentEnvironment();
    } else {

      return new RemoteAgentEnvironmentProxy(host);
    }
  }


  /**
   * @throws IllegalStateException if a checkout dir
   *                               template defined for the given build configuration is
   *                               invalid.
   */
  private static String createCheckoutDirPath(final String customCheckoutDirTemplate, final int buildID,
                                              final String buildName, final String agentHostName) {

    try {

      // handle blank case
      if (StringUtils.isBlank(customCheckoutDirTemplate)) {

        return customCheckoutDirTemplate;
      }

      // handle when the template is set
      final SourceControlSettingResolver sourceControlSettingResolver = new SourceControlSettingResolver(buildName, buildID, agentHostName);
      return sourceControlSettingResolver.resolve(customCheckoutDirTemplate);
    } catch (ValidationException e) {

      final IllegalStateException ise = new IllegalStateException(StringUtils.toString(e));
      ise.initCause(e);
      throw ise;
    }
  }


  /**
   * Returns first live and enabled agent for the given builder ID.
   *
   * @param builderID for that to return first live agent.
   * @return first live agent for the given builder ID.
   */
  public AgentEnvironment getFirstLiveAgentEnvironment(final int builderID) throws NoLiveAgentsException {

    final List agentHosts = getLiveAgentHosts(builderID, false);

    if (agentHosts.isEmpty()) {

      throw new NoLiveAgentsException("There are no live build agents in this build farm: " + builderID);
    }
    return getAgentEnvironment((AgentHost) agentHosts.get(0));
  }


  /**
   * Returns a list of live agent hosts for this builder.
   *
   * @param builderID      builder ID
   * @param returnDisabled if true, will return an agent even if it is disabled.
   * @return list of live Agents.
   */
  public List getLiveAgentHosts(final int builderID, final boolean returnDisabled) {

    final List result = new ArrayList(3);
    final List list = BuilderConfigurationManager.getInstance().getBuilderAgentVOs(builderID);
    for (int i = 0; i < list.size(); i++) {

      final BuilderAgentVO builderAgentVO = (BuilderAgentVO) list.get(i);
      final AgentHost agentHost = new AgentHost(builderAgentVO.getHost(), builderAgentVO.getPassword());
      if (builderAgentVO.isLocal()) {

        // Always available
        result.add(agentHost);
      } else {

        // Skip disabled if requested
        if (!builderAgentVO.isEnabled() && !returnDisabled) {

          continue;
        }

        // Check remote
        final RemoteAgentEnvironmentProxy agentEnvironmentProxy = new RemoteAgentEnvironmentProxy(agentHost);
        try {

          // This makes a network call and returns version
          final String remoteAgentVersion = agentEnvironmentProxy.builderVersionAsString();
          final LocalAgentEnvironment localEnv = new LocalAgentEnvironment();
          final String buildManagerVersion = localEnv.builderVersionAsString();
          if (buildManagerVersion.equals(remoteAgentVersion)) {

            result.add(agentHost);
          } else {

            final String agentHostName = agentHost.getHost();
            upgrade(agentHostName, buildManagerVersion, remoteAgentVersion);
          }
        } catch (Exception e) {

          reportError(e, agentHost);
        }
      }
    }
    return result;
  }


  public void upgrade(final String agentHostName, final String buildManagerVersion, final String remoteAgentVersion) {

    if (SystemConfigurationManagerFactory.getManager().isAutomaticAgentUpgradeEnabled()) {

        if (isUpgradable(remoteAgentVersion)) {

          scheduleForUpgrade(agentHostName, buildManagerVersion, remoteAgentVersion);
        } else {

          reportHostNotUpgradeable(buildManagerVersion, agentHostName);
        }
    } else {

      log(remoteAgentVersion, buildManagerVersion, agentHostName);
    }
  }


  /**
   * Returns a list of live agent builderAgentVOs for this builder.
   *
   * @param builderID builder ID
   * @return list of live Agents.
   */
  public List getLiveAgentVOs(final int builderID) {

    final List result = new ArrayList(3);
    final List list = BuilderConfigurationManager.getInstance().getBuilderAgentVOs(builderID);
    for (int i = 0; i < list.size(); i++) {

      final BuilderAgentVO builderAgentVO = (BuilderAgentVO) list.get(i);
      if (builderAgentVO.isLocal()) {

        // Always available
        result.add(builderAgentVO);
      } else {

        // Check remote
        final AgentHost agentHost = new AgentHost(builderAgentVO.getHost(), builderAgentVO.getPassword());
        final RemoteAgentEnvironmentProxy agentEnvironmentProxy = new RemoteAgentEnvironmentProxy(agentHost);
        try {

          // This makes a network call and returns version
          final String remoteVersion = agentEnvironmentProxy.builderVersionAsString();
          final LocalAgentEnvironment localEnv = new LocalAgentEnvironment();
          if (remoteVersion.equals(localEnv.builderVersionAsString())) {

            result.add(builderAgentVO);
          }
        } catch (IOException e) {

          // Ignore - won't add
        } catch (AgentFailureException e) {

          // Ignore - won't add
        }
      }
    }
    return result;
  }


  /**
   * Sets maximum number of agent upgrades that can be performed in parallel.
   *
   * @param number maximum number of agent upgrades that can be performed in parallel.
   */
  public void setMaximumParallelAgentUpgrades(final int number) {

    agentUpgradeExecutor.setMaximumPoolSize(number);
  }


  /**
   * Returns true if the given remote agent can be upgraded.
   *
   * @param remoteAgentVersion remote agent version
   * @return true if the given remote agent can be upgraded.
   */
  private static boolean isUpgradable(final String remoteAgentVersion) {

    final Matcher matcher = Pattern.compile(AGENT_VERSION_PARSER_REGEX).matcher(remoteAgentVersion);
    if (!matcher.find()) {

      return false;
    }
    final int groupCount = matcher.groupCount();
    if (groupCount == 2) {

      final String stringMajor = matcher.group(1);
      final String build = matcher.group(2);
      return StringUtils.isValidInteger(stringMajor) && StringUtils.isValidInteger(build)
              && Integer.parseInt(stringMajor) >= 4 && Integer.parseInt(build) >= 1664;
    } else {

      return false;
    }
  }


  /**
   * Puts a host name to the upgrade queue.
   *
   * @param host
   */
  private void scheduleForUpgrade(final String host, final String buildManagerVersion, final String remoteAgentVersion) {

    // No changes should be made to agentsScheduledForUpgrade
    synchronized (agentsScheduledForUpgrade) {

      // Check if already scheduled
      final String lowerCasedHost = host.toLowerCase();
      if (agentsScheduledForUpgrade.contains(lowerCasedHost)) {
        return;
      }

      // Register
      agentsScheduledForUpgrade.add(lowerCasedHost);
    }

    // Schedule
    try {
      agentUpgradeExecutor.execute(new Runnable() {
        public void run() {
          upgradeAgent(host, buildManagerVersion, remoteAgentVersion);
        }
      });
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }


  /**
   * Upgrades the agent at the given host.
   *
   * @param hostAsConfigured    agent host.
   * @param buildManagerVersion build manager version that agent will be upgraded to
   * @param remoteAgentVersion  old agent version
   * @noinspection ThrowCaughtLocally
   */
  private void upgradeAgent(final String hostAsConfigured, final String buildManagerVersion, final String remoteAgentVersion) {
    // Normalize
    final String host = RemoteUtils.normalizeHostPort(hostAsConfigured);
    try {

      // Notify started upgrading
      reportStartedUpgrading(host, buildManagerVersion, remoteAgentVersion);

      final String url = "http://" + host;
      final String catalinaBase = System.getProperty("catalina.base");
      final String war = new File(catalinaBase, "app/parabuild.war").toURL().toExternalForm();

      IOException lastError = null;
      final int attemptCount = 3;
      for (int attempt = 1; attempt <= attemptCount; attempt++) {
        try {
          // Undeploy
          if (LOG.isDebugEnabled()) {
            LOG.debug("Undeploying application: " + war);
          }
          final UndeployCommand undeployCommand = new UndeployCommand();
          final String password = RealmConstants.DEFAULT_AGENT_MANAGER_PASSWORD;
          undeployCommand.setPassword(password);
          final String username = RealmConstants.DEFAULT_AGENT_MANAGER_USER;
          undeployCommand.setUsername(username);
          undeployCommand.setUrl(url);
          undeployCommand.setPath("/");
          undeployCommand.execute();

          // Deploy
          if (LOG.isDebugEnabled()) {
            LOG.debug("Deploying application: " + war);
          }
          final DeployCommand deployCommand = new DeployCommand();
          deployCommand.setPassword(password);
          deployCommand.setUsername(username);
          deployCommand.setPath("/");
          deployCommand.setUrl(url);
          deployCommand.setWar(war);
          deployCommand.execute();

          // Success
          lastError = null;
          break;

        } catch (IOException e) {
          if (e.toString().indexOf("Cannot remove document base for path") < 0) {
            throw e;
          } else {
            lastError = e;
            Thread.sleep(1000L);
          }
        }
      }

      // Throw last error if set
      if (lastError != null) {
        throw lastError;
      }

      // Notify finishing upgrading
      if (LOG.isDebugEnabled()) {
        LOG.debug("Finished deploying application: " + war);
      }
      reportFinishedUpgrading(host, buildManagerVersion, remoteAgentVersion);
    } catch (Exception e) {

      // Report failure
      reportUpgradeFailed(host, buildManagerVersion, remoteAgentVersion, e);

      // Remove from agents scheduled for upgrade
      synchronized (agentsScheduledForUpgrade) {
        agentsScheduledForUpgrade.remove(hostAsConfigured.toLowerCase());
      }
    }
  }


  private static void reportError(final Exception e, final AgentHost agentHost) {
    if (LOG.isDebugEnabled()) {
      if (agentHost != null) {
        final String message = "Detected error while trying an agent on host " + agentHost.getHost() + ": " + StringUtils.toString(e);
        final Error error = new Error(message);
        error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
        error.setHostName(agentHost.getHost());
        error.setSendEmail(false);
        error.setDetails(e);
        ErrorManagerFactory.getErrorManager().reportSystemError(error);
        LOG.debug(message, e);
      } else {
        final String message = "Detected error while trying an agent: " + StringUtils.toString(e);
        final Error error = new Error(message);
        error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
        error.setSendEmail(false);
        error.setDetails(e);
        ErrorManagerFactory.getErrorManager().reportSystemError(error);
        LOG.debug(message, e);
      }
    }
  }


  private static void reportUpgradeFailed(final String host, final String buildManagerVersion, final String remoteAgentVersion, final Exception cause) {
    final Error error = new Error("Failed to upgrade agent host at " + host + " from version " + remoteAgentVersion
            + " to version " + buildManagerVersion + ". The agent needs to be upgraded manually.");
    error.setErrorLevel(Error.ERROR_LEVEL_ERROR);
    error.setHostName(host);
    error.setSendEmail(true);
    error.setDetails(cause);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  private static void reportHostNotUpgradeable(final String buildManagerVersion, final String host) {
    final Error error = new Error("Agent host at " + host + " cannot be upgraded automatically because its version is "
            + "too old. The agent needs to be upgraded manually to version " + buildManagerVersion
            + " before automatic upgrading begins to work");
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setHostName(host);
    error.setSendEmail(true);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  private static void reportFinishedUpgrading(final String host, final String buildManagerVersion, final String remoteAgentVersion) {
    final Error error = new Error("Parabuild has upgraded agent host at " + host + " from version "
            + remoteAgentVersion + " to version " + buildManagerVersion);
    error.setErrorLevel(Error.ERROR_LEVEL_INFO);
    error.setHostName(host);
    error.setSendEmail(false);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  private static void reportStartedUpgrading(final String host, final String buildManagerVersion, final String remoteAgentVersion) {
    final Error error = new Error("Parabuild started upgrading agent host at " + host + " from version "
            + remoteAgentVersion + " to version " + buildManagerVersion);
    error.setErrorLevel(Error.ERROR_LEVEL_INFO);
    error.setHostName(host);
    error.setSendEmail(false);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  private static void log(final String agentVersion, final String buildManagerVersion, final String host) {
    final Error error = new Error("Parabuild will not try to upgrade agent " + host + '@' + agentVersion + " to version " + buildManagerVersion + " because automatic agent upgrade is disabled.");
    error.setErrorLevel(Error.ERROR_LEVEL_INFO);
    error.setHostName(host);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  public String toString() {
    return "AgentManager{" +
            "builderIDToLastReturnedAgent=" + builderIDToLastReturnedAgent +
            ", agentUpgradeExecutor=" + agentUpgradeExecutor +
            ", agentsScheduledForUpgrade=" + agentsScheduledForUpgrade +
            '}';
  }
}
