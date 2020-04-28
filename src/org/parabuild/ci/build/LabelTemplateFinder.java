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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.LabelProperty;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.StringUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Find duplicate label templates
 */
public final class LabelTemplateFinder {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(LabelTemplateFinder.class); // NOPMD

  private int buildID = BuildConfig.UNSAVED_ID;
  private String template = null;
  private String foundBuildName = null;


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  public void setTemplate(final String template) {
    this.template = template;
  }


  public boolean find() throws BuildException {
    if (template == null) throw new IllegalStateException("Label template for validation was not set");

    // reset
    foundBuildName = null;

    // generator w/test values
    final BuildLabelNameGenerator nameGenerator = new BuildLabelNameGenerator();
    nameGenerator.setBuildName("test_name");
    nameGenerator.setBuildNumber(0);
    nameGenerator.setChangeListNumber("0");
    nameGenerator.setBuildTimestamp(new Date());
    nameGenerator.setLabelTemplate(template);
    final String ourLabel = nameGenerator.generateLabelName();
    // if (log.isDebugEnabled()) log.debug("ourLabel: " + ourLabel);
    // traverse builds
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    // NOTE: vimeshev - 09/21/2004 - here we check only existing
    // builds, so if there was a build with the same template
    // but it was deleted, we will not find duplicate.
    final List builds = cm.getExistingBuildConfigurationsIDs();
    for (final Iterator i = builds.iterator(); i.hasNext();) {
      final int id = (Integer) i.next();
      if (id == buildID) continue;
      final BuildConfig config = cm.getBuildConfiguration(id);
      final LabelProperty lpType = cm.getLabelSetting(config.getBuildID(), LabelProperty.LABEL_TYPE);
      if (lpType != null) {
        final LabelProperty theirTemplate = cm.getLabelSetting(config.getBuildID(), LabelProperty.LABEL_CUSTOM_VALUE);
        if (!templateIsBlank(theirTemplate)) {
          nameGenerator.setLabelTemplate(theirTemplate.getPropertyValue());
          nameGenerator.setBuildName(config.getBuildName()); // see Bug #715 - we use "others" build name because it a "variable".
          final String theirLabel = nameGenerator.generateLabelName();
          // if (log.isDebugEnabled()) log.debug("theirLabel: " + theirLabel);
          if (theirLabel.equals(ourLabel)) {
            foundBuildName = config.getBuildName();
            return true;
          }
        }
      }
    }
    return false;
  }


  /**
   * @return found build name, or null if not found
   */
  public String getFoundBuildName() {
    return foundBuildName;
  }


  /**
   * Helper method.
   */
  private static boolean templateIsBlank(final LabelProperty templateProperty) {
    return templateProperty == null
     || StringUtils.isBlank(templateProperty.getPropertyValue());
  }


  public String toString() {
    return "LabelTemplateFinder{" +
      "buildID=" + buildID +
      ", template='" + template + '\'' +
      ", foundBuildName='" + foundBuildName + '\'' +
      '}';
  }
}
