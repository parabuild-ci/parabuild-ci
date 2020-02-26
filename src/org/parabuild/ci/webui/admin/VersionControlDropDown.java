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

import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * Version control dropdown contains a list of build VCS types
 */
public final class VersionControlDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 6932472851129032468L; // NOPMD


  /**
   * Constructor
   */
  public VersionControlDropDown() {

    super.addCodeNamePair(VersionControlSystem.SCM_ACCUREV, VersionControlSystem.NAME_SCM_ACCUREV);
    super.addCodeNamePair(VersionControlSystem.SCM_CLEARCASE, VersionControlSystem.NAME_SCM_CLEARCASE);
    super.addCodeNamePair(VersionControlSystem.SCM_BAZAAR, VersionControlSystem.NAME_BAZAAR);
    super.addCodeNamePair(VersionControlSystem.SCM_CVS, VersionControlSystem.NAME_SCM_CVS);
    super.addCodeNamePair(VersionControlSystem.SCM_FILESYSTEM, VersionControlSystem.NAME_SCM_FILESYSTEM);
    super.addCodeNamePair(VersionControlSystem.SCM_GENERIC, VersionControlSystem.NAME_SCM_GENERIC);
    super.addCodeNamePair(VersionControlSystem.SCM_GIT, VersionControlSystem.NAME_SCM_GIT);
    super.addCodeNamePair(VersionControlSystem.SCM_MERCURIAL, VersionControlSystem.NAME_SCM_MERCURIAL);
    super.addCodeNamePair(VersionControlSystem.SCM_MKS, VersionControlSystem.NAME_SCM_MKS);
    super.addCodeNamePair(VersionControlSystem.SCM_PERFORCE, VersionControlSystem.NAME_SCM_PERFORCE);
    super.addCodeNamePair(VersionControlSystem.SCM_PVCS, VersionControlSystem.NAME_SCM_PVCS);
    super.addCodeNamePair(VersionControlSystem.SCM_REFERENCE, VersionControlSystem.NAME_SCM_REFERENCE);
    super.addCodeNamePair(VersionControlSystem.SCM_STARTEAM, VersionControlSystem.NAME_SCM_STARTEAM);
    super.addCodeNamePair(VersionControlSystem.SCM_SURROUND, VersionControlSystem.NAME_SCM_SURROUND);
    super.addCodeNamePair(VersionControlSystem.SCM_SVN, VersionControlSystem.NAME_SCM_SVN);
    super.addCodeNamePair(VersionControlSystem.SCM_VAULT, VersionControlSystem.NAME_SCM_VAULT);
    super.addCodeNamePair(VersionControlSystem.SCM_VSS, VersionControlSystem.NAME_SCM_VSS);

    super.setCode(VersionControlSystem.SCM_CLEARCASE);
  }
}
