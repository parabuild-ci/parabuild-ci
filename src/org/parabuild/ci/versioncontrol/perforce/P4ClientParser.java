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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.IoUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Perforce branch view parser.
 */
final class P4ClientParser {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(P4ClientParser.class); // NOPMD

  private static final String P4_INFO_VIEW = P4ParserHelper.P4_INFO + " View:";
  private static final String TABBED_P4_INFO = P4ParserHelper.P4_INFO + " \t";
  private static final int TABBED_P4_INFO_LENGTH = TABBED_P4_INFO.length();


  /**
   * Parses a file holding output from p4 brach -o command
   *
   * @param file to parse
   * @return Perforce pranch view
   */
  public P4Client parse(final File file) throws IOException {

    BufferedReader br = null;
    String viewLines = null;
    try {
      // get line reader and read first line
      br = new BufferedReader(new FileReader(file), 1024);

      String lineToParse = br.readLine();
      while (lineToParse != null) {
//        if (log.isDebugEnabled()) log.debug("lineToParse: " + lineToParse);

        // check for errors
        P4ParserHelper.validateLine(lineToParse);

        // parse
        if (lineToParse.equals(P4ParserHelper.P4_INFO)) { // NOPMD
          // continue

        } else if (lineToParse.startsWith(P4_INFO_VIEW)) {

          final StringBuilder sb = new StringBuilder(200);
          String viewLine = br.readLine();
//          if (log.isDebugEnabled()) log.debug("viewLine: " + viewLine);
          while (viewLine != null && viewLine.startsWith(TABBED_P4_INFO)) {
            if (!viewLine.equals(TABBED_P4_INFO)) {
              if (sb.length() > 0) sb.append('\n');
              sb.append(viewLine.substring(TABBED_P4_INFO_LENGTH));
            }
            viewLine = br.readLine();
          }
          viewLines = sb.toString();


        } else if (lineToParse.startsWith(P4ParserHelper.P4_EXIT_0)) {
          break;
        }
        // next line
        lineToParse = br.readLine();
      }
      return new P4Client(viewLines);
    } catch (final BuildException e) {
      throw IoUtils.createIOException(e);
    } finally {
      IoUtils.closeHard(br);
    }
  }
}