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

/**
 * Perforce branch view parser.
 */
final class P4BranchViewParserImpl implements P4BranchViewParser {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(P4BranchViewParserImpl.class); // NOPMD

  private static final String P4_INFO_ACCESS = P4ParserHelper.P4_INFO + " Access:\t";
  private static final String P4_INFO_BRANCH = P4ParserHelper.P4_INFO + " Branch:\t";
  private static final String P4_INFO_DESCRIPTION = P4ParserHelper.P4_INFO + " Description:";
  private static final String P4_INFO_OPTIONS = P4ParserHelper.P4_INFO + " Options:\t";
  private static final String P4_INFO_OWNER = P4ParserHelper.P4_INFO + " Owner:\t";
  private static final String P4_INFO_UPDATE = P4ParserHelper.P4_INFO + " Update:\t";
  private static final String P4_INFO_VIEW = P4ParserHelper.P4_INFO + " View:";
  private static final String TABBED_P4_INFO = P4ParserHelper.P4_INFO + " \t";


  /**
   * Parses a file holding output from p4 brach -o command
   *
   * @param file to parse
   *
   * @return Perforce pranch view
   */
  public P4BranchView parse(final File file) throws IOException {

    BufferedReader br = null;
    String branch = null;
    String access = null;
    String options = null;
    String owner = null;
    String update = null;
    String view = null;
    String description = null;
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
        } else if (lineToParse.startsWith(P4_INFO_BRANCH)) {
          branch = lineToParse.substring(P4_INFO_BRANCH.length());

        } else if (lineToParse.startsWith(P4_INFO_ACCESS)) {
          access = lineToParse.substring(P4_INFO_ACCESS.length());

        } else if (lineToParse.startsWith(P4_INFO_OPTIONS)) {
          options = lineToParse.substring(P4_INFO_OPTIONS.length());

        } else if (lineToParse.startsWith(P4_INFO_OWNER)) {
          owner = lineToParse.substring(P4_INFO_OWNER.length());

        } else if (lineToParse.startsWith(P4_INFO_UPDATE)) {
          update = lineToParse.substring(P4_INFO_UPDATE.length());

        } else if (lineToParse.startsWith(P4_INFO_DESCRIPTION)) {
          final StringBuffer sb = new StringBuffer(200);
          String descrLine = br.readLine();
          while (descrLine != null && descrLine.startsWith(TABBED_P4_INFO)) {
            if (!descrLine.equals(TABBED_P4_INFO)) {
              if (sb.length() > 0) sb.append('\n');
              sb.append(descrLine.substring(TABBED_P4_INFO.length()));
            }
            descrLine = br.readLine();
          }
          description = sb.toString();

        } else if (lineToParse.startsWith(P4_INFO_VIEW)) {

          final StringBuffer sb = new StringBuffer(200);
          String viewLine = br.readLine();
//          if (log.isDebugEnabled()) log.debug("viewLine: " + viewLine);
          while (viewLine != null && viewLine.startsWith(TABBED_P4_INFO)) {
            if (!viewLine.equals(TABBED_P4_INFO)) {
              if (sb.length() > 0) sb.append('\n');
              sb.append(viewLine.substring(TABBED_P4_INFO.length()));
            }
            viewLine = br.readLine();
          }
          view = sb.toString();


        } else if (lineToParse.startsWith(P4ParserHelper.P4_EXIT_0)) {
          break;
        }
        // next line
        lineToParse = br.readLine();
      }
      return new P4BranchViewImpl(branch, update, access, owner, description, options, view);
    } catch (BuildException e) {
      throw IoUtils.createIOException(e);
    } finally {
      IoUtils.closeHard(br);
    }
  }
}
