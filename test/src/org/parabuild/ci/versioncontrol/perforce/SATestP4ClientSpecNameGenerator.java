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
package org.parabuild.ci.versioncontrol.perforce;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.BuildException;

/**
 *
 */
public class SATestP4ClientSpecNameGenerator extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestP4ClientSpecNameGenerator.class);

  public static final String TEST_P_4_USER = "test_p4_user";
  public static final String TEST_TEMPLATE1 = "${p4.user}-${build.id}";
  private static final String TEST_TEMPLATE2 = "${p4.user}-${build.id}-${builder.host}";
  private static final String TEST_HOST_NAME_WITH_DOTS = "test.host.name.with.dots";
  private static final String TEST_HOST = "test_host";
  public static final int TEST_BUILD_ID = 999;
  private P4ClientNameGeneratorImpl nameGenerator;


  public SATestP4ClientSpecNameGenerator(final String s) {
    super(s);
  }


  public void test_generateLabelName() throws Exception {
    assertTrue(nameGenerator.isTemplateValid(TEST_TEMPLATE1));
    assertEquals(nameGenerator.generate(TEST_BUILD_ID, TEST_HOST, TEST_P_4_USER, TEST_TEMPLATE1), TEST_P_4_USER + '-' + TEST_BUILD_ID);
  }


  public void test_isTemplateValid() throws Exception {
    assertTrue(!nameGenerator.isTemplateValid("${p4444.user}"));
    try {
      assertEquals(nameGenerator.generate(TEST_BUILD_ID, TEST_HOST, TEST_P_4_USER, "${p4444.user}"), TEST_P_4_USER);
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
    }
  }


  public void test_bug_1106_generateHandlesHostNameWithDots() throws Exception {
    assertEquals("test_p4_user-999-test_host_name_with_dots", nameGenerator.generate(TEST_BUILD_ID, TEST_HOST_NAME_WITH_DOTS, TEST_P_4_USER, TEST_TEMPLATE2));
  }


  public void test_breaksOnEmptyP4User() {
    try {
      nameGenerator.generate(TEST_BUILD_ID, TEST_HOST, "", TEST_TEMPLATE1);
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  public void test_breaksOnEmptyBuildID() {
    try {
      nameGenerator.generate(-1, TEST_HOST, TEST_P_4_USER, TEST_TEMPLATE1);
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  public void test_breaksOnEmptyTemplate() {
    try {
      nameGenerator.generate(TEST_BUILD_ID, TEST_HOST, TEST_P_4_USER, "");
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestP4ClientSpecNameGenerator.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    nameGenerator = new P4ClientNameGeneratorImpl();
  }
}
