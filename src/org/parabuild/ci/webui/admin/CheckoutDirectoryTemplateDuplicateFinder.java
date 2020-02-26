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
package org.parabuild.ci.webui.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.SourceControlSettingResolver;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ValidationException;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.remote.AgentManager;

import java.util.Iterator;
import java.util.List;

/**
 * Find duplicate templates for custom checkout directories.
 */
public final class CheckoutDirectoryTemplateDuplicateFinder {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(CheckoutDirectoryTemplateDuplicateFinder.class); // NOPMD

  private final int activeBuildID;
  private final String host;
  private final String template;
  private static final String TEST_BUILD_NAME = "test_name_build_name";


  public CheckoutDirectoryTemplateDuplicateFinder(final int activeBuildID, final String host, final String template) {
    this.activeBuildID = activeBuildID;
    this.host = ((String) ArgumentValidator.validateArgumentNotNull(host, "host")).trim();
    this.template = ArgumentValidator.validateArgumentNotBlank(template, "template");
  }


  public String find() throws ValidationException {

    // generator w/test values
    final SourceControlSettingResolver nameGenerator = new SourceControlSettingResolver(TEST_BUILD_NAME, activeBuildID, host);
    final String checkoutDirectory = nameGenerator.resolve(template).trim().replace('\\', '/');
    if (log.isDebugEnabled()) {
      log.debug("checkoutDirectory: " + checkoutDirectory);
    }
    // traverse builds
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    // NOTE: vimeshev - 01/21/2007 - here we check only existing
    // builds, so if there was a build with the same template
    // but it was deleted, we will not find duplicate.
    final List builds = cm.getExistingBuildConfigurationsIDs();
    for (final Iterator i = builds.iterator(); i.hasNext();) {
      final int id = ((Number) i.next()).intValue();
      if (id == activeBuildID) {
        continue; // skip self
      }
      final BuildConfig theirBuildConfig = cm.getBuildConfiguration(id);
      final String theirTemplate = cm.getSourceControlSettingValue(theirBuildConfig.getBuildID(), VCSAttribute.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE, null);
      if (StringUtils.isBlank(theirTemplate)) {
        continue; // skip empty
      }
      final SourceControlSettingResolver theirNameGenerator = new SourceControlSettingResolver(theirBuildConfig.getBuildName(), theirBuildConfig.getActiveBuildID(), host);
      final String theirCheckoutDirectory = theirNameGenerator.resolve(theirTemplate).trim().replace('\\', '/');
      if (log.isDebugEnabled()) {
        log.debug("activeBuildID: " + activeBuildID);
      }
      if (log.isDebugEnabled()) {
        log.debug("id: " + id);
      }
      if (log.isDebugEnabled()) {
        log.debug("theirCheckoutDirectory: " + theirCheckoutDirectory);
      }
      final List agentHosts = AgentManager.getInstance().getLiveAgentHosts(theirBuildConfig.getBuilderID(), true);
      for (int j = 0; j < agentHosts.size(); j++) {
        final AgentHost agentHost = (AgentHost) agentHosts.get(j);
        if (theirCheckoutDirectory.equals(checkoutDirectory) && agentHost.getHost().trim().equals(host)) {
          return theirBuildConfig.getBuildName();
        }
      }
    }
    return null;
  }


  public String toString() {
    return "CheckoutDirectoryTemplateDuplicateFinder{" +
            "activeBuildID=" + activeBuildID +
            ", host='" + host + '\'' +
            ", template='" + template + '\'' +
            '}';
  }
}
