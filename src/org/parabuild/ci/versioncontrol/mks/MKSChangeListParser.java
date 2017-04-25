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
package org.parabuild.ci.versioncontrol.mks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.versioncontrol.TimeWindowChangeListAccumulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Parses MKS change list log
 */
final class MKSChangeListParser {

  private static final Log log = LogFactory.getLog(MKSChangeListParser.class);

  private static final long CHANGE_LIST_WINDOW_MS = 60L * 1000L;

  private static final String CHANGE_PACKAGE = "change package:";
  private static final String DATE = "date: ";
  private static final String DESCRIPTION = "description:";
  private static final String FILE_DELIM = "===============================================================================";
  private static final String MEMBER_NAME_LINE = "member name: ";
  private static final String REVISION = "revision ";
  private static final String REVISION_DELIM = "-----------------------";
//  private static final String TOTAL_REVISIONS = "total revisions: ";

  private static final int AUTHOR_LENGTH = " author: ".length();
  private static final int DATE_LENGTH = DATE.length();
  private static final int MEMBER_NAME_LINE_ENGTH = MEMBER_NAME_LINE.length();
  private static final int REVISION_LENGTH = REVISION.length();
//  private static final int TOTAL_REVISIONS_LENGTH = TOTAL_REVISIONS.length();

  private final String project; // NOPMD
  private final String path; // NOPMD
  private final int maxChangeLists;
  private final String developmentPath;
  private final Date startingFrom;
  private final DateFormat dateFormat;
  private final int maxChangeListSize;


  /**
   * Creates MKS change list paser.
   *
   * @param project
   * @param path
   * @param maxChangeLists
   * @param developmentPath
   * @param dateFormat
   */
  public MKSChangeListParser(
          final String project,
          final String path,
          final int maxChangeLists,
          final String developmentPath,
          final Date startingFrom,
          final int maxChangeListSize,
          final String dateFormat) {

    this.project = project;
    this.path = path;
    this.maxChangeLists = maxChangeLists;
    this.developmentPath = developmentPath;
    this.startingFrom = startingFrom != null ? (Date) startingFrom.clone() : (Date) null;
    this.dateFormat = new SimpleDateFormat(dateFormat);
    this.maxChangeListSize = maxChangeListSize;
  }


  /**
   * Parces MKS change log
   *
   * @param stdoutFile to parse
   * @return List of ChaneList elements, maybe empty.
   */
  public List parseChangeLog(final File stdoutFile) throws IOException {
    InputStream bis = null;
    try {
      bis = new FileInputStream(stdoutFile);
      return parseChangeLog(bis);
    } catch (ParseException e) {
      throw IoUtils.createIOException(e);
    } finally {
      IoUtils.closeHard(bis);
    }
  }


