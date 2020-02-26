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
package org.parabuild.ci.util;

import junit.framework.*;

/**
 * Tests BuildVersionGenerator
 */
public class SATestBuildVersionGenerator extends TestCase {

  private BuildVersionGenerator generator;


  public SATestBuildVersionGenerator(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_makeBuildVersion() throws Exception {
    final StringBuffer stringBuffer = generator.makeBuildVersion("product_name_10.5.${version.counter}.${build.number}.${build.name}", "test_build_name", 999, 888);
    assertEquals("product_name_10.5.888.999.test_build_name", stringBuffer.toString());
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();
    generator = new BuildVersionGenerator();
  }


  public static TestSuite suite() {
    return new TestSuite(SATestBuildVersionGenerator.class);

  }
}
