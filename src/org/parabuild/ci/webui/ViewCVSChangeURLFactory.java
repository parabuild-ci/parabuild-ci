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

import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SimpleChange;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.WebuiUtils;

import java.util.Properties;

/**
 * ViewCVS-specific implementation of {@link ChangeURLFactory}.
 */
public final class ViewCVSChangeURLFactory implements ChangeURLFactory {

  private final String viewcvsRoot;
  private final String viewcvsURL;
  private final boolean showRevision;


  public ViewCVSChangeURLFactory(final String viewcvsURL, final String viewcvsRoot) {
    this(viewcvsURL, viewcvsRoot, false);
  }


  public ViewCVSChangeURLFactory(final String viewcvsURL, final String viewcvsRoot, final boolean showRevision) {
    this.viewcvsURL = viewcvsURL;
    this.viewcvsRoot = viewcvsRoot;
    this.showRevision = showRevision;
  }


  /**
   * Produces {@link ChangeURL} from the given change.
   *
   * @param change
   *
   * @return created {@link ChangeURL} object.
   */
  public ChangeURL makeChangeFileURL(final SimpleChange change) {
    return new ChangeURLImpl(makeFileURL(change), change.getFilePath());
  }


  public ChangeURL makeChangeRevisionURL(final SimpleChange change) {
    return new ChangeURLImpl(makeRevisionURL(change), change.getRevision());
  }


  /**
   * Produces {@link ChangeURL} from the given change.
   *
   * @param changeList
   *
   * @return created {@link ChangeURL} object or null if URL
   *  creation for change lists is not supported.
   */
  public ChangeURL makeChangeListNumberURL(final ChangeList changeList) {
    return null;
  }


  /**
   * Helper method.
   *
   * @param change
   *
   * @return String base URL
   */
  private String makeFileURL(final SimpleChange change) {
    // compose parameter list
    final Properties params = new Properties();
    if (showRevision) {
      //  NOTE: vimeshev - 2005-01-15 - see #815 "Remove version
      // information from ViewCVS links".
      final String revision = change.getRevision();
      if (!StringUtils.isBlank(revision)) {
        params.setProperty("rev", revision);
      }
      params.setProperty("view", "log");
    }
    return makeURL(change.getFilePath(), params);
  }


  /**
   * Helper method.
   *
   * @param change
   *
   * @return String base URL
   */
  private String makeRevisionURL(final SimpleChange change) {
    final Properties params = new Properties();
    final String revision = change.getRevision();
    if (StringUtils.isBlank(revision)) return makeFileURL(change); // fall back to file

    // get last dot
    final int lastDotIndex = revision.lastIndexOf('.');
    if (lastDotIndex <= 0 || lastDotIndex == revision.length()) return makeFileURL(change); // fall back to file

    // get tail rev number string
    final String tailRevisionNumberString = revision.substring(lastDotIndex + 1);
    if (!StringUtils.isValidInteger(tailRevisionNumberString)) return makeFileURL(change); // fall back to file

    // get tail rev number
    final int tailRevisionNumber = Integer.parseInt(tailRevisionNumberString);
    if (tailRevisionNumber <= 1) return makeFileURL(change); // fall back to file

    // make and return URL
    final int prevTailRevisionNumber = tailRevisionNumber - 1;
    final String value = revision.substring(0, lastDotIndex + 1) + prevTailRevisionNumber;
    params.setProperty("r1", value);
    params.setProperty("r2", revision);
    return makeURL(change.getFilePath(), params);
  }


  /**
   * Helper method to compose an URL based on viewcvsURL,
   * changeFilePath and URL parameters.
   *
   * @param changeFilePath
   * @param params
   */
  private String makeURL(final String changeFilePath, final Properties params) {
    // REVIEWME: vimeshev - alters params, bad
    if (!StringUtils.isBlank(viewcvsRoot)) {
      params.setProperty("root", viewcvsRoot);
    }
    // compose result URL
    final StringBuilder result = new StringBuilder(200);
    result.append(viewcvsURL);
    result.append(viewcvsURL.endsWith("/") ? "" : "/");
    if (!changeFilePath.isEmpty()) {
      result.append(changeFilePath.charAt(0) == '/' ? changeFilePath.substring(1) : changeFilePath);
    }
    result.append(WebuiUtils.makeURLParameters(params));
    return result.toString();
  }


  public String toString() {
    return "ViewCVSChangeURLFactory{" +
      "viewcvsRoot='" + viewcvsRoot + '\'' +
      ", viewcvsURL='" + viewcvsURL + '\'' +
      ", showRevision=" + showRevision +
      '}';
  }
}
