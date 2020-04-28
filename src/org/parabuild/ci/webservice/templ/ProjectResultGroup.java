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

public class ProjectResultGroup implements Serializable {

  private static final long serialVersionUID = -2465851505161540064L;
  private int ID;
  private int projectID;
  private int resultGroupID;


  public ProjectResultGroup() {
  }


  public ProjectResultGroup(
          final int ID,
          final int projectID,
          final int resultGroupID) {
    this.ID = ID;
    this.projectID = projectID;
    this.resultGroupID = resultGroupID;
  }


  /**
   * Gets the ID value for this ProjectResultGroup.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this ProjectResultGroup.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the projectID value for this ProjectResultGroup.
   *
   * @return projectID
   */
  public int getProjectID() {
    return projectID;
  }


  /**
   * Sets the projectID value for this ProjectResultGroup.
   *
   * @param projectID
   */
  public void setProjectID(final int projectID) {
    this.projectID = projectID;
  }


  /**
   * Gets the resultGroupID value for this ProjectResultGroup.
   *
   * @return resultGroupID
   */
  public int getResultGroupID() {
    return resultGroupID;
  }


  /**
   * Sets the resultGroupID value for this ProjectResultGroup.
   *
   * @param resultGroupID
   */
  public void setResultGroupID(final int resultGroupID) {
    this.resultGroupID = resultGroupID;
  }


  private Object __equalsCalc;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof ProjectResultGroup)) return false;
    final ProjectResultGroup other = (ProjectResultGroup) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.ID &&
            this.projectID == other.projectID &&
            this.resultGroupID == other.resultGroupID;
    __equalsCalc = null;
    return _equals;
  }


  private boolean __hashCodeCalc;


  public synchronized int hashCode() {
    if (__hashCodeCalc) {
      return 0;
    }
    __hashCodeCalc = true;
    int _hashCode = 1;
    _hashCode += ID;
    _hashCode += projectID;
    _hashCode += resultGroupID;
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(ProjectResultGroup.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "ProjectResultGroup"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("projectID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "projectID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("resultGroupID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "resultGroupID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
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
