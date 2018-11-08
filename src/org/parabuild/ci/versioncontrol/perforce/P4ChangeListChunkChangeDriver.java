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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.ThreadUtils;

/**
 * Gets change details from the change list numbers and
 * calls change driver for each change found.
 */
final class P4ChangeListChunkChangeDriver implements P4ChangeListChunkDriver {

  private final P4Command command;
  private final P4ChangeDriver changeDriver;
  private final P4ChangeLogParser logParser;


  /**
   * Constructor.
   *
   * @param command
   * @param changeDriver
   */
  public P4ChangeListChunkChangeDriver(final P4Command command, final P4ChangeDriver changeDriver) {
    this.command = command;
    this.changeDriver = changeDriver;
    this.logParser = new P4ChangeLogParser(Integer.MAX_VALUE);
  }


  public void process(final List changeListNumbers) throws IOException, CommandStoppedException, BuildException, AgentFailureException {

    // filter numbers
    final List filteredNumbers = new ArrayList(changeListNumbers.size());
    for (int i = 0; i < changeListNumbers.size(); i++) {
      final String changeListNumber = (String) changeListNumbers.get(i);
      if (changeDriver.acceptsNumber(changeListNumber)) {
        filteredNumbers.add(changeListNumber);
      }
    }

    // process numbers
    FileInputStream fis = null;
    try {
      command.setExeArguments("describe -s " + changeNumbersToString(filteredNumbers));
      command.setDescription("describe command");
      command.execute();
      fis = new FileInputStream(command.getStdoutFile());
      logParser.parseDescribeLog(changeDriver, fis);
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

}