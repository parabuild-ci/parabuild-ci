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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.InputValidator;
import org.parabuild.ci.object.IssueTrackerProperty;
import org.parabuild.ci.webui.common.CodeNameDropDown;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.CommonCheckBox;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Label;
import viewtier.ui.Tierlet;

import java.util.List;

//import org.parabuild.ci.relnotes.*;

/**
 * This panel is used by build set up panel to set up release
 * notes obtained from FogBugz.
 */
public final class FogBugzSetupPanel extends AbstractIssueTrackerSetupPanel {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(FogBugzSetupPanel.class); // NOPMD
  private static final long serialVersionUID = 7329417969336767278L; // NOPMD

  private static final String STR_FBZ_DB = "FogBugz database:";
  private static final String STR_FBZ_DB_DB = "FogBugz database name:";
  private static final String STR_FBZ_DB_HOST = "FogBugz database host:";
  private static final String STR_FBZ_DB_PASSWD = "FogBugz database password:";
  private static final String STR_FBZ_DB_PORT = "FogBugz database port:";
  private static final String STR_FBZ_DB_USER = "FogBugz database user:";
  private static final String STR_FBZ_PROJECT = "FogBugz project code:";
  private static final String STR_ISSUE_CLOSED = "Log closed";
  private static final String STR_ISSUE_FIXED = "Log fixed";

  // fields
  private final Button btnTestConnection = new CommonButton("Test connection"); // NOPMD
  private final CommonCheckBox cbClosed = new CommonCheckBox(); // NOPMD
  private final CommonCheckBox cbFixed = new CommonCheckBox(); // NOPMD
  private final CodeNameDropDown ddDatabase = new FogBugzDatabaseDropDown(); // NOPMD
  private final EncryptingPassword flDbPasswd = new EncryptingPassword(30, 30, "fogz_db_password"); // NOPMD
  private final CommonField flDbDB = new CommonField(10, 10); // NOPMD
  private final CommonField flDbHost = new CommonField(30, 30); // NOPMD
  private final CommonField flDbPort = new CommonField(5, 5); // NOPMD
  private final CommonField flDbUSer = new CommonField(30, 30); // NOPMD
  private final CommonField flProject = new CommonField(15, 15); // NOPMD
//  private final CommonField flVersion = new CommonField(10, 10); // NOPMD
  private final Label lbConnTestResult = new CommonLabel(); // NOPMD
  private final Label lbProjectValue = new CommonFieldLabel(); // NOPMD // value (r/o) labels


