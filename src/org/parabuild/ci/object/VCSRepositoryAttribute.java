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
 * Stored build properties
 *
 * @hibernate.class table="VCS_REPOSITORY_ATTRIBUTE" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 * @noinspection StaticInheritance
 */
public final class VCSRepositoryAttribute implements Serializable, ObjectConstants {

  private int repositoryId = BuildConfig.UNSAVED_ID;

  private int id = UNSAVED_ID;

  private String name;

  private String value;

  private long timeStamp = 1;


  /**
   * Default constructor. Required by hibernate.
   */
  public VCSRepositoryAttribute() {

  }


  /**
   * Constructor.
   *
   * @param repositoryId
   * @param name
   */
  public VCSRepositoryAttribute(final int repositoryId, final String name, final String value) {

    this.repositoryId = repositoryId;
    this.name = name;
    this.value = value;
  }


  /**
   * Constructor.
   *
   * @param repositoryId
   * @param name
   * @param value
   */
  public VCSRepositoryAttribute(final int repositoryId, final String name, final int value) {

    this(repositoryId, name, Integer.toString(value));
  }


  /**
   * Returns build ID
   *
   * @return String
   * @hibernate.property column="VCS_REPOSITORY_ID" unique="false" null="false"
   */
  public int getRepositoryId() {

    return repositoryId;
  }


  public void setRepositoryId(final int repositoryId) {

    this.repositoryId = repositoryId;
  }


  /**
   * The getter method for this property ID generator-parameter-1="SEQUENCE_GENERATOR"
   * generator-parameter-2="SEQUENCE_ID"
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID"
   * unsaved-value="-1"
   */
  public int getId() {

    return id;
  }


  public void setId(final int id) {

    this.id = id;
  }


  /**
   * Returns property name
   *
   * @return String
   * @hibernate.property column="NAME" unique="true" null="false"
   */
  public String getName() {

    return name;
  }


  public void setName(final String name) {

    this.name = name;
  }


  /**
   * Returns property value
   *
   * @return String
   * @hibernate.property column="VALUE" unique="true" null="false"
   */
  public String getValue() {

    return value;
  }


  public void setValue(final String value) {

    this.value = value;
  }


  public void setValue(final int value) {

    this.value = Integer.toString(value);
  }


  /**
   * Returns property value as int
   *
   * @return int property value
   * @throws NumberFormatException if there was an error converting {@link #value} to integer.
   */
  public int getValueAsInteger() {

    return Integer.parseInt(value);
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


  @Override
  public String toString() {

    return "VCSServerAttribute{" +
            "serverId=" + repositoryId +
            ", id=" + id +
            ", name='" + name + '\'' +
            ", value='" + value + '\'' +
            ", timeStamp=" + timeStamp +
            '}';
  }
}
