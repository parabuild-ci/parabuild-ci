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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.services.BuildStartRequestParameter;

import java.io.Serializable;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Manual build run parameter
 *
 * @hibernate.class table="MANUAL_RUN_PARAMETER" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 * @see BuildStartRequestParameter
 */
public final class StartParameter implements Serializable, ObjectConstants {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(StartParameter.class); //NOPMD

  private static final long serialVersionUID = 1202447165126030920L; // NOPMD

  public static final byte PRESENTATION_CHECK_LIST = (byte) 0;
  public static final byte PRESENTATION_RADIO_LIST = (byte) 1;
  public static final byte PRESENTATION_DROPDOWN_LIST = (byte) 2;
  public static final byte PRESENTATION_SINGLE_VALUE = (byte) 3;

  public static final byte TYPE_BUILD = StartParameterType.BUILD.byteValue();
  public static final byte TYPE_PUBLISH = StartParameterType.PUBLISH.byteValue();
  public static final byte TYPE_SYSTEM = StartParameterType.SYSTEM.byteValue();
  public static final byte TYPE_PROJECT = StartParameterType.PROJECT.byteValue();
  public static final byte TYPE_AGENT = StartParameterType.AGENT.byteValue();

  private boolean enabled = true;
  private boolean required = false;
  private boolean modifiable = false;
  private byte presentation = PRESENTATION_CHECK_LIST;
  private byte type = TYPE_BUILD;
  private int buildID = BuildConfig.UNSAVED_ID;
  private int ID = UNSAVED_ID;
  private long timeStamp = 1L;
  private String description = null;
  private String name = null;
  private String runtimeValue = null;
  private String value = null;
  private int order;


  /**
   * Default constructor required by hibernate.
   */
  public StartParameter() {
  }


  public StartParameter(final int buildID, final byte type, final byte presentation, final String name,
                        final String value, final String description, final long timeStamp,
                        final boolean enabled) {
    this.buildID = buildID;
    this.description = description;
    this.enabled = enabled;
    this.name = name;
    this.presentation = presentation;
    this.timeStamp = timeStamp;
    this.type = type;
    this.value = value;
  }


  /**
   * Returns build ID
   *
   * @hibernate.property column="BUILD_ID" unique="false" null="false"
   */
  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * The getter method for this property ID.
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID" unsaved-value="-1"
   */
  public int getID() {
    return ID;
  }


  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * @hibernate.property column="NAME" unique="true" null="false"
   */
  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Returns build name
   *
   * @hibernate.property column="ENABLED" unique="false" type="yes_no" null="false"
   */
  public boolean isEnabled() {
    return enabled;
  }


  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }


  /**
   * Returns possible values
   *
   * @hibernate.property column="VALUE" unique="false" null="true"
   */
  public String getValue() {
    return value;
  }


  public void setValue(final String value) {
    this.value = value;
  }


  public void setValue(final List values) {
    this.value = valueListToString(values).toString();
  }


  /**
   * Returns possible values
   *
   * @hibernate.property column="RUNTIME_VALUE" unique="false" null="true"
   */
  public String getRuntimeValue() {
    return runtimeValue;
  }


  public void setRuntimeValue(final String runtimeValue) {
    this.runtimeValue = runtimeValue;
  }


  /**
   * Returns presentation type
   *
   * @hibernate.property column="PRESENTATION" unique="false" null="false"
   */
  public byte getPresentation() {
    return presentation;
  }


  public void setPresentation(final byte presentation) {
    this.presentation = presentation;
  }


  /**
   * Returns parameter type
   *
   * @hibernate.property column="TYPE" unique="false" null="false"
   * @see StartParameterType#BUILD
   * @see StartParameterType#PUBLISH
   */
  public byte getType() {
    return type;
  }


  public void setType(final byte type) {
    this.type = type;
  }


  /**
   * Returns description
   *
   * @hibernate.property column="DESCRIPTION" unique="false" null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Returns required
   *
   * @hibernate.property column="REQUIRED" unique="false" type="yes_no" null="false"
   */
  public boolean isRequired() {
    return required;
  }


  public void setRequired(final boolean required) {
    this.required = required;
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


  /**
   * Returns true if this parameter can be modified at startup.
   *
   * @return true if this parameter can be modified at startup.
   * @hibernate.property column="MODIFIABLE" unique="false" type="yes_no" null="false"
   */
  public boolean isModifiable() {
    return modifiable;
  }


  public void setModifiable(final boolean modifiable) {
    this.modifiable = modifiable;
  }

  /**
   * Returns true if this parameter can be modified at startup.
   *
   * @return true if this parameter can be modified at startup.
   * @hibernate.property column="DISPLAY_ORDER" unique="false" null="false"
   */
  public int getOrder() {
    return order;
  }


  public void setOrder(final int order) {
    this.order = order;
  }


  /**
   * Sets runtime value as a list of strings. This method creates a
   * comma-separated list of parameters.
   *
   * @param values
   */
  public void setRuntimeValue(final List values) {
    runtimeValue = valueListToString(values).toString();
  }


  public static StringBuffer valueListToString(final List values) {
    final StringBuffer sb = new StringBuffer(100);
    final int n = values.size();
    for (int i = 0; i < n; i++) {
      sb.append((String) values.get(i));
      if (i + 1 < n) {
        sb.append(',');
      }
    }
    return sb;
  }


  /**
   * @return StringTokenizer for the give "values" paramter.
   */
  public static StringTokenizer makeTokenizer(final String values) {
    return new StringTokenizer(values, ",");
  }


  /**
   * Returns first value or null if not exists.
   *
   * @return first value or null if not exists.
   */
  public String getFirstValue() {
    final StringTokenizer st = makeTokenizer(getValue());
    if (st.hasMoreElements()) {
      return st.nextToken();
    } else {
      return null;
    }
  }


  public String toString() {
    return "StartParameter{" +
            "enabled=" + enabled +
            ", required=" + required +
            ", presentation=" + presentation +
            ", type=" + type +
            ", buildID=" + buildID +
            ", ID=" + ID +
            ", timeStamp=" + timeStamp +
            ", description='" + description + '\'' +
            ", name='" + name + '\'' +
            ", runtimeValue='" + runtimeValue + '\'' +
            ", value='" + value + '\'' +
            '}';
  }
}
