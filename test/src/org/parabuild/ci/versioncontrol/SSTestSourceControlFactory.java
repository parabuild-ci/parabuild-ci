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

import junitx.util.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.versioncontrol.perforce.P4SourceControl;

/**
 * Tests TestSourceControlFactory
 */
public class SSTestSourceControlFactory extends ServersideTestCase {

  public SSTestSourceControlFactory(final String s) {
    super(s);
  }


  public void test_canMakeCVSSourceControl() throws Exception {
    final SourceControl sourceControl = VersionControlFactory.makeVersionControl(ConfigurationManager.getInstance().getBuildConfiguration(TestHelper.TEST_CVS_VALID_BUILD_ID));
    assertTrue(sourceControl instanceof CVSSourceControl);
  }


  public void test_canMakeP4SourceControl() throws Exception {
    final SourceControl sourceControl = VersionControlFactory.makeVersionControl(ConfigurationManager.getInstance().getBuildConfiguration(TestHelper.TEST_P4_VALID_BUILD_ID));
    assertTrue(sourceControl instanceof P4SourceControl);
  }


  public void test_canMakeReferenceControl() throws Exception {
    final SourceControl sourceControl = VersionControlFactory.makeVersionControl(ConfigurationManager.getInstance().getBuildConfiguration(TestHelper.TEST_RECURRENT_BUILD_ID));
    assertTrue(sourceControl instanceof ReferenceSourceControl);
    final SourceControl delegate = (SourceControl)PrivateAccessor.getField(sourceControl, "delegate");
    assertTrue(delegate instanceof P4SourceControl);
  }


  public void test_canMakeDeepReferenceControl() throws Exception {
    final SourceControl sourceControl = VersionControlFactory.makeVersionControl(ConfigurationManager.getInstance().getBuildConfiguration(TestHelper.TEST_REF_RECURRENT_BUILD_ID));
    assertTrue(sourceControl instanceof ReferenceSourceControl);
    final SourceControl delegate = (SourceControl)PrivateAccessor.getField(sourceControl, "delegate");
    assertTrue(delegate instanceof P4SourceControl);
  }


  public void test_canMakePVCSSourceControl() throws Exception {
    final SourceControl sourceControl = VersionControlFactory.makeVersionControl(ConfigurationManager.getInstance().getBuildConfiguration(TestHelper.TEST_PVCS_VALID_BUILD_ID));
    assertTrue(sourceControl instanceof PVCSSourceControl);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestSourceControlFactory.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
