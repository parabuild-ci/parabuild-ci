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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.User;
import org.parabuild.ci.security.BuildRights;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.BuildListService;
import org.parabuild.ci.services.BuildService;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.webui.common.CommonBoldLink;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.CommonSummaryLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import org.parabuild.ci.webui.secured.BuildCommandsLinks;
import org.parabuild.ci.webui.secured.SecuredComponentFactory;
import viewtier.ui.Border;
import viewtier.ui.Color;
import viewtier.ui.Flow;
import viewtier.ui.Font;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Link;
import viewtier.ui.TierletContext;

import java.util.Date;
import java.util.Properties;

/**
 * Buil run summary pnael
 *
 * @noinspection FieldCanBeLocal,InstanceVariableNamingConvention
 */
public final class BuildRunSummaryPanel extends MessagePanel {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(BuildRunSummaryPanel.class); // NOPMD
  private static final long serialVersionUID = 4546658248844898708L; // NOPMD

  private static final Font FONT_BUILD_NAME = new Font(Pages.COMMON_FONT_FAMILY, Font.Bold, Pages.FONT_COMMON.getSize() + 1);

  private static final String CAPTION_BUILD_NAME = "Build name:";
  private static final String CAPTION_BUILD_NUMBER = "Build  number:";
  private static final String CAPTION_CLEAN_CHECKOUT = "Clean checkout:";
  private static final String CAPTION_DESCRIPTION = "Description:";
  private static final String CAPTION_FINISHED = "Finished:";
  private static final String CAPTION_GET_SYNC = "Get/sync:";
  private static final String CAPTION_HOST = "Host:";
  private static final String CAPTION_LABEL = "Label:";
  private static final String CAPTION_NOTE = "Note:";
  private static final String CAPTION_PERFORCE_CLIENT_NAME = "Perforce client name:";
  private static final String CAPTION_RE_RUN_OF = "Re-run of:";
  private static final String CAPTION_RESULT = "Result:";
  private static final String CAPTION_STARTED = "Started:";
  private static final String CAPTION_STARTED_BY = "Started by:";
  private static final String CAPTION_SUMMARY = "Summary";
  private static final String CAPTION_TIME = "Time:";
  private static final String CAPTION_VERSION = "Version: ";
  /**
   * Stateles default build run link factory.
   */
  private static final BuildRunURLFactory DEFAULT_BUILD_RUN_URL_FACTORY = new BuildRunURLFactory() {
    public LinkURL makeLinkURL(final BuildRun buildRun) {
      return new LinkURL(Pages.BUILD_CHANGES, Pages.PARAM_BUILD_RUN_ID, buildRun.getBuildRunID());
    }
  };

  private final Label lbBuildHost = new CommonSummaryLabel(CAPTION_HOST); // NOPMD
  private final Label lbBuildName = new CommonSummaryLabel(CAPTION_BUILD_NAME); // NOPMD SingularField
  private final Label lbCleanCheckout = new CommonSummaryLabel(CAPTION_CLEAN_CHECKOUT);  // NOPMD SingularField
  private final Label lbDescription = new CommonSummaryLabel(CAPTION_DESCRIPTION); // NOPMD SingularField
  private final Label lbLabel = new CommonSummaryLabel(CAPTION_LABEL); // NOPMD SingularField
  private final Label lbNote = new CommonSummaryLabel(CAPTION_NOTE); // NOPMD SingularField
  private final Label lbPerforceClientName = new CommonSummaryLabel(CAPTION_PERFORCE_CLIENT_NAME); // NOPMD SingularField
  private final Label lbReRun = new CommonSummaryLabel(CAPTION_RE_RUN_OF); // NOPMD SingularField
  private final Label lbStartedBy = new CommonSummaryLabel(CAPTION_STARTED_BY); // NOPMD SingularField
  private final Label lbSyncNote = new CommonSummaryLabel(CAPTION_GET_SYNC); // NOPMD SingularField
  private final Label lbVersion = new CommonSummaryLabel(CAPTION_VERSION);  // NOPMD SingularField

