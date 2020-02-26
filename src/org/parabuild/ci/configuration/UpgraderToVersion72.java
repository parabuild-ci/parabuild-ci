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
 * Upgrades to version 72.
 */
final class UpgraderToVersion72 implements SingleStepSchemaUpgrader {

  private static final Log LOG = LogFactory.getLog(UpgraderToVersion72.class); // NOPMD


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
              // LICENSE_USE table
              //
              "create cached table LICENSE_USE (" +
                      "    ID integer not null identity," +
                      "    SAMPLE_DAY datetime not null," +
                      "    DAY_USE_COUNT int not null," +
                      "    MEDIAN_USE_COUNT int not null," +
                      "    EXEEDED_COUNT int not null," +
                      "    LICENSED_NUMBER int not null," +
                      "    constraint LICENSE_USE_UC1 unique (ID)," +
                      "    constraint LICENSE_USE_UC2 unique (SAMPLE_DAY)" +
                      ')',
              //
              // COMMENT table
              //
              "create cached table COMMENT (" +
                      "    ID integer not null identity," +
                      "    OWNER_TYPE tinyint not null," +
                      "    OWNER_ID int not null," +
                      "    COMMENT_TIME datetime not null," +
                      "    COMMENT_TYPE tinyint not null," +
                      "    AUTHOR_ID int not null," +
                      "    RESPONCE_TO_ID int not null," +
                      "    constraint COMMENT_UC1 unique (ID)," +
                      "    constraint COMMENT_FC1 foreign key (AUTHOR_ID) references USERS(ID)" +
                      ')',
              "create index COMMENT_IX1 on COMMENT(OWNER_TYPE, OWNER_ID, COMMENT_TIME)"
      });

      LOG.debug("Updating version");
      st.executeUpdate("update SYSTEM_PROPERTY set VALUE = '" + upgraderVersion() + "' where NAME = 'parabuild.schema.version' ");

      // finish
      conn.commit();

      // request post-startup config manager action
      System.setProperty(SystemConstants.SYSTEM_PROPERTY_INIT_RETRY_SETTINGS, "true");
    } finally {
      IoUtils.closeHard(st);
      conn.setAutoCommit(savedAutoCommit);
    }
  }


  public int upgraderVersion() {
    return 72;
  }
}