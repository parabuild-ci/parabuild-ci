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

import java.io.*;
import java.util.*;

/**
 * Log parser for ClearCase
 */
final class ClearCaseChangeLogParser {

  private static final String[] ADD_TYPES = {};
  private static final String[] CHECKIN_TYPES = {"checkin"};
  private static final String[] DELETE_TYPES = {};
  private static final String[] IGNORE_TYPES = {"mkbranch", "rmbranch"};
  private static final String[] MAKE_TYPES = {"mkelem"};
  private static final String[] NULL_TYPES = {"**null operation kind**"};
  private static final long TIME_WINDOW_MILLIS = 60000L;
  public static final String DATE_PATTERN = "yyyyMMdd.HHmmss";

  private final TokenizingChangeLogParser delegate;


  public ClearCaseChangeLogParser(final int maxChangeLists, final String branch,final int maxChangeListSize) {
    delegate = new TokenizingChangeLogParser(
      maxChangeLists,
      TIME_WINDOW_MILLIS,
      branch,
      ClearCaseLshistoryCommand.FIELD_SEPARATOR,
      ClearCaseLshistoryCommand.END_OF_QUERY,
      DATE_PATTERN,
      CHECKIN_TYPES,
      MAKE_TYPES,
      NULL_TYPES,
      ADD_TYPES,
      DELETE_TYPES,
      IGNORE_TYPES,
      maxChangeListSize);
  }


  public List parseChangeLog(final File file) throws IOException {
    return delegate.parseChangeLog(file);
  }


  public List parseChangeLog(final InputStream input) throws IOException {
    return delegate.parseChangeLog(input);
  }
}
