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
package org.parabuild.ci.webui;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.Version;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.RuntimeUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonBoldLink;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * This class shows information about the product
 */
public final class AboutPage extends BasePage implements StatelessTierlet {

  private static final long serialVersionUID = -7268440360499417996L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(AboutPage.class); // NOPMD

  public AboutPage() {
    setTitle(makeTitle("About"));
  }


  public Result executePage(final Parameters parameters) {

    // content panel
    final Panel cp = super.baseContentPanel().getUserPanel();

    // show cache stats via hidden page parameter.
    if (isShowCacheStatsEnabled(parameters)) {
      final Panel pnlCacheStats = makeCacheStatsPanel();
      pnlCacheStats.setWidth("100%");
      cp.add(pnlCacheStats);
      cp.add(WebuiUtils.makePanelDivider());
    }

    // common details
    final MessagePanel pnlProdInfo = makeProdInfoPanel();
    pnlProdInfo.setWidth(Pages.PAGE_WIDTH);
    cp.add(pnlProdInfo);

    // admin details
    if (isValidAdminUser()) {

      // add overview panel
      final MessagePanel pnlEnvInfo = makeBuildManagerEnvPanel();
      pnlEnvInfo.setWidth(Pages.PAGE_WIDTH);
      cp.add(WebuiUtils.makePanelDivider());
      cp.add(pnlEnvInfo);

      // REVIEWME: vimeshev - 03/29/2004 - temporarely disabled process list (changed to home)
      // untill we are ready with provisioning application re PM.
      // CommonLink lnkProcessList = new CommonLink("Process list", Pages.ADMIN_PROCESS_LIST);

      // shell details
      final MessagePanel pnlShellEnvDetails = makeShellEnvPanel();
      pnlShellEnvDetails.setWidth(Pages.PAGE_WIDTH);
      cp.add(WebuiUtils.makePanelDivider());
      cp.add(pnlShellEnvDetails);

      // JVM details
      final MessagePanel pnlJVMEnvDetails = new EnvironmentPanel(System.getProperties(), "JVM Environment");
      pnlJVMEnvDetails.setWidth(Pages.PAGE_WIDTH);
      cp.add(WebuiUtils.makePanelDivider());
      cp.add(pnlJVMEnvDetails);
    }
    return Result.Done();
  }


  /**
   * Makes product information detail panel
   */
  private static MessagePanel makeProdInfoPanel() {
    final MessagePanel pnlProdInfo = new MessagePanel("Product Information");

    final GridIterator prodInfoGI = new GridIterator(pnlProdInfo.getUserPanel(), 2);
    prodInfoGI.addPair(new CommonLabel("Product:"), new CommonLabel(Version.productName()));
    prodInfoGI.addPair(new CommonLabel("Version:"), new CommonLabel(Version.productVersion()));
    prodInfoGI.addPair(new CommonLabel("Code:"), new CommonLabel(Integer.toString(RuntimeUtils.systemType())));
    prodInfoGI.addPair(new CommonLabel("Release build:"), new CommonLabel(Version.releaseBuild()));
    prodInfoGI.addPair(new CommonLabel("Release date:"), new CommonLabel(Version.releaseDate()));
    prodInfoGI.addPair(new CommonLabel("Release change:"), new CommonLabel(Version.releaseChange()));
    prodInfoGI.addPair(new CommonLabel("Schema version:"), new CommonLabel(Integer.toString(SystemConfigurationManagerFactory.getManager().getSchemaVersion())));
//    prodInfoGI.addPair(new CommonLabel("Tech support e-mail:"), new CommonLink("support@parabuildci.org", "mailto:support@parabuildci.org"));
    prodInfoGI.addPair(new CommonLabel("Parabuild support forum: "), new CommonBoldLink("parabuild.support", "http://forums.parabuildci.org/viewforum.php?f=1"));
    prodInfoGI.addPair(new CommonLabel("Parabuild home: "), new CommonBoldLink("http://www.parabuildci.org", "http://www.parabuildci.org/products/parabuild/index.htm"));
    return pnlProdInfo;
  }


