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
package org.parabuild.ci.webui.result;

import java.text.*;
import java.util.*;

import org.apache.commons.logging.*;

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This panel contains published build run results. Published
 * build run results are assocoated with a given result group.
 *
 * @see PublishedResultsPage
 */
final class PublishedResultsPanel extends MessagePanel {

  private static final long serialVersionUID = 7485793303084405220L;
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(PublishedResultsPanel.class); // NOPMD


  /**
   * Populates the table with content of the given result group.
   */
  public PublishedResultsPanel(final ResultGroup resultGroup) {
    this.setWidth("100%");
    final SimpleDateFormat dateFomat = new SimpleDateFormat(SystemConfigurationManagerFactory.getManager().getDateTimeFormat());
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    // group results by date and description, dates desending

    final Map result = getGrouppedPublishedStepResults(resultGroup);
    for (final Iterator i = result.entrySet().iterator(); i.hasNext();) {
      final Map.Entry entry = (Map.Entry)i.next();
      final List list = (List)entry.getValue();

      // add header
      final PublishedStepResult psr = (PublishedStepResult)list.get(0);
      final BuildRun publisherBuildRun = cm.getBuildRun(psr.getPublisherBuildRunID());
      final String caption = publisherBuildRun.getBuildName() + " #" + Integer.toString(publisherBuildRun.getBuildRunNumber()) + '@' + publisherBuildRun.getChangeListNumber();
      final Flow flHeader = new Flow();
      flHeader.add(new BoldCommonLabel(psr.getDescription()))
        .add(new CommonLabel(" published on " + dateFomat.format(psr.getPublishDate()) + " from "))
        .add(new PublishedResultLinkFlow(caption, psr.getPublisherBuildRunID(), currentUserCanSeeBuild(publisherBuildRun.getActiveBuildID())))
        .setBackground(Pages.COLOR_PANEL_HEADER_BG);
      flHeader.setWidth("100%");
      getUserPanel().add(flHeader);

      // add files
      final PublishedResultFilesTable publishedResultFilesTable = new PublishedResultFilesTable(list);
      publishedResultFilesTable.setWidth("100%");
      getUserPanel().add(publishedResultFilesTable);
      if (i.hasNext()) getUserPanel().add(WebuiUtils.makePanelDivider());
    }
  }


  private static Map getGrouppedPublishedStepResults(final ResultGroup resultGroup) {
    final Map result = new TreeMap(new Comparator() {
      public int compare(final Object o1, final Object o2) {
        final DateAndDescriptionKey k1 = (DateAndDescriptionKey)o1;
        final DateAndDescriptionKey k2 = (DateAndDescriptionKey)o2;
        final int dateComparisonResult = k1.date.compareTo(k2.date);
        if (dateComparisonResult != 0) return -dateComparisonResult;
        return k1.description.compareTo(k2.description);
      }
    });
    final List publishedStepResults = ResultGroupManager.getInstance().getPublishedStepResults(resultGroup.getID(), 200);
    for (int i = 0, n = publishedStepResults.size(); i < n; i++) {
      final PublishedStepResult publishedStepResult = (PublishedStepResult)publishedStepResults.get(i);
      final DateAndDescriptionKey key = new DateAndDescriptionKey(publishedStepResult.getPublishDate(), publishedStepResult.getDescription());
//      if (log.isDebugEnabled()) log.debug("===== handling published step result =====" );
//      if (log.isDebugEnabled()) log.debug("publishedStepResult: " + publishedStepResult);
//      if (log.isDebugEnabled()) log.debug("key: " + key);
      List list = (List)result.get(key);
      if (list == null) {
        list = new ArrayList(11);
        result.put(key, list);
      }
      list.add(publishedStepResult);
    }
    return result;
  }


  private static class DateAndDescriptionKey {

    private final Date date;
    private final String description;


    public DateAndDescriptionKey(final Date date, final String description) {
      this.date = date;
      this.description = description;
    }


    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      final DateAndDescriptionKey that = (DateAndDescriptionKey)o;

      if (!date.equals(that.date)) return false;
      if (!description.equals(that.description)) return false;

      return true;
    }


    public int hashCode() {
      int result;
      result = date.hashCode();
      result = 29 * result + description.hashCode();
      return result;
    }


    public String toString() {
      return "DateAndDescriptionKey{" +
        "date=" + date +
        ", description=" + description +
        '}';
    }
  }


  /**
   * Helper method.
   *
   * @param activeBuildID
   *
   * @return true if current user allowed to view build results
   *  with the given active build ID.
   */
  private boolean currentUserCanSeeBuild(final int activeBuildID) {
    return SecurityManager.getInstance().userCanViewBuild(SecurityManager.getInstance().getUserIDFromContext(getTierletContext()), activeBuildID);
  }
}
