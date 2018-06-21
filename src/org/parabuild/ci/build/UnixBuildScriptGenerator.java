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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentEnvironment;

/**
 * Unix script generator
 */
public final class UnixBuildScriptGenerator extends AbstractBuildScriptGenerator {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(UnixBuildScriptGenerator.class); // NOPMD


  public UnixBuildScriptGenerator(final Agent agent) {
    super(agent);
  }


  /**
   * Generates build sequence script for further execution.
   *
   * @param sequence fo which a script will be created
   * @see #generateScript(BuildSequence)
   */
  protected String doGenerateScript(final BuildSequence sequence) throws BuildException, AgentFailureException {
    BufferedWriter scriptWriter = null;

    try {
      final String scriptText = sequence.getScriptText();

      // create script writer
      final StringWriter result = new StringWriter(300);
      scriptWriter = new UnixWriter(result);
      writeln(scriptWriter, "#!/bin/sh"); // turn echo off

      // reset tomcat wars
      writeCleanupVarsCommands(scriptWriter);

      // write common commands
      writeCommonCommands(scriptWriter);

      // write added vars
      writeAddedVariables(scriptWriter);

      // write OS-specific commands
      String checkOutDirName = agent.getCheckoutDirName();
      String buildDirName = agent.getCheckoutDirName() + '/' + relativeBuildDir;
      if (agent.systemType() == AgentEnvironment.SYSTEM_TYPE_CYGWIN) {
        checkOutDirName = agent.cygwinWindowsPathToUnix(checkOutDirName);
        buildDirName = agent.cygwinWindowsPathToUnix(buildDirName);
      }
      writeSetCommand(scriptWriter, VAR_PARABUILD_CHECKOUT_DIR, StringUtils.putIntoDoubleQuotes(checkOutDirName));
      writeSetCommand(scriptWriter, VAR_PARABUILD_BUILD_DIR, StringUtils.putIntoDoubleQuotes(buildDirName));

      // write cd <build dir> so it's current
      writeln(scriptWriter, "cd " + StringUtils.putIntoDoubleQuotes(buildDirName));

      // write script
      final BufferedReader br = new BufferedReader(new StringReader(scriptText));
      for (String line = br.readLine(); line != null;) {
        writeln(scriptWriter, line);
        line = br.readLine();
      }
      scriptWriter.newLine();
      scriptWriter.flush();
      return result.toString();
    } catch (final IOException e) {
      throw new BuildException(e.toString(), e, agent);
    } finally {
      IoUtils.closeHard(scriptWriter);
    }
  }


  protected void writeSetCommand(final BufferedWriter scriptWriter, final String variable, final String value) throws IOException {
    if (StringUtils.isBlank(value)) {
      // clear variable
      writeln(scriptWriter, "unset " + variable);
    } else {
      // set variable
      writeln(scriptWriter, variable + '=' + ((value.indexOf(' ') >= 0) ? StringUtils.putIntoDoubleQuotes(value) : value));
      writeln(scriptWriter, "export " + variable);
    }
  }


  /**
   * Writes a string and a newline after the string
   */
  private void writeln(final BufferedWriter bw, final String s) throws IOException {
    bw.write(s);
    bw.newLine();
  }


  protected String pathVarName() {
    return "PATH";
  }


  /**
   * WinWriter extends BufferedWriter to override newLine so that
   * it writes a new line in a fixed Windows format.
   */
  private static final class UnixWriter extends BufferedWriter {

    UnixWriter(final Writer out) {
      super(out);
    }


    UnixWriter(final Writer out, final int sz) {
      super(out, sz);
    }


    /**
     * Write a line separator in a fixed windows format.
     *
     * @throws IOException If an I/O error occurs
     */
    public void newLine() throws IOException {
      write('\n');
    }
  }
}
