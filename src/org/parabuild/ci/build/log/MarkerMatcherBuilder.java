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
package org.parabuild.ci.build.log;

import org.parabuild.ci.common.StringUtils;

import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * MarkerMatcherBuilder
 * <p/>
 *
 * @author Slava Imeshev
 * @since Oct 3, 2008 3:51:55 PM
 */
public final class MarkerMatcherBuilder {

  public MarkerMatcher createMarkerMatcher(final String markers) throws PatternSyntaxException {
    final List markerList = StringUtils.multilineStringToList(markers);
    if (markerList.isEmpty()) {
      return new DummyMarkerMatcher();
    } else {
      return new MarkerMatcherList(markerList);
    }
  }
}
