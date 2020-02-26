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
import java.util.Date;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.CommandStoppedException;

/**
 * PVCSChangeLogHandler is a call-back interfaces that is
 * calles by {@link PVCSVlogParser} whenerver a new change
 * is detected.
 *
 * @see PVCSVlogParser
 */
public interface PVCSVlogHandler {

  /**
   * This method is called before the handle is called first
   * time.
   */
  void beforeHandle();


  /**
   * This method is called when a revsion is found in a
   * change log. It is guaranteed that it is called only
   * once for a single revesion.
   *
   * @param changeDate
   * @param revisionDescription
   * @param owner
   * @param branch
   * @param filePath
   * @param revision
   * @param changeType
   */
  void handle(final Date changeDate, final StringBuffer revisionDescription,
              final String owner, final String branch, final String filePath,
              final String revision, final byte changeType) throws IOException, CommandStoppedException, AgentFailureException;


  /**
   * This method is called fater the handle is called last
   * time.
   */
  void afterHandle() throws IOException, CommandStoppedException, AgentFailureException;
}
