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
 * Parameters for Vault LABEL command.
 */
public final class VaultLabelCommandParameters extends VaultCommandParameters {

  private String repositoryPath = null;
  private int version = -1;
  private String label = null;


  public int getVersion() {
    return version;
  }


  public void setVersion(final int version) {
    this.version = version;
  }


  public String getLabel() {
    return label;
  }


  public void setLabel(final String label) {
    this.label = label;
  }


  public String getRepositoryPath() {
    return repositoryPath;
  }


  public void setRepositoryPath(final String repositoryPath) {
    this.repositoryPath = repositoryPath;
  }


  public String toString() {
    return "VaultGetCommandParameters{" +
      super.toString() +
      "repositoryPath='" + repositoryPath + '\'' +
      ", label=" + label +
      ", version=" + version +
      '}';
  }
}

/*
D:\mor2\dev\bt>vault help label
<vault>
<usage>
SourceGear Vault Command Line Client 3.1.6.3619
Copyright (c) 2003-2005 SourceGear LLC. All Rights Reserved.

usage: vault.exe LABEL [options] repositorypath labelname [version]

Applies label to version of repositorypath, which can
be used later for GETLABEL requests.  If no version
is specified, the current version is labelled.

Server and authentication information is specified by:
  -host host
      Hostname of the vault server to connect to. Can also use "-server"
  -ssl
      Enables SSL for server connection
  -user username
      Username to use when connecting to server.
  -password password
      Password to use when connecting to server
  -proxyserver proxyserver
      Server name or url for the proxy to use when connecting.
  -proxyport proxyport
      Port to use to connect to the proxy.
  -proxyuser proxyuser
      Username for proxy authentication.
  -proxypassword proxypassword
      Password for proxy authentication.
  -proxydomain proxydomain
      Domain for proxy authentication.
  -repository repositoryname
      Repository to connect to

This is a list of possible options:
  -comment commentstring
      Checkin comment
</usage>
<result success="yes" />
</vault>
*/