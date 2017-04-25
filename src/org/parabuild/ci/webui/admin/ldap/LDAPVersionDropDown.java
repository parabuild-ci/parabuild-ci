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

import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * Lists LDAP version.
 */
final class LDAPVersionDropDown extends CodeNameDropDown {


  public LDAPVersionDropDown() {
    addCodeNamePair(ConfigurationConstants.LDAP_VERSION_DEFAULT, "Default");
    addCodeNamePair(ConfigurationConstants.LDAP_VERSION_TWO, "2");
    addCodeNamePair(ConfigurationConstants.LDAP_VERSION_THREE, "3");
    setCode(ConfigurationConstants.LDAP_VERSION_DEFAULT);
  }
}
