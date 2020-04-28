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
 * This upgrader calculates time to fix.
 */
final class UpgraderToVersion46 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion46.class);


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

      log.debug("Altering tables");
      PersistanceUtils.executeDDLs(st, new String[]{
        "create cached table ACTIVE_MERGE (" +
          "  ID integer not null identity," +
          "  PROJECT_ID integer not null," +
          "  DELETED char(1) not  null," +
          "  STARTUP_MODE tinyint not null," +
          "  TIMESTAMP bigint not null," +
          "  constraint ACTIVE_MERGE_UC1 unique (ID)," +
          "  constraint ACTIVE_MERGE_UC2 unique (ID, DELETED)," +
          "  constraint ACTIVE_MERGE_FC1 foreign key (PROJECT_ID) references PROJECT(ID) ON DELETE CASCADE" +
          ')',

        "create cached table MERGE (" +
          "  ID integer not null identity," +
          "  ACTIVE_MERGE_ID integer not null," +
          "  SOURCE_BUILD_ID integer not null," +
          "  TARGET_BUILD_ID integer not null," +
          "  CONFLICT_RESOLUTION_MODE tinyint not null," +
          "  DESCRIPTION varchar(1024) not null," +
          "  DISCRIMINATOR char(1) not null," +
          "  MARKER varchar(1024)," +
          "  MERGE_MODE tinyint not null," +
          "  NAME varchar(80) not null," +
          "  TIMESTAMP bigint not null," +
          "  constraint MERGE_UC1 unique (ID)," +
          "  constraint MERGE_FC1 foreign key (ACTIVE_MERGE_ID) references ACTIVE_MERGE(ID) ON DELETE CASCADE," +
          "  constraint MERGE_FC2 foreign key (SOURCE_BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE," +
          "  constraint MERGE_FC3 foreign key (TARGET_BUILD_ID) references BUILD_CONFIG(ID) ON DELETE CASCADE" +
          ')',

        "create cached table MERGE_ATTRIBUTE (" +
          "  MERGE_ID integer not null," +
          "  ID integer not null identity," +
          "  NAME varchar(80) not null," +
          "  VALUE varchar(1024)," +
          "  TIMESTAMP bigint not null," +
          "  constraint MERGE_ATTRIBUTE_UC1 unique (ID)," +
          "  constraint MERGE_ATTRIBUTE_UC2 unique (MERGE_ID, NAME)," +
          "  constraint MERGE_ATTRIBUTE_FC1 foreign key (MERGE_ID) references MERGE(ID) ON DELETE CASCADE" +
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
    return 46;
  }
}
