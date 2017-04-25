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
 * Change log parser that can parse record-based change logs.
 */
public final class TokenizingChangeLogParser {

  private static final Log log = LogFactory.getLog(TokenizingChangeLogParser.class);
  
  private static final int MAX_TOKENS = 6;
  private static final int MIN_TOKENS = MAX_TOKENS - 1;

  private final String[] checkinTypes;
  private final String[] makeTypes;
  private final String[] ignoreTypes;
  private final String[] nullTypes;
  private final String[] addTypes;
  private final String[] deleteTypes;

  private final SimpleDateFormat outDateFormat;
  private final int maxChangeLists;
  private final String branch;
  private final String endOfRecordToken;
  private final String fieldSeparator;
  private final int fieldSeparatorLength;
  private final long timeWindowMillis;
  private final int maxChangeListSize;


  public TokenizingChangeLogParser(final int maxChangeLists, final long timeWindowMillis, final String branch,
    final String fieldSeparator, final String endOfRecord, final String changeDatePattern,
    final String[] checkinTypes, final String[] makeTypes,
    final String[] nullTypes, final String[] addTypes,
    final String[] deleteTypes, final String[] ignoreTypes,
    final int maxChangeListSize) {

    this.maxChangeLists = maxChangeLists;
    this.branch = branch;
    this.endOfRecordToken = endOfRecord;
    this.fieldSeparator = fieldSeparator;
    this.fieldSeparatorLength = fieldSeparator.length();
    this.checkinTypes = checkinTypes;
    this.makeTypes = makeTypes;
    this.ignoreTypes = ignoreTypes;
    this.nullTypes = nullTypes;
    this.addTypes = addTypes;
    this.deleteTypes = deleteTypes;
    this.timeWindowMillis = timeWindowMillis;
    this.outDateFormat = new SimpleDateFormat(changeDatePattern);
    this.maxChangeListSize = maxChangeListSize;
  }


  /**
   * Parces ClearCase change log.
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
   * Parces record-based change log.
   *
   * @param input InputStream to get log data from.
   *
   * @return List of change lists.
   */
  public List parseChangeLog(final InputStream input) throws IOException {
    final String lineSeparator = System.getProperty("line.separator");

    String line = null;
//    String lines = "";
    StringBuffer lines = new StringBuffer(100);
    final TimeWindowChangeListAccumulator accumulator = new TimeWindowChangeListAccumulator(timeWindowMillis, maxChangeLists, maxChangeListSize);
    final BufferedReader br = new BufferedReader(new InputStreamReader(input));
    while ((line = br.readLine()) != null) {
      //if (log.isDebugEnabled()) log.debug("line: " + line);
      if (!(lines.length() == 0)) {
        lines.append(lineSeparator);
      }
      lines.append(line);
      final int eoqIndex = lines.indexOf(endOfRecordToken);
      if (eoqIndex > -1) {

        // parse line
        final String lineToParse = lines.substring(0, eoqIndex);
        final String[] tokens = new String[MAX_TOKENS];
        Arrays.fill(tokens, "");
        int tokenIndex = 0;
        //if (log.isDebugEnabled()) log.debug("fieldSeparator = " + fieldSeparator);
        //if (log.isDebugEnabled()) log.debug("endOfRecordToken = " + endOfRecordToken);
        for (
          int startIndex = 0, stopIndex = lineToParse.indexOf(fieldSeparator, 0);
          tokenIndex < MAX_TOKENS;
          startIndex = stopIndex + fieldSeparatorLength, stopIndex = lineToParse.indexOf(fieldSeparator, startIndex), tokenIndex++) {
          if (stopIndex == -1) {
            tokens[tokenIndex] = lineToParse.substring(startIndex);
            break;
          } else {
            tokens[tokenIndex] = lineToParse.substring(startIndex, stopIndex);
          }
        }
        //if (log.isDebugEnabled()) log.debug("tokenIndex: " + tokenIndex);
        //if (log.isDebugEnabled()) log.debug("MIN_TOKENS: " + MIN_TOKENS);
        if (tokenIndex < MIN_TOKENS) continue;  // unknown entity format, skip

        // extract data
        final String userName = tokens[0].trim();
        final String createdAtString = tokens[1].trim();
        final String elementName = tokens[2].trim();
        final String revision = tokens[3].trim();
        final String changeTypeString = tokens[4].trim();
        final StringBuffer comment = new StringBuffer(tokens[5].trim());

        // ignore branch operation
        if (isInType(changeTypeString, ignoreTypes)) {
          continue;
        }

        // create change and set revision
        final Change change = new Change();
        change.setRevision(revision);
        change.setFilePath(elementName);

        // get change type
        byte changeType = Change.TYPE_UNKNOWN;
        if (isInType(changeTypeString, checkinTypes)) {
          changeType = Change.TYPE_CHECKIN;
        } else if (isInType(changeTypeString, makeTypes)) {
          changeType = Change.TYPE_CREATE_ELEMENT;
        } else if (isInType(changeTypeString, nullTypes)) {
          changeType = Change.TYPE_NULL;
        } else if (isInType(changeTypeString, addTypes)) {
          changeType = Change.TYPE_ADDED;
        } else if (isInType(changeTypeString, deleteTypes)) {
          changeType = Change.TYPE_DELETED;
        } else {
          comment.append("(unknown operation kind: \"").append(changeTypeString).append("\")");
        }
        change.setChangeType(changeType);

        Date createdAt = null;
        try {
          createdAt = outDateFormat.parse(createdAtString);
        } catch (ParseException e) {
          if (log.isDebugEnabled()) log.debug("Error parsing change log date", e);
          continue;
        }

        // add to accumulator
        accumulator.add(createdAt, comment, userName, branch, change);
        lines = new StringBuffer(100);
      }
    }

    // validate and return
    //noinspection UnnecessaryLocalVariable
    final List changeLists = accumulator.getChangeLists(); // NOPMD

    // return result
    return changeLists;
  }


  private boolean isInType(final String type, final String[] typeList) {
    final String lowerCaseType = type.toLowerCase();
    if (typeList.length == 1) {
      return typeList[0].equals(lowerCaseType);
    } else if (typeList.length == 0) {
      return false;
    }

    for (int i = 0; i < typeList.length; i++) {
      if (typeList[i].equals(lowerCaseType)) return true;
    }
    return false;
  }
}
