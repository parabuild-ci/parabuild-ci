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
package org.parabuild.ci.common;

/**
 * This class is responsible for generation of build version field. 
 */
public final class BuildVersionGenerator {

  private static final String PROPERTY_BUILD_NAME = "build.name";
  private static final String PROPERTY_BUILD_NUMBER = "build.number";
  private static final String PROPERTY_VERSION_COUNTER = "version.counter";


  /**
   * Makes build version using provided parameters.
   */
  public StringBuffer makeBuildVersion(final String template, final String buildName, final int buildNumber, final int versionCounter) throws ValidationException {
    // generate
    final NamedPropertyStringGenerator generator = new NamedPropertyStringGenerator(new NamedProperty[]{
      new NamedProperty(PROPERTY_BUILD_NAME, false, true, false),
      new NamedProperty(PROPERTY_BUILD_NUMBER, false, true, false),
      new NamedProperty(PROPERTY_VERSION_COUNTER, false, true, false)},
      template, false);
    generator.setPropertyValue(PROPERTY_BUILD_NAME, buildName);
    generator.setPropertyValue(PROPERTY_BUILD_NUMBER, buildNumber);
    generator.setPropertyValue(PROPERTY_VERSION_COUNTER, versionCounter);
    return new StringBuffer(generator.generate());
  }


  /**
   * Validates template.
   *
   * @param template to validate
   * @throws ValidationException if template is not valid.
   */
  public void validateTemplate(final String template) throws ValidationException {
    makeBuildVersion(template, "test_name", 999, 888);
  }
}
