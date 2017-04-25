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

import org.parabuild.ci.merge.MergeClientNameGenerator;

/**
 */
public class SATestMergeClientNameGenerator extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestMergeClientNameGenerator.class);

  private MergeClientNameGenerator generator;


  public void test_namesAreNotTheSame() throws Exception {
    assertTrue(!generator.generateSourceClientName().equals(generator.generateTargetClientName()));
  }


  public void test_generateValidateClientName() throws Exception {
    assertNotNull(generator.generateValidateClientName());
  }


  public void test_generateSourceClientName() throws Exception {
    assertNotNull(generator.generateSourceClientName());
  }


  public void test_generateTargetClientName() throws Exception {
    assertNotNull(generator.generateTargetClientName());
  }


  protected void setUp() throws Exception {
    super.setUp();
    generator = new MergeClientNameGenerator(0);
  }


  public SATestMergeClientNameGenerator(final String s) {
    super(s);
  }
}
