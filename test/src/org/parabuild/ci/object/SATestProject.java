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
package org.parabuild.ci.object;

import junit.framework.*;

/**
 *
 */
public class SATestProject extends TestCase {

  private Project project;
  private static final String TEST_DESCRIPTION = "test description";
  private static final int TEST_ID = 9999;
  private static final String TEST_KEY = "TESTKEY";
  private static final String TEST_NAME = "test name";
  private static final long TEST_TIME_STAMP = 77777L;
  private static final byte TEST_TYPE = 55;


  public void test_setGetDelete() {
    project.setDeleted(true);
    assertTrue(project.isDeleted());
    project.setDeleted(false);
    assertTrue(!project.isDeleted());
  }


  public void test_setGetDescription() {
    project.setDescription(TEST_DESCRIPTION);
    assertEquals(TEST_DESCRIPTION, project.getDescription());
  }


  public void test_setGetID() {
    project.setID(TEST_ID);
    assertEquals(TEST_ID, project.getID());
  }


  public void test_setGetKey() {
    project.setKey(TEST_KEY);
    assertEquals(TEST_KEY, project.getKey());
  }


  public void test_setGetName() {
    project.setName(TEST_NAME);
    assertEquals(TEST_NAME, project.getName());
  }


  public void test_setGetTimeStamp() {
    project.setTimeStamp(TEST_TIME_STAMP);
    assertEquals(TEST_TIME_STAMP, project.getTimeStamp());
  }


  public void test_setGetType() {
    project.setType(TEST_TYPE);
    assertEquals(TEST_TYPE, project.getType());
  }


  protected void setUp() throws Exception {
    super.setUp();
    project = new Project();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestProject.class);
  }


  public SATestProject(final String s) {
    super(s);
  }
}
