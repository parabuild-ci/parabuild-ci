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

import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * Version control dropdown contains a list of build VCS types
 */
public final class VersionControlDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 6932472851129032468L; // NOPMD

  public static final String NAME_SCM_ACCUREV = "AccuRev";
  private static final String NAME_BAZAAR = "Bazaar";
  public static final String NAME_SCM_CLEARCASE = "ClearCase";
  public static final String NAME_SCM_CVS = "CVS";
  public static final String NAME_SCM_FILESYSTEM = "File system VCS";
  public static final String NAME_SCM_GENERIC = "Generic VCS";
  public static final String NAME_SCM_GIT = "Git";
  public static final String NAME_SCM_MERCURIAL = "Mercurial";
  public static final String NAME_SCM_MKS = "MKS Source Integrity";
  public static final String NAME_SCM_PERFORCE = "Perforce";
  public static final String NAME_SCM_PVCS = "PVCS";
  public static final String NAME_SCM_REFERENCE = "Build reference";
  public static final String NAME_SCM_STARTEAM = "StarTeam";
  public static final String NAME_SCM_SURROUND = "Surround SCM";
  public static final String NAME_SCM_SVN = "Subversion";
  public static final String NAME_SCM_VAULT = "Vault";
  public static final String NAME_SCM_VSS = "Visual SourceSafe";


  /**
   * Constructor
   */
  public VersionControlDropDown() {
    super.addCodeNamePair(BuildConfig.SCM_ACCUREV, NAME_SCM_ACCUREV);
    super.addCodeNamePair(BuildConfig.SCM_CLEARCASE, NAME_SCM_CLEARCASE);
    super.addCodeNamePair(BuildConfig.SCM_BAZAAR, NAME_BAZAAR);
    super.addCodeNamePair(BuildConfig.SCM_CVS, NAME_SCM_CVS);
    super.addCodeNamePair(BuildConfig.SCM_FILESYSTEM, NAME_SCM_FILESYSTEM);
    super.addCodeNamePair(BuildConfig.SCM_GENERIC, NAME_SCM_GENERIC);
    super.addCodeNamePair(BuildConfig.SCM_GIT, NAME_SCM_GIT);
    super.addCodeNamePair(BuildConfig.SCM_MERCURIAL, NAME_SCM_MERCURIAL);
    super.addCodeNamePair(BuildConfig.SCM_MKS, NAME_SCM_MKS);
    super.addCodeNamePair(BuildConfig.SCM_PERFORCE, NAME_SCM_PERFORCE);
    super.addCodeNamePair(BuildConfig.SCM_PVCS, NAME_SCM_PVCS);
    super.addCodeNamePair(BuildConfig.SCM_REFERENCE, NAME_SCM_REFERENCE);
    super.addCodeNamePair(BuildConfig.SCM_STARTEAM, NAME_SCM_STARTEAM);
    super.addCodeNamePair(BuildConfig.SCM_SURROUND, NAME_SCM_SURROUND);
    super.addCodeNamePair(BuildConfig.SCM_SVN, NAME_SCM_SVN);
    super.addCodeNamePair(BuildConfig.SCM_VAULT, NAME_SCM_VAULT);
    super.addCodeNamePair(BuildConfig.SCM_VSS, NAME_SCM_VSS);
    super.setCode(BuildConfig.SCM_CLEARCASE);
  }
}
