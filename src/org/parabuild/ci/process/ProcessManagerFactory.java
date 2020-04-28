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
package org.parabuild.ci.process;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.util.ExceptionUtils;

import java.io.IOException;


/**
 * ProcessManagerFactory impements a Factory pattern interface
 * and serves as an abstraction layer that allows the build
 * process to use system-dependent process managers uniformly.
 *
 * @author simeshev@parabuildci.org
 */
public final class ProcessManagerFactory {

  private ProcessManagerFactory() {
  }


  /**
   * Creates platform-specific implementation of <code>ProcessManager</code>.
   * Supported platforms now are generic Windows and Unix platforms.
   */
  public static ProcessManager getProcessManager(final AgentEnvironment agentEnv) throws IOException {
    try {
      final int system_type = agentEnv.systemType();
      if (agentEnv.isWindows() || system_type == AgentEnvironment.SYSTEM_TYPE_CYGWIN) {
        return new WindowsProcessManager(agentEnv);
      } else if (agentEnv.isUnix()) {
        return new UnixProcessManager(agentEnv);
      }
      throw new IllegalStateException("Unknown agent operating system");
    } catch (final AgentFailureException e) {
      throw ExceptionUtils.createIOException(e);
    }
  }
}
