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
package org.parabuild.ci.versioncontrol;

import java.util.*;
import java.util.regex.*;

import org.parabuild.ci.common.*;

/**
 * Responsible for creating ClearCase view names from templates.
 * <p/>
 * The Templates can include following variables:
 * <p/>
 * ${build.id} ${cc.user}
 * <p/>
 * Example:  ${cc.user}-${build.id} will generate
 * <p/>
 * "parabuild-1"
 */
public final class ClearCaseViewNameGenerator {

  public static final String PROPERTY_CLEARCASE_USER = "cc.user";
  public static final String PROPERTY_BUILD_ID = "build.id";


  private final Map keys = new HashMap(3);
  private String nameTemplate = null;


  /**
   * Constructor.
   *
   * @param ccUser
   * @param buildID
   * @param tempalte
   */
  public ClearCaseViewNameGenerator(final String ccUser, final int buildID, final String tempalte) {
    this.keys.put(PROPERTY_BUILD_ID, Integer.toString(buildID));
    this.keys.put(PROPERTY_CLEARCASE_USER, ccUser);
    this.nameTemplate = tempalte;
    ArgumentValidator.validateArgumentNotBlank(nameTemplate, "template");
    ArgumentValidator.validateArgumentNotBlank(ccUser, "ClearCase user name");
    ArgumentValidator.validateBuildIDInitialized(buildID);
  }


  public String generate() throws BuildException {
    // resolve
    final String name = NamedPropertyUtils.replaceProperties(nameTemplate, keys);
    if (!StringUtils.isValidStrictName(name)) throw new BuildException("ClearCase view name \"" + name + "\" created from template \"" + nameTemplate + "\" is not a valid template.");
    return name;
  }


  public boolean isTemplateValid() {
    final ArrayList properties = new ArrayList();
    final ArrayList fragments = new ArrayList();
    try {
      // parse
      NamedPropertyUtils.parsePropertyString(nameTemplate, fragments, properties);
    } catch (Exception e) {
      return false;
    }

    // check prop names
    for (final Iterator i = properties.iterator(); i.hasNext();) {
      final String s = (String)i.next();
      if (!(s.equals(PROPERTY_CLEARCASE_USER)
        || s.equals(PROPERTY_BUILD_ID)
        )) {
        return false;
      }
    }

    // if we are here, everything is OK so far and we can validate the rest
    final Pattern fragmentPattern = Pattern.compile("[-a-zA-Z_0-9]*");
    for (int i = 0, n = fragments.size(); i < n; i++) {
      final String s = (String)fragments.get(i);
      if (!StringUtils.isBlank(s)) {
        final Matcher matcher = fragmentPattern.matcher(s);
        if (!matcher.matches()) return false;
      }
    }

    // find mandatory build.id
    boolean buildIDFound = false;
    for (final Iterator i = properties.iterator(); i.hasNext();) {
      final String prop = (String)i.next();
      if (prop.equals(PROPERTY_BUILD_ID)) {
        buildIDFound = true;
        break;
      }
    }
    if (!buildIDFound) return false;

    return true;
  }
}
