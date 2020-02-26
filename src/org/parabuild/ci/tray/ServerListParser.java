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
package org.parabuild.ci.tray;

import java.util.*;

import org.parabuild.ci.util.*;

/**
 * Parses server list
 */
final class ServerListParser {

  public List parse(final String serverList) {
    final List result = new ArrayList(5);
    if (StringUtils.isBlank(serverList)) return result;
    final StringTokenizer st = new StringTokenizer(serverList, ",; ", false);
    while (st.hasMoreTokens()) {
      result.add(st.nextToken().toLowerCase());
    }
    return result;
  }
}
