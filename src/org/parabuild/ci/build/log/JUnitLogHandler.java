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
import org.w3c.dom.Document;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.SimpleFileArchivedLogFinder;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.XMLUtils;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.remote.Agent;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles directory with JUnit logs
 */
public final class JUnitLogHandler extends AbstractLogHandler {


  /**
   * @noinspection UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(JUnitLogHandler.class); // NOPMD

  public static final String ARCHIVE_XML_ROOT = "testsuites";


  /**
   * Constructor
   */
  public JUnitLogHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                         final String projectHome, final LogConfig logConfig, final int stepRunID) {
    super(agent, buildRunConfig, projectHome, logConfig, stepRunID);
  }


  /**
   * @return byte log type that this handler can process.
   */
  protected byte logType() {
    return LogConfig.LOG_TYPE_JNUIT_XML_DIR;
  }


  /**
   * Concrete processing.
   *
   * @throws IOException
   */
  protected void processLog() throws IOException {
    final List tempFiles = new LinkedList();
    try {

      // check if it's a directory
      if (!super.agent.pathIsDirectory(fullyQualifiedResultPath)) {
        reportLogPathIsNotDirectory();
        return;
      }

      // get files
      final String[] files = agent.listFilesInDirectory(fullyQualifiedResultPath, "XML, xml");
      if (files.length == 0) {
        return;
      }

      // go over list of files in the JUnit directory
      for (int j = 0; j < files.length; j++) {
        final String source = files[j];
        if (agent.pathIsDirectory(source)) {
          continue;
        }
        final File localCopy = IoUtils.createTempFile(".auto", ".xml");
        tempFiles.add(localCopy);
        agent.readFile(source, localCopy);
      }
      if (tempFiles.isEmpty()) {
        return;
      }

      // merge logs
      // REVIEWME: switch to dom4j/jaxen-dom4j instead of jaxen-dom
      // for better performance:
      //   http://jaxen.sourceforge.net
      //   http://dom4j.org/benchmarks/xpath/index.html
      final Document mergedLog = XMLUtils.mergeXMLFiles(tempFiles, ARCHIVE_XML_ROOT);

      // store merged logs in archive file
      final String archiveFileName = archiveManager.makeNewLogFileNameOnly(); // just a file name, w/o path
      final File archiveFile = archiveManager.fileNameToLogPath(archiveFileName);
      XMLUtils.writeDom2File(mergedLog, archiveFile);
      archiveFile.setLastModified(agent.getFileDescriptor(fullyQualifiedResultPath).lastModified());

      // Get and save stats
      final int activeBuildID = cm.getBuildRunActiveConfigID(buildRunID);
      final JUnitStatisticsProcessor statisticsProcessor = new JUnitStatisticsProcessor(activeBuildID, buildRunID, stepRunID);
      statisticsProcessor.processMergedLog(mergedLog);

      // save log info in the db if necessary
      final StepLog stepLog = new StepLog();
      stepLog.setStepRunID(super.stepRunID);
      stepLog.setDescription(super.logConfig.getDescription());
      stepLog.setPath(resolvedResultPath);
      stepLog.setArchiveFileName(archiveFileName);
      stepLog.setType(StepLog.TYPE_CUSTOM);
      stepLog.setPathType(StepLog.PATH_TYPE_JUNIT_XML);
      stepLog.setFound((byte) 1);
      cm.save(stepLog);
    } catch (final Exception | OutOfMemoryError e) {
      throw IoUtils.createIOException(StringUtils.toString(e), e);
    } finally {
      IoUtils.deleteFilesHard(tempFiles);
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
