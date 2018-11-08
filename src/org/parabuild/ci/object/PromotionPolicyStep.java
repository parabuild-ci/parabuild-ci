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
 * @hibernate.class table="PROMOTION_STEP" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class PromotionPolicyStep implements Serializable, ObjectConstants {

  private static final long serialVersionUID = 216142447373206391L; // NOPMD

  private int ID = UNSAVED_ID;
  private int promotionID = PromotionPolicy.UNSAVED_ID;
  private String name = null;
  private String description = null;
  private int lineNumber = 0;
  private boolean deleted = false;
  private long timeStamp = 0;


  public PromotionPolicyStep() {
  }


  public PromotionPolicyStep(final Integer ID, final String name, final String description) {
    this.ID = ID;
    this.name = name;
    this.description = description;
  }


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
   * @hibernate.property column = "PROMOTION_POLICY_ID" unique="true"
   * null="false"
   */
  public int getPromotionID() {
    return promotionID;
  }


  public void setPromotionID(final int promotionID) {
    this.promotionID = promotionID;
  }


  /**
   * @hibernate.property column = "NAME" unique="true"
   * null="false"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @hibernate.property column = "DESCRIPTION" unique="false"
   * null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @hibernate.property column = "LINE_NUMBER" unique="false"
   * null="false"
   */
  public int getLineNumber() {
    return lineNumber;
  }


  public void setLineNumber(final int lineNumber) {
    this.lineNumber = lineNumber;
  }


  /**
   * @hibernate.property column="DELETED"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isDeleted() {
    return deleted;
  }


  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }


  /**
   * Returns timestamp
   *
   * @return long
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  public String toString() {
    return "PromotionPolicyStep{" +
            "ID=" + ID +
            ", promotionID=" + promotionID +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", lineNumber=" + lineNumber +
            ", deleted=" + deleted +
            ", timeStamp=" + timeStamp +
            '}';
  }
}
