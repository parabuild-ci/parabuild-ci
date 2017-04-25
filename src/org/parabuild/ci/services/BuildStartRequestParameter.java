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
package org.parabuild.ci.services;

import org.parabuild.ci.common.ArgumentValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * This value object holds information about shell variable
 * build parameters to be passed to build scripts.
 */
public final class BuildStartRequestParameter {

  private String variableName = null;
  private List variableValues = null;
  private final String description;
  private int order;


  public BuildStartRequestParameter(final String variableName, final String description, final List variableValues,
                                    final int orger) {

    this.variableName = ArgumentValidator.validateArgumentNotBlank(variableName, "variable name");
    this.variableValues = new ArrayList(ArgumentValidator.validateArgumentNotEmpty(variableValues, "variable values"));
    this.description = description;
    this.order = orger;
  }


  public BuildStartRequestParameter(final String variableName, final String description, final String variableValue,
                                    final int order) {

    this(variableName, description, makeSingleValueList(variableValue), order);
  }


  public BuildStartRequestParameter(final String variableName, final String description, final String[] variableValues,
                                    final int order) {

    this(variableName, description, toList(variableValues), order);
  }


  private static List toList(final String[] variableValues) {
    final List arrayList = new ArrayList(3);
    if (variableValues != null) {
      for (int i = 0; i < variableValues.length; i++) {
        final String value = variableValues[i];
        if (value != null) {
          arrayList.add(value);
        }
      }
    }
    return arrayList;
  }


  public String getName() {
    return variableName;
  }


  public List getValues() {
    return variableValues;
  }


  public String getDescription() {
    return description;
  }

  public int getOrder() {
    return order;
  }

  /**
   * Helper method to create a single value list to be used
   * with the constructor.
   * @param value a value.
   * @return a new list containing the value.
   */
  private static List makeSingleValueList(final String value) {
    
    final List singleValueList = new ArrayList(1);
    singleValueList.add(value);
    return singleValueList;
  }


  public String toString() {
    return "BuildStartRequestParameter{" +
            "variableName='" + variableName + '\'' +
            ", variableValues=" + variableValues +
            '}';
  }
}
