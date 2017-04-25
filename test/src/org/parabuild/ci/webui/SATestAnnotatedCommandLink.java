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
package org.parabuild.ci.webui;

import junit.framework.*;

public final class SATestAnnotatedCommandLink extends TestCase {

  private static final String TEST_CAPTION = "test_caption";


  public void test_create() {
    new AnnotatedCommandLink(TEST_CAPTION, "http://test_url", "test_annotation");
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestAnnotatedCommandLink.class);
  }


  public SATestAnnotatedCommandLink(final String s) {
    super(s);
  }
}
