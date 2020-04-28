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
package org.parabuild.ci.error;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.Version;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 * Error is a container class used to report fatal errors
 */
public final class Error implements Serializable {

  private static final long serialVersionUID = 4138015671189742019L; // NOPMD
  private static final Log log = LogFactory.getLog(Error.class);


  private static final byte ERROR_LEVEL_UNKNOWN = (byte) -1;
  public static final byte ERROR_LEVEL_ERROR = (byte) 0;
  public static final byte ERROR_LEVEL_WARNING = (byte) 1;
  public static final byte ERROR_LEVEL_FATAL = (byte) 3;
  public static final byte ERROR_LEVEL_INFO = (byte) 4;

  public static final String ERROR_SUBSYSTEM_BUILD = "Build";
  public static final String ERROR_SUBSYSTEM_INTEGRATION = "Integration";
  public static final String ERROR_SUBSYSTEM_LOGGING = "Logging";
  public static final String ERROR_SUBSYSTEM_MERGE = "Merge";
  public static final String ERROR_SUBSYSTEM_NOTIFICATION = "Notification";
  public static final String ERROR_SUBSYSTEM_SCHEDULING = "Scheduling";
  public static final String ERROR_SUBSYSTEM_SCM = "Version control";
  public static final String ERROR_SUBSYSTEM_SEARCH = "Search";
  public static final String ERROR_SUBSYSTEM_STATISTICS = "Statistics";
  public static final String ERROR_SUBSYSTEM_WEBUI = "User interface";

  private static final String ERROR_BUILD_NAME = "error.build.name";
  private static final String ERROR_CAUSE = "error.possible.cause";
  private static final String ERROR_DESCRIPTION = "error.description";
  private static final String ERROR_DETAILS = "error.details";
  private static final String ERROR_HTTP_REQUEST_URL = "error.http.request.url";
  private static final String ERROR_HTTP_STATUS = "error.http.status";
  private static final String ERROR_LEVEL = "error.level";
  private static final String ERROR_LOGLINES = "error.loglines";
  private static final String ERROR_PRODUCT_VERSION = "error.product.version";
  private static final String ERROR_STACKTRACE = "error.stacktrace";
  private static final String ERROR_STEP_NAME = "error.step.name";
  private static final String ERROR_SUBSYSTEM = "error.subsystem";
  private static final String ERROR_TIME = "error.time";
  private static final String ERROR_HOST_NAME = "error.host.name";
  private static final String BUILD_ID = "build.id";

  private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS z", Locale.US);
  private boolean sendEmail = true;
  private boolean outputToLog;

  private final Properties content = new Properties();
  private int activeMergeConfigurationID = MergeConfiguration.UNSAVED_ID;


  /**
   * Constructor. Creates a Error object with default error level
   * ERROR_LEVEL_ERROR
   *
   * @see Error#ERROR_LEVEL_ERROR
   */
  public Error() {
    setErrorLevel(ERROR_LEVEL_ERROR);
    setTime(new Date());
    putToContent(ERROR_PRODUCT_VERSION, Version.versionToString(true));
  }


  /**
   * Constructor. Creates a Error object with default error level
   * ERROR_LEVEL_ERROR
   *
   * @see Error#ERROR_LEVEL_ERROR
   */
  public Error(final String description) {
    this();
    setDescription(description);
  }


  /**
   * Constructor. Creates a Error object with default error level
   * ERROR_LEVEL_ERROR
   *
   * @see Error#ERROR_LEVEL_ERROR
   */
  public Error(final String description, final byte errorLevel) {
    this();
    setDescription(description);
    setErrorLevel(errorLevel);
  }


  /**
   * Constructor. Creates a Error object with default error level
   * ERROR_LEVEL_ERROR
   *
   * @see Error#ERROR_LEVEL_ERROR
   */
  public Error(final int buildConfigID, final String description, final byte errorLevel) {
    this();
    setBuildID(buildConfigID);
    setDescription(description);
    setErrorLevel(errorLevel);
  }


