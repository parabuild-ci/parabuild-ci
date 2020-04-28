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
package org.parabuild.ci.webui;

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.Issue;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonSummaryLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Shows the details of a given change lists.
 */
public final class ReleaseNoteDetailsPanel extends MessagePanel {

  private static final long serialVersionUID = -5231129495596445937L;


  public ReleaseNoteDetailsPanel(final Issue issue) {
    super(false);
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
    final String dateFormat = systemCM.getDateFormat();
    final GridIterator gi = new GridIterator(getUserPanel(), 2);
    // add issue details
    addIfValueNotBlank(gi, "Product:", issue.getProduct());
    addIfValueNotBlank(gi, "Project:", issue.getProject());
    addIfValueNotBlank(gi, "Key:", issue.getKey());
    addIfValueNotBlank(gi, "Description:", issue.getDescription());
    addIfValueNotBlank(gi, "Priority:", issue.getPriority());
    addIfValueNotBlank(gi, "Received:", formatIfNotNull(issue.getReceived(), dateFormat));
    addIfValueNotBlank(gi, "Status:", issue.getStatus());
    addIfValueNotBlank(gi, "URL:", issue.getUrl());
    addIfValueNotBlank(gi, "Closed:", formatIfNotNull(issue.getClosed(), dateFormat));
    addIfValueNotBlank(gi, "Closed by:", issue.getClosedBy());
    // add change lists if any
    final List issueChangeLists = cm.getIssueChangeLists(issue.getID());
    if (!issueChangeLists.isEmpty()) {
      final SecurityManager sm = SecurityManager.getInstance();
      final boolean showDescription = sm.userCanSeeChangeListDescriptions(getTierletContext());
      final boolean userCanSeeChangeListFiles = sm.userCanSeeChangeListFiles(getTierletContext());
      final SimpleDateFormat formatter = new SimpleDateFormat(systemCM.getDateTimeFormat(), Locale.US);
      for (final Iterator i = issueChangeLists.iterator(); i.hasNext();) {
        gi.add(new ChangelistDetailsPanel(formatter, (ChangeList)i.next(), null, true, showDescription, userCanSeeChangeListFiles), 2);
      }
    }
  }


  private static String formatIfNotNull(final Date date, final String dateFormat) {
    if (date == null) return "";
    return StringUtils.formatDate(date, dateFormat);
  }


  private static void addIfValueNotBlank(final GridIterator gi, final String caption, final String value) {
    if (StringUtils.isBlank(value)) return;
    gi.addPair(new CommonSummaryLabel(caption), new BoldCommonLabel(value));
  }
}
