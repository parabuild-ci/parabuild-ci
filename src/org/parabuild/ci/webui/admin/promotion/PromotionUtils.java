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
package org.parabuild.ci.webui.admin.promotion;

import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.Parameters;

import java.util.Properties;

/**
 * PromotionUtils
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 9, 2009 9:20:12 PM
 */
final class PromotionUtils {


  private PromotionUtils() {
  }


  public static Properties createProperties(final int policyID) {
    final Properties properties = new Properties();
    properties.setProperty(Pages.PARAM_PROMOTION_POLICY_ID, Integer.toString(policyID));
    return properties;
  }


  public static Parameters createPromotionParameters(final int promotionID) {
    final Parameters parameters = new Parameters();
    parameters.addParameter(Pages.PARAM_PROMOTION_POLICY_ID, promotionID);
    return parameters;
  }
}
