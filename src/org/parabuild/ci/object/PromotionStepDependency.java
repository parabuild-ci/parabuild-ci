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
package org.parabuild.ci.object;

import java.io.Serializable;

/**
 * @hibernate.class table="PROMOTION_STEP_DEPENDENCY" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class PromotionStepDependency implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 216142447373206391L; // NOPMD

  private int ID = UNSAVED_ID;
  private int promotionStepID = PromotionPolicyStep.UNSAVED_ID;
  private int dependencyPromotionStepID = PromotionPolicyStep.UNSAVED_ID;


  /**
   * @hibernate.id generator-class="identity" column="ID"
   * unsaved-value="-1"
   */
  public int getID() {
    return ID;
  }


  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * @hibernate.property column = "PROMOTION_STEP_ID" unique="false"
   * null="false"
   */
  public int getPromotionStepID() {
    return promotionStepID;
  }


  public void setPromotionStepID(final int promotionStepID) {
    this.promotionStepID = promotionStepID;
  }


  /**
   * @hibernate.property column = "DEPENDENCY_PROMOTION_STEP_ID" unique="false"
   * null="false"
   */
  public int getDependencyPromotionStepID() {
    return dependencyPromotionStepID;
  }


  public void setDependencyPromotionStepID(final int dependencyPromotionStepID) {
    this.dependencyPromotionStepID = dependencyPromotionStepID;
  }
}
