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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.DirectoryTraverserCallback;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.LogConfigProperty;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.services.ModifiedFileList;

/**
 * Handles directory-with-HTML-files custom log.
 * <p/>
 * HTMLDirLogHandler stores path to the index file in the
 * StepLog.
 */
public final class HTMLDirLogHandler extends AbstractLogHandler {

  private static final Log log = LogFactory.getLog(HTMLDirLogHandler.class);
  private final String indexPath;
  private final boolean notifyIfIndexIsMissing;


  /**
   * Constructor
   */
  public HTMLDirLogHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                           final String projectHome, final LogConfig logConfig, final int stepRunID) {
    super(agent, buildRunConfig, projectHome, logConfig, stepRunID);
    this.indexPath = cm.getLogConfigPropertyValue(logConfig.getID(), LogConfigProperty.ATTR_HTML_INDEX_FILE, null);
    this.notifyIfIndexIsMissing = cm.getLogConfigPropertyValue(logConfig.getID(), LogConfigProperty.ATTR_NOTIFY_ABOUT_MISSING_INDEX, LogConfigProperty.OPTION_CHECKED).equals(LogConfigProperty.OPTION_CHECKED);
    if (StringUtils.isBlank(indexPath)) {
      throw new IllegalStateException("Index file is undefined for HTML directory log. Please check build configuration.");
    }
  }


  /**
   * @return byte log type that this handler can process.
   */
  protected byte logType() {
    return LogConfig.LOG_TYPE_HTML_DIR;
  }


  /**
   * Concrete processing for single text file.
   *
   * @throws IOException
   */
  protected void processLog() throws IOException, AgentFailureException {

    // check if it's a dur
    if (!super.agent.pathIsDirectory(fullyQualifiedResultPath)) {
      reportLogPathIsNotDirectory();
      return;
    }

    // check if index file prop is there

    // check if index file is there
    final String absoluteIndexPath = fullyQualifiedResultPath + '/' + indexPath;
    if (!agent.absolutePathExists(absoluteIndexPath)) {
      if (notifyIfIndexIsMissing) {
        reportIndexFileWasNotFound(indexPath);
      }
      return;
    }

    // check if there are changes
    final long builderTimeStamp = cm.getBuilderTimeStamp(cm.getStepRun(stepRunID));
    if (log.isDebugEnabled()) {
      log.debug("builderTimeStamp: " + builderTimeStamp);
    }
    final ModifiedFileList modifiedFiles = agent.getModifiedFiles(fullyQualifiedResultPath, builderTimeStamp, 1);
    if (log.isDebugEnabled()) {
      log.debug("modifiedFiles: " + modifiedFiles);
    }
    if (!isIgnoreTimeStamp() && modifiedFiles.getFiles().isEmpty()) {
      return;
    }

    // copy dir content

    // get log and make an archive copy
    final String archiveFileName = archiveManager.makeNewLogFileNameOnly(); // just a file name, w/o path
    final File thisLogArchiveHome = archiveManager.fileNameToLogPath(archiveFileName);
    final int thisLogArchiveHomeLength = thisLogArchiveHome.toString().length();
    thisLogArchiveHome.mkdirs();
    agent.getDirectory(fullyQualifiedResultPath, thisLogArchiveHome);

    // go over list of files in the arhchive directory
    final List filesToIndex = new LinkedList();
    IoUtils.traversePath(thisLogArchiveHome, new DirectoryTraverserCallback() {
      public boolean callback(final File file) {
        if (file.isFile()) {
          final String fileAsString = file.toString();
          if (hasIndexableSuffix(fileAsString)) {
            filesToIndex.add(new IndexableDirectoryFile(file, fileAsString.substring(thisLogArchiveHomeLength)));
          }
        }
        return true;
      }


      /**
       * @param fileAsString
       *
       * @return true if given file has indexable exntension.
       */
      private boolean hasIndexableSuffix(final String fileAsString) {
        final String lowerCased = fileAsString.toLowerCase();
        return lowerCased.endsWith(".html")
                || lowerCased.endsWith(".htm");
      }
    });

    // save log info in the db if necessary
    if (thisLogArchiveHome.exists() && thisLogArchiveHome.list().length > 0) {
      final StepLog stepLog = new StepLog();
      stepLog.setStepRunID(super.stepRunID);
      stepLog.setDescription(super.logConfig.getDescription());
      stepLog.setPath(indexPath.replace('\\', '/')); // we replace it because that's how it's going to appear in search results.
      stepLog.setArchiveFileName(archiveFileName);
      stepLog.setType(StepLog.TYPE_CUSTOM);
      stepLog.setPathType(StepLog.PATH_TYPE_HTML_DIR); // path type is file
      stepLog.setFound((byte) 1);
      cm.save(stepLog);
      if (log.isDebugEnabled()) {
        log.debug("saved stepLog: " + stepLog);
      }
      for (final Iterator i = filesToIndex.iterator(); i.hasNext();) {
        final IndexableDirectoryFile idf = (IndexableDirectoryFile) i.next();
        searchManager.index(stepLog, idf.getContent(), idf.getFileNameInArchiveDir());
      }
    } else {
      IoUtils.deleteFileHard(thisLogArchiveHome);
    }
  }


  /**
   * Returns a list of build run logs that will be used by
   * isLogAlreadyArchived to find if the log about to be
   * processed was already processed.
   * <p/>
   * This impelemtation uses index path instead of log config
   * path used by super class.
   *
   * @param buildRunID for wich perform getting logs
   * @return List StepLog objects.
   * @see AbstractLogHandler#isLogAlreadyArchived(List,long)
   */
  protected List getBuildRunLogs(final int buildRunID) {
    return cm.findBuildRunLogs(buildRunID, logType(), indexPath);
  }


  /**
   * @param archivedLogs     - List of StepLog objects that are
   *                         already archived.
   * @param builderTimeStamp
   * @return true if log being processed was already archived.
   * @see #getBuildRunLogs(int)
   */
  protected boolean isLogAlreadyArchived(final List archivedLogs, final long builderTimeStamp) {
    // NOTE: vimeshev - 07/03/2005 - the fact that we have gotten
    // called means that there was this type and this path. We
    // return true - it means that any second attempt to process
    // same just existsing dir is considered sthe same.
    return !archivedLogs.isEmpty();
  }


  /**
   * Reports that there was not an index file there.
   *
   * @param configuredIndexPath - path
   */
  private void reportIndexFileWasNotFound(final String configuredIndexPath) {
    final Error error = new Error("Index file \"" + configuredIndexPath + "\" for log \"" + logConfig.getDescription() + "\" was not found.");
    error.setPossibleCause("May be log is not configured correctly.");
    error.setBuildID(logConfig.getBuildID());
    error.setSendEmail(true);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_LOGGING);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    errorManager.reportSystemError(error);
  }


  /**
   * Placeholder for iterative call to indexer.
   */
  private static final class IndexableDirectoryFile {

    private File content = null;
    private String fileNameInArchiveDir = null;


    IndexableDirectoryFile(final File content, final String relativePathInArchiveDir) {
      this.content = content;
      this.fileNameInArchiveDir = relativePathInArchiveDir.replace('\\', '/');
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


  public String toString() {
    return "HTMLDirLogHandler{" +
            "indexPath='" + indexPath + '\'' +
            ", notifyIfIndexIsMissing=" + notifyIfIndexIsMissing +
            '}';
  }
}
