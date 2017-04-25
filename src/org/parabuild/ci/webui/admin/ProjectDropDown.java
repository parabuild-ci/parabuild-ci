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

import java.util.*;

import org.parabuild.ci.webui.common.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.project.*;

/**
 * A drop-down to show project names.
 */
public final class ProjectDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 5324112876509147945L;
  public static final int NOT_SELECTED_ID = Project.UNSAVED_ID;


  /**
   * Constructor
   */
  public ProjectDropDown() {
    super.addCodeNamePair(NOT_SELECTED_ID, "Select project:");
    final List allProjects = ProjectManager.getInstance().getProjects();
    for (final Iterator i = allProjects.iterator(); i.hasNext();) {
      final Project project = (Project)i.next();
      super.addCodeNamePair(project.getID(), project.getName());
    }
  }
}
