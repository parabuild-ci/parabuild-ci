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
package org.parabuild.ci.webui.admin.email;

import org.parabuild.ci.configuration.GlobalVCSUserMapManager;
import org.parabuild.ci.webui.CommonCommandLinkWithImage;
import org.parabuild.ci.webui.admin.system.NavigatableSystemConfigurationPage;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;

/**
 * This page is repsonsible for editing Parabuild's global VCS user name to email map.
 */
public final class GlobalVCSUserMapTablePage extends NavigatableSystemConfigurationPage implements ConversationalTierlet {

  private static final long serialVersionUID = -7355330766753424778L; // NOPMD
  private static final String TITLE = "Global Version Control User Map";


  /**
   * Constructor.
   */
  public GlobalVCSUserMapTablePage() {
    setTitle(makeTitle(TITLE));
  }


  protected Result executeSystemConfigurationPage(final Parameters params) {

    final Panel rightPanel = getRightPanel();

    // Add "add map" link - top
    rightPanel.add(new AddMapLink());
    rightPanel.add(WebuiUtils.makeHorizontalDivider(5));

    // Add map table
    final GlobalVCSUserMapManager manager = GlobalVCSUserMapManager.getInstance();
    final GlobalVCSUserMapTable mapTable = new GlobalVCSUserMapTable(manager.getAllMappings());
    mapTable.setWidth("100%");
    rightPanel.add(mapTable);

    // Add "add map" link - top
    rightPanel.add(new AddMapLink());
    rightPanel.add(WebuiUtils.makeHorizontalDivider(5));

    return Result.Done();
  }


  private static final class AddMapLink extends CommonCommandLinkWithImage {

    private static final String CAPTION_ADD_NEW_MAPPING = "Add New Mapping";


    AddMapLink() {
      super(CAPTION_ADD_NEW_MAPPING, Pages.ADMIN_EMAIL_GLOBAL_VCS_USER_MAP_EDIT);
    }
  }
}
