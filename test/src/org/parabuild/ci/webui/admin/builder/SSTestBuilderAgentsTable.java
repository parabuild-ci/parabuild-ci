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
package org.parabuild.ci.webui.admin.builder;

import junit.framework.TestSuite;
import org.apache.cactus.ServletTestCase;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.object.BuilderConfiguration;

/**
 *
 */
public final class SSTestBuilderAgentsTable extends ServletTestCase {

  /**
   * @noinspection UNUSED_SYMBOL,FieldCanBeLocal
   */
  private BuilderAgentsTable table = null;


  public SSTestBuilderAgentsTable(final String s) {
    super(s);
  }


  /**
   */
  public void test_create() throws Exception {
    // do nothing, create is called in setUp method.
  }


  /**
   */
  public void test_load() throws Exception {
    final BuilderConfiguration builderConfiguration = BuilderConfigurationManager.getInstance().getBuilder(0);
    table.load(builderConfiguration);
  }


  /**
   */
  public void test_save() throws Exception {
    final BuilderConfiguration builderConfiguration = BuilderConfigurationManager.getInstance().getBuilder(0);
    table.load(builderConfiguration);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBuilderAgentsTable.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    table = new BuilderAgentsTable();
  }
}
