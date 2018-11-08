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
package org.parabuild.ci.versioncontrol.git;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * Git change log parser
 * <p/>
 *
 * @author Slava Imeshev
 * @noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException, StringBufferReplaceableByString
 * @since Jan 24, 2010 2:31:10 PM
 */
public final class GitTextChangeLogParser {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(GitTextChangeLogParser.class); // NOPMD

  public static final String CHANGE_LIST_DELIMITER = "pArAbIlDb";
  public static final String FIELD_SEPARATOR = "pArAbIlDs";

  private static final int FIELD_SEPARATOR_LENGTH = FIELD_SEPARATOR.length();


  /**
   * Maximum number of change lists
   */
  private int maxChangeLists = 10;

  /**
   * Maximum number of files in change list.
   */
  private int maxChangeListSize = 100;

  /**
   * Tells the parser to use e-mail as a user name.
   */
  private boolean useUserEmailAsUserName = false;


  /**
   * Sets maximum number of change lists.
   *
   * @param maxChangeLists
   */
  public void setMaxChangeLists(final int maxChangeLists) {
    this.maxChangeLists = maxChangeLists;
  }


  /**
   * Sets maximum number of files in a change list.
   *
   * @param maxChangeListSize
   */
  public void setMaxChangeListSize(final int maxChangeListSize) {
    this.maxChangeListSize = maxChangeListSize;
  }


  /**
   * Tells the parser to use e-mail as a user name.
   *
   * @param useUserEmailAsUserName true if the parser must use e-mail as a user name.
   */
  public void setUseUserEmailAsUserName(final boolean useUserEmailAsUserName) {
    this.useUserEmailAsUserName = useUserEmailAsUserName;
  }


