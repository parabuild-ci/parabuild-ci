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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Functor responsible for parsing Subversion version string.
 *
 * @see SVNVersionCommand
 */
final class SVNVersionParser {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(SVNVersionParser.class); // NOPMD


  /**
   * @param versionLine a String in 1.2.3 format
   *
   * @return result of parsing or null if the version line cannot be parsed
   */
  public SVNVersion parse(final String versionLine) {
    if (StringUtils.isBlank(versionLine)) return null;
    final Matcher matcher = Pattern.compile("([0-9]+)\\.([0-9]+).*").matcher(versionLine);
    if (!matcher.matches()) return null;
    final int groupCount = matcher.groupCount();
    if (groupCount == 2) {
      final String stringMajor = matcher.group(1);
      final String stringMinor = matcher.group(2);
      if (StringUtils.isValidInteger(stringMajor)
        && StringUtils.isValidInteger(stringMinor)) {
        return new SVNVersion(Integer.parseInt(stringMajor), Integer.parseInt(stringMinor));
      }
    }
    return null;
  }
}
