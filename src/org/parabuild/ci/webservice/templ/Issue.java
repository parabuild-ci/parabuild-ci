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

public class Issue implements java.io.Serializable {

  private int ID;
  private java.util.Calendar closed;
  private java.lang.String closedBy;
  private java.lang.String description;
  private java.lang.String key;
  private java.lang.String priority;
  private java.lang.String product;
  private java.lang.String project;
  private java.util.Calendar received;
  private java.lang.String status;
  private byte trackerType;
  private java.lang.String url;
  private java.lang.String version;


  public Issue() {
  }


  public Issue(
          final int ID,
          final java.util.Calendar closed,
          final java.lang.String closedBy,
          final java.lang.String description,
          final java.lang.String key,
          final java.lang.String priority,
          final java.lang.String product,
          final java.lang.String project,
          final java.util.Calendar received,
          final java.lang.String status,
          final byte trackerType,
          final java.lang.String url,
          final java.lang.String version) {
    this.ID = ID;
    this.closed = closed;
    this.closedBy = closedBy;
    this.description = description;
    this.key = key;
    this.priority = priority;
    this.product = product;
    this.project = project;
    this.received = received;
    this.status = status;
    this.trackerType = trackerType;
    this.url = url;
    this.version = version;
  }


  /**
   * Gets the ID value for this Issue.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this Issue.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the closed value for this Issue.
   *
   * @return closed
   */
  public java.util.Calendar getClosed() {
    return closed;
  }


  /**
   * Sets the closed value for this Issue.
   *
   * @param closed
   */
  public void setClosed(final java.util.Calendar closed) {
    this.closed = closed;
  }


  /**
   * Gets the closedBy value for this Issue.
   *
   * @return closedBy
   */
  public java.lang.String getClosedBy() {
    return closedBy;
  }


  /**
   * Sets the closedBy value for this Issue.
   *
   * @param closedBy
   */
  public void setClosedBy(final java.lang.String closedBy) {
    this.closedBy = closedBy;
  }


  /**
   * Gets the description value for this Issue.
   *
   * @return description
   */
  public java.lang.String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this Issue.
   *
   * @param description
   */
  public void setDescription(final java.lang.String description) {
    this.description = description;
  }


  /**
   * Gets the key value for this Issue.
   *
   * @return key
   */
  public java.lang.String getKey() {
    return key;
  }


  /**
   * Sets the key value for this Issue.
   *
   * @param key
   */
  public void setKey(final java.lang.String key) {
    this.key = key;
  }


  /**
   * Gets the priority value for this Issue.
   *
   * @return priority
   */
  public java.lang.String getPriority() {
    return priority;
  }


  /**
   * Sets the priority value for this Issue.
   *
   * @param priority
   */
  public void setPriority(final java.lang.String priority) {
    this.priority = priority;
  }


  /**
   * Gets the product value for this Issue.
   *
   * @return product
   */
  public java.lang.String getProduct() {
    return product;
  }


  /**
   * Sets the product value for this Issue.
   *
   * @param product
   */
  public void setProduct(final java.lang.String product) {
    this.product = product;
  }


  /**
   * Gets the project value for this Issue.
   *
   * @return project
   */
  public java.lang.String getProject() {
    return project;
  }


  /**
   * Sets the project value for this Issue.
   *
   * @param project
   */
  public void setProject(final java.lang.String project) {
    this.project = project;
  }


  /**
   * Gets the received value for this Issue.
   *
   * @return received
   */
  public java.util.Calendar getReceived() {
    return received;
  }


  /**
   * Sets the received value for this Issue.
   *
   * @param received
   */
  public void setReceived(final java.util.Calendar received) {
    this.received = received;
  }


  /**
   * Gets the status value for this Issue.
   *
   * @return status
   */
  public java.lang.String getStatus() {
    return status;
  }


  /**
   * Sets the status value for this Issue.
   *
   * @param status
   */
  public void setStatus(final java.lang.String status) {
    this.status = status;
  }


