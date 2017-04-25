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
package org.parabuild.ci.security;

import org.apache.commons.logging.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;

/**
 *
 */
public class SATestSecurityUtils extends TestCase {

  private static final Log log = LogFactory.getLog(SATestSecurityUtils.class);

  public static final String TEST_KEY = "01234567";
  private static final String STRING_TO_ENCRYPT = "test_original";
  private static final String STRING_ENCRYPTED = "E23FAAA7E60842B18C0BF5BA6E79B326";


  public SATestSecurityUtils(final String s) {
    super(s);
  }


  public void test_encrypt() throws Exception {
    final String encrypted = SecurityUtils.encrypt(STRING_TO_ENCRYPT, TEST_KEY);
    if (log.isDebugEnabled()) log.debug("encrypted = " + encrypted);
    assertEquals(STRING_ENCRYPTED, encrypted);
  }


  public void test_decrypt() throws Exception {
    final String decrypted = SecurityUtils.decrypt(STRING_ENCRYPTED, TEST_KEY);
    assertEquals(STRING_TO_ENCRYPT, decrypted);
    if (log.isDebugEnabled()) log.debug("decrypted = " + decrypted);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestSecurityUtils.class, new String[]{
      "test_encrypt",
      "test_decrypt"
    });
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
