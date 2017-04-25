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
package org.parabuild.ci.build;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestSuite;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfigAttribute;

/**
 */
public final class SSTestBuildStartRequestBuilder extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestBuildStartRequestBuilder.class);


  public void test_makeStartRequest() {
    // no defaults by default.
    assertTrue(new BuildStartRequestBuilder().makeStartRequest(1, 1, null).parameterList().isEmpty());

    // mark build as required using default for build parameters
    ConfigurationManager.getInstance().saveObject(new BuildConfigAttribute(TestHelper.TEST_CVS_VALID_BUILD_ID, BuildConfigAttribute.USE_FIRST_PARAMETER_VALUE_AS_DEFAULT, BuildConfigAttribute.OPTION_CHECKED));
    assertTrue(!new BuildStartRequestBuilder().makeStartRequest(1, 1, null).parameterList().isEmpty());
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by NUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestBuildStartRequestBuilder.class,
      new String[]{
      });
  }


  public SSTestBuildStartRequestBuilder(final String s) {
    super(s);
  }
}
