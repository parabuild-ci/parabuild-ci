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
package org.parabuild.ci.build;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.archive.ArchiveEntry;
import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;
import org.parabuild.ci.remote.services.ModifiedFileList;

/**
 * Reusable helper class to find if a given log path is already
 * archived.
 */
public final class SimpleFileArchivedLogFinder {

  private static final Log log = LogFactory.getLog(SimpleFileArchivedLogFinder.class);

  private final Agent agent;
  private final ArchiveManager archiveManager;
  private boolean ignoreTimeStamp = false;


  public SimpleFileArchivedLogFinder(final Agent agent, final ArchiveManager archiveManager) {
    this.agent = agent;
    this.archiveManager = archiveManager;
  }


  /**
   * Tells this finder to ignore log path time stamp.
   *
   * @param ignoreTimeStamp
   */
  public void setIgnoreTimeStamp(final boolean ignoreTimeStamp) {
    this.ignoreTimeStamp = ignoreTimeStamp;
  }


  /**
   * @param archivedLogs     List of StepLog objects that already
   *                         made to the archive.
   * @param targetPath       file for which to find if it is already in
   *                         the archive.
   * @param builderTimeStamp
   * @return true if log was already archived.
   */
  public boolean isAlreadyArchived(final List archivedLogs, final String targetPath, final long builderTimeStamp) throws IOException, AgentFailureException {
    return isAlreadyArchived(archivedLogs, targetPath, false, builderTimeStamp);
  }


  /**
   * @param archivedLogs       List of StepLog objects that already
   *                           made to the archive.
   * @param targetPath         file for which to find if it is already in
   *                           the archive.
   * @param ignoreTargetLength if true, length of the object in
   *                           the archive won't be compared. ignoreTargetLength is useful
   *                           if a target is a directory and the only thing we care it's
   *                           modification time.
   * @param builderTimeStamp
   * @return true if log was already archived.
   */
  public boolean isAlreadyArchived(final List archivedLogs, final String targetPath, final boolean ignoreTargetLength, final long builderTimeStamp) throws IOException, AgentFailureException {

    // get descriptor
    final RemoteFileDescriptor targetDescriptor = agent.getFileDescriptor(targetPath);
    //if (log.isDebugEnabled()) log.debug("resultFileDescriptor: " + resultFileDescriptor);
    final long targetModified = targetDescriptor.lastModified();
    // Check if result was created before the build started (left
    // from previous build runs.
    if (log.isDebugEnabled()) log.debug("targetModified: " + targetModified);
    if (log.isDebugEnabled()) log.debug("builderTimeStamp: " + builderTimeStamp);
    if (log.isDebugEnabled()) log.debug("targetModified - builderTimeStamp: " + (targetModified - builderTimeStamp));

    // Handle file versus directory
    if (targetDescriptor.isDirectory()) {
      final ModifiedFileList modifiedFileList = agent.getModifiedFiles(targetDescriptor.getCanonicalPath(), builderTimeStamp - 1, 1);
      if (modifiedFileList.getFiles().isEmpty()) {
        return true;
      }
    } else {
      // File and the "rest"
      if (!ignoreTimeStamp && targetModified < builderTimeStamp) {
        return true;
      }
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

      // get relative archived file name
      if (log.isDebugEnabled()) log.debug("stepLog: " + stepLog);
      if (log.isDebugEnabled()) log.debug("archivedLogHome: " + archivedLogHome);
      if (log.isDebugEnabled()) log.debug("archivedLogHome.length(): " + archivedLogHome.length());
      final List archivedLogEntries = archiveManager.getArchivedLogEntries(stepLog);
      if (archivedLogEntries.size() != 1) {
        log.warn("Expected the archived entry list size 1 but is was" + archivedLogEntries.size());
        continue;
      }

      // compare time stamps and sizes
      final File archivedLog = new File(archivedLogHome, ((ArchiveEntry) archivedLogEntries.get(0)).getEntryName());
      if (log.isDebugEnabled()) log.debug("archivedLog: " + archivedLog);
      if (log.isDebugEnabled()) log.debug("targetDescriptor.length(): " + targetDescriptor.length());
      if (log.isDebugEnabled()) log.debug("archivedLog.length(): " + archivedLog.length());
      if (log.isDebugEnabled()) log.debug("targetDescriptor.lastModified() : " + targetModified);
      if (log.isDebugEnabled()) log.debug("archivedLog.lastModified(): " + archivedLog.lastModified());
      boolean differs = !ignoreTargetLength && targetDescriptor.length() != archivedLog.length();
      differs |= targetModified != archivedLog.lastModified();
      if (!differs) {
        if (log.isDebugEnabled()) log.debug("found");
        return true; // found in archive
      }
    }

    // not found in archive
    if (log.isDebugEnabled()) log.debug("not found");
    return false;
  }


  public String toString() {
    return "SimpleFileArchivedLogFinder{" +
            "agent=" + agent +
            ", archiveManager=" + archiveManager +
            '}';
  }
}