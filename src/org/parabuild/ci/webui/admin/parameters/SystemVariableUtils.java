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
package org.parabuild.ci.webui.admin.parameters;

import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.object.ProjectBuild;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.services.BuildListService;
import org.parabuild.ci.services.BuildService;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.Parameters;
import viewtier.ui.Tierlet;

import java.util.Iterator;
import java.util.List;

/**
 * BuilderUtils
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 16, 2009 7:31:14 PM
 */
final class SystemVariableUtils {

  private SystemVariableUtils() {
  }


  public static byte getValidType(final Parameters parameters) throws ValidationException {
    if (!parameters.isParameterPresent(Pages.PARAM_VARIABLE_TYPE)) {
      throw new ValidationException("Variable type not found");
    }

    final String stringVariableType = parameters.getParameterValue(Pages.PARAM_VARIABLE_TYPE);
    if (!isValidType(stringVariableType)) {
      throw new ValidationException("Invalid variable type: " + stringVariableType);
    }
    return Byte.parseByte(stringVariableType);
  }


  /**
   * Returns true if the owner is valid.
   *
   * @param variableType
   * @param variableOwner @return true if the owner is valid.
   */
  private static boolean isValidOwner(final int variableType, final String variableOwner) {
    if (!StringUtils.isValidInteger(variableOwner)) {
      return false;
    }
    final int ownerID = Integer.parseInt(variableOwner);
    if (variableType == StartParameter.TYPE_AGENT) {
      return BuilderConfigurationManager.getInstance().getAgentConfig(ownerID) != null;
    } else if (variableType == StartParameter.TYPE_PROJECT) {
      return ProjectManager.getInstance().getProject(ownerID) != null;
    } else if (variableType == StartParameter.TYPE_SYSTEM) {
      return ownerID == -1;
    } else {
      throw new IllegalArgumentException("Unknown variable type: " + variableType);
    }
  }


  /**
   * Returns true if the type is valid.
   *
   * @param variableType
   * @return true if the type is valid.
   */
  private static boolean isValidType(final String variableType) {
    if (!StringUtils.isValidInteger(variableType)) {
      return false;
    }
    final byte byteType = Byte.parseByte(variableType);

    return byteType == StartParameter.TYPE_AGENT || byteType == StartParameter.TYPE_PROJECT
            || byteType == StartParameter.TYPE_SYSTEM;
  }


  public static int getValidOwner(final byte variableType, final Parameters parameters) throws ValidationException {
    if (!parameters.isParameterPresent(Pages.PARAM_VARIABLE_OWNER)) {
      throw new ValidationException("Variable owner not found");
    }

    final String stringVariableOwner = parameters.getParameterValue(Pages.PARAM_VARIABLE_OWNER);
    if (!isValidOwner(variableType, stringVariableOwner)) {
      throw new ValidationException("Invalid variable owner: " + stringVariableOwner + ", type: " + variableType);
    }
    return Integer.parseInt(stringVariableOwner);
  }


  public static Tierlet.Result createReturnToVariableList(final byte variableType, final int variableOwner) {
    final Parameters parameters = new Parameters();
    parameters.addParameter(Pages.PARAM_VARIABLE_TYPE, variableType);
    parameters.addParameter(Pages.PARAM_VARIABLE_OWNER, variableOwner);
    return Tierlet.Result.Done(Pages.PAGE_VARIABLE_LIST, parameters);
  }


  /**
   * Notifies concerned builds that their configurations changed.
   *
   * @param variableType
   * @param variableOwner
   */
  public static void notifyConfigurationChanged(final byte variableType, final int variableOwner) {
    final BuildListService buildListService = ServiceManager.getInstance().getBuildListService();
    ServiceManager.getInstance().getSupportService().executeTask(new Runnable() {
      public void run() {
        if (variableType == StartParameter.TYPE_SYSTEM) {
          // Notify all
          for (final Iterator iter = buildListService.getBuilds().iterator(); iter.hasNext();) {
            ((BuildService) iter.next()).notifyConfigurationChanged();
          }
        } else if (variableType == StartParameter.TYPE_PROJECT) {
          // Notify project builds
          final List projectBuilds = ProjectManager.getInstance().getProjectBuilds(variableOwner);
          for (int i = 0; i < projectBuilds.size(); i++) {
            final ProjectBuild projectBuild = (ProjectBuild) projectBuilds.get(i);
            buildListService.getBuild(projectBuild.getActiveBuildID()).notifyConfigurationChanged();
          }
        } else if (variableType == StartParameter.TYPE_AGENT) {
          // Notify agent builds.
          final List buildIDs = BuilderConfigurationManager.getInstance().getBuildConfigIDsForAgent(variableOwner);
          for (int i = 0; i < buildIDs.size(); i++) {
            final Integer buildID = (Integer) buildIDs.get(i);
            buildListService.getBuild(buildID).notifyConfigurationChanged();
          }
        }
      }
    });
  }

}