  // data
  private final BuildResultLabel lbValueResult = new BuildResultLabel();
  private final Label lbPerforceClientNameValue = new BoldSummaryLabel();
  private final Label lbValueBuildHost = new BoldSummaryLabel();
  private final Label lbValueBuildName = new BoldSummaryLabel();
  private final Label lbValueBuildNumber = new BoldSummaryLabel();
  private final Label lbValueCleanCheckout = new BoldSummaryLabel();
  private final Label lbValueDescription = new BoldSummaryLabel();
  private final Label lbValueFinished = new BoldSummaryLabel();
  private final Label lbValueLabel = new BoldSummaryLabel();
  private final Label lbValueNote = new BoldSummaryLabel();
  private final Label lbValueStarted = new BoldSummaryLabel();
  private final Label lbValueStartedBy = new BoldSummaryLabel();
  private final Label lbValueSyncNote = new BoldSummaryLabel();
  private final Label lbValueTime = new BoldSummaryLabel();
  private final Label lbValueVersion = new BoldSummaryLabel();
  private final Link lbValueReRun = new CommonLink("", "");

  private final Label lbBuildCommandsFiller = new Label();
  private final BuildCommandsLinks fwBuildCommandLinks = SecuredComponentFactory.getInstance().makeBuildCommandsLinks();


  private BuildRunURLFactory buildRunURLFactory = DEFAULT_BUILD_RUN_URL_FACTORY;

  // Test Labels
  private final Label lbTestFailures = new CommonSummaryLabel("Test failures & errors: ", false);
  private final TestFailureComponent lbValueTestFailures = new TestFailureComponent(Color.Red, Color.Red);
  private final Label lbTestSkipped = new CommonSummaryLabel("Test skips: ", false);
  private final Label lbValueTestSkipped = new BoldSummaryLabel(false, Color.Green);
  private final Label lbTestSuccesses = new CommonSummaryLabel("Test successes: ", false);
  private final TestUpGoodComponent lbValueTestSuccesses = new TestUpGoodComponent(Color.DarkGreen, Color.DarkGreen, Pages.FILTER_SUCCESSFUL_TESTS, Pages.FILTER_NEW_SUCCESSFUL_TESTS);
  private final Label lbTestTotal = new CommonSummaryLabel("Total tests: ", false);
  private final TestUpGoodComponent lbValueTestTotal = new TestUpGoodComponent(Color.Black, Color.DarkGreen, Pages.FILTER_ALL_TESTS, Pages.FILTER_NEW_TESTS);


  // PMD
  private final Label lbPMDViolations = new CommonSummaryLabel("PMD violations: ", false);
  private final Label lbValuePMDViolations = new BoldSummaryLabel(false, Color.Red);

  // Findbugs
  private final Label lbFindbugsViolations = new CommonSummaryLabel("Findbugs bugs: ", false);
  private final Label lbValueFindbugsViolations = new BoldSummaryLabel(false, Color.Red);

  // Checkstyle
  private final Label lbCheckstyleViolations = new CommonSummaryLabel("Checkstyle violations: ", false);
  private final Label lbValueCheckstyleViolations = new BoldSummaryLabel(false, Color.Red);

  // Checkout time
  private final Label lbValueCheckoutTime = new BoldSummaryLabel(false);
  private final Label lbCheckoutTime = new CommonSummaryLabel("Checkout time: ");


  private final ConfigurationManager cm = ConfigurationManager.getInstance();


