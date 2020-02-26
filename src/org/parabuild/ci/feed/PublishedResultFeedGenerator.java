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
package org.parabuild.ci.feed;

import java.util.*;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.util.BuildStatusURLGenerator;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.configuration.VerbialBuildResult;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.security.AccessForbiddenException;
import org.parabuild.ci.security.SecurityManager;

/**
 * This class is responsible for generating feeds for published results.
 */
final class PublishedResultFeedGenerator {

  private static final int MAX_BUILD_RUNS = 25;


  /**
   * Returns a feed for all builds that are allowed for a given user.
   *
   * @param userID for that to return feeds.
   *
   * @return a feed for all builds that are allowed for the given user.
   */
  public SyndFeed getAllBuildsFeed(final int userID) {

    final List buildStatusList = org.parabuild.ci.security.SecurityManager.getInstance().getFeedBuildStatuses(userID);

    // traverse build statuses
    final List entries = new ArrayList(11);
    final BuildStatusURLGenerator urlGenerator = new BuildStatusURLGenerator();
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    for (final Iterator i = buildStatusList.iterator(); i.hasNext();) {
      final int activeBuildID = ((BuildState)i.next()).getActiveBuildID();
      entries.addAll(getBuildEntries(cm, activeBuildID, urlGenerator));
    }

    // create feed and set collected entries
    final SyndFeed feed = new SyndFeedImpl();
    feed.setTitle("Parabuild Published Result Feed");
    feed.setLink(urlGenerator.makeBuildListURL());
    feed.setDescription("This feed provides information about published results at Parabuild server");
    feed.setEntries(entries);

    // return result
    return feed;
  }


  /**
   * Returns a feed for all builds that are allowed for a given user.
   *
   * @param userID for that to return feeds.
   *
   * @return a feed for all builds that are allowed for the given user.
   */
  public SyndFeed getBuildFeed(final int userID, final int activeBuildID) throws AccessForbiddenException, FeedNotFoundException {

    // validate that a build exists
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final ActiveBuildConfig activeBuildConfig = cm.getActiveBuildConfig(activeBuildID);
    if (activeBuildConfig == null) {
      throw new FeedNotFoundException("Requested feed not found");
    }

    // validat build access
    if (!SystemConfigurationManagerFactory.getManager().isAnonymousAccessToProtectedFeedsIsEnabled()) {
      final SecurityManager securityManager = SecurityManager.getInstance();
      if (!securityManager.userCanViewBuild(userID, activeBuildID)) {
        throw new AccessForbiddenException("Access to this feed is forbidden");
      }
    }

    final BuildStatusURLGenerator urlGenerator = new BuildStatusURLGenerator();
    final String buildName = activeBuildConfig.getBuildName();
    final SyndFeed feed = new SyndFeedImpl();
    feed.setTitle("Parabuild Feed for " + buildName);
    feed.setLink(urlGenerator.makeBuildStatusURL(activeBuildID));
    feed.setDescription("This feed provides information about statuses for " + buildName);
    feed.setEntries(getBuildEntries(cm, activeBuildID, urlGenerator));

    // return result
    return feed;
  }


  private static List getBuildEntries(final ConfigurationManager cm, final int activeBuildID, final BuildStatusURLGenerator urlGenerator) {
    final List result = new ArrayList(11);

    // traverse cmplete build runs for the given build
    final List completedBuildRuns = cm.getCompletedBuildRuns(activeBuildID, 0, PublishedResultFeedGenerator.MAX_BUILD_RUNS);
    for (final Iterator i = completedBuildRuns.iterator(); i.hasNext();) {
      final BuildRun buildRun = (BuildRun)i.next();

      // compose description
      final String resultDescr = buildRun.getResultID() == BuildRun.BUILD_RESULT_BROKEN ? ": " + buildRun.getResultDescription() : "";
      final StringBuilder subj = new StringBuilder(200);
      subj.append(buildRun.getBuildName()).append(" # ").append(buildRun.getBuildRunNumberAsString());
      subj.append(' ').append(new VerbialBuildResult().getVerbialResultString(buildRun));
      subj.append(resultDescr);

      // compose entry
      final SyndEntry entry = new SyndEntryImpl();
      entry.setTitle(subj.toString());
      entry.setPublishedDate(buildRun.getFinishedAt());
      entry.setLink(urlGenerator.makeBuildRunResultURL(buildRun));
      final SyndContent description = new SyndContentImpl();
      description.setType("text/plain");
      //description.setType("text/html");
      description.setValue(subj.toString());
      entry.setDescription(description);

      // edd entry to the result
      result.add(entry);
    }
    return result;
  }
}
