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

import junit.framework.*;

import com.sun.syndication.feed.synd.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.security.*;

/**
 * Tests FeedGenerator
 */
public class SSTestFeedGenerator extends ServersideTestCase {

  private FeedGenerator feedGenerator = null;


  public SSTestFeedGenerator(final String s) {
    super(s);
  }


  /**
   */
  public void test_getAllBuildsFeed() throws Exception {
    final SyndFeed allBuildsFeed = feedGenerator.getAllBuildsFeed(-1);
    assertNotNull("Feed should not be null", allBuildsFeed);
    assertEquals(2, allBuildsFeed.getEntries().size());
  }


  /**
   */
  public void test_getBuildFeed() throws Exception {
    final SyndFeed allBuildsFeed = feedGenerator.getBuildFeed(-1, 1);
    assertNotNull("Feed should not be null", allBuildsFeed);
    assertEquals(allBuildsFeed.getEntries().size(), 2);
  }


  /**
   */
  public void test_getBuildFeedFailsOnNonExistingBuild() throws AccessForbiddenException {
    try {
      feedGenerator.getBuildFeed(-1, 999999999);
      TestHelper.failNoExceptionThrown();
    } catch (FeedNotFoundException e) {
      // expected
    }
  }


  /**
   */
  public void test_getBuildFeedFailsOnProhibitedBuild() throws FeedNotFoundException {
    try {
      feedGenerator.getBuildFeed(-1, 22);
      TestHelper.failNoExceptionThrown();
    } catch (AccessForbiddenException e) {
      // expected
    }
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();
    feedGenerator = new FeedGenerator();
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestFeedGenerator.class);
  }
}
