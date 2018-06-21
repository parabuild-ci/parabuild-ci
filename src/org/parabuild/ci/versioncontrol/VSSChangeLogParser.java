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
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.Change;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Parses output of ss.exe history.
 *
 * @see VSSSourceControl
 */
final class VSSChangeLogParser {

  private static final Log log = LogFactory.getLog(VSSChangeLogParser.class);

  public static final String STR_BACKSLASH = "\\";
  public static final String STR_SLASH = "/";

  private static final String STR_VSS_ITEM_START = "*****";
  private static final String STR_VSS_LABEL_START = "**********************";
  private static final String STR_VSS_USER = "User: ";
  private static final String STR_VSS_DATE = "Date: ";
  private static final String STR_VSS_ADDED = "added";
  private static final String STR_VSS_BRANCHED = "branched";
  private static final String STR_VSS_DELETED = "deleted";
  private static final String STR_VSS_DESTROYED = "destroyed";
  private static final String STR_VSS_RECOVERED = "recovered";
  private static final String STR_VSS_RENAMED_TO = " renamed to ";
  private static final String STR_VSS_SHARED = "shared";
  private static final String STR_VSS_COMMENT = "Comment:";
  private static final String STR_VSS_VERSION = "Version";
  private static final int INT_VSS_COMMENT_IDX = STR_VSS_COMMENT.length();
  private static final int INT_VSS_USER_IDX = STR_VSS_USER.length();
  private static final int INT_VSS_VERSION_IDX = STR_VSS_VERSION.length();
  private static final long INT_VSS_TIME_WINDOW_MILLIS = 60000L;

  private static final String STR_VSS_PURGED = "purged";
  private static final String STR_VSS_UNPINNED = "unpinned";
  private static final String STR_VSS_MOVED_FROM = " moved from ";

  private String projectPath = null;
  private String projectBranch = "";
  private final int maxChangeLists;
  private final VSSDateFormatFactory formatFactory;
  private final int maxChangeListSize;


  public VSSChangeLogParser(final Locale locale, final int maxChangeLists, final int maxChangeListSize) {
    this.maxChangeLists = maxChangeLists;
    this.formatFactory = new VSSDateFormatFactory(locale);
    this.maxChangeListSize = maxChangeListSize;
  }


  /**
   * Sets VSS project path used to configure VSS source
   * control the log is parsed for.
   *
   * @param projectPath VSS project path used to configure
   * VSS version control the log is parsed for.
   */
  public void setProjectPath(final String projectPath) {
    this.projectPath = projectPath;
  }


  /**
   * Sets VSS project branch used to configure VSS source
   * control the log is parsed for.
   *
   * @param projectBranch VSS project path used to configure
   * VSS version control the log is parsed for.
   */
  public void setProjectBranch(final String projectBranch) {
    this.projectBranch = projectBranch;
  }


