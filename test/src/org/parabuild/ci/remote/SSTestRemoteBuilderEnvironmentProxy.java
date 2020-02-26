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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.io.HessianProtocolException;
import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.process.TailBufferSizeImpl;
import org.parabuild.ci.remote.internal.RemoteAgentEnvironmentProxy;
import org.parabuild.ci.remote.internal.WebServiceLocator;
import org.parabuild.ci.remote.services.RemoteBuilderWebService;

/**
 * Tests proxy to remote agent
 */
public class SSTestRemoteBuilderEnvironmentProxy extends ServersideTestCase {


  private static final Log log = LogFactory.getLog(SSTestRemoteBuilderEnvironmentProxy.class);
  private RemoteAgentEnvironmentProxy remoteBuilderEnvironmentProxy = null;
  private RemoteAgentEnvironmentProxy misconfiguredBuilderEnvironmentProxy;
  private WebServiceLocator misconfiguredWebServiceLocator;
  public static final String TEMP_FILE_PREFIX = "parabuild";


  public SSTestRemoteBuilderEnvironmentProxy(final String s) {
    super(s);
  }


  /**
   * Tests that env map is correct
   */
  public void test_getEnv() throws Exception {
    // is there?
    final Map env = remoteBuilderEnvironmentProxy.getEnv();
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
    final Map env = remoteBuilderEnvironmentProxy.getEnv();
    for (Iterator iter = env.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry e = (Map.Entry) iter.next();
      final String name = (String) e.getKey();
      final String value = remoteBuilderEnvironmentProxy.getEnvVariable(name);
      assertNotNull(value);
      assertEquals(env.get(e.getKey()), value);
    }
  }


  /**
   */
  public void test_systemType() throws Exception {
    final int type = remoteBuilderEnvironmentProxy.systemType();
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
    final int type = remoteBuilderEnvironmentProxy.systemType();
    if (type == AgentEnvironment.SYSTEM_TYPE_SUNOS
            || type == AgentEnvironment.SYSTEM_TYPE_UNIX
            || type == AgentEnvironment.SYSTEM_TYPE_LINUX) {
      command = "/bin/ls";
    } else if (type == AgentEnvironment.SYSTEM_TYPE_CYGWIN
            || type == AgentEnvironment.SYSTEM_TYPE_WIN95
            || type == AgentEnvironment.SYSTEM_TYPE_WINNT) {
      String windowsSystemRoot = remoteBuilderEnvironmentProxy.getEnvVariable("SystemRoot");
      if (StringUtils.isBlank(windowsSystemRoot)) {
        windowsSystemRoot = remoteBuilderEnvironmentProxy.getEnvVariable("SYSTEMROOT");
      }
      command = windowsSystemRoot + "\\system32\\cmd.exe";
    } else {
      fail("Unknown system");
    }
    assertTrue("Command " + command + " should be available",
            remoteBuilderEnvironmentProxy.commandIsAvailable(command));
  }


  public void test_isUnix() throws Exception {
    final int type = remoteBuilderEnvironmentProxy.systemType();
    if (type == AgentEnvironment.SYSTEM_TYPE_CYGWIN
            || type == AgentEnvironment.SYSTEM_TYPE_SUNOS
            || type == AgentEnvironment.SYSTEM_TYPE_UNIX
            || type == AgentEnvironment.SYSTEM_TYPE_LINUX) {
      assertTrue(remoteBuilderEnvironmentProxy.isUnix());
    }
  }


  public void test_isWindows() throws Exception {
    final int type = remoteBuilderEnvironmentProxy.systemType();
    if (type == AgentEnvironment.SYSTEM_TYPE_WIN95
            || type == AgentEnvironment.SYSTEM_TYPE_WINNT) {
      assertTrue(remoteBuilderEnvironmentProxy.isWindows());
    }
  }


