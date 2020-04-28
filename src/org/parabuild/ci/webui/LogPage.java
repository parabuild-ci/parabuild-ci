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
import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ParameterUtils;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Flow;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.Tierlet;
import viewtier.ui.TierletContext;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;

/**
 * This class is a log page.
 */
public final class LogPage extends AbstractBuildRunResultPage implements ConversationalTierlet {

  private static final String STR_LOG_NOT_FOUND = "Requested log not found.";
  private static final String TITLE = "Build log";

  private static final long serialVersionUID = -3767571546131357778L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(LogPage.class); // NOPMD

  private final ConfigurationManager configManager = ConfigurationManager.getInstance();


  public LogPage() {
    setTitle(makeTitle(TITLE));
  }


  /**
   * Should return a build run from the given Parameter list.
   * <p/>
   * If build run ID is not available we use traversing back from
   * log ID: log ID -> step run ID -> build run ID -> build run.
   */
  protected BuildRun getBuildRunFromParameters(final Parameters params) {
    // try "normal" way
    final BuildRun buildRunFromParameters = super.getBuildRunFromParameters(params);
    if (buildRunFromParameters != null) {
      return buildRunFromParameters;
    }

    // traverse from log ID to build run
    return configManager.getBuildRunFromStepLog(ParameterUtils.getLogIDFromParameters(params));
  }


  /**
   * Creates a page title. Implementing classes should provide
   * page titles that correspond the given page result.
   *
   * @param buildRun to create a title for.
   * @return created page title.
   */
  protected String buildRunResultPageTitle(final BuildRun buildRun) {
    return TITLE + " >> " + buildRun.getBuildName() + " >> " + buildRun.getBuildRunNumberAsString();
  }


  protected String description(final Parameters params) {
    final StepLog stepLog = getLogFromParameters(params);
    if (stepLog == null) {
      return "Log";
    } else {
      return stepLog.getDescription();
    }
  }


  /**
   * Implementing classes should provide main handing in this
   * method. It is called after a common build result panel was
   * created and added to the layout. The content panel is
   * provided by {@link #baseContentPanel().getUserPanel()}
   * method.
   *
   * @param params   that #executePage method was called with.
   * @param buildRun BuildRun to process.
   */
  protected Result executeBuildRunResultPage(final Parameters params, final BuildRun buildRun) {

    // get log ID
    final StepLog stepLog = getLogFromParameters(params);
    if (stepLog == null) {
      baseContentPanel().showErrorMessage(STR_LOG_NOT_FOUND);
      return Tierlet.Result.Done();
    }

    // HTML logs are served by dedicated servlet - HTMLLogServlet -
    // redirect
    if (stepLog.getPathType() == StepLog.PATH_TYPE_HTML_FILE
            || stepLog.getPathType() == StepLog.PATH_TYPE_HTML_DIR) {
      return Result.Done(configManager.makeHTMLLogURLPathInfo(stepLog));
    }

    // get parent content panel
    final Panel contentPanel = baseContentPanel().getUserPanel();

    // get file path
    final ArchiveManager archiveManager = ArchiveManagerFactory.getArchiveManager(buildRun.getActiveBuildID());
    switch (stepLog.getPathType()) {
      case StepLog.PATH_TYPE_TEXT_FILE:
        addTextFileLog(contentPanel, archiveManager, stepLog);
        break;
      case StepLog.PATH_TYPE_TEXT_DIR:
        addTextDirLog(contentPanel, archiveManager, buildRun.getBuildRunID(), stepLog, params, stepLog.getID());
        break;
      case StepLog.PATH_TYPE_JUNIT_XML:
        addJUnitXMLLog(contentPanel, archiveManager, stepLog);
        break;
      case StepLog.PATH_TYPE_PHPUNIT_XML:
        addPHPUnitXMLLog(contentPanel, archiveManager, stepLog);
        break;
      case StepLog.PATH_TYPE_PMD_XML:
        addPMDXMLLog(contentPanel, archiveManager, stepLog);
        break;
      case StepLog.PATH_TYPE_CPPUNIT_XML:
        addCppUnitXMLLog(contentPanel, archiveManager, stepLog);
        break;
      case StepLog.PATH_TYPE_NUNIT_XML:
        addNUnitXMLLog(contentPanel, archiveManager, stepLog);
        break;
      case StepLog.PATH_TYPE_BOOST_XML:
        addBoostTestXMLLog(contentPanel, archiveManager, stepLog);
        break;
      case StepLog.PATH_TYPE_CHECKSTYLE_XML:
        addCheckstyleXMLLog(contentPanel, archiveManager, stepLog);
        break;
      case StepLog.PATH_TYPE_UNITTESTPP_XML:
        addUnittestPPXMLLog(contentPanel, archiveManager, stepLog);
        break;
      case StepLog.PATH_TYPE_GOOGLETEST_XML:
        final GoogleTestLogComponent logComponent = new GoogleTestLogComponent(archiveManager, stepLog);
        logComponent.setWidth("100%");
        logComponent.setAlignX(Layout.LEFT);
        logComponent.setAlignY(Layout.TOP);
        contentPanel.add(logComponent);
        break;
      case StepLog.PATH_TYPE_GENERIC_TEST:
        final GenericTestLogPanel pnlGenericTestLog = new GenericTestLogPanel(archiveManager, stepLog);
        pnlGenericTestLog.setWidth("100%");
        pnlGenericTestLog.setAlignX(Layout.LEFT);
        pnlGenericTestLog.setAlignY(Layout.TOP);
        contentPanel.add(pnlGenericTestLog);
        break;
      case StepLog.PATH_TYPE_SQUISH_XML:
        final SquishLogComponent squishLogComponent = new SquishLogComponent(archiveManager, stepLog);
        squishLogComponent.setWidth("100%");
        squishLogComponent.setAlignX(Layout.LEFT);
        squishLogComponent.setAlignY(Layout.TOP);
        contentPanel.add(squishLogComponent);
        break;
      default:
        contentPanel.add(new CommonLabel("Unknown log type"));
        break;
    }

    return makeDoneResult(buildRun);
  }


