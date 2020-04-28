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

import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * ResultTypeDropDown shows a list of result content types
 */
public final class ResultTypeDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 7210592889010278271L; // NOPMD


  public ResultTypeDropDown() {
    addCodeNamePair(ResultConfig.RESULT_TYPE_DIR, "Directory");
    addCodeNamePair(ResultConfig.RESULT_TYPE_FILE_LIST, "File list");
    addCodeNamePair(ResultConfig.RESULT_TYPE_URL, "External URL");
    setCode(ResultConfig.RESULT_TYPE_FILE_LIST);
  }
}
