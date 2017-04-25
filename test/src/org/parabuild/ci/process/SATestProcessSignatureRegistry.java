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
package org.parabuild.ci.process;

import java.util.*;
import junit.framework.*;

import com.gargoylesoftware.base.testing.*;

/**
 * Tests ProcessManager functionality
 */
public class SATestProcessSignatureRegistry extends TestCase {

  private ProcessSignatureRegistry processSignatureRegistry;
  private static final String TEST_SIGNATURE_1 = "test/signature/1";
  private static final String TEST_SIGNATURE_2 = "test/signature/2";


  public SATestProcessSignatureRegistry(final String s) {
    super(s);
  }


  /**
   * Tests that we can register
   */
  public void test_registerString() throws Exception {
    int before = processSignatureRegistry.signtatures().size();
    assertEquals(0, before);
    processSignatureRegistry.register(TEST_SIGNATURE_1);
    int after = processSignatureRegistry.signtatures().size();
    assertEquals(before + 1, after);

    // check it newly added is there
    final List signatures = processSignatureRegistry.signtatures();
    Collections.sort(signatures);
    assertTrue(Collections.binarySearch(signatures, TEST_SIGNATURE_1) >= 0);

    // does not register blank
    before = processSignatureRegistry.signtatures().size();
    processSignatureRegistry.register("");
    after = processSignatureRegistry.signtatures().size();
    assertEquals(before, after);
  }


  /**
   * Tests that we can unregister string
   */
  public void test_unregisterString() throws Exception {
    processSignatureRegistry.register(TEST_SIGNATURE_1);
    processSignatureRegistry.unregister(TEST_SIGNATURE_1);
    assertEquals(0, processSignatureRegistry.signtatures().size());
  }


  /**
   * Tests that we can register list of strings
   */
  public void test_registerList() throws Exception {
    final List toAdd = new ArrayList(2);
    toAdd.add(TEST_SIGNATURE_1);
    toAdd.add(TEST_SIGNATURE_2);
    final int before = processSignatureRegistry.signtatures().size();
    processSignatureRegistry.register(toAdd);
    final int after = processSignatureRegistry.signtatures().size();
    assertEquals(before + 2, after);

    // check it newly added is there
    final List signatures = processSignatureRegistry.signtatures();
    Collections.sort(signatures);
    assertTrue(Collections.binarySearch(signatures, TEST_SIGNATURE_1) >= 0);
    assertTrue(Collections.binarySearch(signatures, TEST_SIGNATURE_2) >= 0);
  }


  /**
   * Tests that we can unregister list of strings
   */
  public void test_unregisterList() throws Exception {
    final List toAdd = new ArrayList(2);
    toAdd.add(TEST_SIGNATURE_1);
    toAdd.add(TEST_SIGNATURE_2);
    processSignatureRegistry.register(toAdd);
    processSignatureRegistry.unregister(toAdd);
    assertEquals(0, processSignatureRegistry.signtatures().size());
  }


  protected void setUp() throws Exception {
    super.setUp();
    processSignatureRegistry = new ProcessSignatureRegistry();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestProcessSignatureRegistry.class,
      new String[]{
        "test_registerString",
        "test_unregisterString",
        "test_registerList",
        "test_unregisterList"
      });
  }
}
