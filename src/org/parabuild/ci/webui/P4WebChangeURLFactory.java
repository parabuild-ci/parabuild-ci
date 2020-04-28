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

/**
 */
public final class P4WebChangeURLFactory implements ChangeURLFactory {

  private final String p4webURL;


  /**
   * @param p4webURL
   */
  public P4WebChangeURLFactory(final String p4webURL) {
    this.p4webURL = p4webURL;
  }


  /**
   * Produces {@link ChangeURL} from the given change.
   *
   * History:
   * <code>
   * http://public.perforce.com:8080/@md=d&cd=//public/perforce/cdsp4/&ra=s&c=AQS@//public/perforce/cdsp4/download.html?ac=22
   * </code>
   *
   * @param change
   *
   * @return created {@link ChangeURL} object.
   */
  public ChangeURL makeChangeFileURL(final SimpleChange change) {
    return new ChangeURLImpl(makeFileURL(change), change.getFilePath());
  }


  private String makeFileURL(final SimpleChange change) {
    final StringBuilder result = new StringBuilder(100);
    result.append(p4webURL);
    result.append("/@md=d");
    result.append("&cd=").append(change.getFilePath());
    result.append("&ra=s");
    result.append("&c=AQS@").append(change.getFilePath());
    result.append("?ac=22");
    return result.toString();
  }


  /**
   * Produces {@link ChangeURL} from the given change.
   *
   * Revision:
   * <code>
   * http://public.perforce.com:8080/@md=d&cd=//public/perforce/cdsp4/&cdf=//public/perforce/cdsp4/download.html&ra=s&c=iyK@//public/perforce/cdsp4/download.html?ac=19&rev1=1&rev2=2
   * </code>
   *
   * @param change
   *
   * @return created {@link ChangeURL} object.
   */
  public ChangeURL makeChangeRevisionURL(final SimpleChange change) {
    if (StringUtils.isValidInteger(change.getRevision())) {
      final int revision = Integer.parseInt(change.getRevision());
      if (revision > 1) {
        final StringBuilder result = new StringBuilder(100);
        result.append(p4webURL);
        result.append("/@md=d");
        result.append("&cd=").append(change.getFilePath());
        result.append("&cdf=").append(change.getFilePath());
        result.append("&ra=s");
        result.append("&c=iyK@").append(change.getFilePath());
        result.append("?ac=19");
        result.append("&rev1=").append((revision - 1)); // NOPMD
        result.append("&rev2=").append(revision);
        return new ChangeURLImpl(result.toString(), change.getRevision());
      } else {
        return new ChangeURLImpl(makeFileURL(change), change.getRevision());
      }
    } else {
      return new ChangeURLImpl(makeFileURL(change), change.getRevision());
    }
  }


  public ChangeURL makeChangeListNumberURL(final ChangeList changeList) {
    final StringBuilder url = new StringBuilder(100);
    url.append(p4webURL);
    url.append("/@md=d");
    url.append("@/");
    url.append(changeList.getNumber());
    url.append("?ac=10");
    return new ChangeURLImpl(url.toString(), changeList.getNumber());
  }
}
