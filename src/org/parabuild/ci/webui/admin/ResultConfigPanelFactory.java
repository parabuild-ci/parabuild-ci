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

/**
 * Factory class to create result config panels.
 */
public final class ResultConfigPanelFactory {

  /**
   * Constructor. This constructor is private to forbid instantiation
   * ResultConfigPanelFactory.
   *
   * @see #makeResultConfigPanel
   */
  private ResultConfigPanelFactory() {
  }


  /**
   * Creates an instance impelemntion abstract result config panel
   * according to the type of result presented in resultConfig.
   *
   * @see AbstractResultConfigPanel
   */
  public static AbstractResultConfigPanel makeResultConfigPanel(final int resultType) {
    switch (resultType) {
      case ResultConfig.RESULT_TYPE_DIR:
        return new DirResultConfigPanel();
      case ResultConfig.RESULT_TYPE_FILE_LIST:
        return new FileListResultConfigPanel();
      case ResultConfig.RESULT_TYPE_URL:
        return new URLResultConfigPanel();
      default:
        throw new IllegalArgumentException("Unknown result type code: " + resultType);
    }
  }
}
