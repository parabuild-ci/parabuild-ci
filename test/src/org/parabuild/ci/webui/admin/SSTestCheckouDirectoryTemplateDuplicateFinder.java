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
package org.parabuild.ci.webui.admin;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.ValidationException;

/**
 */
public class SSTestCheckouDirectoryTemplateDuplicateFinder extends ServersideTestCase {


  public SSTestCheckouDirectoryTemplateDuplicateFinder(final String s) {
    super(s);
  }


  public void test_doesNotFindNeverExisted() throws BuildException, ValidationException {
    // never existed template
    final CheckoutDirectoryTemplateDuplicateFinder templateFinder = new CheckoutDirectoryTemplateDuplicateFinder(TestHelper.TEST_CVS_VALID_BUILD_ID, "", "never_existed_" + System.currentTimeMillis());
    assertNull(templateFinder.find());
  }


  /**
   * Does not find non-intersecting.
   *
   * @throws BuildException
   * @throws ValidationException
   */
  public void test_notFound() throws BuildException, ValidationException {
    final String staticCheckoutDirTemplate = TestHelper.getTestTempDir().toString() + '/' + getClass().getName();
    TestHelper.setSourceControlProperty(TestHelper.TEST_P4_VALID_BUILD_ID, VCSAttribute.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE, staticCheckoutDirTemplate);
    final CheckoutDirectoryTemplateDuplicateFinder templateFinder = new CheckoutDirectoryTemplateDuplicateFinder(TestHelper.TEST_CVS_VALID_BUILD_ID, "", staticCheckoutDirTemplate + "other_path");
    assertNull(templateFinder.find());
  }


  public void test_found() throws BuildException, ValidationException {
    // existing
    final String staticCheckoutDirTemplate = TestHelper.getTestTempDir().toString() + '/' + getClass().getName();
    TestHelper.setSourceControlProperty(TestHelper.TEST_P4_VALID_BUILD_ID, VCSAttribute.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE, staticCheckoutDirTemplate);
    final CheckoutDirectoryTemplateDuplicateFinder templateFinder = new CheckoutDirectoryTemplateDuplicateFinder(TestHelper.TEST_CVS_VALID_BUILD_ID, "", staticCheckoutDirTemplate);
    assertNotNull(templateFinder.find());
  }


  public void test_doesNotFindDynamic() throws BuildException, ValidationException {
    final String staticCheckoutDirTemplate = TestHelper.getTestTempDir().toString() + '/' + getClass().getName() + "${build.id}";
    TestHelper.setSourceControlProperty(TestHelper.TEST_P4_VALID_BUILD_ID, VCSAttribute.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE, staticCheckoutDirTemplate);
    final CheckoutDirectoryTemplateDuplicateFinder templateFinder = new CheckoutDirectoryTemplateDuplicateFinder(TestHelper.TEST_CVS_VALID_BUILD_ID, "", staticCheckoutDirTemplate);
    assertNull(templateFinder.find());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestCheckouDirectoryTemplateDuplicateFinder.class, new String[]{
      "test_found",
    });
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
