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

import java.util.*;

import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.ResultGroupManager;
import org.parabuild.ci.error.*;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.object.PublishedStepResult;

/**
 * This class is responsible for adjusting the publishing
 * dates for results for the given build run to be uniformly
 * set to build run finishing time.
 */
public final class PublishedResultDateAdjuster implements BuildFinishedSubscriber {

  public void buildFinished(final BuildFinishedEvent buildFinishedEvent) {
    try {
      final ResultGroupManager rgm = ResultGroupManager.getInstance();
      final List publishedResults = rgm.getPublishedStepResults(buildFinishedEvent.getBuildRunID());
      for (int i = 0, n = publishedResults.size(); i < n; i++) {
        final PublishedStepResult result = (PublishedStepResult)publishedResults.get(i);
        result.setPublishDate(buildFinishedEvent.getBuildFinishedAt());
        rgm.save(result);
      }
    } catch (final Exception e) {
      ErrorManagerFactory.getErrorManager().reportSystemError(new Error("Error while adjusting published result dates: " + StringUtils.toString(e), e));
    }
  }
}
