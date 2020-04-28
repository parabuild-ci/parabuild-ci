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
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.CommandStoppedException;
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
import java.util.Locale;

/**
 *
 */
final class PVCSVlogParser {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(PVCSVlogParser.class); // NOPMD

  private static final String REVISION_DELIMITER = "-----------------------------------";
  private static final String FILE_DELIMITER = "===================================";

  private static final String ARCHIVE_STRING = "Archive:";
  private static final String AUTHOR_ID_STRING = "Author id: ";
  private static final String BKWD_ARCHIVES_STRING = "\\archives";
  private static final String CHECKED_IN_STRING = "Checked in:";
  private static final String DESCRIPTION_STRING = "Description:";
  private static final String FWD_ARCHIVES_STRING = "/archives";
  private static final String INITIAL_REVISION_STRING = "Initial revision.";
  private static final String REV_COUNT_STRING = "Last trunk rev:";
  private static final String STRING_BRANCHES = "Branches:";

  private static final int ARCHIVE_STRING_LENGTH = ARCHIVE_STRING.length();
  private static final int BKWD_ARCHIVES_STRING_LENGTH = BKWD_ARCHIVES_STRING.length();
  private static final int FWD_ARCHIVES_STRING_LENGTH = FWD_ARCHIVES_STRING.length();
  private static final int DESCRIPTION_STRING_LENGTH = DESCRIPTION_STRING.length();
  private static final int AUTHOR_ID_STRING_LENGTH = AUTHOR_ID_STRING.length();
  private static final int CHECKED_IN_STRING_LENGTH = CHECKED_IN_STRING.length();


  private final PVCSDateFormat dateFormat;
  private final PVCSVlogHandler vlogHandler;
  private final String repository;
  private final String branch;
  private final String project;


  /**
   * Constructor
   *
   * @param locale {@link Locale} to use when
   * @see PVCSVlogHandler
   */
  public PVCSVlogParser(final Locale locale, final String repository, final String project, final String branch, final PVCSVlogHandler vlogHandler) {
    this.dateFormat = new PVCSDateFormat(locale);
    this.repository = ArgumentValidator.validateArgumentNotBlank(repository, "repository");
    this.project = ArgumentValidator.validateArgumentNotBlank(project, "project");
    this.branch = branch;
    this.vlogHandler = vlogHandler;
  }


  /**
   * Parces PVCS change log. Parser expects that changes are
   * passed in revers order - latest come first.
   *
   * @param file to parse
   */
  public void parseChangeLog(final File file) throws IOException, AgentFailureException {
    InputStream is = null;
    try {
      is = new FileInputStream(file);
      parseChangeLog(is);
    } finally {
      IoUtils.closeHard(is);
    }
  }


