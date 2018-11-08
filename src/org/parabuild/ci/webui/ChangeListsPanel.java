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

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Border;
import viewtier.ui.Panel;

/**
 * This panel display changes participating in the build.
 * The panel consists of a header, and a list (table) of changes.
 */
public final class ChangeListsPanel extends Panel {

  private static final long serialVersionUID = 5805873768802255788L; // NOPMD

  private boolean showFiles = false;


  public ChangeListsPanel() {
    setWidth("90%");
  }


  /**
   * Sets build run for which to show changes
   *
   * @param activeBuildID
   * @param changeLists
   * @return number of change lists shown
   */
  public int showChangeLists(final int activeBuildID, final List changeLists) {

    clear();
    final Integer result = (Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {

        final SecurityManager sm = SecurityManager.getInstance();
        final boolean userCanSeeChangeListDescriptions = sm.userCanSeeChangeListDescriptions(getTierletContext());
        final boolean userCanSeeChangeListFiles = sm.userCanSeeChangeListFiles(getTierletContext());
        final SimpleDateFormat formatter = new SimpleDateFormat(SystemConfigurationManagerFactory.getManager().getDateTimeFormat(), Locale.US);
        final ChangeURLFactory changeURLFactory = WebuiUtils.makeChangeURLFactory(activeBuildID);
        int changeListsCount = 0;
        for (final Iterator iter = changeLists.iterator(); iter.hasNext();) {

          final ChangeList changeList = (ChangeList) iter.next();
          final ChangelistDetailsPanel pnlDetails = new ChangelistDetailsPanel(formatter, changeList, changeURLFactory, showFiles, userCanSeeChangeListDescriptions, userCanSeeChangeListFiles);
          pnlDetails.setWidth("100%");
          pnlDetails.setBorder(makeHeaderBorder(changeListsCount == 0, !iter.hasNext()), 1, Pages.COLOR_PANEL_BORDER);
          add(pnlDetails);
          changeListsCount++;
        }
        
        return new Integer(changeListsCount);
      }
    });
    return result;
  }


  /**
   * Enables displaying change files
   */
  public void setShowFiles(final boolean showFiles) {

    this.showFiles = showFiles;
  }


  private int makeHeaderBorder(final boolean isFirstChangeList, final boolean isLastChangeList) {

    int border = Border.LEFT;
    if (showFiles) {

      if (isFirstChangeList) {

        border |= Border.TOP;
      }
    } else {

      border |= Border.TOP;
      if (isLastChangeList) {

        border |= Border.BOTTOM;
      }
    }
    return border;
  }
}
