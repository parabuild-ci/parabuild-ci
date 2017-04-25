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

import java.io.*;
import java.util.*;
import org.apache.commons.logging.*;

/**
 * This class is repsonsible for parsing input stream containing
 * name and value pairs for environmnt variables.
 */
final class EnvironmentParser {

  private static final Log log = LogFactory.getLog(RuntimeUtils.class);
  private static final String LINE_BREAK = System.getProperty("line.separator", "");


  public Map parseEnvironment(final String environment) throws IOException {
    final Map result = new HashMap(111);
    BufferedReader stdInput = null;
    try {
      stdInput = new BufferedReader(new StringReader(environment));
      String inputLine = "";
      String prevName = null;
      while ((inputLine = stdInput.readLine()) != null) {
        //if (log.isDebugEnabled()) log.debug("inputLine: " + inputLine);
        try {
          // get position of var/value separator
          final int equalsCharIndex = inputLine.indexOf('=');
          // check if it is there
          if (equalsCharIndex == -1) {
            // this means that we are processing a previous
            // var boken by a line break
            if (!StringUtils.isBlank(prevName)) {
              final String prevValue = (String)result.get(prevName);
              if (!StringUtils.isBlank(prevValue)) {
                result.put(prevName, prevValue + LINE_BREAK + inputLine);
              }
            }
          } else {
            final String currName = inputLine.substring(0, equalsCharIndex);
            String currValue = null;
            if (equalsCharIndex + 1 > inputLine.length()) {
              currValue = "";
            } else {
              currValue = inputLine.substring(equalsCharIndex + 1);
            }
            result.put(currName, currValue);
            prevName = currName;
          }
        } catch (Exception e) {
          if (log.isWarnEnabled()) log.warn("Expected exception while parsing environment", e);
        }
      }
    } finally {
      IoUtils.closeHard(stdInput);
    }
    return result;
  }
}
