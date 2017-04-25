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
package org.parabuild.ci.promotion;

import java.io.*;

/**
 * A value object to display list of promotion objects.
 */
public final class PromotionVO implements Serializable {

  private static final long serialVersionUID = -5541727371187416179L;

  private final String projectName;
  private final String promotionName;
  private final int promotionID;
  private final String description;


  /**
   * Creates object.
   *
   * @param projectName
   * @param promotionName
   * @param promotionID
   */
  public PromotionVO(final String projectName, final String promotionName, final Integer promotionID, final String description) {
    this.projectName = projectName;
    this.promotionName = promotionName;
    this.promotionID = promotionID.intValue();
    this.description = description;
  }


  public String getProjectName() {
    return projectName;
  }


  public String getPromotionName() {
    return promotionName;
  }


  public int getPromotionID() {
    return promotionID;
  }


  public String getDescription() {
    return description;
  }


  public String toString() {
    return "PromotionVO{" +
      "projectName='" + projectName + '\'' +
      ", promotionName='" + promotionName + '\'' +
      ", promotionID=" + promotionID +
      ", description='" + description + '\'' +
      '}';
  }
}
