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
 * This class is responsible for generation of build result
 * URL according to build result URL template.
 */
public final class URLResultGenerator {

  private static final String PROPERTY_BUILD_NAME = "build.name";
  private static final String PROPERTY_BUILD_NUMBER = "build.number";
  private static final String PROPERTY_CHANGE_LIST_NUMBER = "changelist.number";
  private static final String PROPERTY_BUILD_ID = "build.id";
  private static final String PROPERTY_BUILD_RUN_ID = "build.run.id";


  /**
   * Makes build version using provided parameters.
   */
  public void makeURL(final String template, final String buildName,
                      final int buildNumber, final String changeListNumber, final int buildID,
                      final int buildRunID) throws ValidationException {

    // generate
    final NamedPropertyStringGenerator generator = new NamedPropertyStringGenerator(new NamedProperty[]{
      new NamedProperty(PROPERTY_BUILD_NAME, false, true, false),
      new NamedProperty(PROPERTY_BUILD_NUMBER, false, true, false),
      new NamedProperty(PROPERTY_CHANGE_LIST_NUMBER, false, true, false),
      new NamedProperty(PROPERTY_BUILD_ID, false, true, true),
      new NamedProperty(PROPERTY_BUILD_RUN_ID, false, true, true)
    }, template, false);
    generator.setPropertyValue(PROPERTY_BUILD_NAME, buildName);
    generator.setPropertyValue(PROPERTY_BUILD_NUMBER, buildNumber);

    generator.setPropertyValue(PROPERTY_CHANGE_LIST_NUMBER, changeListNumber);
    generator.setPropertyValue(PROPERTY_BUILD_ID, Integer.toString(buildID));
    generator.setPropertyValue(PROPERTY_BUILD_RUN_ID, Integer.toString(buildRunID));
    generator.generate();
  }


  /**
   * Validates template.
   *
   * @param template to validate
   * @throws ValidationException if template is not valid.
   */
  public void validateTemplate(final String template) throws ValidationException {
    makeURL(template, "test_name", 999, "change_list_number", 999, 999);
  }
}
