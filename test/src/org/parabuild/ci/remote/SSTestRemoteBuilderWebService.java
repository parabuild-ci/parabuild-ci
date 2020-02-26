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
package org.parabuild.ci.remote;

import com.caucho.hessian.client.HessianRuntimeException;
import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.remote.internal.RemoteAgentEnvironmentProxy;
import org.parabuild.ci.remote.internal.WebServiceLocator;
import org.parabuild.ci.remote.services.RemoteBuilderWebService;

import java.util.Iterator;
import java.util.Map;

/**
 * Tests proxy to remote agent
 */
public class SSTestRemoteBuilderWebService extends ServersideTestCase {


  private static final Log log = LogFactory.getLog(SSTestRemoteBuilderWebService.class);
  public static final String TEMP_FILE_PREFIX = "parabuild";
  private RemoteBuilderWebService webService;


  public SSTestRemoteBuilderWebService(final String s) {
    super(s);
  }


  /**
   * Tests that env map is correct
   */
  public void test_getEnv() throws Exception {
    // is there?
    final Map env = webService.getEnv();
    assertNotNull(env);
    // no nulls?
    for (Iterator iter = env.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry e = (Map.Entry) iter.next();
      assertNotNull(e.getKey());
      assertNotNull(e.getValue());
    }
  }


  /**
   */
  public void test_getEnvVariable() throws Exception {
    final Map env = webService.getEnv();
    for (Iterator iter = env.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry e = (Map.Entry) iter.next();
      final String name = (String) e.getKey();
      final String value = webService.getEnvVariable(name);
      assertNotNull(value);
      assertEquals(env.get(e.getKey()), value);
    }
  }


  /**
   */
  public void test_systemType() throws Exception {
    final int type = webService.systemType();
    assertTrue("System type should be one of known values",
            type == AgentEnvironment.SYSTEM_TYPE_CYGWIN
                    || type == AgentEnvironment.SYSTEM_TYPE_SUNOS
                    || type == AgentEnvironment.SYSTEM_TYPE_UNIX
                    || type == AgentEnvironment.SYSTEM_TYPE_LINUX
                    || type == AgentEnvironment.SYSTEM_TYPE_WIN95
                    || type == AgentEnvironment.SYSTEM_TYPE_WINNT);
  }


  public void test_commandIsAvailable() throws Exception {
    String command = null;
    final int type = webService.systemType();
    if (type == AgentEnvironment.SYSTEM_TYPE_SUNOS
            || type == AgentEnvironment.SYSTEM_TYPE_UNIX
            || type == AgentEnvironment.SYSTEM_TYPE_LINUX) {
      command = "/bin/ls";
    } else if (type == AgentEnvironment.SYSTEM_TYPE_CYGWIN
            || type == AgentEnvironment.SYSTEM_TYPE_WIN95
            || type == AgentEnvironment.SYSTEM_TYPE_WINNT) {
      String windowsSystemRoot = webService.getEnvVariable("SystemRoot");
      if (StringUtils.isBlank(windowsSystemRoot)) {
        windowsSystemRoot = webService.getEnvVariable("SYSTEMROOT");
      }
      command = windowsSystemRoot + "\\system32\\cmd.exe";
    } else {
      fail("Unknown system");
    }
    assertTrue("Command " + command + " should be available",
            webService.commandIsAvailable(command));
  }


  public void test_isUnix() throws Exception {
    final int type = webService.systemType();
    if (type == AgentEnvironment.SYSTEM_TYPE_CYGWIN
            || type == AgentEnvironment.SYSTEM_TYPE_SUNOS
            || type == AgentEnvironment.SYSTEM_TYPE_UNIX
            || type == AgentEnvironment.SYSTEM_TYPE_LINUX) {
      assertTrue(webService.isUnix());
    }
  }


  public void test_isWindows() throws Exception {
    final int type = webService.systemType();
    if (type == AgentEnvironment.SYSTEM_TYPE_WIN95
            || type == AgentEnvironment.SYSTEM_TYPE_WINNT) {
      assertTrue(webService.isWindows());
    }
  }


  /**
   * Tests that proxy fails on non-existing host.
   */
  public void test_failsOnNonExistingHost() {
    final RemoteAgentEnvironmentProxy proxy = new RemoteAgentEnvironmentProxy(new AgentHost("host_" + System.currentTimeMillis(), ""));
    try {
      proxy.systemType();
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  public void test_failsOnInvalidPassword() throws Exception {
    try {
      final AgentHost agentHostWWrongPassword = new AgentHost(TestHelper.remoteTestBuilderHostName(), "blah" + System.currentTimeMillis());
      final RemoteBuilderWebService webServiceWithWrongPassword = new WebServiceLocator(agentHostWWrongPassword).getWebService();
      webServiceWithWrongPassword.isUnix();
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
      assertTrue(e instanceof HessianRuntimeException);
    }
  }


  /**
   */
  public void test_createFile() throws Exception {
    final String createdFile = webService.createTempFile(TestHelper.TEST_CVS_VALID_BUILD_ID, TEMP_FILE_PREFIX, ".test", this.getClass().toString());
    assertNotNull(createdFile);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestRemoteBuilderWebService.class, new String[]{
    });
  }


  protected void setUp() throws Exception {
    super.setUp();
    webService = new WebServiceLocator(TestHelper.validBuildHost()).getWebService();
  }
}
