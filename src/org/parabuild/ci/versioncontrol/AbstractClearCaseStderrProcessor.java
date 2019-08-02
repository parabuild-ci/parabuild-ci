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

import org.parabuild.ci.common.StringUtils;

import java.util.List;

/**
 * Strategy class - delivers required-for-all pre-processing.
 */
public abstract class AbstractClearCaseStderrProcessor implements StderrLineProcessor {

  private final List ignoreLines;


  /**
   */
  protected AbstractClearCaseStderrProcessor(final String ignoreLines) {
    this.ignoreLines = StringUtils.multilineStringToList(ignoreLines);
  }


  /**
   * Process line index
   *
   * @param index
   * @param line
   *
   * @return result code
   *
   * @see #RESULT_ADD_TO_ERRORS
   * @see #RESULT_IGNORE
   */
  public final int processLine(final int index, final String line) {

    // NOTE: vimeshev - 10/16/2005 - see http://www.cmcrossroads.com/ubbthreads/showflat.php?Cat=0&Number=27677
    if (line.startsWith("cleartool: Error: Unable to load ") && line.endsWith("no version selected in configuration specification.")) {
      return RESULT_IGNORE;
    } else if (line.startsWith("cleartool: Error: Unable to access \"lost+found")) {
      // NOTE: vimeshev - 2006-02-06 - See bug #825. Though we
      // ignore it, it's still a mystery why they get this
      // error.
      return RESULT_IGNORE;
    }

    // process ignore lines
    for (int i = 0, n = ignoreLines.size(); i < n; i++) {
      final String ignoreLine = (String)ignoreLines.get(i);
      if (line.contains(ignoreLine)) {
        return RESULT_IGNORE;
      }
    }

    // delegate processing to the implementor
    return doProcessLine(index, line);
  }


  /**
   * Implemetors should deliver concrete line processing.
   *
   * @param index
   * @param line
   *
   * @return result code
   *
   * @see #RESULT_ADD_TO_ERRORS
   * @see #RESULT_IGNORE
   */
  protected abstract int doProcessLine(int index, String line);

  public String toString() {
    return "AbstractClearCaseStderrProcessor{" +
            "ignoreLines=" + ignoreLines +
            '}';
  }
}
