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
package org.parabuild.ci;

import org.apache.cactus.ServletTestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.parabuild.ci.build.SSTestServersideTestCase;
import org.parabuild.ci.common.CacheUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.PersistanceUtils;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.BuildConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.util.List;
import java.util.Properties;

/**
 */
public class ServersideTestCase extends ServletTestCase {

  /**
   * @noinspection UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SSTestServersideTestCase.class); // NOPMD

  private static final File DATASET_FILE = new File(System.getProperty("test.dataset"));

  /**
   * REVIEWME: simeshev@parabuilci.org - We have a static
   * connection (this is VERY BAD) because combintation of
   * DatabaseOperation.CLEAN_INSERT.execute(DBUNIT_CONNECTION,
   * new FlatXmlDataSet(DATASET_FILE)) and HSQLDB 1.7.2.4 causes
   * degrading performance of the operation above, so having it
   * static is a workaround - it is relatively harmful. But
   * still, this is bad.
   * <p/>
   * Check with DBUNIT/HSQL teams to find out what is going on.
   *
   * @see org.parabuild.ci.build.SSTestServersideTestCase#test_performance4()
   */
  private static IDatabaseConnection staticDBUnitConnection = null;


  public ServersideTestCase(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();

    // reset ehcache for each test
    CacheUtils.resetAllCaches();

    // init database content if there are other test cases
    DatabaseOperation.CLEAN_INSERT.execute(getIDatabaseConnection(), new FlatXmlDataSet(DATASET_FILE));
    getIDatabaseConnection().getConnection().commit();

    // NOTE: vimeshev - 05/22/2004 - Suppress/enables printing error contents
    // details. Particular tests can overwrite it by setting in
    // testures or in setUp methods.
    disableErrorManagerStackTraces();


    // REVIEWME: simeshev@parabuilci.org -> desperate attempt to fight active build missing in the
    // database after clean insert from dataset.xml
//    if (LOG.isDebugEnabled()) LOG.debug("checking for active build");
    final long startedWaitingForActiveBuild = System.currentTimeMillis();
    while (ConfigurationManager.getInstance().getActiveBuildConfig(TestHelper.TEST_CVS_VALID_BUILD_ID) == null) {
      Thread.sleep(100);
    }
//    if (LOG.isDebugEnabled()) LOG.debug("time to wait for active build appearance: " + (System.currentTimeMillis() - startedWaitingForActiveBuild));
    // re-validate
    final ActiveBuildConfig activeBuildConfig = ConfigurationManager.getInstance().getActiveBuildConfig(TestHelper.TEST_CVS_VALID_BUILD_ID);
//    if (LOG.isDebugEnabled()) LOG.debug("activeBuildConfig after wait: " + activeBuildConfig);
    ConfigurationManager.getInstance().validateIsActiveBuildID(TestHelper.TEST_CVS_VALID_BUILD_ID);

    // NOTE: vimeshev - 07/19/2004 - Updates all the build configurations to
    // use remote agent at localhost.
    if (System.getProperty("test.remote.builder", "false").equals("true")) {
      // Update agent's host
      final AgentConfig agentConfig = BuilderConfigurationManager.getInstance().getAgentConfig(1);
      agentConfig.setHost(TestHelper.remoteTestBuilderHostName());
      BuilderConfigurationManager.getInstance().saveAgent(agentConfig);
      // Set all build configs to the remote builder
      ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          final List configs = ConfigurationManager.getInstance().getExistingBuildConfigs();
          for (int i = 0; i < configs.size(); i++) {
            final BuildConfig buildConfig = (BuildConfig) configs.get(i);
            buildConfig.setBuilderID(TestHelper.REMOTE_BUILDER_ID);
            ConfigurationManager.getInstance().saveObject(buildConfig);
          }
          return null;
        }
      });
    }
  }


  /**
   * Child classes can call this method to disable ErrorManager
   * printing reported Error details.
   */
  public void disableErrorManagerStackTraces() {
    System.setProperty("parabuild.print.stacktrace", "false");
  }


  /**
   * Child classes can call this method to enable ErrorManager
   * printing reported Error details.
   */
  public void enableErrorManagerStackTraces() {
    System.setProperty("parabuild.print.stacktrace", "true");
  }


  protected void tearDown() throws Exception {
    super.tearDown();
    // reset to "don't show print stack trace"
    disableErrorManagerStackTraces();
  }


  private static IDatabaseConnection getIDatabaseConnection() {
    if (staticDBUnitConnection == null) {
      initDBUnitConnection();
    }
    return staticDBUnitConnection;
  }


  /**
   * True double check init.
   */
  private static synchronized void initDBUnitConnection() {
    if (staticDBUnitConnection == null) {
//      if (LOG.isDebugEnabled()) LOG.debug("create dbunit connection");
      staticDBUnitConnection = new DatabaseConnection(makeJDBCConnection());
    }
  }


  private static Connection makeJDBCConnection() {
    try {
      final String catalinaBase = System.getProperty("catalina.base");
      final String databaseHome = (new File(catalinaBase, "data/parabuild")).getAbsolutePath();
      final Properties props = new Properties();
      props.setProperty("user", PersistanceUtils.DATABASE_USER_NAME);
      props.setProperty("password", PersistanceUtils.DATABASE_PASSWORD);
      final Driver driver = (Driver) Class.forName("org.hsqldb.jdbcDriver").newInstance();
      final Connection connection = driver.connect("jdbc:hsqldb:" + databaseHome, props);
      connection.setAutoCommit(false);
      return connection;
    } catch (Exception e) {
      final IllegalStateException ise = new IllegalStateException(StringUtils.toString(e));
      ise.initCause(e);
      throw ise;
    }
  }
}
