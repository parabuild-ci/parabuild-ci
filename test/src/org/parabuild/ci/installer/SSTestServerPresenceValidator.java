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
package org.parabuild.ci.installer;

import java.io.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;

public final class SSTestServerPresenceValidator extends ServersideTestCase {

  public void test_validate() throws IOException {
    final ServerPresenceValidator validator = new ServerPresenceValidator();
    assertTrue(validator.serverIsRunning(new File(System.getProperty("test.dir.manager"))));
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestServerPresenceValidator.class);
  }


  public SSTestServerPresenceValidator(final String s) {
    super(s);
  }
}
