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
import org.parabuild.ci.util.IoUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Perforce integrate parser.
 */
final class P4IntegrateParserImpl implements P4IntegrateParser {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(P4IntegrateParserImpl.class); // NOPMD

  // NOTE: vimeshev note on operation codes
  // 1. can't branch from - usually comes from lines like /depot/dev/bt/src/org/parabuild/ci/versioncontrol/P4ClientViewComposer.java - can't branch from //depot/dev/bt30/src/org/parabuild/ci/versioncontrol/P4ClientViewComposer.java#2 without -d flag
  private static final Pattern PATTERN = Pattern.compile("(.*)#?(\\d+)? - (sync/integrate|integrate|branch/sync|sync/delete|can't integrate|can't branch|can't delete|delete) from ([^#]+)#(\\d+),?#?(\\d+)?.*");


  /**
   * Parses a file holding output from p4 integrate -n command
   *
   * @param file to parse
   *
   */
  public void parse(final File file, final P4IntegrateParserDriver driver) throws IOException {
    BufferedReader br = null;
    try {
      // get line reader and read first line
      br = new BufferedReader(new FileReader(file), 1024);

      String lineToParse = br.readLine();
      while (lineToParse != null) {
//        if (log.isDebugEnabled()) log.debug("lineToParse: " + lineToParse);
        if (lineToParse.startsWith("error: All revision(s) already integrated")) {
          return;
        }
        // check for errors
        P4ParserHelper.validateLine(lineToParse);
        // parse
        if (lineToParse.startsWith(P4ParserHelper.SPACED_P4_INFO)) {
          final String integrateLine = lineToParse.substring(P4ParserHelper.SPACED_P4_INFO_LENGTH);
//          if (log.isDebugEnabled()) log.debug("integrateLine: " + integrateLine);
          final Matcher matcher = PATTERN.matcher(integrateLine);
          if (matcher.matches()) {
            final String target = matcher.group(1);
            final String targetRev = matcher.group(2);
            final String operation = matcher.group(3);
            final String source = matcher.group(4);
            final String sourceRevStart = matcher.group(5);
            final String sourceRevEnd = matcher.groupCount() == 6 ? matcher.group(6) : sourceRevStart;
            driver.foundIntegration(new Integration(new Revision(source, sourceRevStart, sourceRevEnd), new Revision(target, targetRev), operation));
          } else {
            throw new IOException("Unexpected line format: " + lineToParse);
          }
        } else if (lineToParse.startsWith(P4ParserHelper.P4_EXIT_0)) {
          break;
        } else {
          throw new IOException("Unexpected line format: " + lineToParse);
        }
        lineToParse = br.readLine();
      }
    } catch (final RuntimeException | IOException e) {
      throw e;
    } catch (final Exception e) {
      throw IoUtils.createIOException(e);
    } finally {
      IoUtils.closeHard(br);
    }
  }


  /**
   * Parses a file holding output from p4 integrate -n command
   *
   * @param file to parse
   *
   * @return Collection of {@link Integration} objects.
   */
  public Collection parse(final File file) throws IOException {
    final List result = new LinkedList();
    parse(file, new P4IntegrateParserDriver() {
      public void foundIntegration(final Integration integration) {
        result.add(integration);
      }
    });
    return result;
  }
}

