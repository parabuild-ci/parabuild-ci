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
package org.parabuild.ci.webui;

import org.parabuild.ci.build.log.MarkerMatcher;
import org.parabuild.ci.build.log.MarkerMatcherBuilder;
import viewtier.util.StringUtils;
import viewtier.util.XMLEncoder;

/**
 * TextLogLineRendererImpl
 * <p/>
 *
 * @author Slava Imeshev
 * @since Oct 3, 2008 2:31:06 PM
 */
public final class TextLogLineRendererImpl implements TextLogLineRenderer {

  private final boolean logWrapEnabled;
  private final int logLineWrapLength;
  private final MarkerMatcher matcher;


  public TextLogLineRendererImpl(final boolean logWrapEnabled, final int logLineWrapLength, final String markers) {
    this.logWrapEnabled = logWrapEnabled;
    this.logLineWrapLength = logLineWrapLength;
    this.matcher = new MarkerMatcherBuilder().createMarkerMatcher(markers);
  }


  public String render(final String line) {

    // Create wrapper tags for line markers
    final String markerBeginTag;
    final String markerEndTag;
    if (matcher.match(line)) {
      markerBeginTag = "<font color=\"red\">";
      markerEndTag = "</font>";
    } else {
      markerBeginTag = "";
      markerEndTag = "";
    }

    // Encode the line
    final String encodedLine;
    if (logWrapEnabled) {
      if (line.length() < logLineWrapLength) {
        encodedLine = XMLEncoder.encode(line);
      } else {
        encodedLine = XMLEncoder.encode(StringUtils.wrap(line, logLineWrapLength));
      }
    } else {
      encodedLine = XMLEncoder.encode(line);
    }

    // Create resulting line
    return markerBeginTag + encodedLine + markerEndTag;
  }


  public String toString() {
    return "TextLogLineRendererImpl{" +
            "logWrapEnabled=" + logWrapEnabled +
            ", logLineWrapLength=" + logLineWrapLength +
            ", matcher=" + matcher +
            '}';
  }
}
