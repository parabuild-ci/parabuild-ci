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

import org.parabuild.ci.object.*;

/**
 *
 */
public class SATestClearCaseStorageLocationArgumentFactory extends TestCase {

  private static final String STORAGE_LOCATION = "test_stgloc_name";
  private static final String STORAGE_PATH = "test_storage_path";
  private static final int TEST_ACTIVE_BUILD_ID = 9999;


  public SATestClearCaseStorageLocationArgumentFactory(final String s) {
    super(s);
  }


  public void test_generatesEmptyIfNoCode() throws Exception {
    assertEquals("", new ClearCaseStorageLocationArgumentFactory(TEST_ACTIVE_BUILD_ID, false).makeStorageLocationArgument(SourceControlSetting.CLEARCASE_STORAGE_CODE_AUTOMATIC, ""));
    assertEquals("", new ClearCaseStorageLocationArgumentFactory(TEST_ACTIVE_BUILD_ID, false).makeStorageLocationArgument(SourceControlSetting.CLEARCASE_STORAGE_CODE_STGLOC, ""));
    assertEquals("", new ClearCaseStorageLocationArgumentFactory(TEST_ACTIVE_BUILD_ID, false).makeStorageLocationArgument(SourceControlSetting.CLEARCASE_STORAGE_CODE_VWS, ""));
  }


  public void test_generatesStgloc() throws Exception {
    assertEquals(" -stgloc " + STORAGE_LOCATION, new ClearCaseStorageLocationArgumentFactory(TEST_ACTIVE_BUILD_ID, false).makeStorageLocationArgument(SourceControlSetting.CLEARCASE_STORAGE_CODE_STGLOC, STORAGE_LOCATION));
    assertEquals(" -stgloc \"" +STORAGE_LOCATION + '\"', new ClearCaseStorageLocationArgumentFactory(TEST_ACTIVE_BUILD_ID, true).makeStorageLocationArgument(SourceControlSetting.CLEARCASE_STORAGE_CODE_STGLOC, STORAGE_LOCATION));
  }


  public void test_generatesVws() throws Exception {
    assertEquals(" -vws " + STORAGE_PATH, new ClearCaseStorageLocationArgumentFactory(TEST_ACTIVE_BUILD_ID, false).makeStorageLocationArgument(SourceControlSetting.CLEARCASE_STORAGE_CODE_VWS, STORAGE_PATH));
    assertEquals(" -vws \"" + STORAGE_PATH + '\"', new ClearCaseStorageLocationArgumentFactory(TEST_ACTIVE_BUILD_ID, true).makeStorageLocationArgument(SourceControlSetting.CLEARCASE_STORAGE_CODE_VWS, STORAGE_PATH));
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestClearCaseStorageLocationArgumentFactory.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
