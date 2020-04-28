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
import org.parabuild.ci.object.Change;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Parses StarTeam change log
 */
final class StarTeamChangeLogParser {

  private static final Log log = LogFactory.getLog(StarTeamChangeLogParser.class);

  private static final String HISTORY_FOR = "History for: ";
  private static final String FILE_DELIM = "=============================================================================";
  private static final String REVISION_DELIM = "----------------------------";
  private static final String DESCRIPTION = "Description: ";
  private static final String FOLDER = "Folder: ";
  private static final String DATE = "Date: ";
  private static final String REVISION = "Revision: ";

  // lengths
  private static final int HISTORY_FOR_LENGTH = HISTORY_FOR.length();
  private static final int DESCRIPTION_LENGTH = DESCRIPTION.length();
  private static final int DATE_LENGTH = DATE.length();
  private static final int REVISION_LENGTH = REVISION.length();

  private final TimeWindowChangeListAccumulator accumulator;
  private final StarTeamDateFormat dateFormat;
  private final int workingDirLength;
  private final Date startingFrom;
  private final String workingDirLowerCase;


  public StarTeamChangeLogParser(final Locale locale, final String workingDir, final int maxChangeLists, final Date startingFrom, final int maxChangeListSize) {
    this.accumulator = new TimeWindowChangeListAccumulator(60000L, maxChangeLists, maxChangeListSize);
    this.dateFormat = new StarTeamDateFormat(locale);
    final String validatedWorkingDir = ArgumentValidator.validateArgumentNotBlank(workingDir, "working directory");
    this.workingDirLength = validatedWorkingDir.length();
    this.workingDirLowerCase = workingDir.toLowerCase();
    this.startingFrom = startingFrom != null ? (Date)startingFrom.clone() : null;
  }


  /**
   * Parces SVN change log. Parser expects that changes are
   * passed in revers order - latest come first.
   *
   * @param file to parse
   *
   * @return List of ChaneList elements, maybe empty.
   */
  public List parseChangeLog(final File file) throws IOException, ParseException {
    InputStream is = null;
    try {
      is = new FileInputStream(file);
      return parseChangeLog(is);
    } finally {
      IoUtils.closeHard(is);
    }
  }


