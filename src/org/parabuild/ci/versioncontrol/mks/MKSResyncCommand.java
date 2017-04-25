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
package org.parabuild.ci.versioncontrol.mks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.StderrLineProcessor;

/**
 * MKS resync command
 */
final class MKSResyncCommand extends MKSCommand {

  private final MKSResyncCommandParameters parameters;
  private final MKSResyncStderrLineProcessor stderrLineProcessor;


  /**
   * Creates MKSCommand.
   *
   * @param agent
   */
  MKSResyncCommand(final Agent agent, final MKSResyncCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.parameters = parameters;
    this.stderrLineProcessor = new MKSResyncStderrLineProcessor(agent.getCheckoutDirName());
    super.setStderrLineProcessor(stderrLineProcessor);
  }


  protected String mksCommand() {
    return "resync";
  }


  protected String mksCommandArguments() throws IOException, AgentFailureException {
    final StringBuffer result = new StringBuffer(100);
    appendCommand(result, "--restoreTimestamp");
    appendCommand(result, "--includeDropped");
    appendCommand(result, "-R");
    appendCommand(result, "-f"); // overwirite
    appendCommand(result, "-S", StringUtils.putIntoDoubleQuotes(agent.getFileDescriptor(agent.getCheckoutDirName() + '/' + getProjectName()).getCanonicalPath()));
    return result.toString();
  }


  private String getProjectName() {
    final String projectPath = parameters.getProject().replace('\\', '/');
    return projectPath.substring(projectPath.lastIndexOf('/'));
  }


  /**
   * Callback method - this method is called right after call to
   * execute.
   * <p/>
   * Analizes log for known errors
   *
   * @param resultCode - execute command result code. /**
   *                   Analizes log for known errors
   * @throws IOException if there are errors contains
   *                     error descripting
   */
  protected void postExecute(final int resultCode) throws IOException, AgentFailureException {
    // first process the error log
    super.postExecute(resultCode);
    // delete accumulated items
    // REVIEWME: consider passing as a list to agent.
    for (final Iterator i = stderrLineProcessor.toDelete().iterator(); i.hasNext();) {
      agent.deleteFileUnderCheckoutDir((String) i.next());
    }
  }


  private static final class MKSResyncStderrLineProcessor implements StderrLineProcessor {

    private final CommonStderrLineProcessor commonStderrLineProcessor;
    private final List toDelete;


    /**
     * Create common stderr analyzer.
     *
     * @param checkoutDir String is used to excluded staerr lines
     *                    that start with this dir.
     */
    public MKSResyncStderrLineProcessor(final String checkoutDir) {
      this.commonStderrLineProcessor = new CommonStderrLineProcessor(checkoutDir);
      this.toDelete = new ArrayList(111);
    }


    /**
     * Process line index.
     * <p/>
     * If there we files that should have been deleted as a part
     * of sync will accumulate them for deletion in {@link
     * MKSCoCommand#postExecute(int)}.
     *
     * @param index
     * @param line
     * @return result code
     * @see #RESULT_ADD_TO_ERRORS
     * @see #RESULT_IGNORE
     * @see MKSResyncCommand#postExecute(int)
     */
    public int processLine(final int index, final String line) {
      if (line.startsWith("Resynchronizing files...")) return RESULT_IGNORE;
      if (line.startsWith("Resynchronizing file...")) return RESULT_IGNORE;
      if (line.endsWith("No revision existed at the specified date.")) {
        // remember the file to be deleted
        toDelete.add(line.substring(0, line.indexOf(": ")));
        return RESULT_IGNORE;
      }
      return commonStderrLineProcessor.processLine(index, line);
    }


    public List toDelete() {
      return toDelete;
    }
  }
}
