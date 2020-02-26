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

import org.parabuild.ci.common.VCSAttribute;
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

    super.addCodeNamePair(VCSAttribute.SCM_ACCUREV, VCSAttribute.NAME_SCM_ACCUREV);
    super.addCodeNamePair(VCSAttribute.SCM_CLEARCASE, VCSAttribute.NAME_SCM_CLEARCASE);
    super.addCodeNamePair(VCSAttribute.SCM_BAZAAR, VCSAttribute.NAME_BAZAAR);
    super.addCodeNamePair(VCSAttribute.SCM_CVS, VCSAttribute.NAME_SCM_CVS);
    super.addCodeNamePair(VCSAttribute.SCM_FILESYSTEM, VCSAttribute.NAME_SCM_FILESYSTEM);
    super.addCodeNamePair(VCSAttribute.SCM_GENERIC, VCSAttribute.NAME_SCM_GENERIC);
    super.addCodeNamePair(VCSAttribute.SCM_GIT, VCSAttribute.NAME_SCM_GIT);
    super.addCodeNamePair(VCSAttribute.SCM_MERCURIAL, VCSAttribute.NAME_SCM_MERCURIAL);
    super.addCodeNamePair(VCSAttribute.SCM_MKS, VCSAttribute.NAME_SCM_MKS);
    super.addCodeNamePair(VCSAttribute.SCM_PERFORCE, VCSAttribute.NAME_SCM_PERFORCE);
    super.addCodeNamePair(VCSAttribute.SCM_PVCS, VCSAttribute.NAME_SCM_PVCS);
    super.addCodeNamePair(VCSAttribute.SCM_REFERENCE, VCSAttribute.NAME_SCM_REFERENCE);
    super.addCodeNamePair(VCSAttribute.SCM_STARTEAM, VCSAttribute.NAME_SCM_STARTEAM);
    super.addCodeNamePair(VCSAttribute.SCM_SURROUND, VCSAttribute.NAME_SCM_SURROUND);
    super.addCodeNamePair(VCSAttribute.SCM_SVN, VCSAttribute.NAME_SCM_SVN);
    super.addCodeNamePair(VCSAttribute.SCM_VAULT, VCSAttribute.NAME_SCM_VAULT);
    super.addCodeNamePair(VCSAttribute.SCM_VSS, VCSAttribute.NAME_SCM_VSS);

    super.setCode(VCSAttribute.SCM_CLEARCASE);
  }
}
