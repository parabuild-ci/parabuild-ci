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

import java.io.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.archive.*;
import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.*;
import org.parabuild.ci.security.SecurityManager;

/**
 * This servlet serves requests to view HTML logs.
 */
public final class HTMLLogServlet extends AbstractArchiveAccessServlet {

  private static final long serialVersionUID = -4173162628420108092L; // NOPMD
  private static final Log log = LogFactory.getLog(HTMLLogServlet.class);


  /**
   * Gets an InputStream reading from an archive file based on
   * path info.
   *
   * @param userID
   * @param pathInfo
   *
   */
  protected InputStream getArchiveInputStream(final int userID, final String pathInfo) {
    try {
      // get build ID
      int beginIndex = 1;
      int nextIndex = pathInfo.indexOf('/', beginIndex);
      if (nextIndex <= beginIndex) return null;
      final String stringBuildID = pathInfo.substring(beginIndex, nextIndex);
      if (StringUtils.isBlank(stringBuildID)) return null;

      // parse build ID
      int activeBuildID = BuildConfig.UNSAVED_ID;
      try {
        activeBuildID = Integer.parseInt(stringBuildID);
      } catch (NumberFormatException e) {
        return null;
      }

      // validate build ID and get build config
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      ActiveBuildConfig activeBuildConfig = null;
      try {
        activeBuildConfig = cm.getActiveBuildConfig(activeBuildID);
        //if (log.isDebugEnabled()) log.debug("buildConfig: " + buildConfig);
        if (activeBuildConfig == null) return null;
      } catch (Exception e) {
        return null;
      }

      // validate that user can see results belonging to this
      final BuildRights userBuildRights = SecurityManager.getInstance().getUserBuildRights(userID, activeBuildID);
      if (!userBuildRights.isAllowedToViewBuild()) throw new AccessForbiddenException("Access forbidden");

      // get log ID
      beginIndex = nextIndex + 1;
      nextIndex = pathInfo.indexOf('/', beginIndex);
      if (nextIndex <= beginIndex) return null;
      final String stringLogID = pathInfo.substring(beginIndex, nextIndex);
      if (StringUtils.isBlank(stringLogID)) return null;
      int logID = StepLog.UNSAVED_ID;
      try {
        logID = Integer.parseInt(stringLogID);
        //if (log.isDebugEnabled()) log.debug("logID: " + logID);
      } catch (NumberFormatException e) {
        return null;
      }
      // validate log ID and get log
      StepLog stepLog = null;
      try {
        stepLog = (StepLog)cm.getObject(StepLog.class, logID);
        //if (log.isDebugEnabled()) log.debug("stepLog: " + stepLog);
        if (stepLog == null) return null;
      } catch (Exception e) {
        return null;
      }

      // get file name
      beginIndex = nextIndex + 1;
      final String requestFileName = pathInfo.substring(beginIndex);
      //if (log.isDebugEnabled()) log.debug("stringFileName: " + requestFileName);
      if (StringUtils.isBlank(requestFileName)) return null;

      // return input stream
      return ArchiveManagerFactory.getArchiveManager(activeBuildID)
        .getArchivedLogInputStream(stepLog, requestFileName);
    } catch (Exception e) {
      if (log.isDebugEnabled()) log.debug("Exception getting input stream", e);
      return null;
    }
  }
}
