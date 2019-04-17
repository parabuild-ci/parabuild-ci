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
package org.parabuild.ci.versioncontrol.perforce;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.configuration.ChangeListIssueBinding;
import org.parabuild.ci.configuration.ChangeListsAndIssues;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.Issue;
import org.parabuild.ci.object.SystemProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class is responsible for parsing P4 change log generated as a result of
 * <p/>
 * <p/>
 * p4 -s changes -s submitted
 * </p>
 * <p/>
 * command.
 */
final class P4ChangeLogParser {

  private static final Log log = LogFactory.getLog(P4ChangeLogParser.class);

  private static final String P4_JOBS_FIXED = "text: Jobs fixed ...";
  private static final String P4_AFFECTED_FILES = "text: Affected files ...";
  private static final String P4_TEXT_JOB_DESCR = P4ParserHelper.P4_TEXT + " \t";
  private static final String P4_TEXT_CHANGE_DESCR = P4ParserHelper.P4_TEXT + " \t";
  private static final String P4_TEXT_SPACE = P4ParserHelper.P4_TEXT + ' ';


  /**
   * This constant defines maximum size of a change list
   * List to be included into the result. For details see
   * bug #1010.
   */
  public static final int MAX_CHANGE_LIST_CHUNK_SIZE = 500;

  private boolean jobsCollectionEnabled = false;
  private int maxChangeListSize = SystemProperty.DEFAULT_MAX_CHANGE_LIST_SIZE;


  public P4ChangeLogParser() {
    this(SystemProperty.DEFAULT_MAX_CHANGE_LIST_SIZE);
  }


  public P4ChangeLogParser(final int maxChangeListSize) {
    this.maxChangeListSize = maxChangeListSize;
  }


  public void enableJobCollection(final boolean enabled) {
    jobsCollectionEnabled = enabled;
  }


  /**
   * Sets maximim change list size (number of changes in a changelist).
   *
   * @param maxChangeListSize maximim change list size to set
   */
  public void setMaxChangeListSize(final int maxChangeListSize) {
    this.maxChangeListSize = maxChangeListSize;
  }


  /**
   * Parses P4 change log generated as a result of
   * <p/>
   * <p/>
   * p4 -s changes -s submitted
   * </p>
   * <p/>
   * command.
   *
   * @param changesIS InputStream containing output of the command
   *
   * @return List of lists of Strings containing change numbers
   */
  public Collection parseChangesLog(final InputStream changesIS) throws BuildException {
    return parseChangesLog(changesIS, Integer.MAX_VALUE);
  }


  public Collection parseChangesLog(final InputStream changesIS, final int maxResultSize) throws BuildException {
    BufferedReader br = null;

    try {
      // get line reader and read first line
      br = new BufferedReader(new InputStreamReader(changesIS), 1024);
      String lineToParse = br.readLine();
      validateChangeListIsNotEmpty(lineToParse);
      List chunk = makeNewChunk(); // init, will be overwritten in the cycle
      final LinkedList result = new LinkedList();
      while (lineToParse != null) {
        // check for errors
        P4ParserHelper.validateLine(lineToParse);
        // parse
        if (lineToParse.startsWith(P4ParserHelper.P4_INFO)) {
          final StringTokenizer st = new StringTokenizer(lineToParse);
          // skip info token
          st.nextToken();
          // skip Change token
          st.nextToken();
          if (chunk.size() >= MAX_CHANGE_LIST_CHUNK_SIZE) {
            addToResult(result, maxResultSize, chunk);
            // new chunk
            chunk = makeNewChunk();
          }
          chunk.add(st.nextToken());
        } else if (lineToParse.startsWith(P4ParserHelper.P4_EXIT_0)) {
          break;
        }
        // next line
        lineToParse = br.readLine();
      }
      // add leftover
      if (!chunk.isEmpty()) {
        addToResult(result, maxResultSize, chunk);
      }
      return result;
    } catch (final IOException e) {
      throw new BuildException(P4ParserHelper.makeUnexpectedErrorMessage(e.toString()));
    } finally {
      IoUtils.closeHard(br);
    }
  }


