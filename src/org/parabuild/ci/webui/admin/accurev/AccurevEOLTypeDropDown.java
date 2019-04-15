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
package org.parabuild.ci.webui.admin.accurev;

import org.parabuild.ci.webui.common.CodeNameDropDown;
import org.parabuild.ci.object.SourceControlSetting;

/**
 * AccurevEOLTypeDropDown
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 13, 2009 11:49:20 AM
 */
final class AccurevEOLTypeDropDown extends CodeNameDropDown {

  private static final String CAPTION_PLATFORM_SPECIFIC = "Platform-specific";
  private static final String CAPTION_UNIX = "Unix";
  private static final String CAPTION_WINDOWS = "Windows";
  private static final long serialVersionUID = -6364193342135120087L;


  AccurevEOLTypeDropDown() {
    setName("accurev-eol-type");
    addCodeNamePair((int) SourceControlSetting.ACCUREV_EOL_PLATFORM, CAPTION_PLATFORM_SPECIFIC);
    addCodeNamePair((int) SourceControlSetting.ACCUREV_EOL_UNIX, CAPTION_UNIX);
    addCodeNamePair((int) SourceControlSetting.ACCUREV_EOL_WINDOWS, CAPTION_WINDOWS);
  }
}