  public FogBugzSetupPanel() {
    super("FogBugz");
    // layout db releated stuff
    gridIter.addPair(new CommonFieldLabel(STR_FBZ_DB), new RequiredFieldMarker(ddDatabase));
    gridIter.addPair(new CommonFieldLabel(STR_FBZ_DB_HOST), new RequiredFieldMarker(flDbHost));
    gridIter.addPair(new CommonFieldLabel(STR_FBZ_DB_PORT), new RequiredFieldMarker(flDbPort));
    gridIter.addPair(new CommonFieldLabel(STR_FBZ_DB_DB), new RequiredFieldMarker(flDbDB));
    gridIter.addPair(new CommonFieldLabel(STR_FBZ_DB_USER), new RequiredFieldMarker(flDbUSer));
    gridIter.addPair(new CommonFieldLabel(STR_FBZ_DB_PASSWD), new RequiredFieldMarker(flDbPasswd));
    gridIter.add(WebuiUtils.makeHorizontalDivider(10), 2);
    gridIter.addPair(new CommonFieldLabel(STR_FBZ_PROJECT), new CommonFlow(new RequiredFieldMarker(flProject), lbProjectValue));
    gridIter.addPair(new CommonFieldLabel(STR_ISSUE_FIXED), cbFixed);
    gridIter.addPair(new CommonFieldLabel(STR_ISSUE_CLOSED), cbClosed);
    gridIter.addPair(new Label(), new CommonFlow(btnTestConnection, lbConnTestResult));

    // bind props to fields
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.FOGBUGZ_DB_DB, flDbDB);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.FOGBUGZ_DB_HOST, flDbHost);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.FOGBUGZ_DB_PASSWORD, flDbPasswd);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.FOGBUGZ_DB_PORT, flDbPort);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.FOGBUGZ_DB_USER, flDbUSer);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.FOGBUGZ_PROJECT_ID, flProject);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.FOGBUGZ_STATUS_CLOSED, cbClosed);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.FOGBUGZ_STATUS_FIXED, cbFixed);

    // appearance
    showHeaderDivider(true);
    setPadding(5);

    // "test connection" button listener
    btnTestConnection.addListener(new TestFogBugzConnectionButtonListener());
  }


  /**
   * Validates this panel's input.
   *
   * @param errors List to add validation error messages if any.
   *
   * @see AbstractIssueTrackerSetupPanel#doValidate(List)
   */
  protected void doValidate(final List errors) {
    setDatabaseDefaults();
    validateDatabaseSelection(errors);
    InputValidator.validateFieldNotBlank(errors, STR_FBZ_DB_HOST, flDbHost);
    InputValidator.validateFieldNotBlank(errors, STR_FBZ_DB_PORT, flDbPort);
    InputValidator.validateFieldNotBlank(errors, STR_FBZ_DB_DB, flDbDB);
    InputValidator.validateFieldNotBlank(errors, STR_FBZ_DB_USER, flDbUSer);
    InputValidator.validateFieldNotBlank(errors, STR_FBZ_DB_PASSWD, flDbPasswd);
    InputValidator.validateFieldNotBlank(errors, STR_FBZ_PROJECT, flProject);
    InputValidator.validateFieldValidPositiveInteger(errors, STR_FBZ_DB_PORT, flDbPort);
    validateIssueStatus(errors);
  }


  /**
   * Validates that at leas one issue status is selected.
   */
  private void validateIssueStatus(final List errors) {
    if (!cbClosed.isChecked() && !cbFixed.isChecked()) {
      errors.add("Either one or all of \""
        + STR_ISSUE_CLOSED + "\" and \"" + STR_ISSUE_FIXED
        + "\" should be selected");
    }
  }


  /**
   * Validates that a database is selected.
   */
  private void validateDatabaseSelection(final List errors) {
    if (ddDatabase.getCode() == FogBugzDatabaseDropDown.CODE_UNSET) {
      errors.add("Please select database server");
    }
  }


  /**
   * Fills fields that are not set with defaults.
   */
  private void setDatabaseDefaults() {
    switch (ddDatabase.getCode()) {
      case FogBugzDatabaseDropDown.CODE_MS_SQL:
        // TODO: implement
        break;
      case FogBugzDatabaseDropDown.CODE_MYSQL:
        // TODO: implement
        break;
      default:
        // do nothing
        break;
    }
  }


  /**
   * Inner class to test connection parameters to bugzilla
   * database.
   */
  private static final class TestFogBugzConnectionButtonListener implements ButtonPressedListener {

    private static final long serialVersionUID = -5276600478048898080L;


    public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
//      if (validate()) { // NOPMD
//        // create connector
//        BugzillaDatabaseConnector connector = new Bugzilla216DatabaseConnector(flDbHost.getValue(),
//          Integer.parseInt(flDbPort.getValue()),
//          flDbDB.getValue(),
//          flDbUSer.getValue(),
//          flDbPasswd.getValue());
//        // test connection
//        ConnectionTestResult testResult = connector.testConnectionToDB();
//        if (log.isDebugEnabled()) log.debug("testResult: " + testResult);
//        // display result
//        if (testResult.successful()) {
//          lbConnTestResult.setText(" Connection is OK.");
//          lbConnTestResult.setForeground(Color.DarkGreen);
//        } else {
//          lbConnTestResult.setText(" Cannot connect - check settings and database availability.");
//          lbConnTestResult.setForeground(Color.DarkRed);
//        }
//      }
      return Tierlet.Result.Continue();
    }
  }

  /**
   * List of databases available for FogBugz.
   */
  private static final class FogBugzDatabaseDropDown extends CodeNameDropDown {

    public static final int CODE_UNSET = 0;
    public static final int CODE_MS_SQL = 1;
    public static final int CODE_MYSQL = 2;
    private static final long serialVersionUID = 8219260151575265174L;


    public FogBugzDatabaseDropDown() {
      super.addCodeNamePair(CODE_UNSET, "Please select FogBugz database");
      super.addCodeNamePair(CODE_MS_SQL, "Microsoft SQL Server");
      super.addCodeNamePair(CODE_MYSQL, "MySQL Server");
    }
  }
}