  private static void addToResult(final LinkedList result, final int maxResultSize, final List chunk) {
    if (result.size() >= maxResultSize) {
      // delete topmost, if necessary
      result.removeFirst();
    }
    // add chunk to result
    result.add(chunk);
  }


  private static List makeNewChunk() {
    return new ArrayList(MAX_CHANGE_LIST_CHUNK_SIZE);
  }


  public void parseDescribeLog(final P4ChangeDriver changeDriver, final InputStream describeIS) throws BuildException {
    try {
      final SimpleDateFormat p4JobDateFormatter = getP4JobDateFormatter();
      final SimpleDateFormat p4DescribeDateFormatter = getP4DescribeDateFormatter();
      final BufferedReader reader = new BufferedReader(new InputStreamReader(describeIS));
      final Map jobNameToIssueMap = new HashMap(11);

      // read change list
      String line;
      while ((line = IoUtils.readUntil(reader, P4ParserHelper.P4_TEXT_CHANGE, "exit:")) != null) {
        final ChangeList changelist = new ChangeList();
        // check for errors
        P4ParserHelper.validateLine(line);
        if (line.startsWith(P4ParserHelper.P4_TEXT_CHANGE)) {
          final StringTokenizer st = new StringTokenizer(line);
          st.nextToken(); // skip "text" token
          st.nextToken(); // skip "Change" token
          changelist.setNumber(st.nextToken());
          st.nextToken(); // skip "by" token
          // parse user@client
          final StringTokenizer stUser = new StringTokenizer(st.nextToken(), "@");
          changelist.setUser(stUser.nextToken());
          changelist.setClient(stUser.nextToken());
          st.nextToken(); // skip "on" token
          final Date date = p4DescribeDateFormatter.parse(st.nextToken() + ' ' + st.nextToken());
          changelist.setCreatedAt(date);
        } else if (line.startsWith(P4ParserHelper.P4_EXIT_0)) {
          return; // done
        }

        //
        // parse description

        // skip blank line
        reader.readLine();
        final StringBuilder description = new StringBuilder(100);
        // Use this since we don't want the final (empty) line
        String prevLine = null;
        line = reader.readLine();
//        if (log.isDebugEnabled()) log.debug("line: " + line);
        while (line != null
          && (line.startsWith(P4_TEXT_CHANGE_DESCR)
          || !line.startsWith(P4_AFFECTED_FILES) && !line.startsWith(P4_JOBS_FIXED))
          ) {

          if (prevLine != null) {
            if (description.length() > 0) {
              description.append('\n');
            }
            description.append(prevLine);
          }
          try {
            if (line.startsWith(P4ParserHelper.P4_TEXT)) {
              prevLine = line.substring(P4ParserHelper.P4_TEXT.length()).trim();
            } else {
              prevLine = line.trim();
            }
          } catch (final Exception e) {
            throw new BuildException(P4ParserHelper.makeUnexpectedErrorMessage(e.toString() + ", p4 describe line: " + line));
          }

          // skip empty CR or LF or both, line
          line = IoUtils.skipEmptyLines(reader);
//          if (log.isDebugEnabled()) log.debug("line: " + line);
        }

        changelist.setDescription(description.toString());

        // ========================================================================================================
//        text: Change 8 by test_user@vimeshev on 2004/06/13 12:06:44
//        text:
//        text: 	test job job000001
//        text:
//        text: Jobs fixed ...
//        text:
//        text: job000001 on 2004/06/13 by test_user *closed*
//        text:
//        text: 	This is a first test job
//        text: 	that contains multiple lines
//        text: 	in drescription
//        text:
//        text: job000003 on 2004/06/13 by test_user *closed*
//        text:
//        text: 	test job
//        text:
//        text: Affected files ...
//        text:
//        info1: //test/sourceline/alwaysvalid/src/readme.txt#6 edit
//        text:
//        exit: 0

        // ========================================================================================================

        //
        // parse jobs for this given change list
        if (line != null && line.equals(P4_JOBS_FIXED)) {
          reader.readLine(); // go to the next line after "text: Jobs fixed ..."
          line = reader.readLine(); // go to the line with job description
          while (line != null && !line.startsWith(P4_AFFECTED_FILES)) {
            // parse job line
//            if (log.isDebugEnabled()) log.debug("jobs definition: " + line);
            final StringTokenizer st = new StringTokenizer(line);
            if (st.countTokens() == 7) {
              st.nextToken(); // skip "text:"
              final String jobName = st.nextToken();
              st.nextToken(); // skip "on"

              // parse date
              Date date = null;
              try {
                date = p4JobDateFormatter.parse(st.nextToken());
              } catch (final ParseException e) {
                // unexpected jobs definition
                IoUtils.ignoreExpectedException(e);
              }
              if (date != null) {
                st.nextToken(); // skip "by"
                final String jobUser = st.nextToken();
                final String jobStatus = st.nextToken();
                reader.readLine(); // skip next "text:" before job description

                // parse job description
                final StringBuilder jobDescr = new StringBuilder(200);
                line = reader.readLine();
                while (line != null
                  && (line.startsWith(P4_TEXT_JOB_DESCR)
                  || !line.startsWith(P4_AFFECTED_FILES) && !line.startsWith(P4_TEXT_SPACE))) {
//              if (log.isDebugEnabled()) log.debug("line: " + line);
                  jobDescr.append(line.startsWith(P4_TEXT_JOB_DESCR) ? line.substring(P4_TEXT_JOB_DESCR.length()) : line);
                  jobDescr.append('\n');
                  // next line
                  line = reader.readLine();
                }
//                if (log.isDebugEnabled()) log.debug("jobDescr: " + jobDescr.toString());

                // keep closed job
                if (jobsCollectionEnabled && "*closed*".equals(jobStatus)) {
                  // NOTE: vimeshev - looking up if we have already came
                  // across an issue with this name while processing this
                  // describe log. We assume that job name uniquely
                  // identifies a job.
                  Issue issue = (Issue)jobNameToIssueMap.get(jobName);
                  if (issue == null) {
                    issue = new Issue();
                    issue.setClosed(date);
                    issue.setClosedBy(jobUser);
                    issue.setDescription(jobDescr.toString());
                    issue.setKey(jobName);
                    issue.setReceived(new Date());
                    issue.setStatus(jobStatus);
                    issue.setTrackerType(Issue.TYPE_PERFORCE);
                    jobNameToIssueMap.put(jobName, issue);
                  }

                  changeDriver.processIssue(issue);
                }
              } else {
                // date cannot be parsed
                if (log.isDebugEnabled()) log.debug("unexpected jobs definition: " + line);
                // skip broken job definifion
                reader.readLine();
              }
            } else {
              // number of tokens is not 6
              if (log.isDebugEnabled()) log.debug("unexpected jobs definition: " + line);
              // skip broken job definifion
              reader.readLine();
            }

            // next line
            line = reader.readLine();
          }
        }

        // read changes
        if (line != null) {
          int changeCounter = 0;
          reader.readLine(); // read past next 'text:'
          int changesProcessed = 0;
          while ((line = IoUtils.readUntil(reader, P4ParserHelper.P4_INFO_1, P4ParserHelper.P4_TEXT)) != null && line.startsWith(P4ParserHelper.P4_INFO_1)) {
            changeCounter++;
            if (changeCounter <= maxChangeListSize) {
              changeDriver.processChange(parseChangeLine(line));
              changesProcessed++;
            }
          }
          changelist.setTruncated(changesProcessed < changeCounter);
          changelist.setOriginalSize(changeCounter);
        }
        changeDriver.processChangeList(changelist);
      }
    } catch (final BuildException | RuntimeException e) {
      throw e;
    } catch (final Exception e) {
      throw new BuildException(P4ParserHelper.makeUnexpectedErrorMessage(e.toString()));
    }
  }


