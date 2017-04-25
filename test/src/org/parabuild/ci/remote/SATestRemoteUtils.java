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
package org.parabuild.ci.remote;

import junit.framework.*;

/**
 * Tests RemoteUtils
 */
public class SATestRemoteUtils extends TestCase {

  public void test_normalizeHostPort() throws Exception {
    assertEquals("localhost:8080", RemoteUtils.normalizeHostPort(""));
    assertEquals("test:8080", RemoteUtils.normalizeHostPort("test"));
    assertEquals("test:9090", RemoteUtils.normalizeHostPort("test:9090"));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestRemoteUtils.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public SATestRemoteUtils(final String s) {
    super(s);
  }
}
