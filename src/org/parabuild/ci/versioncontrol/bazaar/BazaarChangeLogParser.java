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
package org.parabuild.ci.versioncontrol.bazaar;

import org.apache.log4j.Logger;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Bazaar change log parser
 * <p/>
 *
 * @author Slava Imeshev
 * @noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException,StringBufferReplaceableByString
 */
public final class BazaarChangeLogParser {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(BazaarChangeLogParser.class); // NOPMD

  public static final String CHANGE_LIST_DELIMITER = "------------------------------------------------------------";

  /**
   * Maximum number of change lists
   */
  private int maxChangeLists = 10;

  /**
   * Maximum number of files in change list.
   */
  private int maxChangeListSize = 100;

  private static final String REVNO = "revno: ";
  private static final int REVNO_LEN = REVNO.length();

  private static final String COMMITTER = "committer: ";
  private static final int COMMITTER_LEN = COMMITTER.length();

  private static final String BRANCH_NICK = "branch nick: ";
  private static final int BRANCH_NICK_LEN = BRANCH_NICK.length();

  private static final String TIMESTAMP = "timestamp: ";
  private static final int TIMESTAMP_LEN = TIMESTAMP.length();


  /**
   * Sets a maximum number of change lists.
   *
   * @param maxChangeLists the maximum number of change lists.
   */
  public void setMaxChangeLists(final int maxChangeLists) {
    this.maxChangeLists = maxChangeLists;
  }


  /**
   * Sets a maximum number of files in a change list.
   *
   * @param maxChangeListSize the maximum number of files in a change list.
   */
  public void setMaxChangeListSize(final int maxChangeListSize) {
    this.maxChangeListSize = maxChangeListSize;
  }


