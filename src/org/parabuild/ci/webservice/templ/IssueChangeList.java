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

public class IssueChangeList implements Serializable {

  private static final long serialVersionUID = 642028237802848736L;
  private int ID;
  private int changeListID;
  private int issueID;


  public IssueChangeList() {
  }


  public IssueChangeList(
          final int ID,
          final int changeListID,
          final int issueID) {
    this.ID = ID;
    this.changeListID = changeListID;
    this.issueID = issueID;
  }


  /**
   * Gets the ID value for this IssueChangeList.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this IssueChangeList.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the changeListID value for this IssueChangeList.
   *
   * @return changeListID
   */
  public int getChangeListID() {
    return changeListID;
  }


  /**
   * Sets the changeListID value for this IssueChangeList.
   *
   * @param changeListID
   */
  public void setChangeListID(final int changeListID) {
    this.changeListID = changeListID;
  }


  /**
   * Gets the issueID value for this IssueChangeList.
   *
   * @return issueID
   */
  public int getIssueID() {
    return issueID;
  }


  /**
   * Sets the issueID value for this IssueChangeList.
   *
   * @param issueID
   */
  public void setIssueID(final int issueID) {
    this.issueID = issueID;
  }


  private Object __equalsCalc;


  public synchronized boolean equals(final Object obj) {
    if (!(obj instanceof IssueChangeList)) return false;
    final IssueChangeList other = (IssueChangeList) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.ID &&
            this.changeListID == other.changeListID &&
            this.issueID == other.issueID;
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
    _hashCode += changeListID;
    _hashCode += issueID;
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static final TypeDesc typeDesc =
          new TypeDesc(IssueChangeList.class, true);


  static {
    typeDesc.setXmlType(new QName("http://org.parabuild.ci", "IssueChangeList"));
    ElementDesc elemField = new ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("changeListID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "changeListID"));
    elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new ElementDesc();
    elemField.setFieldName("issueID");
    elemField.setXmlName(new QName("http://org.parabuild.ci", "issueID"));
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
