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

package org.parabuild.ci.webservice.templ;

public class BuildRunAction implements java.io.Serializable {

  private static final long serialVersionUID = -2189213959421107080L;
  private int ID;
  private java.lang.String action;
  private int buildRunID;
  private byte code;
  private java.util.Calendar date;
  private java.lang.String description;
  private int userID;


  public BuildRunAction() {
  }


  public BuildRunAction(
          final int ID,
          final java.lang.String action,
          final int buildRunID,
          final byte code,
          final java.util.Calendar date,
          final java.lang.String description,
          final int userID) {
    this.ID = ID;
    this.action = action;
    this.buildRunID = buildRunID;
    this.code = code;
    this.date = date;
    this.description = description;
    this.userID = userID;
  }


  /**
   * Gets the ID value for this BuildRunAction.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this BuildRunAction.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the action value for this BuildRunAction.
   *
   * @return action
   */
  public java.lang.String getAction() {
    return action;
  }


  /**
   * Sets the action value for this BuildRunAction.
   *
   * @param action
   */
  public void setAction(final java.lang.String action) {
    this.action = action;
  }


  /**
   * Gets the buildRunID value for this BuildRunAction.
   *
   * @return buildRunID
   */
  public int getBuildRunID() {
    return buildRunID;
  }


  /**
   * Sets the buildRunID value for this BuildRunAction.
   *
   * @param buildRunID
   */
  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


  /**
   * Gets the code value for this BuildRunAction.
   *
   * @return code
   */
  public byte getCode() {
    return code;
  }


  /**
   * Sets the code value for this BuildRunAction.
   *
   * @param code
   */
  public void setCode(final byte code) {
    this.code = code;
  }


  /**
   * Gets the date value for this BuildRunAction.
   *
   * @return date
   */
  public java.util.Calendar getDate() {
    return date;
  }


  /**
   * Sets the date value for this BuildRunAction.
   *
   * @param date
   */
  public void setDate(final java.util.Calendar date) {
    this.date = date;
  }


  /**
   * Gets the description value for this BuildRunAction.
   *
   * @return description
   */
  public java.lang.String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this BuildRunAction.
   *
   * @param description
   */
  public void setDescription(final java.lang.String description) {
    this.description = description;
  }


  /**
   * Gets the userID value for this BuildRunAction.
   *
   * @return userID
   */
  public int getUserID() {
    return userID;
  }


  /**
   * Sets the userID value for this BuildRunAction.
   *
   * @param userID
   */
  public void setUserID(final int userID) {
    this.userID = userID;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof BuildRunAction)) return false;
    final BuildRunAction other = (BuildRunAction) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.ID &&
            ((this.action == null && other.action == null) ||
                    (this.action != null &&
                            this.action.equals(other.action))) &&
            this.buildRunID == other.buildRunID &&
            this.code == other.code &&
            ((this.date == null && other.date == null) ||
                    (this.date != null &&
                            this.date.equals(other.date))) &&
            ((this.description == null && other.description == null) ||
                    (this.description != null &&
                            this.description.equals(other.description))) &&
            this.userID == other.userID;
    __equalsCalc = null;
    return _equals;
  }


  private boolean __hashCodeCalc = false;


  public synchronized int hashCode() {
    if (__hashCodeCalc) {
      return 0;
    }
    __hashCodeCalc = true;
    int _hashCode = 1;
    _hashCode += ID;
    if (action != null) {
      _hashCode += action.hashCode();
    }
    _hashCode += buildRunID;
    _hashCode += code;
    if (date != null) {
      _hashCode += date.hashCode();
    }
    if (description != null) {
      _hashCode += description.hashCode();
    }
    _hashCode += userID;
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(BuildRunAction.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "BuildRunAction"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("action");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "action"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("buildRunID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "buildRunID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("code");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "code"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("date");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "date"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("description");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "description"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("userID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "userID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
  }


  /**
   * Return type metadata object
   */
  public static org.apache.axis.description.TypeDesc getTypeDesc() {
    return typeDesc;
  }


  /**
   * Get Custom Serializer
   */
  public static org.apache.axis.encoding.Serializer getSerializer(
          final java.lang.String mechType,
          final java.lang.Class _javaType,
          final javax.xml.namespace.QName _xmlType) {
    return
            new org.apache.axis.encoding.ser.BeanSerializer(
                    _javaType, _xmlType, typeDesc);
  }


  /**
   * Get Custom Deserializer
   */
  public static org.apache.axis.encoding.Deserializer getDeserializer(
          final java.lang.String mechType,
          final java.lang.Class _javaType,
          final javax.xml.namespace.QName _xmlType) {
    return
            new org.apache.axis.encoding.ser.BeanDeserializer(
                    _javaType, _xmlType, typeDesc);
  }

}
