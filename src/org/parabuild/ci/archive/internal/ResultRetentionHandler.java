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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.util.IoUtils;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

/**
 * This class is responsible for deleting "expired" build
 * results.
 */
public final class ResultRetentionHandler extends AbstractArchiveRetentionHandler {


  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(AbstractArchiveRetentionHandler.class); // NOPMD


  /**
   * Constructor.
   *
   * @param activeBuildID  - int ID of a current build
   *                       configuration.
   * @param buildLogDir    - Directory with logs.
   * @param buildLogPrefix - Prefix a file should have.
   */
  public ResultRetentionHandler(final int activeBuildID, final File buildLogDir, final String buildLogPrefix) {
    super(activeBuildID, buildLogDir, buildLogPrefix);
  }


  /**
   * @return cut off days stored in persistent build
   * configuration or null if not configured.
   */
  protected Integer getConfiguredCutOffDays() {
    return ConfigurationManager.getInstance().getBuildAttributeValue(getActiveBuildID(), BuildConfigAttribute.RESULT_RETENTION_DAYS, (Integer) null);
  }


  public final void deleteExpired() {

    //noinspection ControlFlowStatementWithoutBraces
    if (LOG.isDebugEnabled()) LOG.debug("Deleting expired, cutOffTimeMillis: " + getCutOffTimeMillis()); // NOPMD

    // Validate
    if (getCutOffTimeMillis() == null) {
      reportCutOffTimeWasNotSet();
      return; // not set
    }

    // Get cut-off time as a date object
    final Date cutOffTime = new Date(getCutOffTimeMillis());

    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {

        final Query query = session.createQuery("select sres from BuildRun br, StepRun srun, StepResult sres" +
                " where br.activeBuildID = ? " +
                " and br.finishedAt <= ? " +
                "   and srun.buildRunID = br.buildRunID " +
                "   and sres.stepRunID = srun.ID  " +
                "   and sres.pinned = 'N' " +
                "   and sres.found = 'Y' ");
        query.setInteger(0, getActiveBuildID());
        query.setTimestamp(1, cutOffTime);
        final Iterator iterator = query.iterate();
        while (iterator.hasNext()) {

          final StepResult stepResult = (StepResult) iterator.next();
          final String fileName = stepResult.getArchiveFileName();
          final File buildLogDir = getBuildLogDir();
          final File fileToDelete = new File(buildLogDir, fileName);
          if (fileToDelete.exists()) {

            LOG.debug("Deleting fileToDelete: " + fileToDelete);
            IoUtils.deleteFileHard(fileToDelete);
          } else {

            // Possibly was zipped
            final File zippedFileToDelete = new File(buildLogDir, fileName + ArchiveCompressor.ZIP_SUFFIX);
            if (zippedFileToDelete.exists()) {

              IoUtils.deleteFileHard(zippedFileToDelete);
            }
          }

          // Mark as deleted
          stepResult.setFound(false);
        }
        return null;
      }
    });
  }


  @Override
  public String toString() {
    return "ResultRetentionHandler{} " + super.toString();
  }
}
