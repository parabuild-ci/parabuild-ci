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
package org.parabuild.ci.webui.result;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.object.ResultGroup;
import org.parabuild.ci.security.ResultGroupRights;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.ButtonSeparator;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.SaveButton;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;

/**
 * This page is responsible for creating/editting resultGroup
 */
public final class EditResultGroupPage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(EditResultGroupPage.class); // NOPMD

  public static final String PAGE_TITLE_DEFAULT = "Manage Result Group";
  public static final String PAGE_TITLE_ADD_RESULT_GROUP = "Add Result Group";
  public static final String ERROR_RESULT_GROUP_NOT_FOUND = "Requested result group not found";

  private final ResultGroupPanel pnlResultGroup = new ResultGroupPanel(); // NOPMD
  private final SaveButton btnSave = new SaveButton(); // NOPMD
  private final CancelButton btnCancel = new CancelButton(); // NOPMD
  private final Flow flwSaveCancel = new Flow().add(btnSave).add(new ButtonSeparator()).add(btnCancel); // NOPMD


  /**
   * Constructor.
   */
  public EditResultGroupPage() {
    // layout
    setTitle(makeTitle(PAGE_TITLE_DEFAULT)); // default title
    flwSaveCancel.setAlignX(Layout.CENTER);
    flwSaveCancel.setBackground(Pages.COLOR_PANEL_HEADER_BG);
    baseContentPanel().getUserPanel().add(pnlResultGroup);
    baseContentPanel().getUserPanel().add(WebuiUtils.makePanelDivider());
    baseContentPanel().getUserPanel().add(flwSaveCancel);

    // add cancel button listener
    btnCancel.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -2094946202675012233L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        if (log.isDebugEnabled()) log.debug("canceled - returning to result group list");
        return Result.Done(Pages.RESULT_GROUPS);
      }
    });

    // add save button listener
    btnSave.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 5395817615082159432L;


      public Result buttonPressed(final ButtonPressedEvent event) {
        if (pnlResultGroup.save()) {
          if (log.isDebugEnabled()) log.debug("saved - returning to result group list");
          return Result.Done(Pages.RESULT_GROUPS);
        } else {
          if (log.isDebugEnabled()) log.debug("couldn't save - contnuing edit");
          return Result.Continue();
        }
      }
    });
  }


  /**
   * Strategy method derived from BasePage.
   *
   * @param params
   */
  public Result executePage(final Parameters params) {
    if (log.isDebugEnabled()) log.debug("executing edit result group page");
    // authenticate
    if (!super.isValidUser()) {
      return WebuiUtils.storeReturnPathAndForward(getTierletContext(),
        Pages.PUBLIC_LOGIN, Pages.RESULT_GROUPS, params);
    }

    if (isNew()) {
      if (params.isParameterPresent(Pages.PARAM_RESULT_GROUP_ID)) {
        // resultGroup ID is provided
        final ResultGroup resultGroup = ParameterUtils.getResultGroupFromParameters(params);
        if (resultGroup == null) {
          // show error and exit
          baseContentPanel().getUserPanel().clear();
          baseContentPanel().showErrorMessage(ERROR_RESULT_GROUP_NOT_FOUND);
          return Result.Done();
        } else {
          // result group found

          // verify that a user has a right to delete this group
          final ResultGroupRights userResultGroupRights = SecurityManager.getInstance().getUserResultGroupRights(getUser(), resultGroup.getID());
          if (!userResultGroupRights.isAllowedToDeleteResultGroup()) {
            return WebuiUtils.showNotAuthorized(this);
          }

          // load data
          setTitle(makeTitle("Edit result group \"" + resultGroup.getName() + '\"'));
          pnlResultGroup.setTitle("Edit Result Group");
          pnlResultGroup.load(resultGroup);
          return Result.Continue();
        }
      } else {

        // only admin can add result groups
        if (!super.isValidAdminUser()) {
          return WebuiUtils.showNotAuthorized(this);
        }

        // new group
        setFocusOnFirstInput(true);
        setTitle(makeTitle(PAGE_TITLE_ADD_RESULT_GROUP));
        pnlResultGroup.setTitle("New Result Group");
        return Result.Continue();
      }
    } else {
      return Result.Continue();
    }
  }
}
