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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * This class is responsible for packing (zipping) expired build
 * logs.
 */
public final class ArchiveCompressor {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(ArchiveCompressor.class); // NOPMD


  private static final int DEFAULT_COMPRESS_DAYS = 30;
  public static final String ZIP_SUFFIX = ".zip";
  public static final int ZIP_SUFFIX_LENGTH = ZIP_SUFFIX.length();

  private long cutOffTime = calculateCutOffTime(DEFAULT_COMPRESS_DAYS); // default 364 days
  private final String archivableNamePrefix;
  private final File archiveDir;
  private final int buildID;


  /**
   * Constructor.
   *
   * @param buildID - int ID of a build configuration.
   * @param archiveDir - Directory with archive entities, like logs or build results.
   * @param archivableNamePrefix - Prefix a file should have.
   */
  public ArchiveCompressor(final int buildID, final File archiveDir, final String archivableNamePrefix) {
    this.archiveDir = (File)ArgumentValidator.validateArgumentNotNull(archiveDir, "archive dir");
    this.archivableNamePrefix = (String)ArgumentValidator.validateArgumentNotNull(archivableNamePrefix, "archivable name prefix");
    this.buildID = ArgumentValidator.validateBuildIDInitialized(buildID);
    this.recalculateCurrentCutOffTime();
  }


  /**
   * Packs the archive entities.
   */
  public void compressExpiredArchiveEntities() {
//    if (log.isDebugEnabled()) log.debug("cutOffTime: " + cutOffTime);
    archiveDir.listFiles(new FileFilter() {
      public boolean accept(final File pathname) {
//        if (log.isDebugEnabled()) log.debug("pathname: " + pathname);
//        if (log.isDebugEnabled()) log.debug("pathname.lastModified(): " + pathname.lastModified());
//        if (log.isDebugEnabled()) log.debug("cutOffTime: " + cutOffTime);
//        if (log.isDebugEnabled()) log.debug("pathname.lastModified() < cutOffTime: " + (pathname.lastModified() < cutOffTime));
        if (!pathname.getName().endsWith(ZIP_SUFFIX)
          && pathname.getName().startsWith(archivableNamePrefix)
          && pathname.lastModified() <= cutOffTime) {
          try {
            if (log.isDebugEnabled()) log.debug("compressing: " + pathname);
            compress(pathname);
            IoUtils.deleteFileHard(pathname);
          } catch (final IOException e) {
            reportPackingError(e);
          }
        }
        return false;
      }


      /**
       * Packs file or dir.
       *
       * @param pathname a name of a directory or a file to compress.
       */
      private void compress(final File pathname) throws IOException {
        final String zippedName = pathname.getName() + ZIP_SUFFIX;
        final String zippedPath = pathname.getParent();
        final File zippedFile = new File(zippedPath, zippedName);
//        if (log.isDebugEnabled()) log.debug("packing pathname: " + pathname);
//        if (log.isDebugEnabled()) log.debug("   to          : " + zippedFile);
        if (pathname.isFile()) {
          IoUtils.zipFile(pathname, zippedFile);
        } else if (pathname.isDirectory()) {
          IoUtils.zipDir(pathname, zippedFile);
        }
      }
    });
  }


  /**
   */
  public void recalculateCurrentCutOffTime() {
    final BuildConfigAttribute daysAttr = ConfigurationManager.getInstance().getBuildAttribute(buildID, BuildConfigAttribute.LOG_PACK_DAYS);
    final String stringDays = daysAttr == null ? Integer.toString(DEFAULT_COMPRESS_DAYS) : daysAttr.getPropertyValue();
    if (StringUtils.isValidInteger(stringDays)) {
      cutOffTime = calculateCutOffTime(Integer.parseInt(stringDays));
    }
  }


  /**
   * Returns cut off time
   */
  public long getCutOffTimeMillis() {
    return cutOffTime;
  }


  /**
   * This method overwrites cut of time received from database
   * with the given value.
   *
   * @param cutOffTime time to set in milliseconds.
   */
  public void forceCutOffTimeMillis(final long cutOffTime) {
    this.cutOffTime = cutOffTime;
  }


  /**
   * Helper method
   */
  public static long calculateCutOffTime(final int days) {
    return System.currentTimeMillis() - StringUtils.daysToMillis(days);
  }


  /**
   * Helper method to report errors.
   */
  private void reportPackingError(final Exception e) {
    final ErrorManager em = ErrorManagerFactory.getErrorManager();
    final Error error = new Error("Error while compressing: " + StringUtils.toString(e));
    error.setBuildID(buildID);
    error.setDetails(e);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSendEmail(false);
    em.reportSystemError(error);
  }


  public String toString() {
    return "ArchiveCompressor{" +
      "cutOffTime=" + cutOffTime +
      ", archivableNamePrefix='" + archivableNamePrefix + '\'' +
      ", archiveDir=" + archiveDir +
      ", buildID=" + buildID +
      '}';
  }
}
