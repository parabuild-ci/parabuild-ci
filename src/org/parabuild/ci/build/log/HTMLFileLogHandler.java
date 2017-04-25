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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.SimpleFileArchivedLogFinder;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.remote.Agent;

/**
 * Handles single-HTML-file customer log.
 */
public final class HTMLFileLogHandler extends AbstractLogHandler {

  private static final Log log = LogFactory.getLog(HTMLFileLogHandler.class);


  /**
   * Constructor
   */
  public HTMLFileLogHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                            final String projectHome, final LogConfig logConfig, final int stepRunID) {
    super(agent, buildRunConfig, projectHome, logConfig, stepRunID);
  }


  /**
   * @return byte log type that this handler can process.
   */
  protected byte logType() {
    return LogConfig.LOG_TYPE_HTML_FILE;
  }


  /**
   * Concrete processing for single text file.
   *
   * @throws IOException
   */
  protected void processLog() throws IOException, AgentFailureException {

    // check if it's a file
    if (!super.agent.pathIsFile(fullyQualifiedResultPath)) {
      reportLogPathIsNotFile();
      return;
    }

    // get log and make an archive copy

    // make a new name
    final String archiveFileName = archiveManager.makeNewLogFileNameOnly(); // just a file name, w/o path
    // create a dir File object to store full log path
    final File archiveFileDir = archiveManager.fileNameToLogPath(archiveFileName);
    // create a fully qualified File object to store full log path
    final File archiveFile = new File(archiveFileDir, resolvedResultPath);
    // create dirs
    archiveFile.getParentFile().mkdirs();
    // get remote path
    agent.readFile(super.fullyQualifiedResultPath, archiveFile);

    // save log info in the db if necessary
    if (archiveFile.exists()) {
      // save into db
      final StepLog stepLog = new StepLog();
      stepLog.setStepRunID(super.stepRunID);
      stepLog.setDescription(super.logConfig.getDescription());
      stepLog.setPath(resolvedResultPath.replace('\\', '/'));
      stepLog.setArchiveFileName(archiveFileName);
      stepLog.setType(StepLog.TYPE_CUSTOM);
      stepLog.setPathType(StepLog.PATH_TYPE_HTML_FILE); // path type is file
      stepLog.setFound((byte) 1);
      if (log.isDebugEnabled()) {
        log.debug("saved stepLog: " + stepLog);
      }
      cm.save(stepLog);
      // index
      searchManager.index(stepLog, archiveFile);
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
