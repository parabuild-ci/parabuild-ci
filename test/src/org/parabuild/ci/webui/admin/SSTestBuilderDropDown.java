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
package org.parabuild.ci.webui.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;

/**
 * SSTestBuilderDropDown
 *
 * @author Slava Imeshev
 * @since Apr 29, 2009 5:39:51 PM
 */
public final class SSTestBuilderDropDown extends ServersideTestCase {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SSTestBuilderDropDown.class); // NOPMD
  private BuilderDropDown dropDown;


  public SSTestBuilderDropDown(final String s) {
    super(s);
  }


  public void testPopulateExcldedDeleted() {
    dropDown.populate(false);
    assertTrue(dropDown.getItemCount() > 0);
  }


  public void testPopulateIncludeDeleted() {
    dropDown.populate(true);
    assertTrue(dropDown.getItemCount() > 0);
  }


  public void setUp() throws Exception {
    super.setUp();
    dropDown = new BuilderDropDown();
  }


  public String toString() {
    return "SSTestBuilderDropDown{" +
            "dropDown=" + dropDown +
            "} " + super.toString();
  }
}
