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

import net.sf.hibernate.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunTest;
import org.parabuild.ci.object.TestSuiteName;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.BasePage;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonBoldLink;
import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Color;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Font;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

import java.util.ArrayList;
import java.util.List;

/**
 * BuildRunTestsPage
 * <p/>
 *
 * @author Slava Imeshev
 * @since Jul 8, 2008 10:28:52 PM
 */
public final class BuildRunTestsPage extends BasePage implements StatelessTierlet {

  private static final Log LOG = LogFactory.getLog(BuildRunSummaryPanel.class); // NOPMD

  private static final Font SMALL_FOUNT = new Font(Font.Monospace, Font.Plain, Pages.COMMMON_FONT_SIZE - 1);
  private static final long serialVersionUID = -7535921681585450884L;


  public BuildRunTestsPage() {
    super(FLAG_FLOATING_WIDTH | FLAG_SHOW_QUICK_SEARCH | FLAG_SHOW_PAGE_HEADER_LABEL | FLAG_SHOW_QUICK_SEARCH);
  }

  /**
   * Strategy method to be implemented by classes inheriting
   * BasePage.
   *
   * @param parameters
   * @return result of page execution
   */
  protected Result executePage(final Parameters parameters) {
    final BuildRun buildRun = ParameterUtils.getBuildRunFromParameters(parameters);
    if (buildRun == null) {
      return WebuiUtils.showBuildNotFound(this);
    } else {
      // Authorise
      if (!getUserRights(buildRun.getActiveBuildID()).isAllowedToViewBuild()) {
        baseContentPanel().getUserPanel().clear();
        return WebuiUtils.showNotAuthorized(this);
      }

      // Get filter
      final String filter = getFilterFromParameters(parameters);

      // Set title
      final String pageTitle = createTitle(filter, buildRun);
      setTitle(pageTitle);
      setPageHeader(pageTitle);
      setPageHeaderForeground(createHeaderForeground(filter));

      // display table with tests
      // Show suites
      final Panel userPanel = baseContentPanel().getUserPanel();
      final int buildRunID = buildRun.getBuildRunID();
      final BuildRunTestDAO buildRunTestDAO = new BuildRunTestDAO(buildRunID);
      final List testSuites = buildRunTestDAO.getTestSuites(filter);
      for (int i = 0; i < testSuites.size(); i++) {
        final TestSuiteName testSuite = (TestSuiteName) testSuites.get(i);
        final String testSuiteName = testSuite.getName();
        final int testSuiteID = testSuite.getID();
        final List testCases = buildRunTestDAO.getTestCases(testSuiteID, filter);
        // Show components
        final BoldCommonLabel lbTestSuiteName = new BoldCommonLabel(testSuiteName);
        lbTestSuiteName.setHeight(35);
        lbTestSuiteName.setAlignY(Layout.CENTER);
        userPanel.add(lbTestSuiteName);
        final TestSuiteTable testSuiteTable = new TestSuiteTable(buildRunID, testCases, null);
        testSuiteTable.populate();
        userPanel.add(testSuiteTable);
      }
    }

    return Result.Done();
  }

  private static Color createHeaderForeground(final String filterString) {
    if (filterString.equals(Pages.FILTER_ALL_FAILED_TESTS)) {
      return Color.DarkRed;
    } else if (filterString.equals(Pages.FILTER_NEW_FAILED_TESTS)) {
      return Color.DarkRed;
    } else if (filterString.equals(Pages.FILTER_ALL_TESTS)) {
      return Pages.COLOR_PAGE_HEADER_FOREGROUND;
    } else {
      return Pages.COLOR_PAGE_HEADER_FOREGROUND;
    }
  }

  private static String getFilterFromParameters(final Parameters parameters) {
    String filter = parameters.getParameterValue(Pages.PARAM_FILTER);
    if (StringUtils.isBlank(filter)) {
      filter = Pages.FILTER_ALL_TESTS;
    }
    return filter;
  }

