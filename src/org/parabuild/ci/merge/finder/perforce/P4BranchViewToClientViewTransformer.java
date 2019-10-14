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
package org.parabuild.ci.merge.finder.perforce;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.versioncontrol.perforce.P4ClientView;
import org.parabuild.ci.versioncontrol.perforce.P4ClientViewLine;
import org.parabuild.ci.versioncontrol.perforce.P4ClientViewParser;

import java.util.List;

/**
 *
 */
public class P4BranchViewToClientViewTransformer {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(P4NewChangeListsFinder.class); // NOPMD

  private static final String DUMMY_RELATIVE_DIR = "dummy";

  private final String branchView;
  private final boolean reverse;


  /**
   * Constructor.
   *
   * @param branchView to transform
   */
  public P4BranchViewToClientViewTransformer(final String branchView, final boolean reverse) {
    this.branchView = ArgumentValidator.validateArgumentNotBlank(branchView, "branch view");
    this.reverse = reverse;
  }


  /**
   * Transforms branch view to a client view. The client
   * contains source or "letf" part of the view.
   *
   * @return client view containing the source or "letf"
   *  part of the branch view.
   *
   * @throws ValidationException
   */
  public String transformToSourceClientView() throws ValidationException {
    return reverse ? doTransformToTargetClientView() : doTransformToSourceClientView();
  }


  /**
   * Transforms branch view to a client view. The client
   * contains target or "right" part of the view.
   *
   * @return client view containing the target or "right"
   *  part of the branch view.
   *
   * @throws ValidationException
   */
  public String transformToTargetClientView() throws ValidationException {
    return reverse ? doTransformToSourceClientView() : doTransformToTargetClientView();
  }


  private String doTransformToSourceClientView() throws ValidationException {
    final P4ClientViewParser clientViewParser = new P4ClientViewParser(false);
    clientViewParser.setValidateClientPath(false);
    final P4ClientView p4ClientView = clientViewParser.parse(DUMMY_RELATIVE_DIR, branchView);
    final List clientViewLines = p4ClientView.getClientViewLines();
    final StringBuffer sourceClientView = new StringBuffer(500);
    for (int i = 0; i < clientViewLines.size(); i++) {
      final P4ClientViewLine line = (P4ClientViewLine)clientViewLines.get(i);
      final String fromSide = line.getDepotSide();
      final int doubleSlashIndex = fromSide.indexOf("//");
      sourceClientView.append(fromSide).append(' ').append(fromSide.substring(doubleSlashIndex)).append('\n');
    }

    // normalize
    return normalize(sourceClientView);
  }


  private String doTransformToTargetClientView() throws ValidationException {
    final P4ClientViewParser clientViewParser = new P4ClientViewParser(false);
    clientViewParser.setValidateClientPath(false);
    final P4ClientView p4ClientView = clientViewParser.parse(DUMMY_RELATIVE_DIR, branchView);
    final List clientViewLines = p4ClientView.getClientViewLines();
    final StringBuffer targetClientView = new StringBuffer(500);
    for (int i = 0; i < clientViewLines.size(); i++) {
      final P4ClientViewLine line = (P4ClientViewLine)clientViewLines.get(i);
      final String fromSide = line.getDepotSide();
      final int doubleSlashIndex = fromSide.indexOf("//");
      final String toSide = line.getClientSide();
      targetClientView.append(fromSide, 0, doubleSlashIndex).append(toSide).append(' ').append(toSide).append('\n');
    }

    // normalize
    return normalize(targetClientView);
  }


  private static String normalize(final StringBuffer view) throws ValidationException {
    final StringBuilder result = new StringBuilder(500);
    final P4ClientViewParser clientViewParser = new P4ClientViewParser(true);
    final P4ClientView p4ClientView = clientViewParser.parse(DUMMY_RELATIVE_DIR, view.toString());
    final List clientViewLines = p4ClientView.getClientViewLines();
    for (int i = 0; i < clientViewLines.size(); i++) {
      final P4ClientViewLine p4ClientViewLine = (P4ClientViewLine)clientViewLines.get(i);
      result.append(p4ClientViewLine.getDepotSide()).append(' ').append(p4ClientViewLine.getClientSide()).append('\n');
    }
    return result.toString();
  }


  public String toString() {
    return "BranchViewToClientViewTransformer{" +
      "branchView='" + branchView + '\'' +
      ", reverse=" + reverse +
      '}';
  }
}
