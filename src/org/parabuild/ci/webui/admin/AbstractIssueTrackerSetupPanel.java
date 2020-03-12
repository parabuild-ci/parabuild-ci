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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.IssueTracker;
import org.parabuild.ci.object.IssueTrackerProperty;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonText;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.IssueFilterField;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.Validatable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Abstract issue tracker set up panel implementing Template GoF.
 * Defines common interface for issue tracker set up.
 */
public abstract class AbstractIssueTrackerSetupPanel extends MessagePanel implements Validatable {

  private static final long serialVersionUID = 2382416523126482085L; // NOPMD

  private static final String STR_URL = "Issue URL template:";
  private static final String STR_ISSUE_FILTER = "Issue filter:";
  private static final String STR_ISSUE_LINK_PATTERN = "Change to issue link patterns:";

  private final CommonText flLinkPattern = new CommonText(50, 4); // NOPMD
  private final CommonField flIssueFilter = new IssueFilterField(); // NOPMD
  private final IssueURLTemplateField flURLTemplate = new IssueURLTemplateField(); // NOPMD

  protected final PropertyToInputMap propertyToInputMap = new PropertyToInputMap(false, makePropertyHandler());
  protected final GridIterator gridIter = new GridIterator(super.getUserPanel(), 2);


  public AbstractIssueTrackerSetupPanel(final String title, final boolean showURLTemplate, final boolean showLinkPattern) {
    super(title);
    // layout
    gridIter.addPair(new CommonFieldLabel(STR_ISSUE_FILTER), flIssueFilter);
    if (showLinkPattern) gridIter.addPair(new CommonFieldLabel(STR_ISSUE_LINK_PATTERN), flLinkPattern);
    if (showURLTemplate) gridIter.addPair(new CommonFieldLabel(STR_URL), flURLTemplate);
    // map to props
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.ISSUE_FILTER, flIssueFilter);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.ISSUE_URL_TEMPLATE, flURLTemplate);
    propertyToInputMap.bindPropertyNameToInput(IssueTrackerProperty.ISSUE_LINK_PATTERN, flLinkPattern);
  }


  public AbstractIssueTrackerSetupPanel(final String title) {
    this(title, true, true);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public final boolean validate() {
    clearMessage();
    final List errors = new ArrayList(5);
    // validate URL template
    flURLTemplate.validate(errors);
    // validate link pattern
    final String value = flLinkPattern.getValue();
    final List list = StringUtils.multilineStringToList(value);
    for (int i = 0, n = list.size(); i < n; i++) {
      final String patternString = (String)list.get(i);
      try {
        Pattern.compile(patternString);
      } catch (final Exception e) {
        errors.add("Pattern \"" + patternString + "\" is not a valid regular expression.");
      }
    }
    // call implementors
    doValidate(errors); // POST
    if (errors.isEmpty()) return true;
    showErrorMessage(errors);
    return false;
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   */
  protected abstract void doValidate(List errors);


  /**
   * Loads properties for given issue tracker
   *
   * @param issueTracker to load properties for
   */
  public final void load(final IssueTracker issueTracker) {
    final List props = ConfigurationManager.getInstance().getIssueTrackerProperties(issueTracker.getID());
    propertyToInputMap.setProperties(props);
  }


  public final List getUpdatedProperties() {
    return propertyToInputMap.getUpdatedProperties();
  }


  /**
   * Factory method to create IssueTrackerProperty handler to be
   * used by propertyToInputMap
   *
   * @return implementation of PropertyToInputMap.PropertyHandler
   *
   * @see PropertyToInputMap.PropertyHandler
   */
  private static PropertyToInputMap.PropertyHandler makePropertyHandler() {
    return new PropertyToInputMap.PropertyHandler() {
      private static final long serialVersionUID = -356895114891038404L;


      public Object makeProperty(final String propertyName) {
        final IssueTrackerProperty prop = new IssueTrackerProperty();
        prop.setName(propertyName);
        return prop;
      }


      public void setPropertyValue(final Object property, final String propertyValue) {
        ((IssueTrackerProperty)property).setValue(propertyValue);
      }


      public String getPropertyValue(final Object property) {
        return ((IssueTrackerProperty)property).getValue();
      }


      public String getPropertyName(final Object property) {
        return ((IssueTrackerProperty)property).getName();
      }
    };
  }
}
