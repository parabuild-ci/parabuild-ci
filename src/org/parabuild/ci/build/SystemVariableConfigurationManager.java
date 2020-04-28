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

import net.sf.hibernate.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.ProjectBuild;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SystemVariableConfigurationManager {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SystemVariableConfigurationManager.class); // NOPMD

  private static final SystemVariableConfigurationManager instance = new SystemVariableConfigurationManager();


  public static SystemVariableConfigurationManager getInstance() {
    return instance;
  }


  /**
   * Returns a map of objects {@link StartParameter} with variable name as a key.
   *
   * @param buildID
   * @param agentHostName
   * @return a map of objects {@link StartParameter} with variable name as a key.
   */
  public Map getCommonVariableMap(final int buildID, final String agentHostName) {
    // Put parameters - order is important
    final ConfigurationManager cm = ConfigurationManager.getInstance();

    // Get active build
    final int activeBuildID = cm.getActiveIDFromBuildID(buildID);

    final Map result = new LinkedHashMap(11);

    // First - system
    final List systemStartParameters = cm.getStartParameters(StartParameterType.SYSTEM, -1);
    addToMap(result, systemStartParameters);

    // Second - project
    final ProjectBuild projectBuild = ProjectManager.getInstance().getProjectBuild(activeBuildID);
    final int projectID = projectBuild.getProjectID();
    final List projectStartParameters = cm.getStartParameters(StartParameterType.PROJECT, projectID);
    addToMap(result, projectStartParameters);

    // Third - agent
    if (!StringUtils.isBlank(agentHostName)) {
      final AgentConfig agentConfig = BuilderConfigurationManager.getInstance().findAgentByHost(agentHostName);
      if (agentConfig != null) {
        addToMap(result, cm.getStartParameters(StartParameterType.AGENT, agentConfig.getID()));
      }
    }

    return result;
  }


  private static void addToMap(final Map parameters, final List systemParameters) {
    for (int i = 0; i < systemParameters.size(); i++) {
      final StartParameter startParameter = (StartParameter) systemParameters.get(i);
      parameters.put(startParameter.getName(), startParameter);
    }
  }


  public int getVariableCount(final byte variableType, final int variableOwner) {
    return ((Number) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery(
                "select count(mrp) from StartParameter as mrp " +
                        " where mrp.buildID = ? " +
                        " and mrp.type = ?");
        q.setInteger(0, variableOwner);
        q.setByte(1, variableType);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    })).intValue();
  }
}
