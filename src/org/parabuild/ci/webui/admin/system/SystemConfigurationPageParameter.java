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
package org.parabuild.ci.webui.admin.system;

import java.util.Properties;

/**
 * SystemConfigurationPageParameter hold system configuration page parameters.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Sep 27, 2008 3:57:02 PM
 */
final class SystemConfigurationPageParameter {

  public static final String MODE = "mode";
  public static final String MODE_VALUE_PREVIEW = "preview";
  public static final String MODE_VALUE_EDIT = "edit";
  public static final String EDIT_CONFIGURATION = "edit_configuration";

  private SystemConfigurationPageParameter() {
  }

  public static Properties createPreviewParameters() {
    final Properties parameters = new Properties();
    parameters.setProperty(MODE, MODE_VALUE_PREVIEW);
    return parameters;
  }
}
