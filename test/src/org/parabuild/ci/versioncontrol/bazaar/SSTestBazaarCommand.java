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
package org.parabuild.ci.versioncontrol.bazaar;

import org.apache.log4j.Logger;
import org.parabuild.ci.ServersideTestCase;

/**
 * SSTestBazaarCommand
 * <p/>
 *
 * @author Slava Imeshev
 * @since Apr 16, 2010 12:12:26 PM
 */
public final class SSTestBazaarCommand extends ServersideTestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(SSTestBazaarCommand.class); // NOPMD


  public SSTestBazaarCommand(final String name) {
    super(name);
  }


  public void testBranchLocationToRelativeBuildDir() throws Exception {
    assertEquals(".", BazaarCommand.branchLocationToRelativeBuildDir("bzr+ssh://test:8888"));
    assertEquals(".", BazaarCommand.branchLocationToRelativeBuildDir("file://test"));
    assertEquals("my_path", BazaarCommand.branchLocationToRelativeBuildDir("bzr+ssh://test:8888/my_path"));
    assertEquals("my_path", BazaarCommand.branchLocationToRelativeBuildDir("bzr+ssh://test:8888/long/my_path"));
  }
}
