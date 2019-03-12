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
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;

import java.io.File;

/**
 * GoF Strategy class to handle cleanup of outdated build archive
 * content.
 *
 * @see LogRetentionHandler
 */
public abstract class AbstractArchiveRetentionHandler {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(AbstractArchiveRetentionHandler.class); // NOPMD

  private Long cutOffTimeMillis = null;
  private final String buildLogPrefix;
  private final File buildLogDir;
  private final int activeBuildID;


  /**
   * Constructor.
   *
   * @param activeBuildID  - int ID of a current build
   *                       configuration.
   * @param buildLogDir    - Directory with logs.
   * @param buildLogPrefix - Prefix a file should have.
   * @throws IllegalStateException if activeBuildID is not an
   *                               active build ID
   */
  AbstractArchiveRetentionHandler(final int activeBuildID, final File buildLogDir, final String buildLogPrefix) {
    this.buildLogDir = (File) ArgumentValidator.validateArgumentNotNull(buildLogDir, "build log archive directory");
    this.buildLogPrefix = ArgumentValidator.validateArgumentNotBlank(buildLogPrefix, "build log archive prefix");
    this.activeBuildID = ArgumentValidator.validateBuildIDInitialized(activeBuildID);

    // paranoid validation - archive rentention handler always works
    // using current build config
    ConfigurationManager.getInstance().validateIsActiveBuildID(activeBuildID);
  }


  /**
   * Deletes build logs that are not to be retained.
   *
   * @throws IllegalStateException if cut-off time is not set.
   */
  public abstract void deleteExpired() throws IllegalStateException;


  /**
   * Helper method to make a archive file name stored in step
   * result from a file file obtained from a list of archived
   * files.
   */
  public static String makeRecordedFileName(final String name) {
    return name.endsWith(ArchiveCompressor.ZIP_SUFFIX) ? name.substring(0, name.length() - ArchiveCompressor.ZIP_SUFFIX_LENGTH) : name;
  }


  final void reportCutOffTimeWasNotSet() {
    final ErrorManager em = ErrorManagerFactory.getErrorManager();
    final Error error = new Error("Archive clean up was not performed - cut off time to clean up archive was not set.");
    error.setBuildID(activeBuildID);
    error.setSendEmail(false);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    em.reportSystemError(error);
  }


  /**
   * Sets current cutoff time from build configuration.
   */
  public final void setCurrentCutOffDaysFromConfiguration() {
    final Integer days = getConfiguredCutOffDays();
    if (days == null) {
      return; // not set
    }
    setCurrentCutOffDays(days.intValue());
  }


  /**
   * @return cut off days stored in persistant build
   *         configuration.
   */
  protected abstract Integer getConfiguredCutOffDays();


  /**
   * Forcefully sets current cut-off days.
   * @param days the cut off day.
   */
  public final void setCurrentCutOffDays(final int days) {
    cutOffTimeMillis = new Long(calculateCutOffTimeFromDays(days));
  }


  /**
   * Returns cut off time. Returns null if time is not set.
   */
  public final Long getCutOffTimeMillis() {
    return cutOffTimeMillis;
  }


  /**
   * Helper method
   */
  private static long calculateCutOffTimeFromDays(final int days) {
    return System.currentTimeMillis() - StringUtils.daysToMillis(days);
  }


  public String toString() {
    return "AbstractArchiveRetentionHandler{" +
            "cutOffTimeMillis=" + cutOffTimeMillis +
            ", buildLogPrefix='" + buildLogPrefix + '\'' +
            ", buildLogDir=" + buildLogDir +
            ", activeBuildID=" + activeBuildID +
            '}';
  }


  protected void setCutOffTimeMillis(final Long cutOffTimeMillis) {
    this.cutOffTimeMillis = cutOffTimeMillis;
  }


  protected String getBuildLogPrefix() {
    return buildLogPrefix;
  }


  File getBuildLogDir() {
    return buildLogDir;
  }


  int getActiveBuildID() {
    return activeBuildID;
  }
}
