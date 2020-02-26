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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is responsible for generation of checkout
 * directory names from templates.
 */
public final class SourceControlSettingResolver extends SettingResolver {

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SourceControlSettingResolver.class); // NOPMD

  private static final String PROPERTY_BUILD_NAME = "build.name";
  private static final String PROPERTY_BUILD_ID = "build.id";
  private static final String PROPERTY_BUILDER_HOST = "builder.host";


  /**
   * Creates a new resolver for source control properties.
   *
   * @param buildName     a build name.
   * @param activeBuildID an active build ID
   * @param agentHostName an agent's host name.
   */
  public SourceControlSettingResolver(final String buildName, final int activeBuildID, final String agentHostName) {
    
    super(activeBuildID, agentHostName);
    namedPropertyDefinitions.add(new NamedProperty(PROPERTY_BUILDER_HOST, false, true, true));
    namedPropertyDefinitions.add(new NamedProperty(PROPERTY_BUILD_NAME, false, true, true));
    namedPropertyDefinitions.add(new NamedProperty(PROPERTY_BUILD_ID, false, true, true));
    namedPropertyValues.put(PROPERTY_BUILD_ID, Integer.toString(activeBuildID));
    namedPropertyValues.put(PROPERTY_BUILDER_HOST, agentHostName);
    namedPropertyValues.put(PROPERTY_BUILD_NAME, buildName);
  }
}
