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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.security.AccessForbiddenException;
import org.parabuild.ci.security.BuildRights;
import org.parabuild.ci.security.SecurityManager;

import java.io.InputStream;
import java.net.URLDecoder;

/**
 * This servlet serves requests to download build results.
 * <p/>
 * The incoming requests are expected to come in the given format:
 * <p/>
 * /parabuild/build/result/<build id>/<step result id>/<path to build result
 * <p/>
 * Example:
 * <p/>
 * /parabuild/build/result/1/499/temp/result_dist/my_product_v_1_0.tar.gz
 * <p/>
 * where:
 * <p/>
 * 1 is a build ID,
 * 499 is a step result id,
 * temp/result_dist/my_product_v_1_0.tar.gz is a path.
 */
public final class ResultDownloadServlet extends AbstractArchiveAccessServlet {

  private static final long serialVersionUID = -8066817583590450367L; // NOPMD
  private static final Log log = LogFactory.getLog(ResultDownloadServlet.class);


  /**
   * Gets an InputStream reading from an archive file based on path
   * info.
   *
   * @param userID
   * @param pathInfo
   */
  protected InputStream getArchiveInputStream(final int userID, final String pathInfo) {
    try {
      // Get build ID
      int beginIndex = 1;
      int nextIndex = pathInfo.indexOf('/', beginIndex);
      if (nextIndex <= beginIndex) return null;
      final String stringBuildID = pathInfo.substring(beginIndex, nextIndex);
      if (StringUtils.isBlank(stringBuildID)) return null;

      // Parse build ID
      int activeBuildID = BuildConfig.UNSAVED_ID;
      try {
        activeBuildID = Integer.parseInt(stringBuildID);
      } catch (final NumberFormatException e) {
        return null;
      }

      // Validate build ID and get build config
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      try {
        final ActiveBuildConfig activeBuildConfig = cm.getActiveBuildConfig(activeBuildID);
        //if (result.isDebugEnabled()) result.debug("buildConfig: " + buildConfig);
        if (activeBuildConfig == null) return null;
      } catch (final Exception e) {
        return null;
      }

      // Validate that user can see results belonging to this
      final BuildRights userBuildRights = SecurityManager.getInstance().getUserBuildRights(userID, activeBuildID);
      if (!userBuildRights.isAllowedToViewBuild()) throw new AccessForbiddenException("Access forbidden");

      // Get result ID
      beginIndex = nextIndex + 1;
      nextIndex = pathInfo.indexOf('/', beginIndex);
      if (nextIndex <= beginIndex) return null;
      final String stringResultID = pathInfo.substring(beginIndex, nextIndex);
      if (StringUtils.isBlank(stringResultID)) return null;
      int resultID = StepResult.UNSAVED_ID;
      try {
        resultID = Integer.parseInt(stringResultID);
        //if (result.isDebugEnabled()) result.debug("resultID: " + resultID);
      } catch (final NumberFormatException e) {
        return null;
      }
      // Validate result ID and get result
      StepResult stepResult = null;
      try {
        stepResult = (StepResult) cm.getObject(StepResult.class, resultID);
        //if (result.isDebugEnabled()) result.debug("stepResult: " + stepResult);
        if (stepResult == null) return null;
      } catch (final Exception e) {
        return null;
      }

      // Get file name
      beginIndex = nextIndex + 1;
      final String urlEncodedRequestFileName = pathInfo.substring(beginIndex);
      //if (result.isDebugEnabled()) result.debug("stringFileName: " + requestFileName);
      if (StringUtils.isBlank(urlEncodedRequestFileName)) return null;

      // Return input stream
      final String requestFileName = URLDecoder.decode(urlEncodedRequestFileName);
      return ArchiveManagerFactory.getArchiveManager(activeBuildID)
              .getArchivedResultInputStream(stepResult, requestFileName);
    } catch (final Exception e) {
      if (log.isDebugEnabled()) log.debug("Exception getting input stream", e);
      return null;
    }
  }
}
