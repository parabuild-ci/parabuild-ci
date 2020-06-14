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
import java.util.Date;

/**
 * Merge is a header of a group of a branch
 * change list pending merge.
 *
 *
 * @hibernate.class table="MERGE" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public class Merge implements ObjectConstants, Serializable {

  private static final long serialVersionUID = 1828411785428441913L;

  public static final byte RESULT_NOT_MERGED = 0;
  public static final byte RESULT_CONFLICTS = 1;

  /**
   * This result is set when validating build run didn't
   * produce any results.
   */
  public static final byte RESULT_CANNOT_VALIDATE = 2;

  /**
   * This result is set when the validating build run
   * failed.
   */
  public static final byte RESULT_VALIDATION_FAILED = 3;

  private boolean validated;
  private byte resultCode = RESULT_NOT_MERGED;
  private Date created;
  private int ID = UNSAVED_ID;
  private int mergeConfigurationID = MergeConfiguration.UNSAVED_ID;


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
   * @hibernate.property column="MERGE_CONFIGURATION_ID"
   *  unique="false" null="false"
   */
  public int getMergeConfigurationID() {
    return mergeConfigurationID;
  }


  public void setMergeConfigurationID(final int mergeConfigurationID) {
    this.mergeConfigurationID = mergeConfigurationID;
  }


  /**
   * @hibernate.property column="VALIDATED" type="yes_no"
   *  unique="false" null="false"
   */
  public boolean isValidated() {
    return validated;
  }


  public void setValidated(final boolean validated) {
    this.validated = validated;
  }


  /**
   * @hibernate.property column="RESULT_CODE"
   *  unique="false" null="false"
   */
  public byte getResultCode() {
    return resultCode;
  }


  public void setResultCode(final byte resultCode) {
    this.resultCode = resultCode;
  }


  /**
   * @hibernate.property column="CREATED"
   *  unique="false" null="false"
   */
  public Date getCreated() {
    return created;
  }


  public void setCreated(final Date created) {
    this.created = created;
  }


  public String toString() {
    return "Merge{" +
      "validated=" + validated +
      ", resultCode=" + resultCode +
      ", created=" + created +
      ", ID=" + ID +
      ", mergeConfigurationID=" + mergeConfigurationID +
      '}';
  }
}
