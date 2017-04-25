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
package org.parabuild.ci.tray;

import java.util.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;

/**
 * Tests ServerListParser
 */
public class SATestServerListParser extends TestCase {


  private ServerListParser parser;


  /**
   */
  public void test_parse() throws Exception {
    final List list = parser.parse("localhost:8080, otherhost:9090");
    assertEquals("localhost:8080", list.get(0));
    assertEquals("otherhost:9090", list.get(1));
    assertEquals(0, parser.parse("").size());
    assertEquals(0, parser.parse(null).size());
  }


  protected void setUp() throws Exception {
    super.setUp();
    parser = new ServerListParser();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestServerListParser.class, new String[]{
    });
  }


  public SATestServerListParser(final String s) {
    super(s);
  }
}
