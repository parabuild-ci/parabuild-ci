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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.StringUtils;

/**
 * Generates wrapper scripts for windows.
 */
final class WindowsWrapperScriptGenerator extends AbstractWrapperScriptGenerator {

  WindowsWrapperScriptGenerator(final Agent agent) {
    super(agent);
  }


  protected void writeSetCommand(final BufferedWriter scriptWriter, final String variable, final String value) throws IOException {
    if (StringUtils.isBlank(value)) {
      writeln(scriptWriter, "set " + variable + '=');
    } else {
      writeln(scriptWriter, "set " + variable + '=' + value);
    }
  }


  protected String pathVarName() {
    return "Path";
  }


  /**
   * @return suffix such as ".bat" or ".sh"
   */
  protected String getScriptSuffix() {
    return ".bat";
  }


  /**
   * This method must be overwritten to contain a platform-specific
   * command to CD to a current directory.
   *
   * @param scriptWriter created in {@link #makeWriter} method
   */
  protected void writeChangeDir(final BufferedWriter scriptWriter) throws IOException, AgentFailureException {
    if (!StringUtils.isBlank(currentDir)) {
      // extract drive and path
      final String absoluteCurrentDir = agent.getFileDescriptor(currentDir).getCanonicalPath();
      final String driveString = absoluteCurrentDir.substring(0, 2);
      final String pathString = absoluteCurrentDir.substring(2);

      // write cd <build dir> so it's current
      writeln(scriptWriter, "if not exist \"" + absoluteCurrentDir + "\" (");
      writeln(scriptWriter, "  echo Directory \"" + absoluteCurrentDir + "\" not found");
      writeln(scriptWriter, "  exit 1");
      writeln(scriptWriter, ")");
      writeln(scriptWriter, driveString);
      writeln(scriptWriter, "cd " + pathString);
    }
  }


  /**
   * This method may be overwritten to contain a optional
   * script prolog lines. An implementation should use this chance
   * to write at the very beginning of the script.
   *
   * @param scriptWriter created in {@link #makeWriter} method
   */
  protected void writeProlog(final BufferedWriter scriptWriter) throws IOException {
    writeln(scriptWriter, "@echo off"); // turn echo off
  }


  /**
   * This method by be overwritten to contain a optional
   * script epilog lines. An implementor should use this chance
   * to write at the very end of the script.
   *
   * @param writer created in {@link #makeWriter} method
   */
  protected void writeEpilog(final BufferedWriter writer) {
    // nothing to write for windows
  }


  /**
   * {@inheritDoc}
   *
   * This implementation writes a Windows-specific new line.
   *
   * @param stringWriter the string writer to wrap.
   * @return Windows-specific {@link BufferedWriter}
   */
  protected BufferedWriter makeWriter(final StringWriter stringWriter) {
    return new WinWriter(stringWriter);
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
