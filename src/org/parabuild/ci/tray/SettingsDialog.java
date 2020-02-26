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
package org.parabuild.ci.tray;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.util.*;

/**
 * Modal dialog to enter tray settings.
 */
final class SettingsDialog extends JDialog {

  private static final Log log = LogFactory.getLog(SettingsDialog.class);

  private static final String CAPTION_SERVERS = "Servers:";
  private static final String CAPTION_POLL_INTERVAL_SECONDS = "Poll interval, seconds:";
  private static final String CAPTION_DEBUG_LOGGING = "Debug logging:";
  private static final String CAPTION_SAVE = "Save";
  private static final String CAPTION_CANCEL = "Cancel";
  private static final long serialVersionUID = 1897465782576832897L;

  private final JTextField flServerList = new JTextField(30); // NOPMD
  private final JTextField flPollInterval = new JTextField(3); // NOPMD
  private final JCheckBox cbShowInactiveBuilds = new JCheckBox(); // NOPMD
  private final JCheckBox cbDebugLogging = new JCheckBox(); // NOPMD
  private final JButton btnSave = new JButton(CAPTION_SAVE); // NOPMD
  private final JButton btnCancel = new JButton(CAPTION_CANCEL); // NOPMD

  private boolean isOK = false;
  private static final String CAPTION_SHOW_INACTIVE_BUILDS = "Show inactive builds";


  /**
   * Creates a modal dialog to enter tray settings.
   */
  public SettingsDialog() {
    super((Frame)null, "Parabuild Tray Client Settings", true);

    //
    flServerList.setToolTipText("Server list in host:port format");
    flPollInterval.setToolTipText("Interval to poll server's status, seconds");
    cbDebugLogging.setToolTipText("If checked, debug messages will be written into a debug log");
    cbShowInactiveBuilds.setToolTipText("If checked, inactive builds are shown");

    final JDialog owner = this;

    // set up save listener
    btnSave.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        if (log.isDebugEnabled()) log.debug("e: " + e);
        // validate
        JComponent firstFocusIfError = null;
        final List errors = new ArrayList(3);
        if (StringUtils.isBlank(flServerList.getText())) {
          firstFocusIfError = flServerList;
          errors.add("Server list cannot be blank");
        }
        if (!StringUtils.isValidInteger(flPollInterval.getText())) {
          if (firstFocusIfError == null) firstFocusIfError = flPollInterval;
          errors.add("Poll interval should be a valid positive integer");
        }
        if (errors.isEmpty()) {
          // exit
          isOK = true;
          owner.setVisible(false);
          owner.dispose();
        } else {
          // show errors and continue
          final StringBuilder message = new StringBuilder(200);
          for (int i = 0, n = errors.size(); i < n; i++) {
            message.append("- ").append((String)errors.get(i)).append('\n');
          }
          JOptionPane.showMessageDialog(owner, message.toString(), "Please correct input errors", JOptionPane.ERROR_MESSAGE);
          // TODO: set focus to first error
        }
      }
    });

    // set up cancel listener
    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        if (log.isDebugEnabled()) log.debug("e: " + e);
        isOK = false;
        owner.setVisible(false);
        owner.dispose();
      }
    });

    // layout
    final Container contentPane = getContentPane();
    final GridBagLayout mgr = new GridBagLayout();
    contentPane.setLayout(mgr);
    final GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.insets = new Insets(4, 2, 4, 2);
    c.anchor = GridBagConstraints.EAST;
    contentPane.add(new JLabel(CAPTION_SERVERS), c);

    c.gridx = 1;
    c.anchor = GridBagConstraints.WEST;
    contentPane.add(flServerList, c);

    // poll interval

    c.gridx = 0;
    c.gridy++;
    c.anchor = GridBagConstraints.EAST;
    contentPane.add(new JLabel(CAPTION_POLL_INTERVAL_SECONDS), c);

    c.gridx = 1;
    c.anchor = GridBagConstraints.WEST;
    contentPane.add(flPollInterval, c);

    // show inactive builds

    c.gridx = 0;
    c.gridy++;
    c.anchor = GridBagConstraints.EAST;
    contentPane.add(new JLabel(CAPTION_SHOW_INACTIVE_BUILDS), c);

    c.gridx = 1;
    c.anchor = GridBagConstraints.WEST;
    contentPane.add(cbShowInactiveBuilds, c);

    // debug logging

    c.gridx = 0;
    c.gridy++;
    c.anchor = GridBagConstraints.EAST;
    contentPane.add(new JLabel(CAPTION_DEBUG_LOGGING), c);

    c.gridx = 1;
    c.anchor = GridBagConstraints.WEST;
    contentPane.add(cbDebugLogging, c);

    c.gridx = 0;
    c.gridy++;
    c.anchor = GridBagConstraints.EAST;
    contentPane.add(btnSave, c);

    c.gridx = 1;
    c.anchor = GridBagConstraints.WEST;
    contentPane.add(btnCancel, c);

    // center
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final Dimension dialogSize = getPreferredSize();
    setLocation((screenSize.width >> 1) - (dialogSize.width >> 1), (screenSize.height >> 1) - (dialogSize.height >> 1));
    setResizable(false);
  }


  /**
   * Sets poll interval.
   *
   * @param pollInterval
   */
  public void setPollInterval(final int pollInterval) {
    this.flPollInterval.setText(Integer.toString(pollInterval));
  }


  public int getPollInterval() {
    if (StringUtils.isValidInteger(flPollInterval.getText())) {
      return Integer.parseInt(flPollInterval.getText());
    } else {
      return Tray.DEFAULT_POLL_INTERVAL;
    }
  }


  public boolean isOK() {
    return isOK;
  }


  public String getServerList() {
    return flServerList.getText();
  }


  public void setServerList(final String list) {
    flServerList.setText(list);
  }


  public void setDebugLogging(final boolean set) {
    cbDebugLogging.setSelected(set);
  }


  public boolean isDebugLogging() {
    return cbDebugLogging.isSelected();
  }


  /**
   * @return true if inactive builds should be shown.
   */
  public boolean isShowInactiveBuilds() {
    return cbShowInactiveBuilds.isSelected();
  }


  /**
   * @param show true if inactive builds should be shown.
   */
  public void setShowInactiveBuilds(final boolean show) {
    this.cbShowInactiveBuilds.setSelected(show);
  }
}
