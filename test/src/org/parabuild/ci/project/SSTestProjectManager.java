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
package org.parabuild.ci.project;

import org.apache.commons.logging.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.object.*;

/**
 *
 */
public class SSTestProjectManager extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestProjectManager.class);

  private ProjectManager pm = null;
  private static final String TEST_KEY = "MYPROJECT";


  public void test_getProjects() {
    assertTrue(!pm.getProjects().isEmpty());
  }


  public void test_getProject() {
    final Project project = pm.getProject(0);
    assertNotNull(project);
    assertEquals(Project.TYPE_SYSTEM, project.getType());
    assertEquals(Project.KEY_SYSTEM, project.getKey());
  }


  public void test_getProjectByInteger() {
    final Project project = pm.getProject(new Integer(0));
    assertNotNull(project);
    assertEquals(Project.TYPE_SYSTEM, project.getType());
    assertEquals(Project.KEY_SYSTEM, project.getKey());
  }


  public void test_getProjectByKey() {
    final Project project = pm.getProjectByKey(TEST_KEY);
    assertNotNull(project);
    assertEquals(Project.TYPE_USER, project.getType());
    assertEquals(TEST_KEY, project.getKey());
  }


  public void test_save() {
    // test values
    final String newName = "new name";
    final String newKey = "newkey";
    // update
    final Project oldProject = pm.getProject(1);
    oldProject.setName(newName);
    oldProject.setKey(newKey);
    pm.saveProject(oldProject);
    // assert
    final Project updatedProject = pm.getProject(1);
    assertEquals(newName, updatedProject.getName());
    assertEquals(newKey, updatedProject.getKey());
  }


  public void test_delete() {
    final Project project = pm.getProject(1);
    assertTrue(!project.isDeleted());
    pm.deleteProject(project);
    assertTrue(project.isDeleted());
  }


  public void test_getProjectBuild() {
    final ProjectBuild projectBuild = pm.getProjectBuild(TestHelper.TEST_P4_VALID_BUILD_ID);
    assertNotNull(projectBuild);
    assertEquals(TestHelper.TEST_P4_VALID_BUILD_ID, projectBuild.getActiveBuildID());
    assertEquals(1, projectBuild.getProjectID());
  }


  public void test_getProjectResultGroup() {
    final ProjectResultGroup projectResultGroup = pm.getProjectResultGroup(0);
    assertNotNull(projectResultGroup);
    assertEquals(0, projectResultGroup.getResultGroupID());
    assertEquals(1, projectResultGroup.getProjectID());
  }


  /**
   * Required by JUnit
   */
  public SSTestProjectManager(final String s) {
    super(s);
  }


  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestProjectManager.class, new String[]{
    });
  }


  protected void setUp() throws Exception {
    // call ServerSideTest setup that initializes db data
    super.setUp();
    pm = ProjectManager.getInstance();
  }
}
