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

import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.process.RemoteCommandTimeStamp;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 *
 */
public abstract class AbstractBuildScriptGenerator implements BuildScriptGenerator {

  private static final int UNSET_BUILD_NUMBER = -1;
  private static final int UNSET_BUILD_RUN_ID = BuildRun.UNSAVED_ID;
  private static final String VAR_PARABUILD_SCRIPT = "PARABUILD_SCRIPT";
  private static final String VAR_CLASSPATH = "CLASSPATH";
  private static final String[] VARS_TO_ERASE = {
          "_JAVACMD",
          "_EXECJAVA",
          "PARABUILD_BASE",
          "PARABUILD_HOME",
          "PARABUILD_OPTS",
          "PARABUILD_TMPDIR",
          "PARABUILD_JAVA_HOME",
          "DEBUG_OPTS",
          "JAVA_ENDORSED_DIRS",
          "JAVA_OPTS",
          "JPDA_ADDRESS",
          "JPDA_TRANSPORT",
          "JSSE_HOME",
          "MAINCLASS",
          "SECURITY_POLICY_FILE"
  };

  public static final String DEFAULT_BUILD_STARTED_BY_USER = "system";

  protected String relativeBuildDir = null;
  protected final Agent agent;

  private Date buildStartedAt = null;
  private final Map addedVariables = new HashMap(11);
  private final RemoteCommandTimeStamp timeStamp;
  private int buildNumber = UNSET_BUILD_NUMBER;
  private int buildRunID = UNSET_BUILD_RUN_ID;
  private String buildName = null;
  private String changeListNumber = null;
  private String stepName = null;
  private String stepScriptPath = null;
  private Date changeListDate = null;
  private String buildStartedByUser = DEFAULT_BUILD_STARTED_BY_USER;
  private String version = null; // optional, not set by default
  private int versionCounter = -1; // optional, not set by default
  private boolean cleanCheckout = false;
  private int leadingBuildRunID = -1;
  private String leadingBuildName = null;
  private int leadingBuildID = -1;
  private int sequenceNumber = 0;
  private String previousChangeListNumber = null;
  private Date previousChangeListDate = null;
  private Date lastGoodBuildDate = null;
  private Date lastGoodChangeListDate = null;
  private String lastGoodChangeListNumber = null;
  private int lastGoodBuildNumber = -1;
  private int buildID = -1;
  private int projectID = -1;
  private String projectName = null;
  private int upstreamBuildRunID = -1;
  private String upstreamBuildName = null;
  private int upstreamBuildID = -1;
  private String getBranchName = null;
  private List previousStepRuns;


  protected AbstractBuildScriptGenerator(final Agent agent) {
    this.agent = agent;
    this.timeStamp = new RemoteCommandTimeStamp();
  }


  /**
   * Sets build, or source line home directory. Build directory
   * is the directory where the build script is running from, and
   * from all uses-specified directories and files, like logs,
   * are counted from.
   */
  public final void setRelativeBuildDir(final String buildDir) {
    this.relativeBuildDir = buildDir;
  }


  /**
   * Sets build number value.
   * <p/>
   * Each build has a sequence number. Value set by this method
   * is used to add setting an environment variable
   * PARABUILD_BUILD_NUMBER in a build script.
   */
  public final void setBuildNumber(final int number) {
    this.buildNumber = number;
  }


  /**
   * Sets changelist number the build is built to
   *
   * @param changeListNumber
   */
  public final void setChangeListNumber(final String changeListNumber) {
    this.changeListNumber = changeListNumber;
  }


  /**
   * Change list date.
   *
   * @param changeListDate
   */
  public void setChangeListDate(final Date changeListDate) {
    if (changeListDate == null) {
      return;
    }
    this.changeListDate = (Date) changeListDate.clone();
  }


  /**
   * Sets required build name.
   */
  public final void setBuildName(final String buildName) {
    this.buildName = buildName;
  }


  /**
   * Sets required step name.
   */
  public final void setStepName(final String stepName) {
    this.stepName = stepName;
  }


  /**
   * Sets required build run ID.
   */
  public final void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


  /**
   * Adds shell variables to be present in the script.
   *
   * @param variables a Map with a shell variable name as a key
   *                  and variable value as value.
   */
  public final void addVariables(final Map variables) {
    this.addedVariables.putAll(variables);
  }


  /**
   * Sets required date and time the build started at.
   *
   * @param startedAt date and time the build started at.
   */
  public final void setBuildStartedAt(final Date startedAt) {
    this.buildStartedAt = (Date) startedAt.clone();
  }


