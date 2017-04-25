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

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.versioncontrol.accurev.AccurevVersionControl;
import org.parabuild.ci.versioncontrol.bazaar.BazaarVersionControl;
import org.parabuild.ci.versioncontrol.git.GitSourceControl;
import org.parabuild.ci.versioncontrol.mercurial.MercurialVersionControl;
import org.parabuild.ci.versioncontrol.mks.MKSSourceControl;
import org.parabuild.ci.versioncontrol.perforce.P4SourceControl;

/**
 * VersionControlFactory impements a Factory pattern interface and
 * serves as an abstraction layer that allows the build process
 * to use different version control system uniformly.
 */
public final class VersionControlFactory {

  /**
   * Private constructor to disable instantiation of the
   * factory.
   */
  private VersionControlFactory() {
  }


  /**
   * Creates a new instance of SourceControl based on the given
   * build config.
   *
   * @param buildConfig - build config to use to create a
   *                    SourceControl for. BuildConfig can be either BuildRunConfig
   *                    or ActiveBuildConfig. ActiveBuildConfig is passed by
   *                    automatic schedulers that whatch source line for changes.
   *                    BuildRunConfig is passed by build runners to run a build
   *                    against the given build configuration.
   */
  public static SourceControl makeVersionControl(final BuildConfig buildConfig) {
    final byte scmCode = buildConfig.getSourceControl();
    if (scmCode == BuildConfig.SCM_REFERENCE) {
      return makeReferenceSourceControl(buildConfig);
    } else {
      return makeDirectSourceControl(scmCode, buildConfig);
    }
  }


  /**
   * Creates a reference source control.
   */
  private static SourceControl makeReferenceSourceControl(final BuildConfig originalBuildConfig) {
    // referred SCM ID from reference build config
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final byte effectiveSCMCode = cm.getEffectiveBuildConfig(originalBuildConfig).getSourceControl();
    // make direct
    final SourceControl referredSourceControl = makeDirectSourceControl(effectiveSCMCode, originalBuildConfig);
    return new ReferenceSourceControl(originalBuildConfig, referredSourceControl);
  }


  /**
   * This method is ised to create vendor, or staight, version
   * controls
   *
   * @param scmID       ID which is used to identify which SCM to
   *                    create
   * @param buildConfig is passed to an SCM constructor.
   *                    buildConfig can be vendor and reference.
   */
  private static SourceControl makeDirectSourceControl(final byte scmID, final BuildConfig buildConfig) {
    if (scmID == BuildConfig.SCM_CVS) {
      return new CVSSourceControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_ACCUREV) {
      return new AccurevVersionControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_BAZAAR) {
      return new BazaarVersionControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_MERCURIAL) {
      return new MercurialVersionControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_PERFORCE) {
      return new P4SourceControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_VSS) {
      return new VSSSourceControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_SVN) {
      return new SVNSourceControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_CLEARCASE) {
      return new ClearCaseSourceControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_SURROUND) {
      return new SurroundSourceControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_VAULT) {
      return new VaultSourceControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_PVCS) {
      return new PVCSSourceControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_STARTEAM) {
      return new StarTeamSourceControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_MKS) {
      return new MKSSourceControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_FILESYSTEM) {
      return new FileSystemSourceControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_GENERIC) {
      return new GenericSourceControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_GIT) {
      return new GitSourceControl(buildConfig);
    }
    if (scmID == BuildConfig.SCM_SYNERGY) {
      return new SynergySourceControl(buildConfig);
    }
    throw new IllegalArgumentException("Unexpected version control ID: " + scmID);
  }
}
