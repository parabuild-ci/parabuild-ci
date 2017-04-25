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
package org.parabuild.ci.versioncontrol.accurev;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.BuildException;

/**
 *
 */
public class SATestAccurevWorkspaceNameGenerator extends TestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SATestAccurevWorkspaceNameGenerator.class);

  public static final String TEST_USER = "test_user";
  public static final String TEST_TEMPLATE1 = "${accurev.user}-${build.id}";
  private static final String TEST_TEMPLATE2 = "${accurev.user}-${build.id}-${builder.host}";
  private static final String TEST_HOST_NAME_WITH_DOTS = "test.host.name.with.dots";
  private static final String TEST_HOST = "test_host";
  public static final int TEST_BUILD_ID = 999;
  private AccurevObjectNameGenerator generator;


  public SATestAccurevWorkspaceNameGenerator(final String s) {
    super(s);
  }


  public void test_generateLabelName() throws Exception {
    assertTrue(generator.isTemplateValid(TEST_TEMPLATE1));
    assertEquals(generator.generate(TEST_BUILD_ID, TEST_HOST, TEST_USER, TEST_TEMPLATE1, "parabuild_on_${builder.host}_${build.id}"), TEST_USER + '-' + TEST_BUILD_ID);
  }


  public void test_isTemplateValid() throws Exception {
    assertTrue(!generator.isTemplateValid("${aaaa.user}"));
    try {
      assertEquals(generator.generate(TEST_BUILD_ID, TEST_HOST, TEST_USER, "${p4444.user}", "parabuild_on_${builder.host}_${build.id}"), TEST_USER);
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
    }
  }


  public void test_bug_1106_generateHandlesHostNameWithDots() throws Exception {
    final String workspaceName = generator.generate(TEST_BUILD_ID, TEST_HOST_NAME_WITH_DOTS, TEST_USER, TEST_TEMPLATE2, "parabuild_on_${builder.host}_${build.id}");
    assertEquals("test_user-999-test_host_name_with_dots", workspaceName);
  }


  public void test_breaksOnEmptyP4User() {
    try {
      generator.generate(SATestAccurevWorkspaceNameGenerator.TEST_BUILD_ID, TEST_HOST, "", TEST_TEMPLATE1, "parabuild_on_${builder.host}_${build.id}");
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  public void test_breaksOnEmptyBuildID() {
    try {
      generator.generate(-1, TEST_HOST, TEST_USER, TEST_TEMPLATE1, "parabuild_on_${builder.host}_${build.id}");
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  public void test_breaksOnEmptyTemplate() {
    try {
      generator.generate(TEST_BUILD_ID, TEST_HOST, TEST_USER, "", "parabuild_on_${builder.host}_${build.id}");
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestAccurevWorkspaceNameGenerator.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    generator = new AccurevObjectNameGenerator();
  }
}