  public void setBuildStartedByUser(final String userName) {
    this.buildStartedByUser = userName;
  }


  /**
   * Sets optional version if it was generated
   *
   * @param version
   */
  public void setVersion(final String version) {
    this.version = version;
  }


  /**
   * Sets optional version counter if it was generated
   *
   * @param versionCounter
   */
  public void setVersionCounter(final int versionCounter) {
    this.versionCounter = versionCounter;
  }


  /**
   * Signifies that a build performed for a clean workspace.
   */
  public void setCleanCheckout(final boolean clean) {
    this.cleanCheckout = clean;
  }


  /**
   * Adds a shell variable to be present in the script.
   *
   * @param name  variable name
   * @param value variable value
   */
  public void addVariable(final String name, final String value) {
    this.addedVariables.put(name, value);
  }


  /**
   * Sets last good build date if any.
   *
   * @param lastGoodBuildDate last good build date
   */
  public void setLastGoodBuildDate(final Date lastGoodBuildDate) {
    if (lastGoodBuildDate == null) {
      return;
    }
    this.lastGoodBuildDate = (Date) lastGoodBuildDate.clone();
  }


  /**
   * Sets last good change list date if any.
   *
   * @param lastGoodChangeListDate last good change list date
   */
  public void setLastGoodChangeListDate(final Date lastGoodChangeListDate) {
    if (lastGoodChangeListDate == null) {
      return;
    }
    this.lastGoodChangeListDate = (Date) lastGoodChangeListDate.clone();
  }


  /**
   * Sets last good change list number, if any.
   *
   * @param lastGoodChangeListNumber last good change list number
   */
  public void setLastGoodChangeListNumber(final String lastGoodChangeListNumber) {
    this.lastGoodChangeListNumber = lastGoodChangeListNumber;
  }


  /**
   * Sets last good build number, if any.
   *
   * @param lastGoodBuildNumber last good build number.
   */
  public void setLastGoodBuildNumber(final int lastGoodBuildNumber) {
    this.lastGoodBuildNumber = lastGoodBuildNumber;
  }


  /**
   * Sets leading build run ID if any
   *
   * @param leadingBuildRunID
   */
  public void setLeadingBuildRunID(final int leadingBuildRunID) {
    this.leadingBuildRunID = leadingBuildRunID;
  }


  /**
   * Sets leading build name if any.
   *
   * @param leadingBuildName
   */
  public void setLeadingBuildName(final String leadingBuildName) {
    this.leadingBuildName = leadingBuildName;
  }


  /**
   * Sets leading build ID if any.
   *
   * @param leadingBuildID
   */
  public void setLeadingBuildID(final int leadingBuildID) {
    this.leadingBuildID = leadingBuildID;
  }


  /**
   * Sets an ID of the build run that started the build.
   *
   * @param upstreamBuildRunID
   */
  public void setUpstreamBuildRunID(final int upstreamBuildRunID) {
    this.upstreamBuildRunID = upstreamBuildRunID;
  }


  /**
   * Sets a name of the build that started the build.
   *
   * @param upstreamBuildName
   */
  public void setUpstreamBuildName(final String upstreamBuildName) {
    this.upstreamBuildName = upstreamBuildName;
  }


  /**
   * Sets an ID of the build that started the build.
   *
   * @param upstreamBuildID
   */
  public void setUpstreamBuildID(final int upstreamBuildID) {
    this.upstreamBuildID = upstreamBuildID;
  }


  /**
   * {@inheritDoc}
   */
  public void setGitBranchName(final String branchName) {
    this.getBranchName = branchName;
  }

  public void setPreviousStepRuns(final List previousStepRuns) {
    this.previousStepRuns = new ArrayList(previousStepRuns);
  }


  /**
   * Sets sequence number that is unique per build
   * configuration.
   */
  public void setSequenceNumber(final int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }


  public void setPreviousChangeListNumber(final String previousChangeListNumber) {
    this.previousChangeListNumber = previousChangeListNumber;
  }


  public void setPreviousChangeListDate(final Date previousChangeListDate) {
    if (previousChangeListDate == null) {
      return;
    }
    this.previousChangeListDate = (Date) previousChangeListDate.clone();
  }


  /**
   * Generates build sequence script for further execution
   *
   * @param sequence fo which a script will be created
   * @return String containing generated script
   */
  private String generateScript(final BuildSequence sequence) throws BuildException, AgentFailureException {
    return doGenerateScript(sequence);
  }


  public final String generateScriptFile(final BuildSequence sequence) throws IOException, AgentFailureException {
    try {
      stepScriptPath = agent.makeStepScriptPath(sequence.getSequenceID());
      agent.createFile(stepScriptPath, generateScript(sequence));
      return stepScriptPath;
    } catch (final BuildException e) {
      throw IoUtils.createIOException(e);
    }
  }


