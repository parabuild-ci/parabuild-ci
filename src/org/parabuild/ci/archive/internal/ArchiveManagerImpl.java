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
package org.parabuild.ci.archive.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.archive.ArchiveEntry;
import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.common.DirectoryTraverserCallback;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.remote.internal.LocalBuilderFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Implementation of LogArchiveManager
 *
 * @see ArchiveManager
 */
public final class ArchiveManagerImpl implements ArchiveManager {

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(ArchiveManagerImpl.class); // NOPMD
  /**
   * @noinspection StaticNonFinalField
   */
  private static int logFileStepSource = 0;
  /**
   * @noinspection StaticNonFinalField
   */
  private static int resultFileStepSource = 0;
  private final ConfigurationManager cm = ConfigurationManager.getInstance();
  private final File buildLogDir;
  private final File buildResultDir;
  private int activeBuildID;


  public ArchiveManagerImpl(final int buildID) {
    // NOTE: simeshev@parabuildci.org -> 04/05/2005 - currently we translate
    // this build ID to guaranteed active ID. Instead we may want to go
    // through the usage of ArchiveManager and make sure that active build
    // is used there.
    activeBuildID = cm.getActiveIDFromBuildID(buildID);
//    if (log.isDebugEnabled()) log.debug("buildID: " + buildID + ", activeBuildID: " + activeBuildID);
    if (ConfigurationManager.validateActiveID) {
      cm.validateIsActiveBuildID(activeBuildID);
    }
    // create build logs dir
    final File archiveHomeDir = LocalBuilderFiles.archiveHome();
    buildLogDir = new File(archiveHomeDir, "b" + activeBuildID);
    buildResultDir = new File(archiveHomeDir, "r" + activeBuildID);
  }


  /**
   * Creates fully qualified build sequence log file from given
   * fileName.
   */
  public File fileNameToLogPath(final String fileName) throws IOException {
    return new File(getBuildLogDir(), fileName);
  }


  /**
   * Creates fully qualified build sequence result file from
   * given fileName.
   *
   * @return fully qualified build sequence result file from
   * given fileName.
   */
  public File fileNameToResultPath(final String archiveFileName) throws IOException {
    return new File(getBuildResultDir(), archiveFileName);
  }


  /**
   * Creates a new build sequence log file name. It's guaranteed
   * the the returned file will have a distinct name.
   *
   * @param sequence the build step to generate the new log name for.
   */
  public String makeNewStepLogFileName(final BuildSequence sequence) {
    synchronized (ArchiveManagerImpl.class) {
      logFileStepSource++;
      return getBuildLogPrefix() + 's' + sequence.getSequenceID()
              + '_' + logFileStepSource + System.currentTimeMillis() + ".log";
    }
  }


  /**
   * Creates a new build log file name. It's guaranteed the the
   * returned file will have a distinct name.
   * <p/>
   * A caller can make both a file and a directory from this
   * name.
   */
  public String makeNewLogFileNameOnly() {
    synchronized (ArchiveManagerImpl.class) {
      logFileStepSource++;
      return getBuildLogPrefix()
              + 'c' + logFileStepSource + System.currentTimeMillis() + ".log";
    }
  }


  /**
   * Creates a new build result file name. It's guaranteed the
   * the returned file will have a distinct name.
   * <p/>
   * A caller can make both a file and a directory from this
   * name.
   */
  public String makeNewResultFileNameOnly() {
    synchronized (ArchiveManagerImpl.class) {
      resultFileStepSource++;
      return getBuildResultPrefix()
              + 'c' + resultFileStepSource + System.currentTimeMillis() + ".res";
    }
  }


  /**
   * Deletes build logs that are older than given number of
   * days.
   */
  public void deleteExpiredBuildLogs(final int days) throws IOException {
    // delegate handling to LogRetentionHandler
    final LogRetentionHandler logRetentionHandler = new LogRetentionHandler(activeBuildID, getBuildLogDir(), getBuildLogPrefix());
    logRetentionHandler.setCurrentCutOffDays(days);
    logRetentionHandler.deleteExpired();
  }


