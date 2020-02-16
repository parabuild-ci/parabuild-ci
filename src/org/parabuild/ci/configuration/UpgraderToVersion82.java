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
 * Upgrades to version 82. Add repository configuration table. The idea behind the repository list is that multiple
 * builds can be using the same repository URLs, user names and passwords, keys, authentication methods etc. Instead
 * of using a per-build configuration, we would use a single unique configuration that would be referenced by the
 * pipeline templates.
 * <p>
 * Another possibility is a single VCS server hosting multiple repositories such as Github. The authentication happens
 * once for the VCS server. The access to a particular repository is controlled outside of Parabuild. In this case there
 * is a one-to-many relationship between the VCS server and the VCS repository. Configuring a VCS server should be done
 * separately and the VCS repository should refere the server.
 * <p>
 * The side effect of using references is that it is possible to change a VVS server and a repository, and the pipeline
 * will behave differently or stop working. The solution could be using the same versioning approach Parabuild uses for
 * the build configuration, which is copy config on build run.
 * <p>
 * Another alternative is using copy on update where once the repo config was referenced, any changes to the repo config
 * and the server will produce an immutable copy.
 * <p>
 * The repository configurations can be a template themselves.
 */
final class UpgraderToVersion82 implements SingleStepSchemaUpgrader {

  private static final Log log = LogFactory.getLog(UpgraderToVersion82.class);


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

      log.debug("Creating tables");
      PersistanceUtils.executeDDLs(st, new String[]{
              "create cached table VCS_SERVER (\n" +
                      "  ID integer not null identity,\n" +
                      "  NAME varchar(512) not null,\n" +
                      "  DESCRIPTION varchar(512) not null,\n" +
                      "  TYPE integer not null,\n" +
                      "  DELETED char(1) not null,\n" +
                      "  TIMESTAMP bigint not null,\n" +
                      "  constraint VCS_SERVER_UC1 unique (ID)\n" +
                      ')',

              "create cached table VCS_SERVER_ATTRIBUTE (\n" +
                      "  VCS_SERVER_ID integer not null,\n" +
                      "  ID integer not null identity,\n" +
                      "  NAME varchar(80) not null,\n" +
                      "  VALUE varchar(1024),\n" +
                      "  TIMESTAMP bigint not null,\n" +
                      "  constraint VCS_SERVER_ATTRIBUTE_UC1 unique (ID),\n" +
                      "  constraint VCS_SERVER_ATTRIBUTE_UC2 unique (VCS_SERVER_ID, NAME),\n" +
                      "  constraint VCS_SERVER_ATTRIBUTE_FC1 foreign key (VCS_SERVER_ID) references VCS_SERVER(ID) ON DELETE CASCADE\n" +
                      ')',

              "create cached table VCS_REPOSITORY (\n" +
                      "  VCS_SERVER_ID integer not null,\n" +
                      "  ID integer not null identity,\n" +
                      "  NAME varchar(512) not null,\n" +
                      "  DESCRIPTION varchar(512) not null,\n" +
                      "  TYPE integer not null,\n" +
                      "  DELETED char(1) not null,\n" +
                      "  TIMESTAMP bigint not null,\n" +
                      "  constraint VCS_REPOSITORY_UC1 unique (ID),\n" +
                      "  constraint VCS_REPOSITORY_FC1 foreign key (VCS_SERVER_ID) references VCS_SERVER(ID) ON DELETE CASCADE\n" +
                      ')',

              "create cached table VCS_REPOSITORY_ATTRIBUTE (\n" +
                      "  VCS_REPOSITORY_ID integer not null,\n" +
                      "  ID integer not null identity,\n" +
                      "  NAME varchar(80) not null,\n" +
                      "  VALUE varchar(1024),\n" +
                      "  TIMESTAMP bigint not null,\n" +
                      "  constraint VCS_REPOSITORY_ATTRIBUTE_UC1 unique (ID),\n" +
                      "  constraint VCS_REPOSITORY_ATTRIBUTE_UC2 unique (VCS_REPOSITORY_ID, NAME),\n" +
                      "  constraint VCS_REPOSITORY_ATTRIBUTE_FC1 foreign key (VCS_REPOSITORY_ID) references VCS_REPOSITORY(ID) ON DELETE CASCADE\n" +
                      ')',
      });

      log.debug("Updating version");
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
    return 82;
  }
}
