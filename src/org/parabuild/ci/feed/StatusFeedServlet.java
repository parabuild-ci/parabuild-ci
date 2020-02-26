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

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.object.User;
import org.parabuild.ci.security.AccessForbiddenException;
import org.parabuild.ci.webui.common.Pages;

/**
 * This servlet is responsible for presenting syndicated build
 * statuses in various formats (RSS, ATOM, e.t.c.).
 *
 * The following feeds are supported - system-wide feed that
 * contains a superposition of all build feeds accessible to
 * a anonimous clients, and per-build feeds.
 */
public final class StatusFeedServlet extends HttpServlet {

  private static final int INITIAL_TYPE_MAP_CAPACITY = 4;

  private static final String FEED_TYPE = "type";
  private static final String MIME_TYPE = "application/xml; charset=UTF-8";
  private static final String DEFAULT_FEED_TYPE = "rss_2.0";
  private static final Map VALID_FEED_TYPES = createValidFeedTypes();
  private static final long serialVersionUID = -6042171890401737448L;


  public void doGet(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
    try {
      final SyndFeed feed = getBuildStatusFeed(req);
      res.setContentType(MIME_TYPE);
      final SyndFeedOutput output = new SyndFeedOutput();
      output.output(feed, res.getWriter());
    } catch (final FeedException e) {
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, StringUtils.toString(e));
    } catch (final AccessForbiddenException e) {
      res.sendError(HttpServletResponse.SC_FORBIDDEN, StringUtils.toString(e));
    } catch (final FeedNotFoundException e) {
      res.sendError(HttpServletResponse.SC_NOT_FOUND, StringUtils.toString(e));
    }
  }


  private SyndFeed getBuildStatusFeed(final HttpServletRequest req) throws AccessForbiddenException, FeedNotFoundException {
    final SyndFeed feed;
    final int userID = User.UNSAVED_ID;
    final String stringBuildID = req.getParameter(Pages.PARAM_BUILD_ID);
    if (StringUtils.isBlank(stringBuildID)) {
      // no build ID
      final FeedGenerator feedGenerator = new FeedGenerator();
      feed = feedGenerator.getAllBuildsFeed(userID);
    } else if (StringUtils.isValidInteger(stringBuildID)) {
      // build ID is defined
      final FeedGenerator feedGenerator = new FeedGenerator();
      feed = feedGenerator.getBuildFeed(userID, Integer.parseInt(stringBuildID));
    } else {
      // invalid build ID
      throw new FeedNotFoundException("Requested feed could not be found");
    }
    feed.setFeedType(getFeedType(req));
    return feed;
  }


  private static String getFeedType(final HttpServletRequest req) {
    final String feedType = req.getParameter(FEED_TYPE);
    if (StringUtils.isBlank(feedType)) {
      return DEFAULT_FEED_TYPE;
    }
    final String trimmedFeedType = feedType.trim();
    if (VALID_FEED_TYPES.containsKey(trimmedFeedType)) {
      return trimmedFeedType;
    }
    return DEFAULT_FEED_TYPE;
  }


  /**
   * Helper to create unmodifiable Map with valid String feed types as keys.
   *
   * @return new unmodifiable Map with valid String feed types as keys.
   */
  private static Map createValidFeedTypes() {
    final String[] validTypeArray = {"atom_0.3", "atom_1.0", "rss_1.0", "rss_2.0"};
    final Map result = new HashMap(INITIAL_TYPE_MAP_CAPACITY);
    for (int i = 0; i < validTypeArray.length; i++) {
      result.put(validTypeArray[i], Boolean.TRUE);
    }
    return Collections.unmodifiableMap(result);
  }


  public String toString() {
    return "StatusFeedServlet{}";
  }
}
