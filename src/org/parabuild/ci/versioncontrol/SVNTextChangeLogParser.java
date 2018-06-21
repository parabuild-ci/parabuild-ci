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
package org.parabuild.ci.versioncontrol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SimpleChange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Parses subversion change log
 */
final class SVNTextChangeLogParser extends SVNChangeLogParser {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SVNTextChangeLogParser.class); // NOPMD

  /**
   * Change lists are separated by this line. First change list
   * start with this line.
   */
  private static final String CHANGE_LIST_DELIMITER = "------------------------------------------------------------------------";

//  /**
//   * Changed path list starts with this string.
//   */
//  private static final String CHANGED_PATHS = "Changed paths:";

  /**
   * Every changed path has this prefix.
   */
  private static final String CHANGE_LIST_PATH_PREFIX = "   ";


  SVNTextChangeLogParser(final int maxChangeListSize) {
    super(maxChangeListSize);
  }


  /**
   * Parces SVN change log. Parser expects that changes are
   * passed in revers order - latest come first.
   *
   * @param input InputStream to get log data from.
   * @return List of change lists.
   */
  public List parseChangeLog(final InputStream input) throws IOException {
    /* Typical entry
------------------------------------------------------------------------
r13248 | sussman | 2005-03-03 13:31:09 -0800 (Thu, 03 Mar 2005) | 6 lines
Changed paths:
   M /trunk/subversion/libsvn_client/info.c

Make svn_client_info() use new svn_ra_stat() API for efficiency.

* subversion/libsvn_client/info.c
  (svn_client_info):  simplify by using svn_ra_stat().  if an older
     svnserve throws an error, then fall back to the inefficient technique.

------------------------------------------------------------------------
  */
    if (LOG.isDebugEnabled()) {
      LOG.debug("started parsing SVN change log");
    }
    final List result = new LinkedList();

    final SimpleDateFormat svnChangeLogFormatter = getSvnChangeLogDateFormatter();
    final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    // traverse to first change.
    String line = IoUtils.readToNotPast(reader, CHANGE_LIST_DELIMITER, null);
    if (line == null) {
      throw new ChangeLogParserException("SVN", "Change log does not contain any entries. One of the sources of this condition is Parabuild running out of space on the build manager or on an agent.");
    }
    int changeListCounter = 0;
    while (line != null) {
      line = reader.readLine();
      //if (log.isDebugEnabled()) log.debug("line at cycle: " + line);
      if (line == null) {
        break;
      }

      // parse change description
      final StringTokenizer st = new StringTokenizer(line, "|", false);

      // parse revision
      if (!st.hasMoreTokens()) {
        // NOTE: simeshev@parabuilci.org - 2009-09-15 - This is to cever
        // the case when someone cut'n'pasted a description that look
        // exactly like log output. See PARABUILD-1372 for details.
        line = IoUtils.readToNotPast(reader, CHANGE_LIST_DELIMITER, null);
        continue;
      }
      final String revisionToken = st.nextToken().trim();
      if (!(revisionToken.charAt(0) == 'r')) {
        throw new ChangeLogParserException("SVN", "Unexpected format of revision number in line: " + line);
      }
      final String revisionNumberToken = revisionToken.substring(1, revisionToken.length());
      //if (log.isDebugEnabled()) log.debug("revisionNumberToken: " + revisionNumberToken);
      if (!StringUtils.isValidInteger(revisionNumberToken)) {
        throw new ChangeLogParserException("SVN", "Unexpected format of revision number in line: " + line);
      }

      // parse user name
      final String stringUserName = st.nextToken().trim();
      //if (log.isDebugEnabled()) log.debug("stringUserName: " + stringUserName);

      // get date
      final String stringDateToken = st.nextToken().trim();

      // NOTE: simeshev@parabuilci.org - See #1154 "Add
      // ignoring unparseable subversion date"
      if ("<no date>".equals(stringDateToken) || "(no date)".equals(stringDateToken)) {
        // skip everything until the next change list, if any
        line = IoUtils.readToNotPast(reader, CHANGE_LIST_DELIMITER, null);
        continue;
      }

      // parse date
      final int firstVerbalDateBracket = stringDateToken.indexOf('(');
      if (firstVerbalDateBracket <= 1) {
        throw new ChangeLogParserException("SVN", "Unexpected format of revision date in line: " + line);
      }


      final String stringDate = stringDateToken.substring(0, firstVerbalDateBracket - 1);
      //if (log.isDebugEnabled()) log.debug("stringDate: " + stringDate);
      final Date date;
      try {
        date = svnChangeLogFormatter.parse(stringDate);
      } catch (final ParseException e) {
        throw new ChangeLogParserException("SVN", "Unexpected format of revision date in line: " + line + ", error: " + StringUtils.toString(e));
      }

      // NOTE: simeshev@parabuilci.org - we don't use the counter
      // provided by SVN - left it here for clarity.
      // String stringCommentLineCount = st.nextToken();

      final ChangeList changeList = new ChangeList();
      changeList.setUser(stringUserName);
      changeList.setNumber(revisionNumberToken);
      changeList.setCreatedAt(date);

      // skip to Changed paths:
      final String changedPathsMarker = reader.readLine();
      //if (log.isDebugEnabled()) log.debug("changedPathsMarker: " + changedPathsMarker);
      if (changedPathsMarker == null) {
        break;
      }

      // process paths
      String pathLine = reader.readLine();
      while (pathLine != null && pathLine.startsWith(CHANGE_LIST_PATH_PREFIX)) {
        if (changeList.getOriginalSize() < maxChangeListSize) {
          //if (log.isDebugEnabled()) log.debug("pathLine: " + pathLine);
          changeList.getChanges().add(parsePathLine(pathLine));
        } else {
          changeList.setTruncated(true);
        }
        changeList.incrementOriginalSize();
        // next line
        pathLine = reader.readLine();
      }

      //if (log.isDebugEnabled()) log.debug("changeList: " + changeList);

      // process description
      final StringBuffer descr = new StringBuffer(100);
      String descrLine = reader.readLine();
      while (descrLine != null && !descrLine.startsWith(CHANGE_LIST_DELIMITER)) {
        descr.append(descrLine).append('\n');
        descrLine = reader.readLine();
      }
      if (descr.length() < 1024) {
        changeList.setDescription(descr.toString());
      } else {
        changeList.setDescription(descr.substring(0, 1023));
      }
      //if (log.isDebugEnabled()) log.debug("descrLine: " + descrLine.toString());
      //if (log.isDebugEnabled()) log.debug("descr: " + descr);

      // NOTE: at this point reader is positioned to the
      // line denoting next change list.

      // handle ignorable
      if (StringUtils.isBlank(ignoreChangeListNumber) || !changeList.getNumber().equals(ignoreChangeListNumber)) {
        if (StringUtils.isBlank(subSubdirectory)) {
          result.add(changeList);
          changeListCounter++;
        } else {
          // Delete any directory changes that have a given directory as a parent
          final Set set = new HashSet(changeList.getChanges());
          for (final Iterator iter = set.iterator(); iter.hasNext();) {
            final String filePath = ((SimpleChange) iter.next()).getFilePath();
            if (filePath.startsWith(subSubdirectory) && filePath.indexOf('/', subSubdirectory.length() + 1) >= 0) {
              iter.remove();
            }
          }
          if (!set.isEmpty()) {
            result.add(changeList);
            changeListCounter++;
          }
        }
      }
      if (changeListCounter >= maxChangeLists) {
        break;
      }
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("finished parsing SVN change log");
    }
    return result;
  }


  /**
   * Parses SVN's path line and creates Change from it.
   *
   * @param pathLine
   */
  private Change parsePathLine(final String pathLine) {
    final String operation = pathLine.substring(CHANGE_LIST_PATH_PREFIX.length(), CHANGE_LIST_PATH_PREFIX.length() + 1);
    final String path = pathLine.substring(CHANGE_LIST_PATH_PREFIX.length() + 2);
    final Change change = new Change();
    change.setFilePath(path);
    change.setRevision("");
    setChangeType(change, operation);
    return change;
  }


  public static SimpleDateFormat getSvnChangeLogDateFormatter() {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
  }
}
