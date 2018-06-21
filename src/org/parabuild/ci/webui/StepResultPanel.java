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
package org.parabuild.ci.webui;

import org.apache.commons.io.FileUtils;
import org.parabuild.ci.archive.ArchiveEntry;
import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Label;
import viewtier.ui.Panel;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * This panel contains links files for a given step result.
 */
public final class StepResultPanel extends Panel {

  /**
   * Sets panel's content. If content has been set already it is discarded.
   *
   * @param activeBuildID
   * @param am
   * @param stepResult
   * @param showDescription
   */
  public void setStepResult(final int activeBuildID, final ArchiveManager am,
                            final StepResult stepResult, final boolean showDescription) {

    //clear
    clear();

    if (showDescription) {
      // add desciption
      add(new CommonLabel(stepResult.getDescription() + ':'));
    }

    final byte pathType = stepResult.getPathType();
    if (pathType == StepResult.PATH_TYPE_DIR || pathType == StepResult.PATH_TYPE_SINGLE_FILE) {
      // show file list
      try {
        final List entries = am.getArchivedResultEntries(stepResult);
        for (final Iterator j = entries.iterator(); j.hasNext();) {
          final ArchiveEntry archiveEntry = (ArchiveEntry) j.next();
          final CommonLink lnkResult = makeResultLink(stepResult, archiveEntry.getEntryName(), activeBuildID);
          final CommonLabel lbByteCount = new CommonLabel(FileUtils.byteCountToDisplaySize(archiveEntry.getLength()));
          add(new CommonFlow(lnkResult, new Label("  "), lbByteCount));
        }
      } catch (final IOException e) {
        reportArchiveReadingError(e);
      }
    } else if (pathType == StepResult.PATH_TYPE_EXTERNAL_URL) {
      // show URL stored in the result path
      final CommonLink lnk = new CommonLink(stepResult.getPath(), stepResult.getPath());
      lnk.setTarget("_blank");
      add(lnk);
    } else {
      // unknown type
      reportUnknownPathType(pathType, activeBuildID);
    }
  }


  private static CommonLink makeResultLink(final StepResult stepResult, final String entryName, final int activeBuildID) {
    final String url = WebuiUtils.makeResultURLPathInfo(activeBuildID, stepResult.getID(), entryName);
    final CommonLink lnk = new CommonLink(entryName, url);
    lnk.setTarget("_blank");
    return lnk;
  }


  private void reportArchiveReadingError(final IOException e) {
    final Error error = new Error("Unexcpected IO error while reading arhive: " + StringUtils.toString(e));
    error.setSendEmail(false);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSubsystemName(Error.ERROR_SUSBSYSTEM_WEBUI);
    error.setDetails(e);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  private void reportUnknownPathType(final byte pathType, final int activeBuildID) {
    final Error error = new Error("Unknown result path type: " + Integer.toString(pathType));
    error.setSendEmail(false);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSubsystemName(Error.ERROR_SUSBSYSTEM_WEBUI);
    error.setBuildID(activeBuildID);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }
}
