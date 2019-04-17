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

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.VCSUserToEmailMap;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.EmailField;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebuiUtils;
import org.parabuild.ci.webui.common.WebUIConstants;
import viewtier.ui.Component;
import viewtier.ui.AbstractInput;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Table to dispay editable list of maps of VCS users to
 * e-mails.
 *
 * @see NotificationSettingsPanel
 */
public final class VCSUserToEmailTable extends AbstractFlatTable implements Validatable {

  private static final long serialVersionUID = 1519458711843586803L; // NOPMD

  private static final int COLUMN_COUNT = 2;
  private static final int COL_USER_NAME = 0;
  private static final int COL_USER_EMAIL = 1;

  public static final String NAME_USER_NAME = "Version control user name";
  public static final String NAME_USER_EMAIL = "E-Mail";

  private List userToEmailMap = new ArrayList(1);
  private final List deleted = new ArrayList(1);
  private int buildID = BuildConfig.UNSAVED_ID;
  private final byte viewMode;


  public VCSUserToEmailTable(final byte viewMode) {
    super(COLUMN_COUNT, true);
    this.viewMode = viewMode;
    setTitle("Optional Version Control User To E-Mail Map");
  }


  /**
   * Sets user to e-mail map
   *
   * @param userToEmailMap
   */
  public void populate(final List userToEmailMap) {
    this.userToEmailMap = userToEmailMap;
    populate();
  }


  /**
   * Returns modified list of user to e-mail mappings
   *
   * @return list of user to e-mail mappings
   */
  public List getUserToEmailMap() {
    return userToEmailMap;
  }


  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    clearMessage();

    // validate rows
    final ArrayList errors = new ArrayList(11);
    for (int i = 0, n = getRowCount(); i < n; i++) {
      final VCSUserToEmailMap userToEmail = (VCSUserToEmailMap) userToEmailMap.get(i);
      final Component[] row = getRow(i);
      if (isRowNewAndBlank(userToEmail, row)) {
        continue;
      }
      WebuiUtils.validateColumnNotBlank(errors, i, NAME_USER_NAME, ((AbstractInput) row[COL_USER_NAME]).getValue());
      WebuiUtils.validateColumnNotBlank(errors, i, NAME_USER_EMAIL, ((AbstractInput) row[COL_USER_EMAIL]).getValue());
      WebuiUtils.validateColumnValidEmail(errors, i, NAME_USER_EMAIL, ((AbstractInput) row[COL_USER_EMAIL]).getValue());
    }

    if (!errors.isEmpty()) {
      showErrorMessage(errors);
      return false;
    }

    return true;
  }


  /**
   * Saves table data.
   */
  public boolean save() {
    // validate
    if (buildID == BuildConfig.UNSAVED_ID) {
      throw new IllegalArgumentException("Build ID can not be uninitialized");
    }

    // delete deleted
    for (final Iterator iter = deleted.iterator(); iter.hasNext();) {
      final VCSUserToEmailMap userToEmail = (VCSUserToEmailMap) iter.next();
      if (userToEmail.getMapID() != VCSUserToEmailMap.UNSAVED_ID) {
        ConfigurationManager.getInstance().delete(userToEmail);
      }
    }

    // save modified
    for (int i = 0, n = getRowCount(); i < n; i++) {
      final Component[] row = getRow(i);
      final VCSUserToEmailMap userToEmail = (VCSUserToEmailMap) userToEmailMap.get(i);
      if (isRowNewAndBlank(userToEmail, row)) {
        continue;
      }
      if (userToEmail.getBuildID() == BuildConfig.UNSAVED_ID) {
        userToEmail.setBuildID(buildID);
      }
      userToEmail.setUserEmail(((AbstractInput) row[COL_USER_EMAIL]).getValue());
      userToEmail.setUserName(((AbstractInput) row[COL_USER_NAME]).getValue());
      ConfigurationManager.getInstance().save(userToEmail);
    }
    return true;
  }


  /**
   * Returns true if a row is new and blank
   *
   * @param userToEmail
   * @param row
   */
  private static boolean isRowNewAndBlank(final VCSUserToEmailMap userToEmail, final Component[] row) {
    return userToEmail.getBuildID() == BuildConfig.UNSAVED_ID
            && StringUtils.isBlank(((AbstractInput) row[COL_USER_EMAIL]).getValue())
            && StringUtils.isBlank(((AbstractInput) row[COL_USER_NAME]).getValue());
  }

  // =============================================================================================
  // == Table lifecycle methods                                                                 ==
  // =============================================================================================


  /**
   * This notification method is called when a row is deleted.
   */
  public void notifyRowDeleted(final int index) {
    deleted.add(userToEmailMap.remove(index));
  }


  /**
   * This notification method is called when a new row is added.
   * Implementing class can use it to keep track of deleted rows
   */
  public void notifyRowAdded(final int addedRowIndex) {
    userToEmailMap.add(addedRowIndex, new VCSUserToEmailMap());
  }


  /**
   */
  protected Component[] makeHeader() {
    final Component[] header = new Component[COLUMN_COUNT];
    header[0] = new TableHeaderLabel(NAME_USER_NAME, 177);
    header[1] = new TableHeaderLabel(NAME_USER_EMAIL, 177);
    return header;
  }


  protected Component[] makeRow(final int rowIndex) {
    final boolean editable = viewMode == WebUIConstants.MODE_EDIT;
    final AbstractInput[] row = new AbstractInput[COLUMN_COUNT];

    row[0] = new CommonField(40, 20);
    row[0].setEditable(editable);

    row[1] = new EmailField();
    row[1].setEditable(editable);
    return row;
  }


  /**
   */
  protected int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= userToEmailMap.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final Component[] row = getRow(rowIndex);
    ((AbstractInput) row[COL_USER_NAME]).setValue(get(rowIndex).getUserName());
    ((AbstractInput) row[COL_USER_EMAIL]).setValue(get(rowIndex).getUserEmail());
    return TBL_ROW_FETCHED;
  }


  /**
   * Helper method to get typed object out of the list
   *
   * @param index
   */
  private VCSUserToEmailMap get(final int index) {
    return (VCSUserToEmailMap) userToEmailMap.get(index);
  }


  public String toString() {
    return "VCSUserToEmailTable{" +
            "userToEmailMap=" + userToEmailMap +
            ", deleted=" + deleted +
            ", buildID=" + buildID +
            ", viewMode=" + viewMode +
            '}';
  }
}
