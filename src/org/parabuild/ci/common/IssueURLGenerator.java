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

import java.net.*;
import java.util.*;

/**
 * Responsible for handling generation of issue URLs for displaying on release notes page.
 * <p/>
 * URL templates can include following variables:
 * <p/>
 * ${issue.key}
 * <p/>
 * Example:  http://bugzilla/bugs?bug_id=${issue.key} will generate
 * "http://bugzilla/bugs?bug_id=122"
 */
public final class IssueURLGenerator {

  public static final String PROPERTY_ISSUE_KEY = "issue.key";

  private final Map keys = new HashMap(3);
  private String urlTemplate = null;


  /**
   * Constructor
   *
   * @param urlTemplate to use with this generator. Can be blank or null.
   */
  public IssueURLGenerator(final String urlTemplate) {
    this.urlTemplate = urlTemplate;
  }


  /**
   * Constructor
   */
  public IssueURLGenerator() {
  }


  /**
   * Sets issue key
   */
  public void setIssueKey(final String issueKey) {
    keys.put(PROPERTY_ISSUE_KEY, issueKey);
  }


  public void setURLTemplate(final String urlTemplate) {
    this.urlTemplate = urlTemplate;
  }


  /**
   * @return generated URL or empty string if template was blank
   *
   * @throws BuildException if issue key is null
   */
  public String generateIssueURL() throws BuildException {
    if (StringUtils.isBlank(urlTemplate)) return "";

    // validate
    if (StringUtils.isBlank((String)keys.get(PROPERTY_ISSUE_KEY))) throw new BuildException("Issue key was empty when generating URL for \"" + urlTemplate + "\". Check build issue tracker settings.");
    // resolve
    return NamedPropertyUtils.replaceProperties(urlTemplate, keys);
  }


  public boolean isTemplateValid() {
    final List properties = new ArrayList(7);
    final List fragments = new ArrayList(7);
    try {
      // parse
      NamedPropertyUtils.parsePropertyString(urlTemplate, fragments, properties);
    } catch (Exception e) {
      return false;
    }

    // check prop names
    boolean issueKeyPresent = false;
    for (final Iterator i = properties.iterator(); i.hasNext();) {
      final String s = (String)i.next();
      if (!s.equals(PROPERTY_ISSUE_KEY)) {
        return false;
      }
      if (s.equals(PROPERTY_ISSUE_KEY)) {
        issueKeyPresent = true;
      }
    }

    // check if mandatory ${issue.key} is present
    if (!issueKeyPresent) return false;

    return urlIsValid(urlTemplate);
  }


  /**
   * This method makes sure that provided URL template is a valid URL.
   *
   * @return true if valid
   */
  private static boolean urlIsValid(final String template) {
    try {
      final IssueURLGenerator urlGenerator = new IssueURLGenerator(template);
      urlGenerator.setIssueKey("101"); // set test url
      final String urlAsString = urlGenerator.generateIssueURL();
      new URL(urlAsString);
    } catch (MalformedURLException e) {
      return false;
    } catch (BuildException e) {
      return false;
    }
    return true;
  }


  public String toString() {
    return "IssueURLGenerator{" +
      "keys=" + keys +
      ", urlTemplate='" + urlTemplate + '\'' +
      '}';
  }
}
