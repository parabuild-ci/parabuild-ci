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
import org.parabuild.ci.util.IoUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Upgrades to version 58.
 */
final class UpgraderToVersion58 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion58.class);


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

      log.debug("Altering table");
      PersistanceUtils.executeDDLs(st, new String[]{

        "create cached table PROMOTION_POLICY (\n" +
          "  ID integer not null identity,\n" +
          "  PROJECT_ID integer not null,\n" +
          "  NAME varchar(80) not null,\n" +
          "  DESCRIPTION varchar(1024) not null,\n" +
          "  DELETED char(1) not  null,\n" +
          "  TIMESTAMP bigint not null,\n" +
          "  constraint PROMOTION_POLICY_UC1 unique (ID),\n" +
          "  constraint PROMOTION_POLICY_UC2 unique (ID, DELETED),\n" +
          "  constraint PROMOTION_POLICY_FC1 foreign key (PROJECT_ID) references PROJECT(ID) ON DELETE CASCADE\n" +
                ')',

        "create cached table PROMOTION_STEP (\n" +
          "  ID integer not null identity,\n" +
          "  PROMOTION_POLICY_ID integer not null,\n" +
          "  NAME varchar(80) not null,\n" +
          "  DESCRIPTION varchar(1024) not null,\n" +
          "  DELETED char(1) not  null,\n" +
          "  TIMESTAMP bigint not null,\n" +
          "  constraint PROMOTION_STEP_UC1 unique (ID),\n" +
          "  constraint PROMOTION_STEP_UC2 unique (ID, DELETED),\n" +
          "  constraint PROMOTION_STEP_FC1 foreign key (PROMOTION_POLICY_ID) references PROMOTION_POLICY(ID) ON DELETE CASCADE\n" +
                ')',

        "create cached table PROMOTION_STEP_DEPENDENCY (\n" +
          "  ID integer not null identity,\n" +
          "  PROMOTION_STEP_ID integer not null,\n" +
          "  DEPENDENCY_PROMOTION_STEP_ID integer not null,\n" +
          "  constraint PROMOTION_STEP_DEPENDENCY_UC1 unique (ID),\n" +
          "  constraint PROMOTION_STEP_DEPENDENCY_UC2 unique (PROMOTION_STEP_ID, DEPENDENCY_PROMOTION_STEP_ID),\n" +
          "  constraint PROMOTION_STEP_DEPENDENCY_FC1 foreign key (PROMOTION_STEP_ID) references PROMOTION_STEP(ID) ON DELETE CASCADE,\n" +
          "  constraint PROMOTION_STEP_DEPENDENCY_FC2 foreign key (DEPENDENCY_PROMOTION_STEP_ID) references PROMOTION_STEP(ID) ON DELETE CASCADE\n" +
                ')',
      });

      log.debug("Updating version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");

      // finish
      conn.commit();

    } finally {
      IoUtils.closeHard(st);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  public int upgraderVersion() {
    return 58;
  }
}