  protected final StringBuffer cleanupPathLikeEnvVariable(final String varName) throws IOException, AgentFailureException {
    final String catalinaHome = agent.getSystemProperty("catalina.base");
    final RemoteFileDescriptor fileDescriptor = agent.getFileDescriptor(catalinaHome + "//..");
    final String originalPathEnvVar = agent.getEnvVariable(varName);
    final StringBuffer resultingPathEnvVar = new StringBuffer(50);
    if (originalPathEnvVar != null) {
      final String pathSeparator = agent.pathSeparator();
      //System.out.println("DEBUG: pathSeparator = " + pathSeparator);
      for (final StringTokenizer st = new StringTokenizer(originalPathEnvVar, pathSeparator); st.hasMoreTokens(); ) {
        final String pathElem = st.nextToken();
        if (pathElem.startsWith(fileDescriptor.getCanonicalPath()) || pathElem.isEmpty()) {
          //System.out.println("DEBUG: pathElem = " + pathElem);
          continue;
        }
        resultingPathEnvVar.append(pathElem).append(pathSeparator);
      }
    }
    return resultingPathEnvVar;
  }


  protected final void writeCleanupVarsCommands(final BufferedWriter writer) throws IOException, AgentFailureException {
    // cleanup paths
    writeSetCommand(writer, pathVarName(), cleanupPathLikeEnvVariable(pathVarName()).toString());
    writeSetCommand(writer, VAR_CLASSPATH, cleanupPathLikeEnvVariable(VAR_CLASSPATH).toString());

    // reset wars
    for (int i = 0; i < VARS_TO_ERASE.length; i++) {
      writeSetCommand(writer, VARS_TO_ERASE[i], "");
    }
  }


  protected final void writeCommonCommands(final BufferedWriter writer) throws IOException {
    // Prepare
    final SimpleDateFormat timestampFormat = createTimestampFormat();

    // process "signature" env vars
    writeSetCommand(writer, VAR_PARABUILD_SCRIPT, stepScriptPath);
    writeSetCommand(writer, timeStamp.name(), timeStamp.value());
    // mandatory vars
    writeSetCommand(writer, VAR_PARABUILD_BUILD_NUMBER, getValidBuildNumber());
    writeSetCommand(writer, VAR_PARABUILD_CONFIGURATION_ID, agent.getActiveBuildID());
    writeSetCommand(writer, VAR_PARABUILD_BUILD_NAME, getValidBuildName());
    writeSetCommand(writer, VAR_PARABUILD_STEP_NAME, getValidStepName());
    writeSetCommand(writer, VAR_PARABUILD_CHANGE_LIST_NUMBER, getValidChangeListNumber());
    writeSetCommand(writer, VAR_PARABUILD_CHANGE_LIST_DATETIME, getValidChangeListDate());
    writeSetCommand(writer, VAR_PARABUILD_PREVIOUS_CHANGE_LIST_DATETIME, getPreviousChangeListDate());
    writeSetCommand(writer, VAR_PARABUILD_PREVIOUS_CHANGE_LIST_NUMBER, previousChangeListNumber);
    writeSetCommand(writer, VAR_PARABUILD_LAST_GOOD_BUILD_DATETIME, lastGoodBuildDate, timestampFormat);
    writeSetCommand(writer, VAR_PARABUILD_LAST_GOOD_BUILD_NUMBER, lastGoodBuildNumber);
    writeSetCommand(writer, VAR_PARABUILD_LAST_GOOD_CHANGE_LIST_DATETIME, lastGoodChangeListDate, timestampFormat);
    writeSetCommand(writer, VAR_PARABUILD_LAST_GOOD_CHANGE_LIST_NUMBER, lastGoodChangeListNumber);
    writeSetCommand(writer, VAR_PARABUILD_BUILD_RUN_ID, getValidBuildRunID());
    writeSetCommand(writer, VAR_PARABUILD_BUILD_TIMESTAMP, timestampFormat.format(buildStartedAt));
    writeSetCommand(writer, VAR_PARABUILD_BUILD_TIMESTAMP_UTC, getBuildTimeStampUTC());
    writeSetCommand(writer, VAR_PARABUILD_BUILD_DATE, new SimpleDateFormat(BUILD_DATE_FORMAT, Locale.US).format(buildStartedAt));
    writeSetCommand(writer, VAR_PARABUILD_BUILD_STARTED_BY_USER, buildStartedByUser);
    writeSetCommand(writer, VAR_PARABUILD_SEQUENCE_NUMBER, sequenceNumber);
    writeSetCommand(writer, VAR_PARABUILD_BUILD_ID, buildID);
    writeSetCommand(writer, VAR_PARABUILD_PROJECT_ID, projectID);
    writeSetCommand(writer, VAR_PARABUILD_PROJECT_NAME, projectName);
    writeSetCommand(writer, VAR_PARABUILD_PREVIOUS_STEPS, createPreviousSteps());

    // optional vars
    if (!StringUtils.isBlank(version)) {
      writeSetCommand(writer, VAR_PARABUILD_VERSION, version);
    }
    if (versionCounter != -1) {
      writeSetCommand(writer, VAR_PARABUILD_VERSION_COUNTER, versionCounter);
    }
    if (cleanCheckout) {
      writeSetCommand(writer, VAR_PARABUILD_CLEAN_CHECKOUT, "true");
    }
    // optional variables for parallel builds
    if (leadingBuildID != -1) {
      writeSetCommand(writer, VAR_PARABUILD_LEADING_BUILD_ID, leadingBuildID);
    }
    if (leadingBuildRunID != -1) {
      writeSetCommand(writer, VAR_PARABUILD_LEADING_BUILD_RUN_ID, leadingBuildRunID);
    }
    if (upstreamBuildRunID != -1) {
      writeSetCommand(writer, VAR_PARABUILD_UPSTREAM_BUILD_RUN_ID, upstreamBuildRunID);
      writeSetCommand(writer, VAR_PARABUILD_UPSTREAM_BUILD_NAME, upstreamBuildName);
      writeSetCommand(writer, VAR_PARABUILD_UPSTREAM_BUILD_ID, upstreamBuildID);
    }
    if (!StringUtils.isBlank(leadingBuildName)) {
      writeSetCommand(writer, VAR_PARABUILD_LEADING_BUILD_NAME, leadingBuildName);
    }
    if (!StringUtils.isBlank(getBranchName)) {
      writeSetCommand(writer, VAR_PARABUILD_GIT_BRANCH, getBranchName);
    }
  }


