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
package org.parabuild.ci.versioncontrol.perforce;

import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;

/**
 * Creates a client view lines ready for including into spec. It
 * parses client view from build configuration.
 */
final class P4ClientViewComposer {

  private String clientName = null;
  private String view = null;
  private String realtiveBuildDir = null;


  public void setClientName(final String clientName) {
    this.clientName = clientName;
  }


  public void setView(final String view) {
    this.view = view;
  }


  /**
   * Composes actual client view, i.e. //depot/src1/...
   * //parabuild_client_1/src1/... //depot/src2/...
   * //parabuild_client_1/src2/...
   *
   * @return
   *
   * @throws ValidationException
   */
  public String composeClientView() throws ValidationException {
    StringWriter sw = null;
    PrintWriter pw = null;
    try {
      // make client view template param
      sw = new StringWriter(100);
      pw = new PrintWriter(sw);
      final P4ClientViewParser parser = new P4ClientViewParser(true, clientName);
      final P4ClientView clientView = parser.parse(realtiveBuildDir, view);
      final Collection viewLines = clientView.getClientViewLines();
      for (final Iterator i = viewLines.iterator(); i.hasNext();) {
        final P4ClientViewLine viewLine = (P4ClientViewLine)i.next();
        // make a line and write to SB pw
        final StringBuilder sb = new StringBuilder(100);
        final String quotedDepotSide = StringUtils.putIntoDoubleQuotes(viewLine.getDepotSide());
        final String quotedClientSide = StringUtils.putIntoDoubleQuotes(viewLine.getClientSide());
        sb.append('\t').append(quotedDepotSide);
        sb.append(' ');
        sb.append(quotedClientSide);
        pw.println(sb.toString());
      }
      // return from pw sb
      return sw.getBuffer().toString();
    } finally {
      IoUtils.closeHard(pw);
      IoUtils.closeHard(sw);
    }
  }


  /**
   * Composes actual label view, i.e. //depot/src1/...
   * //depot/src2/...
   */
  public String composeLabelView() throws ValidationException {
    StringWriter sw = null;
    PrintWriter pw = null;
    try {
      sw = new StringWriter(100);
      pw = new PrintWriter(sw);
      final P4ClientViewParser parser = new P4ClientViewParser();
      final P4ClientView clientView = parser.parse(realtiveBuildDir, view);
      final Collection viewLines = clientView.getClientViewLines();
      for (final Iterator i = viewLines.iterator(); i.hasNext();) {
        final P4ClientViewLine viewLine = (P4ClientViewLine)i.next();
        final StringBuilder sb = new StringBuilder(100);
        final String quotedDepotSide = StringUtils.putIntoDoubleQuotes(viewLine.getDepotSide());
        sb.append('\t').append(quotedDepotSide);
        pw.println(sb.toString());
      }
      // return from pw sb
      return sw.getBuffer().toString();
    } finally {
      IoUtils.closeHard(pw);
      IoUtils.closeHard(sw);
    }
  }


  public void setRelativeBuildDir(final String relativeBuildDir) {
    this.realtiveBuildDir = relativeBuildDir;
  }
}
