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
package org.parabuild.ci.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.SystemVariableConfigurationManager;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.object.StartParameterType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for generation of checkout
 * directory names from templates.
 */
public class SettingResolver {

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SettingResolver.class); // NOPMD

  protected final List namedPropertyDefinitions = new ArrayList(11);
  protected final Map namedPropertyValues = new HashMap(11);


  /**
   * Constructor. Creates a resolver for source control properties.
   *
   * @param buildID       a build ID.
   * @param agentHostName an agent's host name.
   */
  public SettingResolver(final int buildID, final String agentHostName) {

    final ConfigurationManager cm = ConfigurationManager.getInstance();

    // Get active build
    final int activeBuildID = ConfigurationManager.getInstance().getActiveIDFromBuildID(buildID);

    // Get variables
    final Map variables = new HashMap(11);
    if (activeBuildID == buildID) {
      // Active build resolution

      // Pull the variables from the active build configuration
      final Map common = SystemVariableConfigurationManager.getInstance().getCommonVariableMap(buildID, agentHostName);
      for (final Iterator iterator = common.values().iterator(); iterator.hasNext();) {
        final StartParameter parameter = (StartParameter) iterator.next();
        variables.put(parameter.getName(), parameter.getRuntimeValue());
      }

      // Get build-specific parameters
      if (isUseFirstParameterValueAsDefault(buildID)) {
        final List configuredParameters = cm.getStartParameters(StartParameterType.BUILD, buildID);
        for (int i = 0; i < configuredParameters.size(); i++) {
          final StartParameter param = (StartParameter) configuredParameters.get(i);
          final String firstValue = param.getFirstValue();
          if (firstValue != null) {
            variables.put(param.getName(), firstValue);
          }
        }
      }
    } else {
      // Config resolution, get runtime value
      final List configuredParameters = cm.getStartParameters(StartParameterType.BUILD, buildID);
      for (int i = 0; i < configuredParameters.size(); i++) {
        final StartParameter param = (StartParameter) configuredParameters.get(i);
        final String runtimeValue = param.getRuntimeValue();
        if (!StringUtils.isBlank(runtimeValue)) {
          variables.put(param.getName(), runtimeValue);
        }
      }
    }

    // Populate property definitions and value
    for (Iterator iterator = variables.entrySet().iterator(); iterator.hasNext();) {
      final Map.Entry entry = (Map.Entry) iterator.next();
      namedPropertyDefinitions.add(new NamedProperty((String) entry.getKey(), false, true, true));
      namedPropertyValues.put(entry.getKey(), entry.getValue());
    }
  }


  /**
   * Makes build version using provided parameters.
   *
   * @param template a template.
   * @return the result.
   * @throws ValidationException if the template is invalid.
   */
  public String resolve(final String template) throws ValidationException {

    final NamedProperty[] definitionsAsArray = (NamedProperty[]) namedPropertyDefinitions.toArray(new NamedProperty[namedPropertyDefinitions.size()]);
    final NamedPropertyStringGenerator generator = new NamedPropertyStringGenerator(definitionsAsArray, template, false);
    for (final Iterator iterator = namedPropertyValues.entrySet().iterator(); iterator.hasNext();) {

      final Map.Entry entry = (Map.Entry) iterator.next();
      generator.setPropertyValue(entry.getKey().toString(), entry.getValue().toString());
    }
    return generator.generate();
  }


  private boolean isUseFirstParameterValueAsDefault(final int buildID) {

    return ConfigurationManager.getInstance().getBuildAttributeValue(buildID,
            BuildConfigAttribute.USE_FIRST_PARAMETER_VALUE_AS_DEFAULT, BuildConfigAttribute.OPTION_UNCHECKED)
            .equals(BuildConfigAttribute.OPTION_CHECKED);
  }


  public String toString() {
    return "SettingResolver{" +
            "namedPropertyDefinitions=" + namedPropertyDefinitions +
            ", namedPropertyValues=" + namedPropertyValues +
            '}';
  }
}