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

import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.Calendar;

public class Issue implements Serializable {

  private static final long serialVersionUID = 427589672332767104L;
  private int ID;
  private Calendar closed;
  private String closedBy;
  private String description;
  private String key;
  private String priority;
  private String product;
  private String project;
  private Calendar received;
  private String status;
  private byte trackerType;
  private String url;
  private String version;


  public Issue() {
  }


  public Issue(
          final int ID,
          final Calendar closed,
          final String closedBy,
          final String description,
          final String key,
          final String priority,
          final String product,
          final String project,
          final Calendar received,
          final String status,
          final byte trackerType,
          final String url,
          final String version) {
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
  public Calendar getClosed() {
    return closed;
  }


  /**
   * Sets the closed value for this Issue.
   *
   * @param closed
   */
  public void setClosed(final Calendar closed) {
    this.closed = closed;
  }


  /**
   * Gets the closedBy value for this Issue.
   *
   * @return closedBy
   */
  public String getClosedBy() {
    return closedBy;
  }


  /**
   * Sets the closedBy value for this Issue.
   *
   * @param closedBy
   */
  public void setClosedBy(final String closedBy) {
    this.closedBy = closedBy;
  }


  /**
   * Gets the description value for this Issue.
   *
   * @return description
   */
  public String getDescription() {
    return description;
  }


  /**
   * Sets the description value for this Issue.
   *
   * @param description
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * Gets the key value for this Issue.
   *
   * @return key
   */
  public String getKey() {
    return key;
  }


  /**
   * Sets the key value for this Issue.
   *
   * @param key
   */
  public void setKey(final String key) {
    this.key = key;
  }


  /**
   * Gets the priority value for this Issue.
   *
   * @return priority
   */
  public String getPriority() {
    return priority;
  }


  /**
   * Sets the priority value for this Issue.
   *
   * @param priority
   */
  public void setPriority(final String priority) {
    this.priority = priority;
  }


  /**
   * Gets the product value for this Issue.
   *
   * @return product
   */
  public String getProduct() {
    return product;
  }


  /**
   * Sets the product value for this Issue.
   *
   * @param product
   */
  public void setProduct(final String product) {
    this.product = product;
  }


  /**
   * Gets the project value for this Issue.
   *
   * @return project
   */
  public String getProject() {
    return project;
  }


  /**
   * Sets the project value for this Issue.
   *
   * @param project
   */
  public void setProject(final String project) {
    this.project = project;
  }


  /**
   * Gets the received value for this Issue.
   *
   * @return received
   */
  public Calendar getReceived() {
    return received;
  }


  /**
   * Sets the received value for this Issue.
   *
   * @param received
   */
  public void setReceived(final Calendar received) {
    this.received = received;
  }


  /**
   * Gets the status value for this Issue.
   *
   * @return status
   */
  public String getStatus() {
    return status;
  }


  /**
   * Sets the status value for this Issue.
   *
   * @param status
   */
  public void setStatus(final String status) {
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
  public String getUrl() {
    return url;
  }


  /**
   * Sets the url value for this Issue.
   *
   * @param url
   */
  public void setUrl(final String url) {
    this.url = url;
  }


  /**
   * Gets the version value for this Issue.
   *
   * @return version
   */
  public String getVersion() {
    return version;
  }


  /**
   * Sets the version value for this Issue.
   *
   * @param version
   */
  public void setVersion(final String version) {
    this.version = version;
  }


  private Object __equalsCalc = null;


  public synchronized boolean equals(final Object obj) {
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
            this.ID == other.ID &&
            ((this.closed == null && other.closed == null) ||
                    (this.closed != null &&
                            this.closed.equals(other.closed))) &&
            ((this.closedBy == null && other.closedBy == null) ||
                    (this.closedBy != null &&
                            this.closedBy.equals(other.closedBy))) &&
            ((this.description == null && other.description == null) ||
                    (this.description != null &&
                            this.description.equals(other.description))) &&
            ((this.key == null && other.key == null) ||
                    (this.key != null &&
                            this.key.equals(other.key))) &&
            ((this.priority == null && other.priority == null) ||
                    (this.priority != null &&
                            this.priority.equals(other.priority))) &&
            ((this.product == null && other.product == null) ||
                    (this.product != null &&
                            this.product.equals(other.product))) &&
            ((this.project == null && other.project == null) ||
                    (this.project != null &&
                            this.project.equals(other.project))) &&
            ((this.received == null && other.received == null) ||
                    (this.received != null &&
                            this.received.equals(other.received))) &&
            ((this.status == null && other.status == null) ||
                    (this.status != null &&
                            this.status.equals(other.status))) &&
            this.trackerType == other.trackerType &&
            ((this.url == null && other.url == null) ||
                    (this.url != null &&
                            this.url.equals(other.url))) &&
            ((this.version == null && other.version == null) ||
                    (this.version != null &&
                            this.version.equals(other.version)));
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
    if (closed != null) {
      _hashCode += closed.hashCode();
    }
    if (closedBy != null) {
      _hashCode += closedBy.hashCode();
    }
    if (description != null) {
      _hashCode += description.hashCode();
    }
    if (key != null) {
      _hashCode += key.hashCode();
    }
    if (priority != null) {
      _hashCode += priority.hashCode();
    }
    if (product != null) {
      _hashCode += product.hashCode();
    }
    if (project != null) {
      _hashCode += project.hashCode();
    }
    if (received != null) {
      _hashCode += received.hashCode();
    }
    if (status != null) {
      _hashCode += status.hashCode();
    }
    _hashCode += trackerType;
    if (url != null) {
      _hashCode += url.hashCode();
    }
    if (version != null) {
      _hashCode += version.hashCode();
    }
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(Issue.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "Issue"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("closed");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "closed"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("closedBy");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "closedBy"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("description");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "description"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("key");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "key"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("priority");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "priority"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("product");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "product"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("project");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "project"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("received");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "received"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("status");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "status"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("trackerType");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "trackerType"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "byte"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("url");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "url"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("version");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "version"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    elemField.setNillable(true);
    typeDesc.addFieldDesc(elemField);
  }


  /**
   * Return type metadata object
   */
  public static TypeDesc getTypeDesc() {
    return typeDesc;
  }


  /**
   * Get Custom Serializer
   */
  public static Serializer getSerializer(
          final String mechType,
          final Class _javaType,
          final QName _xmlType) {
    return
            new BeanSerializer(
                    _javaType, _xmlType, typeDesc);
  }


  /**
   * Get Custom Deserializer
   */
  public static Deserializer getDeserializer(
          final String mechType,
          final Class _javaType,
          final QName _xmlType) {
    return
            new BeanDeserializer(
                    _javaType, _xmlType, typeDesc);
  }

}
