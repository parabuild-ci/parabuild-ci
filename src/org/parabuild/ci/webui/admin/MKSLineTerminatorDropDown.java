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
import org.parabuild.ci.webui.common.*;

/**
 * Shows selection of MKS line terminator options used to create a sandbox.
 */
public final class MKSLineTerminatorDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 4662209824080566402L; // NOPMD


  public MKSLineTerminatorDropDown() {
    addCodeNamePair(VCSAttribute.MKS_LINE_TERMINATOR_LF, "lf");
    addCodeNamePair(VCSAttribute.MKS_LINE_TERMINATOR_CRLF, "crlf");
    addCodeNamePair(VCSAttribute.MKS_LINE_TERMINATOR_NATIVE, "native");
    setCode(VCSAttribute.MKS_LINE_TERMINATOR_NATIVE);
  }
}
