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

import java.io.IOException;
import java.util.List;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;

/**
 * A driver for new chages found.
 *
 * @see P4SourceControl
 */
public interface P4ChangeListChunkDriver {

  /**
   * Perforce's getChanges calls this method when the changes are collected.
   *
   * @param changeListNumbers list of String change list numbers.
   */
  void process(final List changeListNumbers) throws IOException, CommandStoppedException, BuildException, AgentFailureException;
}
