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
package org.parabuild.ci.build.result;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.archive.ArchiveEntry;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.ResultConfigProperty;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Handles directory with build result files.
 */
public final class DirResultHandler extends AbstractResultHandler {

  private static final Log LOG = LogFactory.getLog(DirResultHandler.class); // NOPMD


  /**
   * Constructor
   */
  public DirResultHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                          final String projectHome, final ResultConfig resultConfig, final int stepRunID) {
    // call super
    super(agent, buildRunConfig, projectHome, resultConfig, stepRunID, true);
  }


  /**
   * @return byte result type that this handler can process.
   */
  protected byte resultType() {
    return ResultConfig.RESULT_TYPE_DIR;
  }


  /**
   * Concrete processing for single text file.
   *
   * @param builderTimeStamp
   * @param fullyQualifiedResultPath
   * @param resolvedResultPath       @throws IOException
   */
  protected void processResult(final long builderTimeStamp, final String fullyQualifiedResultPath,
                               final String resolvedResultPath) throws IOException, AgentFailureException {

    // check if it's not a directory
    if (!super.agent.pathIsDirectory(fullyQualifiedResultPath)) {
      reportResultPathIsNotDirectory(fullyQualifiedResultPath);
      return;
    }

    //
    // check if anything needs archiving
    //

    if (LOG.isDebugEnabled()) {
      LOG.debug("checking if results are already archived");
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("builderTimeStamp: " + builderTimeStamp);
    }

    // NOTE: vimeshev - 2007-01-25 - any file newer than a
    // time stamp will trigger an activation attempt
    final List descriptorsPossiblyNeededArchiving = new ArrayList(11);
    final String[] resultDirectoryFiles = agent.listFilesInDirectory(fullyQualifiedResultPath, getExtensions());
    if (LOG.isDebugEnabled()) {
      LOG.debug("resultDirectoryFiles in results directory length: " + resultDirectoryFiles.length);
    }
    for (int j = 0; j < resultDirectoryFiles.length; j++) {

      final String resultDirectoryFile = resultDirectoryFiles[j];

      // discard directories

      if (agent.pathIsDirectory(resultDirectoryFile)) {
        continue;
      }

      // collect descriptors for resultDirectoryFiles possibly needed archiving

      final RemoteFileDescriptor resultDirectoryFileDescriptor = agent.getFileDescriptor(resultDirectoryFile);
      if (resultConfig.isIgnoreTimestamp() || resultDirectoryFileDescriptor.lastModified() > builderTimeStamp) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("possibly needs archiving: " + resultDirectoryFile);
        }
        descriptorsPossiblyNeededArchiving.add(resultDirectoryFileDescriptor);
      }
    }

    //
    // check if we have to do anything
    //

    if (descriptorsPossiblyNeededArchiving.isEmpty()) {
      return;
    }

    //
    // compose a map of resultDirectoryFiles already in the archive for the same type
    //

    final Map archivedFilesMap = new HashMap(11);
    final StepRun stepRun = cm.getStepRun(stepRunID); // REVIEWME: do we really need this operation? may be we should just store StepRun object in AbstractResultHandler?
    final List archivedResults = cm.findBuildRunResults(stepRun.getBuildRunID(), resultType(), resolvedResultPath);
    for (final Iterator i = archivedResults.iterator(); i.hasNext(); ) {
      final StepResult stepResult = (StepResult) i.next();

      // enumerate all archived resultDirectoryFiles

      final File archivedResultHome = archiveManager.getArchivedResultHome(stepResult);
      if (!archivedResultHome.exists()) {
        continue;
      }

      // get relative archived file names

      final List archivedResultEntries = archiveManager.getArchivedResultEntries(stepResult);
      if (archivedResultEntries.isEmpty()) {
        continue;
      }

      // go through the list and put resultDirectoryFiles to the map

      for (int j = 0; j < archivedResultEntries.size(); j++) {
        final String archivedFileNameOnly = ((ArchiveEntry) archivedResultEntries.get(j)).getEntryName();
        final File archivedFile = new File(archivedResultHome, archivedFileNameOnly);
        archivedFilesMap.put(archivedFileNameOnly, archivedFile);
      }
    }

    //
    // copy dir content needed archiving
    //

    // go ever list of files possible needed archiving. check if already archived

    final String archiveFileName = archiveManager.makeNewResultFileNameOnly(); // just a file name, w/o path
    final File archiveDir = archiveManager.fileNameToResultPath(archiveFileName);
    if (LOG.isDebugEnabled()) {
      LOG.debug("archiveFileName: " + archiveFileName);
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("resultDirectoryFiles.length: " + resultDirectoryFiles.length);
    }
    boolean archiveDirCreated = false;
    for (int j = 0; j < descriptorsPossiblyNeededArchiving.size(); j++) {
      final RemoteFileDescriptor descriptor = (RemoteFileDescriptor) descriptorsPossiblyNeededArchiving.get(j);
      if (LOG.isDebugEnabled()) {
        LOG.debug("source: " + descriptor);
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("agent.getFileDescriptor(source).lastModified(): " + descriptor.lastModified());
      }


      // check if a particular file is already archived

      // NOTE: simeshev - 2007-01-25 - we do this because
      // file time stamps are rounded to seconds.

      final String source = descriptor.getCanonicalPath();
      final String fileNameOnly = agent.getFileName(source);
      final File archivedFile = (File) archivedFilesMap.get(fileNameOnly);
      if (archivedFile != null && archivedFile.lastModified() >= descriptor.lastModified()) {
        continue;
      }

      // archive a file

      if (!archiveDirCreated) {
        archiveDir.mkdirs(); // create destination directory
        archiveDirCreated = true;
      }
      final File destination = new File(archiveDir, fileNameOnly); // NOPMD (AvoidInstantiatingObjectsInLoops)
      agent.readFile(source, destination);
    }

    //
    // save result info in the db if necessary
    //

    if (archiveDir.exists() && archiveDir.list().length > 0) {
      final StepResult stepResult = new StepResult();
      stepResult.setStepRunID(super.stepRunID);
      stepResult.setDescription(super.resultConfig.getDescription());
      stepResult.setPath(resolvedResultPath);
      stepResult.setArchiveFileName(archiveFileName);
      stepResult.setPathType(StepResult.PATH_TYPE_DIR); // path type is file
      stepResult.setPinned(pinResult);
      stepResult.setFound(true);
      if (LOG.isDebugEnabled()) {
        LOG.debug("saved stepResult: " + stepResult);
      }
      cm.saveObject(stepResult);
      searchManager.index(stepResult);

      // auto-publish if necessary
      publish(stepResult);
    }
  }


  /**
   * Implementation of the isResultAlreadyArchived.
   *
   * @param archivedBuildRunResults  List of already stored and archived
   *                                 results. It is guaranteed that the list is non-zero size.
   * @param builderTimeStamp
   * @param fullyQualifiedResultPath
   * @return true if result was already archived.
   * @see AbstractResultHandler#isResultAlreadyArchived
   */
  protected boolean isResultAlreadyArchived(final List archivedBuildRunResults, final long builderTimeStamp,
                                            final String fullyQualifiedResultPath) {
    return false; // leave to the processor
  }


  /**
   * @return configured build results file extensions or zero
   *         length string if no extensions configured.
   */
  private String getExtensions() {
    final ResultConfigProperty rep = cm.getResultConfigProperty(resultConfig.getID(), ResultConfigProperty.ATTR_FILE_EXTENSIONS);
    if (rep == null || StringUtils.isBlank(rep.getValue())) {
      return ""; // no extensions
    }
    return rep.getValue();
  }
}
