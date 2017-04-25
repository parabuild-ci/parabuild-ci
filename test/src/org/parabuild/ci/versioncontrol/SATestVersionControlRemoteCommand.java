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

import junit.framework.*;

/**
 *
 */
public final class SATestVersionControlRemoteCommand extends TestCase {

  /**
   * Tests that password is removed.
   *
   * @throws Exception
   */
  public void test_bug962_removePasswordFromDebugString() throws Exception {
    assertEquals("\"C:\\Subversion\\bin\\svn.exe\" --non-interactive --username user_name update -r53 \"D:\\xxxxxxxxxxxxxxxxxxxx\"", VersionControlRemoteCommand.removePasswordFromDebugString("\"C:\\Subversion\\bin\\svn.exe\" --non-interactive --username user_name --password clear_text_password update -r53 \"D:\\xxxxxxxxxxxxxxxxxxxx\""));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestVersionControlRemoteCommand.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }


  public SATestVersionControlRemoteCommand(final String s) {
    super(s);
  }
}
