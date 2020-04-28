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

import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Helper to parse parse P4 counter log output.
 *
 */
final class P4CounterParser {

  /**
   * Parses P4 counter log generated as a result of
   * <p>p4 -s counter [counter name] </p> command.
   *
   * Format of the output is following:
   * <code>
   * info: 741
   * exit: 0
   * </code>
   *
   * @return int value of the counter
   */
  public int parse(final File parserInput) throws BuildException {
    BufferedReader br = null;
    int result = 0;
    try {
      // read
      if (parserInput == null) throw new IllegalStateException("P4 counter parser input is undefined.");
      br = new BufferedReader(new FileReader(parserInput), 25);
      final String line = br.readLine();
      validateLine(line);
      // parse
      if (line.startsWith(P4ParserHelper.P4_INFO)) {
        final StringTokenizer st = new StringTokenizer(line);
        st.nextToken();// skip info token
        final String counter = st.nextToken(); // counter value
        result = Integer.parseInt(counter);
      } else {
        throwUnexpectedError("Can not read P4 counter: \"" + line + '\"');
      }
    } catch (final IOException e) {
      throwUnexpectedError(StringUtils.toString(e));
    } finally {
      IoUtils.closeHard(br);
    }
    return result;
  }


  /**
   * Validates that line being parsed does not containt P4 errors
   */
  private void validateLine(final String line) throws BuildException {
    if (StringUtils.isBlank(line)) throwUnexpectedError("Line to parse can no be empty or null");
    if (line.startsWith(P4ParserHelper.P4_EXIT_1) || line.startsWith(P4ParserHelper.P4_ERROR)) {
      throwUnexpectedError(line);
    }
  }


  public void throwUnexpectedError(final String error) throws BuildException {
    throw new BuildException("Error while getting value of P4 counter: " + error);
  }
}
