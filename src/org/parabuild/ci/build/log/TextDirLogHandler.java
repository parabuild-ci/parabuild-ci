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
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.LogConfigProperty;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.services.ModifiedFileList;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles directory-with-text-files custom log.
 */
public final class TextDirLogHandler extends AbstractLogHandler {

  private static final Log log = LogFactory.getLog(TextDirLogHandler.class);


  /**
   * Constructor
   */
  public TextDirLogHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                           final String projectHome, final LogConfig logConfig, final int stepRunID) {
    // call super
    super(agent, buildRunConfig, projectHome, logConfig, stepRunID);
  }


  /**
   * @return byte log type that this handler can process.
   */
  protected byte logType() {
    return LogConfig.LOG_TYPE_TEXT_DIR;
  }


  /**
   * Concrete processing for single text file.
   *
   * @throws IOException
   */
  protected void processLog() throws IOException, AgentFailureException {

    // check if it's a file
    if (!super.agent.pathIsDirectory(fullyQualifiedResultPath)) {
      reportLogPathIsNotDirectory();
      return;
    }

    // check if there are changes
    final long builderTimeStamp = cm.getBuilderTimeStamp(cm.getStepRun(stepRunID));
    final ModifiedFileList modifiedFiles = agent.getModifiedFiles(fullyQualifiedResultPath, builderTimeStamp, 1);
    if (!isIgnoreTimeStamp() && modifiedFiles.getFiles().isEmpty()) {
      return;
    }

    // copy dir content, only root files with given extension

    // get log and make an archive copy
    final String archiveFileName = archiveManager.makeNewLogFileNameOnly(); // just a file name, w/o path
    final File archiveDir = archiveManager.fileNameToLogPath(archiveFileName);

    // get extensions
    final String extensions = cm.getLogConfigPropertyValue(logConfig.getID(), LogConfigProperty.ATTR_FILE_EXTENSIONS, ".log");

    // go over list of files in the directory
    final List filesRead = new ArrayList(23);
    final String[] files = agent.listFilesInDirectory(fullyQualifiedResultPath, extensions);
    if (files != null && files.length > 0) {
      final long logLastModified = agent.getFileDescriptor(fullyQualifiedResultPath).lastModified();
      archiveDir.mkdirs(); // create destination directory
      if (log.isDebugEnabled()) {
        log.debug("logLastModified: " + logLastModified);
      }
      if (log.isDebugEnabled()) {
        log.debug("archiveDir.lastModified(): " + archiveDir.lastModified());
      }
      for (int j = 0; j < files.length; j++) {
        final String source = files[j];
        if (agent.pathIsDirectory(source)) {
          continue;
        }
        if (agent.getFileDescriptor(source).lastModified() < builderTimeStamp) {
          continue;
        }
        final String fileNameOnly = agent.getFileName(source);
        final File readInTo = new File(archiveDir, fileNameOnly);
        agent.readFile(source, readInTo);
        filesRead.add(new IndexableDirectoryFile(readInTo, fileNameOnly));
      }
      // NOTE: vimeshev - 07/11/2005 - we set the date in the end because
      // adding files changes the dir's mod date.
      archiveDir.setLastModified(logLastModified);
    }


    // save log info in the db if necessary
    if (archiveDir.exists() && archiveDir.list().length > 0) {
      final StepLog stepLog = new StepLog();
      stepLog.setStepRunID(super.stepRunID);
      stepLog.setDescription(super.logConfig.getDescription());
      stepLog.setPath(resolvedResultPath.replace('\\', '/'));
      stepLog.setArchiveFileName(archiveFileName);
      stepLog.setType(StepLog.TYPE_CUSTOM);
      stepLog.setPathType(StepLog.PATH_TYPE_TEXT_DIR); // path type is file
      stepLog.setFound((byte) 1);
      cm.save(stepLog);
      if (log.isDebugEnabled()) {
        log.debug("saved stepLog: " + stepLog);
      }
      for (final Iterator i = filesRead.iterator(); i.hasNext();) {
        final IndexableDirectoryFile idf = (IndexableDirectoryFile) i.next();
        searchManager.index(stepLog, idf.getContent(), idf.getFileNameInArchiveDir());
      }
    } else {
      IoUtils.deleteFileHard(archiveDir);
    }
  }


  /**
   * @param archivedLogs     - List of StepLog objects that are
   *                         already archived.
   * @param builderTimeStamp
   * @return true if log being processed was already archived.
   */
  protected boolean isLogAlreadyArchived(final List archivedLogs, final long builderTimeStamp) throws IOException, AgentFailureException {

    // REVIEWME: consider extracting this code to arch/source dir comparer

    // get descriptor
    final RemoteFileDescriptor targetDescriptor = agent.getFileDescriptor(fullyQualifiedResultPath);
    if (log.isDebugEnabled()) {
      log.debug("logPath: " + fullyQualifiedResultPath);
    }
    if (log.isDebugEnabled()) {
      log.debug("targetDescriptor: " + targetDescriptor);
    }

    // there are results, find if time stamps and sizes are different.
    for (final Iterator i = archivedLogs.iterator(); i.hasNext();) {
      final StepLog stepLog = (StepLog) i.next();

      // for us it is the archived file result itself
      final File archivedLogHome = archiveManager.getArchivedLogHome(stepLog);
      if (!archivedLogHome.exists()) {
        log.warn("Expected the archived file \"" + archivedLogHome + "\" to exist, but it did not. " + stepLog.toString());
        continue;
      }

      if (log.isDebugEnabled()) {
        log.debug("stepLog: " + stepLog);
      }
      if (log.isDebugEnabled()) {
        log.debug("archivedLogHome.lastModified(): " + archivedLogHome.lastModified());
      }
      if (log.isDebugEnabled()) {
        log.debug("targetDescriptor.lastModified(): " + targetDescriptor.lastModified());
      }
      if (targetDescriptor.lastModified() == archivedLogHome.lastModified()) {
        if (log.isDebugEnabled()) {
          log.debug("found");
        }
        return true; // found in archive
      }
    }

    // not found in archive
    return false;
  }


  /**
   * Placeholder for iterative call to indexer.
   */
  private static final class IndexableDirectoryFile {

    private final File content;
    private final String fileNameInArchiveDir;


    IndexableDirectoryFile(final File content, final String relativePathInArchiveDir) {
      this.content = content;
      this.fileNameInArchiveDir = relativePathInArchiveDir;
    }


    public File getContent() {
      return content;
    }


    public String getFileNameInArchiveDir() {
      return fileNameInArchiveDir;
    }


    public String toString() {
      return "IndexableDirectoryFile{" +
              "content=" + content +
              ", fileNameInArchiveDir='" + fileNameInArchiveDir + '\'' +
              '}';
    }
  }
}
