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
package org.parabuild.ci.remote.internal;

import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;

/**
 * Tests LocalAgentEnvironment
 */
public class SSTestLocalBuilderEnvironment extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestLocalBuilderEnvironment.class);

  private LocalAgentEnvironment environment = null;


  public void test_isProhibitedPath() throws Exception {
    if (environment.isWindows()) {
      assertTrue(environment.isProhibitedPath("c:\\"));
    }
  }


  public void test_createExecutorHandle() throws IOException {
    final int handle1 = environment.createExecutorHandle();
    final int handle2 = environment.createExecutorHandle();
    assertTrue(handle2 != handle1);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestLocalBuilderEnvironment.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    environment = new LocalAgentEnvironment();
  }


  public SSTestLocalBuilderEnvironment(final String s) {
    super(s);
  }
}
