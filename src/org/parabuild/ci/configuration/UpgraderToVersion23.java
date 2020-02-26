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

import java.sql.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.util.*;

/**
 * Upgrades to version 23
 */
final class UpgraderToVersion23 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion23.class);


  /**
   * Perform upgrade.
   */
  public void upgrade(final Connection conn, final int upgradeToVersion) throws SQLException {
    final boolean savedAutoCommit = conn.getAutoCommit();
    Statement st = null; // NOPMD
    try {
      conn.setAutoCommit(false);
      final String[] ddls = {
        "create cached table CLUSTER (" +
          "  ID integer not null identity," +
          "  NAME varchar(254) not null," +
          "  DESCR varchar(254) not null," +
          "  ENABLED char(1) not null," +
          "  TIMESTAMP bigint not null," +
          "  constraint CLUSTER_UC1 unique (ID)," +
          "  constraint CLUSTER_UC2 unique (NAME)" +
          ')',

        "create cached table CLUSTER_ATTRIBUTE (" +
          "  CLUSTER_ID integer not null," +
          "  ID integer not null identity," +
          "  NAME varchar(80) not null," +
          "  VALUE varchar(1024)," +
          "  TIMESTAMP bigint not null," +
          "  constraint CLUSTER_ATTRIBUTE_UC1 unique (ID)," +
          "  constraint CLUSTER_ATTRIBUTE_UC2 unique (CLUSTER_ID, NAME)," +
          "  constraint CLUSTER_ATTRIBUTE_FC1 foreign key (CLUSTER_ID) references CLUSTER(ID) ON DELETE CASCADE" +
          ')',

        "create cached table CLUSTER_MEMBER (" +
          "  ID integer not null identity," +
          "  CLUSTER_ID integer not null," +
          "  BUILDER_HOST varchar(80) not null," +
          "  ENABLED char(1) not null," +
          "  TIMESTAMP bigint not null," +
          "  constraint CLUSTER_MEMBER_UC1 unique (ID)," +
          "  constraint CLUSTER_MEMBER_UC2 unique (CLUSTER_ID, BUILDER_HOST)," +
          "  constraint CLUSTER_MEMBER_FC2 foreign key (CLUSTER_ID) references CLUSTER(ID) ON DELETE CASCADE" +
          ')',

        "create index BUILD_RUN_IX4 on BUILD_RUN(ACTIVE_BUILD_ID, NUMBER)",

      };

      log.debug("Alter structure");
      st = conn.createStatement();
      PersistanceUtils.executeDDLs(st, ddls);

      // update version
      log.debug("Updating version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");
      IoUtils.closeHard(st);

      // finish
      conn.commit();

      // request post-startup config manager action
      System.setProperty(SystemConstants.SYSTEM_PROPERTY_INIT_STATISTICS, "true");
    } finally {
      IoUtils.closeHard(st);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  public int upgraderVersion() {
    return 23;
  }
}
