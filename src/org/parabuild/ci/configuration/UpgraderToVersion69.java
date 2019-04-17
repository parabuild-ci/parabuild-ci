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
package org.parabuild.ci.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Upgrades to version 69.
 */
final class UpgraderToVersion69 implements SingleStepSchemaUpgrader {

  private static final Log LOG = LogFactory.getLog(UpgraderToVersion69.class); // NOPMD


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null; // NOPMD
    try {
      // create psBuildConfig
      conn.setAutoCommit(true);
      st = conn.createStatement();

      LOG.debug("Prepare");
      PersistanceUtils.executeDDLs(st, new String[]{
              " alter table BUILD_CONFIG add column BUILDER_ID integer default '-1' not null "
      });


      // Get max agent id
      final PreparedStatement psAgentMax = conn.prepareStatement("select max(ID) from AGENT");
      final ResultSet rsMaxAgent = psAgentMax.executeQuery();
      int agentID = rsMaxAgent.next() ? rsMaxAgent.getInt(1) : 0;
      IoUtils.closeHard(rsMaxAgent);
      IoUtils.closeHard(psAgentMax);

      // Get max builder ID
      final PreparedStatement psMaxBuilder = conn.prepareStatement("select max(ID) from CLUSTER");
      final ResultSet rsMaxBuilder = psMaxBuilder.executeQuery();
      int builderID = rsMaxBuilder.next() ? rsMaxBuilder.getInt(1) : 0;
      IoUtils.closeHard(rsMaxBuilder);
      IoUtils.closeHard(psMaxBuilder);

      // Get max builder agent ID
      final PreparedStatement psMaxBuilderAgent = conn.prepareStatement("select max(ID) from CLUSTER_MEMBER");
      final ResultSet rsMaxBuilderAgent = psMaxBuilderAgent.executeQuery();
      int builderAgentID = rsMaxBuilderAgent.next() ? rsMaxBuilderAgent.getInt(1) : 0;
      IoUtils.closeHard(rsMaxBuilderAgent);
      IoUtils.closeHard(psMaxBuilderAgent);


      // Normalize host name
      final PreparedStatement psNormalizeHostName = conn.prepareStatement("update BUILD_CONFIG set BUILDER_HOST = trim(both from lcase(BUILDER_HOST))");
      psNormalizeHostName.executeUpdate();
      IoUtils.closeHard(psNormalizeHostName);


      // Create builders
      final PreparedStatement psBuildConfig = conn.prepareStatement("select distinct(BUILDER_HOST) from BUILD_CONFIG");
      final ResultSet rsBuildConfig = psBuildConfig.executeQuery();
      while (rsBuildConfig.next()) {
        final String builderHost = rsBuildConfig.getString(1);
        if (StringUtils.isBlank(builderHost)) {
          // Local builder
          setBuilder(conn, 0, "");
        } else {
          // Create agent
          final PreparedStatement psInsertAgent = conn.prepareStatement("insert into AGENT (ID, HOST, DESCRIPTION, ENABLED, DELETED, TIMESTAMP) values (?, ?, ?, 'Y', 'N', 0)");
          psInsertAgent.setInt(1, ++agentID);
          psInsertAgent.setString(2, builderHost);
          psInsertAgent.setString(3, "Remote agent automatically created by Parabuild");
          psInsertAgent.executeUpdate();
          IoUtils.closeHard(psInsertAgent);

          // Create builder
          final PreparedStatement psInsertBuilder = conn.prepareStatement("insert into CLUSTER (ID, NAME, DESCR, ENABLED, DELETED, TIMESTAMP) values (?, ?, ?, 'Y', 'N', 0)");
          psInsertBuilder.setInt(1, ++builderID);
          psInsertBuilder.setString(2, BuilderConfigurationManager.hostNameToBuilderName(builderHost));
          psInsertBuilder.setString(3, "Single-agent build farm automatically created by Parabuild for remote agent " + builderHost);
          psInsertBuilder.executeUpdate();
          IoUtils.closeHard(psInsertBuilder);

          // Create builder agent
          final PreparedStatement psInsertBuilderAgent = conn.prepareStatement("insert into CLUSTER_MEMBER (ID, CLUSTER_ID, AGENT_ID, TIMESTAMP) values (?, ?, ?, 0)");
          psInsertBuilderAgent.setInt(1, ++builderAgentID);
          psInsertBuilderAgent.setInt(2, builderID);
          psInsertBuilderAgent.setInt(3, agentID);
          psInsertBuilderAgent.executeUpdate();
          IoUtils.closeHard(psInsertBuilderAgent);

          // Update BUILD_CONFIGs
          setBuilder(conn, builderID, builderHost);
        }
      }
      IoUtils.closeHard(rsBuildConfig);
      IoUtils.closeHard(psBuildConfig);

      LOG.debug("Finish");
      PersistanceUtils.executeDDLs(st, new String[]{
              " alter table BUILD_CONFIG drop column BUILDER_HOST ",
              " alter table BUILD_CONFIG drop column BUILDER_PASSWORD ",
              " alter table BUILD_CONFIG alter column BUILDER_ID drop default ",
              " alter table BUILD_CONFIG add constraint BUILD_CONFIG_FC1 foreign key (BUILDER_ID) references CLUSTER(ID) ON DELETE CASCADE ",
      });


      LOG.debug("Updating Version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");

      // finish
      conn.commit();

      // request post-startup config manager action
      System.setProperty(SystemConstants.SYSTEM_PROPERTY_INIT_ADVANCED_SETTINGS, "true");
    } finally {
      IoUtils.closeHard(st);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  private static void setBuilder(final Connection conn, final int builderID, final String builderHost) throws SQLException {
    final PreparedStatement psUpdateBuildConfig = conn.prepareStatement("update BUILD_CONFIG set BUILDER_ID = ? where BUILDER_HOST=?");
    psUpdateBuildConfig.setInt(1, builderID);
    psUpdateBuildConfig.setString(2, builderHost);
    psUpdateBuildConfig.executeUpdate();
    IoUtils.closeHard(psUpdateBuildConfig);
  }


  public int upgraderVersion() {
    return 69;
  }
}