  /**
   * Parces MKS change log
   *
   * @param stdoutInput InputStream to get log data from.
   * @return List of change lists.
   */
  public List parseChangeLog(final InputStream stdoutInput) throws IOException, ParseException {
    /* Typical entry set:
member name: second_sourceline\src\readme.txt;	working file:
head:	1.1
member:	1.1
branch:
locks:	; strict
attributes:
file format: text
revision storage: reverse deltas
total revisions: 1; branches: 0; branch revisions: 0
description:
Initial add to the test ptoject
-----------------------
revision 1.1
date: Apr 11, 2006 - 1:32 AM; author: test_user; state: Exp; lines: +0 -0
change package: 1:1 Pupulated test project
Initial revision
Member added to project /opt/mks/integrity_server2005/mksis/projects/project.pj
===============================================================================
member name: sourceline\alwaysvalid\src\readme.txt;	working file:
head:	1.2
member:	1.2
branch:
locks:	; strict
attributes:
file format: text
revision storage: reverse deltas
total revisions: 2; branches: 0; branch revisions: 0
description:
Initial add to the test ptoject
-----------------------
revision 1.2
date: Apr 11, 2006 - 1:40 AM; author: test_user; state: Exp; lines: +2 -0
change package: 2:1 Change package to add a line
Added a line to files in the same subdirectory
-----------------------
revision 1.1
date: Apr 11, 2006 - 1:32 AM; author: test_user; state: Exp; lines: +0 -0
change package: 1:1 Pupulated test project
Initial revision
Member added to project /opt/mks/integrity_server2005/mksis/projects/project.pj
===============================================================================
member name: test_cvs_change_log_with_outside_branch.txt;	working file:
head:	1.1
member:	1.1
branch:
locks:	; strict
attributes:
file format: text
revision storage: reverse deltas
total revisions: 1; branches: 0; branch revisions: 0
description:
Initial add to the test ptoject
-----------------------
revision 1.1
date: Apr 11, 2006 - 1:32 AM; author: test_user; state: Exp; lines: +0 -0
change package: 1:1 Pupulated test project
Initial revision
Member added to project /opt/mks/integrity_server2005/mksis/projects/project.pj
===============================================================================
    */
    if (log.isDebugEnabled()) log.debug("parseChangeLog - start");

    // result (change list accumulator)
    final TimeWindowChangeListAccumulator accumulator = new TimeWindowChangeListAccumulator(CHANGE_LIST_WINDOW_MS, maxChangeLists, maxChangeListSize);

    // read to the first RCS file name. The first entry in the log
    // information will begin with this line. A MKS_FILE_DELIMITER is NOT
    // present. If no RCS file lines are found then there is nothing to do.
    final BufferedReader reader = new BufferedReader(new InputStreamReader(stdoutInput));
    String line = IoUtils.readToNotPast(reader, MEMBER_NAME_LINE, null);
    while (line != null) {
      //
      // process member name
      //
      // we are at the line that starts with "member name:".
      // such line typiclally looks like
      //
      // member name: second_sourceline\src\readme.txt;	working file:
      //
      int semiColonIndex = line.indexOf(';');
      if (semiColonIndex < 0) semiColonIndex = line.length();
      final String memberName = line.substring(MEMBER_NAME_LINE_ENGTH, semiColonIndex).replace('\\', '/');

      //
      // read description
      //
      final StringBuffer firstRevisionDescription = new StringBuffer(100);
      line = IoUtils.readToNotPast(reader, DESCRIPTION, null);
      if (line != null) {
        line = reader.readLine(); // skip "description:"
      }
      while (line != null && !line.equals(REVISION_DELIM)) {
        firstRevisionDescription.append(line);
        line = IoUtils.readAndTrim(reader, false);
        if (line != null && !line.equals(REVISION_DELIM)) {
          firstRevisionDescription.append('\n');
        }
      }
      if (line == null) return accumulator.getChangeLists();

      //
      // process revisions
      //
      if (!line.startsWith(REVISION_DELIM)) {
        line = IoUtils.readToNotPast(reader, REVISION_DELIM, null);
      }
      while (line != null && !line.equals(FILE_DELIM)) {

        // process revision
        line = IoUtils.readToNotPast(reader, REVISION, null);
//        if (log.isDebugEnabled()) log.debug("line in revisions: " + line);
        final String stringRevision = line.substring(REVISION_LENGTH);

        // process date and author
        line = IoUtils.readToNotPast(reader, DATE, null);
        final int dateEndIndex = line.indexOf(';');
        final String stringDate = line.substring(DATE_LENGTH, dateEndIndex);
        final Date date = dateFormat.parse(stringDate);
//        if (log.isDebugEnabled()) log.debug("date: " + date);
        if (startingFrom == null || date.compareTo(startingFrom) >= 0) {

          final int authorBeginIndex = dateEndIndex + AUTHOR_LENGTH;
          final String author = line.substring(authorBeginIndex, line.indexOf(';', authorBeginIndex));

          //
          // process description
          //
          final StringBuffer description = new StringBuffer(100);
          line = reader.readLine();
//        if (log.isDebugEnabled()) log.debug("line 1: " + line);
          if (line.startsWith(CHANGE_PACKAGE)) {
            line = reader.readLine(); // skip optional "change package:"
//          if (log.isDebugEnabled()) log.debug("line 2: " + line);
          }
          while (line != null && !line.equals(REVISION_DELIM) && !line.equals(FILE_DELIM)) {
            description.append(line);
            line = reader.readLine();
          }
          final boolean firstRevision = stringRevision.equals("1.1");
          final StringBuffer message = firstRevision ? firstRevisionDescription : description;
          final Change change = new Change(memberName, stringRevision, firstRevision ? Change.TYPE_ADDED : Change.TYPE_CHECKIN);
          accumulator.add(date, message, author, developmentPath, change);
//        if (log.isDebugEnabled()) log.debug("================================");
//        if (log.isDebugEnabled()) log.debug("date: " + date);
//        if (log.isDebugEnabled()) log.debug("message: " + message);
//        if (log.isDebugEnabled()) log.debug("author: " + author);
//        if (log.isDebugEnabled()) log.debug("change: " + change);
//        if (log.isDebugEnabled()) log.debug("================================");
        } else {
          // doesn't make sense to process the rest of revisions
          // for this file, read to the nex file
          line = IoUtils.readToNotPast(reader, FILE_DELIM, null);
        }
      }
      line = IoUtils.readToNotPast(reader, MEMBER_NAME_LINE, null);
    }

    return accumulator.getChangeLists();
  }
}