  /**
   * Parces StarTeam change log. Parser expects that changes are
   * passed in revers order - latest come first.
   *
   * @param input InputStream to get log data from.
   *
   * @return List of change lists.
   */
  public List parseChangeLog(final InputStream input) throws IOException, ParseException {
    /* Typical entry
------------------------ log start ------------------------------------------------
Folder: test_project  (working dir: D:\projectory\test_starteam)
History for: test_cvs_change_log_with_outside_branch.txt
Description: Initial add
Locked by:
Status: Current
----------------------------
Revision: 2 View: test_project Branch Revision: 1.1
Author: Test User Date: 3/6/06 1:06:59 AM PST
Added empty lines to add a revision

----------------------------
Revision: 1 View: test_project Branch Revision: 1.0
Author: Test User Date: 3/6/06 12:46:21 AM PST
=============================================================================

Folder: second_sourceline  (working dir: D:\projectory\test_starteam\second_sourceline)
Folder: src  (working dir: D:\projectory\test_starteam\second_sourceline\src)
History for: readme.txt
Description: Added file in second source line
Locked by:
Status: Current
----------------------------
Revision: 2 View: test_project Branch Revision: 1.1
Author: Test User Date: 3/6/06 1:06:59 AM PST
Added empty lines to add a revision

----------------------------
Revision: 1 View: test_project Branch Revision: 1.0
Author: Test User Date: 3/6/06 12:51:24 AM PST
=============================================================================

Folder: sourceline  (working dir: D:\projectory\test_starteam\sourceline)
Folder: alwaysvalid  (working dir: D:\projectory\test_starteam\sourceline\alwaysvalid)
Folder: src  (working dir: D:\projectory\test_starteam\sourceline\alwaysvalid\src)
History for: readme.txt
Description: Added file in always valid sourceline
Locked by:
Status: Current
----------------------------
Revision: 2 View: test_project Branch Revision: 1.1
Author: Test User Date: 3/6/06 1:06:59 AM PST
Added empty lines to add a revision

----------------------------
Revision: 1 View: test_project Branch Revision: 1.0
Author: Test User Date: 3/6/06 12:51:02 AM PST
=============================================================================

-------------------------- log end ----------------------------------------------
  */
    if (log.isDebugEnabled()) {
      log.debug("started parsing StartTeam change log");
    }
    final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    String line = "";
    while (line != null) {
      line = IoUtils.readToNotPast(reader, FOLDER, null);
      //if (log.isDebugEnabled()) log.debug("line: " + line);

      // find file path
      String lastFolderLine = line;
      //if (log.isDebugEnabled()) log.debug("line before entering file path: " + line);
      while (line != null && line.startsWith(FOLDER)) {
        line = IoUtils.readUntil(reader, FOLDER, HISTORY_FOR);
        //if (log.isDebugEnabled()) log.debug("line in file path: " + line);
        if (line != null && line.startsWith(FOLDER)) {
          lastFolderLine = line;
        }
      }
      if (line == null || !line.startsWith(HISTORY_FOR)) return accumulator.getChangeLists();
      //if (log.isDebugEnabled()) log.debug("lastFolderLine: " + lastFolderLine);
      //if (log.isDebugEnabled()) log.debug("line: " + line);
      final String nameOnly = line.substring(HISTORY_FOR_LENGTH);
      final int workingDirStart = lastFolderLine.toLowerCase().indexOf(workingDirLowerCase);
      final int beginPathIndex = workingDirStart + workingDirLength - 1;
      final int endPathIndex = lastFolderLine.length() - 1;
      final String pathOnly = beginPathIndex > endPathIndex ? "" : lastFolderLine.substring(beginPathIndex, endPathIndex); // -1 to cut off bracket
      final String fileName = (StringUtils.isBlank(pathOnly) ? "" : pathOnly.substring(1) + '\\') + nameOnly;
      //if (log.isDebugEnabled()) log.debug("fileName: " + fileName);

      // collect description for first revision
      final StringBuffer firstRevisionDescription = new StringBuffer(100);
      line = IoUtils.readUntil(reader, DESCRIPTION, REVISION_DELIM);
      while (line != null
        && !line.equals(REVISION_DELIM)
        && !line.startsWith("Locked by:")
        && !line.startsWith("Status: ")
        && !line.equals(FILE_DELIM)
        ) {
        firstRevisionDescription.append(line.startsWith(DESCRIPTION) ? line.substring(DESCRIPTION_LENGTH) : line);
        line = reader.readLine();
      }
      if (line == null) return accumulator.getChangeLists();
      //if (log.isDebugEnabled()) {
      //  log.debug("firstRevisionDescription.toString(): " + firstRevisionDescription.toString());
      //}

      // process revisions
      if (!line.startsWith(REVISION_DELIM)) line = IoUtils.readToNotPast(reader, REVISION_DELIM, null);
      while (line != null && !line.equals(FILE_DELIM)) {

        // process revision
        line = reader.readLine(); // skip REVISION_DELIM
        //if (log.isDebugEnabled()) log.debug("line in revisions: " + line);
        final String stringRevision = line.substring(REVISION_LENGTH, line.indexOf(' ', REVISION_LENGTH));

        // process author and date
        line = reader.readLine();
        final int dateIndex = line.indexOf(DATE);
        final String stringDate = line.substring(dateIndex + DATE_LENGTH);
        final Date date = dateFormat.parseOutput(stringDate);

        // check if date is equal or higher than designated
        if (startingFrom == null || date.compareTo(startingFrom) >= 0) {
          final String author = line.substring("Author: ".length(), dateIndex);
          // process description if any
          final StringBuffer description = new StringBuffer(100);
          line = reader.readLine();
          while (line != null && !line.equals(REVISION_DELIM) && !line.equals(FILE_DELIM)) {
            description.append(line);
            line = reader.readLine();
          }
          final StringBuffer sbMessage = "1".equals(stringRevision) ? firstRevisionDescription : description;
          final Change change = new Change(fileName, stringRevision, "1".equals(stringRevision) ? Change.TYPE_ADDED : Change.TYPE_CHECKIN);
          //if (log.isDebugEnabled()) log.debug("================================");
          //if (log.isDebugEnabled()) log.debug("date: " + date);
          //if (log.isDebugEnabled()) log.debug("sbMessage: " + sbMessage);
          //if (log.isDebugEnabled()) log.debug("author: " + author);
          //if (log.isDebugEnabled()) log.debug("change: " + change);
          //if (log.isDebugEnabled()) log.debug("================================");
          // REVIEWME: simeshev@parabuilci.org -> what about branch?
          final String branch = "";
          accumulator.add(date, sbMessage, author, branch, change);
        } else {
          // doesn't make sense to process the rest of revisions
          // for this file, read to the nex file
          line = IoUtils.readToNotPast(reader, FILE_DELIM, null);
        }
      }
    }
    return accumulator.getChangeLists();
  }
}