  /**
   * Deletes build results that are older than given number of
   * days.
   */
  public void deleteExpiredBuildResults(final int days) throws IOException {
    // delegate handling to ResultRetentionHandler
    final ResultRetentionHandler resultRetentionHandler = new ResultRetentionHandler(activeBuildID, getBuildResultDir(), getBuildResultPrefix());
    resultRetentionHandler.setCurrentCutOffDays(days);
    resultRetentionHandler.deleteExpired();
  }


  /**
   * Deletes log files from the arhive.
   *
   * @param stepLog StepLog to delete.
   */
  public void deleteLog(final StepLog stepLog) throws IOException {
    IoUtils.deleteFileHard(getDeleteablePath(getArchivedLogHome(stepLog)));
  }


  /**
   * Deletes result files from the archive. The step result also
   * gets deleted from the database.
   *
   * @param stepResult StepResult to delete.
   */
  public void deleteResult(final StepResult stepResult) throws IOException {
    IoUtils.deleteFileHard(getDeleteablePath(getArchivedResultHome(stepResult)));
    cm.deleteObject(stepResult);
  }


  /**
   * Packs (zips) expired build logs. Original log or a directory
   * gets deleted.
   * <p/>
   * Name of the file or a directory holding a logs changes by
   * adding a ".zip" suffix. For instance, if it was a log file
   * b1c101098905223569.log, it will become b1c101098905223569.log.zip
   */
  public void packExpiredBuildLogs() throws IOException {
    // delegate handling to LogPakingHandler
    final ArchiveCompressor logPackingHandler = new ArchiveCompressor(activeBuildID, getBuildLogDir(), getBuildLogPrefix());
    logPackingHandler.compressExpiredArhiveEntities();
  }


  /**
   * Returns fully qualifed path to an archived log.
   *
   * @param stepLog for which to return the path.
   * @return fully qualifed path to an archived log.
   */
  public File getArchivedLogHome(final StepLog stepLog) throws IOException {
    return fileNameToLogPath(stepLog.getArchiveFileName());
  }


  /**
   * Returns fully qualifed path to an archived result.
   *
   * @param stepResult for which to return the path.
   * @return fully qualifed path to an archived result.
   */
  public File getArchivedResultHome(final StepResult stepResult) throws IOException {
    return fileNameToResultPath(stepResult.getArchiveFileName());
  }


  /**
   * @return File main build result directory. The main result
   * directory is a directory where build runner stores
   * the build results to.
   */
  public File getResultDir() throws IOException {
    return getBuildResultDir();
  }


  /**
   * Returns build log prefix
   */
  public String getBuildLogPrefix() {
    return 'b' + Integer.toString(activeBuildID);
  }


  /**
   * Returns build result prefix
   */
  public String getBuildResultPrefix() {
    // REVIEWME:  the same methos as getBuildLogPrefix
    return 'b' + Integer.toString(activeBuildID);
  }


  /**
   * @return File main build log directory. The main log
   * directory is a directory where build runner wrtites
   * the buin build log to.
   */
  public File getBuildLogDir() throws IOException {
    IoUtils.createDirs(buildLogDir);
    return buildLogDir;
  }


