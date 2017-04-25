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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.object.*;

/**
 * Panel to configure a PMD XML log.
 *
 * This panel does not present any additional controls because all we
 * need for PMD is path to a PMD XML file log.
 *
 * @see AbstractLogConfigPanel
 */
public final class PMDLogConfigPanel extends AbstractLogConfigPanel {

  private static final long serialVersionUID = -1731911756101403145L; // NOPMD


  /**
   * Creates message panel without title.
   */
  public PMDLogConfigPanel() {
    super(false); // no conent border
    super.setLogType(LogConfig.LOG_TYPE_PMD_XML_FILE);
    super.setLogDecription("PMD Report");
  }


  public boolean validateProperties() {
    return getErrors().isEmpty();
  }
}
