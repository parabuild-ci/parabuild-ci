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
package org.parabuild.ci.webui.merge;

import java.util.*;

import org.parabuild.ci.merge.MergeStatus;
import org.parabuild.ci.webui.common.CommandLinkWithSeparator;
import org.parabuild.ci.webui.common.CommonCommandLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;

/**
 */
final class MergeCommandFlow extends Flow {

  private static final long serialVersionUID = 6794511404459228441L;

  private static final String CAPTION_EDIT = "Edit";
  private static final String CAPTION_STOP = "Stop";
  private static final String CAPTION_RESUME = "Resume";
  private static final String CAPTION_COMMANDS = "Commands";
  private static final String CAPTION_MERGE_REPORT = "Integration Report";
  private static final String CAPTION_QUEUE = "Queue";


  private final CommandLinkWithSeparator lnkMergeReport = new CommandLinkWithSeparator(new CommonCommandLink(CAPTION_MERGE_REPORT, Pages.PAGE_MERGE_REPORT));
  private final CommandLinkWithSeparator lnkQueueReport = new CommandLinkWithSeparator(new CommonCommandLink(CAPTION_QUEUE, Pages.PAGE_MERGE_QUEUE_REPORT));
  private final CommandLinkWithSeparator lnkEdit = new CommandLinkWithSeparator(new CommonCommandLink(CAPTION_EDIT, Pages.PAGE_MERGE_EDIT));
  private final CommandLinkWithSeparator lnkStop = new CommandLinkWithSeparator(new CommonCommandLink(CAPTION_STOP, Pages.PAGE_MERGE_STOP));
  private final CommandLinkWithSeparator lnkResume = new CommandLinkWithSeparator(new CommonCommandLink(CAPTION_RESUME, Pages.PAGE_MERGE_RESUME));
  private final CommandLinkWithSeparator lnkCommands = new CommandLinkWithSeparator(new CommonCommandLink(CAPTION_COMMANDS, Pages.PAGE_MERGE_COMMANDS));


  public MergeCommandFlow() {
    add(lnkMergeReport);
    add(lnkQueueReport);
    add(lnkEdit);
    add(lnkResume);
    add(lnkStop);
    add(lnkCommands);
    lnkCommands.hideSeparator();

    setInitialVisibility();
  }


  private void setInitialVisibility() {
    lnkStop.setVisible(false);
    lnkResume.setVisible(false);
  }


  public void setMergeID(final int mergeID) {
    final Properties props = WebuiUtils.makeMergeIDParameters(mergeID);
    lnkMergeReport.setParameters(props);
    lnkQueueReport.setParameters(props);
    lnkEdit.setParameters(props);
    lnkCommands.setParameters(props);
    lnkResume.setParameters(props);
    lnkStop.setParameters(props);
  }


  /**
   * Sets merge status.
   *
   * The flow hides/shows commands according to the status.
   *
   * @param status
   */
  public void setMergeStatus(final MergeStatus status) {
    if (status.equals(MergeStatus.PAUSED)) {

      lnkResume.setVisible(true);
      lnkStop.setVisible(false);

    } else if (status.equals(MergeStatus.CHECKING_OUT)

      || status.equals(MergeStatus.GETTING_CHANGES)
      || status.equals(MergeStatus.INITIALIZING)
      || status.equals(MergeStatus.MERGING)
      || status.equals(MergeStatus.STARTING)
      || status.equals(MergeStatus.VALIDATING)
      ) {

      lnkStop.setVisible(true);
      lnkResume.setVisible(false);

    } else {

      setInitialVisibility();
      
    }
  }
}
