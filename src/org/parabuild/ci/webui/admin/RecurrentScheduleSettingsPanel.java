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

import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;

/**
 */
public final class RecurrentScheduleSettingsPanel extends AbstractScheduleSettingsPanel {

  private static final long serialVersionUID = 1301336121355637905L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(RecurrentScheduleSettingsPanel.class); // NOPMD

  private final ScheduleItemsTable tblScheduleItems = new ScheduleItemsTable();  // NOPMD
  private final ConfigurationManager cm = ConfigurationManager.getInstance();  // NOPMD


  /**
   * Creates message panel with title displayed
   */
  public RecurrentScheduleSettingsPanel() {
    super("Schedule Settings");

    // add default row
    final ScheduleItem item = new ScheduleItem();
    item.setHour("3");
    item.setDayOfWeek("1-5");
    item.setDayOfMonth("");
    final List defaultRow = new ArrayList(3);
    defaultRow.add(item);
    tblScheduleItems.setScheduleItems(defaultRow);

    // layout
    gridIterator.add(WebuiUtils.makePanelDivider(), 2);
    gridIterator.add(tblScheduleItems, 2);

    // bind properties

    // set defaults
    setCleanCheckoutInterval(1); // clean checkout every build
  }


  public final void setBuildID(final int buildID) {
    super.setBuildID(buildID);
    this.tblScheduleItems.setBuildID(buildID);
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public final void load(final BuildConfig buildConfig) {
    super.load(buildConfig);
//    if (log.isDebugEnabled()) log.debug("Loading data to schedule items table, buildID: " + buildConfig.getBuildID());
    this.tblScheduleItems.setScheduleItems(cm.getScheduleItems(buildConfig.getBuildID()));
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public final boolean validate() {
    // call super
    boolean valid = super.validate();

    // validate schedule items
    valid = tblScheduleItems.validate() && valid;

    // return
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
  public final boolean save() {
    // call super
    boolean saved = super.save();

    // save schedule items
    saved &= tblScheduleItems.save();

    // return
    return saved;
  }
}
