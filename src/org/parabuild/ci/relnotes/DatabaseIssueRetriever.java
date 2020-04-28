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
package org.parabuild.ci.relnotes;

import org.parabuild.ci.util.BuildException;

import java.sql.Date;
import java.util.List;

/**
 * Responsible for getting issues from external issue databases
 * acording to tracker configuration.
 */
interface DatabaseIssueRetriever {

  /**
   * Retrieves issues from issue database
   *
   * @param fromDate starting date, inclusive.
   * @param toDate enfing date, inclusive
   *
   * @return List of Issues
   *
   * @throws BuildException if errors happen.
   */
  List retrieveBugs(Date fromDate, Date toDate) throws BuildException;
}

