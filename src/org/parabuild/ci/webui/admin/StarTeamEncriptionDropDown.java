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

import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;

/**
 * Shows selection of clear case text modes (-tmode) used to
 * create a view.
 */
public final class StarTeamEncriptionDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 4662209824080566402L; // NOPMD


  public StarTeamEncriptionDropDown() {
    addCodeNamePair(SourceControlSetting.STARTEAM_ENCRYPTION_NO_ENCRYPTION, "No encryption");
    addCodeNamePair(SourceControlSetting.STARTEAM_ENCRYPTION_RSA_R4_STREAM_CIPHER, "RSA R4 stream cipher");
    addCodeNamePair(SourceControlSetting.STARTEAM_ENCRYPTION_RSA_R2_BLOCK_CIPHER_ECB, "RSA R2 block cipher (Electronic Codebook)");
    addCodeNamePair(SourceControlSetting.STARTEAM_ENCRYPTION_RSA_R2_BLOCK_CIPHER_CF, "RSA R2 block cipher (Cipher Block Chaining)");
    addCodeNamePair(SourceControlSetting.STARTEAM_ENCRYPTION_RSA_R2_BLOCK_CIPHER_CF, "RSA R2 block cipher (Cipher Feedback)");
    setCode(SourceControlSetting.STARTEAM_ENCRYPTION_NO_ENCRYPTION);
  }
}

