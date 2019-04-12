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
package org.parabuild.ci.build;

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.services.BuildStartRequest;
import org.parabuild.ci.services.BuildStartRequestParameter;

import java.util.List;

/**
 * BuildStartRequestBuilder encapsulates creation of a
 * {@link BuildStartRequest} be be handed to the {@link
 * BuildRunner}.
 * <p/>
 * This class is used by schedulers.
 *
 * @see AutomaticScheduler
 * @see RepeatableScheduler
 */
final class BuildStartRequestBuilder {

  /**
   * Creates {@link BuildStartRequest} be be handed to the
   * {@link BuildRunner}.
   * <p/>
   * This class is used by schedulers.
   *
   * @see AutomaticScheduler
   * @see RepeatableScheduler
   */
  public BuildStartRequest makeStartRequest(final int activeBuildID, final int buildChangeList,
                                            final BuildStartRequest runOnceRequest) {
    final BuildStartRequest startRequest;
    if (runOnceRequest == null) {
      startRequest = new BuildStartRequest(-1, buildChangeList);
      // is adding default parameter values required?
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      if (cm.getBuildAttributeValue(activeBuildID, BuildConfigAttribute.USE_FIRST_PARAMETER_VALUE_AS_DEFAULT,
              BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED)) {
        // yes, set default parameters
        final List configurationStartParameters = cm.getStartParameters(startRequest.isPublishingRun() ? StartParameterType.PUBLISH : StartParameterType.BUILD, activeBuildID);
        for (int i = 0; i < configurationStartParameters.size(); i++) {
          final StartParameter configuredParameter = (StartParameter) configurationStartParameters.get(i);
          final String firstValue = configuredParameter.getFirstValue();
          if (firstValue != null) {
            startRequest.addParameter(new BuildStartRequestParameter(configuredParameter.getName(),
                    configuredParameter.getDescription(), firstValue, configuredParameter.getOrder()));
          }
        }
      }
    } else {
      startRequest = new BuildStartRequest(runOnceRequest, buildChangeList);
    }
    return startRequest;
  }
}
