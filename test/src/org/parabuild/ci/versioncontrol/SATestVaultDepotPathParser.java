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

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Tests VaultDepotPathParser
 */
public final class SATestVaultDepotPathParser extends TestCase {

  /**
   * @noinspection UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SATestVaultDepotPathParser.class); // NOPMD
  private static final String TEST_PATH = "$/test/sourceline/alwaysvalid";

  private VaultDepotPathParser parser;


  /**
   * @noinspection JUnitTestMethodWithNoAssertions,HardcodedLineSeparator
   */
  public void testValidate() throws Exception {
    this.parser.validate("$/");
    this.parser.validate("$/test1\n$/test2");
    parser.validate(TEST_PATH);
  }


  public void testParseDepotPath() throws Exception {
    assertEquals(TEST_PATH, ((RepositoryPath) parser.parseDepotPath(TEST_PATH).get(0)).getPath());
  }


  protected void setUp() throws Exception {
    super.setUp();
    parser = new VaultDepotPathParser();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestVaultDepotPathParser.class, new String[]{
            "testValidate"
    });
  }


  public SATestVaultDepotPathParser(final String s) {
    super(s);
  }


  public String toString() {
    return "SATestVaultDepotPathParser{" +
            "parser=" + parser +
            "} " + super.toString();
  }
}