  /**
   * Default constructor.
   */
  public BuildRunSummaryPanel() {
    super(CAPTION_SUMMARY);

    // presentation tweaks
    lbSyncNote.setBorder(Border.TOP, 1, Pages.COLOR_PANEL_BORDER);
    lbValueSyncNote.setBorder(Border.TOP, 1, Pages.COLOR_PANEL_BORDER);
    lbValueBuildName.setFont(FONT_BUILD_NAME);

    // layout
    final GridIterator gridIter = new GridIterator(this.getUserPanel(), 4);

    gridIter.add(lbBuildName);
    gridIter.add(lbValueBuildName, 3);

    gridIter.add(lbBuildCommandsFiller);
    gridIter.add(fwBuildCommandLinks, 3);

    gridIter.add(lbVersion);
    gridIter.add(lbValueVersion);

    gridIter.add(lbBuildHost);
    gridIter.add(lbValueBuildHost);

    gridIter.add(new CommonSummaryLabel(CAPTION_BUILD_NUMBER));
    gridIter.add(lbValueBuildNumber);

    gridIter.add(new CommonSummaryLabel(CAPTION_STARTED));
    gridIter.add(lbValueStarted);

    gridIter.add(new CommonSummaryLabel(CAPTION_TIME));
    gridIter.add(lbValueTime);

    gridIter.add(new CommonSummaryLabel(CAPTION_FINISHED));
    gridIter.add(lbValueFinished);

    // Checkout time and clean checkout in on line
    gridIter.add(lbCheckoutTime).add(lbValueCheckoutTime);
    gridIter.add(lbCleanCheckout).add(lbValueCleanCheckout);

    // TESTS
    gridIter.add(lbTestFailures);
    gridIter.add(lbValueTestFailures, 3);

    gridIter.add(lbTestSkipped);
    gridIter.add(lbValueTestSkipped, 3);

    gridIter.add(lbTestSuccesses);
    gridIter.add(lbValueTestSuccesses, 3);

    gridIter.add(lbTestTotal);
    gridIter.add(lbValueTestTotal, 3);
    // END TESTS

    // PMD
    gridIter.add(lbPMDViolations);
    gridIter.add(lbValuePMDViolations, 3);
    // End PMD

    // Findbugs
    gridIter.add(lbFindbugsViolations);
    gridIter.add(lbValueFindbugsViolations, 3);
    // End Findbugs

    gridIter.add(new CommonSummaryLabel(CAPTION_RESULT));
    gridIter.add(lbValueResult);
    gridIter.moveToNextLine();

    gridIter.add(lbDescription);
    gridIter.add(lbValueDescription, 3);

    gridIter.add(lbReRun);
    gridIter.add(lbValueReRun, 3);

    gridIter.add(lbNote);
    gridIter.add(lbValueNote, 3);

    gridIter.add(lbStartedBy);
    gridIter.add(lbValueStartedBy, 3);

    gridIter.add(lbLabel);
    gridIter.add(lbValueLabel, 3);

    gridIter.add(lbSyncNote);
    gridIter.add(lbValueSyncNote, 3);

    gridIter.add(lbPerforceClientName);
    gridIter.add(lbPerforceClientNameValue, 3);
  }


  /**
   * Sets panel display data
   *
   * @param buildRun BuildRun to set
   */
  public void setBuildRun(final BuildRun buildRun) {
    // set values from buildRun
    lbValueResult.setBuildRun(buildRun);
    lbValueBuildNumber.setText(buildRun.getBuildRunNumberAsString());
    lbValueStarted.setText(dateTimeToString(buildRun.getStartedAt()));
    lbValueFinished.setText(dateTimeToString(buildRun.getFinishedAt()));
    lbValueTime.setText(intervalToSecondsString(buildRun.getStartedAt(), buildRun.getFinishedAt()));
    lbValueSyncNote.setText(buildRun.getSyncNote());
    lbValueLabel.setText(buildRun.getLabelNote());

    // handle standalone/leader/subordinate case
    final byte dependenceType = buildRun.getDependence();
    if (dependenceType == BuildRun.DEPENDENCE_STANDALONE) {
      lbValueBuildName.setText(buildRun.getBuildName());
    } else if (dependenceType == BuildRun.DEPENDENCE_LEADER
            || dependenceType == BuildRun.DEPENDENCE_SUBORDINATE) {

      // make a panel with list of build runs
      final ParallelBuildRunListPanel pnlBuildRunLists = new ParallelBuildRunListPanel(buildRun, buildRunURLFactory);
      pnlBuildRunLists.setAlignY(Layout.TOP);
      // align label
      lbBuildName.setAlignY(Layout.TOP);

      // use this drop down instead of label
      this.getUserPanel().replace(lbValueBuildName, pnlBuildRunLists);
    }

    // handle host agentHost and visibility
    final String agentHost = cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.AGENT_HOST);
    if (!StringUtils.isBlank(agentHost)) {
      lbValueBuildHost.setText(agentHost);
    }