  /**
   * Constructor. Creates a Error object with default error level
   * ERROR_LEVEL_ERROR
   *
   * @see Error#ERROR_LEVEL_ERROR
   */
  public Error(final String description, final Throwable th) {
    this();
    setDescription(description);
    setStacktrace(th);
  }


  public Error(final int activeBuildID, final String stepName, final String subsystemName, final Throwable e) {
    setDescription(StringUtils.toString(e));
    setSubsystemName(subsystemName);
    setBuildID(activeBuildID);
    setStepName(stepName);
    setDetails(e);
  }


  /**
   * If true, ErrorManager should attempt to send e-mail
   * notification to administrator.
   * <p/>
   * Default value is false.
   */
  public void setSendEmail(final boolean sendEmail) {
    this.sendEmail = sendEmail;
  }


  public void setBuildID(final int buildID) {
    putToContent(BUILD_ID, Integer.toString(buildID));
  }


  public int getBuildID() {
    final String property = content.getProperty(BUILD_ID);
    if (StringUtils.isBlank(property)) {
      return BuildConfig.UNSAVED_ID;
    }
    return Integer.parseInt(property);
  }


  public void setDescription(final String errorDescription) {
    putToContent(ERROR_DESCRIPTION, errorDescription);
  }


  public void setDetails(final String errorDetails) {
    putToContent(ERROR_DETAILS, errorDetails);
  }


  /**
   * Sets error details in StringBuffer form
   */
  public void setDetails(final StringBuffer errorDetails) {
    setDetails(errorDetails.toString());
  }


  public void setLogLines(final String errorLogLines) {
    putToContent(ERROR_LOGLINES, errorLogLines);
  }


  public void setDetails(final Throwable th) {
    if (StringUtils.isBlank(getStacktrace())) {
      setStacktrace(th);
    }
    if (StringUtils.isBlank(getDescription())) {
      setDescription(StringUtils.toString(th));
    }
    if (th instanceof BuildException) {
      final BuildException be = (BuildException) th;
      be.setReported(true);
      setLogLines(be.getLogContent());
      setHostName(be.getHostName());
    }
  }


  public final void setHostName(final String hostName) {
    if (!StringUtils.isBlank(hostName)) {
      putToContent(ERROR_HOST_NAME, hostName);
    }
  }


  private void setStacktrace(final Throwable th) {
    putToContent(ERROR_STACKTRACE, StringUtils.stackTraceToString(th));
  }


  public void setPossibleCause(final String cause) {
    putToContent(ERROR_CAUSE, cause);
  }


  public void setStepName(final String stepName) {
    putToContent(ERROR_STEP_NAME, stepName);
  }


  public void setSubsystemName(final String subsystemName) {
    putToContent(ERROR_SUBSYSTEM, subsystemName);
  }


  public void setHTTPStatus(final String httpStatus) {
    putToContent(ERROR_HTTP_STATUS, httpStatus);
  }


  public void setHTTPRequestURL(final String httpRequestURL) {
    putToContent(ERROR_HTTP_REQUEST_URL, httpRequestURL);
  }


  public void setErrorLevel(final byte errorLevel) {
    putToContent(ERROR_LEVEL, Byte.toString(errorLevel));
  }


  public void setTime(final Date errorTime) {
    putToContent(ERROR_TIME, dateFormatter.format(errorTime));
  }


  public boolean isSendEmail() {
    return sendEmail;
  }


  public boolean isOutputToLog() {
    return outputToLog;
  }


  public void setOutputToLog(final boolean outputToLog) {
    this.outputToLog = outputToLog;
  }


  public Date getTime() {
    try {
      return dateFormatter.parse(content.getProperty(ERROR_TIME));
    } catch (final ParseException e) {
      log.warn("Can not parse error time", e);
      return new Date();
    }
  }


  public void setBuildName(final String errorBuildName) {
    putToContent(ERROR_BUILD_NAME, errorBuildName);
  }


  public String getBuildName() {
    return content.getProperty(ERROR_BUILD_NAME);
  }


  public String getHostName() {
    return content.getProperty(ERROR_HOST_NAME, "");
  }


  public String getPossibleCause() {
    return content.getProperty(ERROR_CAUSE);
  }


