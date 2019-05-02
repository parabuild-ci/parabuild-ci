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

import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This panel holds build setting related to e-mail build results
 * notification.
 */
public final class NotificationSettingsPanel extends MessagePanel implements Validatable, Saveable, Loadable {

  private static final long serialVersionUID = -7943989513770712199L; // NOPMD

  private final BuildWatcherTable tblWatchers;
  private final VCSUserToEmailTable tblUserToEmail;
  private final NotificationPolicyPanel pnlPolicy;
  private int buildID = BuildConfig.UNSAVED_ID;


  /**
   * Creates message panel without title.
   * @param viewMode
   */
  public NotificationSettingsPanel(final byte viewMode) {
    super(false);
    setPadding(4);
    showHeaderDivider(true);

    tblWatchers = new BuildWatcherTable(viewMode);
    tblUserToEmail = new VCSUserToEmailTable(viewMode);
    pnlPolicy = new NotificationPolicyPanel(viewMode);

    this.setWidth(Pages.PAGE_WIDTH);
    pnlPolicy.setWidth(Pages.PAGE_WIDTH);

    // show header
    tblUserToEmail.populate();
    tblWatchers.populate();

    // set defaults - add blank rows to user-email map
    for (int i = 0; i < 5; i++) {
      tblUserToEmail.addRow();
    }

    // set defaults - add blank rows to watchers
    for (int index = 0; index < 5; index++) {
      tblWatchers.addRow();
    }

    // create divider
    final Panel vDiv = new Panel();
    vDiv.setWidth(30);

    final GridIterator gi = new GridIterator(getUserPanel(), 3);
    gi.add(pnlPolicy, 3);
    gi.add(WebuiUtils.makePanelDivider(), 3);
    gi.add(tblUserToEmail).add(vDiv).add(tblWatchers);
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
    this.pnlPolicy.setBuildID(buildID);
    this.tblUserToEmail.setBuildID(buildID);
    this.tblWatchers.setBuildID(buildID);
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
    ArgumentValidator.validateBuildIDInitialized(buildID);
    boolean saved = pnlPolicy.save();
    saved &= tblUserToEmail.save();
    saved &= tblWatchers.save();
    return saved;
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    return pnlPolicy.validate() && tblUserToEmail.validate() && tblWatchers.validate();
  }


  public void load(final BuildConfig buildConfig) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    setBuildID(buildConfig.getBuildID());
    pnlPolicy.load(buildConfig);
    tblUserToEmail.populate(cm.getVCSUserToEmailMaps(buildConfig.getBuildID()));
    tblWatchers.populate(cm.getWatchers(buildConfig.getBuildID()));
  }


  public String toString() {
    return "NotificationSettingsPanel{" +
            "tblWatchers=" + tblWatchers +
            ", tblUserToEmail=" + tblUserToEmail +
            ", pnlPolicy=" + pnlPolicy +
            ", buildID=" + buildID +
            '}';
  }
}
