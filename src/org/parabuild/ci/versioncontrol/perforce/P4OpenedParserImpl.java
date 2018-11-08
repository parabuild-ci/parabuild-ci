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
import java.util.regex.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.common.IoUtils;

/**
 */
public class P4OpenedParserImpl implements P4OpenedParser {


  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(P4IntegrateParserImpl.class); // NOPMD

  private static final Pattern PATTERN = Pattern.compile("(.*)#(\\d+) - (edit|add|delete|integrate|branch).*");


  public void parse(final File file, final P4OpenedDriver driver) throws IOException {
    BufferedReader br = null;
    try {
      // get line reader and read first line
      br = new BufferedReader(new FileReader(file), 1024);

      String lineToParse = P4ParserHelper.readLineAndValidate(br);
      while (lineToParse != null) {
//        if (log.isDebugEnabled()) log.debug("lineToParse: " + lineToParse);
        // parse
        if (lineToParse.startsWith(P4ParserHelper.SPACED_P4_INFO)) {
          final String openedLine = lineToParse.substring(P4ParserHelper.SPACED_P4_INFO_LENGTH);
//          if (log.isDebugEnabled()) log.debug("openedLine: " + openedLine);
          final Matcher matcher = PATTERN.matcher(openedLine);
          if (matcher.matches()) {
            driver.process(new OpenedImpl(matcher.group(1), matcher.group(2), matcher.group(3)));
          } else {
            // not recognizing that something is opened is
            // bad, so we fail fast here
            throw new IOException("Unexpected line format while parsing output of p4 open: " + lineToParse);
          }
        } else if (lineToParse.startsWith(P4ParserHelper.P4_EXIT_0)) {
          break;
        } else {
          if (log.isDebugEnabled()) log.debug("Unexpected line format: lineToParse: " + lineToParse);
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
}
