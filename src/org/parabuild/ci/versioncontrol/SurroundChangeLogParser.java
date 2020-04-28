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
import org.parabuild.ci.util.IoUtils;

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
import java.util.StringTokenizer;

/**
 * Parses surround change log
 */
final class SurroundChangeLogParser {

  private static final Log log = LogFactory.getLog(SurroundChangeLogParser.class);


  /**
   * Maxumim number of change lists.
   */
  private final int maxChangeLists;

  private String branch;

  private static final String FILE_HEADER = "File:";
  private static final int FILE_HEADER_LENGTH = FILE_HEADER.length();
  private static final String STR_ADD = "Add";
  private static final String STR_REMOVE = "Remove";
//  private static final String TYPE_CHECKIN = "Check in";
  private static final String STR_CHECK = "Check";
  private static final String STR_LABEL = "Label";
  private final SurroundDateFormat format;
  private final int maxChangeListSize;


  public SurroundChangeLogParser(final Locale locale, final int maxChangeLists, final int maxChangeListSize) {
    this.maxChangeLists = maxChangeLists;
    this.maxChangeListSize = maxChangeListSize;
    this.format = new SurroundDateFormat(locale);
  }


  /**
   * Parces Surround change log. Parser expects that changes are
   * passed in revers order - latest come first.
   *
   * @param file to parse
   *
   * @return List of ChaneList elements, maybe empty.
   */
  public List parseChangeLog(final File file) throws IOException {
    InputStream is = null;
    try {
      is = new FileInputStream(file);
      return parseChangeLog(is);
    } finally {
      IoUtils.closeHard(is);
    }
  }


