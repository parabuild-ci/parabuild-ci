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
package org.parabuild.ci.versioncontrol;

import org.parabuild.ci.remote.Agent;

import java.util.List;


/**
 * stderr processor for {@link ClearCaseSetcsCommand}
 *
 * @see ClearCaseSetcsCommand#ClearCaseSetcsCommand(Agent, String, String, String)
 */
final class ClearCaseSetcsCommandStderrProcessor extends AbstractClearCaseStderrProcessor {

  private static final String PREFIX_LHBWT = "Log has been written to";
  private static final int PREFIX_LHBWT_LENGTH = PREFIX_LHBWT.length();

  private final List updateLogFileNames;


  public ClearCaseSetcsCommandStderrProcessor(final String ignoreLines, final List updateLogFileNames) {
    super(ignoreLines);
    this.updateLogFileNames = updateLogFileNames;
  }


  protected int doProcessLine(final int index, final String line) {
    if (line.startsWith(PREFIX_LHBWT)) {
      final String unparsedLogName = line.substring(PREFIX_LHBWT_LENGTH).trim();
      if (unparsedLogName.startsWith("\"")) {
        final int end = unparsedLogName.lastIndexOf('\"');
        if (end > 1) {
          final String fileName = unparsedLogName.substring(1, end);
          updateLogFileNames.add(fileName);
        }
      }
      return RESULT_IGNORE; // ignorable
    }
    return RESULT_ADD_TO_ERRORS;
  }
}