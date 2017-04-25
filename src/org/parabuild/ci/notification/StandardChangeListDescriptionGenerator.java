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
package org.parabuild.ci.notification;

import java.text.*;
import java.util.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 * @see ChangeListDescriptionGenerator
 */
public final class StandardChangeListDescriptionGenerator implements ChangeListDescriptionGenerator, CommonConstants {

  private final BuildRun buildRun;
  private final SimpleDateFormat formatter;
  private final ConfigurationManager cm;
  private final SystemConfigurationManager scm;


  /**
   * Constructor
   *
   * @param buildRun
   */
  public StandardChangeListDescriptionGenerator(final BuildRun buildRun) {
    this.buildRun = buildRun;
    cm = ConfigurationManager.getInstance();
    scm = SystemConfigurationManagerFactory.getManager();
    formatter = new SimpleDateFormat(scm.getDateTimeFormat(), Locale.US);
  }


  /**
   * Creates a text description of the changes built
   *
   * @return StringBuffer with text
   */
  public StringBuffer generateChangeListDescription() {
    final StringBuffer result = new StringBuffer(1000);

    // get changes
    final List changeLists = cm.getChangeListsOrderedByDate(buildRun.getBuildRunID());
    if (changeLists.isEmpty()) {
      result.append("There are no new changes since previos build.");
      return result; // empty result
    }

    // traverse changes
    final int changeListTruncateLength = scm.getSystemPropertyValue(SystemProperty.CHANGE_LIST_DESCRIPTION_QUOTE_LENGTH, ConfigurationConstants.DEFAULT_CHANGE_LIST_DESCRIPTION_QUOTE_LENGTH);
    final boolean sendFileNames = cm.getBuildAttributeValue(buildRun.getActiveBuildID(), BuildConfigAttribute.SEND_FILE_DETAILS, BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED);
    String prevUser = "";
    for (final Iterator iter = changeLists.iterator(); iter.hasNext();) {
      final ChangeList changeList = (ChangeList)iter.next();
      final String currentUser = changeList.getUser();
      final String currentDescr = changeList.getDescription();
      if (!currentUser.equals(prevUser)) {
        result.append("Change(s) by ").append(currentUser).append(" :");
        result.append(STR_CRCR);
        prevUser = currentUser;
      }
      result.append(" - ").append(truncate(currentDescr, changeListTruncateLength)).append(';').append('\n');
      result.append("   ").append(formatTimeAndNumber(changeList)).append('\n');

      // output change type and file if necessary
      if (sendFileNames) {
        final List changes = cm.getChanges(changeList);
        if (changes.isEmpty()) {
          result.append('\n'); // empty line if file list is empty
        } else {
          result.append("   ").append("Files:").append('\n');
          for (final Iterator j = changes.iterator(); j.hasNext();) {
            final Change change = (Change)j.next();
            result.append("   ");
            result.append(change.getChangeTypeAsString());
            result.append(": ");
            result.append(change.getFilePath());
            result.append('#');
            result.append(change.getRevision());
            result.append('\n'); // next line
          }
          result.append('\n'); // empty line after files
        }
      } else {
        result.append('\n'); // empty line if don't have to print files
      }
    }

    return result;
  }


  private String formatTimeAndNumber(final ChangeList changeList) {
    return "Time: " + formatter.format(changeList.getCreatedAt()) + "; Change list: " + changeList.getNumber();
  }


  private String truncate(final String description, final int length) {
    // get rid of breaks
    String result = description.replace('\n', ' ').trim();
    // cut
    int cutPosition = Math.min(length, result.length());
    if (cutPosition < 0) cutPosition = 0;
    result = result.substring(0, cutPosition);
    // return result
    result = result.length() == 0 ? "Description was not provided" : result;
    //if (!result.endsWith(".")) result += ".";
    return result;
  }


  public String toString() {
    return "StandardChangeListDescriptionGenerator{" +
      "buildRun=" + buildRun +
      ", formatter=" + formatter +
      ", cm=" + cm +
      ", scm=" + scm +
      '}';
  }
}