  /**
   * Parses VSS change long an returns list of change lists.
   */
  public List parseChangeLog(final File changeLogFile) throws IOException {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(changeLogFile), 10000);
      return parseChangeLog(br);
    } finally {
      IoUtils.closeHard(br);
    }
  }


  /**
   * Parses VSS change long an returns list of change lists.
   */
  public List parseChangeLog(final BufferedReader changeLogReader) throws IOException {
    final TimeWindowChangeListAccumulator accumulator = new TimeWindowChangeListAccumulator(INT_VSS_TIME_WINDOW_MILLIS, maxChangeLists, maxChangeListSize);

    // go through the file
    for (String line = changeLogReader.readLine(); line != null;) {
      if (line.startsWith(STR_VSS_ITEM_START)) {
        // read
        final List changeLines = new ArrayList(11);
        do {
          changeLines.add(line);
          line = changeLogReader.readLine();
        } while (line != null && !line.startsWith(STR_VSS_ITEM_START));

        // parse change lines
        try {
          // ignore labels and un-usual labels for dirs that we can not parse
          if (changeLines.get(0).equals(STR_VSS_LABEL_START)
            || changeLines.size() > 4 && ((String)changeLines.get(3)).startsWith("Labeled")) {
            continue;
          }

          // change list attributes
          String version = "";
          final StringBuffer comment = new StringBuffer(100);

          // change
          final Change change = new Change();

          // parse version
          if (((String)changeLines.get(0)).startsWith("*****  ")) {
            // line at idx 1 is version
            final String versionLine = (String)changeLines.get(1);
            version = versionLine.substring(INT_VSS_VERSION_IDX).trim();
          }


          // skip label lines
          int nameDateIndex = 2;
          if (((String)changeLines.get(0)).startsWith("***************** ")) {
            nameDateIndex = 1;
          }
          String nameDateLine = (String)changeLines.get(nameDateIndex);
          if (nameDateLine.startsWith("Label:")) {
            nameDateIndex++;
            nameDateLine = (String)changeLines.get(nameDateIndex);
          }

          // parse user
          String user = "";
          user = nameDateLine.substring(INT_VSS_USER_IDX, nameDateLine.indexOf(STR_VSS_DATE) - 1).trim();

          // parse date
          String dateTime = nameDateLine.substring(nameDateLine.indexOf(STR_VSS_DATE));

          final int indexOfColon = dateTime.indexOf("/:");
          if (indexOfColon != -1) {
            dateTime = dateTime.substring(0, indexOfColon)
              + dateTime.substring(indexOfColon, indexOfColon + 2).replace(':', '0')
              + dateTime.substring(indexOfColon + 2);
          }

          Date modified = null;
          if (formatFactory.isEUFormat()) {
            modified = formatFactory.inputDateTimeFormat().parse(dateTime.trim());
          } else {
            modified = formatFactory.inputDateTimeFormat().parse(dateTime.trim() + 'm');
          }

          // parse file name
          String fileName = null;
          String filePath = null;
          final String filePathLine = (String)changeLines.get(0);
          final String fileNameLine = (String)changeLines.get(nameDateIndex + 1);
          if (fileNameLine.startsWith("Checked in")) {
            change.setChangeType(Change.TYPE_MODIFIED);

            // set file name
            filePath = fileNameLine.substring(12);
            fileName = filePathLine.substring(7, filePathLine.indexOf("  *"));

            // parse comment
            final int commentIndex = nameDateIndex + 2;
            // remove "Comment:"
            final String commentFirstLine = (String)changeLines.get(commentIndex);
            if (commentFirstLine.startsWith(STR_VSS_COMMENT)) {
              comment.append(commentFirstLine.substring(INT_VSS_COMMENT_IDX));
            } else {
              comment.append(commentFirstLine);
            }
            comment.append(' ');
            for (int i = commentIndex + 1; i < changeLines.size(); i++) {
              comment.append(changeLines.get(i)).append(' ');
            }
          } else if (fileNameLine.endsWith("Created")) {
            // REVIEWME: here we ignore directory addition - VSS lists directory
            // one by one (dir/test/file as dir than test), so it does not make sense
            // to include them into the change list till we we reassable full dir name.
            continue;
            //
            // change.setChangeType(Change.TYPE_ADDED);
          } else {
            filePath = projectPath + (nameDateIndex == 1 ? "" : STR_BACKSLASH + filePathLine.substring(7, filePathLine.indexOf("  *")));
            final int lastSpaceIndex = fileNameLine.lastIndexOf(' ');
            if (lastSpaceIndex == -1) {
              fileName = fileNameLine;
            } else {
              fileName = fileNameLine.substring(0, lastSpaceIndex);
            }

            if ("Branched".equals(fileName)) {
              continue; // ignore branched file here
            }

            if (fileNameLine.endsWith(STR_VSS_ADDED)) {
              change.setChangeType(Change.TYPE_ADDED);
            } else if (fileNameLine.endsWith(STR_VSS_BRANCHED)) {
              change.setChangeType(Change.TYPE_BRANCHED);
            } else if (fileNameLine.endsWith(STR_VSS_DELETED)) {
              change.setChangeType(Change.TYPE_DELETED);
            } else if (fileNameLine.endsWith(STR_VSS_DESTROYED)) {
              change.setChangeType(Change.TYPE_DESTROYED);
            } else if (fileNameLine.endsWith(STR_VSS_RECOVERED)) {
              change.setChangeType(Change.TYPE_RECOVERED);
            } else if (fileNameLine.indexOf(STR_VSS_RENAMED_TO) != -1) {
              fileName = fileNameLine;
              change.setChangeType(Change.TYPE_RENAMED);
            } else if (fileNameLine.endsWith(STR_VSS_SHARED)) {
              continue; // ignore share, does not change source line status
            } else if (fileNameLine.endsWith(STR_VSS_PURGED)) {
              continue; // ignore purged, does not change source line status
            } else if (fileNameLine.endsWith(STR_VSS_UNPINNED)) {
              continue; // ignore unpinned, does not change source line status
            } else if (fileNameLine.indexOf(STR_VSS_MOVED_FROM) > -1) {
              continue; // REVIEWME: ignore moved for now, check if affect state of src line.
            } else {
              reportUnknownVSSAction(changeLines, fileNameLine);
              continue;
            }
          }

          if (filePath == null && fileName == null) {
            if (log.isDebugEnabled()) log.debug("ignoring empty file");
          }

          change.setFilePath(filePath + STR_SLASH + fileName);
          change.setRevision(version);
          accumulator.add(modified, comment, user, projectBranch, change);
        } catch (final Exception e) {
          reportUnexpectedItemProcessingError(changeLines, e);
        }
      } else {
        line = changeLogReader.readLine();
      }
    }
    return accumulator.getChangeLists();
  }


  /**
   * Helper method
   */
  private void reportUnexpectedItemProcessingError(final List changeLines, final Exception e) {
    final Error error = new Error("Error while processing VSS item: " + StringUtils.toString(e));
    error.setDetails(e);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setLogLines(StringUtils.linesToString(changeLines));
    error.setSendEmail(false);
    reportError(error);
  }


  /**
   * Helper method
   */
  private void reportUnknownVSSAction(final List changeLines, final String line) {
    final Error error = new Error("Unknown action met while processing VSS item: " + line);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setLogLines(StringUtils.linesToString(changeLines));
    error.setSendEmail(false);
    reportError(error);
  }


  /**
   * Helper method
   */
  private void reportError(final Error error) {
    final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.reportSystemError(error);
  }
}
