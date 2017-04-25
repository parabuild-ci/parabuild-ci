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

import com.dautelle.util.Enum;
import org.parabuild.ci.common.CommonConstants;
import org.parabuild.ci.common.StringUtils;
import viewtier.ui.Border;
import viewtier.ui.Color;
import viewtier.ui.Component;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.OrderedList;
import viewtier.ui.Panel;

import java.util.Collection;
import java.util.List;

/**
 * Message panel is a composite panel consisting of two panels, a
 * message panel laying on top of the panel, and a content panel,
 * where user constent goes to.
 * <p/>
 * Main purpose of this panel is to provide a unified ability to
 * display error messages in case of user errors.
 */
public class MessagePanel extends Panel implements CommonConstants {

  public static final Style PAGE_HEADER = new Style(0, "Page header");
  public static final Style PAGE_SECTION = new Style(1, "Page subheader");

  private static final long serialVersionUID = -5929100408109746796L; // NOPMD

  private final Component headerDivider = WebuiUtils.makeHorizontalDivider(15);
  private final Label lbTitle = new BoldCommonLabel();
  private final Panel internalContentPanel = new Panel();
  private final Panel messagePanel = new Panel();
  private final Panel userPanel = new Panel();


  /**
   * Creates message panel without title.
   */
  public MessagePanel(final boolean enableContentBorder) {
    final Layout layout = new Layout(0, 0, 1, 1);

    // add title
    add(lbTitle, layout);
    hideTitle();
    lbTitle.setWidth("100%");

    // internal content
    layout.positionY++;
    add(internalContentPanel, layout);
    internalContentPanel.setWidth("100%");

    // add header divider to internal content
    headerDivider.setWidth("100%");
    headerDivider.setVisible(false);
    internalContentPanel.add(headerDivider);

    // add message panel to internal content
    messagePanel.setWidth("100%");
    messagePanel.setVisible(false);
    internalContentPanel.add(messagePanel);

    // add user panel to internal content
    userPanel.setWidth("100%");
    internalContentPanel.add(userPanel);

    showContentBorder(enableContentBorder);
  }


  /**
   * Creates message panel with title displayed
   */
  public MessagePanel(final String title) {
    this(true);
    setTitle(title);
  }


  public MessagePanel() {
    this(true);
  }


  /**
   * Shows/hides content border
   */
  public final void showContentBorder(final boolean show) {
    if (show) {
      internalContentPanel.setBorder(Border.ALL, 1, Pages.COLOR_PANEL_BORDER);
    } else {
      internalContentPanel.setBorder(Border.ALL, 0, Pages.COLOR_PAGE_HEADER_BACKGROUND);
    }
  }


  public final void setTitle(final String title) {
    lbTitle.setText("  " + title + "  ");
    lbTitle.setVisible(true);
    lbTitle.setHeight(25);
    setTitleBackground(Pages.COLOR_PANEL_HEADER_BG);
  }


  /**
   * Sets title background color.
   *
   * @param color to set.
   */
  public final void setTitleBackground(final Color color) {
    lbTitle.setBackground(color);
  }


  /**
   * Sets title foreground color.
   *
   * @param color to set.
   */
  public final void setTitleForeground(final Color color) {
    lbTitle.setForeground(color);
  }


  /**
   * Shows/hides header divider
   *
   * @param show
   */
  public final void showHeaderDivider(final boolean show) {
    headerDivider.setVisible(show);
  }


  /**
   * Dispalays error message
   *
   * @param error
   */
  public final void showErrorMessage(final String error) {
    showMessage(error, Pages.COLOR_ERROR_FG);
  }


  /**
   * Dispalays error message
   *
   * @param error
   */
  public final void showErrorMessage(final StringBuffer error) {
    showErrorMessage(error.toString());
  }


  /**
   * Dispalays error messages
   *
   * @param errors
   */
  public final void showErrorMessage(final String[] errors) {
    showMessages(errors, Pages.COLOR_ERROR_FG);
  }


  /**
   * Dispalays error messages
   *
   * @param errors
   */
  public final void showErrorMessage(final List errors) {
    showErrorMessage(StringUtils.toStringArray(errors));
  }


  /**
   * Dispalays info message
   *
   * @param msg
   */
  public final void showInfoMessage(final String msg) {
    showMessage(msg, Color.Navy);
  }


  /**
   * Dispalays OK message in green color.
   *
   * @param msg
   */
  public final void showOKMessage(final String msg) {
    showMessage(msg, Color.DarkGreen);
  }


  public final void clearMessage() {
    messagePanel.clear();
    messagePanel.setVisible(false);
  }


  /**
   * Returns reference to the user content Panel. This panel
   * should be used to add page-specific content.
   *
   * @return Panel user content panel
   */
  public final Panel getUserPanel() {
    return userPanel;
  }


  /**
   * Helper method to show a message with the given color
   *
   * @param message
   * @param foreground
   */
  private void showMessage(final String message, final Color foreground) {
    messagePanel.clear();
    messagePanel.setHeight(20);
    messagePanel.setForeground(foreground);
    messagePanel.add(new Label(message));
    messagePanel.setVisible(true);
  }


  /**
   * Helper method to show a message with the given color
   *
   * @param messages
   * @param foreground
   */
  private void showMessages(final String[] messages, final Color foreground) {
    final BoldCommonLabel[] listItems = new BoldCommonLabel[messages.length];
    for (int i = 0; i < messages.length; i++) {
      listItems[i] = new BoldCommonLabel(messages[i]);
    }
    messagePanel.clear();
    messagePanel.setHeight(20);
    messagePanel.setForeground(foreground);
    messagePanel.add(new OrderedList(listItems));
    messagePanel.setVisible(true);
  }


  /**
   * Hides title.
   */
  public final void hideTitle() {
    lbTitle.setVisible(false);
    lbTitle.setHeight(0);
    // NOTE: simeshev@parabuilci.org - 07/29/2003 - current version of viewtier
    // displays thin hor line with bg color even if component is hidden, so we
    // reset background to panel's background.
    lbTitle.setBackground(getBackground());
  }


  /**
   * Sets this message box style.
   *
   * @param style
   */
  public final void setStyle(final Style style) {
    if (style.equals(PAGE_HEADER)) {
      setTitleBackground(Pages.COLOR_PAGE_HEADER_BACKGROUND);
      setTitleForeground(Pages.COLOR_PAGE_HEADER_FOREGROUND);
      lbTitle.setText(lbTitle.getText().trim());
      lbTitle.setBorder(Border.BOTTOM, 1, Pages.COLOR_PAGE_HEADER_FOREGROUND);
      showContentBorder(false);
      showHeaderDivider(true);
      lbTitle.setFont(Pages.FONT_PAGE_HEADER);
    } else if (style.equals(PAGE_SECTION)) {
      setTitleBackground(Pages.COLOR_PAGE_SECTION_BACKGROUND);
      setTitleForeground(Pages.COLOR_PAGE_SECTION_FOREGROUND);
      lbTitle.setText(lbTitle.getText().trim());
      lbTitle.setBorder(Border.BOTTOM, 1, Pages.COLOR_PAGE_SECTION_FOREGROUND);
      showContentBorder(false);
      showHeaderDivider(true);
      lbTitle.setFont(Pages.FONT_PAGE_SECTION);
    }
  }


  private static final class Style extends Enum {

    public static final Collection VALUES = getInstances(Style.class);


    /**
     * Constructor
     */
    public Style(final long l, final String s) { // NOPMD
      super(l, s);
    }


    public String toString() {
      return super.getName();
    }
  }
}
