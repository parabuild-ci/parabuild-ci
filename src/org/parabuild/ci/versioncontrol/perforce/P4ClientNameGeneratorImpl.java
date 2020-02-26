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
package org.parabuild.ci.versioncontrol.perforce;

import java.util.*;
import java.util.regex.*;

import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.NamedPropertyUtils;
import org.parabuild.ci.util.StringUtils;

/**
 * Responsible for creating P4 client spec names from templates.
 * <p/>
 * The Templates can include following variables:
 * <p/>
 * ${build.id} ${p4.user}
 * <p/>
 * Example:  ${p4.user}-${build.id} will generate
 * <p/>
 * "parabuild-1"
 */
public final class P4ClientNameGeneratorImpl implements P4ClientNameGenerator {

  public static final String PROPERTY_P4_USER = "p4.user";
  public static final String PROPERTY_BUILD_ID = "build.id";
  public static final String PROPERTY_BUILDER_HOST = "builder.host";


  public String generate(final int buildID, final String builderHost, final String p4user, final String template) throws BuildException {
    ArgumentValidator.validateArgumentNotBlank(template, "template");
    ArgumentValidator.validateArgumentNotBlank(p4user, "Perforce user name");
    ArgumentValidator.validateArgumentNotBlank(builderHost, "Agent host");
    ArgumentValidator.validateBuildIDInitialized(buildID);
    final Map keys = new HashMap(5);
    keys.put(PROPERTY_BUILDER_HOST, builderHost.replace('.', '_'));
    keys.put(PROPERTY_BUILD_ID, Integer.toString(buildID));
    keys.put(PROPERTY_P4_USER, p4user);
    // resolve
    final String name = NamedPropertyUtils.replaceProperties(template, keys);
    if (!StringUtils.isValidStrictName(name)) throw new BuildException("P4 client name \"" + name + "\" created from template \"" + template + "\" is not a valid template.");
    return name;
  }


  public boolean isTemplateValid(final String template) {
    final ArrayList properties = new ArrayList(3);
    final ArrayList fragments = new ArrayList(3);
    try {
      // parse
      NamedPropertyUtils.parsePropertyString(template, fragments, properties);
    } catch (final Exception e) {
      return false;
    }

    // check prop names
    for (final Iterator i = properties.iterator(); i.hasNext();) {
      final String s = (String)i.next();
      if (!(s.equals(PROPERTY_P4_USER)
        || s.equals(PROPERTY_BUILD_ID)
        || s.equals(PROPERTY_BUILDER_HOST)
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
    return buildIDFound;
  }
}