  private static String createTitle(final String filterString, final BuildRun buildRun) {
    final String prefix;
    if (filterString.equals(Pages.FILTER_ALL_FAILED_TESTS)) {
      prefix = "Failed";
    } else if (filterString.equals(Pages.FILTER_NEW_FAILED_TESTS)) {
      prefix = "New Failed";
    } else if (filterString.equals(Pages.FILTER_SUCCESSFUL_TESTS)) {
      prefix = "Successful";
    } else if (filterString.equals(Pages.FILTER_NEW_SUCCESSFUL_TESTS)) {
      prefix = "New Successful";
    } else if (filterString.equals(Pages.FILTER_ALL_TESTS)) {
      prefix = "";
    } else {
      prefix = "";
    }
    return prefix + " Tests for Build " + buildRun.getBuildNameAndNumberAsString();
  }

  /**
   * BuildRunTestsPage DAO.
   */
  final static class BuildRunTestDAO {
    private final int buildRunID;

    public BuildRunTestDAO(final int buildRunID) {
      this.buildRunID = buildRunID;
    }

    /**
     * Returns test suites in thie build run.
     *
     * @param filterString
     * @return
     */
    public List getTestSuites(final String filterString) {
      if (filterString.equals(Pages.FILTER_ALL_FAILED_TESTS)) {
        return getFailedTestSuites();
      } else if (filterString.equals(Pages.FILTER_NEW_FAILED_TESTS)) {
        return getNewFailedTestSuites();
      } else if (filterString.equals(Pages.FILTER_NEW_TESTS)) {
        return getNewTestSuites();
      } else if (filterString.equals(Pages.FILTER_SUCCESSFUL_TESTS)) {
        return getSuccessfulTestSuites();
      } else if (filterString.equals(Pages.FILTER_NEW_SUCCESSFUL_TESTS)) {
        return getNewSuccessfulTestSuites();
      } else {
        return getAllTestsuites();
      }
    }