  /**
   * Creates a String containing a list of previous steps and their results.
   *
   * @return a String containing a list of previous steps and their results.
   */
  private String createPreviousSteps() {

    if (previousStepRuns == null || previousStepRuns.isEmpty()) {
      return "";
    }

    final StringBuilder result = new StringBuilder(100);
    for (int i = 0; i < previousStepRuns.size(); i++) {

      // Get step run attributes
      final StepRun stepRun = (StepRun) previousStepRuns.get(i);
      final byte resultID = stepRun.getResultID();
      final String resultString = BuildRun.buildResultToShellString(resultID);
      final String stepName = stepRun.getName();

      // Compose variable value
      result.append(stepName);
      result.append(':');
      result.append(resultString);
      result.append(';');
    }

    return result.toString();
  }


  /**
   * Converts the build start time to a UTC string.
   *
   * @return the build start time as a UTC string.
   */
  private String getBuildTimeStampUTC() {
    final SimpleDateFormat simpleDateFormat = createTimestampFormat();
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return simpleDateFormat.format(buildStartedAt);
  }


  /**
   * Creates a SimpleDateFormat for formatting time stamps.
   *
   * @return the SimpleDateFormat for formatting time stamps.
   * @see #BUILD_TIMESTAMP_FORMAT
   */
  private static SimpleDateFormat createTimestampFormat() {
    return new SimpleDateFormat(BUILD_TIMESTAMP_FORMAT, Locale.US);
  }


  /**
   * @return validated build run ID
   */
  private int getValidBuildRunID() {
    if (buildRunID == UNSET_BUILD_RUN_ID) {
      throw new IllegalStateException("Build run ID was not set");
    }
    return buildRunID;
  }


  /**
   * @return validated change list number
   */
  public final String getValidChangeListNumber() {
    if (StringUtils.isBlank(changeListNumber)) {
      throw new IllegalStateException("Build change list number was not set");
    }
    return changeListNumber;
  }


  private String getValidChangeListDate() {
    if (changeListDate == null) {
      throw new IllegalStateException("Build change list date was not set");
    }
    return createTimestampFormat().format(changeListDate);
  }


  private String getPreviousChangeListDate() {
    if (previousChangeListDate == null) {
      return null;
    }
    return createTimestampFormat().format(previousChangeListDate);
  }


  /**
   * @return validated build number
   */
  public final int getValidBuildNumber() {
    if (buildNumber == UNSET_BUILD_NUMBER) {
      throw new IllegalStateException("Build number was not set");
    }
    return buildNumber;
  }


