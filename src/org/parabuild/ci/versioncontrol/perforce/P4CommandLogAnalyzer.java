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
package org.parabuild.ci.versioncontrol.perforce;

import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;

/**
 * Analyzes results of execution of p4 command
 */
public final class P4CommandLogAnalyzer {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(P4CommandLogAnalyzer.class); //NOPMD

  private String unexpectedError = "Error while accessing Perforce";
  private String unknownError = "Unknown error while accessing Perforce. No error message was provided by P4.";


  public void setUnexpectedError(final String unexpectedError) {
    this.unexpectedError = unexpectedError;
  }


  public void setUnknownError(final String unknownError) {
    this.unknownError = unknownError;
  }


  public void validate(final File stdout, final File stderr) throws BuildException {
    BufferedReader br = null;
    try {
      // check stderr for errors. successful "p4 client -i" writes
      // to stdout, stderr should be empty
      if (stderr.length() != 0) {
        // throw an exception
        final String logContent = IoUtils.fileToString(stderr);
        final BuildException be = new BuildException(unexpectedError + ": " + logContent);
        be.setLogContent(logContent);
        throw be;
      }

      // find errors if any
      if (stdout == null) return; // don't need to validate
      boolean mustBreak = false;
      final StringBuffer errors = new StringBuffer(100);
      br = new BufferedReader(new InputStreamReader(new FileInputStream(stdout)), 1024);
      String ln = br.readLine();
      while (ln != null) {
        if (log.isDebugEnabled()) log.debug("ln: " + ln);
        if (ln.startsWith("error: ")) {
          errors.append(ln.substring("error: ".length())).append('\n');
          if (ln.endsWith("- no such file(s).") || ln.endsWith("over license quota.")) {
            mustBreak = true;
          } else if (ln.endsWith("File(s) not on client.")) {
            mustBreak = true;
          } else if (ln.startsWith("error: Label") && ln.endsWith("doesn't exist.")) { // NOPMD
            return; // ignore
          } else if (ln.endsWith("- file(s) not opened on this client.")) { // NOPMD
            return; // ignore
          }
        }
        if (!mustBreak && ln.startsWith("exit: 0")) return;
        ln = br.readLine();
      }

      // the fact that we are here means we didn't find the "exit: 0"
      final BuildException e = new BuildException(StringUtils.isBlank(errors.toString()) ? unknownError : unexpectedError + ": " + errors);
      e.setLogContent(errors);
      throw e;
    } catch (IOException e) {
      throw new BuildException("Unknown error while analizing results of access to Perforce");
    } finally {
      IoUtils.closeHard(br);
    }
  }
}
