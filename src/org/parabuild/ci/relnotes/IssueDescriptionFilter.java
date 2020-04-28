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
package org.parabuild.ci.relnotes;

import org.parabuild.ci.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Filters issue description based on String pattern.
 */
public final class IssueDescriptionFilter {

  private String pattern = null;
  private final Map pattersToRemove = new HashMap(11); // NOPMD (SingularField)


  /**
   * Constructor
   *
   * @param pattern to perform filtering based on
   */
  public IssueDescriptionFilter(final String pattern) {
    if (StringUtils.isBlank(pattern)) {
      this.pattern = pattern;
    } else {
      this.pattern = pattern.toLowerCase();
    }
  }


  /**
   * If pattern id empty or it is found in description, it's substracted
   * from a description and the resulting description is returned. Otherwise
   * returns null.
   *
   * @param issueDescription
   *
   * @return filtered description or null if pattern is not found.
   */
  public String filter(final String issueDescription) {

    // do not filter at all if the pattern is empty
    if (StringUtils.isBlank(pattern) || issueDescription == null) {
      return issueDescription;
    }

    // find
    final int start = issueDescription.toLowerCase().indexOf(pattern);
    if (start < 0) return null;

    return issueDescription;
  }


  /**
   * Adds a list of patters that are to be removed from filtered
   * description. Patterns can be separated by ";" and by ",".
   *
   * @param patterns String list of patters to be removed from
   * filter result.
   */
  public void addPatternsToRemove(final String patterns) {
    if (StringUtils.isBlank(patterns)) return;
    for (final StringTokenizer st = new StringTokenizer(patterns, ";,"); st.hasMoreTokens();) {
      final String token = st.nextToken();
      pattersToRemove.putIfAbsent(token, token);
    }
  }


  public String toString() {
    return "IssueDescriptionFilter{" +
      "pattern='" + pattern + '\'' +
      ", pattersToRemove=" + pattersToRemove +
      '}';
  }
}
