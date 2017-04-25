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

import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.webui.AnnotatedCommandLink;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.StatelessTierlet;

/**
 * Lists merge commands
 */
public final class MergeCommandListPage extends BaseMergePage implements StatelessTierlet {

  private static final long serialVersionUID = 4542828122356933097L;
  private static final String CAPTION_DELETE = "Delete";
  private static final String CAPTION_EDIT = "Edit";
  private static final String CAPTION_START = "Start";
  private static final String CAPTION_STOP = "Stop";
  private static final String CAPTION_RESUME = "Resume";

  private final AnnotatedCommandLink lnkDelete = new AnnotatedCommandLink(CAPTION_DELETE, Pages.PAGE_MERGE_DELETE, CAPTION_DELETE, true);
  private final AnnotatedCommandLink lnkEdit = new AnnotatedCommandLink(CAPTION_EDIT, Pages.PAGE_MERGE_EDIT, CAPTION_EDIT, true);
  private final AnnotatedCommandLink lnkStart = new AnnotatedCommandLink(CAPTION_START, Pages.PAGE_MERGE_START, CAPTION_START, true);
  private final AnnotatedCommandLink lnkStop = new AnnotatedCommandLink(CAPTION_STOP, Pages.PAGE_MERGE_STOP, CAPTION_STOP, true);
  private final AnnotatedCommandLink lnkResume = new AnnotatedCommandLink(CAPTION_RESUME, Pages.PAGE_MERGE_RESUME, CAPTION_RESUME, true);


  public MergeCommandListPage() {
    super(FLAG_FLOATING_WIDTH | FLAG_SHOW_PAGE_HEADER_LABEL | FLAG_SHOW_HEADER_SEPARATOR);
    baseContentPanel().setWidth("100%");

    final MessagePanel cp = baseContentPanel();
    cp.add(lnkDelete);
    cp.add(lnkEdit);
    cp.add(lnkStart);
    cp.add(lnkStop);
    cp.add(lnkResume);
  }


  /**
   * This method should be implemented by inheriting classes
   *
   * @param parameters
   * @param mergeConfiguration
   * @return Result
   */
  protected Result executeMergePage(final Parameters parameters, final MergeConfiguration mergeConfiguration) {
    setPageHeader("Commands For: " + mergeConfiguration.getName());

    //
    final Properties props = WebuiUtils.makeMergeIDParameters(mergeConfiguration.getActiveMergeID());
    lnkEdit.setParameters(props);
    lnkDelete.setParameters(props);
    lnkStart.setParameters(props);
    lnkStop.setParameters(props);
    lnkResume.setParameters(props);

    return Result.Done();
  }
}
