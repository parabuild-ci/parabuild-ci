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

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.util.*;

/**
 *
 */
public class SATestClearCaseStorageNameGenerator extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestClearCaseStorageNameGenerator.class);

  public static final String TEST_TEMPLATE = "\\\\Path\\To\\Storage\\parabuild_${build.id}";
  public static final int TEST_BUILD_ID = 999;


  public SATestClearCaseStorageNameGenerator(final String s) {
    super(s);
  }


  public void test_generateLabelName() throws Exception {
    final ClearCaseStorageNameGenerator nameGenerator = new ClearCaseStorageNameGenerator(TEST_BUILD_ID, TEST_TEMPLATE);
    assertTrue(nameGenerator.isTemplateValid());
    assertEquals(nameGenerator.generate(), "\\\\Path\\To\\Storage\\parabuild_" + TEST_BUILD_ID);
  }


  public void test_isTemplateValid() throws Exception {
    final ClearCaseStorageNameGenerator nameGenerator = new ClearCaseStorageNameGenerator(TEST_BUILD_ID, "${cc4444.user}");
    assertTrue(!nameGenerator.isTemplateValid());
    try {
      nameGenerator.generate();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
    }
  }


  public void test_DoesntBreakOnNoParamters() throws Exception {
    final String template = "/some/strange/path";
    final ClearCaseStorageNameGenerator nameGenerator = new ClearCaseStorageNameGenerator(TEST_BUILD_ID, template);
    assertTrue(nameGenerator.isTemplateValid());
    assertEquals(template, nameGenerator.generate());
  }


  public void test_breaksOnEmptyBuildID() {
    try {
      new ClearCaseStorageNameGenerator(-1, TEST_TEMPLATE);
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  public void test_breaksOnEmptyTemplate() {
    try {
      new ClearCaseStorageNameGenerator(TEST_BUILD_ID, "");
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestClearCaseStorageNameGenerator.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