  /**
   * @param buildRun   the page was executed with.
   * @param parameters that the page was executed with.
   * @return Flow, typically {@link PreviousNextLinks} that
   *         constant Prev/Next nav links that will be inserted into the
   *         right side header divider.
   */
  protected Flow makePreviousNextNavigationLinks(final BuildRun buildRun, final Parameters parameters) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final CommonLink lnkPrevious = makeLogLink(cm.getPreviousBuildRun(buildRun), parameters, "Previous");
    final CommonLink lnkNext = makeLogLink(cm.getNextBuildRun(buildRun), parameters, "Next");
    final BoldCommonLabel lbCurrent = new BoldCommonLabel(buildRun.getBuildRunNumberAsString());
    return new PreviousNextLinks(lnkPrevious, lbCurrent, lnkNext);
  }


  /**
   * @return a page-specific build run link factory.
   * @see BuildRunURLFactory
   */
  protected BuildRunURLFactory makeBuildRunURLFactory() {
    return new BuildRunLogURLFactory();
  }


  /**
   * Creates a log link that is same as the one provided in the params i.e. current.
   *
   * @param buildRun
   * @param params
   * @param caption
   * @return
   * @see AbstractBuildRunResultPage#makePreviousNextNavigationLinks(BuildRun,Parameters)
   */
  private CommonLink makeLogLink(final BuildRun buildRun, final Parameters params, final String caption) {
    if (buildRun == null) {
      return null;
    }
    final LinkURL linkURL = new BuildRunLogURLFactory().makeLinkURL(buildRun, params);
    return new CommonLink(caption, linkURL.getUrl(), linkURL.getParameters());
  }


  /**
   * Shows log of type "directory with text files".
   */
  private void addTextDirLog(final Panel contentPanel, final ArchiveManager archiveManager, final int buildRunID, final StepLog stepLog, final Parameters params, final int logID) {
    try {
      // try to get file to show
      String fileNameInLogToShow;
      final ConsistentFileList fileList = new ConsistentFileList(archiveManager, stepLog);
      final int fileID = ParameterUtils.getFileIDFromParameters(params);
      if (fileID == -1) {
        // not found, get file from file name parameter, if any
        fileNameInLogToShow = params.getParameterValue(Pages.PARAM_FILE_NAME);
        if (!StringUtils.isBlank(fileNameInLogToShow)) {
          if (fileList.getFileID(fileNameInLogToShow) == -1) {
            fileNameInLogToShow = null; // not in list
          }
        }
      } else {
        fileNameInLogToShow = fileList.getFileNameByID(fileID);
      }
      // show
      if (fileNameInLogToShow != null) {
        // alter log description shown on the header and add log file content
        headerPanel.setDescription(stepLog.getDescription() + " >> " + fileNameInLogToShow);
        addTextFileLog(contentPanel, archiveManager, stepLog, fileNameInLogToShow);
      } else {
        // file ID or file name is invalid or missing, show list of files
        showListOfFileNames(contentPanel, buildRunID, logID, fileList);
      }
    } catch (final Exception e) {
      // show error overview
      baseContentPanel().getUserPanel().clear();
      baseContentPanel().showErrorMessage("There was an error while retrieving log. Please contact build administrator for resolution.");
      // report to error mgr.
      final Error error = new Error("Error displaying text directory log: " + StringUtils.toString(e));
      error.setSubsystemName(Error.ERROR_SUBSYSTEM_WEBUI);
      error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
      error.setDetails(e);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
    }
  }


  /**
   * Shows JUnit XML log.
   */
  private static void addJUnitXMLLog(final Panel contentPanel, final ArchiveManager archiveManager, final StepLog stepLog) {
    final JUnitLogComponent logComponent = new JUnitLogComponent(archiveManager, stepLog);
    logComponent.setWidth(Pages.PAGE_WIDTH);
    logComponent.setAlignX(Layout.LEFT);
    logComponent.setAlignY(Layout.TOP);
    contentPanel.add(logComponent);
  }


  /**
   * Shows JUnit XML log.
   */
  private static void addPHPUnitXMLLog(final Panel contentPanel, final ArchiveManager archiveManager, final StepLog stepLog) {
    final PHPUnitLogComponent logComponent = new PHPUnitLogComponent(archiveManager, stepLog);
    logComponent.setWidth(Pages.PAGE_WIDTH);
    logComponent.setAlignX(Layout.LEFT);
    logComponent.setAlignY(Layout.TOP);
    contentPanel.add(logComponent);
  }


  /**
   * Shows CppUnit XML log.
   */
  private static void addCppUnitXMLLog(final Panel contentPanel, final ArchiveManager archiveManager, final StepLog stepLog) {
    final CppUnitLogComponent logComponent = new CppUnitLogComponent(archiveManager, stepLog);
    logComponent.setWidth(Pages.PAGE_WIDTH);
    logComponent.setAlignX(Layout.LEFT);
    logComponent.setAlignY(Layout.TOP);
    contentPanel.add(logComponent);
  }


  /**
   * Shows NUnit XML log.
   */
  private static void addNUnitXMLLog(final Panel contentPanel, final ArchiveManager archiveManager, final StepLog stepLog) {
    final NUnitLogComponent logComponent = new NUnitLogComponent(archiveManager, stepLog);
    logComponent.setWidth("100%");
    logComponent.setAlignX(Layout.LEFT);
    logComponent.setAlignY(Layout.TOP);
    contentPanel.add(logComponent);
  }


  /**
   * Shows Boost XML log.
   */
  private static void addBoostTestXMLLog(final Panel contentPanel, final ArchiveManager archiveManager, final StepLog stepLog) {
    final BoostTestLogComponent logComponent = new BoostTestLogComponent(archiveManager, stepLog);
    logComponent.setWidth("100%");
    logComponent.setAlignX(Layout.LEFT);
    logComponent.setAlignY(Layout.TOP);
    contentPanel.add(logComponent);
  }


  private static void addUnittestPPXMLLog(final Panel contentPanel, final ArchiveManager archiveManager, final StepLog stepLog) {
    final UnitTestPPLogComponent logComponent = new UnitTestPPLogComponent(archiveManager, stepLog);
    logComponent.setWidth("100%");
    logComponent.setAlignX(Layout.LEFT);
    logComponent.setAlignY(Layout.TOP);
    contentPanel.add(logComponent);
  }


  /**
   * Shows PMD XML log.
   */
  private static void addPMDXMLLog(final Panel contentPanel, final ArchiveManager archiveManager, final StepLog stepLog) {
    final PMDLogComponent logComponent = new PMDLogComponent(archiveManager, stepLog);
    logComponent.setWidth("100%");
    logComponent.setAlignX(Layout.LEFT);
    logComponent.setAlignY(Layout.TOP);
    contentPanel.add(logComponent);
  }


  /**
   * Shows Checkstyle XML log.
   */
  private static void addCheckstyleXMLLog(final Panel contentPanel, final ArchiveManager archiveManager, final StepLog stepLog) {
    final CheckstyleLogComponent logComponent = new CheckstyleLogComponent(archiveManager, stepLog);
    logComponent.setWidth("100");
    logComponent.setAlignX(Layout.LEFT);
    logComponent.setAlignY(Layout.TOP);
    contentPanel.add(logComponent);
  }


  /**
   * Shows file
   */
  private void addTextFileLog(final Panel contentPanel, final ArchiveManager archiveManager, final StepLog stepLog) {
    final TextLogLineRenderer logLineRenderer = createTextLogLineRenderer(stepLog);
    final TextLogComponent logComponent = new TextLogComponent(archiveManager, stepLog, logLineRenderer);
    logComponent.setWidth(Pages.PAGE_WIDTH);
    logComponent.setAlignX(Layout.LEFT);
    logComponent.setAlignY(Layout.TOP);
    contentPanel.add(logComponent);
  }


  /**
   * Shows file
   */
  private void addTextFileLog(final Panel contentPanel, final ArchiveManager archiveManager, final StepLog stepLog, final String fileNameToShow) {
    final TextLogLineRenderer logLineRenderer = createTextLogLineRenderer(stepLog);
    final TextLogComponent logComponent = new TextLogComponent(archiveManager, stepLog, fileNameToShow, logLineRenderer);
    logComponent.setWidth(Pages.PAGE_WIDTH);
    logComponent.setAlignX(Layout.LEFT);
    logComponent.setAlignY(Layout.TOP);
    contentPanel.add(logComponent);
  }


  /**
   * @noinspection HardcodedLineSeparator
   */
  private static TextLogLineRenderer createTextLogLineRenderer(final StepLog stepLog) {
    // Get system-wide markers
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    final String systemWideMarkers = scm.getSystemPropertyValue(SystemProperty.TEXT_LOG_MARKERS, "");

    // Get step markers
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final int stepRunID = stepLog.getStepRunID();
    final StepRun stepRun = cm.getStepRun(stepRunID);
    final BuildSequence buildSequence = cm.getBuildSequence(stepLog, stepRun.getName());
    final String stepMarkers = buildSequence.getFailurePatterns();

    // Create and return result
    return new TextLogLineRendererImpl(false, 200, systemWideMarkers + '\n' + stepMarkers);
  }


  /**
   * Shows given list of files. This method expects that all
   * files belong the same dir.
   */
  private static void showListOfFileNames(final Panel contentPanel, final int buildRunID, final int stepLogID, final ConsistentFileList fileList) {
    final String[] fileNames = fileList.getFileNames();
    for (int i = 0; i < fileNames.length; i++) {
      final String fileName = fileNames[i];
      final Properties params = new Properties();
      params.setProperty(Pages.PARAM_BUILD_RUN_ID, Integer.toString(buildRunID));
      params.setProperty(Pages.PARAM_LOG_ID, Integer.toString(stepLogID));
      params.setProperty(Pages.PARAM_FILE_ID, Integer.toString(fileList.getFileNameID(fileName)));
      contentPanel.add(new CommonLink(fileName, Pages.BUILD_LOG, params));
    }
  }


  static StepLog getLogFromParameters(final Parameters params) {
    return ConfigurationManager.getInstance()
            .getStepLog(ParameterUtils.getLogIDFromParameters(params));
  }


  private final class BuildRunLogURLFactory implements BuildRunURLFactory {

    /**
     * This method works by finding what the current log
     * is and then composing a log for a target build run.
     *
     * @param buildRun
     */
    public final LinkURL makeLinkURL(final BuildRun buildRun) {

      // get context
      final TierletContext tierletContext = getTierletContext();
      if (tierletContext == null) {
        return makeBuildRunChangesLinkURL(buildRun);
      }

      // get current log ID
      final HttpServletRequest httpServletRequest = tierletContext.getHttpServletRequest();
      final String stringLogID = httpServletRequest.getParameter(Pages.PARAM_LOG_ID);
      if (!StringUtils.isValidInteger(stringLogID)) {
        return makeBuildRunChangesLinkURL(buildRun);
      }

      // get log
      final Parameters params = new Parameters();
      params.addParameter(Pages.PARAM_LOG_ID, stringLogID);
      return makeLinkURL(buildRun, params);
    }


    /**
     * This method works by finding what the current log
     * is and then composing a log for a target build run.
     *
     * @param targetBuildRun
     */
    public final LinkURL makeLinkURL(final BuildRun targetBuildRun, final Parameters parameters) {
      final StepLog contextStepLog = getLogFromParameters(parameters);
      if (contextStepLog == null) {
        return makeBuildRunChangesLinkURL(targetBuildRun);
      }

      // find the same kind for log for the parameter build run
      final String description = contextStepLog.getDescription();
      final byte type = contextStepLog.getType();
      final byte pathType = contextStepLog.getPathType();
      final List targetBuildRunLogs = ConfigurationManager.getInstance().findStepLogs(targetBuildRun.getBuildRunID(), description, type, pathType);
      if (targetBuildRunLogs.isEmpty()) {
        return makeBuildRunChangesLinkURL(targetBuildRun);
      }

      final StepLog targetStepLog = (StepLog) targetBuildRunLogs.get(0);
      final Properties props = new Properties();
      props.setProperty(Pages.PARAM_BUILD_RUN_ID, targetBuildRun.getBuildRunIDAsString());
      props.setProperty(Pages.PARAM_LOG_ID, targetStepLog.getIDAsString());
      return new LinkURL(Pages.BUILD_LOG, props);
    }


    private LinkURL makeBuildRunChangesLinkURL(final BuildRun buildRun) {
      return new LinkURL(Pages.BUILD_CHANGES, Pages.PARAM_BUILD_RUN_ID, buildRun.getBuildRunID());
    }
  }


  public String toString() {
    return "LogPage{" +
            "configManager=" + configManager +
            "} " + super.toString();
  }
}
