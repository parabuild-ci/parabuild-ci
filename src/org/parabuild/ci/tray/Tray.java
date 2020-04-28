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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;
import org.parabuild.ci.Version;
import org.parabuild.ci.services.Log4jConfigurator;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Tray is a main class that perform polling of Parabuild
 * servers.
 * <p/>
 * It hold the main  poll loop. Upon exit the polling the
 * main method exits.
 */
public final class Tray implements ActionListener, ItemListener {

  private static final Log log = LogFactory.getLog(Tray.class);

  private static final String CAPTION_ABOUT = "About...";
  private static final String CAPTION_EXIT = "Exit";
  private static final String CAPTION_SETTINGS = "Settings...";

  // settings
  private static final String PROP_SERVER_LIST = "parabuild.server.list";
  private static final String PROP_SHOW_INACTIVE = "parabuild.show.inactive.builds";
  private static final String PROP_DEBUG_LOGGING = "parabuild.debug.logging";
  private static final String PROP_POLL_INTERVAL = "parabuild.poll.interval";

  // defaults

  // error messages
  private static final String ERROR_LOADING_TRAY_PROPERTIES = "Error loading Parabuild tray properties";
  private static final String ERROR_STORING_TRAY_PROPERTIES = "Error storing Parabuild tray properties";

  private static final String TRAY_PROPERTIES_FILE = "paratray.properties";
  public static final int DEFAULT_POLL_INTERVAL = 15;


  private final File configDir;
  private final JPopupMenu menu = new JPopupMenu("Parabuild tray");
  private final TrayIcon trayIcon = new TrayIcon(makeIcon(TrayImageResourceCollection.IMAGE_ACCESS_FORBIDDEN), "Parabuild status", menu);
  private final Map statusMap = new HashMap(11);
  private JMenuItem noBuildsAvailable = null;

  private List servers = new ArrayList(11); // initiallty set to empty list
  private int pollInterval = DEFAULT_POLL_INTERVAL;  // initially set to default poll interval
  private boolean showInactiveBuilds = true; // initially set not to show


  /**
   * Constructor.
   *
   * @param configDir
   */
  private Tray(final File configDir) {
    this.configDir = configDir;
  }


  /**
   * Main tray method. Just creates an instance of this
   * class and calls execute method.
   */
  public static void main(final String[] args) {

    // get home from argv
    File commandLineHome = null;
    if (args.length > 0) {
      final String trayExe = args[0];
      if (log.isDebugEnabled()) log.debug("trayExe: " + trayExe);
      if (!StringUtils.isBlank(trayExe)) {
        final File exeFile = new File(trayExe);
        commandLineHome = exeFile.getParentFile();
        if (log.isDebugEnabled()) log.debug("commandLineHome: " + commandLineHome);
      }
    }

    // get home from system var set by installer
    if (commandLineHome == null) {
      final String trayHomeProperty = System.getProperty("tray.home");
      if (!StringUtils.isBlank(trayHomeProperty)) {
        commandLineHome = new File(trayHomeProperty);
      }
    }

    if (commandLineHome == null) {
      final String userHomeProperty = System.getProperty("user.home");
      final File userHomeFile = new File(userHomeProperty);
      commandLineHome = new File(userHomeFile, "/paratray");
    }

    // config dir
    final File configDir = new File(commandLineHome, "/config");
    configDir.mkdirs();

    // logs dir
    final File logDir = new File(commandLineHome, "/logs");
    logDir.mkdirs();

    // set log dir
    System.setProperty("catalina.base", logDir.getPath());

    new Tray(configDir).execute();
  }


  /**
   * Executes tray service loop.
   */
  private void execute() {

    // load configuration properties
    loadProperties();

    // display startup message
    log.info("Starting up tray client for " + Version.versionToString(true));

    // prepare
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (final Exception e) {
      log.warn("Exception while setting up look and feel", e);
    }
    if (Integer.parseInt(System.getProperty("java.version").substring(2, 3)) >= 5) {
      System.setProperty("javax.swing.adjustPopupLocationToFit", "false");
    }

    // set up tray icon
    trayIcon.setIconAutoSize(true);

    // add tray icon
    SystemTray.getDefaultSystemTray().addTrayIcon(trayIcon);

    noBuildsAvailable = makeNoBuildsAvialableMenuItem();
    noBuildsAvailable.setEnabled(false);
    menu.add(noBuildsAvailable);

    // "About" menu item
    menu.addSeparator();
    final JMenuItem miAbout = new JMenuItem(CAPTION_ABOUT);
    miAbout.addActionListener(this);
    menu.add(miAbout);

    // "Settings" menu item
    menu.addSeparator();
    final JMenuItem miSettings = new JMenuItem(CAPTION_SETTINGS);
    miSettings.addActionListener(this);
    menu.add(miSettings);

    // "Quit" menu item
    menu.addSeparator();
    final JMenuItem miExit = new JMenuItem(CAPTION_EXIT);
    miExit.addActionListener(this);
    menu.add(miExit);

    // main loop
    poll();
  }


