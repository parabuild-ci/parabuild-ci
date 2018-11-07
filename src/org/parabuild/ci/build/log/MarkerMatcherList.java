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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * MarkerMatcherList
 * <p/>
 *
 * @author Slava Imeshev
 * @since Oct 3, 2008 3:33:14 PM
 */
public final class MarkerMatcherList implements MarkerMatcher {

  private static final Log LOG = LogFactory.getLog(HTMLDirLogHandler.class); // NOPMD

  private final List matcherList = new ArrayList(1);
  private static final String PREFIX = "^";
  private static final String SUFFIX = "$";


  public MarkerMatcherList(final List markers) throws PatternSyntaxException {
    for (int i = 0; i < markers.size(); i++) {
      final String marker = (String) markers.get(i);
      if (isRegex(marker)) {
        matcherList.add(new RegexMatcher(stripRegex(marker)));
      } else {
        matcherList.add(new SimpleMatcher(marker));
      }
    }
  }


  private String stripRegex(final String marker) {
    return marker.substring(1, marker.length() - 1);
  }


  private boolean isRegex(final String marker) {
    return marker.startsWith(PREFIX) && marker.endsWith(SUFFIX);
  }


  public boolean match(final String line) {
    for (int i = 0; i < matcherList.size(); i++) {
      final MarkerMatcher matcher = (MarkerMatcher) matcherList.get(i);
      if (matcher.match(line)) {
        return true;
      }
    }
    return false;
  }


  private static final class SimpleMatcher implements MarkerMatcher {

    private String stringToMatch;


    SimpleMatcher(final String stringToMatch) {
      this.stringToMatch = stringToMatch;
    }


    public boolean match(final String string) {
      return string.contains(stringToMatch);
    }


    public String toString() {
      return "SimpleMatcher{" +
              "stringToMatch='" + stringToMatch + '\'' +
              '}';
    }
  }

  private static final class RegexMatcher implements MarkerMatcher {

    private final Pattern pattern;


    RegexMatcher(final String regex) throws PatternSyntaxException {
      this.pattern = Pattern.compile(regex);
    }


    public boolean match(final String line) {
      return pattern.matcher(line).matches();
    }


    public String toString() {
      return "RegexMatcher{" +
              "pattern=" + pattern +
              '}';
    }
  }


  public String toString() {
    return "MarkerMatcherList{" +
            "matcherList=" + matcherList +
            '}';
  }
}
