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

import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.object.*;
import org.parabuild.ci.relnotes.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This panel is used by build set up panel to set up release
 * notes obtained from Parabuild Jira listener.
 */
public final class BugzillaDirectSetupPanel extends AbstractIssueTrackerSetupPanel {

  private static final Log log = LogFactory.getLog(BugzillaDirectSetupPanel.class);
  private static final long serialVersionUID = 7329417969336767278L; // NOPMD

  private static final String STR_BZ_PRODUCT = "Bugzilla product:";
  private static final String STR_BZ_VERSION = "Version:";
  //private static final String STR_BZ_STATUSES = "Bugzilla statuses:";
  private static final String STR_CONNECTION_TYPE = "Connection type:";
  private static final String STR_BZ_MYSQL_HOST = "Bugzilla MySQL host:";
  private static final String STR_BZ_MYSQL_PORT = "Bugzilla MySQL port:";
  private static final String STR_BZ_MYSQL_DB = "Bugzilla MySQL database:";
  private static final String STR_BZ_MYSQL_USER = "Bugzilla MySQL user:";
  private static final String STR_BZ_MYSQL_PASSWD = "Bugzilla MySQL password:";
  private static final String CAPTION_DIRECT_CONNECTION_TO_BUGZILLA_DATABASE = "Direct connection to Bugzilla database";

// NOTE: vimeshev - 05/18/2004 - commented out until it's clear what to do with statuses
//  private Label lbStatuses = new BoldCommonLabel(STR_BZ_STATUSES);

  private final Field flMySQLHost = new CommonField(30, 30); // NOPMD
  private final Field flMySQLPort = new CommonField(5, 5); // NOPMD
  private final Field flMySQLDB = new CommonField(10, 10); // NOPMD
  private final Field flMySQLUSer = new CommonField(30, 30); // NOPMD
  private final EncryptingPassword flMySQLPasswd = new EncryptingPassword(30, 30, "mysql_password"); // NOPMD
  private final Field flProduct = new CommonField(15, 15); // NOPMD
  private final Field flVersion = new CommonField(15, 15); // NOPMD
// NOTE: vimeshev - 05/18/2004 - commented out until it's clear what to do with statuses
//  private Field flStatuses = new CommonField(40, 40); // NOPMD
  private final Button btnTestConnection = new CommonButton("Test connection"); // NOPMD
  private final Label lbConnTestResult = new CommonLabel(); // NOPMD


  public BugzillaDirectSetupPanel() {
    super("Bugzilla");
    // layout
    gridIter.addPair(new CommonFieldLabel(STR_BZ_PRODUCT), new RequiredFieldMarker(flProduct));
    gridIter.addPair(new CommonFieldLabel(STR_BZ_VERSION), flVersion);
    gridIter.addPair(new CommonFieldLabel(STR_CONNECTION_TYPE), new BoldCommonLabel(CAPTION_DIRECT_CONNECTION_TO_BUGZILLA_DATABASE));
    gridIter.addPair(new CommonFieldLabel(STR_BZ_MYSQL_HOST), new RequiredFieldMarker(flMySQLHost));
    gridIter.addPair(new CommonFieldLabel(STR_BZ_MYSQL_PORT), new RequiredFieldMarker(flMySQLPort));
    gridIter.addPair(new CommonFieldLabel(STR_BZ_MYSQL_DB), new RequiredFieldMarker(flMySQLDB));
    gridIter.addPair(new CommonFieldLabel(STR_BZ_MYSQL_USER), new RequiredFieldMarker(flMySQLUSer));
    gridIter.addPair(new CommonFieldLabel(STR_BZ_MYSQL_PASSWD), new RequiredFieldMarker(flMySQLPasswd));
    gridIter.add(WebuiUtils.makeHorizontalDivider(10), 2);
// NOTE: vimeshev - 05/18/2004 - commented out until it's clear what to do with statuses
//    gridIter.addPair(lbStatuses, flStatuses);
    gridIter.addPair(new Label(), new CommonFlow(btnTestConnection, lbConnTestResult));

    // bind props to fields
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.BUGZILLA_MYSQL_HOST, flMySQLHost);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.BUGZILLA_MYSQL_PORT, flMySQLPort);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.BUGZILLA_MYSQL_DB, flMySQLDB);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.BUGZILLA_MYSQL_USER, flMySQLUSer);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.BUGZILLA_MYSQL_PASSWORD, flMySQLPasswd);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.BUGZILLA_PRODUCT, flProduct);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.BUGZILLA_VERSION, flVersion);
// NOTE: vimeshev - 05/18/2004 - commented out until it's clear what to do with statuses
//    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.BUGZILLA_STATUSES, flStatuses);

    // appearance
    showHeaderDivider(true);
    setPadding(5);

    // defaults
    flMySQLPort.setValue(Integer.toString(3306));
    flMySQLDB.setValue("bugs");

    // "test connection" button listener
    btnTestConnection.addListener(new TestBugzillaConnectionButtonListener());
  }


  /**
   * Validates this panel's input.
   *
   * @param errors List to add validation error messages if any.
   *
   * @see AbstractIssueTrackerSetupPanel#doValidate(List)
   */
  protected void doValidate(final List errors) {
    WebuiUtils.validateFieldNotBlank(errors, STR_BZ_MYSQL_HOST, flMySQLHost);
    WebuiUtils.validateFieldNotBlank(errors, STR_BZ_MYSQL_PORT, flMySQLPort);
    WebuiUtils.validateFieldNotBlank(errors, STR_BZ_MYSQL_DB, flMySQLDB);
    WebuiUtils.validateFieldNotBlank(errors, STR_BZ_MYSQL_USER, flMySQLUSer);
    WebuiUtils.validateFieldNotBlank(errors, STR_BZ_MYSQL_PASSWD, flMySQLPasswd);
    WebuiUtils.validateFieldNotBlank(errors, STR_BZ_PRODUCT, flProduct);
    WebuiUtils.validateFieldValidPositiveInteger(errors, STR_BZ_MYSQL_PORT, flMySQLPort);
  }


  /**
   * Inner class to test connection parameters to bugzilla
   * database.
   */
  private final class TestBugzillaConnectionButtonListener implements ButtonPressedListener {

    private static final long serialVersionUID = 5175238890335844927L;


    public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
      if (validate()) {
        // create connector
        final BugzillaMySQLConnectionFactory connector = new BugzillaMySQLConnectionFactory(flMySQLHost.getValue(),
          Integer.parseInt(flMySQLPort.getValue()),
          flMySQLDB.getValue(),
          flMySQLUSer.getValue(),
          flMySQLPasswd.getValue());
        // test connection
        final ConnectionTestResult testResult = connector.testConnectionToDB();
        if (log.isDebugEnabled()) log.debug("testResult: " + testResult);
        // display result
        if (testResult.successful()) {
          lbConnTestResult.setText(" Connection is OK.");
          lbConnTestResult.setForeground(Color.DarkGreen);
        } else {
          lbConnTestResult.setText(" Cannot connect - check settings and database availability: " + testResult.message());
          lbConnTestResult.setForeground(Color.DarkRed);
        }
      }
      return Tierlet.Result.Continue();
    }
  }
}
