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

import org.apache.commons.logging.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;


/**
 * Tests TimeWindowChangeListAccumulator
 */
public final class SATestTimeWindowChangeListAccumulator extends TestCase {

  private static final Log log = LogFactory.getLog(SATestTimeWindowChangeListAccumulator.class);


  public void test_key() throws Exception {
//
//
//    long w = 20;
//    long v1 = 100;
//    long v2 = 110;
//    long v3 = 119;
//
//    if (log.isDebugEnabled()) log.debug("v2: " + (v2 % w));
//    if (log.isDebugEnabled()) log.debug("v3: " + (v3 % w));
//    if (log.isDebugEnabled()) log.debug("");
//
//
//    final long timeWindowMillis = 60L * 1000L;
//    final String stringMessage = "test_message";
//    final String user = "test_user";
//
//    // make first key
//    final Date createdAt1 = new Date();
//    TimeWindowChangeListAccumulator.ChangeListKey key1 =
//      new TimeWindowChangeListAccumulator.ChangeListKey(timeWindowMillis, createdAt1, stringMessage, user);
//
//    // make second key
//    Calendar c = Calendar.getInstance();
//    c.setTime(createdAt1);
//    c.add(Calendar.SECOND, 30);
//    final Date createdAt2 = c.getTime();
//    if (log.isDebugEnabled()) log.debug("test difference: " + (createdAt2.getTime() - createdAt1.getTime()));
//    TimeWindowChangeListAccumulator.ChangeListKey key2 =
//      new TimeWindowChangeListAccumulator.ChangeListKey(timeWindowMillis, createdAt2, stringMessage, user);
//
//    // assert
//    assertEquals(key1, key2);
//    assertEquals(key1.hashCode(), key2.hashCode());
  }


  public SATestTimeWindowChangeListAccumulator(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestTimeWindowChangeListAccumulator.class, new String[]{
    });
  }
}
