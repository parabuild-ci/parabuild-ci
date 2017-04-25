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
package org.parabuild.ci.security;

import org.parabuild.ci.common.*;

/**
 * This class is responsible for generation of user DN used when authenticating a JNDI user.
 */
public final class JNDIUserLookupStringGenerator {

  private static final String PROPERTY_USER_ID = "user.id";


  /**
   * Makes build version using provided parameters.
   */
  StringBuffer makeUserLookupString(final String template, final String userName) throws ValidationException {
    // generate
    final NamedPropertyStringGenerator generator = new NamedPropertyStringGenerator(new NamedProperty[]{
      new NamedProperty(PROPERTY_USER_ID, true, true, false),},
      template, false);
    generator.setPropertyValue(PROPERTY_USER_ID, userName);
    return new StringBuffer(generator.generate());
  }


  /**
   * Validates template.
   *
   * @param template to validate
   * @throws ValidationException if template is not valid.
   */
  public void validateTemplate(final String template) throws ValidationException {
    makeUserLookupString(template, "test_user_name");
  }
}
