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
package org.parabuild.ci.object;

import junit.framework.*;

public final class SATestSourceControlSettingVO extends TestCase {

  private static final String TEST_NAME = "test_name";
  private static final String TEST_VALUE = "test_value";

  private static final String NEW_TEST_NAME = "new_test_name";
  private static final String NEW_TEST_VALUE = "new_test_value";

  private SourceControlSettingVO vo = null;


  public void test_defaultContructor() {
    vo = new SourceControlSettingVO();
    assertNull(vo.getName());
    assertNull(vo.getValue());
  }


  public void test_getName() {
    assertEquals(TEST_NAME, vo.getName());
  }


  public void test_getValue() {
    assertEquals(TEST_VALUE, vo.getValue());
  }


  public void test_setValue() {
    vo.setValue(NEW_TEST_VALUE);
    assertEquals(NEW_TEST_VALUE, vo.getValue());
  }


  public void test_setName() {
    vo.setName(NEW_TEST_NAME);
    assertEquals(NEW_TEST_NAME, vo.getName());
  }


  public void test_scmSettingIsSupported() {
    assertTrue(SourceControlSettingVO.scmSettingIsSupported(SourceControlSettingVO.CVS_BRANCH_NAME));
    assertTrue(SourceControlSettingVO.scmSettingIsSupported(SourceControlSettingVO.SVN_DEPOT_PATH));
    assertTrue(!SourceControlSettingVO.scmSettingIsSupported("not.supported.name"));
  }


  protected void setUp() throws Exception {
    vo = new SourceControlSettingVO(TEST_NAME, TEST_VALUE);
  }


  public static TestSuite suite() {
    return new TestSuite(SATestSourceControlSettingVO.class);
  }


  public SATestSourceControlSettingVO(final String s) {
    super(s);
  }
}
