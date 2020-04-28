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

import org.parabuild.ci.build.AgentFailureException;

import java.io.IOException;
import java.util.Map;

/**
 * WrapperScriptGenerator is responsible for
 * shell scripts that wrap execute commands.
 */
public interface WrapperScriptGenerator {

  /**
   * Generates wrapper sequence script file for further execution.
   *
   * @param command that will be wrapped
   * @return String absolute path to created sctep script file.
   * @throws IOException
   */
  String generateScript(String command) throws IOException, AgentFailureException;


  /**
   * Sets execution directory. Execution directory
   * is the directory where the script is running from.
   */
  void setExecutionDirectory(String buildDirName);


  /**
   * Adds sheel variables to be present in the script.
   *
   * @param variables a Map with a shell variable name as a key
   *                  and variable value as value.
   */
  void addVariables(Map variables);
}