  /**
   * Parses a change log file produced by the command.
   * <pre>
   *  bzr log -v /repository/path
   * </pre>
   *
   * @param changeLogFile the change log file produced by the command.
   * @return a list of <code>ChangeList</code> objects.
   * @throws java.io.IOException if I/O error occurs.
   * @noinspection ReuseOfLocalVariable,HardcodedLineSeparator
   */
  public List parseChangeLog(final File changeLogFile) throws IOException {

    /*

------------------------------------------------------------
revno: 5
committer: test_user <test@parabuildci.org>
branch nick: bazaar
timestamp: Sun 2010-04-04 18:14:04 -0700
message:
  Removed files
removed:
  test/sourceline/file-to-delete-1.txt
  test/sourceline/file-to-delete-2.txt
------------------------------------------------------------
revno: 4
committer: test_user <test@parabuildci.org>
branch nick: bazaar
timestamp: Sun 2010-04-04 18:13:34 -0700
message:
  Added files for future deletion
added:
  test/sourceline/file-to-delete-1.txt
  test/sourceline/file-to-delete-2.txt
------------------------------------------------------------
revno: 3
committer: test_user <test@parabuildci.org>
branch nick: bazaar
timestamp: Sun 2010-04-04 18:07:19 -0700
message:
  Added a line to the codeline with spaces
modified:
  test/sourceline with spaces/readme.txt
------------------------------------------------------------
revno: 2
committer: test_user <test@parabuildci.org>
branch nick: bazaar
timestamp: Sun 2010-04-04 18:05:55 -0700
message:
  Added a line
modified:
  test/sourceline/alwaysvalid/src/readme.txt
------------------------------------------------------------
revno: 1
committer: root <root@baybridge.cacheonix.com>
branch nick: bazaar
timestamp: Sun 2010-04-04 17:50:51 -0700
message:
  Initialized repozitory
added:
  test/
  test/second_sourceline/
  test/second_sourceline/src/
  test/second_sourceline/src/readme.txt
  test/second_sourceline/src/second-file.txt
  test/source_path/
  test/source_path/file_to_ignore_in_branch_view.txt
  test/source_path/mode.txt
  test/source_path/readme.txt
  test/sourceline/
  test/sourceline with spaces/
  test/sourceline with spaces/readme.txt
  test/sourceline/alwaysvalid/
  test/sourceline/alwaysvalid/src/
  test/sourceline/alwaysvalid/src/readme.txt
  test/sourceline/alwaysvalid/src/symlinked_readme.txt

<modified>, <unknown>, <renamed>, <kind-changed>, <removed>, <conflicts>, <added>
     */


    // Prepare
    BufferedReader reader = null;
    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Started parsing Bazaar change log");
      }
      final List result = new LinkedList();
      final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE yyyy-MM-dd HH:mm:ss Z", Locale.US);
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(changeLogFile)));

      // Traverse to first change.
      String line = IoUtils.readToNotPast(reader, CHANGE_LIST_DELIMITER, null);
      if (line == null) {
        return result;
      }
      while (line != null) {
        try {
          line = reader.readLine();
          if (line == null) {
            return result;
          }

          // Revision number
          final String revno = line.substring(REVNO_LEN);

          // Committer
          line = reader.readLine();
          if (line.startsWith("tags:")) {
            line = reader.readLine();
          }
          final String userAndEmail = line.substring(COMMITTER_LEN);
          final int lIndex = userAndEmail.indexOf('<');
          final String email = userAndEmail.substring(lIndex + 1, userAndEmail.indexOf('>'));

          // Branch nick
          line = reader.readLine();
          final String branchNick = line.substring(BRANCH_NICK_LEN);

          // Timestamp
          line = reader.readLine();
          final Date timeStamp;
          try {
            timeStamp = dateFormat.parse(line.substring(TIMESTAMP_LEN));
          } catch (ParseException e) {
            throw new IOException("Invalid date format, line: " + line);
          }

          // Message
          final StringBuffer message = new StringBuffer(100);
          line = reader.readLine(); // Read "message:"
          line = reader.readLine();
          boolean multiline = false;
          while (line.startsWith("  ")) {
            if (multiline) {
              message.append('\n');
            }
            message.append(line.substring(2));
            multiline = true;
            line = reader.readLine();
          }

          // Changes
          final ChangeList changeList = new ChangeList();
          changeList.setEmail(email);
          changeList.setUser(email);
          changeList.setNumber(revno);
          changeList.setDescription(message.toString());
          changeList.setCreatedAt(timeStamp);
          changeList.setBranch(branchNick);
          result.add(changeList);
          while (line != null && !line.startsWith(CHANGE_LIST_DELIMITER)) {

            byte type = Change.TYPE_UNKNOWN;
            if (line.startsWith("modified:")) {
              type = Change.TYPE_MODIFIED;
            } else if (line.startsWith("removed:")) {
              type = Change.TYPE_REMOVED;
            } else if (line.startsWith("added:")) {
              type = Change.TYPE_ADDED;
            } else if (line.startsWith("renamed:")) {
              type = Change.TYPE_RENAMED;
            } else if (line.startsWith("kind-changed:")) {
              type = Change.TYPE_KIND_CHANGED;
            } else if (line.startsWith("conflicts:")) {
              type = Change.TYPE_CONFLICTS;
            }
            line = reader.readLine(); // Read file

            while (line != null && line.startsWith("  ") && !line.startsWith(CHANGE_LIST_DELIMITER)) {
              if (changeList.getOriginalSize() < maxChangeListSize) {
                changeList.getChanges().add(new Change(line.substring(2), revno, type));
              } else {
                changeList.setTruncated(true);
              }
              changeList.incrementOriginalSize();
              line = reader.readLine();
            }

            if (result.size() >= maxChangeLists) {
              return result;
            }
          }
        } catch (RuntimeException e) {
          final IOException ioe = new IOException("Unexpected error while processing Bazaar change log, line: " + line);
          ioe.initCause(e);
          throw ioe;
        }
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("Finished parsing Bazaar change log");
      }
      return result;
    } finally {
      IoUtils.closeHard(reader);
    }
  }


  public String toString() {
    return "BazaarChangeLogParser{" +
            "maxChangeLists=" + maxChangeLists +
            ", maxChangeListSize=" + maxChangeListSize +
            '}';
  }
}