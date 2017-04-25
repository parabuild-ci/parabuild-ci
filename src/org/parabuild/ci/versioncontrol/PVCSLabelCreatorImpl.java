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
package org.parabuild.ci.versioncontrol;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 * This class is responsible for creating PCLI labeing
 * scripts.
 */
final class PVCSLabelCreatorImpl implements PVCSLabelCreator {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(PVCSLabelCreatorImpl.class); // NOPMD

  private final Agent agent;
  private final String label;
  private final PVCSCommandParameters parameters;


  public PVCSLabelCreatorImpl(final Agent agent, final PVCSCommandParameters parameters, final String label) {
    this.agent = agent;
    this.label = label;
    this.parameters = parameters;
  }


  /**
   * Labels given list of {@link PVCSRevision} objects
   *
   * @param collectedRevisionBlock list of {@link
   *                               PVCSRevision} objects to label
   */
  public void label(final List collectedRevisionBlock) throws IOException, CommandStoppedException, AgentFailureException {
    final StringBuffer sb = new StringBuffer(2000);
    // compose the beginning
    // ...

    // compose list part
    final int n = collectedRevisionBlock.size();
    for (int i = 0; i < n; i++) {
      final PVCSRevision revision = (PVCSRevision) collectedRevisionBlock.get(i);
      final String repository = parameters.getRepository().startsWith("\\\\") ? '\\' + parameters.getRepository() : parameters.getRepository();

      sb.append("label ")
              .append("-pr\"").append(repository).append('\"')
              .append(" -r").append(revision.getRevision())
              .append(" -v").append(StringUtils.putIntoDoubleQuotes(label))
              .append(' ').append(StringUtils.putIntoDoubleQuotes(revision.getFilePath()))
              .append('\n')
              ;
    }

//    if (log.isDebugEnabled()) log.debug("sb: " + sb);
    // create and execute run command
    PVCSRunCommand command = null;
    try {
      command = new PVCSRunCommand(agent, new PVCSRunCommandParameters(parameters, sb.toString()));
      command.execute();
    } finally {
      if (command != null) {
        command.cleanup();
      }
    }
  }
}
