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
package org.parabuild.ci.versioncontrol.perforce;

import java.io.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import org.parabuild.ci.TestHelper;

/**
 */
public class SATestP4IntegrateParserImpl extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestP4IntegrateParserImpl.class);

  private static final File P4_INTEGRATE_OUTPUT_FILE = new File(TestHelper.getTestDataDir(), "test_p4_integrate_n.txt");
  private static final File P4_INTEGRATE_ERROR_OUTPUT_FILE = new File(TestHelper.getTestDataDir(), "test_p4_integrate_failed_Branch_mapping_produced_illegal_filename.txt");
  private static final File P4_INTEGRATE_WITH_CANNOT_INTEGRATE_OPERATION = new File(TestHelper.getTestDataDir(), "test_p4_integrate_n_reverse_w_cant_integrate.txt");
  private static final File P4_INTEGRATE_WITH_CANNOT_DELETE = new File(TestHelper.getTestDataDir(), "test_p4_integrate_w_cannot_delete.txt");

  private P4IntegrateParserImpl parser = null;


  public void test_pars() throws Exception {
    final Collection revisions = parser.parse(P4_INTEGRATE_OUTPUT_FILE);
    assertEquals(3362, revisions.size());
    // find rane source revision
    boolean found = false;
    for (Iterator iterator = revisions.iterator(); iterator.hasNext();) {
      final Revision from = ((Integration)iterator.next()).getFrom();
      if (from.getPath().equals("//depot/dev/bt/3rdparty/viewtier/lib/viewtier.jar")) {
        assertEquals("61", from.getStart());
        assertEquals("63", from.getEnd());
        found = true;
        break;
      }
    }
    assertTrue("Path should be found", found);
  }


  public void test_parsesWithCannotInegrateOperation() throws Exception {
    final Collection revisions = parser.parse(P4_INTEGRATE_WITH_CANNOT_INTEGRATE_OPERATION);
    assertEquals(35, revisions.size());
  }


  public void test_parsesWithCannotDelete() throws Exception {
    final Collection revisions = parser.parse(P4_INTEGRATE_WITH_CANNOT_DELETE);
    assertEquals(1, revisions.size());
  }


  public void test_parseErrors() throws Exception {
    try {
      parser.parse(P4_INTEGRATE_ERROR_OUTPUT_FILE);
      TestHelper.failNoExceptionThrown();
    } catch (IOException e) {
    }
  }


  protected void setUp() throws Exception {
    super.setUp();
    parser = new P4IntegrateParserImpl();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestP4IntegrateParserImpl.class, new String[]{
      "test_parsesWithCannotInegrateOperation",
    });
  }


  public SATestP4IntegrateParserImpl(final String s) {
    super(s);
  }
}
