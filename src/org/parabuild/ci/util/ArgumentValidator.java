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
package org.parabuild.ci.util;

import java.util.*;

/**
 * Method attribute utils.
 */
public final class ArgumentValidator {

  private ArgumentValidator() {
  }


  public static String validateArgumentNotBlank(final String argValue, final String argDescr) {
    if (StringUtils.isBlank(argValue)) {
      throw new IllegalArgumentException("Argument \"" + argDescr + "\" can not be blank.");
    }
    return argValue;
  }


  public static Object validateArgumentNotNull(final Object argValue, final String argDescr) {
    if (argValue != null) return argValue;
    throw new IllegalArgumentException("Argument \"" + argDescr + "\" can not be null.");
  }


  public static void validateArgumentGTZero(final int argValue, final String argDescr) {
    if (argValue <= 0) {
      throw new IllegalArgumentException("Argument \"" + argDescr + "\" should be greater than zero but it was " + argValue);
    }
  }


  public static int validateBuildIDInitialized(final int buildID) {
    if (buildID == -1) {
      throw new IllegalArgumentException("Build ID should be initialized but it was not");
    }
    return buildID;
  }


  public static List validateArgumentNotEmpty(final List variableValues, final String argDescr) {
    if (variableValues != null && !variableValues.isEmpty()) return variableValues;
    throw new IllegalArgumentException("Argument \"" + argDescr + "\" can not be empty.");
  }


  public static void validateArgumentGEZero(final int argValue, final String argDescr) {
    if (argValue < 0) {
      throw new IllegalArgumentException("Argument \"" + argDescr + "\" should be greater or equal than zero but it was " + argValue);
    }
  }


  public static void validateArgumentNotZero(final int argValue, final String argDescr) {
    if (argValue == 0) {
      throw new IllegalArgumentException("Argument \"" + argDescr + "\" should be not equal zero but it was " + argValue);
    }
  }
}
