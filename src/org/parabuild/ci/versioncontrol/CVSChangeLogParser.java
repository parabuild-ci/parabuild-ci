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

import java.io.*;
import java.text.*;
import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.object.*;

/**
 * This class is responsible for parsing CVS change log for
 * changes.
 */
final class CVSChangeLogParser {

  private static final Log log = LogFactory.getLog(CVSChangeLogParser.class);
  private static final String CVS_DESCRIPTION = "description:";
  private static final String CVS_FILE_DELIM = "=============================================================================";
  private static final String CVS_HEAD_TAG = "HEAD";
  private static final String CVS_NEW_LINE = System.getProperty("line.separator");
  private static final String CVS_RCSFILE_LINE = "RCS file: ";
  private static final String CVS_REVISION_DATE = "date:";
  private static final String CVS_REVISION_DEAD = "dead";
  private static final String CVS_REVISION_DELIM = "----------------------------";
  private static final String CVS_WORKING_FILE_LINE = "Working file: ";
  private static final int CVS_WORKING_FILE_LINE_LENGTH = CVS_WORKING_FILE_LINE.length();
  private static final int CVS_RCSFILE_LINE_LENGTH = CVS_RCSFILE_LINE.length();
  private static final long CVS_CHANGE_LIST_WINDOW_MS = 60L * 1000L;

  private final SimpleDateFormat CVS_LOG_DATE_FORMATTER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z", Locale.US); // NOPMD

  private final String tag = null; // NOPMD
  private String repositoryPath = null;
  private String branch = null;
  private int repositoryPathLenth = 0;
  private Map missingRevisionsHashes = null;
  private final int maxChangeLogs;
  private final int maxChangeListSize;


  /**
   * Creates CVS change log parser.
   *
   * @param maxChangeLogs
   * @param maxChangeListSize
   */
  public CVSChangeLogParser(final int maxChangeLogs, final int maxChangeListSize) {
    this.maxChangeLogs = maxChangeLogs;
    this.maxChangeListSize = maxChangeListSize;
  }


  /**
   * Parces CVS change log
   *
   * @param stdoutFile to parse
   *
   * @return List of ChaneList elements, maybe empty.
   */
  public List parseChangeLog(final File stdoutFile) throws IOException {
    InputStream bis = null;
    try {
      bis = new FileInputStream(stdoutFile);
      return parseChangeLog(bis);
    } finally {
      IoUtils.closeHard(bis);
    }
  }