  public void test_execute() throws Exception {
    File stdoutFile = null;
    File stderrFile = null;
    OutputStream stdout = null;
    OutputStream stderr = null;
    try {
      final String command = (remoteBuilderEnvironmentProxy.isUnix()) ? "ls" : "cmd /C dir";
      stdoutFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".out", TestHelper.getTestTempDir());
      stderrFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".err", TestHelper.getTestTempDir());
      stdout = new FileOutputStream(stdoutFile);
      stderr = new FileOutputStream(stderrFile);
      remoteBuilderEnvironmentProxy.execute(null, command, null, stdout, stderr);
      stdout.close();
      stderr.close();
      assertTrue(stdoutFile.length() > 0);
      assertTrue(stderrFile.length() == 0);
    } finally {
      IoUtils.closeHard(stdout);
      IoUtils.closeHard(stderr);
      IoUtils.deleteFileHard(stdoutFile);
      IoUtils.deleteFileHard(stderrFile);
    }
  }


  public void test_executeMerged() throws Exception {
    File stdoutFile = null;
    File stderrFile = null;
    File mergedFile = null;
    try {
      final String command = (remoteBuilderEnvironmentProxy.isWindows()) ? "cmd /C dir" : "ls";
      stdoutFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".out", TestHelper.getTestTempDir());
      stderrFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".err", TestHelper.getTestTempDir());
      mergedFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".mrg", TestHelper.getTestTempDir());
      remoteBuilderEnvironmentProxy.execute(0, null, command, null, new TailBufferSizeImpl(0, 0), stdoutFile, stderrFile, mergedFile);
      assertTrue(stdoutFile.length() > 0);
      assertTrue(stderrFile.length() == 0);
      assertTrue(mergedFile.length() >= 0);
      assertEquals(stdoutFile.length(), mergedFile.length());
    } finally {
      IoUtils.deleteFileHard(stdoutFile);
      IoUtils.deleteFileHard(stderrFile);
      IoUtils.deleteFileHard(mergedFile);
    }
  }


  public void test_executeFailsOnNonExistingCurrentDir() throws Exception {
    File stdoutFile = null;
    File stderrFile = null;
    OutputStream stdout = null;
    OutputStream stderr = null;
    final String unexistingCurrentDir = "neverexisted" + System.currentTimeMillis();
    try {
      final String command = (remoteBuilderEnvironmentProxy.isWindows()) ? "cmd /C dir" : "ls";
      stdoutFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".out", TestHelper.getTestTempDir());
      stderrFile = IoUtils.createTempFile(TEMP_FILE_PREFIX, ".err", TestHelper.getTestTempDir());
      stdout = new FileOutputStream(stdoutFile);
      stderr = new FileOutputStream(stderrFile);
      try {
        remoteBuilderEnvironmentProxy.execute(unexistingCurrentDir, command, null, stdout, stderr);
        TestHelper.failNoExceptionThrown();
      } catch (IOException e) {
      }
    } finally {
      IoUtils.closeHard(stdout);
      IoUtils.closeHard(stderr);
      IoUtils.deleteFileHard(stdoutFile);
      IoUtils.deleteFileHard(stderrFile);
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


  public void test_webServiceLocatorExceptions() throws IOException {
    try {
      final RemoteBuilderWebService remoteBuilderWebService = misconfiguredWebServiceLocator.getWebService();
      remoteBuilderWebService.isUnix();
    } catch (HessianRuntimeException e) {
      if (log.isDebugEnabled()) log.debug("e: " + e);
      assertTrue("Expected HessianProtocolException but it was " + e, e.getRootCause() instanceof HessianProtocolException);
    }
  }


  public void test_failsOnInvalidPassword() throws Exception {
    try {
      misconfiguredBuilderEnvironmentProxy.isUnix();
      TestHelper.failNoExceptionThrown();
    } catch (IOException e) {
      assertTrue(!(e instanceof HessianProtocolException));
    }
  }


  public void test_IsAbsolute() throws IOException, AgentFailureException {
    if (remoteBuilderEnvironmentProxy.isUnix()) {
      assertTrue(remoteBuilderEnvironmentProxy.isAbsoluteFile("/bin"));
      assertTrue(!remoteBuilderEnvironmentProxy.isAbsoluteFile("test"));
    } else {
      assertTrue(remoteBuilderEnvironmentProxy.isAbsoluteFile("C:\\TEMP"));
      assertTrue(!remoteBuilderEnvironmentProxy.isAbsoluteFile("test"));
    }
  }


  public void test_createExecutorHandle() throws IOException {
    final int handle1 = remoteBuilderEnvironmentProxy.createExecutorHandle();
    final int handle2 = remoteBuilderEnvironmentProxy.createExecutorHandle();
    assertTrue(handle2 != handle1);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestRemoteBuilderEnvironmentProxy.class, new String[]{
            "test_webServiceLocatorExceptions",
            "test_failsOnInvalidPassword",
            "test_execute",
            "test_executeMerged",
            "test_executeFailsOnNonExistingCurrentDir"
    });
  }


  protected void setUp() throws Exception {
    super.setUp();
    remoteBuilderEnvironmentProxy = new RemoteAgentEnvironmentProxy(TestHelper.validBuildHost());
    misconfiguredBuilderEnvironmentProxy = new RemoteAgentEnvironmentProxy(new AgentHost(TestHelper.remoteTestBuilderHostName(), "blah" + System.currentTimeMillis()));
    misconfiguredWebServiceLocator = new WebServiceLocator(new AgentHost(TestHelper.remoteTestBuilderHostName(), "blah" + System.currentTimeMillis()));
  }
}
