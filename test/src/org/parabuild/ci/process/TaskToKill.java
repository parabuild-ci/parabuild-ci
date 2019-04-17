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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.common.NullOutputStream;
import org.parabuild.ci.remote.AgentEnvironment;

/**
 *
 */
public class TaskToKill extends Thread {

  private static final Log log = LogFactory.getLog(TaskToKill.class);

  public static final String TEST_SIGNATURE_VALUE = "TEST_SIGNATURE_VALUE";

  private String command = null;
  private File dir = null;
  private AgentEnvironment agentEnv = null;


  /**
   */
  public TaskToKill(final AgentEnvironment agentEnv, final String command) {
    this(agentEnv, TestHelper.getTestTempDir(), command);
    setDaemon(true);
  }


  /**
   */
  public TaskToKill(final AgentEnvironment agentEnv, final File dir, final String command) {
    super("LongRunningTestThread");
    //setDaemon(true);
    this.command = command;
    this.dir = dir;
    this.agentEnv = agentEnv;
  }


  /**
   */
  public void run() {
    try {
      // execute command
      final Map env = new HashMap(11);
      env.put("TEST_SIGNATURE", TEST_SIGNATURE_VALUE);
      agentEnv.execute(dir.toString(), command, env, new NullOutputStream(), new NullOutputStream());
    } catch (IOException e) {
      log.error("LongRunningTestThread", e);
    } catch (Exception e) {
      log.error("LongRunningTestThread", e);
    }
  }
}