  private MessagePanel makeBuildManagerEnvPanel() {
    final MessagePanel pnlEnvInfo = new MessagePanel("Build Manager Environment");
    final GridIterator envInfoGI = new GridIterator(pnlEnvInfo.getUserPanel(), 2);
    envInfoGI.addPair(new CommonLabel("Parabuild IP address:"), new CommonLabel(getIPAddress()));
    if (LOG.isDebugEnabled()) {
      LOG.debug("RuntimeUtils.isWindows(): " + RuntimeUtils.isWindows());
    }

    if (RuntimeUtils.isWindows()) {
      envInfoGI.addPair(new CommonLabel("Parabuild MAC address(es):"), new CommonLabel(getMACAddresses()));
    }
    envInfoGI.addPair(new CommonLabel("Installation dir:"), new CommonLabel(IoUtils.getCanonicalPathHard(ConfigurationConstants.INSTALL_HOME)));
    envInfoGI.addPair(new CommonLabel("Operating system name:"), new CommonLabel(System.getProperty("os.name")));
    envInfoGI.addPair(new CommonLabel("Operating system architecture:"), new CommonLabel(System.getProperty("os.arch")));
    envInfoGI.addPair(new CommonLabel("Operating system version:"), new CommonLabel(System.getProperty("os.version")));
    envInfoGI.addPair(new CommonLabel("Operating system user's account name:"), new CommonLabel(System.getProperty("user.name")));
    envInfoGI.addPair(new CommonLabel("Java Runtime Environment version:"), new CommonLabel(System.getProperty("java.version")));
    envInfoGI.addPair(new CommonLabel("Java Runtime Environment vendor:"), new CommonLabel(System.getProperty("java.vendor")));
    envInfoGI.addPair(new CommonLabel("Java installation directory:"), new CommonLabel(System.getProperty("java.home")));
    envInfoGI.addPair(new CommonLabel("Java class path:"), new CommonLabel(System.getProperty("java.class.path")));
    envInfoGI.addPair(new CommonLabel("Total heap:"), new CommonLabel(Long.toString(Runtime.getRuntime().totalMemory())));
    envInfoGI.addPair(new CommonLabel("Free heap:"), new CommonLabel(Long.toString(Runtime.getRuntime().freeMemory())));
    return pnlEnvInfo;
  }


  /**
   * @return formatted list of MAC addresses.
   */
  private static String getMACAddresses() {
    final StringBuilder sb = new StringBuilder(100);
    final List macAddressList = RuntimeUtils.getMacAddressList();
    for (int i = 0; i < macAddressList.size(); i++) {
      sb.append((String) macAddressList.get(i));
      if (i < macAddressList.size() - 1) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }


  /**
   * Creates shell details panel
   */
  private static MessagePanel makeShellEnvPanel() {
    final AgentHost host = new AgentHost(AgentConfig.BUILD_MANAGER);
    MessagePanel result;
    try {
      // REVIEWME: currently shows only local host
      final AgentEnvironment agentEnvironment = AgentManager.getInstance().getAgentEnvironment(host);
      result = new EnvironmentPanel(agentEnvironment.getEnv(), "Shell Environment Details");
      return result;
    } catch (final Exception e) {
      result = new MessagePanel("Shell Environment details");
      result.showContentBorder(true);
      result.getUserPanel().add(new CommonLabel("Details for host " + host.getHost() + " are not available: " + StringUtils.toString(e)));
      return result;
    }
  }


  /**
   * Returns true if showing cache stats is enabled.
   *
   * @return true if showing cache stats is enabled.
   */
  private static boolean isShowCacheStatsEnabled(final Parameters parameters) {
    return ParameterUtils.getBooleanParameter(parameters, Pages.PARAM_SHOW_CACHE_STATS);
  }


  /**
   * Creates cache stats panel.
   */
  private static Panel makeCacheStatsPanel() {
    final MessagePanel result = new MessagePanel("Cache statistis");
    try {
      final GridIterator gi = new GridIterator(result.getUserPanel(), 5);
      gi.add(new BoldCommonLabel("Name"))
              .add(new BoldCommonLabel("Hit count"))
              .add(new BoldCommonLabel("Miss count expired"))
              .add(new BoldCommonLabel("Miss count not found"))
              .add(new BoldCommonLabel("Miss percent"))
              ;
      final CacheManager cacheMan = CacheManager.getInstance();
      final String[] cacheNames = cacheMan.getCacheNames();
      Arrays.sort(cacheNames);
      int totalHitCount = 0;
      int totalMissCount = 0;
      for (final String cacheName : cacheNames) {
        // get cache
        final Cache cache = cacheMan.getCache(cacheName);
        final int hitCount = cache.getHitCount();
        final int missCountExpired = cache.getMissCountExpired();
        final int missCountNotFound = cache.getMissCountNotFound();
        final int missCount = missCountExpired + missCountNotFound;
        final int accessCount = missCount + hitCount;
        final int missPercent = accessCount == 0 ? 0 : (missCount * 100) / accessCount;
        totalHitCount += hitCount;
        totalMissCount += missCount;
        // add attrs
        gi.add(new AboutLabel(cacheName));
        gi.add(new AboutLabel(Integer.toString(hitCount)));
        gi.add(new AboutLabel(Integer.toString(missCountExpired)));
        gi.add(new AboutLabel(Integer.toString(missCountNotFound)));
        final CommonLabel lbMissPercent = new AboutLabel(Integer.toString(missPercent));
        lbMissPercent.setAlignX(Layout.RIGHT);
        gi.add(lbMissPercent);
      }
      // add total
      final int totalAccessCount = totalHitCount + totalMissCount;
      final int totalMissPercent = totalAccessCount == 0 ? 0 : (totalMissCount * 100) / totalAccessCount;
      gi.add(new CommonLabel("--- Total -- "));
      gi.add(new CommonLabel(""));
      gi.add(new CommonLabel(""));
      gi.add(new CommonLabel(""));
      gi.add(new CommonLabel(Integer.toString(totalMissPercent)));
    } catch (final CacheException e) {
      result.showErrorMessage("Error getting cache stats: " + e.toString());
    }
    return result;
  }


  private static String getIPAddress() {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (final UnknownHostException e) {
      return "Cannot detect: " + StringUtils.toString(e);
    }
  }
}
