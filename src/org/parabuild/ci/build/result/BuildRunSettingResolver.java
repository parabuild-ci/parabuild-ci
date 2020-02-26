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

import org.parabuild.ci.build.BuildScriptGenerator;
import org.parabuild.ci.util.NamedProperty;
import org.parabuild.ci.util.SettingResolver;
import org.parabuild.ci.object.BuildRun;

import java.text.SimpleDateFormat;

/**
 * Resolver for result templates.
 */
public class BuildRunSettingResolver extends SettingResolver {

  public static final String PROPERTY_BUILD_DATE = "build.date";
  public static final String PROPERTY_BUILD_ID = "build.id";
  public static final String PROPERTY_BUILD_NAME = "build.name";
  public static final String PROPERTY_BUILD_NUMBER = "build.number";
  public static final String PROPERTY_BUILD_RUN_ID = "build.run.id";
  public static final String PROPERTY_BUILD_TIMESTAMP = "build.timestamp";
  public static final String PROPERTY_BUILDER_HOST = "builder.host";
  public static final String PROPERTY_CHANGE_LIST_NUMBER = "changelist.number";
  public static final String PROPERTY_STEP_NAME = "step.name";


  /**
   * Constructor. Creates a resolver for source control properties.
   *
   * @param buildID       a build ID.
   * @param agentHostName an agent's host name.
   * @param buildRun
   * @param stepRunName
   */
  public BuildRunSettingResolver(final int buildID, final String agentHostName, final BuildRun buildRun, final String stepRunName) {

    super(buildID, agentHostName);

    // Property definitions
    namedPropertyDefinitions.add(new NamedProperty(PROPERTY_BUILD_DATE, false, true, true));
    namedPropertyDefinitions.add(new NamedProperty(PROPERTY_BUILD_ID, false, true, true));
    namedPropertyDefinitions.add(new NamedProperty(PROPERTY_BUILD_NAME, false, true, true));
    namedPropertyDefinitions.add(new NamedProperty(PROPERTY_BUILD_NUMBER, false, true, true));
    namedPropertyDefinitions.add(new NamedProperty(PROPERTY_BUILD_RUN_ID, false, true, true));
    namedPropertyDefinitions.add(new NamedProperty(PROPERTY_BUILD_TIMESTAMP, false, true, true));
    namedPropertyDefinitions.add(new NamedProperty(PROPERTY_BUILDER_HOST, false, true, true));
    namedPropertyDefinitions.add(new NamedProperty(PROPERTY_CHANGE_LIST_NUMBER, false, true, true));
    namedPropertyDefinitions.add(new NamedProperty(PROPERTY_STEP_NAME, false, true, true));

    // Property values
    namedPropertyValues.put(PROPERTY_BUILD_DATE, new SimpleDateFormat(BuildScriptGenerator.BUILD_DATE_FORMAT).format(buildRun.getStartedAt()));
    namedPropertyValues.put(PROPERTY_BUILD_ID, Integer.toString(buildRun.getActiveBuildID()));
    namedPropertyValues.put(PROPERTY_BUILD_NAME, buildRun.getBuildName());
    namedPropertyValues.put(PROPERTY_BUILD_NUMBER, Integer.toString(buildRun.getBuildRunNumber()));
    namedPropertyValues.put(PROPERTY_BUILD_RUN_ID, Integer.toString(buildRun.getBuildRunID()));
    namedPropertyValues.put(PROPERTY_BUILDER_HOST, agentHostName);
    namedPropertyValues.put(PROPERTY_BUILD_TIMESTAMP, new SimpleDateFormat(BuildScriptGenerator.BUILD_TIMESTAMP_FORMAT).format(buildRun.getStartedAt()));
    namedPropertyValues.put(PROPERTY_CHANGE_LIST_NUMBER, buildRun.getChangeListNumber());
    namedPropertyValues.put(PROPERTY_STEP_NAME, stepRunName);
  }
}