  /**
   * Returns input stream for an archived log. This method is
   * valid for single-file logs.
   *
   * @param stepLog the descriptor of a step log to create an input stream of the archived log for.
   * @return null if log does not exist.
   * @throws IllegalArgumentException if given log is not a
   *                                  single-file log.
   */
  public InputStream getArchivedLogInputStream(final StepLog stepLog) throws IllegalArgumentException, IOException {
    final byte pathType = stepLog.getPathType();
    if (pathType == StepLog.PATH_TYPE_HTML_FILE
            || pathType == StepLog.PATH_TYPE_JUNIT_XML
            || pathType == StepLog.PATH_TYPE_NUNIT_XML
            || pathType == StepLog.PATH_TYPE_PHPUNIT_XML
            || pathType == StepLog.PATH_TYPE_GENERIC_TEST
            || pathType == StepLog.PATH_TYPE_CPPUNIT_XML
            || pathType == StepLog.PATH_TYPE_PMD_XML
            || pathType == StepLog.PATH_TYPE_CHECKSTYLE_XML
            || pathType == StepLog.PATH_TYPE_BOOST_XML
            || pathType == StepLog.PATH_TYPE_UNITTESTPP_XML
            || pathType == StepLog.PATH_TYPE_GOOGLETEST_XML
            || pathType == StepLog.PATH_TYPE_SQUISH_XML
            || pathType == StepLog.PATH_TYPE_TEXT_FILE) {
      // process
      final File archivedLogHome = getArchivedLogHome(stepLog);
      if (archivedLogHome.exists()) {
        // file exists return nomal IS
        if (!IoUtils.isFileUnder(archivedLogHome, getBuildLogDir())) {
          return null;
        }
        return new FileInputStream(archivedLogHome);
      } else {
        // didn't find, try packed.
        final File zippedLogHome = new File(archivedLogHome.getPath() + ".zip");
        if (zippedLogHome.exists()) {
          return new PackedLogInputStream(zippedLogHome, archivedLogHome.getName());
        } else {
          return null;
        }
      }
    } else {
      throw new IllegalArgumentException("Can not get log for type "
              + pathType + ". Log path:" + stepLog.getPath());
    }
  }


  /**
   * Returns input stream for an archived log. This method is
   * valid for multifile logs.
   *
   * @param stepLog the descriptor of a step log to create an input stream of the archived log for.
   * @return null if log does not exist.
   * @throws IllegalArgumentException if given log is not a
   *                                  multifile log.
   */
  public InputStream getArchivedLogInputStream(final StepLog stepLog, final String inArchiveFileName) throws IllegalArgumentException, IOException {
    final byte pathType = stepLog.getPathType();
    if (pathType == StepLog.PATH_TYPE_HTML_DIR
            || pathType == StepLog.PATH_TYPE_HTML_FILE
            || pathType == StepLog.PATH_TYPE_TEXT_DIR) {
      // process
      final File archivedLogHome = getArchivedLogHome(stepLog);
      return getArchivedEntityInputStream(archivedLogHome, inArchiveFileName);
    } else {
      throw new IllegalArgumentException("Can not get log for type "
              + pathType + ". Log path:" + stepLog.getPath());
    }
  }


  /**
   * Returns input stream for an archived build result. This
   * method is valid for single-file results.
   *
   * @param stepResult the descriptor of a step result to create an input stream for.
   * @return null if result does not exist.
   * @throws IllegalArgumentException if given log is not a
   *                                  single-file log.
   */
  public InputStream getArchivedResultInputStream(final StepResult stepResult) throws IllegalArgumentException, IOException {
    if (stepResult.getPathType() == StepResult.PATH_TYPE_SINGLE_FILE) {
      // get entry name
      final List entries = getArchivedResultEntries(stepResult);
      if (entries.isEmpty()) {
        return null;
      }
      if (entries.size() > 1) {
        throw new IllegalStateException("Unexpected number of result entries in the archive: " + entries.size());
      }
      // process
      return getArchivedEntityInputStream(getArchivedResultHome(stepResult), ((ArchiveEntry) entries.get(0)).getEntryName());
    } else {
      return throwIllegalPathType(stepResult);
    }
  }


  /**
   * Returns input stream for an archived build result. This
   * method is valid for multifile results.
   *
   * @param stepResult the descriptor of a step result to create an input stream for.
   * @return null if result does not exist.
   * @throws IllegalArgumentException if given log is not a
   *                                  multifile log.
   */
  public InputStream getArchivedResultInputStream(final StepResult stepResult, final String inArchiveFileName) throws IllegalArgumentException, IOException {
    return getArchivedEntityInputStream(getArchivedResultHome(stepResult), inArchiveFileName);
  }


  /**
   * Helper method.
   */
  private static InputStream throwIllegalPathType(final StepResult stepResult) {
    throw new IllegalArgumentException("Can not get result for type "
            + stepResult.getPathType() + ". Result path:" + stepResult.getPath());
  }


