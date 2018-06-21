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
package org.parabuild.ci.build.result;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.ResultConfigProperty;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.remote.Agent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Handles external URL result.
 */
public final class URLResultHandler extends AbstractResultHandler {

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(URLResultHandler.class); // NOPMD


  /**
   * Constructor
   */
  public URLResultHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                          final String projectHome, final ResultConfig resultConfig, final int stepRunID) {

    super(agent, buildRunConfig, projectHome, resultConfig, stepRunID, false);
  }


  /**
   * @return byte result type that this handler can process.
   */
  protected byte resultType() {
    return ResultConfig.RESULT_TYPE_URL;
  }


  /**
   * Concrete processing for single text file.
   *
   * @param builderTimeStamp
   * @param fullyQualifiedResultPath
   * @param resolvedResultPath
   */
  protected void processResult(final long builderTimeStamp, final String fullyQualifiedResultPath,
                               final String resolvedResultPath) {

    // check connection if necessary
    final ResultConfigProperty rcp = cm.getResultConfigProperty(resultConfig.getID(), ResultConfigProperty.ATTR_TEST_URL);
    if (rcp != null && rcp.getValue().equals(ResultConfigProperty.OPTION_CHECKED)) {
      InputStream testInputStream = null;
      try {
        final URLConnection urlConnection = new URL(resolvedResultPath).openConnection();
        urlConnection.setUseCaches(false);
        testInputStream = urlConnection.getInputStream();
      } catch (final IOException e) {
        return; // could not create a connection
      } finally {
        IoUtils.closeHard(testInputStream);
      }
    }
    // save into db
    final StepResult stepResult = new StepResult();
    stepResult.setStepRunID(super.stepRunID);
    stepResult.setDescription(super.resultConfig.getDescription());
    stepResult.setPath(resolvedResultPath);
    stepResult.setArchiveFileName("");
    stepResult.setPathType(StepResult.PATH_TYPE_EXTERNAL_URL);
    stepResult.setPinned(pinResult);
    stepResult.setFound(true);
    cm.saveObject(stepResult);

    // auto-publish if neccessary
    publish(stepResult);
  }


  /**
   * Implementation of the isResultAlreadyArchived.
   *
   * @param archivedBuildRunResults  List of already stored and archived
   *                                 results.
   * @param builderTimeStamp
   * @param fullyQualifiedResultPath
   * @return true if result was already arhived.
   * @see org.parabuild.ci.build.result.AbstractResultHandler#isResultAlreadyArchived
   */
  protected boolean isResultAlreadyArchived(final List archivedBuildRunResults, final long builderTimeStamp,
                                            final String fullyQualifiedResultPath) {
    // NOTE: vimeshev - 06/22/2005 - the fact that we have gotten
    // called means that there was this type and this path. We
    // return true - it means that any second attempt to process
    // same just existsing dir is considered sthe same.
    return !archivedBuildRunResults.isEmpty();
  }
}
