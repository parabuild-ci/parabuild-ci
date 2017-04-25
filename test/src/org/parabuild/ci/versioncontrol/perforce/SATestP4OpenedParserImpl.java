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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

import org.parabuild.ci.TestHelper;

/**
 */
public class SATestP4OpenedParserImpl extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestP4OpenedParserImpl.class);

  private static final File P4_OPENED = new File(TestHelper.getTestDataDir(), "test_p4_opened.txt");
  private static final File P4_OPENED_NOT_OPENED = new File(TestHelper.getTestDataDir(), "test_p4_opened_not_opened.txt");

  private P4OpenedParserImpl parser = null;


  public void test_parse() throws Exception {
    final int[] counter = new int[1];
    parser.parse(P4_OPENED, new P4OpenedDriver() {
      public void process(final Opened integration) {
        counter[0]++;
      }
    });

    assertEquals(636, counter[0]);
  }


  public void test_parseNotOpened() throws Exception {
    parser.parse(P4_OPENED_NOT_OPENED, new P4OpenedDriver() {
      public void process(final Opened integration) {
      }
    });
  }


  protected void setUp() throws Exception {
    super.setUp();
    parser = new P4OpenedParserImpl();
  }


  public SATestP4OpenedParserImpl(final String s) {
    super(s);
  }
}
