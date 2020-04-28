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
package org.parabuild.ci.configuration;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Single step upgrader upgrades database schema one single version up.
 */
public interface SingleStepSchemaUpgrader {

  /**
   * Perform upgrade.
   */
  void upgrade(Connection connection, int upgradeToVersion) throws SQLException;


  /**
   * @return version this upgrader upgrades to.
   */
  int upgraderVersion();
}
