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

import junit.framework.TestSuite;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;

/**
 * Tests CheckoutDirectoryPathGenerator
 */
public class SSTestSourceControlSettingResolver extends ServersideTestCase {

  private static final int ACTIVE_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;
  private static final String AGENT_HOST_NAME = "test-host-name";
  private static final String BUILD_NAME = "test-build";


  private SourceControlSettingResolver resolver;


  public SSTestSourceControlSettingResolver(final String s) {
    super(s);
  }


  /**
   * @throws Exception if errors occur.
   */
  public void test_makeCheckoutDirectoryPathStatic() throws Exception {
    final String staticTemplate = "C:\\test";
    assertEquals(staticTemplate, resolver.resolve(staticTemplate));
  }


  /**
   * @throws Exception if errors occur.
   */
  public void test_makeCheckoutDirectoryPathDynamic() throws Exception {
    assertEquals("C:\\test\\test-build\\1", resolver.resolve("C:\\test\\${build.name}\\${build.id}"));
  }


  /**
   * @throws Exception if errors occur.
   */
  public void test_picksUpSystemLevelParameter() throws Exception {
    assertEquals("SYSTEM_LEVEL_PARAMETER_VALUE", resolver.resolve("${SYSTEM_LEVEL_PARAMETER}"));
  }


  /**
   * @throws Exception if errors occur.
   */
  public void test_picksUpProjectLevelParameter() throws Exception {
    assertEquals("PROJECT_LEVEL_PARAMETER_VALUE", resolver.resolve("${PROJECT_LEVEL_PARAMETER}"));
  }


  public void testResolveBug1589() throws Exception {

    final String resolvedValue = resolver.resolve("${builder.host}_BuildId_${build.id}");

    assertEquals("test-host-name_BuildId_1", resolvedValue);
  }


  public void testToString() throws Exception {

    assertNotNull(resolver.toString());
  }


  public void setUp() throws Exception {
    resolver = new SourceControlSettingResolver(BUILD_NAME, ACTIVE_BUILD_ID, AGENT_HOST_NAME);
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestSourceControlSettingResolver.class);

  }
}
