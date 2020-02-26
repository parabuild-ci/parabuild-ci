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
package org.parabuild.ci.build;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.util.*;
import org.parabuild.ci.object.*;

/**
 */
public class SSTestLabelTemplateFinder extends ServersideTestCase {

  private LabelTemplateFinder templateFinder = null;


  public SSTestLabelTemplateFinder(final String s) {
    super(s);
  }


  public void test_notFound() throws BuildException {
    // never existed template
    templateFinder.setTemplate("never_existed_" + System.currentTimeMillis());
    assertTrue(!templateFinder.find());
    assertNull(templateFinder.getFoundBuildName());
  }


  public void test_found() throws BuildException {
    // existing defined in dataset.xml
    templateFinder.setTemplate("cvs_test_build_${build.number}_${build.timestamp}");
    assertTrue(templateFinder.find());
    assertNotNull(templateFinder.getFoundBuildName());
  }


  public void test_foundNew() throws BuildException {
    // reset to new ID
    templateFinder.setBuildID(BuildConfig.UNSAVED_ID);
    templateFinder.setTemplate("cvs_test_build_${build.number}_${build.timestamp}");
    assertTrue(templateFinder.find());
    assertNotNull(templateFinder.getFoundBuildName());
  }

  public void test_bug713doesNotFindIfBuildNameIsThere() throws BuildException {
    // existing defined in dataset.xml
    templateFinder.setTemplate("cvs_test_build_${build.name}_${build.number}_${build.timestamp}");
    assertTrue("Shouild not find", !templateFinder.find());
    assertNull(templateFinder.getFoundBuildName());
  }



  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestLabelTemplateFinder.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    templateFinder = new LabelTemplateFinder();
    templateFinder.setBuildID(9999);
  }
}
