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
package org.parabuild.ci.webui.admin;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates correctnes of the P4 port field.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Jun 6, 2008 11:18:37 PM
 */
final class P4PortValidator {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(P4PortValidator.class); // NOPMD
  private static final String REGEX = ".+:\\d+";


  /**
   * Returns true if P4 port field has a valid format.
   *
   * @param fieldValue
   * @return true
   */
  boolean validate(final String fieldValue) {
    final Pattern pattern = Pattern.compile(REGEX);
    final Matcher matcher = pattern.matcher(fieldValue);
    return matcher.matches();
  }


  public String toString() {
    return "P4PortValidator{}";
  }
}