  /**
   * @return product version at time of error creation or null if
   *         version was not defined.
   */
  public String getProductVersion() {
    return content.getProperty(ERROR_PRODUCT_VERSION);
  }


  public String getStepName() {
    return content.getProperty(ERROR_STEP_NAME);
  }


  public String getDetails() {
    return content.getProperty(ERROR_DETAILS);
  }


  public String getLogLines() {
    return content.getProperty(ERROR_LOGLINES);
  }


  public String getStacktrace() {
    return content.getProperty(ERROR_STACKTRACE);
  }


  public String getDescription() {
    return content.getProperty(ERROR_DESCRIPTION);
  }


  public String getSubsystemName() {
    return content.getProperty(ERROR_SUBSYSTEM);
  }


  /**
   * Returns error level.
   *
   * @return error level.
   */
  public int getErrorLevel() {
    int errorLevel = ERROR_LEVEL_UNKNOWN;
    try {
      errorLevel = Integer.parseInt(content.getProperty(ERROR_LEVEL));
    } catch (final NumberFormatException e) {
      if (log.isWarnEnabled()) {
        log.warn("Unexpected exception while parsing error level: " + StringUtils.toString(e), e);
      }
    }
    return errorLevel;
  }


  public String getErrorLevelAsString() {

    final int errorLevel = getErrorLevel();

    switch (errorLevel) {
      case ERROR_LEVEL_ERROR:
        return "ERROR";
      case ERROR_LEVEL_WARNING:
        return "WARNING";
      case ERROR_LEVEL_FATAL:
        return "FATAL";
      case ERROR_LEVEL_INFO:
        return "INFO";
      default:
        return "UNKNOWN";
    }
  }


  public Properties getContent() {
    return new Properties(content);
  }


  /**
   * Loads error from a property file
   *
   * @param file to load from
   * @throws IOException if some
   */
  public void load(final File file) throws IOException {
    InputStream is = null;
    try {
      if (file.length() == 0L) {
        setDescription("Error description is missing");
        setDetails("It appears that a error happened but it was not possible to store error information.");
        setPossibleCause("This could happen because there was no free space on the file system when error happened. It is a critical condition. Make sure that Parabuild installation has not run out of disk space.");
      } else {
        is = new BufferedInputStream(new FileInputStream(file), 200);
        content.clear();
        content.load(is);
      }
      // Fix for bug #586
      if (content.getProperty(ERROR_TIME) == null) {
        setTime(new Date(file.lastModified()));
      }
    } finally {
      IoUtils.closeHard(is);
    }
  }


  /**
   * Returns a hashcode used by ErrorManager to find out if this
   * error has been reported within given period of time.
   *
   * @return Integer
   */
  public Integer getRetentionKey() {
    int result = 0;
//    result = addToHashCode(result, getErrorLevelAsString());
//    result = addToHashCode(result, getBuildName());
    result = addToKey(result, getDescription());
//    result = addToHashCode(result, getStepName());
    result = addToKey(result, getDetails());
    return new Integer(result); // NOPMD (IntegerInstantiation)
  }


  private static int addToKey(final int in, final String value) {
    if (value != null) {
      return in ^ value.hashCode();
    }
    return in;
  }


  private void putToContent(final String name, final String value) {
    if (StringUtils.isBlank(value)) {
      return;
    }
    content.setProperty(name, value);
  }


  /**
   * Factory method.
   *
   * @return created Error
   */
  public static Error newWarning(final String p_sSubSystem) {
    final Error objError = new Error();
    objError.setErrorLevel(ERROR_LEVEL_WARNING);
    objError.setSubsystemName(p_sSubSystem);

    return objError;
  }


  /**
   * Sets merge configuration ID for that the error has occurred.
   *
   * @param activeMergeConfigurationID to set.
   */
  public void setMergeID(final int activeMergeConfigurationID) {
    this.activeMergeConfigurationID = activeMergeConfigurationID;
  }


  public String toString() {
    return "Error{" +
            ", sendEmail=" + sendEmail +
            ", outputToLog=" + outputToLog +
            ", content=" + content +
            ", activeMergeConfigurationID=" + activeMergeConfigurationID +
            '}';
  }
}
