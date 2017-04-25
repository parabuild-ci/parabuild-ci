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
package org.parabuild.ci.versioncontrol.mercurial;

import org.apache.log4j.Logger;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
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
public final class MercurialChangeLogParser {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(MercurialChangeLogParser.class); // NOPMD

  private static final String CHANGE_LIST_DELIMITER = "------------------------------------------------------------------------------------------------------------------------";

  /**
   * Maximum number of change lists
   */
  private int maxChangeLists = 10;

  /**
   * Maximum number of files in change list.
   */
  private int maxChangeListSize = 100;

  private static final String CHANGESET = "changeset: ";
  private static final String USER = "user: ";
  private static final String BRANCH = "branch: ";
  private static final String DATE = "date: ";
  private static final String DESCRIPTION = "description:";
  private static final String FILES = "files:";

  private static final String ADDED = "  A ";
  private static final String MODIFIED = "  M ";
  private static final String REMOVED = "  R ";

  private static final int CHANGESET_LEN = CHANGESET.length();
  private static final int USER_LEN = USER.length();
  private static final int DATE_LEN = DATE.length();
  private static final int MODIFIED_LEN = MODIFIED.length();
  private static final int ADDED_LEN = ADDED.length();
  private static final int REMOVED_LEN = REMOVED.length();
  private static final int BRANCH_LEN = BRANCH.length();
  private String changeListToIgnore;
  public static final String HASH = " Hash: ";


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
   * Sets a change list to ignore, format <local number>:<global hash>.
   *
   * @param changeListToIgnore the change list to ignore.
   */
  public void ignoreChangeList(final String changeListToIgnore) {
    this.changeListToIgnore = changeListToIgnore;
  }


  /**
   * Parses a change log file produced by the command.
   * <pre>
   *  hg log -v --style="mercurial-style.txt"
   * </pre>
   *
   * @param changeLogFile the change log file produced by the command.
   * @return a list of <code>ChangeList</code> objects.
   * @throws IOException if I/O error occurs.
   * @noinspection ReuseOfLocalVariable,HardcodedLineSeparator
   */
  public List parseChangeLog(final File changeLogFile) throws IOException {

    /*

------------------------------------------------------------------------------------------------------------------------
changeset: 2:3b8352f003fd
tag: tip
user: Test User <test@parabuildci.org>
date: 2010-06-13 20:28:27 -0700
description:
Made changes to multiple files
files:
  M second_sourceline/src/second-file.txt
  M sourceline/alwaysvalid/src/readme.txt
------------------------------------------------------------------------------------------------------------------------
changeset: 1:87bb00d42a24
user: Test User <test@parabuildci.org>
date: 2010-06-13 20:19:49 -0700
description:
Added line to readme.txt
files:
  M sourceline/alwaysvalid/src/readme.txt
------------------------------------------------------------------------------------------------------------------------
changeset: 0:398519a32d6a
user: Test User <test@parabuildci.org>
date: 2010-06-13 19:09:13 -0700
description:
Populated test repository
files:
  A second_sourceline/src/readme.txt
  A second_sourceline/src/second-file.txt
  A source_path/file_to_ignore_in_branch_view.txt
  A source_path/mode.txt
  A source_path/readme.txt
  A sourceline with spaces/readme.txt
  A sourceline/alwaysvalid/src/readme.txt
  A sourceline/alwaysvalid/src/symlinked_readme.txt
     */


    // Prepare
    BufferedReader reader = null;
    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Started parsing Mercurial change log");
      }
      final List result = new LinkedList();
      final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
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
          final String revnoWithHash = line.substring(CHANGESET_LEN);
          final String number = revnoWithHash.substring(0, revnoWithHash.indexOf(':'));

          // Optional branch
          line = reader.readLine();
          final StringBuffer branch = new StringBuffer(100);
          while (line.startsWith(BRANCH)) {
            branch.append(line.substring(BRANCH_LEN));
            branch.append(',');
            line = reader.readLine();
          }
          if (branch.length() > 0) {
            branch.deleteCharAt(branch.length() - 1);
          }

          while (line.startsWith("tag: ")) {
            line = reader.readLine();
          }

          while (line.startsWith("parent: ")) {
            line = reader.readLine();
          }

          // User
          final String userAndEmail = line.substring(USER_LEN);
          final int lIndex = userAndEmail.indexOf('<');
          final String email;
          final String user;
          if (lIndex < 0) {
            email = "";
            user = "undefined";
          } else {
            email = userAndEmail.substring(lIndex + 1, userAndEmail.indexOf('>'));
            user = email;
          }

          // Date
          line = reader.readLine();
          final Date timeStamp;
          try {
            timeStamp = dateFormat.parse(line.substring(DATE_LEN));
          } catch (ParseException e) {
            throw new IOException("Invalid date format, line: " + line + ", error: " + e.toString());
          }

          // Message
          final StringBuffer message = new StringBuffer(100);
          line = reader.readLine(); // Read "description:"
          if (!line.equals(DESCRIPTION)) {
            throw new IOException("Expected " + DESCRIPTION + " but was " + line);
          }
          line = reader.readLine();
          boolean multiline = false;
          while (line != null && !line.startsWith(FILES)) {
            if (multiline) {
              message.append('\n');
            }
            message.append(line);
            multiline = true;
            line = reader.readLine();
          }

          // Changes
          final String messageAsString = message.toString();
          final ChangeList changeList = new ChangeList();
          changeList.setEmail(user);
          changeList.setUser(email);
          changeList.setNumber(number);
          changeList.setDescription(messageAsString + (messageAsString.endsWith(".") ? "" : ".") + HASH + MercurialUtil.getHash(revnoWithHash) + '.');
          changeList.setCreatedAt(timeStamp);
          changeList.setBranch(branch.toString());
          if (StringUtils.isBlank(changeListToIgnore) || !MercurialUtil.getHash(revnoWithHash).equalsIgnoreCase(changeListToIgnore)) {
            result.add(changeList);
          }

          // Process files
          if (line != null && line.startsWith(FILES)) {
            line = reader.readLine();
          }
          while (line != null && !line.startsWith(CHANGE_LIST_DELIMITER)) {

            byte type = Change.TYPE_UNKNOWN;
            int typeLen = 0;
            if (line.startsWith(MODIFIED)) {
              type = Change.TYPE_MODIFIED;
              typeLen = MODIFIED_LEN;
            } else if (line.startsWith(REMOVED)) {
              type = Change.TYPE_REMOVED;
              typeLen = REMOVED_LEN;
            } else if (line.startsWith(ADDED)) {
              type = Change.TYPE_ADDED;
              typeLen = ADDED_LEN;
            }

            if (changeList.getOriginalSize() < maxChangeListSize) {
              changeList.getChanges().add(new Change(line.substring(typeLen), "", type));
            } else {
              changeList.setTruncated(true);
            }
            changeList.incrementOriginalSize();
            line = reader.readLine();

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