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
package org.parabuild.ci.webui.merge;

import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.util.StringUtils;
import viewtier.ui.Border;
import viewtier.ui.Color;
import viewtier.ui.Panel;
import viewtier.ui.Text;

import java.util.Map;

/**
 * Panel to show depot View
 */
public class DepotViewPanel extends Panel {

  private static final long serialVersionUID = 8157251482509900113L;

  private static final String TEXT_UNDEFINED = "Undefined";

  private final Text flDepotView = new Text(100, 5);


  /**
   * Constructor.
   */
  public DepotViewPanel() {
    setBackground(new Color(0xFDFFD4));
    setBorder(Border.ALL, 1, new Color(0xF1FF00));
    setWidth(200);
    flDepotView.setPadding(5);
    flDepotView.setEditable(false);
    //noinspection OverridableMethodCallInConstructor
    add(flDepotView);
  }


  public void load(final int activeBuildID) {
    final Map effectiveSourceControlSettingsAsMap = ConfigurationManager.getInstance().getEffectiveSourceControlSettingsAsMap(activeBuildID);
    final String depotView = SourceControlSetting.getValue((SourceControlSetting)effectiveSourceControlSettingsAsMap.get(VersionControlSystem.P4_DEPOT_PATH), null);
    if (StringUtils.isBlank(depotView)) {
      flDepotView.setValue(TEXT_UNDEFINED + ", build ID: " + activeBuildID);
    } else {
      flDepotView.setValue(depotView);
    }
  }


  /**
   * Sets editability.
   *
   * @param mode
   */
  public void setMode(final byte mode) {
  }
}
