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

import org.apache.commons.logging.*;

import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This panel holds build log configuration settings.
 */
public final class LogSettingsPanel extends MessagePanel implements Validatable, Saveable, Loadable {

  private static final Log log = LogFactory.getLog(LogSettingsPanel.class);
  private static final long serialVersionUID = -6009879325960322446L; // NOPMD

  private final GeneralLogSettingsPanel pnlGeneralLogSettings = new GeneralLogSettingsPanel();
  private final LogConfigsTable tblLogConfigs = new LogConfigsTable();

  private int buildID = BuildConfig.UNSAVED_ID;


  /**
   * Creates panel without title.
   */
  public LogSettingsPanel() {
    super(false);
//    setPadding(4);
    showHeaderDivider(true);

    // layout
    final Panel contentPanel = super.getUserPanel();
    contentPanel.add(pnlGeneralLogSettings);
    contentPanel.add(WebuiUtils.makePanelDivider());
    contentPanel.add(tblLogConfigs);
    pnlGeneralLogSettings.setWidth(Pages.PAGE_WIDTH - 4); // justify padding
  }


  /**
   * Returns build ID
   */
  public int getBuildID() {
    return buildID;
  }


  /**
   * Sets build ID
   */
  public void setBuildID(final int buildID) {
    if (log.isDebugEnabled()) log.debug("buildID: " + buildID);
    this.buildID = buildID;
    pnlGeneralLogSettings.setBuildID(buildID);
    tblLogConfigs.setBuildID(buildID);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    boolean valid = true;
    valid = this.pnlGeneralLogSettings.validate() && valid;
    valid = this.tblLogConfigs.validate() && valid;
    return valid;
  }


  /**
   * When called, component should save it's content. This method
   * should return <code>true</code> when content of a component
   * is saved successfully. If not, a component should display a
   * error message in it's area and return <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {
    boolean saved = true;
    if (buildID == BuildConfig.UNSAVED_ID) throw new IllegalArgumentException("Build ID can not be uninitialized");
    saved &= pnlGeneralLogSettings.save();
    saved &= tblLogConfigs.save();
    return saved;
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    pnlGeneralLogSettings.load(buildConfig);
    tblLogConfigs.load(buildConfig);
  }
}
