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
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepRunAttribute;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.XMLUtils;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Handles directory with JUnit logs
 */
public final class SquishLogHandler extends AbstractLogHandler {


  /**
   * @noinspection UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SquishLogHandler.class); // NOPMD


  /**
   * Constructor
   */
  public SquishLogHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                              final String projectHome, final LogConfig logConfig, final int stepRunID) {
    super(agent, buildRunConfig, projectHome, logConfig, stepRunID);
  }


  /**
   * @return byte log type that this handler can process.
   */
  protected byte logType() {
    return LogConfig.LOG_TYPE_SQUISH_TESTER_XML_FILE;
  }


  /**
   * Concrete processing.
   *
   * @throws IOException
   */
  protected void processLog() throws IOException {
    //noinspection ErrorNotRethrown
    try {

      // Check if it's a directory
      if (!agent.pathIsFile(fullyQualifiedResultPath)) {
        reportLogPathIsNotFile();
        return;
      }

      // Get log and make an archive copy
      final String archiveFileName = archiveManager.makeNewLogFileNameOnly(); // just a file name, w/o path
      final File archiveFile = archiveManager.fileNameToLogPath(archiveFileName);
      agent.readFile(fullyQualifiedResultPath, archiveFile);

      // Check if any
      if (!archiveFile.exists()) {
        return;
      }

      // Set time stamp
      //noinspection ResultOfMethodCallIgnored
      archiveFile.setLastModified(agent.getFileDescriptor(fullyQualifiedResultPath).lastModified());

      // Get and save stats
      final int activeBuildID = cm.getBuildRunActiveConfigID(buildRunID);
      final Document mergedDocument = XMLUtils.parseDom(archiveFile, false);

      final int errors = XMLUtils.intValueOf(mergedDocument, "/SquishReport/summary/@errors");
      final int failures = XMLUtils.intValueOf(mergedDocument, "/SquishReport/summary/@fails");
      final int testCases = XMLUtils.intValueOf(mergedDocument, "/SquishReport/summary/@testcases");
      final int tests = XMLUtils.intValueOf(mergedDocument, "/SquishReport/summary/@tests");
      final int passes = XMLUtils.intValueOf(mergedDocument, "/SquishReport/summary/@passes");
      final int fatals = XMLUtils.intValueOf(mergedDocument, "/SquishReport/summary/@fatals");
      final int expectedFails = XMLUtils.intValueOf(mergedDocument, "/SquishReport/summary/@expected_fails");
      final int unexpectedPasses = XMLUtils.intValueOf(mergedDocument, "/SquishReport/summary/@unexpected_passes");
      final int warnings = XMLUtils.intValueOf(mergedDocument, "/SquishReport/summary/@warnings");

      // Step statistics
      cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_JUNIT_ERRORS, errors);
      cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_JUNIT_FAILURES, failures);
      cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_JUNIT_SUCCESSES, passes);
      cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_JUNIT_TESTS, tests);

      cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_JUNIT_FATALS, fatals);
      cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_JUNIT_EXPECTED_FAILS, expectedFails);
      cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_JUNIT_UNEXPECTED_PASSES, unexpectedPasses);
      cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_JUNIT_WARNINGS, warnings);
      cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_JUNIT_TESTCASES, testCases);

      // Run statistics
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_ERRORS, errors);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_FAILURES, failures);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_SUCCESSES, passes);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_TESTS, tests);
      
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_FATALS, fatals);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_EXPECTED_FAILS, expectedFails);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_UNEXPECTED_PASSES, unexpectedPasses);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_WARNINGS, warnings);

      // Save log info in the db if necessary
      final StepLog stepLog = new StepLog();
      stepLog.setStepRunID(stepRunID);
      stepLog.setDescription(logConfig.getDescription());
      stepLog.setPath(resolvedResultPath);
      stepLog.setArchiveFileName(archiveFileName);
      stepLog.setType(StepLog.TYPE_CUSTOM);
      stepLog.setPathType(StepLog.PATH_TYPE_SQUISH_XML);
      stepLog.setFound((byte) 1);
      cm.save(stepLog);
    } catch (final Exception | OutOfMemoryError e) {
      throw IoUtils.createIOException(StringUtils.toString(e), e);
    }
  }


  /**
   * @param archivedLogs     - List of StepLog objects that are
   *                         already archived.
   * @param builderTimeStamp
   * @return true if log being processed was already archived.
   */
  protected boolean isLogAlreadyArchived(final List archivedLogs, final long builderTimeStamp) throws IOException, AgentFailureException {
    final SimpleFileArchivedLogFinder finder = new SimpleFileArchivedLogFinder(agent, archiveManager);
    finder.setIgnoreTimeStamp(isIgnoreTimeStamp());
    return finder.isAlreadyArchived(archivedLogs, fullyQualifiedResultPath, true, builderTimeStamp);
  }
}