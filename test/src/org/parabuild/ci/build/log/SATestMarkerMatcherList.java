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
package org.parabuild.ci.build.log;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * MarkerMatcherList Tester.
 *
 * @author simeshev@cacheonix.com
 * @version 1.0
 * @since <pre>10/03/2008</pre>
 */
public final class SATestMarkerMatcherList extends TestCase {

  private MarkerMatcherList matcher = null;


  public SATestMarkerMatcherList(String s) {
    super(s);
  }


  public void testMatchRegex() {
    assertTrue(matcher.match(" testregex "));
    assertTrue(!matcher.match("shouldnotmatch"));
  }


  public void testMatchPlain() {
    assertTrue(matcher.match("testplain"));
  }


  public void testToString() {
    assertNotNull(matcher.toString());
  }


  protected void setUp() throws Exception {
    super.setUp();
    final List markers = new ArrayList(2);
    markers.add("^.*testregex.*$");
    markers.add("testplain");
    matcher = new MarkerMatcherList(markers);
  }
}