  public void parseDescribeLog(final ChangeListsAndIssues changeListsAndIssuesAccumulator, final InputStream describeIS) throws BuildException {
    parseDescribeLog(new P4AccumulatingChangeDriver(changeListsAndIssuesAccumulator), describeIS);
  }


  /**
   * Helper method to parse a line containing Perforce change.
   *
   * @param line String to parse
   *
   * @return resulting {@link Change}
   *
   * @see #parseDescribeLog(ChangeListsAndIssues, InputStream)
   */
  private Change parseChangeLine(final String line) {
    final int lastSpaceIndex = line.lastIndexOf(' ');
    final int lastPoundIndex = line.lastIndexOf('#');
    final String filePath = line.substring(7, lastPoundIndex);
    final Change change = new Change();
    change.setFilePath(filePath);
    change.setChangeType(changeTypeToCode(line.substring(lastSpaceIndex + 1)));
    change.setRevision(line.substring(lastPoundIndex + 1, lastSpaceIndex));
    return change;
  }


  /**
   * Converts String change type to short code
   *
   * @param type String change type code
   *
   * @return short type code
   */
  private static byte changeTypeToCode(final String type) {
    if ("edit".equals(type)) return Change.TYPE_MODIFIED;
    if ("add".equals(type)) return Change.TYPE_ADDED;
    if ("delete".equals(type)) return Change.TYPE_DELETED;
    if ("integrate".equals(type)) return Change.TYPE_INTEGRATED;
    if ("branch".equals(type)) return Change.TYPE_BRANCHED;
    return Change.TYPE_UNKNOWN;
  }