  /**
   * Polls the list of servers for statuses and updates the
   * content of the menu accordingly.
   */
  private void poll() {
    log.debug("Entering main loop");
    while (true) {

      // go through the list of configured servers

      boolean isDirty = false;
      final List serversToProcess = servers; // create a reference copy so that async updates don't affect us;
      for (int i = 0; i < serversToProcess.size(); i++) {
        try {
          // get build statuses
          final String hostPort = (String)servers.get(i);
//          if (log.isDebugEnabled()) log.debug("hostPort: " + hostPort);
          final BuildStatusServiceLocator serviceLocator = new BuildStatusServiceLocator(hostPort, null, null);
          final BuildStatusService webService = serviceLocator.getWebService();
          final List remoteStatusList = webService.getBuildStatusList();

          // status list to map
          final Map remoteStatusMap = new HashMap(11);
          for (final Iterator j = remoteStatusList.iterator(); j.hasNext();) {
            final BuildStatus remoteStatus = (BuildStatus)j.next();
            remoteStatusMap.put(new Integer(remoteStatus.getActiveBuildID()), remoteStatus);
          }

          // collect build IDs that were removed
          final List deleted = new ArrayList(11);
          for (final Iterator j = statusMap.keySet().iterator(); j.hasNext();) {
            final StatusMapKey key = (StatusMapKey)j.next();
            if (key.getHostPort().equals(hostPort)) {
              final BuildStatus remoteStatus = (BuildStatus)remoteStatusMap.get(key.getBuildID());
              if (remoteStatus == null) {
                // for the the host/port being processes the
                // build ID was not found in the current remote
                // statuses (deleted/lost visibility)
                deleted.add(key);
              } else {
                // remote exists, check if we should delete
                // because status is inactive and we should
                // not show inactive.
                if (!showInactiveBuilds && remoteStatus.isInactive()) {
                  deleted.add(key);
                }
              }
            }
          }

          final MenuStatusBuilder menuStatusBuilder = new MenuStatusBuilder();
          final List added = new ArrayList(11);
          for (final Iterator j = remoteStatusList.iterator(); j.hasNext();) {
            final BuildStatus remoteStatus = (BuildStatus)j.next();
            if (!showInactiveBuilds && remoteStatus.isInactive()) continue;
            final Integer remoteBuildID = new Integer(remoteStatus.getActiveBuildID());
            final MenuStatusHolder menuStatusHolder = (MenuStatusHolder)statusMap.get(new StatusMapKey(hostPort, remoteBuildID));
            if (menuStatusHolder == null) {
              added.add(remoteStatus);
            } else {
              // find if an update is needed
              final MenuStatus newMenuStatus = menuStatusBuilder.makeMenuStatus(remoteStatus);
              final MenuStatus menuStatus = menuStatusHolder.getMenuStatus();
              if (!newMenuStatus.equals(menuStatus)) {
//                System.out.println("DEBUG: newMenuStatus: " + newMenuStatus);
                menuStatusHolder.setBuildStatus(remoteStatus);
                menuStatusHolder.setMenuStatus(newMenuStatus);
                final JMenuItem menuItem = menuStatusHolder.getMenuItem();
                menuItem.setText(newMenuStatus.getCaption());
                menuItem.setIcon(makeIcon(newMenuStatus.getImage()));
                isDirty = true;
              }
            }
          }

          // process deleted, delete containd build IDs for the current hostPort
          if (!deleted.isEmpty()) {
            for (int j = 0; j < deleted.size(); j++) {
              final MenuStatusHolder menuStatusHolder = (MenuStatusHolder)statusMap.remove(deleted.get(j));
              if (menuStatusHolder != null) {
                menu.remove(menuStatusHolder.getMenuItem());
              }
            }
            isDirty = true;
          }

          // process added
          // REVIEWME: simeshev@parabuilci.org -> sorting
          if (!added.isEmpty()) {
            for (int j = 0; j < added.size(); j++) {
              final BuildStatus buildStatus = (BuildStatus)added.get(j);
              final MenuStatus menuStatus = menuStatusBuilder.makeMenuStatus(buildStatus);
              final JMenuItem menuItem = new JMenuItem(menuStatus.getCaption(), makeIcon(menuStatus.getImage()));
              menuItem.addActionListener(new BuildStatusMenuItemActionListener(menuItem, hostPort, buildStatus.getActiveBuildID()));
              statusMap.put(new StatusMapKey(hostPort, buildStatus.getActiveBuildID()), new MenuStatusHolder(buildStatus, menuStatus, menuItem));
              menu.add(menuItem, 0);
            }
            isDirty = true;
          }
        } catch (final Exception e) {
          if (log.isDebugEnabled())
            log.debug("Error while communicating with Parabuild server: " + StringUtils.toString(e), e);
        }
      }

      // adjust "summarised" tray's icon
      adjustSummarisedTrayIcon();

      // validate menu
      if (isDirty) {
        if (statusMap.isEmpty()) {
          addNoBuildsAvialable();
        } else {
          removeNoBuildsAvialable();
        }
        menu.validate();
      }

      // sleep
      synchronized (this) {
        try {
          wait(pollInterval * 1000);
        } catch (final InterruptedException e) {
          break;
        }
      }
    }
  }


