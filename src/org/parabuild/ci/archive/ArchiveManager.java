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
package org.parabuild.ci.archive;

import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.object.StepRun;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * LogArchiveManager defines a protocol for handling log
 * archives.
 */
public interface ArchiveManager {


  /**
   * Creates fully qualified build sequence log file from given
   * fileName.
   *
   * @return fully qualified build sequence log file from given
   *         fileName.
   */
  File fileNameToLogPath(String fileName) throws IOException;


  /**
   * Creates fully qualified build sequence result file from
   * given fileName.
   *
   * @return fully qualified build sequence result file from
   *         given fileName.
   */
  File fileNameToResultPath(String archiveFileName) throws IOException;


  /**
   * Creates a new build sequence log file name. It's guaranteed
   * the the returned file will have a distinct name.
   *
   * @param sequence
   *
   */
  String makeNewStepLogFileName(BuildSequence sequence);


  /**
   * Creates a new build log file name. It's guaranteed the the
   * returned file will have a distinct name.
   * <p/>
   * A caller can make both a file and a directory from this
   * name.
   */
  String makeNewLogFileNameOnly();


  /**
   * Creates a new build result file name. It's guaranteed the
   * the returned file will have a distinct name.
   * <p/>
   * A caller can make both a file and a directory from this
   * name.
   */
  String makeNewResultFileNameOnly();


  /**
   * Returns build log prefix
   */
  String getBuildLogPrefix();


  /**
   * @return File main build log directory. The main log
   *         directory is a directory where build runner wrtites
   *         the buin build log to.
   */
  File getBuildLogDir() throws IOException;


  /**
   * Deletes build logs that are older than given number of
   * days.
   */
  void deleteExpiredBuildLogs(int days) throws IOException;


  /**
   * Returns fully qualifed path to an archived log storage.
   * <p/>
   * Whether is a file or a directory depends on log path type.
   *
   * @param stepLog for which to return the path.
   *
   * @return fully qualifed path to an archived log.
   *
   * @see StepLog#PATH_TYPE_HTML_DIR - stored as dir
   * @see StepLog#PATH_TYPE_HTML_FILE - stored as dir
   * @see StepLog#PATH_TYPE_TEXT_DIR - stored as dir
   * @see StepLog#PATH_TYPE_JUNIT_XML  - stored as file
   * @see StepLog#PATH_TYPE_PMD_XML - stored as file
   * @see StepLog#PATH_TYPE_TEXT_FILE - stored as file
   */
  File getArchivedLogHome(StepLog stepLog) throws IOException;


  /**
   * Packs (zips) expired build logs. Original log or a directory
   * gets deleted.
   * <p/>
   * Name of the file or a directory holding a logs changes by
   * adding a ".zip" suffix. For instance, if it was a log file
   * b1c101098905223569.log, it will become b1c101098905223569.log.zip
   */
  void packExpiredBuildLogs() throws IOException;


  /**
   * Returns input stream for an archived log. This method is
   * valid for single-file logs.
   *
   * @param stepLog
   *
   * @return null if log does not exist.
   *
   * @throws IllegalArgumentException if given log is
   * not a single-file log.
   */
  InputStream getArchivedLogInputStream(StepLog stepLog) throws IllegalArgumentException, IOException;


  /**
   * Returns input stream for an archived log. This method is
   * valid for multifile logs.
   *
   * @param stepLog
   *
   * @return null if log does not exist.
   *
   * @throws IllegalArgumentException if given log is
   * not a multifile log.
   */
  InputStream getArchivedLogInputStream(StepLog stepLog, String inArchiveFileName) throws IllegalArgumentException, IOException;


  /**
   * Returns String list of relative file names constituting an archive
   * dir. Valid only for dir-type logs.
   *
   * @param stepLog
   *
   * @return list of {@link ArchiveEntry} constituting an archive
   *         dir.
   */
  List getArchivedLogEntries(StepLog stepLog) throws IOException;


  /**
   * Returns list of log lines
   *
   * @param stepRun
   *
   */
  List getLogWindowLines(StepRun stepRun);


  /**
   * Returns input stream for an archived build result. This
   * method is valid for multifile results.
   *
   * @param stepResult
   *
   * @return null if result does not exist.
   *
   * @throws IllegalArgumentException if given log is
   * not a multifile log.
   */
  InputStream getArchivedResultInputStream(StepResult stepResult, String inArchiveFileName) throws IllegalArgumentException, IOException;


  /**
   * Returns list of relative file names constituting an archive
   * dir. The list is one level only. Valid only for dir-type
   * logs.
   *
   * @param stepResult
   *
   * @return list of {@link ArchiveEntry} constituting an archive
   *         dir, single level.
   */
  List getArchivedResultEntries(StepResult stepResult) throws IOException;


  /**
   * Returns input stream for an archived build result. This
   * method is valid for single-file results.
   *
   * @param stepResult
   *
   * @return null if result does not exist.
   *
   * @throws IllegalArgumentException if given log is
   * not a single-file log.
   */
  InputStream getArchivedResultInputStream(StepResult stepResult) throws IllegalArgumentException, IOException;


  /**
   * Returns fully qualifed path to an archived build result.
   *
   * @param stepResult for which to return the path.
   *
   * @return fully qualifed path to an archived result.
   *
   * @see StepResult#PATH_TYPE_DIR - stored as dir
   * @see StepResult#PATH_TYPE_SINGLE_FILE - stored as dir
   */
  File getArchivedResultHome(StepResult stepResult) throws IOException;


  /**
   * @return File main build result directory. The main result
   *         directory is a directory where build runner stores
   *         the build results to.
   */
  File getResultDir() throws IOException;


  /**
   * Deletes build results that are older than given number of
   * days.
   */
  void deleteExpiredBuildResults(int days) throws IOException;


  /**
   * Returns build result prefix
   */
  String getBuildResultPrefix();


  /**
   * Deletes log files from the archive.
   *
   * @param stepLog StepLog to delete.
   */
  void deleteLog(StepLog stepLog) throws IOException;


  /**
   * Deletes result files from the archive. The step result also
   * gets deleted from the database.
   *
   * @param stepResult StepResult to delete.
   */
  void deleteResult(StepResult stepResult) throws IOException;


  /**
   * Deletes expired build logs it build settings explicitelly
   * allow this. An expiration date is selected as a maximum of
   * build settings and system-wide minumim settings.
   */
  void deleteExpiredBuildResults() throws IOException;
}
