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
package org.parabuild.ci.webui.admin.ldap;

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.webui.common.*;

/**
 * Lists credential digest options.
 */
public final class CredentialDigestDropDown extends CodeNameDropDown {


  private static final long serialVersionUID = 6114223502110362682L;


  public CredentialDigestDropDown() {
    addCodeNamePair(ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_NOT_SELECTED, ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_NOT_SELECTED_VALUE);
    addCodeNamePair(ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_MD2, ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_MD2_VALUE);
    addCodeNamePair(ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_MD5, ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_MD5_VALUE);
    addCodeNamePair(ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_SHA1, ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_SHA1_VALUE);
    setCode(ConfigurationConstants.LDAP_CREDENTIAL_DIGEST_NOT_SELECTED);
  }
}