  /**
   * @param archivedEntityHome fully qualified path to arhive
   *                           entity. An example of the arhive entity is build log or
   *                           build result.
   * @param inArchiveFileName  the name of the archive file.
   * @return
   * @throws FileNotFoundException if the give archive entity doesn't exist.
   */
  private static InputStream getArchivedEntityInputStream(final File archivedEntityHome, final String inArchiveFileName) throws IOException {
    if (archivedEntityHome.exists()) {
      // file exists return nomal InputStream
      final File file = new File(archivedEntityHome, inArchiveFileName);
      if (!IoUtils.isFileUnder(file, archivedEntityHome)) {
        return null;
      }
      return new FileInputStream(file);
    } else {
      // didn't find, try packed.
      final File zippedLogHome = new File(archivedEntityHome.getPath() + ".zip");
      if (zippedLogHome.exists()) {
        return new PackedLogInputStream(zippedLogHome, inArchiveFileName);
      } else {
        return null;
      }
    }
  }


  /**
   * Returns list of relative file names constituting an archive
   * dir. The list is one level only. Valid only for dir-type
   * logs.
   *
   * @param stepLog the descriptor of a step log to get the list of entries for.
   * @return list of {@link ArchiveEntry} constituting an archive dir.
   */
  public List getArchivedLogEntries(final StepLog stepLog) throws IOException {
    return getArchiveEntries(getArchivedLogHome(stepLog));
  }

//  public List getArchivedLogEntries(StepLog stepLog) throws IOException {
//    if (stepLog.getPathType() == StepLog.PATH_TYPE_TEXT_DIR) {
//      return getArchiveEntries(getArchivedLogHome(stepLog));
//    } else {
//      throw new IllegalArgumentException("Can not get list of entries for type "
//        + stepLog.getPathType() + ". Log path:" + stepLog.getPath());
//    }
//  }


  /**
   * Returns list of relative file names constituting an archive
   * dir. The list is one level only. Valid only for dir-type
   * logs.
   *
   * @param stepResult for which to return the list of relative file names constituting an archive dir.
   * @return list of {@link ArchiveEntry} constituting an archive
   * dir, single level.
   */
  public List getArchivedResultEntries(final StepResult stepResult) throws IOException {
    return getArchiveEntries(getArchivedResultHome(stepResult));
  }


  /**
   * Lists entries for a given arichive path.
   *
   * @param archivePath the path to the archive to list the entries of.
   * @return String List of relative file names constituting an
   * archive dir, single level.
   */
  private static List<ArchiveEntry> getArchiveEntries(final File archivePath) throws IOException {
    final List<ArchiveEntry> result = new ArrayList<>(11);
    if (archivePath.exists()) {
      if (archivePath.isDirectory()) {
        // try file system archive dir.
        final File baseDir = archivePath.getCanonicalFile();
        final int baseDirLength = baseDir.getCanonicalPath().length();
        IoUtils.traverseDir(baseDir, new DirectoryTraverserCallback() {
          public boolean callback(final File file) throws IOException {
            if (file.isFile()) {
              result.add(new ArchiveEntryImpl(file.getCanonicalPath().substring(baseDirLength + 1), file.length()));
            }
            return true;
          }
        });
      } else {
        // this is a file, add empty "self"
        result.add(new ArchiveEntryImpl("", archivePath.length()));
      }
    } else {
      // didn't find, try packed.
      final File zippedLogHome = new File(archivePath.getPath() + ".zip");
      if (zippedLogHome.exists()) {
        ZipFile zipFile = null;
        try {
          zipFile = new ZipFile(zippedLogHome);
          for (final Enumeration entries = zipFile.entries(); entries.hasMoreElements(); ) {
            final ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            result.add(new ArchiveEntryImpl(zipEntry.getName(), zipEntry.getSize()));
          }
        } finally {
          IoUtils.closeHard(zipFile);
        }
      }
    }
    return result;
  }


