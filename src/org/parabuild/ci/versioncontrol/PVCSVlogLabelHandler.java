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
package org.parabuild.ci.versioncontrol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.object.Change;

/**
 * This state machine is responsible for collecting PVCS
 * revsions at given time and passing them to a
 * PVCSLabelScriptCreator.
 */
final class PVCSVlogLabelHandler implements PVCSVlogHandler {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(PVCSVlogLabelHandler.class); // NOPMD
  private final PVCSVlogCommandParameters parameters;
  private final PVCSLabelCreator labelCreator;
  private final int fileBlockSize;

  private String previousPath = null;
  private String previousRevision = null;
  private List collectedRevisionBlock = null;


  /**
   * @param parameters
   * @param labelCreator
   * @param fileBlockSize
   */
  public PVCSVlogLabelHandler(final PVCSVlogCommandParameters parameters,
                              final PVCSLabelCreator labelCreator,
                              final int fileBlockSize) {

    this.parameters = parameters;
    this.labelCreator = labelCreator;
    this.fileBlockSize = fileBlockSize;
    this.collectedRevisionBlock = new ArrayList(fileBlockSize);
  }


  /**
   * This method is called before the handle is called first
   * time.
   */
  public void beforeHandle() {

  }


  /**
   * This method is called when a revsion is found in a
   * change log. It is guaranteed that it is called only
   * once for a single revesion.
   */
  public void handle(final Date changeDate, final StringBuffer revisionDescription,
                     final String owner, final String branch,
                     final String filePath, final String revision,
                     final byte changeType) throws IOException, CommandStoppedException, AgentFailureException {

    // file path
    final String fullFilePath = filePath.replace('\\', '/');
//    if (log.isDebugEnabled()) log.debug("filePath: " + filePath);
//    if (log.isDebugEnabled()) log.debug("previousPath:     " + previousPath);
//    if (log.isDebugEnabled()) log.debug("previousRevision: " + previousRevision);
//    if (log.isDebugEnabled()) log.debug("fullFilePath:         " + fullFilePath);
//    if (log.isDebugEnabled()) log.debug("changeDate:       " + changeDate);
//    if (log.isDebugEnabled()) log.debug("parameters.endDate: " + parameters.getEndDate());
//    if (log.isDebugEnabled()) log.debug("");


    // NOTE: vimeshev - 2006-02-04 - we use the fact that revisions
    // are listed in the log in reverse date and revision order.
    // The first revision we encounter that has a the date that is
    // equal or older than label date, is the revision that we are
    // looking for.


    // check if delete - it is impossible
    // to label deleted file in PVCS
    if (changeType == Change.TYPE_DELETED) {
      return;
    }

    // is it still current file?
    if (fullFilePath.equals(previousPath)) {
      // it is current
      processRevision(changeDate, revision);
    } else {
      // no, it is new
      if (previousPath != null && previousRevision != null) {
        // store in the list
        final PVCSRevision pvcsRevision = new PVCSRevision(previousPath, previousRevision);
//        if (log.isDebugEnabled()) log.debug("adding pvcsRevision: " + pvcsRevision);
        collectedRevisionBlock.add(pvcsRevision);
        // empty steate
        previousPath = null;
        previousRevision = null;
        // call creator if collected enough
        if (collectedRevisionBlock.size() >= fileBlockSize) {
          labelCreator.label(collectedRevisionBlock);
          // empty list
          collectedRevisionBlock = new ArrayList(fileBlockSize);
        }
      }

      processRevision(changeDate, revision);

      previousPath = fullFilePath;
    }
  }


  /**
   * This method is called fater the handle is called last
   * time.
   */
  public void afterHandle() throws IOException, CommandStoppedException, AgentFailureException {
    // handle last un-processed if any
    if (previousPath != null && previousRevision != null) {
      // store in the list
      final PVCSRevision pvcsRevision = new PVCSRevision(previousPath, previousRevision);
//      if (log.isDebugEnabled()) log.debug("adding pvcsRevision: " + pvcsRevision);
      collectedRevisionBlock.add(pvcsRevision);
    }

    if (!collectedRevisionBlock.isEmpty()) {
      labelCreator.label(collectedRevisionBlock);
    }
  }


  private void processRevision(final Date changeDate, final String revision) {
    // check date
    if (changeDate.compareTo(parameters.getEndDate()) <= 0) {
      // before or equal label date
      if (previousRevision == null) {
        previousRevision = revision; // first revsions that's date is lesser or equal labels's date
      }
    }
  }
}
