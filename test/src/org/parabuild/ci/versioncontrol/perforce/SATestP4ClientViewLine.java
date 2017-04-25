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

import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 * SATestP4ClientViewLine
 * <p/>
 *
 * @author Slava Imeshev
 * @since May 12, 2009 11:22:50 AM
 */
public final class SATestP4ClientViewLine extends TestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL
   */
  private static final Logger LOG = Logger.getLogger(SATestP4ClientViewLine.class); // NOPMD

  private static final String DEPOT_PATH = "//depot/path/...";
  private static final String CLIENT_PATH = "//parabuild/path/...";

  private P4ClientViewLine clientViewLine;


  public SATestP4ClientViewLine(String s) {
    super(s);
  }


  public void testGetDepotSide() {
    assertEquals(DEPOT_PATH, clientViewLine.getDepotSide());
  }


  public void testGetClientSide() {
    assertEquals(CLIENT_PATH, clientViewLine.getClientSide());
  }


  public void testEquals() {
    assertEquals(clientViewLine, new P4ClientViewLine(DEPOT_PATH, CLIENT_PATH));
  }


  public void testHashCode() {
    assertTrue(clientViewLine.hashCode() != 0);
  }


  public void testToString() {
    assertNotNull(clientViewLine.toString());
  }


  public void testRemovesDoublesSlashes() {
    final P4ClientViewLine doubleSlashedView = new P4ClientViewLine("//depot/path//...", "//parabuild///path//...");
    assertEquals("//depot/path/...", doubleSlashedView.getDepotSide());
    assertEquals("//parabuild/path/...", doubleSlashedView.getClientSide());

    final P4ClientViewLine doubleSlashedViewPlus = new P4ClientViewLine("+//depot/path//...", "//parabuild///path//...");
    assertEquals("+//depot/path/...", doubleSlashedViewPlus.getDepotSide());
    assertEquals("//parabuild/path/...", doubleSlashedViewPlus.getClientSide());

    final P4ClientViewLine doubleSlashedViewMinus = new P4ClientViewLine("-//depot/path//...", "//parabuild///path//...");
    assertEquals("-//depot/path/...", doubleSlashedViewMinus.getDepotSide());
    assertEquals("//parabuild/path/...", doubleSlashedViewMinus.getClientSide());
  }


  protected void setUp() throws Exception {
    super.setUp();
    clientViewLine = new P4ClientViewLine(DEPOT_PATH, CLIENT_PATH);
  }
}
