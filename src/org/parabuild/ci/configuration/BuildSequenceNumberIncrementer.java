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

import java.sql.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.object.*;

/**
 * Increments build sequence number.
 */
public final class BuildSequenceNumberIncrementer {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(BuildSequenceNumberIncrementer.class); // NOPMD
  private final SequenceNumberIncrementer delegate;


  public BuildSequenceNumberIncrementer() {
    delegate = new SequenceNumberIncrementer(SystemProperty.BUILD_SEQUENCE_NUMBER);
  }


  /**
   * Creates a new sequence number for active build
   *
   * @param conn
   */
  public int incrementBuildSequenceNumber(final Connection conn) throws SQLException {
    return delegate.incrementSequenceNumber(conn);
  }


  public String toString() {
    return "BuildSequenceNumberIncrementer{" +
      "delegate=" + delegate +
      '}';
  }
}