  /**
   * Parces Surround change log. Parser expects that changes are
   * passed in revers order - latest come first.
   *
   * @param input InputStream to get log data from.
   *
   * @return List of change lists.
   */
  public List parseChangeLog(final InputStream input) throws IOException {
    /*

    Typical output of the histoty report command:
    sscm rh test_mainline_branch -fSpaces -qSummary -btest_mainline_branch -yadministrator -zlocalhost:4900 -x0



    Surround SCM History Report

Date: Monday, August 15, 2005 1:16 PM
Actions: All change actions
Users: All Users

Branch: test_mainline_branch

Repository: test_mainline_branch
Administrator 7/26/2005 1:52 PM Add test_mainline_branch

File: test_mainline_branch/readme.txt
1 Administrator 7/26/2005 1:59 PM Add
2 Administrator 7/26/2005 2:15 PM Check in  Added blank line

Repository: test_mainline_branch/test
Administrator 7/26/2005 2:46 PM Add
Administrator 7/26/2005 2:53 PM Remove

Repository: test_mainline_branch/test/sourceline
Administrator 7/26/2005 2:46 PM Add
Administrator 7/26/2005 2:53 PM Remove

File: test_mainline_branch/test/sourceline/gsbase-2.0.jar
1 Administrator 8/12/2005 4:40 PM Add administrator - 8/12/2005 4:34:11 PM - jvmstat  added other jar

Repository: test_mainline_branch/test/sourceline/alwaysvalid
Administrator 7/26/2005 2:46 PM Add
Administrator 7/26/2005 2:53 PM Remove

Repository: test_mainline_branch/test/sourceline/jvmstat30
Administrator 8/12/2005 4:35 PM Add administrator - 8/12/2005 4:34:11 PM - jvmstat Comment for adding JVM stat

File: test_mainline_branch/test/sourceline/jvmstat30/LICENSE
1 Administrator 8/12/2005 4:35 PM Add administrator - 8/12/2005 4:34:11 PM - jvmstat  Comment for adding JVM stat

File: test_mainline_branch/test/sourceline/jvmstat30/README
1 Administrator 8/12/2005 4:35 PM Add administrator - 8/12/2005 4:34:11 PM - jvmstat  Comment for adding JVM stat

Repository: test_mainline_branch/test/sourceline/alwaysvalid/src
Administrator 7/26/2005 2:46 PM Add
Administrator 7/26/2005 2:52 PM Remove

File: test_mainline_branch/test/sourceline/alwaysvalid/src/readme.txt
1 Administrator 7/26/2005 3:10 PM Add
2 Administrator 7/26/2005 4:09 PM Check in Added more lines to two files.
3 Administrator 7/26/2005 4:11 PM Check in Removed prev added line
4 test_user     7/26/2005 5:12 PM Check in Removed prev added line with other discription
  */

    if (log.isDebugEnabled()) log.debug("started parsing Surround change log");
    final TimeWindowChangeListAccumulator changeListAccumulator = new TimeWindowChangeListAccumulator(60000L, maxChangeLists, maxChangeListSize);

    final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    // traverse to first change.

    String line = reader.readLine();
    if (line == null) throw new ChangeLogParserException("Surround", "Change log does not contain any entries");
    line = IoUtils.readToNotPast(reader, FILE_HEADER, null);
    FlatChange changeToSave = null;
    while (line != null) {
      // get file path
      final String filePath = line.substring(FILE_HEADER_LENGTH + 1);
      //line = IoUtils.skipEmptyLines(reader);
      while ((line = IoUtils.skipEmptyLines(reader)) != null && !line.startsWith(FILE_HEADER)) {
        // next line - one or more file versions/changes or coninuing comment from prev
        final StringTokenizer st = new StringTokenizer(line, " \t", false);
        if (st.countTokens() < 6) { // potentially parse-able?
          if (line.startsWith("Number of Repositories: ")
            || line.startsWith("Number of Files: ")
            || line.startsWith("Repository: ")) {
//            if (log.isDebugEnabled()) log.debug("skipping: " + line);
            break; // doesn't make sense to keep rolling the cycle
          }
          // process comment leftovers
          if (changeToSave != null) {
            if (log.isDebugEnabled()) log.debug("adding to comment: " + line);
            changeToSave.getDescr().append(line);
          } else {
            log.warn("Unexpected Surround line position, line: " + line);
          }
        } else {
          // the fact we are here doesn't mean we have a correct change line, this can be
          final String revision = st.nextToken();
          final String userName = st.nextToken();
          final String stringDate = st.nextToken();
          final String stringTime = st.nextToken() + ' ' + st.nextToken();
          Date date = null;
          try {
            date = format.parse(stringDate, stringTime);
//            if (log.isDebugEnabled()) log.debug("dateToParse: " + stringDate + " " + stringTime + ", parsed: " + date);
          } catch (final ParseException e) {
            // process comment leftovers
            if (changeToSave != null) {
              changeToSave.getDescr().append(line);
//              if (log.isDebugEnabled()) log.debug("adding to comment: " + line);
            } else {
              log.warn("Unexpected Surround date format, line: " + line + ", " + format.toString(), e);
            }
            continue; // to next line
          }

          //
          // process new valid change line
          //

          // parse operation
          byte changeType = Change.TYPE_UNKNOWN;
          final String oper = st.nextToken();
          if (oper.startsWith(STR_ADD)) {
            changeType = Change.TYPE_ADDED;
          } else if (oper.startsWith(STR_REMOVE)) {
            changeType = Change.TYPE_DELETED;
          } else if (oper.startsWith(STR_CHECK)) {
            changeType = Change.TYPE_CHECKIN;
            st.nextToken(); // skip "in" part of "Checkin"
          } else if (oper.startsWith(STR_LABEL)) {
            changeType = Change.TYPE_LABEL;
          }

          if (changeType != Change.TYPE_LABEL) {

            // add to list

            // compose descr
            final StringBuffer descr = new StringBuffer(100);
            while (st.hasMoreTokens()) {
              descr.append(st.nextToken());
              if (st.hasMoreTokens()) descr.append(' ');
            }

            // store previous change if any
            if (changeToSave != null) {
              addToAccumulator(changeToSave, changeListAccumulator);
            }

            // make new
            changeToSave = new FlatChange();
            changeToSave.setRevision(revision);
            changeToSave.setUserName(userName);
            changeToSave.setDate(date);
            changeToSave.setDescr(descr);
            changeToSave.setChangeType(changeType);
            changeToSave.setFilePath(filePath);
          }
        }
      }

      if (line != null && !line.startsWith(FILE_HEADER)) {
        line = IoUtils.readToNotPast(reader, FILE_HEADER, null);
      }
    }

    if (changeToSave != null) {
      addToAccumulator(changeToSave, changeListAccumulator);
      changeToSave = null;
    }

    if (log.isDebugEnabled()) log.debug("finished parsing Surround change log");
    return changeListAccumulator.getChangeLists();
  }


  private void addToAccumulator(final FlatChange changeToSave, final TimeWindowChangeListAccumulator changeListAccumulator) {
    final Change change = new Change();
    change.setFilePath(changeToSave.getFilePath());
    change.setRevision(changeToSave.getRevision());
    change.setChangeType(changeToSave.getChangeType());
    changeListAccumulator.add(changeToSave.getDate(), changeToSave.getDescr(), changeToSave.getUserName(), branch, change);
  }


  public void setBranch(final String branch) {
    this.branch = branch;
  }


  private static final class FlatChange {

    private byte changeType = Change.TYPE_UNKNOWN;
    private Date date;
    private String filePath;
    private String revision;
    private String userName;
    private StringBuffer descr;


    public byte getChangeType() {
      return changeType;
    }


    public void setChangeType(final byte changeType) {
      this.changeType = changeType;
    }


    public Date getDate() {
      return date;
    }


    public void setDate(final Date date) {
      this.date = date;
    }


    public String getFilePath() {
      return filePath;
    }


    public void setFilePath(final String filePath) {
      this.filePath = filePath;
    }


    public String getRevision() {
      return revision;
    }


    public void setRevision(final String revision) {
      this.revision = revision;
    }


    public String getUserName() {
      return userName;
    }


    public void setUserName(final String userName) {
      this.userName = userName;
    }


    public StringBuffer getDescr() {
      return descr;
    }


    public void setDescr(final StringBuffer descr) {
      this.descr = descr;
    }
  }
}
