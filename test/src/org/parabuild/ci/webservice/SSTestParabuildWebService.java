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
package org.parabuild.ci.webservice;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.axis.client.Stub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.Version;
import org.parabuild.ci.services.ServiceManager;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * Tests parabuild web service.
 *
 * @noinspection JUnitTestMethodWithNoAssertions,InstanceMethodNamingConvention,TestMethodWithIncorrectSignature
 */
public final class SSTestParabuildWebService extends ServersideTestCase {


  private static final Log LOG = LogFactory.getLog(SSTestParabuildWebService.class); // NOPMD


  public void testParabuildWSDL() throws Exception {
//    java.net.URL url = new java.net.URL(testServerURLString() + "?WSDL");
//    final InputStream inputStream = url.openStream();
//    assertNotNull(inputStream);
//    IoUtils.closeHard(inputStream);
  }


  public void test1ParabuildStartBuild() throws Exception {
    final ParabuildSoapBindingStub parabuildService = getParabuildService();
    assertNotNull("binding is null", parabuildService);
    parabuildService.startBuild(1);
    parabuildService.stopBuild(1);
  }


  public void test2ParabuildStopBuild() throws Exception {
    final ParabuildSoapBindingStub parabuildService = getParabuildService();
    assertNotNull("binding is null", parabuildService);
    parabuildService.stopBuild(1);
  }


  public void test3ParabuildResumeBuild() throws Exception {
    final ParabuildSoapBindingStub parabuildService = getParabuildService();
    assertNotNull("binding is null", parabuildService);
    parabuildService.stopBuild(1);
    parabuildService.resumeBuild(1);
  }


  public void test4ParabuildRequestCleanCheckout() throws Exception {
    final ParabuildSoapBindingStub parabuildService = getParabuildService();
    assertNotNull("binding is null", parabuildService);
    parabuildService.requestCleanCheckout(1);
  }


  public void test5ParabuildServerVersion() throws Exception {
    final ParabuildSoapBindingStub parabuildService = getParabuildService();
    assertNotNull("binding is null", parabuildService);
    parabuildService.requestCleanCheckout(1);
    assertEquals(Version.versionToString(true), parabuildService.serverVersion());
  }


  /**
   * Tests accessing version method.
   */
  public void test_version() throws Exception {
    final ParabuildSoapBindingStub stub = getParabuildService();
    LOG.debug("Version value: " + stub.serverVersion());
  }


  public void testGetVariables() throws MalformedURLException, ServiceException, RemoteException {
    final ParabuildSoapBindingStub parabuildService = getParabuildService();
    final StartParameter[] startParameters = parabuildService.getVariables(org.parabuild.ci.object.StartParameter.TYPE_BUILD, TestHelper.TEST_CVS_VALID_BUILD_ID);
    assertTrue(startParameters.length > 0);
    for (int i = 0; i < startParameters.length; i++) {
      final StartParameter startParameter = startParameters[i];
      assertNotNull(startParameter.getName());
    }
  }


  public void testGetCompletedBuildRuns() throws MalformedURLException, ServiceException, RemoteException {
    final ParabuildSoapBindingStub parabuildService = getParabuildService();
    final BuildRun[] buildRuns = parabuildService.getCompletedBuildRuns(1, 0, 1000);
    for (int j = 0; j < buildRuns.length; j++) {
      final BuildRun buildRun = buildRuns[j];
      assertNotNull(buildRun.getResultDescription());
    }
  }


  public void testGetCompletedBuildRunsForNonExistentBuild() throws MalformedURLException, ServiceException, RemoteException {
    final ParabuildSoapBindingStub parabuildService = getParabuildService();
    final BuildRun[] buildRuns = parabuildService.getCompletedBuildRuns(33, 0, 1000);
    assertNotNull(buildRuns);
    assertEquals(0, buildRuns.length);
  }


  public void testGetBuildResultDescription() throws MalformedURLException, ServiceException, RemoteException {
    final ParabuildSoapBindingStub parabuildService = getParabuildService();
    final BuildConfiguration[] configurations = parabuildService.getActiveBuildConfigurations();
    for (int i = 0; i < configurations.length; i++) {
      final BuildConfiguration configuration = configurations[i];
      final BuildRun[] buildRuns = parabuildService.getCompletedBuildRuns(configuration.getActiveBuildID(), 0, 1000);
      for (int j = 0; j < buildRuns.length; j++) {
        final BuildRun buildRun = buildRuns[j];
        assertNotNull("Build run cannot be null", buildRun);
        assertNotNull("Build run description cannot be null", buildRun.getResultDescription());
      }
    }
  }


  /**
   * Tests accessing version method.
   */
  public void test_versionFailsUnderWrongPassword() throws Exception {
    try {
      final ParabuildService parabuildServiceLocator = new ParabuildServiceLocator();
      final Parabuild svc = parabuildServiceLocator.getParabuild(new URL(getTestServerURLString()));
      ((Stub) svc).setPassword("blah");
      ((Stub) svc).setPassword("blah");
      LOG.debug("Version value: " + svc.serverVersion());
// REVIEWME: simeshev@parabuilci.org -> When realm is operational
//      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  /**
   * Tests unexisting build throws an exception.
   */
  public void test_startUnexistingBuild() throws Exception {
    try {
      final ParabuildSoapBindingStub parabuildService = getParabuildService();
      parabuildService.startBuild(-1);
      TestHelper.failNoExceptionThrown();
    } catch (RemoteException e) {
    }
  }


  public void testGetSystemProperties() throws MalformedURLException, ServiceException, RemoteException {
    final ParabuildSoapBindingStub parabuildService = getParabuildService();
    final SystemProperty[] systemProperties = parabuildService.getSystemProperties();
    assertNotNull(systemProperties);
    assertTrue(systemProperties.length > 0);
    for (int i = 0; i < systemProperties.length; i++) {
      assertNotNull(systemProperties[i]);
    }
  }


  private ParabuildSoapBindingStub getParabuildService() throws ServiceException, MalformedURLException {
    final ParabuildSoapBindingStub stub = (ParabuildSoapBindingStub) new ParabuildServiceLocator().getParabuild(new URL(getTestServerURLString()));
    stub.setUsername("admin");
    stub.setPassword("admin");
    stub.setTimeout(60000);
    return stub;
  }


  private String getTestServerURLString() {
    return "http://localhost:" + ServiceManager.getInstance().getListenPort() + "/parabuild/integration/webservice/Parabuild";
  }


  public void testGetAgentStatuses() throws MalformedURLException, ServiceException, RemoteException {
    final ParabuildSoapBindingStub parabuildService = getParabuildService();
    final AgentStatus[] agentStatuses = parabuildService.getAgentStatuses();
    assertNotNull(agentStatuses);
    assertTrue(agentStatuses.length > 0);
    for (int i = 0; i < agentStatuses.length; i++) {
      assertNotNull(agentStatuses[i]);
    }
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();
  }


  public SSTestParabuildWebService(final String s) {
    super(s);
  }


  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestParabuildWebService.class, new String[]{
            "testGetAgentStatuses",
            "testGetCompletedBuildRunsForNonExistentBuild",
            "testGetBuildResultDescription",
            "testGetSystemProperties",
            "test_version",
            "testParabuildWSDL"
    });
  }
}
