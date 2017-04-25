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
package org.parabuild.ci.versioncontrol;

import java.util.*;

import junit.framework.*;


/**
 * Tests VSSDateFormatFactory
 */
public final class SATestVSSDateFormatFactory extends TestCase {

  public void test_isEUFormat() throws Exception {
    assertTrue(!new VSSDateFormatFactory(Locale.US).isEUFormat());
    assertTrue(new VSSDateFormatFactory(Locale.FRANCE).isEUFormat());
  }


  public void test_outputDateTimeFormatUS() throws Exception {
    assertEquals(new VSSDateFormatFactory(Locale.US).outputDateTimeFormatUS(), new VSSDateFormatFactory(Locale.FRANCE).outputDateTimeFormatUS());
  }


  public void test_outputDateTimeFormat() throws Exception {
    assertTrue(!new VSSDateFormatFactory(Locale.US).outputDateTimeFormat().equals(new VSSDateFormatFactory(Locale.FRANCE).outputDateTimeFormat()));
    assertTrue(new VSSDateFormatFactory(Locale.US).outputDateTimeFormat().equals(new VSSDateFormatFactory(Locale.US).outputDateTimeFormat()));
  }


  public void test_inputDateTimeFormat() throws Exception {
    assertTrue(!new VSSDateFormatFactory(Locale.US).inputDateTimeFormat().equals(new VSSDateFormatFactory(Locale.FRANCE).inputDateTimeFormat()));
    assertTrue(new VSSDateFormatFactory(Locale.US).inputDateTimeFormat().equals(new VSSDateFormatFactory(Locale.US).inputDateTimeFormat()));
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestVSSDateFormatFactory.class);
  }


  public SATestVSSDateFormatFactory(final String s) {
    super(s);
  }
}
