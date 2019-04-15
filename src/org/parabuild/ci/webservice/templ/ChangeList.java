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

public class ChangeList implements java.io.Serializable {

  private static final long serialVersionUID = -4691458159225612825L;
  private java.lang.String branch;
  private int changeListID;
  private java.lang.String client;
  private java.util.Calendar createdAt;
  private java.lang.String description;
  private java.lang.String email;
  private java.lang.String number;
  private int originalSize;
  private boolean truncated;
  private java.lang.String user;


  public ChangeList() {
  }


  public ChangeList(
          final java.lang.String branch,
          final int changeListID,
          final java.lang.String client,
          final java.util.Calendar createdAt,
          final java.lang.String description,
          final java.lang.String email,
          final java.lang.String number,
          final int originalSize,
          final boolean truncated,
          final java.lang.String user) {
    this.branch = branch;
    this.changeListID = changeListID;
    this.client = client;
    this.createdAt = createdAt;
    this.description = description;
    this.email = email;
    this.number = number;
    this.originalSize = originalSize;
    this.truncated = truncated;
    this.user = user;
  }


  /**
   * Gets the branch value for this ChangeList.
   *
   * @return branch
   */
  public java.lang.String getBranch() {
    return branch;
  }


  /**
   * Sets the branch value for this ChangeList.
   *
   * @param branch
   */
  public void setBranch(final java.lang.String branch) {
    this.branch = branch;
  }


  /**
   * Gets the changeListID value for this ChangeList.
   *
   * @return changeListID
   */
  public int getChangeListID() {
    return changeListID;
  }


  /**
   * Sets the changeListID value for this ChangeList.
   *
   * @param changeListID
   */
  public void setChangeListID(final int changeListID) {
    this.changeListID = changeListID;
  }


  /**
   * Gets the client value for this ChangeList.
   *
   * @return client
   */
  public java.lang.String getClient() {
    return client;
  }


  /**
   * Sets the client value for this ChangeList.
   *
   * @param client
   */
  public void setClient(final java.lang.String client) {
    this.client = client;
  }


  /**
   * Gets the createdAt value for this ChangeList.
   *
   * @return createdAt
   */
  public java.util.Calendar getCreatedAt() {
    return createdAt;
  }


  /**
   * Sets the createdAt value for this ChangeList.
   *
   * @param createdAt
   */
  public void setCreatedAt(final java.util.Calendar createdAt) {
    this.createdAt = createdAt;
  }


  /**
   * Gets the description value for this ChangeList.
   *
   * @return description
   */
  public java.lang.String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this ChangeList.
   *
   * @param description
   */
  public void setDescription(final java.lang.String description) {
    this.description = description;
  }


  /**
   * Gets the email value for this ChangeList.
   *
   * @return email
   */
  public java.lang.String getEmail() {
    return email;
  }


  /**
   * Sets the email value for this ChangeList.
   *
   * @param email
   */
  public void setEmail(final java.lang.String email) {
    this.email = email;
  }


  /**
   * Gets the number value for this ChangeList.
   *
   * @return number
   */
  public java.lang.String getNumber() {
    return number;
  }


  /**
   * Sets the number value for this ChangeList.
   *
   * @param number
   */
  public void setNumber(final java.lang.String number) {
    this.number = number;
  }


  /**
   * Gets the originalSize value for this ChangeList.
   *
   * @return originalSize
   */
  public int getOriginalSize() {
    return originalSize;
  }


  /**
   * Sets the originalSize value for this ChangeList.
   *
   * @param originalSize
   */
  public void setOriginalSize(final int originalSize) {
    this.originalSize = originalSize;
  }


  /**
   * Gets the truncated value for this ChangeList.
   *
   * @return truncated
   */
  public boolean isTruncated() {
    return truncated;
  }


  /**
   * Sets the truncated value for this ChangeList.
   *
   * @param truncated
   */
  public void setTruncated(final boolean truncated) {
    this.truncated = truncated;
  }


  /**
   * Gets the user value for this ChangeList.
   *
   * @return user
   */
  public java.lang.String getUser() {
    return user;
  }


  /**
   * Sets the user value for this ChangeList.
   *
   * @param user
   */
  public void setUser(final java.lang.String user) {
    this.user = user;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof ChangeList)) return false;
    final ChangeList other = (ChangeList) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            ((this.branch == null && other.branch == null) ||
                    (this.branch != null &&
                            this.branch.equals(other.branch))) &&
            this.changeListID == other.changeListID &&
            ((this.client == null && other.client == null) ||
                    (this.client != null &&
                            this.client.equals(other.client))) &&
            ((this.createdAt == null && other.createdAt == null) ||
                    (this.createdAt != null &&
                            this.createdAt.equals(other.createdAt))) &&
            ((this.description == null && other.description == null) ||
                    (this.description != null &&
                            this.description.equals(other.description))) &&
            ((this.email == null && other.email == null) ||
                    (this.email != null &&
                            this.email.equals(other.email))) &&
            ((this.number == null && other.number == null) ||
                    (this.number != null &&
                            this.number.equals(other.number))) &&
            this.originalSize == other.originalSize &&
            this.truncated == other.truncated &&
            ((this.user == null && other.user == null) ||
                    (this.user != null &&
                            this.user.equals(other.user)));
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
    if (branch != null) {
      _hashCode += branch.hashCode();
    }
    _hashCode += changeListID;
    if (client != null) {
      _hashCode += client.hashCode();
    }
    if (createdAt != null) {
      _hashCode += createdAt.hashCode();
    }
    if (description != null) {
      _hashCode += description.hashCode();
    }
    if (email != null) {
      _hashCode += email.hashCode();
    }
    if (number != null) {
      _hashCode += number.hashCode();
    }
    _hashCode += originalSize;
    _hashCode += (truncated ? Boolean.TRUE : Boolean.FALSE).hashCode();
    if (user != null) {
      _hashCode += user.hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(ChangeList.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "ChangeList"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("branch");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "branch"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("changeListID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "changeListID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("client");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "client"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("createdAt");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "createdAt"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("description");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "description"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("email");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "email"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("number");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "number"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("originalSize");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "originalSize"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("truncated");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "truncated"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("user");
    elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.ci.parabuild.org", "user"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
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
