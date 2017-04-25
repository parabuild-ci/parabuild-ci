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

import org.apache.log4j.Logger;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import viewtier.util.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * EnvironmentPanel is used to display a table with environment variables.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Mar 29, 2010 1:56:00 PM
 */
public final class EnvironmentPanel extends MessagePanel {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(EnvironmentPanel.class); // NOPMD
  private static final long serialVersionUID = 3734377444641156105L;


  public EnvironmentPanel(final Map map, final String title) {
    super(title);
    final GridIterator gridIter = new GridIterator(getUserPanel(), 2);
    final SortedMap sourtedMap = new TreeMap(map);
    for (final Iterator iterator = sourtedMap.entrySet().iterator(); iterator.hasNext();) {
      final Map.Entry entry = (Map.Entry) iterator.next();
      final String propName = (String) entry.getKey();
      if (propName.startsWith("viewtier") || propName.startsWith("parabuild")) {
        continue;
      }
      final String value = StringUtils.wrap((String) entry.getValue(), 85);
      final AboutLabel lbValue = new AboutLabel(value);
      gridIter.addPair(new AboutLabel(propName), lbValue);
    }
  }
}
