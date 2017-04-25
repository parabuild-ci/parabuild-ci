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

import java.text.*;
import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 */
public final class ChangelistDetailsPanel extends Panel {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(ChangelistDetailsPanel.class); // NOPMD
  private static final long serialVersionUID = -734043502550648382L; // NOPMD

  private final Label lbUserValue = new TableHeaderLabel("", 60); // NOPMD
  private final Label lbDescriptionValue = new TableHeaderLabel("", 305);  // NOPMD
  private final Label lbChangeListNumberLabel = new CommonLabel("Changelist: ", 70);  // NOPMD
  private final Label lbChangeListTime = new TableHeaderLabel("", 120);  // NOPMD
  private final Label lbChangeListTimeLabel = new CommonLabel("Time: ", 40);  // NOPMD
  private final Label lbUser = new CommonLabel("User: ", 30);  // NOPMD
  private final Label lbDescription = new CommonLabel("Description: ", 65);  // NOPMD


  /**
   * @param changeList
   * @param changeURLFactory
   * @param showDescription if tre change list description will be shown, otherwize hidden.
   * @param userCanSeeChangeListFiles
   */
  public ChangelistDetailsPanel(final SimpleDateFormat formatter,
    final ChangeList changeList, final ChangeURLFactory changeURLFactory,
    final boolean showFiles, final boolean showDescription,
    final boolean userCanSeeChangeListFiles) {
    setWidth("100");

    //noinspection ThisEscapedInObjectConstruction
    final GridIterator gi = new GridIterator(this, 8);

    gi.add(lbUser);
    gi.add(lbUserValue);

    gi.add(lbDescription);
    gi.add(lbDescriptionValue);

    gi.add(lbChangeListNumberLabel);
    if (changeURLFactory == null) {
      gi.add(makeChangeListNumberValueLabel(changeList));
    } else {
      final ChangeURL changeURL = changeURLFactory.makeChangeListNumberURL(changeList);
      if (changeURL != null) {
        gi.add(new CommonCommandLink(changeURL.getCaption(), changeURL.getURL()));
      } else {
        gi.add(makeChangeListNumberValueLabel(changeList));
      }
    }

    gi.add(lbChangeListTimeLabel);
    gi.add(lbChangeListTime);

    // color and border
    this.setBackground(Pages.TABLE_COLOR_HEADER_BG);

    lbUserValue.setText(changeList.getUser());
    lbChangeListTime.setText(formatter.format(changeList.getCreatedAt()));

    if (showDescription) {
      lbDescriptionValue.setText(changeList.getDescription());
    } else {
      lbDescription.setText("");
      lbDescriptionValue.setText("");
    }

    // add change list content
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    if (showFiles) {
      final List changes = cm.getChanges(changeList);
      if (!changes.isEmpty()) {
        final ChangelistContentTable changeListContentTable = new ChangelistContentTable(changeURLFactory, userCanSeeChangeListFiles);
        changeListContentTable.setWidth("100%");
        changeListContentTable.populate(changes);
        gi.add(changeListContentTable, 8);
      }
    }
  }


  /**
   * Helper agent method.
   */
  private static TableHeaderLabel makeChangeListNumberValueLabel(final ChangeList changeList) {
    return new TableHeaderLabel(StringUtils.isBlank(changeList.getNumber()) ? "" : changeList.getNumber(), 40);
  }
}
