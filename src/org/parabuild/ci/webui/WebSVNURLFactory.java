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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SimpleChange;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.WebuiUtils;

import java.util.Properties;

/**
 * ViewVC SVN-specific implementation of {@link ChangeURLFactory}.
 */
public final class WebSVNURLFactory implements ChangeURLFactory {

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(WebSVNURLFactory.class); // NOPMD

  private String url;
  private boolean showRevision;
  private final String repname;


  public WebSVNURLFactory(final String url, final String repname) {
    this(url, repname, true);
  }


  public WebSVNURLFactory(final String url, final String repname, final boolean showRevision) {
    this.repname = repname;
    this.url = url;
    this.showRevision = showRevision;
  }


  /**
   * Produces {@link ChangeURL} from the given change.
   *
   * @param change a change.
   * @return created {@link ChangeURL} object.
   */
  public ChangeURL makeChangeFileURL(final SimpleChange change) {

    return new ChangeURLImpl(makeFileURL(change), change.getFilePath());
  }


  public ChangeURL makeChangeRevisionURL(final SimpleChange change) {

    return new ChangeURLImpl(makeFileURL(change), "");
  }


  /**
   * Produces {@link ChangeURL} from the given change.
   *
   * @param changeList a change list.
   * @return created {@link ChangeURL} object or null if URL
   *         creation for change lists is not supported.
   */
  public ChangeURL makeChangeListNumberURL(final ChangeList changeList) {

    final Properties params = new Properties();
    params.setProperty("rev", changeList.getNumber());
    return new ChangeURLImpl(makeURL("revision.php", "", params), changeList.getNumber());
  }


  /**
   * Helper method.
   *
   * @param change a change
   * @return String base URL
   */
  private String makeFileURL(final SimpleChange change) {

    // compose parameter list
    final Properties params = new Properties();
    if (showRevision) {

      final String revision = change.getRevision();
      if (!StringUtils.isBlank(revision)) {

        params.setProperty("rev", revision);
      }
    }
    return makeURL("filedetails.php", change.getFilePath(), params);
  }


  /**
   * Helper method to compose an URL based on url,
   * changeFilePath and URL parameters.
   *
   * @param actionPage the action page.
   * @param changeFilePath a file path.
   * @param params parameters.
   * @return a new URL.
   */
  private String makeURL(final String actionPage, final String changeFilePath, final Properties params) {

    // compose result URL
    final StringBuilder result = new StringBuilder(200);

    // REVIEWME: vimeshev - alters params, bad
    if (!StringUtils.isBlank(repname)) {

      params.setProperty("repname", repname);
    }
    if (log.isDebugEnabled()) {

      log.debug("changeFilePath: " + changeFilePath);
    }
    result.append(url);
    result.append(url.endsWith("/") ? "" : "/");
    result.append(actionPage);
    if (!StringUtils.isBlank(changeFilePath)) {
      
      params.setProperty("path", changeFilePath);
    }
    result.append(WebuiUtils.makeURLParameters(params));
    return result.toString();
  }
}
