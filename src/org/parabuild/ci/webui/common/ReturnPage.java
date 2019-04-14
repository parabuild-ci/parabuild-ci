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
package org.parabuild.ci.webui.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import viewtier.ui.Parameters;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Value object to hold return page in the session.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Jan 1, 2010 6:06:15 PM
 */
public final class ReturnPage implements Serializable {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(ReturnPage.class); // NOPMD

  public static final String PARABUILD_RETURN_PAGE = "parabuild.return.page";

  private String page = null;
  private final Properties params = new Properties();


  public void setPage(final String page) {
    this.page = page;
  }


  public String getPage() {
    return page;
  }


  public void setParameter(final String name, final String value) {
    params.setProperty(name, value);
  }


  public Parameters getParemeters() {
    final Parameters result = new Parameters();
    for (final Iterator iterator = params.entrySet().iterator(); iterator.hasNext();) {
      final Map.Entry entry = (Map.Entry) iterator.next();
      result.addParameter((String)entry.getKey(), (String)entry.getValue());
    }
    return result;
  }


  public String toString() {
    return "ReturnPage{" +
            "page='" + page + '\'' +
            ", params=" + params +
            '}';
  }
}