  /**
   * Parces SVN change log. Parser expects that changes are
   * passed in revers order - latest come first.
   *
   * @param input InputStream to get log data from.
   */
  public void parseChangeLog(final InputStream input) throws IOException, AgentFailureException {
    try {
      final boolean trimLine = !StringUtils.isBlank(branch);
      final int repositoryLength = repository.length();
      vlogHandler.beforeHandle();
      final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      String line = IoUtils.readToNotPast(reader, ARCHIVE_STRING, null, trimLine);
      int heuristicFileIndex = -1;
      while (line != null) {
        // we are at the begining of the file at "Archive"

        // calculate trailing archive index
        int arcIndex = line.indexOf("-arc");
        if (arcIndex == -1) arcIndex = line.length();

//        if (log.isDebugEnabled()) log.debug("line: " + line);
//        if (log.isDebugEnabled()) log.debug("repository: " + repository);
//        if (log.isDebugEnabled()) log.debug("line: " + line);
//        if (log.isDebugEnabled()) log.debug("repositoryIndex 1 : " + repositoryIndex);

        // calculate leading file index
        int fileIndex = -1;
        final int repositoryIndex = line.indexOf(repository);
        if (repositoryIndex == -1) {
          // try to find heuristics index using project path
          if (heuristicFileIndex == -1) {
            final String lineForwardSlashed = line.replace('\\', '/');
            final int possibleProjectIndex = lineForwardSlashed.indexOf(project);
            if (possibleProjectIndex >= 0) {
              heuristicFileIndex = possibleProjectIndex;
              fileIndex = heuristicFileIndex;
            }
          } else {
            fileIndex = heuristicFileIndex;
          }
        } else {
          fileIndex = repositoryIndex + repositoryLength;
        }

        if (fileIndex == -1) {
          fileIndex = ARCHIVE_STRING_LENGTH;
        }
//        if (log.isDebugEnabled()) log.debug("repositoryIndex 2 : " + repositoryIndex);

        // get file name
        String filePath = line.substring(fileIndex, arcIndex);
//        if (log.isDebugEnabled()) log.debug("line: " + line);
//        if (log.isDebugEnabled()) log.debug("filePath: " + filePath);
        if (filePath.startsWith(FWD_ARCHIVES_STRING)) filePath = filePath.substring(FWD_ARCHIVES_STRING_LENGTH);
        if (filePath.startsWith(BKWD_ARCHIVES_STRING)) filePath = filePath.substring(BKWD_ARCHIVES_STRING_LENGTH);

        // first revision?
        line = IoUtils.readToNotPast(reader, REV_COUNT_STRING, DESCRIPTION_STRING, trimLine);
        boolean firstRevision = false;
        if (line.startsWith(REV_COUNT_STRING)) {
          final String stringRevCount = line.substring(REV_COUNT_STRING.length()).trim();
          firstRevision = StringUtils.isValidInteger(stringRevCount) && Integer.parseInt(stringRevCount) == 1;
        }
        // read archive archiveDescription in case the revision is the
        // first revision so that we can use this instead of
        // "Initial revision".
        final StringBuilder archiveDescription = new StringBuilder(100);
        line = IoUtils.readToNotPast(reader, DESCRIPTION_STRING, null, trimLine);
        boolean firstLine = true;
        while (line != null && !line.equals(REVISION_DELIMITER)) {
          if (firstLine) {
            archiveDescription.append(line.substring(DESCRIPTION_STRING_LENGTH));
            firstLine = false;
          } else {
            archiveDescription.append(line);
          }
          line = IoUtils.readAndTrim(reader, trimLine);
          if (line != null && !line.equals(REVISION_DELIMITER)) archiveDescription.append('\n');
        }

        while (line != null && !line.equals(FILE_DELIMITER)) {

          // get revision
          line = IoUtils.readToNotPast(reader, "Rev ", null, trimLine);
          if (line == null) break;
          final String revision = line.substring("Rev ".length());

          // get change date
          line = IoUtils.readToNotPast(reader, CHECKED_IN_STRING, null, trimLine);
          if (line == null) break;
          final String stringChangeDate = line.substring(CHECKED_IN_STRING_LENGTH).trim();
          final Date changeDate = dateFormat.parseOutput(stringChangeDate);

          // get owner
          line = IoUtils.readToNotPast(reader, AUTHOR_ID_STRING, null, trimLine);
          if (line == null) break;
          int endOfOwnerName = line.indexOf(' ', AUTHOR_ID_STRING_LENGTH);
          if (endOfOwnerName == -1) endOfOwnerName = line.length();
          final String owner = line.substring(AUTHOR_ID_STRING_LENGTH, endOfOwnerName);

          // get revision description
          StringBuffer revisionDescription = new StringBuffer(100);
          line = IoUtils.readAndTrim(reader, trimLine); // nl
          while (line != null && !line.equals(FILE_DELIMITER) && !line.equals(REVISION_DELIMITER)) {
            // skip Branches: that may follow the "Author is:"
            if (line.startsWith(STRING_BRANCHES)) {
              line = reader.readLine();
              continue;
            }
            revisionDescription.append(line);
            line = IoUtils.readAndTrim(reader, trimLine);
            if (line != null && !line.equals(FILE_DELIMITER)
                    && !line.equals(REVISION_DELIMITER)) {
              revisionDescription.append('\n');
            }
          }
          if (revisionDescription.toString().equals(INITIAL_REVISION_STRING)) {
            revisionDescription = new StringBuffer(archiveDescription.toString());
          }

          // detect type
          byte changeType = Change.TYPE_UNKNOWN;
          if (firstRevision) {
            changeType = Change.TYPE_ADDED;
          } else {
            changeType = Change.TYPE_CHECKIN;
          }

          // call handler
//          if (log.isDebugEnabled())  log.debug("change: " + change);
          vlogHandler.handle(changeDate, revisionDescription, owner, branch,
                  filePath.replace('\\', '/'), revision, changeType);
        }
        line = IoUtils.readToNotPast(reader, ARCHIVE_STRING, null, trimLine);
      }
      vlogHandler.afterHandle();
    } catch (final ParseException e) {
      throw IoUtils.createIOException(e);
    } catch (final CommandStoppedException e) {
      throw IoUtils.createIOException(e);
    }
  }
}
