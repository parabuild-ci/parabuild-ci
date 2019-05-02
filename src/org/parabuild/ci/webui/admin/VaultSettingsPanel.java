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

import org.parabuild.ci.common.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.versioncontrol.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 *
 */
public final class VaultSettingsPanel extends AbstractSourceControlPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  private static final String CAPTION_HOST = "Vault server:";
  private static final String CAPTION_PASSWORD = "Vault password:";
  private static final String CAPTION_PROXY_DOMAIN = "Proxy domain:";
  private static final String CAPTION_PROXY_PASSWORD = "Proxy password:";
  private static final String CAPTION_PROXY_PORT = "Proxy port:";
  private static final String CAPTION_PROXY_SERVER = "Proxy server:";
  private static final String CAPTION_PROXY_USER = "Proxy user:";
  private static final String CAPTION_REPOSITORY = "Vault repository:";
  private static final String CAPTION_REPOSITORY_PATH = "Repository path:";
  private static final String CAPTION_USE_SSL = "Connect using SSL:";
  private static final String CAPTION_USER = "Vault username:";
//  public static final String CAPTION_PATH_TO_EXE = "Path to Vault.exe:";


//  private final Label lbPathToExe = new CommonFieldLabel(CAPTION_PATH_TO_EXE);

  private final Field flProxyDomain = new CommonField(100, 50);
  private final EncryptingPassword flProxyPassword = new EncryptingPassword(30, 20, "vault_proxy_password");
  private final Field flProxyPort = new CommonField(5, 5);
  private final Field flProxyServer = new CommonField(100, 50);
  private final Field flProxyUser = new CommonField(30, 30);
//  private final Field flPathToExe = new Field(200, 50);
  private final Field flHost = new Field(100, 50);
  private final Field flUser = new Field(30, 30);
  private final EncryptingPassword flPassword = new EncryptingPassword(30, 20, "vault_password");
  private final Field flRepository = new Field(100, 50);
  private final CheckBox flUseSSL = new CheckBox();
  private final Text flRepositoryPath = new Text(100, 5);


  /**
   * Creates Vault setting panel.
   */
  public VaultSettingsPanel() {
    super("Vault Settings");
    // layout
//    gridIterator.addPair(lbPathToExe, new RequiredFieldMarker(flPathToExe));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_HOST), new RequiredFieldMarker(flHost));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_USE_SSL), flUseSSL);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_USER), new RequiredFieldMarker(flUser));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PASSWORD), flPassword);
    gridIterator.addBlankLine();
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PROXY_SERVER), flProxyServer);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PROXY_PORT), flProxyPort);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PROXY_USER), flProxyUser);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PROXY_PASSWORD), flProxyPassword);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PROXY_DOMAIN), flProxyDomain);
    gridIterator.addBlankLine();
    gridIterator.addPair(new CommonFieldLabel(CAPTION_REPOSITORY), new RequiredFieldMarker(flRepository));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_REPOSITORY_PATH), flRepositoryPath);

    // init property to input map
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VAULT_PASSWORD, flPassword);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VAULT_USER, flUser);
//    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VAULT_EXE, flPathToExe);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VAULT_HOST, flHost);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VAULT_REPOSITORY, flRepository);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VAULT_USE_SSL, flUseSSL);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VAULT_PROXY_DOMAIN, flProxyDomain);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VAULT_PROXY_PASSWORD, flProxyPassword);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VAULT_PROXY_PORT, flProxyPort);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VAULT_PROXY_SERVER, flProxyServer);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VAULT_PROXY_USER, flProxyUser);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VAULT_REPOSITORY_PATH, flRepositoryPath);

    // add footer
    addCommonAttributes();
  }


  /**
   * Sets edit mode
   *
   * @param mode
   */
  protected final void doSetMode(final int mode) {
    if (mode == WebUIConstants.MODE_VIEW) {
      setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      setEditable(true);
    } else if (mode == WebUIConstants.MODE_INHERITED) {
      setEditable(false);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    flHost.setEditable(editable);
    flPassword.setEditable(editable);
    flUser.setEditable(editable);
    flRepository.setEditable(editable);
    flUseSSL.setEditable(editable);
    flProxyDomain.setEditable(editable);
    flProxyPassword.setEditable(editable);
    flProxyPort.setEditable(editable);
    flProxyServer.setEditable(editable);
    flProxyUser.setEditable(editable);
    flRepositoryPath.setEditable(editable);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  protected final boolean doValidate() {
    clearMessage();
    final ArrayList errors = new ArrayList(11);
//    WebuiUtils.validateFieldNotBlank(errors, CAPTION_PATH_TO_EXE, flPathToExe);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_REPOSITORY, flRepository);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_HOST, flHost);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_USER, flUser);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_REPOSITORY_PATH, flRepositoryPath);

    if (errors.isEmpty()) {
      // validate svn executable exists if there were no other errors
//      super.validateCommandExists(flPathToExe.getValue(), errors,
//        "Path to vault executable is invalid, or sscm executable is not accessible");

      // further validate Vault repository path
      try {
        new VaultDepotPathParser().validate(flRepositoryPath.getValue());
      } catch (final ValidationException e) {
        errors.add(StringUtils.toString(e));
      }
    }

    // show errors
    if (errors.isEmpty()) return true;
    showErrorMessage(errors);
    return false;
  }


//  /**
//   * Returns path to SVN exe.
//   */
//  public String getPathToSSCMExe() {
//    return flPathToExe.getValue();
//  }
}
