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
package org.parabuild.ci.merge.finder.perforce;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

/**
 */
public class SATestBranchViewToClientViewTransformer extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestBranchViewToClientViewTransformer.class);


  public void test_direct() throws Exception {
    final P4BranchViewToClientViewTransformer transformer = new P4BranchViewToClientViewTransformer("//depot/dev/bt/... //depot/dev/bt31/...", false);
    assertEquals("//depot/dev/bt/... //parabuild/dev/bt/...", transformer.transformToSourceClientView().trim());
    assertEquals("//depot/dev/bt31/... //parabuild/dev/bt31/...", transformer.transformToTargetClientView().trim());
  }


  public void test_reverse() throws Exception {
    final P4BranchViewToClientViewTransformer transformer = new P4BranchViewToClientViewTransformer("//depot/dev/bt/... //depot/dev/bt31/...", true);
    assertEquals("//depot/dev/bt31/... //parabuild/dev/bt31/...", transformer.transformToSourceClientView().trim());
    assertEquals("//depot/dev/bt/... //parabuild/dev/bt/...", transformer.transformToTargetClientView().trim());
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public SATestBranchViewToClientViewTransformer(final String s) {
    super(s);
  }
}
