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

import net.sf.hibernate.Query;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.StepLog;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

/**
 * This class is responsible for deleting "expired" build logs.
 */
public final class LogRetentionHandler extends AbstractArchiveRetentionHandler {

  private final ConfigurationManager cm;


  /**
   * Constructor.
   *
   * @param activeBuildID  - int ID of a current build
   *                       configuration.
   * @param buildLogDir    - Directory with logs.
   * @param buildLogPrefix - Prefix a file should have.
   */
  public LogRetentionHandler(final int activeBuildID, final File buildLogDir, final String buildLogPrefix) {
    super(activeBuildID, buildLogDir, buildLogPrefix);
    cm = ConfigurationManager.getInstance();
  }


  /**
   * @return cut off days stored in persistant build
   *         configuration or null if not configured.
   */
  protected Integer getConfiguredCutOffDays() {
    return cm.getBuildAttributeValue(getActiveBuildID(), BuildConfigAttribute.LOG_RETENTION_DAYS, (Integer) null);
  }


  public final void deleteExpired() throws IllegalStateException {

    // Validate
    if (getCutOffTimeMillis() == null) {
      reportCutOffTimeWasNotSet();
      return; // not set
    }

    // Get cut-off time as a date object
    final Date cutOffTime = new Date(getCutOffTimeMillis().longValue());

    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {

        final Query query = session.createQuery("select slog from BuildRun br, StepRun srun, StepLog slog" +
                " where br.activeBuildID = ? " +
                " and br.finishedAt <= ? " +
                "   and srun.buildRunID = br.buildRunID " +
                "   and slog.stepRunID = srun.ID  " +
                "   and slog.found = 1 ");
        query.setInteger(0, getActiveBuildID());
        query.setTimestamp(1, cutOffTime);
        final Iterator iterator = query.iterate();
        while (iterator.hasNext()) {

          final StepLog stepLog = (StepLog) iterator.next();
          final String fileName = stepLog.getArchiveFileName();
          final File buildLogDir = getBuildLogDir();
          final File fileToDelete = new File(buildLogDir, fileName);
          if (fileToDelete.exists()) {

            IoUtils.deleteFileHard(fileToDelete);
          } else {

            // Possibly was zipped
            final File zippedFileToDelete = new File(buildLogDir, fileName + ArchiveCompressor.ZIP_SUFFIX);
            if (zippedFileToDelete.exists()) {

              IoUtils.deleteFileHard(zippedFileToDelete);
            }
          }

          // Mark as deleted
          stepLog.setFound((byte) 0);
        }
        return null;
      }
    });
  }
}
