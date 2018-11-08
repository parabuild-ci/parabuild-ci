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

public class BuildFarmAgent implements java.io.Serializable {

  private int ID;
  private int agentID;
  private int farmID;
  private long timeStamp;


  public BuildFarmAgent() {
  }


  public BuildFarmAgent(
          final int ID,
          final int agentID,
          final int farmID,
          final long timeStamp) {
    this.ID = ID;
    this.agentID = agentID;
    this.farmID = farmID;
    this.timeStamp = timeStamp;
  }


  /**
   * Gets the ID value for this BuilderAgent.
   *
   * @return ID
   */
  public int getID() {
    return ID;
  }


  /**
   * Sets the ID value for this BuilderAgent.
   *
   * @param ID
   */
  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * Gets the agentID value for this BuilderAgent.
   *
   * @return agentID
   */
  public int getAgentID() {
    return agentID;
  }


  /**
   * Sets the agentID value for this BuilderAgent.
   *
   * @param agentID
   */
  public void setAgentID(final int agentID) {
    this.agentID = agentID;
  }


  /**
   * Gets the builderID value for this BuilderAgent.
   *
   * @return builderID
   */
  public int getFarmID() {
    return farmID;
  }


  /**
   * Sets the builderID value for this BuilderAgent.
   *
   * @param farmID
   */
  public void setFarmID(final int farmID) {
    this.farmID = farmID;
  }


  /**
   * Gets the timeStamp value for this BuilderAgent.
   *
   * @return timeStamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Sets the timeStamp value for this BuilderAgent.
   *
   * @param timeStamp
   */
  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  private java.lang.Object __equalsCalc = null;


  public synchronized boolean equals(final java.lang.Object obj) {
    if (!(obj instanceof BuildFarmAgent)) return false;
    final BuildFarmAgent other = (BuildFarmAgent) obj;
    if (obj == null) return false;
    if (this == obj) return true;
    if (__equalsCalc != null) {
      return (__equalsCalc == obj);
    }
    __equalsCalc = obj;
    final boolean _equals;
    _equals = true &&
            this.ID == other.getID() &&
            this.agentID == other.getAgentID() &&
            this.farmID == other.getFarmID() &&
            this.timeStamp == other.getTimeStamp();
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
    _hashCode += getAgentID();
    _hashCode += getFarmID();
    _hashCode += new Long(getTimeStamp()).hashCode();
    __hashCodeCalc = false;
    return _hashCode;
  }


  // Type metadata
  private static org.apache.axis.description.TypeDesc typeDesc =
          new org.apache.axis.description.TypeDesc(BuildFarmAgent.class, true);


  static {
    typeDesc.setXmlType(new javax.xml.namespace.QName("http://org.parabuild.ci", "BuilderAgent"));
    org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("ID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "ID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("agentID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "agentID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("builderID");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "builderID"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
    elemField.setNillable(false);
    typeDesc.addFieldDesc(elemField);
    elemField = new org.apache.axis.description.ElementDesc();
    elemField.setFieldName("timeStamp");
    elemField.setXmlName(new javax.xml.namespace.QName("http://org.parabuild.ci", "timeStamp"));
    elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
