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
package org.parabuild.ci.build;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.services.BuildStartRequestParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A parser of a change list description that finds an optional priority marker with optional parameters.
 */
final class PriorityMarkerParser {

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(PriorityMarkerParser.class); // NOPMD

  static final String PARABUILD_PRIORITY = "PARABUILD_PRIORITY";


  /**
   * Returns a list {@link org.parabuild.ci.services.BuildStartRequestParameter}.
   *
   * @param changeListDescription a change list description to parse.
   * @return a list {@link org.parabuild.ci.services.BuildStartRequestParameter}, possibly empty, if there were no
   *         parameters provided, or null if the priority marker wasn't found.
   */
  List parseChangeListDescription(final String changeListDescription) {

    final int priorityMarkerIndex = changeListDescription.indexOf(PARABUILD_PRIORITY);

    // No marker
    if (priorityMarkerIndex < 0) {

      return null;
    }


    // Marker is embedded into a string
    if (priorityMarkerIndex > 0 && changeListDescription.charAt(priorityMarkerIndex - 1) != ' ') {

      return null;
    }

    // Parse parameters
    final int equalsSignIndex = changeListDescription.indexOf('=', priorityMarkerIndex + PARABUILD_PRIORITY.length());
    if (equalsSignIndex >= 0) {

      // Found a marker
      final List startParameters = new ArrayList(1);

      final String parametersSubstring = changeListDescription.substring(equalsSignIndex + 1);

      // There might be parameters
      final int nextSeparatorIndex = parametersSubstring.indexOf(' ');
      final String parameters = nextSeparatorIndex == -1 ? parametersSubstring : parametersSubstring.substring(0, nextSeparatorIndex);

      // Split
      final StringTokenizer tokenizer = new StringTokenizer(parameters, ";", false);
      while (tokenizer.hasMoreElements()) {

        final String nameValuePair = tokenizer.nextToken();
        final int equalsIndex = nameValuePair.indexOf('=');
        if (equalsIndex <= 0) {

          continue;
        }

        // Extract parameter
        final String parameterName = nameValuePair.substring(0, equalsIndex);
        final String parameterValue = nameValuePair.substring(equalsIndex + 1);

        // Add parameter to the list.
        startParameters.add(new BuildStartRequestParameter(parameterName, parameterName + " was provided through the change list priority feature", parameterValue, 0));
      }

      return startParameters;

    } else {

      // No equals sign

      // Check if it ends with EOL or a space
      if (changeListDescription.length() == priorityMarkerIndex + PARABUILD_PRIORITY.length()) {
        return Collections.EMPTY_LIST;
      }

      if (changeListDescription.charAt(priorityMarkerIndex + PARABUILD_PRIORITY.length()) == ' ') {
        return Collections.EMPTY_LIST;
      }

      return null;
    }
  }
}
