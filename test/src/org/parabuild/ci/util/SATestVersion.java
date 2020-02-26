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
package org.parabuild.ci.util;

import junit.framework.*;

import org.parabuild.ci.*;

public class SATestVersion extends TestCase {

  Version version = null;



  public void test_productName() throws Exception {
    assertNotNull(Version.productName());
    assertEquals(Version.STR_PRODUCT_NAME, Version.productName());
  }


  public void test_productVersion() throws Exception {
    assertNotNull(Version.productVersion());
    assertEquals(-1, Version.productVersion().indexOf('@'));
  }


  public void test_releaseBuild() throws Exception {
    assertNotNull(Version.releaseBuild());
    assertEquals(-1, Version.releaseBuild().indexOf('@'));
  }


  public void test_releaseChange() throws Exception {
    assertNotNull(Version.releaseChange());
    assertEquals(-1, Version.releaseChange().indexOf('@'));
  }


  public void test_releaseDate() throws Exception {
    assertNotNull(Version.releaseDate());
    assertEquals(-1, Version.releaseDate().indexOf('@'));
  }


  public void test_versionToString() throws Exception {
    // full version - true
    assertNotNull(Version.versionToString(true));
    assertEquals(-1, Version.versionToString(true).indexOf('@'));
    assertTrue(Version.versionToString(true).length() > 0);
    // full version - false
    assertNotNull(Version.versionToString(false));
    assertEquals(-1, Version.versionToString(false).indexOf('@'));
    assertTrue(Version.versionToString(false).length() > 0);
  }


  public SATestVersion(final String s) {
    super(s);
  }
}