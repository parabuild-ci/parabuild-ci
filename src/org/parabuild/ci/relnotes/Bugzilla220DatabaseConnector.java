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

import java.sql.*;
import java.sql.Date;
import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.util.*;

/**
 * Bugzilla connector for bugzilla 2.20
 */
final class Bugzilla220DatabaseConnector extends AbstractBugzillaDatabaseConnector {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(Bugzilla220DatabaseConnector.class); // NOPMD


  /**
   * Constructor
   */
  public Bugzilla220DatabaseConnector(final BugzillaMySQLConnectionFactory connectionFactory) {
    super(connectionFactory);
  }


  public Collection requestBugsFromBugzilla(final String productName, final String productVersion, final Date fromDate, final Date toDate) {
    // validate
    if (StringUtils.isBlank(productName)) throw new IllegalStateException("Product name is blank");
    if (toDate == null) throw new IllegalStateException("\"To\" date is blank");
    if (fromDate == null) throw new IllegalStateException("\"From\" date is blank");

    Connection conn = null; // NOPMD
    PreparedStatement pstmt = null; // NOPMD
    PreparedStatement pstmt2 = null; // NOPMD
    ResultSet rs = null; // NOPMD
    ResultSet rs2 = null; // NOPMD
    final HashMap found = new HashMap(11);

    try {
      // connect
      conn = connect();

      // get ID for bug_status field
      pstmt = conn.prepareStatement("select fieldid from fielddefs where name='bug_status'");
      rs = pstmt.executeQuery();
      if (!rs.next()) {
        throw new SQLException("Field bug_status not found in the list of allowed fields");
      }
      final int bugStatusFieldID = rs.getInt(1);
      IoUtils.closeHard(rs);
      IoUtils.closeHard(pstmt);

      // request
      final StringBuilder sql = new StringBuilder(500);
      sql.append(" select ");
      sql.append("   bugs.bug_id, bugs.short_desc, products.name, bugs.version, bugs_activity.added, bugs_activity.bug_when ");
      sql.append(" from ");
      sql.append("   bugs, products, bugs_activity ");
      sql.append(" where ");
      sql.append("   bugs_activity.fieldid=? and bugs.bug_id = bugs_activity.bug_id ");
      sql.append("   and bugs.product_id = products.product_id ");
      sql.append("   and products.name = ? ");
      sql.append("   and bugs_activity.added in ('RESOLVED', 'CLOSED')");
      sql.append("   and bugs_activity.bug_when between ? and ? ");
      if (!StringUtils.isBlank(productVersion)) {
        // add version if provided
        sql.append("   and bugs.version = ? ");
      }
      sql.append(" order by bugs_activity.bug_when desc");

      // preExecute
      pstmt = conn.prepareStatement(sql.toString());
      pstmt.setInt(1, bugStatusFieldID);
      pstmt.setString(2, productName);
      pstmt.setDate(3, fromDate);
      pstmt.setDate(4, toDate);
      if (!StringUtils.isBlank(productVersion)) {
        // add version parameter if provided
        pstmt.setString(5, productVersion);
      }

      // execute and process results
      rs = pstmt.executeQuery();
      while (rs.next()) {
        // get found bug information
        final int bugID = rs.getInt(1);
        final String descr = rs.getString(2);
        final String prod = rs.getString(3);
        final String ver = rs.getString(4);
        final String status = rs.getString(5);
        final Date date = new Date(rs.getTimestamp(6).getTime());
        // store into collector
        final Integer key = new Integer(bugID);
        if (found.get(key) == null) {
          ///////////////////////////
          //if (log.isDebugEnabled()) {
          //  DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          //  log.debug("bugID: " + bugID + ", descr: " + descr + ", status: " + status + ", date: " + df.format(date));
          //}
          //\\\\\\\\\\\\\\\\\\\\\\\\\
          final BugzillaBug bug = new BugzillaBug(bugID, prod, descr, status, ver, date);
          found.put(key, bug);
        }
      }

      // check for post-changes
      pstmt2 = conn.prepareStatement(" select ba.bug_id from bugs_activity ba " +
        "   where ba.bug_id = ? and ba.added = 'REOPENED' " +
        "     and ba.bug_when between ? and ?");
      for (final Iterator i = found.values().iterator(); i.hasNext();) {
        final BugzillaBug bug = (BugzillaBug)i.next();
        // make sure there are no other changes
        pstmt2.setInt(1, bug.getBugID());
        pstmt2.setDate(2, bug.getDate());
        pstmt2.setDate(3, toDate);
        rs2 = pstmt2.executeQuery();
        if (rs2.next()) {
          // there are reopens
          //if (log.isDebugEnabled()) log.debug("found further changes");
          i.remove();
        }
        IoUtils.closeHard(rs2);
      }
    } catch (final SQLException | RuntimeException e) {
      reportUnexpectedError(productName, e);
    } finally {
      // NOTE: simeshev@parabuilci.org - we use this connection in autocommit on mode.
      // IoUtils.commitHard(conn);
      IoUtils.closeHard(rs);
      IoUtils.closeHard(rs2);
      IoUtils.closeHard(pstmt);
      IoUtils.closeHard(pstmt2);
      IoUtils.closeHard(conn);
    }
    return found.values();
  }
}
