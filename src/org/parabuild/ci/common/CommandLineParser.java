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
package org.parabuild.ci.common;

import java.util.*;
import org.apache.commons.logging.*;


public final class CommandLineParser {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(CommandLineParser.class); //NOPMD

  private static final String QUOTE = "\'";
  private static final String DOUBLE_QUOTE = "\"";

  private static final int IS_NORMAL = 0;
  private static final int IS_QUOTED = 1;
  private static final int IS_DOUBLE_QUOTED = 2;

  private int state = IS_NORMAL;
  private boolean lastIsQuoted = false;
  private StringBuffer current = null;
  private List parsed = null;


  public String[] parse(final String cmd) throws CommandLineParserException {
    state = IS_NORMAL;
    lastIsQuoted = false;
    current = new StringBuffer(100);
    parsed = new ArrayList(7);
    for (final StringTokenizer st = new StringTokenizer(cmd, " \"\'", true); st.hasMoreTokens();) {
      final String next = st.nextToken();
      switch (state) {
        case IS_QUOTED:
          processQuoted(next, QUOTE);
          break;
        case IS_DOUBLE_QUOTED:
          processQuoted(next, DOUBLE_QUOTE);
          break;
        default:
          if (next.equals(QUOTE)) {
            state = IS_QUOTED;
          } else if (next.equals(DOUBLE_QUOTE)) {
            state = IS_DOUBLE_QUOTED;
          } else if (" ".equals(next)) {
            if (lastIsQuoted || current.length() != 0) {
              parsed.add(current.toString());
              current.setLength(0);
            }
          } else {
            current.append(next);
          }
          lastIsQuoted = false;
          break;
      }
    }
    finish(cmd);
    return StringUtils.toStringArray(parsed);
  }


  /**
   * Handles quoted state
   */
  private void processQuoted(final String next, final String quote) {
    if (next.equals(quote)) {
      lastIsQuoted = true;
      state = IS_NORMAL;
    } else {
      current.append(next);
    }
  }


  /**
   * Finsih parsing
   */
  private void finish(final String cmd) throws CommandLineParserException {
    if (lastIsQuoted || current.length() != 0) parsed.add(current.toString());
    if (state == IS_QUOTED || state == IS_DOUBLE_QUOTED) {
      throw new CommandLineParserException("RemoteCommand is invalid. Unmatched quotes are found in command " + cmd);
    }
  }


  public String toString() {
    return "CommandLineParser{" +
      "state=" + state +
      ", lastIsQuoted=" + lastIsQuoted +
      ", current=" + current +
      ", parsed=" + parsed +
      '}';
  }
}