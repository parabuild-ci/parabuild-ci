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
package org.parabuild.ci.error.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConstants;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.notification.NotificationManager;
import org.parabuild.ci.notification.NotificationManagerFactory;
import org.parabuild.ci.object.BuildConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public final class ErrorManagerImpl implements ErrorManager {

  private static final long serialVersionUID = 6936940630745776656L; // NOPMD
  private static final Log LOG = LogFactory.getLog(ErrorManagerImpl.class);

  private static final Comparator ERROR_FILE_NAME_COMPARATOR = new ErrorFileNameComparator();
  private static final DummyStatelessRetentionCache DUMMY_STATELESS_RETENTION_CACHE = new DummyStatelessRetentionCache();
  private static final String ERROR_FILE_EXTENSION = ".error";


  private static int errorCounter = 0;
  private boolean notificationEnabled = true;


  public void reportSystemError(final Error error) {
    try {
      if (isErrorReported(error)) return;
      validateDescription(error);
      guessBuildName(error);
      markErrorReported(error);
      incrementErrorCounter();
      storeError(error);
      notifyBuildAdmin(error);
    } catch (final Exception e) {
      logHard("Error while reporting system error", e);
    }
  }


  /**
   * Returns global error counter
   */
  public int errorCount() {
    return errorCounter;
  }


  /**
   * Clears all error files from new errors directory by moving
   * it to "cleared" directory.
   */
  public synchronized void clearAllActiveErrors() {
    try {
      final RetentionCache cache = getRetentionCache();
      cache.removeAll();
      errorCounter = 0;
      final File dir = ConfigurationManager.getSystemNewErrorsDirectory();
      final File[] files = dir.listFiles();
      for (int i = 0; i < files.length; i++) {
        final File file = files[i];
        final String name = file.getName();
        IoUtils.moveFile(file, new File(ConfigurationManager.getSystemClearedErrorsDirectory(), name));
      }
    } catch (final Exception e) {
      logHard("Error while clearing active errors", e);
    }
  }


  /**
   * Clears given error from new errors directory by moving it to
   * "cleared" directory.
   *
   * @param errorID - error ID, i.e. file name w/o ".error"
   *                extension.
   */
  public synchronized void clearActiveError(final String errorID) {
    try {
      // get error file
      final String errorFileName = errorID + ERROR_FILE_EXTENSION;
      final File errorFile = new File(ConfigurationManager.getSystemNewErrorsDirectory(), errorFileName);
      if (!errorFile.exists()) return;
      // move error to cleared directory
      IoUtils.moveFile(errorFile, new File(ConfigurationManager.getSystemClearedErrorsDirectory(), errorFileName));
      // decrease counter
      if (errorCounter > 0) errorCounter--;
    } catch (final IOException e) {
      // we ignore it as we can not do anything about it
      LOG.error("Error while clearing error", e);
    }
  }


  /**
   * Enables/disables sending e-mail notification to a build
   * admin
   */
  public void enableNotification(final boolean enable) {
    notificationEnabled = enable;
  }


  public boolean isNotificationEnabled() {
    return notificationEnabled;
  }


  private void storeError(final Error error) throws IOException {

    BufferedOutputStream bos = null;
    try {

      final File dir = ConfigurationManager.getSystemNewErrorsDirectory();
      final File errorFile = new File(dir, new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US).format(new Date()) + ERROR_FILE_EXTENSION);
      bos = new BufferedOutputStream(new FileOutputStream(errorFile), 512);
      final Properties errorContent = error.getContent();
      errorContent.store(bos, "Parabuild Error");

      // Log as error
      if (StringUtils.isBlank(error.getStacktrace())) {

        LOG.error(error.getDescription());
      } else {

        LOG.error(error.getDescription() + ": " + error.getStacktrace());
      }

      // print debug error content
      if (error.isOutputToLog() || "true".equals(System.getProperty(SystemConstants.SYSTEM_PROPERTY_PRINT_STACKTRACE, "false"))) {

        final Set es = errorContent.entrySet();
        for (final Iterator i = es.iterator(); i.hasNext();) {

          final Map.Entry entry = (Map.Entry) i.next();
          if (LOG.isDebugEnabled()) {
            LOG.debug("error element: " + entry.getKey() + "; error value: " + entry.getValue());
          }
        }
      }
    } finally {
      IoUtils.closeHard(bos);
    }
  }


  /**
   * Loads error. If error can not be found or there was an error
   * while loading, this method will return null.
   *
   * @param errorID error ID
   */
  public Error loadActiveError(final String errorID) {
    if (errorID == null) {
      return null;
    }
    final Error error = new Error();
    try {
      error.load(new File(ConfigurationManager.getSystemNewErrorsDirectory(), errorID + ERROR_FILE_EXTENSION));
    } catch (final IOException e) {
      LOG.error("Error while getting error object", e);
    }
    return error;
  }


  /**
   * Returns list of IDs of active errors.
   *
   * @param maxErrors maximum number of errors to return.
   */
  public List getActiveErrorIDs(final int maxErrors) {
    final int extensionLength = ERROR_FILE_EXTENSION.length();
    final List result = new ArrayList(111);
    final File errorDir = ConfigurationManager.getSystemNewErrorsDirectory();
    final File[] errors = errorDir.listFiles(new FilenameFilter() {
      public boolean accept(final File dir, final String name) {
        return name.endsWith(ERROR_FILE_EXTENSION);
      }
    });

    // Empty check
    if (errors == null) {
      return result;
    }

    // sort
    Arrays.sort(errors, ERROR_FILE_NAME_COMPARATOR);

    // add max errors
    final int maxIndex = Math.min(errors.length, maxErrors);
    for (int i = 0; i < maxIndex; i++) {
      final String name = errors[i].getName();
      result.add(name.substring(0, name.length() - extensionLength));
    }
    return result;
  }


  /**
   * Increments error counter
   */
  private void incrementErrorCounter() {
    synchronized (ErrorManagerImpl.class) {
      errorCounter++;
      if (LOG.isDebugEnabled()) LOG.debug("Incremented error counter: " + errorCounter);
    }
  }


  /**
   * Writes error log ignoring any exceptions
   */
  private void logHard(final String description, final Throwable th) {
    try {
      if (th == null) {
        LOG.error(description);
      } else {
        LOG.error(description, th);
      }
    } catch (final Exception e) {
      // ignore any error, last resort
      IoUtils.ignoreExpectedException(e);
    }
  }


  /**
   * Returns true if error was reported withing retention period
   */
  private boolean isErrorReported(final Error error) {
    boolean result;
    try {
      // calculate error hash
      final RetentionCache cache = getRetentionCache();
      final Element elem = cache.get(error.getRetentionKey());
      result = elem != null;
    } catch (final CacheException e) {
      logHard("Error while checking error retention, will return false", e);
      result = false;
    }
    return result;
  }


  /**
   * Marks error as reported
   */
  private void markErrorReported(final Error error) {
    final Integer retentionCode = error.getRetentionKey();
    try {
      final RetentionCache cache = getRetentionCache();
      cache.put(new Element(retentionCode, retentionCode));
    } catch (final CacheException e) {
      logHard("Error while marking error as reported", e);
    }
  }


  /**
   * Returns reference to retention cache
   */
  private RetentionCache getRetentionCache() throws CacheException {
    final CacheManager manager = CacheManager.getInstance();
    final Cache cache = manager.getCache("retention_cache");
    if (cache == null) {
      return DUMMY_STATELESS_RETENTION_CACHE;
    } else {
      return new DelegatingRetentionCache(cache);
    }
  }


  /**
   * Sends notification to build administrator about error
   */
  private void notifyBuildAdmin(final Error error) {
    try {
      if (!notificationEnabled || !error.isSendEmail()) return; // don't send if not required
      final NotificationManager nm = NotificationManagerFactory.makeNotificationManager();
      nm.notifyBuildAdministrator(error);
    } catch (final Exception e) {
      // last resort
      LOG.error("Error while sending notification to the build administrator", e);
    }
  }


  /**
   * Helper methods to get a build name from ID
   */
  private String getBuildNameHard(final int ID) {
    try {
      return ConfigurationManager.getInstance().getBuildConfiguration(ID).getBuildName();
    } catch (final Exception e) {
      if (ID != BuildConfig.UNSAVED_ID) return "Build ID is " + ID;
      // we don' care
      return "";
    }
  }


  /**
   * Tries to get and set back the build name if possible
   */
  private void guessBuildName(final Error error) {
    if (StringUtils.isBlank(error.getBuildName())
            && error.getBuildID() != BuildConfig.UNSAVED_ID) {
      error.setBuildName(getBuildNameHard(error.getBuildID()));
    }
  }


  private void validateDescription(final Error error) {
    if (StringUtils.isBlank(error.getDescription())) {
      error.setDetails(new Throwable("Description was not provided"));
    }
  }


  /**
   * Class to compare error file names
   */
  private static final class ErrorFileNameComparator implements Comparator {

    public int compare(final Object o1, final Object o2) {
      // we compare f2 to f1 because we need reverse order
      return ((Comparable<File>) o2).compareTo((File) o1);
    }
  }


  public String toString() {
    return "ErrorManagerImpl{" +
            "notificationEnabled=" + notificationEnabled +
            '}';
  }
}
