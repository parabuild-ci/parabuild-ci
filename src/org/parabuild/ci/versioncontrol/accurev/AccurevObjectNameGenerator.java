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
package org.parabuild.ci.versioncontrol.accurev;

import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.NamedPropertyUtils;
import org.parabuild.ci.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
final class AccurevObjectNameGenerator {

  public static final String PROPERTY_ACCUREV_DEPOT = "accurev.depot";
  public static final String PROPERTY_ACCUREV_USER = "accurev.user";
  public static final String PROPERTY_BUILD_ID = "build.id";
  public static final String PROPERTY_AGENT_HOST = "agent.host";
  public static final String PROPERTY_BUILDER_HOST = "builder.host";


  public String generate(final int buildID, final String builderHost, final String user, final String template, final String depot) throws BuildException {
    ArgumentValidator.validateArgumentNotBlank(template, "template");
    ArgumentValidator.validateArgumentNotBlank(user, "Accurev user name");
    ArgumentValidator.validateArgumentNotBlank(builderHost, "Agent host");
    ArgumentValidator.validateArgumentNotBlank(builderHost, "Agent host");
    ArgumentValidator.validateArgumentNotBlank(depot, "Depot");
    ArgumentValidator.validateBuildIDInitialized(buildID);
    final Map keys = new HashMap(5);
    keys.put(PROPERTY_AGENT_HOST, builderHost.replace('.', '_'));
    keys.put(PROPERTY_BUILDER_HOST, builderHost.replace('.', '_'));
    keys.put(PROPERTY_BUILD_ID, Integer.toString(buildID));
    keys.put(PROPERTY_ACCUREV_USER, user);
    keys.put(PROPERTY_ACCUREV_DEPOT, depot);
    // resolve
    final String name = NamedPropertyUtils.replaceProperties(template, keys);
    if (!StringUtils.isValidStrictName(name)) {
      throw new BuildException("Accurev workspace name \"" + name + "\" created from template \"" + template + "\" is not a valid template.");
    }
    return name;
  }


  public boolean isTemplateValid(final String template) {
    final List properties = new ArrayList(3);
    final List fragments = new ArrayList(3);
    try {
      // parse
      NamedPropertyUtils.parsePropertyString(template, fragments, properties);
    } catch (final Exception e) {
      return false;
    }

    // check prop names
    for (final Iterator i = properties.iterator(); i.hasNext();) {
      final String s = (String) i.next();
      if (!(s.equals(PROPERTY_ACCUREV_USER)
              || s.equals(PROPERTY_BUILD_ID)
              || s.equals(PROPERTY_AGENT_HOST)
              || s.equals(PROPERTY_ACCUREV_DEPOT)
      )) {
        return false;
      }
    }

    // if we are here, everything is OK so far and we can validate the rest
    final Pattern fragmentPattern = Pattern.compile("[-a-zA-Z_0-9]*");
    for (int i = 0, n = fragments.size(); i < n; i++) {
      final String s = (String) fragments.get(i);
      if (!StringUtils.isBlank(s)) {
        final Matcher matcher = fragmentPattern.matcher(s);
        if (!matcher.matches()) {
          return false;
        }
      }
    }

    // find mandatory build.id
    boolean buildIDFound = false;
    for (final Iterator i = properties.iterator(); i.hasNext();) {
      final String prop = (String) i.next();
      if (prop.equals(PROPERTY_BUILD_ID)) {
        buildIDFound = true;
        break;
      }
    }
    return buildIDFound;
  }
}
