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
package org.parabuild.ci.versioncontrol.perforce;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.ThreadUtils;
import org.parabuild.ci.configuration.ChangeListsAndIssues;

/**
 */
final class P4AccumulatingChangeListChunkDriver implements P4ChangeListChunkDriver {

  private final P4Command command;
  private final P4ChangeLogParser logParser;
  private final ChangeListsAndIssues changeListsAndIssues;


  P4AccumulatingChangeListChunkDriver(final P4Command command, final ChangeListsAndIssues changeListsAndIssues, final int maxChangeListSize, final boolean jobCollectionEnabled) {
    this.command = command;
    this.logParser = new P4ChangeLogParser(maxChangeListSize);
    this.changeListsAndIssues = changeListsAndIssues;
    this.logParser.enableJobCollection(jobCollectionEnabled);
  }


  public void process(final List changeListNumbers) throws IOException, CommandStoppedException, BuildException, AgentFailureException {
    FileInputStream fis = null;
    try {
      command.setExeArguments("describe -s " + changeNumbersToString(changeListNumbers));
      command.setDescription("describe command");
      command.execute();
      fis = new FileInputStream(command.getStdoutFile());
      logParser.parseDescribeLog(changeListsAndIssues, fis);
      ThreadUtils.checkIfInterrupted();
    } finally {
      IoUtils.closeHard(fis);
      command.cleanup();
    }
  }


  /**
   * Converst list of integers to string
   */
  private static String changeNumbersToString(final List changeNumbers) {
    final StringBuilder sb = new StringBuilder(500);
    for (final Iterator i = changeNumbers.iterator(); i.hasNext();) {
      sb.append(' ').append((String) i.next());
    }
    return sb.toString();
  }


  public String toString() {
    return "P4AccumulatingChangeListChunkDriver{" +
            "command=" + command +
            ", logParser=" + logParser +
            ", changeListsAndIssues=" + changeListsAndIssues +
            '}';
  }
}