  /**
   * @param firstLineToParse
   *
   * @throws BuildException
   */
  private static void validateChangeListIsNotEmpty(final String firstLineToParse) throws BuildException {
    if (firstLineToParse == null)
      throw new BuildException(P4ParserHelper.makeUnexpectedErrorMessage("Cannot process empty change list."));
  }


  public static SimpleDateFormat getP4DescribeDateFormatter() {
    return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
  }


  public static SimpleDateFormat getP4JobDateFormatter() {
    return new SimpleDateFormat("yyyy/MM/dd", Locale.US);
  }


  private static class P4AccumulatingChangeDriver implements P4ChangeDriver {

    private final ChangeListsAndIssues changeListsAndIssuesAccumulator;
    private Set changes;
    private Set issues;


    public P4AccumulatingChangeDriver(final ChangeListsAndIssues cccumulator) {
      this.changeListsAndIssuesAccumulator = cccumulator;
      this.changes = new HashSet(11);
      this.issues = new HashSet(11);
    }


    /**
     * Returns true if the change list number should be
     * processed.
     *
     * @param changeListNumber to check.
     *
     * @return always returns true because the parser gets them all.
     */
    public boolean acceptsNumber(final String changeListNumber) {
      return true;
    }


    /**
     * This method is called when all changes and issues
     * are collected and the processing for change list is
     * finished.
     *
     * @param changelist
     */
    public void processChangeList(final ChangeList changelist) {
      changelist.setChanges(changes);
      changeListsAndIssuesAccumulator.addChangelist(changelist);
      for (final Iterator i = issues.iterator(); i.hasNext();) {
        changeListsAndIssuesAccumulator.addBinding(new ChangeListIssueBinding(changelist, (Issue)i.next()));
      }
      changes = new HashSet(11);
      issues = new HashSet(11);
    }


    /**
     * This method is called when an issue is found.
     */
    public void processIssue(final Issue issue) {
      issues.add(issue);
    }


    /**
     * This method is called when a change is found.
     */
    public void processChange(final Change change) {
      changes.add(change);
    }


    public String toString() {
      return "P4AccumulatingChangeDriver{" +
        "changeListsAndIssuesAccumulator=" + changeListsAndIssuesAccumulator +
        ", changes=" + changes +
        ", issues=" + issues +
        '}';
    }
  }


  public String toString() {
    return "P4ChangeLogParser{" +
      "jobsCollectionEnabled=" + jobsCollectionEnabled +
      ", maxChangeListSize=" + maxChangeListSize +
      '}';
  }
}