  /**
   * Gets the trackerType value for this Issue.
   *
   * @return trackerType
   */
  public byte getTrackerType() {
    return trackerType;
  }


  /**
   * Sets the trackerType value for this Issue.
   *
   * @param trackerType
   */
  public void setTrackerType(final byte trackerType) {
    this.trackerType = trackerType;
  }


  /**
   * Gets the url value for this Issue.
   *
   * @return url
   */
  public java.lang.String getUrl() {
    return url;
  }


  /**
   * Sets the url value for this Issue.
   *
   * @param url
   */
  public void setUrl(final java.lang.String url) {
    this.url = url;
  }


  /**
   * Gets the version value for this Issue.
   *
   * @return version
   */
  public java.lang.String getVersion() {
    return version;
  }


  /**
   * Sets the version value for this Issue.
   *
   * @param version
   */
  public void setVersion(final java.lang.String version) {
    this.version = version;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof Issue)) return false;
    final Issue other = (Issue) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.getID() &&
            ((this.closed == null && other.getClosed() == null) ||
                    (this.closed != null &&
                            this.closed.equals(other.getClosed()))) &&
            ((this.closedBy == null && other.getClosedBy() == null) ||
                    (this.closedBy != null &&
                            this.closedBy.equals(other.getClosedBy()))) &&
            ((this.description == null && other.getDescription() == null) ||
                    (this.description != null &&
                            this.description.equals(other.getDescription()))) &&
            ((this.key == null && other.getKey() == null) ||
                    (this.key != null &&
                            this.key.equals(other.getKey()))) &&
            ((this.priority == null && other.getPriority() == null) ||
                    (this.priority != null &&
                            this.priority.equals(other.getPriority()))) &&
            ((this.product == null && other.getProduct() == null) ||
                    (this.product != null &&
                            this.product.equals(other.getProduct()))) &&
            ((this.project == null && other.getProject() == null) ||
                    (this.project != null &&
                            this.project.equals(other.getProject()))) &&
            ((this.received == null && other.getReceived() == null) ||
                    (this.received != null &&
                            this.received.equals(other.getReceived()))) &&
            ((this.status == null && other.getStatus() == null) ||
                    (this.status != null &&
                            this.status.equals(other.getStatus()))) &&
            this.trackerType == other.getTrackerType() &&
            ((this.url == null && other.getUrl() == null) ||
                    (this.url != null &&
                            this.url.equals(other.getUrl()))) &&
            ((this.version == null && other.getVersion() == null) ||
                    (this.version != null &&
                            this.version.equals(other.getVersion())));
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
    _hashCode += getID();
    if (getClosed() != null) {
      _hashCode += getClosed().hashCode();
    }
    if (getClosedBy() != null) {
      _hashCode += getClosedBy().hashCode();
    }
    if (getDescription() != null) {
      _hashCode += getDescription().hashCode();
    }
    if (getKey() != null) {
      _hashCode += getKey().hashCode();
    }
    if (getPriority() != null) {
      _hashCode += getPriority().hashCode();
    }
    if (getProduct() != null) {
      _hashCode += getProduct().hashCode();
    }
    if (getProject() != null) {
      _hashCode += getProject().hashCode();
    }
    if (getReceived() != null) {
      _hashCode += getReceived().hashCode();
    }
    if (getStatus() != null) {
      _hashCode += getStatus().hashCode();
    }
    _hashCode += getTrackerType();
    if (getUrl() != null) {
      _hashCode += getUrl().hashCode();
    }
    if (getVersion() != null) {
      _hashCode += getVersion().hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(Issue.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "Issue"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("closed");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "closed"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("closedBy");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "closedBy"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("description");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "description"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("key");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "key"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("priority");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "priority"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("product");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "product"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("project");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "project"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("received");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "received"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("status");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "status"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("trackerType");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "trackerType"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("url");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "url"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("version");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "version"));
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
