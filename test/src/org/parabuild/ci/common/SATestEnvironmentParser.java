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
package org.parabuild.ci.common;

import java.io.*;
import java.util.*;

import junit.framework.*;

public final class SATestEnvironmentParser extends TestCase {

  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  private static final String TEST_NAME_1 = "TEST_VARIABLE_1";
  private static final String TEST_NAME_2 = "TEST_VARIABLE_2";
  private static final String TEST_NAME_3 = "TEST_VARIABLE_3";
  private static final String TEST_VALUE_2 = "TTTTVVVV";
  private static final String TEST_VALUE_3 = "VVVVVVVVVVVTTTTTTTT";
  private static final String TEST_VALUE_WITH_BREAK = "AAA" + LINE_SEPARATOR + "BBB" + LINE_SEPARATOR + LINE_SEPARATOR + "CCC";
  private static final String TEST_ENVIRONMENT = TEST_NAME_2 + '=' + TEST_VALUE_2
    + LINE_SEPARATOR + TEST_NAME_1 + '=' + TEST_VALUE_WITH_BREAK
    + LINE_SEPARATOR + TEST_NAME_3 + '=' + TEST_VALUE_3;


  public void test_bug963_parseEnvironment() throws IOException {
    final EnvironmentParser ep = new EnvironmentParser();
    final Map map = ep.parseEnvironment(TEST_ENVIRONMENT);
    assertEquals(TEST_VALUE_WITH_BREAK, map.get(TEST_NAME_1));
    assertEquals(TEST_VALUE_2, map.get(TEST_NAME_2));
    assertEquals(TEST_VALUE_3, map.get(TEST_NAME_3));
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestEnvironmentParser.class);
  }


  public SATestEnvironmentParser(final String s) {
    super(s);
  }
}