  /**
   * Returns build name.
   *
   * @return build name
   */
  protected final String getValidBuildName() {
    if (StringUtils.isBlank(buildName)) {
      throw new IllegalStateException("Build name was not set");
    }
    return buildName;
  }


  /**
   * Returns step name.
   *
   * @return step name
   */
  protected final String getValidStepName() {
    if (StringUtils.isBlank(stepName)) {
      throw new IllegalStateException("Step name was not set");
    }
    return stepName;
  }


  /**
   * Outputs optional added variables.
   *
   * @see #addVariables(Map)
   */
  protected final void writeAddedVariables(final BufferedWriter writer) throws IOException {
    for (final Iterator i = addedVariables.entrySet().iterator(); i.hasNext(); ) {
      final Map.Entry nameValuePair = (Map.Entry) i.next();
      writeSetCommand(writer, (String) nameValuePair.getKey(), (String) nameValuePair.getValue());
    }
  }


  /**
   * Returns a list of variables to erase
   */
  public static String[] getVarsToErase() {
    final String[] result = new String[VARS_TO_ERASE.length];
    System.arraycopy(VARS_TO_ERASE, 0, result, 0, VARS_TO_ERASE.length);
    return result;
  }


  /**
   * {@inheritDoc}
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * {@inheritDoc}
   */
  public void setProjectID(final int projectID) {
    this.projectID = projectID;
  }


  /**
   * {@inheritDoc}
   */
  public void setProjectName(final String projectName) {
    this.projectName = projectName;
  }


  /**
   * Helper method to write set commands using int values.
   *
   * @param writer to use to write the set command
   * @param name   of the shell variable.
   * @param value  value of the shell variable.
   * @throws IOException
   */
  private void writeSetCommand(final BufferedWriter writer, final String name, final int value) throws IOException {
    writeSetCommand(writer, name, Integer.toString(value));
  }


  private void writeSetCommand(final BufferedWriter writer, final String name, final Date value, final SimpleDateFormat format) throws IOException {
    writeSetCommand(writer, name, value == null ? null : format.format(value));
  }


  protected abstract void writeSetCommand(BufferedWriter scriptWriter, String variable, String value) throws IOException;


  protected abstract String pathVarName();


  /**
   * Generates build sequence script for further
   * execution.Implementation of the Template Method pattern.
   *
   * @param sequence fo which a script will be created
   * @return String containing generated script
   * @see #generateScript(BuildSequence)
   */
  protected abstract String doGenerateScript(BuildSequence sequence) throws BuildException, AgentFailureException;


  public String toString() {
    return "AbstractBuildScriptGenerator{" +
            "relativeBuildDir='" + relativeBuildDir + '\'' +
            ", agent=" + agent +
            ", buildStartedAt=" + buildStartedAt +
            ", buildStartedAtUTC=" + buildStartedAt +
            ", addedVariables=" + addedVariables +
            ", timeStamp=" + timeStamp +
            ", buildNumber=" + buildNumber +
            ", buildRunID=" + buildRunID +
            ", buildName='" + buildName + '\'' +
            ", changeListNumber='" + changeListNumber + '\'' +
            ", stepName='" + stepName + '\'' +
            ", stepScriptPath='" + stepScriptPath + '\'' +
            ", changeListDate=" + changeListDate +
            ", buildStartedByUser='" + buildStartedByUser + '\'' +
            ", version='" + version + '\'' +
            ", versionCounter=" + versionCounter +
            ", cleanCheckout=" + cleanCheckout +
            ", leadingBuildRunID=" + leadingBuildRunID +
            ", leadingBuildName='" + leadingBuildName + '\'' +
            ", leadingBuildID=" + leadingBuildID +
            ", sequenceNumber=" + sequenceNumber +
            ", previousChangeListNumber='" + previousChangeListNumber + '\'' +
            ", previousChangeListDate=" + previousChangeListDate +
            ", lastGoodBuildDate=" + lastGoodBuildDate +
            ", lastGoodChangeListDate=" + lastGoodChangeListDate +
            ", lastGoodChangeListNumber='" + lastGoodChangeListNumber + '\'' +
            ", lastGoodBuildNumber=" + lastGoodBuildNumber +
            ", buildID=" + buildID +
            ", projectID=" + projectID +
            ", projectName='" + projectName + '\'' +
            ", upstreamBuildRunID=" + upstreamBuildRunID +
            ", upstreamBuildName='" + upstreamBuildName + '\'' +
            ", upstreamBuildID=" + upstreamBuildID +
            '}';
  }
}
