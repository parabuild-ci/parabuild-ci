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
package org.parabuild.ci.webui.common;

import java.util.*;

/**
 * Class to show "Show/hide files link"
 */
public class ShowHideFilesCommandLink extends CommonCommandLink {

    public static final String CAPTION_HIDE_FILES = "Hide Files";
    public static final String CAPTION_SHOW_FILES = "Show Files";


    public ShowHideFilesCommandLink(final boolean show, final String targetPage, final Properties parameters) {
      super(makeCaption(show), targetPage, makeParamters(show, parameters));
    }


    private static Properties makeParamters(final boolean show, final Properties properties) {
      final Properties result = new Properties(properties);
      result.setProperty(Pages.PARAM_SHOW_FILES, Boolean.toString(!show));
      return result;
    }


    private static String makeCaption(final boolean show) {
      return show ? CAPTION_HIDE_FILES : CAPTION_SHOW_FILES;
    }
  }


