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
package org.parabuild.ci.util;

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.notification.ChangeListURLGenerator;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.StepLog;

/**
 * Makes URL pointing to detailed build status page to be sent by
 * a notification manager.
 */
public final class BuildStatusURLGenerator {

//  private static final String HTTP_PROTOCOL_PREFIX = "http://";


  /**
   * Composes a URL to build status page that is used to send
   * build notifications.
   * <p/>
   * Example: http://host:8080//parabuild/index.htm?detview=true&buildid=1
   *
   * @param buildID
   */
  public String makeBuildStatusURL(final int buildID) {
    final String protocolHostNameAndPort = SystemConfigurationManagerFactory.getManager().getBuildManagerProtocolHostAndPort();
    return makeBuildStatusURL(protocolHostNameAndPort, buildID);
  }


  /**
   * Composes a URL to build status page that is used to send
   * build notifications.
   * <p/>
   * Example: http://host:8080//parabuild/index.htm?detview=true&buildid=1
   *
   * @param buildRun
   */
  public String makeBuildStatusURL(final BuildRun buildRun) {
    return makeBuildStatusURL(buildRun.getActiveBuildID());
  }


  public String makeBuildStatusURL(final String protocolHostNameAndPort, final int buildID) {
    return protocolHostNameAndPort + "/parabuild/index.htm?view=detailed&buildid=" + buildID;
  }


  public String makeBuildStatusURL(final String protocolHostNameAndPort, final BuildRun buildRun) {
    return makeBuildStatusURL(protocolHostNameAndPort, buildRun.getActiveBuildID());
  }


  /**
   * Makes a URL pointing to Parabuild home page (build status list).
   *
   * @return a URL pointing to Parabuild home page (build status list).
   */
  public String makeBuildListURL() {
    final String protocolHostNameAndPort = SystemConfigurationManagerFactory.getManager().getBuildManagerProtocolHostAndPort();
    return protocolHostNameAndPort + "/parabuild/index.htm";
  }


  /**
   * Returns a URL to build run result.
   * <p/>
   * If buil run was successful will return URL to changes
   * else will return URL to last log that normally contains
   * the cause of the build failure.
   *
   * @return URL to build run result
   */
  public String makeBuildRunResultURL(final BuildRun buildRun) {
    if (buildRun.isSuccessful()) {
      final ChangeListURLGenerator changeListURLGenerator = new ChangeListURLGenerator();
      return changeListURLGenerator.makeBuildRunChangesURL(buildRun.getBuildRunID());
    } else {
      // try to get logs
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final StepLog log = cm.getFirstBokenLog(buildRun.getBuildRunID());
      if (log != null) {
        final String protocolHostNameAndPort = SystemConfigurationManagerFactory.getManager().getBuildManagerProtocolHostAndPort();
        return protocolHostNameAndPort + "/parabuild/build/log.htm?logid=" + log.getIDAsString();
      } else {
        final ChangeListURLGenerator changeListURLGenerator = new ChangeListURLGenerator();
        return changeListURLGenerator.makeBuildRunChangesURL(buildRun.getBuildRunID());
      }
    }
  }
}
