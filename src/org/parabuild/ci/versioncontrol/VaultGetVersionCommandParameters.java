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

/**
 * Parameters for Vault get version command.
 */
final class VaultGetVersionCommandParameters extends VaultCommandParameters {

  private boolean makeWriteable = false;
  private String repositoryPath = null;
  private int version = -1;

  public boolean isMakeWriteable() {
    return makeWriteable;
  }


  public void setMakeWriteable(final boolean makeWriteable) {
    this.makeWriteable = makeWriteable;
  }


  public String getRepositoryPath() {
    return repositoryPath;
  }


  public void setRepositoryPath(final String repositoryPath) {
    this.repositoryPath = repositoryPath;
  }


  public int getVersion() {
    return version;
  }


  public void setVersion(final int version) {
    this.version = version;
  }


  public String toString() {
    return "VaultGetCommandParameters{" +
      super.toString() +
      "repositoryPath='" + repositoryPath + '\'' +
      ", makeWriteable=" + makeWriteable +
      ", version=" + version +
      '}';
  }
}

/*
This is a list of possible options:
  -destpath localfolder
      Use localfolder for actions instead of any existing working folder
  -makewritable
      Make all files writable after retrieval
  -makereadonly
      Make all files read-only after retrieval
  -merge [automatic|later|overwrite]
      The action to take when updating a local file with new content.

      automatic*        - attempt to merge changes from the server
      later             - do not overwrite an existing, modified file
      overwrite         - overwrite the local file with the server's file

      * - only applies to GET and GETWILDCARD commands

  -nocloaks
      Performs actions on all folders even if they were previously cloaked
  -norecursive
      Do not act recursively on folders
  -performdeletions [donotremoveworkingcopy|removeworkingcopy|removeworkingcopyifunmodified]
      When getting a folder, this option controls whether files deleted in the
      repository are deleted in the working folder.  The default is
      donotremoveworkingcopy.  This option only applies to GET, GETWILDCARD
      and CHECKOUT commands
  -setfiletime checkin|current|modification
      Sets the time of the local file.

      checkin           - use the last checkin time
      current           - use the current system time
      modification      - use the file's last modified time
  -verbose
      Turn on verbose mode
*/