    private List getAllTestsuites() {
      return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          final Query query = session.createQuery("select distinct tsn " +
                  " from TestSuiteName tsn, TestCaseName tcn, BuildRunTest brt " +
                  " where brt.buildRunID = ? " +
                  "   and tsn.ID = tcn.testSuiteNameID " +
                  "   and tcn.ID = brt.testCaseNameID " +
                  " order by tsn.name");
          query.setInteger(0, buildRunID);
          query.setCacheable(true);
          return query.list();
        }
      });
    }

    private List getNewTestSuites() {
      return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          final Query query = session.createQuery("select distinct tsn " +
                  " from TestSuiteName tsn, TestCaseName tcn, BuildRunTest brt " +
                  " where brt.buildRunID = ? " +
                  "   and tsn.ID = tcn.testSuiteNameID " +
                  "   and tcn.ID = brt.testCaseNameID " +
                  "   and brt.newTest  = yes " +
                  " order by tsn.name");
          query.setInteger(0, buildRunID);
          query.setCacheable(true);
          return query.list();
        }
      });
    }

    private List getNewFailedTestSuites() {
      return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          final Query query = session.createQuery("select distinct tsn " +
                  " from TestSuiteName tsn, TestCaseName tcn, BuildRunTest brt " +
                  " where brt.buildRunID = ? " +
                  "   and tsn.ID = tcn.testSuiteNameID " +
                  "   and tcn.ID = brt.testCaseNameID " +
                  "   and brt.broken = yes " +
                  "   and brt.newFailure  = yes " +
                  " order by tsn.name");
          query.setInteger(0, buildRunID);
          query.setCacheable(true);
          return query.list();
        }
      });
    }

    private List getNewSuccessfulTestSuites() {
      return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          final Query query = session.createQuery("select distinct tsn " +
                  " from TestSuiteName tsn, TestCaseName tcn, BuildRunTest brt " +
                  " where brt.buildRunID = ? " +
                  "   and tsn.ID = tcn.testSuiteNameID " +
                  "   and tcn.ID = brt.testCaseNameID " +
                  "   and brt.broken = no " +
                  "   and brt.newTest =  yes" +
                  " order by tsn.name");
          query.setInteger(0, buildRunID);
          query.setCacheable(true);
          return query.list();
        }
      });
    }

    private List getFailedTestSuites() {
      return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          final Query query = session.createQuery("select distinct tsn " +
                  " from TestSuiteName tsn, TestCaseName tcn, BuildRunTest brt " +
                  " where brt.buildRunID = ? " +
                  "   and tsn.ID = tcn.testSuiteNameID " +
                  "   and tcn.ID = brt.testCaseNameID " +
                  "   and brt.broken = yes " +
                  " order by tsn.name");
          query.setInteger(0, buildRunID);
          query.setCacheable(true);
          return query.list();
        }
      });
    }

    private List getSuccessfulTestSuites() {
      return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          final Query query = session.createQuery("select distinct tsn " +
                  " from TestSuiteName tsn, TestCaseName tcn, BuildRunTest brt " +
                  " where brt.buildRunID = ? " +
                  "   and tsn.ID = tcn.testSuiteNameID " +
                  "   and tcn.ID = brt.testCaseNameID " +
                  "   and brt.broken = no " +
                  " order by tsn.name");
          query.setInteger(0, buildRunID);
          query.setCacheable(true);
          return query.list();
        }
      });
    }

    /**
     * Returns a list of {@link TestCaseVO} objects.
     *
     * @param testSuiteID
     * @param filterString
     * @return
     */
    public List getTestCases(final int testSuiteID, final String filterString) {
      final List nameAndBuildRunTests;
      if (filterString.equals(Pages.FILTER_ALL_FAILED_TESTS)) {
        nameAndBuildRunTests = (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
          public Object runInTransaction() throws Exception {
            final Query query = session.createQuery("select tcn.name, brt " +
                    " from TestCaseName tcn, BuildRunTest brt " +
                    " where brt.buildRunID = ? " +
                    "   and tcn.testSuiteNameID = ? " +
                    "   and tcn.ID = brt.testCaseNameID " +
                    "   and brt.broken = yes " +
                    " order by tcn.name");
            query.setInteger(0, buildRunID);
            query.setInteger(1, testSuiteID);
            query.setCacheable(true);
            return query.list();
          }
        });
      } else if (filterString.equals(Pages.FILTER_SUCCESSFUL_TESTS)) {
        nameAndBuildRunTests = (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
          public Object runInTransaction() throws Exception {
            final Query query = session.createQuery("select tcn.name, brt " +
                    " from TestCaseName tcn, BuildRunTest brt " +
                    " where brt.buildRunID = ? " +
                    "   and tcn.testSuiteNameID = ? " +
                    "   and tcn.ID = brt.testCaseNameID " +
                    "   and brt.broken = no " +
                    " order by tcn.name");
            query.setInteger(0, buildRunID);
            query.setInteger(1, testSuiteID);
            query.setCacheable(true);
            return query.list();
          }
        });
      } else if (filterString.equals(Pages.FILTER_NEW_FAILED_TESTS)) {
        nameAndBuildRunTests = (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
          public Object runInTransaction() throws Exception {
            final Query query = session.createQuery("select tcn.name, brt " +
                    " from TestCaseName tcn, BuildRunTest brt " +
                    " where brt.buildRunID = ? " +
                    "   and tcn.testSuiteNameID = ? " +
                    "   and tcn.ID = brt.testCaseNameID " +
                    "   and brt.broken = yes " +
                    "   and brt.newFailure  = yes " +
                    " order by tcn.name");
            query.setInteger(0, buildRunID);
            query.setInteger(1, testSuiteID);
            query.setCacheable(true);
            return query.list();
          }
        });
      } else if (filterString.equals(Pages.FILTER_NEW_SUCCESSFUL_TESTS)) {
        nameAndBuildRunTests = (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
          public Object runInTransaction() throws Exception {
            final Query query = session.createQuery("select tcn.name, brt " +
                    " from TestCaseName tcn, BuildRunTest brt " +
                    " where brt.buildRunID = ? " +
                    "   and tcn.testSuiteNameID = ? " +
                    "   and tcn.ID = brt.testCaseNameID " +
                    "   and brt.broken = no " +
                    "   and brt.newTest  = yes " +
                    " order by tcn.name");
            query.setInteger(0, buildRunID);
            query.setInteger(1, testSuiteID);
            query.setCacheable(true);
            return query.list();
          }
        });
      } else if (filterString.equals(Pages.FILTER_NEW_TESTS)) {
        nameAndBuildRunTests = (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
          public Object runInTransaction() throws Exception {
            final Query query = session.createQuery("select tcn.name, brt " +
                    " from TestCaseName tcn, BuildRunTest brt " +
                    " where brt.buildRunID = ? " +
                    "   and tcn.testSuiteNameID = ? " +
                    "   and tcn.ID = brt.testCaseNameID " +
                    "   and brt.newTest = yes " +
                    " order by tcn.name");
            query.setInteger(0, buildRunID);
            query.setInteger(1, testSuiteID);
            query.setCacheable(true);
            return query.list();
          }
        });
      } else {
        // All
        nameAndBuildRunTests = (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
          public Object runInTransaction() throws Exception {
            final Query query = session.createQuery("select tcn.name, brt " +
                    " from TestCaseName tcn, BuildRunTest brt " +
                    " where brt.buildRunID = ? " +
                    "   and tcn.testSuiteNameID = ? " +
                    "   and tcn.ID = brt.testCaseNameID " +
                    " order by tcn.name");
            query.setInteger(0, buildRunID);
            query.setInteger(1, testSuiteID);
            query.setCacheable(true);
            return query.list();
          }
        });
      }
      // Iterate result and create a list of TestCaseVO objects
      final List result = new ArrayList(nameAndBuildRunTests.size());
      for (int i = 0; i < nameAndBuildRunTests.size(); i++) {
        final Object[] objects = (Object[]) nameAndBuildRunTests.get(i);
        final String testCaseName = (String) objects[0];
        final BuildRunTest brt = (BuildRunTest) objects[1];
        result.add(new TestCaseVO(testCaseName, brt.getResultCode(), brt.getDurationMillis(),
                brt.isNewFailure(), brt.isBroken(), brt.getBrokenBuildRunCount(),
                brt.getBrokenSinceBuildRunID(), brt.getMessage(), brt.isFix(), brt.isNewTest()));
      }
      return result;
    }
  }

  final static class TestCaseVO {

    private static final String [] RESULT_AS_STRING = {"Unknown", "Success", "Failure", "Error", "Skipped"};

    private final String name;
    private final short resultCode;
    private final long durationMillis;
    private final boolean newFailure;
    private final boolean broken;
    private final boolean newTest;
    private final boolean fix;
    private final int brokenBuildRunCount;
    private int brokenSinceBuildRunID = BuildRun.UNSAVED_ID;
    private final String message;

    public TestCaseVO(final String name, final short resultCode, final long durationMillis,
                      final boolean newFailure, final boolean broken, final int brokenBuildRunCount,
                      final int brokenSinceBuildRunID, final String message, final boolean fix, final boolean newTest) {
      this.name = name;
      this.resultCode = resultCode;
      this.durationMillis = durationMillis;
      this.newFailure = newFailure;
      this.broken = broken;
      this.brokenBuildRunCount = brokenBuildRunCount;
      this.brokenSinceBuildRunID = brokenSinceBuildRunID;
      this.message = message;
      this.fix = fix;
      this.newTest = newTest;
    }

    public String getName() {
      return name;
    }

    public short getResultCode() {
      return resultCode;
    }

    public long getDurationMillis() {
      return durationMillis;
    }

    public boolean isNewFailure() {
      return newFailure;
    }

    public boolean isBroken() {
      return broken;
    }

    public int getBrokenBuildRunCount() {
      return brokenBuildRunCount;
    }

    public int getBrokenSinceBuildRunID() {
      return brokenSinceBuildRunID;
    }

    public String getMessage() {
      return message;
    }

    public String getResultAsString() {
      return RESULT_AS_STRING[resultCode];
    }

    public String getDurationSecondsAsString() {
      return Double.toString(durationMillis / 1000.0);
    }

    public boolean isNewTest() {
      return newTest;
    }

    public boolean isFix() {
      return fix;
    }
  }

  private static final class TestSuiteTable extends AbstractFlatTable {

    private static final Color COLOR_FAILURE = new Color(0xEEE0B7);
    private static final Color COLOR_ERROR = new Color(0xF8C4C2);
    private static final Color GRID_COLOR = new Color(0xE3E3E3);

    private static final int COLUMN_COUNT = 9;
    private static final long serialVersionUID = 7844687858177899246L;
    private final int buildRunID;
    private final List testCases;

    /**
     * Creates TestSuiteTable.
     *
     * @param testCases List of {@link BuildRunTestsPage.TestCaseVO} objects.
     * @param filter
     */
    public TestSuiteTable(final int buildRunID, final List testCases, final String filter) {
      super(COLUMN_COUNT, false);
      this.setWidth("100%");
      this.getUserPanel().setWidth("100%");
      this.setAutomaticRowBackground(false);
      this.setGridColor(GRID_COLOR);
      this.setGridWidth(2);
      this.buildRunID = buildRunID;
      this.testCases = testCases;
    }


    /**
     */
    protected Component[] makeHeader() {
      return new Component[]{
              makeCenteredHeader("Test Case", "25%"),
              makeCenteredHeader("Status", "5%"),
              makeCenteredHeader("Message", "50%"),
              makeNormalHeader("Time, seconds", "5%"),
              makeCenteredHeader("Fix", "4%"),
              makeCenteredHeader("New Test", "4%"),
              makeCenteredHeader("New in This Build", "5%"),
              makeCenteredHeader("Broken for, builds", "5%"),
              makeCenteredHeader("Broken since, build", "5%"),
      };
    }


    /**
     */
    private static Component makeNormalHeader(final String caption, final String width) {
      return new TableHeaderLabel(caption, width);
    }


    /**
     */
    private static TableHeaderLabel makeCenteredHeader(final String caption, final String width) {
      final TableHeaderLabel tableHeaderLabel = new TableHeaderLabel(caption, width);
      tableHeaderLabel.setAlignX(Layout.CENTER);
      return tableHeaderLabel;
    }

    /**
     * Makes row, should be implemented by successor class
     */
    protected Component[] makeRow(final int rowIndex) {
      return new Component[]{
              new SmallLabel(),
              new SmallLabel(),
              new SmallMessageLabel(),
              new SmallLabel(),
              new FixLabel(),
              new NewTestLabel(),
              new NewInThisBuildLabel(buildRunID),
              new BrokenBuildCountLabel(),
              new BrokenSinceBuildLabel(buildRunID),
      };
    }

    /**
     * This implementation of this abstract method is called when
     * the table wants to fetch a row with a given rowIndex.
     * Implementing method should fill the data corresponding the
     * given rowIndex.
     *
     * @return this method should return either TBL_ROW_FETCHED or
     *         TBL_NO_MORE_ROWS if the requested row is out of
     *         range.
     * @see AbstractFlatTable#TBL_ROW_FETCHED
     * @see AbstractFlatTable#TBL_NO_MORE_ROWS
     */
    protected int fetchRow(final int rowIndex, final int rowFlags) {
      if (rowIndex >= testCases.size()) return TBL_NO_MORE_ROWS;
      final Component[] row = getRow(rowIndex);
      final TestCaseVO testCaseVO = (TestCaseVO) testCases.get(rowIndex);
      ((Label) row[0]).setText(testCaseVO.getName());
      ((Label) row[1]).setText(testCaseVO.getResultAsString());
      ((Label) row[2]).setText(testCaseVO.getMessage());
      ((Label) row[3]).setText(testCaseVO.getDurationSecondsAsString());
      ((FixLabel) row[4]).setValue(testCaseVO.isFix());
      ((NewTestLabel) row[5]).setValue(testCaseVO.isNewTest(), testCaseVO.isBroken());
      ((NewInThisBuildLabel) row[6]).setValue(testCaseVO.isNewFailure());
      ((BrokenBuildCountLabel) row[7]).setValue(testCaseVO.getBrokenBuildRunCount());
      ((BrokenSinceBuildLabel) row[8]).setValue(testCaseVO.getBrokenSinceBuildRunID());
      setRowBackground(row, testCaseVO.getResultCode(), rowIndex % 2 == 0);
      return TBL_ROW_FETCHED;
    }


    /*
     * Sets row background according to broken status.
     *
     * @param broken
     * @param even
     * @param row
     */
    private void setRowBackground(final Component[] row, final short resultCode, final boolean even) {
      if (resultCode == BuildRunTest.RESULT_FAILURE) {
        setRowBackground(row, COLOR_FAILURE);
      } else if (resultCode == BuildRunTest.RESULT_ERROR) {
        setRowBackground(row, COLOR_ERROR);
      }
    }

    private static void setRowBackground(final Component[] row, final Color colorFailure) {
      for (int i = 0; i < row.length; i++) {
        row[i].setBackground(colorFailure);
      }
    }
  }

  private static final class NewInThisBuildLabel extends Flow {

    private static final long serialVersionUID = 2637614206955007248L;
    private final int buildRunID;

    public NewInThisBuildLabel(final int buildRunID) {
      this.buildRunID = buildRunID;
      this.setAlignX(Layout.CENTER);
    }

    public void setValue(final boolean newInThisBuild) {
      final String caption;
      if (newInThisBuild) {
        final CommonBoldLink lnk = new CommonBoldLink("New", Pages.BUILD_CHANGES, Pages.PARAM_BUILD_RUN_ID, buildRunID, Pages.PARAM_SHOW_FILES, Boolean.TRUE.toString());
        lnk.setForeground(Color.DarkRed);
        add(lnk);
      }
    }
  }

  private static final class BrokenBuildCountLabel extends BoldCommonLabel {

    private static final long serialVersionUID = 1206585148817994519L;


    public BrokenBuildCountLabel() {
      this.setAlignX(Layout.CENTER);
    }

    public void setValue(final int brokenBuildRunCount) {
      if (brokenBuildRunCount > 0) {
        setText(Integer.toString(brokenBuildRunCount));
      }
    }
  }

  private static final class FixLabel extends BoldCommonLabel {

    private static final long serialVersionUID = 5390302039450586073L;


    public FixLabel() {
      this.setAlignX(Layout.CENTER);
      this.setForeground(Color.DarkGreen);
    }

    public void setValue(final boolean value) {
      if (value) {
        setText("Fix");
      }
    }
  }

  private static final class NewTestLabel extends CommonLabel {

    private static final long serialVersionUID = -2983470331055804680L;


    public NewTestLabel() {
      this.setAlignX(Layout.CENTER);
    }

    public void setValue(final boolean isNew, final boolean broken) {
      if (isNew) {
        setText("New");
        if (broken) {
          setForeground(Color.DarkRed);
        } else {
          this.setForeground(Color.DarkGreen);
        }
      }
    }
  }

  private static final class BrokenSinceBuildLabel extends Flow {

    private static final long serialVersionUID = -566385374168421616L;
    private final int buildRunID;

    public BrokenSinceBuildLabel(final int buildRunID) {
      this.buildRunID = buildRunID;
      this.setAlignX(Layout.CENTER);
    }

    public void setValue(final int brokenSinceBuildRunID) {
      final String caption;
      if (buildRunID == brokenSinceBuildRunID) {
        caption = "Current";
      } else {
        caption = "First";
      }
      add(new CommonCommandLink(caption, Pages.BUILD_CHANGES, Pages.PARAM_BUILD_RUN_ID, brokenSinceBuildRunID, Pages.PARAM_SHOW_FILES, Boolean.TRUE.toString()));
    }
  }

  private static final class SmallMessageLabel extends CommonLabel {

    private static final long serialVersionUID = -2009867503452318045L;


    public SmallMessageLabel() {
      setPreserveFormatting(Integer.MAX_VALUE);
      setFont(SMALL_FOUNT);
      setAlignY(Layout.TOP);
    }
  }


  private static final class SmallLabel extends CommonLabel {

    private static final long serialVersionUID = 2651628309473025774L;


    public SmallLabel() {
      setFont(SMALL_FOUNT);
      setAlignY(Layout.TOP);
    }
  }
}
