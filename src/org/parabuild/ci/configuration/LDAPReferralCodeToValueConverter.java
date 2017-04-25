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
package org.parabuild.ci.configuration;

/**
 */
public final class LDAPReferralCodeToValueConverter {

  public String convert(final byte code) {
    if (code == ConfigurationConstants.LDAP_REFERRAL_DEFAULT) return null;
    if (code == ConfigurationConstants.LDAP_REFERRAL_FOLLOW) return ConfigurationConstants.LDAP_REFERRAL_FOLLOW_VALUE;
    if (code == ConfigurationConstants.LDAP_REFERRAL_IGNORE) return ConfigurationConstants.LDAP_REFERRAL_IGNORE_VALUE;
    if (code == ConfigurationConstants.LDAP_REFERRAL_THROW) return ConfigurationConstants.LDAP_REFERRAL_THROW_VALUE;
    return null;
  }
}