  /**
   * Parses a change log file produced by the command
   * <pre>
   *  git log --name-status "--format=pArAbIlDb%hpArAbIlDs%anpArAbIlDs%aepArAbIlDs%cipArAbIlDs%spArAbIlDs"
   * </pre>
   * This command uses string <i>pArAbIlDb</i> as a marker of a beginning of a commit description and
   * string <i>pArAbIlDs</i> as a separator between format placeholders.
   *
   * @param changeLogFile
   * @return
   */
  public List parseChangeLog(final File changeLogFile) throws IOException {

//  pArAbIlDba048490pArAbIlDsunknownpArAbIlDsvimeshev@.(none)pArAbIlDs2010-02-13 15:46:49 -0800pArAbIlDsMultilinepArAbIlDs
//
//  M	sourceline/alwaysvalid/src/readme.txt
//  pArAbIlDb0f2d214pArAbIlDsunknownpArAbIlDsvimeshev@.(none)pArAbIlDs2010-02-13 15:40:01 -0800pArAbIlDsMultilinepArAbIlDs
//
//  M	sourceline/alwaysvalid/src/readme.txt
//  pArAbIlDb275b733pArAbIlDsunknownpArAbIlDsvimeshev@.(none)pArAbIlDs2010-02-13 15:37:16 -0800pArAbIlDsMultilinepArAbIlDs
//
//  M	sourceline/alwaysvalid/src/readme.txt
//  pArAbIlDbcfdeefdpArAbIlDsunknownpArAbIlDsvimeshev@.(none)pArAbIlDs2010-02-13 15:19:04 -0800pArAbIlDsDeleted a filepArAbIlDs
//
//  D	sourceline/alwaysvalid/src/file-to-delete.txt
//  pArAbIlDb2456376pArAbIlDsunknownpArAbIlDsvimeshev@.(none)pArAbIlDs2010-02-13 15:18:21 -0800pArAbIlDsAdded a file for future deletepArAbIlDs
//
//  A	sourceline/alwaysvalid/src/file-to-delete.txt
//  pArAbIlDb5935a9dpArAbIlDsunknownpArAbIlDsvimeshev@.(none)pArAbIlDs2010-02-13 14:54:54 -0800pArAbIlDsModified a linepArAbIlDs
//
//  M	sourceline/alwaysvalid/src/readme.txt
//  pArAbIlDb76606c2pArAbIlDsunknownpArAbIlDsvimeshev@.(none)pArAbIlDs2010-02-13 14:33:20 -0800pArAbIlDsAdded first files to git repositorypArAbIlDs
//
//  A	second_sourceline/src/readme.txt
//  A	source_path/file_to_ignore_in_branch_view.txt
//  A	source_path/mode.txt
//  A	source_path/readme.txt
//  A	sourceline with spaces/readme.txt
//  A	sourceline/alwaysvalid/src/readme.txt
//  A	sourceline/alwaysvalid/src/symlinked_readme.txt

    // Prepare
    BufferedReader reader = null;
    try {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Started parsing Git change log");
      }
      final List result = new LinkedList();
      final SimpleDateFormat svnChangeLogFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(changeLogFile)));

      // Traverse to first change.
      String line = IoUtils.readToNotPast(reader, CHANGE_LIST_DELIMITER, null);
      if (line == null) {
        return result;
      }
      while (line != null) {
        try {
//          if (LOG.isDebugEnabled()) {
//            LOG.debug("line at cycle: " + line);
//          }

          if (line == null) {
            break;
          }

          // Parse change description

          // Read commit hash
          final int hashBegin = line.indexOf(CHANGE_LIST_DELIMITER) + CHANGE_LIST_DELIMITER.length();
          final int hashEnd = line.indexOf(FIELD_SEPARATOR, hashBegin);
          final String hash = line.substring(hashBegin, hashEnd);
//          if (LOG.isDebugEnabled()) LOG.debug("hash: " + hash);

          // Read user name
          final int userEnd = line.indexOf(FIELD_SEPARATOR, hashEnd + FIELD_SEPARATOR_LENGTH);
          final String user = line.substring(hashEnd + FIELD_SEPARATOR_LENGTH, userEnd);
//          if (LOG.isDebugEnabled()) LOG.debug("user: " + user);

          // Read e-mail
          final int emailEnd = line.indexOf(FIELD_SEPARATOR, userEnd + FIELD_SEPARATOR_LENGTH);
          final String email = line.substring(userEnd + FIELD_SEPARATOR_LENGTH, emailEnd);
//          if (LOG.isDebugEnabled()) LOG.debug("email: " + email);

          // Read date
          final int dateEnd = line.indexOf(FIELD_SEPARATOR, emailEnd + FIELD_SEPARATOR_LENGTH);
          final String stringDate = line.substring(emailEnd + FIELD_SEPARATOR_LENGTH, dateEnd);
//          if (LOG.isDebugEnabled()) LOG.debug("stringDate: " + stringDate);

          // Read date
          final int messageEnd = line.indexOf(FIELD_SEPARATOR, dateEnd + FIELD_SEPARATOR_LENGTH);
          final StringBuilder description = new StringBuilder(100).append(line.substring(dateEnd + FIELD_SEPARATOR_LENGTH, messageEnd));
//          if (LOG.isDebugEnabled()) LOG.debug("description: " + description);

          final Date date;
          try {
            date = svnChangeLogFormatter.parse(stringDate);
          } catch (final ParseException e) {
            throw new IOException("Unexpected format of revision date in line: " + line + ", error: " + StringUtils.toString(e));
          }

          final ChangeList changeList = new ChangeList();
          changeList.setDescription(description.toString());
          changeList.setCreatedAt(date);
          changeList.setEmail(email);
          changeList.setNumber(hash);
          changeList.setUser(!StringUtils.isBlank(email) && useUserEmailAsUserName ? email : user);

          // Read changed files
          line = skipEmptyLines(reader);
          while (line != null && !line.startsWith(CHANGE_LIST_DELIMITER)) {
            if (changeList.getOriginalSize() < maxChangeListSize) {
              changeList.getChanges().add(parsePathLine(line));
            } else {
              changeList.setTruncated(true);
            }
            changeList.incrementOriginalSize();
            line = skipEmptyLines(reader);
          }
          result.add(changeList);

          if (result.size() >= maxChangeLists) {
            break;
          }
        } catch (final RuntimeException e) {
          final IOException ioe = new IOException("Unexpected error while processing Git change log, line: " + line);
          ioe.initCause(e);
          throw ioe;
        }
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("Finished parsing Git change log");
      }
      return result;
    } finally {
      IoUtils.closeHard(reader);
    }
  }


  private String skipEmptyLines(final BufferedReader reader) throws IOException {
    String line = reader.readLine();
    while (line != null && line.trim().isEmpty()) {
      line = reader.readLine();
    }
    return line;
  }


  /**
   * Parses SVN's path line and creates Change from it.
   *
   * @param line
   */
  private Change parsePathLine(final String line) {
    final int operationEnd = line.indexOf('\t');
    final String operation = line.substring(0, operationEnd);
    final String path = line.substring(operationEnd + 1);
    final Change change = new Change();
    change.setFilePath(path);
    change.setRevision("");
    if ("A".equals(operation)) {
      change.setChangeType(Change.TYPE_ADDED);
    } else if ("C".equals(operation)) {
      change.setChangeType(Change.TYPE_COPIED);
    } else if ("D".equals(operation)) {
      change.setChangeType(Change.TYPE_DELETED);
    } else if ("M".equals(operation)) {
      change.setChangeType(Change.TYPE_MODIFIED);
    } else if ("R".equals(operation)) {
      change.setChangeType(Change.TYPE_RENAMED);
    } else if ("U".equals(operation)) {
      change.setChangeType(Change.TYPE_UNMERGED);
    } else if ("X".equals(operation)) {
      change.setChangeType(Change.TYPE_UNKNOWN);
    } else if ("B".equals(operation)) {
      change.setChangeType(Change.TYPE_PARING_BROKEN);
    } else {
      change.setChangeType(Change.TYPE_UNKNOWN);
    }

    return change;
  }


  public String toString() {
    return "GitTextChangeLogParser{" +
            "maxChangeLists=" + maxChangeLists +
            ", maxChangeListSize=" + maxChangeListSize +
            '}';
  }
}
