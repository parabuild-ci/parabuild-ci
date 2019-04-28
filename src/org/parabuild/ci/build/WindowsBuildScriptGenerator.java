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

import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.remote.Agent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Windows build script generator
 */
public final class WindowsBuildScriptGenerator extends AbstractBuildScriptGenerator {

  public WindowsBuildScriptGenerator(final Agent agent) {
    super(agent);
  }


  /**
   * Generates build sequence script for further execution. Name
   * of the build script is obtained from BuildFiles.
   *
   * @param sequence fo which a script will be created
   */
  protected String doGenerateScript(final BuildSequence sequence) throws BuildException, AgentFailureException {
    BufferedWriter scriptWriter = null;

    try {
      final String scriptText = sequence.getScriptText();
      final String absoluteBuildDir = (agent.getCheckoutDirName() + '\\' + relativeBuildDir).replace('/', '\\');

      // extract drive and path
      final String driveString = absoluteBuildDir.substring(0, 2);
      final String pathString = absoluteBuildDir.substring(2);


      // create script writer
      final StringWriter result = new StringWriter(300);
      scriptWriter = new WinWriter(result);
      scriptWriter.write("@echo off"); // turn echo off
      scriptWriter.newLine();


      // reset vars set by tomcat
      writeCleanupVarsCommands(scriptWriter);

      // write common commands
      writeCommonCommands(scriptWriter);

      // write added vars
      writeAddedVariables(scriptWriter);

      // write build time variables
      writeSetCommand(scriptWriter, VAR_PARABUILD_CHECKOUT_DIR, agent.getCheckoutDirName());
      writeSetCommand(scriptWriter, VAR_PARABUILD_BUILD_DIR, absoluteBuildDir);
      scriptWriter.newLine();

      // write cd <build dir> so it's current
      scriptWriter.write(driveString);
      scriptWriter.newLine();
      scriptWriter.write("cd " + pathString);
      scriptWriter.newLine();
      scriptWriter.write(scriptText);
      scriptWriter.newLine();
      scriptWriter.flush();
      return result.toString();
    } catch (final IOException e) {
      throw new BuildException(e, agent);
    } finally {
      IoUtils.closeHard(scriptWriter);
    }
  }


  protected void writeSetCommand(final BufferedWriter scriptWriter, final String variable, final String value) throws IOException {
    if (StringUtils.isBlank(value)) {
      scriptWriter.write("set " + variable + '=');
    } else {
      scriptWriter.write("set " + variable + '=' + value);
    }
    scriptWriter.newLine();
  }


  protected String pathVarName() {
    return "Path";
  }


  /**
   * WinWriter extends BufferedWriter to override newLine so that
   * it writes a new line in a fixed Windows format.
   */
  private static final class WinWriter extends BufferedWriter {

    WinWriter(final Writer out) {
      super(out);
    }


    /**
     * Write a line separator in a fixed windows format.
     *
     * @throws IOException If an I/O error occurs
     */
    public void newLine() throws IOException {
      write(0x0D);
      write(0x0A);
    }
  }
}
