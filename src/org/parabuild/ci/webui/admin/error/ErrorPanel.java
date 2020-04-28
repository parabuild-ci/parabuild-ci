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
package org.parabuild.ci.webui.admin.error;

import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.TechnicalSupportLink;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import viewtier.ui.Component;
import viewtier.ui.Label;
import viewtier.ui.Layout;

final class ErrorPanel extends MessagePanel {

  private static final long serialVersionUID = -3085612289890248727L; // NOPMD

  private GridIterator gridIterator;


  /**
   */
  ErrorPanel(final Error error) {
    // init
    super("Error details");
    this.gridIterator = new GridIterator(getUserPanel(), 2);
    // add error info
    addErrorElement("Product: ", error.getProductVersion());
    addErrorElement("Build name: ", new BoldCommonLabel(error.getBuildName()));
    addErrorElement("Build step name: ", error.getStepName());
    addErrorElement("Host name: ", error.getHostName());
    addErrorElement("Severity: ", error.getErrorLevelAsString());
    addErrorElement("Description: ", error.getDescription());
    addErrorElement("Details: ", error.getDetails());
    addErrorElement("Possible cause: ", error.getPossibleCause());
    addErrorElement("Subsystem: ", error.getSubsystemName());
    addErrorElement("Log lines: ", error.getLogLines(), true);
    addErrorElement("Trace: ", error.getStacktrace(), true);
    addErrorElement("Time: ", SystemConfigurationManagerFactory.getManager().formatDateTime(error.getTime()));
    addErrorElement("Support forum:", new TechnicalSupportLink());
  }

  /**
   * Add unformatted text
   */
  private void addErrorElement(final String label, final String value) {
    addErrorElement(label, value, false);
  }


  /**
   * Add text with formtatting if format set to true
   */
  private void addErrorElement(final String label, final String value, final boolean format) {
    if (StringUtils.isBlank(value)) {
      return;
    }
    final Label lbElemLabel = new CommonFieldLabel(label);
    final Label lbElemValue = new CommonLabel(value);
    if (format) {
      lbElemLabel.setAlignY(Layout.TOP);
      lbElemValue.setPreserveFormatting(110);
    }
    gridIterator.addPair(lbElemLabel, lbElemValue);
  }

  private void addErrorElement(final String label, final Component comp) {
    gridIterator.addPair(new CommonFieldLabel(label), comp);
  }

  public String toString() {
    return "ErrorPanel{" +
            "gridIterator=" + gridIterator +
            '}';
  }
}
