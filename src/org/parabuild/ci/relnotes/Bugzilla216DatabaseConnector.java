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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * -- -- Table structure for table 'bugs' --
 * <p/>
 * CREATE TABLE bugs ( bug_id mediumint(9) NOT NULL
 * auto_increment, groupset bigint(20) NOT NULL default '0',
 * assigned_to mediumint(9) NOT NULL default '0', bug_file_loc
 * text, bug_severity enum('blocker','critical','major','normal','minor','trivial','enhancement')
 * NOT NULL default 'blocker', bug_status enum('UNCONFIRMED','NEW','ASSIGNED','REOPENED','RESOLVED','VERIFIED','CLOSED')
 * NOT NULL default 'UNCONFIRMED', creation_ts datetime NOT NULL
 * default '0000-00-00 00:00:00', delta_ts timestamp(14) NOT
 * NULL, short_desc mediumtext, op_sys enum('All','Windows
 * 3.1','Windows 95','Windows 98','Windows ME','Windows
 * 2000','Windows NT','Windows XP','Mac System 7','Mac System
 * 7.5','Mac System 7.6.1','Mac System 8.0','Mac System 8.5','Mac
 * System 8.6','Mac System 9.x','MacOS X','Linux','BSDI','FreeBSD','NetBSD','OpenBSD','AIX','BeOS','HP-UX','IRIX','Neutrino','OpenVMS','OS/2','OSF/1','Solaris','SunOS','other')
 * NOT NULL default 'All', priority enum('P1','P2','P3','P4','P5')
 * NOT NULL default 'P1', product varchar(64) NOT NULL default
 * '', rep_platform enum('All','DEC','HP','Macintosh','PC','SGI','Sun','Other')
 * default NULL, reporter mediumint(9) NOT NULL default '0',
 * version varchar(64) NOT NULL default '', component varchar(50)
 * NOT NULL default '', resolution enum('','FIXED','INVALID','WONTFIX','LATER','REMIND','DUPLICATE','WORKSFORME','MOVED')
 * NOT NULL default '', target_milestone varchar(20) NOT NULL
 * default '---', qa_contact mediumint(9) NOT NULL default '0',
 * status_whiteboard mediumtext NOT NULL, votes mediumint(9) NOT
 * NULL default '0', keywords mediumtext NOT NULL, lastdiffed
 * datetime NOT NULL default '0000-00-00 00:00:00', everconfirmed
 * tinyint(4) NOT NULL default '0', reporter_accessible
 * tinyint(4) NOT NULL default '1', cclist_accessible tinyint(4)
 * NOT NULL default '1', PRIMARY KEY  (bug_id), KEY assigned_to
 * (assigned_to), KEY creation_ts (creation_ts), KEY delta_ts
 * (delta_ts), KEY bug_severity (bug_severity), KEY bug_status
 * (bug_status), KEY op_sys (op_sys), KEY priority (priority),
 * KEY product (product), KEY reporter (reporter), KEY version
 * (version), KEY component (component), KEY resolution
 * (resolution), KEY target_milestone (target_milestone), KEY
 * qa_contact (qa_contact), KEY votes (votes) ) TYPE=InnoDB;
 * <p/>
 * <p/>
 * -- -- Table structure for table 'fielddefs' --
 * <p/>
 * CREATE TABLE fielddefs ( fieldid mediumint(9) NOT NULL
 * auto_increment, name varchar(64) NOT NULL default '',
 * description mediumtext NOT NULL, mailhead tinyint(4) NOT NULL
 * default '0', sortkey smallint(6) NOT NULL default '0', PRIMARY
 * KEY  (fieldid), UNIQUE KEY name (name), KEY sortkey (sortkey)
 * ) TYPE=InnoDB;
 * <p/>
 * <p/>
 * -- -- Table structure for table 'bugs_activity' --
 * <p/>
 * CREATE TABLE bugs_activity ( bug_id mediumint(9) NOT NULL
 * default '0', attach_id mediumint(9) default NULL, who
 * mediumint(9) NOT NULL default '0', bug_when datetime NOT NULL
 * default '0000-00-00 00:00:00', fieldid mediumint(9) NOT NULL
 * default '0', added tinytext, removed tinytext, KEY bug_id
 * (bug_id), KEY bug_when (bug_when), KEY fieldid (fieldid) )
 * TYPE=InnoDB;
 * <p/>
 * -- -- Dumping data for table 'bugs_activity' --
 * <p/>
 * <p/>
 * INSERT INTO bugs_activity VALUES (3,NULL,3,'2003-02-21
 * 21:20:21',12,'critical','normal'); INSERT INTO bugs_activity
 * VALUES (5,NULL,3,'2003-02-21 21:29:20',21,'6',''); INSERT INTO
 * bugs_activity VALUES (6,NULL,3,'2003-02-21
 * 21:29:20',20,'5',''); INSERT INTO bugs_activity VALUES
 * (8,NULL,3,'2003-02-21 23:05:42',8,'RESOLVED','NEW'); INSERT
 * INTO bugs_activity VALUES (8,NULL,3,'2003-02-21
 * 23:05:42',11,'FIXED',''); INSERT INTO bugs_activity VALUES
 * (8,NULL,3,'2003-02-21 23:06:25',8,'CLOSED','RESOLVED'); INSERT
 * INTO bugs_activity VALUES (7,NULL,3,'2003-02-21
 * 23:28:29',19,'simeshev@parabuilci.org',''); INSERT INTO
 * bugs_activity VALUES (3,NULL,3,'2003-02-21
 * 23:47:51',13,'P1','P2'); INSERT INTO bugs_activity VALUES
 * (2,NULL,3,'2003-02-21 23:48:16',13,'P3','P1'); INSERT INTO
 * bugs_activity VALUES (2,NULL,3,'2003-02-21 23:48:16',2,'Set up
 * NTP demon','Set up NTP demon '); INSERT INTO bugs_activity
 * VALUES (5,NULL,3,'2003-02-21 23:50:25',2,'Set up linux on the
 * www.uwlfaq.com box','Set up static linux on the www.uwlfaq.com
 * box'); INSERT INTO bugs_activity VALUES (10,NULL,3,'2003-02-22
 * 22:32:15',12,'enhancement','normal'); INSERT INTO
 * bugs_activity VALUES (3,NULL,3,'2003-02-23
 * 12:45:33',8,'RESOLVED','NEW'); INSERT INTO bugs_activity
 * VALUES (3,NULL,3,'2003-02-23 12:45:33',11,'FIXED',''); INSERT
 * INTO bugs_activity VALUES (3,NULL,3,'2003-02-23
 * 12:45:46',8,'CLOSED','RESOLVED');
 */
final class Bugzilla216DatabaseConnector extends AbstractBugzillaDatabaseConnector {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(Bugzilla216DatabaseConnector.class); // NOPMD


  /**
   * Constructor
   */
  public Bugzilla216DatabaseConnector(final BugzillaMySQLConnectionFactory connectionFactory) {
    super(connectionFactory);
  }


  public Collection requestBugsFromBugzilla(final String productName, final String productVersion, final Date fromDate, final Date toDate) {
    // validate
    if (StringUtils.isBlank(productName)) throw new IllegalStateException("Product name is blank");
    if (toDate == null) throw new IllegalStateException("\"To\" date is blank");
    if (fromDate == null) throw new IllegalStateException("\"From\" date is blank");

    Connection conn = null; // NOPMD
    PreparedStatement pstmt = null;  // NOPMD
    PreparedStatement pstmt2 = null; // NOPMD
    ResultSet rs = null;  // NOPMD
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
      sql.append("   bugs.bug_id, bugs.short_desc, bugs.product, bugs.version, bugs_activity.added, bugs_activity.bug_when ");
      sql.append(" from ");
      sql.append("   bugs, bugs_activity ");
      sql.append(" where ");
      sql.append("   bugs_activity.fieldid=? and bugs.bug_id = bugs_activity.bug_id ");
      sql.append("   and bugs.product = ? ");
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
        final Integer key = Integer.valueOf(bugID);
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
