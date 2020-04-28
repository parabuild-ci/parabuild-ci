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
package org.parabuild.ci.webui.admin.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.object.BuilderConfiguration;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonBoldLink;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Link;

import java.util.List;

/**
 * This table displays list of clusters
 */
final class BuilderListTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4107190829042266770L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(BuilderListTable.class); // NOPMD

  private static final int COLUMN_COUNT = 5;

  private static final int COL_NAME = 0;
  private static final int COL_AGENT_COUNT = 1;
  private static final int COL_BUILD_COUNT = 2;
  private static final int COL_DESCRIPTION = 3;
  private static final int COL_ACTION = 4;

  private static final String CAPTION_BUILDER_NAME = "Build Farm Name";
  private static final String CAPTION_BUILD_COUNT = "Builds";
  private static final String CAPTION_AGENT_COUNT = "Agents";
  private static final String CAPTION_BUILDER_DESCRIPTION = "Description";
  private static final String CAPTION_ACTION = "Action";

  private List builderList;

  /**
   * Creates and automatically pupulates the cluster list table.
   *
   * @param validAdminUser true if this is a valid admin user.
   */
  BuilderListTable(final boolean validAdminUser) {
    super(validAdminUser ? COLUMN_COUNT : COLUMN_COUNT - 1, false);
    setWidth("100%");
    setGridColor(Pages.COLOR_PANEL_BORDER);
    this.builderList = BuilderConfigurationManager.getInstance().getBuilders();
    super.populate();
  }


  /**
   * Returs array of components containing table headers.
   * Required to be implemented by AbstractFlatTable
   *
   * @return array of components containing table headers.
   */
  public Component[] makeHeader() {
    final Component[] headers = new Label[columnCount()];
    headers[COL_NAME] = new TableHeaderLabel(CAPTION_BUILDER_NAME, "30%");
    headers[COL_AGENT_COUNT] = new TableHeaderLabel(CAPTION_AGENT_COUNT, "7%", Layout.CENTER);
    headers[COL_BUILD_COUNT] = new TableHeaderLabel(CAPTION_BUILD_COUNT, "7%", Layout.CENTER);
    headers[COL_DESCRIPTION] = new TableHeaderLabel(CAPTION_BUILDER_DESCRIPTION, "40%");
    if (columnCount() == COLUMN_COUNT) {
      headers[COL_ACTION] = new TableHeaderLabel(CAPTION_ACTION, "30%");
    }
    return headers;
  }


  /**
   * Returs array of components containing table row. Required to
   * be implemented by AbstractFlatTable
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= builderList.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final BuilderConfigurationManager bcm = BuilderConfigurationManager.getInstance();
    final BuilderConfiguration builderConf = (BuilderConfiguration) builderList.get(rowIndex);
    final Component[] row = getRow(rowIndex);
    ((Link) row[COL_NAME]).setText(builderConf.getName());
    ((Link) row[COL_NAME]).setUrl(Pages.PAGE_BUILDER_DETAILS);
    ((Link) row[COL_NAME]).setParameters(BuilderUtils.createBuilderParameters(builderConf.getID()));
    ((Label) row[COL_AGENT_COUNT]).setText(Integer.toString(bcm.getBuilderAgentCount(builderConf.getID())));
    ((Label) row[COL_BUILD_COUNT]).setText(Integer.toString(bcm.getBuilderBuildCount(builderConf.getID())));
    ((Label) row[COL_DESCRIPTION]).setText(builderConf.getDescription());
    ((BuilderCommandsFlow) row[COL_ACTION]).setBuilderID(builderConf.getID());
    return TBL_ROW_FETCHED;
  }


  /**
   * Makes row
   *
   * @return array of components containing table row.
   */
  public Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[columnCount()];
    result[COL_NAME] = new CommonBoldLink("", "");
    result[COL_AGENT_COUNT] = new CommonLabel();
    result[COL_AGENT_COUNT].setAlignX(Layout.CENTER);
    result[COL_BUILD_COUNT] = new CommonLabel();
    result[COL_BUILD_COUNT].setAlignX(Layout.CENTER);
    result[COL_DESCRIPTION] = new CommonLabel();
    result[COL_ACTION] = new BuilderCommandsFlow();
    return result;
  }


  public String toString() {
    return "BuilderTable{" +
            "builderList=" + builderList +
            '}';
  }
}