  /**
   * Parces CVS change log
   *
   * @param stdoutInput InputStream to get log data from.
   *
   * @return List of change lists.
   */
  public List parseChangeLog(final InputStream stdoutInput) throws IOException {
    if (log.isDebugEnabled()) log.debug("parseChangeLog - start");

    // set to true if we have to do check for excluded files to work around
    // CVS bug where it lists items in a branch change log if they were deleted
    // in HEAD.
    final boolean processExcludes = missingRevisionsHashes != null && !missingRevisionsHashes.isEmpty();

    // result (change list accumulator)
    final TimeWindowChangeListAccumulator changeListAccumulator = new TimeWindowChangeListAccumulator(CVS_CHANGE_LIST_WINDOW_MS, maxChangeLogs, maxChangeListSize);

    // read to the first RCS file name. The first entry in the log
    // information will begin with this line. A CVS_FILE_DELIMITER is NOT
    // present. If no RCS file lines are found then there is nothing to do.
    final BufferedReader reader = new BufferedReader(new InputStreamReader(stdoutInput));
    String line = IoUtils.readToNotPast(reader, CVS_RCSFILE_LINE, null);

    while (line != null) {

      // process RCS file name for excludes
      final String rcsFileName = line.substring(CVS_RCSFILE_LINE_LENGTH);
      if (processExcludes) {
        if (missingRevisionsHashes.get(new Integer(rcsFileName.hashCode())) != null) {
          // should be excluded, read to the next entry.
          line = IoUtils.readToNotPast(reader, CVS_RCSFILE_LINE, null);
          // and go to the begining of the cycle
          continue;
        }
      }


      // read to the working file name line to get the filename. It is ASSUMED
      // that a line will exist with the working file name on it.
      final String workingFileLine = IoUtils.readToNotPast(reader, CVS_WORKING_FILE_LINE, null);
      if (workingFileLine == null) {
        log.warn("Met unexpected end of changelog file");
        break;
      }
      String workingFileName = workingFileLine.substring(CVS_WORKING_FILE_LINE_LENGTH);
      if (!StringUtils.isBlank(repositoryPath) && workingFileName.startsWith(repositoryPath)) {
        workingFileName = workingFileName.substring(repositoryPathLenth);
      }

      String branchRevisionName = null;
      if (tag != null && !tag.equals(CVS_HEAD_TAG)) {
        // look for the revision of the form "tag: *.(0.)y ". this doesn't work for HEAD
        // get line with branch revision on it.

        final String branchRevisionLine = IoUtils.readToNotPast(reader, '\t' + tag + ": ", CVS_DESCRIPTION);

        if (branchRevisionLine != null) {
          // look for the revision of the form "tag: *.(0.)y "
          branchRevisionName = branchRevisionLine.substring(tag.length() + 3);
          if (branchRevisionName.charAt(branchRevisionName.lastIndexOf('.') - 1) == '0') {
            branchRevisionName = branchRevisionName.substring(0, branchRevisionName.lastIndexOf('.') - 2)
              + branchRevisionName.substring(branchRevisionName.lastIndexOf('.'));
          }
        }
      }

      String nextLine = "";
      while (nextLine != null && !nextLine.startsWith(CVS_FILE_DELIM)) {
        nextLine = IoUtils.readToNotPast(reader, "revision", CVS_FILE_DELIM);
        // are there revision more revision in this file?
        if (nextLine == null) break;

        StringTokenizer tokens = new StringTokenizer(nextLine, " ");
        tokens.nextToken();
        final String revision = tokens.nextToken();
        if (tag != null && !tag.equals(CVS_HEAD_TAG)) {
          final String itsBranchRevisionName = revision.substring(0, revision.lastIndexOf('.'));
          if (!itsBranchRevisionName.equals(branchRevisionName)) {
            break;
          }
        }

        // read to the revision date. It is ASSUMED that each revision
        // section will include this date information line.
        nextLine = IoUtils.readToNotPast(reader, CVS_REVISION_DATE, CVS_FILE_DELIM);
        if (nextLine == null) break; // no more revisions for this file.


        tokens = new StringTokenizer(nextLine, " \t\n\r\f;");
        // first token is the keyword for date, then the next two should be
        // the date and time stamps.
        tokens.nextToken();
        final String dateStamp = tokens.nextToken();
        final String timeStamp = tokens.nextToken();

        // the next token should be the author keyword, then the author name.
        // for versions 1.12.9 and up the next token in "+0000" field.
        final String afterTimeToken = tokens.nextToken();
        final char timeZoneMarker = afterTimeToken.charAt(0);
        String timeZone = null;
        if (timeZoneMarker == '+' || timeZoneMarker == '-') {
          timeZone = afterTimeToken;
          tokens.nextToken(); // this is 1.12.9 or up, skip over the "author" keyword
        } else {
          timeZone = "GMT";
        }
        final Date createdAt = parseModifiedTime(dateStamp, timeStamp, timeZone);

        // user name
        final String userName = tokens.nextToken();

        // the next token should be the state keyword, then the state name.
        tokens.nextToken();
        String stateKeyword;
        try {
          stateKeyword = tokens.nextToken();
        } catch (final NoSuchElementException noStateFoundIgnore) {
          // simeshev@parabuilci.org - 2009-06-23-2009 - For some reasone it
          // looks like it is possible that the state token is missing.
          // See PARABUILD-1329 - "java.util.NoSuchElementException"
          stateKeyword = "";
        }

        // if no lines keyword then file is added
        boolean isAdded = false;
        try {
          tokens.nextToken();
        } catch (final NoSuchElementException noLinesFoundIgnore) {
          isAdded = true;
        }

        // all the text from now to the next revision delimiter or working
        // file delimiter constitutes the messsage.
        final StringBuffer message = new StringBuffer(100);
        nextLine = reader.readLine();
        boolean multiLine = false;

        while (nextLine != null
          && !nextLine.startsWith(CVS_FILE_DELIM)
          && !nextLine.startsWith(CVS_REVISION_DELIM)) {

          if (multiLine) {
            message.append(CVS_NEW_LINE);
          } else {
            multiLine = true;
          }
          message.append(nextLine);

          // go to the next line.
          nextLine = reader.readLine();
        }


        if (stateKeyword.equalsIgnoreCase(CVS_REVISION_DEAD)
          && message.indexOf("was initially added on branch") != -1) {
          // this prevents additions to a branch from showing up as action "deleted" from head
          continue;
        }

        final Change change = new Change();
        change.setRevision(revision);
        change.setFilePath(workingFileName);

        if (stateKeyword.equalsIgnoreCase(CVS_REVISION_DEAD)) {
          change.setChangeType(Change.TYPE_DELETED);
        } else if (isAdded) {
          change.setChangeType(Change.TYPE_ADDED);
        } else {
          change.setChangeType(Change.TYPE_MODIFIED);
        }

        // add change to change list taking in account possible time dispersion
        changeListAccumulator.add(createdAt, message, userName, branch, change);
      }
      // Read to the next RCS file line. The CVS_FILE_DELIMITER may have
      // been consumed by the parseEntry method, so we cannot read to it.
      line = IoUtils.readToNotPast(reader, CVS_RCSFILE_LINE, null);
    }
    if (log.isDebugEnabled()) log.debug("parseChangeLog - end");
    return changeListAccumulator.getChangeLists();
  }


  /**
   * Advises parser on repository path. If set, it will be
   * stripped from the change file name.
   */
  public void setRepositoryPath(final String repositoryPath) {
    this.repositoryPath = repositoryPath.endsWith("/") ? repositoryPath : repositoryPath + '/';
    this.repositoryPathLenth = this.repositoryPath.length();
  }


  /**
   * Sets hashes for missing RCS names. If an RCS name is missing
   * it will not make to the change list.
   *
   * @param missingRevisionsHashes Map hashes for missing RCS
   * names. Key is hash and non-null Object is value.
   */
  public void setRCSNamesHashesToExclude(final Map missingRevisionsHashes) {
    this.missingRevisionsHashes = missingRevisionsHashes;
  }


  /**
   * Returns CVS modification time
   */
  private Date parseModifiedTime(final String dateStamp, final String timeStamp, final String timeZone) throws IOException {
    try {
      // NOTE: This replace is needed for CVS version 1.12.9 and up- see bug #634.
      final String toParse = dateStamp.replace('-', '/');
      return CVS_LOG_DATE_FORMATTER.parse(toParse + ' ' + timeStamp + ' ' + timeZone);
    } catch (final ParseException pe) {
      throw new IOException("Error parsing date and time: " + StringUtils.toString(pe));
    }
  }


  /**
   * Sets branch name for information purposes to be used to set
   * branch name in the resulting change list
   */
  public void setBranchName(final String cvsBranch) {
    this.branch = cvsBranch;
  }
}
