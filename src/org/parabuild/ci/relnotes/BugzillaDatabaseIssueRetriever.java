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

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.Issue;
import org.parabuild.ci.object.IssueTracker;
import org.parabuild.ci.object.IssueTrackerProperty;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.IssueURLGenerator;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Responsible for getting issues from bugzilla db acording to
 * tracker configuration.
 */
public final class BugzillaDatabaseIssueRetriever implements DatabaseIssueRetriever {

  public static final String BUGZILLA_MYSQL_HOST = IssueTrackerProperty.BUGZILLA_MYSQL_HOST;
  public static final String BUGZILLA_MYSQL_PORT = IssueTrackerProperty.BUGZILLA_MYSQL_PORT;
  public static final String BUGZILLA_MYSQL_DB = IssueTrackerProperty.BUGZILLA_MYSQL_DB;
  public static final String BUGZILLA_MYSQL_USER = IssueTrackerProperty.BUGZILLA_MYSQL_USER;
  public static final String BUGZILLA_MYSQL_PASSWORD = IssueTrackerProperty.BUGZILLA_MYSQL_PASSWORD;

  private final BugzillaDatabaseConnector bugzillaDatabaseConnector;
  private final Map props;


  public BugzillaDatabaseIssueRetriever(final IssueTracker tracker) throws SQLException {
    this.props = ConfigurationManager.getInstance().getIssueTrackerPropertiesAsMap(tracker.getID());
    this.bugzillaDatabaseConnector = BugzillaDatabaseConnectorFactory.makeInstance(
      getIssueTrackerPropertyValue(BUGZILLA_MYSQL_HOST),
      Integer.parseInt(getIssueTrackerPropertyValue(BUGZILLA_MYSQL_PORT)),
      getIssueTrackerPropertyValue(BUGZILLA_MYSQL_DB),
      getIssueTrackerPropertyValue(BUGZILLA_MYSQL_USER),
      org.parabuild.ci.security.SecurityManager.decryptPassword(getIssueTrackerPropertyValue(BUGZILLA_MYSQL_PASSWORD)));
  }


  /**
   * Retrieves issues from BZ database
   *
   * @param fromDate starting date, inclusive.
   * @param toDate enfing date, inclusive
   *
   * @return List of Issues
   *
   * @throws BuildException if errors happen.
   */
  public List retrieveBugs(final Date fromDate, final Date toDate) throws BuildException {

    // get BZ bugz
    final Collection bugs = bugzillaDatabaseConnector.requestBugsFromBugzilla
      (getIssueTrackerPropertyValue(IssueTrackerProperty.BUGZILLA_PRODUCT),
        getIssueTrackerPropertyValue(IssueTrackerProperty.BUGZILLA_VERSION),
        fromDate, toDate);

    // create description filter
    final String issueFilterString = getIssueTrackerPropertyValue(IssueTrackerProperty.ISSUE_FILTER);
    final IssueDescriptionFilter descriptionFilter = new IssueDescriptionFilter(issueFilterString);

    // create url generator
    final String urlTemplate = getIssueTrackerPropertyValue(IssueTrackerProperty.ISSUE_URL_TEMPLATE);
    final IssueURLGenerator issueURLGenerator = new IssueURLGenerator(urlTemplate);

    // convert to issues
    final List result = new ArrayList(bugs.size());
    for (final Iterator i = bugs.iterator(); i.hasNext();) {
      final BugzillaBug bzBug = (BugzillaBug)i.next();
      // filter description
      if (descriptionFilter.filter(bzBug.getShortDescr()) == null) continue;
      // make URL
      issueURLGenerator.setIssueKey(bzBug.getBugIDAsString());
      final String url = issueURLGenerator.generateIssueURL();
      // store issue
      final Issue issue = new Issue();
      issue.setTrackerType(Issue.TYPE_BUGZILLA);
      issue.setKey(bzBug.getBugIDAsString());
      issue.setDescription(bzBug.getShortDescr());
      issue.setProduct(bzBug.getProduct());
      issue.setVersion(bzBug.getVersion());
      issue.setClosed(bzBug.getDate());
      issue.setStatus(bzBug.getStatus());
      issue.setReceived(new Date(System.currentTimeMillis()));
      issue.setUrl(url);
      result.add(issue);
    }

    // return result
    return result;
  }


  private String getIssueTrackerPropertyValue(final String propName) {
    final IssueTrackerProperty trackerProperty = (IssueTrackerProperty)props.get(propName);
    if (trackerProperty == null) return null;
    return trackerProperty.getValue();
  }


  public String toString() {
    return "BugzillaDatabaseIssueRetriever{" +
      "bugzillaDatabaseConnector=" + bugzillaDatabaseConnector +
      ", props=" + props +
      '}';
  }
}
