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

import java.util.regex.*;
import org.apache.commons.logging.*;

import junit.framework.*;

/**
 * Tests concept of using regex to identify bug IDs in change
 * list descriptions.
 */
public class SATestUsingRegexToLinkChangeListsAndBugsConcept extends TestCase {

  private static final Log log = LogFactory.getLog(SATestUsingRegexToLinkChangeListsAndBugsConcept.class);


  public SATestUsingRegexToLinkChangeListsAndBugsConcept(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_parseSimple() throws Exception {

    final Pattern pattern = Pattern.compile("Fixed #([0-9]+)", Pattern.CASE_INSENSITIVE);
    final Matcher matcher = pattern.matcher("I have just Fixed #546, and Fixed #547 also");

    // #1
    assertTrue(matcher.find());
    if (log.isDebugEnabled()) log.debug("groupCount() = " + matcher.groupCount());
    assertEquals(1, matcher.groupCount());
    assertEquals("546", matcher.group(1));

    // #2
    assertTrue(matcher.find());
    if (log.isDebugEnabled()) log.debug("groupCount() = " + matcher.groupCount());
    assertEquals(1, matcher.groupCount());
    assertEquals("547", matcher.group(1));

    // no more left
    assertTrue(!matcher.find());
  }


  /**
   *
   */
  public void test_parseComplex() throws Exception {

    final Pattern pattern = Pattern.compile("(?:Fixed|Implemented|On)\\s*#([0-9]+)", Pattern.CASE_INSENSITIVE);
    final Matcher matcher = pattern.matcher("I have just Fixed #546, Started working on #547 and implemented#548 also");

    // #1
    assertTrue(matcher.find());
    assertEquals(1, matcher.groupCount());
    assertEquals("546", matcher.group(1));

    // #2
    assertTrue(matcher.find());
    assertEquals(1, matcher.groupCount());
    assertEquals("547", matcher.group(1));

    // #3
    assertTrue(matcher.find());
    assertEquals(1, matcher.groupCount());
    assertEquals("548", matcher.group(1));

    // no more left
    assertTrue(!matcher.find());
  }


  /**
   *
   */
  public void test_parseWhiteSpace() throws Exception {
    final Pattern pattern = Pattern.compile("Bugzilla[a-zA-Z\\s]*#([0-9]+)", Pattern.CASE_INSENSITIVE);
    final Matcher matcher = pattern.matcher("I have just Bugzilla #546 \nand Bugzilla bug #547");
    assertTrue(matcher.find());
    assertEquals(1, matcher.groupCount());
    assertEquals("546", matcher.group(1));
    assertTrue(matcher.find());
    assertEquals(1, matcher.groupCount());
    assertEquals("547", matcher.group(1));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestUsingRegexToLinkChangeListsAndBugsConcept.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
