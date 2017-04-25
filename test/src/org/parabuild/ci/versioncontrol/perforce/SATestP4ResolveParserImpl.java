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
public class SATestP4ResolveParserImpl extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestP4ResolveParserImpl.class);

  private static final File P4_RESOLVE_AM_AUTPUT = new File(TestHelper.getTestDataDir(), "test_p4_resolve_am.txt");
  private static final File P4_RESOLVE_NO_FILES_TO_RESOLVE = new File(TestHelper.getTestDataDir(), "test_p4_resolve_no_files_to_resolve.txt");

  private P4ResolveParserImpl parser = null;


  public void test_parse() throws Exception {
    final int[] counter = new int[1];
    parser.parse(P4_RESOLVE_AM_AUTPUT, new P4ResolveDriver() {
      public void process(final Resolve resolve) {
        counter[0]++;
      }
    });

    assertEquals(27, counter[0]);
  }


  public void test_parseNoFilesToReserve() throws Exception {
    parser.parse(P4_RESOLVE_NO_FILES_TO_RESOLVE, new P4ResolveDriver() {
      public void process(final Resolve resolve) {
      }
    });
  }


  protected void setUp() throws Exception {
    super.setUp();
    parser = new P4ResolveParserImpl();
  }


  public SATestP4ResolveParserImpl(final String s) {
    super(s);
  }
}