    // handle visibility and content of version descriptor
    WebuiUtils.setValueOrHide(lbVersion, lbValueVersion, cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.VERSION));

    // handle description agentHost and visibility
    if (buildRun.completed() && !StringUtils.isBlank(buildRun.getResultDescription())) {
      lbValueDescription.setText(buildRun.getResultDescription());
    } else { // hide if no description
      lbDescription.setVisible(false);
      lbValueDescription.setVisible(false);
    }

    // handle description agentHost and visibility
    if (buildRun.isReRun()) {
      final Integer reRunID = getIntegerAttributeValue(buildRun, BuildRunAttribute.ATTR_RE_RUN_BUILD_RUN_ID);
      if (reRunID != null) {
        final BuildRun reRunBuildRun = cm.getBuildRun(reRunID.intValue());
        final StringBuilder sb = new StringBuilder(30);
        final int reRunBuildRunNumber = reRunBuildRun.getBuildRunNumber();
        final String reRunBuildRunDateTime = dateTimeToString(reRunBuildRun.getStartedAt());
        sb.append("Build #").append(reRunBuildRunNumber).append(" on ").append(reRunBuildRunDateTime);
        lbValueReRun.setText(sb.toString());
        lbValueReRun.setUrl(Pages.BUILD_CHANGES);
        final Properties p = new Properties();
        p.setProperty(Pages.PARAM_BUILD_RUN_ID, reRunBuildRun.getBuildRunIDAsString());
        lbValueReRun.setParameters(p);
      } else {
        hideReRun();
      }
    } else {
      hideReRun();
    }

    // note
    WebuiUtils.setValueOrHide(lbNote, lbValueNote, cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.ATTR_NOTE));

    // handle user started the build
    User startedByUser = null;
    final Integer userID = getIntegerAttributeValue(buildRun, BuildRunAttribute.ATTR_STARTED_USER_ID);
    if (userID != null) {
      startedByUser = SecurityManager.getInstance().getUser(userID);
    }
    WebuiUtils.setValueOrHide(lbStartedBy, lbValueStartedBy, startedByUser != null ? startedByUser.getName() : null);

    // handle JUNIT tests
    final Integer testTotal = getIntegerAttributeValue(buildRun, BuildRunAttribute.ATTR_JUNIT_TESTS);
    if (testTotal != null) {
      setTestFailures(buildRun);
      setTestSuccesses(buildRun);
      WebuiUtils.setValueOrHideIfZero(lbTestSkipped, lbValueTestSkipped, getIntegerAttributeValue(buildRun, BuildRunAttribute.ATTR_JUNIT_NOTRUN));
      setTestTotal(testTotal, buildRun);
    }

    // handle PMD
    WebuiUtils.setValueOrHideIfZero(lbPMDViolations, lbValuePMDViolations, getIntegerAttributeValue(buildRun, BuildRunAttribute.ATTR_PMD_PROBLEMS));

    // handle Findbugs
    WebuiUtils.setValueOrHideIfZero(lbFindbugsViolations, lbValueFindbugsViolations, getIntegerAttributeValue(buildRun, BuildRunAttribute.ATTR_FINDBUGS_PROBLEMS));

    // handle checkstyle
    WebuiUtils.setValueOrHideIfZero(lbCheckstyleViolations, lbValueCheckstyleViolations, getIntegerAttributeValue(buildRun, BuildRunAttribute.ATTR_CHECKSTYLE_ERRORS));

    // handle checkout time
    final Integer integerCheckoutTime = getIntegerAttributeValue(buildRun, BuildRunAttribute.SYNC_TIME);
    final String stringCheckoutTime = integerCheckoutTime == null ? null : StringUtils.durationToString(integerCheckoutTime, false).toString();
    WebuiUtils.setValueOrHide(lbCheckoutTime, lbValueCheckoutTime, stringCheckoutTime);

    // set links
    final TierletContext tierletContext = getTierletContext();
    if (tierletContext != null) {
      final SecurityManager sm = SecurityManager.getInstance();
      final User user = sm.getUserFromRequest(tierletContext.getHttpServletRequest());
      final BuildRights rights = sm.getUserBuildRights(user, buildRun.getActiveBuildID());
      if (rights.isAllowedToListCommands()) {
        final BuildListService buildService = ServiceManager.getInstance().getBuildListService();
        final BuildService build = buildService.getBuild(buildRun.getActiveBuildID());
        final BuildState buildState = build.getBuildState();
        fwBuildCommandLinks.setBuildStatus(buildState);
      } else {
        lbBuildCommandsFiller.setVisible(false);
        fwBuildCommandLinks.setVisible(false);
      }
    }

    // handle visibility and content of label descriptor
    WebuiUtils.setValueOrHide(lbLabel, lbValueLabel, buildRun.getLabelNote());

    // clean checkout
    final boolean cleanCheckout = cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.ATTR_CLEAN_CHECKOUT, false);
    WebuiUtils.setValueOrHide(lbCleanCheckout, lbValueCleanCheckout, cleanCheckout ? "Yes" : null);

    // Perforce change client name
    final String perforceClientName = cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.REFERENCE_P4_CLIENT_NAME);
    WebuiUtils.setValueOrHide(lbPerforceClientName, lbPerforceClientNameValue, perforceClientName);
  }


  private void setTestSuccesses(final BuildRun buildRun) {
    final Integer successes = getIntegerAttributeValue(buildRun, BuildRunAttribute.ATTR_JUNIT_SUCCESSES);
    if (successes == null || successes == 0) {
      lbTestSuccesses.setVisible(false);
      lbValueTestSuccesses.setVisible(false);
    } else {
      final int buildRunID = buildRun.getBuildRunID();
      lbTestSuccesses.setVisible(true);
      lbValueTestSuccesses.setVisible(true);
      lbValueTestSuccesses.setValue(buildRunID, successes, 0);
    }
  }


  private void setTestFailures(final BuildRun buildRun) {
    final int failureTotal = getFailureTotal(buildRun);
    if (failureTotal == 0) {
      lbValueTestFailures.setVisible(false);
      lbTestFailures.setVisible(false);
    } else {
      final int buildRunID = buildRun.getBuildRunID();
      final int newBrokenTests = cm.getBuildRunAttributeValue(buildRunID, BuildRunAttribute.NEW_BROKEN_TESTS, 0);
      lbTestFailures.setVisible(true);
      lbValueTestFailures.setVisible(true);
      lbValueTestFailures.setValue(buildRunID, failureTotal, newBrokenTests);
    }
  }


  private int getFailureTotal(final BuildRun buildRun) {
    final Integer failures = getIntegerAttributeValue(buildRun, BuildRunAttribute.ATTR_JUNIT_FAILURES);
    final Integer errors = getIntegerAttributeValue(buildRun, BuildRunAttribute.ATTR_JUNIT_ERRORS);
    if (failures == null && errors == null) {
      return 0;
    } else {
      return (failures == null ? 0 : failures) + (errors == null ? 0 : errors);
    }
  }


  private void setTestTotal(final Integer testTotal, final BuildRun buildRun) {
    final int total = testTotal == null ? 0 : testTotal;
    if (total == 0) {
      lbValueTestTotal.setVisible(false);
      lbTestTotal.setVisible(false);
    } else {
      final int buildRunID = buildRun.getBuildRunID();
      final int newTests = cm.getBuildRunAttributeValue(buildRunID, BuildRunAttribute.NEW_TESTS, 0);
      lbTestTotal.setVisible(true);
      lbValueTestTotal.setVisible(true);
      lbValueTestTotal.setValue(buildRunID, total, newTests);
    }
  }


  private Integer getIntegerAttributeValue(final BuildRun buildRun, final String attributeName) {
    return cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), attributeName, (Integer) null);
  }


  /**
   * Hides re-run information.
   */
  private void hideReRun() {
    lbReRun.setVisible(false);
    lbValueReRun.setVisible(false);
  }


  /**
   * Helper method to convert date/time to string
   *
   * @param date Date to convert
   * @return String result of conversion
   */
  private String dateTimeToString(final Date date) {
    if (date == null) {
      return "";
    }
    return StringUtils.formatDate(date, SystemConfigurationManagerFactory.getManager().getDateTimeFormat());
  }


  private static String intervalToSecondsString(final Date start, final Date finish) {
    if (start == null || finish == null) {
      return "";
    }
    final long duration = (finish.getTime() - start.getTime()) / 1000L;
    if (duration < 0) {
      return "";
    }
    return StringUtils.durationToString(duration, false).toString();
  }


  /**
   * @param buildRunURLFactory
   */
  public void setBuilRunLinkFactory(final BuildRunURLFactory buildRunURLFactory) {
    this.buildRunURLFactory = buildRunURLFactory;
  }


  /**
   * The diff component contains a value, an up/down arrow and a diff value.
   * <p/>
   * The diff componet allow to hide diff if ncecessary.
   */
  private static final class TestFailureComponent extends Flow {

    private final Color textColor;
    private final Color positiveDiffColor;
    private static final long serialVersionUID = 914342802840816894L;


    private TestFailureComponent(final Color textColor, final Color diffColor) {
      this.textColor = textColor;
      this.positiveDiffColor = diffColor;
    }


    public void setValue(final int buildRunID, final int value, final int diff) {
      clear();
      // Add value link
      final CommonBoldLink lnkValue = new CommonBoldLink(Integer.toString(value), Pages.PAGE_BUILD_RUN_TESTS,
              Pages.PARAM_BUILD_RUN_ID, buildRunID, Pages.PARAM_FILTER, Pages.FILTER_ALL_FAILED_TESTS);
      lnkValue.setForeground(textColor);
      add(lnkValue);
      // Add diff
      if (diff > 0) {
        add(new Label("  "));
        add(WebuiUtils.makeRedBulletTriangleRedUp16x16());
        add(new Label("  "));
        final CommonBoldLink lnkDiff = new CommonBoldLink(StringUtils.diffToString(diff), Pages.PAGE_BUILD_RUN_TESTS,
                Pages.PARAM_BUILD_RUN_ID, buildRunID, Pages.PARAM_FILTER, Pages.FILTER_NEW_FAILED_TESTS);
        lnkDiff.setForeground(positiveDiffColor);
        add(lnkDiff);
      }
    }


    public String toString() {
      return "TestFailureComponent{" +
              "positiveDiffColor=" + positiveDiffColor +
              ", textColor=" + textColor +
              "} " + super.toString();
    }
  }

  /**
   * The diff component contains a value, an up/down arrow and a diff value.
   * <p/>
   * The diff componet allow to hide diff if ncecessary.
   */
  private static final class TestUpGoodComponent extends Flow {

    private final Color textColor;
    private final Color positiveDiffColor;
    private final String valueFilter;
    private final String diffFilter;
    private static final long serialVersionUID = 659505627971414641L;


    private TestUpGoodComponent(final Color textColor, final Color diffColor,
                                final String valueFilter, final String diffFilter) {
      this.textColor = textColor;
      this.positiveDiffColor = diffColor;
      this.valueFilter = valueFilter;
      this.diffFilter = diffFilter;
    }


    public void setValue(final int buildRunID, final int value, final int diff) {
      clear();
      // Add value link
      final CommonBoldLink lnkValue = new CommonBoldLink(Integer.toString(value), Pages.PAGE_BUILD_RUN_TESTS,
              Pages.PARAM_BUILD_RUN_ID, buildRunID, Pages.PARAM_FILTER, valueFilter);
      lnkValue.setForeground(textColor);
      add(lnkValue);
      // Add diff
      if (diff > 0) {
        add(new Label("  "));
        add(WebuiUtils.makeRedBulletTriangleGreenUp16x16());
        add(new Label("  "));
        final CommonBoldLink lnkDiff = new CommonBoldLink(StringUtils.diffToString(diff), Pages.PAGE_BUILD_RUN_TESTS,
                Pages.PARAM_BUILD_RUN_ID, buildRunID, Pages.PARAM_FILTER, diffFilter);
        lnkDiff.setForeground(positiveDiffColor);
        add(lnkDiff);
      }
    }


    public String toString() {
      return "TestUpGoodComponent{" +
              "diffFilter='" + diffFilter + '\'' +
              ", positiveDiffColor=" + positiveDiffColor +
              ", textColor=" + textColor +
              ", valueFilter='" + valueFilter + '\'' +
              "} " + super.toString();
    }
  }


  public String toString() {
    return "BuildRunSummaryPanel{" +
            "buildRunURLFactory=" + buildRunURLFactory +
            ", cm=" + cm +
            ", fwBuildCommandLinks=" + fwBuildCommandLinks +
            ", lbBuildCommandsFiller=" + lbBuildCommandsFiller +
            ", lbBuildHost=" + lbBuildHost +
            ", lbBuildName=" + lbBuildName +
            ", lbCheckoutTime=" + lbCheckoutTime +
            ", lbCheckstyleViolations=" + lbCheckstyleViolations +
            ", lbCleanCheckout=" + lbCleanCheckout +
            ", lbDescription=" + lbDescription +
            ", lbFindbugsViolations=" + lbFindbugsViolations +
            ", lbLabel=" + lbLabel +
            ", lbNote=" + lbNote +
            ", lbPMDViolations=" + lbPMDViolations +
            ", lbReRun=" + lbReRun +
            ", lbStartedBy=" + lbStartedBy +
            ", lbSyncNote=" + lbSyncNote +
            ", lbTestFailures=" + lbTestFailures +
            ", lbTestSkipped=" + lbTestSkipped +
            ", lbTestSuccesses=" + lbTestSuccesses +
            ", lbTestTotal=" + lbTestTotal +
            ", lbValueBuildHost=" + lbValueBuildHost +
            ", lbValueBuildName=" + lbValueBuildName +
            ", lbValueBuildNumber=" + lbValueBuildNumber +
            ", lbValueCheckoutTime=" + lbValueCheckoutTime +
            ", lbValueCheckstyleViolations=" + lbValueCheckstyleViolations +
            ", lbValueCleanCheckout=" + lbValueCleanCheckout +
            ", lbValueDescription=" + lbValueDescription +
            ", lbValueFindbugsViolations=" + lbValueFindbugsViolations +
            ", lbValueFinished=" + lbValueFinished +
            ", lbValueLabel=" + lbValueLabel +
            ", lbValueNote=" + lbValueNote +
            ", lbValuePMDViolations=" + lbValuePMDViolations +
            ", lbValueReRun=" + lbValueReRun +
            ", lbValueResult=" + lbValueResult +
            ", lbValueStarted=" + lbValueStarted +
            ", lbValueStartedBy=" + lbValueStartedBy +
            ", lbValueSyncNote=" + lbValueSyncNote +
            ", lbValueTestFailures=" + lbValueTestFailures +
            ", lbValueTestSkipped=" + lbValueTestSkipped +
            ", lbValueTestSuccesses=" + lbValueTestSuccesses +
            ", lbValueTestTotal=" + lbValueTestTotal +
            ", lbValueTime=" + lbValueTime +
            ", lbValueVersion=" + lbValueVersion +
            ", lbVersion=" + lbVersion +
            "} " + super.toString();
  }
}
