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
package org.parabuild.ci.webui.admin.mercurial;

import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * A drop down box to show repository path options for Mercurial.
 */
public class MercurialRepositoryPathDropDown extends CodeNameDropDown {


  private static final long serialVersionUID = -3431016032527981184L;


  public MercurialRepositoryPathDropDown() {
    super(false);
    addCodeNamePair(SourceControlSetting.MERCURIAL_PATH_TYPE_FILE, "File path");
    addCodeNamePair(SourceControlSetting.MERCURIAL_PATH_TYPE_SSH, "SSH");
    addCodeNamePair(SourceControlSetting.MERCURIAL_PATH_TYPE_URL, "URL");
    setCode(SourceControlSetting.MERCURIAL_PATH_TYPE_URL);
  }
}