  private synchronized void loadProperties() {
    Properties properties;
    try {
      properties = loadPropertiesFile();
    } catch (final IOException e) {
      log.error("Error loading properties", e);
      properties = new Properties(); // empty
    }
    final ServerListParser serverListParser = new ServerListParser();
    final String stringPollInterval = properties.getProperty(PROP_POLL_INTERVAL, "15");
    servers = serverListParser.parse(properties.getProperty(PROP_SERVER_LIST));
    pollInterval = StringUtils.isValidInteger(stringPollInterval) ? Integer.parseInt(stringPollInterval) : DEFAULT_POLL_INTERVAL;
    this.showInactiveBuilds = Boolean.valueOf(properties.getProperty(PROP_SHOW_INACTIVE, Boolean.TRUE.toString()));
    // set logging
    try {
      Log4jConfigurator.getInstance().initialize(Boolean.valueOf(properties.getProperty(PROP_DEBUG_LOGGING, Boolean.FALSE.toString())));
    } catch (final IOException e) {
      log.warn("Error while loading log4j settings", e);
    }
  }


  /**
   * Removes "no builds avialble message".
   */
  private void removeNoBuildsAvialable() {
    if (noBuildsAvailable != null) {
      menu.remove(noBuildsAvailable);
      noBuildsAvailable = null;
    }
  }


  /**
   * Adds "no builds avialble message".
   */
  private void addNoBuildsAvialable() {
    if (noBuildsAvailable == null) {
      noBuildsAvailable = makeNoBuildsAvialableMenuItem();
      menu.add(noBuildsAvailable, 0);
    }
  }


  private static JMenuItem makeNoBuildsAvialableMenuItem() {
    return new JMenuItem("There are no builds avilable");
  }


