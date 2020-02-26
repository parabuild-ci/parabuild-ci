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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * NamedPropertyUtils provides utility methods for setting
 * named properties in strings, like ${my.property}.
 */
public final class NamedPropertyUtils {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(NamedPropertyUtils.class); // NOPMD


  /**
   * Private constructor to disable instantiation of this utility class.
   */
  private NamedPropertyUtils() {
  }


  /**
   * Replace ${} style constructions in the given
   * value with the string value of the
   * corresponding data types.
   *
   * @param value the string to be scanned for property references.
   */
  public static String replaceProperties(final String value, final Map keys) throws BuildException {
    if (value == null) {
      return null;
    }

    final List fragments = new ArrayList(7);
    final List propertyRefs = new ArrayList(7);
    parsePropertyString(value, fragments, propertyRefs);

    final StringBuilder sb = new StringBuilder(100);
    final Iterator j = propertyRefs.iterator();

    for (final Iterator i = fragments.iterator(); i.hasNext();) {
      String fragment = (String) i.next();
      if (fragment == null) {
        final String propertyName = (String) j.next();
        if (!keys.containsKey(propertyName)) {
          throw new BuildException("Property ${" + propertyName + "} has not been set");
        }
        fragment = keys.containsKey(propertyName) ? (String) keys.get(propertyName) : "${" + propertyName + '}';
      }
      sb.append(fragment);
    }

    return sb.toString();
  }


  /**
   * This method will parse a string containing
   * ${value} style property values into two
   * lists. The first list is a collection of text
   * fragments, while the other is a set of string
   * property names null entries in the first list
   * indicate a property reference from the second
   * list.
   */
  public static void parsePropertyString(final String value, final List fragments, final List propertyRefs) throws BuildException {
    int prev = 0;
    int pos = 0;
    while ((pos = value.indexOf('$', prev)) >= 0) {
      if (pos > 0) {
        fragments.add(value.substring(prev, pos));
      }

      if (pos == value.length() - 1) {
        fragments.add("$");
        prev = pos + 1;
      } else if (value.charAt(pos + 1) != '{') {
        fragments.add("$");
        fragments.add(value.substring(pos + 1, pos + 2));
        prev = pos + 2;
      } else {
        final int endName = value.indexOf('}', pos);
        if (endName < 0) {
          throw new BuildException("Syntax error in property: " + value);
        }
        final String propertyName = value.substring(pos + 2, endName);
        fragments.add(null);
        propertyRefs.add(propertyName.trim().toLowerCase());
        prev = endName + 1;
      }
    }

    if (prev < value.length()) {
      fragments.add(value.substring(prev));
    }
  }
}
