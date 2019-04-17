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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.ButtonSeparator;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.SaveButton;
import org.parabuild.ci.webui.common.WebuiUtils;
import org.parabuild.ci.object.GlobalVCSUserMap;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

/**
 * This page is responsible for creating/editting global version control user to email mapping.
 */
public final class GlobalVCSUserMapPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(GlobalVCSUserMapPage.class); // NOPMD

  private static final String CAPTION_EDIT_MAPPING = "Edit Mapping";
  private static final String CAPTION_NEW_MAPPING = "New Mapping";
  private static final String ERROR_MAPPING_NOT_FOUND = "Requested mapping not found";
  private static final String PAGE_TITLE_ADD_MAPPING = "Add new mapping";
  private static final String PAGE_TITLE_DEFAULT = "Manage mapping";

  private final GlobalVCSUserMapPanel pnlMapping = new GlobalVCSUserMapPanel();  // NOPMD
  private final SaveButton btnSave = new SaveButton();  // NOPMD
  private final CancelButton btnCancel = new CancelButton();  // NOPMD
  private final Flow flwSaveCancel = new Flow().add(btnSave).add(new ButtonSeparator()).add(btnCancel);  // NOPMD


  /**
   * Constructor.
   */
  public GlobalVCSUserMapPage() {
    // layout
    setTitle(makeTitle(PAGE_TITLE_DEFAULT)); // default title
    flwSaveCancel.setAlignX(Layout.CENTER);
    flwSaveCancel.setBackground(Pages.COLOR_PANEL_HEADER_BG);
    baseContentPanel().getUserPanel().add(pnlMapping);
    baseContentPanel().getUserPanel().add(WebuiUtils.makePanelDivider());
    baseContentPanel().getUserPanel().add(flwSaveCancel);

    // Add button listeners.
    btnCancel.addListener(createCancelListener());
    btnSave.addListener(createSaveListener());
  }


  /**
   * Strategy method derived from BasePage.
   *
   * @param params
   */
  public Result executePage(final Parameters params) {
    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
              Pages.PUBLIC_LOGIN, Pages.ADMIN_EMAIL_GLOBAL_VCS_USER_MAP, params);
    }

    if (!super.isValidAdminUser()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    if (params.isParameterPresent(Pages.PARAM_VCS_MAPPING_ID)) {
      // Mapping is provided
      final GlobalVCSUserMap map = GlobalVCSUserMapUtil.getMappingFromParameters(params);
      if (map == null) {
        // show error and exit
        baseContentPanel().getUserPanel().clear();
        baseContentPanel().showErrorMessage(ERROR_MAPPING_NOT_FOUND);
        return Result.Done();
      } else {
        // Mapping found, load data
        setTitle(makeTitle("Edit mapping \"" + map.getVcsUserName() + '\"'));
        pnlMapping.setTitle(CAPTION_EDIT_MAPPING);
        pnlMapping.load(map);
        return Result.Continue();
      }
    } else {
      // New mapping
      setFocusOnFirstInput(true);
      setTitle(makeTitle(PAGE_TITLE_ADD_MAPPING));
      pnlMapping.setTitle(CAPTION_NEW_MAPPING);
      return Result.Continue();
    }
  }


  private ButtonPressedListener createSaveListener() {
    return new ButtonPressedListener() {
      private static final long serialVersionUID = -3311229452527885804L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        if (pnlMapping.save()) {
          return Result.Done(Pages.ADMIN_EMAIL_GLOBAL_VCS_USER_MAP);
        } else {
          return Result.Continue();
        }
      }
    };
  }


  private static ButtonPressedListener createCancelListener() {
    return new ButtonPressedListener() {
      private static final long serialVersionUID = 3344092526377787414L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        return Result.Done(Pages.ADMIN_EMAIL_GLOBAL_VCS_USER_MAP);
      }
    };
  }


  public String toString() {
    return "GlobalVersionControlUserMapPage{" +
            "pnlMapping=" + pnlMapping +
            ", btnSave=" + btnSave +
            ", btnCancel=" + btnCancel +
            ", flwSaveCancel=" + flwSaveCancel +
            '}';
  }
}
