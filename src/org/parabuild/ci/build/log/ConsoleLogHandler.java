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
package org.parabuild.ci.build.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.search.SearchManager;

import java.util.Iterator;
import java.util.List;


/**
 * Unlike custom log handlers, the main build log handler does
 * not move a log as it's already in the archive dir.
 * <p/>
 * It just indexes the console build log.
 * <p/>
 * Main build log is a combined stdin/stdout output of the build
 * script.
 *
 * @see LogHandlerFactory#makeLogHandler
 */
public final class ConsoleLogHandler implements LogHandler {

  private static final Log log = LogFactory.getLog(ConsoleLogHandler.class);

  private final int stepRunID;
  private final int buildRunConfigID;


  /**
   * Constructor.
   *
   * @param buildRunConfigID that to process
   * @param stepRunID that to process
   */
  public ConsoleLogHandler(final int buildRunConfigID, final int stepRunID) {
    this.buildRunConfigID = buildRunConfigID;
    this.stepRunID = stepRunID;
  }


  /**
   * Sends request to index main console build logs.
   */
  public void process() {

    // TODO: unit test - suggestion - search build log upon exit of a build run (SSTestBuildRunner).

    try {
      final ArchiveManager am = ArchiveManagerFactory.getArchiveManager(buildRunConfigID);
      final List mainStepLogs = ConfigurationManager.getInstance().getStepLogs(stepRunID, StepLog.TYPE_MAIN);
      for (final Iterator i = mainStepLogs.iterator(); i.hasNext();) {
        final StepLog stepLog = (StepLog)i.next();
        if (stepLog.getType() != StepLog.TYPE_MAIN) {
          log.warn("Log being processed is not a main log :" + stepLog.getType());
          continue;
        }
        SearchManager.getInstance().index(stepLog, am.getArchivedLogHome(stepLog));
      }
    } catch (final Exception e) {
      // call reusable error reporting method
      reportLogProcessingException(e);
    }
  }


  private void reportLogProcessingException(final Exception e) {
    final Error error = new Error("Error while processing build logs");
    error.setBuildID(buildRunConfigID);
    error.setSendEmail(true);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_LOGGING);
    error.setDetails(e);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  public String toString() {
    return "ConsoleLogHandler{" +
      "stepRunID=" + stepRunID +
      ", buildRunConfigID=" + buildRunConfigID +
      '}';
  }
}