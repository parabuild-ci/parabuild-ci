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
 * Handles Findbugs log handler in XML format
 */
public final class FindbugsLogHandler extends AbstractLogHandler {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(FindbugsLogHandler.class); // NOPMD


  /**
   * Constructor
   */
  public FindbugsLogHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                            final String projectHome, final LogConfig logConfig, final int stepRunID) {
    super(agent, buildRunConfig, projectHome, logConfig, stepRunID);
  }


  /**
   * @return byte log type that this handler can process.
   */
  protected byte logType() {
    return LogConfig.LOG_TYPE_FINDBUGS_XML_FILE;
  }


  /**
   * Concrete processing.
   *
   * @throws IOException
   */
  protected void processLog() throws IOException {
    try {

      // check if it's a directory
      if (!super.agent.pathIsFile(fullyQualifiedResultPath)) {
        reportLogPathIsNotFile();
        return;
      }

      // get log and make an archive copy
      final String archiveFileName = archiveManager.makeNewLogFileNameOnly(); // just a file name, w/o path
      final File archiveFile = archiveManager.fileNameToLogPath(archiveFileName);
      agent.readFile(super.fullyQualifiedResultPath, archiveFile);

      // check if any
      if (!archiveFile.exists()) {
        return;
      }

      // save log info in the db if necessary

      // parse
      final Document findbugs = XMLUtils.parseDom(archiveFile, false);

      // get and save
      final int problemCount = XMLUtils.intValueOf(findbugs, "count(/BugCollection/BugInstance)");
      cm.addStepStatistics(super.stepRunID, StepRunAttribute.ATTR_FINDBUGS_PROBLEMS, problemCount);
      cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_FINDBUGS_PROBLEMS, problemCount);

      // save log info
      final StepLog stepLog = new StepLog();
      stepLog.setStepRunID(super.stepRunID);
      stepLog.setDescription(super.logConfig.getDescription());
      stepLog.setPath(resolvedResultPath);
      stepLog.setArchiveFileName(archiveFileName);
      stepLog.setType(StepLog.TYPE_CUSTOM);
      stepLog.setPathType(StepLog.PATH_TYPE_FINDBUGS_XML); // path type is file
      stepLog.setFound((byte) 1);
      cm.save(stepLog);

    } catch (final Exception e) {
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
    final SimpleFileArchivedLogFinder detector = new SimpleFileArchivedLogFinder(agent, archiveManager);
    detector.setIgnoreTimeStamp(isIgnoreTimeStamp());
    return detector.isAlreadyArchived(archivedLogs, fullyQualifiedResultPath, builderTimeStamp);
  }
}
