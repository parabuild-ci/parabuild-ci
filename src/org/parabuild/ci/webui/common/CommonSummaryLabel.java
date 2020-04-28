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
package org.parabuild.ci.webui.common;

import viewtier.ui.Layout;

/**
 * Common label to use a caption for build results summary
 * captions. It's different from {@link CommonLabel} that it has
 * smaller height and is aligned to the right.
 */
public class CommonSummaryLabel extends CommonLabel {

  public static final int SUMMARY_LABEL_HEIGHT = 19;
  public static final int SUMMARY_LABEL_PADDING = 2;
  private static final long serialVersionUID = -1151514772677606372L;


  /**
   * Constructor
   */
  public CommonSummaryLabel() {
    this("");
  }


  /**
   * Constructor
   *
   * @param s String label title
   */
  public CommonSummaryLabel(final String s) {
    this(s, true);
  }


  /**
   * Constructor
   *
   * @param s String label title
   */
  public CommonSummaryLabel(final String s, final boolean initiallyVisible) {
    super(s);
    setHeight(SUMMARY_LABEL_HEIGHT);
    setPadding(SUMMARY_LABEL_PADDING);
    setAlignX(Layout.RIGHT);
    setVisible(initiallyVisible);
  }
}

