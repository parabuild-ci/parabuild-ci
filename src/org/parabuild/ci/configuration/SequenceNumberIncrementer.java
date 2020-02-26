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

import org.parabuild.ci.util.*;

/**
 * Increments a system-wide sequence number.
 * @noinspection ClassHasNoToStringMethod
 */
public final class SequenceNumberIncrementer {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(SequenceNumberIncrementer.class); // NOPMD
  private final String sequenceName;



  public SequenceNumberIncrementer(final String sequenceName) {
    this.sequenceName = ArgumentValidator.validateArgumentNotBlank(sequenceName, "sequence name");
  }


  /**
   * Creates a new sequence number for active build
   *
   * @param conn
   */
  public int incrementSequenceNumber(final Connection conn) throws SQLException {
    PreparedStatement psSelect = null; // NOPMD
    PreparedStatement psUpdate = null; // NOPMD
    ResultSet rs = null;  // NOPMD
    try {
      psSelect = conn.prepareStatement("select ID, VALUE from SYSTEM_PROPERTY where NAME = ?");
      psSelect.setString(1, sequenceName);
      rs = psSelect.executeQuery();
      final int newValue;
      if (rs.next()) {
        // update existing property
        final int id = rs.getInt(1);
        newValue = Integer.parseInt(rs.getString(2)) + 1;
        psUpdate = conn.prepareStatement("update SYSTEM_PROPERTY set VALUE = ? where ID = ?");
        psUpdate.setString(1, Integer.toString(newValue));
        psUpdate.setInt(2, id);
        psUpdate.executeUpdate();
      } else {
        // create new property
        PreparedStatement psInsert = null;
        try {
          newValue = 0;
          psInsert = conn.prepareStatement("insert into SYSTEM_PROPERTY (NAME, VALUE, TIMESTAMP) values (?, ?, 0)");
          psInsert.setString(1, sequenceName);
          psInsert.setString(2, Integer.toString(newValue));
          psInsert.executeUpdate();
        } finally {
          IoUtils.closeHard(psInsert);
        }
      }
      return newValue;
    } finally {
      IoUtils.closeHard(rs);
      IoUtils.closeHard(psSelect);
      IoUtils.closeHard(psUpdate);
    }
  }
}
