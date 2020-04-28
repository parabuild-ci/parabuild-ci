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

import java.sql.Connection;

/**
 * This interface defines a Connection factory to access Fogbugz
 * database. This factory isolataes from the details of datbase
 * configuration used to create the database connection, such as
 * a database server (MS SQL, MySQL e.t.c).
 */
interface FogbugzConnectionFactory {

  /**
   * Creates a connection according to the IssueTracker
   * configuration.
   *
   * @return new connection according to the IssueTracker
   *         configuration.
   */
  Connection makeConnection();
}
