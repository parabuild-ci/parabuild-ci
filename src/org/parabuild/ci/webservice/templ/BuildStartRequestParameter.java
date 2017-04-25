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

import java.io.Serializable;
import java.util.Arrays;

/**
 * This value object holds information about shell variable
 * build parameters to be passed to build scripts.
 *
 * @noinspection UnusedDeclaration,AssignmentToCollectionOrArrayFieldFromParameter,ReturnOfCollectionOrArrayField
 */
public final class BuildStartRequestParameter implements Serializable {

  private static final long serialVersionUID = 0L;

  private String variableName = null;
  private String[] variableValues = null;
  private String description = null;


  public String getVariableName() {
    return variableName;
  }


  public void setVariableName(final String variableName) {
    this.variableName = variableName;
  }


  public String[] getVariableValues() {
    return variableValues;
  }


  public void setVariableValues(final String[] variableValues) {
    this.variableValues = variableValues;
  }


  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  public String toString() {
    return "BuildStartRequestParameter{" +
            "description='" + description + '\'' +
            ", variableName='" + variableName + '\'' +
            ", variableValues=" + (variableValues == null ? null : Arrays.asList(variableValues)) +
            '}';
  }
}