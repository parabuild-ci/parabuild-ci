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
package org.parabuild.ci.webui.admin.usermanagement;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.object.User;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Label;

/**
 * Shows list of users in the system.
 */
public final class UsersTable extends AbstractFlatTable {

  private static final long serialVersionUID = 4107190829042266770L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(UsersTable.class); // NOPMD

  private static final int COLUMN_COUNT = 4;

  private static final int COL_NAME = 0;
  private static final int COL_FULL_NAME = 1;
  private static final int COL_EMAIL = 2;
  private static final int COL_ACTION = 3;

  public static final String STR_USER_NAME = "Login Name";
  public static final String STR_FULL_NAME = "Full Name";
  public static final String STR_EMAIL = "E-mail";
  public static final String STR_ACTION = "Action";

  private List users = null;


  public UsersTable() {
    super(COLUMN_COUNT, false);
    setTitle("User List");
    setWidth(Pages.PAGE_WIDTH);
    setGridColor(Pages.COLOR_PANEL_BORDER);
    populate(SecurityManager.getInstance().getAllUsers());
  }


  /**
   * Returs array of components containing table headers.
   * Required to be implemented by AbstractFlatTable
   */
  public Component[] makeHeader() {
    final Component[] headers = new Label[columnCount()];
    headers[COL_NAME] = new TableHeaderLabel(STR_USER_NAME, "25%");
    headers[COL_FULL_NAME] = new TableHeaderLabel(STR_FULL_NAME, "25%");
    headers[COL_EMAIL] = new TableHeaderLabel(STR_EMAIL, "25%");
    headers[COL_ACTION] = new TableHeaderLabel(STR_ACTION, "25%");
    return headers;
  }


  /**
   * Returs array of components containing table row. Required to
   * be implemented by AbstractFlatTable
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= users.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final User user = (User) users.get(rowIndex);
    final Component[] row = getRow(rowIndex);
    ((Label) row[COL_NAME]).setText(user.getName());
    ((Label) row[COL_FULL_NAME]).setText(user.getFullName());
    ((UserCommandsFlow) row[COL_ACTION]).setUserID(user.getUserID());
    ((EmailColumn) row[COL_EMAIL]).setEmail(user.getEmail());
    return TBL_ROW_FETCHED;
  }


  /**
   * Makes row
   */
  public Component[] makeRow(final int rowIndex) {
    final Component[] result = new Component[columnCount()];
    result[COL_NAME] = new CommonLabel();
    result[COL_FULL_NAME] = new CommonLabel();
    result[COL_ACTION] = new UserCommandsFlow();
    result[COL_EMAIL] = new EmailColumn();
    return result;
  }


  /**
   * Populates table with users. This list is reused in fetchRow
   * method.
   *
   * @see BuildState
   * @noinspection ParameterHidesMemberVariable
   */
  public void populate(final List users) {
    this.users = Collections.unmodifiableList(users);
    super.populate();
  }


  private static final class EmailColumn extends Flow {

    private static final long serialVersionUID = -8948189780022743743L;


    void setEmail(final String email) {
      if (StringUtils.isBlank(email)) {
        add(new CommonLabel(" "));
      } else {
        add(new CommonLink(email, "mailto:" + email));
      }
    }
  }


  public String toString() {
    return "UsersTable{" +
            "users=" + users +
            "} " + super.toString();
  }
}
