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
package org.parabuild.ci.versioncontrol.accurev;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.StderrLineProcessor;

import java.io.IOException;

/**
 * Accurev mksnap command.
 * <p/>
 * accurev mksnap -s <snapshot> -b <existing-stream> -t <time-spec>
 * <p/>
 */
final class AccurevMksnapCommand extends AccurevCommand {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(AccurevMksnapCommand.class); // NOPMD
  private final String txNumber;
  private final String snapshotName;


  /**
   */
  AccurevMksnapCommand(final Agent agent, final AccurevCommandParameters parameters,
                       final String snapshotName, final String txNumber) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.snapshotName = snapshotName;
    this.txNumber = txNumber;
    this.setStderrLineProcessor(new StderrLineProcessor() {
      public int processLine(final int index, final String line) {
//        if (LOG.isDebugEnabled()) LOG.debug("line: \"" + line + "\"");
        if (line.startsWith("Stream") && line.endsWith("already exists")) {
          return RESULT_IGNORE;
        }
        return RESULT_ADD_TO_ERRORS;
      }
    });
  }


  /**
   * Requests changes in the reverse order of transactions.
   */
  protected void preExecute() throws IOException, AgentFailureException {
    super.preExecute();
    final StringBuffer cmd = new StringBuffer(200);
    appendCommand(cmd, StringUtils.putIntoDoubleQuotes(getParameters().getExePath()));
    appendCommand(cmd, "mksnap");
    appendCommand(cmd, "-s", snapshotName);
    appendCommand(cmd, "-b", getParameters().getBackingStream());
    appendCommand(cmd, "-t", txNumber);
    setCommand(cmd.toString());
    if (LOG.isDebugEnabled()) {
      LOG.debug("cmd: " + cmd);
    }
  }


  public String toString() {
    return "AccurevMksnapCommand{" +
            "snapshotName='" + snapshotName + '\'' +
            ", txNumber=" + txNumber +
            "} " + super.toString();
  }
}