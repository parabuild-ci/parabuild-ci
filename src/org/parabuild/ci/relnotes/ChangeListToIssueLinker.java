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
package org.parabuild.ci.relnotes;

import java.util.*;
import java.util.regex.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;

/**
 * This class is responsible for linking issues that are already
 * a part of build run release notes, to build run participants
 * (change lists).
 * <p/>
 * {@link ChangeListToIssueLinker#process(BuildRun)} should be
 * called after all other release notes are called.
 */
final class ChangeListToIssueLinker implements ReleaseNotesHandler {

  private final List linkingRegexList = new ArrayList(11);


  /**
   * Creates ChangeListToIssueLinker using given linking
   * patterns.
   *
   * @param linkingPatternList List of String regex patterns each
   * separated by line breaks.
   */
  public ChangeListToIssueLinker(final List linkingPatternList) {
    // create a set of untiq patterns
    final Set stringPatternSet = new HashSet(11);
    for (final Iterator i = linkingPatternList.iterator(); i.hasNext();) {
      stringPatternSet.addAll(StringUtils.multilineStringToList((String)i.next()));
    }
    // compile patterns and store to list for future use in #process
    for (final Iterator i = stringPatternSet.iterator(); i.hasNext();) {
      final Pattern pattern = Pattern.compile((String)i.next(), Pattern.CASE_INSENSITIVE);
      linkingRegexList.add(pattern);
    }
  }


  /**
   * Links issues that are already a part of build run release
   * notes, to build run participants (change lists) acording to
   * regex linking rules. Linking rules in form of regex patterns
   * are stored in the {@link IssueTrackerProperty#ISSUE_LINK_PATTERN}
   * and configured through the web UI.
   * <p/>
   * Every change list description is processed and issue IDs are
   * extracted using capturing groups, if any. Found issue IDs
   * are looked up in the release notes. If found, an
   * issue-change list link is created and saved.
   *
   * @return always returns 0
   */
  public int process(final BuildRun buildRun) {
    if (linkingRegexList.isEmpty()) return 0;
    try {
      // traverse change lists
      // REVIEWME: consider getting only change list number and description.
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final List buildRunParticipants = cm.getBuildRunParticipants(buildRun);
      for (final Iterator i = buildRunParticipants.iterator(); i.hasNext();) {
        final ChangeList changeList = (ChangeList)i.next();
        if (StringUtils.isBlank(changeList.getDescription())) continue;
        final Set issueKeysToProcess = extractIssueKeysToProcess(changeList.getDescription());
        // process matchedIssueIDs
        for (final Iterator j = issueKeysToProcess.iterator(); j.hasNext();) {
          final String issueKey = (String)j.next();
          final List issueIDs = cm.findIssueIDByKey(buildRun.getActiveBuildID(), issueKey);
          // raverse found issue IDs
          for (final Iterator k = issueIDs.iterator(); k.hasNext();) {
            final Integer issueID = (Integer)k.next();
            // look up in the link first
            if (!cm.issueChangeListExists(changeList.getChangeListID(), issueID)) {
              final IssueChangeList issueChangeList = new IssueChangeList(issueID, changeList.getChangeListID());
              cm.saveObject(issueChangeList);
            }
          }
        }
      }
    } catch (final Exception e) {
      final Error error = new Error(buildRun.getActiveBuildID(), "", Error.ERROR_SUSBSYSTEM_INTEGRATION, e);
      error.setSendEmail(false);
      error.setDescription("Error while processing processing change list to issue links: " + StringUtils.toString(e));
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
    }
    // traverse found issue IDs and try to find them in the DB
    return 0;
  }


  /**
   * Helper method to extract issue key from change list
   * description.
   *
   * @param changeListDescription
   */
  private Set extractIssueKeysToProcess(final String changeListDescription) {
    final Set issueKeysToProcess = new HashSet(5);
    for (final Iterator j = linkingRegexList.iterator(); j.hasNext();) {
      final Matcher matcher = ((Pattern)j.next()).matcher(changeListDescription);
      // find issue IDs
      while (matcher.find()) {
        final int groupCount = matcher.groupCount();
        for (int k = 1; k <= groupCount; k++) {
          // add to set of key to look up in the DB
          issueKeysToProcess.add(matcher.group(k));
        }
      }
    }
    return issueKeysToProcess;
  }


  public String toString() {
    return "ChangeListToIssueLinker{" +
      "linkingRegexList=" + linkingRegexList +
      '}';
  }
}
