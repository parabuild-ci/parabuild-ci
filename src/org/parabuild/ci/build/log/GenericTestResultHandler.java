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
package org.parabuild.ci.build.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.SimpleFileArchivedLogFinder;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepRunAttribute;
import org.parabuild.ci.remote.Agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Handles Generic test result handler in Properties format
 */
public final class GenericTestResultHandler extends AbstractLogHandler {

  public static final String PROPERTY_TEST_ERRORS = "test.errors";
  public static final String PROPERTY_TEST_FAILURES = "test.failures";
  public static final String PROPERTY_TEST_SKIPS = "test.skips";
  public static final String PROPERTY_TEST_SUCCESSES = "test.successes";

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(GenericTestResultHandler.class); // NOPMD


  /**
   * Constructor
   */
  public GenericTestResultHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                                  final String projectHome, final LogConfig logConfig, final int stepRunID) {
    super(agent, buildRunConfig, projectHome, logConfig, stepRunID);
  }


  /**
   * @return byte LOG type that this handler can process.
   */
  protected byte logType() {
    return LogConfig.LOG_TYPE_GENERIC_TEST_RESULT;
  }


  /**
   * Concrete processing.
   *
   * @throws IOException
   * @noinspection ControlFlowStatementWithoutBraces,OverlyBroadCatchBlock
   */
  protected void processLog() throws IOException {
    try {

      // check if it's a directory
      if (!super.agent.pathIsFile(fullyQualifiedResultPath)) {
        reportLogPathIsNotFile();
        return;
      }

      // get LOG and make an archive copy
      final String archiveFileName = archiveManager.makeNewLogFileNameOnly(); // just a file name, w/o path
      final File archiveFile = archiveManager.fileNameToLogPath(archiveFileName);
      agent.readFile(super.fullyQualifiedResultPath, archiveFile);

      // check if any
      if (!archiveFile.exists()) {
        return;
      }

      // save LOG info in the db if necessary

      // parse
      if (LOG.isDebugEnabled()) LOG.debug("parse ");

      final Properties properties = loadTestData(archiveFile);

      // get and save
      if (LOG.isDebugEnabled()) LOG.debug("get statistics ");
      final int errors = getIntegerProperty(properties, PROPERTY_TEST_ERRORS);
      final int failures = getIntegerProperty(properties, PROPERTY_TEST_FAILURES);
      final int skipped = getIntegerProperty(properties, PROPERTY_TEST_SKIPS);
      final int successes = getIntegerProperty(properties, PROPERTY_TEST_SUCCESSES);
      final int tests = errors + failures + skipped + successes;
      cm.addStepStatistics(super.stepRunID, StepRunAttribute.ATTR_JUNIT_ERRORS, errors);
      cm.addStepStatistics(super.stepRunID, StepRunAttribute.ATTR_JUNIT_FAILURES, failures);
      cm.addStepStatistics(super.stepRunID, StepRunAttribute.ATTR_JUNIT_SKIPS, skipped);
      cm.addStepStatistics(super.stepRunID, StepRunAttribute.ATTR_JUNIT_SUCCESSES, successes);
      cm.addStepStatistics(super.stepRunID, StepRunAttribute.ATTR_JUNIT_TESTS, tests);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_ERRORS, errors);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_FAILURES, failures);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_SUCCESSES, successes);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_TESTS, tests);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_NOTRUN, skipped);

      // save LOG info
      final StepLog stepLog = new StepLog();
      stepLog.setStepRunID(super.stepRunID);
      stepLog.setDescription(super.logConfig.getDescription());
      stepLog.setPath(resolvedResultPath);
      stepLog.setArchiveFileName(archiveFileName);
      stepLog.setType(StepLog.TYPE_CUSTOM);
      stepLog.setPathType(StepLog.PATH_TYPE_GENERIC_TEST); // path type is file
      stepLog.setFound((byte) 1);
      cm.save(stepLog);

    } catch (final Exception e) {
      throw IoUtils.createIOException(StringUtils.toString(e), e);
    }
  }


  /**
   * Helper method to load test data,
   *
   * @param archiveFile
   * @return properties object
   * @throws IOException
   */
  private static Properties loadTestData(final File archiveFile) throws IOException {
    final Properties properties = new Properties();
    InputStream is = null;
    try {
      is = new FileInputStream(archiveFile);
      properties.load(is);
    } finally {
      IoUtils.closeHard(is);
    }
    return properties;
  }


  /**
   * Helper method.
   *
   * @param properties
   * @param propertyName
   * @return
   */
  private int getIntegerProperty(final Properties properties, final String propertyName) {
    final String property = properties.getProperty(propertyName, "0");
    if (StringUtils.isValidInteger(property) && Integer.parseInt(property) >= 0) {
      return Integer.parseInt(property);
    } else {
      final Error error = new Error("Invalid integer value in the generic test report. The value should be an integer greater or equal zero: " + property, Error.ERROR_LEVEL_ERROR);
      errorManager.reportSystemError(error);
      return 0;
    }
  }


  /**
   * @param archivedLogs     - List of StepLog objects that are
   *                         already archived.
   * @param builderTimeStamp
   * @return true if LOG being processed was already archived.
   */
  protected boolean isLogAlreadyArchived(final List archivedLogs, final long builderTimeStamp) throws IOException, AgentFailureException {
    final SimpleFileArchivedLogFinder detector = new SimpleFileArchivedLogFinder(agent, archiveManager);
    detector.setIgnoreTimeStamp(isIgnoreTimeStamp());
    return detector.isAlreadyArchived(archivedLogs, fullyQualifiedResultPath, builderTimeStamp);
  }


  public String toString() {
    return "GenericTestResultHandler{}";
  }
}
