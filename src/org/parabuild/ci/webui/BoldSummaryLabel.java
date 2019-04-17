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

import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonSummaryLabel;
import viewtier.ui.Color;
import viewtier.ui.Layout;

/**
 * Common bold label to use with this panel. It's different
 * that it has smaller height.
 */
final class BoldSummaryLabel extends BoldCommonLabel {

  private static final long serialVersionUID = -7699263132924700120L;


  public BoldSummaryLabel() {
    this("");
  }


  public BoldSummaryLabel(final boolean initiallyVisible) {
    this(initiallyVisible, null);
  }


  public BoldSummaryLabel(final boolean initiallyVisible, final Color foreground) {
    this();
    setVisible(initiallyVisible);
    if (foreground != null) setForeground(foreground);
  }


  public BoldSummaryLabel(final String s) {
    super(s);
    setHeight(CommonSummaryLabel.SUMMARY_LABEL_HEIGHT);
    setPadding(CommonSummaryLabel.SUMMARY_LABEL_PADDING);
    setAlignX(Layout.LEFT);
  }
}
