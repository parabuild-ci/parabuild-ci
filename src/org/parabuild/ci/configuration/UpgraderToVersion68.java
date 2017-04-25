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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Upgrades to version 68.
 */
final class UpgraderToVersion68 implements SingleStepSchemaUpgrader {

  private static final Log LOG = LogFactory.getLog(UpgraderToVersion68.class); // NOPMD


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null; // NOPMD
    try {
      // create statement
      conn.setAutoCommit(true);
      st = conn.createStatement();

      LOG.debug("Altering table");
      PersistanceUtils.executeDDLs(st, new String[]{
              //
              // AGENT table
              //
              " create cached table AGENT (" +
                      "    ID integer not null identity," +
                      "    HOST varchar(80) not null," +
                      "    DESCRIPTION varchar(300) not null," +
                      "    ENABLED char(1) not null," +
                      "    DELETED char(1) not null," +
                      "    TIMESTAMP bigint not null," +
                      "    constraint AGENT_UC1 unique (ID)" +
                      ") ",
              //
              // AGENT_ATTRIBUTE table
              //
              "create cached table AGENT_ATTRIBUTE (" +
                      "    AGENT_ID integer not null," +
                      "    ID integer not null identity," +
                      "    NAME varchar(80) not null," +
                      "    VALUE varchar(1024)," +
                      "    TIMESTAMP bigint not null," +
                      "    constraint AGENT_ATTRIBUTE_UC1 unique (ID)," +
                      "    constraint AGENT_ATTRIBUTE_UC2 unique (AGENT_ID, NAME)," +
                      "    constraint AGENT_ATTRIBUTE_FC1 foreign key (AGENT_ID) references AGENT(ID) ON DELETE CASCADE" +
                      ')',
              //
              // CLUSTER_MEMBER_ATTRIBUTE table
              //
              "create cached table CLUSTER_MEMBER_ATTRIBUTE (" +
                      "    CLUSTER_MEMBER_ID integer not null," +
                      "    ID integer not null identity," +
                      "    NAME varchar(80) not null," +
                      "    VALUE varchar(1024)," +
                      "    TIMESTAMP bigint not null," +
                      "    constraint CLUSTER_MEMBER_ATTRIBUTE_UC1 unique (ID)," +
                      "    constraint CLUSTER_MEMBER_ATTRIBUTE_UC2 unique (CLUSTER_MEMBER_ID, NAME)," +
                      "    constraint CLUSTER_MEMBER_ATTRIBUTE_FC1 foreign key (CLUSTER_MEMBER_ID) references CLUSTER_MEMBER(ID) ON DELETE CASCADE" +
                      ')' +
              //
              // CLUSTER table
              //
              "  alter table CLUSTER add column DELETED char(1) default 'N' not null ",
              //
              // CLUSTER_MEMBER
              //
              "  alter table CLUSTER_MEMBER drop constraint CLUSTER_MEMBER_UC2 ",
              "  alter table CLUSTER_MEMBER drop column BUILDER_HOST ",
              "  alter table CLUSTER_MEMBER drop column ENABLED ",
              "  alter table CLUSTER_MEMBER add column AGENT_ID integer default '-1' not null ",
              "  alter table CLUSTER_MEMBER add constraint CLUSTER_MEMBER_FC1 foreign key (AGENT_ID) references AGENT(ID) ON DELETE CASCADE ",
              "  alter table CLUSTER_MEMBER add constraint CLUSTER_MEMBER_UC2 unique (CLUSTER_ID, AGENT_ID) ",
              "  insert into AGENT (ID, HOST, DESCRIPTION, ENABLED, DELETED, TIMESTAMP) values (0, '<Build Manager>', 'Agent that runs on the build manager machine', 'Y', 'N', 0) ",
              "  insert into CLUSTER (ID, NAME, DESCR, ENABLED, DELETED, TIMESTAMP) values (0, 'Local Build Farm', 'Single-agent build farm that runs builds on the build manager machine', 'Y', 'N', 0) ",
              "  insert into CLUSTER_MEMBER (ID, CLUSTER_ID, AGENT_ID, TIMESTAMP) values (0, 0, 0, 0) ",
      });

      LOG.debug("Updating version");
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


  public int upgraderVersion() {
    return 68;
  }
}
