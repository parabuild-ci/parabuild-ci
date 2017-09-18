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
package org.parabuild.ci.service;

import junit.framework.TestSuite;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.services.Log4jConfigurator;

import java.io.InputStream;
import java.util.Properties;

/**
 * Tests Logging service
 */
public class SSTestLoggingService extends ServersideTestCase {


  public SSTestLoggingService(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_debugLog4JPropertiesAreAvailable() throws Exception {
    InputStream is = null;
    try {
      // accessible?
      is = getClass().getResourceAsStream(Log4jConfigurator.DEBUG_LOG4_CONFIG);
      assertNotNull(is);

      // loadable?
      final Properties properties = new Properties();
      properties.load(is);
    } finally {
      IoUtils.closeHard(is);
    }
  }


  /**
   *
   */
  public void test_releaseLog4JPropertiesAreAvailable() throws Exception {
    InputStream is = null;
    try {
      // accessible?
      is = getClass().getResourceAsStream(Log4jConfigurator.RELEASE_LOG4_CONFIG);
      assertNotNull(is);

      // loadable?
      final Properties properties = new Properties();
      properties.load(is);
    } finally {
      IoUtils.closeHard(is);
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestLoggingService.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