  /**
   * Returns arhived item's path, either direct or compressed.
   *
   * @param archivedLogHome the path to the archive home.
   * @return path or null if path doesn't exist
   * @see #deleteLog
   * @see #deleteResult
   */
  private static File getDeleteablePath(final File archivedLogHome) {

    // check if direct home exists.
    if (archivedLogHome.exists()) {
      return archivedLogHome;
    }

    // didn't find, try compressed.
    final File zippedLogHome = new File(archivedLogHome.getPath() + ".zip");
    if (zippedLogHome.exists()) {
      return zippedLogHome;
    }

    // could not find anything
    return null;
  }


  /**
   * Returns list of log lines
   *
   * @param stepRun the step run to return the list of log lines for.
   */
  public List getLogWindowLines(final StepRun stepRun) {
    final List<String> result = new ArrayList<>(200);
    BufferedReader br = null;
    try {
      // get log
      final List logs = cm.getStepLogs(stepRun, StepLog.TYPE_WINDOW);
      if (logs == null || logs.isEmpty()) {
        return result; // return empty list
      }
      // get input stream
      final StepLog stepLog = (StepLog) logs.get(0); // use first one, normally we don't get more them one.
      final InputStream archivedLogInputStream = getArchivedLogInputStream(stepLog);
      if (archivedLogInputStream == null) {
        return result;  // return empty list
      }
      br = new BufferedReader(new InputStreamReader(archivedLogInputStream), 1024);
      String line = br.readLine();
      while (line != null) {
        result.add(line);
        line = br.readLine();
      }
    } catch (final IOException e) {
      final Error error = new Error("Error while retrieving log window lines");
      error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
      error.setStepName(stepRun.getName());
      error.setDetails(e);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
    } finally {
      IoUtils.closeHard(br);
    }
    return result;
  }


  /**
   * Deletes expired build logs it build settings explicitelly
   * allow this. An expiration date is selected as a maximum of
   * build settings and system-wide minumim settings.
   *
   * @noinspection ControlFlowStatementWithoutBraces
   */
  public void deleteExpiredBuildResults() throws IOException {

    final boolean deleted = cm.getActiveBuild(activeBuildID).isDeleted();
    if (!deleted && !cm.getBuildAttributeValue(activeBuildID, BuildConfigAttribute.ENABLE_AUTOMATIC_DELETING_OLD_BUILD_RESULTS, BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED)) {
      return;
    }

    final long startTimeMillis = System.currentTimeMillis();

    // Prepare
    final int buildConfigurationCutOff = cm.getBuildAttributeValue(activeBuildID, BuildConfigAttribute.RESULT_RETENTION_DAYS, new Integer(SystemProperty.DEFAULT_MINIMUM_RESULTS_RETENTION));
    final int minimumSystemCutOff = SystemConfigurationManagerFactory.getManager().getSystemPropertyValue(SystemProperty.MINIMUM_RESULTS_RETENTION, SystemProperty.DEFAULT_MINIMUM_RESULTS_RETENTION);
    final int days = deleted ? 1 : Math.max(buildConfigurationCutOff, minimumSystemCutOff);

    // Delete
    deleteExpiredBuildResults(days);

    // Report
    if (SystemConfigurationManagerFactory.getManager().isLoggingCleanupTimingEnabled()) {

      final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
      final String buildName = cm.getBuildConfiguration(activeBuildID).getBuildName();
      final Error message = new Error("Finished deleting expired results for build " + buildName + ". Time to delete was " + ((System.currentTimeMillis() - startTimeMillis) / 1000) + " seconds.");
      message.setErrorLevel(Error.ERROR_LEVEL_INFO);
      message.setBuildID(activeBuildID);
      message.setSendEmail(false);
      errorManager.reportSystemError(message);
    }
  }


  public String toString() {
    return "ArchiveManagerImpl{" +
            "buildLogDir=" + buildLogDir +
            ", buildResultDir=" + buildResultDir +
            ", activeBuildID=" + activeBuildID +
            ", cm=" + cm +
            '}';
  }


  private File getBuildResultDir() throws IOException {
    IoUtils.createDirs(buildResultDir);
    return buildResultDir;
  }
}
