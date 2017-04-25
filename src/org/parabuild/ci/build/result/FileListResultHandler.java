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
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;


/**
 * Handles single-file customer result.
 */
public final class FileListResultHandler extends AbstractResultHandler {

  private static final Log LOG = LogFactory.getLog(FileListResultHandler.class); // NOPMD


  /**
   * Constructor
   */
  public FileListResultHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                               final String projectHome, final ResultConfig resultConfig, final int stepRunID) {
    super(agent, buildRunConfig, projectHome, resultConfig, stepRunID, true);
  }


  /**
   * @return byte result type that this handler can process.
   */
  protected byte resultType() {
    return ResultConfig.RESULT_TYPE_FILE_LIST;
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

    // check if it's a file
    if (!super.agent.pathIsFile(fullyQualifiedResultPath)) {
      reportResultPathIsNotFile(fullyQualifiedResultPath);
      return;
    }

    // get result to archive copy - go over list of files in the directory
    final String archiveFileName = archiveManager.makeNewResultFileNameOnly(); // just a file name, w/o path
    final File archiveDir = archiveManager.fileNameToResultPath(archiveFileName);

    // create destination directory
    //noinspection ResultOfMethodCallIgnored
    archiveDir.mkdirs();

    // copy into archive
    final String fileNameOnly = agent.getFileName(fullyQualifiedResultPath);
    final File readInTo = new File(archiveDir, fileNameOnly);
    agent.readFile(fullyQualifiedResultPath, readInTo); // this also sets archive's  time stamp to the result's

    //if (log.isDebugEnabled()) log.debug("readInTo = " + readInTo);
    //if (log.isDebugEnabled()) log.debug("readInTo.length() " + readInTo.length());

    // save result info in the db if necessary
    if (readInTo.exists()) {
      // save into db
      final StepResult stepResult = new StepResult();
      stepResult.setStepRunID(super.stepRunID);
      stepResult.setDescription(super.resultConfig.getDescription());
      stepResult.setPath(resolvedResultPath);
      stepResult.setArchiveFileName(archiveFileName);
      stepResult.setPathType(StepResult.PATH_TYPE_SINGLE_FILE); // path type is file
      stepResult.setPinned(pinResult);
      stepResult.setFound(true);
      cm.saveObject(stepResult);
      //if (log.isDebugEnabled()) log.debug("saved: " + stepResult);

      // index
      searchManager.index(stepResult);

      // auto-publish if necessary
      publish(stepResult);
    }
  }


  /**
   * Implementation of the isResultAlreadyArchived.
   *
   * @param archivedBuildRunResults  List of already stored and archived
   *                                 results.
   * @param builderTimeStamp
   * @param fullyQualifiedResultPath
   * @return true if result was already arhived.
   * @see org.parabuild.ci.build.result.AbstractResultHandler#isResultAlreadyArchived
   */
  protected boolean isResultAlreadyArchived(final List archivedBuildRunResults, final long builderTimeStamp,
                                            final String fullyQualifiedResultPath) throws IOException, AgentFailureException {

    // get descriptor
    final RemoteFileDescriptor resultDescriptor = agent.getFileDescriptor(fullyQualifiedResultPath);
    final long resultModified = resultDescriptor.lastModified();
    // Check if result was created before the build started (left
    // from previous build runs.
    if (!resultConfig.isIgnoreTimestamp() && resultModified < builderTimeStamp) {
      return true;
    }
    //if (log.isDebugEnabled()) log.debug("resultFileDescriptor: " + resultFileDescriptor);

    // there are results, find if time stamps and sizes are different.
    for (final Iterator i = archivedBuildRunResults.iterator(); i.hasNext(); ) {
      final StepResult stepResult = (StepResult) i.next();

      // for us it is the arhived file result itself
      final File archivedResultHome = archiveManager.getArchivedResultHome(stepResult);
      if (!archivedResultHome.exists()) {
        LOG.warn("Expected the arhived file \"" + archivedResultHome + "\" to exist, but it did not. " + stepResult.toString());
        continue;
      }

      // get relative arthived file name
      final List archivedResultEntries = archiveManager.getArchivedResultEntries(stepResult);
      if (archivedResultEntries.size() != 1) {
        LOG.warn("Expected the arhived entry list size 1 but is was" + archivedResultEntries.size());
        continue;
      }

      final File archivedResult = new File(archivedResultHome, ((ArchiveEntry) archivedResultEntries.get(0)).getEntryName());

      // compare time stamps and sizes
      //if (log.isDebugEnabled()) log.debug("archivedResultHome) = " + archivedResult);
      //if (log.isDebugEnabled()) log.debug("archivedResultHome.length() = " + archivedResult.length());
      //if (log.isDebugEnabled()) log.debug("archivedResultHome.lastModified() = " + archivedResult.lastModified());
      if (resultDescriptor.length() == archivedResult.length()
              && resultModified == archivedResult.lastModified()) {
        return true; // found in arhive
      }
    }

    // not found in archive
    return false;
  }
}
