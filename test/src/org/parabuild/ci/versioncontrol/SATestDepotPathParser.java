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

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.util.*;

/**
 * Tests DepotPathParser
 */
public final class SATestDepotPathParser extends TestCase {

  private static final String TEST_DEPOT_PATH = "test depot parser";


  public void test_acceptSpaces() throws ValidationException {
    new DepotPathParser("Depot", true).validate(TEST_DEPOT_PATH);
  }


  public void test_removesDeoubleQuotes() throws ValidationException {
    new DepotPathParser("Depot", true).parseDepotPath('\"' + TEST_DEPOT_PATH + '\"').get(0).equals(TEST_DEPOT_PATH);
  }


  public void test_doesNotAcceptSpaces() {
    try {
      new DepotPathParser("Depot", false).validate(TEST_DEPOT_PATH);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      // expected
    }
  }


  public void test_doesNotAcceptSubpaths() {
    try {
      new DepotPathParser("Depot", false).validate("test/path\ntest/path/subpath");
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      // expected
    }
// NOTE: simeshev@parabuildci.org ->
//    try {
//      new DepotPathParser("Depot", false).validate("test/path\n/");
//      TestHelper.failNoExceptionThrown();
//    } catch (ValidationException e) {
//      // expected
//    }
  }


  public void test_bug1205_acceptsSimilarSubpaths() throws ValidationException {
    new DepotPathParser("Depot", false).validate("test/path\ntest/pat");
    new DepotPathParser("Depot", false).validate("test/path/\ntest/pat/");
  }


  public void test_defaultConstructorDoesNotAcceptSpaces() {
    try {
      new DepotPathParser("Depot").validate(TEST_DEPOT_PATH);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      // expected
    }
  }


  public SATestDepotPathParser(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestDepotPathParser.class, new String[]{
    });
  }
}
