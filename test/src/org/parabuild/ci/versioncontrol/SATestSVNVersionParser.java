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
 * Tests SVNChangeLogParser
 */
public class SATestSVNVersionParser extends TestCase {

  private static final Log log = LogFactory.getLog(SATestSVNVersionParser.class);

  private SVNVersionParser paser = null;


  public void test_parse() {

    assertNull(paser.parse(null));
    assertNull(paser.parse(""));

    final SVNVersion svnVersion = paser.parse("1.2.3");
    assertEquals(1, svnVersion.getMajor());
    assertEquals(2, svnVersion.getMinor());
  }


  public SATestSVNVersionParser(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    paser = new SVNVersionParser();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestSVNVersionParser.class, new String[]{
    });
  }
}
