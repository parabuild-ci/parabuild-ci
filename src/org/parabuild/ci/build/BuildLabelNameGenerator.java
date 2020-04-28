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
package org.parabuild.ci.build;

import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.NamedPropertyUtils;
import org.parabuild.ci.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for handling build label names.
 * <p/>
 * Label templates can include following variables:
 * <p/>
 * ${build.name} ${build.number} ${build.timestamp}
 * ${build.date}
 * <p/>
 * Example:  parabuild-2.0-eap-build-${build.number}-${build.timestamp}
 * will generate
 * <p/>
 * "parabuild-2.0-eap-build-128-200311200548"
 */
public final class BuildLabelNameGenerator {

  public static final String PROPERTY_BUILD_NAME = "build.name";
  public static final String PROPERTY_BUILD_NUMBER = "build.number";
  public static final String PROPERTY_BUILD_TIMESTAMP = "build.timestamp";
  public static final String PROPERTY_BUILD_DATE = "build.date";
  public static final String PROPERTY_CHANGELIST_NUMBER = "changelist.number";

  private final Map keys = new HashMap(3);
  private String labelTemplate = null;
  private static final String BUILD_DATE_FORMAT = "yyyyMMdd";


  /**
   * Sets build name
   */
  public void setBuildName(final String buildName) {
    keys.put(PROPERTY_BUILD_NAME, buildName);
  }


  /**
   * Sets build number
   */
  public void setBuildNumber(final String buildNumber) {
    keys.put(PROPERTY_BUILD_NUMBER, buildNumber);
  }


  /**
   * Sets build number
   */
  public void setBuildNumber(final int buildNumber) {
    setBuildNumber(Integer.toString(buildNumber));
  }


  /**
   * Sets build timestamp
   */
  public void setBuildTimestamp(final Date buildTimestamp) {
    keys.put(PROPERTY_BUILD_TIMESTAMP, getTimeStampFormatter().format(buildTimestamp));
    keys.put(PROPERTY_BUILD_DATE, new SimpleDateFormat(BUILD_DATE_FORMAT, Locale.US).format(buildTimestamp));
  }


  /**
   * Sets template that will be used to generated build label
   * name.
   */
  public void setLabelTemplate(final String labelTemplate) {
    this.labelTemplate = labelTemplate;
  }


  /**
   * Sets change list number.
   *
   * @param changeListNumber
   */
  public void setChangeListNumber(final String changeListNumber) {
    keys.put(PROPERTY_CHANGELIST_NUMBER, changeListNumber);
    ArgumentValidator.validateArgumentNotBlank(changeListNumber, "change list number");
  }


  public String generateLabelName() throws BuildException {
    // validate
    if (StringUtils.isBlank(labelTemplate)) throw new IllegalStateException("Build label template is empty");
    if (StringUtils.isBlank(getPropValue(PROPERTY_BUILD_NAME))) throw new IllegalStateException("Build name is empty");
    if (StringUtils.isBlank(getPropValue(PROPERTY_BUILD_NUMBER))) throw new IllegalStateException("Build number is empty");
    if (StringUtils.isBlank(getPropValue(PROPERTY_BUILD_TIMESTAMP))) throw new IllegalStateException("Build time stamp is empty");
    if (StringUtils.isBlank(getPropValue(PROPERTY_BUILD_DATE))) throw new IllegalStateException("Build date stamp is empty");
    if (StringUtils.isBlank(getPropValue(PROPERTY_CHANGELIST_NUMBER))) throw new IllegalStateException("Change list number is empty");

    // resolve
    final String label = NamedPropertyUtils.replaceProperties(labelTemplate, keys);
    if (!StringUtils.isValidStrictName(label)) throw new BuildException("Label name \"" + label + "\" created from template \"" + labelTemplate + "\" is not a valid label.");
    return label;
  }


  public boolean isTemplateValid() {
    final List properties = new ArrayList(7);
    final List fragments = new ArrayList(7);
    try {
      // parse
      NamedPropertyUtils.parsePropertyString(labelTemplate, fragments, properties);
    } catch (final Exception e) {
      return false;
    }

    // check prop names
    for (final Iterator i = properties.iterator(); i.hasNext();) {
      final String s = (String)i.next();
      if (!(s.equals(PROPERTY_BUILD_NAME)
        || s.equals(PROPERTY_BUILD_NUMBER)
        || s.equals(PROPERTY_BUILD_TIMESTAMP)
        || s.equals(PROPERTY_BUILD_DATE)
        || s.equals(PROPERTY_CHANGELIST_NUMBER)
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

    return true;
  }


  public boolean isTemplateStatic() {
    try {
      final ArrayList properties = new ArrayList(7);
      NamedPropertyUtils.parsePropertyString(labelTemplate, new ArrayList(7), properties);
      for (final Iterator i = properties.iterator(); i.hasNext();) {
        final String prop = (String)i.next();
        if (prop.equals(PROPERTY_BUILD_NUMBER)) return false;
        if (prop.equals(PROPERTY_BUILD_TIMESTAMP)) return false;
        if (prop.equals(PROPERTY_BUILD_DATE)) return false;
        if (prop.equals(PROPERTY_CHANGELIST_NUMBER)) return false;
      }
    } catch (final Exception e) {
      // we return true because we have to consider
      // label static and deletable.
      return true;
    }
    return true;
  }


  /**
   * Helper method
   *
   * @param propName
   */
  private String getPropValue(final String propName) {
    return (String)keys.get(propName);
  }


  public static SimpleDateFormat getTimeStampFormatter() {
    return new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
  }


  public String toString() {
    return "BuildLabelNameGenerator{" +
      "keys=" + keys +
      ", labelTemplate='" + labelTemplate + '\'' +
      '}';
  }
}
