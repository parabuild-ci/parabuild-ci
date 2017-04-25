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
package org.parabuild.ci.promotion;

import java.util.List;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;

/**
 *
 */
public class SSTestPromotionManager extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestPromotionManager.class);
  private PromotionConfigurationManager pm;


  public void test_get() {
    final List promotionList = pm.getPromotionList();
    assertTrue(promotionList.size() > 0);
  }


  /**
   * Required by JUnit
   */
  public SSTestPromotionManager(final String s) {
    super(s);
  }


  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestPromotionManager.class, new String[]{
    });
  }


  protected void setUp() throws Exception {
    // call ServerSideTest setup that initializes db data
    super.setUp();
    pm = PromotionConfigurationManager.getInstance();
  }
}
