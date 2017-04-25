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
package org.parabuild.ci.object;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 */
public class SATestManualStartParameter extends TestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SATestManualStartParameter.class);
  private StartParameter parameter = null;
  private static final String TEST_VALUE2 = "test_value2";
  private static final String TEST_VALUE1 = "test_value1";


  public void test_encrypt() throws Exception {
    final List list = new ArrayList();
    list.add(TEST_VALUE1);
    list.add(TEST_VALUE2);
    parameter.setRuntimeValue(list);
    assertEquals(TEST_VALUE1 + ',' + TEST_VALUE2, parameter.getRuntimeValue());
  }


  public void test_setPresentation() {
    final byte presentationRadioList = StartParameter.PRESENTATION_RADIO_LIST;
    parameter.setPresentation(presentationRadioList);
    assertEquals(presentationRadioList, parameter.getPresentation());
  }


  public void test_makeTokenizer() {
    final String testValue = "blah";
    final StringTokenizer st = StartParameter.makeTokenizer(testValue);
    assertTrue(st.hasMoreTokens());
    assertEquals(testValue, st.nextToken());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestManualStartParameter.class, new String[]{
    });
  }


  public SATestManualStartParameter(final String s) {
    super(s);
    this.parameter = new StartParameter();
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
