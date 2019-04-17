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
 * Parser for output of the P4 resolve command.
 */
public class P4ResolveParserImpl implements P4ResolveParser {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(P4ResolveParserImpl.class); // NOPMD

  private static final String SPACED_P4_INFO = P4ParserHelper.P4_INFO + ' ';
  private static final Pattern DIFF_PATTERN = Pattern.compile("Diff chunks: (\\d+) yours \\+ (\\d+) theirs \\+ (\\d+) both \\+ (\\d+) conflicting");
  private static final Pattern MERGING_PATTERN = Pattern.compile("(.*) - (merging) ([^#]+)#(\\d+),?#?(\\d+)?");
  private static final Pattern RESULT_PATTERN = Pattern.compile("(.*) - (ignored|merge from|copy from|resolve skipped).*");
  private static final String DIFF_CHUNKS = "Diff chunks:";
  private static final String UNEXPECTED_LINE_FORMAT = "Unexpected line format: ";


  /**
   * Parses resolve output.
   * @param file
   * @param driver
   */
  public void parse(final File file, final P4ResolveDriver driver) throws IOException {

    // Example # 1 : Successful resolve
    //    info: c:\WORK\mor2\dev\bt\build-deploy.xml - merging //depot/dev/bt31/build-deploy.xml#2,#3
    //    info: Diff chunks: 0 yours + 1 theirs + 0 both + 0 conflicting
    //    info: //vimeshev/dev/bt/build-deploy.xml - copy from //depot/dev/bt31/build-deploy.xml

    // Example # 2 : Successful resolve
    //    info: c:\WORK\mor2\dev\bt\build-helper.xml - merging //depot/dev/bt31/build-helper.xml#2,#4
    //    info: Diff chunks: 0 yours + 3 theirs + 1 both + 0 conflicting
    //    info: //vimeshev/dev/bt/build-helper.xml - copy from //depot/dev/bt31/build-helper.xml

    // Example # 3 : Failed resolve
    //    info: c:\WORK\mor2\dev\bt\conf\RELEASE-NOTES.txt - merging //depot/dev/bt31/conf/RELEASE-NOTES.txt#32,#56
    //    info: Diff chunks: 7 yours + 0 theirs + 0 both + 3 conflicting
    //    info: //vimeshev/dev/bt/conf/RELEASE-NOTES.txt - resolve skipped.

    BufferedReader br = null;
    try {
      // get line reader and read first line
      br = new BufferedReader(new FileReader(file), 1024);

      String lineToParse = P4ParserHelper.readLineAndValidate(br);
      while (lineToParse != null) {

        // parse merging line
        final String localTarget;
        final String operation;
        final String source;
        final String sourceRevStart;
        final String sourceRevEnd;
        if (log.isDebugEnabled()) log.debug("resolve line to parse: " + lineToParse);
        if (lineToParse.startsWith(SPACED_P4_INFO)) {
          final String mergingLine = lineToParse.substring(SPACED_P4_INFO.length());
          final Matcher matcher = MERGING_PATTERN.matcher(mergingLine);
          if (matcher.matches()) {
            localTarget = matcher.group(1);
            operation = matcher.group(2);
            source = matcher.group(3);
            sourceRevStart = matcher.group(4);
            sourceRevEnd = matcher.groupCount() == 5 ? matcher.group(5) : sourceRevStart;
          } else {
            throw makeUnexpectedLineFormatException(lineToParse);
          }
        } else if (lineToParse.startsWith(P4ParserHelper.P4_EXIT_0)) {
          break;
        } else {
          throw makeUnexpectedLineFormatException(lineToParse);
        }

        // parse diff line
        final int yours;
        final int theirs;
        final int both;
        final int conflicting;
        lineToParse = P4ParserHelper.readLineAndValidate(br);
        if (log.isDebugEnabled()) log.debug("resolve line to parse: " + lineToParse);
        if (lineToParse.startsWith(SPACED_P4_INFO) || lineToParse.startsWith(DIFF_CHUNKS)) {
          final String diffLine = lineToParse.startsWith(SPACED_P4_INFO) ? lineToParse.substring(SPACED_P4_INFO.length()) : lineToParse;
          final Matcher matcher = DIFF_PATTERN.matcher(diffLine);
          if (matcher.matches()) {
            yours = Integer.parseInt(matcher.group(1));
            theirs = Integer.parseInt(matcher.group(2));
            both = Integer.parseInt(matcher.group(3));
            conflicting = Integer.parseInt(matcher.group(4));
          } else {
            throw makeUnexpectedLineFormatException(lineToParse);
          }
        } else if (lineToParse.startsWith(P4ParserHelper.P4_EXIT_0)) {
          break;
        } else {
          throw makeUnexpectedLineFormatException(lineToParse);
        }

        // parse results line
        final String target;
        final String result;
        lineToParse = P4ParserHelper.readLineAndValidate(br);
        if (log.isDebugEnabled()) log.debug("resolve line to parse: " + lineToParse);
        if (lineToParse.startsWith(SPACED_P4_INFO)) {
          final String diffLine = lineToParse.substring(SPACED_P4_INFO.length());
          final Matcher matcher = RESULT_PATTERN.matcher(diffLine);
          if (matcher.matches()) {
            target = matcher.group(1);
            result = matcher.group(2);
          } else {
            throw makeUnexpectedLineFormatException(lineToParse);
          }
        } else if (lineToParse.startsWith(P4ParserHelper.P4_EXIT_0)) {
          break;
        } else {
          throw makeUnexpectedLineFormatException(lineToParse);
        }

        // hand the result to driver
        driver.process(new ResolveImpl(localTarget, operation, source, sourceRevStart, sourceRevEnd, yours, theirs, both, conflicting, target, result));

        lineToParse = P4ParserHelper.readLineAndValidate(br);
      }
    } catch (final RuntimeException | IOException e) {
      throw e;
    } catch (final Exception e) {
      throw IoUtils.createIOException(e);
    } finally {
      IoUtils.closeHard(br);
    }
  }


  private static IOException makeUnexpectedLineFormatException(final String lineToParse) {
    return new IOException(UNEXPECTED_LINE_FORMAT + lineToParse);
  }
}
