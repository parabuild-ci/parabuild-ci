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
package org.parabuild.ci.webui.admin.parameters;

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.webui.CommonCommandLinkWithImage;
import org.parabuild.ci.webui.admin.system.NavigatableSystemConfigurationPage;
import org.parabuild.ci.webui.common.BreakLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

import java.util.Properties;

/**
 * Page with a list of parameters.
 */
public final class SystemVariableListPage extends NavigatableSystemConfigurationPage implements StatelessTierlet {

  private static final long serialVersionUID = -2472052514871569348L; // NOPMD
  private static final String CAPTION_ADD_VARIABLE = "Add Variable";
  private static final String CAPTION_VARIABLES = "Variables";


  public SystemVariableListPage() {
    setTitle(makeTitle(CAPTION_VARIABLES));
  }


  protected Result executeSystemConfigurationPage(final Parameters parameters) {
    try {
      final GridIterator gi = new GridIterator(getRightPanel(), 1);

      // Get Parameters
      final byte variableType = SystemVariableUtils.getValidType(parameters);
      final int variableOwner = SystemVariableUtils.getValidOwner(variableType, parameters);
      final String title = createTableTitle(variableType, variableOwner);
      setTitle(title);

      final MessagePanel pnlHeader = new MessagePanel(CAPTION_VARIABLES);
      pnlHeader.setWidth("100%");
      pnlHeader.getUserPanel().add(new BreakLabel());
      pnlHeader.getUserPanel().add(new CommonLabel("A variable is a shell variable that is available to build scripts when running a build. Variables may also be used as template variables in some entry fields."));
      pnlHeader.getUserPanel().add(new BreakLabel());

      // Add variable list table
      gi.add(pnlHeader);
      gi.add(WebuiUtils.makePanelDivider());
      gi.add(new SystemVariableListTable(title, variableType, variableOwner, isValidAdminUser()));

      // Add new variable link - bottom
      if (isValidAdminUser()) {
        gi.add(WebuiUtils.makeHorizontalDivider(5));
        gi.add(makeNewVariavleLink(variableType, variableOwner));
      }
      return Result.Done();
    } catch (ValidationException ve) {
      return showPageErrorAndExit(StringUtils.toString(ve));
    }
  }


  /**
   * Creates table title.
   *
   * @param variableType
   * @param variableOwner
   * @return table title.
   */
  private String createTableTitle(final byte variableType, final int variableOwner) {
    final String tableTitle;
    if (variableType == StartParameter.TYPE_AGENT) {
      tableTitle = "AGENT " + CAPTION_VARIABLES + ": " + BuilderConfigurationManager.getInstance().getAgentConfig(variableOwner).getHost();
    } else if (variableType == StartParameter.TYPE_PROJECT) {
      tableTitle = "PROJECT " + CAPTION_VARIABLES + ": " + ProjectManager.getInstance().getProject(variableOwner).getName();
    } else {
      tableTitle = "SYSTEM " + CAPTION_VARIABLES;
    }
    return tableTitle;
  }


  private CommonCommandLinkWithImage makeNewVariavleLink(final byte variableType, final int variableOwner) {
    final CommonCommandLinkWithImage lnkAddNewBuilder = new CommonCommandLinkWithImage(CAPTION_ADD_VARIABLE, Pages.PAGE_VARIABLE_EDIT);
    lnkAddNewBuilder.setAlignX(Layout.LEFT);
    lnkAddNewBuilder.setAlignY(Layout.TOP);
    final Properties param = new Properties();
    param.setProperty(Pages.PARAM_VARIABLE_TYPE, Byte.toString(variableType));
    param.setProperty(Pages.PARAM_VARIABLE_OWNER, Integer.toString(variableOwner));
    lnkAddNewBuilder.setParameters(param);
    return lnkAddNewBuilder;
  }
}