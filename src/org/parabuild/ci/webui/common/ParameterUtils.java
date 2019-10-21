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
package org.parabuild.ci.webui.common;

import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.DisplayGroupManager;
import org.parabuild.ci.configuration.ResultGroupManager;
import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.object.ActiveBuild;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.ActiveMergeConfiguration;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuilderAgent;
import org.parabuild.ci.object.BuilderConfiguration;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.object.Group;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.object.Project;
import org.parabuild.ci.object.ResultGroup;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.object.User;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.security.SecurityManager;
import viewtier.ui.Parameters;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Parameters helper class
 */
public final class ParameterUtils {

  private ParameterUtils() {
  }


  /**
   * Parses build ID
   *
   * @return build ID. If build id is missing, or malformed,
   *         returns 0.
   */
  public static ActiveBuildConfig getActiveBuildConfigFromParameters(final Parameters params) {
    final Integer buildID = getActiveBuildIDFromParameters(params);
    if (buildID == null) {
      return null;
    }
    return ConfigurationManager.getInstance().getActiveBuildConfig(buildID);
  }


  public static ActiveBuild getActiveBuildFromParameters(final Parameters params) {
    final Integer buildID = getActiveBuildIDFromParameters(params);
    if (buildID == null) {
      return null;
    }
    return ConfigurationManager.getInstance().getActiveBuild(buildID);
  }


  /**
   * Parses user ID and returns User
   *
   * @return user or null if not found
   */
  public static User getUserFromParameters(final Parameters params) {
    final Integer userID = getIntegerParameter(params, Pages.PARAM_USER_ID, null);
    if (userID == null) {
      return null;
    }
    return SecurityManager.getInstance().getUser(userID);
  }


  public static Group getGroupFromParameters(final Parameters params) {
    final Integer groupID = getIntegerParameter(params, Pages.PARAM_GROUP_ID, null);
    if (groupID == null) {
      return null;
    }
    return SecurityManager.getInstance().getGroup(groupID);
  }


  public static Integer getActiveBuildIDFromParameters(final Parameters params) {
    return getIntegerParameter(params, Pages.PARAM_BUILD_ID, null);
  }


  /**
   * @param params
   * @param paramName
   * @param defaultValue
   * @return Integer build parameter value
   */
  public static Integer getIntegerParameter(final Parameters params, final String paramName, final Integer defaultValue) {
    final String paramValue = params.getParameterValue(paramName);
    if (paramValue == null) {
      return defaultValue;
    }
    try {
      return new Integer(Integer.parseInt(paramValue));
    } catch (final NumberFormatException e) {
      return defaultValue;
    }
  }


  public static BuildRun getBuildRunFromParameters(final Parameters params) {
    final int runID = getIntegerParameter(params, Pages.PARAM_BUILD_RUN_ID, new Integer(BuildRun.UNSAVED_ID));
    if (runID == BuildRun.UNSAVED_ID) {
      return null;
    }
    return ConfigurationManager.getInstance().getBuildRun(runID);
  }


  /**
   * Returns -1 if parameter not found
   */
  public static int getLogIDFromParameters(final Parameters params) {
    return getIntegerParameter(params, Pages.PARAM_LOG_ID, new Integer(-1));
  }


  /**
   * Returns -1 if parameter not found
   */
  public static int getFileIDFromParameters(final Parameters params) {
    return getIntegerParameter(params, Pages.PARAM_FILE_ID, new Integer(-1));
  }


  public static boolean getBooleanParameter(final Parameters params, final String paramName) {
    return params.isParameterPresent(paramName)
            && "true".equalsIgnoreCase(params.getParameterValue(paramName));
  }


  public static DisplayGroup getDisplayGroupFromParameters(final Parameters params) {
    final Integer displayGroupID = getDisplayGroupIDFromParameters(params);
    if (displayGroupID == null) {
      return null;
    }
    return DisplayGroupManager.getInstance().getDisplayGroup(displayGroupID.intValue());
  }


  public static Integer getDisplayGroupIDFromParameters(final Parameters params) {
    return getIntegerParameter(params, Pages.PARAM_DISPLAY_GROUP_ID, null);
  }


  public static BuilderConfiguration getBuilderFromParameters(final Parameters params) {
    final Integer builderID = getIntegerParameter(params, Pages.PARAM_BUILDER_ID, null);
    if (builderID == null) {
      return null;
    }
    return BuilderConfigurationManager.getInstance().getBuilder(builderID);
  }


  public static ResultGroup getResultGroupFromParameters(final Parameters params) {
    final Integer resultGroupID = getIntegerParameter(params, Pages.PARAM_RESULT_GROUP_ID, null);
    if (resultGroupID == null) {
      return null;
    }
    return ResultGroupManager.getInstance().getResultGroup(resultGroupID);
  }


  public static boolean getEditFromParameters(final Parameters params) {
    return getBooleanParameter(params, Pages.PARAM_EDIT);
  }


  public static Properties makeBuildRunResultsParameters(final int buildRunID, final Boolean editMode) {
    final Properties editLinkParams = new Properties();
    editLinkParams.setProperty(Pages.PARAM_BUILD_RUN_ID, Integer.toString(buildRunID));
    editLinkParams.setProperty(Pages.PARAM_EDIT, editMode.toString());
    return editLinkParams;
  }


  public static Parameters propertiesToParameters(final Properties props) {
    final Parameters result = new Parameters();
    for (final Iterator i = props.entrySet().iterator(); i.hasNext();) {
      final Map.Entry entry = (Map.Entry) i.next();
      result.addParameter(entry.getKey().toString(), entry.getValue().toString());
    }
    return result;
  }


  public static Integer getPublishedResultIDFromParameters(final Parameters params) {
    return getIntegerParameter(params, Pages.PARAM_PUBLISHED_RESULT_ID, null);
  }


  public static Project getProjectFromParameters(final Parameters params) {
    final Integer projectID = getIntegerParameter(params, Pages.PARAM_PROJECT_ID, null);
    if (projectID == null) {
      return null;
    }
    return ProjectManager.getInstance().getProject(projectID);
  }


  public static Integer getActiveMergeIDFromParameters(final Parameters params) {
    return getIntegerParameter(params, Pages.PARAM_MERGE_ID, null);
  }


  public static ActiveMergeConfiguration getActiveMergeConfigurationFromParameters(final Parameters params) {
    final int id = getIntegerParameter(params, Pages.PARAM_MERGE_ID, new Integer(MergeConfiguration.UNSAVED_ID));
    if (id == MergeConfiguration.UNSAVED_ID) {
      return null;
    }
    return MergeManager.getInstance().getActiveMergeConfiguration(id);
  }


  public static AgentConfig getAgentFromParameters(final Parameters params) {
    final Integer configID = getIntegerParameter(params, Pages.PARAM_AGENT_ID, null);
    if (configID == null) {
      return null;
    }
    return BuilderConfigurationManager.getInstance().getAgentConfig(configID);
  }


  public static StartParameter getStartParameterFromParameters(final Parameters params) {
    final Integer parameterID = getIntegerParameter(params, Pages.PARAM_VARIABLE_ID, null);
    if (parameterID == null) {
      return null;
    }
    return ConfigurationManager.getInstance().getStartParameter(parameterID);
  }


  public static BuilderAgent getBuilderAgentFromParameters(final Parameters parameters) {
    final Integer configID = getIntegerParameter(parameters, Pages.PARAM_BUILDER_AGENT_ID, null);
    if (configID == null) {
      return null;
    }
    return BuilderConfigurationManager.getInstance().getBuilderAgent(configID);
  }
}