  private void adjustSummarisedTrayIcon() {
    if (statusMap.isEmpty()) {
      // no statuses, set to "not run yet" aka "I do not know"
      trayIcon.setIcon(makeIcon(TrayImageResourceCollection.IMAGE_NOT_RUN_YET));
    } else {
      // find failed
      boolean successful = true;
      for (final Iterator j = statusMap.values().iterator(); j.hasNext();) {
        final MenuStatusHolder menuStatusHolder = (MenuStatusHolder)j.next();
        final BuildStatus buildStatus = menuStatusHolder.getBuildStatus();
//        System.out.println("DEBUG: buildStatus: " + buildStatus);
        if (buildStatus.getLastBuildRunResultID() != 1 // failed
          && !buildStatus.isInactive() // active
          && buildStatus.getLastCompleteBuildRunID() >= 0 // there were runs
          ) {
          successful = false;
          break;
        }
      }

      // find failed
//      System.out.println("DEBUG: successful: " + successful);
      if (successful) {
        trayIcon.setIcon(makeIcon(TrayImageResourceCollection.IMAGE_SUCCESSFUL));
      } else {
        trayIcon.setIcon(makeIcon(TrayImageResourceCollection.IMAGE_FAILED));
      }
    }
  }


  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e) {
    final JMenuItem source = (JMenuItem)e.getSource();
    final String sourceText = source.getText();
    if (sourceText.equalsIgnoreCase(CAPTION_EXIT)) {
      // handle request to quit
      System.exit(0);
    } else if (sourceText.equalsIgnoreCase(CAPTION_ABOUT)) {
      // show "About"
      JOptionPane.showMessageDialog(null, "Parabuild tray client\n" + Version.versionToString(true), "About", JOptionPane.INFORMATION_MESSAGE);
    } else if (sourceText.equalsIgnoreCase(CAPTION_SETTINGS)) {
      // handle request to edit settings
      editSettings();
    } else {
      log.debug("Action event detected.\n " + "    Event source: " + source + " (an instance of " + source.getClass().getName() + ')');
    }
  }


  /**
   * Edits Parabuild tray setting by displaying a dialog.
   */
  private void editSettings() {
    // loag properties
    Properties properties = null;
    try {
      properties = loadPropertiesFile();
    } catch (final IOException e) {
      // we catch IO exception so that we still can display a dialog.
      log.error(ERROR_LOADING_TRAY_PROPERTIES, e);
      JOptionPane.showMessageDialog(null, StringUtils.toString(e),
        ERROR_LOADING_TRAY_PROPERTIES, JOptionPane.ERROR_MESSAGE);
      return;
    }

    // create and execute dialog
    final String stringPollInterval = properties.getProperty(PROP_POLL_INTERVAL, Integer.toString(DEFAULT_POLL_INTERVAL));
    final SettingsDialog dialog = new SettingsDialog();
    dialog.setServerList(properties.getProperty(PROP_SERVER_LIST, ""));
    dialog.setPollInterval(StringUtils.isValidInteger(stringPollInterval) ? Integer.parseInt(stringPollInterval) : DEFAULT_POLL_INTERVAL);
    dialog.setShowInactiveBuilds(Boolean.valueOf(properties.getProperty(PROP_SHOW_INACTIVE, Boolean.TRUE.toString())));
    dialog.setDebugLogging(Boolean.valueOf(properties.getProperty(PROP_DEBUG_LOGGING, Boolean.FALSE.toString())));
    dialog.pack();
    dialog.setVisible(true);
    if (log.isDebugEnabled()) log.debug("dialog: " + dialog);

    if (dialog.isOK()) {
      // store changed properties
      properties.setProperty(PROP_SERVER_LIST, dialog.getServerList());
      properties.setProperty(PROP_SHOW_INACTIVE, Boolean.toString(dialog.isShowInactiveBuilds()));
      properties.setProperty(PROP_DEBUG_LOGGING, Boolean.toString(dialog.isDebugLogging()));
      properties.setProperty(PROP_POLL_INTERVAL, Integer.toString(dialog.getPollInterval()));
      OutputStream os = null;
      try {
        // store
        os = new FileOutputStream(getPropertiesFile());
        properties.store(os, "Parabuild tray settings");
        IoUtils.closeHard(os);

        // re-load configuration into member variables
        loadProperties();
      } catch (final IOException e) {
        // we catch IO exception so that we still can display a dialog.
        log.error(ERROR_STORING_TRAY_PROPERTIES, e);
        JOptionPane.showMessageDialog(null, StringUtils.toString(e),
          ERROR_STORING_TRAY_PROPERTIES, JOptionPane.ERROR_MESSAGE);
      } finally {
        IoUtils.closeHard(os);
      }
      // set debug level
      try {
        //REVIEWME: curretly it is happeneing because we re-use common
        // debug log4j settings and they refer catalina.base
        Log4jConfigurator.getInstance().initialize(dialog.isDebugLogging());
      } catch (final IOException e) {
        log.warn("Error while saving logging settings", e);
      }
    }
  }


  private Properties loadPropertiesFile() throws IOException {
    InputStream is = null;
    try {
      final Properties properties = new Properties();
      final File propertiesFile = getPropertiesFile();
      is = new FileInputStream(propertiesFile);
      properties.load(is);
      return properties;
    } finally {
      IoUtils.closeHard(is);
    }
  }


  private File getPropertiesFile() throws IOException {
    final File propertiesFile = new File(configDir, TRAY_PROPERTIES_FILE);
    if (!propertiesFile.exists()) {
      propertiesFile.getParentFile().mkdirs();
      propertiesFile.createNewFile();
    }
    return propertiesFile;
  }


  /**
   * Invoked when an item has been selected or deselected by
   * the user. The code written for this method performs the
   * operations that need to occur when an item is selected
   * (or deselected).
   */
  public void itemStateChanged(final ItemEvent e) {

  }


  private static Icon makeIcon(final String imageName) {
    return new ImageIcon(Tray.class.getResource(imageName));
  }


  public String toString() {
    return "Tray{" +
      "configDir=" + configDir +
      ", menu=" + menu +
      ", trayIcon=" + trayIcon +
      ", statusMap=" + statusMap +
      ", noBuildsAvailable=" + noBuildsAvailable +
      ", servers=" + servers +
      ", pollInterval=" + pollInterval +
      ", showInactiveBuilds=" + showInactiveBuilds +
      '}';
  }
}
