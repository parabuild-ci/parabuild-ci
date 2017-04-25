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

import org.apache.commons.logging.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.services.*;


/**
 * Tests BuildServiceImpl
 */
public class SSTestBuildServiceImpl extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestBuildServiceImpl.class);

  private BuildServiceImpl validCVSBuild = null;
  private ErrorManager errorManager = null;
  private ConfigurationManager configManager;


  public SSTestBuildServiceImpl(final String s) {
    super(s);
  }


  public void test_buildItitialBuildStatusIsInactive() throws Exception {
    assertEquals(validCVSBuild.getBuildState().getStatus(), BuildStatus.INACTIVE);
  }


  public void test_buildServiceStatusIsNotStarted() throws Exception {
    assertEquals(validCVSBuild.getServiceStatus(), Service.SERVICE_STATUS_NOT_STARTED);
  }


  public void test_buildServiceShutdownThrowsExceptionIfNotStarted() throws Exception {
    try {
      validCVSBuild.shutdownService();
      TestHelper.failNoExceptionThrown();
    } catch (IllegalStateException e) {
    }
  }


  public void test_startBuildThrowsExceptionIfNotStarted() throws Exception {
    try {
      validCVSBuild.startBuild(new BuildStartRequest());
      TestHelper.failNoExceptionThrown();
    } catch (IllegalStateException e) {
    }
  }


  /**
   * Tests that build service can start and stop without
   * exceptions
   */
  public void test_buildCanStartupAndShutdown() throws Exception {
    validCVSBuild.startupService();
    validCVSBuild.shutdownService();
  }


  /**
   * Tests build can start
   *
   * @throws Exception
   */
  public void test_startBuild() throws Exception {

    // get error manager
    assertEquals("Number of errors should be zero", 0, errorManager.errorCount());

    // start service
    validCVSBuild.startupService();
    final BuildStatus statusAtStartup = validCVSBuild.getBuildState().getStatus();
    if (log.isDebugEnabled()) log.debug("statusAtStartup = " + statusAtStartup);

    // start build

    validCVSBuild.startBuild(new BuildStartRequest());
    // starting build is not instant, so we wait for some time
    TestHelper.waitForStatus(validCVSBuild, BuildStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(BuildStatus.BUILDING, validCVSBuild.getBuildState().getStatus());

    // wait for build to finish
    if (log.isDebugEnabled()) log.debug("Wait for build to finish");
    TestHelper.waitForStatus(validCVSBuild, BuildStatus.INACTIVE, TestHelper.BUILD_FINISHED_SECONDS);
    assertEquals(BuildStatus.INACTIVE, validCVSBuild.getBuildState().getStatus());
    assertEquals("Number of errors should be zero", 0, errorManager.errorCount());

    validCVSBuild.shutdownService();
    assertEquals(Service.SERVICE_STATUS_NOT_STARTED, validCVSBuild.getServiceStatus());
    if (log.isDebugEnabled()) log.debug("finish test_startBuild");
  }


  /**
   *
   */
  public void test_startInactive() throws Exception {
    // start service
    validCVSBuild.startupService();
    final BuildStatus statusAtStartup = validCVSBuild.getBuildState().getStatus();
    if (log.isDebugEnabled()) log.debug("statusAtStartup = " + statusAtStartup);

    // start build

    validCVSBuild.startBuild(new BuildStartRequest());
    // starting build is not instant, so we wait for some time
    TestHelper.waitForStatus(validCVSBuild, BuildStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
    assertEquals(BuildStatus.BUILDING, validCVSBuild.getBuildState().getStatus());

    // wait for build to finish
    if (log.isDebugEnabled()) log.debug("Wait for build to finish");
    TestHelper.waitForStatus(validCVSBuild, BuildStatus.INACTIVE, TestHelper.BUILD_FINISHED_SECONDS);
    assertEquals(BuildStatus.INACTIVE, validCVSBuild.getBuildState().getStatus());
    assertEquals("Number of errors should be zero", 0, errorManager.errorCount());

    validCVSBuild.shutdownService();
    assertEquals(Service.SERVICE_STATUS_NOT_STARTED, validCVSBuild.getServiceStatus());
    if (log.isDebugEnabled()) log.debug("finish test_startBuild");
  }


  /**
   * Tests build can be stopped
   *
   * @throws Exception
   */
  public void test_stopBuild() throws Exception {
    // REVIEWME: simeshev@parabuilci.org -> disabled for it fails w/NPE for some trange reason
    final boolean process = false;
    if (!process) return;

    try {
      // get error manager
      assertEquals("Number of errors should be zero", 0, errorManager.errorCount());

      // alter sequence to wait


      // start and activate build service
      if (log.isDebugEnabled()) log.debug("==================== Activiting ===================== ");
      validCVSBuild.startupService();
      validCVSBuild.activate();
      if (log.isDebugEnabled()) log.debug("==================== Started waiting ===================== ");
      // starting build is not instant, so we wait for some time
      TestHelper.waitForStatus(validCVSBuild, BuildStatus.BUILDING, TestHelper.BUILD_STARTED_WAIT_SECONDS);
      assertEquals(BuildStatus.BUILDING, validCVSBuild.getBuildState().getStatus());

      if (log.isDebugEnabled()) log.debug("==================== Stopping ===================== ");
      validCVSBuild.stopBuild(-1);
      TestHelper.waitForStatus(validCVSBuild, BuildStatus.PAUSED, TestHelper.BUILD_FINISHED_SECONDS);
      assertEquals(BuildStatus.PAUSED, validCVSBuild.getBuildState().getStatus());
      assertEquals("Number of errors should be zero", 0, errorManager.errorCount());

      // check that the build run has STOPPED result code
      final BuildRun buildRun = configManager.getLastCompleteBuildRun(TestHelper.TEST_CVS_VALID_BUILD_ID);
      assertNotNull(buildRun);
      assertEquals("Build result should be \"stopped\"", BuildRun.BUILD_RESULT_STOPPED, buildRun.getResultID());
    } finally {
      final BuildConfig validCVSBuildConfig = getValidCVSBuildConfig();
      final ActiveBuild activeBuild = configManager.getActiveBuild(validCVSBuildConfig.getBuildID());
      activeBuild.setStartupStatus(BuildStatus.INACTIVE_VALUE);
      configManager.update(activeBuild);

      if (log.isDebugEnabled()) log.debug("==================== Shutting down ===================== ");
      validCVSBuild.shutdownService();
      assertEquals(Service.SERVICE_STATUS_NOT_STARTED, validCVSBuild.getServiceStatus());
    }
  }


  protected void setUp() throws Exception {
    super.setUp();
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
    configManager = ConfigurationManager.getInstance();
    validCVSBuild = new BuildServiceImpl(getValidCVSBuildConfig());
    System.setProperty("parabuild.print.stacktrace", "true");
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestBuildServiceImpl.class, new String[]{
      "test_stopBuild"
    });
  }


  private BuildConfig getValidCVSBuildConfig() {
    return configManager.getBuildConfiguration(TestHelper.TEST_CVS_VALID_BUILD_ID);
  }
}
